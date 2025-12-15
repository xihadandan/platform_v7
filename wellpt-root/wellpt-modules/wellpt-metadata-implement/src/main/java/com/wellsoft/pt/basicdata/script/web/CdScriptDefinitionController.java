/*
 * @(#)2018年9月25日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.script.web;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
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
 * 2018年9月25日.1	zhulh		2018年9月25日		Create
 * </pre>
 * @date 2018年9月25日
 */

@Controller
@RequestMapping("/basicdata/script/definition")
public class CdScriptDefinitionController extends BaseController {

    /**
     * 跳转到脚本定义界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String scriptDefinition() {
        return forward("/basicdata/script/cd_script_definition_list");
    }

}
