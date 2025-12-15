/*
 * @(#)2019年8月30日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.facade.service.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.dyform.implement.data.service.FormDataService;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo;
import com.wellsoft.pt.dyform.implement.repository.rest.client.request.FormDataCountRequest;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.FormDataCountResponse;
import com.wellsoft.pt.dyform.implement.repository.rest.client.support.RepositoryFormDataApiServiceNames;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service(RepositoryFormDataApiServiceNames.FORM_DATA_COUNT)
public class FormDataCountServiceImpl implements WellptService<FormDataCountRequest> {

    @Autowired
    private FormDataService formDataService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FormDataCountRequest request) {
        String formId = request.getFormId();
        FormDataQueryInfo formDataQueryInfo = new FormDataQueryInfo(formId);
        BeanUtils.copyProperties(request, formDataQueryInfo);
        long count = formDataService.count(formDataQueryInfo);

        // 响应数据
        FormDataCountResponse response = new FormDataCountResponse();
        response.setCount(count);
        return response;
    }

}
