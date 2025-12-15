package com.wellsoft.pt.multi.org.web.controller;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.bean.OrgTreeNode;
import com.wellsoft.pt.multi.org.bean.OrgVersionVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgVersionFacade;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 组织版本管理接口
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
@Api(tags = "组织版本管理接口")
@RestController
@RequestMapping("/api/org/version/")
public class MultiOrgVersionController extends BaseController {

    @Autowired
    private MultiOrgVersionFacade multiOrgVersionFacade;

    @ApiOperation(value = "添加一个新版本组织", notes = "添加一个新版本组织", tags = {"组织管理-->组织版本"})
    @PostMapping("/addMultiOrgVersion")
    @ApiOperationSupport(order = 10)
    public ApiResult<OrgVersionVo> addMultiOrgVersion(@RequestBody OrgVersionVo vo) {
        return ApiResult.success(multiOrgVersionFacade.addMultiOrgVersion(vo));
    }

    @ApiOperation(value = "修改组织版本基本信息", notes = "修改组织版本基本信息", tags = {"组织管理-->组织版本"})
    @PostMapping("/modifyOrgVersionBaseInfo")
    @ApiOperationSupport(order = 20)
    public ApiResult<OrgVersionVo> modifyOrgVersionBaseInfo(@RequestBody OrgVersionVo vo) {
        return ApiResult.success(multiOrgVersionFacade.modifyOrgVersionBaseInfo(vo));
    }

    @ApiOperation(value = "获取一个组织版本信息", notes = "获取一个组织版本信息", tags = {"组织管理-->组织版本"})
    @GetMapping("/getOrgVersionVo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgVersionUuid", value = "组织版本uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 30)
    public ApiResult<OrgVersionVo> getOrgVersionVo(
            @RequestParam(name = "orgVersionUuid", required = false) String orgVersionUuid) {
        return ApiResult.success(multiOrgVersionFacade.getOrgVersionVo(orgVersionUuid));
    }

    @ApiOperation(value = "升级新版本", notes = "升级新版本", tags = {"组织管理-->组织版本"})
    @PostMapping("/addNewOrgVersionForUpgrade")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sourceVersionUuid", value = "组织版本uuid", paramType = "query", dataType = "String", required = false),
            @ApiImplicitParam(name = "isSyncName", value = "是否同步名字", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 40)
    public ApiResult<MultiOrgVersion> addNewOrgVersionForUpgrade(
            @RequestParam(name = "sourceVersionUuid", required = false) String sourceVersionUuid,
            @RequestParam(name = "isSyncName", required = false) boolean isSyncName) {
        return ApiResult.success(multiOrgVersionFacade.addNewOrgVersionForUpgrade(sourceVersionUuid, isSyncName));
    }

    @ApiOperation(value = "启用组织版本", notes = "启用组织版本", tags = {"组织管理-->组织版本"})
    @PostMapping("/activeOrgVersion")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgVersionUuid", value = "组织版本uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 50)
    public ApiResult<Boolean> activeOrgVersion(
            @RequestParam(name = "orgVersionUuid", required = false) String orgVersionUuid) {
        Boolean flg = multiOrgVersionFacade.activeOrgVersion(orgVersionUuid);
        if (flg) {
            return ApiResult.success();
        }
        return ApiResult.fail();
    }

    @ApiOperation(value = "禁用组织版本", notes = "禁用组织版本", tags = {"组织管理-->组织版本"})
    @PostMapping("/unactiveOrgVersion")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgVersionUuid", value = "组织版本uuid", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 50)
    public ApiResult<Boolean> unactiveOrgVersion(
            @RequestParam(name = "orgVersionUuid", required = false) String orgVersionUuid) {
        Boolean flg = multiOrgVersionFacade.unactiveOrgVersion(orgVersionUuid);
        if (flg) {
            return ApiResult.success();
        }
        return ApiResult.fail();
    }

    @ApiOperation(value = "获取组织版本对象", notes = "获取组织版本对象", tags = {"组织管理-->组织版本"})
    @GetMapping("/getVersionById")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "orgVersionId", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 60)
    public ApiResult<MultiOrgVersion> getVersionById(@RequestParam(name = "id", required = false) String id) {
        return ApiResult.success(multiOrgVersionFacade.getVersionById(id));
    }

    @ApiOperation(value = "获取我的子单位的的组织版本", notes = "获取我的子单位的的组织版本", tags = {"组织管理-->组织版本"})
    @GetMapping("/queryVersionTreeOfMySubUnit")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isAllVersion", value = "是否所有版本", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 70)
    public ApiResult<List<OrgTreeNode>> queryVersionTreeOfMySubUnit(
            @RequestParam(name = "isAllVersion", required = false) boolean isAllVersion) {
        List<OrgTreeNode> list = multiOrgVersionFacade.queryVersionTreeOfMySubUnit(isAllVersion);
        return ApiResult.success(list);
    }

    @ApiOperation(value = "获取单位下默认组织版本", notes = "获取单位下默认组织版本", tags = {"组织管理-->组织版本"})
    @GetMapping("/getDefaultVersionBySystemUnitId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "systemUnitId", value = "系统单位ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 80)
    public ApiResult<MultiOrgVersion> getDefaultVersionBySystemUnitId(
            @RequestParam(name = "systemUnitId", required = false) String systemUnitId) {
        return ApiResult.success(multiOrgVersionFacade.getDefaultVersionBySystemUnitId(systemUnitId));
    }

    @ApiOperation(value = "获取单位下已启用的组织版本", notes = "获取单位下已启用的组织版本")
    @GetMapping("/getVersionBySystemUnitId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "systemUnitId", value = "系统单位ID", paramType = "query", dataType = "String", required = false)})
    @ApiOperationSupport(order = 90)
    public ApiResult<List<MultiOrgVersion>> getVersionBySystemUnitId(
            @RequestParam(name = "systemUnitId", required = false) String systemUnitId) {
        return ApiResult.success(multiOrgVersionFacade.queryCurrentActiveVersionListOfSystemUnit(systemUnitId));
    }

}
