/*
 * @(#)May 23, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.dms.bean.DmsDocExchangeRecordDto;
import com.wellsoft.pt.dms.core.proxy.DyFormDataProxyFactory;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class DocExchangeDocumentData extends AbstractDocumentData {

    public String title;

    // 表单数据
    private DyFormData dyFormData;

    private DmsDocExchangeRecordDto docExchangeRecord;

    private boolean displayAsLabel;

    /**
     *
     */
    public DocExchangeDocumentData() {
        super();
    }

    /**
     * @param dyFormData
     */
    public DocExchangeDocumentData(DyFormData dyFormData) {
        this.dyFormData = dyFormData;
    }

    /**
     * @param dyFormData
     */
    public DocExchangeDocumentData(DyFormData dyFormData, boolean displayAsLabel) {
        this.dyFormData = dyFormData;
        this.displayAsLabel = displayAsLabel;
    }

    /**
     * (non-Javadoc)
     *
     * @see DocumentData#getData()
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

    public DmsDocExchangeRecordDto getDocExchangeRecord() {
        return docExchangeRecord;
    }

    public void setDocExchangeRecord(DmsDocExchangeRecordDto docExchangeRecord) {
        this.docExchangeRecord = docExchangeRecord;
    }
}
