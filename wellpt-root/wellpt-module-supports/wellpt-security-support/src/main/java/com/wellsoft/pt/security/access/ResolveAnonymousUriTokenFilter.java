package com.wellsoft.pt.security.access;

import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.web.controller.FaultMessage;
import com.wellsoft.pt.security.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.UUID;

/**
 * Description: 部分后端服务接口地址是非匿名访问的，设计该token解析是为了满足部分场景下部分数据是运行匿名访问的
 * 例如：文件下载地址，正常情况下是安全访问的，但是有部分文件ID是运行匿名访问的，比如登录页的图片地址
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年04月29日   chenq	 Create
 * </pre>
 */
public class ResolveAnonymousUriTokenFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String uriToken = null;
        if (httpServletRequest.getParameter("guest-uri-token") != null) {
            uriToken = httpServletRequest.getParameter("guest-uri-token");
        }
        if (StringUtils.isNotBlank(uriToken)) {
            try {
                Jws<Claims> claimsJws = JwtTokenUtil.getClaims(uriToken);
                // 解析出匿名访问的uri，进行转发
                String requestURI = claimsJws.getBody().getSubject();
                SecurityContextHolder.getContext().setAuthentication(
                        new PreAuthenticatedAuthenticationToken(UUID.randomUUID().toString(), "anonymousUser", null
                                // AuthorityUtils.createAuthorityList(BuildInRole.ROLE_ANONYMOUS.name(), BuildInRole.ROLE_ANONYMOUS_TOKEN_VALID.name())
                        ));
                httpServletRequest.getRequestDispatcher(requestURI).forward(httpServletRequest, httpServletResponse);
                return;
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

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }
}
