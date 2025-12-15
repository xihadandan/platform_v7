/*
 * @(#)2015-10-10 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.org.bean.OrganizationBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
 * 2013-1-14.1	zhulh		2015-10-10		Create
 * </pre>
 * @date 2015-10-10
 */
@Controller
@RequestMapping(value = "/org/organization")
public class OrganizationMgrController extends BaseController {

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String orgTypeList(Model model) {
        model.addAttribute("organization", new OrganizationBean());
        return forward("/org/organization_list");
    }

}
