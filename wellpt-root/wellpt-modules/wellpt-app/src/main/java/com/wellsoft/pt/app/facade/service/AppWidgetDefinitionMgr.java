/*
 * @(#)2016-09-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.bean.AppWidgetDefinitionBean;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;

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
 * 2016-09-18.1	zhulh		2016-09-18		Create
 * </pre>
 * @date 2016-09-18
 */
public interface AppWidgetDefinitionMgr extends BaseService, Select2QueryApi {

    /**
     * 获取
     *
     * @param uuid
     * @return
     */
    AppWidgetDefinitionBean getBean(String uuid);

    /**
     * 根据产品集成UUID，获取组件定义
     *
     * @param productIntegrationUuid
     * @return
     */
    AppWidgetDefinitionBean getByPiUuid(String productIntegrationUuid);

    /**
     * 保存
     *
     * @param bean
     * @return
     */
    void saveBean(AppWidgetDefinitionBean bean);

    /**
     * 删除
     *
     * @param uuid
     * @return
     */
    void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuid
     * @return
     */
    void removeAll(Collection<String> uuids);

    /**
     * 根据页面定义UUID获取所有组件
     *
     * @param appPageUuid
     * @return
     */
    List<AppWidgetDefinition> getAllByAppPageUuid(String appPageUuid);

    /**
     * 根据组件定义ID判断组件是否被引用
     *
     * @param appWidgetId
     * @return
     */
    boolean isWidgetReferencedById(String appWidgetId);

    /**
     * 获取布局窗口组件
     *
     * @param select2QueryInfo
     * @return
     */
    Select2QueryData loadLayoutSelectData(Select2QueryInfo select2QueryInfo);

    /**
     * 根据ID信息，获取布局窗口组件
     *
     * @param select2QueryInfo
     * @return
     */
    Select2QueryData loadLayoutSelectDataByIds(Select2QueryInfo select2QueryInfo);


}
