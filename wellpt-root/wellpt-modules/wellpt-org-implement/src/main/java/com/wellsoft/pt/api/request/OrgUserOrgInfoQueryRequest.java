package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.OrgUserOrgInfoQueryResponse;

/**
 * Description: 用户查询请求
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-9-19.1  zhengky	2014-9-19	  Create
 *
 * 立达信方需要有个接口取某一用户的所在部门、岗位、职务、群组列表。
 * 输入参数可能：用户帐号、ID、工号
 * 返回JSon数据：department/job/duty/group
 *
 * </pre>
 * @date 2014-9-19
 */
public class OrgUserOrgInfoQueryRequest extends WellptRequest<OrgUserOrgInfoQueryResponse> {

    private String userCode;//用户帐号、ID、工号 三选一

    @Override
    public String getApiServiceName() {
        return ApiServiceName.ORG_USER_ORGINFO_QUERY;
    }

    @Override
    public Class<OrgUserOrgInfoQueryResponse> getResponseClass() {
        return OrgUserOrgInfoQueryResponse.class;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

}
