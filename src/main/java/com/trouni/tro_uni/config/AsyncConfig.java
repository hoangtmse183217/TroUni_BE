package com.trouni.tro_uni.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * AsyncConfig - Cấu hình cho async processing
 * <p>
 * Chức năng chính:
 * - Cấu hình thread pool cho async tasks
 * - Tối ưu hiệu suất cho email sending
 * - Quản lý thread pool size và queue
 * <p>
 * @author TroUni Team
 * @version 1.0
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Cấu hình thread pool cho async email processing
     * <p>
     * @return Executor - Thread pool executor
     */
    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Cấu hình thread pool
        executor.setCorePoolSize(2);                    // Số thread cơ bản
        executor.setMaxPoolSize(5);                     // Số thread tối đa
        executor.setQueueCapacity(100);                 // Kích thước queue
        executor.setKeepAliveSeconds(60);               // Thời gian giữ thread
        executor.setThreadNamePrefix("EmailAsync-");    // Prefix cho thread name
        
        // Cấu hình rejection policy
        executor.setRejectedExecutionHandler((r, executor1) -> {
            log.warn("Email task rejected, queue is full. Task: {}", r.toString());
            // Có thể implement retry logic ở đây
        });
        
        // Khởi tạo thread pool
        executor.initialize();
        
        log.info("Email async executor initialized with core={}, max={}, queue={}", 
                executor.getCorePoolSize(), 
                executor.getMaxPoolSize(), 
                executor.getQueueCapacity());
        
        return executor;
    }

    /**
     * Cấu hình thread pool cho general async tasks
     * <p>
     * @return Executor - Thread pool executor
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Cấu hình thread pool
        executor.setCorePoolSize(3);                    // Số thread cơ bản
        executor.setMaxPoolSize(8);                     // Số thread tối đa
        executor.setQueueCapacity(200);                 // Kích thước queue
        executor.setKeepAliveSeconds(60);               // Thời gian giữ thread
        executor.setThreadNamePrefix("Async-");         // Prefix cho thread name
        
        // Cấu hình rejection policy
        executor.setRejectedExecutionHandler((r, executor1) -> {
            log.warn("Async task rejected, queue is full. Task: {}", r.toString());
        });
        
        // Khởi tạo thread pool
        executor.initialize();
        
        log.info("General async executor initialized with core={}, max={}, queue={}", 
                executor.getCorePoolSize(), 
                executor.getMaxPoolSize(), 
                executor.getQueueCapacity());
        
        return executor;
    }
}
