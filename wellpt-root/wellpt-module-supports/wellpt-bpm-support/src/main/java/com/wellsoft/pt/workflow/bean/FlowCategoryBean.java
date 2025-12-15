/*
 * @(#)2012-12-3 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.bean;

import com.wellsoft.pt.workflow.entity.FlowCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 流程分类值对象类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-3.1	zhulh		2012-12-3		Create
 * </pre>
 * @date 2012-12-3
 */
@ApiModel("流程分类")
public class FlowCategoryBean extends FlowCategory {
    private static final long serialVersionUID = -1155586754316419210L;

    // 父结点编号
    @ApiModelProperty("父结点编号")
    private String parentName;

    // 父结点UUID
    @ApiModelProperty("父结点UUID")
    private String parentUuid;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }


}
