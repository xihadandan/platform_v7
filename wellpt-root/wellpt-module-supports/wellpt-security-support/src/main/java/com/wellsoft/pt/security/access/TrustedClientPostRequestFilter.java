package com.wellsoft.pt.security.access;

import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.web.controller.FaultMessage;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.util.JwtTokenUtil;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * Description: 校验可信客户端的api调用请求
 * client-token 为后端服务或者 node 端生成的 jwt
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年04月29日   chenq	 Create
 * </pre>
 */
public class TrustedClientPostRequestFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ObjectMapper objectMapper = new ObjectMapper();
    private String clientTokenParameterName = "client-token";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
//        if (httpServletRequest.getMethod().equalsIgnoreCase(HttpMethod.POST.name())) {
        String clientToken = null;
        if (httpServletRequest.getHeader(clientTokenParameterName) != null) {
            clientToken = httpServletRequest.getHeader(clientTokenParameterName);
        }

        if (StringUtils.isNotBlank(clientToken)) {
            try {
                JwtTokenUtil.getClaims(clientToken, false);
                SecurityContextHolder.getContext().setAuthentication(
                        new PreAuthenticatedAuthenticationToken("anonymousUser", "trustedClient",
                                AuthorityUtils.createAuthorityList(BuildInRole.ROLE_TRUSTED_CLIENT.name(), BuildInRole.ROLE_ANONYMOUS.name())));
            } catch (Exception e) {
                httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                httpServletResponse.setCharacterEncoding(Encoding.UTF8.getValue());
                FaultMessage msg = new FaultMessage("无权限访问");
                msg.setErrorCode(JsonDataErrorCode.SessionExpired.name());
                httpServletResponse.setStatus(HttpStatus.OK.value());
                Writer writer = httpServletResponse.getWriter();
                objectMapper.writeValue(writer, msg);
                return;
            }
        }
//        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }
}
