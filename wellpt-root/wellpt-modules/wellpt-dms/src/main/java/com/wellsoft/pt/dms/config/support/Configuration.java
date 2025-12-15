/*
 * @(#)Feb 28, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.support;

import com.wellsoft.pt.app.ui.WidgetConfiguration;
import com.wellsoft.pt.dms.core.context.ActionContext;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 28, 2017.1	zhulh		Feb 28, 2017		Create
 * </pre>
 * @date Feb 28, 2017
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface Configuration extends WidgetConfiguration {

    String getName();

    String getCategoryName();

    String getCategoryCode();

    String getCode();

    Store getStore(ActionContext actionContext, HttpServletRequest request);

    Document getDocument(ActionContext actionContext, HttpServletRequest request);

    void setDocument(Document document);

    /**
     * 是否启用版本
     *
     * @return
     */
    boolean isEnableVersioning();

    /**
     * 是否启用置顶
     *
     * @return
     */
    boolean isEnableStick();

    /**
     * 获取置顶状态字段
     *
     * @return
     */
    String getStickStatusField();

    /**
     * 获取置顶时间字段
     *
     * @return
     */
    String getStickTimeField();

    /**
     * 是否启用阅读记录
     *
     * @return
     */
    boolean isEnableReadRecord();

    /**
     * 阅读人员字段
     *
     * @return
     */
    String getReadRecordField();

    View getView(ActionContext actionContext, HttpServletRequest request);

}
