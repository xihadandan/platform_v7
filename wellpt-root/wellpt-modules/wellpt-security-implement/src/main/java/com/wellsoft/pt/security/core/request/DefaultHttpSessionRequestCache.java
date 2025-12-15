package com.wellsoft.pt.security.core.request;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月08日   chenq	 Create
 * </pre>
 */
public class DefaultHttpSessionRequestCache extends HttpSessionRequestCache {
    private PortResolver portResolver = new PortResolverImpl();
    private boolean createSessionAllowed = true;
    private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;
    private String sessionAttrName = "SPRING_SECURITY_SAVED_REQUEST";

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        super.saveRequest(request, response);
        if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"))) {// 未登录情况下的ajax请求，记录请求的发起页面，待登录后回到该页面
            if (requestMatcher.matches(request)) {
                DefaultSavedRequest.Builder builder = new DefaultSavedRequest.Builder();
                builder.setContextPath(request.getContextPath());
                builder.setServletPath(request.getServletPath());
                builder.setServerName(request.getServerName());
                builder.setScheme(request.getScheme());
                builder.setServerPort(portResolver.getServerPort(request));
                String url = request.getHeader("loginSuccessRedirect");// 可以通过头部设置登录成功后的重定向地址参数
                if (StringUtils.isBlank(url)) {
                    url = request.getHeader("referer").substring(request.getHeader("referer").indexOf(request.getHeader("host")) + request.getHeader("host").length());
                }
                builder.setRequestURI(url);
                if (createSessionAllowed || request.getSession(false) != null) {
                    request.getSession().setAttribute(this.sessionAttrName, builder.build());
                }
            }
        }

    }
}
