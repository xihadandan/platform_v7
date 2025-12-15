/*
 * @(#)2019年9月3日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.client.response;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.dyform.implement.repository.FormField;

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
 * 2019年9月3日.1	zhulh		2019年9月3日		Create
 * </pre>
 * @date 2019年9月3日
 */
public class FormFieldGetResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3944792646987018035L;

    private List<FormField> dataList;

    /**
     * @return the dataList
     */
    public List<FormField> getDataList() {
        return dataList;
    }

    /**
     * @param dataList 要设置的dataList
     */
    public void setDataList(List<FormField> dataList) {
        this.dataList = dataList;
    }

}
