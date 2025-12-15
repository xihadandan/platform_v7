/*
 * @(#)2012-11-12 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.workhour.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 工作时间实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-12.1	zhulh		2012-11-12		Create
 * </pre>
 * @date 2012-11-12
 */
@Entity
@Table(name = "cd_work_hour")
@DynamicUpdate
@DynamicInsert
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entity")
public class WorkHour extends IdEntity {

    public static final String TIME_MIN = "00:00:00";
    public static final String TIME_MAX = "23:59:59";
    /**
     * 工作时间类型常量
     */
    public static final String TYPE_WORK_DAY = "Workday";//每周工作日
    public static final String TYPE_FIXED_HOLIDAYS = "Fixed";//年度固定节假日
    public static final String TYPE_SPECIAL_HOLIDAYS = "Special";//特殊节假日
    public static final String TYPE_MAKE_UP = "Makeup";//补班日期
    private static final long serialVersionUID = 8082791703533075664L;
    /**
     * 工作时间类型(每周工作日、年度固定节假日、特殊节假日、补班日期)
     */
    private String type;
    /**
     * 名称(星期一、元旦...)
     */
    private String name;
    /**
     * 名称对应的编号
     */
    private String code;
    /**
     * 是否为工作日
     */
    private Boolean isWorkday;
    /**
     * 开始日期
     */
    private String fromDate;
    /**
     * 结束日期
     */
    private String toDate;
    /**
     * 上午上班时间
     */
    private String fromTime1;
    /**
     * 上午下班时间
     */
    private String toTime1;
    /**
     * 下午上班时间
     */
    private String fromTime2;
    /**
     * 下午下班时间
     */
    private String toTime2;
    /**
     * 排序
     */
    private Integer sortOrder;
    /**
     * 备注
     */
    private String remark;
    /**
     * 单位
     */
    private String unitId;

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
     * @return the isWorkday
     */
    public Boolean getIsWorkday() {
        return isWorkday;
    }

    /**
     * @param isWorkday 要设置的isWorkday
     */
    public void setIsWorkday(Boolean isWorkday) {
        this.isWorkday = isWorkday;
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
     * @return the fromTime1
     */
    public String getFromTime1() {
        return fromTime1;
    }

    /**
     * @param fromTime1 要设置的fromTime1
     */
    public void setFromTime1(String fromTime1) {
        this.fromTime1 = fromTime1;
    }

    /**
     * @return the toTime1
     */
    public String getToTime1() {
        return toTime1;
    }

    /**
     * @param toTime1 要设置的toTime1
     */
    public void setToTime1(String toTime1) {
        this.toTime1 = toTime1;
    }

    /**
     * @return the fromTime2
     */
    public String getFromTime2() {
        return fromTime2;
    }

    /**
     * @param fromTime2 要设置的fromTime2
     */
    public void setFromTime2(String fromTime2) {
        this.fromTime2 = fromTime2;
    }

    /**
     * @return the toTime2
     */
    public String getToTime2() {
        return toTime2;
    }

    /**
     * @param toTime2 要设置的toTime2
     */
    public void setToTime2(String toTime2) {
        this.toTime2 = toTime2;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
     * @return the unitId
     */
    public String getUnitId() {
        return unitId;
    }

    /**
     * @param unitId 要设置的unitId
     */
    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

}
