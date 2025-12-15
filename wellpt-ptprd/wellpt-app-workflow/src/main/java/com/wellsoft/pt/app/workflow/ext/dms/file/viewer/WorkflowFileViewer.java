/*
 * @(#)2018年9月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.ext.dms.file.viewer;

import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.dms.file.view.FileViewer;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * 2018年9月26日.1	zhulh		2018年9月26日		Create
 * </pre>
 * @date 2018年9月26日
 */
@Component
public class WorkflowFileViewer implements FileViewer {

    public final static String WORKFLOW_FILE_VIEWER_PREFIX = "/workflow/file/viewer";
    private static String VIEW_ARCHIVE_URL = WORKFLOW_FILE_VIEWER_PREFIX + "/view/archive";

    @Autowired
    private FlowService flowService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.view.FileViewer#matches(java.lang.String)
     */
    @Override
    public boolean matches(String contentType) {
        return WorkflowMediaType.APPLICATION_WORKFLOW_VALUE.equals(contentType);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.view.FileViewer#getViewUrl(com.wellsoft.pt.dms.model.DmsFile, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String getViewUrl(DmsFile dmsFile, HttpServletRequest request, HttpServletResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getContextPath());
        sb.append(VIEW_ARCHIVE_URL);
        sb.append("?flowInstUuid=" + dmsFile.getDataUuid());
        sb.append("&fileUuid=" + dmsFile.getUuid());
        sb.append("&folderUuid=" + dmsFile.getFolderUuid());
        return sb.toString();
    }

    @Override
    public DyFormData getDyformData(DmsFile dmsFile) {
        String flowInstUuid = dmsFile.getDataUuid();
        FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
        if (flowInstance == null) {
            return null;
        }

        return dyFormFacade.getDyFormData(flowInstance.getFormUuid(), flowInstance.getDataUuid());
    }

}
