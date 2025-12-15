package com.wellsoft.pt.org.web;

import com.alibaba.excel.EasyExcel;
import com.google.common.base.Throwables;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.multi.org.service.OrgDutySeqService;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.org.dto.*;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.org.listener.OrgConstructImportListener;
import com.wellsoft.pt.org.listener.OrgElementExcelDo;
import com.wellsoft.pt.org.query.OrgVersionQueryItem;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserSystemOrgDetails;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

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
@RequestMapping("/api/org")
public class ApiOrganizationController extends BaseController {

    @Resource
    OrgUnitService orgUnitService;

    @Resource
    OrganizationService organizationService;

    @Resource
    OrgVersionService orgVersionService;

    @Resource
    OrgElementService orgElementService;

    @Resource
    OrgRoleService orgRoleService;

    @Resource
    OrgElementRoleMemberService orgElementRoleMemberService;

    @Resource
    UserInfoService userInfoService;

    @Resource
    OrgUserService orgUserService;

    @Resource
    OrgSettingService orgSettingService;

    @Resource
    OrgDutySeqService orgDutySeqService;

    @Resource
    OrgGroupService orgGroupService;

    @Resource
    OrgPartnerSysCategoryService orgPartnerSysCategoryService;

    @Resource
    OrgPartnerSysApplyService orgPartnerSysApplyService;

    @Resource
    OrgFacadeService orgFacadeService;

    @Resource
    BizOrganizationService bizOrganizationService;

    @Resource
    BizOrgElementService bizOrgElementService;

    @Resource
    BizOrgElementMemberService bizOrgElementMemberService;

    @Resource
    OrgElementI18nService orgElementI18nService;

    @Resource
    OrgElementManagementService orgElementManagementService;


    @ApiOperation(value = "保存单位", notes = "保存单位")
    @PostMapping("/unit/save")
    public ApiResult<String> save(@RequestBody OrgUnitDto orgUnitDto) {
        Long uuid = orgUnitService.saveOrgUnit(orgUnitDto);
        return ApiResult.success(uuid.toString());
    }

    @ApiOperation(value = "查询单位详情", notes = "查询单位详情")
    @GetMapping("/unit/details/{uuid}")
    public ApiResult<OrgUnitDto> details(@PathVariable("uuid") String uuid) {
        return ApiResult.success(orgUnitService.getOrgUnitDetailsByUuid(Long.parseLong(uuid)));
    }

    @ApiOperation(value = "查询单位列表", notes = "查询单位列表")
    @GetMapping("/unit/listUnits")
    public ApiResult<List<OrgUnitEntity>> listUnits(@RequestParam(required = false) String system, @RequestParam(required = false) String tenant) {
        return ApiResult.success(orgUnitService.listBySystemAndTenant(system, tenant));
    }

    @ApiOperation(value = "启用单位", notes = "启用单位")
    @GetMapping("/unit/enable")
    public ApiResult<Boolean> enable(@RequestParam Long uuid, @RequestParam(required = false) Boolean enable) {
        orgUnitService.enable(uuid, enable);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "删除单位", notes = "删除单位")
    @GetMapping("/unit/delete")
    public ApiResult<Boolean> delete(@RequestParam Long uuid) {
        orgUnitService.deleteByUuid(uuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "获取使用单位的组织列表", notes = "获取使用单位的组织列表")
    @GetMapping("/unit/listUsedOrganization")
    public ApiResult<List<OrganizationEntity>> listUsedOrganization(@RequestParam Long uuid) {
        List<OrganizationEntity> organizationEntities = orgUnitService.listUsedOrganizationByUuid(uuid);
        return ApiResult.success(organizationEntities);
    }


//    @ApiOperation(value = "单位ID查重", notes = "单位ID查重")
//    @GetMapping("/unit/existById")
//    public ApiResult<Boolean> enable(@RequestParam String id) {
//        OrgUnitEntity orgUnitEntity = orgUnitService.getById(id);
//        return ApiResult.success(orgUnitEntity != null);
//    }

    @ApiOperation(value = "保存组织", notes = "保存组织")
    @PostMapping("/organization/save")
    public ApiResult<String> save(@RequestBody OrganizationEntity entity) {
        Long uuid = organizationService.saveOrg(entity);
        return ApiResult.success(uuid.toString());
    }


    @ApiOperation(value = "查询组织详情", notes = "查询组织详情")
    @GetMapping("/organization/details/{uuid}")
    public ApiResult<OrganizationEntity> orgDetails(@PathVariable("uuid") Long uuid) {
        OrganizationEntity organizationEntity = organizationService.getOne(uuid);
        if (organizationEntity != null) {
            organizationEntity.setI18ns(orgElementI18nService.getOrgElementI18ns(organizationEntity.getUuid()));
        }
        return ApiResult.success(organizationEntity);
    }

    @ApiOperation(value = "启用组织", notes = "启用组织")
    @GetMapping("/organization/enable")
    public ApiResult<Boolean> orgEnable(@RequestParam Long uuid, @RequestParam(required = false) Boolean enable) {
        organizationService.enable(uuid, enable);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "获取系统默认组织", notes = "获取系统默认组织")
    @GetMapping("/organization/getDefaultOrgBySystem")
    public ApiResult<OrganizationEntity> getDefaultOrgBySystem(@RequestParam(name = "system", required = false) String system) {
        return ApiResult.success(organizationService.getDefaultOrgBySystem(StringUtils.defaultIfBlank(system, RequestSystemContextPathResolver.system())));
    }

    @ApiOperation(value = "查询所有启用的组织列表", notes = "查询所有启用的组织列表")
    @GetMapping("/organization/queryEnableOrgs")
    public ApiResult<List<OrganizationEntity>> queryEnableOrgs(@RequestParam(required = false) String system,
                                                               @RequestParam(required = false) Boolean fetchBizOrg) {
        List<OrganizationEntity> list = organizationService.listEnabledBySystem(StringUtils.defaultIfBlank(system, RequestSystemContextPathResolver.system()));
        Date currentTime = Calendar.getInstance().getTime();
        list = list.stream().filter(entity -> {
            Date expireTime = entity.getExpireTime();
            if (expireTime != null && BooleanUtils.isFalse(entity.getExpired())
                    && BooleanUtils.isFalse(entity.getNeverExpire()) && expireTime.before(currentTime)) {
                return false;
            }
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                OrgElementI18nEntity i18n = orgElementI18nService.getOrgElementI18n(entity.getUuid(), "name", LocaleContextHolder.getLocale().toString());
                if (i18n != null) {
                    entity.setName(i18n.getContent());
                }
            }
            return true;
        }).collect(Collectors.toList());
        if (BooleanUtils.isTrue(fetchBizOrg)) {
            for (OrganizationEntity entity : list) {
                List<BizOrganizationEntity> bizOrganizationEntities = bizOrganizationService.getValidBizOrgByOrgUuid(entity.getUuid());
                if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString()) && CollectionUtils.isNotEmpty(bizOrganizationEntities)) {
                    for (BizOrganizationEntity bizOrg : bizOrganizationEntities) {
                        OrgElementI18nEntity i18n = orgElementI18nService.getOrgElementI18n(bizOrg.getUuid(), "name", LocaleContextHolder.getLocale().toString());
                        if (i18n != null) {
                            bizOrg.setName(i18n.getContent());
                        }
                    }
                }
                entity.setBizOrgs(bizOrganizationEntities);


            }
        }
        return ApiResult.success(list);
    }

    @ApiOperation(value = "查询所有的组织列表", notes = "查询所有的组织列表")
    @GetMapping("/organization/queryOrgs")
    public ApiResult<List<OrganizationEntity>> queryOrgs(@RequestParam(required = false) String system,
                                                         @RequestParam(required = false) Boolean fetchBizOrg) {
        List<OrganizationEntity> list = organizationService.listBySystem(StringUtils.defaultIfBlank(system, RequestSystemContextPathResolver.system()));
        list.forEach(entity -> {
            Date currentTime = Calendar.getInstance().getTime();
            Date expireTime = entity.getExpireTime();
            if (expireTime != null && BooleanUtils.isFalse(entity.getExpired())
                    && BooleanUtils.isFalse(entity.getNeverExpire()) && expireTime.before(currentTime)) {
                entity.setExpired(true);
            }
        });
        if (BooleanUtils.isTrue(fetchBizOrg)) {
            for (OrganizationEntity entity : list) {
                entity.setBizOrgs(bizOrganizationService.listBizOrgByOrgId(entity.getId()));
            }
        }
        return ApiResult.success(list);
    }

    @ApiOperation(value = "查询所有启用的组织列表", notes = "查询所有启用的组织列表")
    @PostMapping("/organization/queryEnableOrgUnderSystems")
    public ApiResult<List<OrganizationEntity>> queryEnableOrgs(@RequestBody List<String> system) {
        return ApiResult.success(organizationService.listEnabledBySystem(system));
    }

    @ApiOperation(value = "删除组织", notes = "删除组织")
    @GetMapping("/organization/delete")
    public ApiResult<Boolean> deleteOrg(@RequestParam Long uuid) {
        organizationService.deleteOrg(uuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "设为默认", notes = "设为默认")
    @GetMapping("/organization/setDefault")
    public ApiResult<Boolean> setDefault(@RequestParam Long uuid) {
        organizationService.setDefault(uuid, true);
        return ApiResult.success(true);
    }


    @ApiOperation(value = "查询组织单元详情", notes = "查询组织单元详情")
    @GetMapping("/organization/version/orgElementDetails/{uuid}")
    public ApiResult<OrgElementDto> orgElementDetails(@PathVariable("uuid") Long uuid) {
        return ApiResult.success(orgElementService.getDetails(uuid));
    }

    @ApiOperation(value = "保存组织单元实例", notes = "保存组织单元实例")
    @PostMapping("/organization/version/saveOrgElement")
    public ApiResult<OrgElementDto> saveOrgElement(@RequestBody OrgElementDto orgElementDto) {
        Long uuid = null;
        try {
            uuid = orgElementService.saveOrgElement(orgElementDto);
        } catch (Exception e) {
            logger.error("保存组织单元实例异常: ", e);
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success(orgElementService.getDetails(uuid));
    }

    @ApiOperation(value = "更新组织单元实例关联的用户", notes = "更新组织单元实例关联的用户")
    @GetMapping("/organization/updateOrgUserUnderOrgElement")
    public ApiResult<Boolean> updateOrgUserUnderOrgElement(@RequestParam Long orgElementUuid) {
        orgElementService.updateOrgUserUnderOrgElement(orgElementUuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "保存组织节点顺序", notes = "保存组织节点顺序")
    @PostMapping("/organization/version/updateOrgElementSeq")
    public ApiResult<Void> updateOrgElementSeq(@RequestBody List<OrgElementDto> list) {
        try {
            orgElementService.updateOrgElementSeq(list);
        } catch (Exception e) {
            logger.error("保存组织单元实例异常: ", e);
            return ApiResult.fail(e.getMessage());
        }
        return ApiResult.success();
    }


    @ApiOperation(value = "删除组织单元实例", notes = "删除组织单元实例")
    @GetMapping("/organization/version/deleteOrgElement")
    public ApiResult<Boolean> deleteOrgElement(@RequestParam Long uuid) {
        orgElementService.deleteOrgElement(uuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "创建新版本", notes = "创建新版本")
    @GetMapping("/organization/version/new")
    public ApiResult<OrgVersionEntity> newVersion(@RequestParam(required = false) Long uuid, @RequestParam(required = false) boolean copyVersion,
                                                  @RequestParam(required = false) boolean copyUser) {
        // 判断是否已经存在设计版，如果存在则不允许再次创建
        OrgVersionEntity entity = orgVersionService.getOne(uuid);
        OrgVersionEntity existVersion = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.DESIGNING, entity.getOrgUuid());
        if (existVersion != null) {
            return ApiResult.fail(existVersion, "已存在设计版的组织版本");// 已存在组织版本UUID返回
        }
        if (copyVersion) {
            Long newOrgVUuid = orgVersionService.saveCopyByUuid(uuid, copyUser);
            return ApiResult.success(orgVersionService.getOne(newOrgVUuid));
        } else {
            OrgVersionEntity newVersion = orgVersionService.createEmptyOrgVersionFromOrgVersion(entity, copyUser);
            return ApiResult.success(newVersion);
        }
    }

    @ApiOperation(value = "删除版本", notes = "删除版本")
    @GetMapping("/organization/version/delete")
    public ApiResult<Boolean> delVersion(@RequestParam Long uuid) {
        orgVersionService.deleteOrgVersion(uuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "获取所有版本", notes = "获取所有版本")
    @GetMapping("/organization/allVersion")
    public ApiResult<List<OrgVersionEntity>> delVersion(@RequestParam(required = false) Long orgUuid, @RequestParam(required = false) String orgId) {
        if (orgUuid == null && orgId == null) {
            return ApiResult.success(Collections.EMPTY_LIST);
        }
        List<OrgVersionEntity> versionEntities = orgUuid != null ? orgVersionService.listAllByOrgUuid(orgUuid) : orgVersionService.listAllByOrgId(orgId);
        return ApiResult.success(versionEntities);
    }

    @ApiOperation(value = "发布版本", notes = "发布版本")
    @GetMapping("/organization/version/publish")
    public ApiResult<Boolean> publishVersion(@RequestParam Long uuid) {

        orgVersionService.updatePublished(uuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "设置版本发布时间", notes = "设置版本发布时间")
    @PostMapping("/organization/version/setPublishTime")
    public ApiResult<Boolean> setPublishTime(@RequestBody OrgVersionEntity versionEntity) {
        orgVersionService.updatePublishTime(versionEntity.getUuid(), versionEntity.getPublishTime());
        return ApiResult.success(true);
    }

    @ApiOperation(value = "获取组织的正式组织版本", notes = "获取组织的正式组织版本")
    @GetMapping("/organization/version/published")
    public ApiResult<OrgVersionDto> getOrgPublishedVersion(@RequestParam(required = false, name = "orgUuid") Long orgUuid,
                                                           @RequestParam(required = false, name = "orgId") String orgId,
                                                           @RequestParam(required = false, name = "system") String system,
                                                           @RequestParam(required = false) Boolean fetchBizOrg) {
        OrganizationEntity entity = orgUuid == null && orgId == null
                ? organizationService.getDefaultOrgBySystem(StringUtils.defaultIfBlank(system, RequestSystemContextPathResolver.system())) : (
                orgUuid != null ? organizationService.getOne(orgUuid) : organizationService.getById(orgId)
        );
        OrgVersionDto dto = new OrgVersionDto();
        if (entity != null) {
            OrgVersionEntity versionEntity = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, entity.getUuid());
            BeanUtils.copyProperties(versionEntity, dto);
            dto.setOrganization(entity);
            if (BooleanUtils.isTrue(fetchBizOrg)) {
                List<BizOrganizationEntity> bizOrganizationEntities = bizOrganizationService.getValidBizOrgByOrgUuid(entity.getUuid());
                if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString()) && CollectionUtils.isNotEmpty(bizOrganizationEntities)) {
                    for (BizOrganizationEntity bizOrg : bizOrganizationEntities) {
                        OrgElementI18nEntity i18n = orgElementI18nService.getOrgElementI18n(bizOrg.getUuid(), "name", LocaleContextHolder.getLocale().toString());
                        if (i18n != null) {
                            bizOrg.setName(i18n.getContent());
                        }
                    }
                }
                entity.setBizOrgs(bizOrganizationEntities);
            }
            return ApiResult.success(dto);
        }
        return ApiResult.success(null);
    }

    @ApiOperation(value = "根据组织版本ID列表获取组织版本信息", notes = "获取组织版本列表")
    @GetMapping("/organization/version/listByOrgVersionIds")
    public ApiResult<List<OrgVersionQueryItem>> listByOrgVersionIds(@RequestParam(name = "orgVersionIds") List<String> orgVersionIds,
                                                                    @RequestParam(required = false) Boolean fetchBizOrg) {
        List<OrgVersionQueryItem> orgVersionQueryItems = orgVersionService.listItemByOrgVersionIds(orgVersionIds);
        for (OrgVersionQueryItem entity : orgVersionQueryItems) {
            if (BooleanUtils.isTrue(fetchBizOrg)) {
                List<BizOrganizationEntity> bizOrganizationEntities = bizOrganizationService.getValidBizOrgByOrgUuid(entity.getOrgUuid());
                if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString()) && CollectionUtils.isNotEmpty(bizOrganizationEntities)) {
                    for (BizOrganizationEntity bizOrg : bizOrganizationEntities) {
                        OrgElementI18nEntity i18n = orgElementI18nService.getOrgElementI18n(bizOrg.getUuid(), "name", LocaleContextHolder.getLocale().toString());
                        if (i18n != null) {
                            bizOrg.setName(i18n.getContent());
                        }
                    }
                }
                entity.setBizOrgs(bizOrganizationEntities);
            }
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                OrgElementI18nEntity i18n = orgElementI18nService.getOrgElementI18n(entity.getOrgUuid(), "name", LocaleContextHolder.getLocale().toString());
                if (i18n != null) {
                    entity.setOrgName(i18n.getContent());
                }
            }
        }
        return ApiResult.success(orgVersionQueryItems);
    }

    @ApiOperation(value = "根据组织版本UUID获取组织名称", notes = "获取组织名称")
    @GetMapping("/organization/version/getOrgNameByVersionUuid")
    public ApiResult<String> getOrgNameByVersionUuid(@RequestParam(required = false, name = "orgVersionUuid") Long orgVersionUuid) {
        OrgVersionEntity orgVersionEntity = orgVersionService.getOne(orgVersionUuid);
        if (orgVersionEntity == null) {
            return ApiResult.success(StringUtils.EMPTY);
        }
        OrganizationEntity organizationEntity = organizationService.getOne(orgVersionEntity.getOrgUuid());
        return ApiResult.success(organizationEntity != null ? organizationEntity.getName() : StringUtils.EMPTY);
    }

    @ApiOperation(value = "根据组织ID获取组织版本ID", notes = "获取组织版本ID")
    @GetMapping("/organization/version/getOrgVersionIdByOrgId")
    public ApiResult<String> getOrgVersionIdByOrgId(@RequestParam(name = "orgId") String orgId) {
        OrgVersionEntity orgVersionEntity = orgVersionService.getOrgVersionByOrgId(orgId);
        return ApiResult.success(orgVersionEntity != null ? orgVersionEntity.getId() : null);
    }

    @ApiOperation(value = "版本详情", notes = "版本详情")
    @GetMapping("/organization/version/details")
    public ApiResult<OrgVersionDto> versionDetails(@RequestParam(required = false, name = "uuid") Long uuid, @RequestParam(required = false, name = "orgUuid") Long orgUuid) {
        OrgVersionDto dto = null;
        if (uuid != null) {
            dto = orgVersionService.getDetailsByUuid(uuid);
        } else if (orgUuid != null) {
            // 查询正式版本UUID
            OrgVersionEntity versionEntity = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, orgUuid);
            dto = orgVersionService.getDetailsByUuid(versionEntity.getUuid());
        }
        return ApiResult.success(dto);
    }

    @ApiOperation(value = "版本详情", notes = "版本详情")
    @GetMapping("/organization/version/byId")
    public ApiResult<OrgVersionEntity> versionDetails(@RequestParam String id, @RequestParam(required = false) Boolean fetchBizOrg) {
        OrgVersionEntity orgVersionEntity = orgVersionService.getById(id);
        if (BooleanUtils.isTrue(fetchBizOrg)) {
            OrganizationEntity organizationEntity = organizationService.getOne(orgVersionEntity.getOrgUuid());
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                OrgElementI18nEntity i18n = orgElementI18nService.getOrgElementI18n(organizationEntity.getUuid(), "name", LocaleContextHolder.getLocale().toString());
                if (i18n != null) {
                    organizationEntity.setName(i18n.getContent());
                }
            }
            List<BizOrganizationEntity> bizOrganizationEntities = bizOrganizationService.getValidBizOrgByOrgUuid(organizationEntity.getUuid());
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString()) && CollectionUtils.isNotEmpty(bizOrganizationEntities)) {
                for (BizOrganizationEntity bizOrg : bizOrganizationEntities) {
                    OrgElementI18nEntity i18n = orgElementI18nService.getOrgElementI18n(bizOrg.getUuid(), "name", LocaleContextHolder.getLocale().toString());
                    if (i18n != null) {
                        bizOrg.setName(i18n.getContent());
                    }
                }
            }
            organizationEntity.setBizOrgs(bizOrganizationEntities);
            orgVersionEntity.setOrganization(organizationEntity);
        }
        return ApiResult.success(orgVersionEntity);
    }

    @ApiOperation(value = "组织角色详情", notes = "组织角色详情")
    @GetMapping("/organization/version/roleDetails")
    public ApiResult<OrgRoleEntity> roleDetails(@RequestParam Long uuid) {
        return ApiResult.success(orgRoleService.getOne(uuid));
    }

    @ApiOperation(value = "组织角色列表", notes = "组织角色列表")
    @GetMapping("/organization/version/listRole")
    public ApiResult<List<OrgRoleEntity>> listRole(@RequestParam(name = "orgVersionUuid") Long orgVersionUuid) {
        return ApiResult.success(orgRoleService.listByOrgVersionUuid(orgVersionUuid));
    }

    @ApiOperation(value = "新增组织角色成员", notes = "新增组织角色成员")
    @PostMapping("/organization/version/addOrgRoleMember")
    public ApiResult addOrgRoleMember(@RequestBody List<OrgElementRoleMemberEntity> list) {
        orgElementRoleMemberService.addOrgRoleMember(list);
        return ApiResult.success();
    }

    @ApiOperation(value = "删除组织角色成员", notes = "删除组织角色成员")
    @PostMapping("/organization/version/removeOrgRoleMember/{orgVersionUuid}")
    public ApiResult removeOrgRoleMember(@RequestBody List<String> userIds, @PathVariable Long orgVersionUuid) {
        orgElementRoleMemberService.removeOrgRoleMember(userIds, orgVersionUuid);
        return ApiResult.success();
    }


    @ApiOperation(value = "新增组织角色", notes = "新增组织角色")
    @PostMapping("/organization/version/saveOrgRole")
    public ApiResult<Long> saveOrgRole(@RequestBody OrgRoleEntity entity) {
        return ApiResult.success(orgRoleService.saveOrgRole(entity));
    }

    @ApiOperation(value = "删除组织角色", notes = "删除组织角色")
    @GetMapping("/organization/version/deleteRole")
    public ApiResult<Boolean> deleteRole(@RequestParam Long uuid) {
        orgRoleService.deleteOrgRole(uuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "组织角色ID是否重复", notes = "组织角色ID是否重复")
    @GetMapping("/organization/version/existRoleId")
    public ApiResult<Boolean> existRoleId(@RequestParam String id, @RequestParam Long orgVersionUuid, @RequestParam(required = false) Long uuid) {
        boolean exist = orgRoleService.existRoleIdExcludeUuid(id, orgVersionUuid, uuid);
        return ApiResult.success(exist);
    }

    @ApiOperation(value = "统计组织版本下的用户数据", notes = "统计组织版本下的用户数据")
    @GetMapping("/organization/version/countUserCountUnderOrgElement")
    public ApiResult<Long> countUserCountUnderOrgElement(@RequestParam Long orgVersionUuid, @RequestParam String orgElementId) {
        Long total = this.orgUserService.countOrgElementUserByOrgVersionUuid(orgElementId, orgVersionUuid);
        return ApiResult.success(total);
    }

    @ApiOperation(value = "统计组织版本下的用户数据", notes = "统计组织版本下的用户数据")
    @GetMapping("/organization/version/countUserCountUnderOrgRole")
    public ApiResult<Long> countUserCountUnderOrgRole(@RequestParam Long orgVersionUuid, @RequestParam Long orgRoleUuid) {
        return ApiResult.success(this.orgElementRoleMemberService.countOrgRoleUserByOrgVersionUuid(orgRoleUuid, orgVersionUuid));
    }


    @ApiOperation(value = "统计组织版本下的用户数据", notes = "统计组织版本下的用户数据")
    @GetMapping("/organization/version/countAllUserCount")
    public ApiResult<Long> countAllUserCount(@RequestParam Long orgVersionUuid) {
        Long total = this.orgUserService.countUserByOrgVersionUuid(orgVersionUuid);
        return ApiResult.success(total);
    }

    @ApiOperation(value = "统计系统下的用户数据", notes = "统计系统下的用户数据")
    @GetMapping("/organization/countAllUserCountUnderSystem")
    public ApiResult<Long> countAllUserCountUnderSystem(@RequestParam(required = false) String system) {
        Long total = this.orgUserService.countUserBySystemAndTenant(StringUtils.defaultIfBlank(system, RequestSystemContextPathResolver.system()), SpringSecurityUtils.getCurrentTenantId());
        return ApiResult.success(total);
    }

    @ApiOperation(value = "获取组织树数据", notes = "获取组织树数据")
    @PostMapping("/organization/fetchOrgTree/{type}")
    public ApiResult<List<OrgSelectProvider.Node>> fetchOrgTree(@PathVariable("type") String type, @RequestBody OrgSelectProvider.Params params,
                                                                HttpServletResponse response) {
        List<OrgSelectProvider.Node> nodes = orgElementService.getTree(type, params);
        if (params.containsKey("FORCE_ASYNC")) {
            response.setHeader("force-query-async", "true");
        }
        if (params.containsKey("FORCE_USER_QUERY_ASYNC")) {
            response.setHeader("force-user-query-async", "true");
        }
        return ApiResult.success(nodes);
    }


    @ApiOperation(value = "获取组织树用户数据", notes = "获取组织树用户数据")
    @PostMapping("/organization/fetchOrgTreeUser/{type}")
    public ApiResult<OrgSelectProvider.PageNode> fetchOrgTreeUser(@PathVariable("type") String type, @RequestBody OrgSelectProvider.Params params, HttpServletResponse response) {
        return ApiResult.success(orgElementService.getTreeUserNodes(type, params));
    }

    @ApiOperation(value = "通过指定值集合获取组织节点数据", notes = "通过指定值集合获取组织节点数据")
    @PostMapping("/organization/fetchOrgTreeNodesByKeys/{type}")
    public ApiResult<List<OrgSelectProvider.Node>> fetchOrgTreeNodesByKeys(@PathVariable("type") String type, @RequestBody OrgSelectProvider.Params params) {
        return ApiResult.success(orgElementService.getTreeNodesByKeys(type, params));
    }

    @ApiOperation(value = "通过指定值集合获取组织节点数据", notes = "通过指定值集合获取组织节点数据")
    @PostMapping("/organization/fetchOrgTreeNodesByTypeKeys")
    public ApiResult<List<OrgSelectProvider.Node>> fetchOrgTreeNodesByTypeKeys(@RequestBody OrgSelectProvider.Params params) {
        return ApiResult.success(orgElementService.getTreeNodesByTypeKeys(params));
    }


    @ApiOperation(value = "通过指定值集合获取组织节点数据", notes = "通过指定值集合获取组织节点数据")
    @PostMapping("/organization/fetchOrgTreeNodesByKeys")
    public ApiResult<List<OrgSelectProvider.Node>> fetchOrgTreeNodesByKeys(@RequestBody OrgSelectProvider.Params params) {
        return ApiResult.success(orgElementService.getTreeNodesByKeys(null, params));
    }

    @ApiOperation(value = "通过指定标题集合获取组织节点数据", notes = "通过指定标题集合获取组织节点数据")
    @PostMapping("/organization/getTreeNodesByTitles/{type}")
    public ApiResult<List<OrgSelectProvider.Node>> getTreeNodesByTitles(@PathVariable("type") String type,
                                                                        @RequestBody OrgSelectProvider.Params params) {
        return ApiResult.success(orgElementService.getTreeNodesByTitles(type, params));
    }

    @ApiOperation(value = "导入组织", notes = "导入组织")
    @PostMapping("/organization/import/{orgVersionUuid}")
    public ApiResult importOrgConstruct(@PathVariable Long orgVersionUuid, MultipartHttpServletRequest request
            , @RequestParam(required = false) String[] types) {
        try {
            MultipartFile multifile = request.getFile("file");
            OrgConstructImportListener listener = new OrgConstructImportListener(orgVersionUuid, types);
            EasyExcel.read(multifile.getInputStream(), OrgElementExcelDo.class, listener).doReadAll();
            listener.finish();
            // 处理导出结果
            return ApiResult.success(listener.getOrgElementExcelDoMap());
        } catch (Exception e) {
            logger.error("导入组织异常：{}", Throwables.getStackTraceAsString(e));
            return ApiResult.fail();
        }
    }


    @ApiOperation(value = "查询组织群组详情", notes = "查询组织群组详情")
    @GetMapping("/organization/orgGroup/details/{uuid}")
    public ApiResult<OrgGroupDto> orgGroupDetails(@PathVariable("uuid") Long uuid, @RequestParam(required = false) Boolean fetchI18ns) {
        OrgGroupDto dto = orgGroupService.getOrgGroupDetails(uuid);
        if (BooleanUtils.isTrue(fetchI18ns) && dto != null) {
            dto.setI18ns(orgElementI18nService.getOrgElementI18ns(uuid));
        }
        return ApiResult.success(dto);
    }

    @ApiOperation(value = "保存组织群组", notes = "查询组织群组详情")
    @PostMapping("/organization/orgGroup/save")
    public ApiResult<Long> saveOrgGroup(@RequestBody OrgGroupDto dto) {
        return ApiResult.success(orgGroupService.saveOrgGroup(dto));
    }


    @ApiOperation(value = "删除组织群组", notes = "删除组织群组")
    @GetMapping("/organization/orgGroup/delete")
    public ApiResult<Boolean> deletOrgGroup(@RequestParam Long uuid) {
        orgGroupService.deleteOrgGroup(uuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "获取角色关联的群组", notes = "删除组织群组")
    @GetMapping("/organization/orgGroup/getRoleRelaGroups")
    public ApiResult<List<OrgGroupEntity>> getRoleRelaGroups(@RequestParam String roleUuid,
                                                             @RequestParam(required = false) String system, @RequestParam(required = false) String tenant) {
        List<OrgGroupEntity> orgGroupEntities = orgGroupService.getRoleRelaGroups(roleUuid, StringUtils.defaultIfBlank(system, RequestSystemContextPathResolver.system()), StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()));
        return ApiResult.success(orgGroupEntities);
    }

    @ApiOperation(value = "查询协作系统分类详情", notes = "查询协作系统分类详情")
    @GetMapping("/organization/orgPartnerSysCategory/details/{uuid}")
    public ApiResult<OrgPartnerSysCategoryEntity> orgPartnerSysCategory(@PathVariable("uuid") Long uuid) {
        return ApiResult.success(orgPartnerSysCategoryService.getOne(uuid));
    }

    @ApiOperation(value = "保存协作系统分类", notes = "保存协作系统分类")
    @PostMapping("/organization/orgPartnerSysCategory/save")
    public ApiResult<Long> saveOrgPartnerSysCategory(@RequestBody OrgPartnerSysCategoryEntity entity) {
        return ApiResult.success(orgPartnerSysCategoryService.saveOrgPartnerSysCategory(entity));
    }


    @ApiOperation(value = "删除协作系统分类", notes = "删除协作系统分类")
    @GetMapping("/organization/orgPartnerSysCategory/delete")
    public ApiResult<Boolean> delOrgPartnerSysCategory(@RequestParam(required = false) Long uuid) {
        orgPartnerSysCategoryService.deleteOrgPartnerSysCategory(uuid);
        return ApiResult.success(true);
    }

    @ApiOperation(value = "查询协作系统分类", notes = "查询协作系统分类")
    @GetMapping("/organization/orgPartnerSysCategory/list")
    public ApiResult<List<OrgPartnerSysCategoryEntity>> listOrgPartnerSysCategory(@RequestParam(required = false) String system) {
        return ApiResult.success(orgPartnerSysCategoryService.listBySystemAndTenant(StringUtils.defaultString(system, RequestSystemContextPathResolver.system()), SpringSecurityUtils.getCurrentTenantId()));
    }


    @ApiOperation(value = "统计待确认的协作系统申请", notes = "统计待确认的协作系统申请")
    @GetMapping("/organization/orgPartnerSysApply/countWaitConfirmApply")
    public ApiResult<Long> countWaitConfirmApply(@RequestParam(required = false) String system) {
        Long num = orgPartnerSysApplyService.countByStateAndSystemAndTenant(OrgPartnerSysApplyEntity.State.WAIT_CONFIRM,
                StringUtils.defaultString(system, RequestSystemContextPathResolver.system()), SpringSecurityUtils.getCurrentTenantId());
        return ApiResult.success(num);
    }

    @ApiOperation(value = "添加协作系统", notes = "添加协作系统")
    @PostMapping("/organization/orgPartnerSysApply/addApply")
    public ApiResult<Long> addOrgPartnerSysApply(@RequestBody OrgPartnerSysApplyEntity entity) {
        return ApiResult.success(orgPartnerSysApplyService.addOrgPartnerSysApply(entity));
    }

    @ApiOperation(value = "更新协作系统状态", notes = "更新协作系统状态")
    @GetMapping("/organization/orgPartnerSysApply/updateState")
    public ApiResult<Boolean> addOrgPartnerSysApply(@RequestParam Long uuid, @RequestParam String state) {
        orgPartnerSysApplyService.updateOrgPartnerSysApplyState(uuid, OrgPartnerSysApplyEntity.State.valueOf(state));
        return ApiResult.success(true);
    }

    @ApiOperation(value = "根据ID列表获取对应的名称", notes = "根据ID列表获取对应的名称")
    @PostMapping("/organization/element/getNameByOrgEleIds")
    public ApiResult<Map<String, String>> getNameByOrgEleIds(@RequestBody List<String> eleIds) {
        return ApiResult.success(orgFacadeService.getNameByOrgEleIds(eleIds));
    }

    @ApiOperation(value = "根据ID列表获取对应的名称路径", notes = "根据ID列表获取对应的名称路径")
    @PostMapping("/organization/element/getNamePathByOrgEleIds")
    public ApiResult<Map<String, String>> getNamePathByOrgEleIds(@RequestBody List<String> eleIds) {
        return ApiResult.success(orgFacadeService.getNamePathByOrgEleIds(eleIds));
    }

    @ApiOperation(value = "添加用户到指定组织节点", notes = "添加用户到指定组织节点")
    @PostMapping("/organization/joinOrgUser")
    public void addUserToOrgVersion(@RequestBody OrgUserElementDto orgUserAddition) {
        orgUserService.addUser(orgUserAddition);
    }

    @ApiOperation(value = "移出用户", notes = "移出用户")
    @PostMapping("/organization/removeOrgUser")
    public void removeUser(@RequestBody OrgUserElementDto orgUserElementDto) {
        orgUserService.removeUser(orgUserElementDto);
    }


    @GetMapping("/organization/version/orgElement/getOrgElementRolePrivilegeTree")
    public @ResponseBody
    ApiResult<List<TreeNode>> getOrgElementRolePrivilegeTree(@RequestParam Long orgElementUuid) {

        try {
            return ApiResult.success(orgElementService.getOrgElementRolePrivilegeTree(orgElementUuid));
        } catch (Exception e) {
            logger.error("查询组织元素的角色权限树异常:", e);
        } finally {
            TreeNode.TreeContextHolder.remove();
        }
        return ApiResult.success(null);
    }

    @GetMapping("/organization/version/user/getOrgUserRolePrivilegeTree")
    public @ResponseBody
    ApiResult<List<TreeNode>> getOrgUserRolePrivilegeTree(@RequestParam String userId, @RequestParam(required = false) Long orgVersionUuid) {
        try {
            return ApiResult.success(orgUserService.getOrgUserRolePrivilegeTree(userId, orgVersionUuid));
        } catch (Exception e) {
            logger.error("查询组织用户的角色权限树异常:", e);
        } finally {
            TreeNode.TreeContextHolder.remove();
        }
        return ApiResult.success(null);
    }

    @GetMapping("/organization/version/getOrgElementsRelaRole")
    public ApiResult<List<OrgElementEntity>> getRoleOrgElementMember(@RequestParam String roleUuid, @RequestParam(required = false) String system, @RequestParam(required = false) String tenant) {
        return ApiResult.success(orgElementService.getRoleOrgElementMemberByRoleSystemTenant(roleUuid, StringUtils.defaultIfBlank(system, RequestSystemContextPathResolver.system()), StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId())));
    }

    @GetMapping("/organization/user/getRoleRelaUsers")
    public ApiResult<List<UserInfoEntity>> getRoleRelaUsers(@RequestParam String roleUuid,
                                                            @RequestParam(required = false) String system, @RequestParam(required = false) String tenant) {
        return ApiResult.success(orgUserService.getRoleRelaUsers(roleUuid, StringUtils.defaultIfBlank(system, RequestSystemContextPathResolver.system()), StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId())));
    }

    @PostMapping("/biz/saveBizOrg")
    public ApiResult<Long> saveBizOrg(@RequestBody BizOrganizationEntity bizOrganizationEntity) {
        return ApiResult.success(bizOrganizationService.saveBizOrg(bizOrganizationEntity));
    }

    @GetMapping("/biz/syncBizOrgByOrgUuid")
    public ApiResult<Void> syncBizOrgByOrgUuid(@RequestParam(required = true) Long orgUuid) {
        try {
            List<BizOrganizationEntity> bizOrganizationEntities = bizOrganizationService.getBizOrgByOrgUuid(orgUuid);
            if (CollectionUtils.isNotEmpty(bizOrganizationEntities)) {
                for (BizOrganizationEntity entity : bizOrganizationEntities) {
                    try {
                        bizOrganizationService.syncBizOrg(entity.getUuid());
                    } catch (Exception e) {
                        logger.error("同步组织异常", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("业务组织同步异常: {}", Throwables.getStackTraceAsString(e));
            if (e instanceof LockedException) {
                return ApiResult.build(1, "同步中", null);
            } else if (e instanceof BusinessException) {
                return ApiResult.build(2, e.getMessage(), null);
            }
            return ApiResult.fail("同步异常");
        }
        return ApiResult.success();
    }

    @GetMapping("/biz/syncBizOrg")
    public ApiResult<Void> syncBizOrg(@RequestParam(required = true) Long uuid) {
        try {
            bizOrganizationService.syncBizOrg(uuid);
        } catch (Exception e) {
            logger.error("业务组织同步异常: {}", Throwables.getStackTraceAsString(e));
            if (e instanceof LockedException) {
                return ApiResult.build(1, "同步中", null);
            } else if (e instanceof BusinessException) {
                return ApiResult.build(2, e.getMessage(), null);
            }
            return ApiResult.fail("同步异常");
        }
        return ApiResult.success();
    }

    @GetMapping("/biz/getValidBizOrg")
    public ApiResult<List<BizOrganizationEntity>> getValidBizOrg(@RequestParam(required = false) Long orgUuid) {
        return ApiResult.success(bizOrganizationService.getValidBizOrgByOrgUuid(orgUuid));
    }

    @GetMapping("/biz/listBizOrgByOrgId")
    public ApiResult<List<BizOrganizationEntity>> listBizOrgByOrgId(@RequestParam(name = "orgId") String orgId) {
        return ApiResult.success(bizOrganizationService.listBizOrgByOrgId(orgId));
    }

    @GetMapping("/biz/listValidBizOrgByOrgId")
    public ApiResult<List<BizOrganizationEntity>> listValidBizOrgByOrgId(@RequestParam(name = "orgId") String orgId) {
        return ApiResult.success(bizOrganizationService.listValidBizOrgByOrgId(orgId));
    }

    @GetMapping("/biz/getBizOrgByUuid")
    public ApiResult<BizOrganizationEntity> getBizOrgByUuid(@RequestParam Long uuid) {
        BizOrganizationEntity entity = bizOrganizationService.getOne(uuid);
        if (BooleanUtils.isFalse(entity.getNeverExpire()) && entity.getExpireTime() != null) {
            entity.setExpired(DateUtils.addDays(entity.getExpireTime(), 1).compareTo(new Date()) < 0);
        }
        OrganizationEntity organizationEntity = organizationService.getOne(entity.getOrgUuid());
        if (organizationEntity != null) {
            entity.setOrgName(organizationEntity.getName());
        }
        entity.setI18ns(orgElementI18nService.getOrgElementI18ns(entity.getUuid()));
        return ApiResult.success(entity);
    }

    @PostMapping("/biz/deleteBizOrg")
    public ApiResult<Void> deleteBizOrg(@RequestBody List<Long> uuids) {
        bizOrganizationService.deleteBizOrg(uuids);
        return ApiResult.success();
    }

    @PostMapping("/biz/saveBizOrgRoleTemplate")
    public ApiResult<Void> saveBizOrgRoleTemplate(@RequestBody BizOrgRoleTemplateEntity templateEntity) {
        bizOrganizationService.saveBizOrgRoleTemplate(templateEntity);
        return ApiResult.success();
    }

    @GetMapping("/biz/getBizOrgConfig")
    public ApiResult<BizOrgConfigEntity> getBizOrgConfig(@RequestParam Long bizOrgUuid) {
        return ApiResult.success(bizOrganizationService.getBizOrgConfigByBizOrgUuid(bizOrgUuid));
    }

    @PostMapping("/biz/saveBizOrgConfig")
    public ApiResult<Void> saveBizOrgConfig(@RequestBody BizOrgConfigDto dto) {
        bizOrganizationService.saveBizOrgConfig(dto);
        return ApiResult.success();
    }

    @PostMapping("/biz/saveBizOrgDimension")
    public ApiResult<Void> saveBizOrgDimension(@RequestBody BizOrgDimensionEntity entity) {
        bizOrganizationService.saveBizOrgDimension(entity);
        return ApiResult.success();
    }

    @PostMapping("/biz/deleteBizOrgRoleTemplate")
    public ApiResult<Void> deleteBizOrgRoleTemplate(@RequestBody List<Long> uuids) {
        bizOrganizationService.deleteBizOrgRoleTemplate(uuids);
        return ApiResult.success();
    }

    @PostMapping("/biz/deleteBizOrgDimension")
    public ApiResult<Void> deleteBizOrgDimension(@RequestBody List<Long> uuids) {
        bizOrganizationService.deleteBizOrgDimension(uuids);
        return ApiResult.success();
    }

    @GetMapping("/biz/getSystemBizOrgDimensions")
    public ApiResult<List<BizOrgDimensionEntity>> getSystemBizOrgDimensions() {
        return ApiResult.success(bizOrganizationService.getBizOrgDimensionsBySystemAndTenant(RequestSystemContextPathResolver.system(), SpringSecurityUtils.getCurrentTenantId()));
    }

    @GetMapping("/biz/existSystemBizOrgDimensionId")
    public ApiResult<Boolean> existSystemBizOrgDimensionId(@RequestParam String id) {
        return ApiResult.success(bizOrganizationService.getBizOrgDimensionById(id) != null);
    }

    @GetMapping("/biz/existBizOrgRoleIdUnderBizOrg")
    public ApiResult<Boolean> existBizOrgRoleIdUnderBizOrg(@RequestParam String id, @RequestParam Long bizOrgUuid) {
        return ApiResult.success(bizOrganizationService.getBizOrgRoleById(id, bizOrgUuid) != null);
    }

    @GetMapping("/biz/getSystemBizOrgRoleTemplates")
    public ApiResult<List<BizOrgRoleTemplateEntity>> getSystemBizOrgRoleTemplates() {
        return ApiResult.success(bizOrganizationService.getBizOrgRoleTemplateBySystemAndTenant(RequestSystemContextPathResolver.system(), SpringSecurityUtils.getCurrentTenantId()));
    }

    @GetMapping("/biz/getBizOrgRolesByBizOrgUuid")
    public ApiResult<List<BizOrgRoleEntity>> getBizOrgRolesByBizOrgUuid(@RequestParam Long bizOrgUuid, @RequestParam(required = false) Boolean fetchI18ns) {
        List<BizOrgRoleEntity> entities = bizOrganizationService.getBizOrgRolesByBizOrgUuid(bizOrgUuid);
        if (CollectionUtils.isNotEmpty(entities) && BooleanUtils.isTrue(fetchI18ns)) {
            for (BizOrgRoleEntity role : entities) {
                role.setI18ns(orgElementI18nService.getOrgElementI18ns(role.getUuid()));
            }
        }
        return ApiResult.success(entities);
    }

    @GetMapping("/biz/getBizOrgRolesByBizOrgId")
    public ApiResult<List<BizOrgRoleEntity>> getBizOrgRolesByBizOrgUuid(@RequestParam(name = "bizOrgId") String bizOrgId) {
        return ApiResult.success(bizOrganizationService.getBizOrgRolesByBizOrgId(bizOrgId));
    }

    @GetMapping("/biz/getBizOrgDimensionById")
    public ApiResult<BizOrgDimensionEntity> getBizOrgDimensionById(@RequestParam String id) {
        return ApiResult.success(bizOrganizationService.getBizOrgDimensionById(id));
    }

    @GetMapping("/biz/getAllBizOrgElementByBizOrgUuid")
    public ApiResult<List<BizOrgElementEntity>> getAllBizOrgElementByBizOrgUuid(@RequestParam Long bizOrgUuid) {
        return ApiResult.success(bizOrgElementService.getAllByBizOrgUuid(bizOrgUuid));
    }


    @GetMapping("/biz/resetBizOrgElementPathChains")
    public ApiResult<Void> resetBizOrgElementPathChains(@RequestParam(required = false) Long bizOrgUuid) {
        bizOrgElementService.resetBizOrgElementPathChains(bizOrgUuid);
        return ApiResult.success();
    }


    @PostMapping("/biz/resortOrgElements")
    public ApiResult<Void> resortOrgElements(@RequestBody List<Long> uuids) {
        bizOrgElementService.resortOrgElements(uuids);
        return ApiResult.success();
    }

    @GetMapping("/biz/getBizOrgElementByUuid")
    public ApiResult<BizOrgElementEntity> getBizOrgElementByUuid(@RequestParam Long uuid) {
        return ApiResult.success(bizOrgElementService.getOne(uuid));
    }

    @GetMapping("/biz/getBizOrgElementById")
    public ApiResult<BizOrgElementEntity> getBizOrgElementById(@RequestParam String id) {
        return ApiResult.success(bizOrgElementService.getById(id));
    }

    @PostMapping("/biz/saveBizOrgElement")
    public ApiResult<BizOrgElementEntity> saveBizOrgElement(@RequestBody BizOrgElementDto dto) {
        return ApiResult.success(bizOrgElementService.saveBizOrgElement(dto));
    }

    @GetMapping("/biz/deleteBizOrgElementById")
    public ApiResult<Void> deleteBizOrgElementById(@RequestParam String id) {
        bizOrgElementService.deleteBizOrgElementById(id);
        return ApiResult.success();
    }

    @GetMapping("/biz/deleteBizOrgElementByUuid")
    public ApiResult<Void> deleteBizOrgElementByUuid(@RequestParam Long uuid) {
        bizOrgElementService.deleteBizOrgElementByUuid(uuid);
        return ApiResult.success();
    }


    @PostMapping("/biz/addBizOrgElementMember")
    public ApiResult<Void> addBizOrgElementMember(@RequestBody List<BizOrgElementMemberEntity> elementMembers) {
        bizOrgElementMemberService.addBizOrgElementMember(elementMembers);
        return ApiResult.success();
    }

    @RequestMapping(value = "/biz/saveBizOrgElementMember/{bizOrgElementId}", method = RequestMethod.POST)
    public ApiResult<Void> saveBizOrgElementMember(@PathVariable("bizOrgElementId") String bizOrgElementId, @RequestBody List<BizOrgElementMemberEntity> elementMembers) {
        bizOrgElementMemberService.saveBizOrgElementMember(bizOrgElementId, null, elementMembers);
        return ApiResult.success();
    }

    @RequestMapping(value = "/biz/saveBizOrgElementMember/{bizOrgElementId}/{bizOrgRoleId}", method = RequestMethod.POST)
    public ApiResult<Void> saveBizOrgElementMember(@PathVariable("bizOrgElementId") String bizOrgElementId, @PathVariable("bizOrgRoleId") String bizOrgRoleId, @RequestBody List<BizOrgElementMemberEntity> elementMembers) {
        bizOrgElementMemberService.saveBizOrgElementMember(bizOrgElementId, bizOrgRoleId, elementMembers);
        return ApiResult.success();
    }


    @PostMapping("/biz/removeBizOrgElementMember")
    public ApiResult<Void> removeBizOrgElementMember(@RequestBody List<BizOrgElementMemberEntity> elementMembers) {
        bizOrgElementMemberService.removeBizOrgElementMember(elementMembers);
        return ApiResult.success();
    }

    @GetMapping("/biz/getBizOrgElementRelaRoleUuids")
    public ApiResult<List<String>> getBizOrgElementRelaRoleUuids(@RequestParam String bizOrgElementId) {
        List<String> roleUuids = bizOrgElementService.getBizOrgElementRelaRoleUuids(bizOrgElementId);
        return ApiResult.success(roleUuids);
    }

    @GetMapping("/biz/getBizOrgElementRolePrivilegeTree")
    public ApiResult<List<TreeNode>> getBizOrgElementRolePrivilegeTree(@RequestParam String bizOrgElementId) {
        List<TreeNode> nodes = bizOrgElementService.getBizOrgElementRolePrivilegeTree(bizOrgElementId);
        return ApiResult.success(nodes);
    }

    @ApiOperation(value = "获取组织树数据", notes = "获取组织树数据")
    @RequestMapping("/biz/fetchBizOrgTree/{uuid}")
    public ApiResult<List<OrgSelectProvider.Node>> fetchBizOrgTree(@PathVariable("uuid") Long uuid, @RequestBody OrgSelectProvider.Params params,
                                                                   HttpServletResponse response) {
        List<OrgSelectProvider.Node> nodes = bizOrgElementService.getTreeByBizOrgUuid(uuid, params);
        return ApiResult.success(nodes);
    }

    @ApiOperation(value = "获取业务组织树用户数据", notes = "获取业务组织树用户数据")
    @RequestMapping("/biz/fetchBizOrgTreeUser/{uuid}")
    public ApiResult<OrgSelectProvider.PageNode> fetchBizOrgTreeUser(@PathVariable("uuid") Long uuid, @RequestBody OrgSelectProvider.Params params, HttpServletResponse response) {
        return ApiResult.success(bizOrgElementService.getTreeUserNodesByBizOrgUuid(uuid, params));
    }


    @GetMapping("/biz/getUserBizOrgElementRoles")
    public ApiResult getUserBizOrgElementRoles(@RequestParam String userId) {
        List<BizOrgElementMemberDto> bizOrgElementMemberDtos = bizOrgElementMemberService.getDetailsByMemberId(userId, RequestSystemContextPathResolver.system());
        return ApiResult.success(bizOrgElementMemberDtos);
    }

    @ApiOperation(value = "根据业务组织ID获取业务组织UUID", notes = "获取业务组织UUID")
    @GetMapping("/organization/version/getBizOrgUuidByBizOrgId")
    public ApiResult<Long> getBizOrgUuidByBizOrgId(@RequestParam(name = "bizOrgId") String bizOrgId) {
        Long bizOrgUuid = bizOrganizationService.getBizOrgUuidByBizOrgId(bizOrgId);
        return ApiResult.success(bizOrgUuid);
    }


    @GetMapping("/organization/element/getI18ns")
    public ApiResult<List<OrgElementI18nEntity>> getElementI18ns(@RequestParam(required = false) Long uuid, @RequestParam(required = false) String id) {
        return ApiResult.success(
                uuid != null ? orgElementI18nService.getOrgElementI18ns(uuid) : (
                        StringUtils.isNotBlank(id) ? orgElementI18nService.getOrgElementI18ns(id) : null
                )
        );
    }


    @GetMapping("/organization/version/element/translateAll")
    public ApiResult<Void> translateAllElements(@RequestParam Long orgVersionUuid, @RequestParam Boolean onlyTranslateEmpty) {
        orgElementService.translateAllElements(orgVersionUuid, onlyTranslateEmpty);
        return ApiResult.success();
    }

    @GetMapping("/biz/organization/element/translateAll")
    public ApiResult<Void> translateAllBizOrgElements(@RequestParam Long bizOrgUuid, @RequestParam Boolean onlyTranslateEmpty) {
        bizOrgElementService.translateAllElements(bizOrgUuid, onlyTranslateEmpty);
        return ApiResult.success();
    }

    @PostMapping("/user/orgDetails")
    public ApiResult<Map<String, UserSystemOrgDetails>> getUserSystemOrgDetails(
            @RequestParam(required = false) String system,
            @RequestBody List<String> userIds) {
        return ApiResult.success(orgUserService.getUserSystemOrgDetailsMap(userIds, system));
    }

    @GetMapping("/organization/updateOrgElementPathChain")
    public ApiResult<Boolean> updateOrgElementPathChain(
            @RequestParam(required = false) Long orgUuid,
            @RequestParam(required = false) Long orgVersionUuid) {
        orgElementService.updateOrgElementPathChain(orgUuid, orgVersionUuid);
        return ApiResult.success(true);
    }

    @GetMapping("/organization/hasPermissionToEditOrgConstruct")
    public ApiResult<Boolean> hasPermissionToEditOrgConstruct(@RequestParam Long orgUuid) {
        if (SpringSecurityUtils.hasAnyRole(BuildInRole.ROLE_TENANT_ADMIN.name(), BuildInRole.ROLE_ADMIN.name())) {
            return ApiResult.success(true);
        }
        OrganizationEntity org = organizationService.getOne(orgUuid);
        if (org != null) {
            if (StringUtils.isNotBlank(org.getManager()) && org.getManager().contains(SpringSecurityUtils.getCurrentUserId())) {
                return ApiResult.success(true);
            }
            return ApiResult.success(orgElementManagementService.isOrgElementManager(SpringSecurityUtils.getCurrentUserId(), orgUuid));
        }

        return ApiResult.success(false);
    }


    @GetMapping("/organization/listUserOrgElementManagements")
    public ApiResult<List<OrgElementManagementEntity>> listUserOrgElementManagements(@RequestParam Long orgUuid) {
        return ApiResult.success(orgElementManagementService.listUserOrgElementManagement(SpringSecurityUtils.getCurrentUserId(), orgUuid));
    }

}
