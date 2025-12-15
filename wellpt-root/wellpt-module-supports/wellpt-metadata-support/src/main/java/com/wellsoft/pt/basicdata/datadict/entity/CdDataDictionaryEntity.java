/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据字典
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8/9/23.1	zhulh		8/9/23		Create
 * </pre>
 * @date 8/9/23
 */
@ApiModel("数据字典")
@Entity
@Table(name = "cd_data_dictionary")
@DynamicUpdate
@DynamicInsert
public class CdDataDictionaryEntity extends SysEntity {

    @ApiModelProperty("字典名称")
    private String name;

    @ApiModelProperty("字典编码")
    private String code;

    @ApiModelProperty("扩展定义JSON信息")
    private String extDefinitionJson;

    @ApiModelProperty("模块ID")
    private String moduleId;

    @ApiModelProperty("字典分类UUID")
    private Long categoryUuid;

    @ApiModelProperty("备注")
    private String remark;

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
     * @return the extDefinitionJson
     */
    public String getExtDefinitionJson() {
        return extDefinitionJson;
    }

    /**
     * @param extDefinitionJson 要设置的extDefinitionJson
     */
    public void setExtDefinitionJson(String extDefinitionJson) {
        this.extDefinitionJson = extDefinitionJson;
    }

    /**
     * @return the moduleId
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId 要设置的moduleId
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return the categoryUuid
     */
    public Long getCategoryUuid() {
        return categoryUuid;
    }

    /**
     * @param categoryUuid 要设置的categoryUuid
     */
    public void setCategoryUuid(Long categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
