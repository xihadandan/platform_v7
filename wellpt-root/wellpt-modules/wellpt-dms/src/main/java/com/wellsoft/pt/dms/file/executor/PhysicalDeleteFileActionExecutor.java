/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.PhysicalDeleteFileAction;
import com.wellsoft.pt.dms.file.executor.PhysicalDeleteFileActionExecutor.PhysicalDeleteFileActionParam;
import com.wellsoft.pt.dms.file.executor.PhysicalDeleteFileActionExecutor.PhysicalDeleteFileActionResult;
import org.apache.commons.lang.StringUtils;
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
 * Jan 3, 2018.1	zhulh		Jan 3, 2018		Create
 * </pre>
 * @date Jan 3, 2018
 */
@Component
public class PhysicalDeleteFileActionExecutor extends
        AbstractFileActionExecutor<PhysicalDeleteFileActionParam, PhysicalDeleteFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public PhysicalDeleteFileActionResult execute(PhysicalDeleteFileActionParam param) {
        String fileUuid = param.getFileUuid();
        String folderUuid = param.getFolderUuid();
        String dataUuid = param.getDataUuid();
        String dataDefUuid = param.getDataDefUuid();
        if (StringUtils.isNotBlank(fileUuid)) {
            this.dmsFileActionService.physicalDeleteFile(fileUuid);
        } else {
            this.dmsFileActionService.physicalDeleteFileByFolderUuidAndDataUuid(folderUuid, dataUuid, dataDefUuid);
        }
        return new PhysicalDeleteFileActionResult();
    }

    public static class PhysicalDeleteFileActionParam extends FileActionParam<PhysicalDeleteFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 6498143194853116693L;

        private String fileUuid;

        private String folderUuid;

        private String dataUuid;

        private String dataDefUuid;

        /**
         * @return the fileUuid
         */
        public String getFileUuid() {
            return fileUuid;
        }

        /**
         * @param fileUuid 要设置的fileUuid
         */
        public void setFileUuid(String fileUuid) {
            this.fileUuid = fileUuid;
        }

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
         * @return the dataUuid
         */
        public String getDataUuid() {
            return dataUuid;
        }

        /**
         * @param dataUuid 要设置的dataUuid
         */
        public void setDataUuid(String dataUuid) {
            this.dataUuid = dataUuid;
        }

        /**
         * @return the dataDefUuid
         */
        public String getDataDefUuid() {
            return dataDefUuid;
        }

        /**
         * @param dataDefUuid 要设置的dataDefUuid
         */
        public void setDataDefUuid(String dataDefUuid) {
            this.dataDefUuid = dataDefUuid;
        }

    }

    public static class PhysicalDeleteFileActionResult extends FileActionResult<PhysicalDeleteFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 7117304927770662873L;

    }

}
