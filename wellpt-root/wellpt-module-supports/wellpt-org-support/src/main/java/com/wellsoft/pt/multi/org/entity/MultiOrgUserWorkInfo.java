/*
 * @(#)2017-12-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-12-11.1	zyguo		2017-12-11		Create
 * </pre>
 * @date 2017-12-11
 */
@Entity
@Table(name = "MULTI_ORG_USER_WORK_INFO")
@DynamicUpdate
@DynamicInsert
public class MultiOrgUserWorkInfo extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1512981137416L;

    // 对应的USER_ID
    private String userId;
    // 职位id多个用;分隔
    private String jobIds;
    // 职级多个用;分隔
    private String jobRank;
    // 职档多个用;分隔
    private String jobLevel;
    // 部门id多个用;分隔
    private String deptIds;
    // 节点全路径多个用;分隔
    private String eleIdPaths;
    // 直属上级领导多个用;分隔
    private String directLeaderIds;
    // 默认工作台UUID
    private String defPageUuid;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobIds() {
        return jobIds;
    }

    public void setJobIds(String jobIds) {
        this.jobIds = jobIds;
    }

    public String getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String deptIds) {
        this.deptIds = deptIds;
    }

    public String getEleIdPaths() {
        return eleIdPaths;
    }

    public void setEleIdPaths(String eleIdPaths) {
        this.eleIdPaths = eleIdPaths;
    }

    public String getDirectLeaderIds() {
        return directLeaderIds;
    }

    public void setDirectLeaderIds(String directLeaderIds) {
        this.directLeaderIds = directLeaderIds;
    }

    public String getDefPageUuid() {
        return defPageUuid;
    }

    public void setDefPageUuid(String defPageUuid) {
        this.defPageUuid = defPageUuid;
    }

    public String getJobRank() {
        return jobRank;
    }

    public void setJobRank(String jobRank) {
        this.jobRank = jobRank;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }
}
