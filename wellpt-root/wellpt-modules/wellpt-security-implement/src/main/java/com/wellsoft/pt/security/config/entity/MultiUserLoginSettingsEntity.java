/*
 * @(#)2021-11-18 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.config.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Description: 数据库表MULTI_USER_LOGIN_SETTINGS的实体类
 *
 * @author baozh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-11-18.1	baozh		2021-11-18		Create
 * </pre>
 * @date 2021-11-18
 */
@Entity
@Table(name = "MULTI_USER_LOGIN_SETTINGS")
@DynamicUpdate
@DynamicInsert
public class MultiUserLoginSettingsEntity extends TenantEntity {

    private static final long serialVersionUID = 1637225044568L;

    // 是否启用邮箱
    private int emailEnable;
    // 英文名别名
    private String nameEnAlias;
    // 账户别名
    private String accountAlias;
    // 是否启用手机号
    private int tellEnable;
    // 中文账户别名
    private String accountZhAlias;
    // 是否启用身份证号
    private int identifierCodeEnable;
    // 是否启用员工编号
    private int empCodeEnable;
    // 员工编号别名
    private String empCodeAlias;
    // 邮箱别名
    private String emailAlias;
    // 手机号别名
    private String tellAlias;
    // 是否启用英文名
    private int nameEnEnable;
    // 是否启用中文账户
    private int accountZhEnable;
    // 身份证号别名
    private String identifierCodeAlias;

    /**
     * @return the emailEnable
     */
    public int getEmailEnable() {
        return this.emailEnable;
    }

    /**
     * @param emailEnable
     */
    public void setEmailEnable(int emailEnable) {
        this.emailEnable = emailEnable;
    }

    /**
     * @return the nameEnAlias
     */
    public String getNameEnAlias() {
        return this.nameEnAlias;
    }

    /**
     * @param nameEnAlias
     */
    public void setNameEnAlias(String nameEnAlias) {
        this.nameEnAlias = nameEnAlias;
    }

    /**
     * @return the accountAlias
     */
    public String getAccountAlias() {
        return this.accountAlias;
    }

    /**
     * @param accountAlias
     */
    public void setAccountAlias(String accountAlias) {
        this.accountAlias = accountAlias;
    }

    /**
     * @return the tellEnable
     */
    public int getTellEnable() {
        return this.tellEnable;
    }

    /**
     * @param tellEnable
     */
    public void setTellEnable(int tellEnable) {
        this.tellEnable = tellEnable;
    }

    /**
     * @return the accountZhAlias
     */
    public String getAccountZhAlias() {
        return this.accountZhAlias;
    }

    /**
     * @param accountZhAlias
     */
    public void setAccountZhAlias(String accountZhAlias) {
        this.accountZhAlias = accountZhAlias;
    }

    /**
     * @return the identifierCodeEnable
     */
    public int getIdentifierCodeEnable() {
        return this.identifierCodeEnable;
    }

    /**
     * @param identifierCodeEnable
     */
    public void setIdentifierCodeEnable(int identifierCodeEnable) {
        this.identifierCodeEnable = identifierCodeEnable;
    }

    /**
     * @return the empCodeEnable
     */
    public int getEmpCodeEnable() {
        return this.empCodeEnable;
    }

    /**
     * @param empCodeEnable
     */
    public void setEmpCodeEnable(int empCodeEnable) {
        this.empCodeEnable = empCodeEnable;
    }

    /**
     * @return the empCodeAlias
     */
    public String getEmpCodeAlias() {
        return this.empCodeAlias;
    }

    /**
     * @param empCodeAlias
     */
    public void setEmpCodeAlias(String empCodeAlias) {
        this.empCodeAlias = empCodeAlias;
    }

    /**
     * @return the emailAlias
     */
    public String getEmailAlias() {
        return this.emailAlias;
    }

    /**
     * @param emailAlias
     */
    public void setEmailAlias(String emailAlias) {
        this.emailAlias = emailAlias;
    }

    /**
     * @return the tellAlias
     */
    public String getTellAlias() {
        return this.tellAlias;
    }

    /**
     * @param tellAlias
     */
    public void setTellAlias(String tellAlias) {
        this.tellAlias = tellAlias;
    }

    /**
     * @return the nameEnEnable
     */
    public int getNameEnEnable() {
        return this.nameEnEnable;
    }

    /**
     * @param nameEnEnable
     */
    public void setNameEnEnable(int nameEnEnable) {
        this.nameEnEnable = nameEnEnable;
    }

    /**
     * @return the accountZhEnable
     */
    public int getAccountZhEnable() {
        return this.accountZhEnable;
    }

    /**
     * @param accountZhEnable
     */
    public void setAccountZhEnable(int accountZhEnable) {
        this.accountZhEnable = accountZhEnable;
    }

    /**
     * @return the identifierCodeAlias
     */
    public String getIdentifierCodeAlias() {
        return this.identifierCodeAlias;
    }

    /**
     * @param identifierCodeAlias
     */
    public void setIdentifierCodeAlias(String identifierCodeAlias) {
        this.identifierCodeAlias = identifierCodeAlias;
    }

}
