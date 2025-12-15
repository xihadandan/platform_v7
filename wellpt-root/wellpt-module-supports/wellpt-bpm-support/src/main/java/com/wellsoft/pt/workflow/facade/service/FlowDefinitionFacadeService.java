/*
 * @(#)2021年7月14日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.bpm.engine.dto.FlowDefinitionDto;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;

import java.util.Collection;
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
 * 2021年7月14日.1	zhulh		2021年7月14日		Create
 * </pre>
 * @date 2021年7月14日
 */
public interface FlowDefinitionFacadeService extends Facade {


    /**
     * @param flowDefId
     * @return
     */
    FlowDefinitionDto getById(String flowDefId);

    /**
     * 根据流程定义UUID进行逻辑删除
     *
     * @param uuid
     */
    void logicalDelete(String uuid);

    /**
     * 根据流程定义UUID列表进行逻辑删除
     *
     * @param uuids
     */
    void logicalDeleteAll(Collection<String> uuids);

    /**
     * 根据流程定义UUID进行恢复
     *
     * @param uuid
     */
    void recovery(String uuid);

    /**
     * 根据流程定义UUID列表进行恢复
     *
     * @param uuids
     */
    void recoveryAll(Collection<String> uuids);

    /**
     * 根据流程定义UUID进行物理删除
     *
     * @param uuid
     */
    void physicalDelete(String uuid);

    /**
     * 根据流程定义UUID列表进行物理删除
     *
     * @param uuids
     */
    void physicalDeleteAll(Collection<String> uuids);

    /**
     * 是否有启用定时删除功能
     *
     * @return
     */
    boolean isEnableCleanup();

    /**
     * @param deleteStatus
     * @param pagingInfo
     * @return
     */
    List<FlowDefinition> listByDeleteStatus(int deleteStatus, PagingInfo pagingInfo);

    /**
     * @param deleteStatus
     * @param systemUnitId
     * @param pagingInfo
     * @return
     */
    List<FlowDefinition> listByDeleteStatusAndSystemUnitId(int deleteStatus, String systemUnitId,
                                                           PagingInfo pagingInfo);

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    boolean isFlowDefinitionInUse(String uuid);

    /**
     * @param uuid
     * @param code
     */
    void updateDeleteStatusByUuid(String uuid, Integer deleteStatus);

    /**
     * @param uuid
     */
    void autoCleanUp(String uuid);

}
