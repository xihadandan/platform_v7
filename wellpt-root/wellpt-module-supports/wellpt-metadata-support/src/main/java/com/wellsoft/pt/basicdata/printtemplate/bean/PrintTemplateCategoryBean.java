/*
 * @(#)2021-10-15 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.bean;

import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("打印模版分类")
public class PrintTemplateCategoryBean extends PrintTemplateCategory {
    private static final long serialVersionUID = -1155586754316419210L;

    // 父结点编号
    @ApiModelProperty("父结点名称")
    private String parentName;

//	// 父结点UUID
//	@ApiModelProperty("父结点UUID")
//	private String parentUuid;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }


}
