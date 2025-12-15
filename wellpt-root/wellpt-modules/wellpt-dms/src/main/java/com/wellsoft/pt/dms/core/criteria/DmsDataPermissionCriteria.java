/*
 * @(#)2019年10月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.criteria;

import com.google.common.base.CaseFormat;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.dms.entity.DmsDataPermissionDefinitionEntity;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.TableCriteria;
import com.wellsoft.pt.jpa.dao.NativeDao;

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
 * 2019年10月12日.1	zhulh		2019年10月12日		Create
 * </pre>
 * @date 2019年10月12日
 */
public class DmsDataPermissionCriteria extends TableCriteria {

    /**
     * @param nativeDao
     * @param tableName
     */
    public DmsDataPermissionCriteria(NativeDao nativeDao, String tableName,
                                     DmsDataPermissionDefinitionEntity dataPermissionDefinitionEntity) {
        super(nativeDao, tableName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.TableCriteria#initCriteriaMetadata()
     */
    @Override
    protected CriteriaMetadata initCriteriaMetadata() {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tableName", getTableName());
        List<QueryItem> columns = this.nativeDao.namedQuery("queryTableColumnMetadata", params, QueryItem.class);
        for (QueryItem column : columns) {
            String columnName = column.getString("columnName");
            // 使用驼峰风格列索引
            String camelColumnIndex = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
            criteriaMetadata.add(camelColumnIndex, columnName, column.getString("comments"),
                    column.getString("dataType"));
        }
        return criteriaMetadata;
    }

}
