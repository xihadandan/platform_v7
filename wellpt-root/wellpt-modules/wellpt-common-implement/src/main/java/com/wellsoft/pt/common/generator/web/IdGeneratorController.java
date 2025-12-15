/*
 * @(#)2016年2月2日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.generator.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年2月2日.1	zhulh		2016年2月2日		Create
 * </pre>
 * @date 2016年2月2日
 */
@Controller
@RequestMapping(value = "/common/id/generator")
public class IdGeneratorController extends BaseController {
    @Autowired
    private IdGeneratorService idGeneratorService;

    /**
     * @param beanName
     * @return
     */
    @RequestMapping(value = "/generate/sysdate")
    @ResponseBody
    public String generateBySysdate() {
        return idGeneratorService.getBySysDate();
    }

}
