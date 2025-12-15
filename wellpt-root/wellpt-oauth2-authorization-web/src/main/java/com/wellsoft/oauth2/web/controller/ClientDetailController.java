package com.wellsoft.oauth2.web.controller;

import com.wellsoft.oauth2.entity.OAuthClientDetailEntity;
import com.wellsoft.oauth2.service.OAuthClientDetailService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/10    chenq		2019/9/10		Create
 * </pre>
 */
@Controller
@RequestMapping("/client")
public class ClientDetailController extends EntityManagerController<OAuthClientDetailEntity> {


    @Autowired
    OAuthClientDetailService oAuthClientDetailService;


    @Override
    protected ResponseEntity toCreate(Model model, RedirectAttributes redirectAttributes,
                                      OAuthClientDetailEntity entity, BindingResult errors,
                                      HttpServletRequest request, HttpServletResponse response) {
        //设置clientId/clientSecret
        entity.setClientId(
                DigestUtils.md5Hex(entity.getClientName() + UUID.randomUUID().toString()));
        entity.setClientSecret(DigestUtils.sha256Hex(
                entity.getClientName() + entity.getWebServerRedirectUri() + UUID.randomUUID().toString()));
        entity.setAutoapprove("true");
        entity.setAuthorities("ROLE_TRUSTED_CLIENT");
        entity.setAuthorizedGrantTypes("authorization_code,password,implicit,client_credentials");
        return super.toCreate(model, redirectAttributes, entity, errors, request, response);
    }


    @Override
    protected OAuthClientDetailEntity toUpdate(RedirectAttributes model,
                                               OAuthClientDetailEntity entity, BindingResult errors,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {
        OAuthClientDetailEntity clientDetailEntity = toModel(entity.getUuid());
        if (clientDetailEntity == null) {
            throw new RuntimeException("系统数据异常");
        }
        clientDetailEntity.setWebServerRedirectUri(entity.getWebServerRedirectUri());
        clientDetailEntity.setAdditionalInformation(entity.getAdditionalInformation());
        clientDetailEntity.setClientName(entity.getClientName());
        clientDetailEntity.setLoginPage(entity.getLoginPage());
        return super.toUpdate(model, clientDetailEntity, errors, request, response);
    }
}
