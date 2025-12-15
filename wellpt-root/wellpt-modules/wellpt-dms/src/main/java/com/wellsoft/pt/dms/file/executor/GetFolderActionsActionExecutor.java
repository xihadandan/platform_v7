/*
 * @(#)Jan 10, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.GetFolderActionsAction;
import com.wellsoft.pt.dms.file.executor.GetFolderActionsActionExecutor.GetFolderActionsActionParam;
import com.wellsoft.pt.dms.file.executor.GetFolderActionsActionExecutor.GetFolderActionsActionResult;
import com.wellsoft.pt.dms.model.DmsFileAction;
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
 * Jan 10, 2018.1	zhulh		Jan 10, 2018		Create
 * </pre>
 * @date Jan 10, 2018
 */
@Component
public class GetFolderActionsActionExecutor extends
        AbstractFileActionExecutor<GetFolderActionsActionParam, GetFolderActionsActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public GetFolderActionsActionResult execute(GetFolderActionsActionParam param) {
        String folderUuid = param.getFolderUuid();
        List<DmsFileAction> fileActions = this.dmsFileActionService.getFolderActions(folderUuid);
        GetFolderActionsActionResult result = new GetFolderActionsActionResult();
        result.setDataList(fileActions);
        return result;
    }

    public static class GetFolderActionsActionParam extends FileActionParam<GetFolderActionsAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -5195532814295189537L;
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

    public static class GetFolderActionsActionResult extends FileActionResult<GetFolderActionsAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 5291065497670659241L;
        private List<DmsFileAction> dataList;

        /**
         * @return the dataList
         */
        public List<DmsFileAction> getDataList() {
            return dataList;
        }

        /**
         * @param dataList 要设置的dataList
         */
        public void setDataList(List<DmsFileAction> dataList) {
            this.dataList = dataList;
        }

    }

}
