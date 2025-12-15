/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.UserLeader;
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
public class UserLeaderDao extends OrgHibernateDao<UserLeader, String> {

    private String QUERY_LEADER = "from UserLeader user_leader where user_leader.leader.uuid = ?";
    private String QUERY_BY_USER_UUID = "from UserLeader user_leader where user_leader.user.uuid = ?";
    private String DELETE_BY_USER_UUID = "delete from UserLeader user_leader where user_leader.user.uuid = ?";
    private String DELETE_LEADER_BY_USER_UUID = "delete from UserLeader user_leader where user_leader.leader.uuid = ?";

    /**
     * @param uuid
     * @return
     */
    public List<UserLeader> getByUser(String uuid) {
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
    public void deleteLeader(String uuid) {
        this.batchExecute(DELETE_LEADER_BY_USER_UUID, uuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<UserLeader> getLeaders(String uuid) {
        return this.find(QUERY_LEADER, uuid);
    }

}
