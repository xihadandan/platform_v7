/*
 * @(#)11/17/22 V1.0
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
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/17/22.1	zhulh		11/17/22		Create
 * </pre>
 * @date 11/17/22
 */
@ApiModel("业务集成")
@Entity
@Table(name = "BIZ_BUSINESS_INTEGRATION")
@DynamicUpdate
@DynamicInsert
public class BizBusinessIntegrationEntity extends IdEntity {
    private static final long serialVersionUID = 5671077281333808315L;

    @ApiModelProperty("业务集成类型，1工作流")
    private String type;

    @ApiModelProperty("事项实例UUID")
    private String itemInstUuid;

    @ApiModelProperty("业务实例UUID")
    private String bizInstUuid;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the itemInstUuid
     */
    public String getItemInstUuid() {
        return itemInstUuid;
    }

    /**
     * @param itemInstUuid 要设置的itemInstUuid
     */
    public void setItemInstUuid(String itemInstUuid) {
        this.itemInstUuid = itemInstUuid;
    }

    /**
     * @return the bizInstUuid
     */
    public String getBizInstUuid() {
        return bizInstUuid;
    }

    /**
     * @param bizInstUuid 要设置的bizInstUuid
     */
    public void setBizInstUuid(String bizInstUuid) {
        this.bizInstUuid = bizInstUuid;
    }
    
}
