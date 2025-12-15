/*
 * @(#)2013-3-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.web;

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
 * 2013-3-30.1	zhulh		2013-3-30		Create
 * </pre>
 * @date 2013-3-30
 */
@Controller
@RequestMapping("/workflow/work/unread")
public class WorkUnreadController extends BaseController {
    @RequestMapping()
    public String openView() {
        return forward("/workflow/work/unread_list");
    }
}
