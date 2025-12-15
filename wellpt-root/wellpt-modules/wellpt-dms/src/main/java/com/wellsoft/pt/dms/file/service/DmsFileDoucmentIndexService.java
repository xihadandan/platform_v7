/*
 * @(#)6/3/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.service;

import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.file.index.DmsFileDocumentIndex;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.fulltext.request.IndexRequestParams;
import com.wellsoft.pt.fulltext.service.DocumentIndexService;

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
public interface DmsFileDoucmentIndexService extends DocumentIndexService<DmsFileDocumentIndex> {

    /**
     * 建立文件索引
     *
     * @param fileEntity
     */
    void index(DmsFileEntity fileEntity);

    /**
     * 建立文件索引
     *
     * @param fileEntity
     */
    void index(DmsFileEntity fileEntity, boolean async);


    /**
     * 建立文件索引
     *
     * @param fileEntity
     * @param fileId
     */
    void index(DmsFileEntity fileEntity, String fileId);

    /**
     * 建立文件索引
     *
     * @param fileEntity
     * @param fileId
     * @param async
     */
    void index(DmsFileEntity fileEntity, String fileId, boolean async);

    /**
     * 建立文件索引
     *
     * @param fileEntity
     * @param dyFormData
     */
    void index(DmsFileEntity fileEntity, DyFormData dyFormData);

    /**
     * 建立文件索引
     *
     * @param fileEntity
     * @param dyFormData
     * @param async
     */
    void index(DmsFileEntity fileEntity, DyFormData dyFormData, boolean async);

    /**
     * 逻辑删除文件索引
     *
     * @param fileEntity
     */
    void logicDelete(DmsFileEntity fileEntity);

    /**
     * @param libraryUuid
     * @return
     */
    boolean isEnableFulltextIndex(String libraryUuid);

    /**
     * 全文检索
     *
     * @param keyword
     * @param libraryUuid
     * @return
     */
    QueryData query(String keyword, String libraryUuid);

    /**
     * 全文检索
     *
     * @param requestParams
     * @param libraryUuid
     * @return
     */
    QueryData query(IndexRequestParams requestParams, String libraryUuid);

    /**
     * @param system
     */
    void deleteBySystem(String system);
}
