package com.wellsoft.pt.multi.org.web.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.bean.UserLoginLogVo;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.org.service.UserLoginLogService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
@Api(tags = "组织用户管理接口")
@RestController
@RequestMapping("/api/org/user/")
public class MultiOrgUserController extends BaseController {

    @Autowired
    private MultiOrgUserService multiOrgUserService;
    @Autowired
    private UserLoginLogService userLoginLogService;

    @ApiOperation(value = "修改账号信息", notes = "修改账号信息", tags = {"组织用户账号分组", "组织管理--->用户"})
    @PostMapping("/modifyUser")
    @ApiOperationSupport(order = 10)
    public ApiResult<OrgUserVo> modifyUser(@RequestBody OrgUserVo vo) {
        vo.setPassword(null);
        return ApiResult.success(multiOrgUserService.modifyUser(vo));
    }

    @ApiOperation(value = "通过ID获取用户信息", notes = "通过ID获取用户信息", tags = {"组织用户账号分组", "组织管理--->用户"})
    @GetMapping("/getUserById")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 20)
    public ApiResult<OrgUserVo> getUserById(@RequestParam(name = "userId", required = false) String userId) {
        return ApiResult.success(multiOrgUserService.getUserById(userId));
    }

    @ApiOperation(value = "根据组织版本ID和组织结点ID重新计算用户的工作信息", notes = "根据组织版本ID和组织结点ID重新计算用户的工作信息", tags = {"组织用户账号分组",
            "组织管理--->用户"})
    @PostMapping("/recomputeUserWorkInfoByEleId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgVersionId", value = "组织版本ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "eleId", value = "组织结点ID", paramType = "query", dataType = "String", required = true)})
    @ApiOperationSupport(order = 30)
    public ApiResult recomputeUserWorkInfoByEleId(
            @RequestParam(name = "orgVersionId", required = true) String orgVersionId,
            @RequestParam(name = "eleId", required = true) String eleId) {
        multiOrgUserService.recomputeUserWorkInfoByEleId(orgVersionId, eleId);
        return ApiResult.success();
    }

    @ApiOperation(value = "根据来源组织版本ID和目标组织版本ID重新计算用户的工作信息", notes = "根据来源组织版本ID和目标组织版本ID重新计算用户的工作信息", tags = {
            "组织用户账号分组", "组织管理--->用户"})
    @PostMapping("/recomputeUserWorkInfoByVersions")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromVersionId", value = "来源组织版本ID", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "toVersionId", value = "目标组织版本ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 30)
    public ApiResult recomputeUserWorkInfoByVersions(
            @RequestParam(name = "fromVersionId", required = false) String fromVersionId,
            @RequestParam(name = "toVersionId", required = false) String toVersionId) {
        multiOrgUserService.recomputeUserWorkInfoByVersions(fromVersionId, toVersionId);
        return ApiResult.success();
    }

    @ApiOperation(value = "记录登录日志", notes = "记录登录日志", tags = {"组织用户账号分组", "组织管理--->用户"})
    @PostMapping("/loginLog")
    @ApiOperationSupport(order = 40)
    public ApiResult loginLog(HttpServletRequest request, @RequestBody UserLoginLogVo vo) {

        userLoginLogService.loginLog(request, vo);
        return ApiResult.success();
    }

    @ApiOperation(value = "记录注销日志", notes = "记录注销日志", tags = {"组织用户账号分组", "组织管理--->用户"})
    @PostMapping("/logoutLog")
    @ApiOperationSupport(order = 50)
    public ApiResult logoutLog(HttpServletRequest request, @RequestBody UserLoginLogVo vo) {
        userLoginLogService.logoutLog(request, vo);
        return ApiResult.success();
    }

    @GetMapping("/updateIdnumber/{userId}")
    public ApiResult updateUserIdNumber(@RequestParam(name = "idNumber", required = true) String idNumber,
                                        @PathVariable("userId") String userId) {
        multiOrgUserService.updateIdNumber(idNumber, userId);
        return ApiResult.success();
    }

}
