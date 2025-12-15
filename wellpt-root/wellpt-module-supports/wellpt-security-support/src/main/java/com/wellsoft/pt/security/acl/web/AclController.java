/*
 * @(#)2013-8-19 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.acl.service.AclConstants;
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
 * 2013-8-19.1	zhulh		2013-8-19		Create
 * </pre>
 * @date 2013-8-19
 */
@Controller
@RequestMapping("/security/acl")
public class AclController extends BaseController {

    public static final String defaultErrorView = AclConstants.DEFAULT_ERROR_VIEW;

    //@Autowired
    //private ClearDataService clearDataService;

    /**
     * @return the defaulterrorview
     */
    public static String getDefaulterrorview() {
        return defaultErrorView;
    }

    @RequestMapping("error")
    public String error() {
        return forward(defaultErrorView);
    }

    @RequestMapping("/acl/clear/data")
    @ResponseBody
    public String clearData() {
        // try {
        // List<String> classNames = clearDataService.getAllClass();
        // for (String className : classNames) {
        // List<String> ids =
        // clearDataService.getDeletedAclObjectIdentities(className);
        // for (String aclObjectIdentity : ids) {
        // clearDataService.clearAclEntry(className, aclObjectIdentity);
        // clearDataService.clearAclObjectIdentity(className,
        // aclObjectIdentity);
        // }
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        return "success";
    }

    // @RequestMapping("/reset/new/data")
    // @ResponseBody
    // public String resetNewData() {
    // clearDataService.resetNewData();
    // return "success";
    // }

    @RequestMapping("/acl/clear/sid")
    @ResponseBody
    public String clearSid() {
        // clearDataService.clearAclSid();
        return "success";
    }
}
