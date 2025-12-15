package com.wellsoft.pt.org.web;

import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.org.entity.OrgElementModelEntity;
import com.wellsoft.pt.org.entity.OrgSettingEntity;
import com.wellsoft.pt.org.service.OrgElementModelService;
import com.wellsoft.pt.org.service.OrgSettingService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
@Api(tags = "组织")
@RestController
@RequestMapping("/api/org/elementModel")
public class ApiOrgElementModelController extends BaseController {

    @Resource
    OrgElementModelService orgElementModelService;

    @Resource
    OrgSettingService orgSettingService;

    @ApiOperation(value = "获取当前系统下的所有组织单元模型", notes = "获取当前系统下的所有组织单元模型")
    @GetMapping("/getAllOrgElementModels")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "system", value = "system", paramType = "query", dataType = "String", required = true)})
    public ApiResult<List<OrgElementModelEntity>> getAllOrgElementModels(@RequestParam(name = "system", required = false) String system, @RequestParam(required = false) String tenant) {
        if (StringUtils.isNotBlank(system)) {
            // 查询系统级的组织单元模型数据
            return ApiResult.success(orgElementModelService.listOrgElementModels(StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()), system));
        } else {
            // 查询平台级的组织单元模型数据
            return ApiResult.success(orgElementModelService.listOrgElementModels(null, null));
        }
    }


    @ApiOperation(value = "启用或者停用组织单元模型", notes = "启用或者停用组织单元模型")
    @GetMapping("/enable")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> queryOrgElementListByIds(@RequestParam(name = "uuid", required = true) String uuid,
                                                       @RequestParam(name = "enable", required = true) Boolean enable) {
        return ApiResult.success(orgElementModelService.enableOrgElementModel(Long.parseLong(uuid), enable));
    }

    @ApiOperation(value = "删除组织单元模型", notes = "删除组织单元模型")
    @GetMapping("/delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> deleteOrgElementModel(@RequestParam(name = "uuid", required = true) String uuid) {
        return ApiResult.success(orgElementModelService.deleteOrgElementModel(Long.parseLong(uuid)));
    }

    @ApiOperation(value = "保存组织单元模型", notes = "保存组织单元模型")
    @PostMapping("/save")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult<String> save(@RequestBody OrgElementModelEntity orgElementModel) {
        Long uuid = orgElementModelService.saveOrgElementModel(orgElementModel);
        return ApiResult.success(uuid.toString());
    }

    @ApiOperation(value = "查询组织单元模型", notes = "查询组织单元模型")
    @GetMapping("/getDetailByUuid")
    public ApiResult<OrgElementModelEntity> getDetailByUuid(@RequestParam Long uuid) {
        return ApiResult.success(orgElementModelService.getDetailByUuid(uuid));
    }

    @ApiOperation(value = "查询组织单元模型的定义数据", notes = "查询组织单元模型的定义数据")
    @GetMapping("/getDefJson")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query", dataType = "String", required = true)})
    public ApiResult<String> getDefJson(@RequestParam String id, @RequestParam(required = false) String system) {
        return ApiResult.success(orgElementModelService.getOrgElementModelDefJson(id, system));
    }

    @ApiOperation(value = "ID查重", notes = "ID查重")
    @GetMapping("/id/exist")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> exist(@RequestParam(name = "id", required = true) String id,
                                    @RequestParam(name = "system", required = false) String system) {
        OrgElementModelEntity entity = orgElementModelService.getOrgElementModelByIdAndSystem(id, system);
        return ApiResult.success(entity != null);
    }


    @ApiOperation(value = "查询组织设置参数", notes = "查询组织设置参数")
    @GetMapping("/queryOrgSetting")
    public ApiResult<List<OrgSettingEntity>> queryOrgSetting(@RequestParam(required = false) String system, @RequestParam(required = false) String tenant) {
        return ApiResult.success(StringUtils.isBlank(system) ? orgSettingService.listBySystemIsNull() : orgSettingService.listBySystemAndTenant(system, StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId())));
    }

    @ApiOperation(value = "更新组织设置参数", notes = "更新组织设置参数")
    @PostMapping("/updateOrgSetting")
    public ApiResult<Boolean> updateOrgSetting(@RequestBody OrgSettingEntity orgSettingEntity) {
        orgSettingService.updateOrgSetting(orgSettingEntity);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "启用组织的指定设置", notes = "启用组织的指定设置")
    @GetMapping("/enableOrgSetting")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "uuid", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "enable", value = "enable", paramType = "query", dataType = "Boolean", required = true)})
    public ApiResult<Boolean> enableOrgSetting(@RequestParam(required = true) String uuid, @RequestParam(name = "enable", required = true) Boolean enable) {
        orgSettingService.enableByUuid(Long.parseLong(uuid), enable);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "删除组织的指定设置", notes = "删除组织的指定设置")
    @GetMapping("/deleteOrgSetting")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult<Boolean> deleteOrgSetting(@RequestParam(required = true) String uuid) {
        orgSettingService.delete(Long.parseLong(uuid));
        return ApiResult.success(true);
    }

    @ApiOperation(value = "新增基于指定目录下的组织设置", notes = "新增基于指定目录下的组织设置")
    @PostMapping("/addOrgSetting")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "uuid", paramType = "query", dataType = "String", required = true)})
    public ApiResult<String> addOrgSetting(@RequestBody OrgSettingEntity orgSettingEntity) {
        String system = RequestSystemContextPathResolver.system();
        if (orgSettingEntity.getUuid() == null && StringUtils.isNotBlank(system)) {
            orgSettingEntity.setSystem(system);
            orgSettingEntity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        orgSettingService.save(orgSettingEntity);
        return ApiResult.success(orgSettingEntity.getUuid().toString());
    }


    @GetMapping("/createOrgElementModelAndSetting")
    public ApiResult<Boolean> createOrgElementModelAndSetting(@RequestParam String system, @RequestParam(required = false) String tenant) {
        orgElementModelService.createOrgElementModelAndSetting(system, StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()));
        return ApiResult.success(true);
    }
}
