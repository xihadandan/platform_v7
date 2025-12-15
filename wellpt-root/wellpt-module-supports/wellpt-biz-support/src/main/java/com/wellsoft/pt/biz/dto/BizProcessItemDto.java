/*
 * @(#)10/12/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.dto;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description: 业务事项传输对象
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/12/22.1	zhulh		10/12/22		Create
 * </pre>
 * @date 10/12/22
 */
@ApiModel("业务事项传输对象")
public class BizProcessItemDto extends BaseObject {
    private static final long serialVersionUID = 3352088711569702100L;

    @ApiModelProperty("业务事项ID")
    private String id;

    @ApiModelProperty("事项定义名称")
    private String itemDefName;

    @ApiModelProperty("事项定义ID")
    private String itemDefId;

    @ApiModelProperty("事项名称")
    private String itemName;

    @ApiModelProperty("事项编码")
    private String itemCode;

    @ApiModelProperty("事项类型")
    private String itemType;

//    @ApiModelProperty("是否分发事项")
//    private boolean dispenseItem;

    @ApiModelProperty("并联事项包含的事项")
    private List<ItemIncludeItemDto> includeItemDtos;

    @ApiModelProperty("互斥事项")
    private List<ProcessItemConfig.ItemMutexItemConfig> mutexItems;

    @ApiModelProperty("关联事项")
    private List<ProcessItemConfig.ItemRelatedItemConfig> relatedItems;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

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

//    /**
//     * @return
//     */
//    public boolean isDispenseItem() {
//        return dispenseItem;
//    }
//
//    /**
//     * @param dispenseItem
//     */
//    public void setDispenseItem(boolean dispenseItem) {
//        this.dispenseItem = dispenseItem;
//    }

    /**
     * @return the includeItemDtos
     */
    public List<ItemIncludeItemDto> getIncludeItemDtos() {
        return includeItemDtos;
    }

    /**
     * @param includeItemDtos 要设置的includeItemDtos
     */
    public void setIncludeItemDtos(List<ItemIncludeItemDto> includeItemDtos) {
        this.includeItemDtos = includeItemDtos;
    }

    /**
     * @return the mutexItems
     */
    public List<ProcessItemConfig.ItemMutexItemConfig> getMutexItems() {
        return mutexItems;
    }

    /**
     * @param mutexItems 要设置的mutexItems
     */
    public void setMutexItems(List<ProcessItemConfig.ItemMutexItemConfig> mutexItems) {
        this.mutexItems = mutexItems;
    }

    /**
     * @return the relatedItems
     */
    public List<ProcessItemConfig.ItemRelatedItemConfig> getRelatedItems() {
        return relatedItems;
    }

    /**
     * @param relatedItems 要设置的relatedItems
     */
    public void setRelatedItems(List<ProcessItemConfig.ItemRelatedItemConfig> relatedItems) {
        this.relatedItems = relatedItems;
    }

    public static class ItemIncludeItemDto extends BaseObject {

        private static final long serialVersionUID = -641490892220342132L;

        @ApiModelProperty("业务事项ID")
        private String id;

        @ApiModelProperty("事项名称")
        private String itemName;

        @ApiModelProperty("事项编码")
        private String itemCode;

        @ApiModelProperty("前置事项编码")
        private String frontItemCode;

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
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
         * @return the frontItemCode
         */
        public String getFrontItemCode() {
            return frontItemCode;
        }

        /**
         * @param frontItemCode 要设置的frontItemCode
         */
        public void setFrontItemCode(String frontItemCode) {
            this.frontItemCode = frontItemCode;
        }
    }
}
