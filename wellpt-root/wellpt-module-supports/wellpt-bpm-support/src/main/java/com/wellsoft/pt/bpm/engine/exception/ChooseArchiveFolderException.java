/*
 * @(#)2018年10月11日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;

import java.util.HashMap;
import java.util.List;
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
 * 2018年10月11日.1	zhulh		2018年10月11日		Create
 * </pre>
 * @date 2018年10月11日
 */
public class ChooseArchiveFolderException extends WorkFlowException {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -125506884590952333L;

    private String taskName;

    private String taskId;

    private List<String> rootFolderUuids;

    /**
     * @param taskName
     * @param taskId
     * @param rootFolderUuids
     */
    public ChooseArchiveFolderException(String taskName, String taskId, List<String> rootFolderUuids) {
        this.taskName = taskName;
        this.taskId = taskId;
        this.rootFolderUuids = rootFolderUuids;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.exception.WorkFlowException#getData()
     */
    @Override
    public Object getData() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("taskName", taskName);
        variables.put("taskId", taskId);
        variables.put("rootFolderUuids", rootFolderUuids);
        return variables;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.exception.WorkFlowException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.ChooseArchiveFolder;
    }

}
