/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;
import com.wellsoft.pt.webmail.facade.service.WmWebmailService;
import com.wellsoft.pt.webmail.service.WmMailConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: 邮件配置管理器控制器
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	t		2016-06-03		Create
 * </pre>
 * @date 2016-06-03
 */
@Controller
@RequestMapping(value = "/webmail/wm/mail/config")
public class WmMailConfigMgrController extends BaseController {

    @Autowired
    private WmMailConfigService wmMailConfigService;

    @Autowired
    private WmWebmailService wmWebmailService;

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String wmMailConfig(Model model) {
        WmMailConfigEntity wmMailConfig = wmMailConfigService.getBySystemUnitId(
                SpringSecurityUtils.getCurrentUserUnitId());
        if (wmMailConfig == null) {
            wmMailConfig = new WmMailConfigEntity();
        }
        model.addAttribute("mailConfig", wmMailConfig);
        return forward("/webmail/wm_mail_config");
    }

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String wmMailConfigList() {
        return forward("/webmail/wm_mail_config_list");
    }

}
