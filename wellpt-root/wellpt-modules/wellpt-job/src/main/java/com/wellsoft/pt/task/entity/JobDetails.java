/*
 * @(#)2013-9-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.context.validator.RemoteUnique;
import com.wellsoft.pt.task.constant.JobConstant;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-9-17.1	zhulh		2013-9-17		Create
 * </pre>
 * @date 2013-9-17
 */
@Entity
@Table(name = "task_job_details")
@DynamicUpdate
@DynamicInsert
public class JobDetails extends TenantEntity {

    private static final long serialVersionUID = 2745137050271598717L;

    // 任务名
    @NotBlank
    private String name;

    // ID
    @NotBlank
    @RemoteUnique
    private String id;

    // 编号
    @NotBlank
    private String code;

    // 任务类名
    @NotBlank
    private String jobClassName;

    // 任务类型：定时、临时
    private String type;

    // CRON表达式
    private String expression;

    // 临时任务重复时间间隔(毫秒)
    private Long repeatInterval;

    // 备注
    private String remark;

    // 租户ID
    private String tenantId;

    // 下次执行时间
    private Date nextFireTime;
    // 任务状态
    private Integer state;

    // 定时方式(1、每天，2、每周，3、每月、4、每季、5、每年，6、时间间隔)
    private Integer timingMode;

    // 重复日期(每周)
    private String repeatDayOfWeek;

    // 重复日期(每月)
    private String repeatDayOfMonth;

    // 重复日期(每季)
    private String repeatDayOfSeason;

    // 重复日期(每年)
    private String repeatDayOfYear;

    // 时间点(HH:mm:ss)
    private String timePoint;

    // 重复间隔时间
    private String repeatIntervalTime;

    // 临时任务重复次数
    private Integer repeatCount;

    // 任务开始时间
    private Date startTime;

    // 任务结束时间
    private Date endTime;

    private String starter;

    private String instanceName;

    // 自动调度
    private Boolean autoScheduling;

    private String moduleId;

    private String assignIp;//指定ip执行

    private String lastExecuteInstance;//最近执行的机器名

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
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the jobClassName
     */
    public String getJobClassName() {
        return jobClassName;
    }

    /**
     * @param jobClassName 要设置的jobClassName
     */
    public void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
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
    public Integer getRepeatCount() {
        return repeatCount;
    }

    /**
     * @param repeatCount 要设置的repeatCount
     */
    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    /**
     * @return the repeatInterval
     */
    public Long getRepeatInterval() {
        return repeatInterval;
    }

    /**
     * @param repeatInterval 要设置的repeatInterval
     */
    public void setRepeatInterval(Long repeatInterval) {
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
     * @return the nextFireTime
     */
    @Transient
    public Date getNextFireTime() {
        return nextFireTime;
    }

    /**
     * @param nextFireTime 要设置的nextFireTime
     */
    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    /**
     * @return the state
     */
    @Transient
    public Integer getState() {
        return state;
    }

    /**
     * @param state 要设置的state
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * @return the timingMode
     */
    public Integer getTimingMode() {
        return timingMode;
    }

    /**
     * @param timingMode 要设置的timingMode
     */
    public void setTimingMode(Integer timingMode) {
        this.timingMode = timingMode;
    }

    /**
     * @return the repeatDayOfWeek
     */
    public String getRepeatDayOfWeek() {
        return repeatDayOfWeek;
    }

    /**
     * @param repeatDayOfWeek 要设置的repeatDayOfWeek
     */
    public void setRepeatDayOfWeek(String repeatDayOfWeek) {
        this.repeatDayOfWeek = repeatDayOfWeek;
    }

    /**
     * @return the repeatDayOfMonth
     */
    public String getRepeatDayOfMonth() {
        return repeatDayOfMonth;
    }

    /**
     * @param repeatDayOfMonth 要设置的repeatDayOfMonth
     */
    public void setRepeatDayOfMonth(String repeatDayOfMonth) {
        this.repeatDayOfMonth = repeatDayOfMonth;
    }

    /**
     * @return the repeatDayOfSeason
     */
    public String getRepeatDayOfSeason() {
        return repeatDayOfSeason;
    }

    /**
     * @param repeatDayOfSeason 要设置的repeatDayOfSeason
     */
    public void setRepeatDayOfSeason(String repeatDayOfSeason) {
        this.repeatDayOfSeason = repeatDayOfSeason;
    }

    /**
     * @return the repeatDayOfYear
     */
    public String getRepeatDayOfYear() {
        return repeatDayOfYear;
    }

    /**
     * @param repeatDayOfYear 要设置的repeatDayOfYear
     */
    public void setRepeatDayOfYear(String repeatDayOfYear) {
        this.repeatDayOfYear = repeatDayOfYear;
    }

    /**
     * @return the timePoint
     */
    public String getTimePoint() {
        return timePoint;
    }

    /**
     * @param timePoint 要设置的timePoint
     */
    public void setTimePoint(String timePoint) {
        this.timePoint = timePoint;
    }

    public String getRepeatIntervalTime() {
        return repeatIntervalTime;
    }

    public void setRepeatIntervalTime(String repeatIntervalTime) {
        this.repeatIntervalTime = repeatIntervalTime;
    }

    public String getStarter() {
        return starter;
    }

    public void setStarter(String starter) {
        this.starter = starter;
    }

    @Transient
    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    /**
     * @return the autoScheduling
     */
    public Boolean getAutoScheduling() {
        return autoScheduling;
    }

    /**
     * @param autoScheduling 要设置的autoScheduling
     */
    public void setAutoScheduling(Boolean autoScheduling) {
        this.autoScheduling = autoScheduling;
    }

    /**
     * 获取任务的执行规则描述
     *
     * @return
     */
    @Transient
    public String getQrtzCronDescription() {
        StringBuilder description = new StringBuilder("");
        if (this.getTimingMode() != null) {
            switch (this.getTimingMode()) {
                case 1:
                    description.append("每天/");
                    description.append(this.getTimePoint());
                    break;
                case 2:
                    description.append("每周/");
                    if (StringUtils.isNotBlank(this.getRepeatDayOfWeek())) {
                        String[] days = this.getRepeatDayOfWeek().split(",");
                        for (String d : days) {
                            description.append(JobConstant.engWeekDay2ChnDay.get(d) + ",");
                        }
                        description.deleteCharAt(description.lastIndexOf(","));
                        description.append("/" + this.getTimePoint());
                    }
                    break;
                case 3:
                    description.append("每月/");
                    if (StringUtils.isNotBlank(this.getRepeatDayOfMonth())) {
                        description.append(this.getRepeatDayOfMonth().replaceFirst("LW", "每月最后一天"));
                        description.append("/" + this.getTimePoint());
                    }
                    break;
                case 4:
                    description.append("每季/");
                    if (StringUtils.isNotBlank(this.getRepeatDayOfSeason())) {
                        String[] days = this.getRepeatDayOfSeason().replaceFirst("L", "最后一").split(
                                "\\s");
                        if (days.length == 2) {
                            description.append(String.format("第%s月%s日/", days[1], days[0]));
                        }
                        description.append(this.getTimePoint());
                    }
                    break;
                case 5:
                    description.append("每年/");
                    if (StringUtils.isNotBlank(this.getRepeatDayOfYear())) {
                        String[] days = this.getRepeatDayOfYear().replaceFirst("L", "最后一").split(
                                "\\s");
                        if (days.length == 2) {
                            description.append(String.format("%s月%s日/", days[1], days[0]));
                        }
                        description.append(this.getTimePoint());
                    }
                    break;
                case 6:
                    description.append("时间间隔/");
                    description.append(this.getRepeatIntervalTime());
                    if (this.getRepeatCount() != null) {
                        switch (this.getRepeatCount()) {
                            case -1:
                                description.append("/无限重复执行");
                                break;
                            case 0:
                                description.append("/不执行");
                                break;
                            default:
                                description.append("/重复执行" + this.getRepeatCount() + "次");
                        }
                    }
                    break;
                default:
                    break;
            }


        }

        return description.toString();
    }


    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getAssignIp() {
        return assignIp;
    }

    public void setAssignIp(String assignIp) {
        this.assignIp = assignIp;
    }

    public String getLastExecuteInstance() {
        return lastExecuteInstance;
    }

    public void setLastExecuteInstance(String lastExecuteInstance) {
        this.lastExecuteInstance = lastExecuteInstance;
    }
}
