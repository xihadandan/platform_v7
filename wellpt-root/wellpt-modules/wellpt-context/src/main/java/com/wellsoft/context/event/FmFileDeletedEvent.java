/*
 * @(#)2015-5-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.event;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-5-25.1	Administrator		2015-5-25		Create
 * </pre>
 * @date 2015-5-25
 */
public class FmFileDeletedEvent extends WellptEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4280126346792933309L;

    private String fmFileUuid;

    /**
     * @param source
     */
    public FmFileDeletedEvent(String fmFileUuid) {
        super(fmFileUuid);

        this.fmFileUuid = fmFileUuid;
    }

    /**
     * @return the fmFileUuid
     */
    public String getFmFileUuid() {
        return fmFileUuid;
    }

}
