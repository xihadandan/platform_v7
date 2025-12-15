package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.SecurityQueryResponse;

/**
 * Description: 权限查询request
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-19.1  zhengky	2014-8-19	  Create
 * </pre>
 * @date 2014-8-19
 */
public class SecurityQueryRequest extends WellptRequest<SecurityQueryResponse> {

    private String userLoginName;

    private String moduleCode;

    @Override
    public String getApiServiceName() {
        return ApiServiceName.SECURITY_QUERY;
    }

    @Override
    public Class<SecurityQueryResponse> getResponseClass() {
        return SecurityQueryResponse.class;
    }

    public String getUserLoginName() {
        return userLoginName;
    }

    public void setUserLoginName(String userLoginName) {
        this.userLoginName = userLoginName;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

}
