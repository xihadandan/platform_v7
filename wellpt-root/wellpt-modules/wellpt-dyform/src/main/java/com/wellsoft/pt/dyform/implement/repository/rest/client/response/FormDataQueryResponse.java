/*
 * @(#)2019年8月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.client.response;

import com.wellsoft.pt.api.WellptQueryResponse;

import java.util.List;
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
 * 2019年8月29日.1	zhulh		2019年8月29日		Create
 * </pre>
 * @date 2019年8月29日
 */
public class FormDataQueryResponse extends WellptQueryResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6138826144489361312L;

    // 数据列表
    private List<Map<String, Object>> dataList;

    /**
     * @return the dataList
     */
    public List<Map<String, Object>> getDataList() {
        return dataList;
    }

    /**
     * @param dataList 要设置的dataList
     */
    public void setDataList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
    }

}
