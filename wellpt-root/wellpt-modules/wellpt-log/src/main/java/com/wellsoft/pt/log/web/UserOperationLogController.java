/*
 * @(#)2013-10-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.web;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-14.1	zhulh		2013-10-14		Create
 * </pre>
 * @date 2013-10-14
 */
@Controller
@RequestMapping("/log/user/operation")
public class UserOperationLogController extends BaseController {

    /**
     * 打开用户操作日志页面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String userOperationLog() {
        return forward("/log/user_operation_log");
    }

}
