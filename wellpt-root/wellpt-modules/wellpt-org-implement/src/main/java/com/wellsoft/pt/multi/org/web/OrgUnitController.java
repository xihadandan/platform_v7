/*
 * @(#)2015-10-10 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/multi/org/unit")
public class OrgUnitController extends BaseController {

    @Autowired
    private MultiOrgService multiOrgService;

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String list(Model model) {
        return "/multi/org/org_unit_list";
    }

    /**
     * 打开管理员列表界面
     *
     * @return
     */
    @RequestMapping(value = "/admin/list")
    public String adminList(Model model) {
        return "/multi/org/org_unit_admin_list";
    }

}
