/*
 * @(#)2015-5-27 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.security.service.KeyStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-5-27.1	zhulh		2015-5-27		Create
 * </pre>
 * @date 2015-5-27
 */
@Controller
@RequestMapping(value = "/security/keystroe")
public class KeyStoreManagerController extends BaseController {

    @Autowired
    private KeyStoreService keyStoreService;

    @RequestMapping(value = "/index")
    public String index(Model model) {
        return forward("/pt/keymgr/keystore-mgr");
    }

    /**
     * 添加密钥库
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/add-keystore", method = RequestMethod.POST)
    @ResponseBody
    public ResultMessage addKeyStore(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam(value = "username", required = true) String username) {
        ResultMessage resultMessage = new ResultMessage("", false);
        return resultMessage;
    }

    /**
     * 打包下载公钥(匿名访问)
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public ResultMessage download(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "keyStoreName", required = true) String keyStoreName,
            @RequestParam(value = "alias", required = true) String alias) {
        ResultMessage resultMessage = new ResultMessage("", false);
        return resultMessage;
    }

}
