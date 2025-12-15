/*
 * @(#)2012-10-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.web;

import com.wellsoft.context.component.jqgrid.JqGridQueryController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: GroupController.java
 *
 * @author zhulh
 * @date 2013-1-13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-13.1	zhulh		2013-1-13		Create
 * </pre>
 */
@Controller
@RequestMapping(value = "/multi/org/group")
public class OrgGroupController extends JqGridQueryController {

    /**
     * 打开群组列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String list() {
        return "/multi/org/org_group";
    }

}
