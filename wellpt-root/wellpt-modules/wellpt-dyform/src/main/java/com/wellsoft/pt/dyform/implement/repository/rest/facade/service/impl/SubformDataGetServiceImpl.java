/*
 * @(#)2019年8月30日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.rest.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.service.FormDataService;
import com.wellsoft.pt.dyform.implement.repository.rest.client.request.SubformDataGetRequest;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.SubformDataGetResponse;
import com.wellsoft.pt.dyform.implement.repository.rest.client.support.RepositoryFormDataApiServiceNames;
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
@Service(RepositoryFormDataApiServiceNames.SUBFORM_DATA_GET)
public class SubformDataGetServiceImpl implements WellptService<SubformDataGetRequest> {

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
    public WellptResponse doService(SubformDataGetRequest request) {
        // 从表ID
        String subformId = request.getSubformId();
        String subformUuid = dyFormFacade.getFormDefinitionById(subformId).getUuid();
        // 主表ID
        String mainformId = request.getMainformId();
        String mainformUuid = dyFormFacade.getFormDefinitionById(mainformId).getUuid();

        // 主表数据UUID
        String mainformDataUuid = request.getMainformDataUuid();
        QueryData queryData = formDataService.getFormDataOfParentNode(subformUuid, mainformUuid, mainformDataUuid, false);

        SubformDataGetResponse response = new SubformDataGetResponse();
        if (queryData != null && queryData.getDataList() != null) {
            response.setDataList((List<Map<String, Object>>) queryData.getDataList());
        } else {
            List<Map<String, Object>> dataList = Lists.newArrayListWithCapacity(0);
            response.setDataList(dataList);
        }
        return response;
    }

}
