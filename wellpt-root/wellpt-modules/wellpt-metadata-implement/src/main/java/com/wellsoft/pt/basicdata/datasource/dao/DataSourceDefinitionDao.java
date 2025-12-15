/*
 * @(#)2014-7-31 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.dao;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceDefinition;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import com.wellsoft.pt.org.entity.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-31.1	wubin		2014-7-31		Create
 * </pre>
 * @date 2014-7-31
 */
@Repository
public class DataSourceDefinitionDao extends HibernateDao<DataSourceDefinition, String> {

    public DataSourceDefinition getById(String id) {
        return findUniqueBy("dataSourceId", id);
    }

    public DataSourceDefinition getByJqGridId(String id) {
        return findUniqueBy("id", id);
    }

    public DataSourceDefinition getByUuid(String uuid) {
        return findUniqueBy("uuid", uuid);
    }

    public List<QueryItem> getByHql(String hql) {
        List<User> users = this.find("from User t  where 1=1", new HashMap<String, Object>());
        return null;
    }

    public List<DataSourceDefinition> queryByName(String dataSourceName, PagingInfo paginInfo) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "%" + dataSourceName + "%");
        return this.query(" from DataSourceDefinition o where o.dataSourceName like :name ", params,
                DataSourceDefinition.class, paginInfo);
    }

    public List<DataSourceDefinition> queryByIds(String[] dataSourceIds) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dataSourceIds", dataSourceIds);
        return this.find(" from DataSourceDefinition o where o.dataSourceId in :dataSourceIds ", params);
    }

    public boolean idIsExists(String id, String uuid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        StringBuilder hql = new StringBuilder(" from DataSourceDefinition o where o.dataSourceId = :id ");
        if (StringUtils.isNotBlank(uuid)) {
            hql.append(" and o.uuid <> :uuid ");
            params.put("uuid", uuid);
        }
        return this.query(hql.toString(), params, DataSourceDefinition.class).size() > 0;
    }
}
