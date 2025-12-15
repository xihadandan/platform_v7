/*
 * @(#)Feb 20, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.dms.core.proxy.DyFormDataProxyFactory;
import com.wellsoft.pt.dms.core.proxy.DyFormDataProxyUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.support.DyFormDataDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 20, 2017.1	zhulh		Feb 20, 2017		Create
 * </pre>
 * @date Feb 20, 2017
 */
public class DyFormActionData extends ActionData {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6285462669472950993L;

    private String formUuid;

    private String dataUuid;

    @JsonDeserialize(using = DyFormDataDeserializer.class)
    private DyFormData dyFormData;

    /**
     *
     */
    public DyFormActionData() {
        super();
    }

    /**
     * @param formUuid
     * @param dataUuid
     */
    public DyFormActionData(String formUuid, String dataUuid) {
        super();
        this.formUuid = formUuid;
        this.dataUuid = dataUuid;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
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
     * @param targetClass
     * @return
     */
    public <T extends BaseObject> T getDyFormDataProxy(Class<T> targetClass) {
        return DyFormDataProxyFactory.getProxy(targetClass, dyFormData);
    }

    /**
     * @param targetObject
     */
    public String saveDyFormDataProxy(Object proxy) {
        return DyFormDataProxyUtils.saveFormData(proxy);
    }

}
