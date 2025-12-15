/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 节假日历史实例实体类
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
@Table(name = "TS_HOLIDAY_INSTANCE")
@DynamicUpdate
@DynamicInsert
public class TsHolidayInstanceEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6943341304830837661L;

    // 节假日UUID
    private String holidayUuid;
    // 名称
    private String name;
    // ID
    private String id;
    // 历法类型1：阳历2：阴历
    private String calendarType;
    // 节假日，真实值存储对应的数字，用符号-连接，如阳历5月20日为5-20，阴历五月二十为5-20
    private String holidayDate;
    // 年份
    private String year;
    // 实例日期
    private String instanceDate;

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
     * @return the instanceDate
     */
    public String getInstanceDate() {
        return instanceDate;
    }

    /**
     * @param instanceDate 要设置的instanceDate
     */
    public void setInstanceDate(String instanceDate) {
        this.instanceDate = instanceDate;
    }

}
