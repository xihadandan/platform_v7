/*
 * @(#)2016-09-12 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client;

import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import com.wellsoft.pt.app.ui.AbstractWidget;
import com.wellsoft.pt.app.ui.Widget;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description: 默认的组件定义后台Java实例化代理对象
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-12.1	zhulh		2016-09-12		Create
 * </pre>
 * @date 2016-09-12
 */
public class DefaultWidgetDefinitionProxyView extends AbstractWidget {

    // 包含的组件
    protected final List<Widget> items;

    public DefaultWidgetDefinitionProxyView(String widgetDefinition, boolean parseJSON) throws Exception {
        super(widgetDefinition);

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
            tempWidgets.add(WidgetDefinitionUtils.fromJSON(array.getJSONObject(index).toString()));
        }
        items = Collections.unmodifiableList(tempWidgets);
    }

    /**
     * @param widgetDefinition
     * @throws JSONException
     */
    public DefaultWidgetDefinitionProxyView(String widgetDefinition) throws Exception {
        super(widgetDefinition);

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
            tempWidgets.add(WidgetDefinitionUtils.parseWidget(array.getJSONObject(index).toString()));
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
