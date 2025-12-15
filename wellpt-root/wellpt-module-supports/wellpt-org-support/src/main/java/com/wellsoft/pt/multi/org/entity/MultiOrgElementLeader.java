/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.CollectionUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 组织节点基本类
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
@Table(name = "MULTI_ORG_ELEMENT_LEADER")
@DynamicUpdate
@DynamicInsert
public class MultiOrgElementLeader extends IdEntity implements LeaderType {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1271163672033319245L;

    // 对应的元素Id  部门相关节点Id
    private String eleId;
    // 元素对应的组织版本ID
    private String eleOrgVersionId;
    // 领导类型， 0：负责人，1：分管领导，2：管理员
    private Integer leaderType;
    // 目标对象的ID  职位Id 或 用户Id
    private String targetObjId;
    // 目标对象的归属组织版本ID
    private String targetObjOrgVersionId;

    public static String leaderList2idPaths(List<MultiOrgElementLeader> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            ArrayList<String> ids = new ArrayList<String>();
            for (MultiOrgElementLeader leader : list) {
                ids.add(leader.getTargetObjOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL
                        + leader.getTargetObjId());
            }
            return StringUtils.join(ids, ";");
        }
    }

    // public static String leaderList2names(List<MultiOrgElementLeader> list) {
    // if (CollectionUtils.isEmpty(list)) {
    // return null;
    // } else {
    // ArrayList<String> ids = new ArrayList<String>();
    // for (MultiOrgElementLeader multiOrgElementManager : list) {
    // ids.add(multiOrgElementManager.getTargetObjName());
    // }
    // return StringUtils.join(ids, ";");
    // }
    // }

    /**
     * @return the leaderType
     */
    public Integer getLeaderType() {
        return leaderType;
    }

    /**
     * @param leaderType 要设置的leaderType
     */
    public void setLeaderType(Integer leaderType) {
        this.leaderType = leaderType;
    }

    /**
     * @return the targetObjId
     */
    public String getTargetObjId() {
        return targetObjId;
    }

    /**
     * @param targetObjId 要设置的targetObjId
     */
    public void setTargetObjId(String targetObjId) {
        this.targetObjId = targetObjId;
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

    /**
     * @return the targetObjOrgVersionId
     */
    public String getTargetObjOrgVersionId() {
        return targetObjOrgVersionId;
    }

    /**
     * @param targetObjOrgVersionId 要设置的targetObjOrgVersionId
     */
    public void setTargetObjOrgVersionId(String targetObjOrgVersionId) {
        this.targetObjOrgVersionId = targetObjOrgVersionId;
    }

    /**
     * @return the eleOrgVersionId
     */
    public String getEleOrgVersionId() {
        return eleOrgVersionId;
    }

    /**
     * @param eleOrgVersionId 要设置的eleOrgVersionId
     */
    public void setEleOrgVersionId(String eleOrgVersionId) {
        this.eleOrgVersionId = eleOrgVersionId;
    }

}
