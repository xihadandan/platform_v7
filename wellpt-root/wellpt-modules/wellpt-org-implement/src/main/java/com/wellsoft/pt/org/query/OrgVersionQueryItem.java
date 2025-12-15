/*
 * @(#)8/1/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
import com.wellsoft.pt.org.entity.BizOrganizationEntity;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8/1/23.1	zhulh		8/1/23		Create
 * </pre>
 * @date 8/1/23
 */
public class OrgVersionQueryItem implements BaseQueryItem {
    private String id;
    private Long orgUuid;
    private String orgId;
    private String orgName;
    private List<BizOrganizationEntity> bizOrgs;

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
     * @return the orgId
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * @param orgId 要设置的orgId
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * @return the orgName
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * @param orgName 要设置的orgName
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public List<BizOrganizationEntity> getBizOrgs() {
        return bizOrgs;
    }

    public void setBizOrgs(List<BizOrganizationEntity> bizOrgs) {
        this.bizOrgs = bizOrgs;
    }
}
