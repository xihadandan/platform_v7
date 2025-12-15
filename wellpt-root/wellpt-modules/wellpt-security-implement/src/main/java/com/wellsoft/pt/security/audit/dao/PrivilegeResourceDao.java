/*
 * @(#)2014-6-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;

import java.util.List;

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
public interface PrivilegeResourceDao extends JpaDao<PrivilegeResource, String> {

    /**
     * @param privilegeUuid
     */
    List<PrivilegeResource> getByPrivilegeUuid(String privilegeUuid);

    /**
     * @param privilegeUuid
     */
    void deleteByPrivilegeUuid(String privilegeUuid);
}
