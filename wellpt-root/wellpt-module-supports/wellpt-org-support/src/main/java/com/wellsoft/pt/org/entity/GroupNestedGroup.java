/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * Description: 群组选择群组
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-10-9.1  zhengky	2014-10-9	  Create
 * </pre>
 * @date 2014-10-9
 */
//@Entity
//@Table(name = "org_group_nested_group")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class GroupNestedGroup extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2808701034037438932L;

    private Group group;

    private Group nestedGroup;

    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the group
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.REFRESH})
    @JoinColumn(name = "group_uuid")
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public Group getGroup() {
        return group;
    }

    /**
     * @param group 要设置的group
     */
    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * @return the nestedGroup
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nested_group_uuid")
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public Group getNestedGroup() {
        return nestedGroup;
    }

    /**
     * @param nestedGroup 要设置的nestedGroup
     */
    public void setNestedGroup(Group nestedGroup) {
        this.nestedGroup = nestedGroup;
    }

}
