/*
 * @(#)2019年8月21日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.api.BearerTokenBasedWellptClient;
import com.wellsoft.pt.api.WellptClient;
import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryData;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo;
import com.wellsoft.pt.dyform.implement.repository.rest.client.request.*;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.*;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
 * 2019年8月21日.1	zhulh		2019年8月21日		Create
 * </pre>
 * @date 2019年8月21日
 */
@Component
public class RestApiFormRepository extends AbstractFormRepository {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#getDataOfMainform(java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public Map<String, Object> getDataOfMainform(String formId, String dataUuid, FormRepositoryContext repositoryContext) {
        WellptClient wellptClient = getServiceClient(repositoryContext);
        MainformDataGetRequest request = new MainformDataGetRequest();
        request.setFormId(formId);
        request.setDataUuid(dataUuid);
        // 请求日志
        logRequest(request, repositoryContext);
        MainformDataGetResponse response = wellptClient.execute(request);
        // 响应日志
        logResponse(response, repositoryContext);
        // 执行失败抛出异常
        throwExceptionIfRequire(response);
        return response.getData();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#getDataOfSubform(java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public List<Map<String, Object>> getDataOfSubform(String subformId, String mainformId, String mainformDataUuid,
                                                      FormRepositoryContext repositoryContext) {
        WellptClient wellptClient = getServiceClient(repositoryContext);
        SubformDataGetRequest request = new SubformDataGetRequest();
        request.setSubformId(subformId);
        request.setMainformId(mainformId);
        request.setMainformDataUuid(mainformDataUuid);
        // 请求日志
        logRequest(request, repositoryContext);
        SubformDataGetResponse response = wellptClient.execute(request);
        // 响应日志
        logResponse(response, repositoryContext);
        // 执行失败抛出异常
        throwExceptionIfRequire(response);
        return response.getDataList();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#saveFormData(com.wellsoft.pt.dyform.implement.repository.FormData, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public String saveFormData(FormData formData, FormRepositoryContext repositoryContext) {
        WellptClient wellptClient = getServiceClient(repositoryContext);
        FormDataSaveRequest request = new FormDataSaveRequest();
        request.setData(formData);
        // 请求日志
        logRequest(request, repositoryContext);
        FormDataSaveResponse response = wellptClient.execute(request);
        // 响应日志
        logResponse(response, repositoryContext);
        // 执行失败抛出异常
        throwExceptionIfRequire(response);
        return response.getDataUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#query(com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public FormDataQueryData query(FormDataQueryInfo queryInfo, FormRepositoryContext repositoryContext) {
        if (StringUtils.isBlank(SpringSecurityUtils.getCurrentUserId())) {
            FormDataQueryData queryData = new FormDataQueryData();
            queryData.setDataList(Collections.emptyList());
            return queryData;
        }
        WellptClient wellptClient = getServiceClient(repositoryContext);
        FormDataQueryRequest request = new FormDataQueryRequest();
        BeanUtils.copyProperties(queryInfo, request);
        // 请求日志
        logRequest(request, repositoryContext);
        FormDataQueryResponse response = wellptClient.execute(request);
        // 响应日志
        logResponse(response, repositoryContext);
        // 执行失败抛出异常
        throwExceptionIfRequire(response);
        FormDataQueryData queryData = new FormDataQueryData();
        queryData.setDataList(response.getDataList());
        PagingInfo pagingInfo = queryInfo.getPagingInfo();
        if (pagingInfo != null) {
            pagingInfo.setTotalCount(response.getTotal());
            queryData.setPagingInfo(pagingInfo);
        }
        return queryData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#count(com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public long count(FormDataQueryInfo queryInfo, FormRepositoryContext repositoryContext) {
        WellptClient wellptClient = getServiceClient(repositoryContext);
        FormDataCountRequest request = new FormDataCountRequest();
        BeanUtils.copyProperties(queryInfo, request);
        // 请求日志
        logRequest(request, repositoryContext);
        FormDataCountResponse response = wellptClient.execute(request);
        // 响应日志
        logResponse(response, repositoryContext);
        // 执行失败抛出异常
        throwExceptionIfRequire(response);
        return response.getCount();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#deleteFormData(java.lang.String, java.lang.String, boolean, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public void deleteFormData(String formId, String dataUuid, boolean cascade, FormRepositoryContext repositoryContext) {
        WellptClient wellptClient = getServiceClient(repositoryContext);
        FormDataDeleteRequest request = new FormDataDeleteRequest();
        request.setFormId(formId);
        request.setDataUuid(dataUuid);
        request.setCascade(cascade);
        // 请求日志
        logRequest(request, repositoryContext);
        FormDataDeleteResponse response = wellptClient.execute(request);
        // 响应日志
        logResponse(response, repositoryContext);
        // 执行失败抛出异常
        throwExceptionIfRequire(response);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#deleteSubformData(java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public void deleteSubformData(String mainformId, String mainformDataUuid, FormRepositoryContext repositoryContext) {
        WellptClient wellptClient = getServiceClient(repositoryContext);
        SubformDataDeleteRequest request = new SubformDataDeleteRequest();
        request.setMainformId(mainformId);
        request.setMainformDataUuid(mainformDataUuid);
        // 请求日志
        logRequest(request, repositoryContext);
        SubformDataDeleteResponse response = wellptClient.execute(request);
        // 响应日志
        logResponse(response, repositoryContext);
        // 执行失败抛出异常
        throwExceptionIfRequire(response);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#deleteSubformData(java.lang.String, java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public void deleteSubformData(String mainformId, String mainformDataUuid, String subformId,
                                  FormRepositoryContext repositoryContext) {
        WellptClient wellptClient = getServiceClient(repositoryContext);
        SubformDataDeleteRequest request = new SubformDataDeleteRequest();
        request.setMainformId(mainformId);
        request.setMainformDataUuid(mainformDataUuid);
        request.setSubformId(subformId);
        // 请求日志
        logRequest(request, repositoryContext);
        SubformDataDeleteResponse response = wellptClient.execute(request);
        // 响应日志
        logResponse(response, repositoryContext);
        // 执行失败抛出异常
        throwExceptionIfRequire(response);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#getFormFields(java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public List<FormField> getFormFields(String formId, FormRepositoryContext repositoryContext) {
        WellptClient wellptClient = getServiceClient(repositoryContext);
        FormFieldGetRequest request = new FormFieldGetRequest();
        request.setFormId(formId);
        // 请求日志
        logRequest(request, repositoryContext);
        FormFieldGetResponse response = wellptClient.execute(request);
        // 响应日志
        logResponse(response, repositoryContext);
        // 执行失败抛出异常
        throwExceptionIfRequire(response);
        return response.getDataList();
    }

    /**
     * @param request
     * @param repositoryContext
     */
    private void logRequest(WellptRequest<?> request, FormRepositoryContext repositoryContext) {
        if (logger.isInfoEnabled()) {
            logger.info("request data {} to {}", JsonUtils.object2Json(request), repositoryContext.getServiceUrl());
        }
    }

    /**
     * @param response
     * @param repositoryContext
     */
    private void logResponse(WellptResponse response, FormRepositoryContext repositoryContext) {
        if (logger.isInfoEnabled()) {
            logger.info("response data {} from {}", JsonUtils.object2Json(response), repositoryContext.getServiceUrl());
        }
    }

    /**
     * @param response
     */
    private void throwExceptionIfRequire(WellptResponse response) {
        if (response == null) {
            throw new RuntimeException("REST接口返回数据解析错误!");
        }
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getMsg());
        }
    }

    /**
     * @return
     */
    protected WellptClient getServiceClient(FormRepositoryContext repositoryContext) {
        String serverUrl = repositoryContext.getServiceUrl();
        String tenantId = SpringSecurityUtils.getCurrentTenantId();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String accessToken = repositoryContext.getServiceToken();
        return new BearerTokenBasedWellptClient(serverUrl, tenantId, userId, accessToken);
    }

}
