/*
 * @(#)2019年5月14日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.license.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.license.service.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
 * 2019年5月14日.1	zhulh		2019年5月14日		Create
 * </pre>
 * @date 2019年5月14日
 */
@Controller
@RequestMapping(value = "/api/security/license")
public class LicenseController extends BaseController {

    @Autowired
    private LicenseService licenseService;

    @GetMapping(value = "/info")
    @ResponseBody
    public ApiResult<Map<String, Object>> info()
            throws Exception {
        return ApiResult.success(licenseService.getLicenseInfo());
    }

}
