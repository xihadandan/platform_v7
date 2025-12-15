/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.RenameFileAction;
import com.wellsoft.pt.dms.file.executor.RenameFileActionExecutor.RenameFileActionParam;
import com.wellsoft.pt.dms.file.executor.RenameFileActionExecutor.RenameFileActionResult;
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
public class RenameFileActionExecutor extends AbstractFileActionExecutor<RenameFileActionParam, RenameFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public RenameFileActionResult execute(RenameFileActionParam param) {
        String fileUuid = param.getFileUuid();
        String newFileName = param.getNewFileName();
        this.dmsFileActionService.renameFile(fileUuid, newFileName);
        return new RenameFileActionResult();
    }

    public static class RenameFileActionParam extends FileActionParam<RenameFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 4944622518674914185L;

        private String fileUuid;
        private String newFileName;

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
         * @return the newFileName
         */
        public String getNewFileName() {
            return newFileName;
        }

        /**
         * @param newFileName 要设置的newFileName
         */
        public void setNewFileName(String newFileName) {
            this.newFileName = newFileName;
        }

    }

    public static class RenameFileActionResult extends FileActionResult<RenameFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 5735807108927245182L;

    }

}
