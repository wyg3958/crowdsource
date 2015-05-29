package de.axelspringer.ideas.crowdsource.config.mail;

import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class MailTemplateConfig {

    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Bean
    public Expression activationEmailTemplate() {
        return createExpressionFromFile("/email/activation.template");
    }

    @Bean
    public Expression newProjectEmailTemplate() {
        return createExpressionFromFile("/email/new-project.template");
    }

    @Bean
    public Expression passwordForgottenEmailTemplate() {
        return createExpressionFromFile("/email/password-forgotten.template");
    }

    @Bean
    public Expression projectPublishedEmailTemplate() {
        return createExpressionFromFile("/email/project-published.template");
    }

    @Bean
    public Expression projectRejectedEmailTemplate() {
        return createExpressionFromFile("/email/project-rejected.template");
    }

    private Expression createExpressionFromFile(final String templatePath) {
        try {
            final InputStream resourceAsStream = getClass().getResourceAsStream(templatePath);
            final String fileContent = IOUtils.toString(resourceAsStream, "UTF-8");

            return parser.parseExpression(fileContent, ParserContext.TEMPLATE_EXPRESSION);

        } catch (IOException e) {
            throw new ResourceNotFoundException();
        }
    }
}
