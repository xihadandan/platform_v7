/*
 * @(#)2013-5-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.web.json;

import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.context.web.controller.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-30.1	zhulh		2013-5-30		Create
 * </pre>
 * @date 2013-5-30
 */
@Controller
@RequestMapping("/common/validate")
public class CommonValidateController extends BaseController {

    @Autowired
    private CommonValidateService commonValidateService;

    @RequestMapping(value = "/check/exists", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String checkExists(HttpServletRequest request) {
        String checkType = request.getParameter("checkType");
        String uuid = request.getParameter("uuid");
        String fieldName = request.getParameter("fieldName");
        String fieldValue = request.getParameter("fieldValue");
        if (StringUtils.isBlank(uuid)) {
            return commonValidateService.checkExists(checkType, fieldName, fieldValue) ? "false" : "true";
        }
        return commonValidateService.checkUnique(uuid, checkType, fieldName, fieldValue) ? "true" : "false";
    }

}
