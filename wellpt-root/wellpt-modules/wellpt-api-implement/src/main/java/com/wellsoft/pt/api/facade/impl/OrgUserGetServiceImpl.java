/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.OrgUserGetRequest;
import com.wellsoft.pt.api.response.OrgUserGetResponse;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.bean.UserBean;
import com.wellsoft.pt.org.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 用户所属组织相关查询请求
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-9-19.1  zhengky	2014-9-19	  Create
 * </pre>
 * @date 2014-9-19
 */
@Service(ApiServiceName.ORG_USER_GET)
@Transactional
public class OrgUserGetServiceImpl extends BaseServiceImpl implements WellptService<OrgUserGetRequest> {

    @Autowired
    private UserService userService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#getResponse(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(OrgUserGetRequest orgUserGetRequest) {
        String userId = orgUserGetRequest.getId();
        UserBean bean = userService.getBeanById(userId);

        OrgUserGetResponse response = new OrgUserGetResponse();
        response.setData(bean);
        return response;
    }

}
