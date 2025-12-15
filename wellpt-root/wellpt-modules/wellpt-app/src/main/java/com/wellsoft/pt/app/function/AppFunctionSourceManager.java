/*
 * @(#)2016年8月2日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function;

import com.wellsoft.pt.app.entity.AppFunction;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
public interface AppFunctionSourceManager {
    /**
     * 指定功能数据源转成功能实体，functionType为空时取全部
     *
     * @return
     */
    List<AppFunction> appFunctionSource2Entity(String functionType);

    /**
     * 指定功能数据源转成功能实体
     *
     * @param entityUuid
     * @param functionType
     */
    List<AppFunction> appFunctionSource2Entity(String entityUuid, String functionType);

    /**
     * 指定功能数据源转成功能实体
     *
     * @param entityUuid
     * @param functionType
     */
    List<AppFunctionSource> getAppFunctionSources(String entityUuid, String functionType);

    /**
     * 指定功能数据源转成功能实体
     *
     * @param entityId
     * @param functionType
     * @return
     */
    List<AppFunctionSource> getAppFunctionSourcesById(String entityId, String functionType);

    /**
     * 指定的功能数据源转成功能实体
     *
     * @param appFunctionSource
     * @return
     */
    AppFunction convert2AppFunction(AppFunctionSource appFunctionSource);

    /**
     * 指定的数据转成功能数据源
     *
     * @param item
     * @param functionType
     * @return
     */
    <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item, String functionType);

    /**
     * 获取具体功能受保护的对象
     *
     * @param dataUuid
     * @return
     */
    Map<String, Collection<Object>> getObjectIdentities(String objectIdIdentity);

    /**
     * 获取功能不需要受保护的资源
     *
     * @return
     */
    List<String> getAnonymousResources();

    void saveConvert2AppFunction(AppFunctionSource appFunctionSource);

}
