/*
 * @(#)2021-07-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;


/**
 * Description: 数据库表DMS_DOC_EXCHANGE_FIELD_VER的对应的DTO类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-22.1	leo		2021-07-22		Create
 * </pre>
 * @date 2021-07-22
 */
@ApiModel("公文交换字段版本")
public class DmsDocExchangeFieldVerDto implements Serializable {

    private static final long serialVersionUID = 1626918429290L;

    // 字段版本
    @ApiModelProperty(value = "字段版本", required = true)
    @NotBlank(message = "字段版本不能为空")
    private String version;
    // 已读用户Id
    @ApiModelProperty(value = "已读用户Id", required = true)
    @NotBlank(message = "已读用户Id不能为空")
    private String userId;

    /**
     * @return the userId
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
