/*
 * @(#)2013-4-15 V1.0
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
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-15.1	rzhu		2013-4-15		Create
 * </pre>
 * @date 2013-4-15
 */
@Controller
@RequestMapping("/workflow/work/attention")
public class WorkAttentionController extends BaseController {

    @RequestMapping()
    public String openView() {
        return forward("/workflow/work/attention_list");
    }

}
