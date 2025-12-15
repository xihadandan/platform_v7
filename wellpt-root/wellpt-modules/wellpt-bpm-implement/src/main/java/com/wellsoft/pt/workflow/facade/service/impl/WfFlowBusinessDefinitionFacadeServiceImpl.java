/*
 * @(#)11/11/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.dto.WfFlowBusinessDefinitionDto;
import com.wellsoft.pt.workflow.entity.WfFlowBusinessDefinitionEntity;
import com.wellsoft.pt.workflow.facade.service.FlowDefinitionFacadeService;
import com.wellsoft.pt.workflow.facade.service.WfFlowBusinessDefinitionFacadeService;
import com.wellsoft.pt.workflow.listener.FlowBusinessListener;
import com.wellsoft.pt.workflow.query.WfFlowBusinessDefinitionQueryItem;
import com.wellsoft.pt.workflow.service.WfFlowBusinessDefinitionService;
import com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
public class WfFlowBusinessDefinitionFacadeServiceImpl extends AbstractApiFacade
        implements WfFlowBusinessDefinitionFacadeService, Select2QueryApi {

    @Autowired
    private WfFlowBusinessDefinitionService flowBusinessDefinitionService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    @Autowired
    private FlowDefinitionFacadeService flowDefinitionFacadeService;

    @Autowired(required = false)
    private Map<String, FlowBusinessListener> flowBusinessListenerMap;

    /**
     * 根据UUID获取流程业务定义
     *
     * @param uuid
     * @return
     */
    @Override
    public WfFlowBusinessDefinitionDto getDto(String uuid) {
        WfFlowBusinessDefinitionDto dto = new WfFlowBusinessDefinitionDto();
        WfFlowBusinessDefinitionEntity entity = flowBusinessDefinitionService.getOne(uuid);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * 根据ID获取流程定义ID
     *
     * @param id
     * @return
     */
    @Override
    public String getFlowDefIdById(String id) {
        return flowBusinessDefinitionService.getFlowDefIdById(id);
    }

    /**
     * 保存流程业务定义
     *
     * @param dto
     */
    @Override
    public void saveDto(WfFlowBusinessDefinitionDto dto) {
        WfFlowBusinessDefinitionEntity entity = new WfFlowBusinessDefinitionEntity();
        if (StringUtils.isNotBlank(dto.getUuid())) {
            entity = flowBusinessDefinitionService.getOne(dto.getUuid());
        } else {
            // ID唯一性判断
            if (this.flowBusinessDefinitionService.countById(dto.getId()) > 0) {
                throw new RuntimeException(String.format("已经存在ID为[%s]的流程业务定义!", dto.getId()));
            }
        }
        List<String> ignoreFields = Lists.newArrayList(IdEntity.BASE_FIELDS);
        ignoreFields.add("definitionJson");
        BeanUtils.copyProperties(dto, entity, ignoreFields.toArray(new String[0]));

        // 设置流程定义UUID及分类UUID
        String flowDefId = dto.getFlowDefId();
        if (StringUtils.isNotBlank(flowDefId)) {
            FlowDefinition flowDefinition = flowDefinitionService.getById(flowDefId);
            if (flowDefinition != null) {
                entity.setFlowDefUuid(flowDefinition.getUuid());
                entity.setCategoryUuid(flowDefinition.getCategory());
            } else {
                entity.setFlowDefUuid(StringUtils.EMPTY);
                entity.setCategoryUuid(StringUtils.EMPTY);
            }
        }

        // 更新json定义信息
        String definitionJson = dto.getDefinitionJson();
        if (StringUtils.isNotBlank(definitionJson)) {
            FlowBusinessDefinitionJson flowBusinessDefinitionJson = JsonUtils.json2Object(definitionJson, FlowBusinessDefinitionJson.class);
            BeanUtils.copyProperties(entity, flowBusinessDefinitionJson);
            entity.setDefinitionJson(JsonUtils.object2Json(flowBusinessDefinitionJson));
        }
        flowBusinessDefinitionService.save(entity);
    }

    /**
     * 保存流程业务定义
     *
     * @param flowBusinessDefinitionJsons
     */
    @Override
    public void saveAll(Collection<FlowBusinessDefinitionJson> flowBusinessDefinitionJsons) {
        for (FlowBusinessDefinitionJson flowBusinessDefinitionJson : flowBusinessDefinitionJsons) {
            WfFlowBusinessDefinitionEntity entity = flowBusinessDefinitionService.getByFlowDefId(flowBusinessDefinitionJson.getFlowDefId());

            WfFlowBusinessDefinitionDto dto = new WfFlowBusinessDefinitionDto();
            if (entity != null) {
                BeanUtils.copyProperties(entity, dto);
                FlowBusinessDefinitionJson definitionJson = JsonUtils.json2Object(entity.getDefinitionJson(), FlowBusinessDefinitionJson.class);
                definitionJson.setStates(flowBusinessDefinitionJson.getStates());
                dto.setDefinitionJson(JsonUtils.object2Json(definitionJson));
            } else {
                BeanUtils.copyProperties(flowBusinessDefinitionJson, dto);
                dto.setDefinitionJson(JsonUtils.object2Json(flowBusinessDefinitionJson));
            }

            saveDto(dto);
        }
    }

    /**
     * 根据流程业务定义UUID列表删除流程业务定义
     *
     * @param uuids
     */
    @Override
    public void deleteAll(List<String> uuids) {
        flowBusinessDefinitionService.deleteByUuids(uuids);
    }

    /**
     * 根据流程定义ID获取表单字段下拉选项
     *
     * @param flowDefId
     * @return
     */
    @Override
    public List<Select2DataBean> getFormFieldSelectDataByFlowDefId(String flowDefId) {
        if (StringUtils.isBlank(flowDefId)) {
            return Collections.emptyList();
        }

        List<Select2DataBean> list = Lists.newArrayList();
        FlowDefinition flowDefinition = flowDefinitionService.getById(flowDefId);
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(flowDefinition.getFormUuid());
        if (dyFormFormDefinition != null) {
            List<DyformFieldDefinition> fieldDefinitions = dyFormFormDefinition.doGetFieldDefintions();
            for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
                list.add(new Select2DataBean(fieldDefinition.getFieldName(), fieldDefinition.getDisplayName()));
            }
        }
        return list;
    }

    /**
     * 根据流程定义ID获取流程环节下拉数据
     *
     * @param flowDefId
     * @return
     */
    @Override
    public List<Select2DataBean> getFlowTaskSelectDataByFlowDefId(String flowDefId) {
        if (StringUtils.isBlank(flowDefId)) {
            return Collections.emptyList();
        }

        List<Select2DataBean> list = Lists.newArrayList();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinitionService.getById(flowDefId));
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            if (Integer.valueOf(1).equals(node.getType())) {
                continue;
            }
            list.add(new Select2DataBean(node.getId(), node.getName()));
        }
        return list;
    }

    /**
     * 根据流程定义ID获取流程流向下拉数据
     *
     * @param flowDefId
     * @return
     */
    @Override
    public List<Select2DataBean> getFlowDirectionSelectDataByFlowDefId(String flowDefId) {
        if (StringUtils.isBlank(flowDefId)) {
            return Collections.emptyList();
        }

        List<Select2DataBean> list = Lists.newArrayList();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinitionService.getById(flowDefId));
        List<Direction> directions = flowDelegate.getAllDirections();
        for (Direction direction : directions) {
            list.add(new Select2DataBean(direction.getId(), direction.getName()));
        }
        return list;
    }

    /**
     * 获取流程业务监听器下拉数据
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData listFlowBusinessListenerSelectData(Select2QueryInfo queryInfo) {
        List<Select2DataBean> select2DataBeans = Lists.newArrayList();
        if (MapUtils.isNotEmpty(flowBusinessListenerMap)) {
            for (Map.Entry<String, FlowBusinessListener> entry : flowBusinessListenerMap.entrySet()) {
                select2DataBeans.add(new Select2DataBean(entry.getKey(), entry.getValue().getName()));
            }
        }
        return new Select2QueryData(select2DataBeans);
    }

    /**
     * 服务端返回传入的阶段树
     *
     * @param treeNodes
     * @return
     */
    @Override
    public List<TreeNode> serverReturnStageTree(List<TreeNode> treeNodes) {
        return treeNodes;
    }

    /**
     * select2查询接口
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        String hql = "select t.id as id, t.name as name from WfFlowBusinessDefinitionEntity t order by t.createTime desc";
        List<WfFlowBusinessDefinitionQueryItem> queryItems = flowBusinessDefinitionService.listItemHqlQuery(hql,
                WfFlowBusinessDefinitionQueryItem.class, null);
        return new Select2QueryData(queryItems, "id", "name");
    }

    /**
     * 通过ID查找Text.对于远程查找分页需要实现，否则无法设置选中。
     *
     * @param queryInfo
     * @return
     */
    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        List<String> ids = Arrays.asList(queryInfo.getIds());
        if (CollectionUtils.isEmpty(ids)) {
            return new Select2QueryData();
        }
        String hql = "select t.id as id, t.name as name from WfFlowBusinessDefinitionEntity t where t.id in(:ids)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("ids", ids);
        List<WfFlowBusinessDefinitionQueryItem> queryItems = flowBusinessDefinitionService.listItemHqlQuery(hql, WfFlowBusinessDefinitionQueryItem.class, values);
        return new Select2QueryData(queryItems, "id", "name");
    }

}
