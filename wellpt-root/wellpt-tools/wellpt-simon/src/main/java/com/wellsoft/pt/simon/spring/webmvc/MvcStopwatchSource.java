/*
 * @(#)2019年11月23日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.simon.spring.webmvc;

import org.javasimon.Manager;
import org.javasimon.source.AbstractStopwatchSource;

/**
 * Description: 
 *  
 * @author zhongzh
 * @date 2019年11月23日
 * @version 1.0
 * 
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月23日.1	zhongzh		2019年11月23日		Create
 * </pre>
 *
 */
public class MvcStopwatchSource extends AbstractStopwatchSource<MvcInvoLocation> {
    /**
     * Prefix used for Simon names.
     */
    protected static final String PREFIX = "org.javasimon.mvc";

    public MvcStopwatchSource(Manager manager) {
        super(manager);
    }

    @Override
    protected String getMonitorName(MvcInvoLocation t) {
        StringBuilder stringBuilder = new StringBuilder(t.getHandler());
        // Append step
        stringBuilder.append(".").append(t.getStep());
        return stringBuilder.toString();
    }
}