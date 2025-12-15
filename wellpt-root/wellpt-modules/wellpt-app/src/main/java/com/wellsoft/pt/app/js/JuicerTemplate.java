/*
 * @(#)2017-01-03 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.js;

import com.wellsoft.pt.app.support.AppConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;

/**
 * Description: Juicer的HTML模板
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-03.1	zhulh		2017-01-03		Create
 * </pre>
 * @date 2017-01-03
 */
public class JuicerTemplate implements JavaScriptTemplate {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -547079699191667375L;

    // 模板ID
    private String id;
    // 模板名称
    private String name;
    // 模板内容
    private String content;
    // 模板内容来源
    private String source;
    // 模板内容来源是否可重新加载
    private boolean sourceReloadable = true;
    // 排序号
    private int order;

    /**
     *
     */
    public JuicerTemplate() {
        super();
    }

    /**
     * @param id
     * @param name
     * @param source
     * @param order
     */
    public JuicerTemplate(String id, String name, String source, int order) {
        super();
        this.id = id;
        this.name = name;
        this.source = source;
        this.order = order;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
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

    /**
     * @return the content
     */
    public String getContent() {
        if (StringUtils.isBlank(content)) {
            readContentSource();
            return content;
        }
        if (sourceReloadable && StringUtils.isNotBlank(source)) {
            readContentSource();
        }
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source 要设置的source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the sourceReloadable
     */
    public boolean isSourceReloadable() {
        return sourceReloadable;
    }

    /**
     * @param sourceReloadable 要设置的sourceReloadable
     */
    public void setSourceReloadable(boolean sourceReloadable) {
        this.sourceReloadable = sourceReloadable;
    }

    /**
     * 读取内容来源
     */
    public void readContentSource() {
        try {
            if (StringUtils.isBlank(source)) {
                return;
            }
            String source = this.source;
            if (!source.endsWith(AppConstants.DOT_HTML)) {
                source += AppConstants.DOT_HTML;
            }
            InputStream ins = JuicerTemplate.class.getResourceAsStream(source);
            setContent(IOUtils.toString(ins));
            ins.close();
            IOUtils.closeQuietly(ins);
        } catch (Exception e) {
            throw new RuntimeException("JavaScriptTemplate readContentSource error:" + getName(), e);
        }
    }

    /**
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * @param order 要设置的order
     */
    public void setOrder(int order) {
        this.order = order;
    }

}
