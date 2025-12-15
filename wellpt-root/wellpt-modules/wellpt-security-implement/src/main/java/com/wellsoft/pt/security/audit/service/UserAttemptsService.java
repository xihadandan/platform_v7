/*
 * @(#)Sep 7, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.audit.dao.UserAttemptsDao;
import com.wellsoft.pt.security.audit.entity.UserAttempts;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Sep 7, 2017.1	zhulh		Sep 7, 2017		Create
 * </pre>
 * @date Sep 7, 2017
 */
public interface UserAttemptsService extends JpaService<UserAttempts, UserAttemptsDao, String> {

    /**
     * 根据登录名获取尝试登录信息
     *
     * @param loginName
     * @return
     */
    UserAttempts getByLoginName(String loginName);

    /**
     * 尝试登录次数加1
     *
     * @param loginName
     */
    void addAttempts(String loginName);

    /**
     * 重置尝试登录次数
     *
     * @param loginName
     */
    void resetAttempts(String loginName);

    /**
     * 判断是否多次尝试登录，5分钟内大于5次失败时返回true
     *
     * @param loginName
     * @return
     */
    boolean isAttemptTooFrequently(String loginName);


}
