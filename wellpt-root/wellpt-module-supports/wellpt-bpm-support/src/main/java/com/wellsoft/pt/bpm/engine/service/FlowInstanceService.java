/*
 * @(#)2013-3-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.FlowInstanceDao;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
import java.util.Map;

/**
 * Description: 流程实例服务接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-17.1	zhulh		2013-3-17		Create
 * </pre>
 * @date 2013-3-17
 */
public interface FlowInstanceService extends JpaService<FlowInstance, FlowInstanceDao, String> {
    /**
     * @param uuid
     * @return
     */
    FlowInstance get(String uuid);

    /**
     * @param taskInstUuid
     * @return
     */
    FlowInstance getByTaskInstUuid(String taskInstUuid);

    /**
     * 保存流程实例
     *
     * @param flowInstance
     */
    void save(FlowInstance flowInstance);

    void merge(FlowInstance flowInstance);

    /**
     * 判断流程的所有子流程是否已经全部结束
     *
     * @param flowInstance
     * @return
     */
    boolean isAllAsyncSubFlowCompleted(FlowInstance flowInstance);

    /**
     * 判断指定的流程定义UUID是否已经被流程实例使用
     *
     * @param uuid
     * @return
     */
    boolean isFlowDefInUse(String flowDefUuid);

    /**
     * 根据流程实例UUID删除流程实例
     *
     * @param flowInstUuid
     */
    void remove(String flowInstUuid);

    /**
     * 判断指定的流程定义UUID是否已经被活动流程实例使用
     *
     * @param flowDefUuid
     * @return
     */
    boolean isFlowActivityDefInUse(String flowDefUuid);

    /**
     * 如何描述该方法
     *
     * @param cOUNT_FLOW_MANAGEMENT
     * @param values
     * @return
     */
    Long countFlowManagement(Map<String, Object> values);

    /**
     * 如何描述该方法
     *
     * @param cOUNT_ALL_FLOW_MANAGEMENT
     * @param values
     * @return
     */
    Long countAllFlowManagement(Map<String, Object> values);

    /**
     * 如何描述该方法
     *
     * @param hql
     * @param values
     * @return
     */
    <X> List<X> find(String queryString, Map<String, Object> values);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     * @return
     */
    boolean isMainFlowInstance(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     * @return
     */
    List<FlowInstance> getUnfinishedSubFlowInstances(String flowInstUuid);

    /**
     * 根据流程实例UUID获取流程定义
     *
     * @param uuid
     * @return
     */
    FlowDefinition getFlowDefinitionByUuid(String flowInstUuid);

    List<FlowDefinition> getFlowDefinitionByids(List<String> ids);

    /**
     * 根据表单数据UUID获取流程实例UUID
     *
     * @param dataUuid
     * @return
     */
    String getUuidByDataUuid(String dataUuid);

    /**
     * 根据流程实例UUID获取流程实例归属系统
     *
     * @param flowInstUuid
     * @return
     */
    String getSystemByUuid(String flowInstUuid);

    boolean isSubFlowInstance(String flowInstUuid);
}
