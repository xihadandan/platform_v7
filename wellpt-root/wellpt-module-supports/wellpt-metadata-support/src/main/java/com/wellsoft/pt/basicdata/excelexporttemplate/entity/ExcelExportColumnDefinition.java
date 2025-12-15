/*
 * @(#)2014-6-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-13.1	wubin		2014-6-13		Create
 * </pre>
 * @date 2014-6-13
 */
@Entity
@Table(name = "excel_export_column_definition")
@DynamicUpdate
@DynamicInsert
public class ExcelExportColumnDefinition extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 201674316787941073L;

    /**
     * 第几列
     */
    private Integer columnNum;
    /**
     * 属性名（域名）
     */
    private String attributeName;

    private String titleName;

    private String columnDataType;

    private String entityName;

    private String columnAliase;

    private String valueType;// 列值类型 1表示基础列值 2表示高级列值

    @UnCloneable
    private ExcelExportDefinition excelExportDefinition;

    /**
     * @return the columnDataType
     */
    public String getColumnDataType() {
        return columnDataType;
    }

    /**
     * @param columnDataType 要设置的columnDataType
     */
    public void setColumnDataType(String columnDataType) {
        this.columnDataType = columnDataType;
    }

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName 要设置的entityName
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return the columnAliase
     */
    public String getColumnAliase() {
        return columnAliase;
    }

    /**
     * @param columnAliase 要设置的columnAliase
     */
    public void setColumnAliase(String columnAliase) {
        this.columnAliase = columnAliase;
    }

    /**
     * @return the titleName
     */
    public String getTitleName() {
        return titleName;
    }

    /**
     * @param titleName 要设置的titleName
     */
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    /**
     * @return the valueType
     */
    public String getValueType() {
        return valueType;
    }

    /**
     * @param valueType 要设置的valueType
     */
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    /**
     * @return the columnNum
     */
    public Integer getColumnNum() {
        return columnNum;
    }

    /**
     * @param columnNum 要设置的columnNum
     */
    public void setColumnNum(Integer columnNum) {
        this.columnNum = columnNum;
    }

    /**
     * @return the attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @param attributeName 要设置的attributeName
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * @return the excelExportDefinition
     */
    @ManyToOne
    @JoinColumn(name = "excel_export_definition_uuid")
    public ExcelExportDefinition getExcelExportDefinition() {
        return excelExportDefinition;
    }

    /**
     * @param excelExportDefinition 要设置的excelExportDefinition
     */
    public void setExcelExportDefinition(ExcelExportDefinition excelExportDefinition) {
        this.excelExportDefinition = excelExportDefinition;
    }

}
