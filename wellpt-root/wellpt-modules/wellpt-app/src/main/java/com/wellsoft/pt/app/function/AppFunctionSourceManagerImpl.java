/*
 * @(#)2016年8月2日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.service.AppFunctionService;
import com.wellsoft.pt.app.service.AppProductIntegrationContextService;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * 2016年8月2日.1	zhulh		2016年8月2日		Create
 * </pre>
 * @date 2016年8月2日
 */
@Component
public class AppFunctionSourceManagerImpl implements AppFunctionSourceManager {

    @Autowired
    private List<AppFunctionSourceLoader> appFunctionSourceLoaders;

    private Map<String, AppFunctionSourceLoader> appFunctionSourceLoaderMap = new HashMap<String, AppFunctionSourceLoader>();

    @Autowired
    private AppFunctionService appFunctionService;

    @Autowired
    private AppProductIntegrationContextService appProductIntegrationContextService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceManager#appFunctionSource2Entity()
     */
    @Override
    public List<AppFunction> appFunctionSource2Entity(String functionType) {
        // 初始化功能数据加载器Map
        initAppFunctionSourceLoaderMap();
        Set<AppFunction> appFunctions = new HashSet<AppFunction>();

        List<String> functionIds = new ArrayList<String>();
        Map<String, String> functionIdMap = new HashMap<String, String>();
        List<AppFunctionSourceLoader> loaders = getAppFunctionSourceLoaders(functionType);
        for (AppFunctionSourceLoader appFunctionSourceLoader : loaders) {
            Set<AppFunctionSource> sources = new HashSet<AppFunctionSource>();
            sources.addAll(appFunctionSourceLoader.getAppFunctionSources());
            if (CollectionUtils.isEmpty(sources)) {
                continue;
            }
            for (AppFunctionSource appFunctionSource : sources) {
                // 功能重复判断
                String functionId = appFunctionSource.getCategory() + ": " + appFunctionSource.getId();
                if (functionIdMap.containsKey(functionId) && Boolean.FALSE.equals(appFunctionSource.repeatable())) {
                    if (functionIdMap.containsKey(functionId)) {
                        functionIds.add(functionId);
                    }
                }
                functionIdMap.put(functionId, functionId);
                appFunctions.add(convert2AppFunction(appFunctionSource));
            }
        }

        if (!functionIds.isEmpty()) {
            throw new RuntimeException("重复ID的功能: " + functionIds);
        }

        return Arrays.asList(appFunctions.toArray(new AppFunction[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceManager#appFunctionSource2Entity(java.lang.String, java.lang.String)
     */
    @Override
    public List<AppFunction> appFunctionSource2Entity(String entityUuid, String functionType) {
        // 初始化功能数据加载器Map
        initAppFunctionSourceLoaderMap();
        Set<AppFunction> appFunctions = new LinkedHashSet<AppFunction>();
        List<AppFunctionSourceLoader> loaders = getAppFunctionSourceLoaders(functionType);
        for (AppFunctionSourceLoader appFunctionSourceLoader : loaders) {
            Set<AppFunctionSource> sources = new HashSet<AppFunctionSource>();
            sources.addAll(appFunctionSourceLoader.getAppFunctionSourceByUuid(entityUuid));
            for (AppFunctionSource appFunctionSource : sources) {
                appFunctions.add(convert2AppFunction(appFunctionSource));
            }
        }
        return Arrays.asList(appFunctions.toArray(new AppFunction[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceManager#getAppFunctionSources(java.lang.String, java.lang.String)
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources(String entityUuid, String functionType) {
        // 初始化功能数据加载器Map
        initAppFunctionSourceLoaderMap();
        List<AppFunctionSourceLoader> loaders = getAppFunctionSourceLoaders(functionType);
        List<AppFunctionSource> functionSources = Lists.newArrayList();
        for (AppFunctionSourceLoader appFunctionSourceLoader : loaders) {
            functionSources.addAll(appFunctionSourceLoader.getAppFunctionSourceByUuid(entityUuid));
        }
        return functionSources;
    }

    /**
     * 指定功能数据源转成功能实体
     *
     * @param entityId
     * @param functionType
     * @return
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSourcesById(String entityId, String functionType) {
        // 初始化功能数据加载器Map
        initAppFunctionSourceLoaderMap();
        List<AppFunctionSourceLoader> loaders = getAppFunctionSourceLoaders(functionType);
        List<AppFunctionSource> functionSources = Lists.newArrayList();
        for (AppFunctionSourceLoader appFunctionSourceLoader : loaders) {
            List list = appFunctionSourceLoader.getAppFunctionSourceById(entityId);
            if (list != null) {
                functionSources.addAll(list);
            }
        }
        return functionSources;
    }

    /**
     * 如何描述该方法
     *
     * @param functionType
     * @return
     */
    private List<AppFunctionSourceLoader> getAppFunctionSourceLoaders(String functionType) {
        if (StringUtils.isBlank(functionType)) {
            return appFunctionSourceLoaders;
        }

        String iexportType = functionType;
        if (IexportType.hasDependencyParentType(iexportType)) {
            iexportType = IexportType.getDependencyParentType(iexportType);
        }

        Set<AppFunctionSourceLoader> loaders = new HashSet<AppFunctionSourceLoader>();
        if (appFunctionSourceLoaderMap.containsKey(functionType)) {
            loaders.add(appFunctionSourceLoaderMap.get(functionType));
        }
        if (appFunctionSourceLoaderMap.containsKey(iexportType)) {
            loaders.add(appFunctionSourceLoaderMap.get(iexportType));
        }
        return Arrays.asList(loaders.toArray(new AppFunctionSourceLoader[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceManager#convert2AppFunctionSource(java.io.Serializable, java.lang.String)
     */
    @Override
    public <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item, String functionType) {
        // 初始化功能数据加载器Map
        initAppFunctionSourceLoaderMap();
        return appFunctionSourceLoaderMap.get(functionType).convert2AppFunctionSource(item);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceManager#getObjectIdentities(java.lang.String)
     */
    @Override
    public Map<String, Collection<Object>> getObjectIdentities(String objectIdIdentity) {
        AppFunction appFunction = AppCacheUtils.getAppFunction(objectIdIdentity);
        if (appFunction == null) {
            return Collections.emptyMap();
        }

        AppFunctionSourceLoader appFunctionSourceLoader = getAppFunctionSourceLoader(appFunction);
        if (appFunctionSourceLoader == null) {
            return Collections.emptyMap();
        }

        Collection<Object> objects = appFunctionSourceLoader.getObjectIdentities(entity2AppFunctionSource(appFunction));
        Map<String, Collection<Object>> map = new HashMap<String, Collection<Object>>();
        map.put(appFunction.getType(), objects);
        return map;
    }

    /**
     * @param appFunction
     * @return
     */
    private AppFunctionSourceLoader getAppFunctionSourceLoader(AppFunction appFunction) {
        // 初始化功能数据加载器Map
        initAppFunctionSourceLoaderMap();
        return appFunctionSourceLoaderMap.get(appFunction.getType());
    }

    /**
     * 初始化功能数据加载器Map
     */
    private void initAppFunctionSourceLoaderMap() {
        if (appFunctionSourceLoaderMap.isEmpty()) {
            for (AppFunctionSourceLoader appFunctionSourceLoader : appFunctionSourceLoaders) {
                String functionType = appFunctionSourceLoader.getAppFunctionType();
                if (appFunctionSourceLoaderMap.containsKey(functionType)
                        && !appFunctionSourceLoaderMap.get(functionType).equals(appFunctionSourceLoader)) {
                    String clsName1 = appFunctionSourceLoaderMap.get(functionType).getClass().getCanonicalName();
                    String clsName2 = appFunctionSourceLoader.getClass().getCanonicalName();
                    throw new RuntimeException("存在多个类型为[" + functionType + "]的功能加载器[" + clsName1 + ", " + clsName2
                            + "]!");
                }
                appFunctionSourceLoaderMap.put(appFunctionSourceLoader.getAppFunctionType(), appFunctionSourceLoader);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceManager#convert2AppFunction(com.wellsoft.pt.app.function.AppFunctionSource)
     */
    @Override
    public AppFunction convert2AppFunction(AppFunctionSource appFunctionSource) {
        AppFunction appFunction = new AppFunction();
        appFunction.setUuid(appFunctionSource.getUuid());
        appFunction.setName(appFunctionSource.getName());
        appFunction.setId(appFunctionSource.getId());
        appFunction.setCode(appFunctionSource.getCode());
        appFunction.setType(appFunctionSource.getCategory());
        appFunction.setExportable(appFunctionSource.exportable());
        appFunction.setExportType(appFunctionSource.getExportType());
        appFunction.setRemark(appFunctionSource.getRemark());
        Map<String, Object> details = new HashMap<String, Object>();
        if (appFunctionSource.getExtras() != null) {
            details.putAll(appFunctionSource.getExtras());
        }
        details.put("name", appFunctionSource.getName());
        details.put("id", appFunctionSource.getId());
        details.put("action", appFunctionSource.getAction());
        details.put("data", appFunctionSource.getData());
        details.put("category", appFunctionSource.getCategory());
        details.put("repeatable", appFunctionSource.repeatable());
        appFunction.setDefinitionJson(JsonUtils.object2Json(details));
        return appFunction;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceManager#entity2AppFunctionSource(com.wellsoft.pt.app.entity.AppFunction)
     */
    @SuppressWarnings("unchecked")
    private AppFunctionSource entity2AppFunctionSource(AppFunction appFunction) {
        Map<String, Object> extras = JsonUtils.json2Object(appFunction.getDefinitionJson(), Map.class);
        String uuid = appFunction.getUuid();
        String name = appFunction.getName();
        String fullName = name;
        String id = appFunction.getId();
        String code = appFunction.getCode();
        String action = ObjectUtils.toString(extras.get("action"));
        String data = ObjectUtils.toString(extras.get("data"));
        String category = appFunction.getType();
        boolean exportable = appFunction.getExportable();
        String exportType = appFunction.getExportType();
        boolean repeatable = Boolean.valueOf(ObjectUtils.toString(extras.get("repeatable"), "true"));
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, action, data, category, exportable,
                exportType, repeatable, extras);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceManager#getAnonymousResources()
     */
    @Override
    public List<String> getAnonymousResources() {
        return appProductIntegrationContextService.getAllAnonymousFunctionUuids();
    }

    @Override
    public void saveConvert2AppFunction(AppFunctionSource appFunctionSource) {
        AppFunction appFunction = this.convert2AppFunction(appFunctionSource);
        AppFunction exists = appFunctionService.getOne(appFunction.getUuid());
        if (exists != null) {
            BeanUtils.copyProperties(appFunction, exists, IdEntity.BASE_FIELDS);
            appFunction = exists;
        }
        appFunctionService.save(appFunction);
    }


}
