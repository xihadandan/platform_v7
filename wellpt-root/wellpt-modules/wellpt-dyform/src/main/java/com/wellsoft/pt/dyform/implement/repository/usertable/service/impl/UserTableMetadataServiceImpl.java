/*
 * @(#)2019年8月22日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.usertable.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.enums.EnumFormDataStatus;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.dyform.implement.definition.event.FormDefinitionChangedEvent;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext;
import com.wellsoft.pt.dyform.implement.repository.enums.FormRepositoryModeEnum;
import com.wellsoft.pt.dyform.implement.repository.usertable.metadata.ColumnMetadata;
import com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableMetadataService;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryUtils;
import com.wellsoft.pt.repository.dao.DbTableDao;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月22日.1	zhulh		2019年8月22日		Create
 * </pre>
 * @date 2019年8月22日
 */
@Service
public class UserTableMetadataServiceImpl implements UserTableMetadataService,
        ApplicationListener<FormDefinitionChangedEvent> {

    private static final List<String> AUTO_FILL_SYSTEM_FIELDS = Lists.newArrayList(EnumSystemField.creator.getName(),
            EnumSystemField.create_time.getName(), EnumSystemField.modifier.getName(),
            EnumSystemField.modify_time.getName(), EnumSystemField.rec_ver.getName(),
            EnumSystemField.system_unit_id.getName(), EnumSystemField.status.getName());
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private DbTableDao dbTableDao;

    private Map<String, Map<String, ColumnMetadata>> tableColumnMetadataMap = Maps.newHashMap();

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableMetadataService#getColumnMetadata(java.lang.String, java.lang.String)
     */
    public ColumnMetadata getColumnMetadata(String columnName, String tableName) {
        if (!tableColumnMetadataMap.containsKey(tableName)) {
            synchronized (tableColumnMetadataMap) {
                if (!tableColumnMetadataMap.containsKey(tableName)) {
                    loadTableColumnMetadata(tableName);
                }
            }
        }
        return tableColumnMetadataMap.get(tableName).get(columnName);
    }

    /**
     * @param tableName
     * @return
     */
    private Map<String, ColumnMetadata> getColumnMetadataMap(String tableName) {
        if (!tableColumnMetadataMap.containsKey(tableName)) {
            synchronized (tableColumnMetadataMap) {
                if (!tableColumnMetadataMap.containsKey(tableName)) {
                    loadTableColumnMetadata(tableName);
                }
            }
        }
        return tableColumnMetadataMap.get(tableName);
    }

    /**
     * @param tableName
     */
    private void loadTableColumnMetadata(String tableName) {
        Map<String, ColumnMetadata> columnMetaDataMap = new LinkedCaseInsensitiveMap<ColumnMetadata>();
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            JdbcConnectionAccess jdbcConnectionAccess = ((SessionImplementor) SessionFactoryUtils
                    .getMultiTenantSessionFactory().getCurrentSession()).getJdbcConnectionAccess();
            connection = jdbcConnectionAccess.obtainConnection();
            // 列信息
            resultSet = connection.createStatement().executeQuery("select * from " + tableName + "  where 1=2");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            // 获得列的信息
            int size = rsmd.getColumnCount();
            for (int column = 1; column <= size; column++) {
                String columnName = StringUtils.lowerCase(rsmd.getColumnName(column));

                ColumnMetadata cmd = new ColumnMetadata();
                cmd.setName(columnName);
                cmd.setDataType(StringUtils.lowerCase(rsmd.getColumnTypeName(column)));
                cmd.setNullable(rsmd.isNullable(column) != ResultSetMetaData.columnNoNulls);
                cmd.setPrecision(rsmd.getPrecision(column));
                cmd.setScale(rsmd.getScale(column));
                columnMetaDataMap.put(columnName, cmd);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
        tableColumnMetadataMap.put(tableName, columnMetaDataMap);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableService#filterTableFields(java.util.Set, java.lang.String)
     */
    @Override
    public Set<String> filterTableFields(Set<String> fieldSet, String tableName) {
        Map<String, ColumnMetadata> columnMetadataMap = getColumnMetadataMap(tableName);
        Set<String> tableFieldSet = columnMetadataMap.keySet();
        Set<String> returnFields = Sets.newHashSet(fieldSet);
        returnFields.retainAll(tableFieldSet);
        return returnFields;
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(FormDefinitionChangedEvent event) {
        // 表单定义变更，删除对应的元数据
        String formUuid = (String) event.getSource();
        if (StringUtils.isNotBlank(formUuid)) {
            FormDefinition formDefinition = ((FormDefinition) dyFormFacade.getFormDefinition(formUuid));
            if (formDefinition == null) {
                return;
            }
            FormDefinitionHandler formDefinitionHandler = formDefinition.doGetFormDefinitionHandler();
            FormRepositoryContext repositoryContext = new FormRepositoryContext(formDefinitionHandler);
            if (FormRepositoryModeEnum.Dyform.getValue().equals(repositoryContext.getRepositoryMode())) {
                synchronized (tableColumnMetadataMap) {
                    tableColumnMetadataMap.remove(formDefinition.getTableName());
                    tableColumnMetadataMap.remove(StringUtils.lowerCase(formDefinition.getTableName()));
                }
            } else if (FormRepositoryModeEnum.UserTable.getValue().equals(repositoryContext.getRepositoryMode())) {
                synchronized (tableColumnMetadataMap) {
                    tableColumnMetadataMap.remove(repositoryContext.getUserTableName());
                    tableColumnMetadataMap.remove(StringUtils.lowerCase(repositoryContext.getUserTableName()));
                }
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableService#getSystemColumns(java.lang.String, java.lang.String[])
     */
    @Override
    public Map<String, Object> getSystemColumns(String tableName, String... candidateColumns) {
        List<String> candidateColumnList = Lists.newArrayList(candidateColumns);
        Map<String, Object> values = Maps.newHashMap();
        Map<String, ColumnMetadata> columnMap = getColumnMetadataMap(tableName);
        for (Entry<String, ColumnMetadata> entry : columnMap.entrySet()) {
            String columnName = entry.getKey();
            if (CollectionUtils.isEmpty(candidateColumnList)
                    && AUTO_FILL_SYSTEM_FIELDS.contains(StringUtils.lowerCase(columnName))) {
                putSystemFieldValue(columnName, values);
            } else if (candidateColumnList.contains(columnName)) {
                putSystemFieldValue(columnName, values);
            }
        }
        return values;
    }

    /**
     * @param columnName
     * @param values
     */
    private void putSystemFieldValue(String columnName, Map<String, Object> values) {
        // 创建人
        if (StringUtils.equalsIgnoreCase(EnumSystemField.creator.getName(), columnName)) {
            values.put(columnName, SpringSecurityUtils.getCurrentUserId());
        }
        // 创建时间
        if (StringUtils.equalsIgnoreCase(EnumSystemField.create_time.getName(), columnName)) {
            values.put(columnName, Calendar.getInstance().getTime());
        }
        // 修改人
        if (StringUtils.equalsIgnoreCase(EnumSystemField.modifier.getName(), columnName)) {
            values.put(columnName, SpringSecurityUtils.getCurrentUserId());
        }
        // 修改时间
        if (StringUtils.equalsIgnoreCase(EnumSystemField.modify_time.getName(), columnName)) {
            values.put(columnName, Calendar.getInstance().getTime());
        }
        // 版本号
        if (StringUtils.equalsIgnoreCase(EnumSystemField.rec_ver.getName(), columnName)) {
            values.put(columnName, 0);
        }
        // 系统单位ID
        if (StringUtils.equalsIgnoreCase(EnumSystemField.system_unit_id.getName(), columnName)) {
            values.put(columnName, SpringSecurityUtils.getCurrentUserUnitId());
        }
        // 数据状态
        if (StringUtils.equalsIgnoreCase(EnumSystemField.status.getName(), columnName)) {
            values.put(columnName, EnumFormDataStatus.DYFORM_DATA_STATUS_DEFAULT.getValue());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableService#getColumnNames(java.lang.String)
     */
    @Override
    public Set<String> getColumnNames(String tableName) {
        return getColumnMetadataMap(tableName).keySet();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableMetadataService#getColumnMetadatas(java.lang.String)
     */
    @Override
    public List<ColumnMetadata> getColumnMetadatas(String tableName) {
        return Lists.newArrayList(getColumnMetadataMap(tableName).values());
    }

}
