package com.benayn.constell.service.server.repository;

public interface ExamplePageFeature {

    /**
     * Add page feature to Mybatis generator Example class
     * @param example
     * @param page
     * @param size
     * @param defaultOrderBy    Use if example not set order by field
     * @param defaultSortBy     User if example not set order by field
     */
    void add(Object example, int page, int size, String defaultOrderBy, Sorting defaultSortBy);

    /**
     * Set page feature expression, default is PostgreSQL page expression: "limit %s offset %s" <br/>
     * @param expression
     */
    void setExpression(String expression);

}
