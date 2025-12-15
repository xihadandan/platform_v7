/*
 * @(#)2012-10-30 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.acl.dao.AclSidDao;
import com.wellsoft.pt.security.acl.entity.AclSid;
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
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-30.1	zhulh		2012-10-30		Create
 * </pre>
 * @date 2012-10-30
 */
@Repository
public class AclSidDaoImpl extends AbstractJpaDaoImpl<AclSid, String> implements AclSidDao {
    private final static String QUERY_ACL_SID = "from AclSid t where t.principal = :principal  and t.sid = :sid";

    private final static String IS_EXISTS_SID = "from AclSid t where t.principal = :principal  and t.sid = :sid";

    public AclSid get(AclSid aclSid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("principal", aclSid.getPrincipal());
        values.put("sid", aclSid.getSid());
        return this.getOneByHQL(QUERY_ACL_SID, values);
    }

    public boolean isExists(boolean principal, String sid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("principal", principal);
        values.put("sid", sid);
        return this.getOneByHQL(IS_EXISTS_SID, values) != null ? true : false;
    }
}
