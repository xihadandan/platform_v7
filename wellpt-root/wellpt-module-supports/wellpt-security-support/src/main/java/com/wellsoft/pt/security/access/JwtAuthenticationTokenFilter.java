package com.wellsoft.pt.security.access;

import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.web.controller.FaultMessage;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetailsCacheHolder;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.util.JwtTokenUtil;
import com.wellsoft.pt.security.util.TenantContextHolder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author chenq
 * @date 2020/4/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/4/9    chenq		2020/4/9		Create
 * </pre>
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ThreadLocal<String> tokenThreadLocal = new ThreadLocal<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    private String[] annoymousUrls;
    private Set<AntPathRequestMatcher> antPathRequestMatchers = Sets.newHashSet();

    @Value("${jwt.authentication.autoRefreshToken:true}")
    private boolean autoRefreshToken;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication existAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (existAuthentication != null && (!(existAuthentication instanceof AnonymousAuthenticationToken)
                || (existAuthentication instanceof AnonymousAuthenticationToken
                && existAuthentication.getAuthorities() != null
                && existAuthentication.getAuthorities().contains(new SimpleGrantedAuthority(BuildInRole.ROLE_ANONYMOUS_TOKEN_VALID.name())))
        )) {
            filterChain.doFilter(request, response);
            return;
        }


        String token = JwtTokenUtil.getToken(request);
        String error = "";

        if (StringUtils.isNotBlank(token)) {
            try {
                Jws<Claims> claimsJws = JwtTokenUtil.getClaims(token);
                String username = claimsJws.getBody().get("loginName", String.class);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = (UserDetails) UserDetailsCacheHolder.getUserFromCache(
                            username);//FIXME: 用户详情缓存获取
                    if (userDetails == null) {
                        TenantContextHolder.setLoginType(StringUtils.defaultIfBlank(
                                claimsJws.getBody().get("loginType", String.class), LoginType.USER));
                        //重新获取用户信息
                        userDetails = (UserDetails) ApplicationContextHolder.getBean(BeanIds.USER_DETAILS_SERVICE, UserDetailsService.class).loadUserByUsername(username);
                        UserDetailsCacheHolder.putUserInCache(userDetails);
                        if (userDetails == null) {
                            throw new AccountNotFoundException("无效的用户");
                        }
                        TenantContextHolder.reset();
                    }
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    if (autoRefreshToken) {
                        Date now = new Date();
                        Date expiration = claimsJws.getBody().getExpiration();
                        Date closetLimitTime = DateUtils.addMinutes(claimsJws.getBody().getExpiration(),
                                -(int) JwtTokenUtil.getExpiration(TimeUnit.MINUTES) / 2);
                        if (now.after(closetLimitTime) && now.before(expiration)) {//当剩余时效不足一半时候自动刷新token
                            //token临近过期，则自动刷新token
                            token = JwtTokenUtil.refreshToken(token);
                            response.setHeader("RefreshTOKEN", token);
                        }
                    }


                }
                Cookie tokenCookie = new Cookie("jwt", token);
                tokenCookie.setPath("/");
                response.addCookie(tokenCookie);
                Cookie authTypeCookie = new Cookie("auth", "jwt");
                authTypeCookie.setPath("/");
                response.addCookie(authTypeCookie);
            } catch (ExpiredJwtException expiredEx) {
                // 匿名请求，token无效的情况下，不需要抛异常
                for (AntPathRequestMatcher matcher : antPathRequestMatchers) {
                    if (matcher.matches(request)) {
                        filterChain.doFilter(request, response);
                        return;
                    }
                }

                logger.error("token校验异常：", expiredEx);
                Cookie tokenCookie = new Cookie("jwt", token);
                tokenCookie.setPath("/");
                tokenCookie.setMaxAge(0);
                response.addCookie(tokenCookie);
                error = "登录超时";
            } catch (AccountNotFoundException ex) {
                logger.error("token校验异常：", ex);
                error = "无效的用户";
            } catch (Exception e) {
                logger.error("token校验异常：", e);
                error = "登录无效";
            }
        }

        if (StringUtils.isNotBlank(error)) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(Encoding.UTF8.getValue());
            FaultMessage msg = new FaultMessage("无有效登录信息");
            msg.setErrorCode(JsonDataErrorCode.SessionExpired.name());
            response.setStatus(HttpStatus.OK.value());
            Writer writer = response.getWriter();
            objectMapper.writeValue(writer, msg);
            return;
//            SecurityContextHolder.getContext().setAuthentication(null);
//            ApplicationContextHolder.getBean(AuthenticationEntryPoint.class).commence(request,
//                    response, new BadCredentialsException(error));
//            return;
        }

        filterChain.doFilter(request, response);


    }

    public void setAnnoymousUrls(String[] annoymousUrls) {
        this.annoymousUrls = annoymousUrls;
        if (this.annoymousUrls != null) {
            for (String u : annoymousUrls) {
                antPathRequestMatchers.add(new AntPathRequestMatcher(u));
            }
        }
    }
}
