/*
 * @(#)Jan 31, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.personal.document.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.personal.document.support.PersonalDocumentUtils;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.file.query.api.DmsFileQuery;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.lang.StringUtils;
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
 * Jan 31, 2018.1	zhulh		Jan 31, 2018		Create
 * </pre>
 * @date Jan 31, 2018
 */
@Component
public class PersonalDocumentLatestDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "个人文档——最新文档";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        return dmsFileServiceFacade.createFileQuery().getCriteriaMetadata();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        DmsFileQuery dmsFileQuery = preQuery(queryContext);
        dmsFileQuery.order(queryContext.getOrderString());
        dmsFileQuery.setFirstResult(queryContext.getPagingInfo().getFirst());
        dmsFileQuery.setMaxResults(queryContext.getPagingInfo().getPageSize());
        return dmsFileQuery.list();
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
    private DmsFileQuery preQuery(QueryContext queryContext) {
        Map<String, Object> values = queryContext.getQueryParams();
        String folderUuid = (String) values.get("folderUuid");
        if (StringUtils.isBlank(folderUuid)) {
            folderUuid = PersonalDocumentUtils.getMyFolderUuid();
        }
        DmsFileQuery dmsFileQuery = dmsFileServiceFacade.createFileQuery();
        dmsFileQuery.setFolderUuid(folderUuid);
        dmsFileQuery.setListFileAction(FileActions.LIST_ALL_FILES);
        dmsFileQuery.where(queryContext.getWhereSqlString(), values);
        return dmsFileQuery;
    }

}
