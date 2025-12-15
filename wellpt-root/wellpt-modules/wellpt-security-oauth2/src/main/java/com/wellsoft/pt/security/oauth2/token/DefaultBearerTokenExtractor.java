package com.wellsoft.pt.security.oauth2.token;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Description: oauth2 token获取，增加了忽略地址，避免其他带有access_token的地址被解析
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年01月06日   chenq	 Create
 * </pre>
 */
public class DefaultBearerTokenExtractor extends BearerTokenExtractor {

    private List<RequestMatcher> ignoreRequestMatchers = Lists.newArrayList();// 忽略的请求匹配

    public DefaultBearerTokenExtractor(String[] ignoreUrls) {
        for (String url : ignoreUrls) {
            if (StringUtils.isNotBlank(url))
                ignoreRequestMatchers.add(new AntPathRequestMatcher(url));
        }
    }

    @Override
    public Authentication extract(HttpServletRequest request) {
        for (RequestMatcher matcher : ignoreRequestMatchers) {
            if (matcher.matches(request)) return null;
        }
        return super.extract(request);
    }
}

