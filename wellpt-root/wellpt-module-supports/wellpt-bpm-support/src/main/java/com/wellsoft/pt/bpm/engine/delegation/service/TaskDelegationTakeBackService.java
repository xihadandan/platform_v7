/*
 * @(#)2015-3-30 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.delegation.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;

/**
 * Description: 委托工作到期回收接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-3-30.1	zhulh		2015-3-30		Create
 * </pre>
 * @date 2015-3-30
 */
public interface TaskDelegationTakeBackService extends BaseService {

    /**
     * 回收到期的委托工作
     *
     * @param taskDelegationUuid
     */
    public void takeBack(String taskDelegationUuid);

    /**
     * 委托工作会签到期回收
     *
     * @param taskIdentity
     */
    public void takeBackCounterSignDelegation(TaskIdentity taskIdentity);

    /**
     * 手动终止时收回受委托人在委托期间还未处理的待办工作
     *
     * @param delegationSettingsUuid
     */
    public void deactiveToTakeBack(String delegationSettingsUuid);


    /**
     * 委托人提交，回收受托人任务
     *
     * @param taskInstUuid 任务id
     * @param userId       任务处理人id
     */
    void taskSubmitToTakeBack(String taskInstUuid, String userId);

}
