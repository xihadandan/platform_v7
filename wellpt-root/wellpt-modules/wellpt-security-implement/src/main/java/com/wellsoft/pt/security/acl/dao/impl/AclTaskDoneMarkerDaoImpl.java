/*
 * @(#)1/8/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.dao.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.acl.dao.AclTaskDoneMarkerDao;
import com.wellsoft.pt.security.acl.entity.AclTaskDoneMarker;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 1/8/25.1	    zhulh		1/8/25		    Create
 * </pre>
 * @date 1/8/25
 */
@Repository
public class AclTaskDoneMarkerDaoImpl extends AbstractJpaDaoImpl<AclTaskDoneMarker, Long> implements AclTaskDoneMarkerDao {

    @Override
    public long countByAclTaskUuidAndUserId(String aclTaskUuid, String userId) {
        String hql = "select count(uuid) from AclTaskDoneMarker where aclTaskUuid = :aclTaskUuid and userId = :userId";
        Map<String, Object> params = new HashMap<>();
        params.put("aclTaskUuid", aclTaskUuid);
        params.put("userId", userId);
        return this.countByHQL(hql, params);
    }

    @Override
    public List<String> listUserIdByAclTaskUuids(List<String> aclTaskUuids) {
        List<String> userIds = Lists.newArrayList();
        String hql = "select t.userId as userId from AclTaskDoneMarker t where t.aclTaskUuid in(:aclTaskUuids)";
        ListUtils.handleSubList(aclTaskUuids, 1000, uuids -> {
            Map<String, Object> params = new HashMap<>();
            params.put("aclTaskUuids", uuids);
            userIds.addAll(this.listCharSequenceByHQL(hql, params));
        });
        return userIds;
    }

}
