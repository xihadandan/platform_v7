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
import com.wellsoft.pt.dyform.implement.repository.rest.client.request.SubformDataDeleteRequest;
import com.wellsoft.pt.dyform.implement.repository.rest.client.response.SubformDataDeleteResponse;
import com.wellsoft.pt.dyform.implement.repository.rest.client.support.RepositoryFormDataApiServiceNames;
import org.apache.commons.lang.StringUtils;
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
@Service(RepositoryFormDataApiServiceNames.SUBFORM_DATA_DELETE)
public class SubformDataDeleteServiceImpl implements WellptService<SubformDataDeleteRequest> {

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
    public WellptResponse doService(SubformDataDeleteRequest request) {
        // 主表ID
        String mainformId = request.getMainformId();
        String mainformUuid = dyFormFacade.getFormDefinitionById(mainformId).getUuid();
        // 主表数据UUID
        String mainformDataUuid = request.getMainformDataUuid();
        // 从表ID
        String subformId = request.getSubformId();
        String subformUuid = null;
        if (StringUtils.isNotBlank(subformId)) {
            subformUuid = dyFormFacade.getFormDefinitionById(subformId).getUuid();
        }

        // 删除指定从表所有数据
        if (StringUtils.isNotBlank(subformUuid)) {
            formDataService.delFullSubFormData(mainformUuid, mainformDataUuid, subformUuid);
        } else {
            // 删除所有从表数据
            formDataService.delFullSubFormData(mainformUuid, mainformDataUuid);
        }
        return new SubformDataDeleteResponse();
    }

}
