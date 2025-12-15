package com.wellsoft.oauth2.web.filter;

import com.google.common.collect.Lists;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 支持跨域
 */
public class EnableCorsFilter implements Filter {


    private List<AntPathRequestMatcher> requestMatcherList = Lists.newArrayList();

    public EnableCorsFilter() {

    }

    public EnableCorsFilter(String... antPathRequestUrl) {
        for (String url : antPathRequestUrl) {
            requestMatcherList.add(new AntPathRequestMatcher(url));
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        if (requestMatcher(request)) {
            //支持跨域
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with, authorization");
        }

        chain.doFilter(req, res);

    }

    private boolean requestMatcher(HttpServletRequest request) {
        for (AntPathRequestMatcher matcher : this.requestMatcherList) {
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}