/*
 * @(#)2020年5月27日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.params.event;

import com.wellsoft.context.event.WellptEvent;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月27日.1	zhongzh		2020年5月27日		Create
 * </pre>
 * @date 2020年5月27日
 */
public class SysPatamItemRemoveEvent extends WellptEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private SysParamItem sysParamItem;

    /**
     * @param userId
     * @param flowInstUuid
     * @param taskInstUuid
     */
    public SysPatamItemRemoveEvent(SysParamItem sysParamItem) {
        this(null, sysParamItem);
    }

    /**
     * @param source
     * @param userId
     * @param flowInstUuid
     * @param taskInstUuid
     */
    public SysPatamItemRemoveEvent(Object source, SysParamItem sysParamItem) {
        super(source);
        this.sysParamItem = sysParamItem;
    }

    public SysParamItem getSysParamItem() {
        return sysParamItem;
    }

}
