/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.ForceDeleteFolderAction;
import com.wellsoft.pt.dms.file.executor.ForceDeleteFolderActionExecutor.ForceDeleteFolderActionParam;
import com.wellsoft.pt.dms.file.executor.ForceDeleteFolderActionExecutor.ForceDeleteFolderActionResult;
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
public class ForceDeleteFolderActionExecutor extends
        AbstractFileActionExecutor<ForceDeleteFolderActionParam, ForceDeleteFolderActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public ForceDeleteFolderActionResult execute(ForceDeleteFolderActionParam param) {
        String folderUuid = param.getFolderUuid();
        this.dmsFileActionService.forceDeleteFolder(folderUuid);
        return new ForceDeleteFolderActionResult();
    }

    public static class ForceDeleteFolderActionParam extends FileActionParam<ForceDeleteFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -1426700202044283690L;

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

    public static class ForceDeleteFolderActionResult extends FileActionResult<ForceDeleteFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 579648103731773780L;

    }

}
