/*
 * @(#)2020年1月3日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
 * 2020年2月13日.1	wangrf		2020年2月13日		Create
 * </pre>
 * @date 2020年2月13日
 */
@Component
public class FormDefinitionDataStoreQuery extends AbstractDataStoreQueryInterface {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "公共资源_表单管理";
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
        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("name", "name", "名称", String.class);
        metadata.add("formType", "form_type", "表单类型", String.class);
        metadata.add("formTypeName", "form_type_name", "表单类型名称", String.class);
        metadata.add("code", "code", "code", String.class);
        metadata.add("id", "id", "ID", String.class);
        metadata.add("tableName", "table_name", "表名称", String.class);
        metadata.add("version", "version", "版本", String.class);
        metadata.add("level", "level", "节点的级别", Integer.class);
        metadata.add("parentUuid", "parent_uuid", "父节点Uuid", String.class);
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
     * 改造于multiOrgVersionFacade.getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo)
     * 方法
     * 前端上传的参数nodeid（点开二级分页时）、n_level
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        // 设置查询字段条件
        Map<String, Object> params = context.getQueryParams();
        List<QueryItem> result = new ArrayList<QueryItem>();
        params.put("orderBy", context.getOrderString());
        params.put("whereSql", context.getWhereSqlString());
        List<QueryItem> results = context.getNativeDao().namedQuery("dyFormDefinitionStore", params, QueryItem.class);
        if (!CollectionUtils.isEmpty(results)) {
            // id - version
            Map<String, List<QueryItem>> itemMap = new HashMap<String, List<QueryItem>>();
            Map<String, QueryItem> map = new HashMap<String, QueryItem>();
            for (QueryItem item : results) {
                // 获取根数据
                String key = item.getString("id");
                if (itemMap.containsKey(key)) {
                    itemMap.get(key).add(item);
                } else {
                    List<QueryItem> list = new ArrayList<QueryItem>();
                    list.add(item);
                    itemMap.put(key, list);
                }
                map.put(item.getString("id"), item);
            }
            // 遍历map集合
            QueryItem nItem = null;
            for (String key : itemMap.keySet()) {
                List<QueryItem> list = itemMap.get(key);
                if (list.size() == 1) {
                    nItem = list.get(0);
                    QueryItem item = dealData(nItem, null, true);
                    result.add(item);
                } else {
                    // 降序排列
                    Collections.sort(list, new Comparator<QueryItem>() {
                        @Override
                        public int compare(QueryItem o1, QueryItem o2) {
                            if (Double.valueOf(o1.getString("version")) > Double.valueOf(o2.getString("version"))) {
                                return -1;
                            }
                            if (Double.valueOf(o1.getString("version")) < Double.valueOf(o2.getString("version"))) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                    // 最大版本在第一个数据
                    String parentId = null;
                    for (int i = 0; i < list.size(); i++) {
                        nItem = list.get(i);
                        if (i == 0) {
                            parentId = nItem.getString("uuid");
                            QueryItem item = dealData(nItem, null, true);
                            result.add(item);
                        } else {
                            QueryItem item = dealData(nItem, parentId, true);
                            result.add(item);
                        }
                    }
                }
            }
        }
        context.getPagingInfo().setTotalCount(results.size());
        return result;
    }

    public QueryItem dealData(QueryItem queryItem, String parentId, Boolean isLeaf) {
        QueryItem item = new QueryItem();
        item.put("uuid", queryItem.getString("uuid"));
        item.put("name", queryItem.getString("name"));
        item.put("formType", queryItem.getString("formType"));
        item.put("formTypeName", queryItem.getString("formTypeName"));
        item.put("code", queryItem.getString("code"));
        item.put("id", queryItem.getString("id"));
        item.put("tableName", queryItem.getString("tableName"));
        item.put("version", queryItem.getString("version"));
        item.put("level", queryItem.getString("level"));
        item.put("parentUuid", parentId);
        item.put("isLeaf", isLeaf);
        return item;
    }
}
