/*
 * @(#)Jan 31, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.query.api;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.query.Query;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 31, 2018.1	zhulh		Jan 31, 2018		Create
 * </pre>
 * @date Jan 31, 2018
 */
public interface DmsFileQuery extends Query<DmsFileQuery, QueryItem> {

    void setFolderUuid(String folderUuid);

    void setUuidPath(String uuidPath);

    void setKeyword(String keyword);

    void setListFileAction(String listFileAction);

    /**
     * @param projectionClause
     */
    DmsFileQuery projection(String projectionClause);

    /**
     * @param string
     */
    DmsFileQuery join(String joinClause);

    DmsFileQuery where(String whereSql, Map<String, Object> params);

    DmsFileQuery order(String order);

    CriteriaMetadata getCriteriaMetadata();

    /**
     * @return
     */
    String getFileQueryName();

    /**
     * @return
     */
    Map<String, Object> getQueryParams();

    /**
     * 加载可访问的夹UUID
     *
     * @param folderUuid
     * @param values
     */
    void loadReadFolderAndFileFolderUuids(String folderUuid, Map<String, Object> values);

}
