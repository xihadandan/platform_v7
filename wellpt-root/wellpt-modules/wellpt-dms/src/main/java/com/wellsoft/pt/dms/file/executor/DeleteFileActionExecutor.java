/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.DeleteFileAction;
import com.wellsoft.pt.dms.file.executor.DeleteFileActionExecutor.DeleteFileActionParam;
import com.wellsoft.pt.dms.file.executor.DeleteFileActionExecutor.DeleteFileActionResult;
import org.apache.commons.lang.StringUtils;
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
public class DeleteFileActionExecutor extends AbstractFileActionExecutor<DeleteFileActionParam, DeleteFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public DeleteFileActionResult execute(DeleteFileActionParam param) {
        String fileUuid = param.getFileUuid();
        String folderUuid = param.getFolderUuid();
        String dataUuid = param.getDataUuid();
        if (StringUtils.isNotBlank(fileUuid)) {
            this.dmsFileActionService.deleteFile(fileUuid);
        } else {
            this.dmsFileActionService.deleteFileByFolderUuidAndDataUuid(folderUuid, dataUuid);
        }
        return new DeleteFileActionResult();
    }

    public static class DeleteFileActionParam extends FileActionParam<DeleteFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 6498143194853116693L;

        private String fileUuid;

        private String folderUuid;

        private String dataUuid;

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
         * @return the dataUuid
         */
        public String getDataUuid() {
            return dataUuid;
        }

        /**
         * @param dataUuid 要设置的dataUuid
         */
        public void setDataUuid(String dataUuid) {
            this.dataUuid = dataUuid;
        }

    }

    public static class DeleteFileActionResult extends FileActionResult<DeleteFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 7117304927770662873L;

    }

}
