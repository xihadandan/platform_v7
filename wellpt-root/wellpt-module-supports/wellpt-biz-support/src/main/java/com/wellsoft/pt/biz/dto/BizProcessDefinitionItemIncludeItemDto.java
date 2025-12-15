/*
 * @(#)10/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.dto;

import com.wellsoft.pt.biz.support.ItemIncludeItem;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/27/22.1	zhulh		10/27/22		Create
 * </pre>
 * @date 10/27/22
 */
public class BizProcessDefinitionItemIncludeItemDto extends ItemIncludeItem {

    private String itemDefName;

    private String itemDefId;

    private String itemType;

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
}
