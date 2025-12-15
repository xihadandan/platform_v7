/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.sso.dao;

import com.wellsoft.pt.basicdata.sso.entity.Accounts;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	wubin		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
@Repository
public class AccountsDao extends HibernateDao<Accounts, String> {

    private static String DELETE_BY_SYSID = "delete from Accounts accounts where accounts.sysId = :sysId";
    private static String FIND_SSO_ACCOUNT = "from Accounts accounts where accounts.sysId = :sysId and accounts.userId = :userId";

    public Accounts getByUserId(String UserId) {
        return findUniqueBy("userId", UserId);
    }

    /**
     * 按sysId删除Accounts账户
     *
     * @param sysId
     */
    public void deleteBySYSID(String sysId) {
        Map<String, String> values = new HashMap<String, String>();
        values.put("sysId", sysId);
        batchExecute(DELETE_BY_SYSID, values);
    }

    public Accounts findAccount(String userId, String sysId) {
        Map<String, String> values = new HashMap<String, String>();
        values.put("sysId", sysId);
        values.put("userId", userId);
        List<Accounts> accounts = find(FIND_SSO_ACCOUNT, values);
        if (accounts == null || accounts.size() == 0) {
            return new Accounts();
        }
        return accounts.get(0);
    }
}
