/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.bean;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-12-19.1	zhulh		2017-12-19		Create
 * </pre>
 * @date 2017-12-19
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DmsFolderAssignRoleBean extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1513679281309L;

    // rowid
    private String uuid;
    // 操作UUID
    private String roleUuid;
    // 操作名称
    private String roleName;
    // 分配的组织相关ID
    private String orgIds;
    // 分配的组织相关名称
    private String orgNames;
    // 允许
    private String permit;
    // 允许名称
    private String permitName;
    // 拒绝
    private String deny;
    // 拒绝名称
    private String denyName;
    // 排序号
    private Integer sortOrder;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the roleUuid
     */
    public String getRoleUuid() {
        return roleUuid;
    }

    /**
     * @param roleUuid 要设置的roleUuid
     */
    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    /**
     * @return the roleName
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName 要设置的roleName
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * @return the orgIds
     */
    public String getOrgIds() {
        return orgIds;
    }

    /**
     * @param orgIds 要设置的orgIds
     */
    public void setOrgIds(String orgIds) {
        this.orgIds = orgIds;
    }

    /**
     * @return the orgNames
     */
    public String getOrgNames() {
        return orgNames;
    }

    /**
     * @param orgNames 要设置的orgNames
     */
    public void setOrgNames(String orgNames) {
        this.orgNames = orgNames;
    }

    /**
     * @return the permit
     */
    public String getPermit() {
        return permit;
    }

    /**
     * @param permit 要设置的permit
     */
    public void setPermit(String permit) {
        this.permit = permit;
    }

    /**
     * @return the permitName
     */
    public String getPermitName() {
        return permitName;
    }

    /**
     * @param permitName 要设置的permitName
     */
    public void setPermitName(String permitName) {
        this.permitName = permitName;
    }

    /**
     * @return the deny
     */
    public String getDeny() {
        return deny;
    }

    /**
     * @param deny 要设置的deny
     */
    public void setDeny(String deny) {
        this.deny = deny;
    }

    /**
     * @return the denyName
     */
    public String getDenyName() {
        return denyName;
    }

    /**
     * @param denyName 要设置的denyName
     */
    public void setDenyName(String denyName) {
        this.denyName = denyName;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

}
