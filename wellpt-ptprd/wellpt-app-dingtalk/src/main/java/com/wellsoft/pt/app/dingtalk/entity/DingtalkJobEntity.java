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
@Table(name = "dingtalk_job")
@DynamicUpdate
@DynamicInsert
public class DingtalkJobEntity extends Entity {
    private static final long serialVersionUID = 5862013586664373499L;

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

    // 职位名称
    private String title;

    // 职位所在部门ID
    private Long deptId;

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
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
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

}
