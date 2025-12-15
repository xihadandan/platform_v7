/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.bean;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: DepartmentBean.java
 *
 * @author zhulh
 * @date 2012-12-23
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-23.1	zhulh		2012-12-23		Create
 * </pre>
 */
public class DepartmentBean extends Department {

    /**
     *
     */
    private static final long serialVersionUID = -3334112145634467985L;

    // 父部门名称
    private String parentName;

    // 父部门Id
    private String parentId;

    // 部门负责人名称
    private String principalLeaderNames;

    // 部门负责人Id
    private String principalLeaderIds;

    //分管领导名称
    private String branchedLeaderNames;

    //分管领导Id
    private String branchedLeaderIds;

    //管理员名称
    private String managerNames;

    //管理员Id
    private String managerIds;

    //职能uuid
    private String functionUuids;
    @UnCloneable
    private Set<Role> roles = new HashSet<Role>(0);
    @UnCloneable
    private Set<Privilege> privileges = new HashSet<Privilege>();

    public String getFunctionUuids() {
        return functionUuids;
    }

    public void setFunctionUuids(String functionUuids) {
        this.functionUuids = functionUuids;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * @return the parentName
     */
    public String getParentName() {
        return parentName;
    }

    /**
     * @param parentName 要设置的parentName
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * @return the parentId
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * @param parentId 要设置的parentId
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the principalLeaderNames
     */
    public String getPrincipalLeaderNames() {
        return principalLeaderNames;
    }

    /**
     * @param principalLeaderNames 要设置的principalLeaderNames
     */
    public void setPrincipalLeaderNames(String principalLeaderNames) {
        this.principalLeaderNames = principalLeaderNames;
    }

    /**
     * @return the principalLeaderIds
     */
    public String getPrincipalLeaderIds() {
        return principalLeaderIds;
    }

    /**
     * @param principalLeaderIds 要设置的principalLeaderIds
     */
    public void setPrincipalLeaderIds(String principalLeaderIds) {
        this.principalLeaderIds = principalLeaderIds;
    }

    /**
     * @return the branchedLeaderNames
     */
    public String getBranchedLeaderNames() {
        return branchedLeaderNames;
    }

    /**
     * @param branchedLeaderNames 要设置的branchedLeaderNames
     */
    public void setBranchedLeaderNames(String branchedLeaderNames) {
        this.branchedLeaderNames = branchedLeaderNames;
    }

    /**
     * @return the branchedLeaderIds
     */
    public String getBranchedLeaderIds() {
        return branchedLeaderIds;
    }

    /**
     * @param branchedLeaderIds 要设置的branchedLeaderIds
     */
    public void setBranchedLeaderIds(String branchedLeaderIds) {
        this.branchedLeaderIds = branchedLeaderIds;
    }

    /**
     * @return the managerNames
     */
    public String getManagerNames() {
        return managerNames;
    }

    /**
     * @param managerNames 要设置的managerNames
     */
    public void setManagerNames(String managerNames) {
        this.managerNames = managerNames;
    }

    /**
     * @return the managerIds
     */
    public String getManagerIds() {
        return managerIds;
    }

    /**
     * @param managerIds 要设置的managerIds
     */
    public void setManagerIds(String managerIds) {
        this.managerIds = managerIds;
    }

}
