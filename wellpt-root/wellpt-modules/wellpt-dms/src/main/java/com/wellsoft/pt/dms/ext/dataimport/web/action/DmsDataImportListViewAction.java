/*
 * @(#)2018年6月28日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.dataimport.web.action;

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
 * @author zhongwd
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月28日.1	zhongwd		2018年6月28日		Create
 * </pre>
 * @date 2018年6月28日
 */
@Action("数据导入")
public class DmsDataImportListViewAction extends OpenViewAction {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    @Autowired
    private AppContextService appContextService;

    @ListViewActionConfig(name = "导入", id = DmsDataImportActions.ACTION_LIST_IMPORT_DATA, executeJsModule = "DmsDataImportListViewAction")
    public String importInventory(String pkValue, HttpServletRequest request, HttpServletResponse response, Model model) {
        return open(pkValue, request, response, model);
    }
}
