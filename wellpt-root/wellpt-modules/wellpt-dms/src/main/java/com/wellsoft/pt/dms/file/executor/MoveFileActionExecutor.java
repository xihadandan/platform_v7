/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.MoveFileAction;
import com.wellsoft.pt.dms.file.executor.MoveFileActionExecutor.MoveFileActionParam;
import com.wellsoft.pt.dms.file.executor.MoveFileActionExecutor.MoveFileActionResult;
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
public class MoveFileActionExecutor extends AbstractFileActionExecutor<MoveFileActionParam, MoveFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public MoveFileActionResult execute(MoveFileActionParam param) {
        String sourceFileUuid = param.getSourceFileUuid();
        String destFolderUuid = param.getDestFolderUuid();
        this.dmsFileActionService.moveFile(sourceFileUuid, destFolderUuid);
        return new MoveFileActionResult();
    }

    public static class MoveFileActionParam extends FileActionParam<MoveFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 2068811269596328652L;
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

    public static class MoveFileActionResult extends FileActionResult<MoveFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 3107307608869114945L;

    }

}
