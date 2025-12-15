/*
 * @(#)2015年8月20日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.cg.core.support;

import com.wellsoft.pt.dyform.facade.dto.DyFormData;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年8月20日.1	zhulh		2015年8月20日		Create
 * </pre>
 * @date 2015年8月20日
 */
public class BizData implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5186899295226484005L;

    private String formUuid;

    private String dataUuid;

    private DyFormData dyFormData;

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

}
