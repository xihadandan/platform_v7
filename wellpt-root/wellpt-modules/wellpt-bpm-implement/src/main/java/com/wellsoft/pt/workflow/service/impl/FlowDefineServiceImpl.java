/*
 * @(#)2012-10-19 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.jqgrid.JqTreeGridNode;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQueryItem;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.service.FlowSchemaService;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.log.enums.LogManageOperationEnum;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.service.FlowDefineService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Description: 流程定义服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-19.1	zhulh		2012-10-19		Create
 * </pre>
 * @date 2012-10-19
 */
@Service
public class FlowDefineServiceImpl implements FlowDefineService {

    private DecimalFormat versionFormat = new DecimalFormat("0.0");

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    @Autowired
    private FlowSchemaService flowSchemaService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefineService#query(com.wellsoft.pt.core.support.QueryInfo)
     */
    @Override
    public List<QueryItem> query(QueryInfo queryInfo) {
        String hql = "select flow_def.uuid as uuid, flow_def.name as name, flow_def.id as id, flow_def.version as version, flow_category.name as category "
                + "from FlowDefinition flow_def, FlowCategory flow_category where flow_def.category = flow_category.uuid  and (flow_def.deleteStatus = 0 or flow_def.deleteStatus is null)";
        String system = RequestSystemContextPathResolver.system();
        Map<String, Object> params = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(system)) {
            hql += " and flow_def.system = :system ";
            params.put("system", system);
        }
        if (StringUtils.isNotBlank(queryInfo.getOrderBy())) {
            hql += "order by " + queryInfo.getOrderBy();
        } else {
            hql += "order by flow_def.category, flow_def.id";
        }
        List<QueryItem> items = this.flowDefinitionService.listQueryItemByHQL(hql, params,
                queryInfo.getPagingInfo());
        return items;
    }

    /**
     * 根据流程分类编号，获取该分类下所有最高版本的流程定义
     *
     * @param catagoryCode 流程分类
     * @return
     */
    public List<QueryItem> getMaxVerFlowDefByCatagoryCode(String categoryCode) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("categoryCode", categoryCode);
        List<QueryItem> results = this.flowDefinitionService.listQueryItemByNameHQLQuery("topFlowDefinitionTreeQuery",
                values, null);
        return results;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefineService#getForPageAsTree(com.wellsoft.pt.common.component.jqgrid.JqGridQueryInfo, com.wellsoft.pt.core.support.QueryInfo)
     */
    @Override
    public QueryData getForPageAsTree(com.wellsoft.context.component.jqgrid.JqGridQueryInfo jqGridQueryInfo,
                                      QueryInfo queryInfo) {
        // 设置查询字段条件
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
        // 查询父节点为null的部门
        List<QueryItem> results = null;
        if (StringUtils.isBlank(jqGridQueryInfo.getNodeid())) {
            results = this.flowDefinitionService.listQueryItemByNameSQLQuery("topFlowDefinitionTreeQuery", values,
                    queryInfo.getPagingInfo());
        } else {
            String uuid = jqGridQueryInfo.getNodeid();
            FlowDefinition parent = this.flowDefinitionService.getOne(uuid);
            values.put("parentUuid", uuid);
            values.put("id", parent.getId());
            results = this.flowDefinitionService.listQueryItemByNameSQLQuery("flowDefinitionTreeQuery", values,
                    queryInfo.getPagingInfo());
        }
        // results = pageData.getResult();
        List<JqTreeGridNode> retResults = new ArrayList<JqTreeGridNode>();

        int level = jqGridQueryInfo.getN_level() == null ? 0 : jqGridQueryInfo.getN_level() + 1;
        String parentId = jqGridQueryInfo.getNodeid() == null ? "null" : jqGridQueryInfo.getNodeid();
        for (int index = 0; index < results.size(); index++) {
            QueryItem item = results.get(index);
            JqTreeGridNode node = new JqTreeGridNode();
            node.setId(item.get("id").toString());// id
            List<Object> cell = node.getCell();
            cell.add(item.get("uuid"));// UUID
            cell.add(item.get("name") + " (" + versionFormat.format(item.get("version")) + ")");// name
            cell.add(item.get("id"));// id
            cell.add(item.get("version"));// version
            cell.add(item.get("enabled"));// enabled
            cell.add(item.get("formName"));// formName
            cell.add(item.get("category"));// category
            cell.add(item.get("remark"));// remark
            // level field
            cell.add(level);
            // parent id field
            cell.add(parentId);
            // leaf field
            if (StringUtils.isBlank(jqGridQueryInfo.getNodeid())) {
                if (Double.valueOf(1).equals(item.get("version"))) {
                    cell.add(true);
                } else {
                    cell.add(this.flowDefinitionService.countById(item.get("id").toString()) <= 1);
                }
            } else {
                cell.add(true);
            }
            // expanded field 第一个节点展开
            if ("null".equals(parentId)) {
                cell.add(true);
            } else {
                cell.add(false);
            }

            retResults.add(node);
        }
        QueryData queryData = new QueryData();
        queryData.setDataList(retResults);
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefineService#save(com.wellsoft.pt.bpm.engine.entity.FlowDefinition)
     */
    @Override
    @Transactional
    public void save(FlowDefinition flowDefinition) {
        flowDefinitionService.save(flowDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefineService#get(java.lang.String)
     */
    @Override
    public FlowDefinition get(String uuid) {
        return flowDefinitionService.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefineService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        String msg = "流程定义已经被使用，无法彻底删除!";
        // 流程实例判断
        if (flowInstanceService.isFlowDefInUse(uuid)) {
            throw new BusinessException(msg);
        }
        // 等价流程判断
        FlowDefinition flowDefinition = flowDefinitionService.getOne(uuid);
        if (flowDefinition != null) {
            FlowDefinition example = new FlowDefinition();
            example.setEqualFlowId(flowDefinition.getId());
            // 等价流程的定义至少要有一个
            if (Long.valueOf(1).equals(this.flowDefinitionService.countByEntity(example))) {
                throw new BusinessException(msg);
            }
        }
        // task:6441 开发-主导-流程定义修改日志
        flowDefinitionService.saveLogManageOperation(null, flowDefinition, LogManageOperationEnum.delete);
        flowSchemaService.delete(flowDefinition.getFlowSchemaUuid());
        flowDefinitionService.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefineService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.remove(uuid);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getTaskMultiUserSubmitType(String flowDefinitionUuid, String taskId) {
        FlowDefinition flowDefinition = get(flowDefinitionUuid);
        if (FlowDelegateUtils.getFlowDelegate(flowDefinition).isAnyone(taskId)) {
            return "isAnyone";
        } else if (FlowDelegateUtils.getFlowDelegate(flowDefinition).isByOrder(taskId)) {
            return "isByOrder";
        }
        return "isMultiSubmit";
    }

    @Override
    @Transactional
    public void updateFlowDefCategory(String uuid, String categoryUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("uuid", uuid);
        param.put("category", categoryUuid);
        this.flowDefinitionService.updateByHQL("update FlowDefinition set " + (StringUtils.isBlank(categoryUuid) ? "category is null" : " category=:category ") + " where uuid=:uuid", param);
    }

    @Override
    public List<FlowDefinitionQueryItem> queryAllModuleFlowDefs(String moduleId) {
        return this.queryAllModuleFlowDefs(Lists.newArrayList(moduleId));
    }

    @Override
    public List<FlowDefinitionQueryItem> queryAllModuleFlowDefs(List<String> moduleId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("moduleId", moduleId);
        return this.flowDefinitionService.listItemByNameSQLQuery("queryAllModuleFlowDefs", FlowDefinitionQueryItem.class, param, null);
    }
}
