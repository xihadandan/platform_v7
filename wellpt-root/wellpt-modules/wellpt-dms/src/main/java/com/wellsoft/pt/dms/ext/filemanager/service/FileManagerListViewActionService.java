/*
 * @(#)Feb 1, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.filemanager.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dms.model.DmsFileDyformListViewRowData;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 1, 2018.1	zhulh		Feb 1, 2018		Create
 * </pre>
 * @date Feb 1, 2018
 */
public interface FileManagerListViewActionService extends BaseService {

    /**
     * 如何描述该方法
     *
     * @param listViewRowDatas
     */
    void logicDeleteByFolderUuidAndDataUuid(List<DmsFileDyformListViewRowData> listViewRowDatas);

    /**
     * 如何描述该方法
     *
     * @param listViewRowDatas
     */
    void restoreByFolderUuidAndDataUuid(List<DmsFileDyformListViewRowData> listViewRowDatas);

    /**
     * 如何描述该方法
     *
     * @param listViewRowDatas
     */
    void physicalDeleteByFolderUuidAndDataUuid(List<DmsFileDyformListViewRowData> listViewRowDatas);

    /**
     * 如何描述该方法
     *
     * @param dmsFiles
     */
    void logicalDelete(List<DmsFile> dmsFiles);

    /**
     * 如何描述该方法
     *
     * @param dmsFiles
     */
    void restore(List<DmsFile> dmsFiles);

    /**
     * 如何描述该方法
     *
     * @param dmsFiles
     */
    void physicalDelete(List<DmsFile> dmsFiles);

}
