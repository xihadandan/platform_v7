package com.wellsoft.pt.app.dingtalk.web;

import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import com.google.common.base.Throwables;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.api.response.ApiResponse;
import com.wellsoft.pt.app.dingtalk.constants.DingtalkApis;
import com.wellsoft.pt.app.dingtalk.entity.EventCallBack;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgDingUser;
import com.wellsoft.pt.app.dingtalk.service.EventCallBackService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgDingDeptService;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgDingUserService;
import com.wellsoft.pt.app.dingtalk.support.DingtalkConfig;
import com.wellsoft.pt.app.dingtalk.support.event.CallBackEvent;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiUtils;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkUtils;
import com.wellsoft.pt.app.dingtalk.utils.EventTypeUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetailsCacheHolder;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.JwtTokenUtil;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 钉钉接口controller
 *
 * @author Well
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月29日.1	Well		2020年5月29日		Create
 * </pre>
 * @date 2020年5月29日
 */
@Controller
@RequestMapping("mobile/pt/dingtalk")
@Deprecated
public class DingtalkController extends BaseController {

    /**
     * 相应钉钉回调时的值
     */
    private static final String CALLBACK_RESPONSE_SUCCESS = "success";
    @Autowired
    private DingtalkConfig dingtalkConfig;
    @Autowired
    private MultiOrgUserService multiOrgUserService;
    @Autowired
    private EventCallBackService eventCallBackService;
    @Autowired
    private MultiOrgDingUserService multiOrgDingUserService;
    @Autowired
    private MultiOrgDingDeptService multiOrgDingDeptService;
    @Autowired
    @Qualifier("userDetailsServiceProvider")
    private UserDetailsServiceProvider userDetailsServiceProvider;

    /**
     * 回调事件监听
     *
     * @return
     * @throws JSONException
     */
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> callback(HttpServletRequest request, HttpServletResponse response) {
        /** url中的签名 **/
        String msgSignature = request.getParameter("signature");
        /** url中的时间戳 **/
        String timeStamp = request.getParameter("timestamp");
        /** url中的随机字符串 **/
        String nonce = request.getParameter("nonce");
        try {
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, dingtalkConfig.getSystemUnitId());
            /** 对encrypt进行解密 **/
            DingTalkEncryptor dingTalkEncryptor = new DingTalkEncryptor(dingtalkConfig.getEventCallBackToken(),
                    dingtalkConfig.getAppAesKey(), dingtalkConfig.getCorpId());
            JSONObject jsonEncrypt = JSONObject.fromObject(IOUtils.toString(request.getInputStream(),
                    request.getCharacterEncoding()));
            String encrypt = jsonEncrypt.getString("encrypt");
            String plainText = dingTalkEncryptor.getDecryptMsg(msgSignature, timeStamp, nonce, encrypt);

            /** 对从encrypt解密出来的明文进行处理 **/
            JSONObject plainTextJson = JSONObject.fromObject(plainText);
            String eventType = plainTextJson.getString("EventType");
            // 时间类型为check_url时，不做数据插入，该动作为钉钉测试回调地址，请求成功，直接返回“success”加密数据即可。
            if (false == StringUtils.equals("check_url", eventType.trim())) {
                String dingTimeStamp = plainTextJson.getString("TimeStamp");
                String userId = plainTextJson.optString("UserId"); // 用户信息
                String deptId = plainTextJson.optString("DeptId"); // 部门信息
                String corpId = plainTextJson.optString("CorpId");
                String labelId = plainTextJson.optString("LabelIdList");

                // 先保存事件回调的数据，后续通过job任务扫描表数据，进行真正的数据落地，避免数据落地业务复杂，导致平台迟迟不响应钉钉服务器请求二出错
                EventCallBack eventCallBack = new EventCallBack();
                eventCallBack.setEventType(eventType);
                eventCallBack.setDingTimeStamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(dingTimeStamp))));
                eventCallBack.setDingUserId(userId);
                eventCallBack.setDingDeptId(deptId);
                eventCallBack.setDingLabelId(labelId);
                eventCallBack.setDingCorpId(corpId);
                eventCallBack.setStatus(EventCallBack.CALLBACK_STATUS_0); // 默认为0，未处理
                // eventCallBack.setOptTime(new Date());
                eventCallBack.setSyncContent(EventTypeUtils.eventType2SyncContent(eventType));
                eventCallBackService.save(eventCallBack);
                ApplicationContextHolder.publishEvent(new CallBackEvent(this, eventCallBack));
            }
            /** 对返回信息进行加密 **/
            long timeStampLong = Long.parseLong(timeStamp);
            return dingTalkEncryptor.getEncryptedMap(CALLBACK_RESPONSE_SUCCESS, timeStampLong, nonce);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    @RequestMapping(value = "/start")
    public String start(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (userDetails == null) {
            String uri = getStartUri(request);
            return redirect(uri);
        } else {
            return redirect(DingtalkUtils.urlDecode(request.getParameter("uri")));
        }
    }

    /**
     * @param request
     * @return
     */
    @GetMapping(value = "/start/uri")
    public String getStartUri(HttpServletRequest request) {
        // 没有登录，跳转到钉钉获取授权code，并将uri传递过去
        String dingTalkAuthUri = DingtalkApis.URI_SNS_AUTHORIZE;
        String redirectUri = "/mobile/pt/dingtalk/auth?uri=" + DingtalkUtils.urlEncode(request.getParameter("uri"));
        String encodeRedirectUri = DingtalkUtils.urlEncode(dingtalkConfig.getCorpDomainUri() + redirectUri);
        // 跳转到钉钉
        String uri = DingtalkUtils.uriFormat(dingTalkAuthUri, dingtalkConfig.getSnsAppId(), encodeRedirectUri);
        return uri;
    }

    @GetMapping(value = "/auth")
    public String auth(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        UserDetails userDetails = null;
        String redirectUri = DingtalkUtils.urlDecode(request.getParameter("uri"));
        userDetails = SpringSecurityUtils.getCurrentUser();
        if (userDetails == null) {
            try {
                String code = request.getParameter("code");
                userDetails = dingtalkLoginUserByCode(code);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails,
                            userDetails.getPassword(), userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(token);
                } else {
                    throw new BadCredentialsException("授权登录失败");
                }
            } catch (Exception ex) {
                throw new BusinessException(ex.getMessage(), ex);
            }
        }
        return redirect(redirectUri);
    }

    private DefaultUserDetails dingtalkLoginUserByCode(String code) {
        JSONObject userInfoResult = DingtalkApiUtils.getUserInfoByCode(code);
        if (userInfoResult != null) {
            MultiOrgUserAccount account = null;
            DingtalkUtils.checkResult(userInfoResult);
            JSONObject userInfoObj = userInfoResult.getJSONObject("user_info");
            String unionId = userInfoObj.getString("unionid");
            MultiOrgDingUser orgDingUser = multiOrgDingUserService.getByUnionId(unionId);
            if (orgDingUser != null
                    && (account = multiOrgUserService.getAccountByUserId(orgDingUser.getUserId())) != null) {
                String loginName = account.getLoginName();
                return (DefaultUserDetails) userDetailsServiceProvider.getUserDetails(loginName);
            } else {
                throw new BusinessException("用户未同步,请联系管理员同步用户信息.");
            }
        } else {
            throw new BusinessException("获取用户授权失败,请检查网络连接或者应用配置.");
        }
    }

    @GetMapping(value = "/auth/token/{code}")
    public @ResponseBody
    ResponseEntity<ApiResponse> auth(@PathVariable("code") String code) throws IOException {
        if (SpringSecurityUtils.getCurrentUser() == null) {
            try {
                DefaultUserDetails userDetails = dingtalkLoginUserByCode(code);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails,
                            userDetails.getPassword(), userDetails.getAuthorities());
                    UserDetailsCacheHolder
                            .putUserInCache((org.springframework.security.core.userdetails.UserDetails) token
                                    .getPrincipal());
                    userDetails = (DefaultUserDetails) token.getPrincipal();
                    userDetails.setToken(JwtTokenUtil.createToken((UserDetails) token.getPrincipal()));
                    return ResponseEntity.ok(ApiResponse.build().data(userDetails));
                } else {
                    throw new BadCredentialsException("授权登录失败");
                }
            } catch (Exception e) {
                logger.error("钉钉扫码：{} , 登录异常：{}", code, Throwables.getStackTraceAsString(e));
                return ResponseEntity.ok(ApiResponse.build().code(-1).msg(e.getMessage()));
            }
        }
        return ResponseEntity.ok(ApiResponse.build().code(-3).msg("登录无效"));
    }

    @RequestMapping(value = "/getJsApiConfig")
    @ResponseBody
    public Map<String, Object> getJsApiConfig(HttpServletRequest request, HttpServletResponse response, Model model)
            throws IOException {
        String signedUrl = request.getParameter("signedUrl");
        if (StringUtils.isBlank(signedUrl)) {
            String urlString = request.getRequestURL().toString();
            String queryString = request.getQueryString();
            String queryStringEncode = null;
            if (queryString != null) {
                queryStringEncode = DingtalkUtils.urlDecode(queryString);
                signedUrl = urlString + "?" + queryStringEncode;
            } else {
                signedUrl = urlString;
            }
        }
        /**
         * 确认url与配置的应用首页地址一致
         */

        /**
         * 随机字符串
         */
        String nonceStr = dingtalkConfig.getEventCallBackToken();//"abcdefg";
        long timeStamp = System.currentTimeMillis() / 1000;
        String ticket = DingtalkApiUtils.getJsapiTicket();
        String signature = DingtalkUtils.sign(ticket, nonceStr, timeStamp, signedUrl);
        Map<String, Object> configValue = new HashMap<>();
        configValue.put("jsticket", ticket);
        configValue.put("signature", signature);
        configValue.put("nonceStr", nonceStr);
        configValue.put("timeStamp", timeStamp);
        configValue.put("corpId", dingtalkConfig.getCorpId());
        configValue.put("agentId", dingtalkConfig.getAgentId());
        return configValue;
    }

    @RequestMapping(value = "/getconnect/qrconnect")
    @ResponseBody
    public String getQrconnect(HttpServletRequest request, HttpServletResponse response, Model model)
            throws IOException {
        String uri = request.getParameter("uri");
        uri = StringUtils.isBlank(uri) ? "/passport/user/login/success" : uri;
        String dingTalkAuthUri = DingtalkApis.URI_SNS_LOGIN_QR;
        String redirectUri = "/mobile/pt/dingtalk/auth?uri=" + DingtalkUtils.urlEncode(uri);
        String encodeRedirectUri = DingtalkUtils.urlEncode(dingtalkConfig.getCorpDomainUri() + redirectUri);
        return DingtalkUtils.uriFormat(dingTalkAuthUri, dingtalkConfig.getSnsAppId(), encodeRedirectUri);
    }

    @RequestMapping(value = "/getconnect/oauth2")
    @ResponseBody
    public String getOauth2(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        String uri = request.getParameter("uri");
        String loginTmpCode = request.getParameter("loginTmpCode");
        uri = StringUtils.isBlank(uri) ? "/passport/user/login/success" : uri;
        String dingTalkAuthUri = DingtalkApis.URI_SNS_LOGIN_OAUTH2;
        if (StringUtils.isNotBlank(loginTmpCode)) {
            dingTalkAuthUri += "&loginTmpCode=" + loginTmpCode;
        }
        String redirectUri = "/mobile/pt/dingtalk/auth?uri=" + DingtalkUtils.urlEncode(uri);
        String encodeRedirectUri = DingtalkUtils.urlEncode(dingtalkConfig.getCorpDomainUri() + redirectUri);
        return DingtalkUtils.uriFormat(dingTalkAuthUri, dingtalkConfig.getSnsAppId(), encodeRedirectUri);
    }

}
