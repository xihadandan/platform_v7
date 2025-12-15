/*
 * @(#)2013-5-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskSubFlowDispatchDao;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlowDispatch;
import com.wellsoft.pt.bpm.engine.support.NewFlow;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
import java.util.Map;

/**
 * Description: 任务子流程分发服务接口类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年10月29日.1	zhulh		2021年10月29日		Create
 * </pre>
 * @date 2021年10月29日
 */
public interface TaskSubFlowDispatchService extends JpaService<TaskSubFlowDispatch, TaskSubFlowDispatchDao, String> {

    /**
     * 分发子流程
     *
     * @param newFlow
     * @param parentFlowInstance
     * @param parentTaskInstance
     * @param taskData
     * @param users
     * @param todoId
     * @param todoName
     * @param monitors
     * @param b
     * @return
     */
    String dispatchSubFlow(NewFlow newFlow, FlowInstance parentFlowInstance, TaskInstance parentTaskInstance,
                           TaskData taskData, List<String> users, String todoId, String todoName, List<String> monitors,
                           boolean async);

    /**
     * 分发子流程信息
     *
     * @param dispatchUuid
     * @param options
     */
    void dispatchByUuid(String dispatchUuid, Map<String, Object> options);

    /**
     * 获取要分发的子流程
     *
     * @return
     */
    List<TaskSubFlowDispatch> list2Dispatching();

    /**
     * 获取要分发的主流程实例UUID
     *
     * @return
     */
    List<String> listParentFlowInstUuidOfDispatching();

    /**
     * @param parentFlowInstUuid
     * @return
     */
    List<TaskSubFlowDispatch> list2DispatchingByParenFlowInstUuid(String parentFlowInstUuid);

    /**
     * 标记分发错误
     *
     * @param uuid
     * @param message
     */
    void markDispatchErrorByUuid(String uuid, String message);

    /**
     * 根据主流程实例UUID，获取分发中的子流程数量
     *
     * @param parentFlowInstUuid
     * @return
     */
    long countDispatchingByParentFlowInstUuid(String parentFlowInstUuid);

    /**
     * 根据主环节实例UUID，获取分发中的子流程数量
     *
     * @param parentTaskInstUuid
     * @return
     */
    long countDispatchingByParentTaskInstUuid(String parentTaskInstUuid);

    /**
     * 根据主流程实例UUID，获取分发中、分发失败的子流程数量
     *
     * @param parentFlowInstUuid
     * @return
     */
    long countDispatchingAndErrorByParentFlowInstUuid(String parentFlowInstUuid);

    /**
     * 重新分发状态为分发失败的子流程
     *
     * @param parentTaskInstUuid
     */
    void resendByParentTaskInstUuid(String parentTaskInstUuid);

    /**
     * @param flowInstUuid
     * @return
     */
    List<TaskSubFlowDispatch> getByFlowInstUuid(String flowInstUuid);

    /**
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);

    /**
     * @param parentFlowInstUuid
     * @return
     */
    List<TaskSubFlowDispatch> listByParentFlowInstUuid(String parentFlowInstUuid);

    void updateDispatchMsgByUuid(String uuid, String resultMsg);
}
