/*
 * @(#)6/3/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.index;

import com.wellsoft.pt.fulltext.index.BaseDocumentIndex;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 6/3/24.1	zhulh		6/3/24		Create
 * </pre>
 * @date 6/3/24
 */
@Document(indexName = "dms_file_document")
public class DmsFileDocumentIndex extends BaseDocumentIndex {

    @Field(type = FieldType.Keyword, index = false, store = true)
    private String fileUuid;

    @Field(type = FieldType.Keyword, index = false, store = true)
    private String libraryUuid;

    @Field(type = FieldType.Keyword, index = false, store = true)
    private String dataDefUuid;

    @Field(type = FieldType.Keyword, index = false, store = true)
    private String dataUuid;

    @Field(type = FieldType.Keyword, index = false, store = true)
    private String contentType;

//    @Pipeline
//    @Field(name = "attachment.content", store = true, type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
//    private String content;

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
     * @return the libraryUuid
     */
    public String getLibraryUuid() {
        return libraryUuid;
    }

    /**
     * @param libraryUuid 要设置的libraryUuid
     */
    public void setLibraryUuid(String libraryUuid) {
        this.libraryUuid = libraryUuid;
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
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType 要设置的contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

//    /**
//     * @return the content
//     */
//    @Override
//    public String getContent() {
//        return content;
//    }
//
//    /**
//     * @param content 要设置的content
//     */
//    @Override
//    public void setContent(String content) {
//        this.content = content;
//    }
}
