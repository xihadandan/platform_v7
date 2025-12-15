/*
 * @(#)Feb 1, 2018 V1.0
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
 * Feb 1, 2018.1	zhulh		Feb 1, 2018		Create
 * </pre>
 * @date Feb 1, 2018
 */
public interface DmsFileRecentVisitQuery extends Query<DmsFileRecentVisitQuery, QueryItem> {

    /**
     * @param folderUuid
     */
    void setFolderUuid(String folderUuid);

    DmsFileRecentVisitQuery where(String whereSql, Map<String, Object> params);

    DmsFileRecentVisitQuery order(String order);

    CriteriaMetadata getCriteriaMetadata();

}
