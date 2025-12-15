/*
 * @(#)2019年11月24日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.simon.spring;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.javasimon.Manager;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.aop.Monitored;
import org.javasimon.callback.calltree.CallTree;
import org.javasimon.source.StopwatchSource;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;

import com.wellsoft.pt.simon.support.AbstractInterceptor;
import com.wellsoft.pt.simon.support.CallTreeUtils;
import com.wellsoft.pt.simon.support.ClassUtils;

/**
 * Description: 如何描述该类
 *  
 * @author zhongzh
 * @date 2019年11月24日
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月24日.1	zhongzh		2019年11月24日		Create
 * </pre>
 *
 */
public class CallTreeMethodInterceptor extends AbstractInterceptor<String, InvoLocation<String>> implements
        MethodInterceptor, Serializable {

    private static final long serialVersionUID = 1L;

    private String commonPrefix = "com.wellsoft.";

    /**
     *
     * @param stopwatchSource
     */
    public CallTreeMethodInterceptor(StopwatchSource<InvoLocation<String>> stopwatchSource) {
        super(stopwatchSource);
    }

    public CallTreeMethodInterceptor(Manager manager) {
        this(new InvoStopwatchSource(manager));
    }

    public CallTreeMethodInterceptor() {
        this(new InvoStopwatchSource(SimonManager.manager()));
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        CallTree callTree, parentCallTree = CallTreeUtils.get();
        InvoLocation<String> location = new InvoLocation<String>(getMonitorName(invocation));
        Split split = startWatch(location);
        if (null == parentCallTree) {
            CallTreeUtils.set(callTree = CallTreeUtils.create(null));
        } else {
            callTree = parentCallTree;
        }
        callTree.onStopwatchStart(split);
        try {
            Object ret = invocation.proceed();
            stopWatch(location, ret);
            return ret;
        } catch (Throwable ex) {
            stopWatch(location, ex);
            throw ex;
        } finally {
            callTree.onStopwatchStop(split);
            if (null == parentCallTree) {
                CallTreeUtils.remove();
                logger.info(callTree.getLogMessage(split));
            }
        }
    }

    /** Get target class. */
    protected Class<?> getTargetClass(MethodInvocation methodInvocation) {
        return AopUtils.getTargetClass(methodInvocation.getThis());
    }

    protected Method getTargetMethod(MethodInvocation methodInvocation, Class<?> targetClass) {
        return AopUtils.getMostSpecificMethod(methodInvocation.getMethod(), targetClass);
    }

    /**
     * Returns monitor name for the given method invocation with {@link org.javasimon.aop.Monitored#name()}
     * and {@link org.javasimon.aop.Monitored#suffix()} applied as expected.
     *
     * @param methodInvocation current method invocation
     * @return name of the Stopwatch for the invocation
     */
    protected String getMonitorName(MethodInvocation methodInvocation) {
        Class<?> targetClass = getTargetClass(methodInvocation);
        Method targetMethod = getTargetMethod(methodInvocation, targetClass);

        Monitored methodAnnotation = AnnotationUtils.findAnnotation(targetMethod, Monitored.class);
        if (methodAnnotation != null && methodAnnotation.name() != null && methodAnnotation.name().length() > 0) {
            return methodAnnotation.name();
        }

        StringBuilder nameBuilder = new StringBuilder();
        Monitored classAnnotation = AnnotationUtils.findAnnotation(targetClass, Monitored.class);
        if (classAnnotation != null && classAnnotation.name() != null && classAnnotation.name().length() > 0) {
            nameBuilder.append(classAnnotation.name());
        } else {
            nameBuilder.append(ClassUtils.shiftPrefix(getMeaningfulClassName(targetClass), commonPrefix));
        }
        nameBuilder.append(Manager.HIERARCHY_DELIMITER);

        String suffix = targetMethod.getName();
        if (methodAnnotation != null && methodAnnotation.suffix() != null && methodAnnotation.suffix().length() > 0) {
            suffix = methodAnnotation.suffix();
        }
        return nameBuilder.append(suffix).toString();
    }

    protected String getMeaningfulClassName(Class<?> targetClass) {
        if (java.lang.reflect.Proxy.isProxyClass(targetClass)) {
            for (Class<?> iface : targetClass.getInterfaces()) {
                if (iface != SpringProxy.class && iface != Advised.class) {
                    return iface.getName();
                }
            }
        }
        return targetClass.getName();
    }

}
