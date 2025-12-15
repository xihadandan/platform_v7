package com.wellsoft.pt.security.audit.web.controller;

import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.security.audit.bean.RoleBean;
import com.wellsoft.pt.security.audit.bean.RoleDto;
import com.wellsoft.pt.security.audit.dto.QueryRoleByCurrentUserUnitIdDto;
import com.wellsoft.pt.security.audit.dto.UpdateRoleMemberDto;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.security.util.SecurityConfigUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 角色维护控制器类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/10.1	    zenghw		2021/3/10		    Create
 * </pre>
 * @date 2021/3/10
 */
@Api(tags = "角色维护控制器类")
@RestController
@RequestMapping("/api/security/role")
public class RoleRestfulController extends BaseController {
    @Autowired
    private RoleService roleService;


    @Autowired
    private SecurityApiFacade securityApiFacade;


    @Autowired
    SecurityMetadataSourceService securityMetadataSourceService;

    @ApiOperation(value = "获取角色树", notes = "获取角色树")
    @GetMapping("/getRoleTree")
    @ApiOperationSupport(order = 10)
    public ApiResult<TreeNode> getRoleTree() {
        return ApiResult.success(roleService.getRoleTree());
    }

    @ApiOperation(value = "通过当前用户单位ID获取权限列表", notes = "通过当前用户单位ID获取权限列表")
    @GetMapping("/queryRoleByCurrentUserUnitId")
    @ApiOperationSupport(order = 20)
    public ApiResult<List<QueryRoleByCurrentUserUnitIdDto>> queryRoleByCurrentUserUnitId() {

        List<Role> roles = roleService.queryRoleByCurrentUserUnitId();
        List<QueryRoleByCurrentUserUnitIdDto> result = roles.stream()
                .sorted(Comparator.comparing(Role::getCode))
                .map(role -> {
                    QueryRoleByCurrentUserUnitIdDto dto = new QueryRoleByCurrentUserUnitIdDto();
                    dto.setUuid(role.getUuid());
                    dto.setId(role.getId());
                    dto.setCode(role.getCode());
                    dto.setName(role.getName());
                    return dto;
                }).collect(Collectors.toList());
        return ApiResult.success(result);
    }

    @ApiOperation(value = "发布角色更新事件", notes = "发布角色更新事件")
    @PostMapping("/publishRoleUpdatedEvent")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 30)
    public ApiResult publishRoleUpdatedEvent(@RequestParam(name = "uuid", required = false) String uuid,
                                             @RequestParam(name = "reloadAll", required = false) Boolean reloadAll) {
        if (BooleanUtils.isTrue(reloadAll)) {
            SecurityConfigUtils.publishSecurityConfigUpdatedEvent("", null);
        } else {
            roleService.publishRoleUpdatedEvent(uuid);
        }
        return ApiResult.success();
    }


    @ApiOperation(value = "根据UUID获取角色", notes = "根据UUID获取角色")
    @GetMapping("/getBean/{uuid}")
    @ApiOperationSupport(order = 40)
    public ApiResult<RoleBean> getBean(@PathVariable("uuid") String uuid) {
        RoleBean bean = roleService.getBean(uuid);
        return ApiResult.success(bean);
    }

    @ApiOperation(value = "保存角色", notes = "保存角色")
    @PostMapping("/saveBean")
    @ApiOperationSupport(order = 50)
    public ApiResult<String> saveBean(@RequestBody RoleBean bean) {
        String uuid = null;
        try {
            uuid = roleService.saveBean(bean);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (e.getLocalizedMessage().indexOf("Role cannot contain itself recursively") != -1) {
                errorMsg = "角色循环嵌套";
            }
            return ApiResult.fail(errorMsg);
        }
        return ApiResult.success(uuid);
    }

    @PostMapping("/updateRoleMember")
    public ApiResult<List<String>> updateRoleMember(@RequestBody List<UpdateRoleMemberDto> dto) {
        return ApiResult.success(roleService.updateRoleMember(dto));
    }


    @GetMapping("/checkNestedRecursively")
    public ApiResult<Boolean> checkNestedRecursively(@RequestParam String roleUuid, @RequestParam String childRoleUuid) {
        return ApiResult.success(roleService.checkNestedRecursively(roleUuid, childRoleUuid));
    }


    @GetMapping("/queryAppRoles")
    public ApiResult<List<RoleDto>> getModuleRoles(@RequestParam String appId) {
        List<RoleDto> roleDtos = roleService.getRolesByAppId(appId);
        return ApiResult.success(roleDtos);
    }


    @GetMapping("/getUserSecurityAttribute")
    public ApiResult<Set<String>> getUserSecurityAttribute(@RequestParam String object, @RequestParam String functionType) {
        Set<String> atts = Sets.newHashSet();
        Collection<ConfigAttribute> configAttributes = securityMetadataSourceService.getAttributes(object, functionType);
        if (CollectionUtils.isNotEmpty(configAttributes)) {
            for (ConfigAttribute ca : configAttributes) {
                atts.add(ca.getAttribute());
            }
        }
        return ApiResult.success(atts);
    }


    @PostMapping("/queryAppRolesByAppIds")
    public ApiResult<List<RoleDto>> queryAppRolesByAppIds(@RequestBody List<String> appId) {
        List<RoleDto> roleDtos = roleService.getRolesByAppIds(appId);
        return ApiResult.success(roleDtos);
    }

    @PostMapping("/getRolesByUuids")
    public ApiResult<List<RoleDto>> getRolesByUuids(@RequestBody List<String> roleUuids) {
        List<RoleDto> roleDtos = roleService.getRolesByUuids(roleUuids);
        return ApiResult.success(roleDtos);
    }

    @ApiOperation(value = "根据UUID，批量删除角色", notes = "根据UUID，批量删除角色")
    @PostMapping("/removeAll")
    @ApiOperationSupport(order = 60)
    public ApiResult removeAll(@RequestBody Collection<String> uuids) {
        roleService.removeAll(uuids);
        return ApiResult.success();
    }


    @ApiOperation(value = "加载角色权限树，选择已选角色权限", notes = "加载角色权限树，选择已选角色权限")
    @GetMapping("/getRolePrivilegeTree")
    @ApiOperationSupport(order = 70)
    public ApiResult<TreeNode> getRolePrivilegeTree(@RequestParam String uuid, @RequestParam String appId) {
        TreeNode privilegeTree = roleService.getRolePrivilegeTree(uuid, appId);
        return ApiResult.success(privilegeTree);
    }

    @GetMapping("/getRoleMembers")
    @ApiOperationSupport(order = 70)
    public ApiResult<RoleDto> getRoleMembers(@RequestParam String uuid) {
        RoleDto nodes = roleService.getRoleMembers(uuid);
        return ApiResult.success(nodes);
    }

    @GetMapping("/getRoleMembersById")
    @ApiOperationSupport(order = 70)
    public ApiResult<RoleDto> getRoleMembersById(@RequestParam String roleId) {
        RoleDto nodes = roleService.getRoleMembersById(roleId);
        return ApiResult.success(nodes);
    }

    @GetMapping("/getRoleNestedRoles")
    public ApiResult<List<RoleDto>> getRoleNestedRoles(@RequestParam String uuid) {
        List<RoleDto> dtos = roleService.getRoleNestedRoles(uuid);
        return ApiResult.success(dtos);
    }

    @GetMapping("/getRolesByNestedRole")
    public ApiResult<List<RoleDto>> getRolesByNestedRole(@RequestParam String uuid) {
        List<RoleDto> dtos = roleService.getRolesByNestedRole(uuid);
        return ApiResult.success(dtos);
    }

    @ApiOperation(value = "根据UUID加载权限树, 包含角色嵌套及权限", notes = "根据UUID加载权限树, 包含角色嵌套及权限")
    @GetMapping("/queryPrivilegeResultAsTree/{uuid}")
    @ApiOperationSupport(order = 80)
    public ApiResult<TreeNode> queryPrivilegeResultAsTree(@PathVariable String uuid) {
        TreeNode resultAsTree = roleService.queryPrivilegeResultAsTree(uuid);
        return ApiResult.success(resultAsTree);
    }

    @GetMapping("/loadConfigAttribute")
    public ApiResult loadConfigAttribute() {
        return ApiResult.success(SpringSecurityUtils.getCurrentUser().getAuthorities());
    }


    @GetMapping("/loadSecurityMetadataSource")
    public ApiResult<Boolean> loadSecurityMetadataSource(@RequestParam(required = false) String configUuid, @RequestParam(required = false) String configType) {
        securityMetadataSourceService.loadSecurityMetadataSource();
        return ApiResult.success(true);
    }

    @GetMapping("/clearSecurityCache")
    public ApiResult<Boolean> clearSecurityCache() {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        cacheManager.getCache(ModuleID.SECURITY).clear();
        return ApiResult.success(true);
    }

    @GetMapping("/getResources")
    public @ResponseBody
    ApiResult<List<TreeNode>> getResources(@RequestParam String uuid) {
        return ApiResult.success(roleService.getRoleResourceTreeByUuid(uuid));
    }


    @ApiOperation(value = "根据UUID加载权限、资源树, 包含角色嵌套、权限以及资源", notes = "根据UUID加载权限、资源树, 包含角色嵌套、权限以及资源")
    @GetMapping("/getRolePrivilegeResourceTreeByUuid/{uuid}")
    @ApiOperationSupport(order = 80)
    public ApiResult<List<TreeNode>> getRolePrivilegeResourceTreeByUuid(@PathVariable String uuid) {
        List<TreeNode> resultAsTree = roleService.getRolePrivilegeResourceTreeByUuid(uuid);
        return ApiResult.success(resultAsTree);
    }

    @GetMapping("/getRolesInTenantSystem")
    public ApiResult<List<RoleDto>> getRolesInTenantSystem(@RequestParam String system, @RequestParam(required = false) String tenant) {
        List<RoleDto> dtos = roleService.getRolesInTenantSystem(system, StringUtils.defaultIfBlank(tenant,
                StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId())
        ));
        return ApiResult.success(dtos);
    }
}
