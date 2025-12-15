/*
 * @(#)2021年7月14日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.WfFlowDefinitionCleanupConfigDao;
import com.wellsoft.pt.workflow.entity.WfFlowDefinitionCleanupConfigEntity;

/**
 * Description: 流程定义定时清理配置服务接口
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
public interface WfFlowDefinitionCleanupConfigService
        extends JpaService<WfFlowDefinitionCleanupConfigEntity, WfFlowDefinitionCleanupConfigDao, String> {

    /**
     * @param systemUnitId
     * @return
     */
    WfFlowDefinitionCleanupConfigEntity getBySystemUnitId(String systemUnitId);

}
