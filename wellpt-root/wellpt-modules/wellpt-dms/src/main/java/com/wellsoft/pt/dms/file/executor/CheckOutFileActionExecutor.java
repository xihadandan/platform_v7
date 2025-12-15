/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.CheckOutFileAction;
import com.wellsoft.pt.dms.file.executor.CheckOutFileActionExecutor.CheckOutFileActionParam;
import com.wellsoft.pt.dms.file.executor.CheckOutFileActionExecutor.CheckOutFileActionResult;
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
public class CheckOutFileActionExecutor extends
        AbstractFileActionExecutor<CheckOutFileActionParam, CheckOutFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public CheckOutFileActionResult execute(CheckOutFileActionParam param) {
        String fileUuid = param.getFileUuid();
        this.dmsFileActionService.checkOutFile(fileUuid);
        return new CheckOutFileActionResult();
    }

    public static class CheckOutFileActionParam extends FileActionParam<CheckOutFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -2902940152321705027L;

        private String fileUuid;

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

    }

    public static class CheckOutFileActionResult extends FileActionResult<CheckOutFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1895696699574592134L;

        // 检出的新文件UUID
        private String fileUuid;

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

    }

}
