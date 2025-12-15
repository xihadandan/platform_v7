/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.acl.dao.AclObjectIdentityDao;
import com.wellsoft.pt.security.acl.entity.AclObjectIdentity;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
public interface AclObjectIdentityService extends JpaService<AclObjectIdentity, AclObjectIdentityDao, String> {
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
