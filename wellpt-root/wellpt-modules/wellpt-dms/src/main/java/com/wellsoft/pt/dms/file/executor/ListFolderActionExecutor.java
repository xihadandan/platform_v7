/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.dms.file.action.ListFolderAction;
import com.wellsoft.pt.dms.file.executor.ListFolderActionExecutor.ListFolderActionParam;
import com.wellsoft.pt.dms.file.executor.ListFolderActionExecutor.ListFolderActionResult;
import com.wellsoft.pt.dms.model.DmsFileAction;
import com.wellsoft.pt.dms.model.DmsFolder;
import com.wellsoft.pt.dms.service.DmsFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
public class ListFolderActionExecutor extends AbstractFileActionExecutor<ListFolderActionParam, ListFolderActionResult> {

    @Autowired
    private DmsFolderService dmsFolderService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public ListFolderActionResult execute(ListFolderActionParam param) {
        String folderUuid = param.getFolderUuid();
        boolean listNearestIfNotFound = param.isListNearestIfNotFound();
        boolean isLoadActions = param.isLoadAction();
        boolean checkIsParent = param.isCheckIsParent();
        List<DmsFolder> folders = null;
        if (param.isWithoutPermission()) {
            folders = dmsFileActionService.listFolderWithoutPermission(folderUuid);
        } else {
            folders = dmsFileActionService.listFolder(folderUuid, listNearestIfNotFound);
        }
        ListFolderActionResult result = new ListFolderActionResult();
        result.setDataList(folders);

        // 加载夹对应的文件操作权限
        if (isLoadActions) {
            List<DmsFileAction> fileActions = this.dmsFileActionService.getFolderActions(folderUuid);
            result.setFileActions(fileActions);
        }

        // 判断是否父夹
        if (checkIsParent) {
            // 激进处理方式
            Map<String, DmsFolder> folderMap = ConvertUtils.convertElementToMap(folders, IdEntity.UUID);
            Map<String, Long> countMap = dmsFolderService.countByParentUuids(Arrays.asList(folderMap.keySet().toArray(
                    new String[0])));
            for (DmsFolder folder : folders) {
                String temFolderUuid = folder.getUuid();
                if (countMap.containsKey(temFolderUuid)) {
                    folder.setParent(countMap.get(temFolderUuid) > 0);
                }
            }
            //			for (DmsFolder folder : folders) {
            //				folder.setParent(this.dmsFileActionService.listFolderCount(folder.getUuid()) > 0);
            //			}
        }

        return result;
    }

    public static class ListFolderActionParam extends FileActionParam<ListFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 9080472985741296215L;

        // 父夹UUID
        private String folderUuid;

        // 如果找不到，列出最近的子夹
        private boolean listNearestIfNotFound;

        // 是否加载操作权限
        private boolean loadAction;

        // 检测是否父结点
        private boolean checkIsParent;

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
         * @return the listNearestIfNotFound
         */
        public boolean isListNearestIfNotFound() {
            return listNearestIfNotFound;
        }

        /**
         * @param listNearestIfNotFound 要设置的listNearestIfNotFound
         */
        public void setListNearestIfNotFound(boolean listNearestIfNotFound) {
            this.listNearestIfNotFound = listNearestIfNotFound;
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

        /**
         * @return the checkIsParent
         */
        public boolean isCheckIsParent() {
            return checkIsParent;
        }

        /**
         * @param checkIsParent 要设置的checkIsParent
         */
        public void setCheckIsParent(boolean checkIsParent) {
            this.checkIsParent = checkIsParent;
        }

    }

    public static class ListFolderActionResult extends FileActionResult<ListFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -7687492478324788312L;

        private List<DmsFolder> dataList;

        private List<DmsFileAction> fileActions;

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
