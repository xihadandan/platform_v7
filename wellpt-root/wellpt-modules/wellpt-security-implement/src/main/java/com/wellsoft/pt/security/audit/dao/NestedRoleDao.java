/*
 * @(#)2013-1-21 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.security.audit.entity.NestedRole;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-21.1	zhulh		2013-1-21		Create
 * </pre>
 * @date 2013-1-21
 */
public interface NestedRoleDao extends JpaDao<NestedRole, String> {

    /**
     * @param uuid
     */
    void deleteByRold(String uuid);

    /**
     * Description how to use this method
     *
     * @param uuid
     * @return
     */
    List<NestedRole> getByRole(String uuid);
}
