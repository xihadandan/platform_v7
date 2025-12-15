/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.bean.AppPageDefinitionParamDto;
import com.wellsoft.pt.app.dao.AppPageDefinitionDao;
import com.wellsoft.pt.app.dto.AppPageResourceDto;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppPageResourceEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.app.service.AppPageResourceService;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.audit.dto.AuditDataLogDto;
import com.wellsoft.pt.security.audit.facade.service.AuditDataFacadeService;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	t		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
@Service
public class AppPageDefinitionServiceImpl extends
        AbstractJpaServiceImpl<AppPageDefinition, AppPageDefinitionDao, String> implements
        AppPageDefinitionService {

    private static final String GET_LATEST_VERSION_BY_UUID = "select max(t1.version) from AppPageDefinition t1 where t1.id = (select t2.id from AppPageDefinition t2 where t2.uuid = :uuid)";

    private static final String GET_APP_PAGE_DEFINITION_UUIDS_BY_APP_PI_UUID = "select t.uuid from AppPageDefinition t where t.appPiUuid = :appPiUuid order by t.code desc ,t.version desc";

    private static final String GET_DEFAULT_APP_PAGE_DEFINITION_UUIDS_BY_APP_PI_UUID = "select t.uuid from AppPageDefinition t where t.appPiUuid = :appPiUuid and t.isDefault = :isDefault order by t.code asc";

    private static final String GET_BY_USER_ID_AND_APP_PI_UUID = "from AppPageDefinition t where t.userId = :userId and t.appPiUuid = :appPiUuid order by t.isDefault desc, t.code asc";
    @Autowired
    private IdGeneratorService idGeneratorService;

    @Autowired
    private AppPageResourceService appPageResourceService;

    @Autowired
    private SecurityApiFacade securityApiFacade;

    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;
    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    @Override
    @Transactional
    public void save(AppPageDefinition entity) {
        if (StringUtils.isBlank(entity.getId())) {
            entity.setId(AppConstants.PAGE_PREFIX + idGeneratorService.getBySysDate());
        }
        super.save(entity);
    }

    @Override
    @Transactional
    public void deleteByUuid(String pageUuid) {
        // 删除页面资源
        appPageResourceService.removeByAppPageUuid(pageUuid);
        // 删除页面组件
        appWidgetDefinitionService.removeByAppPageUuid(pageUuid);

        delete(pageUuid);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        List<AppPageDefinition> pageDefinitions = dao.listByFieldEqValue("id", id);
        if (CollectionUtils.isNotEmpty(pageDefinitions)) {
            for (AppPageDefinition page : pageDefinitions) {
                deleteByUuid(page.getUuid());
                ApplicationContextHolder.getBean(AuditDataFacadeService.class).saveAuditDataLog(
                        new AuditDataLogDto().name(page.getName()).operation("delete_page").remark("删除页面")
                                .diffEntity(null, page));
            }
        }
    }

    @Override
    public AppPageDefinition get(String uuid) {
        return getOne(uuid);
    }

    @Override
    public AppPageDefinition getLatestUuidAndVersion(String id) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("id", id);
        List<AppPageDefinition> appPageDefinition = this.dao.listBySQL("select uuid , version from APP_PAGE_DEFINITION a where a.version = (select max(t1.version) from APP_PAGE_DEFINITION t1 where t1.id=:id) and a.id =:id", values);
        return CollectionUtils.isNotEmpty(appPageDefinition) ? appPageDefinition.get(0) : null;
    }

    @Override
    public AppPageDefinition getLatestPageDefinition(String id) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("id", id);
        List<AppPageDefinition> appPageDefinition = this.dao.listByHQL("  from AppPageDefinition a where a.version = (select max(t1.version) from AppPageDefinition t1 where t1.id=:id) and a.id =:id", values);
        return CollectionUtils.isNotEmpty(appPageDefinition) ? appPageDefinition.get(0) : null;
    }

    public List<AppPageDefinition> getPageDefinitionVersions(String id) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("id", id);
        List<AppPageDefinition> appPageDefinition = this.dao.listBySQL("select uuid , version ,name , create_time ,creator from APP_PAGE_DEFINITION a where a.id =:id", values);
        return appPageDefinition;
    }

    @Override
    public AppPageDefinition getById(String id) {
        List<AppPageDefinition> appPageDefinitions = this.dao.listByFieldEqValue("id", id);
        return CollectionUtils.isNotEmpty(appPageDefinitions) ? appPageDefinitions.get(0) : null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageDefinitionService#getByAppPiUuid(java.lang.String)
     */
    @Override
    public List<String> getAppPageDefinitionUuidsByAppPiUuid(String appPiUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("appPiUuid", appPiUuid);
        return this.dao.listCharSequenceByHQL(GET_APP_PAGE_DEFINITION_UUIDS_BY_APP_PI_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageDefinitionService#getDefaultAppPageDefinitionUuidsByAppPiUuid(java.lang.String)
     */
    @Override
    public List<String> getDefaultAppPageDefinitionUuidsByAppPiUuid(String appPiUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("appPiUuid", appPiUuid);
        values.put("isDefault", true);
        return this.dao.listCharSequenceByHQL(GET_DEFAULT_APP_PAGE_DEFINITION_UUIDS_BY_APP_PI_UUID,
                values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageDefinitionService#getByUserIdAndAppPiUuid(java.lang.String, java.lang.String)
     */
    @Override
    public List<AppPageDefinition> getByUserIdAndAppPiUuid(String userId, String appPiUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("userId", userId);
        values.put("appPiUuid", appPiUuid);
        return this.dao.listByHQL(GET_BY_USER_ID_AND_APP_PI_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageDefinitionService#getAllAvailableAppPageInfoByUserIdAndAppPiUuid(java.lang.String, java.lang.String)
     */
    @Override
    public List<AppPageDefinition> getAllAvailableAppPageInfoByUserIdAndAppPiUuid(String userId,
                                                                                  String appPiUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("userId", userId);
        values.put("appPiUuid", appPiUuid);
        return this.dao.listByNameSQLQuery("getAllAvailableAppPageInfoByUserIdAndAppPiUuid",
                values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageDefinitionService#updateUserDefaultPortalByPageUuid(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void updateUserDefaultPortalByPageUuid(String userId, String pageUuid) {
        AppPageDefinition appPageDefinition = this.get(pageUuid);
        String appPiUuid = appPageDefinition.getAppPiUuid();
        Map<String, Object> values = Maps.newHashMap();
        values.put("appPiUuid", appPiUuid);
        values.put("userId", userId);
        values.put("isDefalt", false);
        // 重置非默认门户
        String resetHql = "update AppPageDefinition t set t.isDefault = :isDefalt where t.userId = :userId and t.appPiUuid = :appPiUuid";
        this.dao.updateByHQL(resetHql, values);
        // 设置指定页面为默认门户
        values.put("pageUuid", pageUuid);
        values.put("isDefalt", true);
        String setDefaultHql = "update AppPageDefinition t set t.isDefault = :isDefalt where t.userId = :userId and t.appPiUuid = :appPiUuid and t.uuid = :pageUuid";
        this.dao.updateByHQL(setDefaultHql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageDefinitionService#getLatestVersionByUuid(java.lang.String)
     */
    @Override
    public Double getLatestVersionByUuid(String uuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        String version = this.dao.getCharSequenceByHQL(GET_LATEST_VERSION_BY_UUID, values);
        if (StringUtils.isBlank(version)) {
            version = "1";
        }
        return Double.valueOf(version);
    }

    @Override
    public List<AppPageDefinition> getAll() {
        return listAll();
    }

    @Override
    public List<AppPageDefinition> getAllByWtype(String wtype) {

        return this.dao.listByFieldEqValue("wtype", wtype);
    }

    @Override
    public List<AppPageDefinition> findByExample(AppPageDefinition example) {
        return this.dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageDefinitionService#findByExample(com.wellsoft.pt.app.entity.AppPageDefinition, java.lang.String)
     */
    @Override
    public List<AppPageDefinition> findByExample(AppPageDefinition example, String order) {
        return this.dao.listByEntityAndPage(example, null, order);
    }

    @Override
    public AppPageDefinition getDefaultPageDefinition(String appId, Boolean isPc) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("appId", appId);
        if (isPc != null) {
            params.put("isPc", BooleanUtils.isTrue(isPc) ? "1" : "0");
        }
        List<AppPageDefinition> list = this.dao.listByHQL("from AppPageDefinition where appId=:appId and isPc=:isPc and isDefault=true", params);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    @Transactional
    public void remove(String uuid) {
        delete(uuid);
    }

    @Override
    @Transactional
    public void removeAll(Collection<AppPageDefinition> entities) {
        deleteByEntities(entities);
    }

    @Override
    @Transactional
    public void remove(AppPageDefinition entity) {
        this.dao.delete(entity);
    }

    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    @Override
    @Transactional
    public void updateUnDefaultByAppPiUuid(String appPiUuid) {
        this.dao.updateUnDefaultByAppPiUuid(appPiUuid);
    }

    @Override
    @Transactional
    public void updateDefaultTrueByUuid(String appPiUuid, String uuid) {
        this.dao.updateDefaultTrueByUuid(appPiUuid, uuid);
    }

    @Override
    public List<AppPageDefinition> listByAppId(String appId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("appId", appId);
        return this.dao.listBySQL("select id,uuid,name,wtype,version,app_id,is_pc ,is_default ,designable, layout_fixed,remark ,theme from APP_PAGE_DEFINITION where app_id=:appId", params);
    }

    @Override
    public List<AppPageDefinition> listRecentVersionPageByAppId(String appId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("appId", appId);
        return this.dao.listBySQL("select a.id,a.uuid,a.name,a.wtype,a.version,a.app_id,a.is_pc ,a.is_default " +
                ",a.designable, a.layout_fixed,a.remark ,a.theme from APP_PAGE_DEFINITION a where a.app_id=:appId and a.version = (" +
                " select max(m.version) from app_page_definition m where m.id = a.id and m.app_id =:appId" +
                ")", params);

    }

    @Override
    @Transactional
    public void updateBasicInfo(AppPageDefinitionParamDto params) {
        AppPageDefinition definition = getOne(params.getUuid());
        if (definition != null) {
            definition.setName(params.getName());
            definition.setRemark(params.getRemark());
            definition.setCode(params.getCode());
            definition.setId(params.getId());
            definition.setTitle(params.getTitle());
            definition.setDesignable(params.getDesignable());
            definition.setLayoutFixed(params.getLayoutFixed());
            definition.setIsAnonymous(params.getIsAnonymous());
            save(definition);
            if (CollectionUtils.isNotEmpty(params.getI18ns())) {
                appDefElementI18nService.deleteAllCodeI18n(null, definition.getId(), new BigDecimal(definition.getVersion()), IexportType.AppPageDefinition);
                for (AppDefElementI18nEntity i : params.getI18ns()) {
                    i.setVersion(new BigDecimal(definition.getVersion()));
                    i.setDefId(definition.getId());
                }
                appDefElementI18nService.saveAll(params.getI18ns());
            }
        }
    }

    @Override
    @Transactional
    public void updatePageProtected(String pageDefinitionUuid, Boolean isProtected, List<AppPageResourceDto> resourceDtos) {
        if (CollectionUtils.isNotEmpty(resourceDtos)) {
            AppPageDefinition pageDefinition = getOne(pageDefinitionUuid);
            for (AppPageResourceDto temp : resourceDtos) {
                AppPageResourceEntity resEntity = null;
                if (StringUtils.isNotBlank(temp.getUuid())) {
                    resEntity = appPageResourceService.getOne(temp.getUuid());
                } else if (StringUtils.isNotBlank(temp.getId())) {
                    resEntity = appPageResourceService.getByIdAndAppPageUuid(temp.getId(), pageDefinitionUuid);
                }
                if (resEntity != null) {
                    resEntity.setIsProtected(temp.getIsProtected());
                    appPageResourceService.save(resEntity);
                    if (!"vPage".equalsIgnoreCase(pageDefinition.getWtype())) {
                        String resourceId = resEntity.getPrivilegeResouceId();
                        if (!Boolean.TRUE.equals(resEntity.getIsProtected())) {
                            securityApiFacade.addAnonymousResource(resourceId);
                        } else {
                            securityApiFacade.removeAnonymousResource(resourceId);
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void updateDesignable(String uuid, Boolean designable) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        params.put("designable", designable);
        this.dao.updateByHQL("update AppPageDefinition set designable=:designable where uuid=:uuid", params);
    }

    @Override
    public String getPageIdByUuid(String pageUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("uuid", pageUuid);
        return dao.getCharSequenceBySQL("select id from app_page_definition where uuid=:uuid", param);
    }

    @Override
    @Transactional
    public void updatePageTheme(List<AppPageDefinitionParamDto> dtos) {
        if (CollectionUtils.isNotEmpty(dtos)) {
            Map<String, Object> params = Maps.newHashMap();
            for (AppPageDefinitionParamDto dto : dtos) {
                params.put("uuid", dto.getUuid());
                params.put("theme", dto.getTheme());
                this.dao.updateByHQL("update AppPageDefinition set theme=:theme where uuid=:uuid", params);
            }
        }
    }

    @Override
    public List<AppPageDefinition> listByAppIds(List<String> appIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("appIds", appIds);
        return this.dao.listBySQL("select id,uuid,name,wtype,version,app_id,is_pc ,is_default ,designable, layout_fixed,remark ,theme from APP_PAGE_DEFINITION where app_id in :appIds", params);

    }

    @Override
    public List<AppPageDefinition> listLatestVersionPageByAppIds(List<String> appIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("appIds", appIds);
        return this.dao.listByNameSQLQuery("queryLatestVerPageByAppIds", params);
    }

    @Override
    public List<AppPageDefinition> listLatestVersionPageBySystem(String system) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        return this.dao.listByNameSQLQuery("queryLatestVerPageBySystem", params);
    }

    @Override
    public boolean existId(String id) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", id);
        return this.dao.countBySQL("select count(1) from app_page_definition where id=:id", param) > 0;
    }

    @Override
    @Transactional
    public void updateAnonymous(String uuid, Boolean anonymous) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        params.put("anonymous", anonymous);
        this.dao.updateByHQL("update AppPageDefinition set isAnonymous=:anonymous where uuid=:uuid", params);
        AppPageDefinition pageDefinition = getOne(uuid);
        if (pageDefinition != null) {
            if (Boolean.TRUE.equals(anonymous)) {
                securityApiFacade.addAnonymousResource(pageDefinition.getId());
            } else {
                securityApiFacade.removeAnonymousResource(pageDefinition.getId());
            }
        }


    }

    @Override
    @Transactional
    public void updateEnabled(String uuid, Boolean enabled) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        params.put("enabled", enabled);
        this.dao.updateByHQL("update AppPageDefinition set enabled=:enabled where uuid=:uuid", params);
    }

    @Override
    @Transactional
    public void deleteByIds(List<String> ids) {
        for (String id : ids) {
            this.deleteById(id);
        }
    }

    @Override
    public List<AppPageDefinition> listMaxVersionPagesByAppIdAndTenant(String appId, String tenant) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("appId", appId);
        params.put("tenant", tenant);
        return this.dao.listBySQL("select a.id,a.uuid,a.name,a.wtype,a.version,a.app_id,a.is_pc ,a.is_default " +
                ",a.designable, a.layout_fixed, a.remark , a.theme, a.is_anonymous from APP_PAGE_DEFINITION a where a.app_id =:appId "
                + " and  a.version = (" +
                " select max(m.version) from app_page_definition m where m.id = a.id " +
                ")"
                + (StringUtils.isNotBlank(tenant) ? " and a.tenant=:tenant" : ""), params);

    }

    @Override
    public List<String> getAllAnonymousPageDefinitionIds() {
        Map<String, Object> param = Maps.newHashMap();
        param.put("anonymous", 1);
        return dao.listCharSequenceBySQL("select distinct id from app_page_definition where is_anonymous=:anonymous", param);
    }
}
