/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.biz.dao.BizProcessDefinitionDao;
import com.wellsoft.pt.biz.entity.BizDefinitionTemplateEntity;
import com.wellsoft.pt.biz.entity.BizProcessDefinitionEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemDefinitionEntity;
import com.wellsoft.pt.biz.entity.BizProcessNodeDefinitionEntity;
import com.wellsoft.pt.biz.enums.EnumBizBiType;
import com.wellsoft.pt.biz.enums.EnumDefinitionTemplateType;
import com.wellsoft.pt.biz.query.BizProcessDefinitionQueryItem;
import com.wellsoft.pt.biz.service.BizDefinitionTemplateService;
import com.wellsoft.pt.biz.service.BizProcessDefinitionService;
import com.wellsoft.pt.biz.service.BizProcessItemDefinitionService;
import com.wellsoft.pt.biz.service.BizProcessNodeDefinitionService;
import com.wellsoft.pt.biz.support.*;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.facade.service.WfFlowBusinessDefinitionFacadeService;
import com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@Service
public class BizProcessDefinitionServiceImpl extends AbstractJpaServiceImpl<BizProcessDefinitionEntity, BizProcessDefinitionDao, String> implements BizProcessDefinitionService {

    @Autowired
    private BizProcessNodeDefinitionService processNodeDefinitionService;

    @Autowired
    private BizProcessItemDefinitionService processItemDefinitionService;

    @Autowired
    private BizDefinitionTemplateService definitionTemplateService;

    @Autowired
    private WfFlowBusinessDefinitionFacadeService flowBusinessDefinitionFacadeService;

    /**
     * 根据ID获取业务流程定义
     *
     * @param id
     * @return
     */
    @Override
    public BizProcessDefinitionEntity getById(String id) {
        List<BizProcessDefinitionEntity> entities = this.dao.listByFieldEqValue("id", id);
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        return entities.get(0);
    }

    /**
     * 保存业务流程定义
     *
     * @param entity
     */
    @Override
    @Transactional
    public void saveDefinition(BizProcessDefinitionEntity entity) {
        this.save(entity);

        List<BizDefinitionTemplateEntity> templateEntities = definitionTemplateService.listByProcessDefUuid(entity.getUuid());
        ProcessDefinitionJson processDefinitionJson = entity2ProcessDefinitionJson(entity, templateEntities);
        // 更新新版本的定义JSON信息
        if (!StringUtils.equals(entity.getUuid(), processDefinitionJson.getUuid())) {
            BeanUtils.copyProperties(entity, processDefinitionJson);
            entity.setDefinitionJson(JsonUtils.object2Json(processDefinitionJson));
            this.save(entity);
        }

        String processDefUuid = entity.getUuid();
        List<BizProcessNodeDefinitionEntity> nodeDefinitionEntities = processNodeDefinitionService.listByProcessDefUuid(processDefUuid);
        List<BizProcessItemDefinitionEntity> itemDefinitionEntities = processItemDefinitionService.listByProcessDefUuid(processDefUuid);

        ProcessDefinitionJsonParser parser = new ProcessDefinitionJsonParser(processDefinitionJson);

        // 删除的过程节点及事项
        deleteNodeOrItemDefinition(nodeDefinitionEntities, itemDefinitionEntities, parser);

        // 新增或更新的过程节点及事项
        saveOrUpdateNodeOrItemDefinition(nodeDefinitionEntities, itemDefinitionEntities, parser);

        // 更新定义模板
        if (CollectionUtils.isNotEmpty(templateEntities)) {
            updateDefinitionTemplate(templateEntities, parser);
        }

        // 同步流程业务状态管理
        syncFlowBusiness(parser);
    }

    /**
     * 更新定义模板
     *
     * @param templateEntities
     * @param parser
     */
    private void updateDefinitionTemplate(List<BizDefinitionTemplateEntity> templateEntities, ProcessDefinitionJsonParser parser) {
        List<BizDefinitionTemplateEntity> toUpdateEntities = templateEntities.stream().filter(templateEntity -> {
            return EnumDefinitionTemplateType.ItemDefinition.getValue().equals(templateEntity.getType())
                    || EnumDefinitionTemplateType.NodeDefinition.getValue().equals(templateEntity.getType());
        }).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(toUpdateEntities)) {
            toUpdateEntities.forEach(templateEntity -> {
                if (EnumDefinitionTemplateType.ItemDefinition.getValue().equals(templateEntity.getType())) {
                    ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(templateEntity.getItemId());
                    if (processItemConfig != null) {
                        templateEntity.setDefinitionJson(JsonUtils.object2Json(processItemConfig));
                    }
                } else {
                    ProcessNodeConfig processNodeConfig = parser.getProcessNodeConfigById(templateEntity.getNodeId());
                    if (processNodeConfig != null) {
                        templateEntity.setDefinitionJson(JsonUtils.object2Json(processNodeConfig));
                    }
                }
            });
            definitionTemplateService.saveAll(toUpdateEntities);
        }
    }

    /**
     * 同步流程业务
     *
     * @param parser
     */
    private void syncFlowBusiness(ProcessDefinitionJsonParser parser) {
        List<ProcessItemConfig> processItemConfigs = parser.getAllItemConfig();
        List<ProcessItemConfig.BusinessIntegrationConfig> businessIntegrationConfigs = processItemConfigs.stream().filter(config -> config.getBusinessIntegrationConfigByType(EnumBizBiType.Workflow.getValue()) != null)
                .map(config -> config.getBusinessIntegrationConfigByType(EnumBizBiType.Workflow.getValue()))
                .collect(Collectors.toList());

        Map<String, FlowBusinessDefinitionJson> flowBizMap = Maps.newHashMap();
        businessIntegrationConfigs.forEach(config -> {
            String flowDefId = config.getFlowDefId();
            if (StringUtils.isNotBlank(flowDefId) && !flowBizMap.containsKey(flowDefId)) {
                FlowBusinessDefinitionJson businessDefinitionJson = new FlowBusinessDefinitionJson();
                businessDefinitionJson.setName("流程业务_" + flowDefId);
                businessDefinitionJson.setId("flow_biz_" + flowDefId);
                businessDefinitionJson.setFlowDefId(flowDefId);
                businessDefinitionJson.setStates(config.getStates());
                flowBizMap.put(config.getFlowDefId(), businessDefinitionJson);
            }
        });
        flowBusinessDefinitionFacadeService.saveAll(flowBizMap.values());
    }

    /**
     * 删除的过程节点及事项
     *
     * @param nodeDefinitionEntities
     * @param itemDefinitionEntities
     * @param parser
     */
    private void deleteNodeOrItemDefinition(List<BizProcessNodeDefinitionEntity> nodeDefinitionEntities, List<BizProcessItemDefinitionEntity> itemDefinitionEntities, ProcessDefinitionJsonParser parser) {
        List<BizProcessNodeDefinitionEntity> deletedNodes = Lists.newArrayList();
        List<BizProcessItemDefinitionEntity> deletedItems = Lists.newArrayList();

        List<String> nodeIds = parser.getAllNodeId();
        List<String> itemIds = parser.getAllItemId();
        nodeDefinitionEntities.forEach(nodeDefinition -> {
            if (!nodeIds.contains(nodeDefinition.getId())) {
                deletedNodes.add(nodeDefinition);
            }
        });
        itemDefinitionEntities.forEach(itemDefinition -> {
            if (!itemIds.contains(itemDefinition.getItemId())) {
                deletedItems.add(itemDefinition);
            }
        });
        processNodeDefinitionService.deleteByEntities(deletedNodes);
        processItemDefinitionService.deleteByEntities(deletedItems);

        nodeDefinitionEntities.removeAll(deletedNodes);
        itemDefinitionEntities.removeAll(deletedItems);
    }

    /**
     * 新增或更新的过程节点及事项
     *
     * @param nodeDefinitionEntities
     * @param itemDefinitionEntities
     * @param parser
     */
    private void saveOrUpdateNodeOrItemDefinition(List<BizProcessNodeDefinitionEntity> nodeDefinitionEntities, List<BizProcessItemDefinitionEntity> itemDefinitionEntities, ProcessDefinitionJsonParser parser) {
        List<String> nodeIds = parser.getAllNodeId();
        List<String> itemIds = parser.getAllItemId();

        Map<String, BizProcessNodeDefinitionEntity> nodeMap = ConvertUtils.convertElementToMap(nodeDefinitionEntities, "id");
        Map<String, BizProcessItemDefinitionEntity> itemMap = ConvertUtils.convertElementToMap(itemDefinitionEntities, "itemId");
        List<BizProcessNodeDefinitionEntity> nodeEntities = Lists.newArrayList();
        List<BizProcessItemDefinitionEntity> itemEntities = Lists.newArrayList();

        for (int index = 0; index < nodeIds.size(); index++) {
            String nodeId = nodeIds.get(index);
            BizProcessNodeDefinitionEntity nodeDefinition = nodeMap.get(nodeId);
            if (nodeDefinition == null) {
                nodeDefinition = new BizProcessNodeDefinitionEntity();
            }
            ProcessNodeConfig nodeConfig = parser.getProcessNodeConfigById(nodeId);
            nodeDefinition.setName(nodeConfig.getName());
            nodeDefinition.setId(nodeConfig.getId());
            nodeDefinition.setCode(nodeConfig.getCode());
            nodeDefinition.setExtAttrsJson(JsonUtils.object2Json(nodeConfig.getExtAttrs()));
            nodeDefinition.setSortOrder(index);
            nodeDefinition.setParentId(parser.getParentIdOfNode(nodeId));
            nodeDefinition.setProcessDefUuid(parser.getProcessDefUuid());
            nodeDefinition.setRefProcessDefUuid(nodeConfig.getRefProcessDefUuid());
            nodeEntities.add(nodeDefinition);
        }

        for (int index = 0; index < itemIds.size(); index++) {
            String itemId = itemIds.get(index);
            BizProcessItemDefinitionEntity itemDefinition = itemMap.get(itemId);
            if (itemDefinition == null) {
                itemDefinition = new BizProcessItemDefinitionEntity();
            }
            ProcessItemConfig itemConfig = parser.getProcessItemConfigById(itemId);
            itemDefinition.setItemDefName(itemConfig.getItemDefName());
            itemDefinition.setItemDefId(itemConfig.getItemDefId());
            itemDefinition.setItemName(itemConfig.getItemName());
            itemDefinition.setItemId(itemConfig.getId());
            itemDefinition.setItemCode(itemConfig.getItemCode());
            itemDefinition.setItemType(itemConfig.getItemType());
            itemDefinition.setSortOrder(index);
            itemDefinition.setProcessNodeId(parser.getParentNodeIdOfItem(itemConfig.getId()));
            itemDefinition.setProcessDefUuid(parser.getProcessDefUuid());
            itemDefinition.setRefProcessDefUuid(itemConfig.getRefProcessDefUuid());
            itemEntities.add(itemDefinition);
        }

        processNodeDefinitionService.saveAll(nodeEntities);
        processItemDefinitionService.saveAll(itemEntities);
    }

    /**
     * 根据ID获取数量
     *
     * @param id
     * @return
     */
    @Override
    public Long countById(String id) {
        Assert.hasText(id, "业务流程ID不能为空！");

        BizProcessDefinitionEntity entity = new BizProcessDefinitionEntity();
        entity.setId(id);
        return this.dao.countByEntity(entity);
    }

    @Override
    public long countByEntity(BizProcessDefinitionEntity entity) {
        return this.dao.countByEntity(entity);
    }

    /**
     * 根据UUID获取最大版本号
     *
     * @param uuid
     * @return
     */
    @Override
    public Double getMaxVersionByUuid(String uuid) {
        Assert.hasText(uuid, "业务流程UUID不能为空！");

        String hql = "select max(t.version) as version from BizProcessDefinitionEntity t where t.uuid = :uuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        return this.dao.getNumberByHQL(hql, values, Double.class);
    }

    /**
     * 根据ID判断业务流程定义是否存在
     *
     * @param id
     * @return
     */
    @Override
    public boolean isExistsById(String id) {
        Assert.hasText(id, "业务流程ID不能为空！");

        BizProcessDefinitionEntity entity = new BizProcessDefinitionEntity();
        entity.setId(id);
        return this.dao.countByEntity(entity) > 0;
    }

    /**
     * 根据业务ID列表获取业务流程定义数量
     *
     * @param businessIds
     * @return
     */
    @Override
    public Long countByBusinessIds(List<String> businessIds) {
        String hql = "from BizProcessDefinitionEntity t where t.businessId in(:businessIds)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("businessIds", businessIds);
        return this.dao.countByHQL(hql, values);
    }

    /**
     * 根据UUID获取业务流程定义JSON
     *
     * @param uuid
     * @return
     */
    @Override
    public ProcessDefinitionJson getProcessDefinitionJsonByUuid(String uuid) {
        BizProcessDefinitionEntity entity = this.getOne(uuid);
        List<BizDefinitionTemplateEntity> templateEntities = definitionTemplateService.listByProcessDefUuid(uuid);
        return entity2ProcessDefinitionJson(entity, templateEntities);
    }

    /**
     * 根据ID获取业务流程定义JSON
     *
     * @param id
     * @return
     */
    @Override
    public ProcessDefinitionJson getProcessDefinitionJsonById(String id) {
        BizProcessDefinitionEntity entity = this.getById(id);
        List<BizDefinitionTemplateEntity> templateEntities = definitionTemplateService.listByProcessDefUuid(entity.getUuid());
        return entity2ProcessDefinitionJson(entity, templateEntities);
    }

    /**
     * 根据业务流程配置项模板UUID列表判断是否被使用
     *
     * @param templateUuids
     * @return
     */
    @Override
    public boolean isUsedDefinitionTemplateByTemplateUuids(List<String> templateUuids) {
        if (CollectionUtils.isEmpty(templateUuids)) {
            return false;
        }

        String hql = "from BizProcessDefinitionEntity t where t.definitionJson like '%:templateUuid%'";
        Map<String, Object> values = Maps.newHashMap();
        for (String templateUuid : templateUuids) {
            values.put("templateUuid", templateUuid);
            long count = this.dao.countByHQL(hql, values);
            if (count > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据流程定义UUID列表删除流程定义
     *
     * @param uuids
     */
    @Override
    @Transactional
    public void deleteAllByUuids(List<String> uuids) {
        uuids.forEach(uuid -> {
            processNodeDefinitionService.deleteByProcessDefUuid(uuid);
            processItemDefinitionService.deleteByProcessDefUuid(uuid);
        });

        this.deleteByUuids(uuids);
    }

    @Override
    public List<String> listUuidByBusinessId(String businessId, String excludeDefId) {
        String hql = "select t.uuid from BizProcessDefinitionEntity t where t.enabled = true ";
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(businessId)) {
            hql += " and t.businessId = :businessId";
            params.put("businessId", businessId);
        }
        if (StringUtils.isNotBlank(excludeDefId)) {
            hql += " and t.id <> :excludeDefId";
            params.put("excludeDefId", excludeDefId);
        }
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    @Override
    public List<BizProcessDefinitionQueryItem> listOfRefTemplate(String refId, String templateType, String processDefUuid) {
        Assert.hasLength(refId, "引用ID不能为空！");
        Assert.hasLength(templateType, "引用模板类型不能为空！");
        Assert.hasLength(processDefUuid, "业务流程定义UUID不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        if (EnumDefinitionTemplateType.ItemDefinition.getValue().equals(templateType)) {
            params.put("itemId", refId);
        } else {
            params.put("nodeId", refId);
        }
        params.put("templateType", templateType);
        params.put("processDefUuid", processDefUuid);
        return this.dao.listItemByNameSQLQuery("listProcessDefinitionOfRefTemplate", BizProcessDefinitionQueryItem.class, params, null);
    }

    private ProcessDefinitionJson entity2ProcessDefinitionJson(BizProcessDefinitionEntity entity, List<BizDefinitionTemplateEntity> templateEntities) {
        if (entity == null) {
            return new ProcessDefinitionJson();
        }

        List<DefinitionTemplateInfo> templateInfos = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(templateEntities)) {
            templateInfos.addAll(BeanUtils.copyCollection(templateEntities, DefinitionTemplateInfo.class));
        }

        String definitionJson = entity.getDefinitionJson();
        if (StringUtils.isBlank(definitionJson)) {
            ProcessDefinitionJson processDefinitionJson = new ProcessDefinitionJson();
            BeanUtils.copyProperties(entity, processDefinitionJson);
            processDefinitionJson.setDefinitionTemplates(templateInfos);
            return processDefinitionJson;
        }

        ProcessDefinitionJson processDefinitionJson = JsonUtils.json2Object(definitionJson, ProcessDefinitionJson.class);
        processDefinitionJson.setDefinitionTemplates(templateInfos);
        return processDefinitionJson;
    }

}
