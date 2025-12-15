/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.AttentionFileAction;
import com.wellsoft.pt.dms.file.executor.AttentionFileActionExecutor.AttentionFileActionParam;
import com.wellsoft.pt.dms.file.executor.AttentionFileActionExecutor.AttentionFileActionResult;
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
public class AttentionFileActionExecutor extends
        AbstractFileActionExecutor<AttentionFileActionParam, AttentionFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public AttentionFileActionResult execute(AttentionFileActionParam param) {
        String fileUuid = param.getFileUuid();
        this.dmsFileActionService.attentionFile(fileUuid);
        return new AttentionFileActionResult();
    }

    public static class AttentionFileActionParam extends FileActionParam<AttentionFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1847810831470295782L;

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

    public static class AttentionFileActionResult extends FileActionResult<AttentionFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -1642695657107224155L;

    }

}
