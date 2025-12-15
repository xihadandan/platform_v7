/*
 * @(#)12/2/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.document;

import com.mongodb.DBObject;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.criterion.Criterion;
import com.wellsoft.pt.jpa.criterion.Order;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 文档操作接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/2/24.1	    zhulh		12/2/24		    Create
 * </pre>
 * @date 12/2/24
 */
public interface MongoDocumentService {

    /**
     * @param collectionName
     * @param dataMap
     * @return
     */
    String save(String collectionName, Map<String, Object> dataMap);

    /**
     * @param collectionName
     * @param id
     * @param dataMap
     * @return
     */
    String save(String collectionName, String id, Map<String, Object> dataMap);

    /**
     * @param collectionName
     * @param document
     * @return
     */
    String save(String collectionName, BaseObject document);

    /**
     * @param collectionName
     * @param id
     * @param document
     * @return
     */
    String save(String collectionName, String id, BaseObject document);

    /**
     * @param collectionName
     * @param document
     * @return
     */
    String save(String collectionName, JpaEntity document);

    /**
     * @param collectionName
     * @param id
     * @param document
     * @return
     */
    String save(String collectionName, String id, JpaEntity document);

    /**
     * @param collectionName
     * @param dbObject
     * @return
     */
    String save(String collectionName, DBObject dbObject);

    /**
     * @param collectionName
     * @param id
     * @return
     */
    DBObject getOne(String collectionName, String id);


    /**
     * @param collectionName
     * @param id
     * @return
     */
    Map<String, Object> getOneAsMap(String collectionName, String id);

    /**
     * @param collectionName
     * @param field
     * @param value
     * @return
     */
    Map<String, Object> getOneAsMapByFieldEq(String collectionName, String field, Object value);

    /**
     * @param collectionName
     * @param field
     * @param value
     * @return
     */
    List<Map<String, Object>> listByFieldEqValue(String collectionName, String field, Object value);

    List<Map<String, Object>> listByFieldInValue(String collectionName, String field, Collection value);


    /**
     * @param collectionName
     * @param field
     * @param value
     * @param pagingInfo
     * @return
     */
    List<Map<String, Object>> listByFieldEqValue(String collectionName, String field, Object value, PagingInfo pagingInfo);

    /**
     * @param collectionName
     * @param field
     * @param value
     * @return
     */
    boolean existsByFieldEq(String collectionName, String field, Object value);

    /**
     * @param collectionName
     * @param field
     * @param value
     * @return
     */
    String getObjectIdByFieldEq(String collectionName, String field, Object value);


    void saveOrUpdate(String collectionName, Map<String, Object> data, Map<String, Object> query);

    void updateByObjectId(String collectionName, DBObject dbObject, String id);

    /**
     * 查询数据
     *
     * @param collectionName
     * @param query
     * @param orders
     * @param pagingInfo
     * @return
     */
    List<QueryItem> listQueryItem(String collectionName, DBObject query, List<Order> orders, PagingInfo pagingInfo);

    /**
     * 查询数据
     *
     * @param collectionName
     * @param criterion
     * @param orders
     * @param pagingInfo
     * @return
     */
    List<QueryItem> listQueryItem(String collectionName, Criterion criterion, List<Order> orders, PagingInfo pagingInfo);

    Select2QueryData getCollectionNames(Select2QueryInfo queryInfo);

    void delete(String collectionName, DBObject query);
}
