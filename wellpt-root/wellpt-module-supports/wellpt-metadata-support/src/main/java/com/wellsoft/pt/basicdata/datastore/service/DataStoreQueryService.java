/*
 * @(#)2017年4月27日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.service;


import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfiguration;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreQuery;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreQueryData;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreQueryInfo;
import com.wellsoft.pt.jpa.criteria.Criteria;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年4月27日.1	zhulh		2017年4月27日		Create
 * </pre>
 * @date 2017年4月27日
 */
public interface DataStoreQueryService extends BaseService {

    DataStoreQuery createQuery(String dataStoreId);

    DataStoreQuery createQuery(DataStoreQueryInfo queryInfo);

    Criteria createCriteriaQuery(DataStoreParams params, DataStoreConfiguration configuration);

    DataStoreQueryData query(DataStoreQueryInfo queryInfo);
}
