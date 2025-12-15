/*
 * @(#)2016-09-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.bean.AppWidgetDefinitionBean;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.container.Container;
import com.wellsoft.pt.app.entity.AppProductIntegration;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.facade.service.AppWidgetDefinitionMgr;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import com.wellsoft.pt.app.ui.Component;
import com.wellsoft.pt.app.ui.Widget;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

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
@Service
public class AppWidgetDefinitionMgrImpl implements AppWidgetDefinitionMgr {

    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Override
    public AppWidgetDefinitionBean getBean(String uuid) {

        AppWidgetDefinition entity = appWidgetDefinitionService.get(uuid);
        if (entity != null) {
            AppWidgetDefinitionBean bean = new AppWidgetDefinitionBean();
            BeanUtils.copyProperties(entity, bean);
            return bean;
        }
        return null;

    }

    @Override
    public AppWidgetDefinitionBean getByPiUuid(String productIntegrationUuid) {
        AppProductIntegration appProductIntegration = appProductIntegrationService.get(
                productIntegrationUuid);
        if (appProductIntegration == null) {
            return null;
        }
        String uuid = appProductIntegration.getDataUuid();
        if (StringUtils.isNotBlank(uuid) && appProductIntegration.getDataType().equals(
                AppType.FUNCTION.toString())) {
            return this.getBean(uuid);
        }
        return null;
    }


    @Override
    public void saveBean(AppWidgetDefinitionBean bean) {
        String uuid = bean.getUuid();
        AppWidgetDefinition entity = new AppWidgetDefinition();
        if (StringUtils.isNotBlank(uuid)) {
            entity = appWidgetDefinitionService.get(uuid);
        }
        BeanUtils.copyProperties(bean, entity);
        appWidgetDefinitionService.save(entity);
    }

    @Override
    public void remove(String uuid) {
        appWidgetDefinitionService.remove(uuid);
    }

    @Override
    public void removeAll(Collection<String> uuids) {
        appWidgetDefinitionService.removeAllByPk(uuids);
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo) {
        return loadWidgetDefinitionSelectData(select2QueryInfo, false);
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        return loadWidgetDefinitionSelectDataByIds(select2QueryInfo, false);
    }

    @Override
    public List<AppWidgetDefinition> getAllByAppPageUuid(String appPageUuid) {
        return appWidgetDefinitionService.listByAppPageUuid(appPageUuid);
    }

    @Override
    public boolean isWidgetReferencedById(String appWidgetId) {
        return appWidgetDefinitionService.isWidgetReferencedById(appWidgetId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppWidgetDefinitionMgr#loadLayoutSelectData(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadLayoutSelectData(Select2QueryInfo select2QueryInfo) {
        return loadWidgetDefinitionSelectData(select2QueryInfo, true);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppWidgetDefinitionMgr#loadLayoutSelectDataByIds(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadLayoutSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        return loadWidgetDefinitionSelectDataByIds(select2QueryInfo, true);
    }

    /**
     * @param select2QueryInfo
     * @param onlyLayout
     * @return
     */
    private Select2QueryData loadWidgetDefinitionSelectData(Select2QueryInfo select2QueryInfo, boolean onlyLayout) {
        String queryValue = select2QueryInfo.getSearchValue();
        String uniqueKey = select2QueryInfo.getOtherParams("uniqueKey", "uuid");
        String wtype = select2QueryInfo.getOtherParams("wtype", StringUtils.EMPTY);
        String appPageUuid = select2QueryInfo.getOtherParams("appPageUuid", StringUtils.EMPTY);
        String excludeWidgetIds = select2QueryInfo.getOtherParams("excludeWidgetIds", "-1");
        String includeWidgetRef = select2QueryInfo.getOtherParams("includeWidgetRef");
        List<String> excludeIds = Arrays.asList(
                StringUtils.split(excludeWidgetIds, Separator.SEMICOLON.getValue()));
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("title", queryValue);
        values.put("id", queryValue);
        values.put("wtype", wtype);
        values.put("appPageUuid", appPageUuid);
        values.put("excludeIds", excludeIds);
        List<String> systemUnitIds = new ArrayList<String>();
        systemUnitIds.add(MultiOrgSystemUnit.PT_ID);
        systemUnitIds.add(SpringSecurityUtils.getCurrentUserUnitId());
        values.put("systemUnitIds", systemUnitIds);
        if (Config.TRUE.equalsIgnoreCase(includeWidgetRef)) {
            values.put("includeWidgetRef", includeWidgetRef);
        }
        List<AppWidgetDefinition> list = appWidgetDefinitionService.listByNameHQLQueryAndPage(
                "appWidgetDefinitionSelect2Query", values, select2QueryInfo.getPagingInfo());
        // 过滤布局窗口组件
        if (onlyLayout) {
            list = filterLayoutSelectData(list);
        }
        return new Select2QueryData(list, uniqueKey, "title", select2QueryInfo.getPagingInfo());
    }

    /**
     * @param select2QueryInfo
     * @param onlyLayout
     * @return
     */
    private Select2QueryData loadWidgetDefinitionSelectDataByIds(Select2QueryInfo select2QueryInfo, boolean onlyLayout) {
        String wtype = select2QueryInfo.getOtherParams("wtype", StringUtils.EMPTY);
        String[] appWidgetDefUuids = select2QueryInfo.getIds();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("wtype", wtype);
        values.put("appWidgetDefUuids", appWidgetDefUuids);
        List<AppWidgetDefinition> list = appWidgetDefinitionService.listByNameHQLQueryAndPage(
                "appWidgetDefinitionSelect2UuidsQuery", values, select2QueryInfo.getPagingInfo());
        // 过滤布局窗口组件
        if (onlyLayout) {
            list = filterLayoutSelectData(list);
        }
        return new Select2QueryData(list, "uuid", "title", select2QueryInfo.getPagingInfo());
    }

    /**
     * @param list
     */
    private List<AppWidgetDefinition> filterLayoutSelectData(List<AppWidgetDefinition> list) {
        Set<AppWidgetDefinition> layouts = Sets.newLinkedHashSet();
        for (AppWidgetDefinition definition : list) {
            AppWidgetDefinition appWidgetDefinition = appWidgetDefinitionService.get(definition.getUuid());
            Component component = AppContextHolder.getContext().getComponent(appWidgetDefinition.getWtype());
            // 容器组件或布局分类
            if (component instanceof Container || SimpleComponentCategory.LAYOUT.equals(component.getCategory())) {
                layouts.add(appWidgetDefinition);
            }
            Widget widget = WidgetDefinitionUtils.parseWidget(appWidgetDefinition.getDefinitionJson());
            // 布局组件
            if (CollectionUtils.isNotEmpty(widget.getItems())) {
                layouts.add(appWidgetDefinition);
                // 提取子组件归属同一组别的布局信息作为组件定义返回
                layouts.addAll(extractWidgetBelongGroupAsAppWidgetDefinition(widget.getItems()));
            }
        }
        return Lists.newArrayList(layouts);
    }

    /**
     * @param items
     */
    private List<AppWidgetDefinition> extractWidgetBelongGroupAsAppWidgetDefinition(List<Widget> items) {
        List<AppWidgetDefinition> groupLayouts = Lists.newArrayListWithExpectedSize(0);
        Map<String, String> groupMap = Maps.newLinkedHashMap();
        for (Widget widget : items) {
            String groupId = widget.getAttribute("groupId");
            String groupName = widget.getAttribute("groupName");
            if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(groupName)) {
                groupMap.put(groupId, groupName);
            }
        }
        for (Entry<String, String> entry : groupMap.entrySet()) {
            AppWidgetDefinition appWidgetDefinition = new AppWidgetDefinition();
            appWidgetDefinition.setUuid(entry.getKey());
            appWidgetDefinition.setId(entry.getKey());
            appWidgetDefinition.setName(entry.getValue());
            appWidgetDefinition.setTitle(entry.getValue());
            groupLayouts.add(appWidgetDefinition);
        }
        return groupLayouts;
    }

}
