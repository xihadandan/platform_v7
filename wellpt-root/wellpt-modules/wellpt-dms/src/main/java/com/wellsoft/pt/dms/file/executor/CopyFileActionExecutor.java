/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.CopyFileAction;
import com.wellsoft.pt.dms.file.executor.CopyFileActionExecutor.CopyFileActionParam;
import com.wellsoft.pt.dms.file.executor.CopyFileActionExecutor.CopyFileActionResult;
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
public class CopyFileActionExecutor extends AbstractFileActionExecutor<CopyFileActionParam, CopyFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public CopyFileActionResult execute(CopyFileActionParam param) {
        String sourceFileUuid = param.getSourceFileUuid();
        String destFolderUuid = param.getDestFolderUuid();
        String fileUuid = this.dmsFileActionService.copyFile(sourceFileUuid, destFolderUuid);
        CopyFileActionResult result = new CopyFileActionResult();
        result.setFileUuid(fileUuid);
        return result;
    }

    public static class CopyFileActionParam extends FileActionParam<CopyFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 5932563861297263376L;

        // 源文件UUID
        private String sourceFileUuid;
        // 目标文件夹UUID
        private String destFolderUuid;

        /**
         * @return the sourceFileUuid
         */
        public String getSourceFileUuid() {
            return sourceFileUuid;
        }

        /**
         * @param sourceFileUuid 要设置的sourceFileUuid
         */
        public void setSourceFileUuid(String sourceFileUuid) {
            this.sourceFileUuid = sourceFileUuid;
        }

        /**
         * @return the destFolderUuid
         */
        public String getDestFolderUuid() {
            return destFolderUuid;
        }

        /**
         * @param destFolderUuid 要设置的destFolderUuid
         */
        public void setDestFolderUuid(String destFolderUuid) {
            this.destFolderUuid = destFolderUuid;
        }

    }

    public static class CopyFileActionResult extends FileActionResult<CopyFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -996555695870797015L;

        // 复制后新的文件UUID
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
