package com.wellsoft.pt;

import com.google.common.collect.Lists;
import com.wellsoft.context.profile.OnNotDevProfileCondition;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.generator.CustomAnnotationBeanNameGenerator;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description: spring 上下文配置
 *
 * @author chenq
 * @date 2019/11/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/14    chenq		2019/11/14		Create
 * </pre>
 */
@Configuration
@Conditional(OnNotDevProfileCondition.class)
//自动扫描类
@ComponentScan(basePackages = "com.wellsoft", nameGenerator = CustomAnnotationBeanNameGenerator.class, excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class)})
@Order(Ordered.HIGHEST_PRECEDENCE)
@Lazy(false)
public class RootContextConfiguration {

    @Autowired
    ApplicationContextHolder applicationContextHolder;

    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy(SessionRegistry sessionRegistry) {
        List<SessionAuthenticationStrategy> sessionAuthenticationStrategyList = Lists.newArrayList();
        try {
            // 解密类调用
            Class clazz = Thread
                    .currentThread()
                    .getContextClassLoader()
                    .loadClass(
                            "com.wellsoft.pt.security.core.session.CustomConcurrentSessionControlAuthenticationStrategy");
            Constructor constructor = clazz.getConstructor(SessionRegistry.class, int.class, boolean.class);
            sessionAuthenticationStrategyList.add((SessionAuthenticationStrategy) constructor.newInstance(
                    sessionRegistry, 1, false));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sessionAuthenticationStrategyList.add(new SessionFixationProtectionStrategy());
        sessionAuthenticationStrategyList.add(new RegisterSessionAuthenticationStrategy(sessionRegistry));

        CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy = new CompositeSessionAuthenticationStrategy(
                sessionAuthenticationStrategyList);
        return sessionAuthenticationStrategy;
    }

    @Configurable
    @EnableAsync
    public class AsyncEventTreadPoolConfig implements AsyncConfigurer {

        @Resource(name = "asyncExecutor")
        private Executor asyncExecutor;

        @Bean
        public Executor asyncExecutor() {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            //核心线程池数量
            executor.setCorePoolSize(availableProcessors);
            //最大线程数量
            executor.setMaxPoolSize(availableProcessors * 4);
            //线程池的队列容量
            executor.setQueueCapacity(availableProcessors * 2);
            //线程名称的前缀
            executor.setThreadNamePrefix("wellpt-async-event-excutor-");
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            executor.initialize();
            return executor;
        }

        @Override
        public Executor getAsyncExecutor() {
            return asyncExecutor;
        }

        /*异步任务中异常处理*/
        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return new SimpleAsyncUncaughtExceptionHandler();
        }
    }

}
