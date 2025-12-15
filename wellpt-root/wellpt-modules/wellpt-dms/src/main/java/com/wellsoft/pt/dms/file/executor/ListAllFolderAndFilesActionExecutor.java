/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.ListAllFolderAndFilesAction;
import com.wellsoft.pt.dms.file.executor.ListAllFolderAndFilesActionExecutor.ListAllFolderAndFilesActionParam;
import com.wellsoft.pt.dms.file.executor.ListAllFolderAndFilesActionExecutor.ListAllFolderAndFilesActionResult;
import com.wellsoft.pt.dms.model.DmsFile;
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
 * Jan 3, 2018.1	zhulh		Jan 3, 2018		Create
 * </pre>
 * @date Jan 3, 2018
 */
@Component
public class ListAllFolderAndFilesActionExecutor extends
        AbstractFileActionExecutor<ListAllFolderAndFilesActionParam, ListAllFolderAndFilesActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public ListAllFolderAndFilesActionResult execute(ListAllFolderAndFilesActionParam param) {
        String folderUuid = param.getFolderUuid();
        boolean isLoadActions = param.isLoadAction();
        List<DmsFile> folders = this.dmsFileActionService.listAllFolderAndFiles(folderUuid);
        ListAllFolderAndFilesActionResult result = new ListAllFolderAndFilesActionResult();
        result.setDataList(folders);

        // 加载夹对应的文件操作权限
        if (isLoadActions) {
            List<DmsFileAction> fileActions = this.dmsFileActionService.getFolderActions(folderUuid);
            result.setFileActions(fileActions);
        }

        return result;
    }

    public static class ListAllFolderAndFilesActionParam extends FileActionParam<ListAllFolderAndFilesAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -224774496358095203L;

        // 父夹UUID
        private String folderUuid;

        // 是否加载操作权限
        private boolean loadAction;

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
         * @return the loadAction
         */
        public boolean isLoadAction() {
            return loadAction;
        }

        /**
         * @param loadAction 要设置的loadAction
         */
        public void setLoadAction(boolean loadAction) {
            this.loadAction = loadAction;
        }

    }

    public static class ListAllFolderAndFilesActionResult extends FileActionResult<ListAllFolderAndFilesAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -2051992314223606630L;

        private List<DmsFile> dataList;

        private List<DmsFileAction> fileActions;

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

        /**
         * @return the fileActions
         */
        public List<DmsFileAction> getFileActions() {
            return fileActions;
        }

        /**
         * @param fileActions 要设置的fileActions
         */
        public void setFileActions(List<DmsFileAction> fileActions) {
            this.fileActions = fileActions;
        }

    }

}
