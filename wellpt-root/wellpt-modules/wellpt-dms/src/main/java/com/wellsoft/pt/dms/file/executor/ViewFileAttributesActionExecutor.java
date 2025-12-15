/*
 * @(#)Jan 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.pt.dms.file.action.ViewFileAttributesAction;
import com.wellsoft.pt.dms.file.executor.ViewFileAttributesActionExecutor.ViewFileAttributesActionParam;
import com.wellsoft.pt.dms.file.executor.ViewFileAttributesActionExecutor.ViewFileAttributesActionResult;
import com.wellsoft.pt.dms.model.DmsFileAttributes;
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
public class ViewFileAttributesActionExecutor extends
        AbstractFileActionExecutor<ViewFileAttributesActionParam, ViewFileAttributesActionResult> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.executor.FileActionExecutor#execute(com.wellsoft.pt.dms.file.executor.FileActionParam)
     */
    @Override
    public ViewFileAttributesActionResult execute(ViewFileAttributesActionParam param) {
        String fileUuid = param.getFileUuid();
        DmsFileAttributes attributes = this.dmsFileActionService.getFileAttributes(fileUuid);
        ViewFileAttributesActionResult result = new ViewFileAttributesActionResult();
        result.setData(attributes);
        return result;
    }

    public static class ViewFileAttributesActionParam extends FileActionParam<ViewFileAttributesAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -1770353712919271237L;

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

    public static class ViewFileAttributesActionResult extends FileActionResult<ViewFileAttributesAction> {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 6271460007710603048L;

        private DmsFileAttributes data;

        /**
         * @return the data
         */
        public DmsFileAttributes getData() {
            return data;
        }

        /**
         * @param data 要设置的data
         */
        public void setData(DmsFileAttributes data) {
            this.data = data;
        }

    }

}
