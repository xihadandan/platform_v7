/*
 * @(#)10/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.dto;

import com.wellsoft.pt.biz.entity.BizBusinessIntegrationEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/18/22.1	zhulh		10/18/22		Create
 * </pre>
 * @date 10/18/22
 */
@ApiModel("业务流程事项实例")
public class BizProcessItemInstanceDto extends BizProcessItemInstanceEntity {
    private static final long serialVersionUID = 3016620181138881706L;

    @ApiModelProperty("业务集成信息")
    private List<BizBusinessIntegrationEntity> businessIntegrations;

    /**
     * @return the businessIntegrations
     */
    public List<BizBusinessIntegrationEntity> getBusinessIntegrations() {
        return businessIntegrations;
    }

    /**
     * @param businessIntegrations 要设置的businessIntegrations
     */
    public void setBusinessIntegrations(List<BizBusinessIntegrationEntity> businessIntegrations) {
        this.businessIntegrations = businessIntegrations;
    }
}
