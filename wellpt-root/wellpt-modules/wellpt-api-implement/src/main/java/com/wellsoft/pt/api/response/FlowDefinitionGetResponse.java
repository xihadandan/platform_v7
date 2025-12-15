/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.domain.FlowDefinition;

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
public class FlowDefinitionGetResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6481026546983370717L;

    private FlowDefinition data;

    /**
     * @return the data
     */
    public FlowDefinition getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(FlowDefinition data) {
        this.data = data;
    }

}
