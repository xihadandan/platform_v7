/*
 * @(#)2019年8月22日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.usertable.service;

import com.wellsoft.pt.dyform.implement.repository.usertable.metadata.ColumnMetadata;

import java.util.List;
import java.util.Map;
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
public interface UserTableMetadataService {

    /**
     * @param columnName
     * @param tableName
     * @return
     */
    ColumnMetadata getColumnMetadata(String columnName, String tableName);

    /**
     * @param fieldSet
     * @param tableName
     * @return
     */
    Set<String> filterTableFields(Set<String> fieldSet, String tableName);


    /**
     * @param tableName
     * @param candidateColumns
     * @return
     */
    Map<String, Object> getSystemColumns(String tableName, String... candidateColumns);

    /**
     * @param tableName
     * @return
     */
    Set<String> getColumnNames(String tableName);

    /**
     * @param tableName
     * @return
     */
    List<ColumnMetadata> getColumnMetadatas(String tableName);

}
