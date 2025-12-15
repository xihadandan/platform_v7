/*
 * @(#)2012-10-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.workflow.service.FlowDefinitionUpgradeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Description: 流程定义升级
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年3月1日.1	zhulh		2021年3月1日		Create
 * </pre>
 * @date 2021年3月1日
 */
@Controller
@RequestMapping("/workflow/define/flow/upgrade")
public class FlowDefinitionUpgradeController extends JqGridQueryController {

    @Autowired
    private FlowDefinitionUpgradeService flowDefinitionUpgradeService;

    /**
     * 流程定义升级到平台6.2.12版本
     *
     * @return
     */
    @RequestMapping("/2v6_2_12")
    public @ResponseBody
    ResultMessage upgrade2v6_2_12(@RequestParam(name = "flowDefUuid", required = false) String flowDefUuid) {
        List<String> updatedFlowInfos = flowDefinitionUpgradeService.upgrade2v6_2_12(flowDefUuid);
        return new ResultMessage(StringUtils.join(updatedFlowInfos, ","));
    }

    /**
     * 流程定义升级到平台6.2.9.1版本
     *
     * @return
     */
    @RequestMapping("/2v6_2_9_1")
    public @ResponseBody
    ResultMessage upgrade2v6_2_9_1(@RequestParam(name = "flowDefUuid", required = false) String flowDefUuid) {
        List<String> updatedFlowInfos = flowDefinitionUpgradeService.upgrade2v6_2_9_1(flowDefUuid);
        return new ResultMessage(StringUtils.join(updatedFlowInfos, ","));
    }

    /**
     * 流程定义升级到平台6.2.7版本
     *
     * @return
     */
    @RequestMapping("/2v6_2_7")
    public @ResponseBody
    ResultMessage upgrade2v6_2_7(@RequestParam(name = "flowDefUuid", required = false) String flowDefUuid) {
        List<String> updatedFlowInfos = flowDefinitionUpgradeService.upgrade2v6_2_7(flowDefUuid);
        return new ResultMessage(StringUtils.join(updatedFlowInfos, ","));
    }

    @RequestMapping("/2v6_2_5")
    public @ResponseBody
    ResultMessage upgrade2v6_2_5(@RequestParam(name = "flowDefUuid", required = false) String flowDefUuid) {
        List<String> updatedFlowInfos = flowDefinitionUpgradeService.upgrade2v6_2_5(flowDefUuid);
        return new ResultMessage(StringUtils.join(updatedFlowInfos, ","));
    }

}
