/*
 * @(#)2017年5月2日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreOrder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月2日.1	zhulh		2017年5月2日		Create
 * </pre>
 * @date 2017年5月2日
 */
public interface DataStoreQuery {

    /**
     * 是否去重
     *
     * @param distinct
     */
    DataStoreQuery setDistinct(boolean distinct);

    /**
     * 设置查询的列，为空查询所有
     *
     * @param projection
     */
    DataStoreQuery setProjection(Set<String> projection);

    /**
     * 设置查询条件
     *
     * @param conditions
     * @return
     */
    DataStoreQuery setConditions(List<Condition> conditions);

    /**
     * 设置参数信息
     *
     * @param values
     */
    void setProperties(Map<String, Object> values);

    /**
     * 设置排序信息
     *
     * @param orders
     * @return
     */
    public DataStoreQuery setOrders(List<DataStoreOrder> orders);

    /**
     * 设置分页信息
     *
     * @param pagingInfo
     * @return
     */
    DataStoreQuery setPagingInfo(PagingInfo pagingInfo);

    long count();

    <ITEM extends Serializable> List<ITEM> list();

    <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass);
}
