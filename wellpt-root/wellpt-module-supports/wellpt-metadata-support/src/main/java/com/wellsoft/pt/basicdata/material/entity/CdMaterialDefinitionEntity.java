/*
 * @(#)4/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.material.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 材料定义
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 4/27/23.1	zhulh		4/27/23		Create
 * </pre>
 * @date 4/27/23
 */
@ApiModel("材料定义")
@Entity
@Table(name = "CD_MATERIAL_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class CdMaterialDefinitionEntity extends SysEntity {
    private static final long serialVersionUID = 5028391486424887582L;

    @ApiModelProperty("材料名称")
    private String name;

    @ApiModelProperty("材料编号")
    private String code;

    @ApiModelProperty("材料形式：10原件、20复印件、30电子文档")
    private String mediumType;

    @ApiModelProperty("电子文档格式")
    private String format;

    @ApiModelProperty("样例文件UUID")
    private String sampleRepoFileUuid;

    @ApiModelProperty("材料说明")
    private String description;

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
     * @return the mediumType
     */
    public String getMediumType() {
        return mediumType;
    }

    /**
     * @param mediumType 要设置的mediumType
     */
    public void setMediumType(String mediumType) {
        this.mediumType = mediumType;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format 要设置的format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return the sampleRepoFileUuid
     */
    public String getSampleRepoFileUuid() {
        return sampleRepoFileUuid;
    }

    /**
     * @param sampleRepoFileUuid 要设置的sampleRepoFileUuid
     */
    public void setSampleRepoFileUuid(String sampleRepoFileUuid) {
        this.sampleRepoFileUuid = sampleRepoFileUuid;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 要设置的description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
