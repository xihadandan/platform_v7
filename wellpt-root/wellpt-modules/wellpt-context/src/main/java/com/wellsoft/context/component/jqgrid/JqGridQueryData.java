/*
 * @(#)2012-12-20 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.component.jqgrid;

import java.util.List;

/**
 * Description: JqGridQueryData.java
 *
 * @author zhulh
 * @date 2012-12-20
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-20.1		zhulh		2012-12-20		Create
 * </pre>
 */
public class JqGridQueryData {
    private List<?> dataList;
    private long totalPages;
    private int currentPage;

    private long totalRows;
    private boolean repeatitems = false;

    public List<?> getDataList() {
        return dataList;
    }

    public void setDataList(List<?> dataList) {
        this.dataList = dataList;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(long totalRows) {
        this.totalRows = totalRows;
    }

    public boolean isRepeatitems() {
        return repeatitems;
    }

    public void setRepeatitems(boolean repeatitems) {
        this.repeatitems = repeatitems;
    }

}
