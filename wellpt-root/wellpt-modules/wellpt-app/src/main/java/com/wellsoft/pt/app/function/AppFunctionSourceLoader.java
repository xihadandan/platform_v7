/*
 * @(#)2016年8月2日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

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
public interface AppFunctionSourceLoader {

    /**
     * 加载的功能类型
     *
     * @return
     */
    String getAppFunctionType();

    /**
     * 根据UUID返回功能数据集合
     *
     * @param uuid
     * @return
     */
    List<AppFunctionSource> getAppFunctionSourceByUuid(String uuid);

    /**
     * 根据UUID返回功能数据集合
     *
     * @param id
     * @return
     */
    List<AppFunctionSource> getAppFunctionSourceById(String id);

    /**
     * 返回某种功能类型的功能数据集合
     *
     * @return
     */
    List<AppFunctionSource> getAppFunctionSources();

    /**
     * 将数据项转化为功能
     *
     * @param entity
     * @return
     */
    <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item);

    /**
     * 获取被权限保护的功能对象标识
     *
     * @param appFunctionSource
     * @return
     */
    Collection<Object> getObjectIdentities(AppFunctionSource appFunctionSource);

}
