package com.wellsoft.pt.multi.org.web.controller;

import com.wellsoft.context.config.SystemParamsUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.dto.ResetUserDefinedPwdDto;
import com.wellsoft.pt.multi.org.dto.ResetUserPwdDto;
import com.wellsoft.pt.multi.org.entity.MultiUserRelationAccountEntity;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgPwdSettingFacadeService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.vo.MultiUserRelationAccountVo;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetailsCacheHolder;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.util.JwtTokenUtil;
import com.wellsoft.pt.security.util.TenantContextHolder;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description: 组织用户管理接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/8.1	    zenghw		2021/3/8		    Create
 * </pre>
 * @date 2021/3/8
 */
@Api(tags = "组织用户账号管理接口")
@RestController
@RequestMapping("/api/org/user/account")
public class MultiOrgUserAccountController extends BaseController {

    @Autowired
    private MultiOrgUserAccountFacadeService multiOrgUserAccountFacadeService;
    @Autowired
    private MultiOrgPwdSettingFacadeService multiOrgPwdSettingFacadeService;

    @Qualifier("userDetailsServiceProvider")
    @Autowired
    private UserDetailsServiceProvider userDetailsService;

    @ApiOperation(value = "重置所有用户密码-随机密码", notes = "重置所有resetAllUserDefinedPwd用户密码-随机密码", tags = {"组织用户账号分组",
            "组织管理--->用户"})
    @PostMapping("/resetAllUserPwd")
    @ApiOperationSupport(order = 10)
    public ApiResult<String> resetAllUserPwd(HttpServletRequest request, HttpServletResponse response) {
        try {
            return ApiResult.success(multiOrgUserAccountFacadeService.resetAllUserRandomPwd(request, response));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("重置所有用户密码报错：", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "重置所有用户密码-自定义密码", notes = "重置所有用户密码-自定义密码", tags = {"组织用户账号分组", "组织管理--->用户"})
    @PostMapping("/resetAllUserDefinedPwd")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userDefinedPwd", value = "自定义密码", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 11)
    public ApiResult<Boolean> resetAllUserDefinedPwd(
            @RequestParam(name = "userDefinedPwd", required = true) String userDefinedPwd) {

        try {
            Boolean flg = multiOrgUserAccountFacadeService.resetAllUserDefinedPwd(userDefinedPwd);
            if (flg) {
                return ApiResult.success();
            }
        } catch (Exception e) {
            logger.error("重置所有用户密码-自定义密码 :", e);
        }

        return ApiResult.fail();
    }

    @ApiOperation(value = "重置指定用户的密码-随机密码", notes = "重置指定用户的密码-随机密码", tags = {"组织用户账号分组", "组织管理--->用户"})
    @PostMapping("/resetUserPwd")
    @ApiOperationSupport(order = 12)
    public ApiResult<String> resetUserPwd(@RequestBody ResetUserPwdDto vo) {
        try {
            String newPwd = multiOrgUserAccountFacadeService.resetUserRandomPwd(vo.getUserUuids());
            return ApiResult.success(newPwd);
        } catch (Exception e) {
            logger.error("重置指定用户的密码-随机密码 :", e);
            return ApiResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "重置指定用户的密码-自定义密码", notes = "重置指定用户的密码-自定义密码", tags = {"组织用户账号分组", "组织管理--->用户"})
    @PostMapping("/resetUserDefinedPwd")
    @ApiOperationSupport(order = 13)
    public ApiResult<Boolean> resetUserDefinedPwd(@RequestBody ResetUserDefinedPwdDto vo) {
        try {
            Boolean flg = multiOrgUserAccountFacadeService.resetUserDefinedPwd(vo.getUserUuids(),
                    vo.getUserDefinedPwd());
            if (flg) {
                return ApiResult.success();
            }
        } catch (Exception e) {
            logger.error("重置指定用户的密码-自定义密码 :", e);
        }

        return ApiResult.fail();
    }

    @ApiOperation(value = "禁用账号", notes = "禁用账号", tags = {"组织用户账号分组", "组织管理--->用户"})
    @PostMapping("/forbidAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "账号uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 30)
    public ApiResult<Boolean> forbidAccount(@RequestParam(name = "uuid", required = false) String uuid) {
        Boolean flg = multiOrgUserAccountFacadeService.forbidAccount(uuid);
        if (flg) {
            return ApiResult.success();
        }
        return ApiResult.fail();
    }

    @ApiOperation(value = "启用账号", notes = "启用账号", tags = {"组织用户账号分组", "组织管理--->用户"})
    @PostMapping("/unforbidAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "账号uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 40)
    public ApiResult<Boolean> unforbidAccount(@RequestParam(name = "uuid", required = false) String uuid) {
        Boolean flg = multiOrgUserAccountFacadeService.unforbidAccount(uuid);
        if (flg) {
            return ApiResult.success();
        }
        return ApiResult.fail();
    }

    @ApiOperation(value = "锁住账号", notes = "锁住账号", tags = {"组织用户账号分组", "组织管理--->用户"})
    @PostMapping("/lockAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "账号uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 50)
    public ApiResult<Boolean> lockAccount(@RequestParam(name = "uuid", required = false) String uuid) {
        Boolean flg = multiOrgUserAccountFacadeService.lockAccount(uuid);
        if (flg) {
            return ApiResult.success();
        }
        return ApiResult.fail();
    }

    @ApiOperation(value = "解锁账号", notes = "解锁账号", tags = {"组织用户账号分组", "组织管理--->用户"})
    @PostMapping("/unlockAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "账号uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 60)
    public ApiResult<Boolean> unlockAccount(@RequestParam(name = "uuid", required = false) String uuid) {
        Boolean flg = multiOrgUserAccountFacadeService.unlockAccount(uuid);
        if (flg) {
            return ApiResult.success();
        }
        return ApiResult.fail();
    }

    @ApiOperation(value = "密码错误锁定-解锁账号", notes = "密码错误锁定-解锁账号", tags = {"组织用户账号分组", "组织管理--->用户"})
    @PostMapping("/pwdUnlockAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "账号uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 70)
    public ApiResult<Boolean> pwdUnlockAccount(@RequestParam(name = "uuid", required = false) String uuid) {
        Boolean flg = multiOrgUserAccountFacadeService.pwdUnlockAccount(uuid);
        if (flg) {
            return ApiResult.success();
        }
        return ApiResult.fail();
    }

    @ApiOperation(value = "测试密码过期提醒", notes = "测试密码过期提醒")
    @GetMapping("/pwdValidityWarn")
    @ApiOperationSupport(order = 80)
    public ApiResult pwdValidityWarn() {
        return ApiResult.success("有发消息提醒条数：" + multiOrgPwdSettingFacadeService.pwdValidityWarn());
    }

    @ApiOperation(value = "统计非禁用账号总数", notes = "统计非禁用账号总数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "systemUnitId", value = "系统单位ID", paramType = "query", dataType = "String", required = true)})
    @GetMapping("/countUnforbiddenAccount")
    @ApiOperationSupport(order = 90)
    public ApiResult<Integer> countUnforbiddenAccount(@RequestParam(name = "systemUnitId", required = true) String systemUnitId) {
        return ApiResult.success(multiOrgUserAccountFacadeService.countUnforbiddenAccount(systemUnitId));
    }

    @ApiOperation(value = "保存关联账户", notes = "保存关联账户")
    @PostMapping("/saveRelationAccount")
    @ApiOperationSupport(order = 100)
    public ApiResult saveRelationAccount(@RequestBody MultiUserRelationAccountVo relationAccountVo, HttpServletRequest request) {
        String message = multiOrgUserAccountFacadeService.saveRelationAccount(relationAccountVo, request);
        if (StringUtils.isNotBlank(message)) {
            return ApiResult.fail(message);
        }
        return ApiResult.success();
    }

    @ApiOperation(value = "获取关联账户列表", notes = "获取关联账户列表")
    @GetMapping("/getRelationAccounts")
    @ApiOperationSupport(order = 110)
    public ApiResult<List<MultiUserRelationAccountEntity>> getRelationAccounts() {
        return ApiResult.success(multiOrgUserAccountFacadeService.getRelationAccountsWithLoginAddr());
    }

    @ApiOperation(value = "解除账户关联信息", notes = "解除账户关联信息")
    @GetMapping("/unbound/{relationAccount}")
    @ApiOperationSupport(order = 120)
    public ApiResult unboundAccount(@PathVariable String relationAccount) {
        return ApiResult.success(multiOrgUserAccountFacadeService.unboundAccount(relationAccount));
    }

    @ApiOperation(value = "设置为主账户", notes = "设置为主账户")
    @GetMapping("/setMasterAccount/{masterAccount}")
    @ApiOperationSupport(order = 110)
    public ApiResult setMasterAccount(@PathVariable String masterAccount) {
        boolean flag = multiOrgUserAccountFacadeService.setMasterAccount(masterAccount);
        if (!flag) {
            return ApiResult.fail();
        }
        return ApiResult.success();
    }

    @ApiOperation(value = "切换账户", notes = "切换账户")
    @PostMapping("/changingOver/{account}")
    @ApiOperationSupport(order = 120)
    public ApiResult<UserDetails> changingOver(@PathVariable String account, HttpServletRequest request, HttpServletResponse response) {
        //判断账户是否是关联账户
        boolean flag = multiOrgUserAccountFacadeService.checkRelationAccount(account);
        if (!flag) {
            return ApiResult.fail("未关联账户");
        }
        //获取平台系统参数及当前系统和关联账户系统是否开启
        //启用为enable
        String relationEnable = SystemParamsUtils.getValue("system.account.relation.enable");
        if (!"enable".equals(relationEnable)) {
            return ApiResult.fail("平台或系统未启用账户关联,请联系管理员");
        }

        //查询关联账户信息
        UserDetails userDetails = userDetailsService.getUserDetails(account);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
        TenantContextHolder.reset();
        UserDetailsCacheHolder.putUserInCache((UserDetails) authentication.getPrincipal());
        DefaultUserDetails defaultUserDetails = (DefaultUserDetails) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        defaultUserDetails.setToken(JwtTokenUtil.createToken((UserDetails) authentication.getPrincipal()));

        ///重新生成token
        //String token = JwtTokenUtil.createToken(defaultUserDetails);
//        //修改redis用户信息
        response.setHeader("refreshtoken", defaultUserDetails.getToken());
//        Cookie tokenCookie = new Cookie("jwt", token);
//        tokenCookie.setPath("/");
//        response.addCookie(tokenCookie);
//        Cookie authTypeCookie = new Cookie("auth", "jwt");
//        authTypeCookie.setPath("/");
//        response.addCookie(authTypeCookie);
        return ApiResult.success(userDetails);
    }


}
