/*
 * @(#)2013-5-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreOrder;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.dao.TaskSubFlowDao;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.ColumnElement;
import com.wellsoft.pt.bpm.engine.element.OrderElement;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.SuspensionState;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicColumn;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicColumnValue;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.query.FlowShareDataQueryItem;
import com.wellsoft.pt.bpm.engine.query.api.TaskInfoDistributionQueryItem;
import com.wellsoft.pt.bpm.engine.query.api.TaskOperationQueryItem;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.TitleExpressionUtils;
import com.wellsoft.pt.bpm.engine.util.UndertakeSituationUtils;
import com.wellsoft.pt.common.i18n.AppCodeI18nMessageSource;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.enums.SortFieldEnum;
import com.wellsoft.pt.workflow.work.bean.*;
import com.wellsoft.pt.workflow.work.vo.SubTaskDataVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Description: 任务子流程服务实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-15.1	zhulh		2013-5-15		Create
 * </pre>
 * @date 2013-5-15
 */
@Service
public class TaskSubFlowServiceImpl extends AbstractJpaServiceImpl<TaskSubFlow, TaskSubFlowDao, String>
        implements TaskSubFlowService {

    private static final String COUNT = "select count(*) from TaskSubFlow task_sub_flow where task_sub_flow.parentFlowInstUuid = :parentFlowInstUuid and task_sub_flow.flowInstUuid = :flowInstUuid and task_sub_flow.isMerge = :isMerge";
    @Autowired
    DyFormFacade dyFormFacade;
    private String GET_BY_SUB_FLOW_INST_UUID = "from TaskSubFlow task_sub_flow1 where task_sub_flow1.flowInstUuid = :flowInstUuid";
    private String REMOVE_BY_SUB_FLOW_INST_UUID = "delete from TaskSubFlow task_sub_flow1 where task_sub_flow1.flowInstUuid = :flowInstUuid";
    private String GET_OTHERS_BY_SUB_FLOW_INST_UUID = "from TaskSubFlow task_sub_flow1 where exists (select parentTaskInstUuid from TaskSubFlow task_sub_flow2 "
            + " where task_sub_flow1.parentTaskInstUuid = task_sub_flow2.parentTaskInstUuid and "
            + " task_sub_flow2.flowInstUuid = :flowInstUuid) and task_sub_flow1.flowInstUuid != :flowInstUuid";
    private String GET_BEHIND_FLOW_INST_BY_FRONT_FLOW_INST_UUID = "from FlowInstance flow_instance where exists ("
            + " select task_sub_flow_relation.uuid from TaskSubFlowRelation task_sub_flow_relation where flow_instance.uuid = task_sub_flow_relation.newFlowInstUuid "
            + " and task_sub_flow_relation.frontNewFlowInstUuid = :frontNewFlowInstUuid)";
    @Autowired
    private TaskSubFlowRelationService taskSubFlowRelationService;
    @Autowired
    private TaskInstanceService taskInstanceService;
    @Autowired
    private FlowInstanceService flowInstanceService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskOperationService taskOperationService;
    @Autowired
    private TaskInfoDistributionService taskInfoDistributionService;
    @Autowired
    private FlowDefinitionService flowDefinitionService;
    @Autowired
    private AclTaskService aclTaskService;
    //    @Autowired
//    private OrgApiFacade orgApiFacade;
    @Autowired
    private WorkflowOrgService workflowOrgService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private TaskFormAttachmentService taskFormAttachmentService;
    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    /**
     * 根据父流程实例UUID及子流程实例UUID，获取父流程任务实例
     * <p>
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#getParentTaskInstance(String, String)
     */
    @Override
    public TaskInstance getParentTaskInstance(String parentFlowInstUuid, String flowInstUuid) {
        TaskSubFlow taskSubFlow = new TaskSubFlow();
        taskSubFlow.setParentFlowInstUuid(parentFlowInstUuid);
        taskSubFlow.setFlowInstUuid(flowInstUuid);

        List<TaskSubFlow> list = this.dao.listByEntity(taskSubFlow);
        if (list.isEmpty() && list.size() != 1) {
            return null;
        }

        return this.taskInstanceService.get(list.get(0).getParentTaskInstUuid());
    }

    /**
     * 根据父流程实例UUID及子流程实例UUI，获取任务子流程信息
     * <p>
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#get(String, String)
     */
    @Override
    public TaskSubFlow get(String parentFlowInstUuid, String flowInstUuid) {
        TaskSubFlow taskSubFlow = new TaskSubFlow();
        taskSubFlow.setParentFlowInstUuid(parentFlowInstUuid);
        taskSubFlow.setFlowInstUuid(flowInstUuid);

        List<TaskSubFlow> list = this.dao.listByEntity(taskSubFlow);
        if (list.isEmpty() && list.size() != 1) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public boolean isShare(String parentFlowInstUuid, String flowInstUuid) {
        TaskSubFlow taskSubFlow = this.get(parentFlowInstUuid, flowInstUuid);
        boolean isShare = taskSubFlow == null ? false : BooleanUtils.isTrue(taskSubFlow.getIsShare());
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        return isShare ? isShare
                : UndertakeSituationUtils.isFollowUpUser(userDetails, taskSubFlow.getParentTaskId(),
                parentFlowInstUuid);
    }

    /**
     * 根据父流程实例UUID判断其是否异步执行
     * <p>
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#isAsync(String)
     */
    @Override
    public boolean isAsync(String parentFlowInstUuid, String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("parentFlowInstUuid", parentFlowInstUuid);
        values.put("flowInstUuid", flowInstUuid);
        values.put("isMerge", true);
        return (Long) this.dao.getNumberByHQL(COUNT, values) == 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#getAllByParentTaskInstUuid(String)
     */
    @Override
    public List<TaskSubFlow> getAllByParentTaskInstUuid(String parentTaskInstUuid) {
        TaskSubFlow example = new TaskSubFlow();
        example.setParentTaskInstUuid(parentTaskInstUuid);
        return this.dao.listByEntity(example);
    }

    /**
     * 根据父流程实例UUID获取所有子流程信息
     * <p>
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#getAllByParentFlowInstUuid(String)
     */
    @Override
    public List<TaskSubFlow> getAllByParentFlowInstUuid(String parentFlowInstUuid) {
        TaskSubFlow example = new TaskSubFlow();
        example.setParentFlowInstUuid(parentFlowInstUuid);
        return this.dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#getUndertakeSituationDatas(String, String, String)
     */
    @Override
    public List<FlowShareData> getUndertakeSituationDatas(String taskInstUuid, String flowInstUuid,
                                                          String parentFlowInstUuid, List<String> subFlowInstUuids, List<FlowShareDataQueryItem> shareDataQueryItems,
                                                          boolean needSearchAndSort, boolean isDefaultSort) {
        // 主流程定义
        FlowInstance parentFlowInstance = this.flowInstanceService.get(parentFlowInstUuid);
        FlowDefinition parentFlowDefinition = parentFlowInstance.getFlowDefinition();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(parentFlowDefinition);
        TaskInstance latestParentTaskInstance = taskService.getLastTaskInstanceByFlowInstUuid(parentFlowInstUuid);

        // 获取子流程办理信息
        if (!needSearchAndSort) {
            shareDataQueryItems = getShareDataQueryItems(parentFlowInstUuid, subFlowInstUuids);
        }
        if (CollectionUtils.isEmpty(shareDataQueryItems)) {
            return Collections.emptyList();
        }

        // 按上级环节实例UUID分组
        ImmutableListMultimap<String, FlowShareDataQueryItem> immutableListMultimap = Multimaps
                .index(shareDataQueryItems.iterator(), new Function<FlowShareDataQueryItem, String>() {

                    @Override
                    public String apply(FlowShareDataQueryItem flowShareItem) {
                        // 旧数据不存在分发状态，设置为1分发成功
                        if (flowShareItem.getDispatchState() == null) {
                            flowShareItem.setDispatchState(TaskSubFlowDispatch.STATUS_COMPLETED);
                        }
                        return flowShareItem.getBelongToTaskInstUuid();
                    }

                });
        ImmutableMap<String, Collection<FlowShareDataQueryItem>> immutableMap = immutableListMultimap.asMap();

        List<String> parentTaskUuids = Arrays.asList(immutableMap.keySet().toArray(new String[]{}));
        if (CollectionUtils.isEmpty(parentTaskUuids)) {
            return new ArrayList<>();
        }
        List<TaskInstance> parentTaskInstances = taskInstanceService.listByUuids(parentTaskUuids);
        Map<String, TaskInstance> parentTaskInstanceMap = ConvertUtils.convertElementToMap(parentTaskInstances,
                IdEntity.UUID);

        boolean isLatest = true;
        boolean isMajor = false;
        boolean isSupervise = false;
        boolean isMainProcess = flowInstUuid.equals(parentFlowInstUuid);
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        List<FlowShareData> shareDatas = Lists.newArrayList();
        List<CustomDynamicButton> buttons = Lists.newArrayListWithCapacity(0);
        for (Entry<String, Collection<FlowShareDataQueryItem>> entry : immutableMap.entrySet()) {
            String parentTaskInstUuid = entry.getKey();
            List<FlowShareDataQueryItem> queryItems = Lists.newArrayList();
            queryItems.addAll(entry.getValue());
            if (CollectionUtils.isEmpty(queryItems)) {
                continue;
            }

            TaskInstance parentTaskInstance = parentTaskInstanceMap.get(parentTaskInstUuid);
            String subTaskId = parentTaskInstance.getId();
            SubTaskNode subTaskNode = getSubTaskNode(flowDelegate, subTaskId);

            if (isDefaultSort) {
                Collections.sort(queryItems, UndertakeSituationUtils.getFlowShareDataQueryItemComparator(subTaskNode));
            }
            FlowShareData flowShareData = new FlowShareData();
            flowShareData.setBusinessType(subTaskNode.getBusinessType());
            flowShareData.setBusinessRole(subTaskNode.getBusinessRole());

            // 设置发起流程的父流程信息
            if (CollectionUtils.isNotEmpty(queryItems)) {
                FlowShareDataQueryItem flowShareItem = queryItems.iterator().next();
                flowShareData.setBelongToTaskId(flowShareItem.getBelongToTaskId());
                flowShareData.setBelongToTaskInstUuid(flowShareItem.getBelongToTaskInstUuid());
                flowShareData.setBelongToFlowInstUuid(flowShareItem.getBelongToFlowInstUuid());
            }

            // 最新办理阶段
            if (isLatest) {
                if (needSearchAndSort) {
                    TaskSubFlow taskSubFlow = this.get(parentFlowInstUuid, flowInstUuid);
                    if (taskSubFlow != null && Boolean.TRUE.equals(taskSubFlow.getIsMajor())) {
                        isMajor = UndertakeSituationUtils.isMajorUser(user, taskInstUuid);
                    }
                } else {
                    isMajor = UndertakeSituationUtils.isMajorUser(user, taskInstUuid, queryItems);
                }

                isSupervise = UndertakeSituationUtils.isFollowUpUser(user, subTaskId, parentFlowInstUuid);
                // 跟踪办理环节
                if (!isSupervise) {
                    String traceId = subTaskNode.getTraceTask();
                    if (StringUtils.equals(latestParentTaskInstance.getId(), traceId)) {
                        isSupervise = taskService.hasTodoPermission(user, latestParentTaskInstance.getUuid());
                    }
                }
                flowShareData.setMajor(isMajor);
                flowShareData.setSupervise(isSupervise);
                flowShareData.setOver(false);
            } else {
                flowShareData.setOver(true);
            }

            // 办理进度标题
            flowShareData.setTitle(getUndertakeSituationTitle(subTaskNode, flowDelegate, parentTaskInstance,
                    parentFlowInstance, parentFlowDefinition));
            // 分发时间 承办情况列表排序用到
            if (immutableMap != null && immutableMap.size() > 0 && immutableMap.get(flowShareData.getBelongToTaskInstUuid()) != null &&
                    immutableMap.get(flowShareData.getBelongToTaskInstUuid()).size() > 0) {
                List<FlowShareDataQueryItem> flowShareDataQueryItems = ImmutableList.copyOf(immutableMap.get(flowShareData.getBelongToTaskInstUuid()));
                //子流程未分发完成
                if (flowShareDataQueryItems.get(0).getStartTime() == null) {
                    flowShareData.setDistributeTime(new Date());
                } else {
                    flowShareData.setDistributeTime(flowShareDataQueryItems.get(0).getStartTime());
                }
            } else {
                flowShareData.setDistributeTime(parentFlowInstance.getStartTime());
            }

            // 操作按钮，主办、跟进人员、跟踪办理环节才有操作按钮
            if (isLatest && (isMajor || isSupervise)) {
                buttons = UndertakeSituationUtils.getUndertakeSituationButtons(parentFlowInstance,
                        subTaskNode, isMajor, isSupervise, isMainProcess);
            }
            // 列配置
            flowShareData.setColumns(UndertakeSituationUtils.getUndertakeSituationColumns(subTaskNode));

            // 办理信息
            List<FlowShareRowData> shareItems = getFlowShareItems(flowShareData.getColumns(), queryItems, subTaskNode,
                    flowDelegate, user, isMajor, isSupervise, isLatest, needSearchAndSort);
            flowShareData.setShareItems(shareItems);

            shareDatas.add(flowShareData);
            isLatest = false;
        }
        // 处理null字符串
        if (shareDatas.size() > 0) {
            for (FlowShareData shareData : shareDatas) {
                for (FlowShareRowData shareItem : shareData.getShareItems()) {
                    for (CustomDynamicColumnValue columnValue : shareItem.getColumnValues()) {
                        if (columnValue.getIndex() != null && columnValue.getIndex().indexOf("extraColumn") > -1) {
                            if (columnValue.getValue() == null) {
                                columnValue.setValue("");
                            }
                        }
                    }
                }
            }
        }

        if (CollectionUtils.isNotEmpty(shareDatas)) {
            shareDatas.sort(Comparator.naturalOrder());// 按实体类的顺序来：倒序
            shareDatas.get(0).setButtons(buttons);
        }

        return shareDatas;
    }

    @Override
    public List<FlowShareData> getUndertakeSituationTitles(String taskInstUuid, String flowInstUuid,
                                                           String parentFlowInstUuid, List<String> subFlowInstUuids, List<FlowShareDataQueryItem> shareDataQueryItems,
                                                           boolean needSearchAndSort, boolean isDefaultSort) {
        // 主流程定义
        FlowInstance parentFlowInstance = this.flowInstanceService.get(parentFlowInstUuid);
        FlowDefinition parentFlowDefinition = parentFlowInstance.getFlowDefinition();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(parentFlowDefinition);
        TaskInstance latestParentTaskInstance = taskService.getLastTaskInstanceByFlowInstUuid(parentFlowInstUuid);

        // 获取子流程办理信息
        if (!needSearchAndSort) {
            shareDataQueryItems = getShareDataQueryItems(parentFlowInstUuid, subFlowInstUuids);
        }
        if (CollectionUtils.isEmpty(shareDataQueryItems)) {
            return Collections.emptyList();
        }

        // 按上级环节实例UUID分组
        ImmutableListMultimap<String, FlowShareDataQueryItem> immutableListMultimap = Multimaps
                .index(shareDataQueryItems.iterator(), new Function<FlowShareDataQueryItem, String>() {

                    @Override
                    public String apply(FlowShareDataQueryItem flowShareItem) {
                        // 旧数据不存在分发状态，设置为1分发成功
                        if (flowShareItem.getDispatchState() == null) {
                            flowShareItem.setDispatchState(TaskSubFlowDispatch.STATUS_COMPLETED);
                        }
                        return flowShareItem.getBelongToTaskInstUuid();
                    }

                });
        ImmutableMap<String, Collection<FlowShareDataQueryItem>> immutableMap = immutableListMultimap.asMap();

        List<String> parentTaskUuids = Arrays.asList(immutableMap.keySet().toArray(new String[]{}));
        if (CollectionUtils.isEmpty(parentTaskUuids)) {
            return new ArrayList<>();
        }
        List<TaskInstance> parentTaskInstances = taskInstanceService.listByUuids(parentTaskUuids);
        Map<String, TaskInstance> parentTaskInstanceMap = ConvertUtils.convertElementToMap(parentTaskInstances,
                IdEntity.UUID);

        boolean isLatest = true;
        boolean isMajor = false;
        boolean isSupervise = false;
        boolean isMainProcess = flowInstUuid.equals(parentFlowInstUuid);
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        List<FlowShareData> shareDatas = Lists.newArrayList();
        for (Entry<String, Collection<FlowShareDataQueryItem>> entry : immutableMap.entrySet()) {
            String parentTaskInstUuid = entry.getKey();
            List<FlowShareDataQueryItem> queryItems = Lists.newArrayList();
            queryItems.addAll(entry.getValue());
            if (CollectionUtils.isEmpty(queryItems)) {
                continue;
            }

            TaskInstance parentTaskInstance = parentTaskInstanceMap.get(parentTaskInstUuid);
            String subTaskId = parentTaskInstance.getId();
            SubTaskNode subTaskNode = getSubTaskNode(flowDelegate, subTaskId);

            if (isDefaultSort) {
                Collections.sort(queryItems, UndertakeSituationUtils.getFlowShareDataQueryItemComparator(subTaskNode));
            }
            FlowShareData flowShareData = new FlowShareData();
            flowShareData.setBusinessType(subTaskNode.getBusinessType());
            flowShareData.setBusinessRole(subTaskNode.getBusinessRole());

            // 设置发起流程的父流程信息
            if (CollectionUtils.isNotEmpty(queryItems)) {
                FlowShareDataQueryItem flowShareItem = queryItems.iterator().next();
                flowShareData.setBelongToTaskId(flowShareItem.getBelongToTaskId());
                flowShareData.setBelongToTaskInstUuid(flowShareItem.getBelongToTaskInstUuid());
                flowShareData.setBelongToFlowInstUuid(flowShareItem.getBelongToFlowInstUuid());
            }

            // 最新办理阶段
            if (isLatest) {
                if (needSearchAndSort) {
                    TaskSubFlow taskSubFlow = this.get(parentFlowInstUuid, flowInstUuid);
                    if (taskSubFlow != null && Boolean.TRUE.equals(taskSubFlow.getIsMajor())) {
                        isMajor = UndertakeSituationUtils.isMajorUser(user, taskInstUuid);
                    }
                } else {
                    isMajor = UndertakeSituationUtils.isMajorUser(user, taskInstUuid, queryItems);
                }

                isSupervise = UndertakeSituationUtils.isFollowUpUser(user, subTaskId, parentFlowInstUuid);
                // 跟踪办理环节
                if (!isSupervise) {
                    String traceId = subTaskNode.getTraceTask();
                    if (StringUtils.equals(latestParentTaskInstance.getId(), traceId)) {
                        isSupervise = taskService.hasTodoPermission(user, latestParentTaskInstance.getUuid());
                    }
                }
                flowShareData.setMajor(isMajor);
                flowShareData.setSupervise(isSupervise);
                flowShareData.setOver(false);
            } else {
                flowShareData.setOver(true);
            }

            // 办理进度标题
            flowShareData.setTitle(getUndertakeSituationTitle(subTaskNode, flowDelegate, parentTaskInstance,
                    parentFlowInstance, parentFlowDefinition));
            // 分发时间 承办情况列表排序用到
            if (immutableMap != null && immutableMap.size() > 0 && immutableMap.get(flowShareData.getBelongToTaskInstUuid()) != null &&
                    immutableMap.get(flowShareData.getBelongToTaskInstUuid()).size() > 0) {
                List<FlowShareDataQueryItem> flowShareDataQueryItems = ImmutableList.copyOf(immutableMap.get(flowShareData.getBelongToTaskInstUuid()));
                //子流程未分发完成
                if (flowShareDataQueryItems.get(0).getStartTime() == null) {
                    flowShareData.setDistributeTime(new Date());
                } else {
                    flowShareData.setDistributeTime(flowShareDataQueryItems.get(0).getStartTime());
                }
            } else {
                flowShareData.setDistributeTime(parentFlowInstance.getStartTime());
            }


            // 操作按钮，主办、跟进人员、跟踪办理环节才有操作按钮
            if (isLatest && (isMajor || isSupervise)) {
                flowShareData.setButtons(UndertakeSituationUtils.getUndertakeSituationButtons(parentFlowInstance,
                        subTaskNode, isMajor, isSupervise, isMainProcess));
            }
            // 列配置
            flowShareData.setColumns(UndertakeSituationUtils.getUndertakeSituationColumns(subTaskNode));

            // 办理信息
            // List<FlowShareRowData> shareItems =
            // getFlowShareItems(flowShareData.getColumns(), queryItems, subTaskNode,
            // flowDelegate, user, isMajor, isSupervise, isLatest, needSearchAndSort);
            // flowShareData.setShareItems(shareItems);

            shareDatas.add(flowShareData);
            isLatest = false;
        }
        if (CollectionUtils.isNotEmpty(shareDatas)) {
            shareDatas.sort(Comparator.naturalOrder());//按实体类的顺序来：倒序
        }

        return shareDatas;
    }

    @Override
    public List<FlowShareData> getUndertakeSituationDatas(SubTaskData subTaskData) {
        List<FlowShareData> flowShareDatas = Lists.newArrayList();
        boolean isParentFlowInstance = subTaskData.isParentFlowInstance();
        boolean isChildFlowInstance = subTaskData.isChildFlowInstance();
        String undertakeSituationPlaceHolder = subTaskData.getUndertakeSituationPlaceHolder();
        String taskInstUuid = subTaskData.getTaskInstUuid();
        String flowInstUuid = subTaskData.getFlowInstUuid();
        String parentFlowInstUuid = subTaskData.getParentFlowInstUuid();
        List<String> subFlowInstUuids = subTaskData.getSubFlowInstUuids();
        // <显示位置，承办信息列表>
        Map<String, List<FlowShareData>> shareDatas = Maps.newHashMapWithExpectedSize(0);
        if (!isParentFlowInstance && isChildFlowInstance) {
            // 承办信息
            if (StringUtils.isNotBlank(undertakeSituationPlaceHolder)) {
                // 作为子流程
                flowShareDatas = getUndertakeSituationTitles(taskInstUuid, flowInstUuid, parentFlowInstUuid,
                        subFlowInstUuids, null, false, true);
            }
        } else {
            // 作为主流程
            // 承办信息
            if (StringUtils.isNotBlank(undertakeSituationPlaceHolder)) {
                flowShareDatas = getUndertakeSituationTitles(taskInstUuid, flowInstUuid, flowInstUuid, subFlowInstUuids,
                        null, false, true);
                shareDatas.put(undertakeSituationPlaceHolder, flowShareDatas);
            }
            // 同时作为子流程
            if (isChildFlowInstance) {
                // 承办信息
                if (StringUtils.isNotBlank(subTaskData.getAsChildUndertakeSituationPlaceHolder())) {
                    List<FlowShareData> childShareDatas = getUndertakeSituationTitles(taskInstUuid, flowInstUuid,
                            parentFlowInstUuid, subFlowInstUuids, null, false, true);
                    if (shareDatas.containsKey(subTaskData.getAsChildUndertakeSituationPlaceHolder())) {
                        shareDatas.get(subTaskData.getAsChildUndertakeSituationPlaceHolder()).addAll(childShareDatas);
                        // 按分发时间降序
                        Collections.sort(shareDatas.get(subTaskData.getAsChildUndertakeSituationPlaceHolder()));
                        flowShareDatas = shareDatas.get(subTaskData.getAsChildUndertakeSituationPlaceHolder());
                    } else {
                        flowShareDatas = childShareDatas;
                    }
                }
            }
        }
        return flowShareDatas;
    }

    @Override
    public List<FlowShareData> getUndertakeSituationDatasAll(SubTaskData subTaskData) {
        List<FlowShareData> flowShareDatas = Lists.newArrayList();
        boolean isParentFlowInstance = subTaskData.isParentFlowInstance();
        boolean isChildFlowInstance = subTaskData.isChildFlowInstance();
        String undertakeSituationPlaceHolder = subTaskData.getUndertakeSituationPlaceHolder();
        String taskInstUuid = subTaskData.getTaskInstUuid();
        String flowInstUuid = subTaskData.getFlowInstUuid();
        String parentFlowInstUuid = subTaskData.getParentFlowInstUuid();
        List<String> subFlowInstUuids = subTaskData.getSubFlowInstUuids();
        // <显示位置，承办信息列表>
        Map<String, List<FlowShareData>> shareDatas = Maps.newHashMapWithExpectedSize(0);
        if (!isParentFlowInstance && isChildFlowInstance) {
            // 承办信息
            if (StringUtils.isNotBlank(undertakeSituationPlaceHolder)) {
                // 作为子流程
                flowShareDatas = getUndertakeSituationDatas(taskInstUuid, flowInstUuid, parentFlowInstUuid,
                        subFlowInstUuids, null, false, true);
            }
        } else {
            // 作为主流程
            // 承办信息
            if (StringUtils.isNotBlank(undertakeSituationPlaceHolder)) {
                flowShareDatas = getUndertakeSituationDatas(taskInstUuid, flowInstUuid, flowInstUuid, subFlowInstUuids,
                        null, false, true);
                shareDatas.put(undertakeSituationPlaceHolder, flowShareDatas);
            }
            // 同时作为子流程
            if (isChildFlowInstance) {
                // 承办信息
                if (StringUtils.isNotBlank(subTaskData.getAsChildUndertakeSituationPlaceHolder())) {
                    List<FlowShareData> childShareDatas = getUndertakeSituationDatas(taskInstUuid, flowInstUuid,
                            parentFlowInstUuid, subFlowInstUuids, null, false, true);
                    if (shareDatas.containsKey(subTaskData.getAsChildUndertakeSituationPlaceHolder())) {
                        shareDatas.get(subTaskData.getAsChildUndertakeSituationPlaceHolder()).addAll(childShareDatas);
                        // 按分发时间降序
                        Collections.sort(shareDatas.get(subTaskData.getAsChildUndertakeSituationPlaceHolder()));
                        flowShareDatas = shareDatas.get(subTaskData.getAsChildUndertakeSituationPlaceHolder());
                    } else {
                        flowShareDatas = childShareDatas;
                    }
                }
            }
        }
        return flowShareDatas;
    }

    @Override
    public List<FlowShareData> getUndertakeSituationDatasByPage(SubTaskDataVo vo) {
        // 主流程定义
        FlowInstance parentFlowInstance = flowInstanceService.get(vo.getParentFlowInstUuid());
        FlowDefinition parentFlowDefinition = parentFlowInstance.getFlowDefinition();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(parentFlowDefinition);
        // TaskInstance taskInstance = taskService.getTask(vo.getTaskInstUuid());
        // //获取子流程信息
        // List<TaskElement> tasks = flowDelegate.getFlow().getTasks();
        // SubTaskElement subTask = null;
        // for (TaskElement task : tasks) {
        // if (task.getId().equals(taskInstance.getId())&&task instanceof
        // SubTaskElement){//如果是当前节点实例且为子流程实例
        // subTask = (SubTaskElement) task;
        // }
        // }
        // List<ColumnElement> undertakeSituationColumns = null;
        // if (subTask == null){
        //
        // undertakeSituationColumns = subTaskNode.getUndertakeSituationColumns();
        // }else{
        // undertakeSituationColumns = subTask.getUndertakeSituationColumns();
        // }
        TaskInstance taskInstance = taskService.getTask(vo.getTaskInstUuid());
        SubTaskNode subTaskNode = getSubTaskNode(flowDelegate, taskInstance.getId());
        if (subTaskNode == null) {
            TaskInstance latestParentTaskInstance = taskService
                    .getLastTaskInstanceByFlowInstUuid(vo.getParentFlowInstUuid());
            subTaskNode = getSubTaskNode(flowDelegate, latestParentTaskInstance.getId());
        }
        if (subTaskNode == null) {
            TaskInstance taskInstance2 = taskService.getTask(vo.getBelongToTaskInstUuid());
            subTaskNode = getSubTaskNode(flowDelegate, taskInstance2.getId());
        }
        // taskInstance.get
        // 获取表字段及扩展字段流程信息
        StringBuffer keywordSql = new StringBuffer();
        Map<String, List<String>> childSelectMap = new HashMap<>();
        Map<String, List<String>> extraMap;
        if (subTaskNode == null) {
            extraMap = getExtraColumnMap(keywordSql, Lists.newArrayList(), childSelectMap);
        } else {
            extraMap = getExtraColumnMap(keywordSql, subTaskNode.getUndertakeSituationColumns(), childSelectMap);
        }
        StringBuffer leftJoinSql = new StringBuffer();
        // 根据流程定义Id获取流程定义
        // 获取流程定义表
        Set<String> flowDefIdSet = Sets.newHashSet();
        for (String flowDefId : extraMap.keySet()) {
            flowDefIdSet.add(flowDefId);
        }


        List<FlowDefinition> flowDefinitions = flowDefinitionService.getByIds(flowDefIdSet);
        // key :flowDefId
        Map<String, FlowDefinition> flowDefinitionMap = getFlowDefinitionMap(flowDefinitions);
        Set<String> formUuids = getFormUuids(flowDefinitions);
        List<DyFormFormDefinition> formDefinitions = dyFormFacade.getFormDefinitionByFormUuids(formUuids);
        // key :FormUuid
        Map<String, FormDefinition> formDefinitionMap = getFormDefinitionMap(formDefinitions);
        for (String flowDefId : extraMap.keySet()) {
            FlowDefinition flowDefinition = flowDefinitionMap.get(flowDefId);// 获取最新版本定义
            FormDefinition formDefinition = formDefinitionMap.get(flowDefinition.getFormUuid());
            List<String> colmunList = childSelectMap.get(flowDefId);
            String configColumnString = StringUtils.join(colmunList, ",");
            for (int i = 0; i < 10; i++) {
                if (StringUtils.contains(configColumnString, "extraColumn" + i)) {
                    continue;
                }
                colmunList.add(" '' as extraColumn" + i);
            }
            if (leftJoinSql.length() == 0) {
                leftJoinSql.append(" select uuid," + StringUtils.join(colmunList, ",") + " from "
                        + formDefinition.getTableName()
                        + "  WHERE uuid in (select wfi.data_uuid from wf_flow_instance wfi WHERE wfi.parent_flow_inst_uuid = :parent_flow_inst_uuid ) ");
            } else {
                leftJoinSql.append(" union all select tab1.uuid," + StringUtils.join(colmunList, ",") + " from "
                        + formDefinition.getTableName()
                        + " tab1  WHERE tab1.uuid in (select wfi.data_uuid from wf_flow_instance wfi WHERE wfi.parent_flow_inst_uuid = :parent_flow_inst_uuid ) ");
            }
        }
        String orderBySql = orderBySql(vo.getOrders(), subTaskNode);
        if (StringUtils.isBlank(orderBySql) && subTaskNode != null && "custom".equals(subTaskNode.getSortRule())) {
            orderBySql = orderBySql(subTaskNode.getUndertakeSituationOrders(),
                    subTaskNode.getUndertakeSituationColumns());
        }

        // 关联流程定义表查询数据
        List<FlowShareData> newResult = new ArrayList<>();
        queryFlowShareData(subTaskNode, vo, leftJoinSql.toString(), keywordSql.toString(), orderBySql, newResult);
        return newResult;
    }

    /**
     * @param subTaskNode
     * @param vo
     * @param leftJoinSql
     * @param keywordSql
     * @param orderBySql
     * @param newResult
     */
    private void queryFlowShareData(SubTaskNode subTaskNode, SubTaskDataVo vo, String leftJoinSql, String keywordSql,
                                    String orderBySql, List<FlowShareData> newResult) {
        List<Page<FlowShareDataQueryItem>> itemPages = getShareDataQueryItems(vo, leftJoinSql,
                keywordSql, orderBySql);
        for (Page<FlowShareDataQueryItem> page : itemPages) {
            // 构建返回数据
            List<FlowShareData> result;
            if (subTaskNode == null) {
                result = getUndertakeSituationDatas(vo.getTaskInstUuid(), vo.getFlowInstUuid(),
                        vo.getParentFlowInstUuid(), vo.getSubFlowInstUuids(), page.getResult(), true,
                        Boolean.TRUE);
            } else {
                result = getUndertakeSituationDatas(vo.getTaskInstUuid(), vo.getFlowInstUuid(),
                        vo.getParentFlowInstUuid(), vo.getSubFlowInstUuids(), page.getResult(), true,
                        CollectionUtils.isEmpty(vo.getOrders()) && !"custom".equals(subTaskNode.getSortRule()));
            }
            FlowShareData shareData = new FlowShareData();
            if (CollectionUtils.isNotEmpty(result)) {
                shareData = result.get(0);
                // 没有权限查看数据时且存在更多的数据，获取下一页数据
                if (CollectionUtils.isEmpty(shareData.getShareItems()) && CollectionUtils.isNotEmpty(page.getResult())
                        && page.isHasNext()) {
                    vo.setPageNum(vo.getPageNum() + 1);
                    queryFlowShareData(subTaskNode, vo, leftJoinSql, keywordSql, orderBySql, newResult);
                    newResult.forEach(item -> {
                        item.setTotalCount(Long.valueOf(CollectionUtils.size(item.getShareItems())));
                    });
                    continue;
                }
            }
            shareData.setPageNo(vo.getPageNum());
            shareData.setPageSize(vo.getPageSize());
            shareData.setTotalCount(page.getTotalCount());
            newResult.add(shareData);
        }
    }

    /**
     * 方法描述
     *
     * @return
     * @author baozh
     * @date 2022/1/4 20:22
     * @params 流程uuid->字段列表
     */
    private Map<String, List<String>> getExtraColumnMap(StringBuffer whereSql, List<ColumnElement> extendColumns,
                                                        Map<String, List<String>> childSelectMap) {
        UndertakeSituationUtils.getIndexEqualExtraColumn(extendColumns);
        List<String> excludeField = new ArrayList<>();
        excludeField.add("resultFiles");
        excludeField.add("remainingTime");
        excludeField.add("workProcesses");
        Map<String, List<String>> extraColumnMap = new HashMap<>();
        Integer trueInteger = 1;
        int i = 0;
        // 扩展列数据
        for (ColumnElement columnElement : extendColumns) {
            String sources = columnElement.getSources();
            if (StringUtils.isNotBlank(sources)) {
                String field = columnElement.getIndex();// "extraColumn" + i;
                // columnElement.setIndex(field);
                String[] subflowSources = StringUtils.split(sources, Separator.SEMICOLON.getValue());
                for (String subflowSource : subflowSources) {
                    String[] fieldInfos = StringUtils.split(subflowSource, Separator.COMMA.getValue());
                    String subflowDefId = fieldInfos[0];
                    String fieldName = fieldInfos[1];
                    if (!extraColumnMap.containsKey(subflowDefId)) {
                        extraColumnMap.put(subflowDefId, new ArrayList<>());
                        childSelectMap.put(subflowDefId, new ArrayList<>());
                    }
                    // field = "extraColumn" + extraColumnMap.get(subflowDefId).size();
                    if (trueInteger.equals(columnElement.getSearchFlag())) {
                        if (whereSql.length() > 0) {
                            whereSql.append(" or ");
                        }
                        whereSql.append(" " + field + " like :keyword");
                    }
                    List<String> columnList = childSelectMap.get(subflowDefId);
                    if (i > columnList.size()) {
                        for (int j = columnList.size(); j < i; j++) {
                            childSelectMap.get(subflowDefId).add(" '' as extraColumn" + j);
                        }
                    }
                    columnList.add(fieldName + " as " + field);
                    extraColumnMap.get(subflowDefId).add(field);
                }
            } else {
                SortFieldEnum sortField = SortFieldEnum.getSortField(columnElement.getIndex());
                String index = sortField == null ? columnElement.getIndex() : sortField.getField();
                if (!excludeField.contains(index) && trueInteger.equals(columnElement.getSearchFlag())) {
                    if (whereSql.length() > 0) {
                        whereSql.append(" or ");
                    }
                    whereSql.append(" " + index + " like :keyword");
                }
            }
        }
        return extraColumnMap;
    }


    private String orderBySql(List<DataStoreOrder> orders, SubTaskNode subTaskNode) {
        if (CollectionUtils.isEmpty(orders) || subTaskNode == null) {
            return null;
        }

        List<ColumnElement> extendColumns = subTaskNode.getUndertakeSituationColumns();
        UndertakeSituationUtils.getIndexEqualExtraColumn(extendColumns);
        Map<String, String> extendMap = extendColumns.stream()
                .filter(element -> StringUtils.isNotBlank(element.getExtraColumn()))
                .collect(Collectors.toMap(ColumnElement::getExtraColumn, ColumnElement::getIndex));
        List<String> result = new ArrayList<>();
        orders.forEach(order -> {
            if (extendMap.containsKey(order.getSortName())) {
                result.add(extendMap.get(order.getSortName()) + " " + order.getSortOrder());
            }
        });
        if (result.size() > 0) {
            return "order by " + StringUtils.join(result, ",");
        }
        return null;
    }

    /**
     * 方法描述
     *
     * @return
     * @author baozh
     * @date 2022/1/4 20:22
     * @params 流程->字段列表
     */
    private String orderBySql(List<OrderElement> undertakeSituationOrders, List<ColumnElement> extendColumns) {
        UndertakeSituationUtils.getIndexEqualExtraColumn(extendColumns);
        Map<String, String> extendMap = extendColumns.stream()
                .filter(element -> StringUtils.isNotBlank(element.getExtraColumn()))
                .collect(Collectors.toMap(ColumnElement::getExtraColumn, ColumnElement::getIndex));

        List<String> result = new ArrayList<>();
        for (OrderElement orderElement : undertakeSituationOrders) {
            SortFieldEnum sortField = SortFieldEnum.getSortField(orderElement.getName());
            String field = sortField == null ? orderElement.getName() : sortField.getField();
            if (extendMap.containsKey(field)) {
                field = extendMap.get(field);
            }
            if (StringUtils.isBlank(field)) {
                continue;
            }
            result.add(field + " " + orderElement.getDirection());
        }
        if (result.size() > 0) {
            return "order by " + StringUtils.join(result, ",");
        }

        return null;
    }

    /**
     * @param subTaskNode
     * @param flowDelegate
     * @param parentTaskInstance
     * @param parentFlowInstance
     * @return
     */
    private String getUndertakeSituationTitle(SubTaskNode subTaskNode, FlowDelegate flowDelegate,
                                              TaskInstance parentTaskInstance, FlowInstance parentFlowInstance, FlowDefinition parentFlowDefinition) {
        String undertakeSituationTitleExpression = subTaskNode.getUndertakeSituationTitleExpression();
        String title = TitleExpressionUtils.getUndertakeSituationTitleExpression(undertakeSituationTitleExpression,
                flowDelegate, parentTaskInstance, parentFlowInstance, parentFlowDefinition);
        return title;
    }

    /**
     * @param columns
     * @param shareDataQueryItems
     * @param subTaskNode
     * @return
     */
    private List<FlowShareRowData> getFlowShareItems(List<CustomDynamicColumn> columns,
                                                     Collection<FlowShareDataQueryItem> shareDataQueryItems, SubTaskNode subTaskNode, FlowDelegate flowDelegate,
                                                     UserDetails user, boolean isMajor, boolean isSupervise, boolean isLatest, boolean needSearchAndSort) {
        // 组装数据
        List<FlowShareItem> flowShareItems = Lists.newArrayList();
        // 扩展列
        List<ColumnElement> extendColumns = UndertakeSituationUtils.getExtendColumns(subTaskNode);
        if (CollectionUtils.isNotEmpty(extendColumns)) {
            for (ColumnElement extendColumn : extendColumns) {
                if (StringUtils.isNotBlank(extendColumn.getIndex())
                        && !extendColumn.getIndex().equals(extendColumn.getExtraColumn())) {
                    extendColumn.setIndex(extendColumn.getExtraColumn());
                }
            }
        }
        // 扩展列的表单数据
        Map<String, DyFormData> subflowDyformDataMap = Maps.newHashMap();
        // 固定列数据
        // key:flowInstUuid
        Map<String, List<TaskFormAttachment>> taskFormAttachmentMap = getTaskFormAttachmentMap(shareDataQueryItems);
        for (FlowShareDataQueryItem shareDataQueryItem : shareDataQueryItems) {
            // 流程数据是否共享
            if (Boolean.TRUE.equals(shareDataQueryItem.getIsShare())) {
            } else if (isSupervise) {
                // 子流程分发中或跟踪人员权限可见全部子流程实例
            } else if (StringUtils.isBlank(shareDataQueryItem.getTaskInstUuid())
                    || taskService.hasViewPermission(user, shareDataQueryItem.getTaskInstUuid())) {
                // 当前子流程实例的办理人可见
            } else {
                continue;
            }

            FlowShareItem flowShareItem = new FlowShareItem();
            BeanUtils.copyProperties(shareDataQueryItem, flowShareItem);

            // 子流程配置信息
            NewFlow newFlow = getNewFlow(flowDelegate, shareDataQueryItem.getBelongToTaskId(),
                    shareDataQueryItem.getFlowDefId(), shareDataQueryItem.getIsMajor());
            if (newFlow == null) {
                newFlow = getNewFlow(flowDelegate, shareDataQueryItem.getBelongToTaskId(),
                        shareDataQueryItem.getFlowDefId());
            }
            // 承办部门
            UndertakeSituationUtils.setTodoName(shareDataQueryItem, flowShareItem, newFlow);

            // 设置当前环节名称
            UndertakeSituationUtils.setCurrentTaskName(shareDataQueryItem, flowShareItem);

            // 办理时限
            UndertakeSituationUtils.setDueTimeFormatString(shareDataQueryItem, flowShareItem);

            // 剩余时限
            UndertakeSituationUtils.setRemainingTime(shareDataQueryItem, flowShareItem, isLatest);

            // 办理附件
            UndertakeSituationUtils.setResultFiles(shareDataQueryItem, flowShareItem, taskFormAttachmentMap);

            // 扩展列数据
            for (ColumnElement columnElement : extendColumns) {
                String sources = columnElement.getSources();
                if (StringUtils.isNotBlank(sources)) {
                    // if (!needSearchAndSort) {
                    String[] subflowSources = StringUtils.split(sources, Separator.SEMICOLON.getValue());
                    boolean matchSource = false;
                    for (String subflowSource : subflowSources) {
                        String[] fieldInfos = StringUtils.split(subflowSource, Separator.COMMA.getValue());
                        String subflowDefId = fieldInfos[0];
                        String fieldName = fieldInfos[1];
                        if (StringUtils.equals(flowShareItem.getFlowDefId(), subflowDefId)) {
                            matchSource = true;
                            Object fieldValue = UndertakeSituationUtils.getFieldValue(fieldName, flowShareItem,
                                    subflowDyformDataMap);
                            if (fieldValue != null) {
                                ((CustomDynamicColumnValue) fieldValue).setType(columnElement.getType());
                                ((CustomDynamicColumnValue) fieldValue).setIndex(columnElement.getIndex());
                            }
                            flowShareItem.getExtras().put(columnElement.getIndex(),
                                    fieldValue == null ? "" : fieldValue);
                            break;
                        }
                    }
                    if (BooleanUtils.isNotTrue(matchSource)) {
                        try {
                            CustomDynamicColumnValue columnValue = new CustomDynamicColumnValue();
                            columnValue.setValue(ReflectionUtils.getFieldValue(shareDataQueryItem, columnElement.getIndex()));
                            columnValue.setType(columnElement.getType());
                            columnValue.setIndex(columnElement.getIndex());
                            flowShareItem.getExtras().put(columnElement.getIndex(),
                                    columnValue == null ? "" : columnValue);
                        } catch (Exception e) {
                        }
                    }
                    if (flowShareItem.getExtras().get(columnElement.getIndex()) == null) {
                        // 没有配置扩展字段对应数据 -> 默认空值
                        flowShareItem.getExtras().put(columnElement.getIndex(),
                                new CustomDynamicColumnValue("1", columnElement.getIndex(), ""));
                    }
                    // }else{
                    // flowShareItem.getExtras().put(columnElement.getIndex(), new
                    // CustomDynamicColumnValue("1", columnElement.getIndex(),
                    // getExtraValue(columnElement,shareDataQueryItem)));
                    // }
                }
            }
            flowShareItems.add(flowShareItem);
        }

        // 转化为统一处理的列表数据
        List<FlowShareRowData> valueList = Lists.newArrayList();
        for (FlowShareItem flowShareItem : flowShareItems) {
            valueList.add(UndertakeSituationUtils.convertRowData(flowShareItem, columns));
        }

        return valueList;
    }

    private Object getExtraValue(ColumnElement columnElement, FlowShareDataQueryItem shareDataQueryItem) {
        String result = null;
        switch (columnElement.getIndex()) {
            case "extraColumn0":
                result = Objects.toString(shareDataQueryItem.getExtraColumn0(), StringUtils.EMPTY);
                break;
            case "extraColumn1":
                result = Objects.toString(shareDataQueryItem.getExtraColumn1(), StringUtils.EMPTY);
                break;
            case "extraColumn2":
                result = Objects.toString(shareDataQueryItem.getExtraColumn2(), StringUtils.EMPTY);
                break;
            case "extraColumn3":
                result = Objects.toString(shareDataQueryItem.getExtraColumn3(), StringUtils.EMPTY);
                break;
            case "extraColumn4":
                result = Objects.toString(shareDataQueryItem.getExtraColumn4(), StringUtils.EMPTY);
                break;
            case "extraColumn5":
                result = Objects.toString(shareDataQueryItem.getExtraColumn5(), StringUtils.EMPTY);
                break;
            case "extraColumn6":
                result = Objects.toString(shareDataQueryItem.getExtraColumn6(), StringUtils.EMPTY);
                break;
            case "extraColumn7":
                result = Objects.toString(shareDataQueryItem.getExtraColumn7(), StringUtils.EMPTY);
                break;
            case "extraColumn8":
                result = Objects.toString(shareDataQueryItem.getExtraColumn8(), StringUtils.EMPTY);
                break;
            case "extraColumn9":
                result = Objects.toString(shareDataQueryItem.getExtraColumn9(), StringUtils.EMPTY);
                break;
            default:
                result = "";
        }
        if (result == null)
            result = "";
        return result;
    }

    /**
     * @param flowDelegate
     * @param subTaskId
     * @return
     */
    private SubTaskNode getSubTaskNode(FlowDelegate flowDelegate, String subTaskId) {
        Node node = flowDelegate.getTaskNode(subTaskId);
        if (node instanceof SubTaskNode) {
            return (SubTaskNode) node;
        }
        return null;
    }

    private List<Page<FlowShareDataQueryItem>> getShareDataQueryItems(SubTaskDataVo vo, String leftJoinSql,
                                                                      String keywordSql, String orderBySql) {
        Page<FlowShareDataQueryItem> page = new Page<>();
        PagingInfo pagingInfo = new PagingInfo(vo.getPageNum(), vo.getPageSize());
        // 作为父流程实例UUID获取数据
        TaskSubFlow example = new TaskSubFlow();
        example.setParentFlowInstUuid(vo.getParentFlowInstUuid());
        List<TaskSubFlow> list = listByEntity(example);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("suspension_state", SuspensionState.NORMAL.getState());
        values.put("subFlowInstUuids", vo.getSubFlowInstUuids());
        if (StringUtils.isNotBlank(vo.getKeyword())) {
            values.put("keyword", "%" + vo.getKeyword() + "%");
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString()) && StringUtils.isNotBlank(vo.getKeyword())) {
                // 需要关联相关国际化查询
                if (keywordSql.indexOf("t1.name") != -1) {
                    // 关联了流程环节名称的国际化查询
                    String taskInfoQuery = "select v.id,nvl(i.content,v.name) as name,v.uuid,v.todo_user_id,v.todo_user_name,v.flow_inst_uuid from (\n" +
                            " select t.*,d.id as flow_id from wf_task_instance t ,wf_flow_definition d where t.flow_def_uuid = d.uuid and t.suspension_state in (0, 3, 4) ) v\n" +
                            " left join app_def_element_i18n i on i.def_id = v.flow_id and i.locale='" + LocaleContextHolder.getLocale().toString() + "' and  i.code = v.id ||'.taskName' ";
                    values.put("taskInfoQuery", taskInfoQuery);
                }
                if (keywordSql.indexOf("t1.todo_user_name") != -1) {
                    // 办理人国际化查询关联
                    keywordSql += " or exists ( select 1 from org_element_i18n oi ,wf_task_identity ti where ti.task_inst_uuid = t1.uuid and oi.data_id = ti.user_id and oi.content like :keyword " +
                            " and oi.locale='" + LocaleContextHolder.getLocale().toString() + "'  ) ";
                    keywordSql += " or exists\n" +
                            "        (select 1\n" +
                            "           from user_info ui, user_name_i18n uni, wf_task_identity ti\n" +
                            "          where uni.user_uuid = ui.uuid\n" +
                            "            and ui.user_id = ti.user_id \n" +
                            "            and ti.task_inst_uuid = t1.uuid\n" +
                            "            and uni.user_name like :keyword\n" +
                            "            and uni.locale = '" + LocaleContextHolder.getLocale().toString() + "')";
                }
                if (keywordSql.indexOf("t3.todo_name") != -1) {
//                    keywordSql+=" "
                }

            }
        }
        values.put("leftJoinSql", leftJoinSql);
        values.put("keywordSql", keywordSql);
        values.put("orderBySql", orderBySql);
        String queryName = null;
        List<Page<FlowShareDataQueryItem>> flowShareDataQueryItems = new ArrayList<>();
        try {
            if (CollectionUtils.isNotEmpty(list)) {
                List<String> belongToTaskInstUuidList = new ArrayList<>();
                if (StringUtils.isBlank(vo.getBelongToTaskInstUuid())) {
                    Set<String> collect = list.stream().map(TaskSubFlow::getParentTaskInstUuid)
                            .collect(Collectors.toSet());
                    belongToTaskInstUuidList.addAll(collect);
                } else {
                    belongToTaskInstUuidList.add(vo.getBelongToTaskInstUuid());
                }
                values.put("parent_flow_inst_uuid", vo.getParentFlowInstUuid());
                queryName = "getFlowShareData";
                for (String belongToTaskInstUuid : belongToTaskInstUuidList) {
                    values.put("belongToTaskInstUuid", belongToTaskInstUuid);
                    page.setResult(dao.listItemByNameSQLQuery(queryName, FlowShareDataQueryItem.class, values, pagingInfo));
                    page.setPageNo(pagingInfo.getCurrentPage());
                    page.setPageSize(pagingInfo.getPageSize());
                    page.setTotalCount(dao.countFlowShareDataBySQL(queryName, values));
                    flowShareDataQueryItems.add(page);
                }
            } else {
                values.put("sub_flow_inst_uuid", vo.getParentFlowInstUuid());
                queryName = "getFlowShareDataBySubFlowInstUuid";
                page.setResult(dao.listItemByNameSQLQuery(queryName, FlowShareDataQueryItem.class, values, pagingInfo));
                page.setPageNo(pagingInfo.getCurrentPage());
                page.setPageSize(pagingInfo.getPageSize());
                page.setTotalCount(dao.countFlowShareDataBySQL(queryName, values));
                flowShareDataQueryItems.add(page);
            }
            if (CollectionUtils.isNotEmpty(flowShareDataQueryItems) && !LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                for (Page<FlowShareDataQueryItem> p : flowShareDataQueryItems) {
                    if (CollectionUtils.isNotEmpty(p.getResult())) {
                        List<FlowShareDataQueryItem> queryItems = p.getResult();
                        if (CollectionUtils.isNotEmpty(queryItems)) {
                            for (FlowShareDataQueryItem item : queryItems) {
                                if (item.getCurrentTodoUserId() != null) {
                                    item.setCurrentTodoUserName(IdentityResolverStrategy.resolveAsNames(Arrays.asList(item.getCurrentTodoUserId().split(Separator.SEMICOLON.getValue()))));
                                }
                                if (item.getTodoId() != null) {
                                    item.setTodoName(IdentityResolverStrategy.resolveAsNames(Arrays.asList(item.getTodoId().split(Separator.SEMICOLON.getValue()))));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("getShareDataQueryItems查询数据异常：", e);
            throw new RuntimeException(e.getMessage());
        }

        return flowShareDataQueryItems;
    }

    /**
     * @param parentFlowInstUuid
     * @return
     */
    private List<FlowShareDataQueryItem> getShareDataQueryItems(String parentFlowInstUuid,
                                                                List<String> subFlowInstUuids) {
        List<FlowShareDataQueryItem> shareDataQueryItems;
        // 作为父流程实例UUID获取数据
        TaskSubFlow example = new TaskSubFlow();
        example.setParentFlowInstUuid(parentFlowInstUuid);
        List<TaskSubFlow> list = this.dao.listByEntity(example);
        Collections.sort(list, new Comparator<TaskSubFlow>() {
            @Override
            public int compare(TaskSubFlow o1, TaskSubFlow o2) {
                int between = (int) (o2.getCreateTime().getTime() - o1.getCreateTime().getTime());
                return between;
            }
        });
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("parent_flow_inst_uuid", parentFlowInstUuid);
            values.put("suspension_state", SuspensionState.NORMAL.getState());
            values.put("subFlowInstUuids", subFlowInstUuids);
            shareDataQueryItems = this.dao.listItemByNameSQLQuery("getFlowShareData", FlowShareDataQueryItem.class,
                    values, new PagingInfo(1, Integer.MAX_VALUE, false));
        } else {
            return Collections.emptyList();
            // Map<String, Object> values = new HashMap<String, Object>();
            // values.put("sub_flow_inst_uuid", parentFlowInstUuid);
            // values.put("suspension_state", SuspensionState.NORMAL.getState());
            // values.put("subFlowInstUuids", subFlowInstUuids);
            // shareDataQueryItems =
            // this.dao.listItemByNameSQLQuery("getFlowShareDataBySubFlowInstUuid",
            // FlowShareDataQueryItem.class, values, new PagingInfo(1, Integer.MAX_VALUE,
            // false));
        }

        List<String> subTaskIdList = new ArrayList<>();
        for (FlowShareDataQueryItem shareDataQueryItem : shareDataQueryItems) {
            String currentTodoUserId = shareDataQueryItem.getCurrentTodoUserId();
            if (StringUtils.isBlank(currentTodoUserId)
                    && StringUtils.isNotBlank(shareDataQueryItem.getTaskInstUuid())) {
                // 办理人为空，可能为子流程环节任务
                subTaskIdList.add(shareDataQueryItem.getTaskInstUuid());
            }
        }

        if (CollectionUtils.isNotEmpty(subTaskIdList)) {
            // 查询并设置二次分发流程的办理人
            Map<String, Object> todoUserNameValues = new HashMap<String, Object>();
            todoUserNameValues.put("parentTaskInstUuids", subTaskIdList);
            List<QueryItem> todoUserNameQueryItemList = this.dao.listQueryItemByNameSQLQuery(
                    "getTodoUserNameByParentTaskInstUuid", todoUserNameValues,
                    new PagingInfo(1, Integer.MAX_VALUE, false));
            for (QueryItem queryItem : todoUserNameQueryItemList) {
                String parentTaskInstUuid = queryItem.getString("parentTaskInstUuid");
                String todoUserName = queryItem.getString("todoUserName");
                for (FlowShareDataQueryItem shareDataQueryItem : shareDataQueryItems) {
                    if (StringUtils.equals(parentTaskInstUuid, shareDataQueryItem.getTaskInstUuid())) {
                        shareDataQueryItem.setCurrentTodoUserName(todoUserName);
                    }
                }
            }
        }
        return shareDataQueryItems;
    }

    /**
     * @param flowDelegate
     * @param parentTaskId
     * @param flowDefId
     * @return
     */
    private NewFlow getNewFlow(FlowDelegate flowDelegate, String parentTaskId, String flowDefId) {
        Node taskNode = flowDelegate.getTaskNode(parentTaskId);
        if (taskNode instanceof SubTaskNode) {
            List<NewFlow> newFlows = ((SubTaskNode) taskNode).getNewFlows();
            for (NewFlow newFlow : newFlows) {
                if (StringUtils.equals(newFlow.getFlowId(), flowDefId)) {
                    return newFlow;
                }
            }
        }
        return null;
    }

    /**
     * @param flowDelegate
     * @param parentTaskId
     * @param flowDefId
     * @param isMajor
     * @return
     */
    private NewFlow getNewFlow(FlowDelegate flowDelegate, String parentTaskId, String flowDefId, Boolean isMajor) {
        Node taskNode = flowDelegate.getTaskNode(parentTaskId);
        if (taskNode instanceof SubTaskNode) {
            List<NewFlow> newFlows = ((SubTaskNode) taskNode).getNewFlows();
            for (NewFlow newFlow : newFlows) {
                if (StringUtils.equals(newFlow.getFlowId(), flowDefId)
                        && Boolean.valueOf(newFlow.isMajor()).equals(isMajor)) {
                    if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString()) && newFlow.getI18n() != null) {
//                        AppDefElementI18nEntity i18nEntity = appDefElementI18nService.getI18n(flowDelegate.getFlow().getId(), null, "flowLabel_" + newFlow.getId(), new BigDecimal(flowDelegate.getFlow().getVersion()), IexportType.FlowDefinition, LocaleContextHolder.getLocale().toString());
//                        if (i18nEntity != null) {
//                            newFlow.setLabel(i18nEntity.getContent());
//                        }
                        Map<String, String> map = newFlow.getI18n().get(LocaleContextHolder.getLocale().toString());
                        if (map != null && newFlow.getId() != null && map.containsKey("flowLabel_" + newFlow.getId())) {
                            newFlow.setLabel(map.get("flowLabel_" + newFlow.getId()));
                        }
                    }
                    return newFlow;
                }
            }
        }
        return null;
    }

    /**
     * @param flowDefinition
     * @param parentTaskId
     * @return
     */
    private List<NewFlow> getNewFlows(FlowDefinition flowDefinition, String parentTaskId) {
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);
        Node taskNode = flowDelegate.getTaskNode(parentTaskId);
        if (taskNode instanceof SubTaskNode) {
            return ((SubTaskNode) taskNode).getNewFlows();
        }
        return Collections.emptyList();
    }

    /**
     * 获取子流程共享数据
     *
     * @param flowInstUuid
     * @param taskSubFlow
     * @param subFlowInstance
     * @param subTaskInstance
     * @return
     */
    private Map<Object, Object> getSubFlowShareData(TaskSubFlow taskSubFlow, FlowInstance subFlowInstance,
                                                    TaskInstance subTaskInstance) {
        Map<Object, Object> data = new HashMap<Object, Object>();
        // 名称
        data.put("title", subFlowInstance.getTitle());
        String todoName = taskSubFlow.getTodoName();
        // 办理对象
        if (StringUtils.isBlank(todoName)) {
            data.put("todoUser", "");
        } else {
            data.put("todoUser", IdentityResolverStrategy
                    .resolveAsNames(Arrays.asList(StringUtils.split(todoName, Separator.SEMICOLON.getValue()))));
        }
        // 办理意见
        data.put("opinion", getOpinions(subFlowInstance.getUuid()));
        // 办理状态
        data.put("currentTask", subTaskInstance == null && subFlowInstance.getEndTime() == null ? "草稿中"
                : (subTaskInstance == null ? "已结束" : subTaskInstance.getName()));
        // 当前在办
        data.put("currentUser", getAssignee(subTaskInstance));
        return data;
    }

    /**
     * 获取流程的待办人
     *
     * @param flowInstance
     * @return
     */
    private String getAssignee(TaskInstance taskInstance) {
        StringBuilder sb = new StringBuilder();
        List<AclTaskEntry> aclSids = aclTaskService.getSid(taskInstance.getUuid(), AclPermission.TODO);
        Iterator<AclTaskEntry> it = aclSids.iterator();
        while (it.hasNext()) {
            String name = IdentityResolverStrategy.resolveAsName(it.next().getSid());
            if (name != null) {
                sb.append(name);
                if (it.hasNext()) {
                    sb.append(Separator.SEMICOLON.getValue());
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取办理意见
     *
     * @param flowInstUuid
     * @return
     */
    public String getOpinions(String flowInstUuid) {
        List<TaskActivity> taskActivities = taskService.getTaskActivities(flowInstUuid);
        Map<String, List<TaskOperation>> operationMap = taskService.getOperationAsMap(flowInstUuid);
        StringBuilder sb = new StringBuilder();
        for (TaskActivity taskActivity : taskActivities) {
            // 流转信息
            String taskInstUuid = taskActivity.getTaskInstUuid();
            TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
            sb.append("<" + taskInstance.getName() + ">");
            sb.append(" ");
            sb.append(DateUtils.formatDateTime(taskActivity.getCreateTime()));
            sb.append(" ");
            // sb.append(orgApiFacade.getAccountByUserId(taskActivity.getCreator()).getUserName());
            sb.append(workflowOrgService.getNameById(taskActivity.getCreator()));
            sb.append(Separator.LINE.getValue() + "<br>");

            // 操作信息
            List<TaskOperation> taskOperations = operationMap.get(taskInstUuid);
            if (taskOperations == null) {
                continue;
            }
            for (TaskOperation taskOperation : taskOperations) {
                String[] userIds = taskOperation.getAssignee().split(Separator.COMMA.getValue());
                Map<String, String> userIdMap = workflowOrgService.getNamesByIds(Arrays.asList(userIds));
                List<String> userNames = new ArrayList<String>();
                for (String id : userIds) {
//                    MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(id);
//                    if (user != null) {
//                        userNames.add(user.getUserName());
//                    }
                    userNames.add(userIdMap.get(id));
                }
                sb.append(StringUtils.join(userNames, Separator.COMMA.getValue()));
                sb.append(" ");
                sb.append(DateUtils.formatDateTime(taskOperation.getCreateTime()));
                sb.append(" ");
                sb.append(WorkFlowOperation.getName(taskOperation.getActionType()));
                if (StringUtils.isNotBlank(taskOperation.getOpinionText())) {
                    sb.append(Separator.LINE.getValue() + "<br>");
                    sb.append(" ");
                    sb.append(taskOperation.getOpinionText());
                }
                sb.append(Separator.LINE.getValue() + "<br>");
            }
        }
        return sb.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#initSubTaskRelations(String, SubTaskNode)
     */
    @Override
    @Transactional
    public void initSubTaskRelations(String parentTaskInstUuid, SubTaskNode subTaskNode) {
        TaskSubFlow example = new TaskSubFlow();
        example.setParentTaskInstUuid(parentTaskInstUuid);
        List<TaskSubFlow> taskSubFlows = this.dao.listByEntity(example);
        List<NewFlowRelation> relations = subTaskNode.getRelations();
        for (NewFlowRelation relation : relations) {
            // 子流程配置信息
            String newFlowId = relation.getNewFlowId();
            String newFlowName = relation.getNewFlowName();
            String taskId = relation.getTaskId();
            String taskName = relation.getTaskName();

            // 前置子流程配置信息
            String frontNewFlowId = relation.getFrontNewFlowId();
            String frontNewFlowName = relation.getFrontNewFlowName();
            String frontTaskId = relation.getFrontTaskId();
            String frontTaskName = relation.getFrontTaskName();

            // 保存子流程实例的前置关系
            List<TaskSubFlow> subFlows = filterTaskSubFlow(taskSubFlows, newFlowId);
            List<TaskSubFlow> frontSubFlows = filterTaskSubFlow(taskSubFlows, frontNewFlowId);
            for (TaskSubFlow taskSubFlow : subFlows) {
                List<TaskSubFlowRelation> subFlowRelations = taskSubFlowRelationService.getByTaskSubFlowUuid(taskSubFlow.getUuid());
                for (TaskSubFlow frontTaskSubFlow : frontSubFlows) {
                    TaskSubFlowRelation taskSubFlowRelation = new TaskSubFlowRelation();
                    if (CollectionUtils.isNotEmpty(subFlowRelations)) {
                        taskSubFlowRelation = subFlowRelations.stream()
                                .filter(exist -> StringUtils.equals(exist.getFrontNewFlowInstUuid(), frontTaskSubFlow.getFlowInstUuid()))
                                .findFirst().orElse(taskSubFlowRelation);
                    }
                    // 子流程实例UUID
                    String newFlowInstUuid = taskSubFlow.getFlowInstUuid();
                    taskSubFlowRelation.setNewFlowInstUuid(newFlowInstUuid);
                    taskSubFlowRelation.setNewFlowId(newFlowId);
                    taskSubFlowRelation.setNewFlowName(newFlowName);
                    taskSubFlowRelation.setTaskId(taskId);
                    taskSubFlowRelation.setTaskName(taskName);
                    // 前置子流程实例UUID
                    String frontNewFlowInstUuid = frontTaskSubFlow.getFlowInstUuid();
                    taskSubFlowRelation.setFrontNewFlowInstUuid(frontNewFlowInstUuid);
                    taskSubFlowRelation.setFrontNewFlowId(frontNewFlowId);
                    taskSubFlowRelation.setFrontNewFlowName(frontNewFlowName);
                    taskSubFlowRelation.setFrontTaskId(frontTaskId);
                    taskSubFlowRelation.setFrontTaskName(frontTaskName);
                    // 设置前置关系所属的子流程
                    taskSubFlowRelation.setTaskSubFlow(taskSubFlow);
                    if (BooleanUtils.isTrue(frontTaskSubFlow.getCompleted())) {
                        taskSubFlowRelation.setAllowSubmit(true);
                        taskSubFlowRelation.setSubmitStatus(TaskSubFlowRelation.STATUS_PASS);
                    }
                    taskSubFlowRelationService.save(taskSubFlowRelation);
                }
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param newFlowId
     * @return
     */
    private List<TaskSubFlow> filterTaskSubFlow(List<TaskSubFlow> taskSubFlows, String newFlowId) {
        List<TaskSubFlow> subFlows = new ArrayList<TaskSubFlow>();
        for (TaskSubFlow taskSubFlow : taskSubFlows) {
            if (taskSubFlow.getFlowId().equals(newFlowId)) {
                subFlows.add(taskSubFlow);
            }
        }
        return subFlows;
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#evaluateRelationStatus(String)
     */
    @Override
    public void checkSubFlowAllowSubmit(String fromTaskId, String flowInstUuid) {
        TaskSubFlowRelation example = new TaskSubFlowRelation();
        example.setNewFlowInstUuid(flowInstUuid);
        example.setTaskId(fromTaskId);
        List<TaskSubFlowRelation> relations = this.taskSubFlowRelationService.findByExample(example);
        for (TaskSubFlowRelation relation : relations) {
            // 允许后置流程提交
            if (Boolean.TRUE.equals(relation.getAllowSubmit())) {
                continue;
            }
            if (!TaskSubFlowRelation.STATUS_PASS.equals(relation.getSubmitStatus())) {
                String flowName = relation.getFrontNewFlowName();
                String taskName = relation.getFrontTaskName();
                String msg = "前置流程[" + flowName + "]的环节[" + taskName + "]未完成，不能提交工作!";
                if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                    FlowInstance flowInstance = flowInstanceService.getOne(relation.getFrontNewFlowInstUuid());
                    AppDefElementI18nEntity i18nEntity = appDefElementI18nService.getI18n(relation.getFrontNewFlowId(), null, "workflowName",
                            new BigDecimal(flowInstance.getFlowDefinition().getVersion()), IexportType.FlowDefinition, LocaleContextHolder.getLocale().toString());
                    if (i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent())) {
                        flowName = i18nEntity.getContent();
                    }
                    i18nEntity = appDefElementI18nService.getI18n(relation.getFrontNewFlowId(), null, relation.getFrontTaskId() + ".taskName",
                            new BigDecimal(flowInstance.getFlowDefinition().getVersion()), IexportType.FlowDefinition, LocaleContextHolder.getLocale().toString());
                    if (i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent())) {
                        taskName = i18nEntity.getContent();
                    }
                    Map<String, Object> data = Maps.newHashMap();
                    data.put("flowName", flowName);
                    data.put("taskName", taskName);
                    msg = AppCodeI18nMessageSource.getMessage("WorkflowWork.message.cannotCommitPreviousFlowUnfinished", "pt-workflow", LocaleContextHolder.getLocale().toString(), data, "委托给");
                }


                throw new WorkFlowException(msg);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#updateSubFlowRelationStatus(String, String)
     */
    @Override
    @Transactional
    public List<TaskSubFlowRelation> updateSubFlowRelationStatus(String taskId, String flowInstUuid,
                                                                 Integer submitStatus) {
        List<TaskSubFlowRelation> subFlowRelations = new ArrayList<TaskSubFlowRelation>();
        TaskSubFlowRelation example = new TaskSubFlowRelation();
        example.setFrontNewFlowInstUuid(flowInstUuid);
        example.setFrontTaskId(taskId);
        List<TaskSubFlowRelation> relations = this.taskSubFlowRelationService.findByExample(example);
        for (TaskSubFlowRelation relation : relations) {
            relation.setSubmitStatus(submitStatus);
            if (TaskSubFlowRelation.STATUS_PASS.equals(submitStatus)) {
                relation.setAllowSubmit(true);
            } else {
                relation.setAllowSubmit(false);
            }
            this.taskSubFlowRelationService.save(relation);
            subFlowRelations.add(relation);
        }
        return subFlowRelations;
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#isFrontNewFlowFinished(TaskInstance, FlowInstance)
     */
    @Override
    public boolean isFrontNewFlowFinished(String taskId, String flowInstUuid) {
        TaskSubFlowRelation example = new TaskSubFlowRelation();
        example.setNewFlowInstUuid(flowInstUuid);
        example.setTaskId(taskId);
        List<TaskSubFlowRelation> relations = this.taskSubFlowRelationService.findByExample(example);
        for (TaskSubFlowRelation relation : relations) {
            // 如果不允许后置环节提交，返回false
            if (!Boolean.TRUE.equals(relation.getAllowSubmit())) {
                return false;
            }

            // 存在前置子流程未完成，返回false
            if (!TaskSubFlowRelation.STATUS_PASS.equals(relation.getSubmitStatus())) {
                return false;
            }
        }

        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#getOthersBySubFlowInstUuid(String)
     */
    @Override
    public List<TaskSubFlow> getOthersBySubFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listByHQL(GET_OTHERS_BY_SUB_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#getBehindFlowInstanceByFrontFlowInstanceUuid(String)
     */
    @Override
    public List<FlowInstance> getBehindFlowInstanceByFrontFlowInstanceUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("frontNewFlowInstUuid", flowInstUuid);
        return this.flowInstanceService.listByHQL(GET_BEHIND_FLOW_INST_BY_FRONT_FLOW_INST_UUID, values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#getByFlowInstUuid(String)
     */
    @Override
    public List<TaskSubFlow> getByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listByHQL(GET_BY_SUB_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#removeByFlowInstUuid(String)
     */
    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.deleteByHQL(REMOVE_BY_SUB_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#getDistributeInfos(String)
     */
    @Override
    public List<TaskInfoDistributionData> getDistributeInfos(String flowInstUuid) {
        List<TaskInfoDistributionQueryItem> taskInfoDistributions = this.taskInfoDistributionService
                .getSubFlowDistributeInfos(flowInstUuid, null, null).getResult();
        if (CollectionUtils.isEmpty(taskInfoDistributions)) {
            return Lists.newArrayList();
        }
        return getDistributeInfos(flowInstUuid, taskInfoDistributions);

    }

    private List<TaskInfoDistributionData> getDistributeInfos(String flowInstUuid,
                                                              List<TaskInfoDistributionQueryItem> taskInfoDistributions) {
        List<TaskInfoDistributionData> taskInfoDistributionDatas = Lists.newArrayList();
        // 按作记录按父环节实例UUID分组并计算各自分组的标题
        ImmutableListMultimap<String, TaskInfoDistributionQueryItem> immutableListMultimap = Multimaps
                .index(taskInfoDistributions.iterator(), new Function<TaskInfoDistributionQueryItem, String>() {

                    @Override
                    public String apply(TaskInfoDistributionQueryItem taskInfoDistributionQueryItem) {
                        return taskInfoDistributionQueryItem.getDistributeTaskInstUuid();
                    }

                });
        ImmutableMap<String, Collection<TaskInfoDistributionQueryItem>> immutableMap = immutableListMultimap.asMap();

        // 主流程定义
        FlowInstance parentFlowInstance = this.flowInstanceService.get(flowInstUuid);
        FlowDefinition parentFlowDefinition = parentFlowInstance.getFlowDefinition();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(parentFlowDefinition);

        for (Entry<String, Collection<TaskInfoDistributionQueryItem>> entry : immutableMap.entrySet()) {
            // 分组数据
            TaskInfoDistributionData taskInfoDistributionData = new TaskInfoDistributionData();
            Collection<TaskInfoDistributionQueryItem> groupTaskInfoDistributionQueryItems = entry.getValue();
            List<TaskInfoDistributionBean> distributionBeans = Lists.newArrayList();
            for (TaskInfoDistributionQueryItem taskInfoDistribution : groupTaskInfoDistributionQueryItems) {
                TaskInfoDistributionBean distributionBean = new TaskInfoDistributionBean();
                BeanUtils.copyProperties(taskInfoDistribution, distributionBean);
                distributionBean
                        .setDistributorName(IdentityResolverStrategy.resolveAsName(distributionBean.getCreator()));
                String repoFileIds = taskInfoDistribution.getRepoFileIds();
                if (StringUtils.isNotBlank(repoFileIds)) {
                    List<MongoFileEntity> mongoFileEntities = mongoFileService
                            .getFilesFromFolder(taskInfoDistribution.getUuid(), null);
                    if (CollectionUtils.isNotEmpty(mongoFileEntities)) {
                        List<LogicFileInfo> logicFileInfos = new ArrayList<LogicFileInfo>();
                        for (MongoFileEntity mongoFileEntity : mongoFileEntities) {
                            logicFileInfos.add(mongoFileEntity.getLogicFileInfo());
                        }
                        distributionBean.setLogicFileInfos(logicFileInfos);
                    }
                }
                distributionBeans.add(distributionBean);
            }
            taskInfoDistributionData.setDistributeInfos(distributionBeans);

            // 分组标题
            String parentTaskInstUuid = entry.getKey();
            TaskInstance parentTaskInstance = taskService.getTask(parentTaskInstUuid);
            FlowDelegate parentFlowDelegate = FlowDelegateUtils
                    .getFlowDelegate(parentTaskInstance.getFlowInstance().getFlowDefinition());
            SubTaskNode subTaskNode = getSubTaskNode(parentFlowDelegate, parentTaskInstance.getId());
            taskInfoDistributionData.setTitle(getInfoDistributionTitle(subTaskNode, flowDelegate, parentTaskInstance,
                    parentFlowInstance, parentFlowDefinition));

            // 归属流程实例UUID
            taskInfoDistributionData.setBelongToFlowInstUuid(flowInstUuid);
            taskInfoDistributionDatas.add(taskInfoDistributionData);
        }
        return taskInfoDistributionDatas;
    }

    @Override
    public Page<TaskInfoDistributionData> getDistributeInfosByPage(SubTaskDataVo vo) {
        Page<TaskInfoDistributionData> page = new Page<>();
        Page<TaskInfoDistributionQueryItem> paramPage = new Page<>();
        paramPage.pageNo(vo.getPageNum());
        paramPage.pageSize(vo.getPageSize());
        List<TaskInfoDistributionQueryItem> taskInfoDistributions = this.taskInfoDistributionService
                .getSubFlowDistributeInfos(vo.getParentFlowInstUuid(), vo.getKeyword(), paramPage).getResult();
        if (!CollectionUtils.isEmpty(taskInfoDistributions)) {
            page.setResult(getDistributeInfos(vo.getParentFlowInstUuid(), taskInfoDistributions));
        }
        page.setTotalCount(paramPage.getTotalCount());
        return page;
    }

    /**
     * @param subTaskNode
     * @param flowDelegate
     * @param parentTaskInstance
     * @param parentFlowInstance
     * @return
     */
    private String getInfoDistributionTitle(SubTaskNode subTaskNode, FlowDelegate flowDelegate,
                                            TaskInstance parentTaskInstance, FlowInstance parentFlowInstance, FlowDefinition parentFlowDefinition) {
        String infoDistributionTitleExpression = subTaskNode.getInfoDistributionTitleExpression();
        String title = TitleExpressionUtils.getUndertakeSituationTitleExpression(infoDistributionTitleExpression,
                flowDelegate, parentTaskInstance, parentFlowInstance, parentFlowDefinition);
        return title;
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#getSubflowRelateOperation(String)
     */
    @Override
    public List<TaskOperationData> getSubflowRelateOperation(String flowInstUuid) {
        List<TaskOperationQueryItem> taskOperations = taskOperationService
                .getSubflowRelateOperation(flowInstUuid, null, null).getResult();
        if (CollectionUtils.isEmpty(taskOperations)) {
            return Lists.newArrayList();
        }
        return getSubflowRelateOperation(flowInstUuid, taskOperations);
    }

    private List<TaskOperationData> getSubflowRelateOperation(String flowInstUuid,
                                                              List<TaskOperationQueryItem> taskOperations) {
        List<TaskOperationData> taskOperationDatas = Lists.newArrayList();
        // 按作记录按父环节实例UUID分组并计算各自分组的标题
        ImmutableListMultimap<String, TaskOperationQueryItem> immutableListMultimap = Multimaps
                .index(taskOperations.iterator(), new Function<TaskOperationQueryItem, String>() {
                    @Override
                    public String apply(TaskOperationQueryItem taskOperationQueryItem) {
                        return taskOperationQueryItem.getBelongToTaskInstUuid();
                    }
                });
        ImmutableMap<String, Collection<TaskOperationQueryItem>> immutableMap = immutableListMultimap.asMap();

        // 主流程定义
        FlowInstance parentFlowInstance = this.flowInstanceService.get(flowInstUuid);
        FlowDefinition parentFlowDefinition = parentFlowInstance.getFlowDefinition();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(parentFlowDefinition);

        for (Entry<String, Collection<TaskOperationQueryItem>> entry : immutableMap.entrySet()) {
            // 分组数据
            TaskOperationData taskOperationData = new TaskOperationData();
            Collection<TaskOperationQueryItem> groupTaskOperationQueryItems = entry.getValue();
            List<TaskOperationBean> operationBeans = Lists.newArrayList();
            for (TaskOperationQueryItem taskOperation : groupTaskOperationQueryItems) {
                TaskOperationBean operationBean = new TaskOperationBean();
                BeanUtils.copyProperties(taskOperation, operationBean);
                String userId = operationBean.getUserId();
                if (StringUtils.isNotBlank(userId)) {
                    List<String> orgIds = Arrays.asList(StringUtils.split(userId, Separator.SEMICOLON.getValue()));
                    operationBean.setUserName(IdentityResolverStrategy.resolveAsNames(orgIds));
                } else {
                    operationBean.setUserName(StringUtils.EMPTY);
                }
                if (StringUtils.isNotBlank(operationBean.getAssignee()) && !LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                    operationBean.setAssigneeName(IdentityResolverStrategy.resolveAsName(operationBean.getAssignee()));
                }
                if (StringUtils.isBlank(operationBean.getOpinionText())) {
                    operationBean.setOpinionText(StringUtils.EMPTY);
                }
                operationBeans.add(operationBean);
            }
            taskOperationData.setOperations(operationBeans);

            // 分组标题
            String parentTaskInstUuid = entry.getKey();
            TaskInstance parentTaskInstance = taskService.getTask(parentTaskInstUuid);
            if (parentTaskInstance != null) {
                SubTaskNode subTaskNode = getSubTaskNode(flowDelegate, parentTaskInstance.getId());
                if (subTaskNode != null) {
                    taskOperationData.setTitle(getOperationRecordTitle(subTaskNode, flowDelegate, parentTaskInstance,
                            parentFlowInstance, parentFlowDefinition));
                }
            }

            // 归属流程实例UUID
            taskOperationData.setBelongToFlowInstUuid(flowInstUuid);
            taskOperationDatas.add(taskOperationData);
        }
        return taskOperationDatas;
    }

    @Override
    public Page<TaskOperationData> getSubflowRelateOperationByPage(SubTaskDataVo vo) {
        Page<TaskOperationData> page = new Page<>();
        page.setPageNo(vo.getPageNum());
        page.setPageSize(vo.getPageSize());
        Page<TaskOperationQueryItem> paramPage = new Page<>();
        paramPage.setPageNo(vo.getPageNum());
        paramPage.setPageSize(vo.getPageSize());
        Page<TaskOperationQueryItem> pageTaskOperations = taskOperationService
                .getSubflowRelateOperation(vo.getFlowInstUuid(), vo.getKeyword(), paramPage);
        if (!CollectionUtils.isEmpty(pageTaskOperations.getResult())) {
            page.setResult(getSubflowRelateOperation(vo.getFlowInstUuid(), pageTaskOperations.getResult()));
            page.setTotalCount(pageTaskOperations.getTotalCount());
        }
        return page;
    }

    /**
     * @param subTaskNode
     * @param flowDelegate
     * @param parentTaskInstance
     * @param parentFlowInstance
     * @return
     */
    private String getOperationRecordTitle(SubTaskNode subTaskNode, FlowDelegate flowDelegate,
                                           TaskInstance parentTaskInstance, FlowInstance parentFlowInstance, FlowDefinition parentFlowDefinition) {
        String infoDistributionTitleExpression = subTaskNode.getOperationRecordTitleExpression();
        String title = TitleExpressionUtils.getUndertakeSituationTitleExpression(infoDistributionTitleExpression,
                flowDelegate, parentTaskInstance, parentFlowInstance, parentFlowDefinition);
        return title;
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#listTodoUserNameByParentTaskInstUuid(String)
     */
    @Override
    public List<String> listTodoUserNameByParentTaskInstUuid(String parentTaskInstUuid) {
        Set<String> todoUserNames = Sets.newLinkedHashSet();
        Map<String, Object> todoUserNameValues = new HashMap<String, Object>();
        todoUserNameValues.put("parentTaskInstUuids", Lists.newArrayList(parentTaskInstUuid));
        List<QueryItem> todoUserNameQueryItemList = this.dao.listQueryItemByNameSQLQuery(
                "getTodoUserNameByParentTaskInstUuid", todoUserNameValues, new PagingInfo(1, Integer.MAX_VALUE, false));
        for (QueryItem queryItem : todoUserNameQueryItemList) {
            String todoUserName = queryItem.getString("todoUserName");
            if (StringUtils.isNotBlank(todoUserName)) {
                todoUserNames.addAll(Arrays.asList(StringUtils.split(todoUserName, Separator.SEMICOLON.getValue())));
            }
        }
        return Arrays.asList(todoUserNames.toArray(new String[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#isInCompletionStatesByFlowInstUuid(String, Integer[])
     */
    @Override
    public boolean isInCompletionStatesByFlowInstUuid(String flowInstUuid, Integer... states) {
        String hql = "from TaskSubFlow t where t.flowInstUuid = :flowInstUuid and t.completionState in(:states)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        values.put("states", states);
        return this.dao.countByHQL(hql, values) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#filterCompletionStatesByFlowInstUuids(List, Integer[])
     */
    @Override
    public List<String> filterCompletionStatesByFlowInstUuids(List<String> flowInstUuids, Integer... states) {
        String hql = "select t.flowInstUuid as flowInstUuid from TaskSubFlow t where t.flowInstUuid in(:flowInstUuids) and t.completionState in(:states)";
        List<String> retFlowInstUuids = Lists.newArrayList();
        Set<String> tmpFlowInstUuids = Sets.newHashSet();
        int num = 0;
        int size = flowInstUuids.size();
        for (String flowInstUuid : flowInstUuids) {
            num++;
            tmpFlowInstUuids.add(flowInstUuid);
            if (num % 1000 == 0 || num == size) {
                Map<String, Object> values = Maps.newHashMap();
                values.put("flowInstUuids", tmpFlowInstUuids);
                values.put("states", states);
                List<QueryItem> queryItems = this.dao.listQueryItemByHQL(hql, values, null);
                for (QueryItem queryItem : queryItems) {
                    retFlowInstUuids.add(queryItem.getString("flowInstUuid"));
                }
                tmpFlowInstUuids.clear();
            }
        }
        return retFlowInstUuids;
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#countByParentFlowInstUuid(String)
     */
    @Override
    public long countByParentFlowInstUuid(String parentFlowInstUuid) {
        String hql = "from TaskSubFlow t where t.parentFlowInstUuid = :parentFlowInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("parentFlowInstUuid", parentFlowInstUuid);
        return this.dao.countByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#countByFlowInstUuid(String)
     */
    @Override
    public long countByFlowInstUuid(String flowInstUuid) {
        String hql = "from TaskSubFlow t where t.flowInstUuid = :flowInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.countByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see TaskSubFlowService#getLatestOneByParentFlowInstUuid(String)
     */
    @Override
    public TaskSubFlow getLatestOneByParentFlowInstUuid(String parentFlowInstUuid) {
        String hql = "from TaskSubFlow t where t.parentFlowInstUuid = :parentFlowInstUuid order by t.createTime desc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("parentFlowInstUuid", parentFlowInstUuid);
        List<TaskSubFlow> taskSubFlows = this.dao.listByHQLAndPage(hql, values, new PagingInfo(1, 1));
        if (CollectionUtils.isNotEmpty(taskSubFlows)) {
            return taskSubFlows.get(0);
        }
        return null;
    }

    /**
     * list转map key:id
     *
     * @param flowDefinitions
     * @return java.util.Map<java.lang.String, com.wellsoft.pt.bpm.engine.entity.FlowDefinition>
     **/
    private Map<String, FlowDefinition> getFlowDefinitionMap(List<FlowDefinition> flowDefinitions) {
        Map<String, FlowDefinition> flowDefinitionMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(flowDefinitions)) {
            return flowDefinitionMap;
        }
        for (FlowDefinition flowDefinition : flowDefinitions) {
            flowDefinitionMap.put(flowDefinition.getId(), flowDefinition);
        }
        return flowDefinitionMap;
    }

    /**
     * list转map key:formUuid
     *
     * @param formDefinitions
     * @return java.util.Map<java.lang.String, com.wellsoft.pt.bpm.engine.entity.FlowDefinition>
     **/
    private Map<String, FormDefinition> getFormDefinitionMap(List<DyFormFormDefinition> formDefinitions) {
        Map<String, FormDefinition> formDefinitionMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(formDefinitions)) {
            return formDefinitionMap;
        }
        for (DyFormFormDefinition formDefinition : formDefinitions) {
            formDefinitionMap.put(formDefinition.getUuid(), (FormDefinition) formDefinition);
        }
        return formDefinitionMap;
    }

    /**
     * 获取流程定义表中的表单uuid集合
     *
     * @param flowDefinitions
     * @return java.util.Set<java.lang.String>
     **/
    private Set<String> getFormUuids(List<FlowDefinition> flowDefinitions) {
        Set<String> formUuids = Sets.newHashSet();
        if (CollectionUtils.isEmpty(flowDefinitions)) {
            return formUuids;
        }
        for (FlowDefinition flowDefinition : flowDefinitions) {
            formUuids.add(flowDefinition.getFormUuid());
        }
        return formUuids;
    }

    /**
     * 环节表单附件集合
     * key：flowInstUuid
     *
     * @param shareDataQueryItems 承办情况行数据
     * @return java.util.Map<java.lang.String, java.util.List < com.wellsoft.pt.bpm.engine.entity.TaskFormAttachment>>
     **/
    private Map<String, List<TaskFormAttachment>> getTaskFormAttachmentMap(
            Collection<FlowShareDataQueryItem> shareDataQueryItems) {
        List<String> flowInstUuids = Lists.newArrayList();
        for (FlowShareDataQueryItem shareDataQueryItem : shareDataQueryItems) {
            flowInstUuids.add(shareDataQueryItem.getFlowInstUuid());
        }
        List<TaskFormAttachment> taskFormAttachments = taskFormAttachmentService.getByFlowInstUuids(flowInstUuids);
        Map<String, List<TaskFormAttachment>> taskFormAttachmentMap = Maps.newHashMap();
        for (TaskFormAttachment taskFormAttachment : taskFormAttachments) {
            List<TaskFormAttachment> taskFormAttachmentList = taskFormAttachmentMap
                    .get(taskFormAttachment.getFlowInstUuid());
            if (CollectionUtils.isEmpty(taskFormAttachmentList)) {
                taskFormAttachmentList = Lists.newArrayList();
            }
            taskFormAttachmentList.add(taskFormAttachment);
            taskFormAttachmentMap.put(taskFormAttachment.getFlowInstUuid(), taskFormAttachmentList);
        }
        return taskFormAttachmentMap;
    }

}
