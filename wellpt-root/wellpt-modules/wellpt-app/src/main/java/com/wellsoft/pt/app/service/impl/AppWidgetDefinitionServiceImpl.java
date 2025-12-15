/*
 * @(#)2016-09-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.bean.AppPageDefinitionParamDto;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.dao.AppWidgetDefinitionDao;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import com.wellsoft.pt.app.ui.Widget;
import com.wellsoft.pt.app.ui.client.widget.configuration.AppWidgetDefinitionElement;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 组件定义的服务实现类
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
public class AppWidgetDefinitionServiceImpl extends
        AbstractJpaServiceImpl<AppWidgetDefinition, AppWidgetDefinitionDao, String> implements
        AppWidgetDefinitionService {

    // 根据页面定义UUID删除组件定义
    private static final String REMOVE_BY_APP_PAGE_UUID = "delete from AppWidgetDefinition t where t.appPageUuid = :appPageUuid";

    // 判断组件是否被引用
    private static final String IS_WIDGET_REFERENCED_BY_ID = "select count(t1.uuid) from AppWidgetDefinition t1 where t1.id = :appWidgetId and exists(select t2.uuid from AppWidgetDefinition t2 where t1.uuid = t2.refWidgetDefUuid)";

    // 列出组件使用频率
    private static final String LIST_WIDGET_USAGE_FREQUENCY = "select t1.wtype as wtype, count(t1.wtype) as frequency from AppWidgetDefinition t1 where exists(select t2.uuid from AppProductIntegration t2 where t1.appPageUuid = t2.appPageUuid and t2.appPageReference = false) group by t1.wtype";


    @Autowired
    private AppDefElementI18nService appDefElementI18nService;


    @Override
    public AppWidgetDefinition get(String uuid) {
        return getOne(uuid);
    }

    @Override
    public AppWidgetDefinition getById(String id) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("id", id);
        List<AppWidgetDefinition> appWidgetDefinitions = this.dao.listByNameSQLQuery(
                "getLatestVersionWidgetDefinitionByIdQuery", values);
        // List<AppWidgetDefinition> appWidgetDefinitions = this.dao.listByFieldEqValue("id", id);
        return CollectionUtils.isNotEmpty(appWidgetDefinitions) ? appWidgetDefinitions.get(0) : null;
    }

    @Override
    public List<AppWidgetDefinition> getAll() {
        return listAll();
    }

    @Override
    public List<AppWidgetDefinition> getAllByAppPageUuid(String appPageUuid) {
        return this.dao.listByFieldEqValue("appPageUuid", appPageUuid);
    }

    @Override
    public List<AppWidgetDefinition> findByExample(AppWidgetDefinition example) {
        return this.dao.listByEntity(example);
    }

    @Override
    @Transactional
    public void remove(String uuid) {
        delete(uuid);
    }

    @Override
    @Transactional
    public void removeAll(Collection<AppWidgetDefinition> entities) {
        deleteByEntities(entities);
    }

    @Override
    @Transactional
    public void remove(AppWidgetDefinition entity) {
        this.dao.delete(entity);
    }

    @Override
    public void removeAllByPk(Collection<String> uuids) {
        this.dao.deleteByUuids(uuids);
    }

    @Override
    public void removeByAppPageUuid(String appPageUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appPageUuid", appPageUuid);
        this.dao.deleteByHQL(REMOVE_BY_APP_PAGE_UUID, values);
    }

    @Override
    public boolean isWidgetReferencedById(String appWidgetId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("appWidgetId", appWidgetId);
        return this.dao.countByHQL(IS_WIDGET_REFERENCED_BY_ID, values) > 0;
    }

    @Override
    public List<AppWidgetDefinition> listByRefWidgetDefUuid(String refWidgetDefUuid) {
        return this.dao.listByFieldEqValue("refWidgetDefUuid", refWidgetDefUuid);
    }

    @Override
    public List<AppWidgetDefinition> listByAppPageUuid(String appPageUuid) {
        return this.dao.listByFieldEqValue("appPageUuid", appPageUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppWidgetDefinitionService#listWidgetUsageFrequency()
     */
    @Override
    public Map<String, Long> getWidgetUsageFrequency() {
        List<QueryItem> queryItems = this.dao.listQueryItemByHQL(LIST_WIDGET_USAGE_FREQUENCY, null, new PagingInfo(1,
                Integer.MAX_VALUE, false));
        Map<String, Long> widgetUsageFrequency = Maps.newHashMap();
        for (QueryItem queryItem : queryItems) {
            widgetUsageFrequency.put(queryItem.getString("wtype"), queryItem.getLong("frequency"));
        }
        return widgetUsageFrequency;
    }

    @Override
    public Map<String, Long> getVueWidgetUsageFrequency() {
        List<QueryItem> queryItems = this.dao.listQueryItemBySQL("select x.count as frequency,x.wtype as wtype\n" +
                "  from (select count(1) as count, wtype\n" +
                "          from (select wtype\n" +
                "                  from app_widget_definition\n" +
                "                 where app_page_id is not null\n" +
                "                   and wtype like 'Widget%') v\n" +
                "         group by v.wtype) x", null, null);
        Map<String, Long> widgetUsageFrequency = Maps.newHashMap();
        for (QueryItem queryItem : queryItems) {
            widgetUsageFrequency.put(queryItem.getString("wtype"), queryItem.getLong("frequency"));
        }
        return widgetUsageFrequency;
    }

    @Override
    public List<AppWidgetDefinition> getWidgetsByAppId(String appId, String wtype, Boolean main) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("appId", appId);
        StringBuilder sql = new StringBuilder("FROM AppWidgetDefinition a WHERE a.appId=:appId " +
                " and a.version = ( select max(b.version) from AppWidgetDefinition b where b.id = a.id and b.appPageId = a.appPageId ) ");
        if (StringUtils.isNotEmpty(wtype)) {
            param.put("wtype", wtype.split(","));
            sql.append(" and a.wtype in :wtype");
        }
        if (main != null) {
            param.put("main", main);
            sql.append(" and a.main =:main");
        }
        List<AppWidgetDefinition> appWidgetDefinitions = listByHQL(sql.toString(), param);
        if (CollectionUtils.isNotEmpty(appWidgetDefinitions)) {
            for (AppWidgetDefinition definition : appWidgetDefinitions) {
                definition.setI18ns(appDefElementI18nService.getI18ns(definition.getAppPageId(), definition.getId(), definition.getVersion(), IexportType.AppPageDefinition, LocaleContextHolder.getLocale().toString()));
            }
        }

        return appWidgetDefinitions;
    }

    @Override
    @Transactional
    public void saveWidgetDefinitions(AppPageDefinitionParamDto dto) {
        String pageUuid = dto.getUuid();
        String pageId = dto.getId();
        List<AppWidgetDefinitionElement> appWidgetDefinitionElements = dto.getAppWidgetDefinitionElements();
        List<AppWidgetDefinition> oldAppWidgetDefinitionList = this.listByAppPageUuid(pageUuid);
        Map<String, AppWidgetDefinition> oldWidgetMap = oldAppWidgetDefinitionList.stream().collect(
                Collectors.toMap(AppWidgetDefinition::getUuid, widget -> widget));
        // 保存组件定义
        List<Widget> widgets = WidgetDefinitionUtils.parseWidgets(appWidgetDefinitionElements);
        appDefElementI18nService.deleteAllI18n(null, pageId, new BigDecimal("1.0"),
                "vForm".equalsIgnoreCase(dto.getWtype()) ? IexportType.DyFormDefinition :
                        IexportType.AppPageDefinition);
        List<AppWidgetDefinition> widgetEntities = new ArrayList<AppWidgetDefinition>();
        for (Widget widget : widgets) {
            String widgetTitle = widget.getTitle();
            String widgetId = widget.getId();
            String wtype = widget.getWtype();
            if (StringUtils.isBlank(wtype)) {
                continue;
            }
            String uuid = DigestUtils.md5Hex(pageUuid + widgetId);
            AppWidgetDefinition widgetDefinition = oldWidgetMap.get(uuid);
            if (widgetDefinition == null) {
                widgetDefinition = new AppWidgetDefinition();
                widgetDefinition.setUuid(uuid);
            }
            widgetDefinition.setTitle(widgetTitle);
            widgetDefinition.setName(widgetTitle);

            widgetDefinition.setId(widgetId);
            UIDesignComponent designComponent = AppContextHolder.getContext().getComponent(wtype);
            if (designComponent != null) {
                widgetDefinition.setName(designComponent.getName());
            } else {
                widgetDefinition.setName(widgetTitle);
            }
            widgetDefinition.setWtype(wtype);
            widgetDefinition.setAppId(dto.getAppId());
            widgetDefinition.setAppPageId(pageId);
            widgetDefinition.setVersion(dto.getVersion());
            widgetDefinition.setMain(widget.getAttribute("main", Boolean.class));
            try {
                widgetDefinition.setDefinitionJson(widget.getDefinitionJson());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            widgetDefinition.setRefWidgetDefUuid(
                    widget.getAttribute(AppConstants.KEY_REF_WIDGET_DEF_UUID));
            widgetDefinition.setAppPageUuid(pageUuid);
            this.save(widgetDefinition);
            oldWidgetMap.remove(uuid);
            widgetEntities.add(widgetDefinition);
        }
        this.deleteByEntities(oldWidgetMap.values());
        if (CollectionUtils.isNotEmpty(appWidgetDefinitionElements)) {
            List<AppDefElementI18nEntity> i18nEntities = Lists.newArrayList();
            for (AppWidgetDefinitionElement element : appWidgetDefinitionElements) {
                if (CollectionUtils.isNotEmpty(element.getI18ns())) {
                    for (AppDefElementI18nEntity i : element.getI18ns()) {
                        i.setDefId(dto.getId());
                        i.setApplyTo("vForm".equalsIgnoreCase(dto.getWtype()) ? IexportType.DyFormDefinition :
                                IexportType.AppPageDefinition);
                        i.setVersion(dto.getVersion() == null ? new BigDecimal("1.0") : dto.getVersion());
                        i18nEntities.add(i);
                    }
                }
            }
            if (!i18nEntities.isEmpty()) {
                appDefElementI18nService.saveAll(i18nEntities);
            }
        }

    }

    @Override
    @Transactional
    public void copyWidgetDefinitionAsNew(String fromPageUuid, String toPageId, String toPageUuid, String toAppId, String version) {
        List<AppWidgetDefinition> froms = this.listByAppPageUuid(fromPageUuid);
        if (CollectionUtils.isNotEmpty(froms)) {
            List<AppWidgetDefinition> saves = Lists.newArrayListWithCapacity(froms.size());
            for (AppWidgetDefinition d : froms) {
                AppWidgetDefinition n = new AppWidgetDefinition();
                BeanUtils.copyProperties(d, n, n.BASE_FIELDS);
                if (StringUtils.isNotBlank(toAppId)) {
                    n.setAppId(toAppId);
                }
                if (StringUtils.isNotBlank(version)) {
                    n.setVersion(new BigDecimal(version));
                } else {
                    n.setVersion(null);
                }
                n.setAppPageId(toPageId);
                n.setAppPageUuid(toPageUuid);
                saves.add(n);
            }
            this.saveAll(saves);
        }
    }

    @Override
    public AppWidgetDefinition getWidgetByIdAndAppPage(String id, String appPageId, String appPageUuid) {
        Assert.notNull(id, "组件ID不为空");
        AppWidgetDefinition example = new AppWidgetDefinition();
        if (StringUtils.isNotBlank(appPageUuid)) {
            // 按定义 UUID 优先匹配
            example.setAppPageUuid(appPageUuid);
            example.setId(id);
            List<AppWidgetDefinition> list = this.listByEntity(example);
            if (CollectionUtils.isNotEmpty(list)) {
                return list.get(0);
            }
            example.setAppPageUuid(null);
        }

        Map<String, Object> param = Maps.newHashMap();
        param.put("id", id);

        // 按定义 ID 匹配最大版本的定义
        if (StringUtils.isNotBlank(appPageId)) {
            param.put("appPageId", appPageId);
            List<AppWidgetDefinition> list = this.listByHQL("from AppWidgetDefinition a where a.id=:id and a.appPageId=:appPageId " +
                    "and a.version = ( select max(b.version) from AppWidgetDefinition b where b.id = a.id and b.appPageId = a.appPageId )", param);
            if (CollectionUtils.isNotEmpty(list)) {
                return list.get(0);
            }
        }
        List<AppWidgetDefinition> list = this.dao.listByHQL("from AppWidgetDefinition a where a.id=:id and a.version = ( select max(b.version) from AppWidgetDefinition b where b.id = a.id and b.appPageId = a.appPageId )", param);
        AppWidgetDefinition appWidgetDefinition = CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
        if (appWidgetDefinition != null) {
            appWidgetDefinition.setI18ns(appDefElementI18nService.getI18ns(appWidgetDefinition.getAppPageId(), appWidgetDefinition.getId(), appWidgetDefinition.getVersion(), IexportType.AppPageDefinition, LocaleContextHolder.getLocale().toString()));
        }

        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }


}
