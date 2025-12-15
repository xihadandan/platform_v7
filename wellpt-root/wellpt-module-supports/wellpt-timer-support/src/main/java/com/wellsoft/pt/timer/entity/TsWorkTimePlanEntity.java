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
import java.util.Date;

/**
 * Description: 工作时间方案实体类
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
@Table(name = "TS_WORK_TIME_PLAN")
@DynamicUpdate
@DynamicInsert
@ApiModel("工作时间方案")
public class TsWorkTimePlanEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7568150986813066943L;

    // 名称
    @ApiModelProperty("名称")
    private String name;
    // ID
    @ApiModelProperty("ID，自动生成")
    private String id;
    // 编号
    @ApiModelProperty("编号")
    private String code;
    // 版本号
    @ApiModelProperty("版本号")
    private String version;
    // 是否默认工作时间方案，1是0否
    @ApiModelProperty("是否默认工作时间方案，1是0否")
    private Boolean isDefault;
    // 状态0未生效、1已生效、2已失效
    @ApiModelProperty("状态0未生效、1已生效、2已失效")
    private Integer status;
    // 生效时间
    @ApiModelProperty("生效时间")
    private Date activeTime;
    // 失效时间
    @ApiModelProperty("失效时间")
    private Date deactiveTime;
    // 工作时间安排，json数组对象[{工作时间安排1}, {工作时间安排2}...]
    @ApiModelProperty("工作时间安排，json数组对象[{工作时间安排1}, {工作时间安排2}...]")
    private String workTimeSchedule;
    // 节假日安排，json数组对象[{节假日安排1}, {节假日安排2}...]
    @ApiModelProperty("节假日安排，json数组对象[{节假日安排1}, {节假日安排2}...]")
    private String holidaySchedule;

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
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault 要设置的isDefault
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status 要设置的status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return the activeTime
     */
    public Date getActiveTime() {
        return activeTime;
    }

    /**
     * @param activeTime 要设置的activeTime
     */
    public void setActiveTime(Date activeTime) {
        this.activeTime = activeTime;
    }

    /**
     * @return the deactiveTime
     */
    public Date getDeactiveTime() {
        return deactiveTime;
    }

    /**
     * @param deactiveTime 要设置的deactiveTime
     */
    public void setDeactiveTime(Date deactiveTime) {
        this.deactiveTime = deactiveTime;
    }

    /**
     * @return the workTimeSchedule
     */
    public String getWorkTimeSchedule() {
        return workTimeSchedule;
    }

    /**
     * @param workTimeSchedule 要设置的workTimeSchedule
     */
    public void setWorkTimeSchedule(String workTimeSchedule) {
        this.workTimeSchedule = workTimeSchedule;
    }

    /**
     * @return the holidaySchedule
     */
    public String getHolidaySchedule() {
        return holidaySchedule;
    }

    /**
     * @param holidaySchedule 要设置的holidaySchedule
     */
    public void setHolidaySchedule(String holidaySchedule) {
        this.holidaySchedule = holidaySchedule;
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
