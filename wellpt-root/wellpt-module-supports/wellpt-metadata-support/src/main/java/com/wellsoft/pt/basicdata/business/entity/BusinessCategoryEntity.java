/*
 * @(#)2019-02-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据库表BUSINESS_CATEGORY的实体类
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
@Table(name = "BUSINESS_CATEGORY")
@DynamicUpdate
@DynamicInsert
public class BusinessCategoryEntity extends TenantEntity {

    private static final long serialVersionUID = 1550125139810L;

    // 编号
    @NotBlank
    private String code;
    // 管理员key值
    private String manageUser;
    // 管理单位key值
    @NotBlank
    private String manageDept;
    // 管理单位value值
    private String manageDeptValue;
    // id
    @NotBlank
    private String id;
    // 管理员value值
    private String manageUserValue;
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
     * @return the manageUser
     */
    public String getManageUser() {
        return this.manageUser;
    }

    /**
     * @param manageUser
     */
    public void setManageUser(String manageUser) {
        this.manageUser = manageUser;
    }

    /**
     * @return the manageDept
     */
    public String getManageDept() {
        return this.manageDept;
    }

    /**
     * @param manageDept
     */
    public void setManageDept(String manageDept) {
        this.manageDept = manageDept;
    }

    /**
     * @return the manageDeptValue
     */
    public String getManageDeptValue() {
        return this.manageDeptValue;
    }

    /**
     * @param manageDeptValue
     */
    public void setManageDeptValue(String manageDeptValue) {
        this.manageDeptValue = manageDeptValue;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the manageUserValue
     */
    public String getManageUserValue() {
        return this.manageUserValue;
    }

    /**
     * @param manageUserValue
     */
    public void setManageUserValue(String manageUserValue) {
        this.manageUserValue = manageUserValue;
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

}
