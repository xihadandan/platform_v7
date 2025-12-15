/*
 * @(#)2014-2-27 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.FlowInstanceParameterDao;
import com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-2-27.1	zhulh		2014-2-27		Create
 * </pre>
 * @date 2014-2-27
 */
public interface FlowInstanceParameterService extends
        JpaService<FlowInstanceParameter, FlowInstanceParameterDao, String> {

    /**
     * 如何描述该方法
     *
     * @param example
     * @return
     */
    List<FlowInstanceParameter> findByExample(FlowInstanceParameter example);

    /**
     * @param flowInstUuid
     * @return
     */
    List<FlowInstanceParameter> getByFlowInstanceUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);

    /**
     * 根据流程实例UUID及参数名称获取总数
     *
     * @param flowInstUuid
     * @param name
     * @return
     */
    long countByFlowInstUuidAndName(String flowInstUuid, String name);

    /**
     * 根据流程实例UUID及参数名称获取流程参数
     *
     * @param flowInstUuid
     * @param name
     * @return
     */
    FlowInstanceParameter getByFlowInstUuidAndName(String flowInstUuid, String name);
}
