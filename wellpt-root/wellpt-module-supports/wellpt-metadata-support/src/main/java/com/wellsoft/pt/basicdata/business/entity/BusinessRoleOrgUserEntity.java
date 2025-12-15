/*
 * @(#)2019-03-01 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Description: 数据库表BUSINESS_ROLE_ORG_USER的实体类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-03-01.1	leo		2019-03-01		Create
 * </pre>
 * @date 2019-03-01
 */
@Entity
@Table(name = "BUSINESS_ROLE_ORG_USER")
@DynamicUpdate
@DynamicInsert
public class BusinessRoleOrgUserEntity extends IdEntity {

    private static final long serialVersionUID = 1551423356625L;

    // 部门id
    private String businessCategoryOrgUuid;

    private String usersValue;
    // 角色id
    private String businessRoleUuid;
    // 成员(多选)
    private String users;


    /**
     * @return the usersValue
     */
    public String getUsersValue() {
        return this.usersValue;
    }

    /**
     * @param usersValue
     */
    public void setUsersValue(String usersValue) {
        this.usersValue = usersValue;
    }

    public String getBusinessCategoryOrgUuid() {
        return businessCategoryOrgUuid;
    }

    public void setBusinessCategoryOrgUuid(String businessCategoryOrgUuid) {
        this.businessCategoryOrgUuid = businessCategoryOrgUuid;
    }

    public String getBusinessRoleUuid() {
        return businessRoleUuid;
    }

    public void setBusinessRoleUuid(String businessRoleUuid) {
        this.businessRoleUuid = businessRoleUuid;
    }

    /**
     * @return the users
     */
    public String getUsers() {
        return this.users;
    }

    /**
     * @param users
     */
    public void setUsers(String users) {
        this.users = users;
    }

}
