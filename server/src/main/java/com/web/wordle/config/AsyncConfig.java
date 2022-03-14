package com.web.wordle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@Async
public class AsyncConfig {

    public Executor asyncThreadPool(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(2);//실행대기 쓰레드 수
        taskExecutor.setMaxPoolSize(10);//최대 쓰레드 수
        taskExecutor.setQueueCapacity(10);
        taskExecutor.setThreadNamePrefix("Async-Executor");
        taskExecutor.setDaemon(true);
        taskExecutor.initialize();

        return taskExecutor;
    }
}