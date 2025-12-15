/*
 * @(#)10/19/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/19/22.1	zhulh		10/19/22		Create
 * </pre>
 * @date 10/19/22
 */
@Component
public class BizProcessNodeInstanceDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private BizProcessItemInstanceService bizProcessItemInstanceService;

    /**
     * 返回查询名称
     *
     * @return
     */
    @Override
    public String getQueryName() {
        return "业务流程管理_过程节点办件";
    }

    /**
     * 返回查询信息元数据
     *
     * @param context
     * @return
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "UUID", String.class);
        criteriaMetadata.add("name", "t.name", "过程节点名称", String.class);
        criteriaMetadata.add("id", "t.id", "过程节点ID", String.class);
        criteriaMetadata.add("title", "t.title", "标题", String.class);
        criteriaMetadata.add("entityName", "t.entity_name", "业务主体名称", String.class);
        criteriaMetadata.add("entityId", "t.entity_id", "业务主体ID", String.class);
        criteriaMetadata.add("formUuid", "t.form_uuid", "表单定义UUID", String.class);
        criteriaMetadata.add("dataUuid", "t.data_uuid", "表单数据UUID", String.class);
        criteriaMetadata.add("startTime", "t.start_time", "开始时间", Date.class);
        criteriaMetadata.add("endTime", "t.end_time", "结束时间", Date.class);
        criteriaMetadata.add("state", "t.state", "办理状态", String.class);
        criteriaMetadata.add("processInstUuid", "t.process_inst_uuid", "业务流程实例UUID", String.class);
        criteriaMetadata.add("processDefUuid", "t.process_def_uuid", "业务流程定义UUID", String.class);
        criteriaMetadata.add("itemInstCount", "t.item_inst_count", "过程节点下的事项实例数量", Long.class);
        criteriaMetadata.add("creator", "t.creator", "创建人ID", String.class);
        criteriaMetadata.add("createTime", "t.create_time", "创建时间", Date.class);
        criteriaMetadata.add("modifier", "t.modifier", "修改人ID", String.class);
        criteriaMetadata.add("modifyTime", "t.modify_time", "修改时间", Date.class);
        criteriaMetadata.add("recVer", "t.rec_ver", "版本号", Integer.class);
        return criteriaMetadata;
    }

    /**
     * 如何描述该方法
     *
     * @param queryContext
     * @return
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("processNodeInstanceDataStoreQuery",
                queryParams, QueryItem.class, queryContext.getPagingInfo());
        Object loadItemInstCount = queryParams.get("loadItemInstCount");
        // 加载过程节点下的事项实例数量
        if (StringUtils.equals("true", Objects.toString(loadItemInstCount)) && CollectionUtils.isNotEmpty(queryItems)) {
            List<String> processNodeInstUuids = Lists.newArrayList();
            queryItems.stream().forEach(item -> processNodeInstUuids.add(item.getString("uuid")));
            Map<String, Long> countMap = bizProcessItemInstanceService.getCountMapByProcessNodeInstUuids(processNodeInstUuids);
            for (QueryItem item : queryItems) {
                String processNodeInstUuid = item.getString("uuid");
                if (countMap.containsKey(processNodeInstUuid)) {
                    item.put("item_inst_count", countMap.get(processNodeInstUuid));
                }
            }
        }
        return queryItems;
    }


    /**
     * 返回查询的条数
     *
     * @param queryContext
     * @return
     */
    @Override
    public long count(QueryContext queryContext) {
        return queryContext.getPagingInfo().getTotalCount();
    }
}
