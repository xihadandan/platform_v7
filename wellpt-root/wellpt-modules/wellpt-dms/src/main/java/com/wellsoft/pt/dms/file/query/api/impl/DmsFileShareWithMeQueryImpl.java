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
import com.wellsoft.pt.dms.file.query.api.DmsFileShareWithMeQuery;
import com.wellsoft.pt.dms.support.FileHelper;
import com.wellsoft.pt.dms.support.FileQueryTemplateUtils;
import com.wellsoft.pt.dms.support.FileStatus;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.query.AbstractQuery;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DmsFileShareWithMeQueryImpl extends AbstractQuery<DmsFileShareWithMeQuery, QueryItem> implements
        DmsFileShareWithMeQuery {

    // 夹UUID
    private String folderUuid;

    @Autowired
    private OrgFacadeService orgApiFacade;

    /**
     * @return the folderUuid
     */
    public String getFolderUuid() {
        return folderUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileShareWithMeQuery#setFolderUuid(java.lang.String)
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
        return this.nativeDao.countByNamedQuery(getFileShareWithMeQuery(), getValues());
    }

    /**
     * @return
     */
    private String getFileShareWithMeQuery() {
        if (StringUtils.isBlank(folderUuid)) {
            return "dmsFileShareWithMeQuery";
        }
        return "dmsFileShareWithMeListFolderAndFilesQuery";
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
        return this.nativeDao.namedQuery(getFileShareWithMeQuery(), getValues(), itemClass, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#uniqueResult()
     */
    @Override
    public QueryItem uniqueResult() {
        return this.nativeDao.findUniqueByNamedQuery(getFileShareWithMeQuery(), getValues(), QueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileShareWithMeQuery#where(java.lang.String, java.util.Map)
     */
    @Override
    public DmsFileShareWithMeQuery where(String whereSql, Map<String, Object> params) {
        addParameter("whereSql", whereSql);
        for (Entry<String, Object> entry : params.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileShareWithMeQuery#order(java.lang.String)
     */
    @Override
    public DmsFileShareWithMeQuery order(String order) {
        addParameter("orderString", order);
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileShareWithMeQuery#getCriteriaMetadata()
     */
    @Override
    public CriteriaMetadata getCriteriaMetadata() {
        CriteriaMetadata criteriaMetadata = FileHelper.getCriteriaMetadata();
        criteriaMetadata.add("ownerId", "t.owner_id", "分享人ID", String.class);
        criteriaMetadata.add("shareUuid", "t.share_uuid", "分享ID", String.class);
        criteriaMetadata.add("shareTime", "t.share_time", "分享时间", Date.class);
        return criteriaMetadata;
    }

    /**
     * @return
     */
    private Map<String, Object> getValues() {
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        values.put("fileDataType", DmsFileEntity.class.getCanonicalName());
        values.put("folderDataType", DmsFolderEntity.class.getCanonicalName());
        values.put("status", FileStatus.NORMAL);
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        return values;
    }

}
