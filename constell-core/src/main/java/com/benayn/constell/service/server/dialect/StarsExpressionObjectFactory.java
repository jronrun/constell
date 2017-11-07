package com.benayn.constell.service.server.dialect;

import com.benayn.constell.service.server.dialect.expression.StarExpression;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.IExpressionObjectFactory;

public class StarsExpressionObjectFactory implements IExpressionObjectFactory {

    private static final String STAR_EVALUATION_VARIABLE_NAME = "star";

    private static final Set<String> ALL_EXPRESSION_OBJECT_NAMES =
        Collections.unmodifiableSet(Sets.newHashSet(STAR_EVALUATION_VARIABLE_NAME));

    @Override
    public Set<String> getAllExpressionObjectNames() {
        return ALL_EXPRESSION_OBJECT_NAMES;
    }

    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        if (STAR_EVALUATION_VARIABLE_NAME.equals(expressionObjectName)) {
            return new StarExpression(context.getLocale());
        }

        return null;
    }

    @Override
    public boolean isCacheable(String expressionObjectName) {
        return expressionObjectName != null && STAR_EVALUATION_VARIABLE_NAME.equals(expressionObjectName);
    }
}
