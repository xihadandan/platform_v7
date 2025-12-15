/*
 * @(#)2021-07-13 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;


/**
 * Description: 数据库表DMS_DOC_EXCHANGE_DYFORM的对应的DTO类
 *
 * @author yt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-13.1	yt		2021-07-13		Create
 * </pre>
 * @date 2021-07-13
 */
@ApiModel("公文交换文档展示单据")
public class DmsDocExchangeDyformDto implements Serializable {

    private static final long serialVersionUID = 1626145242905L;

    @ApiModelProperty("uuid")
    protected String uuid;

    // 动态表单定义uuid
    @ApiModelProperty(value = "动态表单定义uuid")
    private String dyformUuid;
    // 文档交换-配置UUID
    @ApiModelProperty(value = "文档交换-配置UUID", required = true)
    @NotBlank(message = "文档交换-配置UUID不能为空")
    private String dmsDocExchangeConfigUuid;
    // 表单定义JSON
    @ApiModelProperty(value = "表单定义JSON", required = true)
    @NotBlank(message = "表单定义JSON不能为空")
    private String definitionJson;

    @ApiModelProperty(value = "编辑类型（区分发件，收件表单）")
    private String editType;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the dyformUuid
     */
    public String getDyformUuid() {
        return this.dyformUuid;
    }

    /**
     * @param dyformUuid
     */
    public void setDyformUuid(String dyformUuid) {
        this.dyformUuid = dyformUuid;
    }

    /**
     * @return the dmsDocExchangeConfigUuid
     */
    public String getDmsDocExchangeConfigUuid() {
        return this.dmsDocExchangeConfigUuid;
    }

    /**
     * @param dmsDocExchangeConfigUuid
     */
    public void setDmsDocExchangeConfigUuid(String dmsDocExchangeConfigUuid) {
        this.dmsDocExchangeConfigUuid = dmsDocExchangeConfigUuid;
    }

    /**
     * @return the definitionJson
     */
    public String getDefinitionJson() {
        return this.definitionJson;
    }

    /**
     * @param definitionJson
     */
    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    public String getEditType() {
        return editType;
    }

    public void setEditType(String editType) {
        this.editType = editType;
    }
}
