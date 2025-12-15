/*
 * @(#)9/28/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.biz.dto.BizProcessDefinitionDto;
import com.wellsoft.pt.biz.dto.BizProcessItemDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeDto;
import com.wellsoft.pt.biz.entity.BizProcessDefinitionEntity;
import com.wellsoft.pt.biz.entity.BizProcessDefinitionHistoryEntity;
import com.wellsoft.pt.biz.enums.EnumDefinitionTemplateType;
import com.wellsoft.pt.biz.enums.EnumItemType;
import com.wellsoft.pt.biz.facade.service.BizProcessDefinitionFacadeService;
import com.wellsoft.pt.biz.listener.BizProcessItemListener;
import com.wellsoft.pt.biz.listener.BizProcessListener;
import com.wellsoft.pt.biz.listener.BizProcessNodeListener;
import com.wellsoft.pt.biz.listener.RuntimeListener;
import com.wellsoft.pt.biz.query.BizDefinitionTemplateQueryItem;
import com.wellsoft.pt.biz.query.BizProcessDefinitionQueryItem;
import com.wellsoft.pt.biz.service.BizDefinitionTemplateService;
import com.wellsoft.pt.biz.service.BizProcessDefinitionHistoryService;
import com.wellsoft.pt.biz.service.BizProcessDefinitionService;
import com.wellsoft.pt.biz.service.BizProcessInstanceService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
 * 9/28/22.1	zhulh		9/28/22		Create
 * </pre>
 * @date 9/28/22
 */
@Service
public class BizProcessDefinitionFacadeServiceImpl extends AbstractApiFacade implements BizProcessDefinitionFacadeService {

    @Autowired
    private BizProcessDefinitionService bizProcessDefinitionService;

    @Autowired
    private BizProcessDefinitionHistoryService bizProcessDefinitionHistoryService;

    @Autowired
    private BizProcessInstanceService bizProcessInstanceService;

    @Autowired
    private BizDefinitionTemplateService definitionTemplateService;

//    @Autowired
//    private BizItemDefinitionService bizItemDefinitionService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired(required = false)
    private Map<String, BizProcessListener> processListenerMap;

    @Autowired(required = false)
    private Map<String, BizProcessNodeListener> processNodeListenerMap;

    @Autowired(required = false)
    private Map<String, BizProcessItemListener> processItemListenerMap;

    /**
     * 获取业务流程定义
     *
     * @param uuid
     * @return
     */
    @Override
    public BizProcessDefinitionDto getDto(String uuid) {
        BizProcessDefinitionDto dto = new BizProcessDefinitionDto();
        BizProcessDefinitionEntity entity = bizProcessDefinitionService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
            // 获取引用的阶段、事项定义
            ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(uuid);
            dto.setDefinitionJson(JsonUtils.object2Json(parser.getProcessDefinitionJson()));
        }
        return dto;
    }

    /**
     * 根据ID获取业务流程定义
     *
     * @param id
     * @return
     */
    @Override
    public BizProcessDefinitionDto getDtoById(String id) {
        BizProcessDefinitionDto dto = new BizProcessDefinitionDto();
        BizProcessDefinitionEntity entity = bizProcessDefinitionService.getById(id);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * 保存业务流程定义
     *
     * @param dto
     */
    @Override
    public String saveDto(BizProcessDefinitionDto dto) {
        BizProcessDefinitionEntity entity = new BizProcessDefinitionEntity();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            entity = bizProcessDefinitionService.getOne(dto.getUuid());
        } else {
            // ID唯一性判断
            if (this.bizProcessDefinitionService.countById(dto.getId()) > 0) {
                throw new RuntimeException(String.format("已经存在ID为[%s]的业务流程定义!", dto.getId()));
            }
            dto.setVersion(1d);
        }
        List<String> ignoreFields = Lists.newArrayList(IdEntity.BASE_FIELDS);
        ignoreFields.add("definitionJson");
        BeanUtils.copyProperties(dto, entity, ignoreFields.toArray(new String[0]));
        // 更新json定义信息
        String definitionJson = entity.getDefinitionJson();
        if (StringUtils.isNotBlank(definitionJson)) {
            ProcessDefinitionJson processDefinitionJson = JsonUtils.json2Object(definitionJson, ProcessDefinitionJson.class);
            BeanUtils.copyProperties(entity, processDefinitionJson);
            entity.setDefinitionJson(JsonUtils.object2Json(processDefinitionJson));
            // 清空缓存
            ProcessDefinitionUtils.clearCache(entity);
        }
        bizProcessDefinitionService.save(entity);
        return entity.getUuid();
    }

    /**
     * 根据业务流程定义UUID列表删除业务流程定义
     *
     * @param uuids
     */
    @Override
    public void deleteAll(List<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }

        // 判断业务流程定义是否被使用
        if (isUsed(uuids)) {
            throw new BusinessException("业务流程定义已存在业务流程实例，无法删除！");
        }

        // 清空缓存
        List<BizProcessDefinitionEntity> entities = bizProcessDefinitionService.listByUuids(uuids);
        for (BizProcessDefinitionEntity entity : entities) {
            ProcessDefinitionUtils.clearCache(entity);
        }

        bizProcessDefinitionService.deleteAllByUuids(uuids);
    }

    private boolean isUsed(List<String> uuids) {
        return bizProcessInstanceService.countByProcessDefUuids(uuids) > 0;
    }

    /**
     * 获取表单定义下拉数据
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData listDyFormDefinitionSelectData(Select2QueryInfo queryInfo) {
        List<DyFormFormDefinition> dyFormFormDefinitions = dyFormFacade.listDyFormDefinitionBasicInfo();
        return new Select2QueryData(dyFormFormDefinitions, "uuid", "name");
    }

    /**
     * 根据表单ID获取表单定义下拉数据
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData getDyFormDefinitionSelectDataByUuids(Select2QueryInfo queryInfo) {
        String[] formUuids = queryInfo.getIds();
        List<DyFormFormDefinition> dyFormFormDefinitions = Lists.newArrayList();
        for (String formUuid : formUuids) {
            DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(formUuid);
            dyFormFormDefinitions.add(dyFormFormDefinition);
        }
        return new Select2QueryData(dyFormFormDefinitions, "uuid", "name");
    }

    /**
     * 获取业务流程监听器下拉数据
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData listBizProcessListenerSelectData(Select2QueryInfo queryInfo) {
        List<Select2DataBean> select2DataBeans = Lists.newArrayList();
        if (MapUtils.isNotEmpty(processListenerMap)) {
            for (Map.Entry<String, BizProcessListener> entry : processListenerMap.entrySet()) {
                // 运行时监听器不可配置
                BizProcessListener listener = entry.getValue();
                if (listener instanceof RuntimeListener) {
                    continue;
                }
                select2DataBeans.add(new Select2DataBean(entry.getKey(), listener.getName()));
            }
        }
        return new Select2QueryData(select2DataBeans);
    }

    /**
     * 获取过程节点监听器下拉数据
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData listBizProcessNodeListenerSelectData(Select2QueryInfo queryInfo) {
        List<Select2DataBean> select2DataBeans = Lists.newArrayList();
        if (MapUtils.isNotEmpty(processNodeListenerMap)) {
            for (Map.Entry<String, BizProcessNodeListener> entry : processNodeListenerMap.entrySet()) {
                // 运行时监听器不可配置
                BizProcessNodeListener listener = entry.getValue();
                if (listener instanceof RuntimeListener) {
                    continue;
                }
                select2DataBeans.add(new Select2DataBean(entry.getKey(), listener.getName()));
            }
        }
        return new Select2QueryData(select2DataBeans);
    }

    /**
     * 获取业务事项监听器下拉数据
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData listBizProcessItemListenerSelectData(Select2QueryInfo queryInfo) {
        List<Select2DataBean> select2DataBeans = Lists.newArrayList();
        if (MapUtils.isNotEmpty(processItemListenerMap)) {
            for (Map.Entry<String, BizProcessItemListener> entry : processItemListenerMap.entrySet()) {
                // 运行时监听器不可配置
                BizProcessItemListener listener = entry.getValue();
                if (listener instanceof RuntimeListener) {
                    continue;
                }
                select2DataBeans.add(new Select2DataBean(entry.getKey(), listener.getName()));
            }
        }
        return new Select2QueryData(select2DataBeans);
    }

    /**
     * 保存业务流程定义JSON信息
     *
     * @param processDefinitionJson
     */
    @Override
    @Transactional
    public String saveProcessDefinitionJson(ProcessDefinitionJson processDefinitionJson) {
        String uuid = processDefinitionJson.getUuid();
        BizProcessDefinitionEntity definitionEntity = null;
        if (StringUtils.isBlank(uuid)) {
            BizProcessDefinitionDto dto = new BizProcessDefinitionDto();
            BeanUtils.copyProperties(processDefinitionJson, dto);
            uuid = this.saveDto(dto);
            definitionEntity = bizProcessDefinitionService.getOne(uuid);
            BeanUtils.copyProperties(definitionEntity, processDefinitionJson);
        } else {
            definitionEntity = bizProcessDefinitionService.getOne(uuid);
            BeanUtils.copyProperties(processDefinitionJson, definitionEntity);
        }

        definitionEntity.setDefinitionJson(JsonUtils.object2Json(processDefinitionJson));
        bizProcessDefinitionService.saveDefinition(definitionEntity);

        // 保存历史版本
        saveProcessDefinitionHistory(definitionEntity);
        // 清空缓存
        ProcessDefinitionUtils.clearCache(definitionEntity);
        return definitionEntity.getUuid();
    }

    /**
     * 保存业务流程定义JSON信息为新版本
     *
     * @param processDefinitionJson
     * @return
     */
    @Override
    @Transactional
    public String saveProcessDefinitionJsonAsNewVersion(ProcessDefinitionJson processDefinitionJson) {
        String uuid = processDefinitionJson.getUuid();
        BizProcessDefinitionEntity entity = bizProcessDefinitionService.getOne(uuid);

        BizProcessDefinitionEntity definitionEntity = new BizProcessDefinitionEntity();
        BeanUtils.copyProperties(entity, definitionEntity, IdEntity.BASE_FIELDS);
        BeanUtils.copyProperties(processDefinitionJson, definitionEntity, IdEntity.BASE_FIELDS);
        definitionEntity.setDefinitionJson(JsonUtils.object2Json(processDefinitionJson));
        definitionEntity.setVersion(bizProcessDefinitionService.getMaxVersionByUuid(uuid) + 0.1);
        bizProcessDefinitionService.saveDefinition(definitionEntity);

        // 保存历史版本
        saveProcessDefinitionHistory(definitionEntity);
        // 清空缓存
        ProcessDefinitionUtils.clearCache(entity);
        return definitionEntity.getUuid();
    }

    /**
     * 复制业务流程定义
     *
     * @param uuid
     * @param newName
     * @param newId
     * @return
     */
    @Override
    @Transactional
    public String copy(String uuid, String newName, String newId) {
        boolean exists = bizProcessDefinitionService.isExistsById(newId);
        if (exists) {
            throw new BusinessException("业务流程定义ID已经存在！");
        }

        BizProcessDefinitionEntity entity = bizProcessDefinitionService.getOne(uuid);

        BizProcessDefinitionEntity definitionEntity = new BizProcessDefinitionEntity();
        BeanUtils.copyProperties(entity, definitionEntity, IdEntity.BASE_FIELDS);
        definitionEntity.setName(newName);
        definitionEntity.setId(newId);
        definitionEntity.setVersion(1d);
        bizProcessDefinitionService.save(definitionEntity);

        String definitionJson = definitionEntity.getDefinitionJson();
        if (StringUtils.isNotBlank(definitionJson)) {
            ProcessDefinitionJson processDefinitionJson = JsonUtils.json2Object(definitionJson, ProcessDefinitionJson.class);
            BeanUtils.copyProperties(definitionEntity, processDefinitionJson);
            definitionEntity.setDefinitionJson(JsonUtils.object2Json(processDefinitionJson));
            bizProcessDefinitionService.save(definitionEntity);
        }

        // 保存历史版本
        saveProcessDefinitionHistory(definitionEntity);
        return definitionEntity.getUuid();
    }

    /**
     * 保存业务流程定义历史
     *
     * @param definitionEntity
     */
    private void saveProcessDefinitionHistory(BizProcessDefinitionEntity definitionEntity) {
        BizProcessDefinitionHistoryEntity historyEntity = new BizProcessDefinitionHistoryEntity();
        BeanUtils.copyProperties(definitionEntity, historyEntity, IdEntity.BASE_FIELDS);
        historyEntity.setProcessDefUuid(definitionEntity.getUuid());
        bizProcessDefinitionHistoryService.save(historyEntity);
    }

    /**
     * 根据业务流程定义UUID获取过程结点信息
     *
     * @param uuid
     * @return
     */
    @Override
    public List<BizProcessNodeDto> listProcessNodeItemByUuid(String uuid) {
        ProcessDefinitionJson processDefinitionJson = bizProcessDefinitionService.getProcessDefinitionJsonByUuid(uuid);
        List<BizProcessNodeDto> processNodeDtos = extractProcessNode(processDefinitionJson);
        return processNodeDtos;
    }

    /**
     * 根据业务流程定义ID获取过程结点信息
     *
     * @param id
     * @return
     */
    @Override
    public List<BizProcessNodeDto> listProcessNodeItemById(String id) {
        ProcessDefinitionJson processDefinitionJson = bizProcessDefinitionService.getProcessDefinitionJsonById(id);
        List<BizProcessNodeDto> processNodeDtos = extractProcessNode(processDefinitionJson);
        return processNodeDtos;
    }

    /**
     * @param businessId
     * @param excludeDefId
     * @return
     */
    @Override
    public List<TreeNode> getTemplateTree(String businessId, String excludeDefId) {
        List<TreeNode> treeNodes = Lists.newArrayList();
        List<String> processDefUuids = bizProcessDefinitionService.listUuidByBusinessId(businessId, excludeDefId);
        if (CollectionUtils.isNotEmpty(processDefUuids)) {
            List<String> templateTypes = Lists.newArrayList(EnumDefinitionTemplateType.ItemDefinition.getValue(), EnumDefinitionTemplateType.NodeDefinition.getValue());
            List<BizDefinitionTemplateQueryItem> templateQueryItems = definitionTemplateService.listItemByProcessDefUuidsAndTypes(processDefUuids, templateTypes);
            Map<String, List<BizDefinitionTemplateQueryItem>> listTemplateMap = ListUtils.list2group(templateQueryItems, "processDefUuid");
            processDefUuids.forEach(processDefUuid -> {
                ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processDefUuid);
                ProcessDefinitionJson processDefinitionJson = parser.getProcessDefinitionJson();
                treeNodes.add(convertProcessTreeNode(processDefinitionJson, listTemplateMap));
            });
        }
        return treeNodes;
    }

    @Override
    public List<BizDefinitionTemplateQueryItem> listNodeAndItemTemplateItemByUuid(String uuid) {
        List<String> processDefUuids = Lists.newArrayList(uuid);
        List<String> templateTypes = Lists.newArrayList(EnumDefinitionTemplateType.ItemDefinition.getValue(), EnumDefinitionTemplateType.NodeDefinition.getValue());
        return definitionTemplateService.listItemByProcessDefUuidsAndTypes(processDefUuids, templateTypes);
    }

    /**
     * @param refId
     * @param templateType
     * @param processDefUuid
     * @return
     */
    @Override
    public List<BizProcessDefinitionQueryItem> listOfRefTemplate(String refId, String templateType, String processDefUuid) {
        return bizProcessDefinitionService.listOfRefTemplate(refId, templateType, processDefUuid);
    }

    /**
     * @param processDefinitionJson
     * @param listTemplateMap
     * @return
     */
    private TreeNode convertProcessTreeNode(ProcessDefinitionJson processDefinitionJson, Map<String, List<BizDefinitionTemplateQueryItem>> listTemplateMap) {
        TreeNode treeNode = new TreeNode();
        treeNode.setName(processDefinitionJson.getName());
        treeNode.setId(processDefinitionJson.getUuid());
        treeNode.setNocheck(true);
        Map<String, Object> data = Maps.newHashMap();
        data.put("uuid", processDefinitionJson.getUuid());
        data.put("name", processDefinitionJson.getName());
        data.put("id", processDefinitionJson.getId());
        data.put("version", processDefinitionJson.getVersion());
        data.put("type", "process");
        treeNode.setData(data);

        // 阶段节点
        List<ProcessNodeConfig> nodeConfigs = processDefinitionJson.getNodes();
        if (CollectionUtils.isNotEmpty(nodeConfigs)) {
            nodeConfigs.forEach(nodeConfig -> {
                treeNode.getChildren().add(convertNodeTreeNode(nodeConfig, processDefinitionJson, listTemplateMap));
            });
        }

        return treeNode;
    }

    /**
     * @param nodeConfig
     * @param processDefinitionJson
     * @param listTemplateMap
     * @return
     */
    private TreeNode convertNodeTreeNode(ProcessNodeConfig nodeConfig, ProcessDefinitionJson processDefinitionJson,
                                         Map<String, List<BizDefinitionTemplateQueryItem>> listTemplateMap) {
        TreeNode treeNode = new TreeNode();
        treeNode.setName(nodeConfig.getName());
        treeNode.setId(processDefinitionJson.getUuid() + "_" + nodeConfig.getId());
        boolean isRef = StringUtils.isNotBlank(nodeConfig.getRefProcessDefUuid());
        Map<String, Object> data = Maps.newHashMap();
        data.put("name", nodeConfig.getName());
        data.put("id", nodeConfig.getId());
        data.put("code", nodeConfig.getCode());
        data.put("type", "node");
        data.put("isRef", isRef);
        data.put("isTemplate", isTemplate(nodeConfig.getId(), processDefinitionJson.getUuid(), EnumDefinitionTemplateType.NodeDefinition, listTemplateMap));
        data.put("processDefUuid", processDefinitionJson.getUuid());
        treeNode.setData(data);

        // 子阶段
        List<ProcessNodeConfig> nodeConfigs = nodeConfig.getNodes();
        if (CollectionUtils.isNotEmpty(nodeConfigs)) {
            nodeConfigs.forEach(childNodeConfig -> {
                treeNode.getChildren().add(convertNodeTreeNode(childNodeConfig, processDefinitionJson, listTemplateMap));
            });
        }

        // 阶段下的事项
        List<ProcessItemConfig> itemConfigs = nodeConfig.getItems();
        if (CollectionUtils.isNotEmpty(itemConfigs)) {
            itemConfigs.forEach(itemConfig -> {
                treeNode.getChildren().add(convertItemTreeNode(itemConfig, processDefinitionJson, listTemplateMap));
            });
        }

        return treeNode;
    }

    /**
     * @param itemConfig
     * @param processDefinitionJson
     * @param listTemplateMap
     * @return
     */
    private TreeNode convertItemTreeNode(ProcessItemConfig itemConfig, ProcessDefinitionJson processDefinitionJson,
                                         Map<String, List<BizDefinitionTemplateQueryItem>> listTemplateMap) {
        TreeNode treeNode = new TreeNode();
        treeNode.setName(itemConfig.getItemName());
        treeNode.setId(processDefinitionJson.getUuid() + "_" + itemConfig.getId());
        boolean isRef = StringUtils.isNotBlank(itemConfig.getRefProcessDefUuid());
        Map<String, Object> data = Maps.newHashMap();
        data.put("name", itemConfig.getItemName());
        data.put("id", itemConfig.getId());
        data.put("code", itemConfig.getItemCode());
        data.put("type", "item");
        data.put("isRef", isRef);
        data.put("isTemplate", isTemplate(itemConfig.getId(), processDefinitionJson.getUuid(), EnumDefinitionTemplateType.ItemDefinition, listTemplateMap));
        data.put("processDefUuid", processDefinitionJson.getUuid());
        treeNode.setData(data);
        return treeNode;
    }

    /**
     * @param configId
     * @param processDefUuid
     * @param templateType
     * @param listTemplateMap
     * @return
     */
    private boolean isTemplate(String configId, String processDefUuid, EnumDefinitionTemplateType templateType,
                               Map<String, List<BizDefinitionTemplateQueryItem>> listTemplateMap) {
        List<BizDefinitionTemplateQueryItem> templateQueryItems = listTemplateMap.get(processDefUuid);
        if (CollectionUtils.isEmpty(templateQueryItems)) {
            return false;
        }

        Optional<BizDefinitionTemplateQueryItem> templateQueryItemOptional = templateQueryItems.stream().filter(item -> {
            if (EnumDefinitionTemplateType.ItemDefinition.equals(templateType) && templateType.getValue().equals(item.getType())
                    && StringUtils.equals(configId, item.getItemId())) {
                return true;
            } else if (EnumDefinitionTemplateType.NodeDefinition.equals(templateType) && templateType.getValue().equals(item.getType())
                    && StringUtils.equals(configId, item.getNodeId())) {
                return true;
            }
            return false;
        }).findFirst();
        return templateQueryItemOptional.isPresent();
    }

    private List<BizProcessNodeDto> extractProcessNode(ProcessDefinitionJson processDefinitionJson) {
        List<BizProcessNodeDto> processNodeDtos = Lists.newArrayList();
        List<ProcessNodeConfig> nodeConfigs = processDefinitionJson.getNodes();
        for (ProcessNodeConfig nodeConfig : nodeConfigs) {
            processNodeDtos.add(extractProcessNode(nodeConfig, processDefinitionJson));
        }
        return processNodeDtos;
    }

    private BizProcessNodeDto extractProcessNode(ProcessNodeConfig nodeConfig, ProcessDefinitionJson processDefinitionJson) {
        BizProcessNodeDto processNodeDto = new BizProcessNodeDto();
        processNodeDto.setName(nodeConfig.getName());
        processNodeDto.setId(nodeConfig.getId());

        List<BizProcessNodeDto> childrenNodeDtos = Lists.newArrayList();
        List<BizProcessItemDto> itemDtos = Lists.newArrayList();
        // 子过程结点
        List<ProcessNodeConfig> nodes = nodeConfig.getNodes();
        for (ProcessNodeConfig node : nodes) {
            childrenNodeDtos.add(extractProcessNode(node, processDefinitionJson));
        }
        // 业务事项
        List<ProcessItemConfig> items = nodeConfig.getItems();
        for (ProcessItemConfig item : items) {
            itemDtos.add(extractProcessItem(item, processDefinitionJson));
        }

        processNodeDto.setNodes(childrenNodeDtos);
        processNodeDto.setItems(itemDtos);
        return processNodeDto;
    }

    private BizProcessItemDto extractProcessItem(ProcessItemConfig item, ProcessDefinitionJson processDefinitionJson) {
        BizProcessItemDto itemDto = new BizProcessItemDto();
        String itemDefId = item.getItemDefId();
        String itemCode = item.getItemCode();
        itemDto.setId(item.getId());
        itemDto.setItemDefName(item.getItemDefName());
        itemDto.setItemDefId(itemDefId);
        itemDto.setItemName(item.getItemName());
        itemDto.setItemCode(itemCode);
        itemDto.setItemType(item.getItemType());
        // itemDto.setDispenseItem(item.getDispenseItem());
        // 组合事项包含的子事项
        if (EnumItemType.Combined.getValue().equals(item.getItemType())) {
            // 包含事项
            List<BizProcessItemDto.ItemIncludeItemDto> includeItemDtos = item.getIncludeItems().stream()
                    .map(config -> {
                        BizProcessItemDto.ItemIncludeItemDto includeItem = new BizProcessItemDto.ItemIncludeItemDto();
                        includeItem.setId(config.getId());
                        includeItem.setItemCode(config.getItemCode());
                        includeItem.setItemName(config.getItemName());
                        includeItem.setFrontItemCode(config.getFrontItemCode());
                        return includeItem;
                    }).collect(Collectors.toList());
            // List<ItemIncludeItem> includeItems = bizItemDefinitionService.listIncludeItemDataByItemCode(itemDefId, itemCode);
            // 获取包含事项的事项数据
//            List<String> itemCodes = Lists.newArrayList();
//            includeItems.forEach(includeItem -> itemCodes.add(includeItem.getItemCode()));
//            List<ItemData> itemDataList = bizItemDefinitionService.listItemDataByProcessDefUuidAndItemCode(processDefinitionJson.getUuid(), itemCodes.toArray(new String[0]));
//            Map<String, ItemData> itemDataMap = ConvertUtils.convertElementToMap(itemDataList, "itemCode");
//            // 设置包含事项的事项ID
//            List<BizProcessItemDto.ItemIncludeItemDto> includeItemDtos = BeanUtils.copyCollection(includeItems, BizProcessItemDto.ItemIncludeItemDto.class);
//            for (BizProcessItemDto.ItemIncludeItemDto includeItemDto : includeItemDtos) {
//                ItemData itemData = itemDataMap.get(includeItemDto.getItemCode());
//                String itemId = itemDto.getId() + Separator.SLASH.getValue() + itemData.getItemDefId() +
//                        Separator.COLON.getValue() + includeItemDto.getItemCode();
//                includeItemDto.setId(itemId);
//            }
            itemDto.setIncludeItemDtos(includeItemDtos);
            // 互斥事项
//            List<ItemMutexItem> mutexItems = bizItemDefinitionService.listMutexItemDataByItemCode(itemDefId, itemCode);
            itemDto.setMutexItems(item.getMutexItems());
            // 关联事项
//            List<ItemRelateItem> relateItems = bizItemDefinitionService.listRelateItemDataByItemCode(itemDefId, itemCode);
            itemDto.setRelatedItems(item.getRelatedItems());
        }
        return itemDto;
    }

}
