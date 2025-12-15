/*
 * @(#)2012-11-19 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.delegate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.core.*;
import com.wellsoft.pt.bpm.engine.element.*;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.enums.DirectionType;
import com.wellsoft.pt.bpm.engine.enums.TaskNodeType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.form.*;
import com.wellsoft.pt.bpm.engine.node.*;
import com.wellsoft.pt.bpm.engine.parser.TaskFieldProperty;
import com.wellsoft.pt.bpm.engine.parser.TaskFieldPropertyParser;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.entity.FlowFormat;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;
import com.wellsoft.pt.workflow.service.FlowFormatService;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;

/**
 * Description: 流程规划解析委派类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-19.1	zhulh		2012-11-19		Create
 * </pre>
 * @date 2012-11-19
 */
public class FlowDelegate {
    public static final String START_FLOW_ID = FlowConstant.START_FLOW_ID;
    public static final String END_FLOW_ID = FlowConstant.END_FLOW_ID;
    private static final String BEGIN = "BEGIN";
    private FlowElement flow;

    private Map<String, List<MessageTemplate>> messageTemplateMap = new HashMap<String, List<MessageTemplate>>(0);

    /**
     * @param contentAsString
     */
    public FlowDelegate(FlowDefinition flowDefinition) {
        FlowSchemeService flowSchemeService = ApplicationContextHolder.getBean(FlowSchemeService.class);
        flow = flowSchemeService.getFlowElement(flowDefinition);
    }

    /**
     * @param flow
     */
    public FlowDelegate(String flowDefUuid) {
        FlowSchemeService flowSchemeService = ApplicationContextHolder.getBean(FlowSchemeService.class);
        flow = flowSchemeService.getFlowElementByFlowDefUuid(flowDefUuid);
    }

    /**
     * @param flow
     */
    public FlowDelegate(FlowElement flow) {
        this.flow = flow;
    }

    /**
     * 如何描述该方法
     *
     * @param element
     */
    private static Direction convertDirectionElement(DirectionElement element) {
        Direction direction = new Direction();
        // 基本信息
        direction.setId(element.getId());
        direction.setUseAsButton(element.getUseAsButton());
        direction.setButtonName(element.getButtonName());
        direction.setType(element.getType());
        if (StringUtils.isNotBlank(element.getBtnStyle())) {
            direction.setButtonClassName(element.getBtnStyle());
        } else {
            direction.setButtonClassName(element.getButtonClassName());
        }
        direction.setButtonOrder(element.getButtonOrder());
        direction.setName(element.getName());
        direction.setFromID(element.getFromID());
        direction.setToID(element.getToID());
        direction.setSortOrder(element.getSortOrder());
        direction.setRemark(element.getRemark());
        direction.setShowRemark(element.getIsShowRemark());
        // 判断条件
        direction.setIsDefault(element.getIsDefault());
        direction.setIsEveryCheck(element.getIsEveryCheck());
        List<String> conditions = new ArrayList<String>(0);
        List<String> conditionDatas = new ArrayList<String>(0);
        for (ConditionUnitElement unit : element.getConditions()) {
            conditions.add(unit.getValue());
            conditionDatas.add(unit.getData());
        }
        direction.setConditions(conditions);
        direction.setConditionDatas(conditionDatas);
        // 文件分发
        direction.setIsSendFile(element.getIsSendFile());
        direction.setFileRecipients(element.getFileRecipients());
        // 消息分发
        direction.setIsSendMsg(element.getIsSendMsg());
        direction.setMsgRecipients(element.getMsgRecipients());
        // 事件监听
        direction.setListener(element.getListener());
        // 分支模式
        direction.setBranchMode(element.getBranchMode());
        // 分支实例类型
        direction.setBranchInstanceType(element.getBranchInstanceType());
        // 来源
        direction.setBranchCreateWay(element.getBranchCreateWay());
        // 表单
        direction.setBranchCreateInstanceFormId(element.getBranchCreateInstanceFormId());
        // 是否主表创建方式
        direction.setMainFormBranchCreateWay(element.isMainFormBranchCreateWay());
        // 字段
        direction.setBranchTaskUsers(element.getBranchTaskUsers());
        // 创建方式
        direction.setBranchCreateInstanceWay(element.getBranchCreateInstanceWay());
        // 按从表行分批次生成实例
        direction.setBranchCreateInstanceBatch(element.getIsBranchCreateInstanceBatch());
        // 分支流接口
        direction.setBranchInterface(element.getBranchInterface());
        // 分支流接口名称
        direction.setBranchInterfaceName(element.getBranchInterfaceName());
        // 共享分支
        direction.setShareBranch(element.getIsShareBranch());
        // 该流向不参与聚合
        direction.setIndependentBranch(element.isIndependentBranch());
        // 事件脚本
        ScriptElement eventScript = element.getEventScript();
        if (eventScript != null) {
            String type = eventScript.getType();
            String contentType = eventScript.getContentType();
            String content = eventScript.getContent();
            if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(content)) {
                Script script = new Script();
                script.setPointcut(eventScript.getPointcut());
                script.setType(type);
                script.setContentType(contentType);
                script.setContent(content);
                direction.setEventScript(script);
            }
        }
        // 归档设置
        List<ArchiveElement> archiveElements = element.getArchives();
        List<Archive> archives = new ArrayList<Archive>(0);
        for (ArchiveElement archiveElement : archiveElements) {
            Archive archive = new Archive();
            BeanUtils.copyProperties(archiveElement, archive);
            archives.add(archive);
        }
        direction.setArchives(archives);
        return direction;
    }

    /**
     * 如何描述该方法
     *
     * @param equalFlow
     * @param rawFlow
     */
    private void fixedEqualFlow(FlowElement equalFlow, FlowElement rawFlow) {
        equalFlow.setName(rawFlow.getName());
        equalFlow.setId(rawFlow.getId());
        equalFlow.setCode(rawFlow.getCode());
        equalFlow.setUuid(rawFlow.getUuid());
        equalFlow.setVersion(rawFlow.getVersion());

        equalFlow.setProperty(rawFlow.getProperty());
        equalFlow.setTimers(rawFlow.getTimers());

        this.flow = equalFlow;
    }

    /**
     * @return the flow
     */
    public FlowElement getFlow() {
        return flow;
    }

    /**
     * 获取开始节点
     *
     * @param flow
     * @return
     */
    public Node getStartNode() {
        StartNode node = new StartNode();
        List<DirectionElement> directions = flow.getDirections();
        for (DirectionElement direction : directions) {
            if (BEGIN.equals(direction.getTerminalType())) {
                node.setName(direction.getFromID());
                node.setId(START_FLOW_ID);
                node.setType(direction.getType());
                node.setToID(direction.getToID());
                node.setListeners(flow.getProperty().getListeners());
                break;
            }
        }
        return node;
    }

    /**
     * 获取结束节点
     *
     * @param flow
     * @return
     */
    public Node getEndNode() {
        return getEndNode(END_FLOW_ID);
    }

    /**
     * 获取结束结点
     *
     * @param flow
     * @param taskID
     * @return
     */
    private Node getEndNode(String taskID) {
        EndNode node = new EndNode();
        List<DirectionElement> directions = flow.getDirections();
        for (DirectionElement direction : directions) {
            if (taskID.equals(direction.getToID())) {
                node.setName(direction.getName());
                node.setId(taskID);
                node.setType(direction.getType());
                node.setListeners(flow.getProperty().getListeners());
                break;
            }
        }
        return node;
    }

    /**
     * @param direction
     * @return
     */
    private Node getEndNode(Direction direction) {
        EndNode node = new EndNode();
        node.setDirectionId(direction.getId());
        node.setName(direction.getName());
        node.setId(direction.getToID());
        node.setType(direction.getType());
        node.setListeners(flow.getProperty().getListeners());
        return node;
    }

    /**
     * 根据流向获取下一任务结点
     *
     * @param direction
     * @return
     */
    public Node getToTaskNode(Direction direction) {
        String taskID = direction.getToID();
        // 如果下一结点是结束结点
        if (taskID.equals(END_FLOW_ID)) {
            return getEndNode(direction);
        }
        Node to = getTaskNode(direction.getToID());
        if (to == null) {
            return to;
        }
        to.setDirectionId(direction.getId());
        return to;
    }

    /**
     * 根据环节ID，流向id的md5的消息摘要，获取下一任务结点
     *
     * @param taskId
     * @param digest
     * @return
     */
    public Node getToTaskNodeWidthDirectionIdDigest(String taskId, String digest) {
        List<Direction> directions = this.getDirections(taskId);
        for (Direction direction : directions) {
            if (StringUtils.equals(direction.getIdMd5Hex(), digest)) {
                Node node = getTaskNode(direction.getToID());
                node.setDirectionId(direction.getId());
                return node;
            }
        }
        return null;
    }

    /**
     * 根据任务ID判断是否存在任务节点
     *
     * @param taskId
     * @return
     */
    public boolean existsTaskNode(String taskId) {
        if (StringUtils.equals(START_FLOW_ID, taskId) || StringUtils.equals(END_FLOW_ID, taskId)) {
            return true;
        }
        return flow.getTask(taskId) != null;
    }

    /**
     * 根据任务ID获取任务节点
     *
     * @param taskID
     * @return
     */
    public Node getTaskNode(String taskID) {
        // 如果下一结点是结束结点
        if (taskID.equals(END_FLOW_ID)) {
            return getEndNode(taskID);
        }
        if (taskID.equals(START_FLOW_ID)) {
            return getStartNode();
        }

        // 获取下一任务结点
        TaskNode node = null;
        TaskElement task = flow.getTask(taskID);
        if (task == null) {
            return null;
        }
        String taskName = task.getName();
//        String locale = LocaleContextHolder.getLocale().toString();
//        if (MapUtils.isNotEmpty(task.getI18n()) && task.getI18n().containsKey(locale)
//                && StringUtils.isNotBlank(task.getI18n().get(locale).get(task.getId()))) {
//            taskName = task.getI18n().get(locale).get(task.getId());
//        }
        String[] globalTaskListeners = flow.getProperty().getGlobalTaskListeners();
        if (task instanceof SubTaskElement) {// 子流程结点
            SubTaskElement subTask = (SubTaskElement) task;
            SubTaskNode subNode = new SubTaskNode();
            if (taskID.equals(task.getId())) {
                subNode.setName(taskName);
                subNode.setId(task.getId());
                subNode.setType(task.getType());
                subNode.setBusinessType(subTask.getBusinessType());
                subNode.setBusinessTypeName(subTask.getBusinessTypeName());
                subNode.setBusinessRole(subTask.getBusinessRole());
                subNode.setBusinessRoleName(subTask.getBusinessRoleName());
                List<String> listeners = new ArrayList<String>();
                listeners.addAll(Arrays.asList(task.getListeners()));
                listeners.addAll(Arrays.asList(globalTaskListeners));
                subNode.setListeners(listeners.toArray(new String[0]));
                // 设置新流程列表
                List<NewFlow> newFlows = new ArrayList<NewFlow>();
                List<NewFlowElement> newFlowElements = subTask.getNewFlows();
                for (int index = 0; index < newFlowElements.size(); index++) {
                    NewFlowElement newFlowElement = newFlowElements.get(index);
                    NewFlow newFlow = new NewFlow();
                    newFlow.setId(newFlowElement.getId());
                    newFlow.setFlowName(newFlowElement.getName());
                    newFlow.setFlowId(newFlowElement.getValue());
                    newFlow.setLabel(newFlowElement.getLabel());
                    newFlow.setConditions(newFlowElement.getConditions());
                    newFlow.setGranularity(newFlowElement.getGranularity());
                    newFlow.setBizRoleId(newFlowElement.getBizRoleId());
                    newFlow.setCreateWay(newFlowElement.getCreateWay());
                    newFlow.setCreateInstanceFormId(newFlowElement.getCreateInstanceFormId());
                    newFlow.setMainFormCreateWay(newFlowElement.isMainFormCreateWay() || StringUtils
                            .equals(newFlowElement.getCreateInstanceFormId(), getFlow().getProperty().getFormID()));
                    newFlow.setTaskUsers(newFlowElement.getTaskUsers());
                    newFlow.setCreateInstanceWay(newFlowElement.getCreateInstanceWay());
                    newFlow.setCreateInstanceBatch(newFlowElement.getIsCreateInstanceBatch());
                    newFlow.setInterfaceName(newFlowElement.getInterfaceName());
                    newFlow.setInterfaceValue(newFlowElement.getInterfaceValue());
                    newFlow.setMajor(newFlowElement.isMajor());
                    newFlow.setMerge(newFlowElement.isMerge());
                    newFlow.setWait(newFlowElement.isWait());
                    newFlow.setShare(newFlowElement.isShare());
                    newFlow.setNotifyDoing(newFlowElement.getIsNotifyDoing());
                    newFlow.setToTaskName(newFlowElement.getToTaskName());
                    newFlow.setToTaskId(newFlowElement.getToTaskId());
                    newFlow.setCopyBotRuleId(newFlowElement.getCopyBotRuleId());
                    newFlow.setSyncBotRuleId(newFlowElement.getSyncBotRuleId());
                    newFlow.setReturnWithOver(newFlowElement.getIsReturnWithOver());
                    newFlow.setReturnWithDirection(newFlowElement.getIsReturnWithDirection());
                    newFlow.setReturnDirectionId(newFlowElement.getReturnDirectionId());
                    newFlow.setReturnBotRuleId(newFlowElement.getReturnBotRuleId());
                    newFlow.setCopyFields(newFlowElement.getCopyFields());
                    newFlow.setReturnOverrideFields(newFlowElement.getReturnOverrideFields());
                    newFlow.setReturnAdditionFields(newFlowElement.getReturnAdditionFields());
                    newFlow.setSortOrder(index);
                    newFlow.setSubformId(newFlowElement.getSubformId());
                    newFlow.setUndertakeSituationPlaceHolder(newFlowElement.getUndertakeSituationPlaceHolder());
                    newFlow.setInfoDistributionPlaceHolder(newFlowElement.getInfoDistributionPlaceHolder());
                    newFlow.setOperationRecordPlaceHolder(newFlowElement.getOperationRecordPlaceHolder());
                    newFlow.setI18n(newFlowElement.getI18n());
                    newFlows.add(newFlow);
                }
                // 现在确定：新流程列表
                subNode.setNewFlows(newFlows);

                // 设置子流程前置关系
                List<NewFlowRelation> relations = new ArrayList<NewFlowRelation>();
                List<RelationElement> relationElements = subTask.getRelations();
                for (RelationElement relationElement : relationElements) {
                    NewFlowRelation relation = new NewFlowRelation();
                    relation.setNewFlowName(relationElement.getNewFlowName());
                    relation.setNewFlowId(relationElement.getNewFlowId());
                    relation.setTaskName(relationElement.getTaskName());
                    relation.setTaskId(relationElement.getTaskId());
                    relation.setFrontNewFlowName(relationElement.getFrontNewFlowName());
                    relation.setFrontNewFlowId(relationElement.getFrontNewFlowId());
                    relation.setFrontTaskName(relationElement.getFrontTaskName());
                    relation.setFrontTaskId(relationElement.getFrontTaskId());
                    relations.add(relation);
                }
                subNode.setRelations(relations);
                // // 工作提交时选择具体流程
                // subNode.setSetFlow(subTask.isSetFlow());
                // // 是否自动提交
                // subNode.setAutoSubmit(subTask.isAutoSubmit());
                // // 是否拷贝整个文档
                // subNode.setCopyAll(subTask.isCopyAll());
                // // 拷贝整个文档
                // subNode.setCopyBody(subTask.isCopyBody());
                // // 拷贝文档中的附件
                // subNode.setCopyAttach(subTask.isCopyAttach());

                // 承办情况显示位置
                subNode.setUndertakeSituationPlaceHolder(subTask.getUndertakeSituationPlaceHolder());
                // 承办情况列表标题表达式
                subNode.setUndertakeSituationTitleExpression(subTask.getUndertakeSituationTitleExpression());
                // 承办情况按钮配置
                subNode.setUndertakeSituationButtons(subTask.getUndertakeSituationButtons());
                // 承办情况列配置
                subNode.setUndertakeSituationColumns(subTask.getUndertakeSituationColumns());
                // 排序规则
                subNode.setSortRule(subTask.getSortRule());
                // 排序列配置
                subNode.setUndertakeSituationOrders(subTask.getUndertakeSituationOrders());
                // 信息分发显示位置
                subNode.setInfoDistributionPlaceHolder(subTask.getInfoDistributionPlaceHolder());
                // 信息分发列表标题表达式
                subNode.setInfoDistributionTitleExpression(subTask.getInfoDistributionTitleExpression());
                // 操作记录显示位置
                subNode.setOperationRecordPlaceHolder(subTask.getOperationRecordPlaceHolder());
                // 操作记录列表标题表达式
                subNode.setOperationRecordTitleExpression(subTask.getOperationRecordTitleExpression());
                // 跟踪人员
                subNode.setSubTaskMonitors(subTask.getSubTaskMonitors());
                // 分阶段办理判断条件
                subNode.setInStagesCondition(subTask.getInStagesCondition());
                // 0:默认不可查看主流程 1:默认可查看主流程
                subNode.setChildLookParent(subTask.getChildLookParent());
                // 1:允许主流程更改查看权限
                subNode.setParentSetChild(subTask.getParentSetChild());
                // 阶段划分
                subNode.setStageDivisionFormId(subTask.getStageDivisionFormId());
                // 阶段完成状态
                subNode.setStageHandlingState(subTask.getStageHandlingState());
                // 分阶段状态
                subNode.setStageState(subTask.getStageState());
                // 分阶段办理
                List<NewFlowStage> newFlowStages = new ArrayList<NewFlowStage>();
                List<StageElement> stageElements = subTask.getStages();
                for (StageElement stageElement : stageElements) {
                    NewFlowStage newFlowStage = new NewFlowStage();
                    newFlowStage.setNewFlowId(stageElement.getNewFlowId());
                    newFlowStage.setNewFlowName(stageElement.getNewFlowName());
                    newFlowStage.setTaskUsers(stageElement.getTaskUsers());
                    newFlowStage.setTaskUsersName(stageElement.getTaskUsersName());
                    newFlowStage.setCreateInstanceWay(stageElement.getCreateInstanceWay());
                    newFlowStage.setLimitTime(stageElement.getLimitTime());
                    newFlowStage.setLimitTimeName(stageElement.getLimitTimeName());
                    newFlowStages.add(newFlowStage);
                }
                subNode.setStages(newFlowStages);
                // 跟踪环节
                subNode.setTraceTask(subTask.getTraceTask());

                node = subNode;
            }
        } else {
            // 协作节点
            if (StringUtils.equals(TaskNodeType.CollaborationTask.getValue(), task.getType())) {
                node = new CollaborationTask();
            } else if (StringUtils.equals(TaskNodeType.ScriptTask.getValue(), task.getType())) {
                // 脚本节点
                node = new ScriptNode();
            } else {
                // 正常流程结点
                node = new TaskNode();
            }
            node.setName(taskName);
            node.setId(task.getId());
            node.setType(task.getType());
            List<String> listeners = new ArrayList<String>();
            listeners.addAll(Arrays.asList(task.getListeners()));
            listeners.addAll(Arrays.asList(globalTaskListeners));
            node.setListeners(listeners.toArray(new String[0]));
            node.setJoinMode(task.getParallelGateway().getJoinMode());
            node.setForkMode(task.getParallelGateway().getForkMode());
            node.setSameUserSubmit(task.getSameUserSubmit());
            node.setAnyone(task.isAnyone());
            node.setByOrder(task.isByOrder());
            if (StringUtils.isNotBlank(task.getCanEditForm())) {
                node.setCanEditForm("1".equals(task.getCanEditForm()));
            }
            // 设置动态表单，如果环节有设置表单ID直接获取，否则从流程中获取
            /* lmw 2015=4-22 10:04 begin */
            if (StringUtils.isNotBlank(task.getFormID())) {
                node.setFormID(task.getFormID());
            } else {
                node.setFormID(flow.getProperty().getFormID());
            }

            // node.setFormID(flow.getProperty().getFormID());
            /* lmw 2015=4-22 10:04 end */
        }

        // 从流向中获取下一节点ID
        List<String> toIDs = new ArrayList<String>();
        List<DirectionElement> directions = flow.getDirections();
        for (DirectionElement direction : directions) {
            if (taskID.equals(direction.getFromID())) {
                String toID = direction.getToID();
                if (StringUtils.isNotBlank(toID)) {
                    TaskElement toTaskElement = flow.getTask(toID);
                    if (toTaskElement != null) {
                        toIDs.add(toID);
                    } else if (FlowDelegate.START_FLOW_ID.equals(toID) || FlowDelegate.END_FLOW_ID.equals(toID)) {
                        toIDs.add(toID);
                    } else {
                        // 新版网关判断节点，通过TaskTransitionResolver.resolveConditionTask解析
                    }
                }
            }
        }

        // 任务节点可以没有流出其他节点，表示结束
        if (node instanceof ScriptNode && CollectionUtils.isEmpty(toIDs) && !isConditionTask(node)) {
            toIDs.add(FlowDelegate.END_FLOW_ID);
        }
        node.setToIDs(toIDs);

        return node;
    }

//    /**
//     * 取机器节点流出的用户节点ID
//     *
//     * @param scriptTaskElement
//     * @param toIDs
//     * @param checkedNodeIds
//     */
//    private void extractScriptTaskToUserTaskId(TaskElement scriptTaskElement, List<String> toIDs, Set<String> checkedNodeIds) {
//        String scriptTaskId = scriptTaskElement.getId();
//        if (checkedNodeIds.contains(scriptTaskId)) {
//            return;
//        }
//
//        checkedNodeIds.add(scriptTaskId);
//
//        List<Direction> directions = getDirections(scriptTaskId);
//        directions.forEach(direction -> {
//            String toID = direction.getToID();
//            TaskElement toTaskElement = flow.getTask(toID);
//            if (toTaskElement != null) {
//                // 获取机器节点流出的环节
//                if (TaskNodeType.ScriptTask.getValue().equals(toTaskElement.getType())) {
//                    extractScriptTaskToUserTaskId(toTaskElement, toIDs, checkedNodeIds);
//                } else {
//                    toIDs.add(toID);
//                }
//            } else if (FlowDelegate.START_FLOW_ID.equals(toID) || FlowDelegate.END_FLOW_ID.equals(toID)) {
//                toIDs.add(toID);
//            }
//        });
//    }

    /**
     * @return
     */
    public List<Node> getAllTaskNodes() {

        // 获取下一任务结点
        List<Node> nodes = new ArrayList<Node>();
        List<TaskElement> tasks = flow.getTasks();
        for (TaskElement task : tasks) {
            Node node = new TaskNode();
            node.setName(task.getName());
            node.setId(task.getId());
            node.setType(task.getType());
            node.setForkMode(task.getParallelGateway().getForkMode());
            node.setJoinMode(task.getParallelGateway().getJoinMode());
            nodes.add(node);
        }
        return nodes;
    }

    /**
     * @return
     */
    public List<SubTaskNode> getAllSubTaskNodes() {
        List<SubTaskNode> nodes = new ArrayList<SubTaskNode>();
        List<TaskElement> tasks = flow.getTasks();
        for (TaskElement task : tasks) {
            if ("2".equals(task.getType())) {
                nodes.add((SubTaskNode) getTaskNode(task.getId()));
            }
        }
        return nodes;
    }

    /**
     * 是否可查看主流程配置读取
     * key:子流程Id
     * val:是否可查看主流程
     * 示例格式：{S808:0,S408:1}
     *
     * @return
     */
    public String getViewMainFlowJson() {
        JSONObject jsonObject = new JSONObject();
        List<TaskElement> tasks = flow.getTasks();
        for (TaskElement task : tasks) {
            if ("2".equals(task.getType())) {
                SubTaskElement subTaskElement = (SubTaskElement) task;
                String val = subTaskElement.getChildLookParent() == null ? "0" : subTaskElement.getChildLookParent();
                jsonObject.put(task.getId(), val);
            }
        }
        String json = jsonObject.toString();
        return json;
    }

    /**
     * @param taskId
     * @param pointcut
     * @return
     */
    public Script getTaskEventScript(String taskId, String pointcut) {
        TaskElement taskElement = this.flow.getTask(taskId);
        List<ScriptElement> eventScripts = taskElement.getEventScripts();
        for (ScriptElement eventScript : eventScripts) {
            String pc = eventScript.getPointcut();
            if (StringUtils.equals(pointcut, pc)) {
                String type = eventScript.getType();
                String contentType = eventScript.getContentType();
                String content = eventScript.getContent();
                if (StringUtils.isBlank(type) || StringUtils.isBlank(content)) {
                    return null;
                }
                Script script = new Script();
                script.setPointcut(pc);
                script.setType(type);
                script.setContentType(contentType);
                script.setContent(content);
                return script;
            }
        }
        return null;
    }

    /**
     * @return
     */
    public List<Direction> getAllDirections() {
        // 获取下一任务结点
        List<Direction> directions = new ArrayList<Direction>();
        List<DirectionElement> nodes = flow.getDirections();
        for (DirectionElement node : nodes) {
            Direction direction = new Direction();
            direction.setId(node.getId());
            direction.setName(node.getName());
            direction.setType(node.getType());
            direction.setToID(node.getToID());
            direction.setSortOrder(node.getSortOrder());
            direction.setRemark(node.getRemark());
            direction.setShowRemark(node.getIsShowRemark());
            direction.setFromID(node.getFromID());
            directions.add(direction);
        }
        return directions;
    }

    /**
     * 判断是否为分支的判断结点，根据task的conditionName不为空判断
     *
     * @param id
     * @return
     */
    public Boolean isConditionTask(Node node) {
        Boolean result = false;
        String taskID = node.getId();
        TaskElement taskElement = flow.getTask(taskID);
        result = taskElement != null && StringUtils.isNotBlank(taskElement.getConditionName());
//        List<TaskElement> tasks = flow.getTasks();
//        for (TaskElement task : tasks) {
//            if (taskID.equals(task.getId())) {
//                if (StringUtils.isNotBlank(task.getConditionName())) {
//                    result = true;
//                }
//                break;
//            }
//        }
        // 新版流程定义判断节点处理
        if (!result && taskElement != null) {
            List<GatewayElement> gatewayElements = flow.getGateways();
            if (CollectionUtils.isNotEmpty(gatewayElements)) {
                GatewayElement gatewayElement = gatewayElements.stream().filter(gateway -> {
                    Direction direction = getDirection(taskID, gateway.getId());
                    return direction != null;
                }).findFirst().orElse(null);
                result = gatewayElement != null;
            }
        }
        return result;
    }

    /**
     * 如何描述该方法
     *
     * @param node
     * @return
     */
    public boolean isMergeTask(Node node) {
        return false;
    }

    /**
     * @param directionId
     * @return
     */
    public Direction getDirection(String directionId) {
        DirectionElement directionElement = flow.getDirection(directionId);
        if (directionElement != null) {
            return convertDirectionElement(directionElement);
        }
//        List<DirectionElement> directions = flow.getDirections();
//        for (DirectionElement node : directions) {
//            if (node.getId().equals(directionId)) {
//                return convertDirectionElement(node);
//            }
//        }
        return null;
    }

    /**
     * @param digest
     * @return
     */
    public Direction getDirectionByIdDigest(String digest) {
        List<DirectionElement> directions = flow.getDirections();
        for (DirectionElement node : directions) {
            if (StringUtils.equals(DigestUtils.md5Hex(node.getId()), digest)) {
                return convertDirectionElement(node);
            }
        }
        return null;
    }

    /**
     * 根据流向开始fromID及结束toID获取流向对象
     *
     * @param fromID
     * @param toID
     * @return
     */
    public Direction getDirection(String fromID, String toID) {
        List<DirectionElement> directionElements = flow.getDirection(fromID, toID);
        Direction direction = null;
        if (CollectionUtils.isNotEmpty(directionElements)) {
            direction = convertDirectionElement(directionElements.get(0));
        }
//        List<DirectionElement> directions = flow.getDirections();
//        for (DirectionElement node : directions) {
//            if (fromID.equals(node.getFromID()) && node.getToID().equals(toID)) {
//                direction = convertDirectionElement(node);
//                break;
//            }
//        }
        return direction;
    }

    /**
     * 根据流向开始fromID及结束toID获取流向对象
     *
     * @param fromID
     * @param toIDs
     * @return
     */
    public List<Direction> getDirections(String fromID, List<String> toIDs) {
        List<Direction> rawDirections = new ArrayList<Direction>();
        Set<String> toIDSet = Sets.newLinkedHashSet(toIDs);
        for (String toId : toIDSet) {
            List<DirectionElement> directionElements = flow.getDirection(fromID, toId);
            if (CollectionUtils.isNotEmpty(directionElements)) {
                directionElements.forEach(directionElement -> {
                    rawDirections.add(convertDirectionElement(directionElement));
                });
            }
        }
//        Set<String> toIds = new LinkedHashSet<String>(toIDs);
//        List<DirectionElement> directions = flow.getDirections();
//        Direction direction = new Direction();
//        // List<Direction> rawDirections = new ArrayList<Direction>();
//        for (String toID : toIds) {
//            for (DirectionElement node : directions) {
//                if (fromID.equals(node.getFromID()) && toID.equals(node.getToID())) {
//                    direction = convertDirectionElement(node);
//                    rawDirections.add(direction);
//                }
//            }
//        }
        return rawDirections;
    }

    /**
     * 根据流向开始fromID获取流向对象
     *
     * @param fromID
     * @return
     */
    public List<Direction> getDirections(String fromID) {
        List<DirectionElement> directions = flow.getDirections();
        List<Direction> rawDirections = new ArrayList<Direction>();
        for (DirectionElement node : directions) {
            if (fromID.equals(node.getFromID())) {
                rawDirections.add(convertDirectionElement(node));
            }
        }
        return rawDirections;
    }

    // /**
    // * 判断某任务环节是否有指定的操作权限
    // *
    // * @param taskID
    // * @param string
    // */
    // public Boolean hasPermission(String taskID, String permission) {
    // return Boolean.TRUE;
    // }

    /**
     * 判断任务是否为流程定义的第一个任务
     *
     * @param taskID
     * @return
     */
    public Boolean isFirstTaskNode(String taskID) {
        Boolean result = Boolean.FALSE;
        List<DirectionElement> directions = flow.getDirections();
        for (DirectionElement direction : directions) {
            if (START_FLOW_ID.equals(direction.getFromID()) && taskID.equals(direction.getToID())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 获取指定任务的所有前任务结点
     *
     * @param taskID
     * @return
     */
    public List<String> getPreviousTaskNodes(String taskID) {
        List<DirectionElement> directions = flow.getDirections();
        List<String> nodeIDs = new ArrayList<String>();
        for (DirectionElement direction : directions) {
            if (taskID.equals(direction.getToID())) {
                // 如果流向的起点是开始结点忽略
                if (START_FLOW_ID.equals(direction.getFromID())) {
                    continue;
                }
                // 获取任务结点
                nodeIDs.add(direction.getFromID());
            }
        }
        return nodeIDs;
    }

    /**
     * 获取指定任务的所有前任务结点
     *
     * @param taskID
     * @return
     */
    public List<String> getNextTaskNodes(String taskID) {
        List<DirectionElement> directions = flow.getDirections();
        List<String> nodeIDs = new ArrayList<String>();
        for (DirectionElement direction : directions) {
            if (taskID.equals(direction.getFromID())) {
                // 获取任务结点
                nodeIDs.add(direction.getToID());
            }
        }
        return nodeIDs;
    }

    /**
     * 判断指定的任务结点是否为子任务
     *
     * @param nodeID
     * @return
     */
    public boolean isSubTaskNode(String taskID) {
        Node node = this.getTaskNode(taskID);
        return node == null ? false : "2".equals(node.getType());
    }

    /**
     * 判断指定的任务结点是否为最后一个任务
     *
     * @param taskID
     * @return
     */
    public boolean isLastTaskNode(String taskID) {
        Node node = this.getTaskNode(taskID);
        return END_FLOW_ID.equals(node.getToID());
    }

    /**
     * @param taskId
     * @return
     */
    public TaskForm getTaskForm(String taskId) {
        TaskForm taskForm = new TaskForm("-1");
        List<TaskElement> taskElements = flow.getTasks();

        for (TaskElement taskElement : taskElements) {
            if (taskElement.getId().equals(taskId)) {

                /* lmw 2015-4-22 10:05 begin */
                // taskForm = new TaskForm(taskElement.getFormID());
                taskForm = new TaskForm(flow.getProperty().getFormID(), taskElement.getFormID());
                /* lmw 2015-4-22 10:05 end */

                // add&modify by wujx 20160728 begin
                TaskFieldPropertyParser taskFieldPropertyParser = new TaskFieldPropertyParser();
                TaskFieldProperty taskFieldProperty = taskFieldPropertyParser.parser(taskElement);

                List<String> readFieldList = new ArrayList<String>();
                List<String> editFieldList = new ArrayList<String>();
                List<String> hideFieldList = new ArrayList<String>();
                List<String> notNullFieldList = new ArrayList<String>();
                List<String> fileRightsList = new ArrayList<String>();
                List<String> allFormFieldList = new ArrayList<String>();
                readFieldList.addAll(taskElement.getReadFieldValues());
                readFieldList.addAll(taskFieldProperty.getOnlyReadFields());
                editFieldList.addAll(taskElement.getEditFieldValues());
                editFieldList.addAll(taskFieldProperty.getEditFields());
                fileRightsList.addAll(taskElement.getFileRightsValues());
                allFormFieldList.addAll(taskElement.getAllFormFieldsValues());
                hideFieldList.addAll(taskElement.getHideFieldValues());
                hideFieldList.addAll(taskFieldProperty.getHideFields());
                notNullFieldList.addAll(taskElement.getNotNullFieldValues());
                notNullFieldList.addAll(taskFieldProperty.getNotNullFields());
                // 新版的流程表单字段配置没有只读域配置
                if (CollectionUtils.isNotEmpty(allFormFieldList)) {
                    taskForm.setOnlyReadFields(Lists.<String>newArrayListWithCapacity(0));
                } else {
                    taskForm.setOnlyReadFields(readFieldList);
                }
                taskForm.setEditFields(editFieldList);
                taskForm.setHideFields(hideFieldList);
                taskForm.setNotNullFields(notNullFieldList);
                taskForm.setFileRights(fileRightsList);
                taskForm.setAllFormFields(allFormFieldList);
                taskForm.setAllFormFieldWidgetIds(taskElement.getAllFormFieldWidgetIdValues());
                taskForm.setFormBtnRightSettings(taskElement.getFormBtnRightSettingValues());

                taskForm.setAllowUpload(taskFieldProperty.getAllowUploadFields());
                taskForm.setAllowDownload(taskFieldProperty.getAllowDownloadFields());
                taskForm.setAllowDelete(taskFieldProperty.getAllowDeleteFields());

                // add&modify by wujx 20160728 begin

                taskForm.setHideBlocks(taskElement.getHideBlockValues());
                taskForm.setHideTabs(taskElement.getHideTabValues());

                // 信息格式
                List<RecordElement> recordElements = Lists.newArrayList(taskElement.getRecords());

                /* lmw 2015-4-23 11:41 begin */
                /* lmw 2015-4-27 14:38 modify */
                // task配置优于流程设置，忽略第一个task节点
                // modify: 第一个环节也支持签署意见
                // if (!isFirstTaskNode(taskId)) {
                /* lmw 2015-4-29 14:38 modify */
                // 获取流程配置的信息格式
                /*
                 * PropertyElement pe = flow.getProperty(); List<RecordElement> records = pe !=
                 * null ? pe.getRecords() : new ArrayList<RecordElement>();
                 *
                 * // 生效的流程设置 List<RecordElement> recordsAdd = new LinkedList<RecordElement>();
                 * for (RecordElement r : records) { String f1 = r != null ? r.getField() :
                 * null; boolean isContain = false;// 表示表单字段是否已经在task配置中设置 for (RecordElement r1
                 * : recordElements) { String f2 = r1 != null ? r1.getField() : null; isContain
                 * = isContain || (StringUtils.isNotBlank(f1) && StringUtils.isNotBlank(f2) &&
                 * f1.equals(f2)); } if (!isContain) { recordsAdd.add(r); } }
                 * recordElements.addAll(recordsAdd);
                 */
                PropertyElement pe = flow.getProperty();
                if (CollectionUtils.isEmpty(recordElements) && pe != null) {
                    List<RecordElement> taskRecords = Lists.newArrayList();
                    for (RecordElement recordElement : pe.getRecords()) {
                        // 作用环节
                        String taskIds = recordElement.getTaskIds();
                        if (StringUtils.isBlank(taskIds) || StringUtils.contains(taskIds, taskId)) {
                            taskRecords.add(recordElement);
                        }
                    }
                    recordElements = taskRecords;
                }
                // }
                /* lmw 2015-4-23 11:41 end */

                /* lmw 2015-4-27 14:57 begin */
                if (recordElements == null) {
                    recordElements = new LinkedList<RecordElement>();
                }
                /* lmw 2015-4-27 14:57 end */
                FlowFormatService flowFormatService = ApplicationContextHolder.getBean(FlowFormatService.class);
                List<Record> records = new ArrayList<Record>();
                for (RecordElement recordElement : recordElements) {
                    Record record = new Record();
                    record.setName(recordElement.getName());
                    record.setField(recordElement.getField());
                    record.setWay(recordElement.getWay());
                    record.setValue(recordElement.getValue());
                    record.setAssembler(recordElement.getAssembler());
                    record.setIgnoreEmpty(recordElement.getIgnoreEmpty());
                    record.setContentOrigin(recordElement.getContentOrigin());
                    record.setFieldNotValidate(recordElement.getFieldNotValidate());
                    record.setEnableWysiwyg(recordElement.getIsEnableWysiwyg());
                    record.setEnablePreCondition(recordElement.getIsEnablePreCondition());
                    List<ConditionUnitElement> conditionElements = recordElement.getConditions();
                    if (CollectionUtils.isNotEmpty(conditionElements)) {
                        List<RecordCondition> recordConditions = Lists.newArrayList();
                        for (UnitElement conditionElement : conditionElements) {
                            RecordCondition recordCondition = new RecordCondition();
                            recordCondition.setType(conditionElement.getType());
                            recordCondition.setName(conditionElement.getArgValue());
                            recordCondition.setValue(conditionElement.getValue());
                            recordConditions.add(recordCondition);
                        }
                        record.setRecordConditions(recordConditions);
                    }
                    // 标记信息格式是否包含办理意见变量
                    FlowFormat flowFormat = flowFormatService.getByCode(recordElement.getValue());
                    if (flowFormat != null && (StringUtils.contains(flowFormat.getValue(), "${opinionText}")
                            || StringUtils.contains(flowFormat.getValue(), "${opinionText!}"))) {
                        record.setIncludeOpinionTextVariable(true);
                    }
                    records.add(record);
                }
                taskForm.setRecords(records);
            }
        }
        return taskForm;
    }

    /**
     * @return
     */
    public TaskForm getStartTaskForm() {
        Node node = getStartNode();
        return getTaskForm(node.getToID());
    }

    /**
     * 判断是否现在指定参与者
     *
     * @param id
     * @return
     */
    public boolean getIsSetTaskUser(String taskId) {
        return flow.getTask(taskId).isSetUser();
    }

    /**
     * 判断是否办理人为空自动进入下一个环节
     *
     * @param taskId
     * @return
     */
    public boolean getIsSetUserEmptyToTask(String taskId) {
        return flow.getTask(taskId).isSetUserEmptyToTask();
    }

    /**
     * 判断是否办理人为空自动进入下一个环节
     *
     * @param taskId
     * @return
     */
    public boolean getIsSetUserEmptyToUser(String taskId) {
        return flow.getTask(taskId).isSetUserEmptyToUser();
    }

    /**
     * 办理人为空自动进入的环节
     *
     * @param taskId
     * @return
     */
    public String getEmptyToTask(String taskId) {
        return flow.getTask(taskId).getEmptyToTask();
    }

    /**
     * 办理人为空转办时消息通知已办人员
     *
     * @param taskId
     * @return
     */
    public Boolean getEmptyNoteDone(String taskId) {
        return flow.getTask(taskId).getIsEmptyNoteDone();
    }

    /**
     * 选择具体办理人、只能选择一个人办理
     *
     * @param taskId
     * @return
     */
    public Boolean isSelectAgain(String taskId) {
        return flow.getTask(taskId).isSelectAgain();
    }

    /**
     * 选择具体办理人、只能选择一个人办理
     *
     * @return
     */
    public Boolean isOnlyOne(String taskId) {
        return flow.getTask(taskId).isOnlyOne();
    }

    /**
     * 只需要其中一个人办理、按人员顺序依次办理
     *
     * @param taskId
     * @return
     */
    public Boolean isAnyone(String taskId) {
        return flow.getTask(taskId).isAnyone();
    }

    /**
     * 只需要其中一个人办理、按人员顺序依次办理
     *
     * @param taskId
     * @return
     */
    public boolean isByOrder(String taskId) {
        return flow.getTask(taskId).isByOrder();
    }

    /**
     * 是否设置环节督办人
     *
     * @param taskId
     * @return
     */
    public String getIsSetMonitor(String taskId) {
        return flow.getTask(taskId).getIsSetMonitor();
    }

    /**
     * 是否设置环节手机提交
     *
     * @param taskId
     * @return
     */
    public String getIsAllowApp(String taskId) {
        TaskElement taskElement = flow.getTask(taskId);
        if (taskElement == null) {
            return "0";
        }
        return taskElement.getIsAllowApp();
    }

    /**
     * 是否允许环节手机提交
     *
     * @param taskId
     * @return
     */
    public boolean isAllowApp(String taskId) {
        TaskElement taskElement = flow.getTask(taskId);
        if (taskElement == null) {
            return false;
        }
        String isAllowApp = taskElement.getIsAllowApp();
        return StringUtils.isBlank(isAllowApp) || "1".equals(isAllowApp);
    }

    /**
     * 是否继承已存在的督办人
     *
     * @param taskId
     * @return
     */
    public boolean getIsInheritMonitor(String taskId) {
        return flow.getTask(taskId).isInheritMonitor();
    }

    /**
     * @return
     */
    public TaskData getFistTaskProcessInfo() {
        Node node = this.getStartNode();
        Node task = this.getTaskNode(node.getToID());
        TaskData taskData = new TaskData();
        TaskElement taskElement = this.flow.getTask(task.getId());
        // 环节ID
        taskData.setTaskId(taskElement.getId());
        // 环节名称
        taskData.setTaskName(taskElement.getName());
//        String locale = LocaleContextHolder.getLocale().toString();
//        if (MapUtils.isNotEmpty(taskElement.getI18n()) && taskElement.getI18n().containsKey(locale)
//                && StringUtils.isNotBlank(taskElement.getI18n().get(locale).get(taskElement.getId()))) {
//            taskData.setTaskName(taskElement.getI18n().get(locale).get(taskElement.getId()));
//        }
        // 环节参与人原始配置名称
        List<UserUnitElement> userElements = taskElement.getUsers();
        StringBuilder sb = new StringBuilder();
        Iterator<UserUnitElement> it = userElements.iterator();
        while (it.hasNext()) {
            sb.append(it.next().getArgValue());
            if (it.hasNext()) {
                sb.append(Separator.COMMA.getValue());
            }
        }
        taskData.setTaskRawUserNames(sb.toString());
        return taskData;
    }

    /**
     * 获取流程第一个环节的相关任务数据
     *
     * @return
     */
    public TaskData getFistTaskData() {
        Node node = this.getStartNode();
        Node task = this.getTaskNode(node.getToID());
        TaskData taskData = getTaskConfigInfo(task.getId());
        // 第一个环节信息
        taskData.setFirstTaskId(task.getId());
        taskData.setFirstTaskName(task.getName());
        // 设置配置的表单
        if (!StringUtils.isBlank(task.getFormID())) {
            taskData.setFormUuid(task.getFormID());
        } else {
            taskData.setFormUuid(this.flow.getProperty().getFormID());
        }
        // 流程转为提交按钮
        List<Direction> directions = this.getDirections(task.getId());
        if (isUseAsButton(directions)) {
            Collections.sort(directions);
            List<CustomDynamicButton> buttons = new ArrayList<CustomDynamicButton>();
            boolean allDirectionAsButton = isAllDirectionAsButton(directions);
            addToCustomDynamicButton(directions, buttons, allDirectionAsButton);
            taskData.setCustomDynamicButtons(buttons);
        }
        taskData.filterOwnerCustomDynamicButton();
        return taskData;
    }

    /**
     * @param directions
     * @return
     */
    private boolean isAllDirectionAsButton(List<Direction> directions) {
        for (Direction direction : directions) {
            if (!direction.isUseAsButton()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param directions
     * @return
     */
    private boolean isUseAsButton(List<Direction> directions) {
        for (Direction direction : directions) {
            if (direction.isUseAsButton()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param directions
     * @param buttons
     */
    private void addToCustomDynamicButton(List<Direction> directions, List<CustomDynamicButton> buttons, boolean replace) {
        int index = 0;
        for (Direction direction : directions) {
            if (!direction.isUseAsButton()) {
                continue;
            }
            CustomDynamicButton button = new CustomDynamicButton();
            button.setBtnSource("1");
            button.setId(direction.getIdMd5Hex());
            button.setCode(WorkFlowPrivilege.Submit.getCode());
            button.setNewCode(WorkFlowPrivilege.Submit.getCode() + "_" + index);
            button.setRequiredNewCodePrivilege(false);
            if (StringUtils.isNotBlank(direction.getButtonName())) {
                button.setName(direction.getButtonName());
            } else {
                button.setName(direction.getName());
            }
            if (StringUtils.isNotBlank(direction.getButtonClassName())) {
                button.setClassName(direction.getButtonClassName());
            } else {
                button.setClassName(direction.getId());
            }
            button.setUseWay(replace ? CustomDynamicButton.REPLACE : CustomDynamicButton.NEW_OPERATE);
            button.setTaskId(direction.getToID());
            button.setUuid(direction.getId() + ".useAsButtonName");
            if (StringUtils.isNotBlank(direction.getSortOrder())) {
                button.setSortOrder(Integer.valueOf(direction.getSortOrder()));
            }
            buttons.add(button);
            index++;
        }
    }

    /**
     * @return
     */
    public TaskData getLastTaskData() {
        return null;
    }

    /**
     * @param taskId
     * @return
     */
    public TaskConfiguration getTaskConfiguration(String taskId) {
        TaskConfiguration configuration = new TaskConfiguration(taskId, this);

        // 套打模板ID
        List<PrintTemplate> printTemplates = getTaskPrintTemplates(taskId);
        if (printTemplates.size() == 1) {
            configuration.setPrintTemplateId(printTemplates.get(0).getId());
            configuration.setPrintTemplateUuid(printTemplates.get(0).getUuid());
        }

        TaskElement taskElement = this.flow.getTask(taskId);
        if (taskElement == null) {
            return configuration;
        }

        // 环节信息
        configuration.setTaskName(taskElement.getName());
//        String locale = LocaleContextHolder.getLocale().toString();
//        if (MapUtils.isNotEmpty(taskElement.getI18n()) && taskElement.getI18n().containsKey(locale)
//                && StringUtils.isNotBlank(taskElement.getI18n().get(locale).get(taskElement.getId()))) {
//            configuration.setTaskName(taskElement.getI18n().get(locale).get(taskElement.getId()));
//        }
        // 设置配置的表单
        if (!StringUtils.isBlank(taskElement.getFormID())) {
            configuration.setFormUuid(taskElement.getFormID());
        } else {
            configuration.setFormUuid(this.flow.getProperty().getFormID());
        }

        // 流水号
        configuration.setSerialNoDefId(taskElement.getSerialNo());
        // 发起工作按钮
        configuration.setStartRights(taskElement.getButtonStartRights());
        configuration.setStartButtons(taskElement.getStartButtons());
        configuration.setStartRightConfig(taskElement.getStartRightConfig());
        // 待办工作按钮
        configuration.setTodoRights(taskElement.getButtonTodoRights());
        configuration.setTodoButtons(taskElement.getTodoButtons());
        configuration.setTodoRightConfig(taskElement.getTodoRightConfig());
        // 已办工作按钮
        configuration.setDoneRights(taskElement.getButtonDoneRights());
        configuration.setDoneButtons(taskElement.getDoneButtons());
        configuration.setDoneRightConfig(taskElement.getDoneRightConfig());
        // 督办工作按钮
        configuration.setMonitorRights(taskElement.getButtonMonitorRights());
        configuration.setMonitorButtons(taskElement.getMonitorButtons());
        configuration.setMonitorRightConfig(taskElement.getMonitorRightConfig());
        // 监控工作按钮
        configuration.setAdminRights(taskElement.getButtonAdminRights());
        configuration.setAdminButtons(taskElement.getAdminButtons());
        configuration.setAdminRightConfig(taskElement.getAdminRightConfig());
        // 抄送工作按钮
        configuration.setCopyToRights(taskElement.getButtonCopyToRights());
        configuration.setCopyToButtons(taskElement.getCopyToButtons());
        configuration.setCopyToRightConfig(taskElement.getCopyToRightConfig());
        // 查阅工作按钮
        configuration.setViewerRights(taskElement.getButtonViewerRights());
        configuration.setViewerButtons(taskElement.getViewerButtons());
        configuration.setViewerRightConfig(taskElement.getViewerRightConfig());

        // 子环节
        if (taskElement.isSubTask()) {
            List<Button> doneRightsButtons = new ArrayList<>();
            List<String> doneRights = new ArrayList<String>();
            doneRights.add(WorkFlowPrivilege.Cancel.getCode());
            doneRights.add(WorkFlowPrivilege.Attention.getCode());
            doneRights.add(WorkFlowPrivilege.Print.getCode());
            doneRights.add(WorkFlowPrivilege.CopyTo.getCode());
            doneRights.add(WorkFlowPrivilege.ViewProcess.getCode());
            doneRightsButtons.add(new Button(WorkFlowPrivilege.Cancel.getName(), WorkFlowPrivilege.Cancel.getCode()));
            doneRightsButtons.add(new Button(WorkFlowPrivilege.Attention.getName(), WorkFlowPrivilege.Attention.getCode()));
            doneRightsButtons.add(new Button(WorkFlowPrivilege.Print.getName(), WorkFlowPrivilege.Print.getCode()));
            doneRightsButtons.add(new Button(WorkFlowPrivilege.CopyTo.getName(), WorkFlowPrivilege.CopyTo.getCode()));
            doneRightsButtons.add(new Button(WorkFlowPrivilege.ViewProcess.getName(), WorkFlowPrivilege.ViewProcess.getCode()));
            configuration.setDoneRights(doneRights);
            configuration.setDoneButtons(doneRightsButtons);
            // 子流程环节可查看办理过程
            // configuration.getDoneRights().add(WorkFlowPrivilege.ViewProcess.getCode());
            configuration.getMonitorRights().add(WorkFlowPrivilege.ViewProcess.getCode());
            configuration.getMonitorButtons().add(new Button(WorkFlowPrivilege.ViewProcess.getName(), WorkFlowPrivilege.ViewProcess.getCode()));
            configuration.getAdminRights().add(WorkFlowPrivilege.ViewProcess.getCode());
            configuration.getAdminButtons().add(new Button(WorkFlowPrivilege.ViewProcess.getName(), WorkFlowPrivilege.ViewProcess.getCode()));
        }

        List<CustomDynamicButton> buttons = getTaskCustomDynamicButtons(taskElement);
        configuration.setCustomDynamicButtons(buttons);

        // 流程转为提交按钮
        List<Direction> directions = this.getDirections(taskId);
        if (isUseAsButton(directions)) {
            Collections.sort(directions);
            boolean allDirectionAsButton = isAllDirectionAsButton(directions);
            addToCustomDynamicButton(directions, buttons, allDirectionAsButton);
            configuration.setCustomDynamicButtons(buttons);
        }

        // 意见立场
        List<UnitElement> optNames = taskElement.getOptNames();
        List<Map<String, String>> opinions = new ArrayList<Map<String, String>>(0);
        for (UnitElement unitElement : optNames) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", unitElement.getArgValue());
            map.put("value", unitElement.getValue());
            opinions.add(map);
        }
        configuration.setOpinions(opinions);


        return configuration;
    }

    /**
     * @param id
     * @return
     */
    public TaskData getTaskConfigInfo(String taskId) {
        TaskData taskData = new TaskData();

        // 设置是否为流程的第一个任务结点
        taskData.setIsFirstTaskNode(taskId, isFirstTaskNode(taskId));

        // 套打模板ID
        List<PrintTemplate> printTemplates = getTaskPrintTemplates(taskId);
        if (printTemplates.size() == 1) {
            taskData.setPrintTemplateId(taskId, printTemplates.get(0).getId());
            taskData.setPrintTemplateUuid(taskId, printTemplates.get(0).getUuid());
        }

        TaskElement taskElement = this.flow.getTask(taskId);
        if (taskElement == null) {
            return taskData;
        }
        // // 环节名称
        // taskData.setTaskName(taskElement.getName());
        // // 环节参与人原始配置名称
        // List<UnitElement> userElements = taskElement.getUsers();
        // StringBuilder sb = new StringBuilder();
        // Iterator<UnitElement> it = userElements.iterator();
        // while (it.hasNext()) {
        // sb.append(it.next().getArgValue());
        // if (it.hasNext()) {
        // sb.append(Separator.COMMA.getValue());
        // }
        // }
        // taskData.setTaskRawUserNames(sb.toString());

        // 流水号
        taskData.setSerialNoDefId(taskElement.getSerialNo());
        // 发起工作按钮
        taskData.setStartRights(taskElement.getButtonStartRights());
        // 待办工作按钮
        taskData.setTodoRights(taskElement.getButtonTodoRights());
        // 已办工作按钮
        taskData.setDoneRights(taskElement.getButtonDoneRights());
        // 督办工作按钮
        taskData.setMonitorRights(taskElement.getButtonMonitorRights());
        // 监控工作按钮
        taskData.setAdminRights(taskElement.getButtonAdminRights());

        // 子环节
        if (taskElement.isSubTask()) {
            List<String> doneRights = new ArrayList<String>();
            doneRights.add(WorkFlowPrivilege.Attention.getCode());
            doneRights.add(WorkFlowPrivilege.Print.getCode());
            doneRights.add(WorkFlowPrivilege.CopyTo.getCode());
            taskData.setDoneRights(doneRights);
            // 子流程环节可查看办理过程
            taskData.getDoneRights().add(WorkFlowPrivilege.ViewProcess.getCode());
            taskData.getMonitorRights().add(WorkFlowPrivilege.ViewProcess.getCode());
            taskData.getAdminRights().add(WorkFlowPrivilege.ViewProcess.getCode());
        }

        List<CustomDynamicButton> buttons = getTaskCustomDynamicButtons(taskElement);
        taskData.setCustomDynamicButtons(buttons);

        // 流程转为提交按钮
        List<Direction> directions = this.getDirections(taskId);
        if (isUseAsButton(directions)) {
            Collections.sort(directions);
            boolean allDirectionAsButton = isAllDirectionAsButton(directions);
            addToCustomDynamicButton(directions, buttons, allDirectionAsButton);
            taskData.setCustomDynamicButtons(buttons);
        }

        // 意见立场
        List<UnitElement> optNames = taskElement.getOptNames();
        List<Map<String, String>> options = new ArrayList<Map<String, String>>(0);
        for (UnitElement unitElement : optNames) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", unitElement.getArgValue());
            map.put("value", unitElement.getValue());
            options.add(map);
        }
        taskData.setOptions(taskId, options);

        return taskData;
    }

    /**
     * 如何描述该方法
     *
     * @param taskElement
     * @return
     */
    private List<CustomDynamicButton> getTaskCustomDynamicButtons(TaskElement taskElement) {
        List<ButtonElement> buttonElements = taskElement.getButtons();
        List<CustomDynamicButton> buttons = new ArrayList<CustomDynamicButton>();
        for (ButtonElement buttonElement : buttonElements) {
            CustomDynamicButton button = new CustomDynamicButton();
            button.setUuid(buttonElement.getUuid());
            // 按钮来源
            button.setBtnSource(buttonElement.getBtnSource());
            // 按钮角色
            button.setBtnRole(buttonElement.getBtnRole());
            // 按钮ID
            button.setId(buttonElement.getBtnId());
            // 产品集成UUID
            button.setPiUuid(buttonElement.getPiUuid());
            // 产品集成名称
            button.setPiName(buttonElement.getPiName());
            // 锚点类型
            button.setHashType(buttonElement.getHashType());
            // 锚点
            button.setHash(buttonElement.getHash());
            // 事件处理
            button.setEventHandler(buttonElement.getEventHandler());
            // 事件参数
            button.setEventParams(buttonElement.getEventParams());
            // 目标位置
            button.setTargetPosition(buttonElement.getTargetPosition());
            // 按钮编号
            button.setCode(buttonElement.getBtnValue());
            // 新的按钮编号
            button.setNewCode(buttonElement.getNewCode());
            // 新版本没有新的编码，自动生成
            if (CustomDynamicButton.SOURCE_EVENT_HANDLER.equals(buttonElement.getBtnSource())) {
                if (StringUtils.isBlank(buttonElement.getId())) {
                    buttonElement.setId(DigestUtils.md5Hex(buttonElement.getNewName()));
                }
            } else {
                button.setRequiredNewCodePrivilege(false);
                if (StringUtils.isBlank(button.getNewCode())) {
                    button.setNewCode(button.getCode() + "_" + DigestUtils.md5Hex(buttonElement.getNewName()));
                    button.setId(button.getNewCode());
                } else if (StringUtils.isBlank(button.getId())) {
                    button.setId(button.getNewCode());
                }
            }
            // 按钮名称
            button.setName(buttonElement.getNewName());
            // 使用方式，0(替换名称)、1(全部替换)、2(新增操作)
            button.setUseWay(buttonElement.getUseWay());
            // 目标提交环节
            button.setTaskId(buttonElement.getBtnArgument());
            // 按钮使用者
            if (StringUtils.isNotBlank(buttonElement.getBtnOwners())) {
                button.setOwners(Arrays.asList(buttonElement.getBtnOwnerIds().split(Separator.SEMICOLON.getValue())));
            }
            // 按钮提交参与者
            String rawUserIds = buttonElement.getBtnUserIds();
            String[] users = new String[]{"", "", "", ""};
            if (StringUtils.isNotBlank(rawUserIds)) {
                rawUserIds = rawUserIds.replaceAll(Separator.COMMA.getValue(), " , ");
                String[] userIds = rawUserIds.split(Separator.COMMA.getValue());
                if (userIds.length == 5) {
                    for (int index = 1; index < userIds.length; index++) {
                        users[index - 1] = StringUtils.trim(userIds[index]);
                    }
                }
            }
            button.setUsers(Arrays.asList(users));
            // 按钮提交抄送者
            String rawCopyUserIds = buttonElement.getBtnCopyUserIds();
            String[] copyUsers = new String[]{"", "", "", ""};
            if (StringUtils.isNotBlank(rawCopyUserIds)) {
                rawCopyUserIds = rawCopyUserIds.replaceAll(Separator.COMMA.getValue(), " , ");
                String[] copyUserIds = rawCopyUserIds.split(Separator.COMMA.getValue());
                if (copyUserIds.length == 5) {
                    for (int index = 1; index < copyUserIds.length; index++) {
                        copyUsers[index - 1] = StringUtils.trim(copyUserIds[index]);
                    }
                }
            }
            button.setCopyUsers(Arrays.asList(copyUsers));
            if (StringUtils.isNotBlank(buttonElement.getBtnStyle())) {
                button.setClassName(buttonElement.getBtnStyle());
            } else {
                button.setClassName(buttonElement.getBtnClassName());
            }
            button.setBtnIcon(buttonElement.getBtnIcon());
            if (StringUtils.isNotBlank(buttonElement.getSortOrder())) {
                button.setSortOrder(Integer.valueOf(buttonElement.getSortOrder()));
            }
            button.setConfiguration(buttonElement.getConfiguration());
            buttons.add(button);
        }
        return buttons;
    }

    /**
     * 获取环节流水号定义ID
     *
     * @param taskId
     * @return
     */
    public String getTaskSerialNoDefId(String taskId) {
        TaskElement taskElement = this.flow.getTask(taskId);
        return taskElement.getSerialNo();
    }

    /**
     * @param taskID
     * @param actionRollBack
     * @return
     */
    public boolean hasPermission(String taskID, String actionRollBack) {
        return true;
    }

    /**
     * 流程监控者
     *
     * @return
     */
    public List<UserUnitElement> getFlowAdmins() {
        return this.flow.getProperty().getAdmins();
    }

    /* add by huanglinchuan 2014.10.28 begin */

    /**
     * 流程阅读者
     *
     * @return
     */
    public List<UserUnitElement> getFlowViewers() {
        return this.flow.getProperty().getViewers();
    }

    /* add by huanglinchuan 2014.10.28 end */

    /**
     * 获取流程组织选择框的监控者
     *
     * @return
     */
    public List<String> getFlowUnitAdminValues() {
        return this.flow.getProperty().getUnitAdminValues();
    }

    /**
     * @return
     */
    public List<String> getFlowFormFieldAdminValues() {
        return this.flow.getProperty().getFormFieldAdminValues();
    }

    /**
     * @return
     */
    public List<String> getFlowOptionAdminValues() {
        return this.flow.getProperty().getOptionAdminValues();
    }

    /**
     *
     */
    public List<UserUnitElement> getFlowMonitors() {
        List<UserUnitElement> unitElements = this.flow.getProperty().getMonitors();
        return unitElements;
    }

    /**
     * @param flowStateCode
     * @return
     */
    public String getFlowStateName(String flowStateCode) {
        List<FlowStateElement> flowStates = this.flow.getProperty().getFlowStates();
        if (CollectionUtils.isNotEmpty(flowStates)) {
            for (FlowStateElement flowState : flowStates) {
                if (StringUtils.equals(flowState.getCode(), flowStateCode)) {
                    return flowState.getName();
                }
            }
        }

        // 返回流程状态默认值
        if (StringUtils.equals(WorkFlowState.Draft, flowStateCode)) {
            return "草稿";
        }
        if (StringUtils.equals(WorkFlowState.Todo, flowStateCode)) {
            return "审批";
        }
        if (StringUtils.equals(WorkFlowState.Over, flowStateCode)) {
            return "办结";
        }

        return flowStateCode;
    }

    /**
     * @param pointcut
     * @return
     */
    public Script getFlowEventScript(String pointcut) {
        List<ScriptElement> eventScripts = this.flow.getProperty().getEventScripts();
        for (ScriptElement eventScript : eventScripts) {
            String pc = eventScript.getPointcut();
            if (StringUtils.equals(pointcut, pc)) {
                String type = eventScript.getType();
                String contentType = eventScript.getContentType();
                String content = eventScript.getContent();
                if (StringUtils.isBlank(type) || StringUtils.isBlank(content)) {
                    return null;
                }
                Script script = new Script();
                script.setPointcut(pc);
                script.setType(type);
                script.setContentType(contentType);
                script.setContent(content);
                return script;
            }
        }
        return null;
    }

    /**
     * @param taskId
     * @return
     */
    public List<UserUnitElement> getTaskUsers(String taskId) {
        return this.flow.getTask(taskId).getUsers();
    }

    public List<UserUnitElement> getTaskTransferUsers(String taskId) {
        return this.flow.getTask(taskId).getTransferUsers();
    }

    /**
     * @param taskId
     * @return
     */
    public List<UserUnitElement> getTaskCopyUsers(String taskId) {
        return this.flow.getTask(taskId).getCopyUsers();
    }

    /**
     * @param taskId
     * @return
     */
    public List<UserUnitElement> getTaskEmptyToUsers(String taskId) {
        return this.flow.getTask(taskId).getEmptyToUsers();
    }

    /**
     * @param taskId
     * @return
     */
    public List<UserUnitElement> getTaskDecisionMakers(String taskId) {
        return this.flow.getTask(taskId).getDecisionMakers();
    }

    /**
     * @param taskId
     * @return
     */
    public List<UserUnitElement> getTaskMonitors(String taskId) {
        return this.flow.getTask(taskId).getMonitors();
    }

    /**
     * @param taskId
     * @return
     */
    public String getIsSetCopyUser(String taskId) {
        return this.flow.getTask(taskId).getIsSetCopyUser();
    }

    /**
     * @param taskId
     * @return
     */
    public boolean getIsSetTransferUser(String taskId) {
        return this.flow.getTask(taskId).isSetTransferUser();
    }

    public String getTransferUser(String taskId) {
        return this.flow.getTask(taskId).getIsSetTransferUser();
    }

    /**
     * @param taskId
     * @return
     */
    public boolean isConfirmCopyUser(String taskId) {
        return this.flow.getTask(taskId).isConfirmCopyUser();
    }

    /**
     * @param taskId
     * @return
     */
    public String getCopyUserCondition(String taskId) {
        return this.flow.getTask(taskId).getCopyUserCondition();
    }

    /**
     * @param taskId
     * @return
     */
    public List<PrintTemplate> getTaskPrintTemplates(String taskId) {
        TaskElement taskElement = this.flow.getTask(taskId);
        if (taskElement == null) {
            return Collections.emptyList();
        }
        String printTemplateId = taskElement.getPrintTemplateId();
        String printTemplateName = taskElement.getPrintTemplate();
        String printTemplateUuid = taskElement.getPrintTemplateUuid();
        if (StringUtils.isBlank(printTemplateId)) {
            printTemplateId = this.flow.getProperty().getPrintTemplateId();
            printTemplateName = this.flow.getProperty().getPrintTemplate();
            printTemplateUuid = this.flow.getProperty().getPrintTemplateUuid();
        }
        List<PrintTemplate> templates = Lists.newArrayListWithCapacity(0);
        if (StringUtils.isNotBlank(printTemplateId)) {
            String[] ids = StringUtils.split(printTemplateId, Separator.SEMICOLON.getValue());
            String[] names = StringUtils.split(printTemplateName, Separator.SEMICOLON.getValue());
            String[] uuids = StringUtils.split(printTemplateUuid, Separator.SEMICOLON.getValue());


            // 控制前端存在删除names与uuids，不会删除ids
            Map<String, PrintTemplate> map = Maps.newHashMap();
            for (int i = 0; i < names.length; i++) {
                PrintTemplate template = new PrintTemplate();
                template.setId(ids[i]);
                template.setName(names[i]);
                template.setUuid(uuids != null ? uuids[i] : null);
                templates.add(template);
                map.put(template.getUuid(), template);
            }

            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                AppDefElementI18nService appDefElementI18nService = ApplicationContextHolder.getBean(AppDefElementI18nService.class);
                List<AppDefElementI18nEntity> i18nEntities = appDefElementI18nService.getI18ns(Sets.newHashSet(uuids), IexportType.PrintTemplate
                        , "name", LocaleContextHolder.getLocale().toString());
                if (CollectionUtils.isNotEmpty(i18nEntities)) {
                    for (AppDefElementI18nEntity i : i18nEntities) {
                        if (map.containsKey(i.getDefId()) && StringUtils.isNotBlank(i.getContent())) {
                            map.get(i.getDefId()).setName(i.getContent());
                        }
                    }
                }
            }
        }
        return templates;
    }

    /**
     * @return
     */
    public boolean isFree() {
        return this.flow.getProperty().isFree();
    }

    /**
     * @return
     */
    public boolean isKeepRuntimePermission() {
        return "1".equals(this.flow.getProperty().getKeepRuntimePermission());
    }

    /**
     * @param taskId
     * @return
     */
    public String getGranularity(String taskId) {
//        String granularity = null;
//        TaskElement taskElement = this.flow.getTask(taskId);
//        // 环节的权限粒度
//        if (taskElement != null) {
//            granularity = taskElement.getGranularity();
//        }
//        // 流程的权限粒度
//        if (StringUtils.isBlank(granularity)) {
//            granularity = this.flow.getProperty().getGranularity();
//        }
//
//        if (StringUtils.isNotBlank(granularity)) {
//            return granularity;
//        }
        // 默认粒度为动态
        return SidGranularity.ACTIVITY;
    }

    /**
     * @param taskId
     * @param taskData
     * @return
     */
    public String getGranularity(String taskId, TaskData taskData, List<UserUnitElement> userUnitElements) {
        // 环节数据指定的权限粒度
        String granularity = taskData.getSidGranularity(taskId);
        if (StringUtils.isNotBlank(granularity)) {
            return granularity;
        }

        // 存在人员过滤时，用户粒度为用户
        if (CollectionUtils.isNotEmpty(userUnitElements)) {
            boolean hasFilterUserOption = userUnitElements.stream().filter(userUnitElement -> Integer.valueOf(8).equals(userUnitElement.getType()))
                    .findFirst().isPresent();
            if (hasFilterUserOption) {
                return SidGranularity.USER;
            }
        }

        TaskElement taskElement = this.flow.getTask(taskId);
        // 配置由前一环节办理人选择具体办理人、按人员顺序办理时，用户粒度为用户
        if (taskElement != null) {
            if (taskElement.isSelectAgain() || taskElement.isByOrder()
                    || TaskNodeType.CollaborationTask.getValue().equals(taskElement.getType())) {
                return SidGranularity.USER;
            }
        }

//        // 环节的权限粒度
//        if (taskElement != null) {
//            granularity = taskElement.getGranularity();
//        }
//        // 流程的权限粒度
//        if (StringUtils.isBlank(granularity)) {
//            granularity = this.flow.getProperty().getGranularity();
//        }
//
//        if (StringUtils.isNotBlank(granularity)) {
//            return granularity;
//        }
        // 默认粒度为动态
        return SidGranularity.ACTIVITY;
    }

    /**
     * @return
     */
    public String getFlowGranularity(List<UserUnitElement> userUnitElements) {
        // 存在人员过滤时，权限粒度为用户
        if (CollectionUtils.isNotEmpty(userUnitElements)) {
            boolean hasFilterUserOption = userUnitElements.stream().filter(userUnitElement -> Integer.valueOf(8).equals(userUnitElement.getType()))
                    .findFirst().isPresent();
            if (hasFilterUserOption) {
                return SidGranularity.USER;
            }
        }

        // 流程的权限粒度
        String granularity = this.flow.getProperty().getGranularity();

        if (StringUtils.isNotBlank(granularity)) {
            return granularity;
        }
        // 默认粒度为动态
        return SidGranularity.ACTIVITY;
    }

    /**
     * @param taskId
     * @return
     */
    public boolean isOnlyOneGranularity(String taskId) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * 根据所有的定时器配置信息
     *
     * @param taskId
     * @return
     */
    public List<TimerElement> getTimers() {
        List<TimerElement> timerElements = this.flow.getTimers();
        return timerElements == null ? Collections.emptyList() : timerElements;
    }

    /**
     * 根据环节ID获取环节所有的定时器配置信息
     *
     * @param taskId
     * @return
     */
    public List<TimerElement> getTimer(String taskId) {
        List<TimerElement> timerElements = new ArrayList<TimerElement>();
        List<TimerElement> allTimers = this.flow.getTimers();
        for (TimerElement timerElement : allTimers) {
            Map<String, UnitElement> taskMap = timerElement.getTaskMap();
            if (taskMap.containsKey(taskId)) {
                timerElements.add(timerElement);
            }
        }
        return timerElements;
    }

    /**
     * @return
     */
    public List<BackUser> getBakUsers() {
        List<BackUser> backUsers = new ArrayList<BackUser>();
        List<UserUnitElement> unitElements = this.getFlow().getProperty().getBakUsers();
        for (UnitElement unitElement : unitElements) {
            String value = unitElement.getValue();
            String argValue = unitElement.getArgValue();
            String[] aUsers = StringUtils.split(value, "|");
            String[] bUsers = StringUtils.split(argValue, "|");
            if (aUsers.length == 2) {
                String[] aUserIds = StringUtils.split(aUsers[1], Separator.SEMICOLON.getValue());
                String[] bUserIds = StringUtils.split(bUsers[1], Separator.SEMICOLON.getValue());
                for (String aUserId : aUserIds) {
                    BackUser backUser = new BackUser();
                    backUser.setaUserId(aUserId);
                    backUser.setbUserIds(Arrays.asList(bUserIds));
                    backUsers.add(backUser);
                }
            }
        }
        return backUsers;
    }

    public Map<String, List<MessageTemplate>> getMessageTemplateMap() {
        if (!messageTemplateMap.isEmpty()) {
            return messageTemplateMap;
        }

        List<MessageTemplateElement> messageTemplates = this.flow.getProperty().getMessageTemplates();
        for (MessageTemplateElement template : messageTemplates) {
            List<MessageTemplate> templateList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(template.getDistributerElements())) {// 新的消息分发配置
                for (MessageDistributerElement distributer : template.getDistributerElements()) {
                    MessageTemplate temp = new MessageTemplate();
                    temp.setIsSendMsg(template.getIsSendMsg());
                    temp.setType(typeConvertion(template.getType(), distributer));
                    temp.setTypeName(template.getTypeName());
                    temp.setId(distributer.getId());// 消息模板ID
                    temp.setName(distributer.getName());
                    String condition = template.getConditionElementString();
                    temp.setCondition(condition);
                    temp.setMsgRecipients(distributer.getDesignee());// 指定人员
                    templateList.add(temp);
                }
            } else {
                MessageTemplate messageTemplate = new MessageTemplate();
                messageTemplate.setType(template.getType());
                messageTemplate.setTypeName(template.getTypeName());
                messageTemplate.setId(template.getId());
                messageTemplate.setName(template.getName());
                messageTemplate.setCondition(template.getCondition());
                templateList.add(messageTemplate);
            }

            List<UserUnitElement> extraMsgRecipientElements = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(template.getCopyMsgRecipients())) {// 抄送人员（新版流程定义的配置）
                extraMsgRecipientElements = template.getCopyMsgRecipients();
            } else if (StringUtils.isNotBlank(template.getExtraMsgRecipientUserIds())) { // 旧版流程定义的抄送人员
                String[] extraMsgRecipientUserIds = template.getExtraMsgRecipientUserIds().split(",");
                for (int i = 1; i < extraMsgRecipientUserIds.length; i++) {
                    if (!StringUtils.trimToEmpty(extraMsgRecipientUserIds[i]).equals("")) {
                        String[] arr_tmp = extraMsgRecipientUserIds[i].split(";");
                        for (String tmp : arr_tmp) {
                            UserUnitElement ue = new UserUnitElement();
                            ue.setType((int) Math.pow(2, i - 1));
                            // ue.setArgValue(extraMsgRecipientUserIds[i]);
                            ue.setValue(tmp);
                            extraMsgRecipientElements.add(ue);
                        }
                    }
                }
            }

            // 额外的自定义分发对象
            if (StringUtils.isNotBlank(template.getExtraMsgCustomRecipientUserIds())) {
                UserUnitElement ue = new UserUnitElement();
                ue.setType(16);
                ue.setValue(template.getExtraMsgCustomRecipientUserIds());
                extraMsgRecipientElements.add(ue);
            }

            for (MessageTemplate messageTemplate : templateList) {
                messageTemplate.setIsSendMsg(template.getIsSendMsg());
                if (!extraMsgRecipientElements.isEmpty()) {
                    messageTemplate.setExtraMsgRecipients(extraMsgRecipientElements);
                }
                if (!messageTemplateMap.containsKey(messageTemplate.getType())) {
                    messageTemplateMap.put(messageTemplate.getType(), new ArrayList<MessageTemplate>());
                }
                messageTemplateMap.get(messageTemplate.getType()).add(messageTemplate);
            }
        }

        if (messageTemplateMap.isEmpty()) {
            messageTemplateMap.put("", null);
        }

        return messageTemplateMap;
    }

    /**
     * 新旧流程的消息类型转换
     *
     * @param type
     * @param template
     * @param distributer
     * @return
     */
    private String typeConvertion(String type, MessageDistributerElement distributer) {
        boolean isAlarm = WorkFlowMessageTemplate.WF_WORK_FLOW_ALARM.getType().equals(type);
        if (isAlarm || WorkFlowMessageTemplate.WF_WORK_FLOW_DUE.getType().equals(type)) { // 预警提醒/逾期提醒类型转换
            switch (distributer.getDistributerTypeName()) {
                case "办理人":
                    return isAlarm ? WorkFlowMessageTemplate.WF_WORK_ALARM_DOING.getType()
                            : WorkFlowMessageTemplate.WF_WORK_DUE_DOING.getType();
                case "办理人的上级领导":
                    return isAlarm ? WorkFlowMessageTemplate.WF_WORK_ALARM_DOING_SUPERIOR.getType()
                            : WorkFlowMessageTemplate.WF_WORK_DUE_DOING_SUPERIOR.getType();
                case "督办人":
                    return isAlarm ? WorkFlowMessageTemplate.WF_WORK_ALARM_SUPERVISE.getType()
                            : WorkFlowMessageTemplate.WF_WORK_DUE_SUPERVISE.getType();
                case "跟踪人":
                    return isAlarm ? WorkFlowMessageTemplate.WF_WORK_ALARM_TRACER.getType()
                            : WorkFlowMessageTemplate.WF_WORK_DUE_TRACER.getType();
                case "其他人员":
                    return isAlarm ? WorkFlowMessageTemplate.WF_WORK_ALARM_OTHER.getType()
                            : WorkFlowMessageTemplate.WF_WORK_DUE_OTHER.getType();
                case "流程管理员":
                    return isAlarm ? WorkFlowMessageTemplate.WF_WORK_ALARM_ADMIN.getType()
                            : WorkFlowMessageTemplate.WF_WORK_DUE_ADMIN.getType();
                default:
                    return type;
            }
        }

        // 逾期流程移交给督办人员办理通知
        if (WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_SUPERVISE.getType().equals(type)) {
            switch (distributer.getDistributerTypeName()) {
                case "原办理人":
                    return WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_NOTIFY_OLD_DOING.getType();
                default:
                    return type;
            }
        }

        // 逾期流程移交给其他人员办理通知
        if (WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_OTHER.getType().equals(type)) {
            switch (distributer.getDistributerTypeName()) {
                case "原办理人":
                    return WorkFlowMessageTemplate.WF_WORK_DUE_TURN_OVER_OTHER_NOTIFY_OLD_DOING.getType();
                default:
                    return type;
            }
        }

        // 逾期流程自动退回上一环节通知
        if (WorkFlowMessageTemplate.WF_WORK_DUE_RETRUN_PREV_TASK.getType().equals(type)) {
            switch (distributer.getDistributerTypeName()) {
                case "原办理人":
                    return WorkFlowMessageTemplate.WF_WORK_DUE_RETRUN_PREV_TASK_NOTIFY_OLD_DOING.getType();
                default:
                    return type;
            }
        }

        // 逾期流程自动进入下一环节通知
        if (WorkFlowMessageTemplate.WF_WORK_DUE_ENTER_NEXT_TASK.getType().equals(type)) {
            switch (distributer.getDistributerTypeName()) {
                case "原办理人":
                    return WorkFlowMessageTemplate.WF_WORK_DUE_ENTER_NEXT_TASK_NOTIFY_OLD_DOING.getType();
                default:
                    return type;
            }
        }

        // 子流程重办通知
        if (WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REDO.getType().equals(type)) {
            switch (distributer.getDistributerTypeName()) {
                case "子流程全部已办人员":
                    return WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REDO_DONE.getType();
                default:
                    return type;
            }
        }

        // 子流程终止通知
        if (WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_STOP.getType().equals(type)) {
            switch (distributer.getDistributerTypeName()) {
                case "子流程全部已办人员":
                    return WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_STOP_DONE.getType();
                default:
                    return type;
            }
        }

        // 子流程催办通知
        if (WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND.getType().equals(type)) {
            switch (distributer.getDistributerTypeName()) {
                case "子流程办理人上级领导":
                    return WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_DOING_SUPERIOR.getType();
                case "子流程督办人员":
                    return WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_SUPERVISE.getType();
                case "子流程跟踪人员":
                    return WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_TRACER.getType();
                case "子流程流程管理人员":
                    return WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REMIND_ADMIN.getType();
                default:
                    return type;
            }
        }

        // 流程退回通知
        if (WorkFlowMessageTemplate.WF_WORK_ROLL_BACK.getType().equals(type)) {
            switch (distributer.getDistributerTypeName()) {
                case "全部已办人员":
                    return WorkFlowMessageTemplate.WF_WORK_ROLL_BACK_DONE.getType();
                default:
                    return type;
            }
        }

        return type;
    }

    /**
     * 获取自定义按钮信息
     *
     * @param fromTaskId
     * @param customButton
     * @return
     */
    public CustomDynamicButton convertTaskCustomDynamicButton(String taskId, CustomDynamicButton customButton) {
        TaskElement taskElement = this.flow.getTask(taskId);
        if (taskElement == null || customButton == null) {
            return customButton;
        }
        List<CustomDynamicButton> buttons = getTaskCustomDynamicButtons(taskElement);
        for (CustomDynamicButton button : buttons) {
            if (StringUtils.equals(button.getCode(), customButton.getCode())
                    && StringUtils.equals(button.getNewCode(), customButton.getNewCode())) {
                return button;
            }
        }
        return customButton;
    }

    /**
     * @param taskId
     * @return
     */
    public boolean isAllowReturnAfterRollback(String taskId) {
        if (!existsTaskNode(taskId)) {
            return false;
        }
        return flow.getTask(taskId).getIsAllowReturnAfterRollback();
    }

    public boolean isNotRollback(String taskId) {
        if (!existsTaskNode(taskId)) {
            return false;
        }
        return flow.getTask(taskId).getIsNotRollback();
    }

    /**
     * @param taskId
     * @return
     */
    public boolean isNotCancel(String taskId) {
        if (!existsTaskNode(taskId)) {
            return false;
        }
        return flow.getTask(taskId).getIsNotCancel();
    }

    /**
     * @param taskId
     * @return
     */
    public boolean isOnlyReturnAfterRollback(String taskId) {
        if (!existsTaskNode(taskId)) {
            return false;
        }
        return flow.getTask(taskId).getIsOnlyReturnAfterRollback();
    }

    /**
     * 判断是否XML的流程定义
     *
     * @return
     */
    public boolean isXmlDefinition() {
        return flow.isXmlDefinition();
    }

    /**
     * @param fromId
     * @return
     */
    public List<Direction> getAvailableGatewayToDirections(String fromId) {
        List<Direction> toDirections = Lists.newArrayList();
        List<Direction> directions = getDirections(fromId);
        if (CollectionUtils.isEmpty(directions)) {
            return toDirections;
        }

        List<String> checkedGatwayIds = Lists.newArrayList();
        directions.forEach(direction -> {
            GatewayElement gatewayElement = flow.getGateway(direction.getToID());
            if (gatewayElement != null) {
                addGatewayToDirection(gatewayElement, toDirections, checkedGatwayIds);
            }
        });
        return toDirections;
    }

    /**
     * @param gatewayElement
     * @param toDirections
     * @param checkedGatwayIds
     */
    private void addGatewayToDirection(GatewayElement gatewayElement, List<Direction> toDirections, List<String> checkedGatwayIds) {
        checkedGatwayIds.add(gatewayElement.getId());
        List<Direction> directions = getDirections(gatewayElement.getId());

        directions.forEach(direction -> {
            // 指向网关的流向类型为3
            if (DirectionType.ToGateway.getValue().equals(direction.getType())) {
                GatewayElement nextGatewayElement = flow.getGateway(direction.getToID());
                if (!checkedGatwayIds.contains(nextGatewayElement.getId())) {
                    addGatewayToDirection(nextGatewayElement, toDirections, checkedGatwayIds);
                }
            } else {
                toDirections.add(direction);
            }
        });
    }

    /**
     * @param taskId
     * @return
     */
    public boolean isScriptTaskNode(String taskId) {
        TaskElement taskElement = flow.getTask(taskId);
        return taskElement != null && TaskNodeType.ScriptTask.getValue().equals(taskElement.getType());
    }

    /**
     * @param taskId
     * @return
     */
    public boolean isCollaborationTaskNode(String taskId) {
        TaskElement taskElement = flow.getTask(taskId);
        return taskElement != null && TaskNodeType.CollaborationTask.getValue().equals(taskElement.getType());
    }

    /**
     * @param taskId
     * @return
     */
    public JobIdentity getJobIdentity(String taskId) {
        JobIdentity jobIdentity = new JobIdentity();
        TaskElement taskElement = flow.getTask(taskId);

        boolean isEnabledJobFlowType = taskElement != null && taskElement.isEnabledJobFlowType();
        if (isEnabledJobFlowType) {
            jobIdentity.setMultiJobFlowType(taskElement.getMultiJobFlowType());
            jobIdentity.setMainJobNotFoundFlowType(taskElement.getMainJobNotFoundFlowType());
            jobIdentity.setSelectJobMode(taskElement.getSelectJobMode());
            jobIdentity.setSelectJobField(taskElement.isSelectJobField());
            jobIdentity.setJobField(taskElement.getJobField());
            jobIdentity.setTaskScope(true);
        } else {
            PropertyElement property = flow.getProperty();
            jobIdentity.setMultiJobFlowType(property.getMultiJobFlowType());
            jobIdentity.setMainJobNotFoundFlowType(property.getMainJobNotFoundFlowType());
            jobIdentity.setSelectJobMode(property.getSelectJobMode());
            jobIdentity.setSelectJobField(property.isSelectJobField());
            jobIdentity.setJobField(property.getJobField());
            jobIdentity.setTaskScope(false);
        }
        return jobIdentity;
    }

}
