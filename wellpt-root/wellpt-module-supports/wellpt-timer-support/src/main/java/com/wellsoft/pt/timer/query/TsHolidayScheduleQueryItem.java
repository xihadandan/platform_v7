/*
 * @(#)2021年5月27日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年5月27日.1	zhulh		2021年5月27日		Create
 * </pre>
 * @date 2021年5月27日
 */
public class TsHolidayScheduleQueryItem implements BaseQueryItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8325218038867263184L;

    // UUID
    @ApiModelProperty("uuid")
    protected String uuid;
    // 引用的节假日UUID
    @ApiModelProperty("引用的节假日UUID")
    private String holidayUuid;
    // 节假日名称
    @ApiModelProperty("节假日名称")
    private String holidayName;
    // 具体年份节假日日期，格式yyyy-MM-dd
    @ApiModelProperty("具体年份节假日实例日期，格式yyyy-MM-dd")
    private String holidayInstanceDate;
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
    @ApiModelProperty("补班日期，多个以分号隔开")
    private String makeupDate;
    // 备注
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("归属系统")
    private String system;
    @ApiModelProperty("归属租户")
    private String tenant;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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
     * @return the holidayName
     */
    public String getHolidayName() {
        return holidayName;
    }

    /**
     * @param holidayName 要设置的holidayName
     */
    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    /**
     * @return the holidayInstanceDate
     */
    public String getHolidayInstanceDate() {
        return holidayInstanceDate;
    }

    /**
     * @param holidayInstanceDate 要设置的holidayInstanceDate
     */
    public void setHolidayInstanceDate(String holidayInstanceDate) {
        this.holidayInstanceDate = holidayInstanceDate;
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
