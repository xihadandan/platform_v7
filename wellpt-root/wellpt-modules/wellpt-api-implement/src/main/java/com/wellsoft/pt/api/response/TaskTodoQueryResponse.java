/*
 * @(#)2019年8月14日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptQueryResponse;
import com.wellsoft.pt.bpm.engine.query.api.TaskQueryItem;

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
 * 2019年8月14日.1	zhulh		2019年8月14日		Create
 * </pre>
 * @date 2019年8月14日
 */
public class TaskTodoQueryResponse extends WellptQueryResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5167962446418626761L;

    // 数据列表
    private List<TaskQueryItem> dataList;

    /**
     * @return the dataList
     */
    public List<TaskQueryItem> getDataList() {
        return dataList;
    }

    /**
     * @param dataList 要设置的dataList
     */
    public void setDataList(List<TaskQueryItem> dataList) {
        this.dataList = dataList;
    }

}
