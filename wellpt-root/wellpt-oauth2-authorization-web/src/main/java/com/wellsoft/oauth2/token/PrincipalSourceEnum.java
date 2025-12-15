package com.wellsoft.oauth2.token;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/12
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/12    chenq		2019/10/12		Create
 * </pre>
 */
public enum PrincipalSourceEnum {
    DEFAULT_OAUTH2_PRINCIPAL("默认统一认证用户"), GITHUB_OAUTH2_PRINCIPAL(
            "github统一认证用户"), WECHAT_OAUTH2_PRINCIPAL("微信统一认证用户");

    private String name;


    PrincipalSourceEnum(String name) {
        this.name = name;
    }
}
