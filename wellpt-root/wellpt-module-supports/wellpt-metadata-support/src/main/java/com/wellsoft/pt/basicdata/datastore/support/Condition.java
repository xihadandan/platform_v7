/*
 * @(#)2017年4月26日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description: 查询条件类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年4月26日.1	zhulh		2017年4月26日		Create
 * </pre>
 * @date 2017年4月26日
 */
@ApiModel("查询条件")
public class Condition extends BaseObject {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7866606071247670057L;

    @ApiModelProperty("列索引")
    private String columnIndex;
    @ApiModelProperty("条件值")
    private Object value;
    @ApiModelProperty("条件类型")
    private String type;
    @ApiModelProperty("SQL语句")
    private String sql;
    // 自定义查询条件，spring对应的bean名称
    @ApiModelProperty("自定义查询条件，spring对应的bean名称")
    private String customCriterion;

    @ApiModelProperty("嵌套的条件列表")
    private List<Condition> conditions;

    /**
     *
     */
    public Condition() {
        super();
    }

    /**
     * @param columnIndex
     * @param value
     * @param type
     */
    public Condition(String columnIndex, Object value, CriterionOperator operator) {
        super();
        this.columnIndex = columnIndex;
        this.value = value;
        this.type = operator.getType();
    }

    /**
     * @param columnIndex
     * @param value
     * @param type
     */
    public Condition(String columnIndex, Object value, String type) {
        super();
        this.columnIndex = columnIndex;
        this.value = value;
        this.type = type;
    }

    /**
     * @return the columnIndex
     */
    public String getColumnIndex() {
        return columnIndex;
    }

    /**
     * @param columnIndex 要设置的columnIndex
     */
    public void setColumnIndex(String columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(Object value) {
        this.value = value;
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
     * @return the sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * @param sql 要设置的sql
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * @return the conditions
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * @param conditions 要设置的conditions
     */
    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    /**
     * @return the customCriterion
     */
    public String getCustomCriterion() {
        return customCriterion;
    }

    /**
     * @param customCriterion 要设置的customCriterion
     */
    public void setCustomCriterion(String customCriterion) {
        this.customCriterion = customCriterion;
    }

}
