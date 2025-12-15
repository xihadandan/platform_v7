/*
 * @(#)Jan 23, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.web.view;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.service.DmsFileService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 23, 2018.1	zhulh		Jan 23, 2018		Create
 * </pre>
 * @date Jan 23, 2018
 */
public class FileManagerDyFormView extends DyFormView {

    private String folderUuid;

    private String fileUuid;

    /**
     * @param dataUuid
     */
    public FileManagerDyFormView(String folderUuid, String fileUuid, String formUuid, String dataUuid) {
        super(formUuid, dataUuid);
        this.folderUuid = folderUuid;
        this.fileUuid = fileUuid;
    }

    /**
     * @return the folderUuid
     */
    public String getFolderUuid() {
        return folderUuid;
    }

    /**
     * @param folderUuid 要设置的folderUuid
     */
    public void setFolderUuid(String folderUuid) {
        this.folderUuid = folderUuid;
    }

    /**
     * @return the fileUuid
     */
    public String getFileUuid() {
        return fileUuid;
    }

    /**
     * @param fileUuid 要设置的fileUuid
     */
    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.web.view.AbstractView#loadActionData(org.springframework.ui.Model, com.wellsoft.pt.dms.core.context.ActionContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void loadActionData(Model model, ActionContext actionContext, HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        String fileFolderUuid = folderUuid;
        String fileFormUuid = getFormUuid();
        String fileDataUuid = getDataUuid();
        DmsFileService dmsFileService = ApplicationContextHolder.getBean(DmsFileService.class);
        DyFormFacade dyFormApiFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        if (StringUtils.isNotBlank(fileUuid)) {
            DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
            fileFolderUuid = dmsFileEntity.getFolderUuid();
            fileFormUuid = dmsFileEntity.getDataDefUuid();
            fileDataUuid = dmsFileEntity.getDataUuid();
            // 文件管理的默认标题
            model.addAttribute("title", dmsFileEntity.getFileName());
        } else if (StringUtils.isNotBlank(fileFolderUuid) && StringUtils.isNotBlank(fileDataUuid)) {
            DmsFileEntity dmsFileEntity = dmsFileService.getByFolderUuidAndDataUuid(fileFolderUuid, fileDataUuid);
            // 文件管理的默认标题
            if (dmsFileEntity != null) {
                model.addAttribute("title", dmsFileEntity.getFileName());
            }
        }

        // 当前打开的文件其所在夹可能与归属夹不是同一个
        Map<String, Object> extraParams = getExtraParams(model);
        extraParams.put("dms_belongToFolderUuid", folderUuid);
        extraParams.put("dms_folderUuid", fileFolderUuid);
        extraParams.put("dms_fileUuid", fileUuid);

        model.addAttribute("formUuid", fileFormUuid);
        model.addAttribute("dataUuid", fileDataUuid);
        // 单据数据
        model.addAttribute("documentData", dyFormApiFacade.getDyFormData(fileFormUuid, fileDataUuid));
        // 单据二开模块
        model.addAttribute("documentViewModule", "DmsFileManagerDyformDocumentView");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getExtraParams(Model model) {
        Map<String, Object> extraParams = new HashMap<String, Object>(0);
        if (model.containsAttribute("extraParams")) {
            extraParams = (Map<String, Object>) model.asMap().get("extraParams");
        } else {
            model.addAttribute("extraParams", extraParams);
        }
        return extraParams;
    }

}
