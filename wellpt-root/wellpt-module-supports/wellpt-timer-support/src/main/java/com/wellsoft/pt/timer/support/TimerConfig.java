/*
 * @(#)2021年4月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.timer.enums.EnumTimingMode;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月9日.1	zhulh		2021年4月9日		Create
 * </pre>
 * @date 2021年4月9日
 */
public class TimerConfig extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5851564813364162990L;

    // 工作时间方案UUID
    private String workTimePlanUuid;
    // 计时方式
    private String timingMode;
    // 倒计时的计时方式
    private String runtimeTimingMode;
    // 时限类型
    private String timeLimitType;
    // 计时包含启动时间点，1是0否，默认0
    private boolean includeStartTimePoint;
    // 自动推迟到下一工作时间起始点前，1是0否，默认0
    private boolean autoDelay;
    // 计时器当前状态
    private int currentStatus;

    /**
     * @return the workTimePlanUuid
     */
    public String getWorkTimePlanUuid() {
        return workTimePlanUuid;
    }

    /**
     * @param workTimePlanUuid 要设置的workTimePlanUuid
     */
    public void setWorkTimePlanUuid(String workTimePlanUuid) {
        this.workTimePlanUuid = workTimePlanUuid;
    }

    /**
     * @return the timingMode
     */
    public String getTimingMode() {
        return timingMode;
    }

    /**
     * @param timingMode 要设置的timingMode
     */
    public void setTimingMode(String timingMode) {
        this.timingMode = timingMode;
    }

    /**
     * @return
     */
    public EnumTimingMode getEnumTimingMode() {
        return EnumTimingMode.getByValue(timingMode);
    }

    /**
     * @return the runtimeTimingMode
     */
    public String getRuntimeTimingMode() {
        return runtimeTimingMode;
    }

    /**
     * @param runtimeTimingMode 要设置的runtimeTimingMode
     */
    public void setRuntimeTimingMode(String runtimeTimingMode) {
        this.runtimeTimingMode = runtimeTimingMode;
    }

    /**
     * @return
     */
    public EnumTimingMode getRuntimeEnumTimingMode() {
        if (StringUtils.isBlank(runtimeTimingMode)) {
            return getEnumTimingMode();
        }
        return EnumTimingMode.getByValue(runtimeTimingMode);
    }

    /**
     * @return the timeLimitType
     */
    public String getTimeLimitType() {
        return timeLimitType;
    }

    /**
     * @param timeLimitType 要设置的timeLimitType
     */
    public void setTimeLimitType(String timeLimitType) {
        this.timeLimitType = timeLimitType;
    }

    /**
     * @return the includeStartTimePoint
     */
    public boolean isIncludeStartTimePoint() {
        return includeStartTimePoint;
    }

    /**
     * @param includeStartTimePoint 要设置的includeStartTimePoint
     */
    public void setIncludeStartTimePoint(boolean includeStartTimePoint) {
        this.includeStartTimePoint = includeStartTimePoint;
    }

    /**
     * @return the autoDelay
     */
    public boolean isAutoDelay() {
        return autoDelay;
    }

    /**
     * @param autoDelay 要设置的autoDelay
     */
    public void setAutoDelay(boolean autoDelay) {
        this.autoDelay = autoDelay;
    }

    /**
     * @return the currentStatus
     */
    public int getCurrentStatus() {
        return currentStatus;
    }

    /**
     * @param currentStatus 要设置的currentStatus
     */
    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }

}
