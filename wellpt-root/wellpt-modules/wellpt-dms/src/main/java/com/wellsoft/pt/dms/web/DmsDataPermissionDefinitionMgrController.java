/*
 * @(#)2019年9月30日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
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
 * 2019年9月30日.1	zhulh		2019年9月30日		Create
 * </pre>
 * @date 2019年9月30日
 */
@Controller
@RequestMapping(value = "/dms/data/permission/definition")
public class DmsDataPermissionDefinitionMgrController extends BaseController {

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String dmsDataPermissionDefinitionList() {
        return forward("/dms/dms_data_permission_definition_list");
    }

    /**
     * 打开数据范围列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list/range")
    public String dmsDataPermissionRangeList() {
        return forward("/dms/dms_data_permission_range_list");
    }

}
