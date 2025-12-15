package com.wellsoft.pt.security.oauth2.facade.service.impl;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.wellsoft.context.config.Config;
import com.wellsoft.pt.security.audit.facade.service.OAuth2UserFacadeService;
import com.wellsoft.pt.security.oauth2.token.LocalOAuth2UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月10日   chenq	 Create
 * </pre>
 */
@Service
@Conditional(OAuth2UserFacadeServiceImpl.OAuth2UserCondition.class)
public class OAuth2UserFacadeServiceImpl implements OAuth2UserFacadeService {
    @Value("#{'${spring.security.oauth2.accessTokenUri}'.replace('oauth/token','')}")
    private String oauthUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Resource(name = "trustClientRestTemplate")
    private OAuth2RestTemplate oAuth2RestTemplate;

    @Override
    public boolean existUser(String loginName) {
        ResponseEntity<Map> responseEntity = oAuth2RestTemplate.getForEntity(oauthUrl + "user/check?account=" + loginName, Map.class);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            Map result = (Map) responseEntity.getBody();
            return (Boolean) result.get("data");
        }
        return true;
    }

    @Override
    public void addUser(String userjson) {
        Map<String, String> account = new Gson().fromJson(userjson, Map.class);
        LocalOAuth2UserInfo userInfo = new LocalOAuth2UserInfo();
        userInfo.setAccountNumber(account.get("loginName"));
        userInfo.setPassword(account.get("password"));
        userInfo.setUserName(account.get("userName"));
        ResponseEntity<Map> responseEntity = oAuth2RestTemplate.postForEntity(oauthUrl + "user/registerAccount", userInfo, Map.class);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            Map result = (Map) responseEntity.getBody();
            if (!"0".equals((String) result.get("code"))) {
                throw new RuntimeException("统一认证中心新增用户失败：" + result.get("msg") + "");
            }
        }
    }

    @Override
    public void deleteUser(String loginName) {

    }

    @Override
    public void modifyPassword(String loginName, String newPassword, String oldPassword) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("_old", oldPassword);
        params.put("_new", newPassword);
        params.put("_accountNumber", loginName);
        ResponseEntity<Map> responseEntity = oAuth2RestTemplate.postForEntity(oauthUrl + "user/password", params, Map.class);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            Map result = (Map) responseEntity.getBody();
            if (!"0".equals((String) result.get("code"))) {
                throw new RuntimeException("统一认证中心修改用户密码失败：" + result.get("msg") + "");
            }
        }
    }

    @Override
    public void expiredUser(String loginName) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("_accountNumber", loginName);
        ResponseEntity<Map> responseEntity = oAuth2RestTemplate.postForEntity(oauthUrl + "user/expired", params, Map.class);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            Map result = (Map) responseEntity.getBody();
            if (!"0".equals((String) result.get("code"))) {
                throw new RuntimeException("统一认证中心注销用户失败：" + result.get("msg") + "");
            }
        }
    }

    public static class OAuth2UserCondition implements Condition {
        public OAuth2UserCondition() {
        }

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            // 默认是开启统一认证用户与组织用户一致性的
            return !"true".equalsIgnoreCase(Config.getValue("oauth2.user.disabledSync"));
        }
    }
}
