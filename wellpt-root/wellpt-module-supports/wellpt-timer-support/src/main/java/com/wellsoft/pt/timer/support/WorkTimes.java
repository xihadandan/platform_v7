/*
 * @(#)2021年6月3日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.date.DateUtils;

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
 * 2021年6月3日.1	zhulh		2021年6月3日		Create
 * </pre>
 * @date 2021年6月3日
 */
public class WorkTimes extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6298503405682493117L;

    private List<WorkTime> workTimes;

    /**
     * @param workTimes
     */
    public WorkTimes(List<WorkTime> workTimes) {
        this.workTimes = workTimes;
    }

    /**
     * @return the workTimes
     */
    public List<WorkTime> getWorkTimes() {
        return workTimes;
    }

    /**
     * @return
     */
    public int getTotalWorkHours() {
        long millisecond = 0;
        for (WorkTime workTime : workTimes) {
            millisecond += workTime.getTimeInMillisecond();
        }
        return DateUtils.millisecondToHour(millisecond).intValue();
    }

    /**
     * @return
     */
    public int getTotalWorkMinutes() {
        long millisecond = 0;
        for (WorkTime workTime : workTimes) {
            millisecond += workTime.getTimeInMillisecond();
        }
        return DateUtils.millisecondToMinute(millisecond).intValue();
    }

}
