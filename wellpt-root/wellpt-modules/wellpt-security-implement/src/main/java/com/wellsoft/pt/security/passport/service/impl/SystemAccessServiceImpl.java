/*
 * @(#)2013-5-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.service.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.passport.dao.SystemAccessDao;
import com.wellsoft.pt.security.passport.entity.SystemAccess;
import com.wellsoft.pt.security.passport.service.SystemAccessService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2013-5-3.1	zhulh		2013-5-3		Create
 * </pre>
 * @date 2013-5-3
 */
@Service
public class SystemAccessServiceImpl extends AbstractJpaServiceImpl<SystemAccess, SystemAccessDao, String> implements
        SystemAccessService {

    @Autowired
    private OrgApiFacade orgApiFacade;

    /**
     * 保存所有可访问系统的用户，以map(userids, usernames)的形式传递数据
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.passport.service.SystemAccessService#saveAllFromMap(java.util.Map)
     */
    @Override
    @Transactional
    public void saveAllFromMap(Map<String, String> map) {
        // 删除已经存在的记录
        for (SystemAccess systemAccess : listAll()) {
            this.dao.delete(systemAccess);
        }

        // 保存新记录
        String userIds = map.get("userIds");
        if (StringUtils.isNotBlank(userIds)) {
            int index = 0;
            for (String userId : userIds.split(Separator.SEMICOLON.getValue())) {
                SystemAccess systemAccess = new SystemAccess();
                systemAccess.setUserId(userId);
                systemAccess.setPermit(true);
                systemAccess.setSortOrder(index++);
                this.dao.save(systemAccess);
            }
        }
    }

    /**
     * 以map(userids, usernames)的形式，返回所有用户ID及用户名
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.passport.service.SystemAccessService#getAllAsMap()
     */
    @Override
    public Map<String, String> getAllAsMap() {
        // 获取所有记录
        List<SystemAccess> systemAccesses = this.dao.listByEntityAndPage(new SystemAccess(), null, "sortOrder asc");

        // 根据用户ID获取相应的用户名
        StringBuilder userIds = new StringBuilder();
        StringBuilder usernames = new StringBuilder();
        for (int index = 0; index < systemAccesses.size(); index++) {
            SystemAccess systemAccess = systemAccesses.get(index);

            userIds.append(systemAccess.getUserId());
            MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(systemAccess.getUserId());
            if (user != null) {
                usernames.append(user.getUserName());
            } else {
                usernames.append("");
            }
            if (index != (systemAccesses.size() - 1)) {
                userIds.append(Separator.SEMICOLON.getValue());
                usernames.append(Separator.SEMICOLON.getValue());
            }
        }

        // 以Map的形式返回
        Map<String, String> map = new HashMap<String, String>();
        map.put("usernames", usernames.toString());
        map.put("userIds", userIds.toString());

        return map;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.passport.service.SystemAccessService#isAllowLogin(java.lang.String)
     */
    @Override
    public boolean isAllowLogin(String userId) {
        // 空表示全部允许
        if (this.dao.countByHQL("select coutn(uuid) from  SystemAccess", null) == 0) {
            return true;
        }
        return this.dao.getCountByUserId(userId) > 0;
    }
}
