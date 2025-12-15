package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.dao.AppSystemInfoDao;
import com.wellsoft.pt.app.dao.impl.AppSystemLoginPolicyDaoImpl;
import com.wellsoft.pt.app.dao.impl.AppSystemPageSettingDaoImpl;
import com.wellsoft.pt.app.dao.impl.AppSystemPageThemeDaoImpl;
import com.wellsoft.pt.app.dao.impl.AppSystemParamDaoImpl;
import com.wellsoft.pt.app.dto.AppPageResourceDto;
import com.wellsoft.pt.app.dto.AppProdVersionDto;
import com.wellsoft.pt.app.dto.AppSystemInfoDto;
import com.wellsoft.pt.app.entity.*;
import com.wellsoft.pt.app.service.*;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.audit.bean.PrivilegeDto;
import com.wellsoft.pt.security.audit.bean.RoleDto;
import com.wellsoft.pt.security.audit.dto.UpdatePrivilegeDto;
import com.wellsoft.pt.security.audit.dto.UpdateRoleMemberDto;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

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
@Service
public class AppSystemInfoServiceImpl extends AbstractJpaServiceImpl<AppSystemInfoEntity, AppSystemInfoDao, Long> implements AppSystemInfoService {

    @Autowired
    AppSystemPageSettingDaoImpl appSystemPageSettingDao;

    @Autowired
    AppSystemLoginPageDefinitionService appSystemLoginPageDefinitionService;

    @Autowired
    AppSystemLoginPolicyDaoImpl appSystemLoginPolicyDao;

    @Autowired
    AppSystemPageThemeDaoImpl appSystemPageThemeDao;

    @Autowired
    AppSystemParamDaoImpl appSystemParamDao;

    @Autowired
    AppPageDefinitionService appPageDefinitionService;
    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    @Autowired
    AppProdVersionService appProdVersionService;

    @Autowired
    AppProdVersionParamService appProdVersionParamService;

    @Autowired
    AppProdVersionSettingService appProdVersionSettingService;

    @Autowired
    SecurityApiFacade securityApiFacade;

    @Autowired
    AppModuleService appModuleService;

    @Autowired
    AppPageResourceService appPageResourceService;

    @Autowired
    PrivilegeFacadeService privilegeFacadeService;

    @Autowired
    RoleFacadeService roleFacadeService;

    @Override
    @Transactional
    public Long saveSystemInfo(AppSystemInfoEntity body) {
        AppSystemInfoEntity entity = body.getUuid() != null ? getOne(body.getUuid()) : new AppSystemInfoEntity();
        if (body.getUuid() == null) {
            if (StringUtils.isNotBlank(body.getTenant()) && StringUtils.isNotBlank(body.getSystem())) {
                AppSystemInfoEntity example = new AppSystemInfoEntity();
                example.setTenant(body.getTenant());
                example.setSystem(body.getSystem());
                List<AppSystemInfoEntity> list = dao.listByEntity(example);
                if (CollectionUtils.isNotEmpty(list)) {
                    entity = list.get(0);
                }
            }
        }
        BeanUtils.copyProperties(body, entity, entity.getUuid() != null ? entity.BASE_FIELDS : null);
        entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        save(entity);
        return entity.getUuid();
    }

    @Override
    public AppSystemInfoDto getSystemInfoWithLayoutThemeByTenantAndSystem(String tenant, String system) {
        Assert.notNull(tenant, "租户参数不为空");
        Assert.notNull(system, "系统参数不为空");
        AppSystemInfoEntity example = new AppSystemInfoEntity();
        example.setTenant(tenant);
        example.setSystem(system);
        List<AppSystemInfoEntity> list = dao.listByEntity(example);
        if (CollectionUtils.isNotEmpty(list)) {
            AppSystemInfoEntity entity = list.get(0);
            AppSystemInfoDto dto = new AppSystemInfoDto();
            BeanUtils.copyProperties(entity, dto);
            AppSystemPageSettingEntity settingExp = new AppSystemPageSettingEntity();
            settingExp.setTenant(tenant);
            settingExp.setSystem(system);
            List<AppSystemPageSettingEntity> setList = appSystemPageSettingDao.listByEntity(settingExp);
            if (CollectionUtils.isNotEmpty(setList)) {
                dto.setLayoutConf(setList.get(0).getLayoutConf());
                dto.setThemeStyle(setList.get(0).getThemeStyle());
                dto.setUserLayoutDefinable(setList.get(0).getUserLayoutDefinable());
                dto.setUserThemeDefinable(setList.get(0).getUserThemeDefinable());
            }
            return dto;
        }
        return null;
    }

    @Override
    @Transactional
    public Long saveSystemPageSetting(AppSystemPageSettingEntity body) {
        AppSystemPageSettingEntity entity = body.getUuid() != null ? appSystemPageSettingDao.getOne(body.getUuid()) : new AppSystemPageSettingEntity();
        if (body.getUuid() == null) {
            BeanUtils.copyProperties(body, entity);
        } else {
            BeanUtils.copyProperties(body, entity, entity.BASE_FIELDS);
        }
        entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        // 获取产品版本对应的页面设置
        if (body.getThemeStyle() == null) {
            AppProdVersionSettingEntity appProdVersionSettingEntity = appProdVersionSettingService.getLatestPublishedVersionSetting(entity.getSystem());
            if (appProdVersionSettingEntity != null) {
                entity.setThemeStyle(appProdVersionSettingEntity.getTheme());
            }
        }
        appSystemPageSettingDao.save(entity);
        return entity.getUuid();
    }

    @Override
    public AppSystemPageSettingEntity getSystemPageSetting(String tenant, String system) {
        AppSystemPageSettingEntity example = new AppSystemPageSettingEntity();
        example.setTenant(tenant);
        example.setSystem(system);
        List<AppSystemPageSettingEntity> list = appSystemPageSettingDao.listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public List<AppSystemLoginPolicyEntity> getTenantSystemLoginPolicies(String tenant, String system) {
        AppSystemLoginPolicyEntity example = new AppSystemLoginPolicyEntity();
        example.setTenant(tenant);
        example.setSystem(system);
        StringBuilder hql = new StringBuilder("from AppSystemLoginPolicyEntity where 1 = 1");
        Map<String, Object> param = Maps.newHashMap();
        if (StringUtils.isNotBlank(tenant)) {
            param.put("tenant", tenant);
            hql.append(" and tenant=:tenant ");
        }
        if (StringUtils.isNotBlank(system)) {
            param.put("system", system);
            hql.append(" and system=:system ");
        } else {
            hql.append(" and system is null ");
        }
        hql.append(" order by seq asc");
        return appSystemLoginPolicyDao.listByHQL(hql.toString(), param);
    }

    @Override
    @Transactional
    public void saveTenantSystemLoginPolicies(List<AppSystemLoginPolicyEntity> list) {
        appSystemLoginPolicyDao.saveAll(list);
    }

    @Override
    @Transactional
    public void updateAppSystemPageSettingLayoutConf(String layoutConf, Boolean userLayoutDefinable, Long uuid) {
        AppSystemPageSettingEntity entity = appSystemPageSettingDao.getOne(uuid);
        if (entity != null) {
            entity.setLayoutConf(layoutConf);
            entity.setUserLayoutDefinable(userLayoutDefinable);
            appSystemPageSettingDao.save(entity);
        }
    }

    @Override
    public void updateAppSystemPageSettingThemeStyle(String themeStyle, Boolean userThemeDefinable, Long uuid) {
        AppSystemPageSettingEntity entity = appSystemPageSettingDao.getOne(uuid);
        if (entity != null) {
            entity.setThemeStyle(themeStyle);
            entity.setUserThemeDefinable(userThemeDefinable);
            appSystemPageSettingDao.save(entity);
        }
    }

    @Override
    @Transactional
    public void saveAppSystemPageTheme(String system, String themeStyle, List<AppSystemPageThemeEntity> pageThemes, Boolean userThemeDefinable) {
        if (userThemeDefinable != null) {
            AppSystemPageSettingEntity settingEntity = this.getSystemPageSetting(SpringSecurityUtils.getCurrentTenantId(), system);
            settingEntity.setThemeStyle(themeStyle);
            settingEntity.setUserThemeDefinable(userThemeDefinable);
            appSystemPageSettingDao.save(settingEntity);
        }
        Map<String, Object> param = Maps.newHashMap();
        param.put("tenant", SpringSecurityUtils.getCurrentTenantId());
        param.put("system", system);
        appSystemPageThemeDao.deleteByHQL("delete from AppSystemPageThemeEntity where tenant=:tenant and system=:system", param);
        if (CollectionUtils.isNotEmpty(pageThemes)) {
            for (AppSystemPageThemeEntity theme : pageThemes) {
                theme.setTenant(SpringSecurityUtils.getCurrentTenantId());
                theme.setSystem(system);
            }
            appSystemPageThemeDao.saveAll(pageThemes);
        }
    }

    @Override
    @Transactional
    public void deleteAppSystemParam(List<Long> uuid) {
        appSystemParamDao.deleteByUuids(uuid);
    }

    @Override
    public String getAppSystemParam(String key, String system) {
        AppSystemParamEntity example = new AppSystemParamEntity();
        example.setPropKey(key);
        example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        example.setSystem(system);
        List<AppSystemParamEntity> list = appSystemParamDao.listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0).getPropValue() : null;
    }

    @Override
    public AppSystemParamEntity getAppSystemParamByKeyAndSystem(String key, String system) {
        AppSystemParamEntity example = new AppSystemParamEntity();
        example.setPropKey(key);
        example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        example.setSystem(system);
        List<AppSystemParamEntity> list = appSystemParamDao.listByEntity(example);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public List<AppSystemParamEntity> listAllAppSystemParam() {
        return appSystemParamDao.listByHQL("from AppSystemParamEntity", null);
    }

    @Override
    public Boolean checkAppSystemParamPropKeyExist(String propKey, String system) {
        AppSystemParamEntity example = new AppSystemParamEntity();
        example.setPropKey(propKey);
        example.setTenant(SpringSecurityUtils.getCurrentTenantId());
        example.setSystem(system);
        List<AppSystemParamEntity> list = appSystemParamDao.listByEntity(example);
        return CollectionUtils.isNotEmpty(list);
    }

    @Override
    public Boolean enableUserThemeDefinable(String tenant, String system) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("tenant", tenant);
        param.put("system", system);
        long cnt = appSystemPageSettingDao.countByHQL("select 1 from AppSystemPageSettingEntity where tenant=:tenant and system=:system and userThemeDefinable = true ", param);
        return cnt != 0;
    }

    @Override
    public List<AppPageDefinition> systemAuthenticatePage(String system, String tenant) {
        AppSystemInfoEntity appSystemInfo = this.getSystemInfoWithLayoutThemeByTenantAndSystem(tenant, system);
        Map<String, Object> param = Maps.newHashMap();
        param.put("tenant", tenant);
        param.put("system", system);
        param.put("prodVersionUuid", appSystemInfo.getProdVersionUuid());
        if (RequestSystemContextPathResolver.isMobileApp()) {
            param.put("wtype", Arrays.asList("vUniPage", "vPage")); // pc 端页面可以作为兼容移动端设计
        } else {
            param.put("wtype", Arrays.asList("vPage"));
        }

        // 获取当前系统租户下的页面定义
        List<AppPageDefinition> pageDefinitions = appPageDefinitionService.listBySQL(" select a.uuid , a.id, a.name , a.title , a.app_id, a.is_anonymous ,a.is_pc , a.wtype, a.code,a.create_time ,a.modify_time " +
                " from app_page_definition a where a.enabled = 1 and a.wtype in (:wtype) and  a.tenant=:tenant and a.app_id=:system" +
                " and a.version = ( select max(b.version) from app_page_definition b where b.id = a.id )", param);
        // 获取当前系统版本的页面定义
        List<AppPageDefinition> prdRelaPages = appPageDefinitionService.listBySQL(" select a.uuid , a.id, a.name , a.title, a.app_id,a.is_anonymous,a.is_pc , a.wtype, a.code,a.create_time ,a.modify_time " +
                " from app_page_definition a where a.enabled = 1 and a.wtype in (:wtype) and ( a.tenant=:tenant or a.tenant is null ) and a.app_id=:system" +
                " and a.version = ( select max(b.version) from app_page_definition b where b.id = a.id ) " +
                " and exists ( select 1 from app_prod_rela_page pp where pp.prod_version_uuid=:prodVersionUuid and pp.page_id=a.id ) ", param);
        Set<String> ids = Sets.newHashSet();
        List<AppPageDefinition> list = Lists.newArrayList(pageDefinitions);
        list.addAll(prdRelaPages);
        List<AppPageDefinition> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {

            Map<String, AppPageDefinition> map = Maps.newHashMap();
            for (AppPageDefinition page : list) {
                // 跳过匿名主页
                if (BooleanUtils.isTrue(page.getIsAnonymous())) {
                    continue;
                }
                if (ids.add(page.getId())) {
                    AppPageDefinition definition = new AppPageDefinition();
                    definition.setId(page.getId());
                    definition.setUuid(page.getUuid());
                    definition.setName(page.getName());
                    definition.setTitle(page.getTitle());
                    definition.setAppId(page.getAppId());
                    definition.setCode(page.getCode());
                    definition.setCreateTime(page.getCreateTime());
                    definition.setModifyTime(page.getModifyTime());
                    definition.setIsAnonymous(page.getIsAnonymous());
                    result.add(definition);
                    map.put(page.getId(), definition);
                }
            }

            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                List<AppDefElementI18nEntity> i18nEntities = appDefElementI18nService.getI18ns(ids, IexportType.AppPageDefinition, null, null, LocaleContextHolder.getLocale().toString());
                if (CollectionUtils.isNotEmpty(i18nEntities)) {
                    for (AppDefElementI18nEntity i18n : i18nEntities) {
                        if (map.containsKey(i18n.getDefId()) && StringUtils.isNotBlank(i18n.getContent())) {
                            if ("title".equalsIgnoreCase(i18n.getCode())) {
                                map.get(i18n.getDefId()).setTitle(i18n.getContent());
                            } else if ("name".equalsIgnoreCase(i18n.getCode())) {
                                map.get(i18n.getDefId()).setName(i18n.getContent());
                            }
                        }
                    }
                }
            }
        }

        if (!SpringSecurityUtils.hasAnyRole(BuildInRole.ROLE_TENANT_ADMIN.name(), BuildInRole.ROLE_ADMIN.name()) && CollectionUtils.isNotEmpty(result)) {
            List<String> systems = SpringSecurityUtils.getAccessableSystem();// 用户允许访问的系统（通过用户挂靠组织，组织归属系统，来限定用户可访问的系统）
            Iterator<AppPageDefinition> iterator = result.iterator();
            while (iterator.hasNext()) {
                AppPageDefinition definition = iterator.next();
                if (systems.contains(definition.getAppId()) &&
                        securityApiFacade.isGranted(definition.getId(), AppFunctionType.AppPageDefinition)) {
                    continue;
                } else {
                    iterator.remove();
                }
            }
        }

        if (CollectionUtils.isNotEmpty(result)) {
            compareAppPageDefinitionWithCode(result);
        }

        return result;
    }

    private static void compareAppPageDefinitionWithCode(List<AppPageDefinition> result) {

        Collections.sort(result, new Comparator<AppPageDefinition>() {
            @Override
            public int compare(AppPageDefinition o1, AppPageDefinition o2) {
                String code1 = o1.getCode();
                String code2 = o2.getCode();

                // 处理null值情况
                if (code1 == null && code2 == null) return 0;
                if (code1 == null) return 1;  // null排后面
                if (code2 == null) return -1; // 非null排前面

                // 比较编码的数字部分
                int numCompare = Integer.compare(extractNumber(code1), extractNumber(code2));
                if (numCompare != 0) {
                    return numCompare;
                }

                // 如果编码相同，则比较时间
                return compareDates(o1, o2);
            }

            private int compareDates(AppPageDefinition o1, AppPageDefinition o2) {
                Date date1 = o1.getCreateTime() != null ? o1.getCreateTime() : o1.getModifyTime();
                Date date2 = o2.getCreateTime() != null ? o2.getCreateTime() : o2.getModifyTime();

                // 处理时间为空的情况
                if (date1 == null && date2 == null) return 0;
                if (date1 == null) return 1;  // 时间为null的排后面
                if (date2 == null) return -1; // 时间非null的排前面

                // 时间最近的排前面（降序）
                return date2.compareTo(date1);
            }

            private int extractNumber(String code) {
                // 去除前导零
                String numStr = code.replaceFirst("^0+", "");
                // 如果去除前导零后为空字符串，说明原始字符串全是0
                if (numStr.isEmpty()) {
                    return 0;
                }
                try {
                    return Integer.parseInt(numStr);
                } catch (NumberFormatException e) {
                    // 如果无法解析为数字，则按原始字符串比较
                    return Integer.MAX_VALUE;
                }
            }
        });
    }


    @Override
    public String getSystemPageTheme(String pageId, String tenant, String system) {
        AppSystemPageThemeEntity example = new AppSystemPageThemeEntity();
        example.setPageId(pageId);
        example.setTenant(tenant);
        example.setSystem(system);
        List<AppSystemPageThemeEntity> themes = appSystemPageThemeDao.listByEntity(example);
        return CollectionUtils.isNotEmpty(themes) ? themes.get(0).getTheme() : null;
    }

    @Override
    public List<AppSystemPageThemeEntity> getAppSystemPageThemesBySystem(String system, String tenant) {
        AppSystemPageThemeEntity example = new AppSystemPageThemeEntity();
        example.setSystem(system);
        example.setTenant(tenant);
        return appSystemPageThemeDao.listByEntity(example);
    }

    @Override
    public List<AppPageDefinition> queryAppSystemPages(String tenant, String system) {
        AppSystemInfoEntity appSystemInfo = this.getSystemInfoWithLayoutThemeByTenantAndSystem(tenant, system);
        Map<String, Object> param = Maps.newHashMap();
        param.put("tenant", tenant);
        param.put("system", system);
        param.put("prodVersionUuid", appSystemInfo.getProdVersionUuid());
        // 获取当前系统租户下的页面定义
        List<AppPageDefinition> pageDefinitions = appPageDefinitionService.listBySQL("select  a.id, a.uuid,a.code,a.title,a.name,a.is_pc,a.enabled," +
                "a.wtype,a.layout_fixed ,a.app_id ,a.tenant,is_anonymous from app_page_definition a where a.enabled = 1 and a.is_pc = '1'" +
                " and a.version = ( select max(b.version) from app_page_definition b where b.id = a.id ) " +
                " and  ( ( a.tenant=:tenant and a.app_id=:system ) " +
                " or ( a.tenant is null and exists (  select 1 from app_prod_rela_page  p where p.page_id =a.id and  p.prod_id=:system and p.prod_version_uuid =:prodVersionUuid  ) ) ) ", param);
        return pageDefinitions;
    }

    @Override
    public TreeNode querySystemUnderControllableResourceTree(String system, String tenant) {
        AppSystemInfoEntity example = new AppSystemInfoEntity();
        example.setTenant(tenant);
        example.setSystem(system);
        List<AppSystemInfoEntity> list = dao.listByEntity(example);
        if (CollectionUtils.isNotEmpty(list)) {
            AppSystemInfoEntity info = list.get(0);
            AppProdVersionDto appProdVersionDto = appProdVersionService.getProdVersionDetails(info.getProdVersionUuid());
            TreeNode root = new TreeNode(info.getSystem(), appProdVersionDto.getProduct().getName() + " - " + appProdVersionDto.getVersion(), null);
            root.setType("appSystem");
            root.setNocheck(true);

            // 查询工作台页面
            List<AppPageDefinition> pageDefinitions = this.queryAppSystemPages(tenant, system);
            if (CollectionUtils.isNotEmpty(pageDefinitions)) {
                for (AppPageDefinition page : pageDefinitions) {
                    if (BooleanUtils.isTrue(page.getIsAnonymous())) {
                        continue;
                    }
                    TreeNode p = new TreeNode(page.getId(), page.getName(), null);
                    p.setType("appPageDefinition");
                    root.getChildren().add(p);

                    // 查询页面资源
                    List<AppPageResourceDto> resourceDtos = appPageResourceService.getAppPageResourcesAndFunction(page.getUuid());
                    if (CollectionUtils.isNotEmpty(resourceDtos)) {
                        for (AppPageResourceDto res : resourceDtos) {
                            if (res.getIsProtected() && res.getAppFunction() != null) {
                                TreeNode r = new TreeNode(page.getId() + Separator.COLON.getValue() + res.getId(), res.getAppFunction().getName(), null);
                                r.setType("AppWidgetFunctionElement");
                                r.setData(res);
                                p.getChildren().add(r);
                            }
                        }
                    }

                }
            }

            // 查询产品版本下模块
            List<AppModule> appModules = appProdVersionService.getVersionModulesByVersionUuid(info.getProdVersionUuid());
            // 查询系统下的模块
            AppModule appModule = new AppModule();
            appModule.setSystem(system);
            appModule.setTenant(tenant);
            appModules.addAll(appModuleService.listByEntity(appModule));
            Set<String> moduleUuids = Sets.newHashSet();
            if (CollectionUtils.isNotEmpty(appModules)) {
                for (AppModule module : appModules) {
                    if (!moduleUuids.add(module.getUuid())) {
                        continue;
                    }
                    TreeNode m = new TreeNode(module.getId(), module.getName(), null);
                    m.setNocheck(true);
                    m.setType("appModule");
                    root.getChildren().add(m);
                    // 查询模块下受保护的页面与页面资源
                    List<AppPageDefinition> modulePages = appPageDefinitionService.listMaxVersionPagesByAppIdAndTenant(m.getId(), null);
                    if (CollectionUtils.isNotEmpty(modulePages)) {
                        for (AppPageDefinition page : modulePages) {
                            if (BooleanUtils.isTrue(page.getIsAnonymous()) || BooleanUtils.isTrue(page.getIsDefault())) {
                                continue;
                            }
                            TreeNode p = new TreeNode(page.getId(), page.getName(), null);
                            p.setType("appPageDefinition");
                            m.getChildren().add(p);
                            // 查询页面资源
                            List<AppPageResourceDto> resourceDtos = appPageResourceService.getAppPageResourcesAndFunction(page.getUuid());
                            if (CollectionUtils.isNotEmpty(resourceDtos)) {
                                for (AppPageResourceDto res : resourceDtos) {
                                    if (res.getIsProtected() && res.getAppFunction() != null) {
                                        TreeNode r = new TreeNode(page.getId() + Separator.COLON.getValue() + res.getId(), res.getAppFunction().getName(), null);
                                        r.setType("AppWidgetFunctionElement");
                                        r.setData(res);
                                        p.getChildren().add(r);
                                    }
                                }
                            }

                        }
                    }
                }
            }


            return root;
        }

        return null;
    }

    @Override
    public AppSystemInfoDto getSystemInfoWithProdVersion(String tenant, String system) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("tenant", tenant);
        params.put("system", system);
        AppSystemInfoEntity systemInfoEntity = this.dao.getOneByHQL("from AppSystemInfoEntity where tenant=:tenant and system=:system", params);
        if (systemInfoEntity != null) {
            AppProdVersionDto versionDto = appProdVersionService.getProdVersionDetails(systemInfoEntity.getProdVersionUuid());
            AppSystemInfoDto dto = new AppSystemInfoDto();
            BeanUtils.copyProperties(systemInfoEntity, dto);
            dto.setProdVersion(versionDto);
            return dto;
        }
        return null;
    }

    @Override
    public List<AppModule> queryAppSystemModules(String tenant, String system) {
        AppSystemInfoEntity systemInfoEntity = this.getSystemInfoWithLayoutThemeByTenantAndSystem(tenant, system);
        if (systemInfoEntity != null) {
            // 查询产品版本下模块
            List<AppModule> appModules = appProdVersionService.getVersionModulesByVersionUuid(systemInfoEntity.getProdVersionUuid());
            Set<String> ids = Sets.newHashSet();
            for (AppModule m : appModules) {
                ids.add(m.getId());
            }
            // 查询系统下的模块
            AppModule appModule = new AppModule();
            appModule.setSystem(system);
            appModule.setTenant(tenant);
            List<AppModule> list = appModuleService.listByEntity(appModule);
            if (CollectionUtils.isNotEmpty(list)) {
                for (AppModule m : list) {
                    if (ids.add(m.getId())) {
                        appModules.add(m);
                    }
                }
            }
            return appModules;
        }
        return null;
    }

    @Override
    @Transactional
    public void createSystemPageAndModuleDefaultRolePvg(String system, String tenant) {
        // 查询系统下的系统首页，判断是否有首页默认访问权限，无则新增
        List<AppPageDefinition> pages = this.queryAppSystemPages(tenant, system);
        if (CollectionUtils.isNotEmpty(pages)) {
            for (AppPageDefinition page : pages) {
                Privilege pvg = privilegeFacadeService.getSystemDefPrivilegeByCode("PRIVILEGE_PAGE_" + page.getId());
                String pvgUuid = pvg != null ? pvg.getUuid() : null;
                if (pvg == null) {
                    PrivilegeDto dto = new PrivilegeDto();
                    dto.setSystemDef(1);
                    dto.setName("页面访问权限");
                    dto.setCode("PRIVILEGE_PAGE_" + page.getId());
                    dto.setAppId(page.getAppId());
                    if (StringUtils.isNotBlank(page.getTenant())) {
                        dto.setSystem(page.getAppId());
                        dto.setTenant(page.getTenant());
                    }
                    UpdatePrivilegeDto updatePrivilegeDto = new UpdatePrivilegeDto();
                    updatePrivilegeDto.setPrivilege(dto);
                    // 与页面绑定资源
                    PrivilegeResource privilegeResource = new PrivilegeResource();
                    privilegeResource.setResourceUuid(page.getId());
                    privilegeResource.setType(AppFunctionType.AppPageDefinition);
                    updatePrivilegeDto.getPrivilegeResourceAdded().add(privilegeResource);
                    List<String> uuids = privilegeFacadeService.updatePrivilege(Lists.newArrayList(updatePrivilegeDto));
                    pvgUuid = uuids.get(0);
                }
                if (pvgUuid != null) {
                    // 页面默认角色
                    Role role = roleFacadeService.getRoleById("ROLE_VIEW_PAGE_" + page.getId());
                    UpdateRoleMemberDto dto = new UpdateRoleMemberDto();
                    if (role == null) {
                        RoleDto roleDto = new RoleDto();
                        roleDto.setName("页面访问角色");
                        roleDto.setId("ROLE_VIEW_PAGE_" + page.getId());
                        roleDto.setCode(roleDto.getId());
                        roleDto.setSystemDef(1);
                        roleDto.setAppId(page.getAppId());
                        roleDto.setSystem(page.getAppId());
                        dto.setRole(roleDto);
                    } else {
                        RoleDto roleDto = new RoleDto();
                        roleDto.setUuid(role.getUuid());
                        dto.setRole(roleDto);
                    }
                    dto.getPrivilegeAdded().add(pvgUuid);
                    roleFacadeService.updateRoleMember(Lists.newArrayList(dto));
                }
            }
        }

        // 查询系统下的模块，判断是否有默认模块访问权限，无则新增并且与该模块下的所有角色关联
        List<AppModule> appModules = this.queryAppSystemModules(tenant, system);
        if (CollectionUtils.isNotEmpty(appModules)) {
            for (AppModule module : appModules) {
                Privilege pvg = privilegeFacadeService.getSystemDefPrivilegeByCode("PRIVILEGE_MOD_" + module.getId().toUpperCase());
                String pvgUuid = pvg != null ? pvg.getUuid() : null;
                if (pvg == null) {
                    PrivilegeDto dto = new PrivilegeDto();
                    dto.setSystemDef(1);
                    dto.setName("模块访问权限");
                    dto.setCode("PRIVILEGE_MOD_" + module.getId().toUpperCase());
                    dto.setSystemDef(1);
                    dto.setAppId(module.getId());
                    if (StringUtils.isNotBlank(module.getSystem())) {
                        dto.setSystem(module.getSystem());
                        dto.setTenant(module.getTenant());
                    }
                    UpdatePrivilegeDto updatePrivilegeDto = new UpdatePrivilegeDto();
                    updatePrivilegeDto.setPrivilege(dto);
                    // 与页面绑定资源
                    PrivilegeResource privilegeResource = new PrivilegeResource();
                    privilegeResource.setResourceUuid(module.getId());
                    privilegeResource.setType(AppFunctionType.AppModule);
                    updatePrivilegeDto.getPrivilegeResourceAdded().add(privilegeResource);
                    List<String> uuids = privilegeFacadeService.updatePrivilege(Lists.newArrayList(updatePrivilegeDto));
                    pvgUuid = uuids.get(0);
                }

                // 查询模块下的所有角色
                List<Role> moduleRoles = roleFacadeService.getRolesByAppId(module.getId());
                if (CollectionUtils.isNotEmpty(moduleRoles)) {
                    for (Role role : moduleRoles) {
                        UpdateRoleMemberDto dto = new UpdateRoleMemberDto();
                        // 关联模块下的角色与模块默认访问权限
                        RoleDto roleDto = new RoleDto();
                        roleDto.setUuid(role.getUuid());
                        dto.setRole(roleDto);
                        dto.getPrivilegeAdded().add(pvgUuid);
                        roleFacadeService.updateRoleMember(Lists.newArrayList(dto));
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void createAppSystemParamsFromAppProdVersion(String system) {
        AppSystemParamEntity example = new AppSystemParamEntity();
        example.setSystem(system);
        long count = appSystemParamDao.countByEntity(example);
        if (count == 0L) {
            AppProdVersionEntity prodVersionEntity = appProdVersionService.queryLatestPubVersion(system);
            if (prodVersionEntity != null) {
                List<AppProdVersionParamEntity> versionParamEntities = appProdVersionParamService.getAllParamsDetail(prodVersionEntity.getUuid());

                if (CollectionUtils.isNotEmpty(versionParamEntities)) {
                    List<AppSystemParamEntity> params = Lists.newArrayList();
                    for (AppProdVersionParamEntity ap : versionParamEntities) {
                        AppSystemParamEntity p = new AppSystemParamEntity();
                        p.setName(ap.getName());
                        p.setSystem(system);
                        p.setTenant(SpringSecurityUtils.getCurrentTenantId());
                        p.setRemark(ap.getRemark());
                        p.setPropValue(ap.getPropValue());
                        p.setPropKey(ap.getPropKey());
                        params.add(p);
                    }
                    appSystemParamDao.saveAll(params);
                }
            }
        }

//        appSystemParamDao.s
    }

    @Override
    public Boolean enableUserLayoutDefinable(String tenant, String system) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("tenant", tenant);
        param.put("system", system);
        long cnt = appSystemPageSettingDao.countByHQL("select 1 from AppSystemPageSettingEntity where tenant=:tenant and system=:system and userLayoutDefinable = true ", param);
        return cnt != 0;
    }

    @Override
    @Transactional
    public void saveAppSystemParam(AppSystemParamEntity param) {
        AppSystemParamEntity entity = new AppSystemParamEntity();
        if (param.getUuid() == null) {
            AppSystemParamEntity example = new AppSystemParamEntity();
            example.setPropKey(param.getPropKey());
            example.setTenant(SpringSecurityUtils.getCurrentTenantId());
            example.setSystem(param.getSystem());
            List<AppSystemParamEntity> list = appSystemParamDao.listByEntity(example);
            if (CollectionUtils.isNotEmpty(list)) {
                throw new RuntimeException("已存在的参数");
            }
            entity = param;
        } else {
            entity = appSystemParamDao.getOne(param.getUuid());
            entity.setPropKey(param.getPropKey());
            entity.setPropValue(param.getPropValue());
            entity.setRemark(param.getRemark());
            entity.setName(param.getName());
        }
        entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        appSystemParamDao.save(entity);
    }
}
