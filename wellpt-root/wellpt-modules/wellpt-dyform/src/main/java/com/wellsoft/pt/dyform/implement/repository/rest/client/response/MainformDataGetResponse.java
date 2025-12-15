/*
 * @(#)2019年8月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.client.response;

import com.wellsoft.pt.api.WellptResponse;

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
public class MainformDataGetResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1494912048386676742L;

    private Map<String, Object> data;

    /**
     * @return the data
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

}
