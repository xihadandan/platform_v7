/*
 * @(#)2014-6-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.bean;

import com.wellsoft.pt.basicdata.excelexporttemplate.entity.ExcelExportDefinition;

import java.util.HashSet;
import java.util.Set;

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
public class ExcelExportDefinitionBean extends ExcelExportDefinition {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8923098371527837470L;
    private String fileName;
    private Set<ExcelExportColumnDefinitionBean> changeColumnDefinitions = new HashSet<ExcelExportColumnDefinitionBean>();

    private Set<ExcelExportColumnDefinitionBean> deletedExcelRows = new HashSet<ExcelExportColumnDefinitionBean>();

    /**
     * @return the changeColumnDefinitions
     */
    public Set<ExcelExportColumnDefinitionBean> getChangeColumnDefinitions() {
        return changeColumnDefinitions;
    }

    /**
     * @param changeColumnDefinitions 要设置的changeColumnDefinitions
     */
    public void setChangeColumnDefinitions(Set<ExcelExportColumnDefinitionBean> changeColumnDefinitions) {
        this.changeColumnDefinitions = changeColumnDefinitions;
    }

    /**
     * @return the deletedExcelRows
     */
    public Set<ExcelExportColumnDefinitionBean> getDeletedExcelRows() {
        return deletedExcelRows;
    }

    /**
     * @param deletedExcelRows 要设置的deletedExcelRows
     */
    public void setDeletedExcelRows(Set<ExcelExportColumnDefinitionBean> deletedExcelRows) {
        this.deletedExcelRows = deletedExcelRows;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
