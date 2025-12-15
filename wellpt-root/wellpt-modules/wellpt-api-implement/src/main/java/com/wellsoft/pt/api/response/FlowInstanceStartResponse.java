/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * Description: 启动流程定义
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
public class FlowInstanceStartResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5759938714189519908L;

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
