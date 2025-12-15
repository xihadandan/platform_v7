package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.domain.UsersQuery;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.QueryForUsersRequest;
import com.wellsoft.pt.api.response.QueryForUsersResponse;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * 根据用户名查询用户
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-3-16.1	lmw		2015-3-16		Create
 * </pre>
 * @date 2015-3-16
 */
@Service(ApiServiceName.QUERYFORUSERS)
public class QueryUsersServiceImpl extends BaseServiceImpl implements WellptService<QueryForUsersRequest> {

    @Autowired
    private UserService userService;

    @Override
    public WellptResponse doService(QueryForUsersRequest qRequest) {
        QueryForUsersResponse qResponse = new QueryForUsersResponse();
        try {
            String userName = qRequest.getName();
            userName = userName != null ? userName : "";

            PagingInfo page = new PagingInfo(qRequest.getPageNo(), qRequest.getPageSize());

            List<QueryItem> qList = userService.queryUserWithKeyWords(userName, page);
            List<UsersQuery> rList = new LinkedList<UsersQuery>();
            for (QueryItem u : qList) {
                UsersQuery uQuery = new UsersQuery();
                uQuery.setUuid(u.getString("uuid"));
                uQuery.setId(u.getString("id"));
                uQuery.setDepartmentName(u.getString("departmentName"));
                uQuery.setMajorJobName(u.getString("majorJobName"));
                uQuery.setUserName(u.getString("userName"));
                rList.add(uQuery);
            }

            qResponse.setUsers(rList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            qResponse.setSuccess(false);
        }

        return qResponse;
    }
}
