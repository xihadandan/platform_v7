/*
 * @(#)2014-10-17 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.org.bean.DutyAgentBean;
import com.wellsoft.pt.org.service.DutyAgentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-17.1	wubin		2014-10-17		Create
 * </pre>
 * @date 2014-10-17
 */
@Controller
@RequestMapping("/org/entrust")
public class OrgEntrustController extends BaseController {

    @Autowired
    private DutyAgentService dutyAgentService;

    /**
     * 新建、编辑职务代理人信息
     *
     * @param model
     * @param uuid
     * @return
     */
    @RequestMapping(value = "/getPage")
    public String getOrgEntrustPage(Model model, @RequestParam(value = "uuid", required = false) String uuid) {
        if (StringUtils.isNotBlank(uuid)) {
            model.addAttribute("uuid", uuid);
        }
        return forward("/basicdata/view/view_org_entrust");
    }

    /**
     * 查看职务代理人信息
     *
     * @param model
     * @param uuid
     * @return
     */
    @RequestMapping(value = "/view/{uuid}")
    public String view(Model model, @PathVariable(value = "uuid") String uuid) {
        model.addAttribute("uuid", uuid);
        // 征求意见
        model.addAttribute("duty_agent_view", true);
        DutyAgentBean dutyAgentBean = dutyAgentService.getBean(uuid);
        model.addAttribute("dutyAgent", dutyAgentBean);
        return forward("/basicdata/view/view_org_entrust");
    }

    /**
     * 查看职务代理人信息
     *
     * @param model
     * @param uuid
     * @return
     */
    @RequestMapping(value = "/consult/{uuid}")
    public String consult(Model model, @PathVariable(value = "uuid") String uuid) {
        model.addAttribute("uuid", uuid);
        // 征求意见
        model.addAttribute("duty_agent_consult", true);
        DutyAgentBean dutyAgentBean = dutyAgentService.getBean(uuid);
        model.addAttribute("dutyAgent", dutyAgentBean);
        return forward("/basicdata/view/view_org_entrust");
    }
}
