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
     * @param example
     * @return
     */
    long countBy(E example);

    /**
     * Delete record with given example
     * @param example
     * @return
     */
    int deleteBy(E example);

    /**
     * Delete record with given primary key
     * @param id
     * @return
     */
    int deleteById(Long id);

    /**
     * Insert selective (none null field) with given record
     * @param record
     * @return
     */
    int insert(T record);

    /**
     * Insert all field with given record
     * @param record
     * @return
     */
    int insertAll(T record);

    /**
     * Select only one record with given example
     * @param example
     * @return
     */
    T selectOne(E example);

    /**
     * Select records with given example
     * @param example
     * @return
     */
    List<T> selectBy(E example);

    /**
     * Select records with given example, limit and offset
     * @param example
     * @param page
     * @param size
     * @return
     */
    List<T> selectBy(E example, int page, int size);

    /**
     * Select records wrap as page with given example, limit and offset
     * @param example
     * @param page
     * @param size
     * @return
     */
    Page<T> selectPageBy(E example, int page, int size);

    /**
     * Select record with given primary key
     * @param id
     * @return
     */
    T selectById(Long id);

    /**
     * Update record selective (none null field) with given example
     * @param record
     * @param example
     * @return
     */
    int updateBy(T record, E example);

    /**
     * Update record all field with given example
     * @param record
     * @param example
     * @return
     */
    int updateByAll(T record, E example);

    /**
     * Update record selective (none null field) with given primary key
     * @param record
     * @return
     */
    int updateById(T record);

    /**
     * Update record all field with given primary key
     * @param record
     * @return
     */
    int updateByIdAll(T record);

}
