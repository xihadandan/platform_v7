/*
 * @(#)9/27/22 V1.0
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
 * Description: 业务流程事项实例实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@ApiModel("业务流程事项实例分发")
@Entity
@Table(name = "BIZ_PROCESS_ITEM_INST_DISPENSE")
@DynamicUpdate
@DynamicInsert
public class BizProcessItemInstanceDispenseEntity extends IdEntity {

    @ApiModelProperty("上级事项实例UUID")
    private String parentItemInstUuid;

    @ApiModelProperty("当前事项实例UUID")
    private String itemInstUuid;

    @ApiModelProperty("事项名称")
    private String itemName;

    @ApiModelProperty("事项编码")
    private String itemCode;

    @ApiModelProperty("排序号")
    private Integer sortOrder;

    @ApiModelProperty("完成状态，0未分发，1已分发")
    private Integer completionState;

    /**
     * @return the parentItemInstUuid
     */
    public String getParentItemInstUuid() {
        return parentItemInstUuid;
    }

    /**
     * @param parentItemInstUuid 要设置的parentItemInstUuid
     */
    public void setParentItemInstUuid(String parentItemInstUuid) {
        this.parentItemInstUuid = parentItemInstUuid;
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
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @param itemName 要设置的itemName
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * @return the itemCode
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * @param itemCode 要设置的itemCode
     */
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the completionState
     */
    public Integer getCompletionState() {
        return completionState;
    }

    /**
     * @param completionState 要设置的completionState
     */
    public void setCompletionState(Integer completionState) {
        this.completionState = completionState;
    }
}
