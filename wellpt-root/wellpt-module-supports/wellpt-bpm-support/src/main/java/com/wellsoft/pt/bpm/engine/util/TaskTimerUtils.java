/*
 * @(#)2013-11-21 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.util;

import com.wellsoft.pt.bpm.engine.entity.TaskTimer;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-21.1	zhulh		2013-11-21		Create
 * </pre>
 * @date 2013-11-21
 */
public class TaskTimerUtils {

    public static String getTraceTitle(TaskTimer taskTimer) {
        return "流程定时跟踪" + "_" + taskTimer.getUuid();
    }

    public static String getAlarmTitle(TaskTimer taskTimer) {
        return "预警提醒" + "_" + taskTimer.getName() + "_" + taskTimer.getUuid();
    }

    public static String getAlarmMsgTitle(TaskTimer taskTimer) {
        return "预警提醒，消息通知" + "_" + taskTimer.getName() + "_" + taskTimer.getUuid() + "_" + taskTimer.getTaskInstUuid();
    }

    public static String getDueDoingTitle(TaskTimer taskTimer) {
        return "逾期处理" + "_" + taskTimer.getName() + "_" + taskTimer.getUuid();
    }

    public static String getDueDoingMsgTitle(TaskTimer taskTimer) {
        return "逾期处理，消息通知" + "_" + taskTimer.getName() + "_" + taskTimer.getUuid() + "_" + taskTimer.getTaskInstUuid();
    }

    public static String getDueDoingMsgTitle(TaskTimer taskTimer, int index) {
        return "逾期处理，消息通知" + index + "_" + taskTimer.getName() + "_" + taskTimer.getTaskInstUuid();
    }

}
