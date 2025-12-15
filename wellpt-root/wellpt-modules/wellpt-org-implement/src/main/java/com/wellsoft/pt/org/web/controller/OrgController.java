package com.wellsoft.pt.org.web.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.bean.OrgUserDto;
import com.wellsoft.pt.multi.org.bean.UserRoleInfoDto;
import com.wellsoft.pt.multi.org.dto.GetCurrentUnitUserListByUserNameKeyDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.dto.GetNameByOrgEleIdsDto;
import com.wellsoft.pt.org.dto.QueryAllUserRoleInfoDtoListByUserDto;
import com.wellsoft.pt.org.dto.QueryAllUserRoleInfoDtoListDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 组织机构对外统一提供的接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/2.1	    zenghw		2021/3/2		    Create
 * </pre>
 * @date 2021/3/2
 */
@Api(tags = "组织机构对外统一提供的接口")
@RestController
@RequestMapping("/api/org/facade")
public class OrgController extends BaseController {

    @Autowired
    private OrgApiFacade orgApiFacade;

    @ApiOperation(value = "批量通过ID获取对应的组织元素", notes = "批量通过ID获取对应的组织元素")
    @GetMapping("/queryOrgElementListByIds")
    @ApiOperationSupport(order = 10)
    public ApiResult<List<MultiOrgElement>> queryOrgElementListByIds(@RequestBody Collection<String> eleIds) {
        List<MultiOrgElement> multiOrgElements = orgApiFacade.queryOrgElementListByIds(eleIds);
        return ApiResult.success(multiOrgElements);
    }

    @ApiOperation(value = "获取某个组织节点下具有某个角色的所有人员接口", notes = "获取某个组织节点下具有某个角色的所有人员接口")
    @GetMapping("/getOrgUserDtosByEleIdRid")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "eleId", value = "组织节点", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "roleId", value = "角色id", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 20)
    public ApiResult<List<OrgUserDto>> getOrgUserDtosByEleIdRid(
            @RequestParam(name = "eleId", required = false) String eleId,
            @RequestParam(name = "roleId", required = false) String roleId) {
        List<OrgUserDto> multiOrgElements = orgApiFacade.getOrgUserDtosByEleIdRid(eleId, roleId);
        return ApiResult.success(multiOrgElements);
    }

    @ApiOperation(value = "通过ID获取对应的组织元素，用户，职务的名称", notes = "通过ID获取对应的组织元素，用户，职务的名称")
    @GetMapping("/getNameByOrgEleId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "eleId", value = "eleId", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 30)
    public ApiResult<String> getNameByOrgEleId(@RequestParam(name = "eleId", required = false) String eleId) {
        return ApiResult.success(orgApiFacade.getNameByOrgEleId(eleId));
    }

    @ApiOperation(value = "通过orgId 批量获取对应的组织名称", notes = "通过orgId 批量获取对应的组织名称")
    @PostMapping("/getNameByOrgEleIds")
    @ApiImplicitParams({@ApiImplicitParam(name = "orgIds", value = "组织ID集合", required = false)})
    @ApiOperationSupport(order = 40)
    public ApiResult<HashMap<String, String>> getNameByOrgEleIds(
            @RequestBody GetNameByOrgEleIdsDto getNameByOrgEleIdsDto) {
        HashMap<String, String> list = orgApiFacade.getNameByOrgEleIds(getNameByOrgEleIdsDto.getOrgIds());
        return ApiResult.success(list);
    }

    @ApiOperation(value = "获取指定用户的所有权限数据列表", notes = "获取指定用户的所有权限数据列表")
    @GetMapping("/queryAllUserRoleInfoDtoListByUserId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 50)
    public ApiResult<List<UserRoleInfoDto>> queryAllUserRoleInfoDtoList(
            @RequestParam(name = "userId", required = false) String userId) {
        List<UserRoleInfoDto> list = orgApiFacade.queryAllUserRoleInfoDtoList(userId);
        return ApiResult.success(list);
    }

    @ApiOperation(value = "获取指定用户和指定角色的所有权限数据列表", notes = "获取指定用户和指定角色的所有权限数据列表")
    @PostMapping("/queryAllUserRoleInfoDtoListByUserIdAndRoleUuids")
    @ApiOperationSupport(order = 60)
    public ApiResult<List<UserRoleInfoDto>> queryAllUserRoleInfoDtoList(
            @RequestBody QueryAllUserRoleInfoDtoListDto vo) {
        List<UserRoleInfoDto> list = orgApiFacade.queryAllUserRoleInfoDtoList(vo.getUserId(), vo.getRoleUuids());
        return ApiResult.success(list);
    }

    @ApiOperation(value = "获取指定组织用户对象和指定角色的所有权限数据列表", notes = "获取指定组织用户对象和指定角色的所有权限数据列表")
    @PostMapping("/queryAllUserRoleInfoDtoListByUserAndRoleUuids")
    @ApiOperationSupport(order = 70)
    public ApiResult<List<UserRoleInfoDto>> queryAllUserRoleInfoDtoList(
            @RequestBody QueryAllUserRoleInfoDtoListByUserDto vo) {
        List<UserRoleInfoDto> list = orgApiFacade.queryAllUserRoleInfoDtoList(vo.getUser(), vo.getRoleUuids());
        return ApiResult.success(list);
    }

    @ApiOperation(value = "获取当前用户的扩展属性", notes = "获取当前用户的扩展属性")
    @GetMapping("/getCurrentUserProperty")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "propName", value = "扩展属性名", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 80)
    public ApiResult<String> getCurrentUserProperty(
            @RequestParam(name = "propName", required = false) String propName) {
        return ApiResult.success(orgApiFacade.getCurrentUserProperty(propName));
    }

    @ApiOperation(value = "获取当前用户的扩展属性值", notes = "获取当前用户的扩展属性值")
    @GetMapping("/getUserPropertyByUserId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "propName", value = "扩展属性名", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 90)
    public ApiResult<String> getCurrentUserProperty(@RequestParam(name = "userId", required = false) String userId,
                                                    @RequestParam(name = "propName", required = false) String propName) {
        return ApiResult.success(orgApiFacade.getCurrentUserProperty(userId, propName));
    }

    @ApiOperation(value = "通过用户名称模糊匹配获取当前用户单位的用户列表", notes = "通过用户名称模糊匹配获取当前用户单位的用户列表")
    @GetMapping("/getCurrentUnitUserListByUserNameKey")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userNameKey", value = "userNameKey", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 100)
    public ApiResult<List<GetCurrentUnitUserListByUserNameKeyDto>> getCurrentUnitUserListByUserNameKey(
            @RequestParam(name = "userNameKey", required = false) String userNameKey) {
        return ApiResult.success(orgApiFacade.getCurrentUnitUserListByUserNameKey(userNameKey));
    }

}
