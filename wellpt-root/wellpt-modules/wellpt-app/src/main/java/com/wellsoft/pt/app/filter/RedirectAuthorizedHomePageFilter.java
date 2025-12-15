package com.wellsoft.pt.app.filter;

import com.wellsoft.pt.app.bean.AppPageDefinitionPathBean;
import com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import com.wellsoft.pt.security.core.userdetails.InternetUserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/17
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/17    chenq		2019/10/17		Create
 * </pre>
 */

public class RedirectAuthorizedHomePageFilter extends OncePerRequestFilter {
    public static final String SECURITY_HOMEPAGE = "/security_homepage";
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    SecurityAuditFacadeService securityApiFacade;
    @Autowired
    NativeDao nativeDao;
    @Autowired
    AppProductIntegrationService appProductIntegrationService;
    @Autowired
    AppPageDefinitionMgr appPageDefinitionMgr;
    private AntPathRequestMatcher homePageRequestMatcher = new AntPathRequestMatcher(
            SECURITY_HOMEPAGE);

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (homePageRequestMatcher != null && homePageRequestMatcher.matches(httpServletRequest)) {
            if (SpringSecurityUtils.getCurrentUser() instanceof InternetUserDetails) {
                httpServletResponse.sendRedirect("/passport/iuser/login/success");
                return;
            }
            //权限判断已在listFacade里面 这里就不走权限判断了
            List<AppPageDefinitionPathBean> appPageDefinitionList = appPageDefinitionMgr.listFacade();
            if (appPageDefinitionList.size() > 0) {
                AppPageDefinitionPathBean item = appPageDefinitionList.get(0);
                httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + item.getUrl());
                return;
            }
            httpServletResponse.sendRedirect("/pt/common/403.jsp");
            return;
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }
}
