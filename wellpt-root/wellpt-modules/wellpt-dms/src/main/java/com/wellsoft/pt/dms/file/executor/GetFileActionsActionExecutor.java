/*
 * @(#)Jan 10, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.GetFileActionsAction;
import com.wellsoft.pt.dms.file.executor.GetFileActionsActionExecutor.GetFileActionsActionParam;
import com.wellsoft.pt.dms.file.executor.GetFileActionsActionExecutor.GetFileActionsActionResult;
import com.wellsoft.pt.dms.model.DmsFileAction;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
 * Jan 10, 2018.1	zhulh		Jan 10, 2018		Create
 * </pre>
 * @date Jan 10, 2018
 */
@Component
public class GetFileActionsActionExecutor extends
        AbstractFileActionExecutor<GetFileActionsActionParam, GetFileActionsActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public GetFileActionsActionResult execute(GetFileActionsActionParam param) {
        Map<String, List<DmsFileAction>> data = new HashMap<String, List<DmsFileAction>>();
        // 夹
        List<String> folderUuids = param.getFolderUuids();
        for (String folderUuid : folderUuids) {
            List<DmsFileAction> folderActions = this.dmsFileActionService.getFolderActions(folderUuid);
            data.put(folderUuid, folderActions);
        }
        // 文件
        List<String> fileUuids = param.getFileUuids();
        for (String fileUuid : fileUuids) {
            List<DmsFileAction> fileActions = this.dmsFileActionService.getFileActions(fileUuid);
            data.put(fileUuid, fileActions);
        }
        GetFileActionsActionResult result = new GetFileActionsActionResult();
        result.setData(data);
        return result;
    }

    public static class GetFileActionsActionParam extends FileActionParam<GetFileActionsAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 2759964228841035789L;
        private List<String> folderUuids;
        private List<String> fileUuids;

        /**
         * @return the folderUuids
         */
        public List<String> getFolderUuids() {
            return folderUuids;
        }

        /**
         * @param folderUuids 要设置的folderUuids
         */
        public void setFolderUuids(List<String> folderUuids) {
            this.folderUuids = folderUuids;
        }

        /**
         * @return the fileUuids
         */
        public List<String> getFileUuids() {
            return fileUuids;
        }

        /**
         * @param fileUuids 要设置的fileUuids
         */
        public void setFileUuids(List<String> fileUuids) {
            this.fileUuids = fileUuids;
        }

    }

    public static class GetFileActionsActionResult extends FileActionResult<GetFileActionsAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -7069125791201073680L;
        private Map<String, List<DmsFileAction>> data;

        /**
         * @return the data
         */
        public Map<String, List<DmsFileAction>> getData() {
            return data;
        }

        /**
         * @param data 要设置的data
         */
        public void setData(Map<String, List<DmsFileAction>> data) {
            this.data = data;
        }

    }

}
