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
 * Description: 节假日安排实体类
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
@Table(name = "TS_HOLIDAY_SCHEDULE")
@DynamicUpdate
@DynamicInsert
@ApiModel("节假日安排")
public class TsHolidayScheduleEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6979509881539478730L;

    // 引用的节假日UUID
    @ApiModelProperty("引用的节假日UUID")
    private String holidayUuid;
    // 年份
    @ApiModelProperty("年份")
    private String year;
    // 开始日期
    @ApiModelProperty("开始日期")
    private String fromDate;
    // 结束日期
    @ApiModelProperty("结束日期")
    private String toDate;
    // 补班日期，多个以分号隔开
    @ApiModelProperty("补班日期，格式yyyy-MM-dd|yyyy-MM-dd，多个以分号隔开")
    private String makeupDate;
    // 备注
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("归属系统")
    private String system;
    @ApiModelProperty("归属租户")
    private String tenant;

    /**
     * @return the holidayUuid
     */
    public String getHolidayUuid() {
        return holidayUuid;
    }

    /**
     * @param holidayUuid 要设置的holidayUuid
     */
    public void setHolidayUuid(String holidayUuid) {
        this.holidayUuid = holidayUuid;
    }

    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year 要设置的year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the fromDate
     */
    public String getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate 要设置的fromDate
     */
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the toDate
     */
    public String getToDate() {
        return toDate;
    }

    /**
     * @param toDate 要设置的toDate
     */
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    /**
     * @return the makeupDate
     */
    public String getMakeupDate() {
        return makeupDate;
    }

    /**
     * @param makeupDate 要设置的makeupDate
     */
    public void setMakeupDate(String makeupDate) {
        this.makeupDate = makeupDate;
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
