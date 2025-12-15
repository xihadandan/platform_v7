/*
 * @(#)2013-5-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.pt.bpm.engine.dao.TaskSubFlowDao;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlow;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlowRelation;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.query.FlowShareDataQueryItem;
import com.wellsoft.pt.bpm.engine.support.FlowShareData;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.work.bean.SubTaskData;
import com.wellsoft.pt.workflow.work.bean.TaskInfoDistributionData;
import com.wellsoft.pt.workflow.work.bean.TaskOperationData;
import com.wellsoft.pt.workflow.work.vo.SubTaskDataVo;

import java.util.List;

/**
 * Description: 任务子流程服务接口类
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
public interface TaskSubFlowService extends JpaService<TaskSubFlow, TaskSubFlowDao, String> {

    /**
     * 根据父流程实例UUID及子流程实例UUID，获取父流程任务实例
     *
     * @param parentFlowInstUuid
     * @param flowInstUuid
     * @return
     */
    TaskInstance getParentTaskInstance(String parentFlowInstUuid, String flowInstUuid);

    /**
     * 根据父流程实例UUID及子流程实例UUI，获取任务子流程信息
     *
     * @param parentFlowInstUuid
     * @param flowInstUuid
     * @return
     */
    TaskSubFlow get(String parentFlowInstUuid, String flowInstUuid);

    boolean isShare(String parentFlowInstUuid, String flowInstUuid);

    /**
     * 根据父流程实例UUID判断其是否异步执行
     *
     * @param uuid
     * @return
     */
    boolean isAsync(String parentFlowInstUuid, String flowInstUuid);

    /**
     * 根据父环节实例UUID获取所有子流程信息
     *
     * @param parentTaskInstUuid
     * @return
     */
    List<TaskSubFlow> getAllByParentTaskInstUuid(String parentTaskInstUuid);

    /**
     * 根据父流程实例UUID获取所有子流程信息
     *
     * @param parentFlowInstUuid
     * @return
     */
    List<TaskSubFlow> getAllByParentFlowInstUuid(String parentFlowInstUuid);

    /**
     * 根据流程实例UUID获取流程共享数据信息标题
     * 返回不包含办理信息shareItems
     *
     * @param parentFlowInstUuid
     */
    List<FlowShareData> getUndertakeSituationTitles(String taskInstUuid, String flowInstUuid, String parentFlowInstUuid,
                                                    List<String> subFlowInstUuids, List<FlowShareDataQueryItem> shareDataQueryItems, boolean needSearchAndSort,
                                                    boolean isDefaultSort);

    /**
     * 根据流程实例UUID获取流程共享数据信息
     *
     * @param parentFlowInstUuid
     */
    // List<Map<Object, Object>> getShareData(String parentFlowInstUuid);
    List<FlowShareData> getUndertakeSituationDatas(String taskInstUuid, String flowInstUuid, String parentFlowInstUuid,
                                                   List<String> subFlowInstUuids, List<FlowShareDataQueryItem> shareDataQueryItems, boolean needSearchAndSort,
                                                   boolean isDefaultSort);

    /**
     * 获取流程的承办信息
     * 返回不包含办理信息列表shareItems
     *
     * @param subTaskData
     * @return java.util.List<com.wellsoft.pt.bpm.engine.support.FlowShareData>
     **/
    List<FlowShareData> getUndertakeSituationDatas(SubTaskData subTaskData);

    /**
     * 获取流程的承办信息
     * 返回包含办理信息列表shareItems
     *
     * @param subTaskData
     * @return java.util.List<com.wellsoft.pt.bpm.engine.support.FlowShareData>
     **/
    List<FlowShareData> getUndertakeSituationDatasAll(SubTaskData subTaskData);

    /**
     * 承办信息分页展示
     *
     * @return
     * @author baozh
     * @date 2022/1/4 14:20
     * @params vo
     */
    List<FlowShareData> getUndertakeSituationDatasByPage(SubTaskDataVo vo);

    /**
     * 初始化子流程前后置关系
     *
     * @param parentTaskInstUuid
     * @param subTaskNode
     */
    void initSubTaskRelations(String parentTaskInstUuid, SubTaskNode subTaskNode);

    /**
     * 检验子流程是否可以提交当前流程
     *
     * @param fromTaskId
     * @param flowInstUuid
     */
    void checkSubFlowAllowSubmit(String fromTaskId, String flowInstUuid);

    /**
     * 设置指定的前置流程执行通过
     *
     * @param taskId
     * @param flowInstUuid
     */
    List<TaskSubFlowRelation> updateSubFlowRelationStatus(String taskId, String flowInstUuid, Integer submitStatus);

    /**
     * 判断指定流程的前置流程是否完成
     *
     * @param taskInstance
     * @param flowInstance
     * @return
     */
    boolean isFrontNewFlowFinished(String taskId, String flowInstUuid);

    /**
     * 根据子流程实例的UUID获取其他子流程
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskSubFlow> getOthersBySubFlowInstUuid(String flowInstUuid);

    /**
     * 根据前置流程实例UUID获取后置流程实例列表
     *
     * @param flowInstUuid
     * @return
     */
    List<FlowInstance> getBehindFlowInstanceByFrontFlowInstanceUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskSubFlow> getByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);

    /**
     * 获取信息分发数据
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskInfoDistributionData> getDistributeInfos(String flowInstUuid);

    /**
     * 获取信息分发数据
     *
     * @param parentFlowInstUuid
     * @return
     */
    List<TaskOperationData> getSubflowRelateOperation(String parentFlowInstUuid);

    /**
     * 分页获取信息分发数据
     *
     * @param vo
     * @return
     */
    Page<TaskInfoDistributionData> getDistributeInfosByPage(SubTaskDataVo vo);

    /**
     * 获取子流程相关的操作
     *
     * @param vo
     * @return
     */
    Page<TaskOperationData> getSubflowRelateOperationByPage(SubTaskDataVo vo);

    /**
     * 根据父环节实例的UUID获取子流程的办理人名称
     *
     * @param parentTaskInstUuid
     * @return
     */
    List<String> listTodoUserNameByParentTaskInstUuid(String parentTaskInstUuid);

    /**
     * 根据流程实例UUID，判断子流程所在的状态
     *
     * @param flowInstUuid
     * @param states
     * @return
     */
    boolean isInCompletionStatesByFlowInstUuid(String flowInstUuid, Integer... states);

    /**
     * 根据流程实例UUID列表，过滤所在的状态
     *
     * @param flowInstUuids
     * @param states
     * @return
     */
    List<String> filterCompletionStatesByFlowInstUuids(List<String> flowInstUuids, Integer... states);

    /**
     * 根据父流程实例UUID获取子流程数量
     *
     * @param parentFlowInstUuid
     * @return
     */
    long countByParentFlowInstUuid(String parentFlowInstUuid);

    /**
     * 根据子流程实例UUID获取子流程数量
     *
     * @param flowInstUuid
     * @return 0或1
     */
    long countByFlowInstUuid(String flowInstUuid);

    /**
     * 根据父流程实例UUID获取最新的子流程
     *
     * @param parentFlowInstUuid
     * @return
     */
    TaskSubFlow getLatestOneByParentFlowInstUuid(String parentFlowInstUuid);

}
