/*
 * @(#)2019-02-14 V1.0
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
 * Description: 数据库表BUSINESS_ROLE的实体类
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
@Table(name = "BUSINESS_ROLE")
@DynamicUpdate
@DynamicInsert
public class BusinessRoleEntity extends IdEntity {

    private static final long serialVersionUID = 1550125121637L;


    private String businessCategoryUuid;
    // 成员(多选)
    private String users;

    // 成员(多选)
    private String usersValue;

    // 名称
    private String name;

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

    public String getUsersValue() {
        return usersValue;
    }

    public void setUsersValue(String usersValue) {
        this.usersValue = usersValue;
    }


}
