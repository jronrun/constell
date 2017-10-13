package com.benayn.constell.service.server.repository;

import java.util.List;

/**
 * Base Repository
 * @param <T>   Record class
 * @param <E>   Example class, used to work with MBG's dynamic select capability
 */
public interface Repository<T, E> {

    /**
     * Count record with given example
     */
    long countBy(E example);

    /**
     * Delete record with given example
     */
    int deleteBy(E example);

    /**
     * Delete record with given primary key
     */
    int deleteById(Long id);

    /**
     * Insert selective (none null field) with given record
     */
    int insert(T record);

    /**
     * Insert all field with given record
     */
    int insertAll(T record);

    /**
     * Select only one record with given example
     */
    T selectOne(E example);

    /**
     * Select records with given example
     */
    List<T> selectBy(E example);

    /**
     * Select records with given example, limit and offset
     */
    List<T> selectBy(E example, int page, int size);

    /**
     * Select records wrap as page with given example, limit and offset
     */
    Page<T> selectPageBy(E example, int page, int size);

    /**
     * Select record with given primary key
     */
    T selectById(Long id);

    /**
     * Update record selective (none null field) with given example
     */
    int updateBy(T record, E example);

    /**
     * Update record all field with given example
     */
    int updateByAll(T record, E example);

    /**
     * Update record selective (none null field) with given primary key
     */
    int updateById(T record);

    /**
     * Update record all field with given primary key
     */
    int updateByIdAll(T record);

}
