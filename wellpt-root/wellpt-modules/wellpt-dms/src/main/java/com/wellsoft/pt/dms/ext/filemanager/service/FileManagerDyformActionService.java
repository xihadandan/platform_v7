/*
 * @(#)Jan 23, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.filemanager.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.core.context.ActionContext;
import com.wellsoft.pt.dms.core.support.FileManagerDyFormActionData;
import com.wellsoft.pt.dms.model.DmsFile;

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
public interface FileManagerDyformActionService extends BaseService {

    /**
     * 如何描述该方法
     *
     * @param dyFormActionData
     * @param actionContext
     * @return
     */
    DmsFile saveAsDraft(FileManagerDyFormActionData dyFormActionData, ActionContext actionContext);

    /**
     * 如何描述该方法
     *
     * @param dyFormActionData
     * @param actionContext
     * @return
     */
    DmsFile saveAsNormal(FileManagerDyFormActionData dyFormActionData, ActionContext actionContext);

    /**
     * 如何描述该方法
     *
     * @param dyFormActionData
     */
    void delete(FileManagerDyFormActionData dyFormActionData);

    /**
     * 如何描述该方法
     *
     * @param dyFormActionData
     */
    void restore(FileManagerDyFormActionData dyFormActionData);

    /**
     * 如何描述该方法
     *
     * @param dyFormActionData
     */
    void physicalDelete(FileManagerDyFormActionData dyFormActionData);

}
