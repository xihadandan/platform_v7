/*
 * @(#)12/10/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/10/24.1	    zhulh		12/10/24		    Create
 * </pre>
 * @date 12/10/24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MultiOrgElement extends BaseObject {
    private static final long serialVersionUID = -881757983293493257L;

    // 使用的组织ID
    private String orgId;
    // 可用业务组织，none不可用，all全部业务组织，assign指定业务组织
    private String availableBizOrg;
    // 指定业务组织，多个以分号隔开
    private String bizOrgId;

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
     * @return the availableBizOrg
     */
    public String getAvailableBizOrg() {
        return availableBizOrg;
    }

    /**
     * @param availableBizOrg 要设置的availableBizOrg
     */
    public void setAvailableBizOrg(String availableBizOrg) {
        this.availableBizOrg = availableBizOrg;
    }

    /**
     * @return the bizOrgId
     */
    public String getBizOrgId() {
        return bizOrgId;
    }

    /**
     * @param bizOrgId 要设置的bizOrgId
     */
    public void setBizOrgId(String bizOrgId) {
        this.bizOrgId = bizOrgId;
    }
}
