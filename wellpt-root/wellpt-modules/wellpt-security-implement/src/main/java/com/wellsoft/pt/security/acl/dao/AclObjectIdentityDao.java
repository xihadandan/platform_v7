/*
 * @(#)2012-10-30 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.security.acl.entity.AclObjectIdentity;

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
public interface AclObjectIdentityDao extends JpaDao<AclObjectIdentity, String> {

    AclObjectIdentity findByObjectId(String cls, String objectIdIdentity);

    /**
     * 判断是否存在ACL
     *
     * @param entityClassName
     * @param objectIdIdentity
     * @param sid
     * @param principal
     * @return
     */
    boolean isExists(String entityClassName, String objectIdIdentity);
}
