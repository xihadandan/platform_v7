/*
 * @(#)Feb 20, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.docexchanger.web.action;

import com.wellsoft.pt.app.facade.service.AppContextService;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.web.action.OpenViewAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 文档交换_新增ACTION
 *
 * @author chenq
 * @date
 */
@Action("文档交换")
public class DocExchangerAddAction extends OpenViewAction {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5230478380296586020L;

    @Autowired
    private AppContextService appContextService;


    @ListViewActionConfig(name = "新增", id = DocExchangerActions.ACTION_NEW_DOC_EXCHANGER, executeJsModule = "DmsDocExchangerAddAction")
    public String add(String pkValue, HttpServletRequest request, HttpServletResponse response,
                      Model model) {
        return open(pkValue, request, response, model);
    }

}
