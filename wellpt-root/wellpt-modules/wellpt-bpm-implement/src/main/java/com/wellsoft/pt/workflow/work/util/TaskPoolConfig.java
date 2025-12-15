package com.wellsoft.pt.workflow.work.util;

import com.wellsoft.context.config.Config;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class TaskPoolConfig {
    @Bean(name = "taskPoolExecutor")
    public ThreadPoolTaskExecutor taskPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程池大小
        String corePoolSize = Config.getValue("taskPoolExecutor.corePoolSize");
        if (StringUtils.isNotBlank(corePoolSize)) {
            executor.setCorePoolSize(Integer.valueOf(corePoolSize));
        } else {
            executor.setCorePoolSize(20);
        }

        // 最大线程数
        String maxPoolSize = Config.getValue("taskPoolExecutor.maxPoolSize");
        if (StringUtils.isNotBlank(maxPoolSize)) {
            executor.setMaxPoolSize(Integer.valueOf(maxPoolSize));
        } else {
            executor.setMaxPoolSize(100);
        }

        // 队列容量
        String queueCapacity = Config.getValue("taskPoolExecutor.queueCapacity");
        if (StringUtils.isNotBlank(queueCapacity)) {
            executor.setQueueCapacity(Integer.valueOf(queueCapacity));
        } else {
            executor.setQueueCapacity(20);
        }

        // 活跃时间
        String keepAliveSeconds = Config.getValue("taskPoolExecutor.keepAliveSeconds");
        if (StringUtils.isNotBlank(keepAliveSeconds)) {
            executor.setKeepAliveSeconds(Integer.valueOf(keepAliveSeconds));
        } else {
            executor.setKeepAliveSeconds(300);
        }

        // 线程名字前缀
        executor.setThreadNamePrefix("taskPoolExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

}