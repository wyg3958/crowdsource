package de.axelspringer.ideas.crowdsource.config.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Configuration
public class MailSenderConfig {

    @Value("${de.axelspringer.ideas.crowdsource.mail.host:localhost}")
    private String host;

    @Value("${de.axelspringer.ideas.crowdsource.mail.port:1025}")
    private Integer port;

    @Value("${de.axelspringer.ideas.crowdsource.mail.username:}")
    private String username;

    @Value("${de.axelspringer.ideas.crowdsource.mail.password:}")
    private String password;

    @Value("${de.axelspringer.ideas.crowdsource.mail.starttls:false}")
    private boolean useStartTls;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setDefaultEncoding("UTF-8");
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
}
