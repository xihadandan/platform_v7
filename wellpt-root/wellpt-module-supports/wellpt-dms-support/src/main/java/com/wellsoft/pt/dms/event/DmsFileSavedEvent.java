/*
 * @(#)6/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.event;

import com.wellsoft.context.event.WellptEvent;
import com.wellsoft.pt.dms.entity.DmsFileEntity;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 6/23/25.1	    zhulh		6/23/25		    Create
 * </pre>
 * @date 6/23/25
 */
public class DmsFileSavedEvent extends WellptEvent {

    private DmsFileEntity dmsFileEntity;

    private String fileID;

    private Object dyFormData;

    /**
     * @param dmsFileEntity
     */
    public DmsFileSavedEvent(DmsFileEntity dmsFileEntity) {
        super(dmsFileEntity.getUuid());
        this.dmsFileEntity = dmsFileEntity;
    }

    /**
     * @param dmsFileEntity
     * @param fileID
     */
    public DmsFileSavedEvent(DmsFileEntity dmsFileEntity, String fileID) {
        super(dmsFileEntity.getUuid());
        this.dmsFileEntity = dmsFileEntity;
        this.fileID = fileID;
    }

    /**
     * @param dyFormData
     * @param dmsFileEntity
     */
    public DmsFileSavedEvent(Object dyFormData, DmsFileEntity dmsFileEntity) {
        super(dmsFileEntity.getUuid());
        this.dyFormData = dyFormData;
        this.dmsFileEntity = dmsFileEntity;
    }

    /**
     * @return the dmsFileEntity
     */
    public DmsFileEntity getDmsFileEntity() {
        return dmsFileEntity;
    }

    /**
     * @return the fileID
     */
    public String getFileID() {
        return fileID;
    }

    /**
     * @return the dyFormData
     */
    public Object getDyFormData() {
        return dyFormData;
    }
}
