/*
 * @(#)8/15/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/15/24.1	    zhulh		8/15/24		    Create
 * </pre>
 * @date 8/15/24
 */
public class BizProcessDefinitionQueryItem implements BaseQueryItem {
    private static final long serialVersionUID = 1459419995138254319L;
    
    // UUID
    private String uuid;

    // 名称
    private String name;

    // ID
    private String id;

    // 编号
    private String code;

    // 版本
    private Double version;

    // 是否启用
    private Boolean enabled;

    // 业务ID
    private String businessId;

    // 业务标签ID
    private String tagId;

    // 备注
    private String remark;

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
     * @return the version
     */
    public Double getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(Double version) {
        this.version = version;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the businessId
     */
    public String getBusinessId() {
        return businessId;
    }

    /**
     * @param businessId 要设置的businessId
     */
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * @return the tagId
     */
    public String getTagId() {
        return tagId;
    }

    /**
     * @param tagId 要设置的tagId
     */
    public void setTagId(String tagId) {
        this.tagId = tagId;
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

}
