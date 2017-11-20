package com.benayn.constell.service.server.repository.bean;

import static com.benayn.constell.service.server.repository.SqlStatement.COUNT_BY_EXAMPLE;
import static com.benayn.constell.service.server.repository.SqlStatement.DELETE_BY_EXAMPLE;
import static com.benayn.constell.service.server.repository.SqlStatement.DELETE_BY_PRIMARYKEY;
import static com.benayn.constell.service.server.repository.SqlStatement.INSERT;
import static com.benayn.constell.service.server.repository.SqlStatement.INSERT_SELECTIVE;
import static com.benayn.constell.service.server.repository.SqlStatement.SELECT_BY_EXAMPLE;
import static com.benayn.constell.service.server.repository.SqlStatement.SELECT_BY_PRIMARY_KEY;
import static com.benayn.constell.service.server.repository.SqlStatement.UPDATE_BY_EXAMPLE;
import static com.benayn.constell.service.server.repository.SqlStatement.UPDATE_BY_EXAMPLE_SELECTIVE;
import static com.benayn.constell.service.server.repository.SqlStatement.UPDATE_BY_PRIMARY_KEY;
import static com.benayn.constell.service.server.repository.SqlStatement.UPDATE_BY_PRIMARY_KEY_SELECTIVE;
import static java.util.Optional.ofNullable;

import com.benayn.constell.service.server.repository.ExamplePageFeature;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.repository.Repository;
import com.benayn.constell.service.server.repository.Sorting;
import com.benayn.constell.service.server.repository.SqlStatement;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class GenericRepository<T, E, M> implements Repository<T, E>, InitializingBean {

    /**
     * Mapper TypeToken
     */
    private final TypeToken<M> mapperTypeToken = new TypeToken<M>(getClass()) {};

    /**
     * Mybatis Example
     */
    private ExamplePageFeature pageFeature;

    @Override
    public long countBy(E example) {
        return getSqlSession().selectOne(getStatement(COUNT_BY_EXAMPLE), example);
    }

    @Override
    public int deleteBy(E example) {
        return getSqlSession().delete(getStatement(DELETE_BY_EXAMPLE), example);
    }

    @Override
    public int deleteById(Long id) {
        return getSqlSession().delete(getStatement(DELETE_BY_PRIMARYKEY), id);
    }

    @Override
    public int insert(T record) {
        return getSqlSession().insert(getStatement(INSERT_SELECTIVE), record);
    }

    @Override
    public int insertAll(T record) {
        return getSqlSession().insert(getStatement(INSERT), record);
    }

    @Override
    public T selectOne(E example) {
        List<T> items = selectBy(example, 1, 1);
        return items.size() > 0 ? items.get(0) : null;
    }

    @Override
    public List<T> selectBy(E example) {
        List<T> items = getSqlSession().selectList(getStatement(SELECT_BY_EXAMPLE), example);
        return ofNullable(items).orElse(EMPTY_ITEMS);
    }

    @Override
    public List<T> selectBy(E example, int page, int size) {
        addPageFeature(example, page, size);
        return selectBy(example);
    }

    @Override
    public Page<T> selectPageBy(E example, int page, int size) {
        Page<T> aPage = Page.of(page, size, countBy(example));
        addPageFeature(example, page, size);
        aPage.setResource(selectBy(example));
        return aPage;
    }

    @Override
    public T selectById(Long id) {
        return getSqlSession().selectOne(getStatement(SELECT_BY_PRIMARY_KEY), id);
    }

    @Override
    public int updateBy(T record, E example) {
        return getSqlSession().update(getStatement(UPDATE_BY_EXAMPLE_SELECTIVE), record);
    }

    @Override
    public int updateByAll(T record, E example) {
        return getSqlSession().update(getStatement(UPDATE_BY_EXAMPLE), record);
    }

    @Override
    public int updateById(T record) {
        return getSqlSession().update(getStatement(UPDATE_BY_PRIMARY_KEY_SELECTIVE), record);
    }

    @Override
    public int updateByIdAll(T record) {
        return getSqlSession().update(getStatement(UPDATE_BY_PRIMARY_KEY), record);
    }

    /**
     * Returns domain Mapper instance
     * @return M
     */
    protected M getMapper() {
        return getSqlSession().getMapper(mapperType);
    }

    /**
     * Returns domain Mapper instance with given Mapper type
     * @param mapperType Mapper Type
     * @param <Mapper> M
     * @return M
     */
    protected <Mapper> Mapper getMapper(Class<Mapper> mapperType) {
        return getSqlSession().getMapper(mapperType);
    }

    /**
     * Returns SqlSession
     * @return SqlSession
     */
    @SuppressWarnings("WeakerAccess")
    protected SqlSession getSqlSession() {
        return sqlSession;
    }

    private String getStatement(SqlStatement sqlId) {
        return namespace + "." + sqlId.getStatement();
    }

    protected void addPageFeature(Object example, int page, int size) {
        addPageFeature(example, page, size, "id", Sorting.DESCENDING);
    }

    protected void addPageFeature(Object example, int page, int size, String defaultOrderBy, Sorting defaultSortBy) {
        pageFeature.add(example, page, size, defaultOrderBy, defaultSortBy);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setup();
    }

    protected void setup() {

    }

    @Autowired
    public void setPageFeature(ExamplePageFeature pageFeature) {
        this.pageFeature = pageFeature;
    }

    @Autowired
    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    /**
     * Mapper Class Type
     */
    @SuppressWarnings("unchecked")
    private final Class<M> mapperType = (Class<M>) mapperTypeToken.getRawType();

    /**
     * Mapper Namespace
     */
    private final String namespace = mapperType.getName() ;

    private SqlSession sqlSession;
    protected final List<T> EMPTY_ITEMS = Lists.newArrayList();

}
