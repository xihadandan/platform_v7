/*
 * @(#)2016-07-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.app.dao.AppFunctionDao;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.entity.AppModule;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.AppFunctionSourceManager;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.service.AppFunctionService;
import com.wellsoft.pt.app.service.AppModuleService;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-07-26.1	zhulh		2016-07-26		Create
 * </pre>
 * @date 2016-07-26
 */
@Service
public class AppFunctionServiceImpl extends
        AbstractJpaServiceImpl<AppFunction, AppFunctionDao, String> implements
        AppFunctionService {

    @Autowired
    private AppFunctionSourceManager appFunctionSourceManager;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Autowired
    private AppModuleService appModuleService;

    @Override
    public AppFunction get(String uuid) {
        return this.dao.getOne(uuid);
    }

    @Override
    public List<AppFunction> getAll() {
        return listAll();
    }

    @Override
    public List<AppFunction> findByExample(AppFunction example) {
        return this.dao.listByEntity(example);
    }

    @Override
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    @Override
    @Transactional
    public void removeAll(Collection<AppFunction> entities) {
        deleteByEntities(entities);
    }

    @Override
    @Transactional
    public void remove(AppFunction entity) {
        this.dao.delete(entity);
    }

    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uuids) {
        deleteByUuids(Lists.newArrayList(uuids));
    }

    @Override
    @Transactional
    public void syncAppFunctionSources(String functionType, boolean clear) {
        if (clear) {
            Map<String, Object> values = new HashMap<String, Object>();
            if (StringUtils.isBlank(functionType)) {
                // 不删除组件功能元素，页面保存、删除时自动生成
                values.put("functionType", AppFunctionType.AppWidgetFunctionElement);
                this.dao.deleteByHQL("delete from AppFunction o where o.type <> :functionType", values);
            } else {
                values.put("functionType", functionType);
                this.dao.deleteByHQL("delete from AppFunction o where o.type = :functionType",
                        values);
            }
            List<AppFunction> appFunctions = appFunctionSourceManager.appFunctionSource2Entity(
                    functionType);
            this.dao.saveAll(appFunctions);
        } else {
            List<AppFunction> appFunctions = appFunctionSourceManager.appFunctionSource2Entity(
                    functionType);
            List<String> deleteUuids = new ArrayList<String>();
            for (AppFunction appFunction : appFunctions) {
                deleteUuids.add(appFunction.getUuid());
                if (deleteUuids.size() > 500) {
                    Map<String, Object> values = new HashMap<String, Object>();
                    values.put("uuids", deleteUuids);
                    this.dao.deleteByHQL("delete from AppFunction t where t.uuid in (:uuids)",
                            values);
                    deleteUuids.clear();
                }
            }
            if (!deleteUuids.isEmpty()) {
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("uuids", deleteUuids);
                this.dao.deleteByHQL("delete from AppFunction t where t.uuid in (:uuids)", values);
            }
            this.dao.saveAll(appFunctions);
        }

        // 写入数据库
        this.dao.getSession().flush();
        this.dao.getSession().clear();

        // 查询名称不一致的数据
        List<AppFunction> appFunctions = this.dao.listByNameSQLQuery("getDiffAppFunctionForPiInfo",
                null);
        for (AppFunction appFunction : appFunctions) {
            appProductIntegrationService.updatPiDataName(appFunction);
        }

        AppCacheUtils.clear();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppFunctionService#synchronize(java.lang.Class, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public <ITEM extends Serializable> void synchronize(Class<ITEM> entityClass, String entityUuid,
                                                        String functionType, String piUuid) {
        // 写入数据库
        this.dao.getSession().flush();
        this.dao.getSession().clear();

        AppFunction item = this.dao.getOne(entityUuid);
        if (item == null) {
            return;
        }
        this.dao.getSession().evict(item);
        AppFunctionSource appFunctionSource = appFunctionSourceManager.convert2AppFunctionSource(
                item, functionType);
        AppFunction appFunction = appFunctionSourceManager.convert2AppFunction(appFunctionSource);

        // 删除存在的功能
        List<String> deleteUuids = new ArrayList<String>();
        deleteUuids.add(appFunction.getUuid());
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuids", deleteUuids);
        this.dao.deleteByHQL("delete from AppFunction t where t.uuid in (:uuids)", values);

        // 写入数据库
        this.dao.getSession().flush();
        this.dao.getSession().clear();

        this.dao.save(appFunction);

        if (StringUtils.isNotBlank(piUuid)) {
            appProductIntegrationService.addAppFunction(piUuid, appFunction);
        }

        AppCacheUtils.clear();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppFunctionService#getByIds(java.lang.String[])
     */
    @Override
    public List<AppFunction> getByIds(String[] ids) {
        return this.dao.listByFieldInValues("id", Arrays.asList(Arrays.asList(ids).toArray()));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppFunctionService#getAll(java.lang.String[])
     */
    @Override
    public List<AppFunction> getAll(String[] ids) {
        return this.dao.listByFieldInValues("uuid", Arrays.asList(Arrays.asList(ids).toArray()));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppFunctionService#syncAppFunctionSource(com.wellsoft.context.jdbc.entity.IdEntity, java.lang.String)
     */
    @Override
    @Transactional
    public List<AppFunction> syncAppFunctionSource(String entityUuid, String functionType) {
        List<String> entityUuids = Lists.newArrayList();
        entityUuids.add(entityUuid);
        return syncAppFunctionSources(entityUuids, functionType);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppFunctionService#syncAppFunctionSources(java.util.List, java.lang.String)
     */
    @Override
    @Transactional
    public List<AppFunction> syncAppFunctionSources(List<String> entityUuids, String functionType) {
        Set<AppFunction> appFunctions = Sets.newLinkedHashSet();
        for (String entityUuid : entityUuids) {
            appFunctions.addAll(
                    appFunctionSourceManager.appFunctionSource2Entity(entityUuid, functionType));
        }

        List<AppFunction> waitSaveList = Lists.newArrayList();
        for (AppFunction function : appFunctions) {
            AppFunction exitsOne = this.getOne(function.getUuid());
            if (exitsOne != null) {
                BeanUtils.copyProperties(function, exitsOne, IdEntity.BASE_FIELDS);//存在则更新
                waitSaveList.add(exitsOne);
            } else {
                waitSaveList.add(function);
            }
        }
        this.dao.saveAll(waitSaveList);
        return waitSaveList;
    }

    /**
     * 保存组件功能元素
     *
     * @param functionElementsMap
     * @return
     */
    @Override
    @Transactional
    public Map<String, List<AppFunction>> saveFunctionElements(Map<String, List<FunctionElement>> functionElementsMap) {
        Map<String, List<AppFunction>> widgetFunctionUuidMap = Maps.newHashMap();
        if (MapUtils.isEmpty(functionElementsMap)) {
            return widgetFunctionUuidMap;
        }

        for (Map.Entry<String, List<FunctionElement>> entry : functionElementsMap.entrySet()) {
            Set<AppFunctionSource> appFunctionSources = Sets.newHashSet();
            String widgetId = entry.getKey();
            List<FunctionElement> functionElements = entry.getValue();
            for (FunctionElement fe : functionElements) {
                if (fe == null) {
                    continue;
                }
                appFunctionSources.addAll(convertFunctionElement2AppFunctionSource(fe, widgetId));
            }
            // 保存或更新功能
            Set<AppFunction> appFunctions = Sets.newHashSet();
            for (AppFunctionSource source : appFunctionSources) {
                appFunctions.add(appFunctionSourceManager.convert2AppFunction(source));
            }
            List<AppFunction> waitSaveList = Lists.newArrayList();
            for (AppFunction function : appFunctions) {
                AppFunction exitsOne = this.getOne(function.getUuid());
                if (exitsOne != null) {
                    org.springframework.beans.BeanUtils.copyProperties(function, exitsOne, IdEntity.BASE_FIELDS);//存在则更新
                    waitSaveList.add(exitsOne);
                } else {
                    waitSaveList.add(function);
                }
            }
            this.saveAll(waitSaveList);

            widgetFunctionUuidMap.put(widgetId, waitSaveList);
        }
        return widgetFunctionUuidMap;
    }

    private Collection<AppFunctionSource> convertFunctionElement2AppFunctionSource(FunctionElement functionElement,
                                                                                   String widgetId) {
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
        } else if (isRef && org.apache.commons.lang.StringUtils.isNotBlank(entityId) && org.apache.commons.lang.StringUtils.isNotBlank(appFunctionType)) {
            List list = appFunctionSourceManager.getAppFunctionSourcesById(entityId, appFunctionType);
            if (CollectionUtils.isNotEmpty(list)) {
                appFunctionSources.addAll(list);
            } else {
                AppFunctionSource source = new SimpleAppFunctionSource(StringUtils.isBlank(functionElement.getUuid()) ? SnowFlake.getId() + "" : functionElement.getUuid(), functionElement.getName(), functionElement.getName(), functionElement.getId(),
                        functionElement.getCode(), null, null,
                        functionElement.getFunctionType(), StringUtils.isNotBlank(functionElement.getExportType()), functionElement.getFunctionType()
                        , true, null);
                appFunctionSources.add(source);
            }
        } else {
            // 2、组件元素功能
            String uuid = DigestUtils.md5Hex(functionElement.getUuid() + widgetId);
            String tile = widgetId;
            String fullName = tile + "_" + functionElement.getName();
            String name = functionElement.getName();
            String id = functionElement.getId();
            String code = functionElement.getCode();
            String category = AppFunctionType.AppWidgetFunctionElement;
            Map<String, Object> extras = new HashMap<String, Object>();
            extras.put("widgetId", widgetId);
            extras.put("title", functionElement.getName());
            extras.put(IdEntity.UUID, uuid);
            AppFunctionSource source = new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null,
                    category, false, category, true, extras);
            appFunctionSources.add(source);
        }
        return appFunctionSources;
    }

    @Override
    @Transactional
    public <ITEM extends Serializable> void synchronizeFunction2ModuleProductIntegrate(
            String entityUuid, String functionType, String moduleId, boolean isProtected) {
        List<AppFunction> appFunctions = this.syncAppFunctionSource(entityUuid, functionType);
        if (StringUtils.isNotBlank(moduleId) && CollectionUtils.isNotEmpty(appFunctions)) {
            AppModule appModule = appModuleService.getById(moduleId);
            if (appModule != null) {
                //添加到模块归属的产品集成树下
                List<AppProductIntegration> appProductIntegrationList = appProductIntegrationService.getByDataUuidAndType(
                        appModule.getUuid(), AppType.MODULE.toString());
                for (AppProductIntegration appProductIntegration : appProductIntegrationList) {
                    if (appProductIntegration != null) {
                        addFunction2ProductIntegateTreeIfNoAdded(appFunctions.get(0),
                                appProductIntegration.getUuid(), isProtected);
                    }
                }
            }
        }
    }


    private void addFunction2ProductIntegateTreeIfNoAdded(AppFunction appFunction, String piUuid,
                                                          boolean isProtected) {
        AppProductIntegration example = new AppProductIntegration();
        example.setDataType(AppType.FUNCTION.toString());
        example.setParentUuid(piUuid);
        example.setDataUuid(appFunction.getUuid());
        List<AppProductIntegration> exists = this.appProductIntegrationService.findByExample(
                example);
        if (CollectionUtils.isEmpty(exists)) {//未添加到产品集成树下，才添加
            appProductIntegrationService.addAppFunction(piUuid,
                    appFunction, isProtected);
        } else {
            //更新
            exists.get(0).setDataName(appFunction.getName());
            appProductIntegrationService.save(exists.get(0));

        }
    }

    @Override
    public <ITEM extends Serializable> void synchronizeFunction2ProductIntegrate(String entityUuid,
                                                                                 String functionType,
                                                                                 String piUuid,
                                                                                 boolean isProtected) {
        List<AppFunction> appFunctions = this.syncAppFunctionSource(entityUuid, functionType);
        if (StringUtils.isNotBlank(piUuid) && CollectionUtils.isNotEmpty(appFunctions)) {
            AppProductIntegration appProductIntegration = appProductIntegrationService.getOne(
                    piUuid);
            if (appProductIntegration != null && (AppType.MODULE.toString().equals(
                    appProductIntegration.getDataType())) || AppType.APPLICATION.toString().equals(
                    appProductIntegration.getDataType())) {
                addFunction2ProductIntegateTreeIfNoAdded(appFunctions.get(0), piUuid, isProtected);
            }
        }
    }

}
