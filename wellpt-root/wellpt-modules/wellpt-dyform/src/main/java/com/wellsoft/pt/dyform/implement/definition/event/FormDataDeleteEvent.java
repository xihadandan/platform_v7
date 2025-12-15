/*
 * @(#)6/18/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.event;

import com.wellsoft.context.event.WellptEvent;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/18/25.1	    zhulh		6/18/25		    Create
 * </pre>
 * @date 6/18/25
 */
public class FormDataDeleteEvent extends WellptEvent {

    private static final long serialVersionUID = -7672802802093845227L;

    private String fromUuid;
    private String dataUuid;

    /**
     * @param fromUuid
     * @param dataUuid
     */
    public FormDataDeleteEvent(String fromUuid, String dataUuid) {
        super(dataUuid);
        this.fromUuid = fromUuid;
        this.dataUuid = dataUuid;
    }

    /**
     * @return the fromUuid
     */
    public String getFromUuid() {
        return fromUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }
    
}
