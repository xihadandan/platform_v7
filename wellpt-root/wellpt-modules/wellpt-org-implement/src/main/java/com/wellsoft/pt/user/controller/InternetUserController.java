package com.wellsoft.pt.user.controller;

import com.wellsoft.context.Context;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.api.response.ApiResponse;
import com.wellsoft.pt.security.audit.facade.service.OAuth2UserFacadeService;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.service.UserAccountService;
import com.wellsoft.pt.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月11日   chenq	 Create
 * </pre>
 */
@Controller
public class InternetUserController extends BaseController {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserAccountService userAccountService;


    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    @PostMapping("/iuser")
    public @ResponseBody
    ApiResponse addUser(@RequestBody UserDto user, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        ApiResponse response = ApiResponse.build();
        try {
            userInfoService.addUserInfo(user);
        } catch (Exception e) {
            logger.error("注册用户异常：", e);
            response.setCode(-1);
            response.setMsg(e.getMessage());
        }
        return response;
    }


    @GetMapping("/iuser/check")
    public @ResponseBody
    ApiResponse checkExistAccount(@RequestParam String loginName) {
        ApiResponse response = ApiResponse.build();
        try {
            if (Context.isOauth2Enable()) {
                try {
                    OAuth2UserFacadeService oAuth2UserFacadeService = ApplicationContextHolder.getBean(OAuth2UserFacadeService.class);
                    response.setCode(oAuth2UserFacadeService.existUser(loginName) ? -1 : 0);
                    return response;
                } catch (Exception e) {
                    logger.error("统一认证校验用户账号是否存在异常：", e);
                    response.setCode(-1);
                    return response;
                }
            }
            response.setCode(userAccountService.getByLoginName(loginName.toLowerCase()) == null ? 0 : -1);
        } catch (Exception e) {
            response.setCode(-1);
            response.setMsg(e.getMessage());
        }
        return response;
    }


    @RequestMapping("/iuser/register")
    public String toRegisterPage(HttpServletRequest request, HttpServletResponse response) {
        return forward("/user/register");
    }

    @RequestMapping("/iuser/role")
    public String role(HttpServletRequest request, HttpServletResponse response) {
        return forward("/user/user_role_bind");
    }


}
