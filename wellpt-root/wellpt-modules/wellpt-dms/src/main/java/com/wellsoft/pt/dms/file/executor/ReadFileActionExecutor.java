/*
 * @(#)Jan 5, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.ReadFileAction;
import com.wellsoft.pt.dms.file.executor.ReadFileActionExecutor.ReadFileActionParam;
import com.wellsoft.pt.dms.file.executor.ReadFileActionExecutor.ReadFileActionResult;
import com.wellsoft.pt.dms.model.DmsFile;
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
 * Jan 5, 2018.1	zhulh		Jan 5, 2018		Create
 * </pre>
 * @date Jan 5, 2018
 */
@Component
public class ReadFileActionExecutor extends AbstractFileActionExecutor<ReadFileActionParam, ReadFileActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public ReadFileActionResult execute(ReadFileActionParam param) {
        String fileUuid = param.getFileUuid();
        DmsFile dmsFile = dmsFileActionService.readFile(fileUuid);
        ReadFileActionResult result = new ReadFileActionResult();
        result.setData(dmsFile);
        return result;
    }

    public static class ReadFileActionParam extends FileActionParam<ReadFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -5628621864995195779L;

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

    public static class ReadFileActionResult extends FileActionResult<ReadFileAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -8857422025245654327L;

        private DmsFile data;

        /**
         * @return the data
         */
        public DmsFile getData() {
            return data;
        }

        /**
         * @param data 要设置的data
         */
        public void setData(DmsFile data) {
            this.data = data;
        }

    }

}
