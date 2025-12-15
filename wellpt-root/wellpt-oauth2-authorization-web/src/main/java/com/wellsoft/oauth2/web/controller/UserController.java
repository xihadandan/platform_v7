package com.wellsoft.oauth2.web.controller;

import com.alibaba.excel.EasyExcel;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.wellsoft.oauth2.dto.UserDto;
import com.wellsoft.oauth2.entity.OAuthClientDetailEntity;
import com.wellsoft.oauth2.entity.UserInfoEntity;
import com.wellsoft.oauth2.excel.data.UserImportData;
import com.wellsoft.oauth2.excel.listener.UserImportListener;
import com.wellsoft.oauth2.security.SecurityUserDetailsRepositoryManager;
import com.wellsoft.oauth2.service.BatchDataImportHisService;
import com.wellsoft.oauth2.service.OAuthClientDetailService;
import com.wellsoft.oauth2.service.UserAccountService;
import com.wellsoft.oauth2.service.UserInfoService;
import com.wellsoft.oauth2.web.support.BasicResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.security.auth.login.AccountNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/21    chenq		2019/9/21		Create
 * </pre>
 */
@Controller
@RequestMapping("/user")
public class UserController extends EntityManagerController<UserInfoEntity> {

    @Resource
    UserAccountService userService;

    @Resource
    UserInfoService userInfoService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    BatchDataImportHisService batchDataImportHisService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private OAuthClientDetailService oAuthClientDetailService;

    @PostMapping("/expired")
    public @ResponseBody
    ResponseEntity<BasicResponse> expired(@RequestBody Map<String, String> params) {
        try {
            String loginName = params.get("_accountNumber");
            if (StringUtils.isBlank(loginName)) {
                throw new AccountNotFoundException("账号不为空");
            }
            userService.expiredAccount(loginName);
            List<OAuthClientDetailEntity> list = oAuthClientDetailService.listAll();
            for (OAuthClientDetailEntity client : list) {
                Collection<OAuth2AccessToken> auth2AccessTokens = tokenStore.findTokensByClientIdAndUserName(client.getClientId(), loginName);
                if (CollectionUtils.isNotEmpty(auth2AccessTokens)) {
                    for (OAuth2AccessToken token : auth2AccessTokens) {
                        tokenStore.removeAccessToken(token);
                    }
                }
            }
        } catch (AccountNotFoundException ex) {
            return ResponseEntity.ok(BasicResponse.fail().setMsg(ex.getMessage()));
        }

        return ResponseEntity.ok(BasicResponse.success());
    }

    @PostMapping("/password")
    public @ResponseBody
    ResponseEntity<BasicResponse> modifyPassword(@RequestBody Map<String, String> params) {
        try {
            ((SecurityUserDetailsRepositoryManager) userDetailsService).changePassword(
                    params.get("_old"), params.get("_new"), params.get("_accountNumber"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.ok(BasicResponse.fail().setMsg("请输入正确的旧密码"));
        } catch (Exception ex) {
            return ResponseEntity.ok(BasicResponse.fail().setMsg("修改密码失败"));
        }

        return ResponseEntity.ok(BasicResponse.success());
    }

    @GetMapping("{account}/editByAccoutNumber")
    public String eidtByAcctNumber(@PathVariable String account, Model model) {
        UserInfoEntity userInfoEntity = userInfoService.getBy("accountNumber", account);
        model.addAttribute("entity", userInfoEntity);
        model.addAttribute("allowSave", true);
        return requestMapping() + "/show";
    }

    @GetMapping("/check")
    public @ResponseBody
    ResponseEntity<BasicResponse> check(@RequestParam String account) {
        UserInfoEntity userInfoEntity = userInfoService.getBy("accountNumber", account);
        return ResponseEntity.ok(BasicResponse.success().setData(userInfoService.getBy("accountNumber", account) != null));
    }

    @PostMapping(value = "/registerAccount", produces = "application/json; charset=UTF-8")
    public @ResponseBody
    ResponseEntity<BasicResponse> registerAccount(@RequestBody UserDto userDto) {
        Long uuid = 0L;
        try {
            uuid = userService.addUser(userDto);
            Map<String, Object> user = Maps.newHashMap();
            UserInfoEntity userInfoEntity = toModel(uuid);
            user.put("accountNumber", userInfoEntity.getAccountNumber());
            user.put("uuid", userInfoEntity.getUuid());
            return ResponseEntity.ok(BasicResponse.success().setData(user));
        } catch (Exception e) {
            logger.error("注册用户信息异常：", Throwables.getStackTraceAsString(e));
            return ResponseEntity.ok(BasicResponse.fail().setMsg(e.getMessage()));
        }

    }

    @PostMapping(value = "/addAccount", produces = "application/json; charset=UTF-8")
    public @ResponseBody
    ResponseEntity<BasicResponse> addAccount(@RequestBody UserDto userDto) {
        return this.registerAccount(userDto);
    }

    @Override
    public ResponseEntity<BasicResponse> excelUpload(@RequestParam MultipartFile file) {
        try {
            UserImportListener userImportListener = new UserImportListener(userService,
                    batchDataImportHisService);
            EasyExcel.read(file.getInputStream(), UserImportData.class,
                    userImportListener).sheet().doRead();
            Map<String, Object> importResult = Maps.newHashMap();
            importResult.put("success", userImportListener.getSuccessCount());
            importResult.put("fail", userImportListener.getFailCount());
            importResult.put("batchImportUuid",
                    userImportListener.getBatchDataImportHisEntity().getUuid().toString());
            return ResponseEntity.ok(BasicResponse.success().setData(importResult));
        } catch (Exception e) {
            logger.error("上次用户数据异常：", e);
        }
        return ResponseEntity.ok(BasicResponse.success());
    }


    @Override
    protected UserInfoEntity toUpdate(RedirectAttributes model, UserInfoEntity entity,
                                      BindingResult errors, HttpServletRequest request,
                                      HttpServletResponse response) {
        UserInfoEntity existOne = userInfoService.getOne(entity.getUuid());
        existOne.setCellphoneNumber(entity.getCellphoneNumber());
        existOne.setUserName(entity.getUserName());
        return super.toUpdate(model, existOne, errors, request, response);
    }

    @Override
    public ResponseEntity delete(RedirectAttributes model, Long uuid, UserInfoEntity entity,
                                 HttpServletRequest request, HttpServletResponse response) {
        userInfoService.deleteUser(uuid);
        return ResponseEntity.ok(BasicResponse.success());
    }

    @Override
    protected String toBatchDelete(RedirectAttributes model, Long[] items,
                                   HttpServletRequest request, HttpServletResponse response,
                                   String defaultView) {
        userInfoService.delteUsers(items);
        return null;
    }
}
