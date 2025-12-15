/*
 * @(#)Jan 4, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.ListAllFolderAction;
import com.wellsoft.pt.dms.file.executor.ListAllFolderActionExecutor.ListAllFolderActionParam;
import com.wellsoft.pt.dms.file.executor.ListAllFolderActionExecutor.ListAllFolderActionResult;
import com.wellsoft.pt.dms.model.DmsFolder;
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
public class ListAllFolderActionExecutor extends
        AbstractFileActionExecutor<ListAllFolderActionParam, ListAllFolderActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public ListAllFolderActionResult execute(ListAllFolderActionParam param) {
        String folderUuid = param.getFolderUuid();
        List<DmsFolder> folders = this.dmsFileActionService.listAllFolder(folderUuid);
        ListAllFolderActionResult result = new ListAllFolderActionResult();
        result.setDataList(folders);
        return result;
    }

    public static class ListAllFolderActionParam extends FileActionParam<ListAllFolderAction> {

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

    public static class ListAllFolderActionResult extends FileActionResult<ListAllFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -2603237928629694776L;

        private List<DmsFolder> dataList;

        /**
         * @return the dataList
         */
        public List<DmsFolder> getDataList() {
            return dataList;
        }

        /**
         * @param dataList 要设置的dataList
         */
        public void setDataList(List<DmsFolder> dataList) {
            this.dataList = dataList;
        }

    }

}
