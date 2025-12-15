/*
 * @(#)2013-2-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: 组织单元实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-18.1	zhulh		2013-2-18		Create
 * </pre>
 * @date 2013-2-18
 */
//@Entity
//@Table(name = "org_unit")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class Unit extends IdEntity {

    private static final long serialVersionUID = 4038875956764030333L;
    //组织单元ID
    private String id;
    // 类型，1为分类、2为组织单元
    private String type;
    // 组织单元分类
    private String category;
    // 组织单元名称
    private String name;
    // 编号
    private String code;
    // 组织单元备注
    private String remark;
    private String tenantId;
    // 父单元
    @UnCloneable
    private Unit parent;
    // 子结点
    @UnCloneable
    private List<Unit> children = new ArrayList<Unit>(0);
    // 组织单元成员-用户
    @UnCloneable
    private Set<UnitMember> members = new HashSet<UnitMember>(0);

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category 要设置的category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the parent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_uuid")
    public Unit getParent() {
        return parent;
    }

    /**
     * @param parent 要设置的parent
     */
    public void setParent(Unit parent) {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade({CascadeType.ALL})
    @OrderBy("code asc")
    public List<Unit> getChildren() {
        return children;
    }

    /**
     * @param children 要设置的children
     */
    public void setChildren(List<Unit> children) {
        this.children = children;
    }

    /**
     * @return the members
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "unit")
    @Cascade({CascadeType.ALL})
    @OrderBy("createTime asc")
    public Set<UnitMember> getMembers() {
        return members;
    }

    /**
     * @param members 要设置的members
     */
    public void setMembers(Set<UnitMember> members) {
        this.members = members;
    }

}
