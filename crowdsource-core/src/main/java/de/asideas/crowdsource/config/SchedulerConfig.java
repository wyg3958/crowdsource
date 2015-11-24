package de.asideas.crowdsource.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

    @Value("${de.asideas.crowdsource.scheduler.poolsize:1}")
    private Integer poolSize;

    @Bean
    public ThreadPoolTaskScheduler crowdScheduler() {
        ThreadPoolTaskScheduler res = new ThreadPoolTaskScheduler();
        res.setThreadNamePrefix("crowd-scheduler-");
        res.setPoolSize(poolSize);
        return res;
    }

}
