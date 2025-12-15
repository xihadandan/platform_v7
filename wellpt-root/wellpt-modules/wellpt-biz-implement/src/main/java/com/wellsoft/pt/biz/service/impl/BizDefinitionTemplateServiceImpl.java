/*
 * @(#)11/22/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.biz.dao.BizDefinitionTemplateDao;
import com.wellsoft.pt.biz.entity.BizDefinitionTemplateEntity;
import com.wellsoft.pt.biz.enums.EnumDefinitionTemplateType;
import com.wellsoft.pt.biz.query.BizDefinitionTemplateQueryItem;
import com.wellsoft.pt.biz.service.BizDefinitionTemplateService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
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
 * 11/22/22.1	zhulh		11/22/22		Create
 * </pre>
 * @date 11/22/22
 */
@Service
public class BizDefinitionTemplateServiceImpl extends AbstractJpaServiceImpl<BizDefinitionTemplateEntity, BizDefinitionTemplateDao, String>
        implements BizDefinitionTemplateService {

    /**
     * @param nodeId
     * @param processDefUuid
     * @return
     */
    @Override
    @Transactional
    public ProcessNodeConfig getOrCreateNodeDefinition(String nodeId, String processDefUuid) {
        ProcessNodeConfig processNodeConfig = null;
        BizDefinitionTemplateEntity entity = getByNodeIdAndProcessDefUuid(nodeId, processDefUuid);

        if (entity == null) {
            ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processDefUuid);
            processNodeConfig = parser.getProcessNodeConfigById(nodeId);
            entity = new BizDefinitionTemplateEntity();
            entity.setName(processNodeConfig.getName());
            entity.setType(EnumDefinitionTemplateType.NodeDefinition.getValue());
            entity.setNodeId(processNodeConfig.getId());
            entity.setProcessDefUuid(processDefUuid);
            entity.setDefinitionJson(JsonUtils.object2Json(processNodeConfig));
            this.dao.save(entity);
            ProcessDefinitionUtils.clearCache(processDefUuid);
        } else {
            processNodeConfig = JsonUtils.json2Object(entity.getDefinitionJson(), ProcessNodeConfig.class);
        }

        return processNodeConfig;
    }

    /**
     * @param itemId
     * @param processDefUuid
     * @return
     */
    @Override
    @Transactional
    public ProcessItemConfig getOrCreateItemDefinition(String itemId, String processDefUuid) {
        ProcessItemConfig processItemConfig = null;
        BizDefinitionTemplateEntity entity = getByItemIdAndProcessDefUuid(itemId, processDefUuid);
        if (entity == null) {
            ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processDefUuid);
            processItemConfig = parser.getProcessItemConfigById(itemId);
            entity = new BizDefinitionTemplateEntity();
            entity.setName(processItemConfig.getItemName());
            entity.setType(EnumDefinitionTemplateType.ItemDefinition.getValue());
            entity.setItemId(processItemConfig.getId());
            entity.setProcessDefUuid(processDefUuid);
            entity.setDefinitionJson(JsonUtils.object2Json(processItemConfig));
            this.dao.save(entity);
        } else {
            processItemConfig = JsonUtils.json2Object(entity.getDefinitionJson(), ProcessItemConfig.class);
        }
        return processItemConfig;
    }

    /**
     * @param processDefUuids
     * @param templateTypes
     * @return
     */
    @Override
    public List<BizDefinitionTemplateQueryItem> listItemByProcessDefUuidsAndTypes(List<String> processDefUuids, List<String> templateTypes) {
        Assert.notEmpty(processDefUuids, "业务流程定义UUID不能为空！");
        Assert.notEmpty(templateTypes, "模板类型不能为空！");

        String hql = "select t.uuid as uuid, t.name as name, t.type as type, t.itemId as itemId, t.nodeId as nodeId, t.processDefUuid as processDefUuid from BizDefinitionTemplateEntity t where t.processDefUuid in(:processDefUuids) and t.type in(:types)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("processDefUuids", processDefUuids);
        values.put("types", templateTypes);
        List<BizDefinitionTemplateQueryItem> items = this.listItemHqlQuery(hql, BizDefinitionTemplateQueryItem.class, values);
        return items;
    }

    @Override
    public List<BizDefinitionTemplateEntity> listByProcessDefUuid(String processDefUuid) {
        Assert.hasLength(processDefUuid, "业务流程定义UUID不能为空！");

        BizDefinitionTemplateEntity entity = new BizDefinitionTemplateEntity();
        entity.setProcessDefUuid(processDefUuid);
        return this.dao.listByEntity(entity);
    }

    @Override
    public boolean existsByProcessDefUuidAndTypes(String processDefUuid, List<String> types) {
        Assert.hasLength(processDefUuid, "业务流程定义UUID不能为空！");
        Assert.notEmpty(types, "模板类型不能为空！");

        String hql = "from BizDefinitionTemplateEntity t where t.processDefUuid = :processDefUuid and t.type in(:types)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("processDefUuid", processDefUuid);
        params.put("types", types);
        return this.dao.countByHQL(hql, params) > 0;
    }

    @Override
    @Transactional
    public void deleteByIdAndTypeAndProcessDefUuid(String id, String type, String processDefUuid) {
        Assert.hasLength(id, "事项或阶段ID不能为空！");
        Assert.hasLength(type, "模板类型不能为空！");
        Assert.hasLength(processDefUuid, "业务流程定义UUID不能为空！");

        String hql = "delete from BizDefinitionTemplateEntity t where t.processDefUuid = :processDefUuid and t.type = :type";
        if (EnumDefinitionTemplateType.ItemDefinition.getValue().equals(type)) {
            hql += " and t.itemId = :id";
        } else {
            hql += " and t.nodeId = :id";
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        params.put("type", type);
        params.put("processDefUuid", processDefUuid);
        this.dao.deleteByHQL(hql, params);
    }

    /**
     * @param itemId
     * @param processDefUuid
     * @return
     */
    private BizDefinitionTemplateEntity getByItemIdAndProcessDefUuid(String itemId, String processDefUuid) {
        BizDefinitionTemplateEntity entity = new BizDefinitionTemplateEntity();
        entity.setItemId(itemId);
        entity.setProcessDefUuid(processDefUuid);
        List<BizDefinitionTemplateEntity> entities = this.dao.listByEntity(entity);

        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * @param nodeId
     * @param processDefUuid
     * @return
     */
    private BizDefinitionTemplateEntity getByNodeIdAndProcessDefUuid(String nodeId, String processDefUuid) {
        BizDefinitionTemplateEntity entity = new BizDefinitionTemplateEntity();
        entity.setNodeId(nodeId);
        entity.setProcessDefUuid(processDefUuid);
        List<BizDefinitionTemplateEntity> entities = this.dao.listByEntity(entity);

        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }
        return null;
    }

}
