/*
 * @(#)2013-5-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.workhour.enums;

/**
 * Description: 工作时间单位
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
public enum WorkUnit {
    // 工作日
    WorkingDay,
    // 工作小时
    WorkingHour,
    // 工作分钟
    WorkingMinute,
    // 工作日(24小时制)
    WorkingDayOf24Hour,
    // 工作小时(24小时制)
    WorkingHourOf24Hour,
    // 工作分钟(24小时制)
    WorkingMinuteOf24Hour;
}
