/*
 * @(#)Dec 26, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description: 工作流待办
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 26, 2016.1	zhulh		Dec 26, 2016		Create
 * </pre>
 * @date Dec 26, 2016
 */
@Component
public class WorkFlowTodoCategoryCountDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private AclTaskService aclTaskService;

    /**
     * @return
     */
    @Override
    public String getQueryName() {
        return "工作流程_待办_流程分类数量";
    }

    /**
     * @param context
     * @return
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("categoryUuid", "t.category_uuid", "流程分类UUID", String.class);
        criteriaMetadata.add("categoryName", "t.category_name", "流程分类名称", String.class);
        criteriaMetadata.add("categoryType", "t.category_type", "分类类型: 1流程分类，2流程定义", String.class);
        criteriaMetadata.add("parentUuid", "t.parent_uuid", "上级节点UUID", String.class);
        criteriaMetadata.add("count", "t.count", "待办数量", String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("listTodoTaskCategoryCountQuery", queryParams, QueryItem.class, null);
        Map<String, List<QueryItem>> categoryMap = Maps.newHashMap();
        queryItems.forEach(item -> {
            String categoryUuid = item.getString("categoryUuid");
            if (!categoryMap.containsKey(categoryUuid)) {
                categoryMap.put(categoryUuid, Lists.newArrayList());
            }
            categoryMap.get(categoryUuid).add(item);
        });

        List<QueryItem> returnItems = Lists.newArrayList();
        categoryMap.forEach((categoryUuid, items) -> {
            QueryItem categoryItem = new QueryItem();
            categoryItem.put("categoryUuid", categoryUuid);
            categoryItem.put("categoryName", items.get(0).getString("categoryName"));
            categoryItem.put("categoryType", 1);
            categoryItem.put("count", items.stream().mapToLong(item -> item.getLong("count")).sum());
            returnItems.add(categoryItem);

            items.forEach(item -> {
                QueryItem subItem = new QueryItem();
                subItem.put("categoryUuid", item.getString("id"));
                subItem.put("categoryName", item.getString("name"));
                subItem.put("categoryType", 2);
                subItem.put("count", item.getLong("count"));
                subItem.put("parentUuid", categoryUuid);
                returnItems.add(subItem);
            });
        });
        // Collections.sort(returnItems, (o1, o2) -> -o1.getLong("count").compareTo(o2.getLong("count")));
        return returnItems;
    }

    private Map<String, Object> getQueryParams(QueryContext context) {
        Map<String, Object> queryParams = context.getQueryParams();
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            queryParams.put("system", system);
        }
        queryParams.put("whereSql", context.getWhereSqlString());
        queryParams.put("orderBy", context.getOrderString());
        queryParams.put("userSids", PermissionGranularityUtils.getCurrentUserSids());
        queryParams.put("authWhere", aclTaskService.getAuthWhere(Lists.newArrayList(AclPermission.TODO)));
        return queryParams;
    }

    @Override
    public long count(QueryContext context) {
        return 0;
    }

    /**
     * @return
     */
    @Override
    public int getOrder() {
        return 10;
    }

}
