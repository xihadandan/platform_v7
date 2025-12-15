/*
 * @(#)Jan 22, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.support;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.ui.client.widget.configuration.AppWidgetDefinitionConfiguration;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import com.wellsoft.pt.dms.core.context.ActionContext;

import javax.servlet.http.HttpServletRequest;
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
 * Jan 22, 2018.1	zhulh		Jan 22, 2018		Create
 * </pre>
 * @date Jan 22, 2018
 */
public class DmsDataManagementViewerConfiguration extends AppWidgetDefinitionConfiguration implements Configuration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3301166764093116153L;

    private String name;
    private String categoryName;
    private String categoryCode;
    private String code;

    private Store store;

    private Document document;

    private View view;

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

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName 要设置的categoryName
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return the categoryCode
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * @param categoryCode 要设置的categoryCode
     */
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the store
     */
    public Store getStore(ActionContext actionContext, HttpServletRequest request) {
        return store;
    }

    /**
     * @return the store
     */
    public Store getStore() {
        return store;
    }

    /**
     * @param store 要设置的store
     */
    public void setStore(Store store) {
        this.store = store;
    }

    /**
     * @return the document
     */
    public Document getDocument(ActionContext actionContext, HttpServletRequest request) {
        return document;
    }

    /**
     * @param document 要设置的document
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.Configuration#isEnableVersioning()
     */
    @Override
    public boolean isEnableVersioning() {
        if (this.document == null) {
            return false;
        }
        return document.isEnableVersioning();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.Configuration#isEnableStick()
     */
    @Override
    public boolean isEnableStick() {
        if (getStore() == null) {
            return false;
        }
        return getStore().isStick();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.Configuration#getStickStatusField()
     */
    @Override
    public String getStickStatusField() {
        if (getStore() == null) {
            return null;
        }
        return getStore().getStickStatusField();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.Configuration#getStickTimeField()
     */
    @Override
    public String getStickTimeField() {
        if (getStore() == null) {
            return null;
        }
        return getStore().getStickTimeField();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.Configuration#isEnableReadRecord()
     */
    @Override
    public boolean isEnableReadRecord() {
        if (getStore() == null) {
            return false;
        }
        return getStore().isReadRecord();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.config.support.Configuration#getReadRecordField()
     */
    @Override
    public String getReadRecordField() {
        if (getStore() == null) {
            return null;
        }
        return getStore().getReadRecordField();
    }

    /**
     * @return the view
     */
    public View getView(ActionContext actionContext, HttpServletRequest request) {
        return view;
    }

    /**
     * @param view 要设置的view
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidgetConfiguration#getFunctionElements()
     */
    @Override
    public List<FunctionElement> getFunctionElements() {
        List<FunctionElement> functionElements = Lists.newArrayList();
        Store store = getStore();
        if (store != null) {
            appendRefDyformFunctionElementByUuid(store.getFormUuid(), functionElements);
        }
        return functionElements;
    }

}
