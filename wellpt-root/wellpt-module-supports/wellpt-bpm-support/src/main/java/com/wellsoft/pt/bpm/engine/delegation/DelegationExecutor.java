/*
 * @(#)2015-3-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.delegation;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.entity.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 委托执行器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-3-25.1	zhulh		2015-3-25		Create
 * </pre>
 * @date 2015-3-25
 */
public interface DelegationExecutor extends BaseService {

    /**
     * 检测与准备工作委托信息，只返回一个，如果存在多个满足条件的委托，返回最新修改的工作委托信息
     *
     * @param flowUserSid
     * @param todoUserIds
     * @param taskInstance
     * @param flowInstance
     * @param executionContext
     * @return
     */
    TaskDelegation checkedAndPrepareDelegation(final FlowUserSid flowUserSid, final Set<String> todoUserIds,
                                               final TaskInstance taskInstance, final FlowInstance flowInstance, final ExecutionContext executionContext);

    /**
     * 保存工作委托信息及启动跟踪任务
     *
     * @param taskDelegations
     */
    // void saveAndTrace(TaskDelegation taskDelegation);

    /**
     * 委托当前在办工作
     *
     * @param delegationSettings 委托设置
     */
    void delegationCurrentWork(FlowDelegationSettings delegationSettings);

    /**
     * 委托指定在办工作
     *
     * @param taskInstance   环节实例
     * @param taskIdentity   待办标识实例
     * @param taskDelegation 环节委托信息
     */
    void delegationWork(TaskInstance taskInstance, TaskIdentity taskIdentity, TaskDelegation taskDelegation);

    /**
     * 终止时回收受拖人未处理的待办工作
     *
     * @param uuid
     */
    void deactiveToTakeBack(FlowDelegationSettings delegationSettings);

    /**
     * 回收会签委托
     *
     * @param source
     */
    void takeBackCounterSignDelegation(TaskIdentity taskIdentity);

    /**
     * 完成委托
     *
     * @param executionContext
     * @param taskComplete
     */
    void completeDelegation(ExecutionContext executionContext, boolean taskComplete);


    /**
     * 根据环节实例UUID，撤回委托
     *
     * @param taskInstUuid
     */
    void cancelDelegationByTaskInstUuid(String taskInstUuid);

    /**
     * 根据环节待办标识UUID，撤回委托
     *
     * @param taskIdentityUuid
     */
    void cancelDelegationByTaskIdentityUuid(String taskIdentityUuid);

    /**
     * 委托用户粒度大于人员的待办
     *
     * @param userInSids
     * @param taskInstance
     * @param flowInstance
     * @param userTaskDelegationMap
     * @param executionContext
     */
    boolean delegationWorkOfUserInSid(List<FlowUserSid> userInSids, TaskInstance taskInstance, FlowInstance flowInstance,
                                      Map<String, TaskDelegation> userTaskDelegationMap, ExecutionContext executionContext);
}
