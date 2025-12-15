/*
 * @(#)2019-02-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据库表BUSINESS_CATEGORY_ORG的实体类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-02-14.1	leo		2019-02-14		Create
 * </pre>
 * @date 2019-02-14
 */
@Entity
@Table(name = "BUSINESS_CATEGORY_ORG")
@DynamicUpdate
@DynamicInsert
public class BusinessCategoryOrgEntity extends IdEntity {

    public final static String TYPE_1 = "1";
    public final static String TYPE_2 = "2";
    private static final long serialVersionUID = 1550125081057L;
    // 编号
    @NotBlank
    private String code;
    // 单位value
    private String unitValue;

    @NotBlank
    private String businessCategoryUuid;
    // 单位key
    private String unit;
    // 部门value
    private String deptValue;
    // 部门key
    private String dept;
    // 管理员key值
    private String manageUser;
    // 管理员value值
    private String manageUserValue;
    // 父节点
    @NotBlank
    private String parentUuid;
    // 类型(1单位，2分类)
    @NotBlank
    private String type;

    // 业务ID
    private String id;
    // 名称
    @NotBlank
    private String name;

    /**
     * @return the code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the unitValue
     */
    public String getUnitValue() {
        return this.unitValue;
    }

    /**
     * @param unitValue
     */
    public void setUnitValue(String unitValue) {
        this.unitValue = unitValue;
    }

    /**
     * @return the businessCategoryUuid
     */
    public String getBusinessCategoryUuid() {
        return this.businessCategoryUuid;
    }

    /**
     * @param businessCategoryUuid
     */
    public void setBusinessCategoryUuid(String businessCategoryUuid) {
        this.businessCategoryUuid = businessCategoryUuid;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return this.unit;
    }

    /**
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return the deptValue
     */
    public String getDeptValue() {
        return this.deptValue;
    }

    /**
     * @param deptValue
     */
    public void setDeptValue(String deptValue) {
        this.deptValue = deptValue;
    }

    /**
     * @return the dept
     */
    public String getDept() {
        return this.dept;
    }

    /**
     * @param dept
     */
    public void setDept(String dept) {
        this.dept = dept;
    }

    /**
     * @return the parentUuid
     */
    public String getParentUuid() {
        return this.parentUuid;
    }

    /**
     * @param parentUuid
     */
    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManageUser() {
        return manageUser;
    }

    public void setManageUser(String manageUser) {
        this.manageUser = manageUser;
    }

    public String getManageUserValue() {
        return manageUserValue;
    }

    public void setManageUserValue(String manageUserValue) {
        this.manageUserValue = manageUserValue;
    }
}
