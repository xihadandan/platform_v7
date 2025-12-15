/*
 * @(#)2014-3-21 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-21.1	zhulh		2014-3-21		Create
 *          	linxr		2018-9-21		添加权限调用方法
 * </pre>
 * @date 2014-3-21
 */
@Controller
@RequestMapping(value = "/security/user/details/")
public class UserDetailsController extends BaseController {
    @Autowired
    private SecurityApiFacade securityApiFacade;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public UserDetails getCurrentUser(HttpServletRequest request, HttpServletResponse response) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (userDetails != null && StringUtils.isBlank(userDetails.getTokenId())) {
            // 绑定用户JSESSIONID(JSESSIONID=${TOKEN})
            userDetails.setTokenId(request.getSession().getId());
        }
        return userDetails;
    }

    @RequestMapping(value = "isGranted")
    @ResponseBody
    public boolean isGranted(String path, String functionType) {
        return securityApiFacade.isGranted(path, functionType);
    }
}
