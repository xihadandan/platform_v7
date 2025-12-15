/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.CancelShareFolderAction;
import com.wellsoft.pt.dms.file.executor.CancelShareFolderActionExecutor.CancelShareFolderActionParam;
import com.wellsoft.pt.dms.file.executor.CancelShareFolderActionExecutor.CancelShareFolderActionResult;
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
public class CancelShareFolderActionExecutor extends
        AbstractFileActionExecutor<CancelShareFolderActionParam, CancelShareFolderActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public CancelShareFolderActionResult execute(CancelShareFolderActionParam param) {
        // 夹UUID
        String folderUuid = param.getFolderUuid();
        // 分享UUID
        String shareUuid = param.getShareUuid();
        dmsFileActionService.cancelShareFolder(folderUuid, shareUuid);
        CancelShareFolderActionResult cancelShareFolderActionResult = new CancelShareFolderActionResult();
        return cancelShareFolderActionResult;
    }

    public static class CancelShareFolderActionParam extends FileActionParam<CancelShareFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 4163734233490883645L;

        // 夹UUID
        private String folderUuid;
        // 分享UUID
        private String shareUuid;

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

    public static class CancelShareFolderActionResult extends FileActionResult<CancelShareFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1240867818841440060L;

    }

}
