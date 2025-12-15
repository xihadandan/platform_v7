/*
 * @(#)2014-11-1 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.business.dto.BusinessApplicationConfigDto;
import com.wellsoft.pt.basicdata.business.facade.service.BusinessFacadeService;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.exception.RequiredFieldIsBlankException;
import com.wellsoft.pt.bpm.engine.exception.RequiredFieldIsBlankException.RequiredFieldIsBlank;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.executor.RollBackTask;
import com.wellsoft.pt.bpm.engine.executor.RollbackTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.query.TaskActivityQueryItem;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.bpm.engine.util.TitleExpressionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.ListWorkService;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @date 2014-11-1
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-11-1.1	zhulh		2014-11-1		Create
 * </pre>
 */

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年11月14日.1	zhulh		2018年11月14日		Create
 * </pre>
 * @date 2018年11月14日
 */
@Service
@Transactional(readOnly = true)
public class ListWorkServiceImpl implements ListWorkService {

    @Autowired
    RollbackTaskActionExecutor rollbackTaskActionExecutor;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FlowService flowService;
    @Autowired
    private TaskInstanceService taskInstanceService;
    @Autowired
    private FlowInstanceService flowInstanceService;
    @Autowired
    private TaskBranchService taskBranchService;
    @Autowired
    private WorkService workService;
    @Autowired
    private TaskActivityService taskActivityService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private AclTaskService aclTaskService;
    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskSubFlowService taskSubFlowService;

    //    @Autowired
//    private OrgApiFacade orgApiFacade;
    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private BusinessFacadeService businessFacadeService;
    @Autowired
    private FlowDefinitionService flowDefinitionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isGzWorkData(java.util.Collection)
     */
    @Override
    public boolean isGzWorkData(Collection<String> taskInstUuids) {
        /*
         * String enableGz =
         * SystemParams.getValue(WfGzDataConstant.KEY_WF_GZ_ENABLE); if
         * (!WfGzDataConstant.TRUE.equalsIgnoreCase(enableGz)) { return false; }
         * for (String taskInstUuid : taskInstUuids) {
         * gzWorkDataSyncAspectService.checked(taskInstUuid); }
         */
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#getTodoWorkData(java.lang.String)
     */
    @Override
    public WorkBean getTodoWorkData(String taskInstUuid) {
        WorkBean workBean = workService.getTodo(taskInstUuid, null);
        workBean.setLoadDyFormData(false);
        workBean = workService.getWorkData(workBean);
        return workBean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isAllowedSubmit(java.util.Collection)
     */
    @Override
    public boolean isAllowedSubmit(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (!taskService.isAllowedSubmit(userId, taskInstUuid)) {
                return false;
            }
        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isRequiredSignOpinion(java.util.Collection)
     */
    @Override
    public boolean isRequiredSignOpinion(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (taskService.isRequiredSignOpinion(userId, taskInstUuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#checkSubmitTask(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public void checkSubmitTask(String taskInstUuid) {
        // 检查必输域是否有值
        TaskInstance taskInstance = taskService.getTask(taskInstUuid);
        String formUuid = taskInstance.getFormUuid();
        String dataUuid = taskInstance.getDataUuid();
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        List<RequiredFieldIsBlank> requiredFieldIsBlanks = new ArrayList<RequiredFieldIsBlankException.RequiredFieldIsBlank>();
        TaskForm taskForm = flowDelegate.getTaskForm(taskInstance.getId());
        Map<String, List<String>> notNullFieldMap = taskForm.getNotNullFieldMap();
        for (String key : notNullFieldMap.keySet()) {
            List<String> requiredFields = notNullFieldMap.get(key);
            // 主表字段
            if (formUuid.equals(key)) {
                for (String requiredField : requiredFields) {
                    if (dyFormData.isValueBlank(requiredField)) {
                        requiredFieldIsBlanks.add(new RequiredFieldIsBlank(requiredField, key, dataUuid));
                    }
                }
            } else {
                // 从表字段
                for (String requiredField : requiredFields) {
                    if (!TaskForm.isAllBtnField(key, requiredField)) {
                        List<Map<String, Object>> listMap = dyFormData.getFormDatas(key);
                        if (listMap != null && !listMap.isEmpty()) {
                            for (Map<String, Object> map : listMap) {
                                Object uuid = map.get(IdEntity.UUID);
                                Object fieldValue = map.get(requiredField);
                                if (uuid != null && (fieldValue == null || StringUtils.isBlank(fieldValue.toString()))) {
                                    requiredFieldIsBlanks.add(new RequiredFieldIsBlank(requiredField, key, uuid
                                            .toString()));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!requiredFieldIsBlanks.isEmpty()) {
            throw new RequiredFieldIsBlankException(requiredFieldIsBlanks);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#submit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void submit(String taskInstUuid, String opinionName, String opinionValue, String opinionText) {
        WorkBean workBean = workService.getTodo(taskInstUuid, null);
        workBean = workService.getWorkData(workBean);
        workBean.setOpinionLabel(opinionName);
        workBean.setOpinionValue(opinionValue);
        workBean.setOpinionText(opinionText);
        workService.submit(workBean);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isAllowedHandOver(java.util.Collection)
     */
    @Override
    public boolean isAllowedHandOver(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            TaskInstance taskInstance = taskService.getTask(taskInstUuid);
            // 工作办结检测
            if (taskInstance.getEndTime() != null) {
                List<AclTaskEntry> aclSids = aclTaskService.getSid(taskInstUuid, AclPermission.TODO);
                if (aclSids.isEmpty()) {
                    throw new WorkFlowException("工作已结束或已被处理，无法进行操作!");
                }
            }

            if (!taskService.isAllowedHandOver(userId, taskInstUuid)) {
                return false;
            }
        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isRequiredHandOverOpinion(java.util.Collection)
     */
    @Override
    public boolean isRequiredHandOverOpinion(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (taskService.isRequiredHandOverOpinion(userId, taskInstUuid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void handOver(Collection<String> taskInstUuids, List<String> rawHandOverUsers, String opinionName,
                         String opinionValue, String opinionText) {
        this.handOver(taskInstUuids, rawHandOverUsers, opinionName, opinionValue, opinionText, Collections.emptyList());
    }

    @Override
    @Transactional
    public void handOver(Collection<String> taskInstUuids, List<String> rawHandOverUsers, String opinionName, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        DyFormData dyFormData = null;
        TaskInstance taskInstance = null;
        FlowInstance flowInstance = null;
        FlowDefinition flowDefinition = null;
        for (String taskInstUuid : taskInstUuids) {
            taskInstance = taskInstanceService.get(taskInstUuid);
            flowDefinition = taskInstance.getFlowDefinition();
            if (BooleanUtils.isTrue(flowDefinition.getAutoUpdateTitle())) {
                flowInstance = taskInstance.getFlowInstance();
                dyFormData = dyFormFacade.getDyFormData(taskInstance.getFormUuid(), taskInstance.getDataUuid());
                String title = TitleExpressionUtils.generateFlowInstanceTitle(flowDefinition, taskInstance.getFlowInstance(), taskInstance.getFlowInstance().getStartUserId(),
                        dyFormData);
                flowInstance.setTitle(title);
                flowInstanceService.save(flowInstance);
            }
            taskService.handOver(userId, taskInstUuid, rawHandOverUsers, opinionName, opinionValue, opinionText, opinionFiles, true);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isAllowedGotoTask(java.util.Collection)
     */
    @Override
    public boolean isAllowedGotoTask(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
//			TaskInstance taskInstance = taskService.getTask(taskInstUuid);
            // 办结工作可跳转
            // 工作办结检测
//			if (taskInstance.getEndTime() != null) {
//				List<AclSid> aclSids = aclService.getSid(TaskInstance.class, taskInstUuid, AclPermission.TODO);
//				if (aclSids.isEmpty()) {
//					throw new WorkFlowException("工作已结束或已被处理，无法进行操作!");
//				}
//			}
            if (!taskService.isAllowedGotoTask(userId, taskInstUuid)) {
                return false;
            }
        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isRequiredGotoTaskOpinion(java.util.Collection)
     */
    @Override
    public boolean isRequiredGotoTaskOpinion(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (taskService.isRequiredGotoTaskOpinion(userId, taskInstUuid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void gotoTask(String taskInstUuid, String gotoTaskId, List<String> taskUsers) {
        doGotoTask(taskInstUuid, gotoTaskId, taskUsers, new TaskData());
    }

    /**
     * @param taskInstUuid
     * @param gotoTaskId
     * @param taskUsers
     * @param taskData
     */
    private void doGotoTask(String taskInstUuid, String gotoTaskId, List<String> taskUsers, TaskData taskData) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        taskData.setUserId(userId);
        taskData.setUserName(SpringSecurityUtils.getCurrentUserName());
        taskData.setCustomData("gotoTaskId", gotoTaskId);
        // 任务用户
        Map<String, List<String>> taskUsersMap = new HashMap<String, List<String>>();
        taskUsersMap.put(gotoTaskId, taskUsers);
        taskData.setTaskUsers(taskUsersMap);
        // 操作动作
        String key = taskInstUuid + userId;
        if (StringUtils.isBlank(taskData.getAction(key))) {
            taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.GOTO_TASK));
        }
        if (StringUtils.isBlank(taskData.getActionType(key))) {
            taskData.setActionType(key, WorkFlowOperation.GOTO_TASK);
        }
        taskService.gotoTask(taskInstUuid, gotoTaskId, taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#gotoTasks(java.util.Collection, java.lang.String, java.util.List)
     */
    @Override
    @Transactional
    public void gotoTasks(Collection<String> taskInstUuids, String gotoTaskId, List<String> taskUsers) {
        // 跳转
        for (String taskInstUuid : taskInstUuids) {
            gotoTask(taskInstUuid, gotoTaskId, taskUsers);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#checkGotoTasks(java.util.Collection)
     */
    @Override
    public void checkGotoTasks(Collection<String> taskInstUuids) {
        if (CollectionUtils.isEmpty(taskInstUuids)) {
            throw new WorkFlowException("请选择一条记录!");
        }
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<TaskInstance> taskInstances = taskInstanceService.listByUuids(Lists.newArrayList(taskInstUuids));
        // 检查
        TaskInstance checkTaskInstance = taskInstances.get(0);
        String checkFlowDefUuid = checkTaskInstance.getFlowDefinition().getUuid();
        String checkTaskId = checkTaskInstance.getId();
        for (TaskInstance taskInstance : taskInstances) {
            String taskInstUuid = taskInstance.getUuid();
            // 权限检查
            if (!taskService.isAllowedGotoTask(userId, taskInstUuid)) {
                throw new WorkFlowException("工作已结束或已被处理或没有权限，无法进行操作!");
            }

            // 办结工作可跳转
//			if (taskInstance.getEndTime() != null) {
//				List<AclSid> aclSids = aclService
//						.getSid(TaskInstance.class, taskInstance.getUuid(), AclPermission.TODO);
//				if (aclSids.isEmpty()) {
//					throw new WorkFlowException("工作已结束或已被处理或没有权限，无法进行操作!");
//				}
//			}
            if (checkTaskInstance.getUuid().equals(taskInstance.getUuid())) {
                continue;
            }

            String flowDefUuid = taskInstance.getFlowDefinition().getUuid();
            String taskId = taskInstance.getId();
            if (!StringUtils.equals(checkFlowDefUuid, flowDefUuid) || !StringUtils.equals(checkTaskId, taskId)) {
                throw new WorkFlowException("请选择相同的流程及环节!");
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#getNewFlows(java.lang.String, java.lang.String)
     */
    @Override
    public List<NewFlow> getNewFlows(String flowInstUuid, String taskId) {
        FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowInstance.getFlowDefinition());
        return ((SubTaskNode) flowDelegate.getTaskNode(taskId)).getNewFlows();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#getNewFlowLabelInfos(java.lang.String, java.lang.String)
     */
    @Override
    public List<NewFlowLabel> getNewFlowLabelInfos(String parentTaskInstUuid, String roleFlag) {
        TaskInstance taskInstance = taskInstanceService.get(parentTaskInstUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        List<NewFlow> newFlows = ((SubTaskNode) flowDelegate.getTaskNode(taskInstance.getId())).getNewFlows();

        List<NewFlowLabel> labels = Lists.newArrayList();
        for (NewFlow newFlow : newFlows) {
            String label = newFlow.getLabel();
            String id = newFlow.getId();
            boolean isLabel = true;
            boolean isMajor = newFlow.isMajor();
            // 过滤主办
            if ("1".equals(roleFlag) && !isMajor) {
                continue;
            } else if ("2".equals(roleFlag) && isMajor) {
                // 过滤协办
                continue;
            }
            if (StringUtils.isBlank(label)) {
                label = newFlow.getFlowName();
                id = newFlow.getId();
                isLabel = false;
            }
            NewFlowLabel newFlowLabel = new NewFlowLabel(label, Lists.<String>newArrayList(id), isLabel, isMajor);
            newFlowLabel.setGranularity(newFlow.getGranularity());
            if (!labels.contains(newFlowLabel)) {
                labels.add(newFlowLabel);
            } else {
                // 增加子流程ID
                NewFlowLabel existsNewFlowLabel = getExistsNewFlowLabel(labels, newFlowLabel);
                existsNewFlowLabel.getIds().addAll(newFlowLabel.getIds());
            }
        }
        Collections.sort(labels);
        return labels;
    }

    /**
     * @param labels
     * @param newFlowLabel
     * @return
     */
    private NewFlowLabel getExistsNewFlowLabel(List<NewFlowLabel> labels, NewFlowLabel newFlowLabel) {
        for (NewFlowLabel label : labels) {
            if (label.equals(newFlowLabel)) {
                return label;
            }
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#startBranchTask(java.lang.String, java.lang.String, boolean, java.util.List, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public List<String> startBranchTask(String belongToTaskInstUuid, String belongToFlowInstUuid, boolean isMajor,
                                        List<String> taskUsers, String businessType, String businessRole, String actionName) {
        List<String> branchTaskUuids = taskService.startBranchTask(belongToTaskInstUuid, belongToFlowInstUuid, isMajor,
                taskUsers, businessType, businessRole, actionName);
        return branchTaskUuids;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#startSubFlow(java.lang.String, java.lang.String, java.lang.String, boolean, java.util.List, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public List<String> startSubFlow(String parentTaskInstUuid, String parentFlowInstUuid, String newFlowId,
                                     boolean isMajor, List<String> taskUsers, String businessType, String businessRole, String actionName) {
        List<String> subFlowInstUuids = taskService.startSubFlow(parentTaskInstUuid, parentFlowInstUuid, newFlowId,
                isMajor, taskUsers, businessType, businessRole, actionName);
        return subFlowInstUuids;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#resendSubFlow(java.lang.String)
     */
    @Override
    @Transactional
    public void resendSubFlow(String parentTaskInstUuid) {
        taskService.resendSubFlow(parentTaskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#redoBranchTask(java.util.Collection, java.lang.String)
     */
    @Override
    @Transactional
    public void redoBranchTask(Collection<String> taskInstUuids, String opinionText) {
        for (String taskInstUuid : taskInstUuids) {
            TaskInstance taskInstance = taskService.get(taskInstUuid);
            boolean isBranchTaskFinished = taskBranchService.isBranchTaskCompleted(taskInstance);
            if (isBranchTaskFinished) {
                throw new RuntimeException("流程分支已全部办结，不能进行重办！");
            }

            TaskBranch taskBranch = taskBranchService.getByCurrentTaskInstUuid(taskInstUuid);
            String initTaskInstUuid = taskBranch.getInitTaskInstUuid();
            TaskInstance initTaskInstance = taskService.get(initTaskInstUuid);
            String gotoTaskId = initTaskInstance.getId();

            List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuid(initTaskInstUuid);
            List<String> gotoUserIds = new ArrayList<String>(0);
            for (TaskIdentity taskIdentity : taskIdentities) {
                gotoUserIds.add(taskIdentity.getUserId());
            }
            UserDetails user = SpringSecurityUtils.getCurrentUser();
            TaskData taskData = new TaskData();
            taskData.setUserId(user.getUserId());
            taskData.setUserName(user.getUserName());
            taskData.setPreTaskProperties(gotoTaskId, FlowConstant.BRANCH.IS_PARALLEL, taskInstance.getIsParallel());
            taskData.setPreTaskProperties(gotoTaskId, FlowConstant.BRANCH.PARALLEL_TASK_INST_UUID,
                    taskInstance.getParallelTaskInstUuid());
            taskData.setRelatedTaskBranchUuid(gotoTaskId, taskBranch.getUuid());
            // 操作动作
            String key = taskInstUuid + user.getUserId();
            taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.REDO));
            taskData.setActionType(key, WorkFlowOperation.REDO);
            taskData.setActionCode(taskInstUuid, ActionCode.REDO.getCode());

            // 允许跳转已办结的流程
            taskData.put("allowGotoOverTask", true);
            taskData.put("isLog", false);
            doGotoTask(taskInstUuid, gotoTaskId, gotoUserIds, taskData);

            // 添加环节操作日志
            taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.REDO),
                    ActionCode.REDO.getCode(), WorkFlowOperation.REDO, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.isNotBlank(opinionText) ? opinionText : "重办",
                    user.getUserId(), gotoUserIds, null, null, null, taskInstance, taskInstance.getFlowInstance(),
                    taskData);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#redoFlow(java.util.Collection, java.lang.String)
     */
    @Override
    @Transactional
    public void redoFlow(Collection<String> taskInstUuids, String opinionText) {
        for (String taskInstUuid : taskInstUuids) {
            List<TaskActivityQueryItem> activityQueryItems = taskActivityService
                    .getAllActivityByTaskInstUuid(taskInstUuid);
            // 按创建时间升序排序
            Collections.sort(activityQueryItems, new Comparator<TaskActivityQueryItem>() {

                @Override
                public int compare(TaskActivityQueryItem o1, TaskActivityQueryItem o2) {
                    return o1.getCreateTime().compareTo(o2.getCreateTime());
                }

            });
            String gotoTaskId = activityQueryItems.get(0).getTaskId();
            TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);

            // 子流程重办通知——子流程办理人
            List<MessageTemplate> todoMessageTemplates = null;
            List<String> messageTodoUserIds = null;
            // 子流程重办通知——子流程全部已办人员
            List<MessageTemplate> doneMessageTemplates = null;
            List<String> messageDoneUserIds = null;
            // 子流程获取流程定义的子流程配置信息
            TaskInstance parentTaskInstance = taskInstance.getParent();
            if (parentTaskInstance != null) {
                FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(parentTaskInstance.getFlowDefinition());
                SubTaskNode subTaskNode = (SubTaskNode) flowDelegate.getTaskNode(parentTaskInstance.getId());
                NewFlow newFlow = getNewFlow(subTaskNode, taskInstance.getFlowInstance());
                if (newFlow != null) {
                    String newFlowToTaskId = newFlow.getToTaskId();
                    // 自动提交
                    if (StringUtils.equals(SubTaskNode.AUTO_SUBMIT, newFlowToTaskId) && activityQueryItems.size() > 1) {
                        gotoTaskId = activityQueryItems.get(1).getTaskId();
                    } else if (StringUtils.equals(SubTaskNode.DRAFT, newFlowToTaskId)) {
                        // 保存为草稿
                    } else {
                        gotoTaskId = newFlowToTaskId;
                    }
                }
                todoMessageTemplates = flowDelegate.getMessageTemplateMap().get(
                        WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REDO.getType());
                if (CollectionUtils.isNotEmpty(todoMessageTemplates)
                        && StringUtils.isNotBlank(taskInstance.getTodoUserId())) {
                    messageTodoUserIds = Arrays.asList(StringUtils.split(taskInstance.getTodoUserId(),
                            Separator.SEMICOLON.getValue()));
                }
                doneMessageTemplates = flowDelegate.getMessageTemplateMap().get(
                        WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REDO_DONE.getType());
                if (CollectionUtils.isNotEmpty(doneMessageTemplates)) {
                    messageDoneUserIds = taskService.getDoneUserIds(taskInstUuid);
                }
            }

            // 二级子流程
            List<TaskInstance> subNormalTaskInstanceList = taskInstanceService
                    .getNormalByParentTaskInstUuid(taskInstUuid);
            if (CollectionUtils.isNotEmpty(subNormalTaskInstanceList)) {
                if (CollectionUtils.isNotEmpty(todoMessageTemplates)
                        || CollectionUtils.isNotEmpty(doneMessageTemplates)) {
                    for (TaskInstance subNormalTaskInstance : subNormalTaskInstanceList) {
                        List<String> messageSubTodoUserIds = taskService
                                .getTodoUserIds(subNormalTaskInstance.getUuid());
                        List<String> messageSubDoneUserIds = taskService
                                .getDoneUserIds(subNormalTaskInstance.getUuid());
                        // 发送消息
                        if (CollectionUtils.isNotEmpty(messageSubTodoUserIds)) {
                            TaskData msgTaskData = createMsgTaskData(subNormalTaskInstance, parentTaskInstance);
                            MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REDO,
                                    todoMessageTemplates, parentTaskInstance, parentTaskInstance.getFlowInstance(),
                                    messageSubTodoUserIds, ParticipantType.TodoUser);
                        }
                        if (CollectionUtils.isNotEmpty(messageSubDoneUserIds)) {
                            TaskData msgTaskData = createMsgTaskData(subNormalTaskInstance, parentTaskInstance);
                            MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REDO_DONE,
                                    doneMessageTemplates, parentTaskInstance, parentTaskInstance.getFlowInstance(),
                                    messageSubDoneUserIds, ParticipantType.TodoUser);
                        }
                    }
                }
                // 该任务为子流程任务 -> 先终止二次子流程任务
                taskService.stopByParentFlowInstUuid(taskInstance.getFlowInstance().getUuid(),
                        WorkFlowOperation.getName(WorkFlowOperation.STOP), WorkFlowOperation.STOP, StringUtils.EMPTY,
                        StringUtils.EMPTY, "终止", Collections.emptyList(), true);
            }

            List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuid(activityQueryItems.get(0)
                    .getTaskInstUuid());
            List<String> gotoUserIds = new ArrayList<String>(0);
            for (TaskIdentity taskIdentity : taskIdentities) {
                gotoUserIds.add(taskIdentity.getUserId());
            }
            UserDetails user = SpringSecurityUtils.getCurrentUser();
            TaskData taskData = new TaskData();
            taskData.setUserId(user.getUserId());
            taskData.setUserName(user.getUserName());
            // 操作动作
            String key = taskInstUuid + user.getUserId();
            taskData.setAction(key, WorkFlowOperation.getName(WorkFlowOperation.REDO));
            taskData.setActionType(key, WorkFlowOperation.REDO);
            taskData.setActionCode(taskInstUuid, ActionCode.REDO.getCode());

            // 允许跳转已办结的流程
            taskData.put("allowGotoOverTask", true);
            taskData.put("isLog", false);
            doGotoTask(taskInstUuid, gotoTaskId, gotoUserIds, taskData);

            // 恢复流程实例
            FlowInstance flowInstance = flowService.getFlowInstanceByTaskInstUuid(taskInstUuid);
            flowInstance.setEndTime(null);
            flowInstance.setIsActive(true);
            flowService.saveFlowInstance(flowInstance);

            // 恢复子流程状态
            List<TaskSubFlow> alltaskSubFlows = taskSubFlowService.getByFlowInstUuid(flowInstance.getUuid());
            for (TaskSubFlow taskSubFlow : alltaskSubFlows) {
                taskSubFlow.setCompleted(false);
                taskSubFlow.setCompletionState(TaskSubFlow.STATUS_NORMAL);
                taskSubFlowService.save(taskSubFlow);
            }

            // 发送消息
            if (CollectionUtils.isNotEmpty(messageTodoUserIds)) {
                TaskData msgTaskData = createMsgTaskData(taskInstance, parentTaskInstance);
                MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REDO,
                        todoMessageTemplates, parentTaskInstance, parentTaskInstance.getFlowInstance(),
                        messageTodoUserIds, ParticipantType.TodoUser);
            }
            if (CollectionUtils.isNotEmpty(messageDoneUserIds)) {
                TaskData msgTaskData = createMsgTaskData(taskInstance, parentTaskInstance);
                MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_REDO_DONE,
                        doneMessageTemplates, parentTaskInstance, parentTaskInstance.getFlowInstance(),
                        messageDoneUserIds, ParticipantType.TodoUser);
            }

            // 添加环节操作日志
            taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.REDO),
                    ActionCode.REDO.getCode(), WorkFlowOperation.REDO, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.isNotBlank(opinionText) ? opinionText : "重办",
                    user.getUserId(), gotoUserIds, null, null, null, taskInstance, taskInstance.getFlowInstance(),
                    taskData);
        }
    }

    /**
     * @param subTaskNode
     * @param flowInstance
     * @return
     */
    private NewFlow getNewFlow(SubTaskNode subTaskNode, FlowInstance flowInstance) {
        List<NewFlow> newFlows = subTaskNode.getNewFlows();
        for (NewFlow newFlow : newFlows) {
            if (StringUtils.equals(newFlow.getFlowId(), flowInstance.getId())) {
                return newFlow;
            }
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#stopBranchTask(java.util.Collection, java.lang.String, com.wellsoft.pt.bpm.engine.support.InteractionTaskData)
     */
    @Override
    @Transactional
    public void stopBranchTask(Collection<String> taskInstUuids, String opinionText, InteractionTaskData interactionTaskData) {
        for (String taskInstUuid : taskInstUuids) {
            List<String> todoUserIds = taskService.getTodoUserIds(taskInstUuid);
            if (CollectionUtils.isEmpty(todoUserIds)) {
                throw new RuntimeException("流程分支已办结，不能终止！");
            }
            taskService.stopBranchTask(taskInstUuid, WorkFlowOperation.getName(WorkFlowOperation.STOP),
                    WorkFlowOperation.STOP, todoUserIds, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.isNotBlank(opinionText) ? opinionText : "终止",
                    interactionTaskData);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#stopFlow(java.util.Collection, java.lang.String, com.wellsoft.pt.bpm.engine.support.InteractionTaskData)
     */
    @Override
    @Transactional
    public void stopFlow(Collection<String> taskInstUuids, String opinionText, InteractionTaskData interactionTaskData) {
        for (String taskInstUuid : taskInstUuids) {
            // 二次分发子流程
            List<TaskInstance> subNormalTaskInstanceList = taskInstanceService
                    .getNormalByParentTaskInstUuid(taskInstUuid);
            List<String> todoUserIds = taskService.getTodoUserIds(taskInstUuid);
            String flowInstUuid = taskService.getFlowInstUUidByTaskInstUuid(taskInstUuid);
            // 办理人为空且不是子流程环节
            if (CollectionUtils.isEmpty(todoUserIds) && CollectionUtils.isEmpty(subNormalTaskInstanceList)) {
                throw new RuntimeException("流程已办结，不能终止！");
            }

            TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
            TaskInstance parentTaskInstance = taskInstance.getParent();
            // 子流程终止通知——子流程办理人
            List<MessageTemplate> todoMessageTemplates = null;
            List<String> messageTodoUserIds = null;
            // 子流程终止通知——子流程全部已办人员
            List<MessageTemplate> doneMessageTemplates = null;
            List<String> messageDoneUserIds = null;
            if (parentTaskInstance != null) {
                FlowDelegate parentFlowDelegate = FlowDelegateUtils.getFlowDelegate(parentTaskInstance
                        .getFlowDefinition());
                todoMessageTemplates = parentFlowDelegate.getMessageTemplateMap().get(
                        WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_STOP.getType());
                doneMessageTemplates = parentFlowDelegate.getMessageTemplateMap().get(
                        WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_STOP_DONE.getType());
                messageTodoUserIds = todoUserIds;
                if (CollectionUtils.isNotEmpty(doneMessageTemplates)) {
                    messageDoneUserIds = taskService.getDoneUserIds(taskInstUuid);
                }
            }

            // 二次分发子流程终止及发送消息
            if (CollectionUtils.isNotEmpty(subNormalTaskInstanceList)) {
                // 二次分发子流程发送消息
                if (CollectionUtils.isNotEmpty(todoMessageTemplates)
                        || CollectionUtils.isNotEmpty(doneMessageTemplates)) {
                    for (TaskInstance subNormalTaskInstance : subNormalTaskInstanceList) {
                        List<String> messageSubTodoUserIds = taskService
                                .getTodoUserIds(subNormalTaskInstance.getUuid());
                        List<String> messageSubDoneUserIds = taskService
                                .getDoneUserIds(subNormalTaskInstance.getUuid());
                        // 发送消息
                        if (CollectionUtils.isNotEmpty(messageSubTodoUserIds)) {
                            TaskData msgTaskData = createMsgTaskData(subNormalTaskInstance, parentTaskInstance);
                            MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_STOP,
                                    todoMessageTemplates, parentTaskInstance, parentTaskInstance.getFlowInstance(),
                                    messageSubTodoUserIds, ParticipantType.TodoUser);
                        }
                        if (CollectionUtils.isNotEmpty(messageSubDoneUserIds)) {
                            TaskData msgTaskData = createMsgTaskData(subNormalTaskInstance, parentTaskInstance);
                            MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_STOP_DONE,
                                    doneMessageTemplates, parentTaskInstance, parentTaskInstance.getFlowInstance(),
                                    messageSubDoneUserIds, ParticipantType.TodoUser);
                        }
                    }
                }
                // 该任务为子流程任务 -> 先终止二次子流程任务
                taskService.stopByParentFlowInstUuid(flowInstUuid, WorkFlowOperation.getName(WorkFlowOperation.STOP),
                        WorkFlowOperation.STOP, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.isNotBlank(opinionText) ? opinionText : "终止", Collections.emptyList(), true);
            }

            // 终止流程
            taskService.stopFlow(flowInstUuid, WorkFlowOperation.getName(WorkFlowOperation.STOP),
                    WorkFlowOperation.STOP, todoUserIds, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.isNotBlank(opinionText) ? opinionText : "终止",
                    interactionTaskData);

            // 发送消息
            if (CollectionUtils.isNotEmpty(messageTodoUserIds)) {
                TaskData msgTaskData = createMsgTaskData(taskInstance, parentTaskInstance);
                MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_STOP,
                        todoMessageTemplates, parentTaskInstance, parentTaskInstance.getFlowInstance(),
                        messageTodoUserIds, ParticipantType.TodoUser);
            }
            if (CollectionUtils.isNotEmpty(messageDoneUserIds)) {
                TaskData msgTaskData = createMsgTaskData(taskInstance, parentTaskInstance);
                MessageClientUtils.send(msgTaskData, WorkFlowMessageTemplate.WF_WORK_SUB_FLOW_STOP_DONE,
                        doneMessageTemplates, parentTaskInstance, parentTaskInstance.getFlowInstance(),
                        messageDoneUserIds, ParticipantType.TodoUser);
            }
            // 添加环节操作日志
            //			TaskInstance taskInstance = taskService.get(taskInstUuid);
            //			UserDetails user = SpringSecurityUtils.getCurrentUser();
            //			TaskData taskData = new TaskData();
            //			taskData.setUserId(user.getUserId());
            //			taskData.setUserName(user.getUserName());
            //			taskOperationService.saveTaskOperation(WorkFlowOperation.getName(WorkFlowOperation.STOP),
            //					ActionCode.STOP.getCode(), WorkFlowOperation.STOP, StringUtils.EMPTY, StringUtils.EMPTY, "终止",
            //					user.getUserId(), todoUserIds, null, null, null, taskInstance, taskInstance.getFlowInstance(),
            //					taskData);
        }
    }

    /**
     * @param taskInstance
     * @return
     */
    private TaskData createMsgTaskData(TaskInstance taskInstance, TaskInstance parentTaskInstance) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        TaskData msgTaskData = new TaskData();
        msgTaskData.setUserId(userDetails.getUserId());
        msgTaskData.setUserName(userDetails.getUserName());
        Token token = new Token(parentTaskInstance, msgTaskData);
        msgTaskData.setToken(token);
        msgTaskData.setCustomData("流程实例名称", taskInstance.getFlowInstance().getTitle());
        return msgTaskData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#distributeInfo(java.util.Collection, java.lang.String, java.util.Collection)
     */
    @Override
    @Transactional
    public void distributeInfo(Collection<String> taskInstUuids, String content, List<String> fileIds) {
        for (String taskInstUuid : taskInstUuids) {
            taskService.distributeInfo(taskInstUuid, content, fileIds);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#changeFlowDueTime(java.util.Collection, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void changeFlowDueTime(Collection<String> flowInstUuids, String dueTime, String opinionText) {
        for (String flowInstUuid : flowInstUuids) {
            taskService.changeFlowDueTime(flowInstUuid, dueTime, opinionText);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#changeTaskDueTime(java.util.Collection, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void changeTaskDueTime(Collection<FlowShareItem> flowShareItems, String dueTime, String opinionText) {
        for (FlowShareItem flowShareItem : flowShareItems) {
            taskService.changeTaskDueTime(flowShareItem.getTaskInstUuid(), flowShareItem.getFlowInstUuid(), dueTime, opinionText);
        }
    }

    /* add by huanglinchuan 2014.11.1 begin */
    @Override
    @Transactional
    public void remind(Collection<String> taskInstUuids, String opinionName, String opinionValue, String opinionText) {
        for (String taskInstUuid : taskInstUuids) {
            taskService.remind(taskInstUuid, opinionName, opinionValue, opinionText, Collections.emptyList());
        }
    }

    /* add by huanglinchuan 2014.11.1 end */

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isAllowedTransfer(java.util.Collection)
     */
    @Override
    public boolean isAllowedTransfer(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (!taskService.isAllowedTransfer(userId, taskInstUuid)) {
                return false;
            }
        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isRequiredTransferOpinion(java.util.Collection)
     */
    @Override
    public boolean isRequiredTransferOpinion(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (taskService.isRequiredTransferOpinion(userId, taskInstUuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isAllowedCounterSign(java.util.Collection)
     */
    @Override
    public boolean isAllowedCounterSign(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (!taskService.isAllowedCounterSign(userId, taskInstUuid)) {
                return false;
            }
        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isRequiredCounterSignOpinion(java.util.Collection)
     */
    @Override
    public boolean isRequiredCounterSignOpinion(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (taskService.isRequiredCounterSignOpinion(userId, taskInstUuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isAllowedRollback(java.util.Collection)
     */
    @Override
    public boolean isAllowedRollback(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (!taskService.isAllowedRollbackToTask(userId, taskInstUuid)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断指定的流程环节实例UUID是否允许直接退回
     *
     * @param taskInstUuids
     * @return
     */
    @Override
    public boolean isAllowedDirectRollback(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (!taskService.isAllowedDirectRollbackToTask(userId, taskInstUuid)) {
                return false;
            }
        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#checkAndGetToRollbackTasks(java.util.Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public ResultMessage checkAndGetToRollbackTasks(Collection<String> taskInstUuids) {
        if (taskInstUuids.isEmpty()) {
            throw new WorkFlowException("请选择一条记录!");
        }

        List<TaskInstance> taskInstances = taskInstanceService.listByUuids(Lists.newArrayList(taskInstUuids));
        // 检查
        TaskInstance checkTaskInstance = taskInstances.get(0);
        String checkTaskInstUuid = checkTaskInstance.getUuid();
        String checkFlowDefId = checkTaskInstance.getFlowDefinition().getId();
        Map<String, List<RollBackTask>> rollBackTasksMap = new HashMap<String, List<RollBackTask>>();
        for (TaskInstance taskInstance : taskInstances) {
            if (checkTaskInstUuid.equals(taskInstance.getUuid())) {
                continue;
            }

            String flowDefId = taskInstance.getFlowDefinition().getId();
            if (!StringUtils.equals(checkFlowDefId, flowDefId)) {
                throw new WorkFlowException("请选择同一类型的流程!");
            }

            rollBackTasksMap.put(taskInstance.getUuid(), rollbackTaskActionExecutor.buildToRollbackTasks(taskInstance));
        }

        rollBackTasksMap.put(checkTaskInstUuid, rollbackTaskActionExecutor.buildToRollbackTasks(checkTaskInstance));

        List<RollBackTask> intersectionRollBackTasks = new ArrayList<RollBackTask>();
        intersectionRollBackTasks.addAll(rollBackTasksMap.get(checkTaskInstUuid));
        List<RollBackTask> deleteRollBackTasks = new ArrayList<RollBackTask>();
        // 获取可退回的环节交集
        for (RollBackTask intersectionRollBackTask : intersectionRollBackTasks) {
            for (String key : rollBackTasksMap.keySet()) {
                if (checkTaskInstUuid.equals(key)) {
                    continue;
                }
                List<RollBackTask> rollBackTasks = rollBackTasksMap.get(key);
                if (!rollBackTasks.contains(intersectionRollBackTask)) {
                    deleteRollBackTasks.add(intersectionRollBackTask);
                }
            }
        }
        intersectionRollBackTasks.removeAll(deleteRollBackTasks);

        // 返回各个流程实例可退回的环节交集数据
        for (String key : rollBackTasksMap.keySet()) {
            List<RollBackTask> rollBackTasks = rollBackTasksMap.get(key);
            rollBackTasks.retainAll(intersectionRollBackTasks);
        }
        ResultMessage msg = new ResultMessage();
        msg.setData(rollBackTasksMap);
        return msg;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#rollback(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void rollback(String taskInstUuid, String rollbackToTaskId, String rollbackToTaskInstUuid,
                         String opinionName, String opinionValue, String opinionText) {
        WorkBean workBean = workService.getTodo(taskInstUuid, null);
        workBean = workService.getWorkData(workBean);

        workBean.setRollbackToTaskId(rollbackToTaskId);
        workBean.setRollbackToTaskInstUuid(rollbackToTaskInstUuid);
        workBean.setOpinionLabel(opinionName);
        workBean.setOpinionValue(opinionValue);
        workBean.setOpinionText(opinionText);

        workService.rollbackWithWorkData(workBean);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isRequiredRollbackOpinion(java.util.Collection)
     */
    @Override
    public boolean isRequiredRollbackOpinion(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (taskService.isRequiredRollbackOpinion(userId, taskInstUuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isAllowedCancel(java.util.Collection)
     */
    @Override
    public boolean isAllowedCancel(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (!taskService.isAllowedCancel(userId, taskInstUuid)) {
                TaskActivity taskActivity = taskActivityService.getByTaskInstUuid(taskInstUuid);
                FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskService.getTask(taskInstUuid).getFlowDefinition());
                if (!flowDelegate.existsTaskNode(taskActivity.getPreTaskId())) {
                    throw new RuntimeException("流程已变更，无法撤回！");
                }
                if (flowDelegate.isNotCancel(taskActivity.getTaskId())) {
                    throw new RuntimeException(flowDelegate.getFlow().getTask(taskActivity.getTaskId()).getName() + "环节不可被撤回！");
                }
                return false;
            }
        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#isRequiredCancelOpinion(java.util.Collection)
     */
    @Override
    public boolean isRequiredCancelOpinion(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            if (taskService.isRequiredCancelOpinion(userId, taskInstUuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#gotoApprove(java.lang.String)
     */
    @Override
    public void gotoApprove(String taskInstUuid) {
        // TODO Auto-generated method stub

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#deleteWork(java.util.Collection)
     */
    @Override
    public void deleteWork(Collection<String> flowInstUuids) {
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        String adminId = SpringSecurityUtils.getCurrentUserId();
//        List<String> allAdminIds = orgApiFacade.queryAllAdminIdsByUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        List<String> allAdminIds = workflowOrgService.listCurrentTenantAdminIds();
        if (!allAdminIds.isEmpty()) {
            adminId = allAdminIds.get(0);
        }
        try {
            IgnoreLoginUtils.login(tenantId, adminId);
            for (String flowInstUuid : flowInstUuids) {
                List<TaskInstance> unfinishedTasks = taskService.getUnfinishedTasks(flowInstUuid);
                if (!unfinishedTasks.isEmpty()) {
                    for (TaskInstance taskInstance : unfinishedTasks) {
                        taskService.deleteByAdmin(adminId, taskInstance.getUuid());
                    }
                } else {
                    // 删除草稿
                    FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
                    flowService.deleteDraft(flowInstance.getCreator(), flowInstUuid);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.ListWorkService#getBusinessApplicationConfig(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public BusinessApplicationConfigDto getBusinessApplicationConfig(String dataSource, String actionType,
                                                                     String businessType) {
        BusinessApplicationConfigDto applicationConfigDto = null;
        List<BusinessApplicationConfigDto> applicationConfigDtos = businessFacadeService
                .getBusinessApplicationConfigByCategoryUuid(businessType);
        for (BusinessApplicationConfigDto businessApplicationConfigDto : applicationConfigDtos) {
            String dictCode = businessApplicationConfigDto.getDictCode();
            if (StringUtils.equals(dataSource, FlowConstant.UNDERTAKE_SITUATION_DATA_SOURCE.BRANCH_TASK)) {
                if (StringUtils.equals("branch-task-" + actionType, dictCode)) {
                    applicationConfigDto = businessApplicationConfigDto;
                }
            } else {
                if (StringUtils.equals("subflow-" + actionType, dictCode)) {
                    applicationConfigDto = businessApplicationConfigDto;
                }
            }
        }
        return applicationConfigDto;
    }

}
