/*
 * @(#)2016-07-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import com.wellsoft.pt.jpa.service.UUIDGeneratorIndicate;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 功能实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-07-26.1	zhulh		2016-07-26		Create
 * </pre>
 * @date 2016-07-26
 */
@Entity
@Table(name = "APP_FUNCTION")
@DynamicUpdate
@DynamicInsert
public class AppFunction extends IdEntity implements UUIDGeneratorIndicate, ConfigurableIdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1469538309951L;

    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty("编号")
    private String code;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("功能定义的JSON详细信息")
    @MaxLength(max = 4000)
    private String definitionJson;
    @ApiModelProperty("功能信息是否可导出")
    private Boolean exportable;
    @ApiModelProperty("功能导出类型")
    private String exportType;
    @ApiModelProperty("备注")
    private String remark;

    /**
     * @return the name
     */
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    @Column(name = "ID")
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    @Column(name = "CODE")
    public String getCode() {
        return this.code;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the type
     */
    @Column(name = "TYPE")
    public String getType() {
        return this.type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the definitionJson
     */
    @Column(name = "DEFINITION_JSON")
    public String getDefinitionJson() {
        return definitionJson;
    }

    /**
     * @param definitionJson 要设置的definitionJson
     */
    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    /**
     * @return the exportable
     */
    @Column(name = "EXPORTABLE")
    public Boolean getExportable() {
        return exportable;
    }

    /**
     * @param exportable 要设置的exportable
     */
    public void setExportable(Boolean exportable) {
        this.exportable = exportable;
    }

    /**
     * @return the exportType
     */
    @Column(name = "EXPORT_TYPE")
    public String getExportType() {
        return exportType;
    }

    /**
     * @param exportType 要设置的exportType
     */
    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    /**
     * @return the remark
     */
    @Column(name = "REMARK")
    public String getRemark() {
        return this.remark;
    }

    /**
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
