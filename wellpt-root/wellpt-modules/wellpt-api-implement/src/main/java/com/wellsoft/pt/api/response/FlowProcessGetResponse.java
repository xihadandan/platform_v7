/*
 * @(#)2014-9-27 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.workflow.work.bean.WorkProcessBean;

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
 * 2014-9-27.1	zhulh		2014-9-27		Create
 * </pre>
 * @date 2014-9-27
 */
public class FlowProcessGetResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2964137496780567545L;

    // 办理过程数据列表
    private List<WorkProcessBean> dataList;

    /**
     * @return the dataList
     */
    public List<WorkProcessBean> getDataList() {
        return dataList;
    }

    /**
     * @param dataList 要设置的dataList
     */
    public void setDataList(List<WorkProcessBean> dataList) {
        this.dataList = dataList;
    }

}
