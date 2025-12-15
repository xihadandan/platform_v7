/*
 * @(#)2017年4月27日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support;

import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年4月27日.1	zhulh		2017年4月27日		Create
 * </pre>
 * @date 2017年4月27日
 */
@ApiModel("查询类型")
public class DataStoreProxy extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8035318757218680193L;

    @ApiModelProperty("数据仓库ID")
    private String storeId;

    @ApiModelProperty("类型")
    private String type;

    // 额外信息
    @ApiModelProperty("额外信息")
    private Map<String, Object> extras;

    /**
     * @return the storeId
     */
    public String getStoreId() {
        return storeId;
    }

    /**
     * @param storeId 要设置的storeId
     */
    public void setStoreId(String storeId) {
        this.storeId = storeId;
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

    /**
     * @return the extras
     */
    public Map<String, Object> getExtras() {
        return extras;
    }

    /**
     * @param extras 要设置的extras
     */
    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

}
