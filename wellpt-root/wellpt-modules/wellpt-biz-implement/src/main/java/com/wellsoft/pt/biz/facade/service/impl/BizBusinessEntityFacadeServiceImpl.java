/*
 * @(#)12/20/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.biz.dto.BizBusinessEntityLifecycleDto;
import com.wellsoft.pt.biz.dto.BizProcessInstanceDto;
import com.wellsoft.pt.biz.dto.BizProcessItemInstanceDto;
import com.wellsoft.pt.biz.dto.BizProcessNodeInstanceDto;
import com.wellsoft.pt.biz.entity.BizBusinessIntegrationEntity;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessNodeInstanceEntity;
import com.wellsoft.pt.biz.facade.service.BizBusinessEntityFacadeService;
import com.wellsoft.pt.biz.service.BizBusinessIntegrationService;
import com.wellsoft.pt.biz.service.BizProcessInstanceService;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.biz.service.BizProcessNodeInstanceService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
 * 12/20/23.1	zhulh		12/20/23		Create
 * </pre>
 * @date 12/20/23
 */
@Service
public class BizBusinessEntityFacadeServiceImpl extends AbstractApiFacade implements BizBusinessEntityFacadeService {

    @Autowired
    private BizProcessInstanceService processInstanceService;

    @Autowired
    private BizProcessNodeInstanceService processNodeInstanceService;

    @Autowired
    private BizProcessItemInstanceService processItemInstanceService;

    @Autowired
    private BizBusinessIntegrationService businessIntegrationService;

    /**
     * 根据业务主体表单定义ID及业务主体ID获取办理过的业务流程定义
     *
     * @param entityFormId
     * @param entityId
     * @return
     */
    @Override
    public List<ProcessDefinitionJson> listProcessDefinitionByEntityFormIdAndEntityId(String entityFormId, String entityId) {
        List<String> processDefUuids = processInstanceService.listProcessDefUuidByEntityFormIdAndEntityId(entityFormId, entityId);
        Map<String, ProcessDefinitionJson> definitionJsonMap = Maps.newHashMap();
        processDefUuids.forEach(processDefUuid -> {
            ProcessDefinitionJsonParser definitionJsonParser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processDefUuid);
            ProcessDefinitionJson processDefinitionJson = definitionJsonParser.getProcessDefinitionJson();
            String id = processDefinitionJson.getId();
            ProcessDefinitionJson preDefinitionJson = definitionJsonMap.get(id);
            // 多个版本的业务流程定义取最新版本的
            if (preDefinitionJson != null) {
                if (preDefinitionJson.getVersion() < processDefinitionJson.getVersion()) {
                    definitionJsonMap.put(id, processDefinitionJson);
                }
            } else {
                definitionJsonMap.put(id, processDefinitionJson);
            }
        });
        List<ProcessDefinitionJson> processDefinitionJsons = Lists.newArrayList(definitionJsonMap.values());
        Collections.sort(processDefinitionJsons, (c1, c2) -> Objects.toString(c1.getCode(), StringUtils.EMPTY)
                .compareTo(Objects.toString(c2.getCode(), StringUtils.EMPTY)));
        return processDefinitionJsons;
    }

    /**
     * 根据业务主体表单定义ID及业务主体ID获取办理过的业务流程实例
     *
     * @param entityFormId
     * @param entityId
     * @return
     */
    @Override
    public List<BizProcessInstanceDto> listProcessInstanceByEntityFormIdAndEntityId(String entityFormId, String entityId) {
        List<BizProcessInstanceEntity> processInstanceEntities = processInstanceService.listByEntityFormIdAndEntityId(entityFormId, entityId);
        Collections.sort(processInstanceEntities, IdEntityComparators.CREATE_TIME_ASC);
        return BeanUtils.copyCollection(processInstanceEntities, BizProcessInstanceDto.class);
    }

    /**
     * 根据业务流程定义ID列表及业务主体ID获取办理过的过程节点数据
     *
     * @param processDefId
     * @param entityId
     * @return
     */
    @Override
    public List<BizProcessNodeInstanceDto> listProcessNodeInstanceByProcessDefIdAndEntityId(String processDefId, String entityId) {
        List<BizProcessNodeInstanceEntity> nodeInstanceEntities = processNodeInstanceService.listByProcessDefIdAndEntityId(processDefId, entityId);
        List<BizProcessNodeInstanceDto> nodeInstanceDtos = BeanUtils.copyCollection(nodeInstanceEntities, BizProcessNodeInstanceDto.class);
        Collections.sort(nodeInstanceDtos, IdEntityComparators.CREATE_TIME_ASC);
        return nodeInstanceDtos;
    }

    /**
     * 根据业务流程实例UUID列表获取办理过的过程节点数据
     *
     * @param processInstUuids
     * @return
     */
    @Override
    public List<BizProcessNodeInstanceDto> listProcessNodeInstanceByProcessInstUuids(List<String> processInstUuids) {
        if (CollectionUtils.isEmpty(processInstUuids)) {
            return Collections.emptyList();
        }

        List<BizProcessNodeInstanceEntity> nodeInstanceEntities = processNodeInstanceService.listByProcessInstUuids(processInstUuids);
        List<BizProcessNodeInstanceDto> nodeInstanceDtos = BeanUtils.copyCollection(nodeInstanceEntities, BizProcessNodeInstanceDto.class);
        Collections.sort(nodeInstanceDtos, IdEntityComparators.CREATE_TIME_ASC);
        return nodeInstanceDtos;
    }

    /**
     * 根据过程节点实例UUID获取办理过的业务事项数据
     *
     * @param processNodeInstUuid
     * @return
     */
    @Override
    public List<BizProcessItemInstanceDto> listProcessItemInstanceByProcessNodeInstUuid(String processNodeInstUuid) {
        List<BizProcessItemInstanceEntity> itemInstanceEntities = processItemInstanceService.listByProcessNodeInstUuid(processNodeInstUuid);
        List<BizProcessItemInstanceDto> itemInstanceDtos = BeanUtils.copyCollection(itemInstanceEntities, BizProcessItemInstanceDto.class);
        fillBusinessIntegration(itemInstanceDtos);
        Collections.sort(itemInstanceDtos, IdEntityComparators.CREATE_TIME_ASC);
        return itemInstanceDtos;
    }

    private void fillBusinessIntegration(List<BizProcessItemInstanceDto> itemInstanceDtos) {
        List<String> itemInstUuids = itemInstanceDtos.stream().map(dto -> dto.getUuid()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(itemInstUuids)) {
            Map<String, BizProcessItemInstanceDto> itemMap = ConvertUtils.convertElementToMap(itemInstanceDtos, "uuid");
            List<BizBusinessIntegrationEntity> integrationEntities = businessIntegrationService.listByItemInstUuids(itemInstUuids);
            Map<String, List<BizBusinessIntegrationEntity>> listMap = ListUtils.list2group(integrationEntities, "itemInstUuid");
            for (Map.Entry<String, List<BizBusinessIntegrationEntity>> entry : listMap.entrySet()) {
                itemMap.get(entry.getKey()).setBusinessIntegrations(entry.getValue());
            }
        }
    }

    /**
     * 根据业务流程实例UUID获取办理过的业务事项数据
     *
     * @param processInstUuids
     * @return
     */
    @Override
    public List<BizProcessItemInstanceDto> listProcessItemInstanceByProcessInstUuids(List<String> processInstUuids) {
        List<BizProcessItemInstanceEntity> itemInstanceEntities = processItemInstanceService.listByProcessInstUuids(processInstUuids);
        List<BizProcessItemInstanceDto> itemInstanceDtos = BeanUtils.copyCollection(itemInstanceEntities, BizProcessItemInstanceDto.class);
        fillBusinessIntegration(itemInstanceDtos);
        Collections.sort(itemInstanceDtos, IdEntityComparators.CREATE_TIME_ASC);
        return itemInstanceDtos;
    }

    /**
     * 根据业务主体表单定义ID及业务主体ID获取办生命周期数据
     *
     * @param entityFormId
     * @param entityId
     * @return
     */
    @Override
    public BizBusinessEntityLifecycleDto getLifecycleByEntityFormIdAndEntityId(String entityFormId, String entityId) {
        List<String> processInstUuids = Lists.newArrayList();
        List<ProcessDefinitionJson> processDefinitions = this.listProcessDefinitionByEntityFormIdAndEntityId(entityFormId, entityId);
        List<BizProcessInstanceDto> processInstanceDtos = this.listProcessInstanceByEntityFormIdAndEntityId(entityFormId, entityId);
        processInstanceDtos.forEach(dto -> processInstUuids.add(dto.getUuid()));
        List<BizProcessNodeInstanceDto> nodeInstanceDtos = this.listProcessNodeInstanceByProcessInstUuids(processInstUuids);
        List<BizProcessItemInstanceDto> itemInstanceDtos = this.listProcessItemInstanceByProcessInstUuids(processInstUuids);

        BizBusinessEntityLifecycleDto dto = new BizBusinessEntityLifecycleDto();
        dto.setProcessDefinitions(processDefinitions);
        dto.setProcessInstances(processInstanceDtos);
        dto.setNodeInstances(nodeInstanceDtos);
        dto.setItemInstances(itemInstanceDtos);
        return dto;
    }

}
