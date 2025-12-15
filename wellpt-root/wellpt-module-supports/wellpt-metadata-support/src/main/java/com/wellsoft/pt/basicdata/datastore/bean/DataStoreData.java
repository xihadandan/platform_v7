/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.bean;

import com.wellsoft.context.jdbc.support.PagingInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年11月1日.1	xiem		2016年11月1日		Create
 * </pre>
 * @date 2016年11月1日
 */
@ApiModel("数据仓库查询结果")
public class DataStoreData {
    @ApiModelProperty("分页信息")
    private PagingInfo pagination;
    @ApiModelProperty("数据列表")
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

    /**
     * @return the pagination
     */
    public PagingInfo getPagination() {
        return pagination;
    }

    /**
     * @param pagination 要设置的pagination
     */
    public void setPagination(PagingInfo pagination) {
        this.pagination = pagination;
    }

    /**
     * @return the data
     */
    public List<Map<String, Object>> getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public void addData(Map<String, Object> d) {
        this.data.add(d);
    }

}
