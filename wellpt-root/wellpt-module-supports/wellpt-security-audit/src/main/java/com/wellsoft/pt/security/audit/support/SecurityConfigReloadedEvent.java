/*
 * @(#)2016-12-20 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.support;

import com.wellsoft.context.event.WellptEvent;

/**
 * Description: 安全配置重新加载事件
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-12-20.1	zhulh		2016-12-20		Create
 * </pre>
 * @date 2016-12-20
 */
public class SecurityConfigReloadedEvent extends WellptEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7977329435612481912L;

    /**
     * @param source
     */
    public SecurityConfigReloadedEvent(Object source) {
        super(source);
    }

}
