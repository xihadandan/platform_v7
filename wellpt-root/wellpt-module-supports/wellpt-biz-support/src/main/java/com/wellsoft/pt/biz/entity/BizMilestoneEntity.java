/*
 * @(#)11/18/22 V1.0
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
 * 11/18/22.1	zhulh		11/18/22		Create
 * </pre>
 * @date 11/18/22
 */
@ApiModel("里程碑事件")
@Entity
@Table(name = "BIZ_MILESTONE")
@DynamicUpdate
@DynamicInsert
public class BizMilestoneEntity extends IdEntity {
    private static final long serialVersionUID = -3527164334442004640L;

    @ApiModelProperty("里程碑事件名称")
    private String name;

    @ApiModelProperty("事项实例UUID")
    private String itemInstUuid;

    @ApiModelProperty("里程碑事件描述")
    private String remark;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
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
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
