/*
 * @(#)1/8/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.acl.dao.AclTaskReadMarkerDao;
import com.wellsoft.pt.security.acl.entity.AclTaskReadMarker;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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
public class AclTaskReadMarkerDaoImpl extends AbstractJpaDaoImpl<AclTaskReadMarker, Long> implements AclTaskReadMarkerDao {

    /**
     * @param aclTaskUuid
     * @param userId
     * @return
     */
    @Override
    public Long countByAclTaskUuidAndUserId(String aclTaskUuid, String userId) {
        String hql = "select count(uuid) from AclTaskReadMarker where aclTaskUuid = :aclTaskUuid and userId = :userId";
        Map<String, Object> params = new HashMap<>();
        params.put("aclTaskUuid", aclTaskUuid);
        params.put("userId", userId);
        return this.countByHQL(hql, params);
    }

}
