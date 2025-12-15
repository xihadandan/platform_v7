/*
 * @(#)Sep 7, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.audit.dao.UserAttemptsDao;
import com.wellsoft.pt.security.audit.entity.UserAttempts;
import com.wellsoft.pt.security.audit.service.UserAttemptsService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
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
 * 修改后版本	修改人		修改日期			修改内容
 * Sep 7, 2017.1	zhulh		Sep 7, 2017		Create
 * </pre>
 * @date Sep 7, 2017
 */
@Service
public class UserAttemptsServiceImpl extends AbstractJpaServiceImpl<UserAttempts, UserAttemptsDao, String> implements
        UserAttemptsService {

    @Autowired
    OrgApiFacade orgApiFacade;
    private String IS_ATTEMPT_TOO_FREQUENTLY = "select count(t.uuid) from UserAttempts t where t.loginName = :loginName and t.attempts >= 5 and t.latestAttemptTime > :deadlineTime";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.UserAttemptsService#getByLoginName(java.lang.String)
     */
    @Override
    public UserAttempts getByLoginName(String loginName) {
        if (StringUtils.isNotBlank(loginName)) {
            UserAttempts q = new UserAttempts();
            q.setLoginName(loginName);
            List<UserAttempts> objs = this.dao.listByEntity(q);
            if (CollectionUtils.isNotEmpty(objs)) {
                return objs.get(0);
            }
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.UserAttemptsService#addAttempts(java.lang.String)
     */
    @Override
    @Transactional
    public void addAttempts(String loginName) {
        if (StringUtils.isBlank(loginName)) {
            return;
        }
        UserAttempts userAttempts = this.getByLoginName(loginName);
        if (userAttempts == null) {
            userAttempts = new UserAttempts();
        }

        Integer attempts = userAttempts.getAttempts();
        if (attempts == null) {
            attempts = 0;
        }
        attempts++;

        userAttempts.setLoginName(loginName);
        userAttempts.setAttempts(attempts);
        userAttempts.setLatestAttemptTime(Calendar.getInstance().getTime());
        this.dao.getSession().save(userAttempts);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.UserAttemptsService#resetAttempts(java.lang.String)
     */
    @Override
    @Transactional
    public void resetAttempts(String loginName) {
        UserAttempts userAttempts = this.getByLoginName(loginName);
        if (userAttempts == null) {
            return;
        }
        userAttempts.setAttempts(0);
        this.dao.getSession().save(userAttempts);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.UserAttemptsService#isAttemptTooFrequently(java.lang.String)
     */
    @Override
    public boolean isAttemptTooFrequently(String loginName) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -5);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("loginName", loginName);
        values.put("deadlineTime", calendar.getTime());
        return (Long) this.dao.getNumberByHQL(IS_ATTEMPT_TOO_FREQUENTLY, values) > 0;
    }


}
