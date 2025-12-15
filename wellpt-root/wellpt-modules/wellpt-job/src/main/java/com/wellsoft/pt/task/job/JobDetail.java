/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.job;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 界面任务配置实体类，放入公共库
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-29.1	zhulh		2012-10-29		Create
 * </pre>
 * @date 2012-10-29
 */
public class JobDetail implements Serializable {
    // 定时任务 Cron Expressions
    public static final String TYPE_TIMING = "timing";
    // 临时任务 SimpleTrigger
    public static final String TYPE_TEMPORARY = "temporary";
    private static final long serialVersionUID = 7679542553854185967L;
    // 任务名
    private String name;
    private String jobUuid;

    // 任务类型：定时、临时
    private String type = TYPE_TEMPORARY;
    // 任务类名
    private Class<?> jobClass;
    private boolean volatility = false;
    private boolean durability = false;
    private boolean recover = false;
    private boolean autoScheduling = false;
    // 定时任务调度表达式
    private String expression;
    // 临时任务开始时间
    private Date startTime;
    // 临时任务结束时间
    private Date endTime;
    // 临时任务重复次数
    private int repeatCount = 0;
    // 临时任务重复时间间隔(毫秒)
    private long repeatInterval = 0l;
    // 任务描述
    private String remark;
    // 租户ID，用作quartz任务调度的任务组、触发器组名
    private String tenantId;
    // 用户ID，用于虚拟登录
    private String userId;
    // 任务名称，quartz.properties中配置
    private String schedulerName;

    private String assignIp;//指定ip

    // 任务数据
    private JobData jobData;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the jobClass
     */
    public Class<?> getJobClass() {
        return jobClass;
    }

    /**
     * @param jobClass 要设置的jobClass
     */
    public void setJobClass(Class<?> jobClass) {
        this.jobClass = jobClass;
    }

    /**
     * @return the volatility
     */
    public boolean isVolatility() {
        return volatility;
    }

    /**
     * @param volatility 要设置的volatility
     */
    public void setVolatility(boolean volatility) {
        this.volatility = volatility;
    }

    /**
     * @return the durability
     */
    public boolean isDurability() {
        return durability;
    }

    /**
     * @param durability 要设置的durability
     */
    public void setDurability(boolean durability) {
        this.durability = durability;
    }

    /**
     * @return the recover
     */
    public boolean isRecover() {
        return recover;
    }

    /**
     * @param recover 要设置的recover
     */
    public void setRecover(boolean recover) {
        this.recover = recover;
    }

    /**
     * @return the autoScheduling
     */
    public boolean isAutoScheduling() {
        return autoScheduling;
    }

    /**
     * @param autoScheduling 要设置的autoScheduling
     */
    public void setAutoScheduling(boolean autoScheduling) {
        this.autoScheduling = autoScheduling;
    }

    /**
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @param expression 要设置的expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 要设置的startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the repeatCount
     */
    public int getRepeatCount() {
        return repeatCount;
    }

    /**
     * @param repeatCount 要设置的repeatCount
     */
    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    /**
     * @return the repeatInterval
     */
    public long getRepeatInterval() {
        return repeatInterval;
    }

    /**
     * @param repeatInterval 要设置的repeatInterval
     */
    public void setRepeatInterval(long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId 要设置的tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the jobData
     */
    public JobData getJobData() {
        if (jobData == null) {
            jobData = new JobData();
        }
        jobData.put("type", type);
        jobData.put("tenantId", tenantId);
        jobData.put("userId", userId);
        jobData.put("jobUuid", jobUuid);
        jobData.put("assignIp", assignIp);
        return jobData;
    }

    /**
     * @param jobData 要设置的jobData
     */
    public void setJobData(JobData jobData) {
        this.jobData = jobData;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public String getJobUuid() {
        return jobUuid;
    }

    public void setJobUuid(String jobUuid) {
        this.jobUuid = jobUuid;
    }

    public String getAssignIp() {
        return assignIp;
    }

    public void setAssignIp(String assignIp) {
        this.assignIp = assignIp;
    }
}
