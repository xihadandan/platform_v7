/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.ListFilesAction;
import com.wellsoft.pt.dms.file.executor.ListFilesActionExecutor.ListFilesActionParam;
import com.wellsoft.pt.dms.file.executor.ListFilesActionExecutor.ListFilesActionResult;
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
 * Jan 3, 2018.1	zhulh		Jan 3, 2018		Create
 * </pre>
 * @date Jan 3, 2018
 */
@Component
public class ListFilesActionExecutor extends AbstractFileActionExecutor<ListFilesActionParam, ListFilesActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public ListFilesActionResult execute(ListFilesActionParam param) {
        String folderUuid = param.getFolderUuid();
        List<DmsFile> files = this.dmsFileActionService.listFiles(folderUuid);
        ListFilesActionResult result = new ListFilesActionResult();
        result.setDataList(files);
        return result;
    }

    public static class ListFilesActionParam extends FileActionParam<ListFilesAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -5446187669990607022L;

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

    public static class ListFilesActionResult extends FileActionResult<ListFilesAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -8138053597271912731L;

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
