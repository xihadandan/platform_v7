/*
 * @(#)Jan 5, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.ReadFolderAction;
import com.wellsoft.pt.dms.file.executor.ReadFolderActionExecutor.ReadFolderActionParam;
import com.wellsoft.pt.dms.file.executor.ReadFolderActionExecutor.ReadFolderActionResult;
import com.wellsoft.pt.dms.model.DmsFolder;
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
 * Jan 5, 2018.1	zhulh		Jan 5, 2018		Create
 * </pre>
 * @date Jan 5, 2018
 */
@Component
public class ReadFolderActionExecutor extends AbstractFileActionExecutor<ReadFolderActionParam, ReadFolderActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public ReadFolderActionResult execute(ReadFolderActionParam param) {
        String folderUuid = param.getFolderUuid();
        DmsFolder dmsFolder = param.isWithoutPermission() ? dmsFileActionService.readFolderWithoutPermission(folderUuid)
                : dmsFileActionService.readFolder(folderUuid);
        ReadFolderActionResult result = new ReadFolderActionResult();
        result.setData(dmsFolder);
        return result;
    }

    public static class ReadFolderActionParam extends FileActionParam<ReadFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -5628621864995195779L;

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

    public static class ReadFolderActionResult extends FileActionResult<ReadFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -8857422025245654327L;

        private DmsFolder data;

        /**
         * @return the data
         */
        public DmsFolder getData() {
            return data;
        }

        /**
         * @param data 要设置的data
         */
        public void setData(DmsFolder data) {
            this.data = data;
        }

    }

}
