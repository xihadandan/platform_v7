/*
 * @(#)10/19/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.dto;

import com.wellsoft.pt.biz.entity.BizProcessItemOperationEntity;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/19/22.1	zhulh		10/19/22		Create
 * </pre>
 * @date 10/19/22
 */
public class BizProcessItemOperationDto extends BizProcessItemOperationEntity {
    private static final long serialVersionUID = -574824557058511238L;

    @ApiModelProperty("操作名称")
    private String operateName;

    /**
     * @return the operateName
     */
    public String getOperateName() {
        return operateName;
    }

    /**
     * @param operateName 要设置的operateName
     */
    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }
}
