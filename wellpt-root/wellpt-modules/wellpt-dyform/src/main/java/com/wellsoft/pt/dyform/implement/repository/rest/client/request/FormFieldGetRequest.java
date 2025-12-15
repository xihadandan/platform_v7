/*
 * @(#)2019年9月3日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.client.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.FormFieldGetResponse;
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
 * 2019年9月3日.1	zhulh		2019年9月3日		Create
 * </pre>
 * @date 2019年9月3日
 */
public class FormFieldGetRequest extends WellptRequest<FormFieldGetResponse> {

    private String formId;

    /**
     * @return the formId
     */
    public String getFormId() {
        return formId;
    }

    /**
     * @param formId 要设置的formId
     */
    public void setFormId(String formId) {
        this.formId = formId;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return RepositoryFormDataApiServiceNames.FORM_FIELD_GET;
    }

}
