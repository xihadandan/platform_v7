/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api;

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
public abstract class WellptQueryRequest<T extends WellptResponse> extends WellptRequest<T> {

    /**
     * 当前页，默认为1
     */
    private int pageNo = 1;

    /**
     * 分页记录个数，默认为20
     */
    private int pageSize = 20;

    /**
     * @return the pageNo
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * @param pageNo 要设置的pageNo
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize 要设置的pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
