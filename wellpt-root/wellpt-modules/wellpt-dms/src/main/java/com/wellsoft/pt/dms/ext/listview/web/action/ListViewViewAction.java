/*
 * @(#)Feb 20, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.listview.web.action;

import com.wellsoft.pt.app.facade.service.AppContextService;
import com.wellsoft.pt.dms.core.annotation.Action;
import com.wellsoft.pt.dms.core.annotation.ListViewActionConfig;
import com.wellsoft.pt.dms.core.web.action.OpenViewAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

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
 * Feb 20, 2017.1	zhulh		Feb 20, 2017		Create
 * </pre>
 * @date Feb 20, 2017
 */
@Action("视图列表")
public class ListViewViewAction extends OpenViewAction {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5230478380296586020L;

    @Autowired
    private AppContextService appContextService;

    @ListViewActionConfig(name = "查看", id = ListViewActions.ACTION_VIEW, executeJsModule = "DmsListViewViewAction")
    public String gotoView(String pkValue, HttpServletRequest request, HttpServletResponse response, Model model) {
        return open(pkValue, request, response, model);
    }

}
