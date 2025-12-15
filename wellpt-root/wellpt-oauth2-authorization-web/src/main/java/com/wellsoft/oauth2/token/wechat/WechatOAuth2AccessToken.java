package com.wellsoft.oauth2.token.wechat;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

/**
 * Description: wechat accessToken数据类
 *
 * @author chenq
 * @date 2019/10/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/14    chenq		2019/10/14		Create
 * </pre>
 */
public class WechatOAuth2AccessToken extends DefaultOAuth2AccessToken {

    public static final String SCOPE_BASE = "snsapi_base";
    public static final String SCOPE_USRINFO = "snsapi_userinfo";
    private String openid;//微信用户唯一id值

    public WechatOAuth2AccessToken(String value) {
        super(value);
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
