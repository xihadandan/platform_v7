/*
 * @(#)2021年3月1日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.web;

import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionUpgradeService;
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
 * 2021年3月1日.1	zhulh		2021年3月1日		Create
 * </pre>
 * @date 2021年3月1日
 */
@Controller
@RequestMapping("/pt/dyform/definition/upgrade")
public class FormDefinitionUpgradeController {

    @Autowired
    private FormDefinitionUpgradeService formDefinitionUpgradeService;

    /**
     * 表单定义升级到平台6.2.3版本
     *
     * @return
     */
    @RequestMapping("/2v6_2_5")
    public @ResponseBody
    ResultMessage upgrade2v6_2_5(@RequestParam(name = "formUuid", required = false) String formUuid) {
        List<String> updatedFormInfos = formDefinitionUpgradeService.upgrade2v6_2_5(formUuid);
        return new ResultMessage(StringUtils.join(updatedFormInfos, ","));
    }


    @RequestMapping("/2v6_2_3_repair_json")
    public @ResponseBody
    ResultMessage upgrade_v6_2_3_repair_json(@RequestParam(name = "formUuid", required = false) String formUuid) {
        List<String> updatedFormInfos = formDefinitionUpgradeService.upgrade_v6_2_3_repair_json(formUuid);
        return new ResultMessage(StringUtils.join(updatedFormInfos, ","));
    }

    @RequestMapping("/2v6_2_5_repair_readonly_style")
    public @ResponseBody
    ResultMessage upgrade_v6_2_5_repair_readonly_style(@RequestParam(name = "formUuid", required = false) String formUuid) {
        List<String> updatedFormInfos = formDefinitionUpgradeService.upgrade_v6_2_5_repair_readonly_style(formUuid);
        return new ResultMessage(StringUtils.join(updatedFormInfos, ","));
    }
}
