package de.axelspringer.ideas.crowdsource.config;

import de.axelspringer.ideas.crowdsource.exceptions.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${de.axelspringer.ideas.crowdsource.mail.host:smtp.mailgun.org}")
    private String host;

    @Value("${de.axelspringer.ideas.crowdsource.mail.port:587}")
    private Integer port;

    @Value("${de.axelspringer.ideas.crowdsource.mail.username:postmaster@crowd.asideas.de}")
    private String username;

    @Value("${de.axelspringer.ideas.crowdsource.mail.password:d5400101dbe1f70e1c60d3dcc2450e2d}")
    private String password;

    @Value("${de.axelspringer.ideas.crowdsource.mail.starttls:true}")
    private boolean useStartTls;

    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.starttls.enable", Boolean.toString(useStartTls));

        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            properties.setProperty("mail.smtp.auth", "true");

            javaMailSender.setUsername(username);
            javaMailSender.setPassword(password);
        }

        javaMailSender.setJavaMailProperties(properties);

        return javaMailSender;
    }

    @Bean
    public Expression activationEmailTemplate() {
        return format("email/activation.template");
    }

    @Bean
    public Expression newProjectEmailTemplate() {
        return format("email/new-project.template");
    }

    @Bean
    public Expression passwordForgottenEmailTemplate() {
        return format("email/password-forgotten.template");
    }

    @Bean
    public Expression projectPublishedEmailTemplate() {
        return format("email/project-published.template");
    }

    @Bean
    public Expression projectRejectedEmailTemplate() {
        return format("email/project-rejected.template");
    }

    private Expression format(final String templatePath) {
        try {
            Resource resource = new ClassPathResource(templatePath);
            InputStream inputStream = resource.getInputStream();
            final String fileContent = IOUtils.toString(inputStream);

            return parser.parseExpression(fileContent, ParserContext.TEMPLATE_EXPRESSION);

        } catch (IOException e) {
            throw new ResourceNotFoundException();
        }
    }
}
