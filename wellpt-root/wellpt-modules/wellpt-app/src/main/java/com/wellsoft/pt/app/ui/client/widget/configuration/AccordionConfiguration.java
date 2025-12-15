/*
 * @(#)2020年12月15日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.support.AppWidgetElementType;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Description: 手风琴组件配置类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月15日.1	zhulh		2020年12月15日		Create
 * </pre>
 * @date 2020年12月15日
 */
public class AccordionConfiguration extends AppWidgetDefinitionConfiguration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3023499686971817436L;

    private List<Tab> tabs = Lists.newArrayList();

    /**
     * @return the tabs
     */
    public List<Tab> getTabs() {
        return tabs;
    }

    /**
     * @param tabs 要设置的tabs
     */
    public void setTabs(List<Tab> tabs) {
        this.tabs = tabs;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidgetConfiguration#getFunctionElements()
     */
    @Override
    public List<FunctionElement> getFunctionElements() {
        List<FunctionElement> functionElements = Lists.newArrayList();
        List<Tab> tabs = getTabs();
        if (CollectionUtils.isNotEmpty(tabs)) {
            for (int index = 0; index < tabs.size(); index++) {
                appendTabFunctionElement(index, tabs.get(index), functionElements);
            }
        }
        return functionElements;
    }

    /**
     * @param index
     * @param tab
     * @param functionElements
     */
    private void appendTabFunctionElement(int index, Tab tab, List<FunctionElement> functionElements) {
        FunctionElement functionElement = new FunctionElement();
        functionElement.setUuid(tab.getUuid());
        functionElement.setId(tab.getUuid());
        functionElement.setName("手风琴子项_" + tab.getName());
        functionElement.setCode("accordion_item_" + (index + 1));
        functionElement.setType(AppWidgetElementType.TAB);
        functionElements.add(functionElement);
    }

    private static final class Tab extends ConfigurationBaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 7266426513800128614L;

        private String uuid;

        private String name;

        /**
         * @return the uuid
         */
        public String getUuid() {
            return uuid;
        }

        /**
         * @param uuid 要设置的uuid
         */
        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name 要设置的name
         */
        public void setName(String name) {
            this.name = name;
        }

    }

}
