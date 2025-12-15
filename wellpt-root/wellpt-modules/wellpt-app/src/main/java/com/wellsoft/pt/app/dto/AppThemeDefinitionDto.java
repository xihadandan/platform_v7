/*
 * @(#)2/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dto;

import com.wellsoft.pt.app.entity.AppThemeDefinitionEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 应用主题DTO
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2/27/23.1	zhulh		2/27/23		Create
 * </pre>
 * @date 2/27/23
 */
@ApiModel("应用主题")
public class AppThemeDefinitionDto extends AppThemeDefinitionEntity {
    private static final long serialVersionUID = -4128501328928748582L;

    @ApiModelProperty("主题定义JSON")
    private String definitionJson;

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
}
