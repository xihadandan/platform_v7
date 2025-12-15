/*
 * @(#)2013-7-29 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 意见立场
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-7-29.1	zhulh		2013-7-29		Create
 * </pre>
 * @date 2013-7-29
 */
@Entity
@Table(name = "WF_DEF_OPINION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entity")
@DynamicUpdate
@DynamicInsert
@ApiModel("流程意见")
public class FlowOpinion extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8343509830614267343L;

    // 意见内容
    @ApiModelProperty("意见内容")
    private String content;

    // 编号
    @ApiModelProperty("编号")
    private String code;

    // 流程定义ID
    @ApiModelProperty("流程定义ID")
    private String flowDefId;

    // 环节ID
    @ApiModelProperty("环节ID")
    private String taskId;

    // 所属分类
    @ApiModelProperty("所属分类")
    private String opinionCategoryUuid;

    @ApiModelProperty("排序")
    private Integer seq;

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
     * @return the flowDefId
     */
    public String getFlowDefId() {
        return flowDefId;
    }

    /**
     * @param flowDefId 要设置的flowDefId
     */
    public void setFlowDefId(String flowDefId) {
        this.flowDefId = flowDefId;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the opinionCategoryUuid
     */
    public String getOpinionCategoryUuid() {
        return opinionCategoryUuid;
    }

    /**
     * @param opinionCategoryUuid 要设置的opinionCategoryUuid
     */
    public void setOpinionCategoryUuid(String opinionCategoryUuid) {
        this.opinionCategoryUuid = opinionCategoryUuid;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
