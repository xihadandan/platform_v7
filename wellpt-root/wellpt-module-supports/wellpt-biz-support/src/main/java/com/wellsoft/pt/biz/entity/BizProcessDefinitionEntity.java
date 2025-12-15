/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 业务流程定义实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@ApiModel("业务流程定义")
@Entity
@Table(name = "BIZ_PROCESS_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class BizProcessDefinitionEntity extends IdEntity {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("编号")
    private String code;

    @ApiModelProperty("版本")
    private Double version;

    @ApiModelProperty("是否启用")
    private Boolean enabled;

    @ApiModelProperty("业务ID")
    private String businessId;

    @ApiModelProperty("业务标签ID")
    private String tagId;

    @ApiModelProperty("定义JSON信息")
    private String definitionJson;

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
     * @return the version
     */
    public Double getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(Double version) {
        this.version = version;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the businessId
     */
    public String getBusinessId() {
        return businessId;
    }

    /**
     * @param businessId 要设置的businessId
     */
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * @return the tagId
     */
    public String getTagId() {
        return tagId;
    }

    /**
     * @param tagId 要设置的tagId
     */
    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    /**
     * @return the definitionJson
     */
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
