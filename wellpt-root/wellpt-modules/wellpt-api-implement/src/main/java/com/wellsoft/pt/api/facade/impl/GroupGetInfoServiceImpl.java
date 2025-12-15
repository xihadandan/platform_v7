/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: OrgGroupInfoServiceImpl.java
 * @Package com.wellsoft.pt.api.facade.impl
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-12 上午11:39:26
 * @version V1.0
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.GroupGetInfoRequest;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Administrator
 * @ClassName: OrgGroupInfoServiceImpl
 * @Description: TODO
 * @date 2014-12-12 上午11:39:26
 */
@Service(ApiServiceName.GROUP_GETINFO)
@Transactional
public class GroupGetInfoServiceImpl extends BaseServiceImpl implements WellptService<GroupGetInfoRequest> {

    /**
     * @Fields groupService : TODO
     */
    @Autowired
    private OrgApiFacade orgApiFacade;

    /*
     * (No Javadoc) <p>Title: doService</p> <p>Description: </p>
     *
     * @param request
     *
     * @return
     *
     * @see
     * com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api
     * .WellptRequest)
     */
    @Override
    public WellptResponse doService(GroupGetInfoRequest request) {
        return null;
    }

}
