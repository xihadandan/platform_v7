/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.context.util.tree.TreeUtils;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.dao.AppProductIntegrationDao;
import com.wellsoft.pt.app.entity.*;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.app.support.NoPageDefinition;
import com.wellsoft.pt.app.support.UnauthorizePageDefinition;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期            修改内容
 * 2016-07-24.1 zhulh       2016-07-24      Create
 * </pre>
 * @date 2016-07-24
 */
@Service
public class AppProductIntegrationServiceImpl extends
        AbstractJpaServiceImpl<AppProductIntegration, AppProductIntegrationDao, String> implements
        AppProductIntegrationService {

    private static final String REMOVE_BY_PRODUCT_UUID = "delete from AppProductIntegration t where t.appProductUuid = :appProductUuid";

    private static final String REMOVE_BY_DATA_UUID_AND_TYPE = "delete from AppProductIntegration t where t.dataUuid = :dataUuid and t.dataType = :dataType";

    private static final String REMOVE_FUNCTION_BY_PARENT_DATA_ID = "delete from AppProductIntegration t where t.dataUuid = :dataUuid and t.dataType ='4' and exists ( select 1 from AppProductIntegration p where p.dataId=:dataId and p.uuid=t.parentUuid)";

    private static final String GET_TOP_BYP_RODUCT_UUID = "from AppProductIntegration t where t.appProductUuid = :appProductUuid and t.parentUuid is null order by t.sortOrder";

    private static final String UPDATE_PI_INFO = "update AppProductIntegration t set t.dataName = :dataName where t.dataUuid = :dataUuid";

    private static final String GET_BY_DATA_UUID_AND_TYPE = " from AppProductIntegration t where t.dataUuid = :dataUuid and t.dataType = :dataType";

    private static final String RESET_APP_PAGE_DEFINITION = "update AppProductIntegration t set t.appPageUuid = null, t.appPageReference = false where t.uuid = :uuid";

    private static final String GET_APP_SYSTEM_UUID_BY_UUID = "select t.appSystemUuid from AppProductIntegration t where t.uuid = :uuid";

    @Autowired
    private AppPageDefinitionService appPageDefinitionService;

    @Autowired
    private SecurityAuditFacadeService securityApiFacade;

    @Override
    public AppProductIntegration get(String uuid) {
        return getOne(uuid);
    }

    @Override
    public List<AppProductIntegration> getAll() {
        return listAll();
    }

    @Override
    public List<AppProductIntegration> getTopByProductUuid(String productUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appProductUuid", productUuid);
        return listByHQL(GET_TOP_BYP_RODUCT_UUID, values);
    }

    @Override
    public List<AppProductIntegration> getChildren(String uuid) {
        AppProductIntegration example = new AppProductIntegration();
        example.setParentUuid(uuid);
        return this.dao.listByEntity(example, null, "sortOrder asc", null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#hasChildren(java.lang.String)
     */
    @Override
    public boolean hasChildren(String uuid) {
        AppProductIntegration example = new AppProductIntegration();
        example.setParentUuid(uuid);
        return this.dao.countByEntity(example) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#hasChildrenByDataUuid(java.lang.String)
     */
    @Override
    public boolean hasChildrenByDataUuid(String dataUuid) {
        AppProductIntegration example = new AppProductIntegration();
        example.setDataUuid(dataUuid);
        return this.dao.countByEntity(example) > 0;
    }

    @Override
    public List<AppProductIntegration> findByExample(AppProductIntegration example) {
        return this.dao.listByEntity(example);
    }

    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    @Override
    @Transactional
    public void removeByDataUuidAndType(String dataUuid, String dataType) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataUuid", dataUuid);
        values.put("dataType", dataType);
        this.dao.deleteByHQL(REMOVE_BY_DATA_UUID_AND_TYPE, values);
    }

    @Override
    @Transactional
    public void removeFunctionUnderParentDataId(String parentDataId, String functionDataUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataId", parentDataId);
        values.put("dataUuid", functionDataUuid);
        this.dao.deleteByHQL(REMOVE_FUNCTION_BY_PARENT_DATA_ID, values);
    }

    @Override
    public List<AppProductIntegration> getByDataUuidAndType(String dataUuid, String dataType) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataUuid", dataUuid);
        values.put("dataType", dataType);
        List<AppProductIntegration> list = this.dao.listByHQL(GET_BY_DATA_UUID_AND_TYPE, values);
        return list;
    }

    @Override
    @Transactional
    public void removeAll(Collection<AppProductIntegration> entities) {
        deleteByEntities(entities);
    }

    @Override
    @Transactional
    public void remove(AppProductIntegration entity) {
        this.dao.delete(entity);
    }

    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    @Override
    @Transactional
    public void removeByProductUuid(String productUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appProductUuid", productUuid);
        this.dao.deleteByHQL(REMOVE_BY_PRODUCT_UUID, values);
    }

    @Override
    public Long count(String dataUuid, Integer appType) {
        AppProductIntegration example = new AppProductIntegration();
        example.setDataUuid(dataUuid);
        example.setDataType(appType + StringUtils.EMPTY);
        return this.dao.countByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#getAppPageDefinition(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Deprecated
    public AppPageDefinition getAppPageDefinition(String sysId, String moduleId, String appId) {
        AppProductIntegration example = new AppProductIntegration();
        example.setDataId(sysId);
        example.setDataType(AppType.SYSTEM.toString());
        List<AppProductIntegration> appProductIntegrations = this.dao.listByEntity(example);
        AppProductIntegration appProductIntegration = appProductIntegrations.get(0);
        String productUuid = appProductIntegration.getAppProductUuid();
        String systemUuid = appProductIntegration.getAppSystemUuid();
        String pageUuid = appProductIntegration.getAppPageUuid();

        String appPageUuid = null;
        if (StringUtils.isNotBlank(appId)) {
            example = new AppProductIntegration();
            example.setAppProductUuid(productUuid);
            example.setAppSystemUuid(systemUuid);
            example.setDataId(appId);
            example.setDataPath(Separator.SLASH.getValue() + sysId + Separator.SLASH.getValue() + moduleId
                    + Separator.SLASH.getValue() + appId);
            example.setDataType(AppType.APPLICATION.toString());
            appProductIntegrations = this.dao.listByEntity(example);
            if (!appProductIntegrations.isEmpty()) {
                appProductIntegration = appProductIntegrations.get(0);
                if (StringUtils.isNotBlank(appProductIntegration.getAppPageUuid())) {
                    pageUuid = appProductIntegration.getAppPageUuid();
                    appPageUuid = pageUuid;
                }
            }
        }

        if (StringUtils.isBlank(appPageUuid) && StringUtils.isNotBlank(moduleId)) {
            example = new AppProductIntegration();
            example.setAppProductUuid(productUuid);
            example.setAppSystemUuid(systemUuid);
            example.setDataId(moduleId);
            example.setDataPath(Separator.SLASH.getValue() + sysId + Separator.SLASH.getValue() + moduleId);
            example.setDataType(AppType.MODULE.toString());
            appProductIntegrations = this.dao.listByEntity(example);
            if (!appProductIntegrations.isEmpty()) {
                appProductIntegration = appProductIntegrations.get(0);
                if (StringUtils.isNotBlank(appProductIntegration.getAppPageUuid())) {
                    pageUuid = appProductIntegration.getAppPageUuid();
                }
            }
        }

        return StringUtils.isBlank(pageUuid) ? new NoPageDefinition() : appPageDefinitionService.getOne(pageUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#getAppPageDefinition(java.lang.String)
     */
    @Override
    public AppPageDefinition getAppPageDefinition(String piUuid) {
        AppProductIntegration appProductIntegration = this.dao.getOne(piUuid);
        if (appProductIntegration == null) {
            return new NoPageDefinition();
        }
        String pageUuid = getAppPageDefinitionUuid(appProductIntegration);
        if (StringUtils.isBlank(pageUuid)) {
            if (CollectionUtils.isNotEmpty(appPageDefinitionService
                    .getAppPageDefinitionUuidsByAppPiUuid(appProductIntegration.getUuid()))) {
                //有页面但是都没返回，说明没有权限
                return new UnauthorizePageDefinition();
            }
            return new NoPageDefinition();
        }
        /*String parentUuid = appProductIntegration.getParentUuid();
        while (StringUtils.isBlank(pageUuid) && StringUtils.isNotBlank(parentUuid)) {
            appProductIntegration = this.dao.getOne(parentUuid);
            pageUuid = getAppPageDefinitionUuid(appProductIntegration);
            parentUuid = appProductIntegration.getParentUuid();
        }*/
        // 获取默认页
        if (StringUtils.isBlank(pageUuid)) {
            pageUuid = getDefaultAppPageDefinitionUuid(appProductIntegration);
        }
        return StringUtils.isBlank(pageUuid) ? new NoPageDefinition() : appPageDefinitionService.getOne(pageUuid);
    }

    /**
     * @param appProductIntegration
     * @return
     */
    private String getDefaultAppPageDefinitionUuid(AppProductIntegration appProductIntegration) {
        List<String> appPageDefinitions = appPageDefinitionService
                .getDefaultAppPageDefinitionUuidsByAppPiUuid(appProductIntegration.getUuid());
        if (CollectionUtils.isNotEmpty(appPageDefinitions)) {
            return appPageDefinitions.get(0);
        }
        return null;
    }

    /**
     * @param appProductIntegration
     * @return
     */
    private String getAppPageDefinitionUuid(AppProductIntegration appProductIntegration) {
        String configPageUuid = appProductIntegration.getAppPageUuid();
        if (StringUtils.isNotBlank(configPageUuid)) {
            return configPageUuid;
        }
        List<String> appPageDefinitionUuids = appPageDefinitionService
                .getAppPageDefinitionUuidsByAppPiUuid(appProductIntegration.getUuid());
        for (String appPageDefinitionUuid : appPageDefinitionUuids) {
            if (SpringSecurityUtils.hasAnyRole(BuildInRole.ROLE_TENANT_ADMIN.name(), BuildInRole.ROLE_ADMIN.name())
                    || securityApiFacade.isGranted(appPageDefinitionUuid, AppFunctionType.AppPageDefinition)) {
                return appPageDefinitionUuid;
            }
            // FIXME
            // bug#69691 在新版流程定义未处理好之前，该权限建议取消控制
            if (StringUtils.equals("bddce0f2-0f9e-4457-a87e-d2fd371d03af", appPageDefinitionUuid)) {
                return appPageDefinitionUuid;
            }
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#getAppPageDefinition(java.lang.String, java.lang.String)
     */
    @Override
    public AppPageDefinition getAppPageDefinition(String piUuid, String pageUuid) {
        if (StringUtils.isNotBlank(pageUuid)) {
            return appPageDefinitionService.getOne(pageUuid);
        }
        return getAppPageDefinition(piUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#getUserAppPageDefinition(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public AppPageDefinition getUserAppPageDefinition(String userId, String piUuid, String pageUuid) {
        if (StringUtils.isNotBlank(pageUuid)) {
            boolean isTenantAdmin = SpringSecurityUtils.hasAnyRole(BuildInRole.ROLE_TENANT_ADMIN.name());

            if ("ac525dcd-50b7-42e9-95b7-658b117ac19b".equals(pageUuid)
                    && (((UserDetails) SpringSecurityUtils.getCurrentUser()).isAdmin() || isTenantAdmin)) {//单位管理后台页面，目前先写死
                return appPageDefinitionService.getOne(pageUuid);
            }
            // 调试模式下，不需要判断权限
            if (!isTenantAdmin && (!AppContextHolder.getContext().isDebug() && !securityApiFacade.isGranted(pageUuid, AppFunctionType.AppPageDefinition))) {
                return new UnauthorizePageDefinition();
            }
            return appPageDefinitionService.getOne(pageUuid);
        }
        List<AppPageDefinition> userAppPageDefinitions = appPageDefinitionService.getByUserIdAndAppPiUuid(userId,
                piUuid);
        if (CollectionUtils.isNotEmpty(userAppPageDefinitions)) {
            return userAppPageDefinitions.get(0);
        }
        return getAppPageDefinition(piUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#getAllAvailableAppPageInfoByUserIdAndAppPiUuid(java.lang.String, java.lang.String)
     */
    @Override
    public List<AppPageDefinition> getAllAvailableAppPageInfoByUserIdAndAppPiUuid(String userId, String appPiUuid) {
        List<AppPageDefinition> returnAppPageDefinitions = Lists.newArrayList();
        List<AppPageDefinition> appPageDefinitions = appPageDefinitionService
                .getAllAvailableAppPageInfoByUserIdAndAppPiUuid(userId, appPiUuid);
        for (AppPageDefinition appPageDefinition : appPageDefinitions) {
            // 默认页
            if (Boolean.TRUE.equals(appPageDefinition.getIsDefault())) {
                returnAppPageDefinitions.add(appPageDefinition);
                continue;
            }
            // 公共页面权限判断
            if (StringUtils.isBlank(appPageDefinition.getUserId())
                    && !securityApiFacade.isGranted(appPageDefinition.getUuid(), AppFunctionType.AppPageDefinition)) {
                continue;
            }
            returnAppPageDefinitions.add(appPageDefinition);
        }
        return returnAppPageDefinitions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#createAppSystem(java.lang.String, com.wellsoft.pt.app.entity.AppSystem)
     */
    @Override
    public AppProductIntegration createAppSystem(String appProductUuid, AppSystem appSystem) {
        AppProductIntegration example = new AppProductIntegration();
        example.setDataType(AppType.SYSTEM.toString());
        example.setDataUuid(appSystem.getUuid());
        example.setAppProductUuid(appProductUuid);
        List<AppProductIntegration> appProductIntegrations = this.dao.listByEntity(example);
        if (CollectionUtils.isNotEmpty(appProductIntegrations)) {
            throw new RuntimeException("系统[" + appSystem.getName() + "]已添加到产品集成信息，不能再次添加!");
        }

        String dataPath = Separator.SLASH.getValue() + appSystem.getId();

        AppProductIntegration functipnAppProductIntegration = new AppProductIntegration();

        String uuid = DigestUtils.md5Hex(dataPath + AppType.SYSTEM + appSystem.getUuid());
        functipnAppProductIntegration.setUuid(uuid);
        functipnAppProductIntegration.setAppProductUuid(appProductUuid);
        functipnAppProductIntegration.setAppSystemUuid(appSystem.getUuid());
        functipnAppProductIntegration.setDataUuid(appSystem.getUuid());
        functipnAppProductIntegration.setDataName(appSystem.getName());
        functipnAppProductIntegration.setDataId(appSystem.getId());
        functipnAppProductIntegration.setDataType(AppType.SYSTEM.toString());
        functipnAppProductIntegration.setDataPath(dataPath);
        functipnAppProductIntegration.setAppPageUuid(functipnAppProductIntegration.getAppPageUuid());
        functipnAppProductIntegration.setAppPageReference(functipnAppProductIntegration.getAppPageReference());
        functipnAppProductIntegration.setSortOrder(appProductIntegrations.size());
        functipnAppProductIntegration.setParentUuid(null);
        functipnAppProductIntegration.setIsProtected(true);
        this.dao.save(functipnAppProductIntegration);
        return functipnAppProductIntegration;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#addAppModule(java.lang.String, com.wellsoft.pt.app.entity.AppModule)
     */
    @Override
    public AppProductIntegration addAppModule(String piUuid, AppModule appModule) {
        AppProductIntegration appProductIntegration = this.dao.getOne(piUuid);
        String dataType = appProductIntegration.getDataType();
        if (!(AppType.SYSTEM.equals(Integer.valueOf(dataType)) || AppType.MODULE.equals(Integer.valueOf(dataType)))) {
            throw new RuntimeException("集成信息[" + appProductIntegration.getDataName() + "]不是系统或模块，不能添加模块!");
        }

        // 添加产品集成信息
        return addPiData(appProductIntegration, appModule.getUuid(), appModule.getId(), appModule.getName(),
                AppType.MODULE, true);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#addAppApplication(java.lang.String, com.wellsoft.pt.app.entity.AppApplication)
     */
    @Override
    public AppProductIntegration addAppApplication(String piUuid, AppApplication appApplication) {
        AppProductIntegration appProductIntegration = this.dao.getOne(piUuid);
        String dataType = appProductIntegration.getDataType();
        if (!(AppType.SYSTEM.equals(Integer.valueOf(dataType)) || AppType.MODULE.equals(Integer.valueOf(dataType)))) {
            throw new RuntimeException("集成信息[" + appProductIntegration.getDataName() + "]不是系统或模块，不能添加应用!");
        }

        // 添加产品集成信息
        return addPiData(appProductIntegration, appApplication.getUuid(), appApplication.getId(),
                appApplication.getName(), AppType.APPLICATION, true);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#addAppFunction(java.lang.String, com.wellsoft.pt.app.entity.AppFunction)
     */
    @Override
    public AppProductIntegration addAppFunction(String piUuid, AppFunction appFunction) {
        return addAppFunction(piUuid, appFunction, true);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#addAppFunction(java.lang.String, com.wellsoft.pt.app.entity.AppFunction, boolean)
     */
    @Override
    public AppProductIntegration addAppFunction(String piUuid, AppFunction appFunction, boolean isProtected) {
        AppProductIntegration appProductIntegration = this.dao.getOne(piUuid);
        String dataType = appProductIntegration.getDataType();
        if (!AppType.APPLICATION.equals(Integer.valueOf(dataType)) && !AppType.MODULE.equals(Integer.valueOf(dataType))) {
            throw new RuntimeException("集成信息[" + appProductIntegration.getDataName() + "]不是应用或者模块，不能添加功能!");
        }

        // 添加产品集成信息
        return addPiData(appProductIntegration, appFunction.getUuid(), appFunction.getId(), appFunction.getName(),
                AppType.FUNCTION, isProtected);
    }

    /**
     * @param appProductIntegration
     * @param dataUuid
     * @param dataId
     * @param dataName
     * @param dataType
     * @param isProtected
     * @return
     */
    private AppProductIntegration addPiData(AppProductIntegration appProductIntegration, String dataUuid,
                                            String dataId, String dataName, Integer dataType, boolean isProtected) {
        String dataPath = appProductIntegration.getDataPath() + Separator.SLASH.getValue() + dataId;

        int sortOrder = 0;
        AppProductIntegration functipnAppProductIntegration = new AppProductIntegration();
        AppProductIntegration example = new AppProductIntegration();
        example.setDataType(dataType.toString());
        example.setParentUuid(appProductIntegration.getUuid());
        example.setDataUuid(dataUuid);
        List<AppProductIntegration> appProductIntegrations = this.dao.listByEntity(example);
        if (!appProductIntegrations.isEmpty()) {
            functipnAppProductIntegration = appProductIntegrations.get(0);
            sortOrder = functipnAppProductIntegration.getSortOrder();
        } else {
            String uuid = DigestUtils.md5Hex(dataPath + dataType + dataUuid);
            functipnAppProductIntegration.setUuid(uuid);
            AppProductIntegration countExample = new AppProductIntegration();
            countExample.setDataType(dataType.toString());
            countExample.setParentUuid(appProductIntegration.getUuid());
            sortOrder = Long.valueOf(this.dao.countByEntity(countExample)).intValue();
        }

        functipnAppProductIntegration.setAppProductUuid(appProductIntegration.getAppProductUuid());
        functipnAppProductIntegration.setAppSystemUuid(appProductIntegration.getAppSystemUuid());
        functipnAppProductIntegration.setDataUuid(dataUuid);
        functipnAppProductIntegration.setDataName(dataName);
        functipnAppProductIntegration.setDataId(dataId);
        functipnAppProductIntegration.setDataType(dataType.toString());
        functipnAppProductIntegration.setDataPath(dataPath);
        functipnAppProductIntegration.setAppPageUuid(functipnAppProductIntegration.getAppPageUuid());
        functipnAppProductIntegration.setAppPageReference(functipnAppProductIntegration.getAppPageReference());
        functipnAppProductIntegration.setSortOrder(sortOrder);
        functipnAppProductIntegration.setParentUuid(appProductIntegration.getUuid());
        functipnAppProductIntegration.setIsProtected(isProtected);
        this.dao.save(functipnAppProductIntegration);
        this.dao.flushSession();
        return functipnAppProductIntegration;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updatPiDataName(ConfigurableIdEntity entity) {
        String dataUuid = entity.getUuid();
        String dataName = entity.getName();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataUuid", dataUuid);
        values.put("dataName", dataName);
        this.dao.updateByHQL(UPDATE_PI_INFO, values);
    }

    @Override
    @Transactional
    public void deleteByHQL(String hql, Map<String, Object> values) {
        this.dao.deleteByHQL(hql, values);
    }

    @Override
    public List<AppProductIntegration> queryAppProductIntegrationTree(String uuid, String[] dataTypes,
                                                                      String[] functionTypes) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.* FROM APP_PRODUCT_INTEGRATION a WHERE 1=1 ");
        if (ArrayUtils.isNotEmpty(dataTypes)) {
            sql.append(" AND a.data_type in (:dataTypes) ");
        }
        if (ArrayUtils.isNotEmpty(functionTypes)) {
            sql.append(" AND (a.data_type <> '4' or exists( select 1 from app_function af where af.uuid = a.data_uuid and af.type in (:functionTypes) )) ");
        }
        sql.append(" AND a.parent_uuid in (:uuids)");
        Map<String, Object> params = Maps.newHashMap();
        params.put("functionTypes", functionTypes);
        params.put("dataTypes", dataTypes);
        AppProductIntegration entity = dao.getOne(uuid);
        if (entity == null) {
            return Collections.EMPTY_LIST;
        }
        List<AppProductIntegration> list = Lists.newArrayList(entity);
        List<String> parentUuids = Lists.newArrayList(entity.getUuid());
        params.put("uuids", parentUuids);

        while (!parentUuids.isEmpty()) {
            params.put("uuids", parentUuids);
            List<AppProductIntegration> children = dao.listBySQL(sql.toString(), params);
            parentUuids.clear();
            if (CollectionUtils.isNotEmpty(children)) {
                list.addAll(children);
                for (AppProductIntegration i : children) {
                    parentUuids.add(i.getUuid());
                }
            }
        }

        return list;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#countByAppProductUuid(java.lang.String)
     */
    @Override
    public long countByAppProductUuid(String appProductUuid) {
        AppProductIntegration entity = new AppProductIntegration();
        entity.setAppProductUuid(appProductUuid);
        return this.dao.countByEntity(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#getSelfWithParentsByUuid(java.lang.String)
     */
    @Override
    public List<AppProductIntegration> getSelfWithParentsByUuid(String uuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        AppProductIntegration appProductIntegration = this.get(uuid);
        if (appProductIntegration == null) {
            return Collections.EMPTY_LIST;
        }
        String parentUuid = appProductIntegration.getParentUuid();
        List<AppProductIntegration> list = Lists.newArrayList(appProductIntegration);
        while (StringUtils.isNotBlank(parentUuid)) {
            AppProductIntegration data = this.get(parentUuid);
            if (data != null) {
                list.add(data);
                parentUuid = data.getParentUuid();
            } else {
                parentUuid = null;
            }
        }
        return Lists.reverse(list);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#getSelfWithParentUuidsByUuid(java.lang.String)
     */
    @Override
    public List<String> getSelfWithParentUuidsByUuid(String uuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        List<AppProductIntegration> appProductIntegrations = this.dao.listByNameSQLQuery(
                "getSelfWithParentUuidsByUuidQuery", values);
        List<String> appPiUuids = Lists.newArrayList();
        for (AppProductIntegration appProductIntegration : appProductIntegrations) {
            appPiUuids.add(appProductIntegration.getUuid());
        }
        return appPiUuids;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#getSelfWithChildrenByUuid(java.lang.String)
     */
    @Override
    public List<AppProductIntegration> getSelfWithChildrenByUuid(String uuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        List<AppProductIntegration> appProductIntegrations = this.dao.listByNameSQLQuery(
                "getSelfWithChildrenByUuidQuery", values);
        return appProductIntegrations;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#getSelfWithChildrenModuleAndAppDataIdsByDataId(java.lang.String)
     */
    @Override
    public List<String> getSelfWithChildrenModuleAndAppDataIdsByDataId(String dataId) {
        if (StringUtils.isBlank(dataId)) {
            return Collections.emptyList();
        }
        Map<String, Object> values = Maps.newHashMap();
        List<String> dataTypes = Lists.newArrayList();
        dataTypes.add(String.valueOf(AppType.SYSTEM));
        dataTypes.add(String.valueOf(AppType.MODULE));
        dataTypes.add(String.valueOf(AppType.APPLICATION));
        values.put("dataId", dataId);
        values.put("dataTypes", dataTypes);
        List<AppProductIntegration> productIntegrationList = this.dao.listByFieldEqValue("dataId", dataId);
        List<String> dataIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(productIntegrationList)) {
            dataIds.add(productIntegrationList.get(0).getDataId());
            List<String> parentUuids = Lists.newArrayList(productIntegrationList.get(0).getUuid());
            while (!parentUuids.isEmpty()) {
                List<AppProductIntegration> childs = this.dao.listByFieldInValues("parentUuid", parentUuids);
                parentUuids.clear();
                if (CollectionUtils.isNotEmpty(childs)) {
                    for (AppProductIntegration p : childs) {
                        if (dataTypes.contains(p.getDataType())) {
                            dataIds.add(p.getDataId());
                            parentUuids.add(p.getUuid());
                        }
                    }
                }


            }
        }
        return dataIds;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#updateAppProductUuid(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void updateAppProductUuid(String uuid, String appProductUuid) {
        // 确保从根结点开始更新
        AppProductIntegration appProductIntegration = this.dao.getOne(uuid);
        if (StringUtils.isNotBlank(appProductIntegration.getParentUuid())) {
            throw new RuntimeException("非系统根结点的产品集成信息不能更新其归属产品！");
        }
        // 更新的产品集成信息的产品UUID
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        values.put("appProductUuid", appProductUuid);
        this.dao.updateByNamedSQL("updateSelfWithChildrenAppProductUuid", values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#updateParentUuid(java.lang.String, java.lang.String)
     */
    @Override
    public void updateParentUuid(String uuid, String parentUuid) {
        // 更新归属产品、系统及数据路径
        List<AppProductIntegration> appProductIntegrations = this.getSelfWithChildrenByUuid(uuid);
        List<AppProductIntegration> children = getToUpdateChildren(uuid, appProductIntegrations, parentUuid);
        this.saveAll(children);
    }

    /**
     * @param uuid
     * @param appProductIntegrations
     * @param parentUuid
     * @return
     */
    private List<AppProductIntegration> getToUpdateChildren(String uuid,
                                                            List<AppProductIntegration> appProductIntegrations, String parentUuid) {
        // 转换为产品集成树
        final TreeNode treeNode = TreeUtils.buildTree(uuid, appProductIntegrations, "uuid", "parentUuid",
                new Function<AppProductIntegration, TreeNode>() {

                    @Override
                    public TreeNode apply(AppProductIntegration input) {
                        TreeNode treeNode = new TreeNode();
                        treeNode.setId(input.getUuid());
                        treeNode.setName(input.getDataName());
                        treeNode.setData(input);
                        return treeNode;
                    }

                });
        final Map<String, AppProductIntegration> integrationMap = ConvertUtils.convertElementToMap(
                appProductIntegrations, "uuid");
        // 更新根结点的信息
        AppProductIntegration parentAppProductIntegration = this.getOne(parentUuid);
        AppProductIntegration rootIntegration = integrationMap.get(uuid);
        rootIntegration.setDataPath(parentAppProductIntegration.getDataPath() + Separator.SLASH.getValue()
                + rootIntegration.getDataId());
        rootIntegration.setAppSystemUuid(parentAppProductIntegration.getAppSystemUuid());
        rootIntegration.setParentUuid(parentUuid);
        // 遍历树，更新子结点的信息
        TreeUtils.traverseTree(treeNode, new Function<TreeNode, Void>() {

            @Override
            public Void apply(TreeNode input) {
                // 忽略根结点
                if (treeNode.equals(input)) {
                    return null;
                }
                AppProductIntegration childIntegration = (AppProductIntegration) input.getData();
                AppProductIntegration parentIntegration = integrationMap.get(childIntegration.getParentUuid());
                childIntegration.setDataPath(parentIntegration.getDataPath() + Separator.SLASH.getValue()
                        + childIntegration.getDataId());
                childIntegration.setAppSystemUuid(parentIntegration.getAppSystemUuid());
                return null;
            }

        });
        return appProductIntegrations;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#resetAppPageDefinition(java.lang.String)
     */
    @Override
    public void resetAppPageDefinition(String uuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        this.dao.updateByHQL(RESET_APP_PAGE_DEFINITION, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationService#getAppSystemUuidByUuid(java.lang.String)
     */
    @Override
    public String getAppSystemUuidByUuid(String uuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", uuid);
        return this.dao.getCharSequenceByHQL(GET_APP_SYSTEM_UUID_BY_UUID, values);
    }

}
