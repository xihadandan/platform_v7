package com.wellsoft.pt.security.access;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Description: 解析并设置每次请求所访问的系统ID
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月06日   chenq	 Create
 * </pre>
 */
public class ResolveRequestSystemContextPathFilter extends OncePerRequestFilter {

    public final static String PROD_PARAMETER_NAME = "prod_context_path";
    public final static String SYS_PARAMETER_NAME = "system_id";
    public final static String PAGE_ID_PARAMETER_NAME = "page_id";
    public final static String IS_MOBILE_PARAMETER_NAME = "is_mobile_app";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 解析请求头的产品上下文环境路径
        String system = request.getHeader(SYS_PARAMETER_NAME);
        if (StringUtils.isNotBlank(system)) {
            RequestSystemContextPathResolver.setSystem(system);
        } else {
            // 判断地址请求参数是否指定系统ID
            system = WebUtils.findParameterValue(request, SYS_PARAMETER_NAME);
            if (StringUtils.isNotBlank(system)) {
                RequestSystemContextPathResolver.setSystem(system);
            }

            // 解析产品路径
            String prodContextPath = request.getHeader(PROD_PARAMETER_NAME);
            if (StringUtils.isNotBlank(prodContextPath)) {
                system = prodContextPath.split("/")[1];
                RequestSystemContextPathResolver.setSystem(system);
            }
        }
        request.setAttribute(SYS_PARAMETER_NAME, RequestSystemContextPathResolver.system());

        // 当前请求来源页面ID
        String pageId = request.getHeader(PAGE_ID_PARAMETER_NAME);
        if (StringUtils.isNotBlank(pageId)) {
            RequestSystemContextPathResolver.setPageId(pageId);
        }
        request.setAttribute(PAGE_ID_PARAMETER_NAME, RequestSystemContextPathResolver.pageId());

        String isMobileApp = request.getHeader(IS_MOBILE_PARAMETER_NAME);
        if (StringUtils.isNotBlank(isMobileApp)) {
            RequestSystemContextPathResolver.setIsMobileApp(true);
        } else {
            isMobileApp = WebUtils.findParameterValue(request, IS_MOBILE_PARAMETER_NAME);
            if (StringUtils.isNotBlank(isMobileApp)) {
                RequestSystemContextPathResolver.setIsMobileApp(true);
            }
        }


        filterChain.doFilter(request, response);
        RequestSystemContextPathResolver.clear();
    }
}
