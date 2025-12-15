/*
 * @(#)2016-11-10 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui;

import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description: 只读的组件定义
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-11-10.1	zhulh		2016-11-10		Create
 * </pre>
 * @date 2016-11-10
 */
public class ReadonlyWidgetDefinitionView extends AbstractWidget {

    // 组件JSON对象
    private JSONObject jsonObject;
    // 组件定义JSON
    private String widgetDefinition;
    // 包含的组件
    private List<Widget> items;
    // 解析item的类型
    private Class<?> itemViewClass;

    /**
     * @param widgetDefinition
     * @throws JSONException
     */
    public ReadonlyWidgetDefinitionView(String widgetDefinition, Class<?> itemViewClass) throws Exception {
        super(widgetDefinition);
        this.widgetDefinition = widgetDefinition;
        this.jsonObject = new JSONObject(widgetDefinition);
        this.itemViewClass = itemViewClass;

        parseItems();
    }

    /**
     * @throws JSONException
     */
    private void parseItems() throws JSONException {
        List<Widget> tempWidgets = new ArrayList<Widget>();
        if (!jsonObject.has(AppConstants.KEY_ITEMS)) {
            items = Collections.unmodifiableList(new ArrayList<Widget>());
            return;
        }

        JSONArray array = jsonObject.getJSONArray(AppConstants.KEY_ITEMS);
        if (array == null) {
            items = Collections.unmodifiableList(new ArrayList<Widget>());
            return;
        }

        for (int index = 0; index < array.length(); index++) {
            tempWidgets.add(WidgetDefinitionUtils.parseWidget(array.getJSONObject(index).toString(), itemViewClass));
        }
        items = Collections.unmodifiableList(tempWidgets);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.View#getDefinitionJson()
     */
    @Override
    public String getDefinitionJson() throws Exception {
        if (items.isEmpty()) {
            return this.widgetDefinition;
        }

        try {
            List<JSONObject> itemJsons = new ArrayList<JSONObject>();
            for (Widget widget : items) {
                itemJsons.add(new JSONObject(widget.getDefinitionJson()));
            }

            jsonObject.remove(AppConstants.KEY_ITEMS);
            jsonObject.put(AppConstants.KEY_ITEMS, itemJsons);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Widget#getItems()
     */
    @Override
    public List<Widget> getItems() {
        return this.items;
    }

}
