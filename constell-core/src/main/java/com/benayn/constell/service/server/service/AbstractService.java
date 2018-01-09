package com.benayn.constell.service.server.service;

import static com.benayn.constell.service.util.Assets.checkArgument;
import static com.benayn.constell.service.util.Assets.checkRecordDeleted;
import static com.benayn.constell.service.util.Assets.checkRecordNoneExist;
import static com.benayn.constell.service.util.Assets.checkRecordSaved;

import com.benayn.constell.service.common.Pair;
import com.benayn.constell.service.exception.ServiceException;
import com.benayn.constell.service.server.component.ViewObjectResolver;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.repository.Repository;
import com.benayn.constell.service.server.repository.bean.AbstractRepository;
import com.benayn.constell.service.server.repository.domain.ConditionTemplate;
import com.benayn.constell.service.server.repository.domain.GenericExample;
import com.benayn.constell.service.server.repository.domain.GenericExample.Criteria;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.util.Likes;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractService<T> implements Service<T> {

    private Repository<T, GenericExample> repository;
    private ViewObjectResolver viewObjectResolver;

    public AbstractService(Repository repository) {
        AbstractRepository givenRepository = (AbstractRepository) repository;

        AbstractRepository<T, GenericExample> abstractRepository =
            new AbstractRepository<T, GenericExample>(repository.getNamespace()) {};

        abstractRepository.setSqlSession(givenRepository.getSqlSession());
        abstractRepository.setPageFeature(givenRepository.getPageFeature());

        this.repository = abstractRepository;
    }

    @Override
    public T selectById(long entityId) {
        return repository.selectById(entityId);
    }

    @Override
    public Page<T> selectPageBy(Renderable condition) {
        GenericExample example = new GenericExample();
        Criteria criteria = example.createCriteria();

        SearchEntity searchEntity = viewObjectResolver.getSearchEntity(condition);
        searchEntity.getFields().forEach(field -> {
            ConditionTemplate template = field.getConditionTemplate();

            if (null == field.getValue() && !template.isNoneValueTemplate()) {
                return;
            }

            Object value1, value2 = null;
            if (template.isLikeTemplate()) {
                value1 = Likes.get(String.valueOf(field.getValue()), field.getSide());
            } else if (template.isBetweenTemplate()) {
                Pair<?, ?> pair = (Pair<?, ?>) field.getValue();
                value1 = pair.getKey();
                value2 = pair.getValue();
            } else if (template.isNoneValueTemplate()) {
                value1 = null;
            } else {
                value1 = field.getValue();
            }

            criteria.and(field.getConditionTemplate(), field.getName(), value1, value2);
        });

        /*
            Account - Role
            set current module is Role, touch module is Account
            then touch id is account id
            getModuleOwnerIdsByTouchFromId is get role ids by account id, mean already build relation, account own these roles
            getModuleOwnerIdsInCheckIds is get account id own role id by given check role ids
         */
        if (condition.hasTouchOwner()) {
            List<Long> itemIds = getModuleOwnerIdsByTouchFromId(condition.getTouchModule(),
                condition.getTouchId(), condition.getPageNo(), condition.getPageSize());
            if (null == itemIds) {
                itemIds = Lists.newArrayList();
            }

            if (itemIds.size() < 1) {
                itemIds.add(-1L);
            }

            criteria.andIn(searchEntity.getPrimaryKey(), itemIds);
        }

        Page<T> page = repository.selectPageBy(example, condition.getPageNo(), condition.getPageSize());

        if (condition.hasTouch()) {
            List<Long> checkCurrentModuleItemIds = Lists.newArrayList();

            for (T item : page.getResource()) {
                Object primaryValue = getFieldValue(item, searchEntity.getPrimaryKey());
                if (null != primaryValue) {
                    checkCurrentModuleItemIds.add((Long) primaryValue);
                }
            }

            List<Long> ownerIds = getModuleOwnerIdsInCheckIds(condition.getTouchModule(),
                condition.getTouchId(), checkCurrentModuleItemIds);
            page.setAsTouchOwnerIds(ownerIds);
        }

        return page;
    }

    @Override
    public int deleteById(Long entityId) throws ServiceException {
        CheckDeleteEntity checked = checkBeforeDelete(entityId);
        checkArgument(checked.isDeletable(), checked.getUndeletableMessage());
        return checkRecordDeleted(repository.deleteById(entityId));
    }

    @Override
    public int save(Renderable entity) throws ServiceException {
        int result;

        SaveEntity<T> saveEntity = viewObjectResolver.getSaveEntity(entity, getEntityInstance());
        T item = saveEntity.getEntity();

        CheckSavedEntity<T> checked = checkIfExistWhenSave(saveEntity);
        T savedItem = checked.getSavedEntity();

        // create
        if (saveEntity.isCreate()) {
            checkRecordNoneExist(null == savedItem, checked.getCheckedValue());

            result = repository.insertAll(item);
        }
        // update
        else {
            checkRecordNoneExist(null == savedItem
                || checkIfSameItem(item, savedItem, saveEntity.getPrimaryKey()), checked.getCheckedValue());
            result = repository.updateById(item);
        }

        return checkRecordSaved(result);
    }

    protected abstract CheckSavedEntity<T> checkIfExistWhenSave(SaveEntity<T> saveEntity);

    protected abstract CheckDeleteEntity checkBeforeDelete(Long entityId);

    protected abstract List<Long> getModuleOwnerIdsByTouchFromId(
        String touchFromModule, Long touchFromId, int pageNo, int pageSize);

    protected abstract List<Long> getModuleOwnerIdsInCheckIds(
        String touchFromModule, Long touchFromId, List<Long> checkCurrentModuleItemIds);

    @Autowired
    public void setViewObjectResolver(ViewObjectResolver viewObjectResolver) {
        this.viewObjectResolver = viewObjectResolver;
    }

    private boolean checkIfSameItem(T item, T savedItem, String primaryKey) {
        Object value1 = getFieldValue(item, primaryKey);
        Object value2 = getFieldValue(savedItem, primaryKey);
        return String.valueOf(value1).equals(String.valueOf(value2));
    }

    private Object getFieldValue(T item, String fieldName) {
        try {
            return item.getClass().getField(fieldName).get(item);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Throwables.throwIfUnchecked(e);
        }

        return null;
    }

    private T getEntityInstance() throws ServiceException {
        try {
            return entityClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private final Class<T> entityClass = (Class<T>) new TypeToken<T>(getClass()) {}.getRawType();

}
