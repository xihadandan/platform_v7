package com.wellsoft.pt.unit.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * 公共库单位实体
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Entity
@Table(name = "unit_common_unit")
@DynamicUpdate
@DynamicInsert
public class CommonUnit extends IdEntity {
    private static final long serialVersionUID = 4878959943022329929L;

    //标识
    private String id;

    //名称
    private String name;

    //社区代码
    private String communityId;

    //组织机构代码
    private String orgCode;

    //简称
    private String shortName;

    //编号
    private String code;

    //邮箱地址
    private String email;

    //备注
    private String remark;

    //租户
    private String tenantId;

    private String unitId;
    @JsonIgnore
    @UnCloneable
    private Set<BusinessType> businessTypes = new HashSet<BusinessType>(0);
    @JsonIgnore
    @UnCloneable
    private Set<CommonUser> users = new HashSet<CommonUser>(0);
    @JsonIgnore
    @UnCloneable
    private Set<CommonDepartment> departments = new HashSet<CommonDepartment>(0);
    @JsonIgnore
    @UnCloneable
    private Set<CommonUnitTree> unitTrees = new HashSet<CommonUnitTree>(0);
    @JsonIgnore
    @UnCloneable
    private Set<BusinessUnitTree> businessUnitTrees = new HashSet<BusinessUnitTree>(0);

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

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    @OneToMany(mappedBy = "unit", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    public Set<BusinessType> getBusinessTypes() {
        return businessTypes;
    }

    public void setBusinessTypes(Set<BusinessType> businessTypes) {
        this.businessTypes = businessTypes;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @OneToMany(mappedBy = "unit", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    public Set<CommonUser> getUsers() {
        return users;
    }

    public void setUsers(Set<CommonUser> users) {
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName 要设置的shortName
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
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

    @OneToMany(mappedBy = "unit", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    public Set<CommonDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<CommonDepartment> departments) {
        this.departments = departments;
    }

    @OneToMany(mappedBy = "unit", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    public Set<CommonUnitTree> getUnitTrees() {
        return unitTrees;
    }

    public void setUnitTrees(Set<CommonUnitTree> unitTrees) {
        this.unitTrees = unitTrees;
    }

    @OneToMany(mappedBy = "unit", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    public Set<BusinessUnitTree> getBusinessUnitTrees() {
        return businessUnitTrees;
    }

    public void setBusinessUnitTrees(Set<BusinessUnitTree> businessUnitTrees) {
        this.businessUnitTrees = businessUnitTrees;
    }
}
