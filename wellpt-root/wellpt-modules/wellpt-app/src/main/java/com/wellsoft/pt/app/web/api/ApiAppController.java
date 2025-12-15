package com.wellsoft.pt.app.web.api;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.pt.app.dto.AppModuleDto;
import com.wellsoft.pt.app.dto.AppProdVersionDto;
import com.wellsoft.pt.app.dto.AppProductDto;
import com.wellsoft.pt.app.entity.*;
import com.wellsoft.pt.app.service.*;
import com.wellsoft.pt.app.web.api.request.AppModuleKeywordQuery;
import com.wellsoft.pt.app.web.api.request.AppProdKeywordQuery;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年07月05日   chenq	 Create
 * </pre>
 */
@RestController
@RequestMapping("/api/app")
public class ApiAppController {
    @Autowired
    AppModuleService appModuleService;

    @Autowired
    AppModuleResGroupService appModuleResGroupService;

    @Autowired
    AppResourceUpgradeLogService upgradeLogService;

    @Autowired
    AppTagService appTagService;

    @Autowired
    AppProductService appProductService;

    @Autowired
    AppProductAclService appProductAclService;

    @Autowired
    AppCategoryService appCategoryService;

    @Autowired
    AppProdVersionService appProdVersionService;

    @Autowired
    AppProdVersionParamService appProdVersionParamService;

    @Autowired
    AppProdVersionSettingService appProdVersionSettingService;

    @Autowired
    AppPageDefinitionService appPageDefinitionService;

    @Autowired
    AppProdAnonUrlService appProdAnonUrlService;


    @PostMapping("/module/save")
    public ApiResult<String> saveModule(@RequestBody AppModuleDto dto) {
        return ApiResult.success(appModuleService.saveModule(dto));
    }

    @GetMapping("/module/delete/{uuid}")
    public ApiResult<Boolean> deleteModule(@PathVariable String uuid) {
        appModuleService.remove(uuid);
        return ApiResult.success(true);
    }

    @PostMapping("/module/delete")
    public ApiResult<Boolean> deleteModule(@RequestBody List<String> uuid) {
        appModuleService.deleteModulesByUuids(uuid);
        return ApiResult.success(true);
    }

    @GetMapping("/module/enabled/{uuid}")
    public ApiResult<Boolean> enableModule(@PathVariable String uuid, @RequestParam(required = false) Boolean enabled) {
        appModuleService.updateEnabled(uuid, BooleanUtils.isTrue(enabled));
        return ApiResult.success(true);
    }

    @PostMapping("/module/listById")
    public ApiResult<List<AppModule>> listModuleByIds(@RequestBody List<String> id) {
        return ApiResult.success(appModuleService.listModuleByIds(id));
    }

    @GetMapping("/module/listModuleUnderSystem")
    public ApiResult<List<AppModule>> listModuleUnderSystem(@RequestParam String system, @RequestParam(required = false) String tenant) {
        List<AppModule> modules = appModuleService.listModuleUnderSystem(system, StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()));
        return ApiResult.success(modules);
    }

    @GetMapping("/module/getAllEnabled")
    public ApiResult<List<AppModule>> getAllEnableModules() {
        return ApiResult.success(appModuleService.getAllEnableModules());
    }

    @GetMapping("/module/{id}/exist")
    public ApiResult<Boolean> moduleIdExist(@PathVariable String id) {
        return ApiResult.success(appModuleService.moduleIdExist(id));
    }

    @GetMapping("/module/queryRelaModuleIds")
    public ApiResult<List<String>> queryRelaModuleIds(@RequestParam String moduleId) {
        return ApiResult.success(appModuleService.queryRelaModuleIds(moduleId));
    }

    @GetMapping("/module/queryRelaModuleByModuleId")
    public ApiResult<List<QueryItem>> queryRelaModuleByModuleId(@RequestParam String moduleId) {
        return ApiResult.success(appModuleService.queryRelaModulesByModuleId(moduleId));
    }

    @GetMapping("/module/getModuleRelaSystems")
    public ApiResult<Set<String>> getModuleRelaSystems(@RequestParam String moduleId) {
        return ApiResult.success(appModuleService.getModuleRelaSystems(moduleId));
    }


    @GetMapping("/module/details/{uuid}")
    public ApiResult<AppModule> getModuleDetails(@PathVariable String uuid) {
        return ApiResult.success(appModuleService.get(uuid));
    }

    @GetMapping("/module/detailsById/{id}")
    public ApiResult<AppModule> getModuleDetailsById(@PathVariable String id) {
        return ApiResult.success(appModuleService.getById(id));
    }

    @PostMapping("/module/resGroup/save")
    public ApiResult<Long> saveModuleResGroup(@RequestBody AppModuleResGroupEntity entity) {
        appModuleResGroupService.save(entity);
        return ApiResult.success(entity.getUuid());
    }

    @GetMapping("/module/resGroup/addMember")
    public ApiResult<Boolean> addMember(@RequestParam Long memberUuid, @RequestParam Long afterMemberUuid, @RequestParam String type) {
        appModuleResGroupService.addMember(memberUuid, afterMemberUuid, type);
        return ApiResult.success(true);
    }

    @PostMapping("/module/res/seq/save")
    public ApiResult<Boolean> saveModuleResSeq(@RequestBody List<AppModuleResSeqEntity> list) {
        appModuleService.saveModuleResSeq(list);
        return ApiResult.success(true);
    }

    @GetMapping("/module/res/seq/list")
    public ApiResult<List<AppModuleResSeqEntity>> listModuleResSeq(@RequestParam String moduleId, @RequestParam(required = false) String type) {
        return ApiResult.success(appModuleService.listModuleResSeq(moduleId, type));
    }

    @GetMapping("/module/resGroup/delete")
    public ApiResult deleteModuleResGroup(@RequestParam Long uuid) {
        appModuleResGroupService.deleteGroup(uuid);
        return ApiResult.success(true);
    }

    @GetMapping("/module/resGroup/rename")
    public ApiResult deleteModuleResGroup(@RequestParam Long uuid, @RequestParam String name) {
        appModuleResGroupService.rename(uuid, name);
        return ApiResult.success(true);
    }

    @GetMapping("/module/resGroup/list")
    public ApiResult<List<AppModuleResGroupEntity>> listModuleResGroup(@RequestParam String moduleId) {
        return ApiResult.success(appModuleResGroupService.listByModuleId(moduleId));
    }

    @GetMapping("/module/resGroup/member")
    public ApiResult<List<AppModuleResGroupMemberEntity>> listModuleResGrpMember(@RequestParam String moduleId) {
        return ApiResult.success(appModuleResGroupService.listModuleResGrpMember(moduleId));
    }

    @GetMapping("/module/resGroup/updateMember")
    public ApiResult<Boolean> updateMember(@RequestParam Long memberUuid, @RequestParam(required = false) Long groupUuid,
                                           @RequestParam(required = false) String type,
                                           @RequestParam(required = false) Long updateMemberUuid) {
        appModuleResGroupService.updateModuleResGroupMem(memberUuid, groupUuid, type, updateMemberUuid);
        return ApiResult.success(true);
    }


    @PostMapping("/module/query")
    public ApiResult<Map> queryModule(@RequestBody AppModuleKeywordQuery request) {
        if (request.getPage() != null) {
            request.getPage().setAutoCount(false);
        }
        List<AppModule> dtos = appModuleService.queryByKeyword(request);
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", dtos);
        result.put("page", request.getPage());
        return ApiResult.success(result);
    }


    @PostMapping("/res/upgrade/saveLog")
    public ApiResult<Boolean> saveResUpgradeLog(@RequestBody AppResourceUpgradeLogEntity log) {
        upgradeLogService.saveLog(log);
        return ApiResult.success(true);
    }

    @GetMapping("/res/upgrade/log")
    public ApiResult<List<AppResourceUpgradeLogEntity>> saveResUpgradeLog(@RequestParam String id) {
        return ApiResult.success(upgradeLogService.getById(id));
    }


    @GetMapping("/tag/create")
    public ApiResult<Long> createTag(@RequestParam String name, @RequestParam String applyTo) {
        return ApiResult.success(appTagService.createTag(name, applyTo));
    }


    @GetMapping("/tag/delete/{uuid}")
    public ApiResult<Boolean> deleteTag(@RequestParam Long tagUuid) {
        appTagService.deleteTag(tagUuid);
        return ApiResult.success(true);
    }

    @GetMapping("/tag/deleteDataTag")
    public ApiResult<Boolean> deleteTag(@RequestParam String dataId, @RequestParam Long tagUuid) {
        appTagService.deleteDataTag(dataId, tagUuid);
        return ApiResult.success(true);
    }

    @GetMapping("/tag/queryByApplyTo")
    public ApiResult<List<AppTagEntity>> createTag(@RequestParam String applyTo) {
        return ApiResult.success(appTagService.getTagsByApplyTo(applyTo));
    }

    @GetMapping("/tag/queryDataTag")
    public ApiResult<List<AppTagEntity>> queryDataTag(@RequestParam String dataId) {
        return ApiResult.success(appTagService.getTagsByDataId(dataId));
    }

    @GetMapping("/category/getAll")
    public ApiResult<List<AppCategoryEntity>> getProdCateogries(@RequestParam String applyTo) {
        return ApiResult.success(appCategoryService.getAllCategoryByApplyTo(applyTo));
    }

    @PostMapping("/category/save")
    public ApiResult<Long> saveProdCateogry(@RequestBody AppCategoryEntity dto) {
        return ApiResult.success(appCategoryService.saveCategory(dto));
    }

    @GetMapping("/category/delete/{uuid}")
    public ApiResult<Boolean> deleteProdCategory(@PathVariable String uuid) {
        appCategoryService.deleteCategory(Long.parseLong(uuid));
        return ApiResult.success(true);
    }

    @GetMapping("/prod/getDetail")
    public ApiResult<AppProductDto> getProdDetail(@RequestParam(required = false) String uuid, @RequestParam(required = false) String id) {
        return ApiResult.success(StringUtils.isNotBlank(uuid) ? appProductService.getProductDetail(uuid) : (
                StringUtils.isNotBlank(id) ? appProductService.getProductDetailById(id) : null
        ));
    }

    @GetMapping("/prod/updateStatus")
    public ApiResult<AppProductDto> updateProdStatus(@RequestParam String uuid, @RequestParam AppProduct.Status status) {
        return ApiResult.success(appProductService.updateStatus(uuid, status));
    }

    @PostMapping("/prod/save")
    public ApiResult<AppProductDto> saveProd(@RequestBody AppProductDto dto) {
        return ApiResult.success(appProductService.saveProduct(dto));
    }

    @GetMapping("/prod/{id}/exist")
    public ApiResult<Boolean> prodIdExist(@PathVariable String id) {
        return ApiResult.success(appProductService.idExist(id));
    }

    @PostMapping("/prod/version/listById")
    public ApiResult<List<AppProdVersionEntity>> listProdVersionByIds(@RequestBody List<String> id) {
        return ApiResult.success(appProdVersionService.listProdVersionByIds(id));
    }

    @PostMapping("/prod/acl/save")
    public ApiResult<Boolean> saveProdAcl(@RequestBody AppProductDto dto) {
        appProductAclService.saveProdAcl(dto.getAclList(), dto.getId());
        return ApiResult.success(true);
    }

    @GetMapping("/prod/acl/get")
    public ApiResult<List<AppProductAclEntity>> getProdAcl(@RequestParam String prodId) {
        return ApiResult.success(appProductAclService.getProdAcl(prodId));
    }

    @GetMapping("/prod/version/modules")
    public ApiResult<List<AppModule>> getProdVersionModules(@RequestParam(required = false) Long prodVersionUuid, @RequestParam(required = false) String prodVersionId) {
        List<AppModule> appModules = prodVersionUuid != null ? appProdVersionService.getVersionModulesByVersionUuid(prodVersionUuid) :
                appProdVersionService.getVersionModulesByVersionId(prodVersionId);
        return ApiResult.success(appModules);
    }


    @GetMapping("/prod/version/updateProdVersionPage")
    public ApiResult<Boolean> updateProdVersionPage(@RequestParam Long prodVersionUuid, @RequestParam(required = false) String fromPageUuid, @RequestParam String pageUuid) {
        appProdVersionService.updateProdVersionPage(prodVersionUuid, fromPageUuid, pageUuid);
        return ApiResult.success(true);
    }

    @GetMapping("/prod/version/getProdVersionRelaPage")
    public ApiResult<List<AppProdRelaPageEntity>> getProdVersionRelaPage(@RequestParam Long prodVersionUuid) {
        return ApiResult.success(appProdVersionService.getProdVersionRelaPage(prodVersionUuid));
    }

    @GetMapping("/prod/version/getPageInfosIgnoreDefinitionUnderProdVersion")
    public ApiResult<List<AppPageDefinition>> getPageInfosIgnoreDefinitionUnderProdVersion(@RequestParam Long prodVersionUuid) {
        return ApiResult.success(appProdVersionService.getPageInfosIgnoreDefinitionUnderProdVersion(prodVersionUuid));
    }


    @PostMapping("/prod/version/updateProdVersionRelaPageTheme/{prodVersionUuid}")
    public ApiResult<Boolean> updateProdVersionRelaPageTheme(@PathVariable Long prodVersionUuid,
                                                             @RequestBody List<AppProdRelaPageEntity> pages) {
        appProdVersionService.updateProdVersionRelaPageTheme(prodVersionUuid, pages);
        return ApiResult.success(true);
    }

    @GetMapping("/prod/version/getProdVersionPageTheme")
    public ApiResult<String> getProdVersionPageTheme(@RequestParam Long prodVersionUuid, @RequestParam String pageId) {
        return ApiResult.success(appProdVersionService.getProdVersionPageTheme(prodVersionUuid, pageId));
    }

    @GetMapping("/prod/version/latestPublishedIndexUrl")
    public ApiResult<String> latestPublishedPcIndexUrl(@RequestParam String prodId, @RequestParam(required = false) String type) {
        AppProdVersionSettingEntity settingEntity = appProdVersionSettingService.getLatestPublishedVersionSetting(prodId);
        return ApiResult.success(settingEntity != null ? (StringUtils.isBlank(type) || type.equalsIgnoreCase("pcIndexUrl") ? settingEntity.getPcIndexUrl() : settingEntity.getMobileIndexUrl()) : null);
    }

    @GetMapping("/prod/version/latestPublishedVersionSetting")
    public ApiResult<AppProdVersionSettingEntity> latestPublishedVersionSetting(@RequestParam String prodId) {
        AppProdVersionSettingEntity settingEntity = appProdVersionSettingService.getLatestPublishedVersionSetting(prodId);
        return ApiResult.success(settingEntity);
    }

    @GetMapping("/prod/allPublishedAnonUrls")
    public ApiResult<List<AppProdAnonUrlEntity>> getAllPublishedAnonProdUrl() {
        return ApiResult.success(appProdAnonUrlService.getAllPublishedAnonProdUrls());
    }

    @GetMapping("/prod/version/pageUuid")
    public ApiResult<String> getProdVersionPageUuid(@RequestParam Long prodVersionUuid, @RequestParam String pageId) {
        String pageUuid = appProdVersionService.getProdVersionPageUuid(prodVersionUuid, pageId);
        return ApiResult.success(pageUuid);
    }

    @GetMapping("/prod/version/pages")
    public ApiResult<List<AppPageDefinition>> getProdVersionPages(@RequestParam(required = false) Long prodVersionUuid, @RequestParam(required = false) String prodVersionId) {
        List<AppPageDefinition> pages = appProdVersionService.getProdVersionPages(prodVersionUuid);
        return ApiResult.success(pages);
    }

    @GetMapping("/prod/version/deletePage")
    public ApiResult<Boolean> deletePage(@RequestParam(required = false) String pageId, @RequestParam(required = false) Long prodVersionUuid) {
        appProdVersionService.deleteProdVersionPage(pageId, prodVersionUuid);
        return ApiResult.success(true);
    }


    @GetMapping("/prod/version/underModules")
    public ApiResult<List<AppProdModuleEntity>> getModulesByProdVersionUuid(@RequestParam(required = false) Long prodVersionUuid) {
        List<AppProdModuleEntity> list = appProdVersionService.getModulesByProdVersionUuid(prodVersionUuid);
        return ApiResult.success(list);
    }

    @GetMapping("/prod/version/new")
    public ApiResult<AppProdVersionEntity> saveAsNewVersion(@RequestParam(required = false) Long sourceUuid, @RequestParam String prodId, @RequestParam String version) {
        return ApiResult.success(appProdVersionService.saveAsNewVersion(sourceUuid, version, prodId));
    }

    @GetMapping("/prod/version/delete/{uuid}")
    public ApiResult<Boolean> deleteProdVersion(@PathVariable String uuid) {
        appProdVersionService.deleteVersion(Long.parseLong(uuid));
        return ApiResult.success(true);
    }

    @GetMapping("/prod/version/getPageDefinitions")
    public ApiResult<List<AppPageDefinition>> getVersionPageDefinitions(@RequestParam String prodVersionId) {
        return ApiResult.success(appPageDefinitionService.listByAppId(prodVersionId));
    }

    @GetMapping("/prod/version/getSetting")
    public ApiResult<AppProdVersionSettingEntity> getVersionSetting(@RequestParam Long versionUuid) {
        return ApiResult.success(appProdVersionSettingService.getByProdVersionUuid(versionUuid));
    }

    @GetMapping("/prod/version/getAnonUrls")
    public ApiResult<List<AppProdAnonUrlEntity>> getAnonUrls(@RequestParam Long versionUuid) {
        return ApiResult.success(appProdAnonUrlService.listByProdVersionUuid(versionUuid));
    }

    @GetMapping("/prod/version/exist")
    public ApiResult<Boolean> existVersion(@RequestParam String version, @RequestParam String prodId) {
        AppProdVersionEntity entity = appProdVersionService.getByVersionAndProdId(version, prodId);
        return ApiResult.success(entity != null);
    }

    @GetMapping("/prod/version/details")
    public ApiResult<AppProdVersionDto> getProdVersionDetails(@RequestParam Long uuid) {
        return ApiResult.success(appProdVersionService.getProdVersionDetails(uuid));
    }

    @GetMapping("/prod/version/publish/{uuid}")
    public ApiResult<Boolean> publishProdVersion(@PathVariable String uuid) {
        appProdVersionService.updateStatus(Long.parseLong(uuid), AppProdVersionEntity.Status.PUBLISHED);
        return ApiResult.success(true);
    }

    @GetMapping("/prod/version/roles")
    public ApiResult<List<Role>> getProdVersionRoles(@RequestParam String prodVersionId) {
        List<Role> roles = appProdVersionService.getVersionRolesByVersionId(prodVersionId);
        return ApiResult.success(roles);
    }

    @GetMapping("/prod/version/privileges")
    public ApiResult<List<Privilege>> getProdVersionPrivilege(@RequestParam String prodVersionId) {
        List<Privilege> privileges = appProdVersionService.getVersionPrivilegesByVersionId(prodVersionId);
        return ApiResult.success(privileges);
    }

    @GetMapping("/prod/version/updateStatus")
    public ApiResult<Boolean> updateProdVersionStatus(@RequestParam Long uuid, @RequestParam AppProdVersionEntity.Status status) {
        appProdVersionService.updateStatus(uuid, status);
        return ApiResult.success(true);
    }

    @GetMapping("/prod/version/getAll")
    public ApiResult<List<AppProdVersionEntity>> getAllProdVersions(@RequestParam String prodId) {
        return ApiResult.success(appProdVersionService.getAllByProdId(prodId));
    }


    @GetMapping("/prod/delete/{uuid}")
    public ApiResult<Boolean> deleteProd(@PathVariable String uuid) {
        appProductService.deleteProd(uuid);
        return ApiResult.success(true);
    }

    @PostMapping("/prod/version/save")
    public ApiResult<Long> saveProdVersion(@RequestBody AppProdVersionDto versionDto) {
        return ApiResult.success(appProdVersionService.saveVersion(versionDto));
    }

    @PostMapping("/prod/version/removeUnusedProdModuleNestedRole/{prodVersionUuid}")
    public ApiResult<Boolean> removeUnusedProdModuleNestedRole(@PathVariable("prodVersionUuid") Long prodVersionUuid, @RequestBody List<String> moduleIds) {
        appProdVersionService.removeUnusedProdModuleNestedRole(prodVersionUuid, moduleIds);
        return ApiResult.success(true);
    }


    @PostMapping("/prod/version/saveVersionSetting")
    public ApiResult<Boolean> saveVersionSetting(@RequestBody AppProdVersionSettingEntity settingEntity) {
        appProdVersionSettingService.saveAppProdVersionSetting(settingEntity);
        return ApiResult.success(true);
    }


    @GetMapping("/prod/version/log/detail")
    public ApiResult<AppProductVersionLogEntity> saveProdVersionLog(@RequestParam Long prodVersionUuid) {
        return ApiResult.success(appProdVersionService.getProdVersionLog(prodVersionUuid));
    }

    @PostMapping("/prod/version/log/save")
    public ApiResult<Long> saveProdVersionLog(@RequestBody AppProdVersionDto versionDto) {
        return ApiResult.success(appProdVersionService.saveProdVersionLog(versionDto));
    }

    @PostMapping("/prod/query")
    public ApiResult<Map> queryProd(@RequestBody AppProdKeywordQuery query) {
        if (query.getPage() != null) {
            query.getPage().setAutoCount(true);
        }
        List<AppProduct> dtos = appProductService.queryByKeyword(query);
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", dtos);
        result.put("page", query.getPage());
        return ApiResult.success(result);
    }

    @PostMapping("/prod/version/queryLatestPub")
    public ApiResult<List<AppProdVersionEntity>> queryLatestPubVersions(@RequestBody List<String> prodIds) {
        return ApiResult.success(appProdVersionService.queryLatestPubVersions(prodIds));
    }

    @PostMapping("/prod/version/queryLatestCreate")
    public ApiResult<List<AppProdVersionEntity>> queryLatestCreateVersions(@RequestBody List<String> prodIds) {
        return ApiResult.success(appProdVersionService.queryLatestCreateVersions(prodIds));
    }

    @GetMapping("/prod/version/queryEarliestCreate")
    public ApiResult<AppProdVersionEntity> queryEarliestCreateVersions(@RequestParam String prodId) {
        return ApiResult.success(appProdVersionService.queryEarliestCreateVersion(prodId));
    }

    @GetMapping("/prod/version/getDefaultLoginDef")
    public ApiResult<AppProdVersionLoginEntity> getDefaultLoginDef(@RequestParam Long prodVersionUuid, @RequestParam(required = false) Boolean isPc) {
        return ApiResult.success(appProdVersionService.getDefaultLoginDef(prodVersionUuid, isPc));
    }

    @GetMapping("/prod/version/setDefaultLoginDef")
    public ApiResult setDefaultLoginDef(@RequestParam Long uuid) {
        appProdVersionService.setDefaultLoginDef(uuid);
        return ApiResult.success(true);
    }

    @GetMapping("/prod/version/getLoginDef")
    public ApiResult<AppProdVersionLoginEntity> getLoginDef(@RequestParam Long uuid) {
        return ApiResult.success(appProdVersionService.getLoginDef(uuid));
    }

    @GetMapping("/prod/version/queryLoginDefList")
    public ApiResult<List<AppProdVersionLoginEntity>> queryLoginDefList(@RequestParam Long prodVersionUuid) {
        return ApiResult.success(appProdVersionService.queryProdVersionLoginByProdVersionUuid(prodVersionUuid));
    }

    @PostMapping("/prod/version/deleteLoginDef")
    public ApiResult<Boolean> deleteVersionLoginDef(@RequestBody List<Long> uuids) {
        appProdVersionService.deleteVersionLoginDef(uuids);
        return ApiResult.success(true);
    }

    @PostMapping("/prod/version/saveVersionLoginDef")
    public ApiResult<Long> saveVersionLoginDef(@RequestBody AppProdVersionLoginEntity data) {
        return ApiResult.success(appProdVersionService.saveVersionLoginDef(data));
    }

//    @PostMapping("/prod/version/queryParams")
//    public ApiResult<List<AppProdVersionParamEntity>> queryVersionParams(@RequestBody AppVersionParamKeywordQuery query) {//
//        List<AppProdVersionParamEntity> params = appProdVersionParamService.queryParams(query);
//
//    }

    @GetMapping("/prod/version/allParams/{prodVersionUuid}")
    public ApiResult<List<AppProdVersionParamEntity>> queryVersionParams(@PathVariable Long prodVersionUuid) {
        return ApiResult.success(appProdVersionParamService.getAllParamsDetail(prodVersionUuid));
    }

    @PostMapping("/prod/version/deleteParams")
    public ApiResult<Boolean> deletVersionParam(@RequestBody List<Long> uuid) {
        appProdVersionParamService.deleteParams(uuid);
        return ApiResult.success(true);
    }

    @PostMapping("/prod/version/param/save")
    public ApiResult<Long> saveVersinParam(@RequestBody AppProdVersionParamEntity param) {
        return ApiResult.success(appProdVersionParamService.saveParam(param));
    }


}
