/*
 * @(#)2018年9月5日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.bean;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author {zhongwd}
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月5日.1	{zhongwd}		2018年9月5日		Create
 * </pre>
 * @date 2018年9月5日
 */
public class DmsDataImportBean {

    //开始行
    private String beginRow;
    //动态表名
    private String dynamicTable;
    //动态表名
    private String dynamicTableName;
    //模板UUID
    private String importTemplate;
    //文件名
    private String importTemplate_fileNames;
    //标题
    private String title;
    //类型code
    private String typeCode;
    //类型名称
    private String typeName;
    //字段配置
    private List<DmsDataImportFiledBean> fieldConfigs;

    //处理策略
    private String strategy;
    //处理策略名称
    private String strategyText;
    //唯一性字段
    private String uniquenessField;
    //唯一性字段名称
    private String uniquenessFieldText;

    /**
     * @return the beginRow
     */
    public String getBeginRow() {
        return beginRow;
    }

    /**
     * @param beginRow 要设置的beginRow
     */
    public void setBeginRow(String beginRow) {
        this.beginRow = beginRow;
    }

    /**
     * @return the dynamicTable
     */
    public String getDynamicTable() {
        return dynamicTable;
    }

    /**
     * @param dynamicTable 要设置的dynamicTable
     */
    public void setDynamicTable(String dynamicTable) {
        this.dynamicTable = dynamicTable;
    }

    /**
     * @return the dynamicTableName
     */
    public String getDynamicTableName() {
        return dynamicTableName;
    }

    /**
     * @param dynamicTableName 要设置的dynamicTableName
     */
    public void setDynamicTableName(String dynamicTableName) {
        this.dynamicTableName = dynamicTableName;
    }

    /**
     * @return the importTemplate
     */
    public String getImportTemplate() {
        return importTemplate;
    }

    /**
     * @param importTemplate 要设置的importTemplate
     */
    public void setImportTemplate(String importTemplate) {
        this.importTemplate = importTemplate;
    }

    /**
     * @return the importTemplate_fileNames
     */
    public String getImportTemplate_fileNames() {
        return importTemplate_fileNames;
    }

    /**
     * @param importTemplate_fileNames 要设置的importTemplate_fileNames
     */
    public void setImportTemplate_fileNames(String importTemplate_fileNames) {
        this.importTemplate_fileNames = importTemplate_fileNames;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the typeCode
     */
    public String getTypeCode() {
        return typeCode;
    }

    /**
     * @param typeCode 要设置的typeCode
     */
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName 要设置的typeName
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return the fieldConfigs
     */
    public List<DmsDataImportFiledBean> getFieldConfigs() {
        return fieldConfigs;
    }

    /**
     * @param fieldConfigs 要设置的fieldConfigs
     */
    public void setFieldConfigs(List<DmsDataImportFiledBean> fieldConfigs) {
        this.fieldConfigs = fieldConfigs;
    }

    /**
     * @return the strategy
     */
    public String getStrategy() {
        return strategy;
    }

    /**
     * @param strategy 要设置的strategy
     */
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    /**
     * @return the strategyText
     */
    public String getStrategyText() {
        return strategyText;
    }

    /**
     * @param strategyText 要设置的strategyText
     */
    public void setStrategyText(String strategyText) {
        this.strategyText = strategyText;
    }

    /**
     * @return the uniquenessField
     */
    public String getUniquenessField() {
        return uniquenessField;
    }

    /**
     * @param uniquenessField 要设置的uniquenessField
     */
    public void setUniquenessField(String uniquenessField) {
        this.uniquenessField = uniquenessField;
    }

    /**
     * @return the uniquenessFieldText
     */
    public String getUniquenessFieldText() {
        return uniquenessFieldText;
    }

    /**
     * @param uniquenessFieldText 要设置的uniquenessFieldText
     */
    public void setUniquenessFieldText(String uniquenessFieldText) {
        this.uniquenessFieldText = uniquenessFieldText;
    }

}
