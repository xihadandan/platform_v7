package com.wellsoft.pt.unit.bean;

import com.wellsoft.pt.unit.entity.CommonUnitTree;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Description: CommonUnitTreeBean VO
 *
 * @author liuzq
 * @date 2013-11-5
 */
public class CommonUnitTreeBean extends CommonUnitTree {
    private static final long serialVersionUID = -402914264899586984L;

    //编号
    @NotBlank
    private String code;

    //邮箱地址
    @Email
    private String email;

    //备注
    private String remark;

    // 组织单元UUId
    private String unitUuid;

    private String unitId;

    // 组织单元名称
    @NotBlank
    private String unitName;

    // 组织单元简称
    @NotBlank
    private String unitShortName;

    private String parentName;

    private String parentUuid;

    private String tenantId;

    private String tenantName;

    @NotBlank
    private String id;
    //社区代码
    private String communityId;
    //组织机构代码
    private String orgCode;

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getUnitUuid() {
        return unitUuid;
    }

    public void setUnitUuid(String unitUuid) {
        this.unitUuid = unitUuid;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    /**
     * @return the unitShortName
     */
    public String getUnitShortName() {
        return unitShortName;
    }

    /**
     * @param unitShortName 要设置的unitShortName
     */
    public void setUnitShortName(String unitShortName) {
        this.unitShortName = unitShortName;
    }

}
