/*
 * @(#)11/11/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.WfFlowBusinessDefinitionDao;
import com.wellsoft.pt.workflow.entity.WfFlowBusinessDefinitionEntity;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/11/22.1	zhulh		11/11/22		Create
 * </pre>
 * @date 11/11/22
 */
public interface WfFlowBusinessDefinitionService extends JpaService<WfFlowBusinessDefinitionEntity, WfFlowBusinessDefinitionDao, String> {
    /**
     * 根据ID获取总数
     *
     * @param id
     * @return
     */
    Long countById(String id);

    /**
     * 根据ID获取流程定义ID
     *
     * @param id
     * @return
     */
    String getFlowDefIdById(String id);

    /**
     * 根据ID获取流程业务定义
     *
     * @param id
     * @return
     */
    WfFlowBusinessDefinitionEntity getById(String id);

    /**
     * 根据流程定义ID获取流程业务
     *
     * @param flowDefId
     * @return
     */
    WfFlowBusinessDefinitionEntity getByFlowDefId(String flowDefId);
}
