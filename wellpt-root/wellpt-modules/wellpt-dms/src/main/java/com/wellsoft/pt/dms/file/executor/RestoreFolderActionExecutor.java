/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.RestoreFolderAction;
import com.wellsoft.pt.dms.file.executor.RestoreFolderActionExecutor.RestoreFolderActionParam;
import com.wellsoft.pt.dms.file.executor.RestoreFolderActionExecutor.RestoreFolderActionResult;
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
public class RestoreFolderActionExecutor extends
        AbstractFileActionExecutor<RestoreFolderActionParam, RestoreFolderActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public RestoreFolderActionResult execute(RestoreFolderActionParam param) {
        String folderUuid = param.getFolderUuid();
        this.dmsFileActionService.restoreFolder(folderUuid);
        return new RestoreFolderActionResult();
    }

    public static class RestoreFolderActionParam extends FileActionParam<RestoreFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 6498143194853116693L;

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

    public static class RestoreFolderActionResult extends FileActionResult<RestoreFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 7117304927770662873L;

    }

}
