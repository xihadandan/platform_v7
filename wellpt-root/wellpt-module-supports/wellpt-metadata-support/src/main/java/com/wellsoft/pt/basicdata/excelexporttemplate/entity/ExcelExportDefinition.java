/*
 * @(#)2014-6-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
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
@Entity
@Table(name = "excel_export_definition")
@DynamicUpdate
@DynamicInsert
public class ExcelExportDefinition extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8886108172545540314L;
    // 导出模版的名字
    @NotBlank
    private String name;
    // 导出模版的编号
    @NotBlank
    private String code;
    // 导出模版的id
    @NotBlank
    private String id;
    // 导出模版的开始行
    @Digits(fraction = 0, integer = 10)
    @NotNull
    private Integer startRow;
    // 导出模版对应的视图uuid
    @NotBlank
    private String viewUuid;
    // 导出模版对应的视图的名称
    private String viewName;
    // 导出模版文件的uuid
    private String fileUuid;

    @UnCloneable
    private Set<ExcelExportColumnDefinition> excelExportColumnDefinition;

    /**
     * @return the viewName
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * @param viewName 要设置的viewName
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
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
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

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
     * @return the startRow
     */
    public Integer getStartRow() {
        return startRow;
    }

    /**
     * @param startRow 要设置的startRow
     */
    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    /**
     * @return the viewUuid
     */
    public String getViewUuid() {
        return viewUuid;
    }

    /**
     * @param viewUuid 要设置的viewUuid
     */
    public void setViewUuid(String viewUuid) {
        this.viewUuid = viewUuid;
    }

    /**
     * @return the fileUuid
     */
    public String getFileUuid() {
        return fileUuid;
    }

    /**
     * @param fileUuid 要设置的fileUuid
     */
    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    /**
     * @return the excelExportColumnDefinition
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "excelExportDefinition")
    @Cascade(value = {CascadeType.ALL})
    public Set<ExcelExportColumnDefinition> getExcelExportColumnDefinition() {
        return excelExportColumnDefinition;
    }

    /**
     * @param excelExportColumnDefinition 要设置的excelExportColumnDefinition
     */
    public void setExcelExportColumnDefinition(Set<ExcelExportColumnDefinition> excelExportColumnDefinition) {
        this.excelExportColumnDefinition = excelExportColumnDefinition;
    }

}
