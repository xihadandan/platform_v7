/*
 * @(#)2013-8-20 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.event;

import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.repository.entity.LogicFileInfo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Description: 事件对象接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-8-20.1	zhulh		2013-8-20		Create
 * </pre>
 * @date 2013-8-20
 */
public interface Event extends Serializable {
    /**
     * 获取当前用户ID
     *
     * @return
     */
    String getUserId();

    /**
     * 获取操作动作(环节事件有效)
     *
     * @return
     */
    String getAction();

    /**
     * 获取操作动作类型(环节事件有效)
     *
     * @return
     */
    String getActionType();

    /**
     * 获取操作动作代码(环节事件有效)
     *
     * @return
     */
    Integer getActionCode();

    /**
     * 获取流转类型代码
     *
     * @return
     */
    Integer getTransferCode();

    /**
     * 获取当前任务ID(环节事件有效)
     *
     * @return
     */
    String getTaskId();

    /**
     * 获取当前任务名称(环节事件有效)
     *
     * @return
     */
    String getTaskName();

    /**
     * 获取当前任务实例UUID(环节事件有效)
     *
     * @return
     */
    String getTaskInstUuid();

    /**
     * 获取签署意见值
     *
     * @return
     */
    String getTaskOpinionValue();

    /**
     * 获取签署意见名称
     *
     * @return
     */
    String getTaskOpinionName();

    /**
     * 获取签署意见内容
     *
     * @return
     */
    String getTaskOpinionText();

    /**
     * 获取签署意见附件
     *
     * @return
     */
    List<LogicFileInfo> getTaskOpinionFiles();

    /**
     * 获取到期时间
     *
     * @return
     */
    Date getDueTime();

    /**
     * 获取是否子流程实例
     *
     * @return
     */
    boolean isSubFlowInstce();

    /**
     * 获取前一个任务ID(流向事件有效)
     *
     * @return
     */
    String getPreTaskId();

    /**
     * 获取下一个任务ID(流向事件有效)
     *
     * @return
     */
    String getNextTaskId();

    /**
     * 获取流向ID(流向事件有效)
     *
     * @return
     */
    String getDirectionId();

    /**
     * 获取当前流程实例ID
     *
     * @return
     */
    String getFlowInstId();

    /**
     * 获取当前流程实例UUID
     *
     * @return
     */
    String getFlowInstUuid();

    /**
     * 获取当前流程实例标题
     *
     * @return
     */
    String getTitle();

    /**
     * 获取当前流程发起人ID
     *
     * @return
     */
    String getFlowStartUserId();

    /**
     * 获取当前流程所有者ID
     *
     * @return
     */
    String getFlowOwnerId();

    /**
     * 获取当前流程实例所属部门ID
     *
     * @return
     */
    String getFlowStartDepartmentId();

    /**
     * 获取当前流程实例所属单位ID
     *
     * @return
     */
    String getFlowStartUnitId();

    /**
     * 获取当前流程实例所属部门ID
     *
     * @return
     */
    String getFlowOwnerDepartmentId();

    /**
     * 获取当前流程实例所属单位ID
     *
     * @return
     */
    String getFlowOwnerUnitId();

    /**
     * 获取流程定义UUID
     *
     * @return
     */
    String getFlowDefUuid();

    /**
     * 获取流程定义ID
     *
     * @return
     */
    String getFlowId();

    /**
     * 获取流程名称
     *
     * @return
     */
    String getFlowName();

    /**
     * 获取动态表单定义UUID
     *
     * @return
     */
    String getFormUuid();

    /**
     * 获取动态表单数据UUID
     *
     * @return
     */
    String getDataUuid();

    /**
     * 获取动态表单数据
     *
     * @return
     */
    DyFormData getDyFormData();

    /**
     * 获取动态环节数据
     *
     * @return
     */
    TaskData getTaskData();

    /**
     * 获取预留文本字段1
     *
     * @return
     */
    String getReservedText1();

    /**
     * 获取预留文本字段2
     *
     * @return
     */
    String getReservedText2();

    /**
     * 获取预留文本字段3
     *
     * @return
     */
    String getReservedText3();

    /**
     * 获取预留文本字段4
     *
     * @return
     */
    String getReservedText4();

    /**
     * 获取预留文本字段5
     *
     * @return
     */
    String getReservedText5();

    /**
     * 获取预留文本字段6
     *
     * @return
     */
    String getReservedText6();

    /**
     * 获取预留文本字段7
     *
     * @return
     */
    String getReservedText7();

    /**
     * 获取预留文本字段8
     *
     * @return
     */
    String getReservedText8();

    /**
     * 获取预留文本字段9
     *
     * @return
     */
    String getReservedText9();

    /**
     * 获取预留文本字段10
     *
     * @return
     */
    String getReservedText10();

    /**
     * 获取预留文本字段11
     *
     * @return
     */
    String getReservedText11();

    /**
     * 获取预留文本字段12
     *
     * @return
     */
    String getReservedText12();

    /**
     * 获取预留数值字段1
     *
     * @return
     */
    Integer getReservedNumber1();

    /**
     * 获取预留数值字段2
     *
     * @return
     */
    Double getReservedNumber2();

    /**
     * 获取预留数值字段3
     *
     * @return
     */
    Double getReservedNumber3();

    /**
     * 获取预留日期字段1
     *
     * @return
     */
    Date getReservedDate1();

    /**
     * 获取预留日期字段2
     *
     * @return
     */
    Date getReservedDate2();
}
