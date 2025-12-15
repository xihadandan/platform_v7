/*
 * @(#)2014-10-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

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
 * 2014-10-24.1	zhulh		2014-10-24		Create
 * </pre>
 * @date 2014-10-24
 */
@Controller
@RequestMapping("/workflow/define/develop")
public class FlowDevelopController extends BaseController {
    /**
     * 跳到流程定义界面
     *
     * @return
     */
    @RequestMapping("")
    public String flowDefine() {
        return forward("/workflow/define/develop");
    }
}
