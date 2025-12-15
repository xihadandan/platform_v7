package com.wellsoft.pt.api.filter;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.wellsoft.context.enums.ApiCodeEnum;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.NetUtils;
import com.wellsoft.pt.api.response.ApiResponse;
import com.wellsoft.pt.api.service.ApiOutSysConfigService;
import com.wellsoft.pt.api.support.ApiContextHolder;
import com.wellsoft.pt.api.support.TokenClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Description: token安全鉴权过滤
 *
 * @author chenq
 * @date 2018/8/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/9    chenq		2018/8/9		Create
 * </pre>
 */
public class TokenSecurityFilter extends OncePerRequestFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(
            TokenSecurityFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        AntPathMatcher matcher = new AntPathMatcher();
        if (matcher.match("/api/token/**", httpServletRequest.getRequestURI())) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        String authorization = httpServletRequest.getHeader("Authorization");
        String token = null;
        if (StringUtils.isNotBlank(authorization)) {
            int bearerIndex = authorization.indexOf("Bearer");
            if (bearerIndex != -1) {
                token = authorization.substring(7);
            }
        }
        String ip = NetUtils.getRequestIp(httpServletRequest);
        LOGGER.info("请求IP：{}，解析获取到token值：{}", ip, token);
        if (StringUtils.isBlank(token)) {
            PrintWriter pw = httpServletResponse.getWriter();
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.setStatus(HttpStatus.ACCEPTED.value());
            pw.write(new Gson().toJson(ApiResponse.build().code(ApiCodeEnum.VERIFY_TOKEN_ERROR)));
            return;
        }
        TokenClaims claims = new TokenClaims();

        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(
                    Keys.hmacShaKeyFor(ApplicationContextHolder.getBean(
                            ApiOutSysConfigService.class).tokenKey())).parseClaimsJws(token);

            claims.putAll(claimsJws.getBody());
            ApiContextHolder.getContext().get().put(ApiContextHolder.TOKEN_CLAIMS, claims);

            if (!isAuthorizeApi(httpServletRequest, claims)) {
                PrintWriter pw = httpServletResponse.getWriter();
                httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                httpServletResponse.setStatus(HttpStatus.ACCEPTED.value());
                pw.write(new Gson().toJson(
                        ApiResponse.build().code(ApiCodeEnum.API_NOT_AUTHROIZE)));
                return;
            }

            LOGGER.info("### 请求服务token body {}", claimsJws.getBody().toString());
        } catch (ExpiredJwtException expiredEx) {
            LOGGER.error("请求IP：{}，加解密失败：{}", ip, Throwables.getStackTraceAsString(expiredEx));
            PrintWriter pw = httpServletResponse.getWriter();
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.setStatus(HttpStatus.ACCEPTED.value());
            pw.write(new Gson().toJson(ApiResponse.build().code(ApiCodeEnum.VERIFY_TOKEN_ERROR)));
            return;
        } catch (Exception e) {
            LOGGER.error("请求IP：{}，加解密失败：{}", ip, Throwables.getStackTraceAsString(e));
            PrintWriter pw = httpServletResponse.getWriter();
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.setStatus(HttpStatus.ACCEPTED.value());
            pw.write(new Gson().toJson(ApiResponse.build().code(ApiCodeEnum.DECODE_SECRET_ERROR)));
            return;
        }
        //请求幂等值，秒级内不允许重复调用
        ApiContextHolder.getContext().get().put(ApiContextHolder.REQUEST_IDEMPOTENT_KEY,
                claims.getSystemCode() + httpServletRequest.getRequestURI() + System.currentTimeMillis() / 1000L);

        filterChain.doFilter(httpServletRequest, httpServletResponse);


    }


    private boolean isAuthorizeApi(HttpServletRequest httpServletRequest, TokenClaims tokenClaims) {

        //1.授权列表没配置，或者在授权列表内都表示有授权
        boolean isAuthorize = false;
        if (tokenClaims != null && CollectionUtils.isNotEmpty(tokenClaims.getAuthorizeApis())) {
            AntPathMatcher matcher = new AntPathMatcher();
            List<String> allowApis = tokenClaims.getAuthorizeApis();
            isAuthorize = CollectionUtils.isEmpty(allowApis);
            for (String api : allowApis) {
                if (matcher.match(api, httpServletRequest.getRequestURI())) {
                    isAuthorize = true;
                    break;
                }
            }
        }


        boolean inUnauthorize = false;
        if (tokenClaims != null && CollectionUtils.isNotEmpty(tokenClaims.getUnauthorizeApis())) {
            AntPathMatcher matcher = new AntPathMatcher();
            List<String> unauthorizeApis = tokenClaims.getUnauthorizeApis();
            for (String api : unauthorizeApis) {
                if (matcher.match(api, httpServletRequest.getRequestURI())) {
                    inUnauthorize = true;
                    break;
                }
            }
        }
        //未授权优先级高于授权
        return inUnauthorize ? false : isAuthorize;
    }

}
