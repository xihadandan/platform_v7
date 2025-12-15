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
public class AbstractDataStoreQuery implements DataStoreQuery {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.DataStoreQuery#setDistinct(boolean)
     */
    @Override
    public DataStoreQuery setDistinct(boolean distinct) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.DataStoreQuery#setProjection(java.util.Set)
     */
    @Override
    public DataStoreQuery setProjection(Set<String> projection) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.DataStoreQuery#setConditions(java.util.List)
     */
    @Override
    public DataStoreQuery setConditions(List<Condition> conditions) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.DataStoreQuery#setProperties(java.util.Map)
     */
    @Override
    public void setProperties(Map<String, Object> values) {
        // TODO Auto-generated method stub

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.DataStoreQuery#setOrders(java.util.List)
     */
    @Override
    public DataStoreQuery setOrders(List<DataStoreOrder> orders) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.DataStoreQuery#setPagingInfo(com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public DataStoreQuery setPagingInfo(PagingInfo pagingInfo) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.DataStoreQuery#count()
     */
    @Override
    public long count() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.DataStoreQuery#list()
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> list() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.DataStoreQuery#list(java.lang.Class)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass) {
        // TODO Auto-generated method stub
        return null;
    }

}
