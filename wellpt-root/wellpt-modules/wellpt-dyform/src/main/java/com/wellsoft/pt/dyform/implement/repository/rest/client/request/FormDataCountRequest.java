/*
 * @(#)2019年8月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.client.request;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.FormDataCountResponse;
import com.wellsoft.pt.dyform.implement.repository.rest.client.support.RepositoryFormDataApiServiceNames;

import java.util.List;
import java.util.Map;

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
public class FormDataCountRequest extends WellptRequest<FormDataCountResponse> {

    private String formId;

    private boolean distinct;

    private List<Condition> conditions = Lists.newArrayListWithCapacity(0);

    private Map<String, Object> queryParams = Maps.newHashMapWithExpectedSize(0);

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
     * @return the distinct
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * @param distinct 要设置的distinct
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * @return the conditions
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * @param conditions 要设置的conditions
     */
    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    /**
     * @return the queryParams
     */
    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    /**
     * @param queryParams 要设置的queryParams
     */
    public void setQueryParams(Map<String, Object> queryParams) {
        this.queryParams = queryParams;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return RepositoryFormDataApiServiceNames.FORM_DATA_COUNT;
    }

}
