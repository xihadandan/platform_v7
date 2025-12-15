/*
 * @(#)10/13/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.biz.entity.BizDefinitionTemplateEntity;
import com.wellsoft.pt.biz.enums.EnumBizDefinitionConfigType;
import com.wellsoft.pt.biz.enums.EnumDefinitionTemplateType;
import com.wellsoft.pt.biz.facade.service.BizDefinitionTemplateFacadeService;
import com.wellsoft.pt.biz.service.BizDefinitionTemplateService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
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
 * 10/13/22.1	zhulh		10/13/22		Create
 * </pre>
 * @date 10/13/22
 */
public class ProcessDefinitionJsonParser extends BaseObject {
    private static final long serialVersionUID = 7777201345869761433L;

    private ProcessDefinitionJson processDefinitionJson;

    private Map<String, ProcessNodeConfig> nodeConfigMap = Maps.newLinkedHashMap();

    private Map<String, List<StateDefinition>> nodeStateMap = Maps.newLinkedHashMap();

    private Map<String, ProcessItemConfig> itemConfigMap = Maps.newLinkedHashMap();

    private Map<String, List<StateDefinition>> itemStateMap = Maps.newLinkedHashMap();

    private Map<String, List<ProcessItemConfig.DefineEventPublishConfig>> itemEventPublishMap = Maps.newLinkedHashMap();

    // 上级过程节点映射<childNodeId, parentNodeId>
    private Map<String, String> parentNodeIdMap = Maps.newHashMap();

    // 事项所在过程节点映射<itemId, parentNodeId>
    private Map<String, String> itemParentNodeIdMap = Maps.newHashMap();

    // 包含事项所在事项映射<includeItemId, itemId>
    private Map<String, String> itemParentItemIdMap = Maps.newHashMap();

    // 是否已从模板加载的对象MAP
    private Map<Object, Boolean> loadedFromTemplateMap = Maps.newHashMap();

    // 过程节点表单配置MAP
    private Map<String, DefinitionTemplateProcessNodeFormConfig> processNodeFormConfigMap = Maps.newHashMap();

    //  业务事项表单配置MAP
    private Map<String, DefinitionTemplateProcessItemFormConfig> processItemFormConfigMap = Maps.newHashMap();

    // 事项流定义
    private Map<String, ItemFlowDefinition> itemFlowMap;

    public ProcessDefinitionJsonParser() {
    }

    public ProcessDefinitionJsonParser(ProcessDefinitionJson processDefinitionJson) {
        this.processDefinitionJson = processDefinitionJson;

        parse();
    }

    private void parse() {
        Map<String, List<BizDefinitionTemplateEntity>> definitionTemplateMap = Maps.newHashMap();
        List<ProcessNodeConfig> nodes = processDefinitionJson.getNodes();
        for (ProcessNodeConfig processNodeConfig : nodes) {
            // 解析业务结点
            parseNode(processNodeConfig, definitionTemplateMap);
        }

        parseItemFlow();

        initNodeAndItemStateMap();
    }

    /**
     * 解析事项流
     */
    private void parseItemFlow() {
        itemFlowMap = ItemFlowDefinitionParser.parse(this.processDefinitionJson.getItemFlows(), this);
    }

    private void initNodeAndItemStateMap() {
        // 过程节点状态定义
        for (Map.Entry<String, ProcessNodeConfig> entry : nodeConfigMap.entrySet()) {
            ProcessNodeConfig.ProcessNodeFormConfig processNodeFormConfig = entry.getValue().getFormConfig();
            if (processNodeFormConfig != null && CollectionUtils.isNotEmpty(processNodeFormConfig.getStates())) {
                nodeStateMap.put(entry.getKey(), processNodeFormConfig.getStates());
            }
        }

        // 业务事项状态定义
        for (Map.Entry<String, ProcessItemConfig> entry : itemConfigMap.entrySet()) {
            String itemId = entry.getKey();
            ProcessItemConfig itemConfig = entry.getValue();
            ProcessItemConfig.ProcessItemFormConfig processItemFormConfig = itemConfig.getFormConfig();
            if (processItemFormConfig != null && CollectionUtils.isNotEmpty(processItemFormConfig.getStates())) {
                itemStateMap.put(itemId, processItemFormConfig.getStates());
            }

            // 事件触发定义
            List<ProcessItemConfig.DefineEventConfig> defineEventConfigs = itemConfig.getDefineEvents();
            List<ProcessItemConfig.DefineEventPublishConfig> defineEventPublishConfigs = itemConfig.getEventPublishConfigs();
            if (CollectionUtils.isNotEmpty(defineEventConfigs) && CollectionUtils.isNotEmpty(defineEventPublishConfigs)) {
                Map<String, ProcessItemConfig.DefineEventConfig> eventMap = ConvertUtils.convertElementToMap(defineEventConfigs, "id");
                defineEventPublishConfigs = defineEventPublishConfigs.stream().
                        filter(config -> {
                            ProcessItemConfig.DefineEventConfig defineEventConfig = eventMap.get(config.getEventId());
                            if (defineEventConfig == null) {
                                return false;
                            }
                            config.setMilestone(defineEventConfig.getMilestone());
                            return true;
                        }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(defineEventPublishConfigs)) {
                    itemEventPublishMap.put(itemId, defineEventPublishConfigs);
                }
            }
        }
    }

    /**
     * @param processNodeConfig
     */
    private void parseNode(ProcessNodeConfig processNodeConfig, Map<String, List<BizDefinitionTemplateEntity>> definitionTemplateMap) {
        // 过程结点
        ProcessNodeConfig nodeConfig = getRefProcessNodeConfigIfRequired(processNodeConfig, definitionTemplateMap);
        nodeConfigMap.put(nodeConfig.getId(), nodeConfig);

        // 子过程结点
        for (ProcessNodeConfig childNodeConfig : nodeConfig.getNodes()) {
            parentNodeIdMap.put(childNodeConfig.getId(), nodeConfig.getId());
            // 解析业务结点
            parseNode(childNodeConfig, definitionTemplateMap);
        }

        // 业务事项
        for (ProcessItemConfig itemConfig : nodeConfig.getItems()) {
            addProcessItemConfig(nodeConfig.getId(), itemConfig, definitionTemplateMap);
        }

        // 过程节点表单
        loadProcessNodeFormConfigFromTemplateIfRequired(nodeConfig);
    }

    /**
     * @param processNodeConfig
     * @return
     */
    private ProcessNodeConfig getRefProcessNodeConfigIfRequired(ProcessNodeConfig processNodeConfig, Map<String, List<BizDefinitionTemplateEntity>> definitionTemplateMap) {
        ProcessNodeConfig retConfig = processNodeConfig;
        String refProcessDefUuid = retConfig.getRefProcessDefUuid();
        if (StringUtils.isNotBlank(refProcessDefUuid)) {
            List<BizDefinitionTemplateEntity> templateEntities = definitionTemplateMap.get(refProcessDefUuid);
            if (CollectionUtils.isEmpty(templateEntities)) {
                BizDefinitionTemplateService definitionTemplateService = ApplicationContextHolder.getBean(BizDefinitionTemplateService.class);
                templateEntities = definitionTemplateService.listByProcessDefUuid(refProcessDefUuid);
                definitionTemplateMap.put(refProcessDefUuid, templateEntities);
            }
            BizDefinitionTemplateEntity templateEntity = templateEntities.stream()
                    .filter(item -> EnumDefinitionTemplateType.NodeDefinition.getValue().equals(item.getType())
                            && StringUtils.equals(processNodeConfig.getId(), item.getNodeId()))
                    .findFirst().orElse(null);
            if (templateEntity != null) {
                retConfig = JsonUtils.json2Object(templateEntity.getDefinitionJson(), ProcessNodeConfig.class);
                retConfig.setRefProcessDefUuid(refProcessDefUuid);
                BeanUtils.copyProperties(retConfig, processNodeConfig);
            }
        }
        return retConfig;
    }

    /**
     * @param processItemConfig
     * @param definitionTemplateMap
     * @return
     */
    private ProcessItemConfig getRefProcessItemConfigIfRequired(ProcessItemConfig processItemConfig, Map<String, List<BizDefinitionTemplateEntity>> definitionTemplateMap) {
        ProcessItemConfig refItemConfig = processItemConfig;
        String refProcessDefUuid = refItemConfig.getRefProcessDefUuid();
        if (StringUtils.isNotBlank(refProcessDefUuid)) {
            List<BizDefinitionTemplateEntity> templateEntities = definitionTemplateMap.get(refProcessDefUuid);
            if (CollectionUtils.isEmpty(templateEntities)) {
                BizDefinitionTemplateService definitionTemplateService = ApplicationContextHolder.getBean(BizDefinitionTemplateService.class);
                templateEntities = definitionTemplateService.listByProcessDefUuid(refProcessDefUuid);
                definitionTemplateMap.put(refProcessDefUuid, templateEntities);
            }
            BizDefinitionTemplateEntity templateEntity = templateEntities.stream()
                    .filter(item -> EnumDefinitionTemplateType.ItemDefinition.getValue().equals(item.getType())
                            && StringUtils.equals(processItemConfig.getId(), item.getItemId()))
                    .findFirst().orElse(null);
            if (templateEntity != null) {
                refItemConfig = JsonUtils.json2Object(templateEntity.getDefinitionJson(), ProcessItemConfig.class);
                refItemConfig.setRefProcessDefUuid(refProcessDefUuid);
                BeanUtils.copyProperties(refItemConfig, processItemConfig);
            }
        }
        return refItemConfig;
    }

    public void addChildProcessItemConfig(String parentItemId, ProcessItemConfig itemConfig) {
        String nodeId = itemParentNodeIdMap.get(parentItemId);
        addProcessItemConfig(nodeId, itemConfig, Maps.newHashMapWithExpectedSize(0));
    }

    private void addProcessItemConfig(String nodeId, ProcessItemConfig processItemConfig, Map<String, List<BizDefinitionTemplateEntity>> definitionTemplateMap) {
        ProcessItemConfig itemConfig = getRefProcessItemConfigIfRequired(processItemConfig, definitionTemplateMap);
        itemConfigMap.put(itemConfig.getId(), itemConfig);
        itemParentNodeIdMap.put(itemConfig.getId(), nodeId);
        // 业务事项表单
        loadProcessItemFormConfigFromTemplateIfRequired(itemConfig);

        parseIncludeItems(nodeId, itemConfig, itemConfig.getIncludeItems());
    }

    private void parseIncludeItems(String nodeId, ProcessItemConfig itemConfig, List<ProcessItemConfig> includeItems) {
        if (CollectionUtils.isEmpty(includeItems)) {
            return;
        }

        includeItems.forEach(item -> {
            if (StringUtils.isNotBlank(item.getId())) {
                itemParentItemIdMap.put(item.getId(), itemConfig.getId());
                addProcessItemConfig(nodeId, item, Maps.newHashMapWithExpectedSize(0));
            }
        });
    }

    /**
     * 加载业务事项表单设置模板内容
     *
     * @param itemConfig
     */
    private void loadProcessItemFormConfigFromTemplateIfRequired(ProcessItemConfig itemConfig) {
        ProcessItemConfig.ProcessItemFormConfig formConfig = itemConfig.getFormConfig();
        if (formConfig == null) {
            return;
        }
        String configType = formConfig.getConfigType();
        String templateUuid = formConfig.getTemplateUuid();
        if (EnumBizDefinitionConfigType.RefTemplate.getValue().equals(configType) && StringUtils.isNotBlank(templateUuid)) {
            if (processItemFormConfigMap.containsKey(templateUuid)) {
                BeanUtils.copyProperties(processItemFormConfigMap.get(templateUuid), formConfig);
            } else {
                BizDefinitionTemplateFacadeService definitionTemplateFacadeService = ApplicationContextHolder.getBean(BizDefinitionTemplateFacadeService.class);
                BizDefinitionTemplateEntity templateEntity = definitionTemplateFacadeService.getDto(templateUuid);
                if (StringUtils.isNotBlank(templateEntity.getDefinitionJson())) {
                    DefinitionTemplateProcessItemFormConfig templateConfig = JsonUtils.json2Object(templateEntity.getDefinitionJson(), DefinitionTemplateProcessItemFormConfig.class);
                    BeanUtils.copyProperties(templateConfig, formConfig);
                    processItemFormConfigMap.put(templateUuid, templateConfig);
                }
            }
        }
    }

    /**
     * 加载过程节点表单设置模板内容
     *
     * @param processNodeConfig
     */
    private void loadProcessNodeFormConfigFromTemplateIfRequired(ProcessNodeConfig processNodeConfig) {
        ProcessNodeConfig.ProcessNodeFormConfig formConfig = processNodeConfig.getFormConfig();
        if (formConfig == null) {
            return;
        }
        String configType = formConfig.getConfigType();
        String templateUuid = formConfig.getTemplateUuid();
        if (EnumBizDefinitionConfigType.RefTemplate.getValue().equals(configType) && StringUtils.isNotBlank(templateUuid)) {
            if (processNodeFormConfigMap.containsKey(templateUuid)) {
                BeanUtils.copyProperties(processNodeFormConfigMap.get(templateUuid), formConfig);
            } else {
                BizDefinitionTemplateFacadeService definitionTemplateFacadeService = ApplicationContextHolder.getBean(BizDefinitionTemplateFacadeService.class);
                BizDefinitionTemplateEntity templateEntity = definitionTemplateFacadeService.getDto(templateUuid);
                if (StringUtils.isNotBlank(templateEntity.getDefinitionJson())) {
                    DefinitionTemplateProcessNodeFormConfig templateConfig = JsonUtils.json2Object(templateEntity.getDefinitionJson(), DefinitionTemplateProcessNodeFormConfig.class);
                    BeanUtils.copyProperties(templateConfig, formConfig);
                    processNodeFormConfigMap.put(templateUuid, templateConfig);
                }
            }
        }
    }


    public String getProcessDefUuid() {
        return processDefinitionJson.getUuid();
    }

    public String getProcessDefName() {
        return processDefinitionJson.getName();
    }

    public String getProcessDefId() {
        return processDefinitionJson.getId();
    }

    public String getProcessListener() {
        return processDefinitionJson.getListener();
    }

    public String getProcessBusinessId() {
        return processDefinitionJson.getBusinessId();
    }

    /**
     * @return the processDefinitionJson
     */
    public ProcessDefinitionJson getProcessDefinitionJson() {
        return processDefinitionJson;
    }

    /**
     * 获取业务流程表单设置
     *
     * @return
     */
    public ProcessDefinitionJson.ProcessFormConfig getProcessFormConfig() {
        ProcessDefinitionJson.ProcessFormConfig formConfig = processDefinitionJson.getFormConfig();
        String configType = formConfig.getConfigType();
        if (EnumBizDefinitionConfigType.RefTemplate.getValue().equals(configType)
                && StringUtils.isNotBlank(formConfig.getTemplateUuid())
                && !loadedFromTemplateMap.containsKey(formConfig)) {
            BizDefinitionTemplateFacadeService definitionTemplateFacadeService = ApplicationContextHolder.getBean(BizDefinitionTemplateFacadeService.class);
            BizDefinitionTemplateEntity templateEntity = definitionTemplateFacadeService.getDto(formConfig.getTemplateUuid());
            if (StringUtils.isNotBlank(templateEntity.getDefinitionJson())) {
                DefinitionTemplateProcessFormConfig templateConfig = JsonUtils.json2Object(templateEntity.getDefinitionJson(), DefinitionTemplateProcessFormConfig.class);
                BeanUtils.copyProperties(templateConfig, formConfig);
            }
            loadedFromTemplateMap.put(formConfig, true);
        }
        return formConfig;
    }

    /**
     * 获取业务流程表单设置
     *
     * @return
     */
    public ProcessDefinitionJson.ProcessEntityConfig getProcessEntityConfig() {
        return processDefinitionJson.getEntityConfig();
    }

    public List<String> getAllNodeId() {
        return Lists.newArrayList(nodeConfigMap.keySet());
    }

    public Map<String, List<StateDefinition>> getNodeStateMap() {
        return nodeStateMap;
    }

    public String getParentIdOfNode(String nodeId) {
        return parentNodeIdMap.get(nodeId);
    }

    public List<String> getAllItemId() {
        return Lists.newArrayList(itemConfigMap.keySet());
    }

    public List<ProcessItemConfig> getAllItemConfig() {
        return Lists.newArrayList(itemConfigMap.values());
    }

    public Map<String, List<StateDefinition>> getItemStateMap() {
        return itemStateMap;
    }

    public Map<String, List<ProcessItemConfig.DefineEventPublishConfig>> getItemEventPublishMap() {
        return itemEventPublishMap;
    }

    public String getParentNodeIdOfItem(String itemId) {
        return itemParentNodeIdMap.get(itemId);
    }

    /**
     * 是否包含事项中的事项
     *
     * @param itemId
     * @return
     */
    public boolean isIncludeItem(String itemId) {
        return itemParentItemIdMap.containsKey(itemId);
    }

    public Set<String> getFormUuidSet() {
        Set<String> formUuidSet = Sets.newHashSet();
        formUuidSet.add(getProcessFormConfig().getFormUuid());
        nodeConfigMap.values().forEach(node -> {
            if (node.getFormConfig() != null) {
                formUuidSet.add(node.getFormConfig().getFormUuid());
            }
        });
        itemConfigMap.values().forEach(item -> {
            if (item.getFormConfig() != null) {
                formUuidSet.add(item.getFormConfig().getFormUuid());
            }
        });
        return formUuidSet;
    }

    /**
     * 获取流程定义ID集合
     *
     * @return
     */
    public Set<String> getFlowDefIdSet() {
        Set<String> flowDefIdSet = Sets.newHashSet();
        itemConfigMap.values().forEach(item -> {
            List<ProcessItemConfig.BusinessIntegrationConfig> biConfigs = item.getBusinessIntegrationConfigs();
            if (CollectionUtils.isNotEmpty(biConfigs)) {
                biConfigs.forEach(config -> {
                    if (StringUtils.isNotBlank(config.getFlowDefId())) {
                        flowDefIdSet.add((config.getFlowDefId()));
                    }
                });
            }
        });
        return flowDefIdSet;
    }


    public Set<String> getTagIdSet() {
        Set<String> tagIdSet = Sets.newHashSet();
        tagIdSet.add(processDefinitionJson.getTagId());
        nodeConfigMap.values().forEach(node -> tagIdSet.add(node.getTagId()));
        return tagIdSet;
    }
/*
    public ProcessDefinitionJson getProcessDefinitionJson() {
        return processDefinitionJson;
    }*/

    /**
     * 根据业务事项ID获取业务事项配置
     *
     * @param nodeId
     * @return
     */
    public ProcessNodeConfig getProcessNodeConfigById(String nodeId) {
        return nodeConfigMap.get(nodeId);
    }

    /**
     * 根据业务事项ID获取业务事项配置
     *
     * @param itemId
     * @return
     */
    public ProcessItemConfig getProcessItemConfigById(String itemId) {
        // 事项分发的子事项ID格式为——上级事项ID/子事项定义ID:子事项编码
//        if (StringUtils.contains(itemId, Separator.SLASH.getValue())) {
//            String[] itemIdParts = StringUtils.split(itemId, Separator.SLASH.getValue());
//            if (itemIdParts.length > 1) {
//                String processItemId = itemIdParts[0];
//                String itemCodePart = itemIdParts[1];
//                String[] itemCodeParts = StringUtils.split(itemCodePart, Separator.COLON.getValue());
//                String itemDefId = itemCodeParts[0];
//                String itemCode = itemCodeParts[1];
//                ProcessItemConfig processItemConfig = itemConfigMap.get(processItemId);
//                ProcessItemConfig itemConfig = new ProcessItemConfig();
//                BeanUtils.copyProperties(processItemConfig, itemConfig);
//                itemConfig.setItemDefId(itemDefId);
//                itemConfig.setItemDefName(StringUtils.EMPTY);
//                itemConfig.setItemCode(itemCode);
//                // 表单分发配置
//                ProcessItemConfig.DispenseFormConfig dispenseFormConfig = processItemConfig.getDispenseFormConfigByItemCode(itemCode);
//                if (dispenseFormConfig != null) {
//                    // 使用主表单
//                    ProcessItemConfig.ProcessItemFormConfig itemFormConfig = itemConfig.getFormConfig();
//                    if (ProcessItemConfig.DispenseFormConfig.TYPE_USE_PARENT_ITEM_FORM.equals(dispenseFormConfig.getType())) {
//                        itemConfig.setItemName(dispenseFormConfig.getItemName());
//                        itemFormConfig.setFormUuid(dispenseFormConfig.getFormUuid());
//                        itemFormConfig.setFormName(dispenseFormConfig.getFormName());
//                    } else {
//                        // 使用单据转换
//                        itemConfig.setItemName(dispenseFormConfig.getItemName());
//                        itemFormConfig.setFormUuid(StringUtils.EMPTY);
//                        itemFormConfig.setFormName(StringUtils.EMPTY);
//                        itemFormConfig.setBotId(dispenseFormConfig.getBotId());
//                        itemFormConfig.setEntityNameField(dispenseFormConfig.getEntityNameField());
//                        itemFormConfig.setEntityIdField(dispenseFormConfig.getEntityIdField());
//                        itemFormConfig.setTimeLimitField(dispenseFormConfig.getTimeLimitField());
//                    }
//                    itemConfig.setItemDefId(dispenseFormConfig.getItemDefId());
//                    itemConfig.setItemDefName(dispenseFormConfig.getItemDefName());
//                    itemConfig.setItemType(dispenseFormConfig.getItemType());
//                }
//                return itemConfig;
//            }
//        }
        return itemConfigMap.get(itemId);
    }

    /**
     * 根据业务事项ID获取同级的其他事项ID列表
     *
     * @param itemId
     * @return
     */
    public List<String> getSiblingItemIdsByItemId(String itemId) {
        ProcessNodeConfig processNodeConfig = getProcessNodeConfigByItemId(itemId);
        String nodeId = processNodeConfig.getId();
        List<String> slibingItemIds = getChildrenItemIdsByNodeId(nodeId);
        slibingItemIds.remove(itemId);
        return slibingItemIds;
    }

    /**
     * 根据业务事项ID获取同级的其他过程节点ID列表
     *
     * @param itemId
     * @return
     */
    public List<String> getSiblingNodeIdsByItemId(String itemId) {
        ProcessNodeConfig processNodeConfig = getProcessNodeConfigByItemId(itemId);
        String nodeId = processNodeConfig.getId();
        return getChildrenNodeIdsByNodeId(nodeId);
    }


    public List<String> getChildrenItemIdsByNodeId(String nodeId) {
        List<String> children = Lists.newArrayList();
        for (Map.Entry<String, String> entry : itemParentNodeIdMap.entrySet()) {
            String processItemId = entry.getKey();
            String processNodeId = entry.getValue();
            if (StringUtils.equals(processNodeId, nodeId)) {
                // 并联分发事项不发起自身
//                ProcessItemConfig processItemConfig = itemConfigMap.get(processItemId);
//                if (EnumItemType.Parallel.getValue().equals(processItemConfig.getItemType()) && processItemConfig.getDispenseItem()) {
//                    List<ProcessItemConfig.DispenseFormConfig> dispenseFormConfigs = processItemConfig.getDispenseFormConfigs();
//                    if (CollectionUtils.isNotEmpty(dispenseFormConfigs)) {
//                        for (ProcessItemConfig.DispenseFormConfig dispenseFormConfig : dispenseFormConfigs) {
//                            children.add(processItemId + Separator.SLASH.getValue() + dispenseFormConfig.getItemDefId()
//                                    + Separator.COLON.getValue() + dispenseFormConfig.getItemCode());
//                        }
//                    }
//                } else {
                children.add(processItemId);
//                }
            }
        }
        return children;
    }

    public List<String> getChildrenNodeIdsByNodeId(String nodeId) {
        List<String> children = Lists.newArrayList();
        for (Map.Entry<String, String> entry : parentNodeIdMap.entrySet()) {
            String childNodeId = entry.getKey();
            String parentNodeId = entry.getValue();
            if (StringUtils.equals(parentNodeId, nodeId)) {
                children.add(childNodeId);
            }
        }
        return children;
    }

    /**
     * 根据业务事项ID获取其所在的过程节点配置
     *
     * @param itemId
     * @return
     */
    public ProcessNodeConfig getProcessNodeConfigByItemId(String itemId) {
        String processNodeId = null;
        if (StringUtils.contains(itemId, Separator.SLASH.getValue())) {
            String[] itemIdParts = StringUtils.split(itemId, Separator.SLASH.getValue());
            processNodeId = itemParentNodeIdMap.get(itemIdParts[0]);
        } else {
            processNodeId = itemParentNodeIdMap.get(itemId);
        }
        return nodeConfigMap.get(processNodeId);
    }

    /**
     * 根据业务事项ID获取其所在的过程节点配置
     *
     * @param nodeId
     * @return
     */
    public ProcessNodeConfig getParentProcessNodeConfigByNodeId(String nodeId) {
        String processNodeId = parentNodeIdMap.get(nodeId);
        return nodeConfigMap.get(processNodeId);
    }

    public String getProcessItemNameById(String itemId) {
        Iterator<String> pidIt = Arrays.asList(StringUtils.split(itemId, Separator.SEMICOLON.getValue())).iterator();
        List<String> itemNames = Lists.newArrayList();
        while (pidIt.hasNext()) {
            ProcessItemConfig itemConfig = this.getProcessItemConfigById(pidIt.next());
            if (itemConfig != null) {
                itemNames.add(itemConfig.getItemName());
            } else {
                itemNames.add(StringUtils.EMPTY);
            }
        }
        return StringUtils.join(itemNames, Separator.SEMICOLON.getValue());
    }

    public String getProcessItemCodeById(String itemId) {
        Iterator<String> pidIt = Arrays.asList(StringUtils.split(itemId, Separator.SEMICOLON.getValue())).iterator();
        List<String> itemCodes = Lists.newArrayList();
        while (pidIt.hasNext()) {
            ProcessItemConfig itemConfig = this.getProcessItemConfigById(pidIt.next());
            if (itemConfig != null) {
                itemCodes.add(itemConfig.getItemCode());
            } else {
                itemCodes.add(StringUtils.EMPTY);
            }
        }
        return StringUtils.join(itemCodes, Separator.SEMICOLON.getValue());
    }

    /**
     * @return the itemFlow
     */
    public ItemFlowDefinition getItemFlow(String itemFlowId) {
        return itemFlowMap.get(itemFlowId);
    }

}
