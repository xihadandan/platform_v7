/*
 * @(#)2015-6-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.cg.core.ControlCenter;
import com.wellsoft.pt.cg.entity.CodeGeneratorConfig;
import com.wellsoft.pt.cg.service.CodeGeneratorConfigService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-18.1	zhulh		2015-6-18		Create
 * </pre>
 * @date 2015-6-18
 */
@Controller
@RequestMapping("/cg/code/generator")
public class CodeGeneratorController extends BaseController {
    @Autowired
    private CodeGeneratorConfigService codeGeneratorConfigService;

    /**
     * @return
     */
    @RequestMapping(value = "")
    public String config() {
        return forward("/cg/code_generator");
    }

    @RequestMapping("/generate")
    @ResponseBody
    public String generate(@RequestParam("uuid") String configUuid) {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            CodeGeneratorConfig config = codeGeneratorConfigService.get(configUuid);
            ControlCenter.generate(config);
            map.put("code", 1);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            map.put("code", 0);
            map.put("error", (e.getMessage() != null ? e.getMessage() : "失败"));
        }
        return JSONObject.fromObject(map).toString();
    }

}
