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
public abstract class WellptQueryResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7466658423337966377L;

    // 总条数
    private long total;
    // 第一条的索引号
    private long start;
    // 返回的条数
    private int size;

    /**
     * @return the total
     */
    public long getTotal() {
        return total;
    }

    /**
     * @param total 要设置的total
     */
    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * @param start 要设置的start
     */
    public void setStart(long start) {
        this.start = start;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size 要设置的size
     */
    public void setSize(int size) {
        this.size = size;
    }

}
