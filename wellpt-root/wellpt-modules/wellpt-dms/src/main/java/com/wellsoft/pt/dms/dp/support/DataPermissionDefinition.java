/*
 * @(#)2019年10月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.basicdata.iexport.suport.IexportMetaData.ColumnMetaData;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
 * 2019年10月12日.1	zhulh		2019年10月12日		Create
 * </pre>
 * @date 2019年10月12日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataPermissionDefinition extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7714301451603022118L;

    private String id;

    private String name;

    // 数据名称
    private String dataName;

    // 数据规则
    private List<DataRuleDefinition> ruleDefinitions = Lists.newArrayList();

    // 数据范围
    private List<DataRangeDefinition> rangeDefinitions = Lists.newArrayList();

    // 数据列信息
    private List<ColumnMetaData> columnMetaDatas;

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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
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
     * @return the ruleDefinitions
     */
    public List<DataRuleDefinition> getRuleDefinitions() {
        return ruleDefinitions;
    }

    /**
     * @param ruleDefinitions 要设置的ruleDefinitions
     */
    public void setRuleDefinitions(List<DataRuleDefinition> ruleDefinitions) {
        this.ruleDefinitions = ruleDefinitions;
    }

    /**
     * @return the rangeDefinitions
     */
    public List<DataRangeDefinition> getRangeDefinitions() {
        return rangeDefinitions;
    }

    /**
     * @param rangeDefinitions 要设置的rangeDefinitions
     */
    public void setRangeDefinitions(List<DataRangeDefinition> rangeDefinitions) {
        this.rangeDefinitions = rangeDefinitions;
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

}
