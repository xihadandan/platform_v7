/*
 * @(#)2013-5-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.support;

/**
 * Description: 逾期处理动作
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-28.1	zhulh		2013-5-28		Create
 * </pre>
 * @date 2013-5-28
 */
public class TimerDueAction {
    // 不处理
    public static final int NO_PROCESSING = 0;
    // 移交给B岗人员办理
    public static final int TURN_OVER_TRUSTEE = 1;
    // 移交给督办人员办理
    public static final int TURN_OVER_SUPERVISE = 2;
    // 移交给其他人员办理
    public static final int TURN_OVER_OTHER = 4;
    // 退回上一个办理环节
    public static final int ROLL_BACK = 8;
    // 自动进入下一个办理环节
    public static final int AUTO_SUBMIT = 16;
}
