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
import com.wellsoft.pt.dms.file.query.api.DmsFileRecentVisitQuery;
import com.wellsoft.pt.dms.support.FileHelper;
import com.wellsoft.pt.dms.support.FileStatus;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.query.AbstractQuery;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
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
public class DmsFileRecentVisitQueryImpl extends AbstractQuery<DmsFileRecentVisitQuery, QueryItem> implements
        DmsFileRecentVisitQuery {

    // 夹UUID
    private String folderUuid;

    /**
     * @return the folderUuid
     */
    public String getFolderUuid() {
        return folderUuid;
    }

    /**
     * @param folderUuid 要设置的folderUuid
     */
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
        return this.nativeDao.countByNamedQuery(getFileMyShareInfoQuery(), getValues());
    }

    /**
     * @return
     */
    private String getFileMyShareInfoQuery() {
        if (StringUtils.isBlank(folderUuid)) {
            return "dmsFileRecentVisitQuery";
        }
        return "dmsFileRecentVisitListFolderAndFilesQuery";
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
        String orderString = (String) getValues().get("orderString");
        if (StringUtils.isBlank(orderString)) {
            orderString = StringUtils.EMPTY;
        }
        String folderOrderString = StringUtils.replace(orderString, "t.", "f1.");
        String fileOrderString = StringUtils.replace(folderOrderString, "f1.name", "f1.file_name");
        fileOrderString = StringUtils.replace(fileOrderString, "f1.read_time", "read_time");
        folderOrderString = StringUtils.replace(folderOrderString, "f1.read_time", "read_time");
        folderOrderString = StringUtils.replace(folderOrderString, "f1.file_size", "file_size");
        folderOrderString = StringUtils.replace(folderOrderString, "f1.data_def_uuid", "data_def_uuid");
        folderOrderString = StringUtils.replace(folderOrderString, "data_uuid", "data_uuid");
        values.put("fileOrderString", fileOrderString);
        values.put("folderOrderString", folderOrderString);
        return this.nativeDao.namedQuery(getFileMyShareInfoQuery(), getValues(), itemClass, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#uniqueResult()
     */
    @Override
    public QueryItem uniqueResult() {
        return this.nativeDao.findUniqueByNamedQuery(getFileMyShareInfoQuery(), getValues(), QueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileRecentVisitQuery#where(java.lang.String, java.util.Map)
     */
    @Override
    public DmsFileRecentVisitQuery where(String whereSql, Map<String, Object> params) {
        addParameter("whereSql", whereSql);
        for (Entry<String, Object> entry : params.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileRecentVisitQuery#order(java.lang.String)
     */
    @Override
    public DmsFileRecentVisitQuery order(String order) {
        addParameter("orderString", order);
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileRecentVisitQuery#getCriteriaMetadata()
     */
    @Override
    public CriteriaMetadata getCriteriaMetadata() {
        CriteriaMetadata criteriaMetadata = FileHelper.getCriteriaMetadata();
        criteriaMetadata.add("readTime", "t.read_time", "阅读时间", Date.class);
        return criteriaMetadata;
    }

    /**
     * @return
     */
    private Map<String, Object> getValues() {
        values.put("folderUuid", folderUuid);
        values.put("fileDataType", DmsFileEntity.class.getCanonicalName());
        values.put("folderDataType", DmsFolderEntity.class.getCanonicalName());
        values.put("status", FileStatus.NORMAL);
        values.put("ownerId", SpringSecurityUtils.getCurrentUserId());
        return values;
    }

}
