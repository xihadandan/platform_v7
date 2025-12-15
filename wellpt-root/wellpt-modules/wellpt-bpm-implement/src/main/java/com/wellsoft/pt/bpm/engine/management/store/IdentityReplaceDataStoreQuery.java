/*
 * @(#)2020年1月19日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.management.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.element.PropertyElement;
import com.wellsoft.pt.bpm.engine.element.UnitElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.service.FlowSchemaService;
import com.wellsoft.pt.bpm.engine.util.ParticipantUtils;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月19日.1	wangrf		2020年1月19日		Create
 * </pre>
 * @date 2020年1月19日
 */
@Component
public class IdentityReplaceDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    FlowSchemaService flowSchemaService;

//    @Autowired
//    private FlowDefinitionService flowDefinitionService;
//
//    @Autowired
//    private FlowSchemeService flowSchemeService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "流程管理_流程办理人修改";
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("uuid", "t1.uuid", "UUID", String.class);
        metadata.add("rowId", "t1.uuid", "行标识", String.class);
        metadata.add("createTime", "t1.create_time", "创建时间", Date.class);
        metadata.add("modifyTime", "t1.modify_time", "修改时间", Date.class);
        metadata.add("category", "t1.category", "流程分类UUID", String.class);
        metadata.add("categoryName", "t2.name", "流程分类名称", String.class);
        metadata.add("name", "t1.name", "流程名称", String.class);
        metadata.add("id", "t1.id", "流程ID", String.class);
        metadata.add("code", "t1.code", "流程编号", String.class);
        metadata.add("version", "t1.version", "版本", Double.class);
        metadata.add("formUuid", "t1.form_uuid", "表单定义UUID", String.class);
        metadata.add("formName", "t1.form_name", "表单名称", String.class);
        metadata.add("enabled", "t1.enabled", "是否启用", Boolean.class);
        metadata.add("freeed", "t1.freeed", "是否自由流程", Boolean.class);
        metadata.add("remark", "t1.remark", "备注", String.class);
        metadata.add("deleteStatus", "t1.delete_status", "删除状态", Integer.class);
        metadata.add("deleteTime", "t1.delete_time", "删除时间", Date.class);
        metadata.add("nodeUuids", "t3.node_uuids", "节点UUID", String.class);
        metadata.add("nodeType", "t3.node_type", "人员节点类型", String.class);
        metadata.add("nodeName", "t3.node_name", "人员节点名称", String.class);
        metadata.add("nodeId", "t3.node_id", "人员节点ID", String.class);
        metadata.add("nodeUserAttribute", "t3.node_user_attribute", "人员节点属性", String.class);
        metadata.add("userValue", "t3.user_value", "人员ID", String.class);
        metadata.add("userArgValue", "t3.user_arg_value", "人员名称", String.class);
        metadata.add("userOrgId", "t3.user_org_id", "人员组织ID", String.class);
        metadata.add("creators", "creators", "发起人", String.class);
        metadata.add("users", "users", "参与人", String.class);
        metadata.add("monitors", "monitors", "督办人", String.class);
        metadata.add("admins", "admins", "监控者", String.class);
        metadata.add("viewers", "viewers", "阅读者", String.class);
        return metadata;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        String queryName = "flowDefinitionTaskUserQuery";
        boolean taskUserQuery = true;
        if (!"true".equals(context.getQueryParams().get("queryTaskUsers"))) {
            queryName = "flowDefinitionFlowUserQuery";
            taskUserQuery = false;
        }
        Map<String, Object> queryParams = getQueryParams(context);
        // 前端上传的参数
        List<QueryItem> queryItems = context.getNativeDao().namedQuery(queryName, queryParams,
                QueryItem.class, context.getPagingInfo());

        for (QueryItem queryItem : queryItems) {
            String uuid = queryItem.getString("nodeUuids");
            if (StringUtils.isNotBlank(uuid)) {
                queryItem.put("rowId", uuid);
            } else {
                queryItem.put("rowId", SnowFlake.getId());
            }
            if (taskUserQuery && StringUtils.equals("flow", queryItem.getString("nodeType"))) {
                String nodeUserAttribute = queryItem.getString("nodeUserAttribute");
                if (StringUtils.equals("creators", nodeUserAttribute)) {
                    queryItem.put("creators", queryItem.getString("userArgValue"));
                } else if (StringUtils.equals("users", nodeUserAttribute)) {
                    queryItem.put("users", queryItem.getString("userArgValue"));
                } else if (StringUtils.equals("monitors", nodeUserAttribute)) {
                    queryItem.put("monitors", queryItem.getString("userArgValue"));
                } else if (StringUtils.equals("admins", nodeUserAttribute)) {
                    queryItem.put("admins", queryItem.getString("userArgValue"));
                } else if (StringUtils.equals("viewers", nodeUserAttribute)) {
                    queryItem.put("viewers", queryItem.getString("userArgValue"));
                }
            }
        }
        return queryItems;
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("keyword", queryContext.getKeyword());
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());
        queryParams.put("moduleId", queryContext.getQueryParams().get("moduleId"));
        queryParams.put("categoryUuid", queryContext.getQueryParams().get("categoryUuid"));
        String currentUserUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        if (StringUtils.isBlank((String) queryParams.get("moduleId"))
                && !MultiOrgSystemUnit.PT_ID.equalsIgnoreCase(currentUserUnitId)) {
            queryParams.put("systemUnitIds", Lists.<String>newArrayList(MultiOrgSystemUnit.PT_ID, currentUserUnitId));
        }
        if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
            queryParams.put("systemIds", Lists.newArrayList(RequestSystemContextPathResolver.system()));
        }
        queryParams.put("tenantId", userDetails.getTenantId());
        return queryParams;
    }

    public void dealProperty(PropertyElement propertyElement, QueryItem item) {
        if (propertyElement != null) {
            // 取出发起人集合
            List<UserUnitElement> creators = propertyElement.getCreators();
            if (creators != null && creators.size() > 0) {
                StringBuilder creatorsBuff = new StringBuilder();
                for (UnitElement unitElement : creators) {
                    creatorsBuff.append(
                            StringUtils.isEmpty(unitElement.getArgValue()) ? unitElement.getValue() : unitElement
                                    .getArgValue()).append(";");
                }
                if (StringUtils.isNotEmpty(creatorsBuff.toString())) {
                    item.put("creator",
                            decorateRawNames((creatorsBuff.toString()).substring(0, (creatorsBuff.toString()).lastIndexOf(";"))));
                } else {
                    item.put("creator", "");// 发起人
                }
            } else {
                item.put("creator", "");// 发起人
            } // 发起人结束

            // 取出参与人集合
            List<UserUnitElement> propertyUsers = propertyElement.getUsers();
            if (propertyUsers != null && propertyUsers.size() > 0) {
                StringBuilder propertyUsersBuff = new StringBuilder();
                for (UnitElement unitElement : propertyUsers) {
                    propertyUsersBuff.append(
                            StringUtils.isEmpty(unitElement.getArgValue()) ? unitElement.getValue() : unitElement
                                    .getArgValue()).append(";");
                }
                if (StringUtils.isNotEmpty(propertyUsersBuff.toString())) {
                    item.put("users", decorateRawNames((propertyUsersBuff.toString()).substring(0,
                            (propertyUsersBuff.toString()).lastIndexOf(";"))));// 参与人
                } else {
                    item.put("users", "");// 参与人
                }
            } else {
                item.put("users", "");// 参与人
            } // 参与人结束

            // 取出督办人集合
            List<UserUnitElement> monitors = propertyElement.getMonitors();
            if (monitors != null && monitors.size() > 0) {
                StringBuilder monitorsBuff = new StringBuilder();
                for (UnitElement unitElement : monitors) {
                    monitorsBuff.append(
                            StringUtils.isEmpty(unitElement.getArgValue()) ? unitElement.getValue() : unitElement
                                    .getArgValue()).append(";");
                }
                if (StringUtils.isNotEmpty(monitorsBuff.toString())) {
                    item.put("monitors",
                            decorateRawNames((monitorsBuff.toString()).substring(0, (monitorsBuff.toString()).lastIndexOf(";"))));// 督办人
                } else {
                    item.put("monitors", "");// 督办人
                }
            } else {
                item.put("monitors", "");// 督办人
            } // 督办人结束

            // 取出监控者集合
            List<UserUnitElement> admins = propertyElement.getAdmins();
            if (admins != null && admins.size() > 0) {
                StringBuilder adminsBuff = new StringBuilder();
                for (UnitElement unitElement : admins) {
                    adminsBuff.append(
                            StringUtils.isEmpty(unitElement.getArgValue()) ? unitElement.getValue() : unitElement
                                    .getArgValue()).append(";");
                }
                if (StringUtils.isNotEmpty(adminsBuff.toString())) {
                    item.put("admins", decorateRawNames((adminsBuff.toString()).substring(0, (adminsBuff.toString()).lastIndexOf(";"))));// 监控者
                } else {
                    item.put("admins", "");// 监控者
                }
            } else {
                item.put("admins", "");// 监控者
            } // 监控者结束

            // 取出阅读者集合
            List<UserUnitElement> viewers = propertyElement.getViewers();
            if (viewers != null && viewers.size() > 0) {
                StringBuilder viewersBuff = new StringBuilder();
                for (UnitElement unitElement : viewers) {
                    viewersBuff.append(
                            StringUtils.isEmpty(unitElement.getArgValue()) ? unitElement.getValue() : unitElement
                                    .getArgValue()).append(";");
                }
                if (StringUtils.isNotEmpty(viewersBuff.toString())) {
                    item.put("viewers",
                            decorateRawNames((viewersBuff.toString()).substring(0, (viewersBuff.toString()).lastIndexOf(";"))));// 阅读者
                } else {
                    item.put("viewers", "");// 阅读者
                }
            } else {
                item.put("viewers", "");// 阅读者
            } // 阅读者结束
        }
    }

    /**
     * @param rawNames
     * @return
     */
    private String decorateRawNames(String rawNames) {
        if (StringUtils.isBlank(rawNames)) {
            return StringUtils.EMPTY;
        }

        List<String> names = Lists.newArrayList();
        Iterator<String> it = Arrays.asList(StringUtils.split(rawNames, Separator.SEMICOLON.getValue())).iterator();
        while (it.hasNext()) {
            String name = it.next();
            if (ParticipantUtils.containsKey(name)) {
                names.add(ParticipantUtils.getName(name));
            } else {
                names.add(name);
            }
        }
        return StringUtils.join(names, Separator.SEMICOLON.getValue());
    }

}
