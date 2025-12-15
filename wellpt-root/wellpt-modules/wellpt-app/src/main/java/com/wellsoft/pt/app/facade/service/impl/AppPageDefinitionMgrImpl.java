/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.google.common.collect.*;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.app.bean.AppPageDefinitionBean;
import com.wellsoft.pt.app.bean.AppPageDefinitionParamDto;
import com.wellsoft.pt.app.bean.AppPageDefinitionPathBean;
import com.wellsoft.pt.app.bean.AppProductIntegrationBean;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.entity.*;
import com.wellsoft.pt.app.facade.service.AppContextService;
import com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr;
import com.wellsoft.pt.app.facade.service.AppProductIntegrationMgr;
import com.wellsoft.pt.app.facade.service.AppProductMgr;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.AppFunctionSourceManager;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.service.*;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.ui.*;
import com.wellsoft.pt.app.ui.client.widget.configuration.AppWidgetDefinitionElement;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.preferences.facade.service.CdUserPreferencesFacadeService;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.bean.OrgElementRole;
import com.wellsoft.pt.multi.org.bean.OrgElementVo;
import com.wellsoft.pt.multi.org.bean.UserJob;
import com.wellsoft.pt.multi.org.entity.MultiOrgElementRole;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserRole;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgGroupFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.audit.dto.AuditDataLogDto;
import com.wellsoft.pt.security.audit.entity.NestedRole;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.AuditDataFacadeService;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Description: 页面设计器管理维护
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	zhulh		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
@Service
public class AppPageDefinitionMgrImpl implements AppPageDefinitionMgr, Select2QueryApi {

    private static List<String> widgetUnReproducibleKeys = new ArrayList<String>();

    static {
        widgetUnReproducibleKeys.add(AppConstants.KEY_ID);
        widgetUnReproducibleKeys.add(AppConstants.KEY_TITLE);
        widgetUnReproducibleKeys.add(AppConstants.KEY_WTYPE);
        widgetUnReproducibleKeys.add(AppConstants.KEY_COLUMN_INDEX);
        widgetUnReproducibleKeys.add(AppConstants.KEY_REF_WIDGET_DEF_UUID);
        widgetUnReproducibleKeys.add(AppConstants.KEY_REF_WIDGET_DEF_TITLE);
        // 容器布局不复制子结点
        widgetUnReproducibleKeys.add(AppConstants.KEY_ITEMS);
    }

    private final String[] PAGE_TYPES = new String[]{"jsp", "html"};
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private DecimalFormat decimalFormat = new DecimalFormat("####.0");
    @Autowired
    private AppPageDefinitionService appPageDefinitionService;
    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;
    @Autowired
    private AppDefElementI18nService appDefElementI18nService;
    @Autowired
    private AppProductMgr appProductMgr;
    @Autowired
    private AppProductIntegrationMgr appProductIntegrationMgr;
    @Autowired
    private AppPageResourceService appPageResourceService;
    @Autowired
    private AppFunctionService appFunctionService;
    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private AppContextService appContextService;
    @Autowired
    private CommonValidateService commonValidateService;
    @Autowired
    private IdGeneratorService idGeneratorService;
    @Autowired
    private MultiOrgUserService multiOrgUserService;
    @Autowired
    private RoleFacadeService roleFacade;
    @Autowired
    private PrivilegeFacadeService privilegeFacadeService;
    @Autowired
    private AppProductService appProductService;
    @Autowired
    private AppSystemService appSystemService;
    @Autowired
    private AppProductIntegrationService appProductIntegrationService;
    @Autowired
    private MultiOrgGroupFacade multiOrgGroupFacade;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private AppFunctionSourceManager appFunctionSourceManager;
    @Autowired
    private CdUserPreferencesFacadeService cdUserPreferencesFacadeService;
    @Autowired
    private AppDataDefinitionRefResourceService appDataDefinitionRefResourceService;
    @Autowired
    private AppProdVersionService appProdVersionService;
    @Autowired
    private UserInfoFacadeService userInfoFacadeService;

    @Override
    public AppPageDefinitionBean getBean(String uuid) {
        AppPageDefinition entity = appPageDefinitionService.get(uuid);
        AppPageDefinitionBean bean = new AppPageDefinitionBean();
        BeanUtils.copyProperties(entity, bean);
        bean.setI18ns(appDefElementI18nService.getI18ns(entity.getId(), null, null, new BigDecimal(entity.getVersion()), IexportType.AppPageDefinition, null));
        if (StringUtils.isNotBlank(entity.getAppId()) && appProductService.idExist(entity.getAppId())) {
            bean.setSystem(entity.getAppId());
        }
        return bean;
    }

    @Override
    public AppPageDefinition saveBean(AppPageDefinitionBean bean) {
        String uuid = bean.getUuid();
        AppPageDefinition entity = new AppPageDefinition();
        if (StringUtils.isNotBlank(uuid)) {
            entity = appPageDefinitionService.get(uuid);
        } else {
            // ID非空唯一性判断
            if (commonValidateService.checkExists(
                    StringUtils.uncapitalize(AppPageDefinition.class.getSimpleName()),
                    ConfigurableIdEntity.ID, bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的页面！");
            }
        }
        BeanUtils.copyProperties(bean, entity, new String[]{"definitionJson", "html"});
        if (StringUtils.isBlank(entity.getVersion())) {
            entity.setVersion("1.0");
        }
        appPageDefinitionService.save(entity);
        // 更新子版本归属
        if (Double.valueOf(entity.getVersion()) > 1) {
            updateVersionAppPiUuid(entity);
        }

        AppCacheUtils.clear();

        return entity;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr#saveNewVersion(com.wellsoft.pt.app.bean.AppPageDefinitionBean)
     */
    @Override
    public AppPageDefinition saveNewVersion(AppPageDefinitionBean bean) {
        String uuid = bean.getUuid();
        if (StringUtils.isBlank(bean.getUuid())) {
            return saveBean(bean);
        } else {
            AppPageDefinition entity = appPageDefinitionService.get(uuid);
            Double latestVersion = appPageDefinitionService.getLatestVersionByUuid(uuid);
            if (latestVersion != null) {
                latestVersion += 0.1;
            } else {
                latestVersion = 1d;
            }
            AppPageDefinition appPageDefinition = new AppPageDefinition();
            BeanUtils.copyProperties(entity, appPageDefinition, IdEntity.BASE_FIELDS);
            appPageDefinition.setVersion(decimalFormat.format(latestVersion));
            appPageDefinition.setIsDefault(false);
            appPageDefinitionService.save(appPageDefinition);

            // 更新子版本归属
            if (Double.valueOf(entity.getVersion()) > 1) {
                updateVersionAppPiUuid(entity);
            }

            AppCacheUtils.clear();
            return appPageDefinition;
        }
    }

    /**
     * @param entity
     */
    private void updateVersionAppPiUuid(AppPageDefinition entity) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", entity.getUuid());
        values.put("id", entity.getId());
        values.put("appPiUuid", entity.getAppPiUuid());
        String hql = "update AppPageDefinition t set t.appPiUuid = :appPiUuid where t.id = :id and t.uuid <> :uuid";
        appPageDefinitionService.updateByHQL(hql, values);
    }

    private String saveDefinitionJson(String piUuid, String definitionJson, String pageId, boolean syncWidgetFunctions, String name, List<AppWidgetDefinitionElement> appWidgetDefinitionElements) {
        try {
            String pageDefinitionJson = definitionJson;
            AppProductIntegrationBean piBean = null;
            String title = null;
            if (StringUtils.isNotBlank(piUuid)) {
                piBean = appProductIntegrationMgr.getBean(piUuid);
                title = piBean.getDataName();
            }

            String uuid = WidgetDefinitionUtils.getUuid(pageDefinitionJson);
            String id = WidgetDefinitionUtils.getId(pageDefinitionJson);
            String pageWtype = WidgetDefinitionUtils.getString(pageDefinitionJson,
                    AppConstants.KEY_WTYPE);
            boolean vPage = pageWtype.equalsIgnoreCase("vPage");
            if (StringUtils.isBlank(title)) {
                title = WidgetDefinitionUtils.getTitle(pageDefinitionJson);
            }
            // 判断页面定义是否包含组件引用
            if (!vPage && isReferenceWidget(pageDefinitionJson)) {
                // 填充组件引用的定义
                pageDefinitionJson = fillReferenceWidgetDefinition(pageDefinitionJson);
                appPageDefinitionService.clearSession();
            }
            // 判断同一页面定义内不能多次引用同一个容器组件
            // TODO
            String html = vPage ? null : WidgetDefinitionUtils.getHtml(pageDefinitionJson);
            AppPageDefinition page = new AppPageDefinition();
            if (StringUtils.isNotBlank(uuid)) {
                page = this.appPageDefinitionService.get(uuid);
            } else {
                // ID非空唯一性判断
                if (commonValidateService.checkExists(
                        StringUtils.uncapitalize(AppPageDefinition.class.getSimpleName()),
                        ConfigurableIdEntity.ID, pageId == null ? id : pageId)) {
                    throw new RuntimeException("已经存在ID为[" + (pageId == null ? id : pageId) + "]的页面！");
                }
            }
            String sourceAppPiUuid = page.getAppPiUuid();
            if (StringUtils.isBlank(page.getTitle())) {
                page.setTitle(title);
            }
            page.setId(pageId == null ? id : pageId);
            if (StringUtils.isNotBlank(name)) {
                page.setName(name);
            }
            if (StringUtils.isBlank(page.getName())) {
                page.setName(AppContextHolder.getContext().getComponent(pageWtype).getName());
            }
            page.setWtype(pageWtype);
            if ("wMobilePage".equalsIgnoreCase(pageWtype)) {
                page.setIsPc("2");
            }
            if (StringUtils.isBlank(page.getCode())) {
                page.setCode(page.getId());
            }
            page.setDefinitionJson(pageDefinitionJson);
            page.setHtml(html);
            // 从产品集成配置的页面，设置页面的集成UUID
            if (StringUtils.isNotBlank(piUuid) && StringUtils.isBlank(sourceAppPiUuid)) {
                page.setAppPiUuid(piUuid);
            }
            // 旧的手机页面定义转uni-app页面定义信息
            if (StringUtils.equals(pageWtype, "wMobilePage")) {
                UniAppWidgetDefinitionView uniAppWidgetDefinitionView = new UniAppWidgetDefinitionView(pageDefinitionJson, UniAppWidgetDefinitionView.class);
                page.setUniAppDefinitionJson(uniAppWidgetDefinitionView.getDefinitionJson());
            }
            appPageDefinitionService.save(page);
            // 保存组件定义
            List<AppWidgetDefinition> widgetEntities = saveAppWidgetDefinition(page, appWidgetDefinitionElements);

            // 更新组件引用的所在的页面定义 ( vue版不存在页面引用的情况 )
            if (!vPage) {
                for (AppWidgetDefinition widgetEntity : widgetEntities) {
                    updatePageDefinitionOfRefWidgetDefinition(widgetEntity);
                }
            }
            String appPageUuid = page.getUuid();
            // 保存集成信息的页面配置
            // 从产品集成配置的页面，设置集成信息的页面UUID
            if (StringUtils.isNotBlank(piUuid) && StringUtils.isBlank(sourceAppPiUuid)) {
                appProductIntegrationMgr.savePiPageDefinition(piUuid, appPageUuid);
            }

            // 新增页面配置，更新产品，自动更新乐观锁的版本号
            if (StringUtils.isBlank(uuid) && piBean != null) {
                appProductMgr.update(piBean.getAppProductUuid());
            }


            AppCacheUtils.clear();
            if (syncWidgetFunctions) {
                // 同步页面引用资源
                syncPageResource(page, widgetEntities);
            }
            return appPageUuid;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public String saveDefinitionJson(String piUuid, String definitionJson, String pageId) {
        return this.saveDefinitionJson(piUuid, definitionJson, pageId, true, null, null);
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr#saveDefinitionJson(java.lang.String, java.lang.String, boolean)
     */
    public String saveDefinitionJson(String piUuid, String definitionJson, boolean newVersion) {
        String pageUuid = null;
        if (newVersion) {
            pageUuid = saveDefinitionJsonAsNewVersion(piUuid, definitionJson, true, null);
        } else {
            pageUuid = saveDefinitionJson(piUuid, definitionJson, null, true, null, null);
        }
        return pageUuid;
    }

    /**
     * @param definitionJson
     * @param piUuid
     * @param appWidgetDefinitionElements
     * @return
     */
    private String saveDefinitionJsonAsNewVersion(String piUuid, String pageDefinitionJson, boolean syncWidgetFunctions, List<AppWidgetDefinitionElement> appWidgetDefinitionElements) {
        String newPageUuid = null;
        try {
            String uuid = WidgetDefinitionUtils.getUuid(pageDefinitionJson);
            AppPageDefinition entity = appPageDefinitionService.get(uuid);
            Double latestVersion = appPageDefinitionService.getLatestVersionByUuid(uuid);
            if (latestVersion != null) {
                latestVersion += 0.1;
            } else {
                latestVersion = 1d;
            }
            AppPageDefinition appPageDefinition = new AppPageDefinition();
            BeanUtils.copyProperties(entity, appPageDefinition, IdEntity.BASE_FIELDS);
            appPageDefinition.setAppPiUuid(piUuid);
            appPageDefinition.setVersion(decimalFormat.format(latestVersion));
            appPageDefinitionService.save(appPageDefinition);
            // 更改JSON定义的UUID
            newPageUuid = appPageDefinition.getUuid();
            ModifiableWidgetDefinitionView modifiableWidgetDefinitionView = new ModifiableWidgetDefinitionView(
                    pageDefinitionJson, ModifiableWidgetDefinitionView.class);
            modifiableWidgetDefinitionView.setAttribute(AppConstants.KEY_UUID, newPageUuid);
            // 保存新版本的JSON定义
            newPageUuid = saveDefinitionJson(piUuid,
                    modifiableWidgetDefinitionView.getDefinitionJson(), null, syncWidgetFunctions, null, appWidgetDefinitionElements);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return newPageUuid;
    }

    /**
     * @param page
     */
    @Override
    public void syncPageResource(AppPageDefinition pageDefinition,
                                 List<AppWidgetDefinition> widgetDefinitions) {
        // 获取权限控制的资源ID
        List<String> protectedUuids = appPageResourceService.getProtectedUuidsByAppPageUuidAndConfigType(
                pageDefinition.getUuid(), AppConstants.FUNCTIONREF_TYPE_SYSTEM);
        // 删除原有配置资源
        appPageResourceService.removeByAppPageUuidAndConfigType(pageDefinition.getUuid(),
                AppConstants.FUNCTIONREF_TYPE_SYSTEM);
        List<String> widgetUuids = Lists.newArrayList();
        for (AppWidgetDefinition appWidgetDefinition : widgetDefinitions) {
            widgetUuids.add(appWidgetDefinition.getUuid());
        }
        // 获取新的配置资源
        Set<AppFunction> appFunctions = Sets.newHashSet();
        List<AppFunction> functions = appFunctionService.syncAppFunctionSources(widgetUuids,
                AppFunctionType.AppWidgetDefinition);
        appFunctions.addAll(functions);
        // 保存新的页面引用资源
        List<AppPageResourceEntity> pageResourceEntities = Lists.newArrayList();
        for (AppFunction appFunction : appFunctions) {
            AppPageResourceEntity resourceEntity = new AppPageResourceEntity();
            String uuid = DigestUtils.md5Hex(pageDefinition.getUuid() + appFunction.getUuid());
            resourceEntity.setUuid(uuid);
            resourceEntity.setAppPiUuid(pageDefinition.getAppPiUuid());
            resourceEntity.setAppPageUuid(pageDefinition.getUuid());
            resourceEntity.setAppFunctionUuid(appFunction.getUuid());
            resourceEntity.setConfigType(AppConstants.FUNCTIONREF_TYPE_SYSTEM);
            resourceEntity.setIsProtected(protectedUuids.contains(uuid));
            pageResourceEntities.add(resourceEntity);
        }
        appPageResourceService.saveAll(pageResourceEntities);
        appPageResourceService.flushSession();
        appPageResourceService.clearSession();
        // 增加不受权限控制的资源
        for (AppPageResourceEntity appPageResourceEntity : pageResourceEntities) {
            String resourceId = getPageResourceId(appPageResourceEntity);
            if (!Boolean.TRUE.equals(appPageResourceEntity.getIsProtected())) {
                securityApiFacade.addAnonymousResource(resourceId);
            } else {
                securityApiFacade.removeAnonymousResource(resourceId);
            }
        }
    }

    /**
     * @param entity
     * @return
     */
    private String getPageResourceId(AppPageResourceEntity entity) {
        return AppConstants.FUNCTIONREF_OF_PAGE_PREFIX + "_" + entity.getAppPiUuid() + "_" + entity.getAppPageUuid()
                + "_" + entity.getAppFunctionUuid();
    }

    /**
     * 判断页面定义是否包含组件引用
     *
     * @param definitionJson
     * @return
     */
    private boolean isReferenceWidget(String definitionJson) {
        List<Widget> widgets = WidgetDefinitionUtils.extractWidgets(
                WidgetDefinitionUtils.parseWidget(definitionJson,
                        ReadonlyWidgetDefinitionView.class));
        for (Widget widget : widgets) {
            if (widget.isReferenceWidget()) {
                return true;
            }
        }
        return false;
    }

    private String fillReferenceWidgetDefinition(String pageDefinitionJson) throws Exception {
        String targetPageUuid = WidgetDefinitionUtils.getUuid(pageDefinitionJson);
        ModifiableWidgetDefinitionView proxyView = (ModifiableWidgetDefinitionView) WidgetDefinitionUtils.parseWidget(
                pageDefinitionJson, ModifiableWidgetDefinitionView.class);
        // 引用页面的HTML
        Document targetDoc = Jsoup.parse(proxyView.getAttribute(AppConstants.KEY_HTML));
        List<Widget> pageWidgets = WidgetDefinitionUtils.extractWidgets(proxyView);
        for (Widget targetWidget : pageWidgets) {
            if (!targetWidget.isReferenceWidget()) {
                continue;
            }
            String refWidgetDefUuid = targetWidget.getAttribute(
                    AppConstants.KEY_REF_WIDGET_DEF_UUID);
            AppWidgetDefinition sourceAppWidgetDefinition = appWidgetDefinitionService.get(
                    refWidgetDefUuid);
            if (sourceAppWidgetDefinition == null) {
                continue;
            }
            if (StringUtils.equals(sourceAppWidgetDefinition.getAppPageUuid(), targetPageUuid)) {
                continue;
            }
            AppPageDefinition sourceAppPageDefinition = appPageDefinitionService.getOne(
                    sourceAppWidgetDefinition
                            .getAppPageUuid());
            // 复制JSON信息
            String targetWidgetDefinition = copyWidgetDefinition(
                    sourceAppWidgetDefinition.getDefinitionJson(),
                    targetWidget.getDefinitionJson());
            ((ModifiableWidgetDefinitionView) targetWidget).setDefinitionJson(
                    targetWidgetDefinition);
            // 复制HTML信息
            Document sourceDoc = Jsoup.parse(sourceAppPageDefinition.getHtml());
            Element sourceElement = sourceDoc.getElementById(sourceAppWidgetDefinition.getId());
            Element targetElement = targetDoc.getElementById(targetWidget.getId());
            if (sourceElement != null && targetElement != null) {
                String targetIdValue = targetElement.attr(AppConstants.KEY_ID);
                sourceElement.attr(AppConstants.KEY_ID, targetIdValue);
                targetElement.replaceWith(sourceElement);
            }
        }
        String targetHtml = targetDoc.toString();
        proxyView.setAttribute(AppConstants.KEY_HTML, targetHtml);
        return proxyView.getDefinitionJson();
    }

    /**
     * @param page
     * @param appWidgetDefinitionElements
     * @return
     * @throws Exception
     */
    private List<AppWidgetDefinition> saveAppWidgetDefinition(
            AppPageDefinition page, List<AppWidgetDefinitionElement> appWidgetDefinitionElements) throws Exception {
        String definitionJson = page.getDefinitionJson();
        String title = page.getTitle();
        String html = page.getHtml();
        String appPageUuid = page.getUuid();
        List<AppWidgetDefinition> oldAppWidgetDefinitionList = appWidgetDefinitionService.listByAppPageUuid(appPageUuid);
        Map<String, AppWidgetDefinition> oldWidgetMap = oldAppWidgetDefinitionList.stream().collect(
                Collectors.toMap(AppWidgetDefinition::getUuid, widget -> widget));
        // 保存组件定义
        List<Widget> widgets = null;
        if (CollectionUtils.isEmpty(appWidgetDefinitionElements)) {//由前端组织组件定义数据
            widgets = WidgetDefinitionUtils.extractWidgets(
                    WidgetDefinitionUtils.parseWidget(definitionJson,
                            ReadonlyWidgetDefinitionView.class));
        } else {
            widgets = WidgetDefinitionUtils.parseWidgets(appWidgetDefinitionElements);
        }
        Document doc = StringUtils.isNotBlank(html) ? Jsoup.parse(html) : null;
        List<AppWidgetDefinition> widgetEntities = new ArrayList<AppWidgetDefinition>();
        appDefElementI18nService.deleteAllI18n(null, page.getId(), new BigDecimal(page.getVersion() == null ? "1.0" : page.getVersion()), IexportType.AppPageDefinition);
        List<AppDefElementI18nEntity> i18nEntities = Lists.newArrayList();
        for (Widget widget : widgets) {
            String widgetTitle = widget.getTitle();
            String widgetId = widget.getId();

            String widgetHtml = null;
            if (doc != null && StringUtils.isNotBlank(widgetId)) {
                Element element = doc.getElementById(widgetId);
                if (element != null) {
                    widgetHtml = element.toString();
                }
            }
            // 容器组件引用的子组件不进行存储
            if (isReferenceWidgetItem(widgets, widget)) {
                continue;
            }
            String wtype = widget.getWtype();
            if (StringUtils.isBlank(wtype)) {
                continue;
            }
            String uuid = DigestUtils.md5Hex(appPageUuid + widgetId);
            AppWidgetDefinition widgetDefinition = oldWidgetMap.get(uuid);
            if (widgetDefinition == null) {
                widgetDefinition = new AppWidgetDefinition();
                widgetDefinition.setUuid(uuid);
            }
            widgetDefinition.setTitle(title + "_" + widgetTitle);
            widgetDefinition.setName(widgetTitle);
            widgetDefinition.setId(widgetId);
            widgetDefinition.setAppPageId(page.getId());
            widgetDefinition.setVersion(new BigDecimal(page.getVersion() == null ? "1.0" : page.getVersion()));
            widgetDefinition.setName(widgetTitle);
            if (!"vPage".equalsIgnoreCase(page.getWtype())) {
                UIDesignComponent designComponent = AppContextHolder.getContext().getComponent(wtype);
                if (designComponent != null) {
                    widgetDefinition.setName(designComponent.getName());
                }
            }

            widgetDefinition.setWtype(wtype);
            widgetDefinition.setAppId(page.getAppId());
            widgetDefinition.setMain(widget.getAttribute("main", Boolean.class));
            widgetDefinition.setDefinitionJson(widget.getDefinitionJson());
            widgetDefinition.setHtml(widgetHtml);
            widgetDefinition.setRefWidgetDefUuid(
                    widget.getAttribute(AppConstants.KEY_REF_WIDGET_DEF_UUID));
            widgetDefinition.setAppPageUuid(appPageUuid);
            appWidgetDefinitionService.save(widgetDefinition);
            oldWidgetMap.remove(uuid);
            widgetEntities.add(widgetDefinition);
        }
        if (CollectionUtils.isNotEmpty(appWidgetDefinitionElements)) {
            for (AppWidgetDefinitionElement element : appWidgetDefinitionElements) {
                if (CollectionUtils.isNotEmpty(element.getI18ns())) {
                    for (AppDefElementI18nEntity i : element.getI18ns()) {
                        i.setDefId(page.getId());
                        i.setApplyTo(IexportType.AppPageDefinition);
                        i.setVersion(new BigDecimal(page.getVersion() == null ? "1.0" : page.getVersion()));
                        i18nEntities.add(i);
                    }
                }
            }
        }
        if (!i18nEntities.isEmpty()) {
            appDefElementI18nService.saveAll(i18nEntities);
        }

        appWidgetDefinitionService.deleteByEntities(oldWidgetMap.values());
        appPageDefinitionService.flushSession();
        appPageDefinitionService.clearSession();
        return widgetEntities;
    }

    /**
     * @param widgets
     * @param widget
     * @return
     */
    private boolean isReferenceWidgetItem(List<Widget> widgets, Widget item) {
        for (Widget widget : widgets) {
            if (widget.isReferenceWidget() && !widget.equals(item)) {
                List<Widget> itemWidgets = WidgetDefinitionUtils.extractWidgets(widget);
                if (itemWidgets.contains(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 更新组件引用的所在的页面定义
     *
     * @param widgetEntity
     * @throws Exception
     */
    private void updatePageDefinitionOfRefWidgetDefinition(
            AppWidgetDefinition sourceAppWidgetDefinition)
            throws Exception {
        String refWidgetDefUuid = sourceAppWidgetDefinition.getUuid();
        List<AppWidgetDefinition> refAppWidgetDefinitions = appWidgetDefinitionService
                .listByRefWidgetDefUuid(refWidgetDefUuid);
        if (refAppWidgetDefinitions.isEmpty()) {
            return;
        }

        // 引用的页面
        Set<String> refPageUuids = new HashSet<String>();
        // 引用的页面包含的组件列表
        Map<String, List<AppWidgetDefinition>> refPageWidges = new HashMap<String, List<AppWidgetDefinition>>();
        // 更新configuration配置
        for (AppWidgetDefinition appWidgetDefinition : refAppWidgetDefinitions) {
            String appPageUuid = appWidgetDefinition.getAppPageUuid();
            refPageUuids.add(appPageUuid);
            if (!refPageWidges.containsKey(appPageUuid)) {
                refPageWidges.put(appPageUuid, new ArrayList<AppWidgetDefinition>());
            }
            refPageWidges.get(appPageUuid).add(appWidgetDefinition);
        }
        AppPageDefinition sourceAppPageDefinition = appPageDefinitionService.getOne(
                sourceAppWidgetDefinition
                        .getAppPageUuid());
        // 更新页面定义下的组件定义
        for (Entry<String, List<AppWidgetDefinition>> entry : refPageWidges.entrySet()) {
            String appPageUuid = entry.getKey();
            List<AppWidgetDefinition> appWidgetDefinitions = entry.getValue();
            AppPageDefinition appPageDefinition = appPageDefinitionService.getOne(appPageUuid);
            ModifiableWidgetDefinitionView proxyView = (ModifiableWidgetDefinitionView) WidgetDefinitionUtils
                    .parseWidget(appPageDefinition.getDefinitionJson(),
                            ModifiableWidgetDefinitionView.class);
            // 引用页面的HTML
            Document targetDoc = Jsoup.parse(proxyView.getAttribute(AppConstants.KEY_HTML));
            List<Widget> pageWidgets = WidgetDefinitionUtils.extractWidgets(proxyView);
            for (AppWidgetDefinition appWidgetDefinition : appWidgetDefinitions) {
                String id = appWidgetDefinition.getId();
                String wtype = appWidgetDefinition.getWtype();
                ModifiableWidgetDefinitionView targetWidget = getTargetWidget(id, wtype,
                        refWidgetDefUuid, pageWidgets);
                if (targetWidget == null) {
                    continue;
                }
                // 复制JSON信息
                String targetWidgetDefinition = copyWidgetDefinition(
                        sourceAppWidgetDefinition.getDefinitionJson(),
                        targetWidget.getDefinitionJson());
                targetWidget.setDefinitionJson(targetWidgetDefinition);
                // 复制HTML信息
                Document sourceDoc = Jsoup.parse(sourceAppPageDefinition.getHtml());
                Element sourceElement = sourceDoc.getElementById(sourceAppWidgetDefinition.getId());
                Element targetElement = targetDoc.getElementById(targetWidget.getId());
                if (sourceElement != null && targetElement != null) {
                    String targetIdValue = targetElement.attr(AppConstants.KEY_ID);
                    sourceElement.attr(AppConstants.KEY_ID, targetIdValue);
                    targetElement.replaceWith(sourceElement);
                }
            }
            String targetHtml = targetDoc.toString();
            proxyView.setAttribute(AppConstants.KEY_HTML, targetHtml);
            appPageDefinition.setDefinitionJson(proxyView.getDefinitionJson());
            appPageDefinition.setHtml(targetHtml);
            appPageDefinitionService.save(appPageDefinition);
            saveAppWidgetDefinition(appPageDefinition, null);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param id
     * @param wtype
     * @param refWidgetDefUuid
     * @param pageWidgets
     * @return
     */
    private ModifiableWidgetDefinitionView getTargetWidget(String id, String wtype,
                                                           String refWidgetDefUuid,
                                                           List<Widget> pageWidgets) {
        for (Widget widget : pageWidgets) {
            String widgetId = widget.getId();
            String widgetWtype = widget.getWtype();
            String widgetRefWidgetDefUuid = widget.getAttribute(
                    AppConstants.KEY_REF_WIDGET_DEF_UUID);
            if (StringUtils.equals(id, widgetId) && StringUtils.equals(wtype, widgetWtype)
                    && StringUtils.equals(refWidgetDefUuid, widgetRefWidgetDefUuid)) {
                return (ModifiableWidgetDefinitionView) widget;
            }
        }
        return null;
    }

    private String copyWidgetDefinition(String source, String target) throws JSONException {
        JSONObject sourceJsonObject = new JSONObject(source);
        JSONObject targetJsonObject = new JSONObject(target);
        for (Object key : sourceJsonObject.keySet()) {
            if (isWidgetReproducibleKey(key)) {
                targetJsonObject.put(key.toString(), sourceJsonObject.get(key.toString()));
            }
            // 更新引用组件的标题
            if (AppConstants.KEY_TITLE.endsWith(key.toString())) {
                targetJsonObject.put(AppConstants.KEY_REF_WIDGET_DEF_TITLE,
                        sourceJsonObject.get(key.toString()));
            }
        }
        return targetJsonObject.toString();
    }

    private boolean isWidgetReproducibleKey(Object key) {
        String realKey = ObjectUtils.toString(key, StringUtils.EMPTY);
        if (StringUtils.isBlank(realKey)) {
            return false;
        }
        // 容器组件的子组件内容直接复制
        if (realKey.equals(AppConstants.KEY_ITEMS)) {
            return true;
        }
        if (widgetUnReproducibleKeys.contains(realKey)) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public String copyPageDefinition(String sourcePageUuid) throws Exception {
        return copyPageDefinition(sourcePageUuid, null, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr#copyPageDefinition(java.lang.String, java.lang.String)
     */
    @Override
    public String copyPageDefinition(String sourcePageUuid,
                                     String targetPageUuid, String copyPageId) throws Exception {
        if (StringUtils.isNotBlank(copyPageId) && appPageDefinitionService.getById(copyPageId) != null) {
            throw new BusinessException("已存在ID为" + copyPageId + "的页面");
        }
        AppPageDefinition source = appPageDefinitionService.get(sourcePageUuid);
        AppPageDefinition target = null;
        String pageDefinitionJson = source.getDefinitionJson();
        String pageHtml = source.getHtml();
        if (StringUtils.isNotBlank(pageDefinitionJson) && StringUtils.isNotBlank(pageHtml)) {
            String rootTargetId = null;
            if (StringUtils.isNotBlank(targetPageUuid)) {
                target = appPageDefinitionService.get(targetPageUuid);
                rootTargetId = target.getId();
            }
            ModifiableWidgetDefinitionView proxyView = WidgetDefinitionUtils.copyWidget(
                    pageDefinitionJson, pageHtml,
                    rootTargetId);
            // 设置目标组件信息
            if (target != null) {
                proxyView.setAttribute(AppConstants.KEY_UUID, target.getUuid());
                proxyView.setAttribute(AppConstants.KEY_TITLE, target.getTitle());
            }
            String targetDefinitionJson = proxyView.getDefinitionJson();
            return saveDefinitionJson(null, targetDefinitionJson, copyPageId);
        } else if (StringUtils.isNotBlank(targetPageUuid)) {
            return targetPageUuid;
        } else {
            target = new AppPageDefinition();
            List<String> ignoreFields = Lists.newArrayList();
            ignoreFields.addAll(Arrays.asList(IdEntity.BASE_FIELDS));
            ignoreFields.add(AppConstants.KEY_ID);
            BeanUtils.copyProperties(source, target, ignoreFields.toArray(new String[0]));
            target.setId(copyPageId == null ? AppConstants.PAGE_PREFIX + idGeneratorService.getBySysDate() : copyPageId);
            appPageDefinitionService.save(target);
            return target.getUuid();
        }
    }

    @Override
    public String getCopyPageDefinionJson(String sourcePageUuid, String pageId) throws Exception {
        AppPageDefinition source = appPageDefinitionService.get(sourcePageUuid);
        AppPageDefinition target = null;
        String pageDefinitionJson = source.getDefinitionJson();
        String pageHtml = source.getHtml();
        if (StringUtils.isNotBlank(pageDefinitionJson) && StringUtils.isNotBlank(pageHtml)) {
            ModifiableWidgetDefinitionView proxyView = WidgetDefinitionUtils.copyWidget(
                    pageDefinitionJson, pageHtml,
                    null);
            proxyView.setAttribute(AppConstants.KEY_ID, pageId);
            return proxyView.getDefinitionJson();
        }
        return "";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr#saveUserPageDefinitionJson(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public String saveUserPageDefinitionJson(String sourcePageUuid, String name, String theme,
                                             String definitionJson) {
        String portalPageUuid = null;
        try {
            portalPageUuid = WidgetDefinitionUtils.getUuid(definitionJson);
            ModifiableWidgetDefinitionView modifiableWidgetDefinitionView = new ModifiableWidgetDefinitionView(
                    definitionJson, ModifiableWidgetDefinitionView.class);
            modifiableWidgetDefinitionView.setAttribute(AppConstants.KEY_TITLE, name);
            // 源页面与门户页面UUID一样或门户页面UUID为空，则为新建的门户
            if (StringUtils.equals(sourcePageUuid, portalPageUuid) || StringUtils.isBlank(
                    portalPageUuid)) {
                AppPageDefinition sourcePageDefinition = appPageDefinitionService.get(
                        sourcePageUuid);
                AppPageDefinition portalPageDefinition = new AppPageDefinition();
                portalPageDefinition.setName(name);
                portalPageDefinition.setId(
                        WidgetDefinitionUtils.createWidgetId(modifiableWidgetDefinitionView));
                portalPageDefinition.setTheme(theme);
                portalPageDefinition.setUserId(SpringSecurityUtils.getCurrentUserId());
                portalPageDefinition.setIsDefault(false);
                portalPageDefinition.setShared(false);
                portalPageDefinition.setAppPiUuid(sourcePageDefinition.getAppPiUuid());
                portalPageDefinition.setCorrelativePageUuid(sourcePageUuid);
                appPageDefinitionService.save(portalPageDefinition);

                // 设置页面定义的页面UUID、ID、HTML
                String pageId = modifiableWidgetDefinitionView.getId();
                String newPageId = portalPageDefinition.getId();
                // 更新HTML的ID属性
                Document sourceDoc = Jsoup.parse(
                        modifiableWidgetDefinitionView.getAttribute(AppConstants.KEY_HTML));
                Element sourceElement = sourceDoc.getElementById(pageId);
                if (sourceElement != null) {
                    sourceElement.attr(AppConstants.KEY_ID, newPageId);
                }
                String targetHtml = sourceDoc.toString();
                modifiableWidgetDefinitionView.setAttribute(AppConstants.KEY_UUID,
                        portalPageDefinition.getUuid());
                modifiableWidgetDefinitionView.setAttribute(AppConstants.KEY_ID, newPageId);
                modifiableWidgetDefinitionView.setAttribute(AppConstants.KEY_HTML, targetHtml);
                portalPageUuid = saveDefinitionJson(null,
                        modifiableWidgetDefinitionView.getDefinitionJson(), null);
            } else {
                portalPageUuid = saveDefinitionJson(null,
                        modifiableWidgetDefinitionView.getDefinitionJson(), null);
                AppPageDefinition portalPageDefinition = appPageDefinitionService.get(
                        portalPageUuid);
                portalPageDefinition.setName(name);
                portalPageDefinition.setTheme(theme);
                appPageDefinitionService.save(portalPageDefinition);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return portalPageUuid;
    }

    @Override
    public void remove(String uuid) {
        appPageDefinitionService.remove(uuid);
    }

    @Override
    public void removeAll(Collection<String> uuids) {
        appPageDefinitionService.removeAllByPk(uuids);
    }

    @Override
    public List<String> loadPageResouces() {

        List<String> htmlResources = Lists.newArrayList();
        try {
            //搜索jar包内的html页面资源
            String jarSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils
                    .convertClassNameToResourcePath(
                            "META-INF/resources/WEB-INF/views/html") + "/**/*.html";
            Resource[] jarWebResources = new PathMatchingResourcePatternResolver().getResources(
                    jarSearchPath);
            for (Resource rs : jarWebResources) {
                putWebPageResources(htmlResources, "html", rs,
                        "META-INF/resources/WEB-INF/views/html");
            }

        } catch (Exception e) {
            logger.error("搜索web页面资源异常：", e);
        }

        return htmlResources;
    }

    @Override
    @Transactional(readOnly = true)
    public List<QueryItem> listUserSystemPageDefinition(Map<String, Object> queryParams) {
        List<QueryItem> appPageDefinitionList = ApplicationContextHolder.getBean(
                NativeDao.class).namedQuery(
                "appUserSystemPageDefinitionQuery", queryParams,
                QueryItem.class, null);
        return appPageDefinitionList;
    }

    /**
     * 后台查询用户工作台
     * <p>
     * 工作台有系统默认权限角色
     * 先查出用户所有角色
     * 再根据用户查出所有职位及所有组织节点上的关联角色
     * 关联查出所有角色权限
     * <p>
     * 区分 source 来源
     * 用是否系统默认角色来区分
     * <p>
     * 默认来源 用默认工作台角色
     * * @see com.wellsoft.pt.security.enums.BuildInRole.ROLE_PAGE_SYSTEM_DEF
     *
     * @param userUuid
     * @param source   0：全部，1：用户，2：组织，3：角色，4：默认
     * @param keyword
     * @return
     */
    @Override
    @Transactional
    public List<AppPageDefinitionPathBean> listPath(String userUuid, String source, String keyword) {
        List<AppPageDefinitionPathBean> pathBeanList = new ArrayList<>();
        if (StringUtils.isBlank(userUuid)) {
            return pathBeanList;
        }
        String userId = userUuid;
        if (!userUuid.startsWith(IdPrefix.USER.getValue())) {
            MultiOrgUserAccount multiOrgUserAccount = multiOrgUserService.getAccountByUuid(userUuid);
            userId = multiOrgUserAccount.getId();
        }
        if (StringUtils.isEmpty(source)) {
            source = "0";
        }
        //根据不同来源 关联查询角色
        //key:roleUuid val:object
        Multimap<String, Object> multimap = this.putMultimap(userId, source);
        if (multimap.keySet().size() == 0) {
            return pathBeanList;
        }
        // key:工作台 val:角色
        Multimap<AppPageDefinition, Role> appPageDefinitionRoleMultimap = this.getAppPageDefinitionMultimap(multimap.keySet());

        String userName = multiOrgUserService.getAccountByUserId(userId).getUserName();


        Multimap<AppPageDefinition, AppPageDefinitionPathBean> pathBeanMultimap = HashMultimap.create();

        //组装返回数据
        for (AppPageDefinition appPageDefinition : appPageDefinitionRoleMultimap.keySet()) {
            AppProductIntegration appProductIntegration = appProductIntegrationService.get(appPageDefinition.getAppPiUuid());
            String ascription = "";
            if (appProductIntegration != null) {
                AppProduct appProduct = appProductService.get(appProductIntegration.getAppProductUuid());
                ascription = appProduct.getName() + "/" + appProductIntegration.getDataName();
            }
            //搜索 工作台名称、ID、编号、系统名称
            if (StringUtils.isNotBlank(keyword)) {
                if (appPageDefinition.getName().indexOf(keyword) > -1
                        || appPageDefinition.getId().indexOf(keyword) > -1
                        || appPageDefinition.getCode().indexOf(keyword) > -1
                        || ascription.indexOf(keyword) > -1) {
                    List<AppPageDefinitionPathBean> pathBeans = this.getPathBeanList(multimap, appPageDefinitionRoleMultimap, userName, appPageDefinition, ascription);
                    if (pathBeans.size() > 0) {
                        pathBeanMultimap.putAll(appPageDefinition, pathBeans);
                    }
                }
            } else {
                List<AppPageDefinitionPathBean> pathBeans = this.getPathBeanList(multimap, appPageDefinitionRoleMultimap, userName, appPageDefinition, ascription);
                if (pathBeans.size() > 0) {
                    pathBeanMultimap.putAll(appPageDefinition, pathBeans);
                }
            }
        }
        if (pathBeanMultimap.keySet().size() == 0) {
            return pathBeanList;
        }
        AppPageDefinition first = null;
        //用户默认工作台
        // MultiOrgUserWorkInfo multiOrgUserWorkInfo = this.multiOrgUserService.getMultiOrgUserWorkInfo(userId);
        String userWorkbenchUuid = cdUserPreferencesFacadeService.getDataValue(AppConstants.WORKBENCH, StringUtils.EMPTY, userId, AppConstants.WORKBENCH);
        if (StringUtils.isNotBlank(userWorkbenchUuid)) {
            for (AppPageDefinition appPageDefinition : pathBeanMultimap.keySet()) {
                if (appPageDefinition.getUuid().equals(userWorkbenchUuid)) {
                    first = appPageDefinition;
                    break;
                }
            }
        }
        if (first == null && source.equals("0") && StringUtils.isBlank(keyword)) {
            List<AppPageDefinition> appPageDefinitionList = new ArrayList<>(pathBeanMultimap.keySet());
            this.sortAppPageDefinition(appPageDefinitionList);
            first = appPageDefinitionList.get(0);
        }

        if (first != null) {
            List<AppPageDefinitionPathBean> firstPathBeanList = new ArrayList<>(pathBeanMultimap.get(first));
            this.sortAppPageDefinitionPathBean(firstPathBeanList);
            firstPathBeanList.get(0).setUserDef(true);
            pathBeanList.addAll(firstPathBeanList);
            pathBeanMultimap.removeAll(first);
        }

        List<AppPageDefinition> appPageDefinitionList = new ArrayList<>(pathBeanMultimap.keySet());
        this.sortAppPageDefinition(appPageDefinitionList);
        for (AppPageDefinition appPageDefinition : appPageDefinitionList) {
            List<AppPageDefinitionPathBean> allList = new ArrayList<>(pathBeanMultimap.get(appPageDefinition));
            this.sortAppPageDefinitionPathBean(allList);
            pathBeanList.addAll(allList);
        }
        return pathBeanList;
    }

    private void sortAppPageDefinition(List<AppPageDefinition> appPageDefinitionList) {
        Collections.sort(appPageDefinitionList, new Comparator<AppPageDefinition>() {
            @Override
            public int compare(AppPageDefinition o1, AppPageDefinition o2) {
                int a = o1.getCode().compareTo(o2.getCode());
                if (a == 0) {
                    a = o1.getId().compareTo(o2.getId());
                }
                if (a == 0) {
                    if (o1.getCreateTime() == null || o2.getCreateTime() == null) {
                        a = 0;
                    } else {
                        a = o1.getCreateTime().compareTo(o2.getCreateTime());
                    }
                }
                return a;
            }
        });
    }

    private void sortAppPageDefinitionPathBean(List<AppPageDefinitionPathBean> appPageDefinitionPathBeanList) {
        Collections.sort(appPageDefinitionPathBeanList, new Comparator<AppPageDefinitionPathBean>() {
            @Override
            public int compare(AppPageDefinitionPathBean o1, AppPageDefinitionPathBean o2) {
                int a = Objects.toString(o1.getCode(), StringUtils.EMPTY).compareTo(Objects.toString(o2.getCode(), StringUtils.EMPTY));
                if (a == 0) {
                    a = o1.getId().compareTo(o2.getId());
                }
                if (a == 0) {
                    if (o1.getCreateTime() == null || o2.getCreateTime() == null) {
                        a = 0;
                    } else {
                        a = o1.getCreateTime().compareTo(o2.getCreateTime());
                    }
                }
                return a;
            }
        });
    }

    private void addRoleSetMutimap(Multimap<Role, Role> roleSetMutimap, Role parentRole, Role role) {
        if (role.getSystemDef() == 1 || (StringUtils.startsWithIgnoreCase(role.getAppId(), "PROD_VER_"))) {
            roleSetMutimap.put(role, parentRole);
        }
        for (NestedRole nestedRole : role.getNestedRoles()) {
            Role role1 = roleFacade.get(nestedRole.getRoleUuid());
            if (role1 != null) {
                this.addRoleSetMutimap(roleSetMutimap, parentRole, role1);
            }
        }
    }

    private Multimap<AppPageDefinition, Role> getAppPageDefinitionMultimap(Set<String> roleUuidSet) {
        //key pageRootRole
        Multimap<Role, Role> roleSetMutimap = HashMultimap.create();
        for (String roleUuid : roleUuidSet) {
            if (StringUtils.isBlank(roleUuid)) {
                continue;// java.lang.IllegalArgumentException: id to load is required for loading
            }
            Role role = roleFacade.get(roleUuid);
            if (role != null) {
                this.addRoleSetMutimap(roleSetMutimap, role, role);
            }
        }

        //key:工作台 val:角色
        Multimap<AppPageDefinition, Role> appPageDefinitionRoleMultimap = HashMultimap.create();
//        String rolePageSystemDef = RoleConstants.getRolePageSystemDef(SpringSecurityUtils.getCurrentUserUnitId());
        for (Role role : roleSetMutimap.keySet()) {

            if (StringUtils.startsWithIgnoreCase(role.getAppId(), "PROD_VER_")) {
                AppProdVersionEntity appProdVersionEntity = appProductService.getProdVersionByVersionId(role.getAppId());
                if (appProdVersionEntity != null) {
                    Set<Privilege> privileges = role.getPrivileges();
                    Set<String> pageIds = Sets.newHashSet();
                    if (CollectionUtils.isNotEmpty(privileges)) {
                        for (Privilege p : privileges) {
                            if (p.getSystemDef() == 1 && StringUtils.startsWithIgnoreCase(p.getCode(), "PRIVILEGE_PAGE_")) {
                                String pageId = p.getCode().split("PRIVILEGE_PAGE_")[1];
                                pageIds.add(pageId.toLowerCase());
                            }
                        }
                    }
                    List<AppProdRelaPageEntity> appProdRelaPages = appProdVersionService.getProdVersionRelaPage(appProdVersionEntity.getUuid());
                    if (CollectionUtils.isNotEmpty(appProdRelaPages)) {
                        for (AppProdRelaPageEntity relaPage : appProdRelaPages) {
                            if (pageIds.contains(relaPage.getPageId().toLowerCase())) {
                                for (Role role1 : roleSetMutimap.get(role)) {
                                    AppPageDefinition appPageDefinition = appPageDefinitionService.getOne(relaPage.getPageUuid());
                                    if (appPageDefinition != null) {
                                        appPageDefinition.setAppPiUuid(relaPage.getProdVersionUuid().toString());
                                        appPageDefinitionRoleMultimap.put(appPageDefinition, role1);
                                    }
                                }
                            }
                        }
                    }

                }
            }

//            if (role.getId().equals(rolePageSystemDef)) {
//                for (Privilege privilege : role.getPrivileges()) {
//                    List<PrivilegeResource> privilegeResourceList = privilegeFacadeService.getByPrivilegeUuid(privilege.getUuid());
//                    for (PrivilegeResource privilegeResource : privilegeResourceList) {
//                        String pageUuid = privilegeResource.getResourceUuid().split("_")[2];
//                        AppPageDefinition appPageDefinition = appPageDefinitionService.get(pageUuid);
//                        if (appPageDefinition != null) {
//                            for (Role role1 : roleSetMutimap.get(role)) {
//                                appPageDefinitionRoleMultimap.put(appPageDefinition, role1);
//                            }
//                        }
//
//                    }
//                }
//            } else {
            if (StringUtils.startsWithIgnoreCase(role.getId(), AppConstants.PAGE_DEF_PREFIX)) {
                String pageUuid = role.getId().replace(AppConstants.PAGE_DEF_PREFIX, "");
                if (pageUuid.indexOf("_") > -1) {
                    pageUuid = pageUuid.split("_")[1];
                }
                AppPageDefinition appPageDefinition = appPageDefinitionService.get(pageUuid);
                if (appPageDefinition != null && !"2".equals(appPageDefinition.getIsPc())) {
                    for (Role role1 : roleSetMutimap.get(role)) {
                        appPageDefinitionRoleMultimap.put(appPageDefinition, role1);
                    }
                }
            }


//            }
        }


        return appPageDefinitionRoleMultimap;
    }

    private List<AppPageDefinitionPathBean> getPathBeanList(Multimap<String, Object> multimap, Multimap<AppPageDefinition, Role> appPageDefinitionRoleMultimap,
                                                            String userName, AppPageDefinition appPageDefinition, String ascription) {
        List<AppPageDefinitionPathBean> pathBeanList = new ArrayList<>();
        for (Role role : appPageDefinitionRoleMultimap.get(appPageDefinition)) {
            for (Object object : multimap.get(role.getUuid())) {
                AppPageDefinitionPathBean pathBean = getAppPageDefinitionPathBean(appPageDefinition, ascription);
                //1：用户，2：组织，4：默认
                if (object instanceof MultiOrgUserRole) {
                    if (role.getSystemDef() == 1) {
                        MultiOrgUserRole userRole = (MultiOrgUserRole) object;
                        pathBean.setCreateTime(userRole.getModifyTime());
                        pathBean.setSource("1");
                        pathBean.setCalculatePath(userName);
                    } else {
                        //3：角色，
                        pathBean.setSource("3");
                        pathBean.setCalculatePath(role.getName());
                        pathBean.setCreateTime(role.getModifyTime());
                    }
                } else if (object instanceof String) {
                    //表单角色
                    pathBean.setSource("3");
                    pathBean.setCalculatePath(role.getName());
                    pathBean.setCreateTime(role.getModifyTime());
                } else if (object instanceof OrgElementRole) {
                    OrgElementRole orgElementRole = (OrgElementRole) object;
                    String calculatePath = this.getNamePath(orgElementRole.getOrgElement(), new StringBuilder());
                    pathBean.setCreateTime(orgElementRole.getModifyTime());
                    pathBean.setSource("2");
                    pathBean.setCalculatePath(calculatePath);
                } else {
                    pathBean.setSource("4");
                    pathBean.setCreateTime(role.getModifyTime());
                    pathBean.setCalculatePath("系统默认工作台");
                }
                pathBeanList.add(pathBean);
            }
        }
        return pathBeanList;
    }

    /**
     * key:roleUuid val:object
     *
     * @param userId
     * @param source source  0：全部，1：用户，2：组织，3：角色，4：默认
     * @return
     */
    private Multimap<String, Object> putMultimap(String userId, String source) {
        Multimap<String, Object> multimap = HashMultimap.create();
        if (source.equals("0")) {
            this.putUserMultimap(source, userId, multimap);
            this.putOrgEleMultimap(userId, multimap);
//            this.putDefWorkbenchRoleMultimap(multimap);
            this.addOther(userId, multimap);


            return multimap;
        }
        if (source.equals("1") || source.equals("3")) {
            this.putUserMultimap(source, userId, multimap);
            if (source.equals("3")) {
                this.addOther(userId, multimap);
            }
            return multimap;
        }
        if (source.equals("2")) {
            this.putOrgEleMultimap(userId, multimap);
            return multimap;
        }
//        if (source.equals("4")) {
//            this.putDefWorkbenchRoleMultimap(multimap);
//            return multimap;
//        }
        return multimap;
    }

//    private void putDefWorkbenchRoleMultimap(Multimap<String, Object> multimap) {
//        //默认工作台角色
//        Role defWorkbenchRole = roleFacade.getDefWorkbenchRole();
//        if (defWorkbenchRole != null) {
//            multimap.put(defWorkbenchRole.getUuid(), defWorkbenchRole);
//        }
//    }

    private void putOrgEleMultimap(String userId, Multimap<String, Object> multimap) {
        //组织角色
        Map<String, UserJob> userJobMap = multiOrgUserService.gerUserJob(userId);
        UserJob userJob = userJobMap.get(userId);
        if (userJob != null) {
            List<OrgElementVo> elementVos = new ArrayList<>();
            elementVos.addAll(userJob.getMainJobs());
            elementVos.addAll(userJob.getOtherJobs());
            for (OrgElementVo elementVo : elementVos) {
                this.putMap(multimap, elementVo);
            }
        }
        //FIXME: 7.0 新版用户登录进来在需要确定当前的组织身份
        Set<String> roles = ApplicationContextHolder.getBean(OrgFacadeService.class).getOrgUserRoleUuidsByUserId(userId);
        if (CollectionUtils.isNotEmpty(roles)) {
            for (String uid : roles) {
                Role role = roleFacade.get(uid);
                if (role == null) {
                    continue;
                }
                multimap.put(uid, role);
            }
        }

    }

    private void putUserMultimap(String source, String userId, Multimap<String, Object> multimap) {
        //用户角色
        List<MultiOrgUserRole> userRoleList = multiOrgUserService.queryRoleListOfUser(userId);
        for (MultiOrgUserRole multiOrgUserRole : userRoleList) {
            if (source.equals("1") || source.equals("3")) {
                Role role = roleFacade.get(multiOrgUserRole.getRoleUuid());
                if (role == null) {
                    continue;
                }
                if (source.equals("1") && role.getSystemDef() == 0) {
                    continue;
                }
                if (source.equals("3") && role.getSystemDef() == 1) {
                    continue;
                }
            }
            multimap.put(multiOrgUserRole.getRoleUuid(), multiOrgUserRole);
        }

        List<String> roleUuids = userInfoFacadeService.getUserRolesByUserId(userId);
        if (CollectionUtils.isNotEmpty(roleUuids)) {
            for (String uid : roleUuids) {
                Role role = roleFacade.get(uid);
                if (role == null) {
                    continue;
                }
                multimap.put(uid, role);
            }
        }

        //        //表单角色
//        if(source.equals("0") || source.equals("3")){
//            //  获取表单组织控件中的所有关联角色，判断依次判断用户具备哪些角色
//            List<FormControlRole> controlRoles = dyFormFacade.queryAllControlRoles();
//            if (controlRoles != null) {
//                // 获取跟用户相关的所有组织节点, 包含了群组的信息，然后依次获取该节点对应的的角色信息
//                Set<String> userOrgIds = orgApiFacade.getUserOrgIds(userId);
//                // 需要包含用户自己
//                userOrgIds.add(userId);
//                for (FormControlRole controlRole : controlRoles) {
//                    if (dyFormFacade.isHasControlRoleByUserOrgIds(userOrgIds, controlRole)) {
//                        multimap.put(controlRole.getRoleUuid(), controlRole.getRoleUuid());
//                    }
//                }
//            }
//        }


    }

    private void addOther(String userId, Multimap<String, Object> multimap) {
        //组织角色
        Map<String, UserJob> userJobMap = multiOrgUserService.gerUserJob(userId);
        UserJob userJob = userJobMap.get(userId);
        Set<String> groupObjIdSet = new HashSet<>();
        if (userJob != null) {
            List<OrgElementVo> elementVos = new ArrayList<>();
            elementVos.addAll(userJob.getMainJobs());
            elementVos.addAll(userJob.getOtherJobs());
            for (OrgElementVo elementVo : elementVos) {
                this.putMapOrgElement(multimap, elementVo, groupObjIdSet);
            }
        }
        //群组角色
        groupObjIdSet.add(userId);
        Set<String> roleUuidSet = multiOrgGroupFacade.getRoleIdByGroupMember(groupObjIdSet);
        for (String roleUuid : roleUuidSet) {
            if (!multimap.containsKey(roleUuid)) {
                Role role = roleFacade.get(roleUuid);
                if (role != null && role.getSystemDef() == 0) {
                    multimap.put(roleUuid, new MultiOrgUserRole());
                }
            }
        }
    }

    //递归查询节点角色
    private void putMapOrgElement(Multimap<String, Object> multimap, OrgElementVo orgElementVo, Set<String> groupObjIdSet) {
        List<MultiOrgElementRole> elementRoleList = multiOrgUserService.getElementRoles(orgElementVo.getId());
        for (MultiOrgElementRole multiOrgElementRole : elementRoleList) {
            if (!multimap.containsKey(multiOrgElementRole.getRoleUuid())) {
                Role role = roleFacade.get(multiOrgElementRole.getRoleUuid());
                if (role != null && role.getSystemDef() == 0) {
                    multimap.put(multiOrgElementRole.getRoleUuid(), new MultiOrgUserRole());
                }
            }
        }
        groupObjIdSet.add(orgElementVo.getId());
        if (orgElementVo.getParent() != null) {
            this.putMapOrgElement(multimap, orgElementVo.getParent(), groupObjIdSet);
        }
    }

    private String getNamePath(OrgElementVo orgElementVo, StringBuilder stringBuilder) {
        if (orgElementVo.getParent() != null) {
            getNamePath(orgElementVo.getParent(), stringBuilder);
            stringBuilder.append("/").append(orgElementVo.getName());
        } else {
            stringBuilder.append(orgElementVo.getName());
        }
        return stringBuilder.toString();
    }


    private AppPageDefinitionPathBean getAppPageDefinitionPathBean(AppPageDefinition appPageDefinition, String ascription) {
        AppPageDefinitionPathBean pathBean = new AppPageDefinitionPathBean();
        pathBean.setUuid(appPageDefinition.getUuid());
        pathBean.setAppPiUuid(appPageDefinition.getAppPiUuid());
        pathBean.setName(appPageDefinition.getName() + "（" + appPageDefinition.getVersion() + "）");
        pathBean.setId(appPageDefinition.getId());
        pathBean.setCode(appPageDefinition.getCode());
        pathBean.setAscription(ascription);
        pathBean.setWtype(appPageDefinition.getWtype());
        return pathBean;
    }


    //递归查询节点角色
    private void putMap(Multimap<String, Object> multimap, OrgElementVo orgElementVo) {
        List<MultiOrgElementRole> elementRoleList = multiOrgUserService.getElementRoles(orgElementVo.getId());
        for (MultiOrgElementRole multiOrgElementRole : elementRoleList) {
            Role role = roleFacade.get(multiOrgElementRole.getRoleUuid());
            if (role != null && role.getSystemDef() == 1) {
                OrgElementRole orgElementRole = new OrgElementRole();
                orgElementRole.setUuid(multiOrgElementRole.getUuid());
                orgElementRole.setModifyTime(multiOrgElementRole.getModifyTime());
                orgElementRole.setOrgElement(orgElementVo);
                multimap.put(multiOrgElementRole.getRoleUuid(), orgElementRole);
            }
        }
        if (orgElementVo.getParent() != null) {
            this.putMap(multimap, orgElementVo.getParent());
        }
    }

    @Override
    @Transactional
    public List<AppPageDefinitionPathBean> listFacade() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<AppPageDefinitionPathBean> pathBeanList = new ArrayList<>();
        //key:roleUuid val:object
        Multimap<String, Object> multimap = this.putMultimap(userId, "0");
        if (multimap.keySet().size() == 0) {
            return pathBeanList;
        }
        //用户工作台 key:工作台 val:角色
        Multimap<AppPageDefinition, Role> appPageDefinitionRoleMultimap = this.getAppPageDefinitionMultimap(multimap.keySet());
        //用户默认工作台
        //MultiOrgUserWorkInfo multiOrgUserWorkInfo = this.multiOrgUserService.getMultiOrgUserWorkInfo(userId);
        String userWorkbenchUuid = cdUserPreferencesFacadeService.getDataValue(AppConstants.WORKBENCH, StringUtils.EMPTY, userId, AppConstants.WORKBENCH);
        AppPageDefinitionPathBean first = null;
        //组装返回数据
        for (AppPageDefinition appPageDefinition : appPageDefinitionRoleMultimap.keySet()) {
            //TODO: 组织新版定义地址
            AppPageDefinitionPathBean appPageDefinitionPathBean = this.getAppPageDefinitionPathBean(appPageDefinition, null);
            AppProductIntegration appProductIntegration = appProductIntegrationService.get(appPageDefinition.getAppPiUuid());
            String url = null;
            if ("vPage".equalsIgnoreCase(appPageDefinition.getWtype())) {
                url = "/sys/" + appPageDefinition.getAppId() + "/index";
            } else {
                url = AppConstants.WEB_APP_PATH + appProductIntegration.getDataPath() + AppConstants.DOT_HTML + "?pageUuid=" + appPageDefinition.getUuid();
            }
            appPageDefinitionPathBean.setUrl(url);
            if (first == null && StringUtils.isNotEmpty(userWorkbenchUuid) &&
                    appPageDefinitionPathBean.getUuid().equals(userWorkbenchUuid)) {
                appPageDefinitionPathBean.setUserDef(true);
                first = appPageDefinitionPathBean;
            } else {
                pathBeanList.add(appPageDefinitionPathBean);
            }
        }
        this.sortAppPageDefinitionPathBean(pathBeanList);
        if (first != null) {
            pathBeanList.add(0, first);
        }
        return pathBeanList;
    }

    /**
     * 根据产品集成路径，前台查询用户工作台
     *
     * @param appPiPath
     */
    @Override
    public List<AppPageDefinitionPathBean> listFacadeByAppPath(String appPiPath) {
        String piUuid = AppCacheUtils.getPiUuidByPath(appPiPath);

        // 获取产品集成路径下有权限访问的页面UUID列表
        List<String> appPageDefinitionUuids = appPageDefinitionService
                .getAppPageDefinitionUuidsByAppPiUuid(piUuid);
        List<String> grantedPageUuids = Lists.newArrayList();
        for (String appPageDefinitionUuid : appPageDefinitionUuids) {
            if (securityApiFacade.isGranted(appPageDefinitionUuid, AppFunctionType.AppPageDefinition)) {
                grantedPageUuids.add(appPageDefinitionUuid);
            }
        }

        // 获取页面定义信息
        List<AppPageDefinitionPathBean> appPageDefinitionPathBeans = Lists.newArrayList();
        List<AppPageDefinition> appPageDefinitions = appPageDefinitionService.listByUuids(grantedPageUuids);
        Map<String, AppPageDefinition> appPageDefinitionMap = ConvertUtils.convertElementToMap(appPageDefinitions, IdEntity.UUID);
        grantedPageUuids.stream().forEach(pageUuid -> {
            AppPageDefinition appPageDefinition = appPageDefinitionMap.get(pageUuid);
            if (appPageDefinition != null) {
                appPageDefinitionPathBeans.add(getAppPageDefinitionPathBean(appPageDefinition, null));
            }
        });
        return appPageDefinitionPathBeans;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppPageDefinition> queryByRoleIds(Collection<String> roleIds) {
        Set<Role> pageRole = new HashSet<>();
        for (String roleId : roleIds) {
            Role role = roleFacade.getRoleById(roleId);
            if (role != null) {
                this.addPageRoleSet(role, pageRole);
            }
        }
        List<AppPageDefinition> appPageDefinitionList = new ArrayList<>();
        for (Role role : pageRole) {
            if (role.getId().startsWith(AppConstants.PAGE_DEF_PREFIX)) {
                String pageUuid = role.getId().replace(AppConstants.PAGE_DEF_PREFIX, "");
                if (pageUuid.indexOf("_") > -1) {
                    pageUuid = pageUuid.split("_")[1];
                }
                AppPageDefinition appPageDefinition = appPageDefinitionService.get(pageUuid);
                if (appPageDefinition != null) {
                    appPageDefinitionList.add(appPageDefinition);
                }
            }
        }
        Collections.sort(appPageDefinitionList, new Comparator<AppPageDefinition>() {
            @Override
            public int compare(AppPageDefinition o1, AppPageDefinition o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });
        return appPageDefinitionList;
    }

    private void addPageRoleSet(Role role, Set<Role> pageRole) {
        if (role.getSystemDef() == 1) {
            pageRole.add(role);
        }
        for (NestedRole nestedRole : role.getNestedRoles()) {
            Role role1 = roleFacade.get(nestedRole.getRoleUuid());
            this.addPageRoleSet(role1, pageRole);
        }
    }

    private void putWebPageResources(List<String> htmlResources, String type, Resource rs,
                                     String startPath)
            throws Exception {
        String path = rs.getURL().getPath();
        String redirectUrl = path.substring(path.lastIndexOf(startPath) + startPath.length());
        htmlResources.add(redirectUrl);
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        PagingInfo pagingInfo = queryInfo.getPagingInfo();
        String searchValue = queryInfo.getSearchValue();
        String excludeUuidString = queryInfo.getOtherParams("excludeUuids", "-1");
        List<String> excludeUuids = Arrays.asList(
                StringUtils.split(excludeUuidString, Separator.SEMICOLON.getValue()));
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("title", searchValue);
        values.put("excludeUuids", excludeUuids);
        List<AppPageDefinition> definitions = appPageDefinitionService.listByNameHQLQueryAndPage(
                "appPageDefinitionQuery", values, queryInfo.getPagingInfo());
        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        for (AppPageDefinition appPageDefinition : definitions) {
            String text = appPageDefinition.getName();
            if (StringUtils.isNotBlank(appPageDefinition.getTitle())) {
                text += "——" + appPageDefinition.getTitle();
            }
            text += "(" + appPageDefinition.getVersion() + ")";
            Select2DataBean bean = new Select2DataBean(appPageDefinition.getUuid(), text);
            beans.add(bean);
        }
        return new Select2QueryData(beans, pagingInfo);
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        String[] ids = queryInfo.getIds();
        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        List<AppPageDefinition> definitions = appPageDefinitionService.listByUuids(
                Arrays.asList(ids));
        for (AppPageDefinition appPageDefinition : definitions) {
            Select2DataBean bean = new Select2DataBean(appPageDefinition.getUuid(),
                    appPageDefinition.getTitle());
            beans.add(bean);
        }
        return new Select2QueryData(beans);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr#isSupportsAppHashByAppPath(java.lang.String)
     */
    @Override
    public boolean isSupportsAppHashByAppPath(String appPath) {
        return StringUtils.isNotBlank(getWidgetDefinitionByAppPath(appPath));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr#getAppHashTreeByAppPath(java.lang.String)
     */
    @Override
    public List<TreeNode> getAppHashTreeByAppPath(String appPath) {
        String widgetDefinitionJson = getWidgetDefinitionByAppPath(appPath);
        List<TreeNode> treeNodes = Lists.newArrayList();
        if (StringUtils.isBlank(widgetDefinitionJson)) {
            return treeNodes;
        }
        // 根据组件定义实例化组件对象
        ReadonlyWidgetDefinitionAsFunctionElementTreeView widget;
        try {
            widget = new ReadonlyWidgetDefinitionAsFunctionElementTreeView(widgetDefinitionJson);
            treeNodes.add(widget.getTree());
        } catch (Exception e) {
        }
        return treeNodes;
    }


    /**
     * @param appPath
     * @return
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public String getWidgetDefinitionByAppPath(String appPath) {
        PiFunction piFunction = appContextService.getFunctionByPath(appPath);
        String functionType = piFunction != null ? piFunction.getType() : StringUtils.EMPTY;
        // 组件定义
        if (AppFunctionType.AppWidgetDefinition.equals(functionType)) {
            String definitionJson = piFunction.getDefinitionJson();
            Map<String, Object> definitionMap = JsonUtils.json2Object(definitionJson, Map.class);
            String widgetUuid = String.valueOf(definitionMap.get(IdEntity.UUID));
            AppWidgetDefinition appWidgetDefinition = appWidgetDefinitionService.get(widgetUuid);
            return appWidgetDefinition != null ? appWidgetDefinition.getDefinitionJson() : StringUtils.EMPTY;
        } else if (AppFunctionType.URL.equals(functionType)) {
            // 页面
            String definitionJson = piFunction.getDefinitionJson();
            Map<String, Object> definitionMap = JsonUtils.json2Object(definitionJson, Map.class);
            String pageUrl = String.valueOf(definitionMap.get(AppFunctionType.URL.toLowerCase()));
            // 提取页面定义UUID
            int indexOfPageUuid = StringUtils.indexOf(pageUrl, "pageUuid=");
            if (indexOfPageUuid != -1) {
                String pageUuid = StringUtils.substringAfter(pageUrl, "pageUuid=");
                AppPageDefinition appPageDefinition = appPageDefinitionService.get(pageUuid);
                return appPageDefinition != null ? appPageDefinition.getDefinitionJson() : StringUtils.EMPTY;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public String saveDefinitionJson(String piUuid, String definitionJson, boolean newVersion, HashMap<String, ArrayList<FunctionElement>> functionElements, String name, String id
            , List<AppWidgetDefinitionElement> appWidgetDefinitionElements) {
        String pageUuid = null;
        List<String> protectedIds = Lists.newArrayList();
        if (newVersion) {
            protectedIds = appPageResourceService.getProtectedIdsByAppPageUuidAndConfigType(
                    WidgetDefinitionUtils.getUuid(definitionJson), AppConstants.FUNCTIONREF_TYPE_SYSTEM);
            pageUuid = saveDefinitionJsonAsNewVersion(piUuid, definitionJson, false, appWidgetDefinitionElements);
        } else {
            pageUuid = saveDefinitionJson(piUuid, definitionJson, id, false, name, appWidgetDefinitionElements);
        }


        // 获取权限控制的资源ID
        List<String> protectedUuids = appPageResourceService.getProtectedUuidsByAppPageUuidAndConfigType(
                pageUuid, AppConstants.FUNCTIONREF_TYPE_SYSTEM);
        // 删除原有配置资源
        appPageResourceService.removeByAppPageUuidAndConfigType(pageUuid,
                AppConstants.FUNCTIONREF_TYPE_SYSTEM);

        List<AppWidgetDefinition> appWidgetDefinitions = appWidgetDefinitionService.getAllByAppPageUuid(pageUuid);
        Set<AppFunctionSource> appFunctionSources = Sets.newHashSet();
        Map<String, FunctionElement> functionElementMap = Maps.newHashMap();
        for (AppWidgetDefinition appWidgetDefinition : appWidgetDefinitions) {
            appFunctionSources.add(convert2AppFunctionSource(appWidgetDefinition));
            if (functionElements.get(appWidgetDefinition.getId()) != null) {
                List<FunctionElement> functionElementList = functionElements.get(appWidgetDefinition.getId());
                for (FunctionElement fe : functionElementList) {
                    if (fe == null) {
                        continue;
                    }
                    functionElementMap.put(fe.getId(), fe);
                    appFunctionSources.addAll(convertFunctionElement2AppFunctionSource(fe, appWidgetDefinition));
                }
            }
        }
        Set<AppFunction> appFunctions = Sets.newHashSet();

        for (AppFunctionSource source : appFunctionSources) {
            appFunctions.add(appFunctionSourceManager.convert2AppFunction(source));
        }
        List<AppFunction> waitSaveList = Lists.newArrayList();
        for (AppFunction function : appFunctions) {
            AppFunction exitsOne = appFunctionService.getOne(function.getUuid());
            if (exitsOne != null) {
                org.springframework.beans.BeanUtils.copyProperties(function, exitsOne, IdEntity.BASE_FIELDS);//存在则更新
                waitSaveList.add(exitsOne);
            } else {
                waitSaveList.add(function);
            }
        }
        appFunctionService.saveAll(waitSaveList);
        AppPageDefinition pageDefinition = appPageDefinitionService.get(pageUuid);

        // 保存新的页面引用资源
        List<AppPageResourceEntity> pageResourceEntities = Lists.newArrayList();
        for (AppFunction appFunction : appFunctions) {
            AppPageResourceEntity resourceEntity = new AppPageResourceEntity();
            String uuid = DigestUtils.md5Hex(pageDefinition.getUuid() + appFunction.getUuid());
            resourceEntity.setUuid(uuid);
            resourceEntity.setId(appFunction.getId());
            resourceEntity.setAppPiUuid(pageDefinition.getAppPiUuid());
            resourceEntity.setAppPageUuid(pageDefinition.getUuid());
            resourceEntity.setAppFunctionUuid(appFunction.getUuid());
            resourceEntity.setConfigType(AppConstants.FUNCTIONREF_TYPE_SYSTEM);
            resourceEntity.setIsProtected(newVersion ? protectedIds.contains(appFunction.getId()) : protectedUuids.contains(uuid));
            if (functionElementMap.containsKey(appFunction.getId())) {
                resourceEntity.setResourceId(functionElementMap.get(appFunction.getId()).getResourceId());
                resourceEntity.setResourceType(functionElementMap.get(appFunction.getId()).getResourceType());

            }
            pageResourceEntities.add(resourceEntity);
        }
        appPageResourceService.saveAll(pageResourceEntities);
        appPageResourceService.flushSession();
        appPageResourceService.clearSession();
        boolean vPage = "vPage".equalsIgnoreCase(pageDefinition.getWtype());
        if (vPage) {
            // 新版vue页面的资源，不保护的情况下不会进行权限判断，因此也不需要添加或者删除匿名权限，详见方法: authenticatePage
            return pageUuid;
        }
        // 增加不受权限控制的资源
        for (AppPageResourceEntity appPageResourceEntity : pageResourceEntities) {
            String resourceId = getPageResourceId(appPageResourceEntity);
            if (!Boolean.TRUE.equals(appPageResourceEntity.getIsProtected())) {
                securityApiFacade.addAnonymousResource(resourceId);
            } else {
                securityApiFacade.removeAnonymousResource(resourceId);
            }
        }

        return pageUuid;
    }


    @Override
    @Transactional
    public String savePageDefinition(AppPageDefinitionParamDto params) {
        AppPageDefinition pageDefinition = new AppPageDefinition();
        boolean isUpdate = StringUtils.isNotBlank(params.getUuid()) && !params.getNewVersion();
        AppPageDefinition oldAppPageDefinition = null;
        if (isUpdate) {
            pageDefinition = this.appPageDefinitionService.getOne(params.getUuid());
            oldAppPageDefinition = new AppPageDefinition();
            org.springframework.beans.BeanUtils.copyProperties(pageDefinition, oldAppPageDefinition);
        }

        if (StringUtils.isBlank(params.getUuid()) || isUpdate) {
            pageDefinition.setCode(params.getCode());
            pageDefinition.setTitle(params.getTitle());
            if (StringUtils.isNotBlank(params.getPiUuid())) {
                pageDefinition.setAppPiUuid(params.getPiUuid());
            }
            pageDefinition.setIsPc(params.getIsPc());
            pageDefinition.setRemark(params.getRemark());
            pageDefinition.setAppId(params.getAppId());
            pageDefinition.setIsDefault(params.getIsDefault());
            pageDefinition.setDesignable(params.getDesignable());
            pageDefinition.setLayoutFixed(params.getLayoutFixed());
            pageDefinition.setEnabled(params.getEnabled());
            pageDefinition.setTenant(params.getTenant());
            pageDefinition.setIsAnonymous(params.getIsAnonymous());
            this.appPageDefinitionService.save(pageDefinition);
            if (!isUpdate) {// 新增设置uuid
                params.setDefinitionJson(WidgetDefinitionUtils.putString(params.getDefinitionJson(), "uuid", pageDefinition.getUuid()));
            }
        }

        String uuid = saveDefinitionJson(params.getPiUuid(), params.getDefinitionJson(), params.getNewVersion(), params.getFunctionElements(), params.getName(), params.getId(), params.getAppWidgetDefinitionElements());
        AppPageDefinition page = null;
        if (params.getNewVersion()) {// 升级版本的情况
            page = this.appPageDefinitionService.getOne(uuid);
            page.setName(params.getName());
            page.setCode(params.getCode());
            page.setTitle(params.getTitle());
            page.setIsPc(params.getIsPc());
            page.setRemark(params.getRemark());
            this.appPageDefinitionService.save(page);
            if (CollectionUtils.isNotEmpty(params.getI18ns())) {
                for (AppDefElementI18nEntity i : params.getI18ns()) {
                    i.setVersion(new BigDecimal(page.getVersion()));
                }
                appDefElementI18nService.saveAll(params.getI18ns());
            }
            ApplicationContextHolder.getBean(AuditDataFacadeService.class).saveAuditDataLog(new AuditDataLogDto().name(page.getName())
                    .diffEntity(page, null).remark("创建新版本"));
        } else {
            page = this.appPageDefinitionService.getOne(uuid);
            ApplicationContextHolder.getBean(AuditDataFacadeService.class).saveAuditDataLog(new AuditDataLogDto().name(pageDefinition.getName())
                    .diffEntity(page, oldAppPageDefinition).remark(isUpdate ? "编辑页面" : "创建页面"));
        }
        if (CollectionUtils.isNotEmpty(params.getI18ns())) {
            for (AppDefElementI18nEntity i : params.getI18ns()) {
                i.setVersion(new BigDecimal(page.getVersion()));
            }
            appDefElementI18nService.saveAll(params.getI18ns());
        }
        return uuid;
    }

    @Override
    public AppPageDefinition authenticatePage(String uuid, String id) throws AuthenticationException {
        //TODO: 缓存
        AppPageDefinition pageDefinition = null;
        if (StringUtils.isNotBlank(uuid)) {
            pageDefinition = appPageDefinitionService.getOne(uuid);
        } else if (StringUtils.isNotBlank(id)) {
            pageDefinition = appPageDefinitionService.getLatestPageDefinition(id);
        }

        if (pageDefinition != null) {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            boolean debug = "true".equalsIgnoreCase(request.getHeader("debug"));
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                AppDefElementI18nEntity i18nEntity = appDefElementI18nService.getI18n(pageDefinition.getId(), null, "title", new BigDecimal(pageDefinition.getVersion()), IexportType.AppPageDefinition, LocaleContextHolder.getLocale().toString());
                if (i18nEntity != null) {
                    pageDefinition.setTitle(i18nEntity.getContent());
                }
            }
            if (debug) {
                return pageDefinition;
            }

            //FIXME: 待新版组织用户登录集成后放开
            if (!securityApiFacade.isGranted(pageDefinition.getId(), AppFunctionType.AppPageDefinition)) {
                throw new InsufficientAuthenticationException("未授权页面");
            }
            AppPageDefinitionBean pageBean = new AppPageDefinitionBean();
            org.springframework.beans.BeanUtils.copyProperties(pageDefinition, pageBean);
            List<AppPageResourceEntity> resourceEntities = appPageResourceService.listByAppPageUuid(pageDefinition.getUuid());
            pageBean.setI18ns(appDefElementI18nService.getI18ns(pageDefinition.getId(), null,
                    new BigDecimal(pageDefinition.getVersion()), IexportType.AppPageDefinition, LocaleContextHolder.getLocale().toString()));
            if (CollectionUtils.isNotEmpty(resourceEntities)) {
                for (AppPageResourceEntity res : resourceEntities) {
                    if (res.getIsProtected()) {
                        if (!securityApiFacade.isGranted(pageDefinition.getId() + ":" + res.getId(), AppFunctionType.AppWidgetFunctionElement)) {
                            pageBean.getUnauthorizedResource().add(res.getId());
                            continue;
                        }
                    }

                    // FIXME:判断元素的关系资源是否有权限：例如导航关系到的某个页面，或者导航关联模块等
                    // 暂时屏蔽，待新用户登录完全后放开
                    if (StringUtils.isNotBlank(res.getResourceId()) && StringUtils.isNotBlank(res.getResourceType())
                            && !securityApiFacade.isGranted(res.getResourceId(), res.getResourceType())
                    ) {
                        pageBean.getUnauthorizedResource().add(res.getId());
                    }

                }
            }
            return pageBean;
        }
        return null;
    }

    @Override
    public List<String> getUnauthorizedAppPageResource(String pageUuid) {
        AppPageDefinition pageDefinition = appPageDefinitionService.getOne(pageUuid);
        List<AppPageResourceEntity> resourceEntities = appPageResourceService.listByAppPageUuid(pageUuid);
        List<String> ids = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(resourceEntities)) {
            for (AppPageResourceEntity res : resourceEntities) {
                if (res.getIsProtected()) {
                    if (!securityApiFacade.isGranted(pageDefinition.getId() + ":" + res.getId(), AppFunctionType.AppWidgetFunctionElement)) {
                        ids.add(res.getId());
                        continue;
                    }
                }

                // FIXME:判断元素的关系资源是否有权限：例如导航关系到的某个页面，或者导航关联模块等
                if (StringUtils.isNotBlank(res.getResourceId()) && StringUtils.isNotBlank(res.getResourceType())
                        && !securityApiFacade.isGranted(res.getResourceId(), res.getResourceType())
                ) {
                    ids.add(res.getId());
                }

            }
        }

        return ids;
    }

    public AppFunctionSource convert2AppFunctionSource(AppWidgetDefinition appWidgetDefinition) {
        String uuid = appWidgetDefinition.getUuid();
        String fullName = appWidgetDefinition.getName() + "_" + appWidgetDefinition.getTitle();
        String name = "组件定义_" + fullName;
        String id = appWidgetDefinition.getId();
        String code = id.hashCode() + org.apache.commons.lang.StringUtils.EMPTY;
        String category = AppFunctionType.AppWidgetDefinition;
        Map<String, Object> extras = new HashMap<String, Object>();
        extras.put(IdEntity.UUID, uuid);
        extras.put("title", appWidgetDefinition.getName());
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category, false, null, true,
                extras);
    }

    private Collection<AppFunctionSource> convertFunctionElement2AppFunctionSource(FunctionElement functionElement,
                                                                                   AppWidgetDefinition appWidgetDefinition) {
        List<AppFunctionSource> appFunctionSources = Lists.newArrayList();
        String entityUuid = functionElement.getUuid();
        String entityId = functionElement.getId();
        String appFunctionType = functionElement.getFunctionType();
        boolean isRef = functionElement.isRef();
        // 1、组件引用的平台资源功能
        if (isRef && org.apache.commons.lang.StringUtils.isNotBlank(entityUuid) && org.apache.commons.lang.StringUtils.isNotBlank(appFunctionType)) {
            List list = appFunctionSourceManager.getAppFunctionSources(entityUuid, appFunctionType);
            if (CollectionUtils.isNotEmpty(list)) {
                appFunctionSources.addAll(list);
            } else {
                AppFunctionSource source = new SimpleAppFunctionSource(StringUtils.isBlank(functionElement.getUuid()) ? SnowFlake.getId() + "" : functionElement.getUuid(), functionElement.getName(), functionElement.getName(), functionElement.getId(),
                        functionElement.getCode(), null, null,
                        functionElement.getFunctionType(), StringUtils.isNotBlank(functionElement.getExportType()), functionElement.getFunctionType()
                        , true, null);
                appFunctionSources.add(source);
            }
        } else if (isRef && StringUtils.isNotBlank(entityId) && StringUtils.isNotBlank(appFunctionType)) {
            List list = appFunctionSourceManager.getAppFunctionSourcesById(entityId, appFunctionType);
            if (CollectionUtils.isNotEmpty(list)) {
                appFunctionSources.addAll(list);
            } else {
                AppFunctionSource source = new SimpleAppFunctionSource(DigestUtils.md5Hex(entityId + appFunctionType), functionElement.getName(), functionElement.getName(), functionElement.getId(),
                        functionElement.getCode(), null, null,
                        functionElement.getFunctionType(), org.apache.commons.lang.StringUtils.isNotBlank(functionElement.getExportType()), functionElement.getFunctionType()
                        , true, null);
                appFunctionSources.add(source);
            }
        } else {
            // 2、组件元素功能
            String uuid = DigestUtils.md5Hex(functionElement.getUuid() + appWidgetDefinition.getId() + appWidgetDefinition.getAppPageUuid());
            String tile = org.apache.commons.lang.StringUtils.isNotBlank(appWidgetDefinition.getTitle()) ? appWidgetDefinition.getTitle() : appWidgetDefinition.getName();
            String fullName = tile + "_" + functionElement.getName();
            String name = functionElement.getName();
            String id = functionElement.getId();
            String code = functionElement.getCode();
            String category = AppFunctionType.AppWidgetFunctionElement;
            Map<String, Object> extras = new HashMap<String, Object>();
            extras.put("widgetId", appWidgetDefinition.getId());
            extras.put("title", functionElement.getName());
            extras.put(IdEntity.UUID, uuid);
            AppFunctionSource source = new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null,
                    category, false, category, true, extras);
            appFunctionSources.add(source);
        }
        return appFunctionSources;
    }

}
