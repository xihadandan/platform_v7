/*
 * @(#)Aug 4, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.app.workflow.dto.FlowDelegationSettingsDto;
import com.wellsoft.pt.app.workflow.facade.service.WorkflowDelegationSettiongsService;
import com.wellsoft.pt.bpm.engine.entity.FlowDelegationSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Description: 工作委托设置
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Aug 4, 2017.1	zhulh		Aug 4, 2017		Create
 * </pre>
 * @date Aug 4, 2017
 */
@Controller
@RequestMapping("/workflow/delegation/settings")
public class WorkflowDelegationSettiongsController extends BaseController {

    @Autowired
    private WorkflowDelegationSettiongsService workflowDelegationSettiongsService;

    /**
     * @param uuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add(Model model) {
        return forward("/ptprd/app/workflow/wf_delegation_settings");
    }

    /**
     * @param uuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(@RequestParam(value = "uuid", required = true) String uuid, Model model) {
        model.addAttribute("uuid", uuid);
        return forward("/ptprd/app/workflow/wf_delegation_settings");
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
        // 查看
        model.addAttribute("duty_agent_view", true);
        FlowDelegationSettingsDto delegationSettingsDto = workflowDelegationSettiongsService.getBean(uuid);
        model.addAttribute("dutyAgent", delegationSettingsDto);
        return forward("/ptprd/app/workflow/wf_delegation_settings");
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
        FlowDelegationSettingsDto delegationSettingsDto = workflowDelegationSettiongsService.getBean(uuid);
        if (FlowDelegationSettings.STATUS_CONSULT.equals(delegationSettingsDto.getStatus())) {
            model.addAttribute("duty_agent_consult", true);
        } else {
            model.addAttribute("duty_agent_view", true);
        }
        model.addAttribute("dutyAgent", delegationSettingsDto);
        return forward("/ptprd/app/workflow/wf_delegation_settings");
    }

}
