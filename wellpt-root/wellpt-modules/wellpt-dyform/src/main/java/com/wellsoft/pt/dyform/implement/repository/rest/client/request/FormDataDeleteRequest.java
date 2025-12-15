/*
 * @(#)2019年8月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.client.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.FormDataDeleteResponse;
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
public class FormDataDeleteRequest extends WellptRequest<FormDataDeleteResponse> {
    // 表单ID
    private String formId;

    // 数据UUID
    private String dataUuid;

    // 是否级联删除从表数据
    private boolean cascade;

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
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the cascade
     */
    public boolean isCascade() {
        return cascade;
    }

    /**
     * @param cascade 要设置的cascade
     */
    public void setCascade(boolean cascade) {
        this.cascade = cascade;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return RepositoryFormDataApiServiceNames.FORM_DATA_DELETE;
    }

}
