/*
 * @(#)Feb 1, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.query.api.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.file.query.api.DmsFileRecycleBinQuery;
import com.wellsoft.pt.dms.support.FileHelper;
import com.wellsoft.pt.dms.support.FileStatus;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.query.AbstractQuery;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
@Service
@Transactional(readOnly = true)
@Scope(value = "prototype")
public class DmsFileRecycleBinQueryImpl extends AbstractQuery<DmsFileRecycleBinQuery, QueryItem> implements
        DmsFileRecycleBinQuery {

    // 夹UUID
    private String folderUuid;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileRecycleBinQuery#setFolderUuid(java.lang.String)
     */
    @Override
    public void setFolderUuid(String folderUuid) {
        this.folderUuid = folderUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#count()
     */
    @Override
    public long count() {
        return this.nativeDao.countByNamedQuery("dmsFileRecycleBinQuery", getValues());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#list()
     */
    @Override
    public List<QueryItem> list() {
        return list(QueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#list(java.lang.Class)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass) {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setPageSize(maxResults);
        pagingInfo.setFirst(firstResult);
        pagingInfo.setAutoCount(false);
        return this.nativeDao.namedQuery("dmsFileRecycleBinQuery", getValues(), itemClass, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#uniqueResult()
     */
    @Override
    public QueryItem uniqueResult() {
        return this.nativeDao.findUniqueByNamedQuery("dmsFileRecycleBinQuery", getValues(), QueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileRecycleBinQuery#where(java.lang.String, java.util.Map)
     */
    @Override
    public DmsFileRecycleBinQuery where(String whereSql, Map<String, Object> params) {
        addParameter("whereSql", whereSql);
        for (Entry<String, Object> entry : params.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileRecycleBinQuery#order(java.lang.String)
     */
    @Override
    public DmsFileRecycleBinQuery order(String order) {
        addParameter("orderString", order);
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileRecycleBinQuery#getCriteriaMetadata()
     */
    @Override
    public CriteriaMetadata getCriteriaMetadata() {
        return FileHelper.getCriteriaMetadata();
    }

    /**
     * @return
     */
    private Map<String, Object> getValues() {
        values.put("folderUuid", folderUuid);
        values.put("fileDataType", DmsFileEntity.class.getCanonicalName());
        values.put("folderDataType", DmsFolderEntity.class.getCanonicalName());
        values.put("status", FileStatus.DELETE);
        values.put("userId", SpringSecurityUtils.getCurrentUserId());
        return values;
    }

}
