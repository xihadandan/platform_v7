/*
 * @(#)2016年10月20日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.web;

import com.wellsoft.context.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月20日.1	xiem		2016年10月20日		Create
 * </pre>
 * @date 2016年10月20日
 */
@Controller
@RequestMapping("/basicdata/datastore")
public class CdDataStoreDefinitionController extends BaseController {
    /**
     * 跳转到数据库定义界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String dataStoreDefinition() {
        return forward("/basicdata/datastore/datastore_definition");
    }

}
