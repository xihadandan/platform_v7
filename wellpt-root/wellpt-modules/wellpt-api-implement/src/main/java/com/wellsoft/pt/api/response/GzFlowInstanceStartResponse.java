/*
 * @(#)2015年7月22日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年7月22日.1	zhulh		2015年7月22日		Create
 * </pre>
 * @date 2015年7月22日
 */
public class GzFlowInstanceStartResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1394289547805380169L;

    // 流程实例UUID
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
