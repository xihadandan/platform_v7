/*
 * @(#)11/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
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
 * Description: 业务流程事项定义定义基本信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/13/23.1	zhulh		11/13/23		Create
 * </pre>
 * @date 11/13/23
 */
@ApiModel("业务流程事项定义基本信息")
@Entity
@Table(name = "BIZ_PROCESS_ITEM_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class BizProcessItemDefinitionEntity extends IdEntity {

    @ApiModelProperty("事项定义名称")
    private String itemDefName;

    @ApiModelProperty("事项定义ID")
    private String itemDefId;

    @ApiModelProperty("事项名称")
    private String itemName;

    @ApiModelProperty("事项编码")
    private String itemCode;

    @ApiModelProperty("事项ID，业务流程事项配置自动生成的ID")
    private String itemId;

    @ApiModelProperty("事项类型")
    private String itemType;

    @ApiModelProperty("排序号")
    private Integer sortOrder;

    @ApiModelProperty("过程节点ID")
    private String processNodeId;

    @ApiModelProperty("过程定义UUID")
    private String processDefUuid;

    @ApiModelProperty("引用的过程定义UUID")
    private String refProcessDefUuid;

    /**
     * @return the itemDefName
     */
    public String getItemDefName() {
        return itemDefName;
    }

    /**
     * @param itemDefName 要设置的itemDefName
     */
    public void setItemDefName(String itemDefName) {
        this.itemDefName = itemDefName;
    }

    /**
     * @return the itemDefId
     */
    public String getItemDefId() {
        return itemDefId;
    }

    /**
     * @param itemDefId 要设置的itemDefId
     */
    public void setItemDefId(String itemDefId) {
        this.itemDefId = itemDefId;
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
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId 要设置的itemId
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the itemType
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * @param itemType 要设置的itemType
     */
    public void setItemType(String itemType) {
        this.itemType = itemType;
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
     * @return the processNodeId
     */
    public String getProcessNodeId() {
        return processNodeId;
    }

    /**
     * @param processNodeId 要设置的processNodeId
     */
    public void setProcessNodeId(String processNodeId) {
        this.processNodeId = processNodeId;
    }

    /**
     * @return the processDefUuid
     */
    public String getProcessDefUuid() {
        return processDefUuid;
    }

    /**
     * @param processDefUuid 要设置的processDefUuid
     */
    public void setProcessDefUuid(String processDefUuid) {
        this.processDefUuid = processDefUuid;
    }

    /**
     * @return the refProcessDefUuid
     */
    public String getRefProcessDefUuid() {
        return refProcessDefUuid;
    }

    /**
     * @param refProcessDefUuid 要设置的refProcessDefUuid
     */
    public void setRefProcessDefUuid(String refProcessDefUuid) {
        this.refProcessDefUuid = refProcessDefUuid;
    }
}
