/*
 * @(#)Jan 10, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.service.impl;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.file.service.DmsFileActionService;
import com.wellsoft.pt.dms.file.service.DmsFileQueryService;
import com.wellsoft.pt.dms.model.DmsFileAction;
import com.wellsoft.pt.dms.support.FileQueryTemplateUtils;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
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
 * Jan 10, 2018.1	zhulh		Jan 10, 2018		Create
 * </pre>
 * @date Jan 10, 2018
 */
@Service
@Transactional(readOnly = true)
public class DmsFileQueryServiceImpl extends BaseServiceImpl implements DmsFileQueryService {

    private static final Map<String, String> listFileActionQueryNameMap = new HashMap<String, String>();

    static {
        listFileActionQueryNameMap.put(FileActions.LIST_FOLDER, "dmsListFolderQuery");
        listFileActionQueryNameMap.put(FileActions.LIST_ALL_FOLDER, "dmsListAllFolderQuery");
        listFileActionQueryNameMap.put(FileActions.LIST_FILES, "dmsListFilesQuery");
        listFileActionQueryNameMap.put(FileActions.LIST_ALL_FILES, "dmsListAllFilesQuery");
        listFileActionQueryNameMap.put(FileActions.LIST_FOLDER_AND_FILES, "dmsListFolderAndFilesQuery");
        listFileActionQueryNameMap.put(FileActions.LIST_ALL_FOLDER_AND_FILES, "dmsListAllFolderAndFilesQuery");
    }

    @Autowired
    private DmsFileActionService dmsFileActionService;
    @Autowired
    private OrgFacadeService orgApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileQueryService#query(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        Map<String, Object> values = context.getQueryParams();
        String folderUuid = (String) values.get("folderUuid");
        String fileQueryName = getFileQueryName(folderUuid, context);
        if (StringUtils.isBlank(fileQueryName)) {
            return Collections.emptyList();
        }
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        String whereSql = context.getWhereSqlString();
        if (!values.containsKey("folderUuid")) {
            values.put("folderUuid", "-1");
        }
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        values.put("whereSql", whereSql);
        values.put("orderString", context.getOrderString());
        return this.nativeDao.namedQuery(fileQueryName, values, QueryItem.class, context.getPagingInfo());
    }

    /**
     * @param folderUuid
     * @return
     */
    private String getFileQueryName(String folderUuid, QueryContext context) {
        String fileQueryName = (String) context.getQueryParams().get("_fileQueryName");
        String listFileMode = (String) context.getQueryParams().get("listFileMode");
        if (StringUtils.isNotBlank(fileQueryName)) {
            return fileQueryName;
        }
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
        // 临时存放命名查询
        context.getQueryParams().put("_fileQueryName", fileQueryName);
        return fileQueryName;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.file.service.DmsFileQueryService#count(com.wellsoft.pt.core.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext context) {
        Map<String, Object> values = context.getQueryParams();
        String folderUuid = (String) values.get("folderUuid");
        String fileQueryName = getFileQueryName(folderUuid, context);
        if (StringUtils.isBlank(fileQueryName)) {
            return 0;
        }

        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(orgApiFacade, currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        String whereSql = context.getWhereSqlString();
        values.put("unit_in_expression_org_id", currUserId);
        values.put("userOrgIdsTemplate", userOrgIdsTemplate);
        values.put("orgIds", fileQueryTemplate.getOrgIds());
        values.put("roleIds", fileQueryTemplate.getRoleIds());
        values.put("sids", fileQueryTemplate.getSids());
        values.put("whereSql", whereSql);
        return this.nativeDao.countByNamedQuery(fileQueryName, context.getQueryParams());
    }

}
