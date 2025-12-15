/*
 * @(#)Jan 4, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.ListAllFilesAction;
import com.wellsoft.pt.dms.file.executor.ListAllFilesActionExecutor.ListAllFilesActionParam;
import com.wellsoft.pt.dms.file.executor.ListAllFilesActionExecutor.ListAllFilesActionResult;
import com.wellsoft.pt.dms.model.DmsFile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 4, 2018.1	zhulh		Jan 4, 2018		Create
 * </pre>
 * @date Jan 4, 2018
 */
@Component
public class ListAllFilesActionExecutor extends
        AbstractFileActionExecutor<ListAllFilesActionParam, ListAllFilesActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public ListAllFilesActionResult execute(ListAllFilesActionParam param) {
        String folderUuid = param.getFolderUuid();
        List<DmsFile> folders = this.dmsFileActionService.listAllFiles(folderUuid);
        ListAllFilesActionResult result = new ListAllFilesActionResult();
        result.setDataList(folders);
        return result;
    }

    public static class ListAllFilesActionParam extends FileActionParam<ListAllFilesAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 602207279052336331L;

        // 父夹UUID
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

    public static class ListAllFilesActionResult extends FileActionResult<ListAllFilesAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -2603237928629694776L;

        private List<DmsFile> dataList;

        /**
         * @return the dataList
         */
        public List<DmsFile> getDataList() {
            return dataList;
        }

        /**
         * @param dataList 要设置的dataList
         */
        public void setDataList(List<DmsFile> dataList) {
            this.dataList = dataList;
        }

    }

}
