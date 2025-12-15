/*
 * @(#)2015-7-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.facade.impl;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.api.DefaultWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.gz.entity.WfGzDataSync;
import com.wellsoft.pt.workflow.gz.entity.WfGzDataSyncHis;
import com.wellsoft.pt.workflow.gz.facade.WfGzDataSyncExchange;
import com.wellsoft.pt.workflow.gz.service.WfGzDataSyncHisService;
import com.wellsoft.pt.workflow.gz.service.WfGzDataSyncService;
import com.wellsoft.pt.workflow.gz.support.WfGzDataConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-16.1	zhulh		2015-7-16		Create
 * </pre>
 * @date 2015-7-16
 */
@Service
@Transactional
public class WfGzDataSyncExchangeImpl extends BaseServiceImpl implements WfGzDataSyncExchange {

    @Autowired
    private WfGzDataSyncService wfGzDataSyncService;

    @Autowired
    private WfGzDataSyncHisService wfGzDataSyncHisService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

//    @Autowired
//    private OrgApiFacade orgApiFacade;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    /**
     * 如何描述该方法
     *
     * @param targetTenantId
     * @param userId
     * @return
     */
    private static WellptClient getWellptClient(WfGzDataSync wfGzDataSync) {
        String userId = wfGzDataSync.getUserId();
        String targetTenantId = wfGzDataSync.getTargetTenantId();
        // String baseAddress = "http://10.24.36.53:8080/hloa-web/webservices/wellpt/rest/service";
        String baseAddress = SystemParams.getValue(WfGzDataConstant.KEY_WELL_PT_REST_ADDRESS,
                WfGzDataConstant.DEFAULT_WELL_PT_REST_ADDRESS);
        WellptClient wellptClient = new DefaultWellptClient(baseAddress, targetTenantId, userId, userId);
        return wellptClient;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.facade.WfGzDataSyncExchange#getAllByCreateTimeAsc()
     */
    @Override
    public List<WfGzDataSync> getAllByModifyTimeAsc() {
        return wfGzDataSyncService.getAll("modifyTime asc");
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.gz.facade.WfGzDataSyncExchange#syncDatas(java.util.List)
     */
    @Override
    public void syncDatas(List<WfGzDataSync> syncDatas) {
        for (WfGzDataSync wfGzDataSync : syncDatas) {
            syncData(this.getCommonDao().get(WfGzDataSync.class, wfGzDataSync.getUuid()));
        }
    }

    /**
     * @param wfGzDataSync
     */
    private void syncData(WfGzDataSync wfGzDataSync) {
        // 流程已删除
        if (isDelete(wfGzDataSync)) {
            delete(wfGzDataSync);
            return;
        }
        // 挂起状态(0正常、1挂起、2删除)
        Integer suspensionState = wfGzDataSync.getSuspensionState();
        String targetFlowInstUuid = wfGzDataSync.getTargetFlowInstUuid();
        if (suspensionState.equals(0) && StringUtils.isBlank(targetFlowInstUuid)) {
            // 启动
            startOrRestart(wfGzDataSync);
        } else if (suspensionState.equals(0)) {
            // 重启
            forceSubmit(wfGzDataSync);
        } else if (suspensionState.equals(1)) {
            // 提交或撤消
            submitAndCancel(wfGzDataSync);
        } else if (suspensionState.equals(2)) {
            // 提交或撤消
            submitAndCancel(wfGzDataSync);
        } else if (suspensionState.equals(WfGzDataConstant.STATE_COPY_TO)) {
            // 删除
            copyTo(wfGzDataSync);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param wfGzDataSync
     */
    @SuppressWarnings("unchecked")
    private void startOrRestart(WfGzDataSync wfGzDataSync) {/*
		WellptClient wellptClient = getWellptClient(wfGzDataSync);

		// 4、启动流程实例
		GzFlowInstanceStartRequest gzFlowInstanceStartRequest = new GzFlowInstanceStartRequest();
		gzFlowInstanceStartRequest.setFlowDefinitionId(WfGzDataConstant.FLOW_DEF_ID);
		gzFlowInstanceStartRequest.setToTaskId(WfGzDataConstant.GZ_TASK_ID);
		Map<String, Object> formData = buildFormData(wfGzDataSync);
		gzFlowInstanceStartRequest.setFormData(formData);
		GzFlowInstanceStartResponse gzFlowInstanceStartResponse = wellptClient.execute(gzFlowInstanceStartRequest);
		if (!gzFlowInstanceStartResponse.isSuccess()) {
			throw new RuntimeException(gzFlowInstanceStartResponse.getMsg());
		}
		Map<String, String> result = (Map<String, String>) gzFlowInstanceStartResponse.getData();
		String targetFlowInstUuid = result.get(WfGzDataConstant.FLOW_INST_UUID);

		// 清理与更新数据
		cleanAndUpdate(wfGzDataSync, targetFlowInstUuid);

		// 动态添加流程事件监听器
		FlowInstanceParameter example = new FlowInstanceParameter();
		example.setFlowInstUuid(wfGzDataSync.getSourceFlowInstUuid());
		example.setName(CustomRuntimeData.KEY_FLOW_LISTENER);
		List<FlowInstanceParameter> flowInstanceParameters = flowService.findFlowInstanceParameter(example);
		if (flowInstanceParameters.isEmpty()) {
			FlowInstanceParameter parameter = new FlowInstanceParameter();
			parameter.setFlowInstUuid(wfGzDataSync.getSourceFlowInstUuid());
			parameter.setName(CustomRuntimeData.KEY_FLOW_LISTENER);
			parameter.setValue(WfGzDataConstant.FLOW_LISTENER);
			flowService.saveFlowInstanceParameter(parameter);
		}
	*/
    }

    /**
     * 如何描述该方法
     *
     * @param sourceTenantId
     * @param targetTenantId
     * @param sourceTaskInstUuid
     * @param sourceFlowInstUuid
     * @param flowName
     * @param title
     * @return
     */
    private Map<String, Object> buildFormData(WfGzDataSync wfGzDataSync) {
        FlowInstance flowInstance = flowService.getFlowInstance(wfGzDataSync.getSourceFlowInstUuid());
        List<TaskInstance> unfinishedTasks = taskService.getUnfinishedTasks(flowInstance.getUuid());
        TaskInstance taskInstance = null;
        if (!unfinishedTasks.isEmpty()) {
            taskInstance = getTask(unfinishedTasks, wfGzDataSync.getTaskInstUuid());
        } else {
            taskInstance = taskService.getTask(wfGzDataSync.getTaskInstUuid());
        }
        // 源流程名称
        String flowName = flowInstance.getName();
        // 源流程定义ID
        String flowDefId = flowInstance.getId();
        // 源标题
        String title = flowInstance.getTitle();
        // 源租户ID
        String sourceTenantId = wfGzDataSync.getSourceTenantId();
        // 目标租户ID
        String targetTenantId = wfGzDataSync.getTargetTenantId();
        // 原流程实例UUID
        String sourceFlowInstUuid = wfGzDataSync.getSourceFlowInstUuid();
        // 原环节实例UUID
        String sourceTaskInstUuid = wfGzDataSync.getTaskInstUuid();
        // 原流水号
        String sourceSerialNo = taskInstance.getSerialNo();
        // 当前环节名称
        String currentTaskName = taskInstance.getName();
        // 当前环节ID
        String currentTaskId = taskInstance.getId();
        // 当前环节办理人名称
        String todoUserName = taskInstance.getTodoUserName();
        // 当前环节办理人ID
        String todoUserId = taskInstance.getTodoUserId();
        // 前办理人ID
        String previousOperatorId = taskInstance.getAssignee();
        // 前办理人名称
//        String previousOperatorName = orgApiFacade.getUserNameById(previousOperatorId);
        String previousOperatorName = workflowOrgService.getNameById(previousOperatorId);
        // 到达时间
        Date arriveTime = taskInstance.getStartTime();
        // 到期时间
        Date dueTime = taskInstance.getDueTime();

        // 办理环节名称
        String doneTaskName = null;
        // 办理环节ID
        String doneTaskId = null;
        // 办理时间
        Date doneTime = null;
        List<String> flowInstUuids = new ArrayList<String>();
        flowInstUuids.add(sourceFlowInstUuid);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("assignee", wfGzDataSync.getUserId());
        values.put("flowInstUuids", flowInstUuids);
        List<QueryItem> doneItems = this.nativeDao.namedQuery("myTaskInstanceDoneQuery", values, QueryItem.class);
        if (!doneItems.isEmpty()) {
            doneTaskName = doneItems.get(0).getString(QueryItem.getKey("环节名称"));
            doneTaskId = doneItems.get(0).getString(QueryItem.getKey("办理环节id"));
            doneTime = doneItems.get(0).getDate(QueryItem.getKey("办理时间"));
        }

        Map<String, Object> formData = new HashMap<String, Object>();
        formData.put(WfGzDataConstant.SOURCE_FLOW_NAME, flowName);
        formData.put(WfGzDataConstant.SOURCE_FLOW_DEF_ID, flowDefId);
        formData.put(WfGzDataConstant.SOURCE_TITLE, title);
        formData.put(WfGzDataConstant.SOURCE_TENANT_ID, sourceTenantId);
        formData.put(WfGzDataConstant.TARGET_TENANT_ID, targetTenantId);
        formData.put(WfGzDataConstant.SOURCE_FLOW_INST_UUID, sourceFlowInstUuid);
        formData.put(WfGzDataConstant.SOURCE_TASK_INST_UUID, sourceTaskInstUuid);
        formData.put(WfGzDataConstant.SOURCE_SERIAL_NO, sourceSerialNo);
        formData.put(WfGzDataConstant.CURRENT_TASK_NAME, currentTaskName);
        formData.put(WfGzDataConstant.CURRENT_TASK_ID, currentTaskId);
        formData.put(WfGzDataConstant.TODO_USER_NAME, todoUserName);
        formData.put(WfGzDataConstant.TODO_USER_ID, todoUserId);
        formData.put(WfGzDataConstant.PREVIOUS_OPERATOR_NAME, previousOperatorName);
        formData.put(WfGzDataConstant.PREVIOUS_OPERATOR_ID, previousOperatorId);
        formData.put(WfGzDataConstant.ARRIVE_TIME, arriveTime);
        formData.put(WfGzDataConstant.DUE_TIME, dueTime);
        formData.put(WfGzDataConstant.DONE_TASK_NAME, doneTaskName);
        formData.put(WfGzDataConstant.DONE_TASK_ID, doneTaskId);
        formData.put(WfGzDataConstant.DONE_TIME, doneTime);
        return formData;
    }

    /**
     * 如何描述该方法
     *
     * @param unfinishedTasks
     * @param taskInstUuid
     * @return
     */
    private TaskInstance getTask(List<TaskInstance> unfinishedTasks, String taskInstUuid) {
        for (TaskInstance taskInstance : unfinishedTasks) {
            if (taskInstance.getUuid().equals(taskInstUuid)) {
                return taskInstance;
            }
        }
        return unfinishedTasks.get(0);
    }

    /**
     * 如何描述该方法
     *
     * @param wfGzDataSync
     */
    private void forceSubmit(WfGzDataSync wfGzDataSync) {/*
		String taskInstUuid = wfGzDataSync.getTaskInstUuid();
		String targetFlowInstUuid = wfGzDataSync.getTargetFlowInstUuid();
		WellptClient wellptClient = getWellptClient(wfGzDataSync);
		// 判断流程是否结束
		Boolean isCompleted = flowService.isCompletedByTaskInstUuid(taskInstUuid);
		// 挂职任务提交
		GzTaskSubmitRequest taskSubmitRequest = new GzTaskSubmitRequest();
		taskSubmitRequest.setFlowInstUuid(targetFlowInstUuid);
		if (isCompleted) {
			taskSubmitRequest.setToTaskId(TaskSubmitRequest.END_FLOW_ID);
		} else {
			taskSubmitRequest.setToTaskId(WfGzDataConstant.GZ_TASK_ID);
		}
		Map<String, Object> formData = buildFormData(wfGzDataSync);
		taskSubmitRequest.setFormData(formData);
		GzTaskSubmitResponse taskSubmitResponse = wellptClient.execute(taskSubmitRequest);
		if (!taskSubmitResponse.isSuccess()) {
			throw new RuntimeException(taskSubmitResponse.getMsg());
		}

		// 清理与更新数据
		cleanAndUpdate(wfGzDataSync, targetFlowInstUuid);
	*/
    }

    /**
     * @param wfGzDataSync
     */
    private void submitAndCancel(WfGzDataSync wfGzDataSync) {/*
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("taskInstUuid", wfGzDataSync.getTaskInstUuid());
		values.put("assignee", wfGzDataSync.getUserId());
		Long count = this.dao.countByNamedQuery("gzDataDoneQuery", values);
		String taskInstUuid = wfGzDataSync.getTaskInstUuid();
		String targetFlowInstUuid = wfGzDataSync.getTargetFlowInstUuid();
		WellptClient wellptClient = getWellptClient(wfGzDataSync);
		// 提交
		Boolean isCompleted = false;
		if (count > 0) {
			// 判断流程是否结束
			isCompleted = flowService.isCompletedByTaskInstUuid(taskInstUuid);
			// 挂职任务提交
			GzTaskSubmitRequest taskSubmitRequest = new GzTaskSubmitRequest();
			taskSubmitRequest.setFlowInstUuid(targetFlowInstUuid);
			if (isCompleted) {
				taskSubmitRequest.setToTaskId(TaskSubmitRequest.END_FLOW_ID);
			} else {
				taskSubmitRequest.setToTaskId(WfGzDataConstant.GZ_TASK_ID);
			}
			Map<String, Object> formData = buildFormData(wfGzDataSync);
			taskSubmitRequest.setFormData(formData);
			GzTaskSubmitResponse taskSubmitResponse = wellptClient.execute(taskSubmitRequest);
			if (!taskSubmitResponse.isSuccess()) {
				throw new RuntimeException(taskSubmitResponse.getMsg());
			}

		}

		// 工作未完成，撤回待办
		if (!isCompleted) {
			GzTaskCancelRequest gzTaskCancelRequest = new GzTaskCancelRequest();
			gzTaskCancelRequest.setFlowInstUuid(targetFlowInstUuid);
			GzTaskCancelResponse gzTaskCancelResponse = wellptClient.execute(gzTaskCancelRequest);
			if (!gzTaskCancelResponse.isSuccess()) {
				throw new RuntimeException(gzTaskCancelResponse.getMsg());
			}
		}

		// 清理与更新数据
		cleanAndUpdate(wfGzDataSync, targetFlowInstUuid);
	*/
    }

    /**
     * 如何描述该方法
     *
     * @param wfGzDataSync
     * @param targetFlowInstUuid
     */
    private void cleanAndUpdate(WfGzDataSync wfGzDataSync, String targetFlowInstUuid) {
        // 删除已同步数据
        WfGzDataSyncHis his = new WfGzDataSyncHis();
        BeanUtils.copyProperties(wfGzDataSync, his);
        his.setUuid(null);
        his.setTargetFlowInstUuid(targetFlowInstUuid);
        this.getCommonDao().save(his);
        this.getCommonDao().deleteByPk(WfGzDataSync.class, wfGzDataSync.getUuid());

        // 更新目标流程实例UUID
        WfGzDataSync example = new WfGzDataSync();
        example.setSourceFlowInstUuid(wfGzDataSync.getSourceFlowInstUuid());
        example.setUserId(wfGzDataSync.getUserId());
        List<WfGzDataSync> wfGzDataSyncs = wfGzDataSyncService.findByExample(example);
        for (WfGzDataSync gzDataSync : wfGzDataSyncs) {
            gzDataSync.setTargetFlowInstUuid(targetFlowInstUuid);
            this.getCommonDao().save(gzDataSync);
        }
    }

    /**
     * @param wfGzDataSync
     * @return
     */
    private boolean isDelete(WfGzDataSync wfGzDataSync) {
        String sourceFlowInstUuid = wfGzDataSync.getSourceFlowInstUuid();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", sourceFlowInstUuid);
        Long count = this.dao.countByNamedQuery("gzFlowInstanceQuery", values);
        return Long.valueOf(0).equals(count);
    }

    /**
     * @param wfGzDataSync
     */
    private void delete(WfGzDataSync wfGzDataSync) {/*
		String targetFlowInstUuid = wfGzDataSync.getTargetFlowInstUuid();
		GzTaskDeleteRequest gzTaskDeleteRequest = new GzTaskDeleteRequest();
		gzTaskDeleteRequest.setFlowInstUuid(targetFlowInstUuid);
		WellptClient wellptClient = getWellptClient(wfGzDataSync);
		GzTaskDeleteResponse gzTaskDeleteResponse = wellptClient.execute(gzTaskDeleteRequest);
		if (!gzTaskDeleteResponse.isSuccess()) {
			throw new RuntimeException(gzTaskDeleteResponse.getMsg());
		}

		// 清理与更新数据
		cleanAndUpdate(wfGzDataSync, targetFlowInstUuid);
	*/
    }

    /**
     * @param wfGzDataSync
     */
    @SuppressWarnings("unchecked")
    private void copyTo(WfGzDataSync wfGzDataSync) {/*
		String targetFlowInstUuid = wfGzDataSync.getTargetFlowInstUuid();
		GzTaskCopyToRequest gzTaskCopyToRequest = new GzTaskCopyToRequest();
		gzTaskCopyToRequest.setFlowInstUuid(targetFlowInstUuid);
		WellptClient wellptClient = getWellptClient(wfGzDataSync);
		Map<String, Object> formData = buildFormData(wfGzDataSync);
		gzTaskCopyToRequest.setFlowDefinitionId(WfGzDataConstant.FLOW_DEF_ID);
		gzTaskCopyToRequest.setToTaskId(WfGzDataConstant.GZ_TASK_ID);
		gzTaskCopyToRequest.setFormData(formData);
		GzTaskCopyToResponse gzTaskDeleteResponse = wellptClient.execute(gzTaskCopyToRequest);
		if (!gzTaskDeleteResponse.isSuccess()) {
			throw new RuntimeException(gzTaskDeleteResponse.getMsg());
		}

		Map<String, String> result = (Map<String, String>) gzTaskDeleteResponse.getData();
		// 清理与更新数据
		cleanAndUpdate(wfGzDataSync, result.get(WfGzDataConstant.FLOW_INST_UUID));
	*/
    }

}
