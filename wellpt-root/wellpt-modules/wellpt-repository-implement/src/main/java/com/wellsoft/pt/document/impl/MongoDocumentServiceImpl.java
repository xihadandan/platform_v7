/*
 * @(#)12/2/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.document.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.document.MongoDocumentService;
import com.wellsoft.pt.jpa.criterion.*;
import com.wellsoft.pt.repository.dao.base.BaseMongoDao;
import com.wellsoft.pt.repository.support.convert.FileUtil;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Description:
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
@Service
public class MongoDocumentServiceImpl implements MongoDocumentService {

    @Autowired
    private BaseMongoDao baseMongoDao;

    /**
     * @param collectionName
     * @param dataMap
     * @return
     */
    @Override
    public String save(String collectionName, Map<String, Object> dataMap) {
        return this.save(collectionName, null, dataMap);
    }

    /**
     * @param collectionName
     * @param id
     * @param dataMap
     * @return
     */
    @Override
    public String save(String collectionName, String id, Map<String, Object> dataMap) {
        BasicDBObject dbObject = new BasicDBObject();
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            dbObject.put(entry.getKey(), entry.getValue());
        }
        if (StringUtils.isNotBlank(id)) {
            dbObject.put("_id", id);
        }
        DBObject newObject = baseMongoDao.createDocument(FileUtil.getCurrentTenantId(), collectionName, dbObject);
        Object _id = newObject.get("_id");
        return _id.toString();
    }

    /**
     * @param collectionName
     * @param document
     * @return
     */
    @Override
    public String save(String collectionName, BaseObject document) {
        return this.saveObject(collectionName, null, document);
    }

    /**
     * @param collectionName
     * @param id
     * @param document
     * @return
     */
    @Override
    public String save(String collectionName, String id, BaseObject document) {
        return this.saveObject(collectionName, id, document);
    }

    /**
     * @param collectionName
     * @param id
     * @param document
     * @return
     */
    private String saveObject(String collectionName, String id, Object document) {
        BasicDBObject dbObject = new BasicDBObject();
        BeanWrapper wrapper = new BeanWrapperImpl(document);
        PropertyDescriptor[] propertyDescriptors = wrapper.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getName();
            if (StringUtils.equals("class", propertyName)) {
                continue;
            }
            dbObject.put(propertyName, wrapper.getPropertyValue(propertyName));
        }
        if (StringUtils.isNotBlank(id)) {
            dbObject.put("_id", id);
        }
        DBObject newObject = baseMongoDao.createDocument(FileUtil.getCurrentTenantId(), collectionName, dbObject);
        Object _id = newObject.get("_id");
        return _id.toString();
    }

    @Override
    public String save(String collectionName, JpaEntity document) {
        return this.saveObject(collectionName, null, document);
    }

    @Override
    public String save(String collectionName, String id, JpaEntity document) {
        return this.saveObject(collectionName, id, document);
    }

    /**
     * @param collectionName
     * @param dbObject
     * @return
     */
    @Override
    public String save(String collectionName, DBObject dbObject) {
        DBObject newObject = baseMongoDao.createDocument(FileUtil.getCurrentTenantId(), collectionName, dbObject);
        Object _id = newObject.get("_id");
        return _id.toString();
    }

    /**
     * @param collectionName
     * @param id
     * @return
     */
    @Override
    public DBObject getOne(String collectionName, String id) {
        return baseMongoDao.findDocumentByObjectIdId(FileUtil.getCurrentTenantId(), collectionName, id);
    }

    /**
     * @param collectionName
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> getOneAsMap(String collectionName, String id) {
        DBObject dbObject = baseMongoDao.findDocumentByObjectIdId(FileUtil.getCurrentTenantId(), collectionName, id);
        return dbObject == null ? Collections.emptyMap() : dbObject.toMap();
    }

    /**
     * @param collectionName
     * @param field
     * @param value
     * @return
     */
    @Override
    public Map<String, Object> getOneAsMapByFieldEq(String collectionName, String field, Object value) {
        List<Map<String, Object>> list = this.listByFieldEqValue(collectionName, field, value, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    /**
     * @param collectionName
     * @param field
     * @param value
     * @return
     */
    @Override
    public List<Map<String, Object>> listByFieldEqValue(String collectionName, String field, Object value) {
        return listByFieldEqValue(collectionName, field, value, new PagingInfo(1, Integer.MAX_VALUE));
    }

    @Override
    public List<Map<String, Object>> listByFieldInValue(String collectionName, String field, Collection values) {
        // 创建查询条件
        DBObject query = new BasicDBObject(field,
                new BasicDBObject("$in",
                        values.toArray(new Object[]{})));
        DBCursor dBCursor = baseMongoDao.findDocument(FileUtil.getCurrentTenantId(), collectionName, query);
        List<Map<String, Object>> queryItems = new ArrayList<>();
        // 将游标中返回的结果记录到list集合中
        while (dBCursor.hasNext()) {
            queryItems.add(Maps.newHashMap(dBCursor.next().toMap()));
        }
        dBCursor.close();
        return queryItems;
    }

    /**
     * @param collectionName
     * @param field
     * @param value
     * @param pagingInfo
     * @return
     */
    @Override
    public List<Map<String, Object>> listByFieldEqValue(String collectionName, String field, Object value, PagingInfo pagingInfo) {
        DBObject query = new BasicDBObject();
        query.put(field, value);
        DBCursor dBCursor = baseMongoDao.findDocument(FileUtil.getCurrentTenantId(), collectionName, query);
        // 按创建时间倒叙
        dBCursor.sort(new BasicDBObject("createTime", -1));
        //分页
        if (pagingInfo != null) {
            dBCursor.limit(pagingInfo.getPageSize());
            dBCursor.skip(pagingInfo.getFirst());
            pagingInfo.setTotalCount(dBCursor.count());
        }
        List<Map<String, Object>> queryItems = new ArrayList<>();
        // 将游标中返回的结果记录到list集合中
        while (dBCursor.hasNext()) {
            queryItems.add(Maps.newHashMap(dBCursor.next().toMap()));
        }
        dBCursor.close();
        return queryItems;
    }

    @Override
    public boolean existsByFieldEq(String collectionName, String field, Object value) {
        DBObject query = new BasicDBObject();
        query.put(field, value);
        return baseMongoDao.countDocument(FileUtil.getCurrentTenantId(), collectionName, query) > 0;
    }

    @Override
    public String getObjectIdByFieldEq(String collectionName, String field, Object value) {
        DBObject query = new BasicDBObject();
        query.put(field, value);
        DBObject projection = new BasicDBObject();
        projection.put("_id", "_id");
        DBCursor dBCursor = baseMongoDao.findDocument(FileUtil.getCurrentTenantId(), collectionName, query, projection);
        // 按创建时间倒叙
        dBCursor.sort(new BasicDBObject("createTime", -1));
        return dBCursor.hasNext() ? Objects.toString(dBCursor.next().get("_id"), null) : null;
    }

    @Override
    public void saveOrUpdate(String collectionName, Map<String, Object> data, Map<String, Object> query) {
        baseMongoDao.saveOrUpdate(collectionName, data, query);
    }

    @Override
    public void updateByObjectId(String collectionName, DBObject dbObject, String id) {
        DBObject query = new BasicDBObject("_id", new ObjectId(id));
        baseMongoDao.updateDocument(FileUtil.getCurrentTenantId(), collectionName, dbObject, query);
    }

    @Override
    public List<QueryItem> listQueryItem(String collectionName, DBObject query, List<Order> orders, PagingInfo pagingInfo) {
        DBCursor dBCursor = baseMongoDao.findDocument(FileUtil.getCurrentTenantId(), collectionName, query);
        // 排序
        if (CollectionUtils.isNotEmpty(orders)) {
            BasicDBObject sort = new BasicDBObject();
            orders.forEach(order -> {
                sort.put(order.getColumnIndex(), order.isAscending() ? 1 : -1);
            });
            dBCursor.sort(sort);
        }
        // 分页
        if (pagingInfo != null) {
            dBCursor.limit(pagingInfo.getPageSize());
            dBCursor.skip(pagingInfo.getFirst());
            pagingInfo.setTotalCount(dBCursor.count());
        }
        List<QueryItem> queryItems = Lists.newArrayList();
        // 将游标中返回的结果记录到list集合中
        while (dBCursor.hasNext()) {
            QueryItem queryItem = new QueryItem();
            dBCursor.next().toMap().forEach((k, v) -> {
                queryItem.put(Objects.toString(k), v, false);
            });
            queryItems.add(queryItem);
        }
        dBCursor.close();
        return queryItems;
    }

    @Override
    public List<QueryItem> listQueryItem(String collectionName, Criterion criterion, List<Order> orders, PagingInfo pagingInfo) {
        DBObject query = criterion2DbObject(criterion, RequestSystemContextPathResolver.system());
        return this.listQueryItem(collectionName, query, orders, pagingInfo);
    }

    private DBObject criterion2DbObject(Criterion criterion, String system) {
        DBObject query = new BasicDBObject();
        if (criterion instanceof Conjunction) {
            List<DBObject> conditions = Lists.newArrayList();
            Conjunction conjunction = (Conjunction) criterion;
            if (CollectionUtils.isNotEmpty(conjunction.getConditions())) {
                conditions.addAll(criterions2DbObject(conjunction.getConditions()));
            }
            if (StringUtils.isNotBlank(system)) {
                conditions.add(new BasicDBObject("system", system));
            }
            if (CollectionUtils.isNotEmpty(conditions)) {
                query.put("$and", conditions);
            }
        } else if (criterion instanceof Junction) {
            Junction junction = (Junction) criterion;
            if (CollectionUtils.isNotEmpty(junction.getConditions())) {
                List<DBObject> conditions = Lists.newArrayList();
                conditions.addAll(criterions2DbObject(junction.getConditions()));
                query.put("$or", conditions);
            }
        } else if (criterion instanceof LikeExpression) {
            LikeExpression likeExpression = (LikeExpression) criterion;
            String likeValue = likeExpression.getValue();
            if (StringUtils.startsWith(likeValue, "%")) {
                likeValue = StringUtils.substringAfter(likeValue, "%");
            }
            if (StringUtils.endsWith(likeValue, "%")) {
                likeValue = StringUtils.substringBeforeLast(likeValue, "%");
            }
            query.put(likeExpression.getColumnIndex(), Pattern.compile("^.*" + likeValue + ".*$", Pattern.CASE_INSENSITIVE));
        } else if (criterion instanceof NotLikeExpression) {
            NotLikeExpression notLikeExpression = (NotLikeExpression) criterion;
            String notLikeValue = notLikeExpression.getValue();
            if (StringUtils.startsWith(notLikeValue, "%")) {
                notLikeValue = StringUtils.substringAfter(notLikeValue, "%");
            }
            if (StringUtils.endsWith(notLikeValue, "%")) {
                notLikeValue = StringUtils.substringBeforeLast(notLikeValue, "%");
            }
            query.put(notLikeExpression.getColumnIndex(), new BasicDBObject("$not", Pattern.compile("^.*" + notLikeValue + ".*$", Pattern.CASE_INSENSITIVE)));
        } else if (criterion instanceof BetweenExpression) {
            BetweenExpression betweenExpression = (BetweenExpression) criterion;
            Object lo = betweenExpression.getLo();
            Object hi = betweenExpression.getHi();
            try {
                if (lo instanceof String) {
                    lo = DateUtils.parse(lo.toString());
                }
                if (hi instanceof String) {
                    hi = DateUtils.parse(hi.toString());
                }
            } catch (Exception e) {
            }
            query.put(betweenExpression.getColumnIndex(), new BasicDBObject("$gte", lo).append("$lte", hi));
        } else if (criterion instanceof InExpression) {
            InExpression inExpression = (InExpression) criterion;
            query.put(inExpression.getColumnIndex(), new BasicDBObject("$in", inExpression.getValues()));
        } else if (criterion instanceof SimpleExpression) {
            SimpleExpression simpleExpression = (SimpleExpression) criterion;
            String op = simpleExpression.getOp();
            if ("=".equals(op) || StringUtils.isBlank(op)) {
                query.put(simpleExpression.getColumnIndex(), simpleExpression.getValue());
            } else if ("<>".equals(op)) {
                query.put(simpleExpression.getColumnIndex(), new BasicDBObject("$ne", simpleExpression.getValue()));
            } else if ("<".equals(op)) {
                query.put(simpleExpression.getColumnIndex(), new BasicDBObject("$lt", simpleExpression.getValue()));
            } else if ("<=".equals(op)) {
                query.put(simpleExpression.getColumnIndex(), new BasicDBObject("$lte", simpleExpression.getValue()));
            } else if (">".equals(op)) {
                query.put(simpleExpression.getColumnIndex(), new BasicDBObject("$gt", simpleExpression.getValue()));
            } else if (">=".equals(op)) {
                query.put(simpleExpression.getColumnIndex(), new BasicDBObject("$gte", simpleExpression.getValue()));
            }
        } else {
            if (StringUtils.isNotBlank(system)) {
                query.put("system", system);
            }
        }
        return query;
    }

    private List<DBObject> criterions2DbObject(List<Criterion> conditions) {
        List<DBObject> dbObjects = Lists.newArrayList();
        conditions.forEach(condition -> {
            dbObjects.add(criterion2DbObject(condition, null));
        });
        return dbObjects;
    }

    @Override
    public Select2QueryData getCollectionNames(Select2QueryInfo queryInfo) {
        List<String> collectionNames = baseMongoDao.getCollectionNames().stream()
                .filter(name -> !StringUtils.contains(name, "fs."))
                .collect(Collectors.toList());
        Collection<Select2DataBean> results = Lists.newArrayList();
        collectionNames.forEach(name -> {
            results.add(new Select2DataBean(name, name));
        });
        return new Select2QueryData(results);
    }

    @Override
    public void delete(String collectionName, DBObject query) {
        baseMongoDao.deleteDocument(FileUtil.getCurrentTenantId(), collectionName, query);
    }

}
