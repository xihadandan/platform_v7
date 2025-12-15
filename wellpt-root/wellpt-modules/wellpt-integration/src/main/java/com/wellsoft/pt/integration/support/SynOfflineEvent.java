/*
 * @(#)2016年11月14日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.context.event.WellptEvent;

/**
 * Description: 同步链路离线事件
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年11月14日.1	zhongzh		2016年11月14日		Create
 * </pre>
 * @date 2016年11月14日
 */
public class SynOfflineEvent extends WellptEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 如何描述该构造方法
     *
     * @param source
     */
    public SynOfflineEvent(Object source) {
        super(source);
    }

}
