/*
 * @(#)12/22/22 V1.0
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
 * 12/22/22.1	zhulh		12/22/22		Create
 * </pre>
 * @date 12/22/22
 */
@ApiModel("业务事项发起新业务事项关系")
@Entity
@Table(name = "BIZ_NEW_ITEM_RELATION")
@DynamicUpdate
@DynamicInsert
public class BizNewItemRelationEntity extends IdEntity {
    private static final long serialVersionUID = -1816179775838381515L;

    @ApiModelProperty("源事项实例UUID")
    private String sourceItemInstUuid;

    @ApiModelProperty("目标事项实例UUID")
    private String targetItemInstUuid;

    @ApiModelProperty("事项流实例UUID")
    private String itemFlowInstUuid;

    @ApiModelProperty("附加数据")
    private String extraData;

    /**
     * @return the sourceItemInstUuid
     */
    public String getSourceItemInstUuid() {
        return sourceItemInstUuid;
    }

    /**
     * @param sourceItemInstUuid 要设置的sourceItemInstUuid
     */
    public void setSourceItemInstUuid(String sourceItemInstUuid) {
        this.sourceItemInstUuid = sourceItemInstUuid;
    }

    /**
     * @return the targetItemInstUuid
     */
    public String getTargetItemInstUuid() {
        return targetItemInstUuid;
    }

    /**
     * @param targetItemInstUuid 要设置的targetItemInstUuid
     */
    public void setTargetItemInstUuid(String targetItemInstUuid) {
        this.targetItemInstUuid = targetItemInstUuid;
    }

    /**
     * @return the itemFlowInstUuid
     */
    public String getItemFlowInstUuid() {
        return itemFlowInstUuid;
    }

    /**
     * @param itemFlowInstUuid 要设置的itemFlowInstUuid
     */
    public void setItemFlowInstUuid(String itemFlowInstUuid) {
        this.itemFlowInstUuid = itemFlowInstUuid;
    }

    /**
     * @return the extraData
     */
    public String getExtraData() {
        return extraData;
    }

    /**
     * @param extraData 要设置的extraData
     */
    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}
