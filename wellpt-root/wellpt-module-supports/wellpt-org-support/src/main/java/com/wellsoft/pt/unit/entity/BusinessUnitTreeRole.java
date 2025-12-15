/*
 * @(#)2014-10-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.unit.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-13.1	zhulh		2014-10-13		Create
 * </pre>
 * @date 2014-10-13
 */
@Entity
@Table(name = "unit_business_tree_role")
@DynamicUpdate
@DynamicInsert
public class BusinessUnitTreeRole extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3985147135344423466L;

    // 业务树UUID
    private String businessUnitTreeUuid;
    // 业务角色ID
    private String businessRoleId;
    // 业务角色名称
    private String businessRoleName;
    // 成员ID
    private String memberId;
    // 成员名称
    private String memberName;

    /**
     * @return the businessUnitTreeUuid
     */
    public String getBusinessUnitTreeUuid() {
        return businessUnitTreeUuid;
    }

    /**
     * @param businessUnitTreeUuid 要设置的businessUnitTreeUuid
     */
    public void setBusinessUnitTreeUuid(String businessUnitTreeUuid) {
        this.businessUnitTreeUuid = businessUnitTreeUuid;
    }

    /**
     * @return the businessRoleId
     */
    public String getBusinessRoleId() {
        return businessRoleId;
    }

    /**
     * @param businessRoleId 要设置的businessRoleId
     */
    public void setBusinessRoleId(String businessRoleId) {
        this.businessRoleId = businessRoleId;
    }

    /**
     * @return the businessRoleName
     */
    public String getBusinessRoleName() {
        return businessRoleName;
    }

    /**
     * @param businessRoleName 要设置的businessRoleName
     */
    public void setBusinessRoleName(String businessRoleName) {
        this.businessRoleName = businessRoleName;
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

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((businessRoleId == null) ? 0 : businessRoleId.hashCode());
        result = prime * result + ((businessUnitTreeUuid == null) ? 0 : businessUnitTreeUuid.hashCode());
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        BusinessUnitTreeRole other = (BusinessUnitTreeRole) obj;
        if (businessRoleId == null) {
            if (other.businessRoleId != null)
                return false;
        } else if (!businessRoleId.equals(other.businessRoleId))
            return false;
        if (businessUnitTreeUuid == null) {
            if (other.businessUnitTreeUuid != null)
                return false;
        } else if (!businessUnitTreeUuid.equals(other.businessUnitTreeUuid))
            return false;
        return true;
    }

}
