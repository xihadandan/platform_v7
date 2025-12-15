/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.service.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.encode.Md5PasswordEncoderUtils;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.domain.RequestParam;
import com.wellsoft.pt.api.entity.ApiAccessLog;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.facade.WellptServiceFactory;
import com.wellsoft.pt.api.internal.parser.WellptJsonParser;
import com.wellsoft.pt.api.internal.suport.ErrorCodes;
import com.wellsoft.pt.api.internal.suport.RequestUtils;
import com.wellsoft.pt.api.internal.suport.WellptRequestConverUtils;
import com.wellsoft.pt.api.service.ApiAccessLogService;
import com.wellsoft.pt.api.service.ApiOutSysConfigService;
import com.wellsoft.pt.api.service.WellptWebService;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.util.OrgUtil;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.interfaces.filter.LoginAuthenticationProcessingFilter;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.Calendar;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
@Path("/")
@WebService
public class WellptWebServiceImpl implements WellptWebService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    MongoFileService mongoFileService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private ApiAccessLogService apiAccessLogService;

    private static String getStreamAsString(DataHandler dataHandler) {
        InputStream stream = null;
        try {
            stream = dataHandler.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringWriter writer = new StringWriter();

            char[] chars = new char[256];
            int count = 0;
            while ((count = reader.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }

            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 对于湖里OA云的投诉咨询等一系列接口是不对密码进行校验的,这里请特别注意,如果要把密码校验加上去，请特别注意
     *
     * @see com.wellsoft.pt.api.service.WellptWebService#execute(com.wellsoft.pt.api.domain.RequestParam)
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public String execute(@FormParam("") RequestParam param, @Context MultipartBody multipartBody,
                          @Context HttpServletRequest httpServletRequest, @Context HttpServletResponse httpServletResponse) {
        String responseBody = null;
        boolean isIgnoreLogin = false;
        ApiAccessLog accessLog = preAccessLog(httpServletRequest, param);
        try {
            isIgnoreLogin = preAuth(httpServletRequest, param);

            WellptService service = WellptServiceFactory.getWellptService(param.getJson());

            Attachment attachment = null;
            if (multipartBody != null) {
                attachment = multipartBody.getAttachment("file");
            }
            WellptRequest<?> wellptRequest = WellptRequestConverUtils.convert(service, param, attachment);
            accessLog.setUserId(SpringSecurityUtils.getCurrentUserId());
            accessLog.setUsername(SpringSecurityUtils.getCurrentUserName());
            accessLog.setApiServiceName(wellptRequest.getApiServiceName());

            WellptResponse response = service.doService(wellptRequest);

            responseBody = WellptJsonParser.object2Json(response);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            responseBody = WellptJsonParser.object2Json(new FaultResponse(e));
        } finally {
            // 保存日志
            accessLog.setResponseBody(responseBody);
            apiAccessLogService.save(accessLog);

            if (isIgnoreLogin) {
                IgnoreLoginUtils.logout();
            }
        }

        return responseBody;
    }

    /**
     * 如何描述该方法
     *
     * @param httpServletRequest
     * @param param
     * @return
     */
    private ApiAccessLog preAccessLog(HttpServletRequest request, RequestParam param) {
        ApiAccessLog accessLog = new ApiAccessLog();
        accessLog.setTenantId(param.getTenantId());
        accessLog.setUsername(param.getUsername());
        accessLog.setRequestJson(param.getJson());
        accessLog.setClientIp(ServletUtils.getRemoteAddr(request));
        accessLog.setClientBrowser(request.getHeader("user-agent"));
        accessLog.setLogTime(Calendar.getInstance().getTime());
        return accessLog;
    }

    /**
     * @param request
     * @param param
     * @return
     * @throws Exception
     * @throws JSONException
     */
    private boolean preAuth(HttpServletRequest request, RequestParam param) throws Exception, JSONException {
        boolean isIgnoreLogin = false;
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (userDetails != null && !(StringUtils.equalsIgnoreCase(userDetails.getUserId(), param.getUsername())
                || StringUtils.equalsIgnoreCase(userDetails.getLoginName(), param.getUsername()))) {
            return isIgnoreLogin;
        } else {
            userDetails = null;
        }

        // 模拟虚拟登录
        String tenantId = param.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            tenantId = Config.DEFAULT_TENANT;
        }

        // 验证accessToken的有效性
        validAccessToken(request, param);

        // 获取用户时使用
        request.setAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_ID_KEY, tenantId);

        /*add by hunt 2015.9.15 begin*/
        String loginName = param.getUsername();
        MultiOrgUserAccount user = null;
        if (OrgUtil.isLoginNameUniqueInGlobal()) {// 用户名全局性唯一,租户ID通过用户名来确认
            if (loginName.startsWith(IdPrefix.USER.getValue())) {
                user = orgApiFacade.getAccountByUserId(loginName);
            } else {
                user = orgApiFacade.getAccountByLoginName(loginName);
            }
            if (user == null) {// 用户不存在
                throw new Exception("username[" + loginName + "] not exist in oa system");
            }
            // tenantId = user.getTenantId();
            // logger.error("actual teantId: " + tenantId);
        }
        /*add by hunt 2015.9.15 end*/

        // 强制API接口用户登录验证
        if (userDetails == null && RequestUtils.isRequestAuth()) {
            if (user == null) {
                if (loginName.startsWith(IdPrefix.USER.getValue())) {
                    user = orgApiFacade.getAccountByUserId(loginName);
                } else {
                    user = orgApiFacade.getAccountByLoginName(loginName);
                }
            }
            // 用户不存在
            if (user == null) {
                throw new Exception("username[" + loginName + "] not exist in oa system");
            }
            String password = user.getPassword();
            String paramPassword = param.getPassword();
            if (!(StringUtils.equals(password, paramPassword) || Md5PasswordEncoderUtils.encodePassword(paramPassword,
                    loginName).equalsIgnoreCase(password))) {
                // 密码验证失败
                throw new RuntimeException("password[" + paramPassword + "] verify failure");
            }
        }

        // 某些IP访问地址忽略登录
        if (userDetails == null && RequestUtils.isAllowIgnoreLogin(request)) {
            IgnoreLoginUtils.login(tenantId, loginName);
            isIgnoreLogin = true;
        }

        /*add by huanglinchuan 2015.2.12 begin*/
        String json = param.getJson();
        if (json != null) {
            JSONObject jsonObj = new JSONObject(json);
            jsonObj.put("username", param.getUsername());
            jsonObj.put("password", param.getPassword());
            param.setJson(jsonObj.toString());
        }
        /*add by huanglinchuan 2015.2.12 end*/
        return isIgnoreLogin;
    }

    /**
     * 验证accessToken的有效性
     *
     * @param requestParam
     */
    private void validAccessToken(HttpServletRequest request, RequestParam param) {
        String authorization = request.getHeader("Authorization");
        String accessToken = null;
        if (StringUtils.isNotBlank(authorization)) {
            int bearerIndex = authorization.indexOf("Bearer");
            if (bearerIndex != -1) {
                accessToken = authorization.substring(7);
            }
        } else {
            accessToken = request.getParameter("access_token");
        }
        if (StringUtils.isBlank(accessToken)) {
            return;
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(
                    Keys.hmacShaKeyFor(ApplicationContextHolder.getBean(
                            ApiOutSysConfigService.class).tokenKey())).parseClaimsJws(accessToken);
            logger.info("claimsJws: {}", claimsJws);
        } catch (PrematureJwtException e) {
            throw new RuntimeException("未生效的access_token: " + accessToken, e);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("过期的access_token: " + accessToken, e);
        } catch (Exception e) {
            throw new RuntimeException("无效的access_token: " + accessToken, e);
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("file")
    @Override
    public String executeFile(@Multipart MultipartBody multipartBody, @Context HttpServletRequest httpServletRequest,
                              @Context HttpServletResponse httpServletResponse) {
        Attachment tenantId = multipartBody.getAttachment("tenantId");
        Attachment username = multipartBody.getAttachment("username");
        Attachment password = multipartBody.getAttachment("password");
        Attachment json = multipartBody.getAttachment("json");
        Attachment file = multipartBody.getAttachment("file");
        RequestParam param = new RequestParam();
        param.setTenantId(tenantId == null ? "" : getStreamAsString(tenantId.getDataHandler()));
        param.setUsername(getStreamAsString(username.getDataHandler()));
        param.setPassword(password == null ? "" : getStreamAsString(password.getDataHandler()));
        param.setJson(getStreamAsString(json.getDataHandler()));
        return execute(param, new MultipartBody(file), httpServletRequest, httpServletResponse);
    }

    private static class FaultResponse extends WellptResponse {

        private static final long serialVersionUID = -4260333759388296179L;
        private Object data = null;

        public FaultResponse(Exception e) {
            if (e instanceof JsonDataException) {
                JsonDataException jde = (JsonDataException) e;
                super.setCode(ErrorCodes.getCode(jde.getErrorCode()));
                setData(jde.getData());
            } else {
                super.setCode("-3");
            }
            super.setMsg("error: " + e.getMessage());
            super.setSuccess(false);
        }

        @SuppressWarnings("unused")
        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}
