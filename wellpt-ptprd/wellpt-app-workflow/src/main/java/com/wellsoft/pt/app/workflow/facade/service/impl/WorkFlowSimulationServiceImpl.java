/*
 * @(#)2018年12月24日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.groovy.GroovyUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.context.util.reflection.ServiceInvokeUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.workflow.dto.FlowSimulationDataDto;
import com.wellsoft.pt.app.workflow.dto.FlowSimulationDataDto.SimulationOpinion;
import com.wellsoft.pt.app.workflow.dto.FlowSimulationDataDto.SimulationParams;
import com.wellsoft.pt.app.workflow.dto.FlowSimulationWorkData;
import com.wellsoft.pt.app.workflow.enums.EnumFlowSimulationState;
import com.wellsoft.pt.app.workflow.exception.FlowSimulationDataInteractionException;
import com.wellsoft.pt.app.workflow.facade.service.WorkFlowSimulationService;
import com.wellsoft.pt.app.workflow.support.FlowSimulationRecorder;
import com.wellsoft.pt.app.workflow.support.FlowSimulationRuntimeParams;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.element.UnitElement;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.TaskNodeType;
import com.wellsoft.pt.bpm.engine.enums.TransferCode;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceParameterService;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowDispatchService;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.dto.WfFlowSimulationRecordDto;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordEntity;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordItemEntity;
import com.wellsoft.pt.workflow.facade.service.WfFlowSimulationRecordFacadeService;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import com.wellsoft.pt.workflow.service.WfFlowSimulationRecordItemService;
import com.wellsoft.pt.workflow.service.WfFlowSimulationRecordService;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年12月24日.1	zhulh		2018年12月24日		Create
 * </pre>
 * @date 2018年12月24日
 */
@Service
public class WorkFlowSimulationServiceImpl extends WorkServiceImpl implements WorkFlowSimulationService {

    @Autowired
    private FlowService flowService;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    @Autowired
    private WfFlowSimulationRecordService flowSimulationRecordService;

    @Autowired
    private WfFlowSimulationRecordFacadeService flowSimulationRecordFacadeService;

    @Autowired
    private WfFlowSimulationRecordItemService flowSimulationRecordItemService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private TaskSubFlowDispatchService taskSubFlowDispatchService;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkFlowSimulationService#getSimulationData(java.lang.String)
     */
    @Override
    public FlowSimulationDataDto getSimulationData(String flowInstUuid) {
        SimulationTaskInfo simulationTaskInfo = getNextSimulationTaskInfo(flowInstUuid);

        FlowInstance flowInstance = flowService.getFlowInstance(simulationTaskInfo.getFlowInstUuid());

        String recordUuid = flowSimulationRecordService.getUuidByFlowInstUuid(flowInstUuid) + StringUtils.EMPTY;

        // 返回仿真数据
        FlowSimulationDataDto flowSimulationDataDto = new FlowSimulationDataDto();
        flowSimulationDataDto.setFlowDefUuid(flowInstance.getFlowDefinition().getUuid());
        flowSimulationDataDto.setFlowDefId(flowInstance.getId());
        flowSimulationDataDto.setFlowInstUuid(flowInstance.getUuid());
        flowSimulationDataDto.setFormUuid(flowInstance.getFormUuid());
        flowSimulationDataDto.setDataUuid(flowInstance.getDataUuid());
        flowSimulationDataDto.setTodoUserId(simulationTaskInfo.getTodoUserId());
        flowSimulationDataDto.setTodoUserName(simulationTaskInfo.getTodoUserName());
        flowSimulationDataDto.setSuperviseUserId(simulationTaskInfo.getSuperviseUserId());
        flowSimulationDataDto.setSuperviseUserName(simulationTaskInfo.getSuperviseUserName());
        flowSimulationDataDto.setTaskInstUuid(simulationTaskInfo.getTaskInstUuid());
        flowSimulationDataDto.setFromTaskId(simulationTaskInfo.getTaskId());
        flowSimulationDataDto.setTaskName(simulationTaskInfo.getTaskName());
        flowSimulationDataDto.setTaskId(simulationTaskInfo.getTaskId());
        flowSimulationDataDto.setTaskType(simulationTaskInfo.getTaskType());
        flowSimulationDataDto.setIsOver(flowInstance.getEndTime() != null);
        flowSimulationDataDto.setHandingStateInfo(getFlowHandingStateInfo(flowInstUuid));
        flowSimulationDataDto.setRecordUuid(recordUuid);
        if (flowInstance.getEndTime() != null) {
            flowSimulationDataDto.setState(EnumFlowSimulationState.Success.getValue());
        } else {
            WfFlowSimulationRecordEntity recordEntity = flowSimulationRecordService.getByFlowInstUuid(flowInstUuid);
            if (recordEntity != null && StringUtils.isNotBlank(recordEntity.getState())) {
                flowSimulationDataDto.setState(recordEntity.getState());
            }
        }
        // 子流程
        SimulationTaskInfo subTaskInfo = simulationTaskInfo.getSubTaskInfo();
        while (subTaskInfo != null && subTaskInfo.getSubTaskInfo() != null) {
            subTaskInfo = subTaskInfo.getSubTaskInfo();
        }
        flowSimulationDataDto.setSubTaskInfo(subTaskInfo);

        return flowSimulationDataDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkFlowSimulationService#simulationSubmit(com.wellsoft.pt.app.workflow.dto.FlowSimulationDataDto)
     */
    @Override
    @Transactional
    public ResultMessage simulationSubmit(FlowSimulationDataDto simulationData) {
        UserDetails currentUser = SpringSecurityUtils.getCurrentUser();
        // 获取办理人ID
        String todoUserId = getTodoUserId(simulationData);
        ResultMessage resultMessage = null;
        try {
            IgnoreLoginUtils.login(currentUser.getTenantId(), todoUserId);
            DefaultUserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            ServiceInvokeUtils.invoke("userDetailsServiceProvider.setOrgDetail", new Class[]{
                    DefaultUserDetails.class
            }, userDetails);
            SimulationContextHolder.setFlowSimulationData(simulationData);

            // 获取工作数据
            WorkBean workBean = getWorkBean(simulationData);

            // 提交操作
            workBean.setActionType(WorkFlowOperation.SUBMIT);
            workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.SUBMIT));

            // 提交流程
            resultMessage = this.submit(workBean);

            // 仿真记录
            saveSimulationRecord(workBean, (SubmitResult) resultMessage.getData(), simulationData, userDetails, currentUser);
        } catch (Exception e) {
            if (e instanceof JsonDataException) {
                throw (JsonDataException) e;
            }
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
            SimulationContextHolder.reset();
        }

        // 添加当前用户的抄送权限
        addCopyUser(resultMessage, currentUser, simulationData);

        return resultMessage;
    }

    /**
     * @param workBean
     * @param submitResult
     * @param simulationData
     * @param userDetails
     * @param currentUser
     */
    public void saveSimulationRecord(WorkBean workBean, SubmitResult submitResult, FlowSimulationDataDto simulationData, DefaultUserDetails userDetails, UserDetails currentUser) {
        String recordUuid = simulationData.getRecordUuid();
        if (StringUtils.isBlank(recordUuid)) {
            recordUuid = createSimulationRecord(workBean, submitResult, simulationData, userDetails, currentUser) + StringUtils.EMPTY;
        }

        Long srUuid = Long.valueOf(recordUuid);
        FlowSimulationRecorder recorder = SimulationContextHolder.getFlowSimulationRecorder();
        recorder.getItems().forEach(item -> item.setRecordUuid(Long.valueOf(srUuid)));
        flowSimulationRecordItemService.saveAll(recorder.getItems());

        // 判断流程是否进入死循环，若是则进行暂停
        List<Map<String, Object>> nextTasks = submitResult.getNextTasks();
        if (CollectionUtils.isNotEmpty(nextTasks)) {
            Long toUpdateRecordUuid = Long.valueOf(recordUuid);
            List<TaskActivity> taskActivities = taskService.getTaskActivities(submitResult.getFlowInstUuid());
            Map<String, TaskActivity> taskActivityMap = ConvertUtils.convertElementToMap(taskActivities, "taskInstUuid");
            nextTasks.forEach(nextTask -> {
                if (isCirculate(Objects.toString(nextTask.get("uuid")), taskActivityMap)) {
                    flowSimulationRecordService.updateStateByUuid(toUpdateRecordUuid, EnumFlowSimulationState.Pause.getValue());
                } else {
                    flowSimulationRecordService.updateStateByUuid(toUpdateRecordUuid, EnumFlowSimulationState.Running.getValue());
                }
            });
        } else {
            flowSimulationRecordService.updateStateByUuid(Long.valueOf(recordUuid), EnumFlowSimulationState.Running.getValue());
        }
    }

    /**
     * @param taskInstUuid
     * @param taskActivityMap
     * @return
     */
    private boolean isCirculate(String taskInstUuid, Map<String, TaskActivity> taskActivityMap) {
        boolean circulate = false;
        TaskActivity taskActivity = taskActivityMap.get(taskInstUuid);
        if (taskActivity == null) {
            return circulate;
        }

        String checkedTaskId = taskActivity.getTaskId();
        while (true) {
            taskActivity = taskActivityMap.get(taskActivity.getPreTaskInstUuid());
            if (taskActivity == null || !TransferCode.Submit.getCode().equals(taskActivity.getTransferCode())) {
                break;
            }
            if (StringUtils.equals(checkedTaskId, taskActivity.getTaskId())) {
                circulate = true;
                break;
            }
        }
        return circulate;
    }

    /**
     * @param workBean
     * @param submitResult
     * @param simulationData
     * @param startUser
     * @param operatorUser
     */
    public Long createSimulationRecord(WorkBean workBean, SubmitResult submitResult, FlowSimulationDataDto simulationData, DefaultUserDetails startUser, UserDetails operatorUser) {
        String formUuid = simulationData.getFormUuid();
        String dataUuid = simulationData.getDataUuid();
        WfFlowSimulationRecordEntity recordEntity = null;
        // 只有表单定义UUID、数据UUID的记录为新建的表单数据，用作当前记录
        if (StringUtils.isNotBlank(formUuid) && StringUtils.isNotBlank(dataUuid)) {
            recordEntity = flowSimulationRecordService.getByFormUuidAndDataUuid(formUuid, dataUuid);
            if (recordEntity != null && StringUtils.isNotBlank(recordEntity.getFlowInstUuid())) {
                recordEntity = null;
            }
        } else {
            formUuid = workBean.getFormUuid();
            dataUuid = workBean.getDataUuid();
        }
        if (recordEntity == null) {
            recordEntity = new WfFlowSimulationRecordEntity();
        }

        Map<String, Object> contentMap = Maps.newHashMap();
        contentMap.put("params", simulationData.getSimulationParams());
        contentMap.put("formUuid", formUuid);
        contentMap.put("dataUuid", dataUuid);
        recordEntity.setFlowInstUuid(submitResult.getFlowInstUuid());
        recordEntity.setFlowDefUuid(workBean.getFlowDefUuid());
        recordEntity.setFormUuid(formUuid);
        recordEntity.setDataUuid(dataUuid);
        recordEntity.setOperatorName(operatorUser.getUserName());
        recordEntity.setOperatorId(operatorUser.getUserId());
        recordEntity.setStartUserName(startUser.getUserName());
        recordEntity.setStartUserId(startUser.getUserId());
        recordEntity.setOperatorTime(Calendar.getInstance().getTime());
        recordEntity.setContentJson(JsonUtils.object2Json(contentMap));
        recordEntity.setState(EnumFlowSimulationState.Running.getValue());
        recordEntity.setDeleteStatus(0);
        recordEntity.setSystem(RequestSystemContextPathResolver.system());
        recordEntity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        flowSimulationRecordService.save(recordEntity);
        return recordEntity.getUuid();
    }

    /**
     * @param simulationData
     * @return
     */
    @Override
    @Transactional
    public ResultMessage simulationComplete(FlowSimulationDataDto simulationData) {
        UserDetails currentUser = SpringSecurityUtils.getCurrentUser();
        // 获取督办人ID
        String superviseUserId = simulationData.getSuperviseUserId();
        ResultMessage resultMessage = null;
        try {
            IgnoreLoginUtils.login(currentUser.getTenantId(), superviseUserId);
            DefaultUserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            ServiceInvokeUtils.invoke("userDetailsServiceProvider.setOrgDetail", new Class[]{
                    DefaultUserDetails.class
            }, userDetails);
            SimulationContextHolder.setFlowSimulationData(simulationData);

            // 获取工作数据
            WorkBean workBean = getWorkBean(simulationData);

            // 提交操作
            workBean.setActionType(WorkFlowOperation.COMPLETE);
            workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.COMPLETE));

            // 完成流程
            resultMessage = this.complete(workBean);

            // 仿真记录
            saveSimulationRecord(workBean, (SubmitResult) resultMessage.getData(), simulationData, userDetails, currentUser);
        } catch (Exception e) {
            if (e instanceof JsonDataException) {
                throw (JsonDataException) e;
            }
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
            SimulationContextHolder.reset();
        }

        // 添加当前用户的抄送权限
        addCopyUser(resultMessage, currentUser, simulationData);

        return resultMessage;
    }

    /**
     * @param simulationData
     */
    @Override
    @Transactional
    public void simulationTransfer(FlowSimulationDataDto simulationData) {
        UserDetails currentUser = SpringSecurityUtils.getCurrentUser();
        // 获取办理人ID
        String todoUserId = getTodoUserId(simulationData);
        try {
            IgnoreLoginUtils.login(currentUser.getTenantId(), todoUserId);
            DefaultUserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            ServiceInvokeUtils.invoke("userDetailsServiceProvider.setOrgDetail", new Class[]{
                    DefaultUserDetails.class
            }, userDetails);
            SimulationContextHolder.setFlowSimulationData(simulationData);

            // 获取工作数据
            WorkBean workBean = getWorkBean(simulationData);

            // 转办操作
            workBean.setActionType(WorkFlowOperation.TRANSFER);
            workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.TRANSFER));

            // 转办流程
            this.transferWithWorkData(workBean, Lists.newArrayList());
        } catch (Exception e) {
            if (e instanceof JsonDataException) {
                throw (JsonDataException) e;
            }
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
            SimulationContextHolder.reset();
        }
    }

    /**
     * @param simulationData
     */
    @Override
    @Transactional
    public void simulationCounterSign(FlowSimulationDataDto simulationData) {
        UserDetails currentUser = SpringSecurityUtils.getCurrentUser();
        // 获取办理人ID
        String todoUserId = getTodoUserId(simulationData);
        try {
            IgnoreLoginUtils.login(currentUser.getTenantId(), todoUserId);
            DefaultUserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            ServiceInvokeUtils.invoke("userDetailsServiceProvider.setOrgDetail", new Class[]{
                    DefaultUserDetails.class
            }, userDetails);
            SimulationContextHolder.setFlowSimulationData(simulationData);

            // 获取工作数据
            WorkBean workBean = getWorkBean(simulationData);

            // 会签操作
            workBean.setActionType(WorkFlowOperation.COUNTER_SIGN);
            workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.COUNTER_SIGN));

            // 会签流程
            this.counterSignWithWorkData(workBean, Lists.newArrayList());
        } catch (Exception e) {
            if (e instanceof JsonDataException) {
                throw (JsonDataException) e;
            }
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
            SimulationContextHolder.reset();
        }
    }

    /**
     * @param simulationData
     */
    @Override
    @Transactional
    public void simulationGotoTask(FlowSimulationDataDto simulationData) {
        UserDetails currentUser = SpringSecurityUtils.getCurrentUser();
        // 获取办理人ID
        String todoUserId = getAdminUserId(simulationData);
        try {
            IgnoreLoginUtils.login(currentUser.getTenantId(), todoUserId);
            DefaultUserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            ServiceInvokeUtils.invoke("userDetailsServiceProvider.setOrgDetail", new Class[]{
                    DefaultUserDetails.class
            }, userDetails);
            SimulationContextHolder.setFlowSimulationData(simulationData);

            // 获取工作数据
            WorkBean workBean = getWorkBean(simulationData);

            // 跳转操作
            workBean.setActionType(WorkFlowOperation.GOTO_TASK);
            workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.GOTO_TASK));

            // 跳转流程
            this.gotoTask(workBean);

            // 送结束时，更新仿真记录状态
            if (StringUtils.isNotBlank(simulationData.getRecordUuid())) {
                // 保存仿真记录项
                FlowSimulationRecorder recorder = SimulationContextHolder.getFlowSimulationRecorder();
                recorder.getItems().forEach(item -> item.setRecordUuid(Long.valueOf(simulationData.getRecordUuid())));
                flowSimulationRecordItemService.saveAll(recorder.getItems());
                boolean completed = flowService.isCompleted(workBean.getFlowInstUuid());
                if (completed) {
                    flowSimulationRecordService.updateStateByUuid(Long.valueOf(simulationData.getRecordUuid()), EnumFlowSimulationState.Success.getValue());
                }
            }
        } catch (Exception e) {
            if (e instanceof JsonDataException) {
                throw (JsonDataException) e;
            }
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
            SimulationContextHolder.reset();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl#createTaskData(com.wellsoft.pt.workflow.work.bean.WorkBean, com.wellsoft.pt.security.core.userdetails.UserDetails, java.lang.String, java.lang.String)
     */
    @Override
    protected TaskData createTaskData(WorkBean workBean, UserDetails user, String taskInstUuid, String dataUuid) {
        TaskData taskData = super.createTaskData(workBean, user, taskInstUuid, dataUuid);
        // 仿真参数
        SimulationParams simulationParams = SimulationContextHolder.getFlowSimulationData().getSimulationParams();
        taskData.setGenerateSerialNumber(simulationParams.isGenerateSerialNumber());
        taskData.setSendMsg(simulationParams.isSendMsg());
        taskData.setMsgSendWay(Message.TYPE_ON_LINE);
        taskData.setArchive(simulationParams.isArchive());
        // 动态的环节、流向监听器，拟稿环节不需要交互
        taskData.setCustomData(CustomRuntimeData.KEY_TASK_LISTENER, "flowSimulationTaskListener");
        taskData.setCustomData(CustomRuntimeData.KEY_DIRECTION_LISTENER, "flowSimulationDirectionListener");
        // 开始执行环节
        if (StringUtils.isBlank(taskInstUuid) && StringUtils.isNotBlank(simulationParams.getStartTaskId())) {
            taskData.put("startToNodeId", simulationParams.getStartTaskId());
        } else if (CollectionUtils.isNotEmpty(simulationParams.getEndTasks()) && simulationParams.getEndTasks().contains(workBean.getTaskId())) {
            // 结束执行环节
            taskData.setToTaskId(workBean.getTaskId(), FlowConstant.END_FLOW_ID);
        }

        // 仿真参数使用的办理人标记为不是用户交互指定的办理人
        FlowSimulationRuntimeParams runtimeParams = SimulationContextHolder.getFlowSimulationData().getRuntimeParams();
        Map<String, Set<FlowUserSid>> sidMap = taskData.getTaskUserSidsMap();
        if (MapUtils.isNotEmpty(sidMap)) {
            sidMap.keySet().forEach(taskId -> {
                if (BooleanUtils.isFalse(runtimeParams.getSpecifyTaskUser(taskId))) {
                    taskData.setSpecifyTaskUser(taskId, false);
                }
            });
        }
        return taskData;
    }

    /**
     * @param resultMessage
     * @param currentUser
     * @param simulationDataDto
     */
    private void addCopyUser(ResultMessage resultMessage, UserDetails currentUser, FlowSimulationDataDto simulationDataDto) {
        SubmitResult submitResult = (SubmitResult) resultMessage.getData();
        if (submitResult != null) {
            String flowInstUuid = submitResult.getFlowInstUuid();
            if (StringUtils.isBlank(flowInstUuid)) {
                flowInstUuid = simulationDataDto.getFlowInstUuid();
            }
            TaskInstance taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
            taskService.addUnreadPermission(currentUser.getUserId(), taskInstance.getUuid());
        }
    }

    /**
     * @param flowInstUuid
     * @return
     */
    private SimulationTaskInfo getNextSimulationTaskInfo(String flowInstUuid) {
        SimulationTaskInfo simulationTaskInfo = new SimulationTaskInfo();
        FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
        // 主流程
        if (flowService.isMainFlowInstance(flowInstUuid)) {
            List<FlowInstance> unfinishedSubFlowInstances = flowService.getUnfinishedSubFlowInstances(flowInstUuid);
            // 存在未完成子流程
            if (CollectionUtils.isNotEmpty(unfinishedSubFlowInstances)) {
                simulationTaskInfo = getSimulationTaskInfo(flowInstance);
                // 存在未完成子流程时取一个子流程信息
                SimulationTaskInfo subTaskInfo = getNextSimulationTaskInfo(unfinishedSubFlowInstances.get(0).getUuid());
                subTaskInfo.setFlowDefName(unfinishedSubFlowInstances.get(0).getName());
                List<TaskSubFlowDispatch> dispatches = taskSubFlowDispatchService.listByParentFlowInstUuid(flowInstUuid);
                subTaskInfo.setDispatchingCount(dispatches.stream().filter(dispach -> TaskSubFlowDispatch.STATUS_COMPLETED.equals(dispach.getCompletionState())).count());
                subTaskInfo.setTotalCount(CollectionUtils.size(dispatches));
                simulationTaskInfo.setSubTaskInfo(subTaskInfo);
            } else {
                // 继续处理主流程
                simulationTaskInfo = getSimulationTaskInfo(flowInstance);
            }
        } else {
            // 子流程
            // 子流程未完成
            if (flowInstance.getEndTime() == null) {
                simulationTaskInfo = getSimulationTaskInfo(flowInstance);
            } else if (flowInstance.getParent() != null) {
                // 子流程已完成，获取主流程仿真信息
                simulationTaskInfo = getNextSimulationTaskInfo(flowInstance.getParent().getUuid());
            } else {
                // 获取子流程完成的信息
                simulationTaskInfo = getSimulationTaskInfo(flowInstance);
            }
        }
        return simulationTaskInfo;
    }

    /**
     * 如何描述该方法
     *
     * @param flowInstance
     * @return
     */
    private SimulationTaskInfo getSimulationTaskInfo(FlowInstance flowInstance) {
        String flowInstUuid = flowInstance.getUuid();
        SimulationTaskInfo simulationTaskInfo = new SimulationTaskInfo();
        simulationTaskInfo.setFlowInstUuid(flowInstUuid);
        List<TaskInstance> unfinishedTasks = taskService.getUnfinishedTasks(flowInstUuid);
        for (TaskInstance unfinishedTask : unfinishedTasks) {
            String taskInstUuid = unfinishedTask.getUuid();
            List<String> todoUserIds = Lists.newArrayList(taskService.getTodoUserIds(taskInstUuid));
            if (todoUserIds.stream().filter(userId -> !IdPrefix.startsUser(userId)).findFirst().isPresent()) {
                String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(new Token(flowInstance, new TaskData()));
                Map<String, String> userMap = workflowOrgService.getUsersByIds(todoUserIds, orgVersionIds);
                todoUserIds = Lists.newArrayList(userMap.keySet());
                todoUserIds.removeAll(aclTaskService.listSidDoneMarkerUserId(taskInstUuid));
            }
            simulationTaskInfo.setTaskInstUuid(taskInstUuid);
            simulationTaskInfo.setTaskName(unfinishedTask.getName());
            simulationTaskInfo.setTaskId(unfinishedTask.getId());
            // 协作环节
            if (TaskNodeType.CollaborationTask.getValueAsInt().equals(unfinishedTask.getType())) {
                List<String> doneUserIds = taskService.getDoneUserIds(taskInstUuid);
                todoUserIds.removeAll(doneUserIds);
                if (CollectionUtils.isEmpty(todoUserIds)) {
                    // 环节决策人
                    String decisionMakerName = "decisionMakers_" + unfinishedTask.getId() + "_" + unfinishedTask.getUuid();
                    FlowInstanceParameter parameter = flowInstanceParameterService.getByFlowInstUuidAndName(flowInstUuid, decisionMakerName);
                    if (parameter != null && StringUtils.isNotBlank(parameter.getValue())) {
                        String superviseUserId = null;
                        if (StringUtils.startsWith(parameter.getValue(), "{")) {
                            Map<String, String> superviseIdMap = JsonUtils.json2Object(parameter.getValue(), Map.class);
                            superviseUserId = superviseIdMap.keySet().iterator().next();
                        } else {
                            List<String> superviseIds = Arrays.asList(StringUtils.split(parameter.getValue(), Separator.SEMICOLON.getValue()));
                            superviseUserId = superviseIds.get(0);
                        }
                        simulationTaskInfo.setTaskType(unfinishedTask.getType());
                        simulationTaskInfo.setSuperviseUserId(superviseUserId);
                        simulationTaskInfo.setSuperviseUserName(IdentityResolverStrategy.resolveAsName(superviseUserId));
                        break;
                    }
                }
            } else {
                if (CollectionUtils.isEmpty(todoUserIds)) {
                    todoUserIds = taskService.getTodoUserIds(taskInstUuid);
                }
            }
            if (CollectionUtils.isNotEmpty(todoUserIds)) {
                String todoUserId = todoUserIds.get(0);
                simulationTaskInfo.setTaskType(unfinishedTask.getType());
                simulationTaskInfo.setTodoUserId(todoUserId);
                simulationTaskInfo.setTodoUserName(IdentityResolverStrategy.resolveAsName(todoUserId));
                break;
            }
        }
        return simulationTaskInfo;
    }

    /**
     * @param simulationData
     * @return
     */
    private String getTodoUserId(FlowSimulationDataDto simulationData) {
        String todoUserId = simulationData.getTodoUserId();
        String flowInstUuid = simulationData.getFlowInstUuid();
        String startUserId = simulationData.getSimulationParams().getStartUserId();
        // 启动新仿真
        if (StringUtils.isBlank(flowInstUuid)) {
            int startUserIndex = simulationData.getStartUserIndex();
            if (StringUtils.isNotBlank(startUserId)) {
                // 多个发起人，依次发起仿真
                List<String> startUserIds = Arrays.asList(StringUtils.split(startUserId, Separator.SEMICOLON.getValue()));
                if (CollectionUtils.size(startUserIds) > startUserIndex) {
                    todoUserId = startUserIds.get(startUserIndex);
                } else {
                    todoUserId = startUserIds.get(0);
                }
            } else {
                todoUserId = SpringSecurityUtils.getCurrentUserId();
            }
        }
        return todoUserId;
    }

    /**
     * @param simulationData
     * @return
     */
    private String getAdminUserId(FlowSimulationDataDto simulationData) {
        String taskInstUuid = simulationData.getTaskInstUuid();
        List<AclTaskEntry> aclSids = aclTaskService.getSid(taskInstUuid, AclPermission.MONITOR);
        if (CollectionUtils.isNotEmpty(aclSids)) {
            return aclSids.get(0).getSid();
        }
        return SpringSecurityUtils.getCurrentUserId();
    }

    /**
     * @param simulationData
     * @return
     */
    private WorkBean getWorkBean(FlowSimulationDataDto simulationData) {
        // 生成流程数据workBean
        String flowDefUuid = simulationData.getFlowDefUuid();
        String flowDefId = simulationData.getFlowDefId();
        String flowInstUuid = simulationData.getFlowInstUuid();
        String taskInstUuid = simulationData.getTaskInstUuid();
        WorkBean workBean = null;
        if (StringUtils.isNotBlank(flowInstUuid)) {
            workBean = this.getTodo(taskInstUuid, flowInstUuid);
            workBean = this.getWorkData(workBean);
        } else {
            if (StringUtils.isNotBlank(flowDefUuid)) {
                workBean = this.newWork(flowDefUuid);
            } else {
                workBean = this.newWorkById(flowDefId);
            }
            // 添加流程仿真的流程参数
            workBean.addExtraParam("custom_rt_simulation", "true");
//            workBean.addExtraParam("custom_rt_simulation_allowedDeleteSimulationData",
//                    isAllowedDeleteSimulationData(simulationData.getSimulationParams()) + StringUtils.EMPTY);
            // 变更发起环节
            changeStartTaskIfRequired(workBean, simulationData);
            // 填充表单数据
            fillDyformData(workBean, simulationData);
        }

        // 复制交互式的参数
        addInteractionTaskData(simulationData, workBean);

        // 设置仿真办理人
        setTaskUsersIfRequired(workBean, simulationData);

        // 填充办理意见
        fillOpinionIfRequired(workBean, simulationData);

        // 是否生成流水号
        if (!simulationData.getSimulationParams().isGenerateSerialNumber()) {
            workBean.setSerialNoDefId(StringUtils.EMPTY);
        }

        return workBean;
    }

    /**
     * @param simulationData
     * @param workBean
     */
    private void addInteractionTaskData(FlowSimulationDataDto simulationData, WorkBean workBean) {
        BeanUtils.copyProperties(simulationData, workBean,
                new String[]{"flowDefUuid", "flowDefId", "taskInstUuid", "taskId", "taskName", "flowInstUuid", "formUuid", "dataUuid"});
        Map<String, List<String>> taskUsers = simulationData.getTaskUsers();
        Map<String, List<String>> taskCopyUsers = simulationData.getTaskCopyUsers();
        Map<String, List<String>> taskMonitors = simulationData.getTaskMonitors();
        Map<String, List<String>> taskDecisionMakers = simulationData.getTaskDecisionMakers();
        FlowSimulationRuntimeParams runtimeParams = simulationData.getRuntimeParams();
        if (MapUtils.isNotEmpty(taskUsers)) {
            taskUsers.keySet().forEach(taskId -> {
                if (StringUtils.isBlank(runtimeParams.getTaskUserNoFound(taskId))
                        && CollectionUtils.isEmpty(runtimeParams.getCandidateUserIds(taskId))) {
                    runtimeParams.setTaskUserNoFound(taskId, "modal");
                }
            });
        }
        if (MapUtils.isNotEmpty(taskCopyUsers)) {
            taskCopyUsers.keySet().forEach(taskId -> {
                if (StringUtils.isBlank(runtimeParams.getTaskCopyUserNoFound(taskId))) {
                    runtimeParams.setTaskCopyUserNoFound(taskId, "modal");
                }
            });
        }
        if (MapUtils.isNotEmpty(taskMonitors)) {
            taskMonitors.keySet().forEach(taskId -> {
                if (StringUtils.isBlank(runtimeParams.getTaskSuperviseUserNoFound(taskId))) {
                    runtimeParams.setTaskSuperviseUserNoFound(taskId, "modal");
                }
            });
        }
        if (MapUtils.isNotEmpty(taskDecisionMakers)) {
            taskDecisionMakers.keySet().forEach(taskId -> {
                if (StringUtils.isBlank(runtimeParams.getTaskDecisionMakerNoFound(taskId))) {
                    runtimeParams.setTaskDecisionMakerNoFound(taskId, "modal");
                }
            });
        }
    }

    /**
     * @param simulationParams
     * @return
     */
    private boolean isAllowedDeleteSimulationData(SimulationParams simulationParams) {
        if (simulationParams == null) {
            return true;
        }
//        if (simulationParams.isSendMsg() || simulationParams.isArchive()) {
//            return false;
//        }
        return true;
    }

    /**
     * @param workBean
     * @param simulationData
     */
    private void changeStartTaskIfRequired(WorkBean workBean, FlowSimulationDataDto simulationData) {
        String startTaskId = simulationData.getSimulationParams().getStartTaskId();
        if (StringUtils.isBlank(startTaskId)) {
            return;
        }

        workBean.setFromTaskId(startTaskId);
        workBean.setTaskId(startTaskId);
        workBean.setRecords(FlowDelegateUtils.getFlowDelegate(workBean.getFlowDefUuid()).getTaskForm(startTaskId).getRecords());
    }

    /**
     * @param workBean
     * @param simulationData
     */
    private void fillDyformData(WorkBean workBean, FlowSimulationDataDto simulationData) {
        String formUuid = simulationData.getFormUuid();
        String dataUuid = simulationData.getDataUuid();
        if (StringUtils.isNotBlank(formUuid) && StringUtils.isNotBlank(dataUuid)) {
            workBean.setFormUuid(formUuid);
            workBean.setDataUuid(dataUuid);
            workBean.setDyFormData(dyFormFacade.getDyFormData(formUuid, dataUuid));
        } else {
            formUuid = workBean.getFormUuid();
            DyFormData dyFormData = null;
            SimulationParams simulationParams = simulationData.getSimulationParams();
            String interactionMode = simulationParams.getInteractionMode();
            // 表单数据交互方式，requiredField按必填字段
            TaskForm taskForm = getTaskForm(workBean);
            if (SimulationParams.INTERACTION_MODE_REQUIREDFIELD.equals(interactionMode)) {
                // 必填字段检测
                dyFormData = createOrGetRecordFormData(formUuid, simulationParams);
                // 字段设值
                workBean.setDyFormData(dyFormData);
                setFieldValueIfRequired(workBean.getTaskId(), dyFormData, simulationData);
                Set<String> userInteractedTasks = simulationData.getUserInteractedTasks();
                if (hasRequireFieldIsEmpty(dyFormData, taskForm)
                        && (CollectionUtils.isEmpty(userInteractedTasks) || !userInteractedTasks.contains(workBean.getTaskId()))) {
                    throw new FlowSimulationDataInteractionException(workBean.getTaskName(), workBean.getTaskId(), "1", formUuid, null, dyFormData.getFormDatas(), taskForm);
                }
            } else {
                // task按环节
                // 发起环节包含表单数据交互
                if (simulationParams.getInteractionTasks().contains(workBean.getTaskId())) {
                    dyFormData = createOrGetRecordFormData(formUuid, simulationParams);
                    throw new FlowSimulationDataInteractionException(workBean.getTaskName(), workBean.getTaskId(), "1", formUuid, null, dyFormData.getFormDatas(), taskForm);
                }
            }

            if (dyFormData == null) {
                dyFormData = createOrGetRecordFormData(formUuid, simulationParams);
            }
            dyFormData.setFieldValue(IdEntity.CREATOR, SpringSecurityUtils.getCurrentUserId());
            dataUuid = dyFormFacade.saveFormData(dyFormData);
            workBean.setDataUuid(dataUuid);
            workBean.setDyFormData(dyFormFacade.getDyFormData(formUuid, dataUuid));
        }
        // 字段设值
        if (StringUtils.isBlank(workBean.getFlowInstUuid())) {
            setFieldValueIfRequired(workBean.getTaskId(), workBean.getDyFormData(), simulationData);
        }
    }

    /**
     * @param formUuid
     * @param simulationParams
     * @return
     */
    private DyFormData createOrGetRecordFormData(String formUuid, SimulationParams simulationParams) {
        DyFormData dyFormData = null;
        String formDataSource = simulationParams.getFormDataSource();
        String recordUuid = simulationParams.getRecordUuid();
        if (SimulationParams.FORM_DATA_SOURCE_RECORD.equals(formDataSource) && StringUtils.isNotBlank(recordUuid)) {
            WfFlowSimulationRecordEntity recordEntity = flowSimulationRecordService.getOne(Long.valueOf(recordUuid));
            if (recordEntity != null) {
                Map<String, Object> contentMap = JsonUtils.json2Object(recordEntity.getContentJson(), Map.class);
                String recordFormUuid = Objects.toString(contentMap.get("formUuid"), StringUtils.EMPTY);
                String recordDataUuid = Objects.toString(contentMap.get("dataUuid"), StringUtils.EMPTY);
                if (StringUtils.isNotBlank(recordFormUuid) && StringUtils.isNotBlank(recordDataUuid)) {
                    dyFormData = dyFormFacade.getDyFormData(recordFormUuid, recordDataUuid);
                    if (dyFormData != null) {
                        dyFormData = dyFormFacade.copyFormData(dyFormData);
                    }
                }
            }
        }

        if (dyFormData == null) {
            dyFormData = dyFormFacade.createDyformDataWithDefaultData(formUuid);
        }
        return dyFormData;
    }

    /**
     * @param workBean
     * @param simulationData
     */
    private void setTaskUsersIfRequired(WorkBean workBean, FlowSimulationDataDto simulationData) {
        List<FlowSimulationDataDto.SimulationTask> tasks = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(simulationData.getSimulationParams().getTasks())) {
            tasks.addAll(simulationData.getSimulationParams().getTasks());
        }

        // 补充流程定义中新增的环节
        Set<String> configTaskIds = tasks.stream().map(FlowSimulationDataDto.SimulationTask::getId).collect(Collectors.toSet());
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(workBean.getFlowDefUuid());
        flowDelegate.getAllTaskNodes().forEach(taskNode -> {
            if (!configTaskIds.contains(taskNode.getId())) {
                FlowSimulationDataDto.SimulationTask simulationTask = new FlowSimulationDataDto.SimulationTask();
                simulationTask.setName(taskNode.getName());
                simulationTask.setId(taskNode.getId());
                simulationTask.setType(taskNode.getType());
                tasks.add(simulationTask);
            }
        });

        Map<String, List<String>> taskUsers = workBean.getTaskUsers();
        Map<String, List<String>> taskDecisionMakers = workBean.getTaskDecisionMakers();

        FlowSimulationRuntimeParams runtimeParams = simulationData.getRuntimeParams();

        // 仿真环节配置的办理人 > 仿真流程配置项的办理人 > 流程定义环节配置的办理人
        String taskUserId = simulationData.getSimulationParams().getTaskUserId();
        tasks.forEach(task -> {
            // 第一个环节办理人为仿真启动人
            if (StringUtils.isBlank(workBean.getFlowInstUuid()) && StringUtils.equals(task.getId(), workBean.getTaskId())) {
                return;
            }
            String taskId = task.getId();
            String simulationTaskUserId = task.getSimulationTaskUserId();
            String simulationDecisionMakerId = task.getSimulationDecisionMakerId();
            if (CollectionUtils.isEmpty(taskUsers.get(taskId))) {
                if (StringUtils.isNotBlank(simulationTaskUserId)) {
                    taskUsers.put(taskId, Arrays.asList(StringUtils.split(simulationTaskUserId, Separator.SEMICOLON.getValue())));
                    runtimeParams.setSimulationTaskUserFlags(taskId, true);
                    runtimeParams.setSpecifyTaskUser(taskId, false);
                } else if (StringUtils.isNotBlank(taskUserId)) {
                    taskUsers.put(taskId, Arrays.asList(StringUtils.split(taskUserId, Separator.SEMICOLON.getValue())));
                    runtimeParams.setSimulationDefaultTaskUserFlags(taskId, true);
                    runtimeParams.setSpecifyTaskUser(taskId, false);
                }
            } else {
                runtimeParams.setSpecifyTaskUser(taskId, true);
            }
            if (StringUtils.isNotBlank(simulationDecisionMakerId)) {
                taskDecisionMakers.put(taskId, Arrays.asList(StringUtils.split(simulationDecisionMakerId, Separator.SEMICOLON.getValue())));
                runtimeParams.setSimulationDecisionMakerFlags(taskId, true);
            }
        });
    }

    /**
     * @param workBean
     * @param simulationData
     */
    private void fillOpinionIfRequired(WorkBean workBean, FlowSimulationDataDto simulationData) {
        // List<SimulationOpinion> opinions = simulationData.getSimulationParams().getOpinions();
        List<FlowSimulationDataDto.SimulationTask> tasks = simulationData.getSimulationParams().getTasks();
        if (CollectionUtils.isEmpty(tasks)) {
            return;
        }
        FlowSimulationDataDto.SimulationTask simulationTask = tasks.stream().filter(task -> StringUtils.equals(task.getId(), workBean.getTaskId())).findFirst().orElse(null);
        if (simulationTask == null) {
            return;
        }
        List<SimulationOpinion> opinions = simulationTask.getOpinions();
        if (CollectionUtils.isEmpty(opinions)) {
            return;
        }

        String taskId = workBean.getTaskId();
        String userId = SpringSecurityUtils.getCurrentUserId();
        SimulationOpinion opinion = getOpinion(taskId, userId, opinions);
        if (opinion != null) {
            if (StringUtils.isBlank(opinion.getOpinionText()) && StringUtils.isNotBlank(opinion.getOpinionLabel())) {
                workBean.setOpinionText(opinion.getOpinionLabel());
            } else {
                workBean.setOpinionText(opinion.getOpinionText());
            }
            workBean.setOpinionLabel(opinion.getOpinionLabel());
            workBean.setOpinionValue(opinion.getOpinionValue());
            workBean.setOpinionFiles(opinion.getOpinionFiles());
            List<LogicFileInfo> logicFileInfos = opinion.getOpinionFiles();
            if (CollectionUtils.isNotEmpty(logicFileInfos)) {
                // 确保仿真文件在夹中
                logicFileInfos.forEach(logicFileInfo -> {
                    if (!mongoFileService.isFileInFolder(logicFileInfo.getFileID())) {
                        mongoFileService.pushFileToFolder(simulationData.getFlowDefUuid(), logicFileInfo.getFileID(), "flow_simulation_opinion_file");
                    }
                });
            }
        }
    }

    /**
     * @param taskId
     * @param userId
     * @param opinions
     * @return
     */
    private SimulationOpinion getOpinion(String taskId, String userId, List<SimulationOpinion> opinions) {
        for (SimulationOpinion simulationOpinion : opinions) {
            if (StringUtils.contains(simulationOpinion.getTaskUserId(), userId)) {
                return simulationOpinion;
            }
        }
        return null;
    }

    /**
     * @param taskId
     * @param dyFormData
     * @param simulationData
     * @return
     */
    public boolean setFieldValueIfRequired(String taskId, DyFormData dyFormData, FlowSimulationDataDto simulationData) {
        List<FlowSimulationDataDto.SimulationTask> tasks = simulationData.getSimulationParams().getTasks();
        if (CollectionUtils.isEmpty(tasks)) {
            return false;
        }
        FlowSimulationDataDto.SimulationTask simulationTask = tasks.stream().filter(task -> StringUtils.equals(task.getId(), taskId)).findFirst().orElse(null);
        if (simulationTask == null) {
            return false;
        }
        List<FlowSimulationDataDto.SimulationFormField> formFields = simulationTask.getFormFields();
        if (CollectionUtils.isEmpty(formFields)) {
            return false;
        }

        List<String> updateFields = Lists.newArrayList();
        formFields.forEach(formField -> {
            String fieldName = formField.getName();
            if (!dyFormData.isFieldExist(fieldName)) {
                return;
            }
            if (evalateFormFieldCondition(formField.getConditionConfig(), formField, dyFormData, simulationData)) {
                Object value = formField.getValue();
                dyFormData.setFieldValue(fieldName, value);
                updateFields.add(fieldName);
                if (value instanceof List) {
                    // 确保仿真文件在夹中
                    List<Map<String, Object>> logicFileInfos = (List<Map<String, Object>>) value;
                    logicFileInfos.forEach(logicFileInfo -> {
                        String fileID = Objects.toString(logicFileInfo.get("fileID"));
                        if (StringUtils.isNotBlank(fileID) && !mongoFileService.isFileInFolder(fileID)) {
                            mongoFileService.pushFileToFolder(simulationData.getFlowDefUuid(), fileID, "flow_simulation_form_field_file");
                        }
                    });
                }
            }
        });
        return CollectionUtils.isNotEmpty(updateFields);
    }

    /**
     * @param conditionConfig
     * @param formField
     * @param dyFormData
     * @param simulationData
     * @return
     */
    private boolean evalateFormFieldCondition(FlowSimulationDataDto.SimulationFormFieldConditionConfig conditionConfig,
                                              FlowSimulationDataDto.SimulationFormField formField, DyFormData dyFormData, FlowSimulationDataDto simulationData) {
        List<FlowSimulationDataDto.SimulationFormFieldCondition> conditions = conditionConfig.getConditions();
        if (CollectionUtils.isEmpty(conditions)) {
            return true;
        }

        boolean evalateResult = false;
        boolean matchAll = FlowSimulationDataDto.SimulationFormFieldConditionConfig.MATCH_ALL.equals(conditionConfig.getMatch());
        if (matchAll) {
            evalateResult = true;
        }
        for (FlowSimulationDataDto.SimulationFormFieldCondition condition : conditions) {
            boolean result = evalateFormFieldCondition(condition, dyFormData, simulationData);
            if (matchAll) {
                if (!result) {
                    evalateResult = false;
                    break;
                }
            } else {
                if (result) {
                    evalateResult = true;
                    break;
                }
            }
        }

        return evalateResult;
    }

    /**
     * @param condition
     * @param dyFormData
     * @param simulationData
     * @return
     */
    private boolean evalateFormFieldCondition(FlowSimulationDataDto.SimulationFormFieldCondition condition, DyFormData dyFormData, FlowSimulationDataDto simulationData) {
        String code = condition.getCode();
        String operator = condition.getOperator();
        String value = condition.getValue();

        Map<String, Object> properties = Maps.newHashMap();
        if (dyFormData.getFormDataOfMainform() != null) {
            properties.putAll(dyFormData.getFormDataOfMainform());
        }
        List<String> fieldNames = dyFormData.doGetFieldNames();
        if (CollectionUtils.isNotEmpty(fieldNames)) {
            fieldNames.forEach(fieldName -> {
                if (!properties.containsKey(fieldName)) {
                    properties.put(fieldName, null);
                }
            });
        }

        Map<String, Object> other = Maps.newHashMap();
        other.put("currentRunNum", simulationData.getCurrentRunNum());
        properties.put("other", other);

        StringBuilder script = new StringBuilder("return ");
        switch (operator) {
            case "true":
                String trueValue = Objects.toString(properties.get(code), StringUtils.EMPTY);
                script.append(Boolean.TRUE.equals(trueValue) || StringUtils.isNotBlank(trueValue));
                break;
            case "false":
                String falseValue = Objects.toString(properties.get(code), StringUtils.EMPTY);
                script.append(Boolean.FALSE.equals(falseValue) || StringUtils.isBlank(falseValue));
                break;
            case "in":
                script.append(StringUtils.isNotBlank(value) && StringUtils.contains(value, Objects.toString(properties.get(code), StringUtils.EMPTY)));
                break;
            case "not in":
                script.append(StringUtils.isNotBlank(value) && !StringUtils.contains(value, Objects.toString(properties.get(code), StringUtils.EMPTY)));
                break;
            case "contain":
                String containValue = Objects.toString(properties.get(code), StringUtils.EMPTY);
                script.append(StringUtils.isNotBlank(containValue) && StringUtils.contains(containValue, value));
                break;
            case "not contain":
                String notContainValue = Objects.toString(properties.get(code), StringUtils.EMPTY);
                script.append(StringUtils.isNotBlank(notContainValue) && !StringUtils.contains(notContainValue, value));
                break;
            default:
                if (NumberUtils.isNumber(value)) {
                    if (NumberUtils.isNumber(Objects.toString(properties.get(code), StringUtils.EMPTY))) {
                        properties.put(code, Double.valueOf(properties.get(code).toString()));
                    }
                    if (StringUtils.equals("==", operator) &&
                            NumberUtils.isNumber(Objects.toString(properties.get(code), StringUtils.EMPTY))) {
                        script.append("Double.valueOf(" + value + ").equals(" + code + ")");
                    } else {
                        script.append(code).append(" ").append(operator).append(" ").append(value);
                    }
                } else {
                    if (StringUtils.equals("==", operator)) {
                        script.append("(\"" + value + "\").equals(" + code + ")");
                    } else if ((StringUtils.startsWith(value, "\"") && StringUtils.endsWith(value, "\""))) {
                        script.append(code).append(" ").append(operator).append(" ").append(value);
                    } else {
                        script.append(code).append(" ").append(operator).append(" \"").append(value).append("\"");
                    }
                }
                break;
        }
        Object result = GroovyUtils.run(script.toString(), properties);
        return Boolean.TRUE.equals(result);
    }

    /**
     * @param dyFormData
     * @param taskForm
     * @return
     */
    public boolean hasRequireFieldIsEmpty(DyFormData dyFormData, TaskForm taskForm) {
        String formUuid = dyFormData.getFormUuid();
        List<String> emptyValueFields = new ArrayList<String>();
        // 主表判断
        List<String> requireFields = Lists.newArrayList();
        Map<String, List<String>> notNullFieldMap = taskForm.getNotNullFieldMap();
        if (notNullFieldMap.containsKey(formUuid)) {
            requireFields.addAll(notNullFieldMap.get(formUuid));
        }
        for (String requireField : requireFields) {
            if (dyFormData.isFieldExist(requireField) && dyFormData.getFieldValue(requireField) == null
                    || StringUtils.isBlank(dyFormData.getFieldValue(requireField).toString())) {
                emptyValueFields.add(requireField);
                break;
            }
        }
        return CollectionUtils.isNotEmpty(emptyValueFields);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkFlowSimulationService#listTaskByFlowDefId(java.lang.String)
     */
    @Override
    public List<Map<String, Object>> listTaskByFlowDefId(String flowDefId) {
        List<Map<String, Object>> list = Lists.newArrayList();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowService.getFlowDefinitionById(flowDefId));
        List<Node> taskNodes = flowDelegate.getAllTaskNodes();
        for (Node node : taskNodes) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("text", node.getName());
            map.put("id", node.getId());
            // 意见立场
            TaskElement taskElement = flowDelegate.getFlow().getTask(node.getId());
            List<UnitElement> optNames = taskElement.getOptNames();
            if (CollectionUtils.isNotEmpty(optNames)) {
                List<Map<String, String>> opinions = new ArrayList<Map<String, String>>(0);
                for (UnitElement unitElement : optNames) {
                    Map<String, String> optionMap = new HashMap<String, String>();
                    optionMap.put("name", unitElement.getArgValue());
                    optionMap.put("value", unitElement.getValue());
                    opinions.add(optionMap);
                }
                map.put("opinions", opinions);
            }
            list.add(map);
        }
        return list;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkFlowSimulationService#fillTaskDyformDataControlInfo(com.wellsoft.pt.dyform.facade.dto.DyFormData, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void fillTaskDyformDataControlInfo(DyFormData dyFormData, String taskId, String flowInstUuid,
                                              String flowDefId) {
        FlowDelegate flowDelegate = null;
        if (StringUtils.isNotBlank(flowInstUuid)) {
            flowDelegate = FlowDelegateUtils
                    .getFlowDelegate(flowService.getFlowInstance(flowInstUuid).getFlowDefinition());
        } else {
            flowDelegate = FlowDelegateUtils.getFlowDelegate(flowService.getFlowDefinitionById(flowDefId));
        }
        Map<String, String> wrapperKeyMap = Maps.newHashMap();
        String defaultVFormUuid = dyFormFacade.getFormDefinition(dyFormData.getFormUuid()).doGetDefaultVFormUuid();
        if (StringUtils.isNotBlank(defaultVFormUuid)) {
            wrapperKeyMap.put(dyFormData.getFormUuid(), defaultVFormUuid);
        }
        TaskForm taskForm = flowDelegate.getTaskForm(taskId);
        WorkBean workBean = new WorkBean();
        String canEditForm = flowDelegate.getFlow().getTask(taskId).getCanEditForm();
        if (StringUtils.isNotBlank(canEditForm)) {
            workBean.setCanEditForm("1".equals(canEditForm));
        }
        if (workBean.getCanEditForm() != null) {
            super.setTaskDyformDataControlInfo(dyFormData, taskForm, workBean, wrapperKeyMap);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkFlowSimulationService#cleanSimulationData()
     */
    @Override
    @Transactional
    public synchronized void cleanSimulationData() {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        JSONObject flowSimulation = new JSONObject(workFlowSettings.getFlowSimulation());
        if (flowSimulation == null || !flowSimulation.has("params")) {
            return;
        }

        JSONObject jsonObject = flowSimulation.getJSONObject("params");
        int recordRetainDays = 3;
        if (jsonObject.has("recordRetainDays")) {
            recordRetainDays = jsonObject.getInt("recordRetainDays");
        }
        // 删除3天以前的数据
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, recordRetainDays >= 0 ? -recordRetainDays : recordRetainDays);
        String hql = "select t.uuid as uuid from WfFlowSimulationRecordEntity t where t.createTime < :deadlineTime ";
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            hql += "and t.system = :system";
        } else {
            hql += "and t.system is null";
        }
        Map<String, Object> values = Maps.newHashMap();
        values.put("deadlineTime", calendar.getTime());
        values.put("system", system);
        List<Long> recordUuids = flowSimulationRecordService.listUuidByHQL(hql, values);
        flowSimulationRecordService.deleteAllByUuids(recordUuids);
    }

    /**
     * @param recordUuid
     * @param taskInstUuid
     * @return
     */
    @Override
    public FlowSimulationWorkData getSimulationWorkDataByRecordUuidAndTaskInstUuid(Long recordUuid, String taskInstUuid) {
        WfFlowSimulationRecordDto recordDto = flowSimulationRecordFacadeService.getDto(recordUuid);
        String currentTaskInstUuid = taskInstUuid;
        if (StringUtils.isBlank(currentTaskInstUuid)) {
            currentTaskInstUuid = taskService.getLastTaskInstanceUuidByFlowInstUuid(recordDto.getFlowInstUuid());
        }
        String compareTaskInstUuid = currentTaskInstUuid;
        WfFlowSimulationRecordItemEntity itemEntity = recordDto.getItems().stream().filter(item -> StringUtils.equals(item.getTaskInstUuid(), compareTaskInstUuid)).findFirst().orElse(null);
        if (itemEntity == null) {
            if (CollectionUtils.isNotEmpty(recordDto.getItems())) {
                itemEntity = recordDto.getItems().get(recordDto.getItems().size() - 1);
            } else {
                itemEntity = new WfFlowSimulationRecordItemEntity();
                itemEntity.setDetails(JsonUtils.object2Json(Maps.newHashMapWithExpectedSize(0)));
            }
        }
        Map<String, Object> details = JsonUtils.json2Object(itemEntity.getDetails(), Map.class);
        List<String> taskUserIds = Arrays.asList(StringUtils.split(Objects.toString(details.get("taskUserIds"), StringUtils.EMPTY), Separator.SEMICOLON.getValue()));
        WorkBean workBean = null;
        try {
            if (CollectionUtils.isNotEmpty(taskUserIds)) {
                IgnoreLoginUtils.login(SpringSecurityUtils.getCurrentTenantId(), taskUserIds.get(0));
            }

            workBean = getTodo(currentTaskInstUuid, recordDto.getFlowInstUuid());
            // 快照数据
            if (details.get("formDatas") != null) {
                workBean.setLoadDyFormData(false);
                DyFormData dyFormData = dyFormFacade.createDyformData(Objects.toString(details.get("formUuid")));
                dyFormData.setFormDatas((Map<String, List<Map<String, Object>>>) details.get("formDatas"), false);
                workBean.setDyFormData(dyFormData);
            }
            workBean = getWorkData(workBean);
        } catch (Exception e) {
        } finally {
            IgnoreLoginUtils.logout();
        }

        FlowSimulationWorkData simulationWorkData = new FlowSimulationWorkData();
        simulationWorkData.setRecordUuid(recordUuid);
        simulationWorkData.setRecord(recordDto);
        simulationWorkData.setWorkData(workBean);
        return simulationWorkData;
    }

    @Override
    @Transactional
    public String simulationSaveFormData(DyFormData dyFormData) {
        if (StringUtils.isNotBlank(dyFormData.getDataUuid())) {
            try {
                boolean dataExists = dyFormFacade.queryFormDataExists(dyFormData.getTableName(), "uuid", dyFormData.getDataUuid());
                if (!dataExists && dyFormData.getAddedFormDatas() != null
                        && !dyFormData.getAddedFormDatas().containsKey(dyFormData.getDataUuid())) {
                    dyFormData.getAddedFormDatas().put(dyFormData.getFormUuid(), Lists.newArrayList(dyFormData.getDataUuid()));
                }
            } catch (Exception e) {
            }
        }
        dyFormData.doForceCover();
        String dataUuid = dyFormFacade.saveFormData(dyFormData);
        WfFlowSimulationRecordEntity entity = flowSimulationRecordService.getByFormUuidAndDataUuid(dyFormData.getFormUuid(), dataUuid);
        if (entity == null) {
            entity = new WfFlowSimulationRecordEntity();
            entity.setFormUuid(dyFormData.getFormUuid());
            entity.setDataUuid(dyFormData.getDataUuid());
            entity.setSystem(RequestSystemContextPathResolver.system());
            entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
            flowSimulationRecordService.save(entity);
        }
        return dataUuid;
    }

    /**
     * Description: 仿真上下文
     *
     * @author zhulh
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2021年1月29日.1	zhulh		2021年1月29日		Create
     * </pre>
     * @date 2021年1月29日
     */
    public static final class SimulationContextHolder {
        private static final ThreadLocal<FlowSimulationDataDto> flowSimulationDataHolder = new NamedThreadLocal<>(
                "simulation context");
        private static final ThreadLocal<FlowSimulationRecorder> flowSimulationRecorderHolder = new NamedThreadLocal<>(
                "simulation recorder");

        /**
         * @return
         */
        public static FlowSimulationRecorder getFlowSimulationRecorder() {
            return flowSimulationRecorderHolder.get();
        }

        /**
         * @return
         */
        public static FlowSimulationDataDto getFlowSimulationData() {
            return flowSimulationDataHolder.get();
        }

        /**
         * @param flowSimulationData
         */
        public static void setFlowSimulationData(FlowSimulationDataDto flowSimulationData) {
            flowSimulationRecorderHolder.set(new FlowSimulationRecorder());
            flowSimulationDataHolder.set(flowSimulationData);
        }

        /**
         *
         */
        public static void reset() {
            flowSimulationRecorderHolder.remove();
            flowSimulationDataHolder.remove();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class SimulationTaskInfo extends BaseObject {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -6465873476853751596L;

        // 流程定义名称
        private String flowDefName;
        // 流程实例UUID
        private String flowInstUuid;
        // 环节实例UUID
        private String taskInstUuid;
        // 环节名称
        private String taskName;
        // 环节ID
        private String taskId;
        // 环节类型
        private Integer taskType;
        // 办理人ID
        private String todoUserId;
        // 办理人名称
        private String todoUserName;
        // 督办人ID
        private String superviseUserId;
        // 督办人名称
        private String superviseUserName;
        // 子流程数量
        private long totalCount;
        // 子流程分发数量
        private long dispatchingCount;

        // 子流程信息
        private SimulationTaskInfo subTaskInfo;

        /**
         * @return the flowDefName
         */
        public String getFlowDefName() {
            return flowDefName;
        }

        /**
         * @param flowDefName 要设置的flowDefName
         */
        public void setFlowDefName(String flowDefName) {
            this.flowDefName = flowDefName;
        }

        /**
         * @return the flowInstUuid
         */
        public String getFlowInstUuid() {
            return flowInstUuid;
        }

        /**
         * @param flowInstUuid 要设置的flowInstUuid
         */
        public void setFlowInstUuid(String flowInstUuid) {
            this.flowInstUuid = flowInstUuid;
        }

        /**
         * @return the taskInstUuid
         */
        public String getTaskInstUuid() {
            return taskInstUuid;
        }

        /**
         * @param taskInstUuid 要设置的taskInstUuid
         */
        public void setTaskInstUuid(String taskInstUuid) {
            this.taskInstUuid = taskInstUuid;
        }

        /**
         * @return the taskName
         */
        public String getTaskName() {
            return taskName;
        }

        /**
         * @param taskName 要设置的taskName
         */
        public void setTaskName(String taskName) {
            this.taskName = taskName;
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
         * @return the taskType
         */
        public Integer getTaskType() {
            return taskType;
        }

        /**
         * @param taskType 要设置的taskType
         */
        public void setTaskType(Integer taskType) {
            this.taskType = taskType;
        }

        /**
         * @return the todoUserId
         */
        public String getTodoUserId() {
            return todoUserId;
        }

        /**
         * @param todoUserId 要设置的todoUserId
         */
        public void setTodoUserId(String todoUserId) {
            this.todoUserId = todoUserId;
        }

        /**
         * @return the todoUserName
         */
        public String getTodoUserName() {
            return todoUserName;
        }

        /**
         * @param todoUserName 要设置的todoUserName
         */
        public void setTodoUserName(String todoUserName) {
            this.todoUserName = todoUserName;
        }

        /**
         * @return the superviseUserId
         */
        public String getSuperviseUserId() {
            return superviseUserId;
        }

        /**
         * @param superviseUserId 要设置的superviseUserId
         */
        public void setSuperviseUserId(String superviseUserId) {
            this.superviseUserId = superviseUserId;
        }

        /**
         * @return the superviseUserName
         */
        public String getSuperviseUserName() {
            return superviseUserName;
        }

        /**
         * @param superviseUserName 要设置的superviseUserName
         */
        public void setSuperviseUserName(String superviseUserName) {
            this.superviseUserName = superviseUserName;
        }

        /**
         * @return the totalCount
         */
        public long getTotalCount() {
            return totalCount;
        }

        /**
         * @param totalCount 要设置的totalCount
         */
        public void setTotalCount(long totalCount) {
            this.totalCount = totalCount;
        }

        /**
         * @return the dispatchingCount
         */
        public long getDispatchingCount() {
            return dispatchingCount;
        }

        /**
         * @param dispatchingCount 要设置的dispatchingCount
         */
        public void setDispatchingCount(long dispatchingCount) {
            this.dispatchingCount = dispatchingCount;
        }

        /**
         * @return the subTaskInfo
         */
        public SimulationTaskInfo getSubTaskInfo() {
            return subTaskInfo;
        }

        /**
         * @param subTaskInfo 要设置的subTaskInfo
         */
        public void setSubTaskInfo(SimulationTaskInfo subTaskInfo) {
            this.subTaskInfo = subTaskInfo;
        }
    }

}
