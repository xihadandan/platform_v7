/*
 * @(#)Jan 16, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.criteria;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfiguration;
import com.wellsoft.pt.dms.config.support.Configuration;
import com.wellsoft.pt.dms.config.support.ConfigurationBuilder;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.file.service.DmsFileActionService;
import com.wellsoft.pt.dms.model.DmsFolderDyformDefinition;
import com.wellsoft.pt.dms.service.DmsFolderConfigurationService;
import com.wellsoft.pt.dms.support.FileQueryTemplateUtils;
import com.wellsoft.pt.jpa.criteria.TableCriteria;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;

import java.util.HashMap;
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
 * Jan 16, 2018.1	zhulh		Jan 16, 2018		Create
 * </pre>
 * @date Jan 16, 2018
 */
public class DmsFileDyformCriteria extends TableCriteria {

    private String folderUuid;
    private String readFileField;
    private String editFileField;
    private boolean enableVersioning;

    public DmsFileDyformCriteria(NativeDao nativeDao, DataStoreParams dataStoreParams,
                                 DataStoreConfiguration dataStoreConfiguration) {
        super(nativeDao, dataStoreConfiguration.getTableName());
        folderUuid = (String) dataStoreParams.getParam("folderUuid");

        DmsFolderConfigurationService dmsFolderConfigurationService = ApplicationContextHolder
                .getBean(DmsFolderConfigurationService.class);
        DmsFolderDyformDefinition dmsFolderConfigurationEntity = dmsFolderConfigurationService
                .getFolderDyformDefinitionByFolderUuid(folderUuid);
        readFileField = dmsFolderConfigurationEntity.getReadFileField();
        editFileField = dmsFolderConfigurationEntity.getEditFileField();
        addQueryParams("folderUuid", folderUuid);
        addQueryParams("readFileField", readFileField);
        addQueryParams("editFileField", editFileField);

        String dmsId = (String) dataStoreParams.getProxy().getExtras().get("dms_id");
        ConfigurationBuilder builder = new ConfigurationBuilder();
        Configuration configuration = builder.buildFromWidgetDefinition(dmsId);
        enableVersioning = configuration.isEnableVersioning();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.TableCriteria#getFromSql()
     */
    @Override
    protected String getFromSql() {
        // 数据源为表单时，查询强、弱关系的数据
        String tableName = getTableName();
        Map<String, Object> root = new HashMap<String, Object>();
        String currUserId = SpringSecurityUtils.getCurrentUserId();
        FileQueryTemplateUtils.FileQueryTemplate fileQueryTemplate = FileQueryTemplateUtils.getTemplateOfUserOrgIds(ApplicationContextHolder.getBean(OrgFacadeService.class), currUserId);
        String userOrgIdsTemplate = fileQueryTemplate.getTemplate();
        root.put("tableName", tableName);
        root.put("folderUuid", folderUuid);
        root.put("readFileField", readFileField);
        root.put("editFileField", editFileField);
        root.put("unit_in_expression_org_id", currUserId);
        root.put("userOrgIdsTemplate", userOrgIdsTemplate);
        root.put("orgIds", fileQueryTemplate.getOrgIds());
        root.put("roleIds", fileQueryTemplate.getRoleIds());
        root.put("sids", fileQueryTemplate.getSids());
        getQueryParams().putAll(root);

        String dmsFileFieldTemplate = FileQueryTemplateUtils.getTemplateOfListAllFiles();
        StringBuilder sb = new StringBuilder();
        sb.append(super.getFromSql());
        // 左关联，判断权限查询强、弱关系的数据
        DmsFileActionService dmsFileActionService = ApplicationContextHolder.getBean(DmsFileActionService.class);
        if (dmsFileActionService.hasFolderPermission(folderUuid, FileActions.READ_FILE)) {
            sb.append(" left join (" + dmsFileFieldTemplate + ") f1 ");
        } else {
            // 内关联，查询强关系的数据
            sb.append(" inner join (" + dmsFileFieldTemplate + ") f1 ");
        }
        sb.append(" on f1.data_uuid =  " + getColumnName("uuid"));
        // 启用版本管理
        if (enableVersioning) {
            sb.append(" left join dms_data_version v on this_.uuid = v.data_uuid ");
        }
        try {
            return TemplateEngineFactory.getDefaultTemplateEngine().process(sb.toString(), root);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.criteria.AbstractCriteria#getWhereSql()
     */
    @Override
    protected String getWhereSql() {
        // 启用版本管理
        if (enableVersioning) {
            addQueryParams("is_latest_version", true);
            return super.getWhereSql() + " and (v.is_latest_version = :is_latest_version or v.version is null) ";
        }
        return super.getWhereSql();
    }

}
