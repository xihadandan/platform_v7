/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.ShareFileAction;
import com.wellsoft.pt.dms.file.executor.ShareFileActionExecutor.ShareFileActionParam;
import com.wellsoft.pt.dms.file.executor.ShareFileActionExecutor.ShareFileActionResult;
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
public class ShareFileActionExecutor extends AbstractFileActionExecutor<ShareFileActionParam, ShareFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public ShareFileActionResult execute(ShareFileActionParam param) {
        // 分享的文件UUID
        String fileUuid = param.getFileUuid();
        // 分享的组织ID
        String shareOrgId = param.getShareOrgId();
        // 分享的组织名称
        String shareOrgName = param.getShareOrgName();
        String shareUuid = dmsFileActionService.shareFile(fileUuid, shareOrgId, shareOrgName);
        ShareFileActionResult shareFileActionResult = new ShareFileActionResult();
        shareFileActionResult.setShareUuid(shareUuid);
        return shareFileActionResult;
    }

    public static class ShareFileActionParam extends FileActionParam<ShareFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 4163734233490883645L;

        // 分享的文件UUID
        private String fileUuid;
        // 分享的组织ID
        private String shareOrgId;
        // 分享的组织名称
        private String shareOrgName;

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
         * @return the shareOrgId
         */
        public String getShareOrgId() {
            return shareOrgId;
        }

        /**
         * @param shareOrgId 要设置的shareOrgId
         */
        public void setShareOrgId(String shareOrgId) {
            this.shareOrgId = shareOrgId;
        }

        /**
         * @return the shareOrgName
         */
        public String getShareOrgName() {
            return shareOrgName;
        }

        /**
         * @param shareOrgName 要设置的shareOrgName
         */
        public void setShareOrgName(String shareOrgName) {
            this.shareOrgName = shareOrgName;
        }

    }

    public static class ShareFileActionResult extends FileActionResult<ShareFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1240867818841440060L;

        // 返回的分享UUID
        private String shareUuid;

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

}
