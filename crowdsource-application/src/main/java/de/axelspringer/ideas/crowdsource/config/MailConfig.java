package de.axelspringer.ideas.crowdsource.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${de.axelspringer.ideas.crowdsource.mail.host:axelspringerideas.de}")
    private String host;

    @Value("${de.axelspringer.ideas.crowdsource.mail.port:587}")
    private Integer port;

    @Value("${de.axelspringer.ideas.crowdsource.mail.username:crowd@axelspringerideas.de}")
    private String username;

    @Value("${de.axelspringer.ideas.crowdsource.mail.password:WpMqtXoh9b8dPhfBZhxU#}")
    private String password;

    @Value("${de.axelspringer.ideas.crowdsource.mail.starttls:true}")
    private boolean useStartTls;

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

}
