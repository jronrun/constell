package com.benayn.constell.service.server.dialect;

import com.google.common.collect.Sets;
import java.util.Set;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class StarsDialect extends AbstractProcessorDialect implements IExpressionObjectDialect {

    private static final String DIALECT_NAME = "Stars Dialect";
    private final IExpressionObjectFactory EXPRESSION_OBJECTS_FACTORY = new StarsExpressionObjectFactory();

    public StarsDialect() {
        super(DIALECT_NAME, "stars", StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        final Set<IProcessor> processors = Sets.newHashSet();
        processors.add(new PropertyAttributeTagProcessor(dialectPrefix));
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
        return processors;
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return this.EXPRESSION_OBJECTS_FACTORY;
    }
}
