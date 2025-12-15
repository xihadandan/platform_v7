/*
 * @(#)10/20/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/20/22.1	zhulh		10/20/22		Create
 * </pre>
 * @date 10/20/22
 */
public class ItemIncludeItem extends BaseObject {
    private static final long serialVersionUID = -1959856449497362613L;

    private String itemName;

    private String itemCode;

    private String frontItemCode;

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
