/*
 * @(#)Jan 31, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.personal.document.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dms.model.DmsFolder;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 31, 2018.1	zhulh		Jan 31, 2018		Create
 * </pre>
 * @date Jan 31, 2018
 */
public interface PersonalDocumentService extends BaseService {

    /**
     * 获取我的文件夹
     *
     * @return
     */
    DmsFolder getMyFolder();

    /**
     * 创建我的文件夹
     *
     * @param myFolderUuid
     * @return
     */
    DmsFolder createMyFolder(String myFolderUuid);

    /**
     * 记录最近访问的文件
     */
    void recordRecentVisitFile(DmsFile dmsFile);

}
