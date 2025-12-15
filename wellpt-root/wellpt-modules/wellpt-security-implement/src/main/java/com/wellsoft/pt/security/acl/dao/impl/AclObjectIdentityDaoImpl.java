/*
 * @(#)2012-10-30 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.acl.dao.AclObjectIdentityDao;
import com.wellsoft.pt.security.acl.entity.AclObjectIdentity;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
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
public class AclObjectIdentityDaoImpl extends AbstractJpaDaoImpl<AclObjectIdentity, String> implements
        AclObjectIdentityDao {

    private String COUNT_ACL = "select count(*) from AclObjectIdentity acl where acl.aclClass.cls = :cls and acl.objectIdIdentity = :objectIdIdentity";

    @SuppressWarnings("unchecked")
    public AclObjectIdentity findByObjectId(String cls, String objectIdIdentity) {
        List<AclObjectIdentity> list = getSession().createCriteria(AclObjectIdentity.class)
                .add(Restrictions.eq("objectIdIdentity", objectIdIdentity)).createCriteria("aclClass")
                .add(Restrictions.eq("cls", cls)).list();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 判断是否存在ACL
     *
     * @param entityClassName
     * @param objectIdIdentity
     * @param sid
     * @param principal
     * @return
     */
    public boolean isExists(String entityClassName, String objectIdIdentity) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("cls", entityClassName);
        values.put("objectIdIdentity", objectIdIdentity);
        return (Long) this.getNumberByHQL(COUNT_ACL, values) > 0;
    }
}
