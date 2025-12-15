package com.wellsoft.pt.security.util;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import io.jsonwebtoken.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;
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
@Component
public class JwtTokenUtil {
    private static Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    private static JwtTokenUtil instance;
    @Value("${jwt.secret:g(SPdXCIGQDhqIShNCprAFq0EfQJNXNBbVnHTdDXhJGgPA9RoDkib57irDu2XNJsDY(OpehC^2iC30246e7U4G5n5gCLCW5ma2BwaVI3dTmEe0c~(SMDexqP6qyYVJ4qnWb~yb(14HuZl#08qHvUkj#yPn58WJFx1UwTe#T3R4GLb4vozIlxrKzRKQ7()VAX#DyQz)8wIiWnViPD40VYv3J0)l^N8oSRCJmrQ0#HnLY2U6F^6OLwu5HFqY3YK~Sz}")
    private String secret;
    @Value("${jwt.expiration:240}")//分钟
    private Long expiration;

    private static ThreadLocal<Long> localExpiration = new ThreadLocal<>();

    private static JwtTokenUtil instance() {
        if (instance == null) {
            instance = ApplicationContextHolder.getBean(JwtTokenUtil.class);
        }
        return instance;
    }

    public static long getExpiration(TimeUnit timeUnit) {
        timeUnit = timeUnit == null ? TimeUnit.MINUTES : timeUnit;
        switch (timeUnit) {
            case MINUTES:
                return instance().expiration;
            case SECONDS:
                return instance().expiration * 60;
            case MILLISECONDS:
                return instance().expiration * 60 * 1000;
            default:
                return instance().expiration;
        }
    }

    public static void setLocalExpiration(long expiration) {
        localExpiration.set(expiration);
    }

    public static void clearLocalExpiration() {
        localExpiration.remove();
    }

    public static JwtBuilder jwtBuilder() {
        return Jwts.builder().signWith(instance().key(), SignatureAlgorithm.HS256);
    }


    public static String createToken(Map<String, Object> body, Date expiration) {
        return instance().generateToken(body, expiration);
    }

    /**
     * 创建token
     *
     * @param userDetails
     * @return
     */
    public static String createToken(UserDetails userDetails) {
        try {
            Map<String, Object> claims = Maps.newHashMap();
            claims.put(Claims.SUBJECT, userDetails.getUserId());
            claims.put("userId", userDetails.getUserId());
            claims.put("loginName", userDetails.getLoginName());
            claims.put("userName", userDetails.getUserName());
            claims.put("systemUnitId", userDetails.getSystemUnitId());
            claims.put("loginType", userDetails.getLoginType());
            return instance().generateToken(claims);
        } catch (Exception e) {
            logger.error("token 生成异常");
            throw new RuntimeException(e.getMessage());
        } finally {
            JwtTokenUtil.clearLocalExpiration();
        }

    }

    /**
     * 解析token
     *
     * @param token
     * @return
     * @throws JwtException
     */
    public static Jws<Claims> getClaims(String token) throws JwtException {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(instance().key()).parseClaimsJws(token);
        return claimsJws;
    }

    public static Jws<Claims> getClaims(String token, boolean secretKey) throws Exception {
        JwtParser parser = Jwts.parser();
        if (secretKey) {
            parser.setSigningKey(instance().key());
        } else {
            parser.setSigningKey(instance().secret.getBytes("UTF-8"));
        }
        Jws<Claims> claimsJws = parser.parseClaimsJws(token);
        return claimsJws;
    }

    /**
     * 刷新token
     *
     * @param token
     * @return
     * @throws JwtException
     */
    public static String refreshToken(String token) throws JwtException {
        Jws<Claims> claimsJws = getClaims(token);
        return instance().generateToken(claimsJws.getBody());
    }

    public static String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization-JWT");
        String token = null;
        if (StringUtils.isNotBlank(authorization)) {
            int bearerIndex = authorization.indexOf("Bearer");
            if (bearerIndex != -1) {
                token = authorization.substring(7);
            }
        } else if (request.getParameter("jwt") != null) {
            //
            token = request.getParameter("jwt");
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null)
                for (Cookie ck : cookies) {
                    if (ck.getName().equalsIgnoreCase("jwt") && ck.getMaxAge() != 0) {
                        return ck.getValue();
                    }
                }
        }
        return token;
    }

    private Date expireDate() {
        Date date = DateUtils.addMinutes(new Date(), localExpiration.get() != null ? localExpiration.get().intValue() : instance().expiration.intValue());
        logger.info("生成用户 token 超时时间: {}", DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
        return date;
    }

    private Key key() {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] bytes = DatatypeConverter.parseBase64Binary(instance().secret);
        return new SecretKeySpec(bytes, signatureAlgorithm.getJcaName());
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expireDate())
                .signWith(instance().key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateToken(Map<String, Object> claims, Date expireDate) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expireDate != null ? expireDate : expireDate())
                .signWith(instance().key(), SignatureAlgorithm.HS256)
                .compact();
    }


}
