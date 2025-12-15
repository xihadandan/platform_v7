/*
 * @(#)2016-11-10 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.support.AppWidgetElementType;
import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import java.util.List;

/**
 * Description: 只读的组件定义功能元素树形视图
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年11月30日.1	zhulh		2020年11月30日		Create
 * </pre>
 * @date 2020年11月30日
 */
public class ReadonlyWidgetDefinitionAsFunctionElementTreeView extends AbstractWidget {

    // 组件定义JSON
    private String widgetDefinition;
    private Widget widget;

    /**
     * @param widgetDefinition
     * @throws JSONException
     */
    public ReadonlyWidgetDefinitionAsFunctionElementTreeView(String widgetDefinition) throws Exception {
        super(widgetDefinition);
        this.widgetDefinition = widgetDefinition;
        this.widget = WidgetDefinitionUtils.parseWidget(this.widgetDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.View#getDefinitionJson()
     */
    @Override
    public String getDefinitionJson() throws Exception {
        return widgetDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Widget#getItems()
     */
    @Override
    public List<Widget> getItems() {
        return widget.getItems();
    }

    /**
     * @return
     */
    public TreeNode getTree() {
        TreeNode treeNode = new TreeNode();
        buildTree(widget, treeNode);
        return treeNode;
    }

    /**
     * @param widget
     * @param treeNode
     */
    private void buildTree(Widget widget, TreeNode treeNode) {
        String widgetId = widget.getId();
        treeNode.setName(widget.getTitle());
        treeNode.setId(widgetId);
        treeNode.setNocheck(true);
        // 子组件
        List<Widget> childItems = widget.getItems();
        for (Widget childItem : childItems) {
            TreeNode childNode = new TreeNode();
            buildTree(childItem, childNode);
            // 存在子组件或组件功能元素时才加入树结点
            if (CollectionUtils.isNotEmpty(childNode.getChildren())) {
                treeNode.getChildren().add(childNode);
            }
        }
        // 组件功能元素
        WidgetConfiguration widgetConfiguration = widget.getConfiguration();
        List<FunctionElement> functionElements = widgetConfiguration.getFunctionElements();
        for (FunctionElement functionElement : functionElements) {
            String type = functionElement.getType();
            if (!(StringUtils.equals(type, AppWidgetElementType.MENU)
                    || StringUtils.equals(type, AppWidgetElementType.NAV) || StringUtils.equals(type,
                    AppWidgetElementType.TAB))) {
                continue;
            }
            TreeNode childNode = new TreeNode();
            childNode.setName(functionElement.getName());
            childNode.setId(Separator.SLASH.getValue() + widgetId + Separator.SLASH.getValue()
                    + functionElement.getId());
            treeNode.getChildren().add(childNode);
        }
    }

}
