/*
 * @(#)2019年6月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.support.AppWidgetElementType;
import org.apache.commons.collections.CollectionUtils;

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
 * 2019年6月19日.1	zhulh		2019年6月19日		Create
 * </pre>
 * @date 2019年6月19日
 */
public class BootstrapTabsConfiguration extends AppWidgetDefinitionConfiguration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6561553525196056646L;

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
        functionElement.setName("页签_" + tab.getName());
        functionElement.setCode("tab_" + (index + 1));
        functionElement.setType(AppWidgetElementType.TAB);
        functionElements.add(functionElement);
    }

    private static final class Tab extends ConfigurationBaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 6210540179313143262L;

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
