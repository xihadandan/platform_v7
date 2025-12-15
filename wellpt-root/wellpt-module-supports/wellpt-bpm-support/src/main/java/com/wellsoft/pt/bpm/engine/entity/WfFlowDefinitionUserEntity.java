/*
 * @(#)11/29/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 11/29/24.1	    zhulh		11/29/24		    Create
 * </pre>
 * @date 11/29/24
 */
@javax.persistence.Entity
@Table(name = "WF_FLOW_DEFINITION_USER")
@DynamicUpdate
@DynamicInsert
public class WfFlowDefinitionUserEntity extends Entity {
    private static final long serialVersionUID = 6630531338395645630L;

    // 流程定义ID
    private String flowDefUuid;

    // 节点类型，flow流程属性、timer计时器、task环节属性
    private String nodeType;

    // 流程名称、计时器名称、环节名称
    private String nodeName;

    // 流程ID、计时器ID、环节ID
    private String nodeId;

    // 节点用户属性，如环节办理人users、抄送人copyUsers
    private String nodeUserAttribute;

    // 用户类型
    private String userType;

    // 用户ID值
    private String userValue;

    // 用户ID值参数
    private String userArgValue;

    // 用户组织ID
    private String userOrgId;

    // 排序号
    private Integer sortOrder;

    /**
     * @return the flowDefUuid
     */
    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    /**
     * @param flowDefUuid 要设置的flowDefUuid
     */
    public void setFlowDefUuid(String flowDefUuid) {
        this.flowDefUuid = flowDefUuid;
    }

    /**
     * @return the nodeType
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * @param nodeType 要设置的nodeType
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName 要设置的nodeName
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the nodeId
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId 要设置的nodeId
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return the nodeUserAttribute
     */
    public String getNodeUserAttribute() {
        return nodeUserAttribute;
    }

    /**
     * @param nodeUserAttribute 要设置的nodeUserAttribute
     */
    public void setNodeUserAttribute(String nodeUserAttribute) {
        this.nodeUserAttribute = nodeUserAttribute;
    }

    /**
     * @return the userType
     */
    public String getUserType() {
        return userType;
    }

    /**
     * @param userType 要设置的userType
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * @return the userValue
     */
    public String getUserValue() {
        return userValue;
    }

    /**
     * @param userValue 要设置的userValue
     */
    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }

    /**
     * @return the userArgValue
     */
    public String getUserArgValue() {
        return userArgValue;
    }

    /**
     * @param userArgValue 要设置的userArgValue
     */
    public void setUserArgValue(String userArgValue) {
        this.userArgValue = userArgValue;
    }

    /**
     * @return the userOrgId
     */
    public String getUserOrgId() {
        return userOrgId;
    }

    /**
     * @param userOrgId 要设置的userOrgId
     */
    public void setUserOrgId(String userOrgId) {
        this.userOrgId = userOrgId;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
}
