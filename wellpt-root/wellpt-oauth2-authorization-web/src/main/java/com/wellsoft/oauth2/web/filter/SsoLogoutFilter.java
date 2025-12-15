package com.wellsoft.oauth2.web.filter;

import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 支持跨域的单点登出
 */
public class SsoLogoutFilter implements Filter {

    private String ssoLogoutUrl = "/cors/oauth2/logout";
    private AntPathRequestMatcher logoutRequestMatcher = new AntPathRequestMatcher(ssoLogoutUrl);


    public SsoLogoutFilter() {

    }

    public SsoLogoutFilter(String ssoLogoutUrl) {
        this.ssoLogoutUrl = ssoLogoutUrl;
        logoutRequestMatcher = new AntPathRequestMatcher(ssoLogoutUrl);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        if (this.logoutRequestMatcher.matches(request)) {
            try {
                //退出
                new SecurityContextLogoutHandler().logout(request, null, null);
            } catch (Exception e) {
                throw new BadClientCredentialsException();
            }
            response.getWriter().write("success");
            return;
        } else {
            chain.doFilter(req, res);
        }


    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}