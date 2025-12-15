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
 * Description: 节假日实体类
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
@Table(name = "TS_HOLIDAY")
@DynamicUpdate
@DynamicInsert
@ApiModel("节假日管理")
public class TsHolidayEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3240702826652336516L;

    // 名称
    @ApiModelProperty("名称")
    private String name;
    // ID
    @ApiModelProperty("ID")
    private String id;
    // 历法类型1：阳历2：阴历
    @ApiModelProperty("历法类型1：阳历2：阴历")
    private String calendarType;
    // 节假日，真实值存储对应的数字，用符号-连接，如阳历5月20日为5-20，阴历五月二十为5-20
    @ApiModelProperty("节假日，真实值存储对应的数字，用符号-连接，如阳历5月20日为5-20，阴历五月二十为5-20")
    private String holidayDate;
    // 节假日日期名称
    @ApiModelProperty("节假日日期名称")
    private String holidayDateName;
    // 标签，取枚举类EnumHolidayTag，多个以分号隔开
    @ApiModelProperty("标签，取枚举类EnumHolidayTag，多个以分号隔开")
    private String tag;
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
     * @return the calendarType
     */
    public String getCalendarType() {
        return calendarType;
    }

    /**
     * @param calendarType 要设置的calendarType
     */
    public void setCalendarType(String calendarType) {
        this.calendarType = calendarType;
    }

    /**
     * @return the holidayDate
     */
    public String getHolidayDate() {
        return holidayDate;
    }

    /**
     * @param holidayDate 要设置的holidayDate
     */
    public void setHolidayDate(String holidayDate) {
        this.holidayDate = holidayDate;
    }

    /**
     * @return the holidayDateName
     */
    public String getHolidayDateName() {
        return holidayDateName;
    }

    /**
     * @param holidayDateName 要设置的holidayDateName
     */
    public void setHolidayDateName(String holidayDateName) {
        this.holidayDateName = holidayDateName;
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag 要设置的tag
     */
    public void setTag(String tag) {
        this.tag = tag;
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
