/*
 * @(#)2015-1-12 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.unit.support;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-12.1	zhulh		2015-1-12		Create
 * </pre>
 * @date 2015-1-12
 */
public class BusinessUnitRole implements Serializable {

    public static final String CHANGE_ADDED = "added";
    public static final String CHANGE_DELETED = "deleted";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6988611058290321909L;
    // 单位ID
    private String unitId;
    // 业务类别ID
    private String bizTypeId;
    // 业务角色ID
    private String bizRoleId;
    // 业务角色名称
    private String bizRoleName;

    // 变化的类型
    private String changeType;
    // 变化的成员ID
    private String memberId;
    // 变化的成员名称
    private String memberName;

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

    /**
     * @return the bizTypeId
     */
    public String getBizTypeId() {
        return bizTypeId;
    }

    /**
     * @param bizTypeId 要设置的bizTypeId
     */
    public void setBizTypeId(String bizTypeId) {
        this.bizTypeId = bizTypeId;
    }

    /**
     * @return the bizRoleId
     */
    public String getBizRoleId() {
        return bizRoleId;
    }

    /**
     * @param bizRoleId 要设置的bizRoleId
     */
    public void setBizRoleId(String bizRoleId) {
        this.bizRoleId = bizRoleId;
    }

    /**
     * @return the bizRoleName
     */
    public String getBizRoleName() {
        return bizRoleName;
    }

    /**
     * @param bizRoleName 要设置的bizRoleName
     */
    public void setBizRoleName(String bizRoleName) {
        this.bizRoleName = bizRoleName;
    }

    /**
     * @return the changeType
     */
    public String getChangeType() {
        return changeType;
    }

    /**
     * @param changeType 要设置的changeType
     */
    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    /**
     * @return the memberId
     */
    public String getMemberId() {
        return memberId;
    }

    /**
     * @param memberId 要设置的memberId
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    /**
     * @return the memberName
     */
    public String getMemberName() {
        return memberName;
    }

    /**
     * @param memberName 要设置的memberName
     */
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

}
