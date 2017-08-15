package com.benayn.constell.service.server.repository.bean;

import static java.util.Optional.ofNullable;

import com.benayn.constell.service.server.repository.ExamplePageFeature;
import com.benayn.constell.service.server.repository.Page;
import com.benayn.constell.service.server.repository.Sorting;
import com.google.common.base.Throwables;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.stereotype.Component;

@Component
public class MybatisExamplePageFeature implements ExamplePageFeature {

    private String expression;

    private static final String POSTGRESQL_EXPRESSION = "limit %s offset %s";

    @Override
    public void add(Object example, int page, int size, String defaultOrderBy, Sorting defaultSortBy) {
        try {
            Class<?> exampleClazz = example.getClass();
            Method theGet = exampleClazz.getMethod("getOrderByClause");
            Method theSet = exampleClazz.getMethod("setOrderByClause", String.class);

            Page<?> aPage = Page.of(page, size);
            String expr = String.format(ofNullable(this.expression)
                .orElse(POSTGRESQL_EXPRESSION), aPage.getSize(), aPage.getOffset());

            String orderByClause = ofNullable((String) theGet.invoke(example))
                .orElse(String.format("%s %s", defaultOrderBy, defaultSortBy.getSort()));


            theSet.invoke(example, String.format("%s %s", orderByClause, expr));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Throwables.throwIfUnchecked(e);
        }
    }

    @Override
    public void setExpression(String expression) {
        this.expression = expression;
    }
}
