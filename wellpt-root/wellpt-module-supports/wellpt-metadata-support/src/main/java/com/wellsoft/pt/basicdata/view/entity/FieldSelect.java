/*
 * @(#)2014-9-5 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Description: 查询定义——字段查询
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-9-5.1	wubin		2014-9-5		Create
 * </pre>
 * @date 2014-9-5
 */
@Deprecated
//@Entity
//@Table(name = "view_field_select")
//@DynamicUpdate
//@DynamicInsert
public class FieldSelect extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -364385493193062862L;
    /**
     * 加入查询的字段ID
     */
    private String field;
    /**
     * 查询的字段在定义中展示的名字
     */
    private String showName;
    /**
     * 查询的字段的类型
     */
    private String selectType;
    /**
     * 查询的字段的类型的ID
     */
    private String selectTypeId;
    /**
     * 查询默认值
     */
    private String defaultValue;
    /**
     * 是否按区域查询
     */
    private Boolean isArea;
    /**
     * 是否精确查询
     */
    private Boolean isExact;
    /**
     * 精确查询的条件1、包含，支持多值，多值使用分号隔开，2精确查询 “=”,3不包含 ，4不等于）
     */
    private String exactValue;
    /**
     * 是否模糊查询
     */
    private Boolean isLike;
    /**
     * 日期格式
     */
    private String contentFormat;
    /**
     * 组织选择框
     */
    private String orgInputMode;
    /**
     * 备选项来源
     */
    private String optionDataSource;
    /**
     * 备选项设置
     */
    private String optdata;
    /**
     * 字典名称
     */
    private String dictName;
    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 数据源Id
     */
    private String dataSourceId;

    /**
     * 数据源名称
     */
    private String dataSourceName;

    /**
     * 数据源选项的名称列
     */
    private String selectNameColumn;

    /**
     * 数据源选项的值列
     */
    private String selectValueColumn;

    private Integer sortOrder;//条件排序

    @UnCloneable
    private SelectDefinitionNew selectDefinitionNew;

    /**
     * @return the dataSourceId
     */
    public String getDataSourceId() {
        return dataSourceId;
    }

    /**
     * @param dataSourceId 要设置的dataSourceId
     */
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    /**
     * @return the dataSourceName
     */
    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * @param dataSourceName 要设置的dataSourceName
     */
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    /**
     * @return the selectNameColumn
     */
    public String getSelectNameColumn() {
        return selectNameColumn;
    }

    /**
     * @param selectNameColumn 要设置的selectNameColumn
     */
    public void setSelectNameColumn(String selectNameColumn) {
        this.selectNameColumn = selectNameColumn;
    }

    /**
     * @return the selectValueColumn
     */
    public String getSelectValueColumn() {
        return selectValueColumn;
    }

    /**
     * @param selectValueColumn 要设置的selectValueColumn
     */
    public void setSelectValueColumn(String selectValueColumn) {
        this.selectValueColumn = selectValueColumn;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the exactValue
     */
    public String getExactValue() {
        return exactValue;
    }

    /**
     * @param exactValue 要设置的exactValue
     */
    public void setExactValue(String exactValue) {
        this.exactValue = exactValue;
    }

    /**
     * @return the selectTypeId
     */
    public String getSelectTypeId() {
        return selectTypeId;
    }

    /**
     * @param selectTypeId 要设置的selectTypeId
     */
    public void setSelectTypeId(String selectTypeId) {
        this.selectTypeId = selectTypeId;
    }

    /**
     * @return the isLike
     */
    public Boolean getIsLike() {
        return isLike;
    }

    /**
     * @param isLike 要设置的isLike
     */
    public void setIsLike(Boolean isLike) {
        this.isLike = isLike;
    }

    /**
     * @return the contentFormat
     */
    public String getContentFormat() {
        return contentFormat;
    }

    /**
     * @param contentFormat 要设置的contentFormat
     */
    public void setContentFormat(String contentFormat) {
        this.contentFormat = contentFormat;
    }

    /**
     * @return the orgInputMode
     */
    public String getOrgInputMode() {
        return orgInputMode;
    }

    /**
     * @param orgInputMode 要设置的orgInputMode
     */
    public void setOrgInputMode(String orgInputMode) {
        this.orgInputMode = orgInputMode;
    }

    /**
     * @return the optionDataSource
     */
    public String getOptionDataSource() {
        return optionDataSource;
    }

    /**
     * @param optionDataSource 要设置的optionDataSource
     */
    public void setOptionDataSource(String optionDataSource) {
        this.optionDataSource = optionDataSource;
    }

    /**
     * @return the optdata
     */
    public String getOptdata() {
        return optdata;
    }

    /**
     * @param optdata 要设置的optdata
     */
    public void setOptdata(String optdata) {
        this.optdata = optdata;
    }

    /**
     * @return the dictName
     */
    public String getDictName() {
        return dictName;
    }

    /**
     * @param dictName 要设置的dictName
     */
    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    /**
     * @return the dictCode
     */
    public String getDictCode() {
        return dictCode;
    }

    /**
     * @param dictCode 要设置的dictCode
     */
    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    /**
     * @return the selectDefinitionNew
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "select_def_uuid", nullable = false)
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public SelectDefinitionNew getSelectDefinitionNew() {
        return selectDefinitionNew;
    }

    /**
     * @param selectDefinitionNew 要设置的selectDefinitionNew
     */
    public void setSelectDefinitionNew(SelectDefinitionNew selectDefinitionNew) {
        this.selectDefinitionNew = selectDefinitionNew;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue 要设置的defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field 要设置的field
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the showName
     */
    public String getShowName() {
        return showName;
    }

    /**
     * @param showName 要设置的showName
     */
    public void setShowName(String showName) {
        this.showName = showName;
    }

    /**
     * @return the selectType
     */
    public String getSelectType() {
        return selectType;
    }

    /**
     * @param selectType 要设置的selectType
     */
    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    /**
     * @return the isArea
     */
    public Boolean getIsArea() {
        return isArea;
    }

    /**
     * @param isArea 要设置的isArea
     */
    public void setIsArea(Boolean isArea) {
        this.isArea = isArea;
    }

    /**
     * @return the isExact
     */
    public Boolean getIsExact() {
        return isExact;
    }

    /**
     * @param isExact 要设置的isExact
     */
    public void setIsExact(Boolean isExact) {
        this.isExact = isExact;
    }

}
