/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.ViewFolderAttributesAction;
import com.wellsoft.pt.dms.file.executor.ViewFolderAttributesActionExecutor.ViewFolderAttributesActionParam;
import com.wellsoft.pt.dms.file.executor.ViewFolderAttributesActionExecutor.ViewFolderAttributesActionResult;
import com.wellsoft.pt.dms.model.DmsFolderAttributes;
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
public class ViewFolderAttributesActionExecutor extends
        AbstractFileActionExecutor<ViewFolderAttributesActionParam, ViewFolderAttributesActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public ViewFolderAttributesActionResult execute(ViewFolderAttributesActionParam param) {
        String folderUuid = param.getFolderUuid();
        DmsFolderAttributes attributes = this.dmsFileActionService.getFolderAttributes(folderUuid);
        ViewFolderAttributesActionResult result = new ViewFolderAttributesActionResult();
        result.setData(attributes);
        return result;
    }

    public static class ViewFolderAttributesActionParam extends FileActionParam<ViewFolderAttributesAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -7037056331119147698L;

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

    public static class ViewFolderAttributesActionResult extends FileActionResult<ViewFolderAttributesAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1387041830823674377L;

        private DmsFolderAttributes data;

        /**
         * @return the data
         */
        public DmsFolderAttributes getData() {
            return data;
        }

        /**
         * @param data 要设置的data
         */
        public void setData(DmsFolderAttributes data) {
            this.data = data;
        }

    }

}
