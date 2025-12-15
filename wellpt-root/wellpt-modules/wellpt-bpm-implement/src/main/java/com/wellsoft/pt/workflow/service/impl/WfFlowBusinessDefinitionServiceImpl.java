/*
 * @(#)11/11/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.workflow.dao.WfFlowBusinessDefinitionDao;
import com.wellsoft.pt.workflow.entity.WfFlowBusinessDefinitionEntity;
import com.wellsoft.pt.workflow.service.WfFlowBusinessDefinitionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

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
@Service
public class WfFlowBusinessDefinitionServiceImpl extends AbstractJpaServiceImpl<WfFlowBusinessDefinitionEntity,
        WfFlowBusinessDefinitionDao, String> implements WfFlowBusinessDefinitionService {

    /**
     * 根据ID获取总数
     *
     * @param id
     * @return
     */
    @Override
    public Long countById(String id) {
        Assert.hasText(id, "流程业务定义ID不能为空！");

        WfFlowBusinessDefinitionEntity entity = new WfFlowBusinessDefinitionEntity();
        entity.setId(id);
        return this.dao.countByEntity(entity);
    }

    /**
     * 根据ID获取流程定义ID
     *
     * @param id
     * @return
     */
    @Override
    public String getFlowDefIdById(String id) {
        Assert.hasText(id, "流程业务定义ID不能为空！");

        String hql = "select t.flowDefId as flowDefId from WfFlowBusinessDefinitionEntity t where t.id = :id";
        Map<String, Object> values = Maps.newHashMap();
        values.put("id", id);
        List<String> flowDefIds = this.dao.listCharSequenceByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(flowDefIds)) {
            return flowDefIds.get(0);
        }
        return null;
    }

    /**
     * 根据ID获取流程业务定义
     *
     * @param id
     * @return
     */
    @Override
    public WfFlowBusinessDefinitionEntity getById(String id) {
        Assert.hasText(id, "流程业务定义ID不能为空！");

        WfFlowBusinessDefinitionEntity entity = new WfFlowBusinessDefinitionEntity();
        entity.setId(id);
        List<WfFlowBusinessDefinitionEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }

        return null;
    }

    /**
     * 根据流程定义ID获取流程业务
     *
     * @param flowDefId
     * @return
     */
    @Override
    public WfFlowBusinessDefinitionEntity getByFlowDefId(String flowDefId) {
        Assert.hasText(flowDefId, "流程定义ID不能为空！");

        WfFlowBusinessDefinitionEntity entity = new WfFlowBusinessDefinitionEntity();
        entity.setFlowDefId(flowDefId);
        List<WfFlowBusinessDefinitionEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }

        return null;
    }

}
