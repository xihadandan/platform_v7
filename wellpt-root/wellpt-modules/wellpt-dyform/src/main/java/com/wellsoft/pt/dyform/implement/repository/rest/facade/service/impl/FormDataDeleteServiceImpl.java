/*
 * @(#)2019年8月30日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.facade.service.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.service.FormDataService;
import com.wellsoft.pt.dyform.implement.repository.rest.client.request.FormDataDeleteRequest;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.FormDataDeleteResponse;
import com.wellsoft.pt.dyform.implement.repository.rest.client.support.RepositoryFormDataApiServiceNames;
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
@Service(RepositoryFormDataApiServiceNames.FORM_DATA_DELETE)
public class FormDataDeleteServiceImpl implements WellptService<FormDataDeleteRequest> {

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private FormDataService formDataService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(FormDataDeleteRequest request) {
        String formId = request.getFormId();
        String formUuid = dyFormFacade.getFormDefinitionById(formId).getUuid();
        String dataUuid = request.getDataUuid();
        boolean cascade = request.isCascade();
        // 级联删除从表数据
        if (cascade) {
            formDataService.delFullFormData(formUuid, dataUuid);
        } else {
            formDataService.delFormData(formUuid, dataUuid);
        }
        return new FormDataDeleteResponse();
    }

}
