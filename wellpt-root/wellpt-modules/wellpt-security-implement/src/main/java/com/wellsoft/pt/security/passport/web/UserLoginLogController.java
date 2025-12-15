/*
 * @(#)2012-12-19 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.org.service.UserLoginLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Description: 用户登录日记
 *
 * @author liuzq
 * @date 2013-10-13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-13	liuzq		2013-10-13		Create
 * </pre>
 */
@Api(tags = "用户登录日记")
@RestController
@RequestMapping("/passport/user/loginlog")
public class UserLoginLogController extends BaseController {

    @Autowired
    private UserLoginLogService userLoginLogService;

    @ApiOperation(value = "跳转用户登录日志", notes = "跳转用户登录日志", tags = {"日志管理-->用户登录日志"})
    @GetMapping("")
    public String userLoginLog() {
        return forward("/log/user_login_log");
    }

    @ApiOperation(value = "用户登录日记列表", notes = "用户登录日记列表", tags = {"日志管理-->用户登录日志"})
    @GetMapping("/list")
    public @ResponseBody
    JqGridQueryData listAsJson(@RequestParam(value = "userName", required = false) String userName,
                               JqGridQueryInfo queryInfo) {
        return userLoginLogService.query(userName, queryInfo);
    }
}
