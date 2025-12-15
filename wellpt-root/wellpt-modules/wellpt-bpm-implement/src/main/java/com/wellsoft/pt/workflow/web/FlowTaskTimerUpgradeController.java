/*
 * @(#)2021年6月9日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.workflow.service.FlowTaskTimerUpgradeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年6月9日.1	zhulh		2021年6月9日		Create
 * </pre>
 * @date 2021年6月9日
 */
@Controller
@RequestMapping("/workflow/task/timer/upgrade")
public class FlowTaskTimerUpgradeController extends BaseController {

    @Autowired
    private FlowTaskTimerUpgradeService flowTaskTimerUpgradeService;

    /**
     * 流程计时器升级到平台6.2.3版本
     *
     * @return
     */
    @RequestMapping("/2v6_2_7")
    public @ResponseBody
    ResultMessage upgrade2v6_2_7(@RequestParam(name = "flowDefUuid", required = false) String flowDefUuid) {
        List<String> updatedTaskTimerInfos = flowTaskTimerUpgradeService.upgrade2v6_2_7(flowDefUuid);
        return new ResultMessage(StringUtils.join(updatedTaskTimerInfos, ","));
    }

}
