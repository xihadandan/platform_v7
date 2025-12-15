package com.wellsoft.pt.org.web;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.util.PwdUtils;
import com.wellsoft.pt.org.dto.OrgUserDto;
import com.wellsoft.pt.org.dto.UserAcctPasswordRules;
import com.wellsoft.pt.org.entity.OrgElementI18nEntity;
import com.wellsoft.pt.org.entity.OrgGroupEntity;
import com.wellsoft.pt.org.service.OrgElementI18nService;
import com.wellsoft.pt.org.service.OrgUserService;
import com.wellsoft.pt.org.service.OrgVersionService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.dto.UserDetailsVo;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.entity.UserAcctPasswordRuleEntity;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.service.UserAccountService;
import com.wellsoft.pt.user.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Description: 组织API服务
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Api(tags = "用户")
@RestController
@RequestMapping("/api/user")
public class ApiUserController extends BaseController {

    @Resource
    UserInfoService userInfoService;

    @Resource
    UserAccountService userAccountService;

    @Resource
    OrgUserService orgUserService;

    @Resource
    OrgVersionService orgVersionService;

    @Resource
    OrgElementI18nService orgElementI18nService;

    @ApiOperation(value = "查询账号是否已经被使用了", notes = "查询账号是否已经被使用了")
    @GetMapping("/checkLoginNameOccupied/{loginName}")
    public ApiResult<Boolean> checkLoginNameOccupied(@PathVariable("loginName") String loginName) {
        return ApiResult.success(userInfoService.getByLoginName(loginName) != null);
    }

    @ApiOperation(value = "校验用户是否已经存在", notes = "校验用户是否已经存在")
    @PostMapping("/checkUserExist")
    public ApiResult<Boolean> checkUserExist(@RequestBody UserInfoEntity userInfoEntity) {
        return ApiResult.success(userInfoService.checkUserExist(userInfoEntity));
    }

    @ApiOperation(value = "保存组织用户", notes = "保存组织用户")
    @PostMapping("/org/save")
    public ApiResult<String> saveOrgUser(@RequestBody UserDto user) {
        return ApiResult.success(userInfoService.saveUser(user));
    }


    @ApiOperation(value = "获取组织版本下的用户信息", notes = "获取组织版本下的用户信息")
    @GetMapping("/org/getUserDetails")
    public ApiResult<UserDto> getUserDetails(@RequestParam String userUuid, @RequestParam(required = false) Long orgVersionUuid,
                                             @RequestParam(required = false) Boolean fetchI18nName) {
        UserDto userDto = userInfoService.getUserDetailsByUuid(userUuid, orgVersionUuid);
        if (userDto != null && BooleanUtils.isTrue(fetchI18nName)) {
            userDto.setUserNameI18ns(userInfoService.getUserNameI18nsByUserUuid(userUuid));
        }
        return ApiResult.success(userDto);
    }

    @ApiOperation(value = "获取系统下的用户信息", notes = "获取系统下的用户信息")
    @GetMapping("/org/getUserDetailsUnderSystem")
    public ApiResult<UserDetailsVo> getUserDetailsUnderSystem(@RequestParam String userId, @RequestParam(required = false) String system) {
        return ApiResult.success(orgUserService.getOrgUserDetailsByUerIdAndSystem(userId, StringUtils.defaultIfBlank(system, RequestSystemContextPathResolver.system())));
    }

    @ApiOperation(value = "获取系统下的用户组织信息", notes = "获取系统下的用户组织信息")
    @GetMapping("/org/getUserOrgs")
    public ApiResult<List<OrgUserDto>> getUserOrgs(@RequestParam String userId) {
        String defaultOrgVersionId = orgVersionService.getDefaultOrgVersionBySystem(RequestSystemContextPathResolver.system()).getId();
        return ApiResult.success(orgUserService.listOrgUser(userId, new String[]{defaultOrgVersionId}));
    }

    @ApiOperation(value = "获取用户归属的群组", notes = "获取用户归属的群组")
    @GetMapping("/org/getGroupsIncludeUser")
    public ApiResult<List<OrgGroupEntity>> getGroupsIncludeUser(@RequestParam String userId, @RequestParam(required = false) String system) {
        List<OrgGroupEntity> groups = orgUserService.getGroupsIncludeUser(userId, StringUtils.defaultIfBlank(system, RequestSystemContextPathResolver.system()));
        if (CollectionUtils.isNotEmpty(groups) && !LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            Set<Long> uuids = Sets.newHashSet();
            Map<Long, OrgGroupEntity> map = Maps.newHashMap();
            groups.forEach(entity -> {
                uuids.add(entity.getUuid());
                map.put(entity.getUuid(), entity);
            });
            List<OrgElementI18nEntity> i18nEntities = orgElementI18nService.listOrgElementI18ns(uuids, "name", LocaleContextHolder.getLocale().toString());
            if (CollectionUtils.isNotEmpty(i18nEntities)) {
                i18nEntities.forEach(item -> {
                    if (map.containsKey(item.getDataUuid()) && StringUtils.isNotBlank(item.getContent())) {
                        map.get(item.getDataUuid()).setName(item.getContent());
                    }
                });
            }
        }
        return ApiResult.success(groups);
    }

    @ApiOperation(value = "查询组织版本下的用户", notes = "查询组织版本下的用户")
    @GetMapping("/org/getAllUser")
    public ApiResult<List<UserInfoEntity>> searchUser(@RequestParam Long orgVersionUuid) {
        return ApiResult.success(userInfoService.getAllUsersByOrgVersionUuid(orgVersionUuid));
    }

    @ApiOperation(value = "查询组织版本下的用户选项数据", notes = "查询组织版本下的用户选项数据")
    @GetMapping("/org/queryUserOptionsUnderOrgVersion")
    public ApiResult<Select2QueryData> queryUserOptionsUnderOrgVersion(@RequestParam Long orgVersionUuid) {
        return ApiResult.success(userInfoService.queryUserOptionsUnderOrgVersion(orgVersionUuid));
    }

    @PostMapping("/getUserNamesByIds")
    public ApiResult<Map<String, String>> getUserNames(@RequestBody List<String> userIds) {
        return ApiResult.success(userInfoService.getUserNamesByUserIds(userIds));
    }


    @ApiOperation(value = "查询所有用户", notes = "查询所有用户")
    @GetMapping("/getAllUser")
    public ApiResult<List<UserInfoEntity>> getAllUser() {
        return ApiResult.success(userInfoService.listAll());
    }

    @ApiOperation(value = "根据用户姓名或者拼音匹配搜索用户", notes = "根据用户姓名或者拼音匹配搜索用户")
    @GetMapping("/getUsersLikeUserNameAndPinyin")
    public ApiResult<List<UserInfoEntity>> getUsersLikeUserNameAndPinyin(@RequestParam(required = false) String keyword,
                                                                         @RequestParam(required = false) int pageSize) {
        return ApiResult.success(userInfoService.getUsersLikeUserNameAndPinyin(keyword, pageSize));
    }


    @ApiOperation(value = "冻结或解冻用户", notes = "冻结或解冻用户")
    @GetMapping("/lockUser")
    public ApiResult<Boolean> lockUser(@RequestParam String uuid, @RequestParam boolean locked) {
        userAccountService.lockAccountByUserUuid(uuid, locked);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "启用或禁用用户", notes = "启用或禁用用户")
    @GetMapping("/enableUser")
    public ApiResult<Boolean> enableUser(@RequestParam String uuid, @RequestParam boolean enable) {
        userAccountService.enableUserByUserUuid(uuid, enable);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "组织用户离职", notes = "组织用户离职")
    @GetMapping("/org/quit")
    public ApiResult<Boolean> quitUser(@RequestParam String uuid, @RequestParam(required = false) Long orgVersionUuid) {
        userInfoService.userQuit(uuid, orgVersionUuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "重置用户密码", notes = "重置用户密码")
    @GetMapping("/resetPassword")
    public ApiResult<Boolean> resetPassword(@RequestParam String uuid) {
        userInfoService.resetUserPassword(uuid);
        return ApiResult.success(true);
    }


    @GetMapping("updateUserWorkState")
    public ApiResult<Boolean> updateUserWorkState(@RequestParam String workState, @RequestParam String userId) {
        userInfoService.updateUserWorkState(userId, workState);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "删除用户", notes = "删除用户")
    @GetMapping("/delete")
    public ApiResult<Boolean> deleteUser(@RequestParam String uuid) {
        userInfoService.deleteUser(uuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "删除用户", notes = "删除用户")
    @PostMapping("/delete")
    public ApiResult<Boolean> deleteUser(@RequestBody List<String> uuid) {
        userInfoService.deleteUsers(uuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "删除组织用户", notes = "删除组织用户")
    @GetMapping("/deleteOrgUser")
    public ApiResult<Boolean> deleteOrgUser(@RequestParam String userId, @RequestParam Long orgVersionUuid) {
        orgUserService.deleteOrgUser(userId, orgVersionUuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "批量重置用户密码", notes = "批量重置用户密码")
    @PostMapping("/batchResetPassword")
    public ApiResult<Map<String, Boolean>> batchResetPassword(@RequestBody Map<String, Object> body) {
        List<String> uuids = (List<String>) body.get("uuids");
        Object password = body.get("password");
        Map<String, String> passwordMap = null;
        String commonPwd = null;
        if (password instanceof String) {
            commonPwd = (String) password;
        } else if (password != null && password instanceof Map) {
            passwordMap = (Map<String, String>) password; // 每个用户独立的密码
        }
        Map<String, Boolean> result = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(uuids)) {
            for (String uuid : uuids) {
                try {
                    userInfoService.resetUserPassword(uuid, passwordMap == null ? commonPwd : passwordMap.get(uuid));
                    result.put("uuid", true);
                } catch (Exception e) {
                    result.put("uuid", false);
                }
            }
        }
        return ApiResult.success(result);
    }

    @ApiOperation(value = "批量删除用户", notes = "批量删除用户")
    @PostMapping("/batchDeleteUser")
    public ApiResult<Map<String, Boolean>> batchDeleteUser(@RequestBody Map<String, List<String>> body) {
        List<String> uuids = body.get("uuids");
        Map<String, Boolean> result = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(uuids)) {
            for (String uuid : uuids) {
                try {
                    userInfoService.deleteUser(uuid);
                    result.put("uuid", true);
                } catch (Exception e) {
                    result.put("uuid", false);
                }
            }
        }
        return ApiResult.success(result);
    }

    @GetMapping("/checkPassword")
    public @ResponseBody
    ApiResult<Boolean> checkPassword(@RequestParam("password") String password) {
        try {
            return ApiResult.success(userInfoService.checkPassword(PwdUtils.decodePwdBybase64AndUnicode(password)));
        } catch (Exception ex) {
        }
        return ApiResult.success(false);
    }

    @ApiOperation(value = "修改用户密码", notes = "修改用户密码")
    @GetMapping("/modifyPassword")
    public ApiResult<Boolean> modifyPassword(@RequestParam String newPassword, @RequestParam String oldPassword) {
        try {
            userInfoService.modifyUserPassword(SpringSecurityUtils.getCurrentLoginName(), PwdUtils.decodePwdBybase64AndUnicode(newPassword),
                    PwdUtils.decodePwdBybase64AndUnicode(oldPassword));
        } catch (Exception e) {
            return ApiResult.fail(false);
        }
        return ApiResult.success(true);
    }

    @PostMapping("/saveAcctPasswordRules")
    public ApiResult<Void> saveAcctPasswordRules(@RequestBody List<UserAcctPasswordRuleEntity> ruls) {
        userAccountService.saveUserAcctPasswordRule(ruls);
        return ApiResult.success();
    }

    @GetMapping("/getAcctPasswordRules")
    public ApiResult<Map> getAcctPasswordRules() {
        UserAcctPasswordRules rules = userAccountService.getUserAcctPasswordRules();
        return ApiResult.success(rules.getRuleMap());
    }

    @GetMapping("/notifyUserAccountPasswordAreAboutToExpired")
    public ApiResult<Void> notifyUserAccountPasswordAreAboutToExpired() {
        userAccountService.notifyUserAccountPasswordAreAboutToExpired();
        return ApiResult.success();
    }
//
//    @GetMapping("/getUserBizOrgRoleTree")
//    public ApiResult<List<OrgTreeNodeDto>> getUserBizOrgRoleTree(@RequestParam(required = false) Long uuid, @RequestParam(required = false) String id) {
//        return ApiResult.success(
//
//        );
//    }
}
