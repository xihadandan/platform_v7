/*
 * @(#)11/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
 * 11/18/22.1	zhulh		11/18/22		Create
 * </pre>
 * @date 11/18/22
 */
@ApiModel("里程碑事件交付物")
@Entity
@Table(name = "BIZ_MILESTONE_RESULT")
@DynamicUpdate
@DynamicInsert
public class BizMilestoneResultEntity extends IdEntity {
    private static final long serialVersionUID = -3527164334442004640L;

    @ApiModelProperty("里程碑事件UUID")
    private String milestoneUuid;

    @ApiModelProperty("交付物类型，1结论，2附件")
    private String resultType;

    @ApiModelProperty("结论内容")
    private String content;

    @ApiModelProperty("附件UUID列表，多个以分号隔开")
    private String repoFileUuids;

    /**
     * @return the milestoneUuid
     */
    public String getMilestoneUuid() {
        return milestoneUuid;
    }

    /**
     * @param milestoneUuid 要设置的milestoneUuid
     */
    public void setMilestoneUuid(String milestoneUuid) {
        this.milestoneUuid = milestoneUuid;
    }

    /**
     * @return the resultType
     */
    public String getResultType() {
        return resultType;
    }

    /**
     * @param resultType 要设置的resultType
     */
    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the repoFileUuids
     */
    public String getRepoFileUuids() {
        return repoFileUuids;
    }

    /**
     * @param repoFileUuids 要设置的repoFileUuids
     */
    public void setRepoFileUuids(String repoFileUuids) {
        this.repoFileUuids = repoFileUuids;
    }
}
