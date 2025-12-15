/*
 * @(#)2019年11月26日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.simon.support;

import java.lang.reflect.Method;

import org.javasimon.aop.Monitored;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

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
public class TransactionalMeasuringPointcut implements Pointcut {
    /**
     * Returns a class filter that lets all class through.
     *
     * @return a class filter that lets all class through
     */
    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    /**
     * Returns a method matcher that matches any method that has the {@link Monitored} annotation,
     * or is in a class with the {@link Monitored} annotation or is in a subclass of such a class or interface.
     *
     * @return method matcher matching {@link Monitored} methods
     */
    @Override
    public MethodMatcher getMethodMatcher() {
        return MonitoredMethodMatcher.INSTANCE;
    }

    private enum MonitoredMethodMatcher implements MethodMatcher {
        INSTANCE;

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            return !ClassUtils.isCglibProxyClass(targetClass)
                    && isMonitoredAnnotationOnClassOrMethod(method, targetClass);
        }

        private boolean isMonitoredAnnotationOnClassOrMethod(Method method, Class<?> targetClass) {
            return AnnotationUtils.findAnnotation(targetClass, Transactional.class) != null
                    || AnnotationUtils.findAnnotation(AopUtils.getMostSpecificMethod(method, targetClass),
                            Transactional.class) != null;
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            throw new UnsupportedOperationException("This is not a runtime method matcher");
        }

    }
}