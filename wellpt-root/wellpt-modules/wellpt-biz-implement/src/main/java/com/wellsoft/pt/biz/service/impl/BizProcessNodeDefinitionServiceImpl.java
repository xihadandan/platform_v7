/*
 * @(#)11/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.biz.dao.BizProcessNodeDefinitionDao;
import com.wellsoft.pt.biz.entity.BizProcessNodeDefinitionEntity;
import com.wellsoft.pt.biz.service.BizProcessNodeDefinitionService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
 * 11/13/23.1	zhulh		11/13/23		Create
 * </pre>
 * @date 11/13/23
 */
@Service
public class BizProcessNodeDefinitionServiceImpl extends AbstractJpaServiceImpl<BizProcessNodeDefinitionEntity, BizProcessNodeDefinitionDao, String> implements BizProcessNodeDefinitionService {

    /**
     * 根据业务流程定义UUID获取过程节点定义信息
     *
     * @param processDefUuid
     * @return
     */
    @Override
    public List<BizProcessNodeDefinitionEntity> listByProcessDefUuid(String processDefUuid) {
        Assert.hasText(processDefUuid, "业务流程定义UUID不能为空！");

        BizProcessNodeDefinitionEntity entity = new BizProcessNodeDefinitionEntity();
        entity.setProcessDefUuid(processDefUuid);
        return this.listByEntity(entity);
    }

    /**
     * 根据业务流程定义UUID删除过程节点定义信息
     *
     * @param processDefUuid
     */
    @Override
    @Transactional
    public void deleteByProcessDefUuid(String processDefUuid) {
        Assert.hasText(processDefUuid, "业务流程定义UUID不能为空！");

        String hql = "delete from BizProcessNodeDefinitionEntity t where t.processDefUuid = :processDefUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("processDefUuid", processDefUuid);
        this.dao.deleteByHQL(hql, params);
    }

}
