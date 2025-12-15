/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.UserDeputy;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-15.1	zhulh		2013-1-15		Create
 * </pre>
 * @date 2013-1-15
 */
@Repository
public class UserDeputyDao extends OrgHibernateDao<UserDeputy, String> {
    private String QUERY_BY_USER_UUID = "from UserDeputy user_deputy where user_deputy.user.uuid = ?";
    private String DELETE_BY_USER_UUID = "delete from UserDeputy user_deputy where user_deputy.user.uuid = ?";
    private String DELETE_DEPUTY_BY_USER_UUID = "delete from UserDeputy user_deputy where user_deputy.deputy.uuid = ?";

    /**
     * @param uuid
     * @return
     */
    public List<UserDeputy> getByUser(String uuid) {
        return this.find(QUERY_BY_USER_UUID, uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByUser(String uuid) {
        this.batchExecute(DELETE_BY_USER_UUID, uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteDeputy(String uuid) {
        this.batchExecute(DELETE_DEPUTY_BY_USER_UUID, uuid);
    }
}
