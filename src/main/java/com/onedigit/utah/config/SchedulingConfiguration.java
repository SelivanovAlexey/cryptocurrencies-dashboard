package com.onedigit.utah.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class SchedulingConfiguration {

    @Bean
    public ThreadPoolTaskScheduler healthCheckScheduler() {
        ThreadPoolTaskScheduler s = new ThreadPoolTaskScheduler();
        s.setPoolSize(1);
        s.setThreadNamePrefix("healthcheck-");
        s.setRemoveOnCancelPolicy(true);
        s.setWaitForTasksToCompleteOnShutdown(true);
        s.setAwaitTerminationSeconds(5);
        return s;
    }

    @Bean(destroyMethod = "dispose")
    public Scheduler spreadScheduler() {
        return Schedulers.newParallel("data-process", Runtime.getRuntime().availableProcessors());
    }
}
