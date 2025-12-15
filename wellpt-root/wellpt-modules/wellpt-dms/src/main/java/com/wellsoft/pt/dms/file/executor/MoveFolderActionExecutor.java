/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.MoveFolderAction;
import com.wellsoft.pt.dms.file.executor.MoveFolderActionExecutor.MoveFolderActionParam;
import com.wellsoft.pt.dms.file.executor.MoveFolderActionExecutor.MoveFolderActionResult;
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
public class MoveFolderActionExecutor extends AbstractFileActionExecutor<MoveFolderActionParam, MoveFolderActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public MoveFolderActionResult execute(MoveFolderActionParam param) {
        String sourceFolderUuid = param.getSourceFolderUuid();
        String destFolderUuid = param.getDestFolderUuid();
        this.dmsFileActionService.moveFolder(sourceFolderUuid, destFolderUuid);
        return new MoveFolderActionResult();
    }

    public static class MoveFolderActionParam extends FileActionParam<MoveFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 3820730548723088997L;
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

    public static class MoveFolderActionResult extends FileActionResult<MoveFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -2829899373531738761L;

    }

}
