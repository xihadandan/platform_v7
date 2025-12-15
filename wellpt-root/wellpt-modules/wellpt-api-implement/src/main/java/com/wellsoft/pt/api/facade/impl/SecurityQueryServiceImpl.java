/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.SecurityQueryRequest;
import com.wellsoft.pt.api.response.SecurityQueryResponse;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 权限查询
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-9-14.1  zhengky	2014-9-14	  Create
 * </pre>
 * @date 2014-9-14
 */
@Service(ApiServiceName.SECURITY_QUERY)
@Transactional
public class SecurityQueryServiceImpl extends BaseServiceImpl implements WellptService<SecurityQueryRequest> {

    @Autowired
    private com.wellsoft.pt.security.audit.webservice.PrivilegeQueryService privilegeQueryService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#getResponse(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(SecurityQueryRequest queryRequest) {
        SecurityQueryResponse response = new SecurityQueryResponse();
        String[] codes = queryRequest.getModuleCode().split(";");

        JSONObject privilegejson = privilegeQueryService.getUserPrivileges(queryRequest.getUserLoginName(), codes);

        response.setData(privilegejson.toString());
        return response;
    }

}
