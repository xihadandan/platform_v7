/*
 * @(#)Feb 1, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.file.query.api.DmsFileRecentVisitQuery;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
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
@Component
public class DmsFileRecentVisitDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "数据管理_文件库_最近访问";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        return dmsFileServiceFacade.createFileRecentVisitQuery().getCriteriaMetadata();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        DmsFileRecentVisitQuery dmsFileRecentVisitQuery = preQuery(queryContext);
        dmsFileRecentVisitQuery.order(queryContext.getOrderString());
        dmsFileRecentVisitQuery.setFirstResult(queryContext.getPagingInfo().getFirst());
        dmsFileRecentVisitQuery.setMaxResults(queryContext.getPagingInfo().getPageSize());
        return dmsFileRecentVisitQuery.list();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#count(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return preQuery(queryContext).count();
    }

    /**
     * @param queryContext
     * @return
     */
    private DmsFileRecentVisitQuery preQuery(QueryContext queryContext) {
        Map<String, Object> values = queryContext.getQueryParams();
        String folderUuid = (String) values.get("folderUuid");
        DmsFileRecentVisitQuery dmsFileQuery = dmsFileServiceFacade.createFileRecentVisitQuery();
        dmsFileQuery.setFolderUuid(folderUuid);
        dmsFileQuery.where(queryContext.getWhereSqlString(), values);
        return dmsFileQuery;
    }

}
