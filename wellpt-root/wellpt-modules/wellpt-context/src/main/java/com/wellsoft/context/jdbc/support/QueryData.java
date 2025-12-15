/*
 * @(#)2013-2-1 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.jdbc.support;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 查询结果
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
public class QueryData implements Serializable {
    private static final long serialVersionUID = 5086653289160061076L;

    private List<?> dataList;
    private PagingInfo pagingInfo;

    /**
     * @return the dataList
     */
    public List<?> getDataList() {
        return dataList;
    }

    /**
     * @param dataList 要设置的dataList
     */
    public void setDataList(List<?> dataList) {
        this.dataList = dataList;
    }

    /**
     * @return the pagingInfo
     */
    public PagingInfo getPagingInfo() {
        return pagingInfo;
    }

    /**
     * @param pagingInfo 要设置的pagingInfo
     */
    public void setPagingInfo(PagingInfo pagingInfo) {
        this.pagingInfo = pagingInfo;
    }

}
