/*
 * @(#)2015-10-10 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.web;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zyguo		2015-10-10		Create
 * </pre>
 * @date 2015-10-10
 */
@Controller
@RequestMapping(value = "/multi/org/job/rank")
public class OrgJobRankController extends BaseController {

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String list(Model model) {
        return "/multi/org/org_job_rank_list";
    }

}
