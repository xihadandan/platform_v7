/*
 * @(#)2022-01-12 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * Description: 数据库表MULTI_USER_RELATION_ACCOUNT的实体类
 *
 * @author baozh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-01-12.1	baozh		2022-01-12		Create
 * </pre>
 * @date 2022-01-12
 */
@Entity
@Table(name = "MULTI_USER_RELATION_ACCOUNT")
@DynamicUpdate
@DynamicInsert
public class MultiUserRelationAccountEntity extends TenantEntity {

    private static final long serialVersionUID = 1641971628824L;

    // 主账户
    private String masterAccount;
    // 是否有效
    private Integer isValid;
    // 关联账户
    private String relationAccount;
    // 关联账户ID
    private String relationAccountId;
    // 关联系统ID
    private String relationSystemUnitId;

    // 关联系统名称
    private String relationSystemUnitName;

    private String loginAddr;

    /**
     * @return the masterAccount
     */
    public String getMasterAccount() {
        return this.masterAccount;
    }

    /**
     * @param masterAccount
     */
    public void setMasterAccount(String masterAccount) {
        this.masterAccount = masterAccount;
    }

    /**
     * @return the isValid
     */
    public Integer getIsValid() {
        return this.isValid;
    }

    /**
     * @param isValid
     */
    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    /**
     * @return the relationAccount
     */
    public String getRelationAccount() {
        return this.relationAccount;
    }

    /**
     * @param relationAccount
     */
    public void setRelationAccount(String relationAccount) {
        this.relationAccount = relationAccount;
    }

    public String getRelationAccountId() {
        return relationAccountId;
    }

    public void setRelationAccountId(String relationAccountId) {
        this.relationAccountId = relationAccountId;
    }

    public String getRelationSystemUnitId() {
        return relationSystemUnitId;
    }

    public void setRelationSystemUnitId(String relationSystemUnitId) {
        this.relationSystemUnitId = relationSystemUnitId;
    }

    @Transient
    public String getLoginAddr() {
        return loginAddr;
    }

    public void setLoginAddr(String loginAddr) {
        this.loginAddr = loginAddr;
    }

    @Transient
    public String getRelationSystemUnitName() {
        return relationSystemUnitName;
    }

    public void setRelationSystemUnitName(String relationSystemUnitName) {
        this.relationSystemUnitName = relationSystemUnitName;
    }
}
