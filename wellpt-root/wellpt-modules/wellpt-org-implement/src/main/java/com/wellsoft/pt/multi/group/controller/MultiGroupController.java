package com.wellsoft.pt.multi.group.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.group.service.MultiGroupService;
import com.wellsoft.pt.multi.group.vo.MultiGroupVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 集团管理
 */
@Api(tags = "集团管理")
@RestController
@RequestMapping("/api/multi/group")
public class MultiGroupController extends BaseController {


    @Autowired
    private MultiGroupService multiGroupService;


    @ApiOperation(value = "查询集团数据", notes = "查询集团数据")
    @ApiImplicitParams({@ApiImplicitParam(name = "uuid", value = "uuid", paramType = "query", dataType = "String", required = true)})
    @GetMapping("/get")
    public ApiResult<MultiGroupVo> get(@RequestParam(name = "uuid", required = true) String uuid) {
        MultiGroupVo multiGroupVo = multiGroupService.get(uuid);
        return ApiResult.success(multiGroupVo);
    }


    @ApiOperation(value = "添加或修改集团数据", notes = "添加或修改集团数据")
    @PostMapping("/addOrUpdate")
    public ApiResult<String> addOrUpdate(
            @ApiParam(value = "集团", required = true) @RequestBody @Validated MultiGroupVo multiGroupVo) {
        multiGroupService.addOrUpdate(multiGroupVo);
        return ApiResult.success();
    }


    @ApiOperation(value = "删除集团数据", notes = "删除集团数据")
    @ApiImplicitParams({@ApiImplicitParam(name = "uuids", value = "主键uuid集合，逗号分隔", paramType = "query", dataType = "String", required = true)})
    @PostMapping("/del")
    public ApiResult del(@RequestParam(name = "uuids", required = true) List<String> uuids) {
        multiGroupService.del(uuids);
        return ApiResult.success();
    }

    @ApiOperation(value = "启用禁用", notes = "启用禁用")
    @ApiImplicitParams({@ApiImplicitParam(name = "uuid", value = "uuid", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "isEnable", value = "启用(0:否1:是)", paramType = "query", dataType = "Integer", required = true, example = "0")})
    @PostMapping("/isEnable")
    public ApiResult isEnable(@RequestParam(name = "uuid", required = true) String uuid,
                              @RequestParam(name = "isEnable", required = true) Integer isEnable) {
        multiGroupService.isEnable(uuid, isEnable);
        return ApiResult.success();
    }


}
