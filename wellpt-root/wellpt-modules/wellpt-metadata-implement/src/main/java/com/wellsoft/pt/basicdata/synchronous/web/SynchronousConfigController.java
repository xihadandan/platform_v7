/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.synchronous.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.synchronous.service.SynchronousConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-7.1	ruanhg		2014-7-7		Create
 * </pre>
 * @date 2014-7-7
 */
@Controller
@RequestMapping("/synchronous/config")
public class SynchronousConfigController extends BaseController {

    @Autowired
    private SynchronousConfigService synchronousConfigService;

    @RequestMapping(value = "/sysTables")
    public String sysTables() {
        synchronousConfigService.getSysTables();
        return "sysTables";
    }

}