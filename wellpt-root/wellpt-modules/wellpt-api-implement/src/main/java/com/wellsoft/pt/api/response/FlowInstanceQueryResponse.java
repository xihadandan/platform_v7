/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptQueryResponse;
import com.wellsoft.pt.api.domain.FlowInstance;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public class FlowInstanceQueryResponse extends WellptQueryResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8815108314696996349L;

    // 数据列表
    private List<FlowInstance> dataList;

    /**
     * @return the dataList
     */
    public List<FlowInstance> getDataList() {
        return dataList;
    }

    /**
     * @param dataList 要设置的dataList
     */
    public void setDataList(List<FlowInstance> dataList) {
        this.dataList = dataList;
    }

}
