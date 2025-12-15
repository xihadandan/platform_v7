/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.DeleteFolderAction;
import com.wellsoft.pt.dms.file.executor.DeleteFolderActionExecutor.DeleteFolderActionParam;
import com.wellsoft.pt.dms.file.executor.DeleteFolderActionExecutor.DeleteFolderActionResult;
import org.apache.commons.lang.StringUtils;
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
public class DeleteFolderActionExecutor extends
        AbstractFileActionExecutor<DeleteFolderActionParam, DeleteFolderActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public DeleteFolderActionResult execute(DeleteFolderActionParam param) {
        String folderUuid = param.getFolderUuid();
        List<String> folderUuids = param.getFolderUuids();
        // 删除单个夹
        if (StringUtils.isNotBlank(folderUuid)) {
            this.dmsFileActionService.deleteFolder(folderUuid);
        }
        // 批量删除夹
        if (folderUuids != null) {
            this.dmsFileActionService.deleteFolder(folderUuids);
        }
        return new DeleteFolderActionResult();
    }

    public static class DeleteFolderActionParam extends FileActionParam<DeleteFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -2812760999580207508L;

        private String folderUuid;

        private List<String> folderUuids;

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

    }

    public static class DeleteFolderActionResult extends FileActionResult<DeleteFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -468681225566332027L;

    }

}
