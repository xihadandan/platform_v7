package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptQueryRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.QueryForUsersResponse;

/**
 * 用户查询请求体
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
public class QueryForUsersRequest extends WellptQueryRequest<QueryForUsersResponse> {

    // 用户名称
    private String name;
    private String[] initUserIds;

    @Override
    public String getApiServiceName() {
        return ApiServiceName.QUERYFORUSERS;
    }

    @Override
    public Class<QueryForUsersResponse> getResponseClass() {
        return QueryForUsersResponse.class;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the initUserIds
     */
    public String[] getInitUserIds() {
        return initUserIds;
    }

    /**
     * @param initUserIds 要设置的initUserIds
     */
    public void setInitUserIds(String[] initUserIds) {
        this.initUserIds = initUserIds;
    }
}
