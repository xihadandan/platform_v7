/*
 * @(#)6/17/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.event;

import com.wellsoft.context.event.WellptEvent;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/17/25.1	    zhulh		6/17/25		    Create
 * </pre>
 * @date 6/17/25
 */
public class FormDataSavedEvent extends WellptEvent {

    private String formUuid;
    private String dataUuid;
    private DyFormData dyFormData;
    private String system;

    /**
     * @param formUuid
     * @param dataUuid
     * @param dyFormData
     */
    public FormDataSavedEvent(String formUuid, String dataUuid, DyFormData dyFormData, String system) {
        super(dataUuid);
        this.formUuid = formUuid;
        this.dataUuid = dataUuid;
        this.dyFormData = dyFormData;
        this.system = system;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @return the dyFormData
     */
    public DyFormData getDyFormData() {
        return dyFormData;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

}
