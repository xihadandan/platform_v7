/*
 * @(#)2019年10月13日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.suport.IexportMetaData.ColumnMetaData;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年10月13日.1	zhulh		2019年10月13日		Create
 * </pre>
 * @date 2019年10月13日
 */
public class DataRange extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8148137513920598205L;

    // ID
    private String id;

    // type
    private String type;

    // 表名或视图名
    private String dataName;

    // 列信息
    private List<ColumnMetaData> columnMetaDatas;

    // 数据所有者字段
    private String ownerFieldName;

    // 所有者字段值
    private List<String> ownerIds;

    // 归属角色
    private String roleId;

    // 数据规则
    private List<DataRuleDefinition> dataRuleDefinitions;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the dataName
     */
    public String getDataName() {
        return dataName;
    }

    /**
     * @param dataName 要设置的dataName
     */
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    /**
     * @return the columnMetaDatas
     */
    public List<ColumnMetaData> getColumnMetaDatas() {
        return columnMetaDatas;
    }

    /**
     * @param columnMetaDatas 要设置的columnMetaDatas
     */
    public void setColumnMetaDatas(List<ColumnMetaData> columnMetaDatas) {
        this.columnMetaDatas = columnMetaDatas;
    }

    /**
     * @return the ownerFieldName
     */
    public String getOwnerFieldName() {
        return ownerFieldName;
    }

    /**
     * @param ownerFieldName 要设置的ownerFieldName
     */
    public void setOwnerFieldName(String ownerFieldName) {
        this.ownerFieldName = ownerFieldName;
    }

    /**
     * @return the ownerIds
     */
    public List<String> getOwnerIds() {
        return ownerIds;
    }

    /**
     * @param ownerIds 要设置的ownerIds
     */
    public void setOwnerIds(List<String> ownerIds) {
        this.ownerIds = ownerIds;
    }

    /**
     * @return the roleId
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * @param roleId 要设置的roleId
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * @return the dataRuleDefinitions
     */
    public List<DataRuleDefinition> getDataRuleDefinitions() {
        return dataRuleDefinitions;
    }

    /**
     * @param dataRuleDefinitions 要设置的dataRuleDefinitions
     */
    public void setDataRuleDefinitions(List<DataRuleDefinition> dataRuleDefinitions) {
        this.dataRuleDefinitions = dataRuleDefinitions;
    }

    /**
     * @return
     */
    public String toSqlString(QueryContext queryContext) {
        StringBuilder sb = new StringBuilder();
        // select查询
        sb.append(getSelectSql(queryContext));
        // where条件
        sb.append(getWhereSql(queryContext));

        return sb.toString();
    }

    /**
     * @param queryContext
     * @return
     */
    public String getSelectSql(QueryContext queryContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("select").append(Separator.SPACE.getValue());
        Iterator<ColumnMetaData> it = columnMetaDatas.iterator();
        // 查询列
        while (it.hasNext()) {
            ColumnMetaData columnMetaData = it.next();
            String selectName = columnMetaData.getName();
            // 忽略版本号
            if (StringUtils.equals(selectName, "v.version")) {
                continue;
            }
            // CLOB数据转字符串
//            if (clobToChar && StringUtils.equalsIgnoreCase("clob", columnMetaData.getDataType())) {
//                selectName = "to_char(" + selectName + ")";
//            }
            sb.append(selectName).append(Separator.SPACE.getValue());
            sb.append("as").append(Separator.SPACE.getValue()).append(columnMetaData.getName())
                    .append(Separator.SPACE.getValue());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        // 除去以逗号结尾的查询列SQL
        int tmpLen = sb.length();
        if (tmpLen > 2 && StringUtils.equals(StringUtils.trim(sb.substring(tmpLen - 2)), Separator.COMMA.getValue())) {
            sb.setLength(tmpLen - 2);
        }
        // 表名
        sb.append("from").append(Separator.SPACE.getValue()).append(dataName).append(Separator.SPACE.getValue());
        return sb.toString();
    }

    /**
     * @param queryContext
     * @return
     */
    public String getWhereSql(QueryContext queryContext) {
        StringBuilder sb = new StringBuilder();
        String paramName = "dg_" + id;
        sb.append("where").append(Separator.SPACE.getValue());
        // 数据过滤
        // 数据规则过滤
        if (DataRangeDefinition.DATA_RULE_TYPE.equals(type)) {
            sb.append(" 1 = 1 ");
        } else {
            // 数据所有者过滤
            if (CollectionUtils.isNotEmpty(ownerIds)) {
                sb.append(ownerFieldName).append(Separator.SPACE.getValue()).append("in(:" + paramName + ")");
                queryContext.getQueryParams().put(paramName, ownerIds);
            } else {
                sb.append(" 1 = 2 ");
            }
        }
        // 规则条件
        if (CollectionUtils.isNotEmpty(dataRuleDefinitions)) {
            sb.append(" and ");
            sb.append("(");
            for (DataRuleDefinition dataRuleDefinition : dataRuleDefinitions) {
                sb.append(dataRuleDefinition.toSqlString());
            }
            sb.append(")");
        }
        return sb.toString();
    }

}
