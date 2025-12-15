package com.wellsoft.pt.security.audit.web.controller;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.security.audit.bean.PrivilegeBean;
import com.wellsoft.pt.security.audit.bean.PrivilegeDto;
import com.wellsoft.pt.security.audit.bean.ResourceDto;
import com.wellsoft.pt.security.audit.dto.UpdatePrivilegeDto;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import com.wellsoft.pt.security.audit.service.PrivilegeResourceService;
import com.wellsoft.pt.security.audit.service.PrivilegeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * Description:权限维护控制
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/9/29   Create
 * </pre>
 */
@Api(tags = "权限维护控制")
@RestController
@RequestMapping("/api/security/privilege")
public class PrivilegeRestfulController extends BaseController {

    @Autowired
    private PrivilegeFacadeService privilegeFacadeService;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private PrivilegeResourceService privilegeResourceService;

    @ApiOperation(value = "根据权限UUID获取相应的其他权限树", notes = "根据权限UUID获取相应的其他权限树")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "权限UUID", paramType = "query", dataType = "String", required = true)})
    @GetMapping("/getOtherResourceTree/{uuid}")
    public ApiResult<List<TreeNode>> getOtherResourceTree(@PathVariable String uuid) {
        List<TreeNode> resourceTree = privilegeFacadeService.getOtherResourceTree(uuid);
        return ApiResult.success(resourceTree);
    }

    @ApiOperation(value = "根据UUID获取权限", notes = "根据UUID获取权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "权限UUID", paramType = "query", dataType = "String", required = true)})
    @GetMapping("/getPrivilegeBean/{uuid}")
    public ApiResult<PrivilegeBean> getPrivilegeBean(@PathVariable("uuid") String uuid) {
        PrivilegeBean privilegeBean = privilegeFacadeService.getPrivilegeBean(uuid);
        return ApiResult.success(privilegeBean);
    }

    @ApiOperation(value = "根据code获取权限", notes = "根据code获取权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "权限code", paramType = "query", dataType = "String", required = true)})
    @GetMapping("/getPrivilegeBeanByCode/{code}")
    public ApiResult<PrivilegeBean> getPrivilegeBeanByCode(@PathVariable("code") String code) {
        PrivilegeBean privilegeBean = privilegeService.getPrivilegeBeanByCode(code);
        return ApiResult.success(privilegeBean);
    }

    @ApiOperation(value = "保存权限", notes = "保存权限")
    @PostMapping("/saveBean")
    public ApiResult saveBean(@RequestBody PrivilegeBean bean) {
        String uuid = privilegeFacadeService.saveBean(bean);
        return ApiResult.success(uuid);
    }

    @ApiOperation(value = "保存权限关联的资源", notes = "保存权限关联的资源")
    @PostMapping("/savePrivilegeResource")
    public ApiResult<String> savePrivilegeResource(@RequestBody PrivilegeBean bean) {
        String uuid = privilegeService.savePrivilegeResource(bean);
        return ApiResult.success(uuid);
    }

    @ApiOperation(value = "保存权限关联的角色", notes = "保存权限关联的角色")
    @PostMapping("/savePrivilegeRoles/{uuid}")
    public ApiResult savePrivilegeRoles(@PathVariable String uuid, @RequestBody List<String> roleUuids) {
        privilegeService.savePrivilegeRoles(uuid, roleUuids);
        return ApiResult.success(true);
    }

    @GetMapping("/getPrivilegeOtherResource/{uuid}")
    public ApiResult<List<PrivilegeResource>> getPrivilegeOtherResource(@PathVariable String uuid) {
        return ApiResult.success(privilegeResourceService.getByPrivilegeUuid(uuid));
    }

    @GetMapping("/getPrivilegeResource/{uuid}")
    public ApiResult<List<ResourceDto>> getPrivilegeResource(@PathVariable String uuid) {
        return ApiResult.success(privilegeService.getPrivilegeResourceByPrivilegeUuid(uuid));
    }


    @GetMapping("/getPrivilegeOtherResourceUuids/{uuid}")
    public ApiResult<List<String>> getPrivilegeOtherResourceUuids(@PathVariable String uuid) {
        return ApiResult.success(privilegeResourceService.listResourceUuidsByPrivilegeUuid(uuid));
    }

    @ApiOperation(value = "发布权限更新事件", notes = "发布权限更新事件")
    @GetMapping("/publishPrivilegeUpdatedEvent/{uuid}")
    public ApiResult publishPrivilegeUpdatedEvent(@PathVariable("uuid") String uuid) {
        privilegeFacadeService.publishPrivilegeUpdatedEvent(uuid);
        return ApiResult.success();
    }

    @ApiOperation(value = "根据UUID，批量删除权限", notes = "根据UUID，批量删除权限")
    @PostMapping("/removeAll")
    public ApiResult removeAll(@RequestBody Collection<String> uuids) {
        privilegeFacadeService.removeAll(uuids);
        return ApiResult.success();
    }

    @GetMapping("/queryAppRolePrivileges")
    public ApiResult<List<PrivilegeDto>> queryAppRolePrivileges(@RequestParam String appId) {
        List<PrivilegeDto> privilegeDtos = privilegeService.queryAppRolePrivileges(appId);
        return ApiResult.success(privilegeDtos);
    }

    @GetMapping("/getPrivilegeWithRoleDetails")
    public ApiResult<PrivilegeDto> getPrivilegeWithRoleDetails(@RequestParam String uuid) {
        PrivilegeDto privilegeDto = privilegeService.getPrivilegeWithRoleDetails(uuid);
        return ApiResult.success(privilegeDto);
    }

    @GetMapping("/getResources")
    public @ResponseBody
    ApiResult<List<TreeNode>> getResources(@RequestParam String uuid) {
        return ApiResult.success(privilegeService.getPrivilegeOtherResourceTreeNode(uuid));
    }

    @PostMapping("/updatePrivilegeRoleResource")
    public ApiResult<List<String>> updatePrivilegeRoleResource(@RequestBody List<UpdatePrivilegeDto> dto) {
        return ApiResult.success(privilegeService.updatePrivilege(dto));
    }


    @GetMapping("/getPrivilegesInTenantSystem")
    public ApiResult<List<PrivilegeDto>> getPrivilegesInTenantSystem(@RequestParam String system, @RequestParam(required = false) String tenant) {
        List<PrivilegeDto> dtos = privilegeService.getPrivilegeInTenantSystem(system, StringUtils.defaultIfBlank(tenant,
                StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId())
        ));
        return ApiResult.success(dtos);
    }
}
