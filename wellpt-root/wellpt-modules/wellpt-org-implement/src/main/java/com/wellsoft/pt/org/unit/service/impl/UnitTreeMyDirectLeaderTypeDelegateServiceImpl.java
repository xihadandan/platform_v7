/*
 * @(#)2013-2-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service.impl;

import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Description: 我的直接领导
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-10-27.1  zhengky	2014-10-27	  Create
 * </pre>
 * @date 2014-10-27
 */
@Service
@Transactional
public class UnitTreeMyDirectLeaderTypeDelegateServiceImpl extends UnitTreeMyLeaderTypeDelegateServiceImpl {

    @Autowired
    private UserService userService;

    @Override
    public Document parserUnit(String type, String all, String login, String filterCondtion, HashMap<String, String> filterDisplayMap) {
        //List<User> users = userService.getLeaders(((UserDetails) SpringSecurityUtils.getCurrentUser()).getUserUuid());
        List<User> users = new ArrayList<User>();
        Set<String> ids = userService.getUserLeaderIds_new(((UserDetails) SpringSecurityUtils.getCurrentUser())
                .getUserId());
        for (String id : ids) {
            users.add(userService.getById(id));
        }
        return parseUsers(users, login, filterDisplayMap);
    }

}
