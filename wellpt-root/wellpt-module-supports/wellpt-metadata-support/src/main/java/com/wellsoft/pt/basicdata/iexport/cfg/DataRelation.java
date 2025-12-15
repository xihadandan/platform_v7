/*
 * @(#)2019年4月10日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.cfg;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.Separator;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
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
 * 2019年4月10日.1	zhulh		2019年4月10日		Create
 * </pre>
 * @date 2019年4月10日
 */
public class DataRelation extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1628468083018704438L;

    // 引用的数据类型
    private String referencedType;

    // 引用的数据列
    private String referencedColumnName;

    // 引用的数据表
    private String referencedTableName;

    // 源数据类型
    private String sourceType;

    // 源数据列
    private String sourceColumnName;

    // 源数据类型是否为自身
    private boolean sourceSelf;

    // 列映射<源数据列，要替换的数据列>
    private Map<String, String> columnMapping;

    /**
     * @return the referencedType
     */
    public String getReferencedType() {
        return referencedType;
    }

    /**
     * @param referencedType 要设置的referencedType
     */
    public void setReferencedType(String referencedType) {
        this.referencedType = referencedType;
    }

    /**
     * @return the referencedColumnName
     */
    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    /**
     * @param referencedColumnName 要设置的referencedColumnName
     */
    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

    /**
     * @return the referencedTableName
     */
    public String getReferencedTableName() {
        return referencedTableName;
    }

    /**
     * @param referencedTableName 要设置的referencedTableName
     */
    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    /**
     * @return the sourceType
     */
    public String getSourceType() {
        return sourceType;
    }

    /**
     * @param sourceType 要设置的sourceType
     */
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * @return the sourceColumnName
     */
    public String getSourceColumnName() {
        return sourceColumnName;
    }

    /**
     * @param sourceColumnName 要设置的sourceColumnName
     */
    public void setSourceColumnName(String sourceColumnName) {
        this.sourceColumnName = sourceColumnName;
    }

    /**
     * @return the sourceSelf
     */
    public boolean isSourceSelf() {
        return sourceSelf;
    }

    /**
     * @param sourceSelf 要设置的sourceSelf
     */
    public void setSourceSelf(boolean sourceSelf) {
        this.sourceSelf = sourceSelf;
    }

    /**
     * @return the columnMapping
     */
    public Map<String, String> getColumnMapping() {
        if (columnMapping == null) {
            columnMapping = Maps.newHashMap();
            List<String> sourceColumns = Arrays.asList(StringUtils.split(sourceColumnName,
                    Separator.SEMICOLON.getValue()));
            List<String> targetColumns = Arrays.asList(StringUtils.split(referencedColumnName,
                    Separator.SEMICOLON.getValue()));
            for (int index = 0; index < sourceColumns.size(); index++) {
                columnMapping.put(sourceColumns.get(index), targetColumns.get(index));
            }
        }
        return columnMapping;
    }

}
