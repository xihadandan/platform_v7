/*
 * @(#)2015年8月31日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.pt.bpm.engine.context.listener.TaskUserIndicate;

import java.util.Comparator;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年8月31日.1	zhulh		2015年8月31日		Create
 * </pre>
 * @date 2015年8月31日
 */
public class TaskUserIndicateComparator implements Comparator<TaskUserIndicate> {

    /**
     * (non-Javadoc)
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(TaskUserIndicate o1, TaskUserIndicate o2) {
        return Integer.valueOf(o1.getOrder()).compareTo(o2.getOrder());
    }

}
