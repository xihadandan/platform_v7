/*
 * @(#)2013-4-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.selective.facade.SelectiveDatas;
import com.wellsoft.pt.org.entity.DutyAgent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: 职务代理人控制器类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-15.1	zhulh		2013-4-15		Create
 * </pre>
 * @date 2013-4-15
 */
@Controller
@RequestMapping(value = "/org/duty/agent")
public class DutyAgentController extends BaseController {

    /**
     * 打开职务代理人列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String dutyAgent(Model model) {
        model.addAttribute("dutyAgent", new DutyAgent());
        model.addAttribute("businessTypes", SelectiveDatas.getItems("BUSINESS_TYPE"));
        return forward("/org/duty_agent");
    }
}
