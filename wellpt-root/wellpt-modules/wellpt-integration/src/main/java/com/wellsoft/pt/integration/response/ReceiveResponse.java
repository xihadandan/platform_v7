/*
 * @(#)2013-11-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.response;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-8.1	zhulh		2013-11-8		Create
 * </pre>
 * @date 2013-11-8
 */
public class ReceiveResponse extends Response {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5957158429408791212L;

    private String batchId;

    /**
     * @return the batchId
     */
    public String getBatchId() {
        return batchId;
    }

    /**
     * @param batchId 要设置的batchId
     */
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

}
