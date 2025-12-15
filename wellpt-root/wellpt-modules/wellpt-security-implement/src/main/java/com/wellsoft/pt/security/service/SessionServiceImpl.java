/*
 * @(#)2012-12-1 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.service;

import com.google.common.collect.Lists;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
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
@Service("sessionService")
// @Scope("singleton")
public class SessionServiceImpl implements SessionService {
    private String tenant = Config.DEFAULT_TENANT;
    @Autowired
    private OnLineUser onlineUser;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.context.SessionService#getOnLineUsers()
     */
    @Override
    public List<User> getOnLineUsers() {
        Map<Object, Date> onlineusermap = this.onlineUser.getActiveUsers();
        List<User> userlist = Lists.newArrayList();
        Set userdetailset = onlineusermap.keySet();
        for (Object userdetail : userdetailset) {
            UserDetails u = (UserDetails) userdetail;
            UserService userService = (UserService) ApplicationContextHolder.getInstance().getBean("userService");
            userlist.add(userService.getByLoginName(u.getUsername()));
        }
        return userlist;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.context.SessionService#getTenant()
     */
    @Override
    public String getTenant() {
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        if (userDetail != null) {
            return userDetail.getTenant();
        }
        return tenant;
    }

    @Override
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.context.SessionService#getUserLoginName()
     */
    @Override
    public String getUserLoginName() {
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        return SpringSecurityUtils.getCurrentLoginName();
    }

    // /**
    // * 获取当前用户所在部门id列表
    // *
    // * @return
    // */
    // @Override
    // public List<String> getDeptIds() {
    // UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
    // return userDetail.getDepartmentIds();
    // }
    //
    // /**
    // * 获取当前用户所在部门路径列表
    // *
    // * @return
    // */
    // @Override
    // public List<String> getDeptPaths() {
    // UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
    // return userDetail.getDepartmentPaths();
    // }
    //
    // /**
    // * 获取当前用户所有群组和部门id列表
    // *
    // * @return
    // */
    // @Override
    // public List<String> getAllDeps() {
    // UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
    // return userDetail.getDepartmentPaths();
    // }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.context.SessionService#getCurrentDate()
     */
    @Override
    public Date getCurrentDate() {
        // TODO Auto-generated method stub
        return new Date(System.currentTimeMillis());
    }

}
