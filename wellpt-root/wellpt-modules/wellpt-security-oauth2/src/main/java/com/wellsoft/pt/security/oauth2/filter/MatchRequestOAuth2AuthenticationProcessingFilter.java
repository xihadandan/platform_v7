package com.wellsoft.pt.security.oauth2.filter;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;

/**
 * Description: 统一认证的api token拦截校验
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月22日   chenq	 Create
 * </pre>
 */
public class MatchRequestOAuth2AuthenticationProcessingFilter extends OAuth2AuthenticationProcessingFilter {


    private Set<AntPathRequestMatcher> antPathRequestMatchers = Sets.newHashSet();

    public MatchRequestOAuth2AuthenticationProcessingFilter(String url) {
        if (StringUtils.isNotBlank(url)) {
            String[] urls = url.split(",|;");
            for (String u : urls) {
                antPathRequestMatchers.add(new AntPathRequestMatcher(u));
            }
        }
    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        for (AntPathRequestMatcher matcher : antPathRequestMatchers) {
            if (matcher.matches((HttpServletRequest) req)) {
                super.doFilter(req, res, chain);
                return;
            }
        }
        chain.doFilter(req, res);
    }

}
