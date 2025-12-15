package com.wellsoft.pt.org.dto;

import com.wellsoft.pt.org.entity.BizOrgElementEntity;
import com.wellsoft.pt.org.entity.BizOrgElementPathEntity;
import com.wellsoft.pt.org.entity.BizOrgRoleEntity;
import com.wellsoft.pt.org.entity.BizOrganizationEntity;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年12月09日   chenq	 Create
 * </pre>
 */
public class BizOrgElementMemberDto implements Serializable {

    private static final long serialVersionUID = 8523847843238408847L;
    private Long bizOrgUuid;
    private String memberId;
    private String bizOrgElementId;
    private String bizOrgRoleId;

    private BizOrganizationEntity bizOrg;
    private BizOrgElementEntity bizOrgElement;
    private BizOrgRoleEntity bizOrgRole;
    private BizOrgElementPathEntity bizOrgElementPath;

    public Long getBizOrgUuid() {
        return bizOrgUuid;
    }

    public void setBizOrgUuid(Long bizOrgUuid) {
        this.bizOrgUuid = bizOrgUuid;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getBizOrgElementId() {
        return bizOrgElementId;
    }

    public void setBizOrgElementId(String bizOrgElementId) {
        this.bizOrgElementId = bizOrgElementId;
    }

    public String getBizOrgRoleId() {
        return bizOrgRoleId;
    }

    public void setBizOrgRoleId(String bizOrgRoleId) {
        this.bizOrgRoleId = bizOrgRoleId;
    }

    public BizOrganizationEntity getBizOrg() {
        return bizOrg;
    }

    public void setBizOrg(BizOrganizationEntity bizOrg) {
        this.bizOrg = bizOrg;
    }

    public BizOrgElementEntity getBizOrgElement() {
        return bizOrgElement;
    }

    public void setBizOrgElement(BizOrgElementEntity bizOrgElement) {
        this.bizOrgElement = bizOrgElement;
    }

    public BizOrgRoleEntity getBizOrgRole() {
        return bizOrgRole;
    }

    public void setBizOrgRole(BizOrgRoleEntity bizOrgRole) {
        this.bizOrgRole = bizOrgRole;
    }

    public BizOrgElementPathEntity getBizOrgElementPath() {
        return bizOrgElementPath;
    }

    public void setBizOrgElementPath(BizOrgElementPathEntity bizOrgElementPath) {
        this.bizOrgElementPath = bizOrgElementPath;
    }
}
