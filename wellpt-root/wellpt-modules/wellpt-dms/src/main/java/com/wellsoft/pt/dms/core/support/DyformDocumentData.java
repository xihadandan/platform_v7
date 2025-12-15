/*
 * @(#)May 23, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.dms.core.proxy.DyFormDataProxyFactory;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * May 23, 2017.1	zhulh		May 23, 2017		Create
 * </pre>
 * @date May 23, 2017
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DyformDocumentData extends AbstractDocumentData {

    private String title;

    // 表单数据
    private DyFormData dyFormData;

    private boolean displayAsLabel;

    /**
     *
     */
    public DyformDocumentData() {
        super();
    }

    /**
     * @param dyFormData
     */
    public DyformDocumentData(DyFormData dyFormData) {
        this.dyFormData = dyFormData;
    }

    /**
     * @param dyFormData
     */
    public DyformDocumentData(DyFormData dyFormData, boolean displayAsLabel) {
        this.dyFormData = dyFormData;
        this.displayAsLabel = displayAsLabel;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.core.support.DocumentData#getData()
     */
    @Override
    @JsonIgnore
    public Object getData() {
        return getDyFormData();
    }

    /**
     * @return the dyFormData
     */
    public DyFormData getDyFormData() {
        return dyFormData;
    }

    /**
     * @param dyFormData 要设置的dyFormData
     */
    public void setDyFormData(DyFormData dyFormData) {
        this.dyFormData = dyFormData;
    }

    /**
     * @return the displayAsLabel
     */
    public boolean isDisplayAsLabel() {
        return displayAsLabel;
    }

    /**
     * @param displayAsLabel 要设置的displayAsLabel
     */
    public void setDisplayAsLabel(boolean displayAsLabel) {
        this.displayAsLabel = displayAsLabel;
    }

    /**
     * @param targetClass
     * @return
     */
    @JsonIgnore
    public <T extends BaseObject> T getDyFormDataProxy(Class<T> targetClass) {
        return DyFormDataProxyFactory.getProxy(targetClass, dyFormData);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
