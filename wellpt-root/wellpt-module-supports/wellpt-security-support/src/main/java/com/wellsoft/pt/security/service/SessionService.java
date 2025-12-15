/*
 * @(#)2012-12-1 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.service;

import com.wellsoft.pt.org.entity.User;

import java.util.Date;
import java.util.List;

/**
 * Description: 用户登录后session 中记录的用户、租户、当前用户的部门信息
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-1.1	lilin		2012-12-1		Create
 * </pre>
 * @date 2012-12-1
 */
public interface SessionService {

    /**
     * 获取在线用户列表
     *
     * @return
     */
    public List<User> getOnLineUsers();

    /**
     * 获取当前租户id
     *
     * @return
     */
    public String getTenant();

    /**
     * 如何描述该方法
     *
     * @param tenant
     */
    void setTenant(String tenant);

    // /**
    // * 获取当前用户所在部门id列表
    // *
    // * @return
    // */
    // public List<String> getDeptIds();
    //
    // /**
    // * 获取当前用户所在部门路径列表
    // *
    // * @return
    // */
    // public List<String> getDeptPaths();
    //
    // /**
    // * 获取当前用户所有群组和部门id列表
    // *
    // * @return
    // */
    // public List<String> getAllDeps();

    /**
     * 获取当前用户名
     *
     * @return
     */
    public String getUserLoginName();

    /**
     * 获取当前时间
     *
     * @return
     */
    public Date getCurrentDate();
}
