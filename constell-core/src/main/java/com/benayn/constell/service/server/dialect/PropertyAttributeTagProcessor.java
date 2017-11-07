package com.benayn.constell.service.server.dialect;

import com.benayn.constell.service.common.Pair;
import java.util.List;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

public class PropertyAttributeTagProcessor extends AbstractAttributeTagProcessor {

    private static final String ATTR_NAME = "attributes";
    private static final int PRECEDENCE = 12000;

    PropertyAttributeTagProcessor(final String dialectPrefix) {
        super(
            TemplateMode.HTML,              // This processor will apply only to HTML mode
            dialectPrefix,                  // Prefix to be applied to name for matching
            null,               // No tag name: match any tag name
            false,         // No prefix to be applied to tag name
            ATTR_NAME,                      // Name of the attribute that will be matched
            true,        // Apply dialect prefix to attribute name
            PRECEDENCE,                     // Precedence (inside dialect's precedence)
            true);          // Remove the matched attribute afterwards
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
        String attributeValue, IElementTagStructureHandler structureHandler) {

        final IEngineConfiguration configuration = context.getConfiguration();

        /*
         * Obtain the Thymeleaf Standard Expression parser
         */
        final IStandardExpressionParser parser =
            StandardExpressions.getExpressionParser(configuration);

        /*
         * Parse the attribute value as a Thymeleaf Standard Expression
         */
        final IStandardExpression expression =
            parser.parseExpression(context, attributeValue);

        Object result = expression.execute(context);
        if (null == result) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<Pair<String, String>> attributes = (List<Pair<String, String>>) result;

        if (attributes.size() < 1) {
            return;
        }

        attributes.forEach(attr -> structureHandler.setAttribute(attr.getKey(), attr.getValue()));
    }
}
