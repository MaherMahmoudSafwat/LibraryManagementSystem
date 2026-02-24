package com.LibraryManagementSystem.AiSystems.Configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration
{
    @Bean("asyncTaskExecutor")
    public Executor AsyncTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(4);
        threadPoolTaskExecutor.setQueueCapacity(150);
        threadPoolTaskExecutor.setMaxPoolSize(4);
        threadPoolTaskExecutor.setThreadNamePrefix("AsyncTaskThread-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
