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
import com.wellsoft.pt.dyform.implement.repository.rest.client.request.MainformDataGetRequest;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.MainformDataGetResponse;
import com.wellsoft.pt.dyform.implement.repository.rest.client.support.RepositoryFormDataApiServiceNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service(RepositoryFormDataApiServiceNames.MAINFORM_DATA_GET)
public class MainformDataGetServiceImpl implements WellptService<MainformDataGetRequest> {

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
    public WellptResponse doService(MainformDataGetRequest request) {
        // 表单ID
        String formId = request.getFormId();
        String formUuid = dyFormFacade.getFormDefinitionById(formId).getUuid();
        // 数据UUID
        String dataUuid = request.getDataUuid();
        Map<String, Object> dataMap = formDataService.getFormDataOfMainform(formUuid, dataUuid);
        MainformDataGetResponse response = new MainformDataGetResponse();
        response.setData(dataMap);
        return response;
    }

}
