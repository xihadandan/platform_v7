/*
 * @(#)2019年6月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.tree.TreeUtils;
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
public class NavbarConfiguration extends AppWidgetDefinitionConfiguration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6561553525196056646L;

    // 导航类型——是否自定义导航
    private String isShowCustomNavbar;

    // 自定义导航
    private List<TreeNode> nav = Lists.newArrayList();

    // 主导航
    private SubNav subNav;

    /**
     * @return the isShowCustomNavbar
     */
    public String getIsShowCustomNavbar() {
        return isShowCustomNavbar;
    }

    /**
     * @param isShowCustomNavbar 要设置的isShowCustomNavbar
     */
    public void setIsShowCustomNavbar(String isShowCustomNavbar) {
        this.isShowCustomNavbar = isShowCustomNavbar;
    }

    /**
     * @return the nav
     */
    public List<TreeNode> getNav() {
        return nav;
    }

    /**
     * @param nav 要设置的nav
     */
    public void setNav(List<TreeNode> nav) {
        this.nav = nav;
    }

    /**
     * @return the subNav
     */
    public SubNav getSubNav() {
        return subNav;
    }

    /**
     * @param subNav 要设置的subNav
     */
    public void setSubNav(SubNav subNav) {
        this.subNav = subNav;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidgetConfiguration#getFunctionElements()
     */
    @Override
    public List<FunctionElement> getFunctionElements() {
        final List<FunctionElement> functionElements = Lists.newArrayList();
        // 自定义导航
        if (Config.TRUE.equalsIgnoreCase(isShowCustomNavbar) && CollectionUtils.isNotEmpty(getNav())) {
            TreeUtils.traverseTree(getNav(), new Function<TreeNode, Void>() {
                int index = 0;

                @Override
                public Void apply(TreeNode input) {
                    appendNavFunctionElement(index++, input, functionElements);
                    return null;
                }

            });
        } else if (getSubNav() != null) {
            List<MenuItem> menuItems = getSubNav().getMenuItems();
            if (CollectionUtils.isNotEmpty(menuItems)) {
                int index = 0;
                for (MenuItem menuItem : menuItems) {
                    appendSubNavFunctionElement(index++, menuItem, functionElements);
                }
            }
        }

        return functionElements;
    }

    /**
     * @param index
     * @param tab
     * @param functionElements
     */
    private void appendNavFunctionElement(int index, TreeNode treeNode, List<FunctionElement> functionElements) {
        FunctionElement functionElement = new FunctionElement();
        functionElement.setUuid(treeNode.getId());
        functionElement.setId(treeNode.getId());
        functionElement.setName("自定义主导航_" + treeNode.getName());
        functionElement.setCode("nav_" + (index + 1));
        functionElement.setType(AppWidgetElementType.NAV);
        functionElements.add(functionElement);
    }

    /**
     * @param index
     * @param menuItem
     * @param functionElements
     */
    private void appendSubNavFunctionElement(int index, MenuItem menuItem, List<FunctionElement> functionElements) {
        FunctionElement functionElement = new FunctionElement();
        functionElement.setUuid(menuItem.getUuid());
        functionElement.setId(menuItem.getUuid());
        functionElement.setName("主导航_" + menuItem.getText());
        functionElement.setCode("sub_nav_" + (index + 1));
        functionElement.setType(AppWidgetElementType.NAV);
        functionElements.add(functionElement);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class SubNav extends BaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = -3505294032863456902L;

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
        private static final long serialVersionUID = 121399184697423352L;

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
