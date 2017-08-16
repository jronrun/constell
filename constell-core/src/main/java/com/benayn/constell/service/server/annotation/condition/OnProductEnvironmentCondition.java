package com.benayn.constell.service.server.annotation.condition;

import static com.benayn.constell.service.common.BaseConstants.PRODUCT_ENVIROMENT;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class OnProductEnvironmentCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean isProductEnv = context.getEnvironment().acceptsProfiles(PRODUCT_ENVIROMENT);
        String noMatchMsg = String.format("The following profiles are active: %s",
                (Object) context.getEnvironment().getActiveProfiles());

        if (metadata.isAnnotated(ConditionalOnProductEnvironment.class.getName())) {
            return isProductEnv
                ? ConditionOutcome.match()
                : ConditionOutcome.noMatch(noMatchMsg);
        }

        return isProductEnv
            ? ConditionOutcome.noMatch(noMatchMsg)
            : ConditionOutcome.match();
    }
}
