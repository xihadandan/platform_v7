/*
 * @(#)2019年6月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.app.support.AppWidgetElementType;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
public class HeaderConfiguration extends AppWidgetDefinitionConfiguration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6561553525196056646L;

    private MainNav mainNav;

    private RightNav rightNav;

    /**
     * @return the mainNav
     */
    public MainNav getMainNav() {
        return mainNav;
    }

    /**
     * @param mainNav 要设置的mainNav
     */
    public void setMainNav(MainNav mainNav) {
        this.mainNav = mainNav;
    }

    /**
     * @return the rightNav
     */
    public RightNav getRightNav() {
        return rightNav;
    }

    /**
     * @param rightNav 要设置的rightNav
     */
    public void setRightNav(RightNav rightNav) {
        this.rightNav = rightNav;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidgetConfiguration#getFunctionElements()
     */
    @Override
    public List<FunctionElement> getFunctionElements() {
        List<FunctionElement> functionElements = Lists.newArrayList();
        // 主导航
        if (getMainNav() != null) {
            List<MenuItem> menuItems = getMainNav().getMenuItems();
            if (CollectionUtils.isNotEmpty(menuItems)) {
                int index = 0;
                for (MenuItem menuItem : menuItems) {
                    appendMainNavFunctionElement(index++, menuItem, functionElements);
                }
            }
        }
        // 右侧导航
        if (getRightNav() != null) {
            List<MenuItem> menuItems = getRightNav().getMenuItems();
            if (CollectionUtils.isNotEmpty(menuItems)) {
                int index = 0;
                for (MenuItem menuItem : menuItems) {
                    appendRightNavFunctionElement(index++, menuItem, functionElements);
                }
            }
        }
        return functionElements;
    }

    /**
     * @param index
     * @param menuItem
     * @param functionElements
     */
    private void appendMainNavFunctionElement(int index, MenuItem menuItem, List<FunctionElement> functionElements) {
        FunctionElement functionElement = new FunctionElement();
        functionElement.setUuid(menuItem.getUuid());
        functionElement.setId(menuItem.getUuid());
        functionElement.setName("主导航_" + menuItem.getText());
        functionElement.setCode("main_nav_" + (index + 1));
        functionElement.setType(AppWidgetElementType.MENU);
        functionElements.add(functionElement);
    }

    /**
     * @param index
     * @param menuItem
     * @param functionElements
     */
    private void appendRightNavFunctionElement(int index, MenuItem menuItem, List<FunctionElement> functionElements) {
        FunctionElement functionElement = new FunctionElement();
        functionElement.setUuid(menuItem.getUuid());
        functionElement.setId(menuItem.getUuid());
        functionElement.setName("右侧导航_" + menuItem.getText());
        functionElement.setCode("right_nav_" + (index + 1));
        functionElement.setType(AppWidgetElementType.MENU);
        functionElements.add(functionElement);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class MainNav extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -3978755127323678670L;
        private List<MenuItem> menuItems;

        /**
         * @return the menuItems
         */
        public List<MenuItem> getMenuItems() {
            return menuItems;
        }

        /**
         * @param menuItems 要设置的menuItems
         */
        public void setMenuItems(List<MenuItem> menuItems) {
            this.menuItems = menuItems;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class RightNav extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 5947788428012715197L;
        private List<MenuItem> menuItems;

        /**
         * @return the menuItems
         */
        public List<MenuItem> getMenuItems() {
            return menuItems;
        }

        /**
         * @param menuItems 要设置的menuItems
         */
        public void setMenuItems(List<MenuItem> menuItems) {
            this.menuItems = menuItems;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class MenuItem extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 163875891362307092L;

        private String uuid;

        private String text;

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
         * @return the text
         */
        public String getText() {
            return text;
        }

        /**
         * @param text 要设置的text
         */
        public void setText(String text) {
            this.text = text;
        }

    }

}
