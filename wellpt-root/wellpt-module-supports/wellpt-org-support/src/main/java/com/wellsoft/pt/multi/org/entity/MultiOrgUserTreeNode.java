/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
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
 * 2017-11-21.1	zyguo		2017-11-21		Create
 * </pre>
 * @date 2017-11-21
 */
@Entity
@Table(name = "MULTI_ORG_USER_TREE_NODE")
@DynamicUpdate
@DynamicInsert
public class MultiOrgUserTreeNode extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1511233285634L;

    // 用户UUID
    private String userId;
    // 用户在组织树中的位置节点ID
    private String eleId;
    // 组织版本ID
    private String orgVersionId;
    // 是否主要的
    private Integer isMain;

    /**
     * @return the orgVersionId
     */
    public String getOrgVersionId() {
        return orgVersionId;
    }

    /**
     * @param orgVersionId 要设置的orgVersionId
     */
    public void setOrgVersionId(String orgVersionId) {
        this.orgVersionId = orgVersionId;
    }

    /**
     * @return the isMain
     */
    public Integer getIsMain() {
        return isMain;
    }

    /**
     * @param isMain 要设置的isMain
     */
    public void setIsMain(Integer isMain) {
        this.isMain = isMain;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the eleId
     */
    public String getEleId() {
        return eleId;
    }

    /**
     * @param eleId 要设置的eleId
     */
    public void setEleId(String eleId) {
        this.eleId = eleId;
    }
}
