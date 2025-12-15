/*
 * @(#)2019年9月5日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.support.FlowDataDyformFieldModifyInfo;
import com.wellsoft.pt.bpm.engine.support.FlowDataSnapshot;
import com.wellsoft.pt.bpm.engine.support.FlowDataSnapshotAuditLog;
import com.wellsoft.pt.bpm.engine.support.TaskData;

import java.util.List;

/**
 * Description: 流程取样器服务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年9月5日.1	zhulh		2019年9月5日		Create
 * </pre>
 * @date 2019年9月5日
 */
public interface FlowSamplerService {

    /**
     * 创建流程数据快照
     *
     * @param taskData
     * @param taskOperationUuid
     * @param taskInstUuid
     * @param flowInstance
     */
    void createSnapshot(TaskData taskData, String taskOperationUuid, String taskInstUuid, FlowInstance flowInstance);

    /**
     * 根据流程定义UUID获取数据快照列表
     *
     * @param flowInstUuid
     * @return
     */
    List<FlowDataSnapshot> listWithoutDyformDataByFlowInstUuid(String flowInstUuid);

    /**
     * 根据快照ID获取快照数据
     *
     * @param id
     * @return
     */
    String getAsStringById(String id);

    /**
     * 判断指定的流程ID是否可生成快照数据
     *
     * @param flowDefId
     * @return
     */
    boolean isSampled(String flowDefId);

    /**
     * @param taskData
     * @param flowInstance
     * @return
     */
    boolean isSampled(TaskData taskData, FlowInstance flowInstance);

    /**
     * @param objectId
     * @return
     */
    FlowDataSnapshotAuditLog getFlowDataSnapshotAuditLogByObjecId(String objectId);

    /**
     * @param flowInstUuid
     * @param fieldNames
     * @return
     */
    FlowDataDyformFieldModifyInfo getDyformFieldModifyInfo(String flowInstUuid, List<String> fieldNames);
}
