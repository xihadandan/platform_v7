/*
 * @(#)2013-5-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.support;

/**
 * Description: 如何描述该类
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
public class TimerUnit {
    // 工作日(工作时间)
    public static final int WORKING_DAY = 3;
    // 工作小时(工作时间)
    public static final int WORKING_HOUR = 2;
    // 工作分钟(工作时间)
    public static final int WORKING_MINUTE = 1;

    // 工作日(24小时制)
    public static final int WORKING_DAY_24 = 13;
    // 工作小时(24小时制)
    public static final int WORKING_HOUR_24 = 12;
    // 工作分钟(24小时制)
    public static final int WORKING_MINUTE_24 = 11;

    // 天
    public static final int DAY = 86400;
    // 小时
    public static final int HOUR = 3600;
    // 分钟
    public static final int MINUTE = 60;

    // 日期(2000-01-01)
    public static final int DATE = 23;
    // 日期到时(2000-01-01 12)
    public static final int DATE_HOUR = 22;
    // 日期到分(2000-01-01 12:00)
    public static final int DATE_MINUTE = 21;

    // 日期(2000-01-01 工作时间)
    public static final int DATE_WORKING_DAY = 20;

    // 日期(2000-01-01 12 工作时间)
    public static final int DATE_WORKING_HOUR = 19;
    // 日期(2000-01-01 12:00 工作时间)
    public static final int DATE_WORKING_MINUTE = 18;

    // 日期时间(开始)
    public static final int DATETIME_START = 5;
    // 日期时间(结束)
    public static final int DATETIME_END = 6;
}
