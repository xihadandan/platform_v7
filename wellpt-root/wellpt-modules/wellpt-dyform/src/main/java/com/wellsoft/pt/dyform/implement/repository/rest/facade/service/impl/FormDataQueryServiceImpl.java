/*
 * @(#)2019年8月30日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.facade.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.service.FormDataService;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryData;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo;
import com.wellsoft.pt.dyform.implement.repository.rest.client.request.FormDataQueryRequest;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.FormDataQueryResponse;
import com.wellsoft.pt.dyform.implement.repository.rest.client.support.RepositoryFormDataApiServiceNames;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
 * 2019年8月30日.1	zhulh		2019年8月30日		Create
 * </pre>
 * @date 2019年8月30日
 */
@Service(RepositoryFormDataApiServiceNames.FORM_DATA_QUERY)
public class FormDataQueryServiceImpl implements WellptService<FormDataQueryRequest> {

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private FormDataService formDataService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @SuppressWarnings("unchecked")
    @Override
    public WellptResponse doService(FormDataQueryRequest request) {
        FormDataQueryInfo formDataQueryInfo = new FormDataQueryInfo(request.getFormId());
        BeanUtils.copyProperties(request, formDataQueryInfo);
        FormDataQueryData formDataQueryData = formDataService.query(formDataQueryInfo);
        FormDataQueryResponse response = new FormDataQueryResponse();
        response.setDataList((List<Map<String, Object>>) formDataQueryData.getDataList());
        PagingInfo pagingInfo = formDataQueryData.getPagingInfo();
        if (pagingInfo != null) {
            response.setStart(pagingInfo.getFirst());
            response.setSize(pagingInfo.getPageSize());
            response.setTotal(pagingInfo.getTotalCount());
        }
        return response;
    }

}
