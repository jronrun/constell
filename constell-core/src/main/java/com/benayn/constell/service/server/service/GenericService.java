package com.benayn.constell.service.server.service;

import com.benayn.constell.service.server.repository.Repository;
import java.util.List;

public class GenericService<T> extends AbstractService<T> {

    public GenericService(Repository repository) {
        super(repository);
    }

    @Override
    protected CheckSavedEntity<T> checkIfExistWhenSave(SaveEntity<T> saveEntity) {
        return CheckSavedEntity.of(null, "");
    }

    @Override
    protected CheckDeleteEntity checkBeforeDelete(Long entityId) {
        return CheckDeleteEntity.of(true, "");
    }

    @Override
    protected List<Long> getModuleOwnerIdsByTouchFromId(String touchFromModule, Long touchFromId,
        int pageNo, int pageSize) {
        return null;
    }

    @Override
    protected List<Long> getModuleOwnerIdsInCheckIds(String touchFromModule, Long touchFromId,
        List<Long> checkCurrentModuleItemIds) {
        return null;
    }
}
