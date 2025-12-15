/*
 * @(#)10/14/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 事项数据
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/14/22.1	zhulh		10/14/22		Create
 * </pre>
 * @date 10/14/22
 */
public class ItemData extends BaseObject {
    private static final long serialVersionUID = 4969916340399431704L;

    private String itemUuid;

    private String itemName;

    private String itemCode;

    private String itemType;

    private String itemDefName;

    private String itemDefId;

    private String itemDefUuid;

    /**
     * @return the itemUuid
     */
    public String getItemUuid() {
        return itemUuid;
    }

    /**
     * @param itemUuid 要设置的itemUuid
     */
    public void setItemUuid(String itemUuid) {
        this.itemUuid = itemUuid;
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
     * @return the itemDefUuid
     */
    public String getItemDefUuid() {
        return itemDefUuid;
    }

    /**
     * @param itemDefUuid 要设置的itemDefUuid
     */
    public void setItemDefUuid(String itemDefUuid) {
        this.itemDefUuid = itemDefUuid;
    }
}
