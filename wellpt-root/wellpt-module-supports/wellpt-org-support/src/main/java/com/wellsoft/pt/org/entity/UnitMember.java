/*
 * @(#)2013-4-12 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Description: 组织单元成员
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-12.1	zhulh		2013-4-12		Create
 * </pre>
 * @date 2013-4-12
 */
//@Entity
//@Table(name = "org_unit_member")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class UnitMember extends IdEntity {

    public static final String MEMBER_TYPE_USER = "1";
    public static final String MEMBER_TYPE_EMAIL = "2";
    private static final long serialVersionUID = 3731803922852799979L;
    private String tenantId;
    // 成员所在的单元
    @UnCloneable
    private Unit unit;
    // 业务类型
    private String businessType;
    // 成员类型
    private String memberType;
    // 成员及邮件地址
    private String member;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the unit
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_uuid")
    public Unit getUnit() {
        return unit;
    }

    /**
     * @param unit 要设置的unit
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * @return the businessType
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * @param businessType 要设置的businessType
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /**
     * @return the memberType
     */
    public String getMemberType() {
        return memberType;
    }

    /**
     * @param memberType 要设置的memberType
     */
    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    /**
     * @return the member
     */
    public String getMember() {
        return member;
    }

    /**
     * @param member 要设置的member
     */
    public void setMember(String member) {
        this.member = member;
    }

}
