package com.wellsoft.pt.message.support;

import com.wellsoft.context.jdbc.entity.IdEntity;

/**
 * Description: 消息额外传参类
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2014-10-22.1	tony		2014-10-22		Create
 * </pre>
 * @date 2014-10-22
 */
public class MessageWebserviceParm extends IdEntity {
    private String webServiceUrl;//url
    private String usernameKey;//用户标识
    private String usernameValue;//用户值
    private String passwordKey;//密码标识
    private String passwordValue;//密码
    private String tenantidKey;//租户标识
    private String tenantidValue;//租户
    private String jsondata;

    public String getWebServiceUrl() {
        return webServiceUrl;
    }

    public void setWebServiceUrl(String webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }

    public String getUsernameKey() {
        return usernameKey;
    }

    public void setUsernameKey(String usernameKey) {
        this.usernameKey = usernameKey;
    }

    public String getUsernameValue() {
        return usernameValue;
    }

    public void setUsernameValue(String usernameValue) {
        this.usernameValue = usernameValue;
    }

    public String getPasswordKey() {
        return passwordKey;
    }

    public void setPasswordKey(String passwordKey) {
        this.passwordKey = passwordKey;
    }

    public String getPasswordValue() {
        return passwordValue;
    }

    public void setPasswordValue(String passwordValue) {
        this.passwordValue = passwordValue;
    }

    public String getTenantidKey() {
        return tenantidKey;
    }

    public void setTenantidKey(String tenantidKey) {
        this.tenantidKey = tenantidKey;
    }

    public String getTenantidValue() {
        return tenantidValue;
    }

    public void setTenantidValue(String tenantidValue) {
        this.tenantidValue = tenantidValue;
    }

    public String getJsondata() {
        return jsondata;
    }

    public void setJsondata(String jsondata) {
        this.jsondata = jsondata;
    }


}
