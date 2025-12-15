/*
 * @(#)4/18/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.entity;

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
 * 4/18/25.1	    zhulh		4/18/25		    Create
 * </pre>
 * @date 4/18/25
 */
@javax.persistence.Entity
@Table(name = "dingtalk_dept")
@DynamicUpdate
@DynamicInsert
public class DingtalkDeptEntity extends Entity {
    private static final long serialVersionUID = -810018454248756845L;

    // 钉钉配置信息Uuid
    private Long configUuid;

    // 行政组织UUID
    private Long orgUuid;

    // 行政组织版本UUID
    private Long orgVersionUuid;

    // 应用的App ID
    private String appId;

    // OA系统节点UUID
    private Long orgElementUuid;

    // OA系统节点ID
    private String orgElementId;

    // 部门名称
    private String name;

    // 部门ID，由钉钉服务器生成，在创建部门时返回
    private Long deptId;

    // 父部门的ID，在根部门下创建新部门，该参数值为 1
    private Long parentId;

    // 部门状态，0正常，1删除
    private Status status;

    // 扩展信息
    private String ext;

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
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId 要设置的appId
     */
    public void setAppId(String appId) {
        this.appId = appId;
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
     * @return the deptId
     */
    public Long getDeptId() {
        return deptId;
    }

    /**
     * @param deptId 要设置的deptId
     */
    public void setDeptId(Long deptId) {
        this.deptId = deptId;
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

    /**
     * @return the ext
     */
    public String getExt() {
        return ext;
    }

    /**
     * @param ext 要设置的ext
     */
    public void setExt(String ext) {
        this.ext = ext;
    }

    public enum Status {
        NORMAL,
        DELETED,
    }
}
