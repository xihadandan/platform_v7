/*
 * @(#)Feb 1, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.filemanager.service.impl;

import com.wellsoft.pt.dms.ext.filemanager.service.FileManagerListViewActionService;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dms.model.DmsFileDyformListViewRowData;
import com.wellsoft.pt.dms.support.FileHelper;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Service
@Transactional
public class FileManagerListViewActionServiceImpl extends BaseServiceImpl implements FileManagerListViewActionService {

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.filemanager.service.FileManagerListViewActionService#logicDeleteByFolderUuidAndDataUuid(java.util.List)
     */
    @Override
    public void logicDeleteByFolderUuidAndDataUuid(List<DmsFileDyformListViewRowData> listViewRowDatas) {
        for (DmsFileDyformListViewRowData listViewData : listViewRowDatas) {
            dmsFileServiceFacade
                    .deleteFileByFolderUuidAndDataUuid(listViewData.getFolderUuid(), listViewData.getUuid());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.filemanager.service.FileManagerListViewActionService#restoreByFolderUuidAndDataUuid(java.util.List)
     */
    @Override
    public void restoreByFolderUuidAndDataUuid(List<DmsFileDyformListViewRowData> listViewRowDatas) {
        for (DmsFileDyformListViewRowData rowData : listViewRowDatas) {
            dmsFileServiceFacade.restoreFileByFolderUuidAndDataUuid(rowData.getFolderUuid(), rowData.getUuid());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.filemanager.service.FileManagerListViewActionService#physicalDeleteByFolderUuidAndDataUuid(java.util.List)
     */
    @Override
    public void physicalDeleteByFolderUuidAndDataUuid(List<DmsFileDyformListViewRowData> listViewRowDatas) {
        for (DmsFileDyformListViewRowData rowData : listViewRowDatas) {
            dmsFileServiceFacade.physicalDeleteFileByFolderUuidAndDataUuid(rowData.getFolderUuid(), rowData.getUuid(),
                    rowData.getFormUuid());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.filemanager.service.FileManagerListViewActionService#logicalDelete(java.util.List)
     */
    @Override
    public void logicalDelete(List<DmsFile> dmsFiles) {
        for (DmsFile dmsFile : dmsFiles) {
            if (FileHelper.isFolder(dmsFile)) {
                dmsFileServiceFacade.deleteFolder(dmsFile.getUuid());
            } else {
                dmsFileServiceFacade.deleteFile(dmsFile.getUuid());
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.filemanager.service.FileManagerListViewActionService#restore(java.util.List)
     */
    @Override
    public void restore(List<DmsFile> dmsFiles) {
        for (DmsFile dmsFile : dmsFiles) {
            if (FileHelper.isFolder(dmsFile)) {
                dmsFileServiceFacade.restoreFolder(dmsFile.getUuid());
            } else {
                dmsFileServiceFacade.restoreFile(dmsFile.getUuid());
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.ext.filemanager.service.FileManagerListViewActionService#physicalDelete(java.util.List)
     */
    @Override
    public void physicalDelete(List<DmsFile> dmsFiles) {
        for (DmsFile dmsFile : dmsFiles) {
            if (FileHelper.isFolder(dmsFile)) {
                dmsFileServiceFacade.physicalDeleteFolder(dmsFile.getUuid());
            } else {
                dmsFileServiceFacade.physicalDeleteFile(dmsFile.getUuid());
            }
        }
    }

}
