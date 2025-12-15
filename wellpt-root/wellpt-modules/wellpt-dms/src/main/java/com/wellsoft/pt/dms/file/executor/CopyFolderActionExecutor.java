/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.CopyFolderAction;
import com.wellsoft.pt.dms.file.executor.CopyFolderActionExecutor.CopyFolderActionParam;
import com.wellsoft.pt.dms.file.executor.CopyFolderActionExecutor.CopyFolderActionResult;
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
public class CopyFolderActionExecutor extends AbstractFileActionExecutor<CopyFolderActionParam, CopyFolderActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public CopyFolderActionResult execute(CopyFolderActionParam param) {
        String sourceFolderUuid = param.getSourceFolderUuid();
        String destFolderUuid = param.getDestFolderUuid();
        String folderUuid = this.dmsFileActionService.copyFolder(sourceFolderUuid, destFolderUuid);
        CopyFolderActionResult result = new CopyFolderActionResult();
        result.setFolderUuid(folderUuid);
        return result;
    }

    public static class CopyFolderActionParam extends FileActionParam<CopyFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -5767664549586448112L;

        // 源文件夹UUID
        private String sourceFolderUuid;
        // 目标文件夹UUID
        private String destFolderUuid;

        /**
         * @return the sourceFolderUuid
         */
        public String getSourceFolderUuid() {
            return sourceFolderUuid;
        }

        /**
         * @param sourceFolderUuid 要设置的sourceFolderUuid
         */
        public void setSourceFolderUuid(String sourceFolderUuid) {
            this.sourceFolderUuid = sourceFolderUuid;
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

    public static class CopyFolderActionResult extends FileActionResult<CopyFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 5916138650041067875L;

        // 复制后新的夹UUID
        private String folderUuid;

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

    }

}
