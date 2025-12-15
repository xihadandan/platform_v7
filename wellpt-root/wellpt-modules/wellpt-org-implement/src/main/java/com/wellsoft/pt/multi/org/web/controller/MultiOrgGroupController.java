package com.wellsoft.pt.multi.org.web.controller;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.bean.OrgGroupVo;
import com.wellsoft.pt.multi.org.dto.DeleteGroupsDto;
import com.wellsoft.pt.multi.org.dto.GetByIdsDto;
import com.wellsoft.pt.multi.org.dto.GetRoleIdByGroupMemberDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupMember;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupRole;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgGroupFacade;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * Description: 组织群组管理接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/3.1	    zenghw		2021/3/3		    Create
 * </pre>
 * @date 2021/3/3
 */
@Api(tags = "组织群组管理接口")
@RestController
@RequestMapping("/api/org/group")
public class MultiOrgGroupController extends BaseController {

    @Autowired
    private MultiOrgGroupFacade multiOrgGroupFacade;

    @ApiOperation(value = "通过UUID 获取群组基本信息", notes = "通过UUID 获取群组基本信息", tags = {"群组分组", "组织管理--->群组"})
    @GetMapping("/getGroupByUuid")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupUuid", value = "群组uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 10)
    public ApiResult<MultiOrgGroup> getGroupByUuid(
            @RequestParam(name = "groupUuid", required = false) String groupUuid) {
        return ApiResult.success(multiOrgGroupFacade.getGroupByUuid(groupUuid));
    }

    @ApiOperation(value = "通过ID 获取群组基本信息", notes = "通过ID 获取群组基本信息", tags = {"群组分组", "组织管理--->群组"})
    @GetMapping("/getGroupById")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "群组ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 20)
    public ApiResult<MultiOrgGroup> getGroupById(@RequestParam(name = "groupId", required = false) String groupId) {
        return ApiResult.success(multiOrgGroupFacade.getGroupById(groupId));
    }

    @ApiOperation(value = "通过ID, 删除群组", notes = "通过ID, 删除群组", tags = {"群组分组", "组织管理--->群组"})
    @PostMapping("/deleteGroup")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "群组ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 30)
    public ApiResult<Boolean> deleteGroup(@RequestParam(name = "groupUuid", required = false) String groupId) {
        Boolean flg = multiOrgGroupFacade.deleteGroup(groupId);
        if (flg) {
            return ApiResult.success();
        }
        return ApiResult.fail();
    }

    @ApiOperation(value = "获取群组的所有成员", notes = "获取群组的所有成员", tags = {"群组分组", "组织管理--->群组"})
    @GetMapping("/queryGroupListByMemberId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "成员ID，（用户ID，部门ID，单位ID等）", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 40)
    public ApiResult<List<MultiOrgGroupMember>> queryGroupListByMemberId(
            @RequestParam(name = "memberId", required = false) String memberId) {
        List<MultiOrgGroupMember> multiOrgElements = multiOrgGroupFacade.queryGroupListByMemberId(memberId);
        return ApiResult.success(multiOrgElements);
    }

    @ApiOperation(value = "获取所有的群组，以树形方式展示", notes = "获取所有的群组，以树形方式展示", tags = {"群组分组", "组织管理--->群组"})
    @GetMapping("/queryGroupListAsTreeByType")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "群组类型 1：个人群组 0：公共群组", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 50)
    public ApiResult<List<TreeNode>> queryGroupListAsTreeByType(
            @RequestParam(name = "type", required = false) int type) {
        List<TreeNode> multiOrgElements = multiOrgGroupFacade.queryGroupListAsTreeByType(type);
        return ApiResult.success(multiOrgElements);
    }

    @ApiOperation(value = "获取群组对应的权限树", notes = "获取群组对应的权限树", tags = {"群组分组", "组织管理--->群组"})
    @GetMapping("/getGroupPrivilegeResultTree")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "群组uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 60)
    public ApiResult<TreeNode> getGroupPrivilegeResultTree(@RequestParam(name = "uuid", required = false) String uuid) {
        return ApiResult.success(multiOrgGroupFacade.getGroupPrivilegeResultTree(uuid));
    }

    @ApiOperation(value = "处理角色删除事宜", notes = "处理角色删除事宜-删除指定角色对应的用户")
    @PostMapping("/dealRoleRemoveEvent")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleUuid", value = "角色uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 70)
    public ApiResult<Boolean> dealRoleRemoveEvent(@RequestParam(name = "roleUuid", required = false) String roleUuid) {
        Boolean flg = multiOrgGroupFacade.dealRoleRemoveEvent(roleUuid);
        if (flg) {
            return ApiResult.success();
        }
        return ApiResult.fail();

    }

    @ApiOperation(value = "通过角色获取拥有该角色的所有群组", notes = "通过角色获取拥有该角色的所有群组", tags = {"群组分组", "组织管理--->群组"})
    @GetMapping("/queryGroupListByRole")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleUuid", value = "角色uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 80)
    public ApiResult<List<MultiOrgGroup>> queryGroupListByRole(
            @RequestParam(name = "roleUuid", required = false) String roleUuid) {
        List<MultiOrgGroup> multiOrgElements = multiOrgGroupFacade.queryGroupListByRole(roleUuid);
        return ApiResult.success(multiOrgElements);
    }

    @ApiOperation(value = "获取一个群组所拥有的所有角色", notes = "获取一个群组所拥有的所有角色", tags = {"群组分组", "组织管理--->群组"})
    @GetMapping("/queryRoleListOfGroup")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "群组ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 90)
    public ApiResult<List<MultiOrgGroupRole>> queryRoleListOfGroup(
            @RequestParam(name = "groupId", required = false) String groupId) {
        List<MultiOrgGroupRole> multiOrgElements = multiOrgGroupFacade.queryRoleListOfGroup(groupId);
        return ApiResult.success(multiOrgElements);
    }

    @ApiOperation(value = "处理组织版本升级事宜", notes = "处理组织版本升级事宜", tags = {"群组分组", "组织管理--->群组"})
    @PostMapping("/dealOrgUpgradeEvent")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldOrgVersionId", value = "旧的组织版本ID", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "newOrgVersionId", value = "新的组织版本ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 100)
    public ApiResult<Boolean> dealOrgUpgradeEvent(
            @RequestParam(name = "oldOrgVersionId", required = false) String oldOrgVersionId,
            @RequestParam(name = "newOrgVersionId", required = false) String newOrgVersionId) {
        Boolean flg = multiOrgGroupFacade.dealOrgUpgradeEvent(oldOrgVersionId, newOrgVersionId);
        if (flg) {
            return ApiResult.success();
        }
        return ApiResult.fail();
    }

    @ApiOperation(value = "通过群组ID，批量获取群组基本信息", notes = "通过群组ID，批量获取群组基本信息", tags = {"群组分组", "组织管理--->群组"})
    @PostMapping("/getByIds")
    @ApiOperationSupport(order = 110)
    public ApiResult<List<MultiOrgGroup>> getByIds(@RequestBody GetByIdsDto getByIdsDto) {
        List<MultiOrgGroup> multiOrgElements = multiOrgGroupFacade.getByIds(getByIdsDto.getGroupIds());
        return ApiResult.success(multiOrgElements);
    }

    @ApiOperation(value = "通过ID 获取群组完整信息", notes = "通过ID 获取群组完整信息", tags = {"群组分组", "组织管理--->群组"})
    @GetMapping("/getGroupVoById")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "群组ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 120)
    public ApiResult<OrgGroupVo> getGroupVoById(@RequestParam(name = "groupId", required = false) String groupId) {
        return ApiResult.success(multiOrgGroupFacade.getGroupVoById(groupId));
    }

    @ApiOperation(value = "通过UUID 获取群组完整信息", notes = "通过UUID 获取群组完整信息", tags = {"群组分组", "组织管理--->群组"})
    @GetMapping("/getGroupVo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "群组uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 130)
    public ApiResult<OrgGroupVo> getGroupVo(@RequestParam(name = "uuid", required = false) String uuid) {
        return ApiResult.success(multiOrgGroupFacade.getGroupVo(uuid));
    }

    @ApiOperation(value = "添加群组", notes = "添加群组", tags = {"群组分组", "组织管理--->群组"})
    @PostMapping("/addGroup")
    @ApiOperationSupport(order = 140)
    public ApiResult<OrgGroupVo> addGroup(@RequestBody OrgGroupVo vo) {
        return ApiResult.success(multiOrgGroupFacade.addGroup(vo));
    }

    @ApiOperation(value = "给群组添加一个角色", notes = "给群组添加一个角色", tags = {"群组分组", "组织管理--->群组"})
    @PostMapping("/addRoleListOfGroup")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "群组id", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "roleUuid", value = "roleUuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 150)
    public ApiResult addRoleListOfGroup(@RequestParam(name = "id", required = false) String id,
                                        @RequestParam(name = "roleUuid", required = false) String roleUuid) {
        multiOrgGroupFacade.addRoleListOfGroup(id, roleUuid);
        return ApiResult.success();
    }

    @ApiOperation(value = "批量删除群组", notes = "批量删除群组", tags = {"群组分组", "组织管理--->群组"})
    @PostMapping("/deleteGroups")
    @ApiOperationSupport(order = 160)
    public ApiResult deleteGroups(@RequestBody DeleteGroupsDto deleteGroupsDto) {
        multiOrgGroupFacade.deleteGroups(deleteGroupsDto.getGroupIds());
        return ApiResult.success();
    }

    @ApiOperation(value = "根据memberObjId查询角色Id", notes = "根据memberObjId查询角色Id", tags = {"群组分组", "组织管理--->群组"})
    @GetMapping("/getRoleIdByGroupMember")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberObjIdSet", value = "memberObjId集合", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 170)
    public ApiResult<Set<String>> getRoleIdByGroupMember(
            @RequestBody GetRoleIdByGroupMemberDto getRoleIdByGroupMemberDto) {
        return ApiResult
                .success(multiOrgGroupFacade.getRoleIdByGroupMember(getRoleIdByGroupMemberDto.getMemberObjIdSet()));
    }

    @ApiOperation(value = "修改群组", notes = "修改群组", tags = {"群组分组", "组织管理--->群组"})
    @PostMapping("/modifyGroup")
    @ApiOperationSupport(order = 180)
    public ApiResult<OrgGroupVo> modifyGroup(@RequestBody OrgGroupVo vo) {
        return ApiResult.success(multiOrgGroupFacade.modifyGroup(vo));
    }
}
