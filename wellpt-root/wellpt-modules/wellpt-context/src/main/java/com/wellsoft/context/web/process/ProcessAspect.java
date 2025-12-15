/*
 * @(#)2013-2-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.web.process;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年6月12日.1	zhongzh		2020年6月12日		Create
 * </pre>
 * @date 2020年6月12日
 */
@Aspect
public class ProcessAspect extends ProcessUtils implements Ordered {

    /**
     * 第一次返回空并创建ProcessMessage,后面同一个processId一直返回之前创建ProcessMessage
     *
     * @param processId
     * @return
     */
    public static void putIfAbsent(String processId) {
        ProcessMessage processMessage = processMessages.get(processId);
        if (processMessage == null) {
            synchronized (ProcessUtils.class) {
                processMessage = processMessages.get(processId);
                if (processMessage == null) {
                    processHolder.set(processId);
                    processMessages.put(processId, new ProcessMessage());
                }
            }
        }
        throw new RuntimeException("[" + processId + "]重复请求");
    }

    /**
     * 删除进度消息
     *
     * @param processId
     * @return
     */
    public static ProcessMessage remove(String processId) {
        processHolder.remove();
        return processMessages.remove(processId);
    }

    /**
     * @param joinPoint
     */
    @Around("execution(public * *(..)) and (within(@com.wellsoft.context.web.process.Process *) or @annotation(com.wellsoft.context.web.process.Process))")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes servletRequestAttributes = null;
        servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String processId = request.getParameter("_processId");
        try {
            if (null != processId) {
                putIfAbsent(processId);
            }
            return joinPoint.proceed(joinPoint.getArgs());
        } finally {
            if (null != processId) {
                remove(processId);
            }
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

}