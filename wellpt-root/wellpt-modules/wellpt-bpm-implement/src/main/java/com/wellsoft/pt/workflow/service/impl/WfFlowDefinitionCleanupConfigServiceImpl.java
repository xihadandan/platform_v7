/*
 * @(#)2021年7月14日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.workflow.dao.WfFlowDefinitionCleanupConfigDao;
import com.wellsoft.pt.workflow.entity.WfFlowDefinitionCleanupConfigEntity;
import com.wellsoft.pt.workflow.service.WfFlowDefinitionCleanupConfigService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

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
@Service
public class WfFlowDefinitionCleanupConfigServiceImpl
        extends AbstractJpaServiceImpl<WfFlowDefinitionCleanupConfigEntity, WfFlowDefinitionCleanupConfigDao, String>
        implements WfFlowDefinitionCleanupConfigService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.WfFlowDefinitionCleanupConfigService#getBySystemUnitId(java.lang.String)
     */
    @Override
    public WfFlowDefinitionCleanupConfigEntity getBySystemUnitId(String systemUnitId) {
        WfFlowDefinitionCleanupConfigEntity entity = new WfFlowDefinitionCleanupConfigEntity();
        entity.setSystemUnitId(systemUnitId);
        List<WfFlowDefinitionCleanupConfigEntity> entities = this.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

}
