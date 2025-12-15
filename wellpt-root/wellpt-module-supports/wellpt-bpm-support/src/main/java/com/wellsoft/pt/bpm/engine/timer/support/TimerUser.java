/*
 * @(#)2013-5-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.support;

/**
 * Description: 定时器用户类型
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-25.1	zhulh		2013-5-25		Create
 * </pre>
 * @date 2013-5-25
 */
public class TimerUser {
    // 用户类型(责任人、预警提醒人、预警流程办理人、逾期处理人、逾期处理流程办理人)
    // 责任人
    // public static final int DUTY = 0;

    // 预警提醒
    // 消息通知人员
    public static final int ALARM_OBJECT = 1;
    // 消息通知其他人员
    public static final int ALARM_USER = 2;
    // 发起流程办理人
    public static final int ALARM_FLOW_DOING = 3;
    // 发起流程其他人员
    public static final int ALARM_FLOW_DOING_USER = 4;

    // 逾期处理
    // 消息通知人员
    public static final int DUE_OBJECT = 5;
    // 消息通知其他人员
    public static final int DUE_USER = 6;
    // 移交给其他人员办理
    public static final int DUE_TO_USER = 7;
    // 发起流程办理人
    public static final int DUE_FLOW_DOING = 8;
    // 发起流程其他人员
    public static final int DUE_FLOW_DOING_USER = 9;
}
