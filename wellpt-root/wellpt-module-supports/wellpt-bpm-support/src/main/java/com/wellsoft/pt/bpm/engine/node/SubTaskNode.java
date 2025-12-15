/*
 * @(#)2012-11-20 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.node;

import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.SubTaskTransition;
import com.wellsoft.pt.bpm.engine.core.Transition;
import com.wellsoft.pt.bpm.engine.element.ButtonElement;
import com.wellsoft.pt.bpm.engine.element.ColumnElement;
import com.wellsoft.pt.bpm.engine.element.OrderElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.support.NewFlow;
import com.wellsoft.pt.bpm.engine.support.NewFlowRelation;
import com.wellsoft.pt.bpm.engine.support.NewFlowStage;

import java.util.List;

/**
 * Description: 子任务节点类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-20.1	zhulh		2012-11-20		Create
 * </pre>
 * @date 2012-11-20
 */
public class SubTaskNode extends TaskNode {

    public static final String DRAFT = FlowConstant.AS_DRAFT;
    public static final String AUTO_SUBMIT = FlowConstant.AUTO_SUBMIT;
    private static final long serialVersionUID = -287401577745694862L;
    // 业务类别
    private String businessType;
    // 业务类别名称
    private String businessTypeName;
    // 业务角色
    private String businessRole;
    // 业务角色名称
    private String businessRoleName;

    /**
     * 现在确定：新流程列表
     */
    private List<NewFlow> newFlows;
    /** 新流程Map<flowId, newFlow> */
    // private Map<String, NewFlow> newFlowMap;

    /**
     * 现在确定：新流程前置关系
     */
    private List<NewFlowRelation> relations;

    // 检查分阶段办理
    private boolean checkInStage = true;
    // 承办情况显示位置
    private String undertakeSituationPlaceHolder;
    // 承办情况标题表达式
    private String undertakeSituationTitleExpression;
    // 承办情况操作按钮
    private List<ButtonElement> undertakeSituationButtons;
    // 承办情况列配置
    private List<ColumnElement> undertakeSituationColumns;
    private String sortRule;
    //排序列配置
    private List<OrderElement> undertakeSituationOrders;
    // 信息分发显示位置
    private String infoDistributionPlaceHolder;
    // 信息分发标题表达式
    private String infoDistributionTitleExpression;
    // 操作记录显示位置
    private String operationRecordPlaceHolder;
    // 操作记录标题表达式
    private String operationRecordTitleExpression;
    // 跟踪人员
    private List<UserUnitElement> subTaskMonitors;
    // 分阶段办理判断条件
    private String inStagesCondition;
    //0:默认不可查看主流程  1:默认可查看主流程
    private String childLookParent;
    //1:允许主流程更改查看权限
    private String parentSetChild;
    // 阶段划分
    private String stageDivisionFormId;
    // 阶段完成状态
    private String stageHandlingState;
    // 分阶段状态
    private String stageState;
    // 分阶段办理
    private List<NewFlowStage> stages;
    // 跟踪环节
    private String traceTask;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.node.TaskNode#enter(com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void enter(ExecutionContext executionContext) {
        // System.out.println("enter sub task node: " + name);
        super.enter(executionContext);
        logger.debug("enter sub task node: " + name + "[" + id + "]");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.node.Node#execute(com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void execute(ExecutionContext executionContext) {
        logger.debug("execute sub task node: " + name + "[" + id + "]");
        // 节点执行处理
        handler.execute(this, executionContext);
        // 进入子流程
        executionContext.startSubTasks(this);
        // 判断是否异步执行，或者不是分发结点
        // String fromTaskId =
        // executionContext.getToken().getTaskData().getPreTaskId(this.id);
        if (isAsync || !executionContext.isSubTaskSyncStartd(this)) {
            // executionContext.getToken().getTaskData().setSubTaskNodeIsAsync(this.getId(),
            // true);
            executionContext.getToken().signal();
        } else if (newFlows.isEmpty()) {// 如果子流程为空则继续执行
            executionContext.getToken().signal();
        } else {
            executionContext.getToken().getTaskData().getSubmitResult().addNextTaskInfo(executionContext.getToken().getTask(), null);
        }
    }

    /**
     * @return
     */
    private void initIsAsync() {
        if (newFlows.isEmpty()) {
            this.isAsync = false;
            return;
        }
        for (int index = 0; index < newFlows.size(); index++) {
            if (newFlows.get(index).isWait()) {
                this.isAsync = false;
                return;
            }
        }
        this.isAsync = true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.node.TaskNode#complete(com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public boolean complete(ExecutionContext executionContext) {
        return handler.complete(this, executionContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.node.Node#leave(com.wellsoft.pt.workflow.engine.core.Transition, com.wellsoft.pt.workflow.engine.context.ExecutionContext)
     */
    @Override
    public void leave(Transition transition, ExecutionContext executionContext) {
        // System.out.println("leave sub task node: " + name);
        logger.debug("leave sub task node: " + name + "[" + id + "]");

        super.leave(transition, executionContext);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.node.Node#getLeavingTransition()
     */
    @Override
    public Transition getLeavingTransition() {
        Transition transition = new SubTaskTransition();
        transition.setFrom(this);
        return transition;
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
    public List<NewFlow> getNewFlows() {
        return newFlows;
    }

    /**
     * @param newFlows 要设置的newFlows
     */
    public void setNewFlows(List<NewFlow> newFlows) {
        this.newFlows = newFlows;
        initIsAsync();
    }

    /**
     * @param completedSubFlowId
     * @return
     */
    //	public NewFlow getNewFlow(String completedSubFlowId) {
    //		if (newFlowMap == null) {
    //			newFlowMap = new LinkedHashMap<String, NewFlow>();
    //			for (NewFlow newFlow : newFlows) {
    //				newFlowMap.put(newFlow.getFlowId(), newFlow);
    //			}
    //		}
    //		return newFlowMap.get(completedSubFlowId);
    //	}

    /**
     * @return the relations
     */
    public List<NewFlowRelation> getRelations() {
        return relations;
    }

    /**
     * @param relations 要设置的relations
     */
    public void setRelations(List<NewFlowRelation> relations) {
        this.relations = relations;
    }

    //	/**
    //	 * @return the newFlowMap
    //	 */
    //	public Map<String, NewFlow> getNewFlowMap() {
    //		return newFlowMap;
    //	}
    //
    //	/**
    //	 * @param newFlowMap 要设置的newFlowMap
    //	 */
    //	public void setNewFlowMap(Map<String, NewFlow> newFlowMap) {
    //		this.newFlowMap = newFlowMap;
    //	}

    /**
     * @return the checkInStage
     */
    public boolean isCheckInStage() {
        return checkInStage;
    }

    /**
     * @param checkInStage 要设置的checkInStage
     */
    public void setCheckInStage(boolean checkInStage) {
        this.checkInStage = checkInStage;
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
    public List<NewFlowStage> getStages() {
        return stages;
    }

    /**
     * @param stages 要设置的stages
     */
    public void setStages(List<NewFlowStage> stages) {
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
}
