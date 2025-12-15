/*
 * @(#)2019年8月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.client.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.dyform.implement.repository.FormData;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.FormDataSaveResponse;
import com.wellsoft.pt.dyform.implement.repository.rest.client.support.RepositoryFormDataApiServiceNames;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月29日.1	zhulh		2019年8月29日		Create
 * </pre>
 * @date 2019年8月29日
 */
public class FormDataSaveRequest extends WellptRequest<FormDataSaveResponse> {

    // 表单数据
    private FormData data;

    /**
     * @return the data
     */
    public FormData getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(FormData data) {
        this.data = data;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return RepositoryFormDataApiServiceNames.FORM_DATA_SAVE;
    }

}
