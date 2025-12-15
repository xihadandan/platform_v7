/*
 * @(#)2019年6月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.support.AppWidgetElementType;
import org.apache.commons.lang3.StringUtils;

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
public class PanelConfiguration extends AppWidgetDefinitionConfiguration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5556850432822414446L;

    private Header header;

    private Body body;

    /**
     * @return the header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * @param header 要设置的header
     */
    public void setHeader(Header header) {
        this.header = header;
    }

    /**
     * @return the body
     */
    public Body getBody() {
        return body;
    }

    /**
     * @param body 要设置的body
     */
    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidgetConfiguration#getFunctionElements()
     */
    @Override
    public List<FunctionElement> getFunctionElements() {
        List<FunctionElement> functionElements = Lists.newArrayList();
        Header header = getHeader();
        Body body = getBody();
        // 1、引用的数据仓库
        if (header != null) {
            appendRefDataStoreFunctionElementById(header.getGetBadgeCountDataStore(), functionElements);
        }

        // 2、内容自定义
        if (body != null && StringUtils.equals(body.getContentType(), "contentTab")) {
            appendTabFunctionElement(body, functionElements);
        }
        return functionElements;
    }

    /**
     * @param body
     * @param functionElements
     */
    private void appendTabFunctionElement(Body body, List<FunctionElement> functionElements) {
        List<Tab> tabs = body.getTabs();
        int index = 0;
        for (Tab tab : tabs) {
            FunctionElement functionElement = new FunctionElement();
            functionElement.setUuid(tab.getUuid());
            functionElement.setId(tab.getUuid());
            functionElement.setName("页签_" + tab.getName());
            functionElement.setCode("tab_" + (index + 1));
            functionElement.setType(AppWidgetElementType.TAB);
            functionElements.add(functionElement);
            index++;
        }
    }

    private static class Header extends ConfigurationBaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 7994491143994303248L;
        private String getBadgeCountDataStore;

        /**
         * @return the getBadgeCountDataStore
         */
        public String getGetBadgeCountDataStore() {
            return getBadgeCountDataStore;
        }

        /**
         * @param getBadgeCountDataStore 要设置的getBadgeCountDataStore
         */
        public void setGetBadgeCountDataStore(String getBadgeCountDataStore) {
            this.getBadgeCountDataStore = getBadgeCountDataStore;
        }

    }

    private static class Body extends ConfigurationBaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 8674001789330855714L;

        private String contentType;

        private List<Tab> tabs;

        /**
         * @return the contentType
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * @param contentType 要设置的contentType
         */
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

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

    }

    private static class Tab extends ConfigurationBaseObject {

        /**
         * 如何描述serialVersionUID
         */
        private static final long serialVersionUID = 1679501778367588286L;

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
