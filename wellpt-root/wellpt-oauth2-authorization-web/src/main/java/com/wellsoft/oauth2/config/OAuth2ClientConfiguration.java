package com.wellsoft.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/16    chenq		2019/9/16		Create
 * </pre>
 */

@Configuration
@EnableOAuth2Client
public class OAuth2ClientConfiguration {


    @Autowired
    private OAuth2ClientContext oauth2ClientContext;


}
