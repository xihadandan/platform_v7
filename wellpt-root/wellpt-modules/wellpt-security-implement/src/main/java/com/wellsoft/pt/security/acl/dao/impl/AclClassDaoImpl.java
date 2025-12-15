/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.acl.dao.AclClassDao;
import com.wellsoft.pt.security.acl.entity.AclClass;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Repository
public class AclClassDaoImpl extends AbstractJpaDaoImpl<AclClass, String> implements AclClassDao {
    private final static String QUERY_ACL_CLASS = "from AclClass t where t.cls = :cls";

    public AclClass getByClasssName(String classsName) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("cls", classsName);
        List<AclClass> list = listByHQL(QUERY_ACL_CLASS, values);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
