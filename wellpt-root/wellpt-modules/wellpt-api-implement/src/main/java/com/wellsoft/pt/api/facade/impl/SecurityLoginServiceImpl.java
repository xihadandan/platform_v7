/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.context.util.encode.Md5PasswordEncoderUtils;
import com.wellsoft.context.util.sm.SM3Util;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.domain.LoginConfig;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.SecurityLoginRequest;
import com.wellsoft.pt.api.response.SecurityLoginResponse;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.core.userdetails.RestfulUserDetailsServiceProvider;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.support.CasLoginUtils;
import com.wellsoft.pt.security.support.CasLoginUtils.CasLoginResult;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Description: 用户登录
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-19.1  zhengky	2014-8-19	  Create
 * </pre>
 * @date 2014-8-19
 */
@Service(ApiServiceName.SECURITY_LOGIN)
@Transactional
public class SecurityLoginServiceImpl extends BaseServiceImpl implements WellptService<SecurityLoginRequest> {
    //	@Autowired
    //	private ChatUserExtendService chatUserExtendService;

    @Autowired
    private UserService userService;

    @Autowired
    private MultiOrgUserService multiOrgUserService;

    @Autowired
    private RestfulUserDetailsServiceProvider userDetailsServiceProvider;

    //	@Autowired
    //	private ChatMenuInfoService chatMenuInfoService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#getResponse(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(SecurityLoginRequest req) {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            IgnoreLoginUtils.logout();
        }

        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();

        // 生成返回结果
        SecurityLoginResponse response = new SecurityLoginResponse();
        if (userDetails != null) {
            return response;
        }
        boolean success = false;

        if (req.getAccessToken() != null) {

            String url = LoginConfig.getWeiXin() + "?access_token=" + req.getAccessToken() + "&userid="
                    + req.getUsername();
            logger.info(url);
            try {

                URL reUrl = new URL(url);
                URLConnection urlConnection = null;
                if (LoginConfig.getWeiXin().indexOf("https") == 0) {
                    /*
                     *  fix for
                     *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
                     *       sun.security.validator.ValidatorException:
                     *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
                     *               unable to find valid certification path to requested target
                     */
                    TrustManager[] trustAllCerts = new TrustManager[1];
                    trustAllCerts[0] = new javax.net.ssl.X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                                throws java.security.cert.CertificateException {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                                throws java.security.cert.CertificateException {
                            // TODO Auto-generated method stub

                        }

                    };

                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                    // Create all-trusting host name verifier
                    HostnameVerifier allHostsValid = new HostnameVerifier() {
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    };
                    // Install the all-trusting host verifier
                    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
                    /*
                     * end of the fix
                     */
                    urlConnection = reUrl.openConnection();
                } else {
                    urlConnection = reUrl.openConnection();
                }

                InputStream r = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(r, "UTF-8"));
                StringWriter writer = new StringWriter();
                char[] chars = new char[256];
                int count = 0;
                while ((count = reader.read(chars)) > 0) {
                    writer.write(chars, 0, count);
                }
                String re = writer.toString();
                logger.error(re);
                JSONObject j = JSONObject.fromObject(re);
                String errmsg = null;
                if (j.containsKey("errmsg")) {
                    errmsg = String.valueOf(j.get("errmsg"));
                } else {

                }

                if ("ok".compareToIgnoreCase(errmsg) == 0) {
                    success = true;
                    MultiOrgUserAccount userFromDb = multiOrgUserService.getUserAccountByLoginName(req.getUsername());
                    userDetails = (UserDetails) userDetailsServiceProvider.getUserDetails(req.getUsername());
                    UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(userDetails,
                            req.getPassword(), userDetails.getAuthorities());
                    result.setDetails(userFromDb.getPassword());
                    SecurityContextHolder.getContext().setAuthentication(result);
                } else {
                    response.setMsg("来自微信的登录校验失败：" + re);
                    success = false;
                    return response;
                }

            } catch (MalformedURLException e) {
                logger.error("error", e);
            } catch (IOException e) {
                logger.error("error", e);
            } catch (NoSuchAlgorithmException e) {
                logger.error("error", e);
            } catch (KeyManagementException e) {
                logger.error("error", e);
            }
        } else {
            if (CasLoginUtils.isUseCas()) {
                // 判断是否为单点登录
                try {
                    CasLoginResult loginResult = CasLoginUtils.login(req.getUsername(), req.getPassword());
                    if (loginResult.isSuccess()) {
                        userDetails = (UserDetails) userDetailsServiceProvider.getUserDetails(req.getUsername());
                        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                                userDetails, req.getPassword(), userDetails.getAuthorities());
                        result.setDetails(loginResult.getTicket());
                        SecurityContextHolder.getContext().setAuthentication(result);
                        // 设置access_token
                        String access_token = UUID.randomUUID().toString();
                        SpringSecurityUtils.cacheAccessToken(access_token, result);
                        response.setAccess_token(access_token);
                        success = true;
                    } else {
                        throw new RuntimeException("单点登录失败");
                    }
                } catch (Exception ex) {
                    logger.error("单点登录失败：", ex);
                }

            } else {
                // 非单点登录
                MultiOrgUserAccount userFromDb = multiOrgUserService.getUserAccountByLoginName(req.getUsername());
                if (userFromDb != null
                        && (Md5PasswordEncoderUtils.encodePassword(req.getPassword(), req.getUsername())
                        .equalsIgnoreCase(userFromDb.getPassword()) || StringUtils.equals(
                        SM3Util.encrypt(req.getPassword() + "{" + req.getUsername().toLowerCase() + "}"),
                        userFromDb.getPassword()))) {
                    success = true;

                    userDetails = (UserDetails) userDetailsServiceProvider.getUserDetails(req.getUsername());
                    UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(userDetails,
                            req.getPassword(), userDetails.getAuthorities());
                    result.setDetails(userFromDb.getPassword());
                    SecurityContextHolder.getContext().setAuthentication(result);
                    // 设置access_token
                    String access_token = UUID.randomUUID().toString();
                    SpringSecurityUtils.cacheAccessToken(access_token, result);
                    response.setAccess_token(access_token);
                }
            }
        }

        response.setSuccess(success);
        if (!success) {
            response.setMsg("用户名或密码错误！");
            return response;
        }

        // 如果登录请求来自手机内嵌网页则直接返回
        logger.debug("**********The remote source is ****" + req.getSource());
        if ("html5".equalsIgnoreCase(req.getSource())) {
            logger.debug("Login from HTML5, then return！");
            return response;
        }
        // 如果登录请求不是来自app
        if (null == req.getDeviceId()) {
            logger.debug("Login is not from APP, then return！");
            return response;
        }

        String accessToken = "";
        int deviceType = req.getDevicetype();

        if (deviceType == 0) {// 来自ios的登录
            accessToken = req.getToken();
        } else {
            accessToken = UUID.randomUUID().toString();
        }

        // 返回结果
        return response;
    }
}
