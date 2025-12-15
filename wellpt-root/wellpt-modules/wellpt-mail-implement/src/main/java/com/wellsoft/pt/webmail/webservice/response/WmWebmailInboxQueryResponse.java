/*
 * @(#)2016年7月18日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.webservice.response;

import com.wellsoft.pt.api.WellptQueryResponse;

/**
 * Description: 收件箱查询响应
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月18日.1	zhulh		2016年7月18日		Create
 * </pre>
 * @date 2016年7月18日
 */
public class WmWebmailInboxQueryResponse extends WellptQueryResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1386875992125098842L;

    private Object dataList;

    /**
     * @return the dataList
     */
    public Object getDataList() {
        return dataList;
    }

    /**
     * @param dataList 要设置的dataList
     */
    public void setDataList(Object dataList) {
        this.dataList = dataList;
    }

}
