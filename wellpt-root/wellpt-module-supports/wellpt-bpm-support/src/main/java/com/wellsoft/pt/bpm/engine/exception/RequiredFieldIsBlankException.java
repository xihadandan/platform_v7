/*
 * @(#)2015-3-13 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;

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
 * 2015-3-13.1	zhulh		2015-3-13		Create
 * </pre>
 * @date 2015-3-13
 */
public class RequiredFieldIsBlankException extends WorkFlowException {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2148466865149034169L;

    private List<RequiredFieldIsBlank> requiredFieldIsBlanks;

    /**
     * @param flowExceptionInfo
     */
    public RequiredFieldIsBlankException(List<RequiredFieldIsBlank> requiredFieldIsBlanks) {
        this.requiredFieldIsBlanks = requiredFieldIsBlanks;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getData()
     */
    @Override
    public Object getData() {
        return requiredFieldIsBlanks;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.RequiredFieldIsBlank;
    }

    public static class RequiredFieldIsBlank {
        private String fieldName;

        private String dataUuid;

        private String formUuid;

        /**
         * @param fieldName
         * @param dataUuid
         * @param formUuid
         */
        public RequiredFieldIsBlank(String fieldName, String formUuid, String dataUuid) {
            super();
            this.fieldName = fieldName;
            this.formUuid = formUuid;
            this.dataUuid = dataUuid;
        }

        /**
         * @return the fieldName
         */
        public String getFieldName() {
            return fieldName;
        }

        /**
         * @param fieldName 要设置的fieldName
         */
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
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
         * @return the formUuid
         */
        public String getFormUuid() {
            return formUuid;
        }

        /**
         * @param formUuid 要设置的formUuid
         */
        public void setFormUuid(String formUuid) {
            this.formUuid = formUuid;
        }

    }
}
