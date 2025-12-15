/*
 * @(#)2021年5月28日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年5月28日.1	zhulh		2021年5月28日		Create
 * </pre>
 * @date 2021年5月28日
 */
public class TsTimerParamBuilder {

    // 计时配置UUID
    private String timerConfigUuid;

    // 计时方式
    private String timingMode;

    // 工作时间方案UUID
    private String workTimePlanUuid;

    // 开始时间
    private Date startTime;

    // 时限是否为指定日期
    private boolean isDateOfLimitTime;

    // 办理时限
    private Double timeLimit;

    // 截止时间
    private Date dueTime;

    // 监听器
    private String listener;

    // 预警信息
    private List<TsTimerParam.TsTimerAlarm> timerAlarms = Lists.newArrayListWithCapacity(0);

    // 附加数据
    private Map<String, Object> extraData = Maps.newHashMap();

    public static TsTimerParamBuilder create() {
        return new TsTimerParamBuilder();
    }

    public TsTimerParam build() {
        TsTimerParam timerParam = new TsTimerParam();
        timerParam.setTimerConfigUuid(timerConfigUuid);
        timerParam.setWorkTimePlanUuid(workTimePlanUuid);
        timerParam.setTimingMode(timingMode);
        timerParam.setStartTime(startTime);
        timerParam.setDateOfLimitTime(isDateOfLimitTime);
        timerParam.setTimeLimit(timeLimit);
        timerParam.setDueTime(dueTime);
        timerParam.setListener(listener);
        timerParam.setTimerAlarms(timerAlarms);
        timerParam.setExtraData(extraData);
        return timerParam;
    }

    /**
     * @param timerConfigUuid 要设置的timerConfigUuid
     */
    public TsTimerParamBuilder setTimerConfigUuid(String timerConfigUuid) {
        this.timerConfigUuid = timerConfigUuid;
        return this;
    }

    /**
     * @param timingMode 要设置的timingMode
     */
    public TsTimerParamBuilder setTimingMode(String timingMode) {
        this.timingMode = timingMode;
        return this;
    }

    /**
     * @param workTimePlanUuid 要设置的workTimePlanUuid
     */
    public TsTimerParamBuilder setWorkTimePlanUuid(String workTimePlanUuid) {
        this.workTimePlanUuid = workTimePlanUuid;
        return this;
    }


    /**
     * @param startTime 要设置的startTime
     */
    public TsTimerParamBuilder setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * @param isDateOfLimitTime 要设置的isDateOfLimitTime
     */
    public TsTimerParamBuilder setDateOfLimitTime(boolean isDateOfLimitTime) {
        this.isDateOfLimitTime = isDateOfLimitTime;
        return this;
    }

    /**
     * @param timeLimit 要设置的timeLimit
     */
    public TsTimerParamBuilder setTimeLimit(Double timeLimit) {
        this.timeLimit = timeLimit;
        return this;
    }

    /**
     * @param dueTime 要设置的dueTime
     */
    public TsTimerParamBuilder setDueTime(Date dueTime) {
        this.dueTime = dueTime;
        return this;
    }

    /**
     * @param listener 要设置的listener
     */
    public TsTimerParamBuilder setListener(String listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 添加预警信息
     *
     * @return
     */
    public TsTimerParamBuilder addAlarm(String id, double timeLimit, String timingMode, Integer alarmCount) {
        this.timerAlarms.add(new TsTimerParam.TsTimerAlarm(id, timeLimit, timingMode, alarmCount));
        return this;
    }

    /**
     * 添加附加信息
     *
     * @param key
     * @param value
     * @return
     */
    public TsTimerParamBuilder addExtra(String key, Object value) {
        this.extraData.put(key, value);
        return this;
    }

}
