package com.wellsoft.oauth2.web.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class RequestContextFilter extends OncePerRequestFilter implements Filter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        RequestContext rc = RequestContext.begin(getServletContext(), request, response);
        try {
            setUrlProperties(request);
            before(request, response, filterChain, rc);
            filterChain.doFilter(request, response);
            after(request, response, filterChain, rc);
        } finally {
            if (rc != null)
                rc.end();
        }
    }

    /**
     * 设置其他系统访问地址
     *
     * @param request
     * @author chenqiong
     */
    private void setUrlProperties(HttpServletRequest request) {
    }

    /**
     * Before.
     *
     * @param request     the request
     * @param response    the response
     * @param filterChain the filter chain
     * @param rc          the rc
     */
    protected void before(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
                          RequestContext rc) {

    }

    /**
     * After.
     *
     * @param request     the request
     * @param response    the response
     * @param filterChain the filter chain
     * @param rc          the rc
     */
    protected void after(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
                         RequestContext rc) {

    }
}
