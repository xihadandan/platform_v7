/*
 * @(#)2016-09-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.bean.AppPageDefinitionParamDto;
import com.wellsoft.pt.app.dao.AppWidgetDefinitionDao;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 组件定义的服务接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-18.1	zhulh		2016-09-18		Create
 * </pre>
 * @date 2016-09-18
 */
public interface AppWidgetDefinitionService extends JpaService<AppWidgetDefinition, AppWidgetDefinitionDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    AppWidgetDefinition get(String uuid);

    /**
     * 根据组件定义ID获取组件定义
     *
     * @param id
     * @return
     */
    AppWidgetDefinition getById(String id);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<AppWidgetDefinition> getAll();

    /**
     * 根据页面定义UUID获取所有组件
     *
     * @param appPageUuid
     * @return
     */
    List<AppWidgetDefinition> getAllByAppPageUuid(String appPageUuid);

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<AppWidgetDefinition> findByExample(AppWidgetDefinition example);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(AppWidgetDefinition entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<AppWidgetDefinition> entities);

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
     * 根据页面定义UUID删除组件定义
     *
     * @param appPageUuid
     */
    void removeByAppPageUuid(String appPageUuid);

    /**
     * 根据组件定义ID判断组件是否被引用
     *
     * @param appWidgetId
     * @return
     */
    boolean isWidgetReferencedById(String appWidgetId);

    /**
     * 根据引用组件uuid查询组件数据
     *
     * @param refWidgetDefUuid
     * @return
     */
    List<AppWidgetDefinition> listByRefWidgetDefUuid(String refWidgetDefUuid);

    /**
     * 如何描述该方法
     *
     * @param appPageUuid
     * @return
     */
    List<AppWidgetDefinition> listByAppPageUuid(String appPageUuid);

    /**
     * 获取组件使用频率
     *
     * @return
     */
    Map<String, Long> getWidgetUsageFrequency();

    Map<String, Long> getVueWidgetUsageFrequency();

    List<AppWidgetDefinition> getWidgetsByAppId(String appId, String wtype, Boolean main);


    AppWidgetDefinition getWidgetByIdAndAppPage(String id, String appPageId, String appPageUuid);

    void saveWidgetDefinitions(AppPageDefinitionParamDto params);

    void copyWidgetDefinitionAsNew(String fromPageUuid, String toPageId, String toPageUuid, String toAppId, String version);


}
