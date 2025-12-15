/*
 * @(#)5/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/21/25.1	    zhulh		5/21/25		    Create
 * </pre>
 * @date 5/21/25
 */
@javax.persistence.Entity
@Table(name = "weixin_dept")
@DynamicUpdate
@DynamicInsert
public class WeixinDeptEntity extends Entity {

    private static final long serialVersionUID = -6746530261520912602L;

    // 微信配置信息Uuid
    private Long configUuid;

    // 行政组织UUID
    private Long orgUuid;

    // 行政组织版本UUID
    private Long orgVersionUuid;

    // 企业ID
    private String corpId;

    // OA系统节点UUID
    private Long orgElementUuid;

    // OA系统节点ID
    private String orgElementId;

    // 部门名称
    private String name;

    // 部门ID
    private Long id;

    // 父部门的ID，在根部门下创建新部门，该参数值为 1
    private Long parentId;

    // 在父部门中的次序值
    private Long sortOrder;

    // 部门负责人的UserID
    private String departmentLeaders;

    // 部门状态，0正常，1删除
    private Status status;

    /**
     * @return the configUuid
     */
    public Long getConfigUuid() {
        return configUuid;
    }

    /**
     * @param configUuid 要设置的configUuid
     */
    public void setConfigUuid(Long configUuid) {
        this.configUuid = configUuid;
    }

    /**
     * @return the orgUuid
     */
    public Long getOrgUuid() {
        return orgUuid;
    }

    /**
     * @param orgUuid 要设置的orgUuid
     */
    public void setOrgUuid(Long orgUuid) {
        this.orgUuid = orgUuid;
    }

    /**
     * @return the orgVersionUuid
     */
    public Long getOrgVersionUuid() {
        return orgVersionUuid;
    }

    /**
     * @param orgVersionUuid 要设置的orgVersionUuid
     */
    public void setOrgVersionUuid(Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    /**
     * @return the corpId
     */
    public String getCorpId() {
        return corpId;
    }

    /**
     * @param corpId 要设置的corpId
     */
    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    /**
     * @return the orgElementUuid
     */
    public Long getOrgElementUuid() {
        return orgElementUuid;
    }

    /**
     * @param orgElementUuid 要设置的orgElementUuid
     */
    public void setOrgElementUuid(Long orgElementUuid) {
        this.orgElementUuid = orgElementUuid;
    }

    /**
     * @return the orgElementId
     */
    public String getOrgElementId() {
        return orgElementId;
    }

    /**
     * @param orgElementId 要设置的orgElementId
     */
    public void setOrgElementId(String orgElementId) {
        this.orgElementId = orgElementId;
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
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the parentId
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId 要设置的parentId
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the sortOrder
     */
    public Long getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the departmentLeaders
     */
    public String getDepartmentLeaders() {
        return departmentLeaders;
    }

    /**
     * @param departmentLeaders 要设置的departmentLeaders
     */
    public void setDepartmentLeaders(String departmentLeaders) {
        this.departmentLeaders = departmentLeaders;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status 要设置的status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        NORMAL,
        DELETED,
    }
}
