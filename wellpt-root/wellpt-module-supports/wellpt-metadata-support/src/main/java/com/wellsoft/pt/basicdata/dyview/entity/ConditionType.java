/*
 * @(#)2013-3-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


/**
 * Description: 备选项实体类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-27.1	wubin		2013-3-27		Create
 * </pre>
 * @date 2013-3-27
 */
@Entity
@Table(name = "dyview_select_conditionType")
@DynamicUpdate
@DynamicInsert
public class ConditionType extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 36880229824060154L;

    private String name; //显示值

    private String conditionValue; //真实值

    private String conditionScope; //条件备选项的来源

    private String conditionName; //条件名称

    private String appointColumn; //条件指定的列

    private String appointColumnType; //条件指定的列的字段类型

    private Integer sortOrder;//条件排序

    @UnCloneable
    private SelectDefinition selectDefinition;

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
     * @return the appointColumnType
     */
    public String getAppointColumnType() {
        return appointColumnType;
    }

    /**
     * @param appointColumnType 要设置的appointColumnType
     */
    public void setAppointColumnType(String appointColumnType) {
        this.appointColumnType = appointColumnType;
    }

    /**
     * @return the conditionScope
     */
    public String getConditionScope() {
        return conditionScope;
    }

    /**
     * @param conditionScope 要设置的conditionScope
     */
    public void setConditionScope(String conditionScope) {
        this.conditionScope = conditionScope;
    }

    /**
     * @return the conditionName
     */
    public String getConditionName() {
        return conditionName;
    }

    /**
     * @param conditionName 要设置的conditionName
     */
    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    /**
     * @return the appointColumn
     */
    public String getAppointColumn() {
        return appointColumn;
    }

    /**
     * @param appointColumn 要设置的appointColumn
     */
    public void setAppointColumn(String appointColumn) {
        this.appointColumn = appointColumn;
    }

    /**
     * @return the selectDefinition
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "select_def_uuid", nullable = false)
    public SelectDefinition getSelectDefinition() {
        return selectDefinition;
    }

    /**
     * @param selectDefinition 要设置的selectDefinition
     */
    public void setSelectDefinition(SelectDefinition selectDefinition) {
        this.selectDefinition = selectDefinition;
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
     * @return the conditionValue
     */
    public String getConditionValue() {
        return conditionValue;
    }

    /**
     * @param conditionValue 要设置的conditionValue
     */
    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

}
