/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.CreateFileAction;
import com.wellsoft.pt.dms.file.executor.CreateFileActionExecutor.CreateFileActionParam;
import com.wellsoft.pt.dms.file.executor.CreateFileActionExecutor.CreateFileActionResult;
import com.wellsoft.pt.dms.model.DmsFile;
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
public class CreateFileActionExecutor extends AbstractFileActionExecutor<CreateFileActionParam, CreateFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public CreateFileActionResult execute(CreateFileActionParam param) {
        String parentFolderUuid = param.getFolderUuid();
        String fileUuid = this.dmsFileActionService.createFile(parentFolderUuid, param.getData());
        CreateFileActionResult result = new CreateFileActionResult();
        result.setFileUuid(fileUuid);
        return result;
    }

    public static class CreateFileActionParam extends FileActionParam<CreateFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -5386908495696866780L;

        private String folderUuid;

        private DmsFile data;

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
         * @return the data
         */
        public DmsFile getData() {
            return data;
        }

        /**
         * @param data 要设置的data
         */
        public void setData(DmsFile data) {
            this.data = data;
        }

    }

    public static class CreateFileActionResult extends FileActionResult<CreateFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 7687053253261009983L;

        private String fileUuid;

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

    }

}
