/*
 * @(#)2015-1-23 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptQueryResponse;
import com.wellsoft.pt.api.domain.Notice;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-23.1	zhulh		2015-1-23		Create
 * </pre>
 * @date 2015-1-23
 */
public class NoticeQueryResponse extends WellptQueryResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6698925626312203506L;

    // 数据列表
    private List<Notice> dataList;

    /**
     * @return the dataList
     */
    public List<Notice> getDataList() {
        return dataList;
    }

    /**
     * @param dataList 要设置的dataList
     */
    public void setDataList(List<Notice> dataList) {
        this.dataList = dataList;
    }

}
