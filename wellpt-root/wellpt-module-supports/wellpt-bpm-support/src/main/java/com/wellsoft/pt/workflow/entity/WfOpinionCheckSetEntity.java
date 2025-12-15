/*
 * @(#)2021-05-11 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据库表WF_OPINION_CHECK_SET的实体类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-05-11.1	zenghw		2021-05-11		Create
 * </pre>
 * @date 2021-05-11
 */
@Entity
@Table(name = "WF_OPINION_CHECK_SET")
@DynamicUpdate
@DynamicInsert
public class WfOpinionCheckSetEntity extends IdEntity {

    private static final long serialVersionUID = 1620718434510L;

    // 流程定义ID:对应WF_FLOW_DEFINITION表的ID字段
    private String flowDefId;
    // 固定备选项：全部(值用all) 所在环节ID集合，多个用;号隔开例如：T003;T005
    private String taskIds;

    private String opinionRuleUuid;
    // 场景 枚举：SceneEnum
    // S001:提交
    // S002:退回
    // S003:转办
    // S004:会签
    private String scene;

    /**
     * @return the flowDefId
     */
    public String getFlowDefId() {
        return this.flowDefId;
    }

    /**
     * @param flowDefId
     */
    public void setFlowDefId(String flowDefId) {
        this.flowDefId = flowDefId;
    }

    /**
     * @return the taskIds
     */
    public String getTaskIds() {
        return this.taskIds;
    }

    /**
     * @param taskIds
     */
    public void setTaskIds(String taskIds) {
        this.taskIds = taskIds;
    }

    /**
     * @return the opinionRuleUuid
     */
    public String getOpinionRuleUuid() {
        return this.opinionRuleUuid;
    }

    /**
     * @param opinionRuleUuid
     */
    public void setOpinionRuleUuid(String opinionRuleUuid) {
        this.opinionRuleUuid = opinionRuleUuid;
    }

    /**
     * @return the scene
     */
    public String getScene() {
        return this.scene;
    }

    /**
     * @param scene
     */
    public void setScene(String scene) {
        this.scene = scene;
    }

}
