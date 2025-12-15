/*
 * @(#)2019年11月26日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;

import com.wellsoft.pt.simon.spring.CallTreeMethodInterceptor;
import com.wellsoft.pt.simon.spring.webmvc.MvcHandlerInterceptor;
import com.wellsoft.pt.simon.support.TransactionalMeasuringPointcut;

/**
 * Description: 如何描述该类
 *  
 * @author zhongzh
 * @date 2019年11月26日
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月26日.1	zhongzh		2019年11月26日		Create
 * </pre>
 *
 */
@Configuration
@EnableAspectJAutoProxy
public class MonitorConfiguration {

    @Bean
    public HandlerInterceptor getMonitoringHandlerInterceptor() {
        return new MvcHandlerInterceptor();
    }

    @Bean
    public MappedInterceptor getMonitoringMappedInterceptor() {
        String[] excludePatterns = new String[] { "/resources/**", "/web/res/**", "/**/*.css", "/**/*.js", "/**/*.png",
                "/**/*.gif" };
        return new MappedInterceptor(null, excludePatterns, getMonitoringHandlerInterceptor());
    }

    @Bean(value = "monitoringInterceptor")
    public MethodInterceptor getMonitoringInterceptor() {
        return new CallTreeMethodInterceptor();
    }

    @Bean
    public DefaultPointcutAdvisor monitoringInterceptorMethod() {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(getMonitoringInterceptor());
        advisor.setOrder(Ordered.LOWEST_PRECEDENCE);
        advisor.setPointcut(new TransactionalMeasuringPointcut());
        return advisor;
    }

}
