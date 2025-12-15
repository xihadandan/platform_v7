/*
 * @(#)2019年3月1日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dispatcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Transition;
import com.wellsoft.pt.bpm.engine.element.ParallelGatewayElement;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.org.dto.OrganizationDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 分支流分发处理
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年3月1日.1	zhulh		2019年3月1日		Create
 * </pre>
 * @date 2019年3月1日
 */
@Service
public class DefaultDispatcherBranchTaskResolver extends AbstractDispatcherBranchTaskResolver {

    @Autowired
    private FlowService flowService;

    @Autowired
    private IdentityResolverStrategy identityResolverStrategy;

    @Autowired
    private DyFormFacade dyFormFacade;

//    @Autowired
//    private OrgApiFacade orgApiFacade;

    @Autowired
    private WorkflowOrgService workflowOrgService;

//    @Autowired
//    private BusinessFacadeService businessFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.dispatcher.DispatcherBranchTaskResolver#resolve(com.wellsoft.pt.bpm.engine.core.Transition, com.wellsoft.pt.bpm.engine.core.Direction, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public void resolve(Transition transition, Direction direction, ExecutionContext executionContext) {
        // 来源
        String branchCreateWay = direction.getBranchCreateWay();

        // 表单创建
        if (FlowConstant.BRANCH_CREATE_WAY.DYFORM.equals(branchCreateWay)) {
            createBranchTaskInstanceByDyform(transition, direction, executionContext);
        } else {
            createBranchTaskInstanceByCustomInterface(transition, direction, executionContext);
        }

        // 跟进人员的流程参数，调整为生成分支流实例时添加
        // addBranchTaskMonitors(transition, executionContext);
    }

    /**
     * @param transition
     * @param direction
     * @param executionContext
     */
    private void createBranchTaskInstanceByDyform(Transition transition, Direction direction,
                                                  ExecutionContext executionContext) {
        // 是否主表创建方式
        boolean isMainFormBranchCreateWay = direction.isMainFormBranchCreateWay();

        // 主表创建方式
        if (isMainFormBranchCreateWay) {
            createBranchTaskInstanceByMainform(transition, direction, executionContext);
        } else {
            // 从表创建方式
            createBranchTaskInstanceBySubform(transition, direction, executionContext);
        }
    }

    /**
     * @param transition
     * @param direction
     * @param executionContext
     */
    private void createBranchTaskInstanceByMainform(Transition transition, Direction direction,
                                                    ExecutionContext executionContext) {
        TaskData taskData = executionContext.getToken().getTaskData();
        Node runtimeToNode = transition.getRuntimeToNode();
        String toTaskId = runtimeToNode.getId();
        // 分支实例类型
        String branchInstanceType = direction.getBranchInstanceType();
        // 字段
        String branchTaskUsersField = direction.getBranchTaskUsers();
        // 实例数量：1、单一实例; 2、按办理人生成
        String branchCreateInstanceWay = direction.getBranchCreateInstanceWay();
        // 共享分支
        boolean shareBranch = direction.isShareBranch();
        // 该流向不参与聚合
        boolean isIndependentBranch = direction.isIndependentBranch();

        // 主流程表单数据
        DyFormData dyFormData = getDyformData(executionContext);

        // 实例数量：1、单一实例; 2、按办理人生成
        if (StringUtils.isBlank(branchCreateInstanceWay)) {
            branchCreateInstanceWay = FlowConstant.CREATE_INSTANCE_WAY.SINGLETON;
        }
        // 办理人字段
        if (StringUtils.isBlank(branchTaskUsersField)) {
            // 办理人字段必须指定,否则不处理
            return;
        }

        // 办理人字段
        BranchTaskUsers branchTaskUsers = null;
        // 发起子流程时外部传入的办理人
        Set<String> subTaskUserIds = taskData.getTaskUsers(toTaskId);
        if (CollectionUtils.isEmpty(subTaskUserIds)) {
            String taskUsers = (String) dyFormData.getFieldValue(branchTaskUsersField);
            branchTaskUsers = getBranchTaskUsers(taskUsers, direction, executionContext, branchCreateInstanceWay);
        } else {
            branchTaskUsers = getBranchTaskUsers(StringUtils.join(subTaskUserIds, Separator.SEMICOLON.getValue()),
                    direction, executionContext, branchCreateInstanceWay);
        }

        // 过滤添加承办操作附加的办理人
        branchTaskUsers = filterAdditionalBranchTaskUsersIfRequire(taskData.getDataUuid(), direction, taskData,
                branchTaskUsersField, branchTaskUsers);

        List<String> userIds = branchTaskUsers.getUsers();
        List<String> userNames = branchTaskUsers.getUserNames();

        // 单一实例
        if (StringUtils.equals(branchCreateInstanceWay, FlowConstant.CREATE_INSTANCE_WAY.SINGLETON)) {
            List<String> taskTodoUsers = resolveTaskTodoUsers(userIds, userNames, branchTaskUsers.getType(), direction,
                    executionContext);
            taskData.resetTaskUsers(toTaskId);
            taskData.addTaskUsers(toTaskId, taskTodoUsers);
            taskData.setTaskTodoId(toTaskId, StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
            taskData.setTaskTodoName(toTaskId, StringUtils.join(userNames, Separator.SEMICOLON.getValue()));
            runtimeToNode.enter(executionContext);
        } else {
            // 按办理人生成
            for (int index = 0; index < userIds.size(); index++) {
                String userId = userIds.get(index);
                String userName = userNames.get(index);
                List<String> userList = Lists.newArrayList();
                userList.add(userId);
                List<String> userNameList = Lists.newArrayList();
                userNameList.add(userName);

                List<String> taskTodoUsers = resolveTaskTodoUsers(userList, userNameList, branchTaskUsers.getType(),
                        direction, executionContext);
                taskData.resetTaskUsers(toTaskId);
                taskData.addTaskUsers(toTaskId, taskTodoUsers);
                taskData.setTaskTodoId(toTaskId, userId);
                taskData.setTaskTodoName(toTaskId, userName);
                runtimeToNode.enter(executionContext);
            }
        }
    }

    /**
     * @param transition
     * @param direction
     * @param executionContext
     */
    private void createBranchTaskInstanceBySubform(Transition transition, Direction direction,
                                                   ExecutionContext executionContext) {
        TaskData taskData = executionContext.getToken().getTaskData();
        Node runtimeToNode = transition.getRuntimeToNode();
        String toTaskId = runtimeToNode.getId();
        // 字段
        String branchTaskUsersField = direction.getBranchTaskUsers();
        // 实例数量：1、单一实例; 2、按办理人生成
        String branchCreateInstanceWay = direction.getBranchCreateInstanceWay();

        // 实例数量：1、单一实例; 2、按办理人生成
        if (StringUtils.trimToEmpty(branchCreateInstanceWay).equals(StringUtils.EMPTY)) {
            branchCreateInstanceWay = FlowConstant.CREATE_INSTANCE_WAY.SINGLETON;
        }
        // 办理人字段
        if (StringUtils.trimToEmpty(branchTaskUsersField).equals(StringUtils.EMPTY)) {
            // 办理人字段必须指定,否则不处理
            return;
        }

        // 表单
        String subformId = direction.getBranchCreateInstanceFormId();
        if (subformId.equals(StringUtils.EMPTY)) {
            return;
        }

        // 主流程表单数据
        DyFormData dyFormData = getDyformData(executionContext);
        // 从表数据d
        String subFormUuid = dyFormFacade.getFormUuidById(subformId);
        if (StringUtils.isBlank(subFormUuid)) {
            subFormUuid = subformId;
        }
        List<Map<String, Object>> subFormDatas = dyFormData.getFormDatas(subFormUuid);
        if (CollectionUtils.isEmpty(subFormDatas)) {
            return;
        }

        // 按从表行分批次生成实例
        boolean isBranchCreateInstanceBatch = direction.isBranchCreateInstanceBatch();
        if (isBranchCreateInstanceBatch) {
            createBranchTaskInstanceBySubformBatch(direction, executionContext, taskData, runtimeToNode, toTaskId,
                    branchTaskUsersField, branchCreateInstanceWay, subFormDatas, subFormUuid);
        } else {
            createBranchTaskInstanceBySubformData(direction, executionContext, taskData, runtimeToNode, toTaskId,
                    branchTaskUsersField, branchCreateInstanceWay, subFormDatas);
        }
    }

    /**
     * @param direction
     * @param executionContext
     * @param taskData
     * @param runtimeToNode
     * @param toTaskId
     * @param branchTaskUsersField
     * @param branchCreateInstanceWay
     * @param subFormDatas
     * @param subFormUuid
     */
    private void createBranchTaskInstanceBySubformBatch(Direction direction, ExecutionContext executionContext,
                                                        TaskData taskData, Node runtimeToNode, String toTaskId, String branchTaskUsersField,
                                                        String branchCreateInstanceWay, List<Map<String, Object>> subFormDatas, String subFormUuid) {
        FlowInstance flowInstance = executionContext.getToken().getFlowInstance();
        String taskId = taskData.getPreTaskId(toTaskId);
        String taskInstUuid = taskData.getPreTaskInstUuid(toTaskId);
        // 获取已完成的批次数据UUID
        List<String> completedBatchDataUuids = getCompletedBatchDataUuids(flowInstance, taskId, subFormUuid);
        for (Map<String, Object> subFormData : subFormDatas) {
            String subFormDataUuid = ObjectUtils.toString(subFormData.get(IdEntity.UUID), StringUtils.EMPTY);
            // 按未处理的批次数据UUID生成流程实例
            if (!completedBatchDataUuids.contains(subFormDataUuid)) {
                createBranchTaskInstanceBySubformRowData(direction, executionContext, taskData, runtimeToNode,
                        toTaskId, branchTaskUsersField, branchCreateInstanceWay, subFormData);
                // 保存当前批次的流程参数信息
                saveOrUpdateCurrentBatchInfo(flowInstance, taskInstUuid, subFormUuid, subFormDataUuid);
                break;
            }
        }
    }

    /**
     * @param flowInstance
     * @param taskInstUuid
     * @param subFormUuid
     * @param subFormDataUuid
     */
    private void saveOrUpdateCurrentBatchInfo(FlowInstance flowInstance, String taskInstUuid, String subFormUuid,
                                              String subFormDataUuid) {
        String parentFlowInstUuid = flowInstance.getUuid();
        String parentTaskInstUuid = taskInstUuid;
        String name = FlowConstant.SUB_FLOW.KEY_BATCH_FORM_INFO + Separator.UNDERLINE.getValue() + parentTaskInstUuid;
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(parentFlowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isEmpty(parameters)) {
            example.setValue(subFormUuid + Separator.COLON.getValue() + subFormDataUuid);
            flowService.saveFlowInstanceParameter(example);
        } else {
            for (FlowInstanceParameter flowInstanceParameter : parameters) {
                String batchFormInfo = flowInstanceParameter.getValue();
                String[] batchFormInfos = StringUtils.split(batchFormInfo, Separator.SEMICOLON.getValue());
                List<String> batchFormInfoList = Lists.newArrayList();
                batchFormInfoList.addAll(Arrays.asList(batchFormInfos));
                String newBatchFormInfo = subFormUuid + Separator.COLON.getValue() + subFormDataUuid;
                // 忽略掉多个子流程实例同批次的数据
                if (!batchFormInfoList.contains(newBatchFormInfo)) {
                    batchFormInfoList.add(newBatchFormInfo);
                    flowInstanceParameter.setValue(StringUtils.join(batchFormInfoList, Separator.SEMICOLON.getValue()));
                    flowService.saveFlowInstanceParameter(flowInstanceParameter);
                }
            }
        }
    }

    /**
     * @param flowInstance
     * @param taskId
     * @param subFormUuid
     * @return
     */
    private List<String> getCompletedBatchDataUuids(FlowInstance flowInstance, String taskId, String subFormUuid) {
        String subFormId = dyFormFacade.getFormIdByFormUuid(subFormUuid);
        String parentFlowInstUuid = flowInstance.getUuid();
        String name = FlowConstant.SUB_FLOW.KEY_COMPLATED_BATCH + Separator.UNDERLINE.getValue() + taskId
                + Separator.UNDERLINE.getValue() + subFormId;
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(parentFlowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        List<String> dataUuids = Lists.newArrayList();
        for (FlowInstanceParameter flowInstanceParameter : parameters) {
            String completedBatchDataUuid = flowInstanceParameter.getValue();
            String[] completedBatchDataUuids = StringUtils
                    .split(completedBatchDataUuid, Separator.SEMICOLON.getValue());
            dataUuids.addAll(Arrays.asList(completedBatchDataUuids));
        }
        return dataUuids;
    }

    /**
     * @param direction
     * @param executionContext
     * @param taskData
     * @param runtimeToNode
     * @param toTaskId
     * @param branchTaskUsersField
     * @param branchCreateInstanceWay
     * @param subFormDatas
     */
    private void createBranchTaskInstanceBySubformData(Direction direction, ExecutionContext executionContext,
                                                       TaskData taskData, Node runtimeToNode, String toTaskId, String branchTaskUsersField,
                                                       String branchCreateInstanceWay, List<Map<String, Object>> subFormDatas) {
        for (Map<String, Object> subFormData : subFormDatas) {
            createBranchTaskInstanceBySubformRowData(direction, executionContext, taskData, runtimeToNode, toTaskId,
                    branchTaskUsersField, branchCreateInstanceWay, subFormData);
        }
    }

    /**
     * @param direction
     * @param executionContext
     * @param taskData
     * @param runtimeToNode
     * @param toTaskId
     * @param branchTaskUsersField
     * @param branchCreateInstanceWay
     * @param subFormDatas
     */
    private void createBranchTaskInstanceBySubformRowData(Direction direction, ExecutionContext executionContext,
                                                          TaskData taskData, Node runtimeToNode, String toTaskId, String branchTaskUsersField,
                                                          String branchCreateInstanceWay, Map<String, Object> subFormData) {
        // 办理人字段
        BranchTaskUsers branchTaskUsers = null;
        // 发起子流程时外部传入的办理人
        Set<String> subTaskUserIds = taskData.getTaskUsers(toTaskId);
        if (CollectionUtils.isEmpty(subTaskUserIds)) {
            if (subFormData == null || !subFormData.containsKey(branchTaskUsersField)) {
                return;
            }
            String taskUsers = StringUtils.trimToEmpty((String) subFormData.get(branchTaskUsersField));
            if (StringUtils.isBlank(taskUsers)) {
                return;
            }
            branchTaskUsers = getBranchTaskUsers(taskUsers, direction, executionContext, branchCreateInstanceWay);
        } else {
            branchTaskUsers = getBranchTaskUsers(StringUtils.join(subTaskUserIds, Separator.SEMICOLON.getValue()),
                    direction, executionContext, branchCreateInstanceWay);
        }

        // 过滤添加承办操作附加的办理人
        String dataUuid = ObjectUtils.toString(subFormData.get(IdEntity.UUID), StringUtils.EMPTY);
        branchTaskUsers = filterAdditionalBranchTaskUsersIfRequire(dataUuid, direction, taskData, branchTaskUsersField,
                branchTaskUsers);

        // 办理人
        List<String> userIds = branchTaskUsers.getUsers();
        List<String> userNames = branchTaskUsers.getUserNames();
        // 单一实例
        if (StringUtils.equals(branchCreateInstanceWay, FlowConstant.CREATE_INSTANCE_WAY.SINGLETON)) {
            List<String> taskTodoUsers = resolveTaskTodoUsers(userIds, userNames, branchTaskUsers.getType(), direction,
                    executionContext);
            taskData.resetTaskUsers(toTaskId);
            taskData.addTaskUsers(toTaskId, taskTodoUsers);
            taskData.setTaskTodoId(toTaskId, StringUtils.join(userIds, Separator.SEMICOLON.getValue()));
            taskData.setTaskTodoName(toTaskId, StringUtils.join(userNames, Separator.SEMICOLON.getValue()));
            runtimeToNode.enter(executionContext);
            taskData.resetTaskUsers(toTaskId);
        } else {
            // 按办理人生成
            for (int index = 0; index < userIds.size(); index++) {
                String userId = userIds.get(index);
                String userName = userNames.get(index);
                List<String> userList = Lists.newArrayList();
                userList.add(userId);
                List<String> userNameList = Lists.newArrayList();
                userNameList.add(userName);

                List<String> taskTodoUsers = resolveTaskTodoUsers(userList, userNameList, branchTaskUsers.getType(),
                        direction, executionContext);
                taskData.resetTaskUsers(toTaskId);
                taskData.addTaskUsers(toTaskId, taskTodoUsers);
                taskData.setTaskTodoId(toTaskId, userId);
                taskData.setTaskTodoName(toTaskId, userName);
                runtimeToNode.enter(executionContext);
                taskData.resetTaskUsers(toTaskId);
            }
        }
    }

    /**
     * @param taskUsers
     * @param direction
     * @return
     */
    private BranchTaskUsers getBranchTaskUsers(String taskUsers, Direction direction, ExecutionContext executionContext, String branchCreateInstanceWay) {
        TaskElement fromTaskElement = executionContext.getFlowDelegate().getFlow().getTask(direction.getFromID());
        ParallelGatewayElement parallelGatewayElement = fromTaskElement.getParallelGateway();
        String businessType = parallelGatewayElement.getBusinessType();
        String businessRole = parallelGatewayElement.getBusinessRole();
        String userType = BranchTaskUsers.TYPE_ORG;
        List<String> users = Lists.newArrayList();
        List<String> userNames = Lists.newArrayList();
        // 解析业务单位的人员
        if (StringUtils.isNotBlank(businessType) && StringUtils.isNotBlank(businessRole)) {
            userType = BranchTaskUsers.TYPE_BIZ;
            if (StringUtils.isNotBlank(taskUsers)) {
                String[] ids = taskUsers.split(Separator.SEMICOLON.getValue());
                for (String id : ids) {
//                    // 业务分类结点，按办理人生成实例，7.0组织没有业务分类节点
//                    BusinessCategoryOrgDto businessCategoryNodeDto = businessFacadeService.getBusinessCategoryOrgByUuid(id);
//                    if (businessCategoryNodeDto != null && StringUtils.equals(branchCreateInstanceWay, FlowConstant.CREATE_INSTANCE_WAY.BY_USER)) {
//                        List<BusinessCategoryOrgDto> businessCategoryOrgDtos = businessFacadeService.getBusinessCategoryOrgsByParentUuid(id);
//                        for (BusinessCategoryOrgDto businessCategoryOrgDto : businessCategoryOrgDtos) {
//                            users.add(businessCategoryOrgDto.getDept());
//                            userNames.add(businessCategoryOrgDto.getName());
//                        }
//                    } else {
                    String orgName = workflowOrgService.getNameById(id);
//                        BusinessCategoryOrgDto businessCategoryOrgDto = businessFacadeService.getBusinessCategoryOrgByCategoryUuidAndId(businessType, id);
//                        String orgName = id;
//                        if (businessCategoryOrgDto != null) {
//                            orgName = businessCategoryOrgDto.getName();
//                        }
                    users.add(id);
                    userNames.add(orgName);
//                    }
                }
            }
        } else {
            if (StringUtils.isNotBlank(taskUsers)) {
                String[] ids = taskUsers.split(Separator.SEMICOLON.getValue());
                for (String id : ids) {
                    List<OrgUserJobDto> userJobDtos = workflowOrgService.listUserJobs(id, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(executionContext.getToken()));
                    users.add(id);
                    if (CollectionUtils.isNotEmpty(userJobDtos)) {
                        userNames.add(getUserDepartmentName(userJobDtos));
                    } else {
                        userNames.add(workflowOrgService.getNameById(id));
                    }
//                    MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(id);
//                    if (user != null) {
//                        users.add(id);
//                        String deptName = getUserDepartmentName(id);
//                        if (StringUtils.isNotBlank(deptName)) {
//                            userNames.add(user.getUserName() + "（" + deptName + "）");
//                        } else {
//                            userNames.add(user.getUserName());
//                        }
//                    }
                }
            }
        }

        BranchTaskUsers branchTaskUsers = new BranchTaskUsers();
        branchTaskUsers.setType(userType);
        branchTaskUsers.setUserNames(userNames);
        branchTaskUsers.setUsers(users);
        return branchTaskUsers;
    }

    /**
     * @param direction
     * @param taskData
     * @param branchTaskUsersField
     * @param branchTaskUsers
     * @return
     */
    private BranchTaskUsers filterAdditionalBranchTaskUsersIfRequire(String dataUuid, Direction direction,
                                                                     TaskData taskData, String branchTaskUsersField, BranchTaskUsers branchTaskUsers) {
        String fromTaskId = direction.getFromID();
        if (!taskData.isUseFormFieldUsers(fromTaskId)) {
            // 确保流程参数存储使用表单的办理人
            addFormFieldUserFlowInstanceParameterOnce(dataUuid, taskData, branchTaskUsersField, direction,
                    branchTaskUsers);
            return branchTaskUsers;
        }

        List<String> oldFormFieldUsers = getAndUpdateFormFieldUserByFlowInstanceParameter(dataUuid, taskData,
                branchTaskUsersField, direction, branchTaskUsers);

        List<String> newFormFieldUsers = branchTaskUsers.getUsers();
        List<String> newFormFieldUserNames = branchTaskUsers.getUserNames();
        // 去除已存在的组织名称
        Map<String, Object> newFormFieldUserMap = Maps.newHashMap();
        for (int index = 0; index < newFormFieldUsers.size(); index++) {
            newFormFieldUserMap.put(newFormFieldUsers.get(index), newFormFieldUserNames.get(index));
        }
        for (String oldFormFieldUser : oldFormFieldUsers) {
            if (newFormFieldUserMap.containsKey(oldFormFieldUser)) {
                newFormFieldUserNames.remove(newFormFieldUserMap.get(oldFormFieldUser));
            }
        }
        // 去除已存在的组织ID
        newFormFieldUsers.removeAll(oldFormFieldUsers);
        return branchTaskUsers;
    }

    /**
     * @param dataUuid
     * @param taskData
     * @param preTaskInstUuid
     * @param flowInstUuid
     * @param direction
     * @param branchTaskUsers
     */
    private void addFormFieldUserFlowInstanceParameterOnce(String dataUuid, TaskData taskData,
                                                           String branchTaskUsersField, Direction direction, BranchTaskUsers branchTaskUsers) {
        String toTaskId = direction.getToID();
        String preTaskInstUuid = taskData.getPreTaskInstUuid(toTaskId);
        String flowInstUuid = taskData.getFlowInstUuid();

        // 分发环节实例UUID + "_" + 表单数据UUID + "_" + 流向定义ID + "_" + 表单字段
        String name = preTaskInstUuid + Separator.UNDERLINE.getValue() + dataUuid + Separator.UNDERLINE.getValue()
                + direction.getId() + Separator.UNDERLINE.getValue() + branchTaskUsersField;

        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isEmpty(parameters)) {
            example.setValue(StringUtils.join(branchTaskUsers.getUsers(), Separator.SEMICOLON.getValue()));
            flowService.saveFlowInstanceParameter(example);
        }
    }

    /**
     * @param dataUuid
     * @param taskData
     * @param branchTaskUsersField
     * @param direction
     * @param branchTaskUsers
     */
    private void updateFormFieldUserFlowInstanceParameter(String dataUuid, TaskData taskData,
                                                          String branchTaskUsersField, Direction direction, BranchTaskUsers branchTaskUsers) {
        String toTaskId = direction.getToID();
        String preTaskInstUuid = taskData.getPreTaskInstUuid(toTaskId);
        String flowInstUuid = taskData.getFlowInstUuid();

        // 分发环节实例UUID + "_" + 表单数据UUID + "_" + 流向定义ID + "_" + 表单字段
        String name = preTaskInstUuid + Separator.UNDERLINE.getValue() + dataUuid + Separator.UNDERLINE.getValue()
                + direction.getId() + Separator.UNDERLINE.getValue() + branchTaskUsersField;

        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isEmpty(parameters)) {
            example.setValue(StringUtils.join(branchTaskUsers.getUsers(), Separator.SEMICOLON.getValue()));
            flowService.saveFlowInstanceParameter(example);
        } else {
            for (FlowInstanceParameter flowInstanceParameter : parameters) {
                flowInstanceParameter.setValue(StringUtils.join(branchTaskUsers.getUsers(),
                        Separator.SEMICOLON.getValue()));
                flowService.saveFlowInstanceParameter(flowInstanceParameter);
            }
        }
    }

    /**
     * @param dataUuid
     * @param taskData
     * @param branchTaskUsersField
     * @param direction
     * @param branchTaskUsers
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<String> getAndUpdateFormFieldUserByFlowInstanceParameter(String dataUuid, TaskData taskData,
                                                                          String branchTaskUsersField, Direction direction, BranchTaskUsers branchTaskUsers) {
        String toTaskId = direction.getToID();
        String preTaskInstUuid = taskData.getPreTaskInstUuid(toTaskId);
        String flowInstUuid = taskData.getFlowInstUuid();

        // 分发环节实例UUID + "_" + 表单数据UUID + "_" + 流向定义ID + "_" + 表单字段
        String name = preTaskInstUuid + Separator.UNDERLINE.getValue() + dataUuid + Separator.UNDERLINE.getValue()
                + direction.getId() + Separator.UNDERLINE.getValue() + branchTaskUsersField;

        String taskDataKey = flowInstUuid + name;
        List<String> tmpFormFieldUsers = (List<String>) taskData.get(taskDataKey);
        if (tmpFormFieldUsers != null) {
            return tmpFormFieldUsers;
        }

        List<String> formFieldUsers = Lists.newArrayList();

        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        for (FlowInstanceParameter flowInstanceParameter : parameters) {
            String formFieldUser = flowInstanceParameter.getValue();
            if (StringUtils.isNotBlank(formFieldUser)) {
                List<String> strings = Arrays.asList(StringUtils.split(formFieldUser, Separator.SEMICOLON.getValue()));
                formFieldUsers.addAll(strings);
            }
        }

        // 标记任务数据
        taskData.put(taskDataKey, formFieldUsers);
        // 更新新的任务参数
        Boolean formFieldUserUpdated = (Boolean) taskData.get(taskDataKey + "Update");
        if (!Boolean.TRUE.equals(formFieldUserUpdated)) {
            updateFormFieldUserFlowInstanceParameter(dataUuid, taskData, branchTaskUsersField, direction,
                    branchTaskUsers);
            taskData.put(taskDataKey + "Update", true);

        }
        return formFieldUsers;
    }

    /**
     * @param toCheckUsers
     * @param toCheckNames
     * @param type
     * @param direction
     * @param executionContext
     * @return
     */
    private List<String> resolveTaskTodoUsers(List<String> toCheckUsers, List<String> toCheckNames, String type,
                                              Direction direction, ExecutionContext executionContext) {
        if (BranchTaskUsers.TYPE_ORG.equals(type)) {
            return toCheckUsers;
        }

        TaskElement fromTaskElement = executionContext.getFlowDelegate().getFlow().getTask(direction.getFromID());
        ParallelGatewayElement parallelGatewayElement = fromTaskElement.getParallelGateway();
        String businessType = parallelGatewayElement.getBusinessType();
        String businessTypeName = parallelGatewayElement.getBusinessTypeName();
        String businessRole = parallelGatewayElement.getBusinessRole();
        String businessRoleName = parallelGatewayElement.getBusinessRoleName();

        Set<String> returnUserIds = Sets.newLinkedHashSet();
        for (int index = 0; index < toCheckUsers.size(); index++) {
            String orgElementId = toCheckUsers.get(index);
            String orgElementName = toCheckNames.get(index);
            Set<String> userIdSet = workflowOrgService.listUserIdWithOrgRoleIdAndOrgId(orgElementId, businessRole, businessType);
//            Set<String> userIdSet = businessFacadeService
//                    .getUserByOrgUuidAndRoleUuid(businessType, orgId, businessRole);
            if (CollectionUtils.isEmpty(userIdSet)) {
                if (StringUtils.isBlank(businessTypeName)) {
                    OrganizationDto organization = workflowOrgService.getOrganizationById(businessType);
                    businessTypeName = organization.getName();
                }
                throw new RuntimeException("业务类型为[" + businessTypeName + "]的业务单位通讯录结点[" + orgElementName + "]没有配置角色["
                        + businessRoleName + "]人员！");
            }
            returnUserIds.addAll(userIdSet);
        }

        return Arrays.asList(returnUserIds.toArray(new String[0]));
    }

    /**
     * @param userJobDtos
     * @return
     */
    private String getUserDepartmentName(List<OrgUserJobDto> userJobDtos) {
        String deptName = StringUtils.EMPTY;
        OrgUserJobDto userJob = userJobDtos.stream().filter(job -> job.isPrimary()).findFirst().orElse(userJobDtos.get(0));
        List<String> idPaths = Arrays.asList(StringUtils.split(userJob.getJobIdPath(), Separator.SLASH.getValue()));
        List<String> namePaths = Arrays.asList(StringUtils.split(userJob.getJobNamePath(), Separator.SLASH.getValue()));
        if (CollectionUtils.size(idPaths) != CollectionUtils.size(namePaths)) {
            return deptName;
        }
        for (int index = idPaths.size() - 1; index >= 0; index--) {
            String deptId = idPaths.get(index);
            if (StringUtils.startsWith(deptId, IdPrefix.DEPARTMENT.getValue())) {
                return namePaths.get(index);
            }
        }
        return deptName;
    }

    /**
     * @param userId
     * @return
     */
//    private String getUserDepartmentName(String userId) {
//        String deptName = StringUtils.EMPTY;
//        MultiOrgUserWorkInfo workInfo = orgApiFacade.getUserWorkInfoByUserId(userId);
//        String deptId = workInfo.getDeptIds();
//        if (StringUtils.isEmpty(deptId)) {
//            return deptName;
//        }
//        List<String> deptIds = Arrays.asList(StringUtils.split(deptId, Separator.SEMICOLON.getValue()));
//        deptName = workflowOrgService.getNameById(deptIds.get(0));
//        // deptName = orgApiFacade.getNameByOrgEleId(deptIds.get(0));
//        return deptName;
//    }

    /**
     * @param executionContext
     * @return
     */
    private DyFormData getDyformData(ExecutionContext executionContext) {
        TaskData taskData = executionContext.getToken().getTaskData();
        // 主流程表单定义UUID
        String formUuid = taskData.getFormUuid();
        // 主流程表单数据UUID
        String dataUuid = taskData.getDataUuid();
        DyFormData dyFormData = taskData.getDyFormData(dataUuid);
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            taskData.setDyFormData(dataUuid, dyFormData);
        }
        return dyFormData;
    }

    /**
     * @param transition
     * @param direction
     * @param executionContext
     */
    private void createBranchTaskInstanceByCustomInterface(Transition transition, Direction direction,
                                                           ExecutionContext executionContext) {
        // 分支流分发接口
        String interfaceName = direction.getBranchInterfaceName();
        String interfaceValue = direction.getBranchInterface();
        CustomDispatcherBranchTaskResolver dispatcher = ApplicationContextHolder.getBean(interfaceValue,
                CustomDispatcherBranchTaskResolver.class);
        if (dispatcher == null) {
            throw new WorkFlowException("找不到[" + interfaceName + ":" + interfaceValue + "]的分支流分发实现");
        }
        dispatcher.resolve(transition, direction, executionContext);
    }

    /**
     * @param transition
     * @param executionContext
     */
    private void addBranchTaskMonitors(Transition transition, ExecutionContext executionContext) {
        String fromTaskId = transition.getFromId();
        TaskElement taskElement = executionContext.getFlowDelegate().getFlow().getTask(fromTaskId);
        ParallelGatewayElement gatewayElement = taskElement.getParallelGateway();
        List<UserUnitElement> subTaskMonitorElements = gatewayElement.getBranchTaskMonitors();
        if (CollectionUtils.isEmpty(subTaskMonitorElements)) {
            return;
        }
        List<FlowUserSid> followUpUserSids = identityResolverStrategy.resolve(transition.getFrom(),
                executionContext.getToken(), subTaskMonitorElements, ParticipantType.MonitorUser);
        List<String> followUpUserIds = IdentityResolverStrategy.resolveAsOrgIds(followUpUserSids);
        if (CollectionUtils.isEmpty(followUpUserIds)) {
            return;
        }

        FlowInstanceParameter example = new FlowInstanceParameter();
        String flowInstUuid = executionContext.getToken().getFlowInstance().getUuid();
        String name = FlowConstant.SUB_FLOW.KEY_FOLLOW_UP_USERS + Separator.UNDERLINE.getValue() + fromTaskId;
        example.setFlowInstUuid(flowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isEmpty(parameters)) {
            example.setValue(StringUtils.join(followUpUserIds, Separator.SEMICOLON.getValue()));
            flowService.saveFlowInstanceParameter(example);
        } else {
            for (FlowInstanceParameter flowInstanceParameter : parameters) {
                flowInstanceParameter.setValue(StringUtils.join(followUpUserIds, Separator.SEMICOLON.getValue()));
                flowService.saveFlowInstanceParameter(flowInstanceParameter);
            }
        }
    }

    public static class BranchTaskUsers {
        // 人员类型1、组织用户，2、业务单位通讯录
        public static final String TYPE_ORG = "1";
        public static final String TYPE_BIZ = "2";
        private String type;
        private List<String> users = Lists.newArrayList();
        private List<String> userNames = Lists.newArrayList();

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type 要设置的type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return the users
         */
        public List<String> getUsers() {
            return users;
        }

        /**
         * @param users 要设置的users
         */
        public void setUsers(List<String> users) {
            this.users = users;
        }

        /**
         * @return the userNames
         */
        public List<String> getUserNames() {
            return userNames;
        }

        /**
         * @param userNames 要设置的userNames
         */
        public void setUserNames(List<String> userNames) {
            this.userNames = userNames;
        }

    }

}
