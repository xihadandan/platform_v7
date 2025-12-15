/*
 * @(#)Jan 10, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.store;

import com.google.common.base.CaseFormat;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreColumn;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.file.query.api.DmsFileQuery;
import com.wellsoft.pt.dms.file.service.DmsFileQueryService;
import com.wellsoft.pt.dms.service.DmsFolderConfigurationService;
import com.wellsoft.pt.dms.service.DmsFolderService;
import com.wellsoft.pt.dms.support.FileHelper;
import com.wellsoft.pt.dms.support.FileStatus;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 数据管理_文件库_数据仓库
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 10, 2018.1	zhulh		Jan 10, 2018		Create
 * </pre>
 * @date Jan 10, 2018
 */
@Component
public class DmsFileDataStoreQuery extends AbstractDataStoreQueryInterface implements DmsFileQueryInterface {

    @Autowired
    private DmsFileQueryService dmsFileQueryService;

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    @Autowired
    private DmsFolderConfigurationService dmsFolderConfigurationService;

    @Autowired
    private DmsFolderService dmsFolderService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "数据管理_文件库";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = FileHelper.getCriteriaMetadata();

        // 添加左连接的查询表
        DmsFileDataStoreInterfaceParam interfaceParam = queryContext.interfaceParam(DmsFileDataStoreInterfaceParam.class);
        this.addJoinDmCriteriaMetadata(criteriaMetadata, interfaceParam, queryContext);
        return criteriaMetadata;
    }

    /**
     * @param criteriaMetadata
     * @param interfaceParam
     * @param queryContext
     * @return
     */
    private void addJoinDmCriteriaMetadata(CriteriaMetadata criteriaMetadata, DmsFileDataStoreInterfaceParam interfaceParam,
                                           QueryContext queryContext) {
        if (interfaceParam == null) {
            return;
        }

        DmsFileLeftJoinConfig leftJoinConfig = interfaceParam.getLeftJoinConfig();
        if (leftJoinConfig == null || !leftJoinConfig.getEnabled()) {
            return;
        }

        List<DataStoreColumn> dataStoreColumns = leftJoinConfig.getSelectionColumns();
        for (DataStoreColumn dataStoreColumn : dataStoreColumns) {
            String columnIndex = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, dataStoreColumn.getColumnIndex());
            criteriaMetadata.add(columnIndex, dataStoreColumn.getColumnName(),
                    dataStoreColumn.getTitle(), dataStoreColumn.getDataType(), dataStoreColumn.getColumnType());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        DmsFileQuery dmsFileQuery = preQuery(queryContext);
        String fileQueryName = dmsFileQuery.getFileQueryName();
        if (StringUtils.isBlank(fileQueryName)) {
            return Collections.emptyList();
        }
        queryContext.getQueryParams().put("_fileQueryName", fileQueryName);

        dmsFileQuery.order(queryContext.getOrderString());
        dmsFileQuery.setFirstResult(queryContext.getPagingInfo().getFirst());
        dmsFileQuery.setMaxResults(queryContext.getPagingInfo().getPageSize());
        List<QueryItem> queryItems = dmsFileQuery.list();

        // 回收站查询，返回源路径
        if (BooleanUtils.isTrue((Boolean) queryContext.getQueryParams().get("recycleBin"))) {
            Set<String> sourcePaths = queryItems.stream().map(item -> item.getString("folderUuid")).collect(Collectors.toSet());
            Map<String, String> folderPathNameMap = dmsFolderService.listFolderPathNameAsMap(sourcePaths);
            queryItems.forEach(item -> {
                item.put("sourcePath", folderPathNameMap.get(item.getString("folderUuid")));
            });
        }

        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.QueryInterface#count(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        DmsFileQuery dmsFileQuery = preQuery(queryContext);
        String fileQueryName = dmsFileQuery.getFileQueryName();
        if (StringUtils.isBlank(fileQueryName)) {
            return 0;
        }
        queryContext.getQueryParams().put("_fileQueryName", fileQueryName);
        return dmsFileQuery.count();
    }

    /**
     * @param queryContext
     * @return
     */
    protected DmsFileQuery preQuery(QueryContext queryContext) {
        Map<String, Object> values = queryContext.getQueryParams();
        String folderUuid = (String) values.get("folderUuid");
        if (StringUtils.isBlank(folderUuid)) {
            folderUuid = "-1";
        }
        String uuidPath = null;
        DmsFolderEntity dmsFolderEntity = dmsFolderService.get(folderUuid);
        if (dmsFolderEntity != null) {
            uuidPath = dmsFolderEntity.getUuidPath();
        }

        // 左关联数据模型查询
        DmsFileDataStoreInterfaceParam param = queryContext.interfaceParam(DmsFileDataStoreInterfaceParam.class);
        String joinDmSelectionClause = this.getJoinDmTableProjectionClause(param);
        String joinDmTableClause = this.getJoinDmTableClause(param);
        values.put("joinProjectionClause", joinDmSelectionClause);
        values.put("joinTableClause", joinDmTableClause);

        // 加入回收站查询
        joinRecycleBinIfRequired(values);

        // 关键字查询
        String keyword = (String) values.get("keyword");
        DmsFileQuery dmsFileQuery = dmsFileServiceFacade.createFileQuery();
        dmsFileQuery.setFolderUuid(folderUuid);
        dmsFileQuery.setUuidPath(uuidPath);
        dmsFileQuery.setKeyword(keyword);
        dmsFileQuery.where(queryContext.getWhereSqlString(), values);
        dmsFileQuery.setListFileAction((String) values.get("listFileMode"));
//        dmsFileQuery.setFormId(formId);
//        // 文件表单查询
//        if (StringUtils.isNotBlank(formId)) {
//            preFileDyformQuery(folderUuid, formId, dmsFileQuery, queryContext);
//        }
        // 加载可访问的夹UUID
        // dmsFileQuery.loadReadFolderAndFileFolderUuids(folderUuid, values);
        return dmsFileQuery;
    }

    /**
     * @param values
     */
    private void joinRecycleBinIfRequired(Map<String, Object> values) {
        if (BooleanUtils.isNotTrue((Boolean) values.get("recycleBin"))) {
            return;
        }

        values.put("status", FileStatus.DELETE);
        String joinProjectionClause = Objects.toString(values.get("joinProjectionClause"), StringUtils.EMPTY);
        String joinRecycleBinProjectionClause = " t.folder_uuid as sourcePath, r.create_time as deletedTime, r.user_id as deleteUserId, ru.user_name as deleteUserName";
        String joinRecycleBinClause = " inner join dms_recycle_bin r on t.uuid = r.data_uuid left join user_info ru on r.user_id = ru.user_id";
        values.put("joinProjectionClause", StringUtils.isBlank(joinProjectionClause) ? joinRecycleBinProjectionClause : joinProjectionClause + ", " + joinRecycleBinProjectionClause);
        values.put("joinTableClause", Objects.toString(values.get("joinTableClause"), StringUtils.EMPTY) + joinRecycleBinClause);
    }


    /**
     * @return
     */
    private String getJoinDmTableProjectionClause(DmsFileDataStoreInterfaceParam param) {
        String projectionClause = StringUtils.EMPTY;
        if (param == null) {
            return projectionClause;
        }

        DmsFileLeftJoinConfig leftJoinConfig = param.getLeftJoinConfig();
        if (leftJoinConfig == null || !leftJoinConfig.getEnabled()) {
            return projectionClause;
        }

        List<DataStoreColumn> dataStoreColumns = leftJoinConfig.getSelectionColumns();
        projectionClause = dataStoreColumns.stream().map(column -> column.getColumnName() + " as " + column.getColumnIndex())
                .collect(Collectors.joining(", "));
        return projectionClause;
    }

    /**
     * @return
     */
    private String getJoinDmTableClause(DmsFileDataStoreInterfaceParam param) {
        String projectionClause = StringUtils.EMPTY;
        if (param == null) {
            return projectionClause;
        }

        DmsFileLeftJoinConfig leftJoinConfig = param.getLeftJoinConfig();
        if (leftJoinConfig == null || !leftJoinConfig.getEnabled()) {
            return projectionClause;
        }

        StringBuilder sb = new StringBuilder();
        String tableName = leftJoinConfig.getTableName();
        // 数据模型为视图时，使用视图查询的sql
        String tableSql = leftJoinConfig.getTableSql();
        if (StringUtils.isNotBlank(tableSql)) {
            tableName = "(" + tableSql + ")";
        }
        sb.append(" left join ").append(tableName).append(" ").append(leftJoinConfig.getTableAlias())
                .append(" ").append(leftJoinConfig.getOnConditionSql());

        return sb.toString();
    }

    /**
     * 接口参数定义类
     *
     * @return
     */
    @Override
    public Class<? extends InterfaceParam> interfaceParamsClass() {
        return DmsFileDataStoreInterfaceParam.class;
    }

    //    /**
//     * (non-Javadoc)
//     *
//     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#getInterfaceDesc()
//     */
//    @Override
//    public String getInterfaceDesc() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("1、要关联查询文件库表单数据时，请在数据接口参数里面，输入需要查询的表单ID或表单表名(包含表单数据库视图)，例如x1或formId=x1;");
//        sb.append("<br/>");
//        sb.append("2、要查询总数时可指定默认所在夹UUID参数，多个参数以分号隔开，例如x2;folderUuid=x3或formId=x2;folderUuid=x3;");
//        sb.append("<br/>");
//        sb.append("3、添加查询条件时文件库字段用t.字段名，表单字段用dy.字段名。");
//        return sb.toString();
//    }
}
