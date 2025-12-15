/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 计时配置实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月7日.1	zhulh		2021年4月7日		Create
 * </pre>
 * @date 2021年4月7日
 */
@Entity
@Table(name = "TS_TIMER_CONFIG")
@DynamicUpdate
@DynamicInsert
@ApiModel("计时配置")
public class TsTimerConfigEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8963315850803018774L;

    // 名称
    @ApiModelProperty("名称")
    private String name;
    // ID
    @ApiModelProperty("ID")
    private String id;
    // 编号
    @ApiModelProperty("编号")
    private String code;
    // 计时配置分类UUID
    @ApiModelProperty("计时配置分类UUID")
    private String categoryUuid;
    // 计时方式
    @ApiModelProperty("计时方式")
    private String timingMode;
    // 计时方式类型: 1工作日、2工作日(一天24小时)、3自然日
    @ApiModelProperty("计时方式类型: 1工作日、2工作日(一天24小时)、3自然日")
    private String timingModeType;
    // 计时方式类型名称
    @ApiModelProperty("计时方式类型名称")
    private String timingModeTypeName;
    // 计时方式单位: 1按天、2按小时、3按分钟
    @ApiModelProperty("计时方式单位: 1按天、2按小时、3按分钟")
    private String timingModeUnit;
    // 计时方式单位名称
    @ApiModelProperty("计时方式单位名称")
    private String timingModeUnitName;
    // 时限类型
    @ApiModelProperty("时限类型")
    private String timeLimitType;
    // 时限类型名称
    @ApiModelProperty("时限类型名称")
    private String timeLimitTypeName;
    // 时限
    @ApiModelProperty("时限")
    private String timeLimit;
    // 时限单位
    @ApiModelProperty("时限单位")
    private String timeLimitUnit;
    // 计时包含启动时间点，1是0否，默认0
    @ApiModelProperty("计时包含启动时间点，1是0否，默认0")
    private Boolean includeStartTimePoint;
    // 自动推迟到下一工作时间起始点前，1是0否，默认0
    @ApiModelProperty("自动推迟到下一工作时间起始点前，1是0否，默认0")
    private Boolean autoDelay;
    // 计时监听
    @ApiModelProperty("计时监听")
    private String listener;
    // 备注
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("归属系统")
    private String system;
    @ApiModelProperty("归属租户")
    private String tenant;

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
     * @return the categoryUuid
     */
    public String getCategoryUuid() {
        return categoryUuid;
    }

    /**
     * @param categoryUuid 要设置的categoryUuid
     */
    public void setCategoryUuid(String categoryUuid) {
        this.categoryUuid = categoryUuid;
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
     * @return the timingModeType
     */
    public String getTimingModeType() {
        return timingModeType;
    }

    /**
     * @param timingModeType 要设置的timingModeType
     */
    public void setTimingModeType(String timingModeType) {
        this.timingModeType = timingModeType;
    }

    /**
     * @return the timingModeTypeName
     */
    public String getTimingModeTypeName() {
        return timingModeTypeName;
    }

    /**
     * @param timingModeTypeName 要设置的timingModeTypeName
     */
    public void setTimingModeTypeName(String timingModeTypeName) {
        this.timingModeTypeName = timingModeTypeName;
    }

    /**
     * @return the timingModeUnit
     */
    public String getTimingModeUnit() {
        return timingModeUnit;
    }

    /**
     * @param timingModeUnit 要设置的timingModeUnit
     */
    public void setTimingModeUnit(String timingModeUnit) {
        this.timingModeUnit = timingModeUnit;
    }

    /**
     * @return the timingModeUnitName
     */
    public String getTimingModeUnitName() {
        return timingModeUnitName;
    }

    /**
     * @param timingModeUnitName 要设置的timingModeUnitName
     */
    public void setTimingModeUnitName(String timingModeUnitName) {
        this.timingModeUnitName = timingModeUnitName;
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
     * @return the timeLimitTypeName
     */
    public String getTimeLimitTypeName() {
        return timeLimitTypeName;
    }

    /**
     * @param timeLimitTypeName 要设置的timeLimitTypeName
     */
    public void setTimeLimitTypeName(String timeLimitTypeName) {
        this.timeLimitTypeName = timeLimitTypeName;
    }

    /**
     * @return the timeLimit
     */
    public String getTimeLimit() {
        return timeLimit;
    }

    /**
     * @param timeLimit 要设置的timeLimit
     */
    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * @return the timeLimitUnit
     */
    public String getTimeLimitUnit() {
        return timeLimitUnit;
    }

    /**
     * @param timeLimitUnit 要设置的timeLimitUnit
     */
    public void setTimeLimitUnit(String timeLimitUnit) {
        this.timeLimitUnit = timeLimitUnit;
    }

    /**
     * @return the includeStartTimePoint
     */
    public Boolean getIncludeStartTimePoint() {
        return includeStartTimePoint;
    }

    /**
     * @param includeStartTimePoint 要设置的includeStartTimePoint
     */
    public void setIncludeStartTimePoint(Boolean includeStartTimePoint) {
        this.includeStartTimePoint = includeStartTimePoint;
    }

    /**
     * @return the autoDelay
     */
    public Boolean getAutoDelay() {
        return autoDelay;
    }

    /**
     * @param autoDelay 要设置的autoDelay
     */
    public void setAutoDelay(Boolean autoDelay) {
        this.autoDelay = autoDelay;
    }

    /**
     * @return the listener
     */
    public String getListener() {
        return listener;
    }

    /**
     * @param listener 要设置的listener
     */
    public void setListener(String listener) {
        this.listener = listener;
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
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
