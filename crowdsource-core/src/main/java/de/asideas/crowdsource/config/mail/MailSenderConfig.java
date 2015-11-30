package de.asideas.crowdsource.config.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Configuration
public class MailSenderConfig {

    @Value("${de.asideas.crowdsource.mail.host:localhost}")
    private String host;

    @Value("${de.asideas.crowdsource.mail.port:1025}")
    private Integer port;

    @Value("${de.asideas.crowdsource.mail.username:}")
    private String username;

    @Value("${de.asideas.crowdsource.mail.password:}")
    private String password;

    @Value("${de.asideas.crowdsource.mail.starttls:false}")
    private boolean useStartTls;

    @Value("${de.asideas.crowdsource.mail.connectionTimeout:3000}")
    private int smtpConnectionTimeout;

    @Value("${de.asideas.crowdsource.mail.readTimeout:5000}")
    private int smtpReadTimeout;

    @Value("${taskExecutor.email.corePoolsize:10}")
    private int corePoolsize;

    @Value("${taskExecutor.email.maxPoolsize:30}")
    private int maxPoolsize;

    @Value("${taskExecutor.email.queueCapacity:50}")
    private int queueCapacity;

    @Value("${taskExecutor.email.keepAliveSeconds:60}")
    private int keepAliveSeconds;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.starttls.enable", Boolean.toString(useStartTls));
        properties.setProperty("mail.smtp.connectiontimeout", Integer.toString(smtpConnectionTimeout));
        properties.setProperty("mail.smtp.timeout", Integer.toString(smtpReadTimeout));

        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            properties.setProperty("mail.smtp.auth", "true");

            javaMailSender.setUsername(username);
            javaMailSender.setPassword(password);
        }

        javaMailSender.setJavaMailProperties(properties);

        return javaMailSender;
    }

    @Bean
    public AsyncTaskExecutor taskExecutorSmtp(){
        ThreadPoolTaskExecutor res = new ThreadPoolTaskExecutor();
        res.setCorePoolSize(corePoolsize);
        res.setMaxPoolSize(maxPoolsize);
        res.setQueueCapacity(queueCapacity);
        res.setKeepAliveSeconds(keepAliveSeconds);
        res.setThreadNamePrefix("crowd-smtp-");
        return res;
    }
}
