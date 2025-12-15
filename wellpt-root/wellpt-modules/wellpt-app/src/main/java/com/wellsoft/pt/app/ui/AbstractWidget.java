/*
 * @(#)2016年3月30日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.ui.client.widget.configuration.AppWidgetDefinitionConfiguration;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月30日.1	zhulh		2016年3月30日		Create
 * </pre>
 * @date 2016年3月30日
 */
public abstract class AbstractWidget implements Widget {

    // 组件JSON对象
    protected final JSONObject jsonObject;
    // 组件定义JSON
    protected final String widgetDefinition;

    private String id;
    private String title;
    private String wtype;

    /**
     * @throws Exception
     */
    public AbstractWidget() throws Exception {
        this(AppConstants.JSON_EMPTY);
    }

    /**
     * @param widgetDefinition
     */
    public AbstractWidget(String widgetDefinition) throws Exception {
        this.widgetDefinition = widgetDefinition;
        this.jsonObject = new JSONObject(widgetDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.View#getId()
     */
    @Override
    public String getId() {
        if (StringUtils.isBlank(this.id)) {
            this.id = getJsonString(AppConstants.KEY_ID);
        }
        return this.id;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.View#getTitle()
     */
    @Override
    public String getTitle() {
        if (StringUtils.isBlank(this.title)) {
            this.title = getJsonString(AppConstants.KEY_TITLE, StringUtils.EMPTY);
        }
        return this.title;
    }

    /**
     * @return the wtype
     */
    public String getWtype() {
        if (StringUtils.isBlank(this.wtype)) {
            this.wtype = getJsonString(AppConstants.KEY_WTYPE);
        }
        return this.wtype;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Widget#getAttribute(java.lang.String)
     */
    @Override
    public String getAttribute(String key) {
        return getJsonString(key, null);
    }

    @Override
    public <T> T getAttribute(String key, Class<T> objectType) {
        if (this.jsonObject.has(key)) {
            return (T) this.jsonObject.get(key);
        }
        return null;
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Widget#getConfiguration()
     */
    @Override
    public WidgetConfiguration getConfiguration() {
        return getConfiguration(AppWidgetDefinitionConfiguration.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Widget#getConfiguration(java.lang.Class)
     */
    @Override
    public <C extends WidgetConfiguration> C getConfiguration(Class<C> configurationClass) {
        try {
            String configuration = "{}";
            if (jsonObject.has(AppConstants.KEY_CONFIGURATION)) {
                configuration = jsonObject.getJSONObject(AppConstants.KEY_CONFIGURATION).toString();
            }
            return JsonUtils.json2Object(configuration, configurationClass);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.Widget#isReferenceWidget()
     */
    @Override
    public Boolean isReferenceWidget() {
        return StringUtils.isNotBlank(getAttribute(AppConstants.KEY_REF_WIDGET_DEF_UUID));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.View#useDefinitionHtml()
     */
    @Override
    public boolean useDefinitionHtml() {
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.View#render(com.wellsoft.pt.app.context.AppContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String render(AppContext appContext, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return this.jsonObject.getString(AppConstants.KEY_HTML);
    }

    /**
     * @param key
     * @return
     */
    private String getJsonString(String key) {
        try {
            if (this.jsonObject.has(key)) {
                return this.jsonObject.getString(key);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * @param key
     * @param defaultValue
     * @return
     */
    private String getJsonString(String key, String defaultValue) {
        if (!this.jsonObject.has(key)) {
            return defaultValue;
        }
        return getJsonString(key);
    }

}
