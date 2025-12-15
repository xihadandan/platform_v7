/*
 * @(#)11/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.web.api;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.dms.bean.DmsRoleBean;
import com.wellsoft.pt.dms.entity.DmsObjectAssignRoleEntity;
import com.wellsoft.pt.dms.entity.DmsRoleEntity;
import com.wellsoft.pt.dms.entity.DmsRoleModelEntity;
import com.wellsoft.pt.dms.facade.service.DmsRoleMgr;
import com.wellsoft.pt.dms.service.DmsObjectAssignRoleService;
import com.wellsoft.pt.dms.service.DmsRoleModelService;
import com.wellsoft.pt.dms.service.DmsRoleService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 11/21/25.1	    zhulh		11/21/25		    Create
 * </pre>
 * @date 11/21/25
 */
@Api(tags = "权限组定义接口")
@RestController
@RequestMapping("/api/dms/role")
public class ApiDmsRoleController extends BaseController {

    @Autowired
    private DmsRoleService roleService;

    @Autowired
    private DmsRoleMgr roleMgr;

    @Autowired
    private DmsRoleModelService roleModelService;

    @Resource
    private DmsObjectAssignRoleService objectAssignRoleService;

    /**
     * 保存角色定义
     *
     * @param roleBean
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存角色定义", notes = "保存角色定义")
    public ApiResult<String> save(@RequestBody DmsRoleBean roleBean) {
//        if (roleEntity.getUuid() == null) {
//            roleEntity.setSystem(RequestSystemContextPathResolver.system());
//            roleEntity.setTenant(SpringSecurityUtils.getCurrentTenantId());
//        }
        return ApiResult.success(roleMgr.saveBean(roleBean));
    }

    /**
     * 获取角色定义
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取角色定义", notes = "获取角色定义")
    public ApiResult<DmsRoleBean> get(@RequestParam(name = "uuid") String uuid) {
        return ApiResult.success(roleMgr.getBean(uuid));
    }

    /**
     * 获取角色定义
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取角色定义", notes = "获取角色定义")
    public ApiResult<List<DmsRoleEntity>> list() {
        return ApiResult.success(roleService.listBySystem(RequestSystemContextPathResolver.system()));
    }

    /**
     * 删除角色定义
     *
     * @param uuids
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除角色定义", notes = "删除角色定义")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids") List<String> uuids) {
        List<DmsRoleModelEntity> modelEntities = roleModelService.listByRoleUuids(uuids);
        if (CollectionUtils.isNotEmpty(modelEntities)) {
            return ApiResult.fail(String.format("权限组已被权限模型[%s]使用，不能删除！", modelEntities.stream().map(DmsRoleModelEntity::getName).collect(Collectors.joining("、"))));
        }
        List<DmsObjectAssignRoleEntity> objectAssignRoleEntities = objectAssignRoleService.listByRoleUuids(uuids);
        if (CollectionUtils.isNotEmpty(objectAssignRoleEntities)) {
            return ApiResult.fail("权限组已被文件库，不能删除！");
        }

        roleService.deleteByUuids(uuids);
        return ApiResult.success();
    }

    /**
     * 初始化文件库权限模型
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/init", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "初始化文件库权限模型", notes = "初始化文件库权限模型")
    public ApiResult<Void> init(@RequestParam(name = "system", required = true) String system,
                                @RequestParam(name = "tenant", required = true) String tenant) {
        roleService.initBySystemAndTenant(system, tenant);
        roleModelService.initBySystemAndTenant(system, tenant);
        return ApiResult.success();
    }

    /**
     * 获取初始化文件库权限模型
     *
     * @param id
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getInitRole", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取初始化文件库权限模型", notes = "获取初始化文件库权限模型")
    public ApiResult<DmsRoleEntity> getInitRoleDefinition(@RequestParam(name = "id", required = true) String id) {
        return ApiResult.success(roleService.getInitById(id, RequestSystemContextPathResolver.system(), SpringSecurityUtils.getCurrentTenantId()));
    }

}
