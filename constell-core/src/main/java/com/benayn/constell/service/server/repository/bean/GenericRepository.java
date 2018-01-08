package com.benayn.constell.service.server.repository.bean;

import com.google.common.reflect.TypeToken;

public abstract class GenericRepository<T, E, M> extends AbstractRepository<T, E> {

    /**
     * Mapper TypeToken
     */
    private final TypeToken<M> mapperTypeToken = new TypeToken<M>(getClass()) {};

    /**
     * Mapper Class Type
     */
    @SuppressWarnings("unchecked")
    private final Class<M> mapperType = (Class<M>) mapperTypeToken.getRawType();
    private String genericRepositoryNamespace;

    /**
     * Returns domain Mapper instance
     * @return M
     */
    protected M getMapper() {
        return getSqlSession().getMapper(mapperType);
    }

    public GenericRepository() {
        super(null);
        this.genericRepositoryNamespace = mapperType.getName();
    }

    @Override
    public String getNamespace() {
        return genericRepositoryNamespace;
    }
}
