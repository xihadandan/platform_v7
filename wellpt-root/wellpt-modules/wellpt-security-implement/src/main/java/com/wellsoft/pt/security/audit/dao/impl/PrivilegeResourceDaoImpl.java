/*
 * @(#)2014-6-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.audit.dao.PrivilegeResourceDao;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
 * 2014-6-10.1	wubin		2014-6-10		Create
 * </pre>
 * @date 2014-6-10
 */
@Repository
public class PrivilegeResourceDaoImpl extends AbstractJpaDaoImpl<PrivilegeResource, String> implements
        PrivilegeResourceDao {

    private static final String GET_BY_PRIVILEGE_UUID = "from PrivilegeResource p where p.privilegeUuid = :privilegeUuid";

    private static final String DELETE_BY_PRIVILEGE_UUID = "delete from PrivilegeResource p where p.privilegeUuid = :privilegeUuid";

    /**
     * @param privilegeUuid
     */
    public List<PrivilegeResource> getByPrivilegeUuid(String privilegeUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("privilegeUuid", privilegeUuid);
        return this.listByHQL(GET_BY_PRIVILEGE_UUID, values);
    }

    /**
     * @param privilegeUuid
     */
    @Transactional
    public void deleteByPrivilegeUuid(String privilegeUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("privilegeUuid", privilegeUuid);
        this.deleteByHQL(DELETE_BY_PRIVILEGE_UUID, values);
    }

}
