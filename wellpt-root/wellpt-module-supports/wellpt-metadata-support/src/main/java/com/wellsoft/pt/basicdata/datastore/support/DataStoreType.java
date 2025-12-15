/*
 * @(#)2016年10月28日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月28日.1	xiem		2016年10月28日		Create
 * </pre>
 * @date 2016年10月28日
 */
public enum DataStoreType {
    TABLE("DATA_STORE_TYPE_TABLE"), VIEW("DATA_STORE_TYPE_VIEW"), ENTITY("DATA_STORE_TYPE_ENTITY"), SQL(
            "DATA_STORE_TYPE_SQL"), NAME_QUERY("DATA_STORE_TYPE_NAME_QUERY"), DATA_INTERFACE(
            "DATA_STORE_TYPE_DATA_INTERFACE");
    private String type;

    private DataStoreType(String type) {
        this.setType(type);
    }

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
}
