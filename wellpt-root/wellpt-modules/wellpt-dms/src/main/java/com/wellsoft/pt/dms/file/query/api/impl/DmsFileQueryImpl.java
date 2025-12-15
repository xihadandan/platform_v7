/*
 * @(#)Jan 31, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.query.api.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.file.query.api.DmsFileQuery;
import com.wellsoft.pt.dms.file.service.DmsFileActionService;
import com.wellsoft.pt.dms.model.DmsFileAction;
import com.wellsoft.pt.dms.support.FileHelper;
import com.wellsoft.pt.dms.support.FileQueryTemplateUtils;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.query.AbstractQuery;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
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
 * Jan 31, 2018.1	zhulh		Jan 31, 2018		Create
 * </pre>
 * @date Jan 31, 2018
 */
@Service
@Scope(value = "prototype")
public class DmsFileQueryImpl extends AbstractQuery<DmsFileQuery, QueryItem> implements DmsFileQuery {

    private static final Map<String, String> listFileActionQueryNameMap = new HashMap<String, String>();
    // 查询表单文件视图
    private static final String LIST_ALL_DYFORM_FILES_QUERY = "dmsListAllDyformFilesQuery";
    private static final String LIST_FOLDER_QUERY = "dmsListFolderQuery";
    private static final String LIST_ALL_FOLDER_QUERY = "dmsListAllFolderQuery";
    private static final String LIST_FILES_QUERY = "dmsListFilesQuery";
    private static final String LIST_ALL_FILES_QUERY = "dmsListAllFilesQuery";
    private static final String LIST_FOLDER_AND_FILESQUERY = "dmsListFolderAndFilesQuery";
    private static final String LIST_ALL_FOLDER_AND_FILES_QUERY = "dmsListAllFolderAndFilesQuery";

    static {
        listFileActionQueryNameMap.put(FileActions.LIST_FOLDER, LIST_FOLDER_QUERY);
        listFileActionQueryNameMap.put(FileActions.LIST_ALL_FOLDER, LIST_ALL_FOLDER_QUERY);
        listFileActionQueryNameMap.put(FileActions.LIST_FILES, LIST_FILES_QUERY);
        listFileActionQueryNameMap.put(FileActions.LIST_ALL_FILES, LIST_ALL_FILES_QUERY);
        listFileActionQueryNameMap.put(FileActions.LIST_FOLDER_AND_FILES, LIST_FOLDER_AND_FILESQUERY);
        listFileActionQueryNameMap.put(FileActions.LIST_ALL_FOLDER_AND_FILES, LIST_ALL_FOLDER_AND_FILES_QUERY);
    }

    private String folderUuid;
    private String uuidPath;
    private String keyword;
    private String listFileAction;
    private String formId;
    private String whereSql;
    private String order;
    private String fileQueryName;
    @Autowired
    private DmsFileActionService dmsFileActionService;
    @Autowired
    private OrgFacadeService orgApiFacade;
    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#count()
     */
    @Override
    @Transactional(readOnly = true)
    public long count() {
        String fileQueryName = getFileQueryName();
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        values.put("folderUuid", folderUuid);
        values.put("uuidPath", uuidPath);
        values.put("whereSql", whereSql);
        return this.nativeDao.countByNamedQuery(fileQueryName, values);
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
    @Transactional(readOnly = true)
    public <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass) {
        String fileQueryName = getFileQueryName();
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        values.put("unit_in_expression_org_id", SpringSecurityUtils.getCurrentUserId());
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        values.put("folderUuid", folderUuid);
        values.put("uuidPath", uuidPath);
        values.put("whereSql", whereSql);
        values.put("formId", formId);
        String folderOrderString = StringUtils.replace(order, "t.", "f1.");
        String fileOrderString = StringUtils.replace(folderOrderString, "f1.name", "f1.file_name");
        folderOrderString = StringUtils.replace(folderOrderString, "f1.file_size", "file_size");
        folderOrderString = StringUtils.replace(folderOrderString, "f1.data_def_uuid", "data_def_uuid");
        folderOrderString = StringUtils.replace(folderOrderString, "data_uuid", "data_uuid");
        values.put("fileOrderString", fileOrderString);
        values.put("folderOrderString", folderOrderString);
        values.put("orderString", order);
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setPageSize(maxResults);
        pagingInfo.setFirst(firstResult);
        pagingInfo.setAutoCount(false);
        return this.nativeDao.namedQuery(fileQueryName, values, itemClass, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#uniqueResult()
     */
    @Override
    @Transactional(readOnly = true)
    public QueryItem uniqueResult() {
        String fileQueryName = getFileQueryName();
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        values.put("unit_in_expression_org_id", SpringSecurityUtils.getCurrentUserId());
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        values.put("whereSql", whereSql);
        return this.nativeDao.findUniqueByNamedQuery(fileQueryName, values, QueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileQuery#getCriteriaMetadata()
     */
    @Override
    public CriteriaMetadata getCriteriaMetadata() {
        return FileHelper.getCriteriaMetadata();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileQuery#setFolderUuid(java.lang.String)
     */
    @Override
    public void setFolderUuid(String folderUuid) {
        this.folderUuid = folderUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileQuery#setUuidPath(java.lang.String)
     */
    @Override
    public void setUuidPath(String uuidPath) {
        this.uuidPath = uuidPath;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileQuery#setKeyword(java.lang.String)
     */
    @Override
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileQuery#setListFileAction(java.lang.String)
     */
    @Override
    public void setListFileAction(String listFileAction) {
        this.listFileAction = listFileAction;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileQuery#setWhereSql(java.lang.String)
     */
    @Override
    public DmsFileQuery where(String whereSql, Map<String, Object> params) {
        this.whereSql = whereSql;
        addParameter("whereSql", whereSql);
        if (MapUtils.isNotEmpty(params)) {
            for (Entry<String, Object> entry : params.entrySet()) {
                addParameter(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    /**
     * @param order 要设置的order
     */
    public DmsFileQuery order(String order) {
        this.order = order;
        return this;
    }

    /**
     * @param folderUuid
     * @return
     */
    @Override
    public String getFileQueryName() {
        // 表单视图的文件查询
        if (StringUtils.isNotBlank(fileQueryName)) {
            return fileQueryName;
        }
        String queryName = getQueryName();
        if (StringUtils.isBlank(keyword)) {
            return queryName;
        }
        // 如果是关键字查询，返回对应可以查看所有夹、文件的命名查询
        String listFolderQueryName = listFileActionQueryNameMap.get(FileActions.LIST_FOLDER);
        String listFilesQueryName = listFileActionQueryNameMap.get(FileActions.LIST_FILES);
        String listFolderAndFilesQueryName = listFileActionQueryNameMap.get(FileActions.LIST_FOLDER_AND_FILES);
        if (StringUtils.equals(listFolderQueryName, queryName)) {
            queryName = listFileActionQueryNameMap.get(FileActions.LIST_ALL_FOLDER);
        } else if (StringUtils.equals(listFilesQueryName, queryName)) {
            queryName = listFileActionQueryNameMap.get(FileActions.LIST_ALL_FILES);
        } else if (StringUtils.equals(listFolderAndFilesQueryName, queryName)) {
            queryName = listFileActionQueryNameMap.get(FileActions.LIST_ALL_FOLDER_AND_FILES);
        }
        return queryName;
    }

    public void setFileQueryName(String fileQueryName) {
        this.fileQueryName = fileQueryName;
    }

    /**
     * @return
     */
    private String getQueryName() {
        if (StringUtils.isNotBlank(listFileAction)) {
            return listFileActionQueryNameMap.get(listFileAction);
        }

        // values有设置命名查询的名称，直接返回
        String fileQueryName = (String) values.get("_fileQueryName");
        if (StringUtils.isNotBlank(fileQueryName)) {
            return fileQueryName;
        }

        String listFileMode = (String) values.get("listFileMode");
        // 获取夹操作
        List<DmsFileAction> dmsFileActions = dmsFileActionService.getFolderActions(folderUuid);
        Map<String, DmsFileAction> fileActionMap = ConvertUtils.convertElementToMap(dmsFileActions, "id");
        // 返回设置获取夹的方式
        if (StringUtils.isNotBlank(listFileMode) && fileActionMap.containsKey(listFileMode)) {
            return listFileActionQueryNameMap.get(listFileMode);
        }
        // 自动获取列出夹的方式
        // 列出当前夹下的子夹及文件(包含子夹)
        if (fileActionMap.containsKey(FileActions.LIST_ALL_FOLDER_AND_FILES)) {
            fileQueryName = listFileActionQueryNameMap.get(FileActions.LIST_ALL_FOLDER_AND_FILES);
        } else if (fileActionMap.containsKey(FileActions.LIST_FOLDER_AND_FILES)) {
            // 列出当前夹下的子夹及文件
            fileQueryName = listFileActionQueryNameMap.get(FileActions.LIST_FOLDER_AND_FILES);
        } else if (fileActionMap.containsKey(FileActions.LIST_ALL_FILES)) {
            // 列出当前夹下的文件(包含子夹)
            fileQueryName = listFileActionQueryNameMap.get(FileActions.LIST_ALL_FILES);
        } else if (fileActionMap.containsKey(FileActions.LIST_FILES)) {
            // 列出当前夹下的文件
            fileQueryName = listFileActionQueryNameMap.get(FileActions.LIST_FILES);
        } else if (fileActionMap.containsKey(FileActions.LIST_ALL_FOLDER)) {
            // 列出当前夹下的所有子夹(包含子夹)
            fileQueryName = listFileActionQueryNameMap.get(FileActions.LIST_ALL_FOLDER);
        } else if (fileActionMap.containsKey(FileActions.LIST_FOLDER)) {
            // 列出当前夹下的所有子夹(包含子夹)
            fileQueryName = listFileActionQueryNameMap.get(FileActions.LIST_FOLDER);
        }
        return fileQueryName;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileQuery#projection(java.lang.String)
     */
    @Override
    public DmsFileQuery projection(String projectionClause) {
        addParameter("projectionClause", projectionClause);
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileQuery#join(java.lang.String)
     */
    @Override
    public DmsFileQuery join(String joinClause) {
        addParameter("joinClause", joinClause);
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileQuery#getQueryParams()
     */
    @Override
    public Map<String, Object> getQueryParams() {
        return this.values;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.query.api.DmsFileQuery#loadReadFolderAndFileFolderUuids(java.lang.String, java.util.Map)
     */
    @Override
    public void loadReadFolderAndFileFolderUuids(String folderUuid, Map<String, Object> values) {
        String fileQueryName = this.getFileQueryName();
        if (StringUtils.isBlank(fileQueryName)) {
            return;
        }
        switch (fileQueryName) {
            case LIST_ALL_DYFORM_FILES_QUERY:
                // 添加可访问文件的夹UUID列表参数
                addReadAllFileFolderUuids(folderUuid, values);
                break;
            case LIST_FOLDER_QUERY:
                // 添加可访问当前夹下的夹UUID列表参数
                addReadFolderUuids(folderUuid, values);
                break;
            case LIST_ALL_FOLDER_QUERY:
                // 添加可访问所有夹UUID列表参数
                addReadAllFolderUuids(folderUuid, values);
                break;
            case LIST_FILES_QUERY:
                // 列出当前夹下的文件可忽略处理
                break;
            case LIST_ALL_FILES_QUERY:
                // 添加可访问文件的夹UUID列表参数
                addReadAllFileFolderUuids(folderUuid, values);
                break;
            case LIST_FOLDER_AND_FILESQUERY:
                // 添加可访问当前夹下的夹UUID列表参数，当前夹下的文件可忽略处理
                addReadFolderUuids(folderUuid, values);
                break;
            case LIST_ALL_FOLDER_AND_FILES_QUERY:
                // 添加可访问所有夹、文件的夹UUID列表参数
                addReadAllFolderAndFileFolderUuids(folderUuid, values);
                break;
            default:
                break;
        }
    }

    /**
     * 添加可访问当前夹下的夹UUID列表参数
     *
     * @param folderUuid
     * @param values
     */
    @SuppressWarnings("unchecked")
    private void addReadFolderUuids(String folderUuid, Map<String, Object> values) {
        Map<String, Object> queryParams = this.getQueryParams();
        List<String> readFolderUuids = (List<String>) values.get("readFolderUuids");
        if (readFolderUuids == null) {
            readFolderUuids = this.dmsFileServiceFacade.listReadFolderUuid(folderUuid, queryParams);
            if (CollectionUtils.isEmpty(readFolderUuids)) {
                readFolderUuids = Lists.newArrayList("-1");
            }
            values.put("readFolderUuids", readFolderUuids);
        }
        if (CollectionUtils.isNotEmpty(readFolderUuids)) {
            queryParams.put("readFolderUuids", readFolderUuids);
            if (CollectionUtils.size(readFolderUuids) > 1000) {
                queryParams.put("queryReadFolderUuidSql",
                        FileHelper.generateQueryFolderUuidSql(readFolderUuids, queryParams));
            }
        }
    }

    /**
     * 添加可访问文件的夹UUID列表参数
     *
     * @param folderUuid
     * @param values
     */
    @SuppressWarnings("unchecked")
    private void addReadAllFileFolderUuids(String folderUuid, Map<String, Object> values) {
        Map<String, Object> queryParams = this.getQueryParams();
        List<String> readAllFileFolderUuids = (List<String>) values.get("readAllFileFolderUuids");
        if (false == queryParams.containsKey("folderUuid")) {
            queryParams.put("folderUuid", folderUuid);
        }
        if (readAllFileFolderUuids == null) {
            readAllFileFolderUuids = this.dmsFileServiceFacade.listReadAllFileFolderUuid(folderUuid, queryParams);
            if (CollectionUtils.isEmpty(readAllFileFolderUuids)) {
                readAllFileFolderUuids = Lists.newArrayList("-1");
            }
            values.put("readAllFileFolderUuids", readAllFileFolderUuids);
        }
        if (CollectionUtils.isNotEmpty(readAllFileFolderUuids)) {
            queryParams.put("readAllFileFolderUuids", readAllFileFolderUuids);
            if (CollectionUtils.size(readAllFileFolderUuids) > 1000) {
                queryParams.put("queryReadAllFileFolderUuidSql",
                        FileHelper.generateQueryFolderUuidSql(readAllFileFolderUuids, queryParams));
            }
        }
    }

    /**
     * 添加可访问所有夹UUID列表参数
     *
     * @param folderUuid
     * @param values
     */
    @SuppressWarnings("unchecked")
    private void addReadAllFolderUuids(String folderUuid, Map<String, Object> values) {
        Map<String, Object> queryParams = this.getQueryParams();
        if (false == queryParams.containsKey("folderUuid")) {
            queryParams.put("folderUuid", folderUuid);
        }
        List<String> readAllFolderUuids = (List<String>) values.get("readAllFolderUuids");
        if (readAllFolderUuids == null) {
            readAllFolderUuids = this.dmsFileServiceFacade.listReadAllFolderUuid(folderUuid, queryParams);
            if (CollectionUtils.isEmpty(readAllFolderUuids)) {
                readAllFolderUuids = Lists.newArrayList("-1");
            }
            values.put("readAllFolderUuids", readAllFolderUuids);
        }
        if (CollectionUtils.isNotEmpty(readAllFolderUuids)) {
            queryParams.put("readAllFolderUuids", readAllFolderUuids);
            if (CollectionUtils.size(readAllFolderUuids) > 1000) {
                queryParams.put("queryReadAllFolderUuidSql",
                        FileHelper.generateQueryFolderUuidSql(readAllFolderUuids, queryParams));
            }
        }
    }

    /**
     * 添加可访问所有夹、文件的夹UUID列表参数
     *
     * @param folderUuid
     * @param values
     */
    @SuppressWarnings("unchecked")
    private void addReadAllFolderAndFileFolderUuids(String folderUuid, Map<String, Object> values) {
        Map<String, Object> queryParams = this.getQueryParams();
        if (false == queryParams.containsKey("folderUuid")) {
            queryParams.put("folderUuid", folderUuid);
        }
        List<String> readAllFolderUuids = (List<String>) values.get("readAllFolderUuids");
        List<String> readAllFileFolderUuids = (List<String>) values.get("readAllFileFolderUuids");
        if (readAllFolderUuids == null || readAllFileFolderUuids == null) {
            Map<String, List<String>> folderUuidMap = this.dmsFileServiceFacade.listReadAllFolderAndFileFolderUuid(
                    folderUuid, queryParams);
            readAllFolderUuids = folderUuidMap.get("folder");
            readAllFileFolderUuids = folderUuidMap.get("file");
            if (CollectionUtils.isEmpty(readAllFolderUuids)) {
                readAllFolderUuids = Lists.newArrayList("-1");
            }
            if (CollectionUtils.isEmpty(readAllFileFolderUuids)) {
                readAllFileFolderUuids = Lists.newArrayList("-1");
            }
            values.put("readAllFolderUuids", readAllFolderUuids);
            values.put("readAllFileFolderUuids", readAllFileFolderUuids);
        }
        if (CollectionUtils.isNotEmpty(readAllFolderUuids)) {
            queryParams.put("readAllFolderUuids", readAllFolderUuids);
            if (CollectionUtils.size(readAllFolderUuids) > 1000) {
                queryParams.put("queryReadAllFolderUuidSql",
                        FileHelper.generateQueryFolderUuidSql(readAllFolderUuids, queryParams, "readAllFolder"));
            }
        }
        if (CollectionUtils.isNotEmpty(readAllFileFolderUuids)) {
            queryParams.put("readAllFileFolderUuids", readAllFileFolderUuids);
            if (CollectionUtils.size(readAllFileFolderUuids) > 1000) {
                queryParams.put("queryReadAllFileFolderUuidSql",
                        FileHelper.generateQueryFolderUuidSql(readAllFileFolderUuids, queryParams, "readAllFile"));
            }
        }
    }

}
