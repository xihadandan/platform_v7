/*
 * @(#)Feb 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.view;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.lang.StringUtils;
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
 * Feb 15, 2017.1	zhulh		Feb 15, 2017		Create
 * </pre>
 * @date Feb 15, 2017
 */
public class DyFormView extends TableView {

    private String formUuid;

    private String dataUuid;

    public DyFormView() {

    }


    /**
     * @param formUuid
     * @param dataUuid
     */
    public DyFormView(String formUuid, String dataUuid) {
        this.formUuid = formUuid;
        this.dataUuid = dataUuid;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.view.AbstractView#loadActionData(org.springframework.ui.Model, com.wellsoft.pt.dms.core.context.ActionContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void loadActionData(Model model, ActionContext actionContext, HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        model.addAttribute("formUuid", this.formUuid);
        model.addAttribute("dataUuid", this.dataUuid);
        if (StringUtils.isNotBlank(this.formUuid) && StringUtils.isNotBlank(this.dataUuid)) {
            actionContext.setDyformData(ApplicationContextHolder.getBean(DyFormFacade.class).getFormDataOfMainform(this.formUuid, this.dataUuid));
        }
        // 单据二开模块
        model.addAttribute("documentViewModule", "DmsDyformDocumentView");
        model.addAttribute("target", request.getParameter("target"));
        model.addAttribute("acId", request.getParameter("ac_id"));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.view.AbstractView#getViewName()
     */
    @Override
    public String getViewName() {
        return "/dms/document/dyform_data_view";
    }

}
