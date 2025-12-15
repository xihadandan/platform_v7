/*
 * @(#)2013-5-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.passport.dao.SystemAccessDao;
import com.wellsoft.pt.security.passport.entity.SystemAccess;
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
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-3.1	zhulh		2013-5-3		Create
 * </pre>
 * @date 2013-5-3
 */
@Repository
public class SystemAccessDaoImpl extends AbstractJpaDaoImpl<SystemAccess, String> implements SystemAccessDao {

    public static final String GET_COUNT_BY_USER_ID = "select count(*) from SystemAccess system_access where system_access.userId = :userId";

    /**
     * @param userId
     * @return
     */
    public Long getCountByUserId(String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        return (Long) this.getNumberByHQL(GET_COUNT_BY_USER_ID, values);
    }

}
