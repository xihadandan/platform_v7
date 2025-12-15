/*
 * @(#)4/24/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 4/24/24.1	zhulh		4/24/24		Create
 * </pre>
 * @date 4/24/24
 */
@Entity
@Table(name = "WF_FLOW_SETTING")
@DynamicUpdate
@DynamicInsert
public class WfFlowSettingEntity extends SysEntity {
    private static final long serialVersionUID = 7735769859998311394L;

    private String attrKey;
    private String attrVal;
    private Boolean enabled;
    private String category;
    private String remark;

    /**
     * @return the attrKey
     */
    public String getAttrKey() {
        return attrKey;
    }

    /**
     * @param attrKey 要设置的attrKey
     */
    public void setAttrKey(String attrKey) {
        this.attrKey = attrKey;
    }

    /**
     * @return the attrVal
     */
    public String getAttrVal() {
        return attrVal;
    }

    /**
     * @param attrVal 要设置的attrVal
     */
    public void setAttrVal(String attrVal) {
        this.attrVal = attrVal;
    }

    /**
     * @return the enabled
     */
    @Column(name = "IS_ENABLED")
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
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category 要设置的category
     */
    public void setCategory(String category) {
        this.category = category;
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
