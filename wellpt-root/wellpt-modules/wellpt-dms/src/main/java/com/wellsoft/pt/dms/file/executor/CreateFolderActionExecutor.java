/*
 * @(#)Jan 2, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.CreateFolderAction;
import com.wellsoft.pt.dms.file.executor.CreateFolderActionExecutor.CreateFolderActionParam;
import com.wellsoft.pt.dms.file.executor.CreateFolderActionExecutor.CreateFolderActionResult;
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
 * Jan 2, 2018.1	zhulh		Jan 2, 2018		Create
 * </pre>
 * @date Jan 2, 2018
 */
@Component
public class CreateFolderActionExecutor extends
        AbstractFileActionExecutor<CreateFolderActionParam, CreateFolderActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.core.file.executor.FileActionParam)
     */
    @Override
    public CreateFolderActionResult execute(CreateFolderActionParam param) {
        String parentFolderUuid = param.getFolderUuid();
        String childrenFolderUuid = param.getData().getUuid();
        String childrenFolderName = param.getData().getName();
        // String childrenFolderRemark = param.getData().getRemark();
        String childrenUuid = dmsFileActionService.createFolder(childrenFolderUuid, childrenFolderName,
                parentFolderUuid);
        CreateFolderActionResult result = new CreateFolderActionResult();
        result.setFolderUuid(childrenUuid);
        return result;
    }

    public static class CreateFolderActionParam extends FileActionParam<CreateFolderAction> {
        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -4639761885835946930L;

        // 父夹UUID
        private String folderUuid;
        // 夹数据
        private DmsFolder data;

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

    public static class CreateFolderActionResult extends FileActionResult<CreateFolderAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 3287595110757848675L;

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

}
