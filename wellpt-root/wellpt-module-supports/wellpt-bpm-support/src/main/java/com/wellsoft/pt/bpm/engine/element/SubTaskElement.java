/*
 * @(#)2012-12-17 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

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
 * 2012-12-17.1	zhulh		2012-12-17		Create
 * </pre>
 * @date 2012-12-17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize()
public class SubTaskElement extends TaskElement {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8414648731002383346L;

    private String isSetFlow;
    private String isAutoSubmit;
    private String isCopyAll;
    private String isCopyBody;
    private String isCopyAttach;
    private String businessType;
    private String businessTypeName;
    private String businessRole;
    private String businessRoleName;
    private List<NewFlowElement> newFlows;
    private List<RelationElement> relations;
    // 办理进度显示位置
    private String undertakeSituationPlaceHolder;
    // 办理进度列表标题表达式
    private String undertakeSituationTitleExpression;
    // 按钮配置
    private List<ButtonElement> undertakeSituationButtons;
    // 列配置
    private List<ColumnElement> undertakeSituationColumns;

    private String sortRule;
    //排序列配置
    private List<OrderElement> undertakeSituationOrders;

    // 信息分发显示位置
    private String infoDistributionPlaceHolder;
    // 信息分发列表标题表达式
    private String infoDistributionTitleExpression;
    // 操作记录显示位置
    private String operationRecordPlaceHolder;
    // 操作记录列表标题表达式
    private String operationRecordTitleExpression;
    // 跟进人员
    private List<UserUnitElement> subTaskMonitors;
    private String inStagesCondition;
    private String stageDivisionFormId;
    private String stageHandlingState;
    private String stageState;
    private List<StageElement> stages;
    private String traceTask;
    private String expandList;

    //0:默认不可查看主流程  1:默认可查看主流程
    private String childLookParent;
    //1:允许主流程更改查看权限
    private String parentSetChild;

    public String getChildLookParent() {
        return childLookParent;
    }

    public void setChildLookParent(String childLookParent) {
        this.childLookParent = childLookParent;
    }

    public String getParentSetChild() {
        return parentSetChild;
    }

    public void setParentSetChild(String parentSetChild) {
        this.parentSetChild = parentSetChild;
    }

    /**
     * @return the isSetFlow
     */
    public String getIsSetFlow() {
        return isSetFlow;
    }

    /**
     * @param isSetFlow 要设置的isSetFlow
     */
    public void setIsSetFlow(String isSetFlow) {
        this.isSetFlow = isSetFlow;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isSetFlow() {
        return "1".equals(isSetFlow);
    }

    /**
     * @return the isAutoSubmit
     */
    public String getIsAutoSubmit() {
        return isAutoSubmit;
    }

    /**
     * @param isAutoSubmit 要设置的isAutoSubmit
     */
    public void setIsAutoSubmit(String isAutoSubmit) {
        this.isAutoSubmit = isAutoSubmit;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isAutoSubmit() {
        return "1".equals(isAutoSubmit);
    }

    /**
     * @return the isCopyAll
     */
    public String getIsCopyAll() {
        return isCopyAll;
    }

    /**
     * @param isCopyAll 要设置的isCopyAll
     */
    public void setIsCopyAll(String isCopyAll) {
        this.isCopyAll = isCopyAll;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isCopyAll() {
        return "1".equals(isCopyAll);
    }

    /**
     * @return the isCopyBody
     */
    public String getIsCopyBody() {
        return isCopyBody;
    }

    /**
     * @param isCopyBody 要设置的isCopyBody
     */
    public void setIsCopyBody(String isCopyBody) {
        this.isCopyBody = isCopyBody;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isCopyBody() {
        return "1".equals(isCopyBody);
    }

    /**
     * @return the isCopyAttach
     */
    public String getIsCopyAttach() {
        return isCopyAttach;
    }

    /**
     * @param isCopyAttach 要设置的isCopyAttach
     */
    public void setIsCopyAttach(String isCopyAttach) {
        this.isCopyAttach = isCopyAttach;
    }

    /**
     * @return
     */
    @JsonIgnore
    public boolean isCopyAttach() {
        return "1".equals(isCopyAttach);
    }

    /**
     * @return the businessType
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * @param businessType 要设置的businessType
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /**
     * @return the businessTypeName
     */
    public String getBusinessTypeName() {
        return businessTypeName;
    }

    /**
     * @param businessTypeName 要设置的businessTypeName
     */
    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
    }

    /**
     * @return the businessRole
     */
    public String getBusinessRole() {
        return businessRole;
    }

    /**
     * @param businessRole 要设置的businessRole
     */
    public void setBusinessRole(String businessRole) {
        this.businessRole = businessRole;
    }

    /**
     * @return the businessRoleName
     */
    public String getBusinessRoleName() {
        return businessRoleName;
    }

    /**
     * @param businessRoleName 要设置的businessRoleName
     */
    public void setBusinessRoleName(String businessRoleName) {
        this.businessRoleName = businessRoleName;
    }

    /**
     * @return the newFlows
     */
    public List<NewFlowElement> getNewFlows() {
        return newFlows;
    }

    /**
     * @param newFlows 要设置的newFlows
     */
    public void setNewFlows(List<NewFlowElement> newFlows) {
        this.newFlows = newFlows;
    }

    /**
     * @return the relations
     */
    public List<RelationElement> getRelations() {
        return relations;
    }

    /**
     * @param relations 要设置的relations
     */
    public void setRelations(List<RelationElement> relations) {
        this.relations = relations;
    }

    /**
     * @return the undertakeSituationPlaceHolder
     */
    public String getUndertakeSituationPlaceHolder() {
        return undertakeSituationPlaceHolder;
    }

    /**
     * @param undertakeSituationPlaceHolder 要设置的undertakeSituationPlaceHolder
     */
    public void setUndertakeSituationPlaceHolder(String undertakeSituationPlaceHolder) {
        this.undertakeSituationPlaceHolder = undertakeSituationPlaceHolder;
    }

    /**
     * @return the undertakeSituationTitleExpression
     */
    public String getUndertakeSituationTitleExpression() {
        return undertakeSituationTitleExpression;
    }

    /**
     * @param undertakeSituationTitleExpression 要设置的undertakeSituationTitleExpression
     */
    public void setUndertakeSituationTitleExpression(String undertakeSituationTitleExpression) {
        this.undertakeSituationTitleExpression = undertakeSituationTitleExpression;
    }

    /**
     * @return the undertakeSituationButtons
     */
    public List<ButtonElement> getUndertakeSituationButtons() {
        return undertakeSituationButtons;
    }

    /**
     * @param undertakeSituationButtons 要设置的undertakeSituationButtons
     */
    public void setUndertakeSituationButtons(List<ButtonElement> undertakeSituationButtons) {
        this.undertakeSituationButtons = undertakeSituationButtons;
    }

    /**
     * @return the undertakeSituationColumns
     */
    public List<ColumnElement> getUndertakeSituationColumns() {
        return undertakeSituationColumns;
    }

    /**
     * @param undertakeSituationColumns 要设置的undertakeSituationColumns
     */
    public void setUndertakeSituationColumns(List<ColumnElement> undertakeSituationColumns) {
        this.undertakeSituationColumns = undertakeSituationColumns;
    }

    public String getSortRule() {
        return sortRule;
    }

    public void setSortRule(String sortRule) {
        this.sortRule = sortRule;
    }

    public List<OrderElement> getUndertakeSituationOrders() {
        return undertakeSituationOrders;
    }

    public void setUndertakeSituationOrders(List<OrderElement> undertakeSituationOrders) {
        this.undertakeSituationOrders = undertakeSituationOrders;
    }

    /**
     * @return the infoDistributionPlaceHolder
     */
    public String getInfoDistributionPlaceHolder() {
        return infoDistributionPlaceHolder;
    }

    /**
     * @param infoDistributionPlaceHolder 要设置的infoDistributionPlaceHolder
     */
    public void setInfoDistributionPlaceHolder(String infoDistributionPlaceHolder) {
        this.infoDistributionPlaceHolder = infoDistributionPlaceHolder;
    }

    /**
     * @return the infoDistributionTitleExpression
     */
    public String getInfoDistributionTitleExpression() {
        return infoDistributionTitleExpression;
    }

    /**
     * @param infoDistributionTitleExpression 要设置的infoDistributionTitleExpression
     */
    public void setInfoDistributionTitleExpression(String infoDistributionTitleExpression) {
        this.infoDistributionTitleExpression = infoDistributionTitleExpression;
    }

    /**
     * @return the operationRecordPlaceHolder
     */
    public String getOperationRecordPlaceHolder() {
        return operationRecordPlaceHolder;
    }

    /**
     * @param operationRecordPlaceHolder 要设置的operationRecordPlaceHolder
     */
    public void setOperationRecordPlaceHolder(String operationRecordPlaceHolder) {
        this.operationRecordPlaceHolder = operationRecordPlaceHolder;
    }

    /**
     * @return the operationRecordTitleExpression
     */
    public String getOperationRecordTitleExpression() {
        return operationRecordTitleExpression;
    }

    /**
     * @param operationRecordTitleExpression 要设置的operationRecordTitleExpression
     */
    public void setOperationRecordTitleExpression(String operationRecordTitleExpression) {
        this.operationRecordTitleExpression = operationRecordTitleExpression;
    }

    /**
     * @return the subTaskMonitors
     */
    public List<UserUnitElement> getSubTaskMonitors() {
        return subTaskMonitors;
    }

    /**
     * @param subTaskMonitors 要设置的subTaskMonitors
     */
    public void setSubTaskMonitors(List<UserUnitElement> subTaskMonitors) {
        this.subTaskMonitors = subTaskMonitors;
    }

    /**
     * @return the inStagesCondition
     */
    public String getInStagesCondition() {
        return inStagesCondition;
    }

    /**
     * @param inStagesCondition 要设置的inStagesCondition
     */
    public void setInStagesCondition(String inStagesCondition) {
        this.inStagesCondition = inStagesCondition;
    }

    /**
     * @return the stageDivisionFormId
     */
    public String getStageDivisionFormId() {
        return stageDivisionFormId;
    }

    /**
     * @param stageDivisionFormId 要设置的stageDivisionFormId
     */
    public void setStageDivisionFormId(String stageDivisionFormId) {
        this.stageDivisionFormId = stageDivisionFormId;
    }

    /**
     * @return the stageHandlingState
     */
    public String getStageHandlingState() {
        return stageHandlingState;
    }

    /**
     * @param stageHandlingState 要设置的stageHandlingState
     */
    public void setStageHandlingState(String stageHandlingState) {
        this.stageHandlingState = stageHandlingState;
    }

    /**
     * @return the stageState
     */
    public String getStageState() {
        return stageState;
    }

    /**
     * @param stageState 要设置的stageState
     */
    public void setStageState(String stageState) {
        this.stageState = stageState;
    }

    /**
     * @return the stages
     */
    public List<StageElement> getStages() {
        return stages;
    }

    /**
     * @param stages 要设置的stages
     */
    public void setStages(List<StageElement> stages) {
        this.stages = stages;
    }

    /**
     * @return the traceTask
     */
    public String getTraceTask() {
        return traceTask;
    }

    /**
     * @param traceTask 要设置的traceTask
     */
    public void setTraceTask(String traceTask) {
        this.traceTask = traceTask;
    }

    @Override
    public String getExpandList() {
        return expandList;
    }

    @Override
    public void setExpandList(String expandList) {
        this.expandList = expandList;
    }
}
