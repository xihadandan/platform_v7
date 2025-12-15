/*
 * @(#)2014-10-28 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.parser.activity;

import com.google.common.collect.Lists;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-28.1	zhulh		2014-10-28		Create
 * </pre>
 * @date 2014-10-28
 */
public class TaskActivityStack extends ArrayDeque<TaskActivityItem> {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1845082196506957682L;

    /**
     * @param taskInstUuid
     * @return
     */
    public TaskActivityItem getRollbackItem(String taskInstUuid) {
        TaskActivityItem item = this.peek();
        if (item.getTaskInstUuid().equals(taskInstUuid)) {
            this.pop();
        } else {
            throw new WorkFlowException("当前环节不是最新的活动环节，无法获取退回的环节!");
        }

        if (this.isEmpty()) {
            throw new WorkFlowException("当前环节是第一个活动环节，无法进行退回!");
        }

        // 返回要退回的活动项
        return this.peek();
    }

    /**
     * 获取可退回的任务活动
     *
     * @return
     */
    public List<TaskActivityItem> getAvailableToRollbackTaskActivityItems() {
        List<TaskActivityItem> returnList = new ArrayList<TaskActivityItem>();
        TaskActivityItem top = this.peek();
        Iterator<TaskActivityItem> it = this.descendingIterator();
		/*
		while (it.hasNext()) {
			it.next();
		}
		*/
        while (it.hasNext()) {// it.hasPrevious()
            TaskActivityItem item = it.next();// it.previous()
            if (!top.getTaskInstUuid().equals(item.getTaskInstUuid())) {
                returnList.add(item);
            }
        }
        return returnList;
    }

    /**
     * @return
     */
    public List<TaskOperationItem> getTaskOperationItems() {
        List<TaskOperationItem> operationItems = Lists.newArrayList();
        Iterator<TaskActivityItem> it = this.descendingIterator();
        while (it.hasNext()) {
            TaskActivityItem item = it.next();
            List<TaskOperationItem> items = item.getOperationItems();
            if (CollectionUtils.isNotEmpty(items)) {
                operationItems.addAll(items);
            }
        }
        return operationItems;
    }

}
