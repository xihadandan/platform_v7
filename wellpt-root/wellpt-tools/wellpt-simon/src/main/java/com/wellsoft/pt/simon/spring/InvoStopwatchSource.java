/*
 * @(#)2019年11月24日 V1.0
 * 
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.simon.spring;

import org.javasimon.Manager;
import org.javasimon.source.AbstractStopwatchSource;

/**
 * Description: InvoStopwatchSource
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
public class InvoStopwatchSource extends AbstractStopwatchSource<InvoLocation<String>> {

    /**
     *
     * @param manager
     */
    public InvoStopwatchSource(Manager manager) {
        super(manager);
    }

    @Override
    protected String getMonitorName(InvoLocation<String> location) {
        return location.getHandler();
    }

}
