package de.axelspringer.ideas.crowdsource.config.mail;

import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class MailTemplateConfig {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(MailTemplateConfig.class);
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
            final InputStream resourceAsStream = getInputStreamForTemplateOrDefaultTemplate(templatePath);
            final String fileContent = IOUtils.toString(resourceAsStream, "UTF-8");

            return parser.parseExpression(fileContent, ParserContext.TEMPLATE_EXPRESSION);

        } catch (IOException e) {
            throw new ResourceNotFoundException();
        }
    }

    private InputStream getInputStreamForTemplateOrDefaultTemplate(String templatePath) {
        InputStream resourceAsStream = getClass().getResourceAsStream(templatePath);
        if (resourceAsStream == null) {
            log.warn("No template found, using default template for: " + templatePath);
            resourceAsStream = getClass().getResourceAsStream(toDefaultTemplatePath(templatePath));
        }
        return resourceAsStream;
    }

    String toDefaultTemplatePath(String templatePath) {
        return templatePath + ".default";
    }
}
