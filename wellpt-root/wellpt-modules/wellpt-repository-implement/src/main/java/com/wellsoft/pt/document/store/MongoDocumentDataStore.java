/*
 * @(#)8/15/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.document.store;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.document.MongoDocumentService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.criterion.Order;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/15/25.1	    zhulh		8/15/25		    Create
 * </pre>
 * @date 8/15/25
 */
@Component
public class MongoDocumentDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private MongoDocumentService mongoDocumentService;

    @Override
    public String getQueryName() {
        return "MongoDB文档_集合数据仓库查询";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("_id", "t._id", "文档ID", String.class);
        // 添加集合字段
        String collectionName = getCollectionName(context);
        this.addCollectionFieldCriteriaMetadata(collectionName, criteriaMetadata, context);
        return criteriaMetadata;
    }

    private void addCollectionFieldCriteriaMetadata(String collectionName, CriteriaMetadata criteriaMetadata, QueryContext context) {
        if (StringUtils.isBlank(collectionName)) {
            return;
        }

        List<Order> orders = Lists.newArrayList();
        orders.add(Order.desc("createTime"));
        List<QueryItem> queryItems = mongoDocumentService.listQueryItem(collectionName, new BasicDBObject(), orders, new PagingInfo(1, 10));
        queryItems.forEach(queryItem -> {
            queryItem.forEach((key, value) -> {
                if (value == null) {
                    return;
                }
                if (!criteriaMetadata.containsColumnIndex(key)) {
                    criteriaMetadata.add(key, "t." + key, key, getCollectionFieldType(value));
                }
            });
        });
    }

    private Class<?> getCollectionFieldType(Object value) {
        if (value instanceof Integer) {
            return Integer.class;
        } else if (value instanceof Long) {
            return Long.class;
        } else if (value instanceof Double || value instanceof Number) {
            return Double.class;
        } else if (value instanceof Float) {
            return Float.class;
        } else if (value instanceof Boolean) {
            return Boolean.class;
        } else if (value instanceof Date) {
            return Date.class;
        }
        return String.class;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        String collectionName = getCollectionName(context);
        if (StringUtils.isBlank(collectionName)) {
            return Collections.emptyList();
        }

        List<QueryItem> queryItems = mongoDocumentService.listQueryItem(collectionName, context.getCriterion(), context.getOrders(), context.getPagingInfo());
        queryItems.forEach(queryItem -> {
            Object _id = queryItem.get("_id");
            if (_id instanceof ObjectId) {
                queryItem.put("_id", _id.toString());
            }
        });
        return queryItems;
    }

    private String getCollectionName(QueryContext context) {
        MongoDocumentDataStoreInterfaceParam interfaceParam = context.interfaceParam(MongoDocumentDataStoreInterfaceParam.class);
        return interfaceParam == null ? StringUtils.EMPTY : interfaceParam.getCollectionName();
    }

    @Override
    public long count(QueryContext context) {
        return context.getPagingInfo().getTotalCount();
    }

    @Override
    public Class<? extends InterfaceParam> interfaceParamsClass() {
        return MongoDocumentDataStoreInterfaceParam.class;
    }

}
