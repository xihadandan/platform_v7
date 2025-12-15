/*
 * @(#)11/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.web.api;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.dms.entity.DmsRoleModelEntity;
import com.wellsoft.pt.dms.service.DmsRoleModelService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 11/25/25.1	    zhulh		11/25/25		    Create
 * </pre>
 * @date 11/25/25
 */
@Api(tags = "权限模型接口")
@RestController
@RequestMapping("/api/dms/role/model")
public class ApiDmsRoleModelController extends BaseController {

    @Autowired
    private DmsRoleModelService roleModelService;

    /**
     * 保存权限模型
     *
     * @param roleModelEntity
     * @return
     */
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "保存权限模型", notes = "保存权限模型")
    public ApiResult<Long> save(@RequestBody DmsRoleModelEntity roleModelEntity) {
        if (roleModelEntity.getUuid() == null) {
            roleModelEntity.setSystem(RequestSystemContextPathResolver.system());
        }
        roleModelService.save(roleModelEntity);
        return ApiResult.success(roleModelEntity.getUuid());
    }

    /**
     * 获取权限模型
     *
     * @param uuid
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取权限模型", notes = "获取权限模型")
    public ApiResult<DmsRoleModelEntity> get(@RequestParam(name = "uuid") Long uuid) {
        return ApiResult.success(roleModelService.getOne(uuid));
    }

    /**
     * 获取权限模型
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "获取权限模型", notes = "获取权限模型")
    public ApiResult<List<DmsRoleModelEntity>> list() {
        return ApiResult.success(roleModelService.listBySystem(RequestSystemContextPathResolver.system()));
    }

    /**
     * 删除权限模型
     *
     * @param uuids
     * @return
     */
    @PostMapping(value = "/deleteAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "删除权限模型", notes = "删除权限模型")
    public ApiResult<Void> deleteAll(@RequestParam(name = "uuids") List<Long> uuids) {
        roleModelService.deleteByUuids(uuids);
        return ApiResult.success();
    }

}
