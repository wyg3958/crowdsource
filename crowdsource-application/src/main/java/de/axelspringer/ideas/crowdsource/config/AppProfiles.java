package de.axelspringer.ideas.crowdsource.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public interface AppProfiles {

    String NOT = "!";
    String DEV = "dev";
    String CONS = "cons";

    public class ProductionCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return !context.getEnvironment().acceptsProfiles(CONS, DEV);
        }
    }
}
