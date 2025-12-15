/*
 * @(#)2013-5-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.passport.dao.SystemAccessDao;
import com.wellsoft.pt.security.passport.entity.SystemAccess;

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
public interface SystemAccessService extends JpaService<SystemAccess, SystemAccessDao, String> {
    /**
     * 保存所有可访问系统的用户，以map(userids, usernames)的形式传递数据
     *
     * @param map
     */
    void saveAllFromMap(Map<String, String> map);

    /**
     * 以map(userids, usernames)的形式，返回所有用户ID及用户名
     *
     * @return
     */
    Map<String, String> getAllAsMap();

    /**
     * 判断指定的用户ID是否允许登录系统
     *
     * @param userId
     * @return
     */
    boolean isAllowLogin(String userId);
}
