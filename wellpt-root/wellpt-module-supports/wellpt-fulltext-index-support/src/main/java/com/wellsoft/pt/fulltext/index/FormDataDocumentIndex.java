/*
 * @(#)6/17/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.index;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/17/25.1	    zhulh		6/17/25		    Create
 * </pre>
 * @date 6/17/25
 */
@Document(indexName = "form_data_document", createIndex = true)
public class FormDataDocumentIndex extends BaseDocumentIndex {
    @Field(type = FieldType.Keyword, index = false, store = true)
    private String formUuid;

    @Field(type = FieldType.Keyword, index = false, store = true)
    private String dataUuid;

    @Field(type = FieldType.Keyword, index = false, store = true)
    private String dataModeUuid;

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
     * @return the dataModeUuid
     */
    public String getDataModeUuid() {
        return dataModeUuid;
    }

    /**
     * @param dataModeUuid 要设置的dataModeUuid
     */
    public void setDataModeUuid(String dataModeUuid) {
        this.dataModeUuid = dataModeUuid;
    }

}
