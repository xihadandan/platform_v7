/*
 * @(#)2017-12-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.web;

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
 * 2013-1-14.1	zhulh		2017-12-21		Create
 * </pre>
 * @date 2017-12-21
 */
@Controller
@RequestMapping(value = "/dms/dms/folder/configuration")
public class DmsFolderConfigurationMgrController extends BaseController {

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String dmsFolderConfigurationList() {
        return forward("/dms/dms_folder_configuration_list");
    }

}
