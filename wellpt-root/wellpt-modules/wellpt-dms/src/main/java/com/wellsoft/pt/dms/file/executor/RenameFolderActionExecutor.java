/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.RenameFolderAction;
import com.wellsoft.pt.dms.file.executor.RenameFolderActionExecutor.RenameFolderActionParam;
import com.wellsoft.pt.dms.file.executor.RenameFolderActionExecutor.RenameFolderActionResult;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 3, 2018.1	zhulh		Jan 3, 2018		Create
 * </pre>
 * @date Jan 3, 2018
 */
@Component
public class RenameFolderActionExecutor extends
        AbstractFileActionExecutor<RenameFolderActionParam, RenameFolderActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public RenameFolderActionResult execute(RenameFolderActionParam param) {
        String folderUuid = param.getFolderUuid();
        String newFolderName = param.getNewFolderName();
        this.dmsFileActionService.renameFolder(folderUuid, newFolderName);
        return new RenameFolderActionResult();
    }

    public static class RenameFolderActionParam extends FileActionParam<RenameFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 2131137927763514923L;

        private String folderUuid;

        private String newFolderName;

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
         * @return the newFolderName
         */
        public String getNewFolderName() {
            return newFolderName;
        }

        /**
         * @param newFolderName 要设置的newFolderName
         */
        public void setNewFolderName(String newFolderName) {
            this.newFolderName = newFolderName;
        }

    }

    public static class RenameFolderActionResult extends FileActionResult<RenameFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1591318241366773734L;

    }

}
