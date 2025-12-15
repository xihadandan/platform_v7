/*
 * @(#)2013-5-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.security.passport.entity.SystemAccess;

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
public interface SystemAccessDao extends JpaDao<SystemAccess, String> {

    Long getCountByUserId(String userId);

}
