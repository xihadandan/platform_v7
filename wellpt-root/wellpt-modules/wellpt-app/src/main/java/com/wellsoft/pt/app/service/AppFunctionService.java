/*
 * @(#)2016-07-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppFunctionDao;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import com.wellsoft.pt.jpa.service.JpaService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 应用实体类服务接口
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
public interface AppFunctionService extends JpaService<AppFunction, AppFunctionDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    AppFunction get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<AppFunction> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<AppFunction> findByExample(AppFunction example);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(AppFunction entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<AppFunction> entities);

    /**
     * 根据UUID删除记录
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuids
     */
    void removeAllByPk(Collection<String> uuids);

    /**
     * 同步功能数据源
     */
    void syncAppFunctionSources(String functionType, boolean clear);

    /**
     * 同步功能，并添加到指定的集成信息下
     *
     * @param entityClass
     * @param entityUuid
     * @param functionType
     * @param piUuid
     */
    <ITEM extends Serializable> void synchronize(Class<ITEM> entityClass, String entityUuid,
                                                 String functionType,
                                                 String piUuid);

    /**
     * 如何描述该方法
     *
     * @param ids
     * @return
     */
    List<AppFunction> getByIds(String[] ids);

    /**
     * 如何描述该方法
     *
     * @param ids
     * @return
     */
    List<AppFunction> getAll(String[] ids);

    /**
     * @param entityUuid
     * @param functionType
     */
    List<AppFunction> syncAppFunctionSource(String entityUuid, String functionType);


    <ITEM extends Serializable> void synchronizeFunction2ModuleProductIntegrate(String entityUuid,
                                                                                String functionType,
                                                                                String moduleId,
                                                                                boolean isProtected);

    <ITEM extends Serializable> void synchronizeFunction2ProductIntegrate(String entityUuid,
                                                                          String functionType,
                                                                          String piUuid,
                                                                          boolean isProtected);


    /**
     * @param entityUuids
     * @param functionType
     */
    List<AppFunction> syncAppFunctionSources(List<String> entityUuids, String functionType);

    /**
     * 保存组件功能元素
     *
     * @param functionElementsMap
     * @return
     */
    Map<String, List<AppFunction>> saveFunctionElements(Map<String, List<FunctionElement>> functionElementsMap);
}
