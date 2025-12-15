/*
 * @(#)2016年10月19日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.dao.impl;

import com.wellsoft.pt.basicdata.datastore.dao.CdDataStoreDefinitionDao;
import com.wellsoft.pt.basicdata.datastore.entity.CdDataStoreDefinition;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月19日.1	xiem		2016年10月19日		Create
 * </pre>
 * @date 2016年10月19日
 */
@Repository
public class CdDataStoreDefinitionDaoImpl extends AbstractJpaDaoImpl<CdDataStoreDefinition, String> implements
        CdDataStoreDefinitionDao {

    @Override
    public CdDataStoreDefinition findById(String id) {
        return getUniqueBy("id", id);
    }

    @Override
    public void deleteByIds(String[] ids) {
        String hql = " delete from CdDataStoreDefinition o where o.id in :ids";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ids", ids);
        deleteByHQL(hql, params);
    }

    @Override
    public boolean idIsExists(String id, String uuid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        StringBuilder hql = new StringBuilder(" from CdDataStoreDefinition o where o.id = :id ");
        if (StringUtils.isNotBlank(uuid)) {
            hql.append(" and o.uuid <> :uuid ");
            params.put("uuid", uuid);
        }
        return countByHQL(hql.toString(), params) > 0;
    }


    @Override
    public List<CdDataStoreDefinition> listSimpleEntityByIds(String[] ids) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ids", ids);
        return listBySQL(
                "select o.uuid,o.creator,o.create_time,o.modifier,o.modify_time,o.id,o.code,o.name,o.type,o.module_id from cd_data_store_definition o where o.id in (:ids)",
                params);
    }

    @Override
    public List<CdDataStoreDefinition> listSimpleEntityBySystemUnitId(String systemUnitId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("systemUnitId", systemUnitId);
        return listBySQL(
                "select o.uuid,o.creator,o.create_time,o.modifier,o.modify_time,o.id,o.code,o.name,o.type,o.module_id from cd_data_store_definition o where o.system_unit_id = :systemUnitId order by o.modify_time desc",
                params);
    }
}
