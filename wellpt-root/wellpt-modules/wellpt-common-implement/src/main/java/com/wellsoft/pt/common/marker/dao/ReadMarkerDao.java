/*
 * @(#)2013-3-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.marker.dao;

import com.wellsoft.pt.common.marker.entity.ReadMarker;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-3.1	zhulh		2013-3-3		Create
 * </pre>
 * @date 2013-3-3
 */
@Repository
public class ReadMarkerDao extends HibernateDao<ReadMarker, String> {
    private static final String DELETE_BY_ENTITY_UUID = "delete from ReadMarker read_marker where read_marker.id.entityUuid = :entityUuid";

    private static final String DELETE_MARKER = "delete from ReadMarker read_marker where read_marker.id.entityUuid = :entityUuid and read_marker.id.userId = :userId";

    private static final String COUNT_BY_ENTITY_UUID = "select count(entityUuid) from ReadMarker read_marker where read_marker.id.entityUuid = :entityUuid and read_marker.userId = :userId";

    /**
     * @param uuid
     */
    public void deleteByEntityUuid(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("entityUuid", uuid);
        this.batchExecute(DELETE_BY_ENTITY_UUID, values);
    }

    public boolean isExist(String uuid, String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("entityUuid", uuid);
        values.put("userId", userId);
        return (Long) this.findUnique(COUNT_BY_ENTITY_UUID, values) > 0;
    }

    /**
     * @param uuid
     * @param userId
     */
    public void delete(String uuid, String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("entityUuid", uuid);
        values.put("userId", userId);
        this.batchExecute(DELETE_MARKER, values);
    }

}
