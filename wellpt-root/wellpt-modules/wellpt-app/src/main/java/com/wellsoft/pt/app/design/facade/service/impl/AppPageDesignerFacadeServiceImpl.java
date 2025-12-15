/*
 * @(#)2019年7月5日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.design.component.SimpleComponentCategory;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.design.container.AbstractContainer;
import com.wellsoft.pt.app.design.container.DefaultPageContainer;
import com.wellsoft.pt.app.design.dto.LayoutDto;
import com.wellsoft.pt.app.design.dto.WidgetDto;
import com.wellsoft.pt.app.design.facade.service.AppPageDesignerFacadeService;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
@Service
public class AppPageDesignerFacadeServiceImpl implements AppPageDesignerFacadeService {

    @Autowired
    private List<AbstractContainer> pageContainers;

    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.facade.service.AppPageDesignerFacadeService#getLayouts(java.lang.String)
     */
    @Override
    public List<LayoutDto> getLayouts(String containerWtype) {
        List<UIDesignComponent> layoutComponents = getLayoutComponents(containerWtype);
        return converet2LayoutDtos(layoutComponents);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.design.facade.service.AppPageDesignerFacadeService#getWidgetsByPageUuid(java.lang.String)
     */
    @Override
    public List<WidgetDto> getWidgetsByPageUuid(String pageUuid) {
        if (StringUtils.isBlank(pageUuid)) {
            return Collections.emptyList();
        }
        List<AppWidgetDefinition> appWidgetDefinitions = appWidgetDefinitionService.getAllByAppPageUuid(pageUuid);
        // 按时间升序
        Collections.sort(appWidgetDefinitions, IdEntityComparators.CREATE_TIME_ASC);
        List<WidgetDto> widgetDtos = converet2WidgetDtos(appWidgetDefinitions);
        return widgetDtos;
    }

    /**
     * @param appWidgetDefinitions
     * @return
     */
    private List<WidgetDto> converet2WidgetDtos(List<AppWidgetDefinition> appWidgetDefinitions) {
        List<WidgetDto> widgetDtos = Lists.newArrayList();
        for (AppWidgetDefinition appWidgetDefinition : appWidgetDefinitions) {
            UIDesignComponent component = AppContextHolder.getContext().getComponent(appWidgetDefinition.getWtype());
            if (component == null || component.getCategory() == null
                    || SimpleComponentCategory.LAYOUT.equals(component.getCategory())) {
                continue;
            }
            WidgetDto widgetDto = new WidgetDto();
            widgetDto.setId(appWidgetDefinition.getId());
            try {
                widgetDto.setText(WidgetDefinitionUtils.getTitle(appWidgetDefinition.getDefinitionJson()));
            } catch (Exception e) {
                widgetDto.setText(appWidgetDefinition.getTitle());
            }
            widgetDtos.add(widgetDto);
        }
        return widgetDtos;
    }

    /**
     * @param layoutComponents
     * @return
     */
    private List<LayoutDto> converet2LayoutDtos(List<UIDesignComponent> layoutComponents) {
        List<LayoutDto> layoutDtos = Lists.newArrayList();
        for (UIDesignComponent layoutComponent : layoutComponents) {
            LayoutDto layoutDto = new LayoutDto();
            layoutDto.setText(layoutComponent.getName());
            layoutDto.setId(layoutComponent.getType());
            layoutDtos.add(layoutDto);
        }
        return layoutDtos;
    }

    /**
     * @param containerWtype
     * @return
     */
    private List<UIDesignComponent> getLayoutComponents(String containerWtype) {
        List<UIDesignComponent> layoutComponents = Lists.newArrayList();
        AbstractContainer container = getPageContainer(containerWtype);
        List<UIDesignComponent> components = container.getComponents();
        for (UIDesignComponent uiDesignComponent : components) {
            if (SimpleComponentCategory.LAYOUT.equals(uiDesignComponent.getCategory())) {
                layoutComponents.add(uiDesignComponent);
            }
        }
        OrderComparator.sort(layoutComponents);
        return layoutComponents;
    }

    /**
     * @param wtype
     * @return
     */
    private AbstractContainer getPageContainer(String wtype) {
        if (StringUtils.isBlank(wtype)) {
            return ApplicationContextHolder.getBean(DefaultPageContainer.class);
        }
        for (AbstractContainer abstractContainer : pageContainers) {
            if (StringUtils.equals(abstractContainer.getType(), wtype)) {
                return abstractContainer;
            }
        }
        return null;
    }

}
