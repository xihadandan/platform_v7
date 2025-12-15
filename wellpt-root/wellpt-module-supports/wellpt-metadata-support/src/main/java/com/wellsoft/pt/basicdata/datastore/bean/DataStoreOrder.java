/*
 * @(#)2016年11月3日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.bean;

import com.wellsoft.pt.jpa.criterion.Order;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年11月3日.1	xiem		2016年11月3日		Create
 * </pre>
 * @date 2016年11月3日
 */
@ApiModel("排序信息")
public class DataStoreOrder {

    public static final String ASC = "asc";
    public static final String DESC = "desc";

    @ApiModelProperty("排序列")
    private String sortName;
    @ApiModelProperty("排序方式")
    private String sortOrder;

    /**
     *
     */
    public DataStoreOrder() {
        super();
    }

    /**
     * @param sortName
     * @param sortOrder
     */
    public DataStoreOrder(String sortName, boolean ascending) {
        super();
        this.sortName = sortName;
        this.sortOrder = ascending ? ASC : DESC;
    }

    /**
     * @return the sortName
     */
    public String getSortName() {
        return sortName;
    }

    /**
     * @param sortName 要设置的sortName
     */
    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    /**
     * @return the sortOrder
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Order toOrder() {
        if (ASC.equalsIgnoreCase(sortOrder)) {
            return Order.asc(sortName);
        }
        return Order.desc(sortName);
    }
}
