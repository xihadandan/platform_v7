/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.web;

import com.wellsoft.pt.common.fdext.facade.service.CdFieldFacade;
import com.wellsoft.pt.common.fdext.support.ICdFieldRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
@Controller
@RequestMapping(value = "/cd/field/ext/definition")
public class CdFieldExtDefinitionMgrController extends com.wellsoft.context.web.controller.BaseController {

    @Autowired
    private CdFieldFacade defineFacade;

    /**
     * 打开列表界面
     *
     * @return
     */
    @RequestMapping(value = "/list")
    public String cdFieldExtDefinitionList() {
        return forward("/fdext/cd_field_ext_definition_list");
    }

    /**
     * 展示拓展字段表单
     *
     * @param tenantId
     * @param groupCode
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/view", produces = "text/plain")
    public String cdFieldView(@RequestParam("tenantId") String tenantId, @RequestParam("groupCode") String groupCode,
                              HttpServletRequest request, HttpServletResponse response, Model model) {
        List<ICdFieldRender> fieldDefines = defineFacade.getFields(tenantId, groupCode);
        request.setAttribute("fieldDefines", fieldDefines);
        request.setAttribute("message", fieldDefines.size());
        return forward("/fdext/cd_field_ext_definition");
    }
}
