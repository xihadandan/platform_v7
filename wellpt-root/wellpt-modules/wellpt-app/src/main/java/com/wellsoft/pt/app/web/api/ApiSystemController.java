package com.wellsoft.pt.app.web.api;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.app.dto.AppSystemInfoDto;
import com.wellsoft.pt.app.dto.AppSystemLoginPageDefinitionDto;
import com.wellsoft.pt.app.entity.*;
import com.wellsoft.pt.app.service.AppSystemInfoService;
import com.wellsoft.pt.app.service.AppSystemLoginPageDefinitionService;
import com.wellsoft.pt.app.web.api.request.AppSystemPageThemeSaveRequest;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年01月16日   chenq	 Create
 * </pre>
 */
@RestController
@RequestMapping("/api/system")
public class ApiSystemController {

    @Autowired
    AppSystemInfoService appSystemInfoService;

    @Autowired
    AppSystemLoginPageDefinitionService appSystemLoginPageDefinitionService;

    @Autowired
    SecurityMetadataSourceService securityMetadataSourceService;


    @PostMapping("/saveSystemInfo")
    public ApiResult<Long> saveSystemInfo(@RequestBody AppSystemInfoEntity body) {
        return ApiResult.success(appSystemInfoService.saveSystemInfo(body));
    }

    @PostMapping("/saveAppSystemPageSetting")
    public ApiResult<Long> saveAppSystemPageSetting(@RequestBody AppSystemPageSettingEntity body) {
        return ApiResult.success(appSystemInfoService.saveSystemPageSetting(body));
    }


    @PostMapping("/saveAppSystemPageTheme")
    public ApiResult<Boolean> saveAppSystemPageTheme(@RequestBody AppSystemPageThemeSaveRequest saveRequest) {
        appSystemInfoService.saveAppSystemPageTheme(saveRequest.getSystem(), saveRequest.getTheme(), saveRequest.getPageThemes(), saveRequest.getUserThemeDefinable());
        return ApiResult.success(true);
    }

    @GetMapping("getAppSystemPageThemesBySystem")
    public ApiResult<List<AppSystemPageThemeEntity>> getAppSystemPageThemesBySystem(@RequestParam(required = false) String tenant, @RequestParam String system) {
        List<AppSystemPageThemeEntity> themes = appSystemInfoService.getAppSystemPageThemesBySystem(
                system, StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId())
        );
        return ApiResult.success(themes);
    }


    @PostMapping("/updateAppSystemPageSettingLayoutConf")
    public ApiResult<Boolean> updateAppSystemPageSettingLayoutConf(@RequestBody AppSystemPageSettingEntity body) {
        appSystemInfoService.updateAppSystemPageSettingLayoutConf(body.getLayoutConf(), body.getUserLayoutDefinable(), body.getUuid());
        return ApiResult.success(true);
    }

    @PostMapping("/updateAppSystemPageSettingThemeStyle")
    public ApiResult<Boolean> updateAppSystemPageSettingThemeStyle(@RequestBody AppSystemPageSettingEntity body) {
        appSystemInfoService.updateAppSystemPageSettingThemeStyle(body.getThemeStyle(), body.getUserThemeDefinable(), body.getUuid());
        return ApiResult.success(true);
    }


    @GetMapping("/getTenantSystemInfo")
    public ApiResult<AppSystemInfoEntity> getTenantSystemInfo(@RequestParam(required = false) String tenant, @RequestParam String system) {
        tenant = StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId());
        AppSystemInfoDto dto = appSystemInfoService.getSystemInfoWithLayoutThemeByTenantAndSystem(tenant, system);
        return ApiResult.success(dto);
    }

    @GetMapping("/getSystemInfoWithProdVersion")
    public ApiResult<AppSystemInfoDto> getSystemInfoWithProdVersion(@RequestParam(required = false) String tenant, @RequestParam String system) {
        tenant = StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId());
        return ApiResult.success(appSystemInfoService.getSystemInfoWithProdVersion(tenant, system));
    }

    @GetMapping("/getTenantSystemPageSetting")
    public ApiResult<AppSystemPageSettingEntity> getTenantSystemPageSetting(@RequestParam(required = false) String tenant, @RequestParam String system) {
        tenant = StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId());
        AppSystemPageSettingEntity entity = appSystemInfoService.getSystemPageSetting(tenant, system);
        return ApiResult.success(entity);
    }

    @GetMapping("/enableUserThemeDefinable")
    public ApiResult<Boolean> enableUserThemeDefinable(@RequestParam String system) {
        return ApiResult.success(appSystemInfoService.enableUserThemeDefinable(SpringSecurityUtils.getCurrentTenantId(), system));
    }

    @GetMapping("/enableUserLayoutDefinable")
    public ApiResult<Boolean> enableUserLayoutDefinable(@RequestParam String system) {
        return ApiResult.success(appSystemInfoService.enableUserLayoutDefinable(SpringSecurityUtils.getCurrentTenantId(), system));
    }

    @GetMapping("/getTenantSystemLoginPolicies")
    public ApiResult<List<AppSystemLoginPolicyEntity>> getTenantSystemLoginPolicies(@RequestParam(required = false) String tenant, @RequestParam String system) {
        tenant = StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId());
        List<AppSystemLoginPolicyEntity> list = appSystemInfoService.getTenantSystemLoginPolicies(tenant, system);
        return ApiResult.success(list);
    }

    @PostMapping("/saveTenantSystemLoginPolicies")
    public ApiResult<Boolean> saveTenantSystemLoginPolicies(@RequestBody List<AppSystemLoginPolicyEntity> list) {
        appSystemInfoService.saveTenantSystemLoginPolicies(list);
        return ApiResult.success(true);
    }

    @PostMapping("/updateLoginPageDefJson")
    public ApiResult<Void> updateLoginPageDefJson(@RequestBody AppSystemLoginPageDefinitionEntity body) {
        appSystemLoginPageDefinitionService.updateLoginPageDefJson(body.getDefJson(), body.getUuid());
        return ApiResult.success();
    }

    @PostMapping("/updateLoginPageWithoutDefJson")
    public ApiResult<Void> updateLoginPageWithoutDefJson(@RequestBody AppSystemLoginPageDefinitionEntity body) {
        appSystemLoginPageDefinitionService.updateLoginPageWithoutDefJson(body);
        return ApiResult.success();
    }

    @GetMapping("/getAppSystemLoginPageDefinition")
    public ApiResult<AppSystemLoginPageDefinitionEntity> getAppSystemLoginPageDefinition(@RequestParam Long uuid) {
        return ApiResult.success(appSystemLoginPageDefinitionService.getOne(uuid));
    }

    @PostMapping("/saveLoginPage")
    public ApiResult<Long> saveLoginPage(@RequestBody AppSystemLoginPageDefinitionEntity body) {
        appSystemLoginPageDefinitionService.save(body);
        return ApiResult.success(body.getUuid());
    }

    @GetMapping("/getAllLoginPage")
    public ApiResult<List<AppSystemLoginPageDefinitionEntity>> getAllLoginPage(@RequestParam(required = false) String tenant, @RequestParam String system) {
        return ApiResult.success(appSystemLoginPageDefinitionService.getAllLoginPage(tenant, system));
    }

    @GetMapping("/getAllEnabledLoginPage")
    public ApiResult<List<AppSystemLoginPageDefinitionEntity>> getAllEnabledLoginPage() {
        return ApiResult.success(appSystemLoginPageDefinitionService.getAllEnabledLoginPage());
    }

    @GetMapping("/getAllProdVersionLoginPage")
    public ApiResult<List<AppSystemLoginPageDefinitionEntity>> getAllProdVersionLoginPage(@RequestParam Long prodVersionUuid) {
        return ApiResult.success(appSystemLoginPageDefinitionService.getAllProdVersionLoginPage(prodVersionUuid));
    }

    @GetMapping("getEnableTenantSystemLoginPagePolicy")
    public ApiResult<AppSystemLoginPageDefinitionDto> getEnableTenantSystemLoginPagePolicy(@RequestParam(required = false) String tenant, @RequestParam String system) {
        return ApiResult.success(appSystemLoginPageDefinitionService.getEnableTenantSystemLoginPagePolicy(StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()), system));

    }

    @GetMapping("/copyLoginPage")
    public ApiResult<Long> copyLoginPage(@RequestParam Long uuid) {
        Long copyUuid = appSystemLoginPageDefinitionService.copyLoginPage(uuid);
        return ApiResult.success(copyUuid);
    }

    @GetMapping("/createAppSystemParamsFromAppProdVersion")
    public ApiResult createAppSystemParamsFromAppProdVersion(@RequestParam String system) {
        appSystemInfoService.createAppSystemParamsFromAppProdVersion(system);
        return ApiResult.success();
    }

    @GetMapping("/enableLoginPage")
    public ApiResult<Boolean> enableLoginPage(@RequestParam(required = false, defaultValue = "true") Boolean enabled, @RequestParam Long uuid) {
        return ApiResult.success(appSystemLoginPageDefinitionService.enableLoginPage(uuid, enabled));
    }

    @GetMapping("/setDefaultLoginDef")
    public ApiResult<Boolean> setDefaultLoginDef(@RequestParam Long uuid) {
        appSystemLoginPageDefinitionService.setDefaultLoginDef(uuid);
        return ApiResult.success(true);
    }

    @PostMapping("/deleteLoginPages")
    public ApiResult<Void> deleteLoginPages(@RequestBody List<Long> uuids) {
        appSystemLoginPageDefinitionService.deleteByUuids(uuids);
        return ApiResult.success();
    }

    @GetMapping("/getAppSystemParamByKey")
    public ApiResult<AppSystemParamEntity> getAppSystemParamByKey(@RequestParam("key") String key) {
        AppSystemParamEntity entity = appSystemInfoService.getAppSystemParamByKeyAndSystem(key, RequestSystemContextPathResolver.system());
        return ApiResult.success(entity);
    }

    @PostMapping("/saveAppSystemParam")
    public ApiResult<Boolean> saveAppSystemParam(@RequestBody AppSystemParamEntity param) {
        appSystemInfoService.saveAppSystemParam(param);
        return ApiResult.success(true);
    }

    @PostMapping("/deleteAppSystemParam")
    public ApiResult<Boolean> deleteAppSystemParam(@RequestBody List<Long> uuid) {
        appSystemInfoService.deleteAppSystemParam(uuid);
        return ApiResult.success(true);
    }

    @GetMapping("/checkAppSystemParamPropKeyExist")
    public ApiResult<Boolean> checkAppSystemParamPropKeyExist(@RequestParam String propKey, @RequestParam String system) {
        return ApiResult.success(appSystemInfoService.checkAppSystemParamPropKeyExist(propKey, system));
    }

    @GetMapping("/queryAppSystemPages")
    public ApiResult<List<AppPageDefinition>> queryAppSystemPages(@RequestParam(required = false) String tenant, @RequestParam String system) {
        List<AppPageDefinition> list = appSystemInfoService.queryAppSystemPages(StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()), system);
        return ApiResult.success(list);
    }

    @GetMapping("/queryAppSystemModules")
    public ApiResult<List<AppModule>> queryAppSystemModules(@RequestParam(required = false) String tenant, @RequestParam String system) {
        List<AppModule> list = appSystemInfoService.queryAppSystemModules(StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()), system);
        return ApiResult.success(list);
    }


    @GetMapping("/queryControllableResourceTree")
    public ApiResult<TreeNode> querySystemUnderControllableResourceTree(@RequestParam String system, @RequestParam(required = false) String tenant) {
        TreeNode node = appSystemInfoService.querySystemUnderControllableResourceTree(system, StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()));
        return ApiResult.success(node);
    }

    @GetMapping("/createSystemPageAndModuleDefaultRolePvg")
    public ApiResult<Boolean> createSystemPageAndModuleDefaultRolePvg(@RequestParam String system, @RequestParam(required = false) String tenant) {
        appSystemInfoService.createSystemPageAndModuleDefaultRolePvg(system, StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()));
        securityMetadataSourceService.loadSecurityMetadataSource();
        return ApiResult.success(true);
    }

    @GetMapping("/listAllAppSystemParam")
    public ApiResult<List<AppSystemParamEntity>> listAllAppSystemParam() {
        return ApiResult.success(appSystemInfoService.listAllAppSystemParam());
    }

    @GetMapping("/listAllAppSystemInfos")
    public ApiResult<List<AppSystemInfoEntity>> listAllAppSystemInfos() {
        return ApiResult.success(appSystemInfoService.listAll());
    }

}
