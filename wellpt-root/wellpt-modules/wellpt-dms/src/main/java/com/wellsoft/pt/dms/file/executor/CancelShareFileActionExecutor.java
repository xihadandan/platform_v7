/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.CancelShareFileAction;
import com.wellsoft.pt.dms.file.executor.CancelShareFileActionExecutor.CancelShareFileActionParam;
import com.wellsoft.pt.dms.file.executor.CancelShareFileActionExecutor.CancelShareFileActionResult;
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
public class CancelShareFileActionExecutor extends
        AbstractFileActionExecutor<CancelShareFileActionParam, CancelShareFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public CancelShareFileActionResult execute(CancelShareFileActionParam param) {
        // 文件UUID
        String fileUuid = param.getFileUuid();
        // 分享UUID
        String shareUuid = param.getShareUuid();
        dmsFileActionService.cancelShareFile(fileUuid, shareUuid);
        CancelShareFileActionResult CancelShareFileActionResult = new CancelShareFileActionResult();
        return CancelShareFileActionResult;
    }

    public static class CancelShareFileActionParam extends FileActionParam<CancelShareFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 4163734233490883645L;
        // 文件UUID
        private String fileUuid;
        // 分享UUID
        private String shareUuid;

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
         * @return the shareUuid
         */
        public String getShareUuid() {
            return shareUuid;
        }

        /**
         * @param shareUuid 要设置的shareUuid
         */
        public void setShareUuid(String shareUuid) {
            this.shareUuid = shareUuid;
        }

    }

    public static class CancelShareFileActionResult extends FileActionResult<CancelShareFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1240867818841440060L;

    }

}
