/*
 * @(#)2020年12月14日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.util.tree.TreeUtils;
import com.wellsoft.pt.app.support.AppWidgetElementType;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月14日.1	zhulh		2020年12月14日		Create
 * </pre>
 * @date 2020年12月14日
 */
public class NavBlockConfiguration extends AppWidgetDefinitionConfiguration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4357414786307780956L;

    // 自定义导航块
    private List<TreeNode> nav;

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
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidgetConfiguration#getFunctionElements()
     */
    @Override
    public List<FunctionElement> getFunctionElements() {
        final List<FunctionElement> functionElements = Lists.newArrayList();
        List<TreeNode> nav = getNav();
        // 自定义导航块
        if (CollectionUtils.isNotEmpty(nav)) {
            Map<String, String> rootIdMap = Maps.newHashMap();
            for (TreeNode treeNode : nav) {
                rootIdMap.put(treeNode.getId(), treeNode.getId());
            }
            TreeUtils.traverseTree(nav, new Function<TreeNode, Void>() {
                int index = 0;

                @Override
                public Void apply(TreeNode input) {
                    // 忽略根结点
                    if (rootIdMap.containsKey(input.getId())) {
                        return null;
                    }
                    appendNavBlockFunctionElement(index++, input, functionElements);
                    return null;
                }

            });
        }
        return functionElements;
    }

    /**
     * @param index
     * @param treeNode
     * @param functionElements
     */
    private void appendNavBlockFunctionElement(int index, TreeNode treeNode, List<FunctionElement> functionElements) {
        FunctionElement functionElement = new FunctionElement();
        functionElement.setUuid(treeNode.getId());
        functionElement.setId(treeNode.getId());
        functionElement.setName("自定义导航块_" + treeNode.getName());
        functionElement.setCode("nav_block_" + (index + 1));
        functionElement.setType(AppWidgetElementType.NAV);
        functionElements.add(functionElement);
    }

}
