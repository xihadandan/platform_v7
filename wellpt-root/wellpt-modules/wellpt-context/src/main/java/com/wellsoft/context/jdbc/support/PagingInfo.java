/*
 * @(#)2013-2-1 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.jdbc.support;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-1.1	zhulh		2013-2-1		Create
 * </pre>
 * @date 2013-2-1
 */
@ApiModel("分页信息")
public class PagingInfo implements Serializable {

    private static final long serialVersionUID = 1916174216512069711L;
    @ApiModelProperty("总条数")
    protected long totalCount = 0;
    @ApiModelProperty("是否自动计算总条数")
    protected boolean autoCount = true;
    @ApiModelProperty("当前页码")
    private int currentPage;
    @ApiModelProperty("每页大小")
    private int pageSize;
    // 分页查询开始记录位置
    @ApiModelProperty("分页查询开始记录位置")
    private int first;
    /**
     * 第一页 开始查询时间 由后端生成返回，第二页开始需要前端 携带 传入
     * 以防 翻页过程中 有新增数据 导致 重复展示
     */
    @ApiModelProperty("第一页 开始查询时间 由后端生成返回，第二页开始需要前端 携带 传入，以防 翻页过程中 有新增数据 导致 重复展示")
    private Date firstTime;

    public PagingInfo() {

    }

    public PagingInfo(int currentPage, int pageSize) {
        this(currentPage, pageSize, false);
    }

    public PagingInfo(int currentPage, int pageSize, boolean autoCount) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.autoCount = autoCount;

        calculateFirstResult();
    }

    public Date getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(Date firstTime) {
        this.firstTime = firstTime;
    }

    /**
     * @return the currentPage
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * @param currentPage the currentPage to set
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;

        calculateFirstResult();
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;

        calculateFirstResult();
    }

    /**
     * @return the totalCount
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount 要设置的totalCount
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the firstResult
     */
    public void calculateFirstResult() {
        if (currentPage <= 0) {
            first = 0;
            return;
        }
        first = pageSize * (currentPage - 1);
    }

    /**
     * @return the first
     */
    public int getFirst() {
        if (first == 0) {
            calculateFirstResult();
        }
        return first;
    }

    /**
     * @param first 要设置的first
     */
    public void setFirst(int first) {
        this.first = first;
    }

    /**
     * @return the autoCount
     */
    public boolean isAutoCount() {
        return autoCount;
    }

    /**
     * @param autoCount 要设置的autoCount
     */
    public void setAutoCount(boolean autoCount) {
        this.autoCount = autoCount;
    }

    /**
     * @return
     */
    @ApiModelProperty("总页数")
    public long getTotalPages() {
        if (totalCount <= 0 || pageSize <= 0) {
            return 0;
        }
        return (totalCount - 1) / pageSize + 1;
    }
}
