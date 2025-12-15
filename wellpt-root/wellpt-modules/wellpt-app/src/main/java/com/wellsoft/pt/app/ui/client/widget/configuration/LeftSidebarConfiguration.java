/*
 * @(#)2019年6月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.util.tree.TreeUtils;
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
public class LeftSidebarConfiguration extends AppWidgetDefinitionConfiguration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1916428096133061462L;

    // 导航类型1、系统导航，2、自定义，3、自定义接口，4、数据仓库
    private String navType;
    private List<TreeNode> nav;
    private String navDataStoreId;

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
     * @return the navType
     */
    public String getNavType() {
        return navType;
    }

    /**
     * @param navType 要设置的navType
     */
    public void setNavType(String navType) {
        this.navType = navType;
    }

    /**
     * @return the navDataStoreId
     */
    public String getNavDataStoreId() {
        return navDataStoreId;
    }

    /**
     * @param navDataStoreId 要设置的navDataStoreId
     */
    public void setNavDataStoreId(String navDataStoreId) {
        this.navDataStoreId = navDataStoreId;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidgetConfiguration#getFunctionElements()
     */
    @Override
    public List<FunctionElement> getFunctionElements() {
        final List<FunctionElement> functionElements = Lists.newArrayList();
        // 自定义
        if ("2".equals(navType) && CollectionUtils.isNotEmpty(getNav())) {
            TreeUtils.traverseTree(getNav(), new Function<TreeNode, Void>() {
                int index = 0;

                @Override
                public Void apply(TreeNode input) {
                    appendNavFunctionElement(index++, input, functionElements);
                    return null;
                }

            });
        }
        // 数据仓库
        if ("4".equals(navType)) {
            appendRefDataStoreFunctionElementById(getNavDataStoreId(), functionElements);
        }
        return functionElements;
    }

    /**
     * @param index
     * @param button
     * @param functionElements
     */
    private void appendNavFunctionElement(int index, TreeNode treeNode, List<FunctionElement> functionElements) {
        FunctionElement functionElement = new FunctionElement();
        functionElement.setUuid(treeNode.getId());
        functionElement.setId(treeNode.getId());
        functionElement.setName("导航_" + treeNode.getName());
        functionElement.setCode("sidebar_" + (index + 1));
        functionElement.setType(AppWidgetElementType.NAV);
        functionElements.add(functionElement);
    }
}
