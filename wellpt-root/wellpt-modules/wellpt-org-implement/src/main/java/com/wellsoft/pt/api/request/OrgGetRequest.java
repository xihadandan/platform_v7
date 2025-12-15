package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.OrgGetResponse;

/**
 * Description: 获得组织架构请求类
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
public class OrgGetRequest extends WellptRequest<OrgGetResponse> {

    //组织树根节点名称
    private String rootName;
    //组织树根节点名称
    private String type;

    @Override
    public String getApiServiceName() {
        return ApiServiceName.ORG_GET;
    }

    @Override
    public Class<OrgGetResponse> getResponseClass() {
        return OrgGetResponse.class;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    public String getRootName() {
        return rootName;
    }

    public void setRootName(String rootName) {
        this.rootName = rootName;
    }

}
