/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.OrgUserOrgInfoQueryRequest;
import com.wellsoft.pt.api.response.OrgUserOrgInfoQueryResponse;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.webservice.service.OrgInterfaceQueryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Service(ApiServiceName.ORG_USER_ORGINFO_QUERY)
@Transactional
public class OrgUserOrgInfoQueryServiceImpl extends BaseServiceImpl implements WellptService<OrgUserOrgInfoQueryRequest> {

    @Autowired
    private OrgInterfaceQueryService orgInterfaceQueryService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#getResponse(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(OrgUserOrgInfoQueryRequest queryRequest) {
        OrgUserOrgInfoQueryResponse response = new OrgUserOrgInfoQueryResponse();
        String queryValue = queryRequest.getUserCode();
        if (StringUtils.isNotBlank(queryValue)) {
            String OrgInfoJsonStr = orgInterfaceQueryService.getOrgInfoByUserCode(queryRequest.getUserCode());
            response.setData(OrgInfoJsonStr);
        } else {
            Map<String, Object> values = new HashMap<String, Object>();
            if (StringUtils.isNotBlank(queryValue)) {
                values.put("loginName", queryValue);
                values.put("userName", queryValue);
                values.put("departmentName", queryValue);
                values.put("majorJobName", queryValue);
                /*add by huanglinchuan 2015.3.22 begin*/
                values.put("pinyin", queryValue);
                /*add by huanglinchuan 2015.3.22 end*/
            }
            List<String> limitUserIds = new ArrayList<String>();
            if (limitUserIds != null && !limitUserIds.isEmpty()) {
                values.put("limitUserIds", limitUserIds);
            }
            PagingInfo pagingInfo = new PagingInfo();
            pagingInfo.setAutoCount(false);
            pagingInfo.setCurrentPage(1);
            pagingInfo.setPageSize(200);
            List<QueryItem> users = this.nativeDao.namedQuery("getAllUsersQuery", values, QueryItem.class, pagingInfo);
            for (QueryItem queryItem : users) {
                queryItem.remove(IdEntity.UUID);
            }
            response.setDataList(users);
        }

        return response;
    }

}
