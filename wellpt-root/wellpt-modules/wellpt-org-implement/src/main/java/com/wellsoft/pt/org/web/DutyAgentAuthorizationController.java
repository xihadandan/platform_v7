/*
 * @(#)2015-4-3 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.org.entity.DutyAgent;
import com.wellsoft.pt.org.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 2015-4-3.1	zhulh		2015-4-3		Create
 * </pre>
 * @date 2015-4-3
 */
@Controller
@RequestMapping(value = "/org/duty/agent/authorization")
public class DutyAgentAuthorizationController extends BaseController {
    @Autowired
    private UnitService unitService;

    /**
     * 打开职务代理授权列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String dutyAgent(Model model) {
        model.addAttribute("dutyAgentAuthorization", new DutyAgent());
        model.addAttribute("businessTypes", unitService.getBusinessTypes("BUSINESS_TYPE"));
        return forward("/org/duty_agent_authorization");
    }
}
