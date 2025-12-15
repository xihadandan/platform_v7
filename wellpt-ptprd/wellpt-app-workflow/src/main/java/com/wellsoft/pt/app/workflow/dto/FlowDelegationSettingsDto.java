/*
 * @(#)2018年8月14日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.dto;

import com.wellsoft.pt.bpm.engine.entity.FlowDelegationSettings;
import io.swagger.annotations.ApiModel;
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
 * 2018年8月14日.1	zhulh		2018年8月14日		Create
 * </pre>
 * @date 2018年8月14日
 */
@ApiModel("流程工作委托设置")
public class FlowDelegationSettingsDto extends FlowDelegationSettings {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6880478998129771257L;

    @ApiModelProperty("使用的常用委托UUID")
    private Long commonUuid;

    /**
     * @return the commonUuid
     */
    public Long getCommonUuid() {
        return commonUuid;
    }

    /**
     * @param commonUuid 要设置的commonUuid
     */
    public void setCommonUuid(Long commonUuid) {
        this.commonUuid = commonUuid;
    }
    
}
