package com.wellsoft.pt.security.oauth2.token;

import com.google.common.collect.Sets;
import com.wellsoft.pt.security.enums.BuildInRole;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年05月19日   chenq	 Create
 * </pre>
 */
public class DefaultRemoteTokenServices extends RemoteTokenServices {

    private String[] anoymouseUrls = null;
    private Set<AntPathRequestMatcher> antPathRequestMatchers = Sets.newHashSet();

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        try {
            return super.loadAuthentication(accessToken);
        } catch (InvalidTokenException e) {
            // 判断请求地址是否是匿名，如果是匿名则不抛异常，返回匿名认证信息
            ServletRequestAttributes servletRequestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();

            for (AntPathRequestMatcher matcher : antPathRequestMatchers) {
                if (matcher.matches(request)) {
                    return new AnoymouseOAuth2Authentication(new NoOAuth2Request(),
                            new AnonymousAuthenticationToken(UUID.randomUUID().toString(), "anonymousUser", AuthorityUtils.createAuthorityList(BuildInRole.ROLE_ANONYMOUS.name()
                                    , BuildInRole.ROLE_ANONYMOUS_TOKEN_VALID.name())));
                }
            }

            throw e;

        }

    }

    public void setAnoymouseUrls(String[] anoymouseUrls) {
        this.anoymouseUrls = anoymouseUrls;
        if (this.anoymouseUrls != null) {
            for (String u : this.anoymouseUrls) {
                antPathRequestMatchers.add(new AntPathRequestMatcher(u));
            }
        }
    }


    class AnoymouseOAuth2Authentication extends OAuth2Authentication {

        /**
         * Construct an OAuth 2 authentication. Since some grant types don't require user authentication, the user
         * authentication may be null.
         *
         * @param storedRequest      The authorization request (must not be null).
         * @param userAuthentication The user authentication (possibly null).
         */
        public AnoymouseOAuth2Authentication(OAuth2Request storedRequest, Authentication userAuthentication) {
            super(storedRequest,
                    userAuthentication);
        }


    }

    class NoOAuth2Request extends OAuth2Request {
        public NoOAuth2Request() {
            super(null, "", Collections.EMPTY_LIST, true, Collections.EMPTY_SET
                    , null, null, null,
                    null);
        }
    }


}
