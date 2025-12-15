/*
 * @(#)2019年9月3日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.dyform.implement.repository.FormField;
import com.wellsoft.pt.dyform.implement.repository.rest.client.request.FormFieldGetRequest;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.FormFieldGetResponse;
import com.wellsoft.pt.dyform.implement.repository.rest.client.support.RepositoryFormDataApiServiceNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service(RepositoryFormDataApiServiceNames.FORM_FIELD_GET)
public class FormFieldGetServiceImpl implements WellptService<FormFieldGetRequest> {

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FormFieldGetRequest request) {
        FormDefinitionHandler formDefinitionHandler = ((FormDefinition) dyFormFacade.getFormDefinitionById(request
                .getFormId())).doGetFormDefinitionHandler();

        List<String> fieldNames = formDefinitionHandler.getFieldNames();
        List<FormField> formFields = Lists.newArrayList();
        for (String fieldName : fieldNames) {
            FormField formField = new FormField();
            formField.setName(fieldName);
            formField.setLabel(fieldName);
        }
        FormFieldGetResponse response = new FormFieldGetResponse();
        response.setDataList(formFields);
        return response;
    }

}
