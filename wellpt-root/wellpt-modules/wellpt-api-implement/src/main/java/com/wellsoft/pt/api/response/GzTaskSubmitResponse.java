/*
 * @(#)2015-7-17 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-7-17.1	zhulh		2015-7-17		Create
 * </pre>
 * @date 2015-7-17
 */
public class GzTaskSubmitResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3166250537009730010L;

    private Object data;

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(Object data) {
        this.data = data;
    }

}
