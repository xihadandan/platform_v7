/*
 * @(#)2019年7月5日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.portal.facade.service;

import com.wellsoft.context.annotation.Description;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;

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
 * 2019年7月5日.1	zhulh		2019年7月5日		Create
 * </pre>
 * @date 2019年7月5日
 */
public interface AppPortalFacadeService extends BaseService {

    /**
     * 通过产品集成UUID获取组件功能定义
     *
     * @param piUuid
     * @return
     */
    @Description("通过产品集成UUID复制组件功能定义")
    AppWidgetDefinition copyAppWidgetDefinitionByPiUud(String piUuid);

    /**
     * 通过页面UUID获取页面定义，权限过滤页面定义的组件功能元素
     *
     * @param pageUuid
     * @return
     */
    @Description("通过页面UUID获取页面定义，权限过滤页面定义的组件功能元素")
    AppPageDefinition getAppPageDefinitionByPageUuid(String pageUuid);

    /**
     * 根据组件定义ID判断组件是否被引用
     *
     * @param widgtId
     * @return
     */
    @Description("根据组件定义ID判断组件是否被引用")
    boolean isWidgetReferencedById(String widgtId);

    /**
     * 根据页面UUID获取页面定义的门户基础数据
     *
     * @param pageUuid
     * @return
     */
    @Description("根据页面UUID获取页面定义的门户基础数据")
    String getPagePortalInfoByPageUuid(String pageUuid);

    /**
     * 保存用户页面定义
     *
     * @param sourcePageUuid
     * @param name
     * @param theme
     * @param definitionJson
     * @return
     */
    @Description("保存用户门户页面")
    String saveUserPageDefinitionJson(String sourcePageUuid, String name, String theme, String definitionJson);

    /**
     * 根据产品集成UUID，获取用户可访问的页面定义
     *
     * @param piUuid
     * @return
     */
    @Description("获取用户可访问的页面定义信息")
    List<AppPageDefinition> getUserAppPageDefinitionByPiUuid(String piUuid);

    /**
     * 根据页面UUID获取其关联的页面UUID
     *
     * @param pageUuid
     * @return
     */
    @Description("根据页面UUID获取其关联的页面UUID")
    String getPortalCorrelativePageUuidByPageUuid(String pageUuid);

    /**
     * @param pageUuid
     */
    @Description("根据页面UUID设置为默认门户")
    void updateUserDefaultPortalByPageUuid(String pageUuid);

    /**
     * 根据页面UUID删除用户门户
     *
     * @param pageUuid
     */
    @Description("根据页面UUID删除用户门户")
    void deleteUserPortalByPageUuid(String pageUuid);

}
