/*
 * @(#)2020年6月12日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.web.process;

import java.lang.annotation.*;

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
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Process {

    /**
     * 单例（浏览器刷新时可以续点）
     * <pre>{@code
     * 	MethodSignature signature = (MethodSignature) joinPoint.getSignature();
     * 	Method mothod = signature.getMethod();
     * 	Process process = mothod.getAnnotation(Process.class);
     * 	boolean singleton = process.singleton();
     *  ConcurrentMap<method, processId>
     *    }</pre>
     *
     * @return
     */
    boolean singleton() default false;
}
