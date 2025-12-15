/*
 * @(#)2019年9月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据权限定义实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年9月29日.1	zhulh		2019年9月29日		Create
 * </pre>
 * @date 2019年9月29日
 */
@Entity
@Table(name = "dms_data_permission_definition")
@DynamicUpdate
@DynamicInsert
public class DmsDataPermissionDefinitionEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1614673820573831668L;

    // 数据权限名称
    @NotBlank
    private String name;

    // ID
    @NotBlank
    private String id;

    // 编号
    private String code;

    // 数据类型，1数据库表，2数据库视图
    @NotBlank
    private String type;

    // 数据名称，数据库表名或数据库视图名
    private String dataName;

    // 数据规则定义JSON
    private String ruleDefinition;

    // 数据范围定义JSON
    private String rangeDefinition;

    // 备注
    private String remark;

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
     * @return the dataName
     */
    public String getDataName() {
        return dataName;
    }

    /**
     * @param dataName 要设置的dataName
     */
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    /**
     * @return the ruleDefinition
     */
    public String getRuleDefinition() {
        return ruleDefinition;
    }

    /**
     * @param ruleDefinition 要设置的ruleDefinition
     */
    public void setRuleDefinition(String ruleDefinition) {
        this.ruleDefinition = ruleDefinition;
    }

    /**
     * @return the rangeDefinition
     */
    public String getRangeDefinition() {
        return rangeDefinition;
    }

    /**
     * @param rangeDefinition 要设置的rangeDefinition
     */
    public void setRangeDefinition(String rangeDefinition) {
        this.rangeDefinition = rangeDefinition;
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

}
