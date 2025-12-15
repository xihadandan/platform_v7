/*
 * @(#)2015-1-27 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.event;

import com.wellsoft.context.event.WellptEvent;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.context.ApplicationListener;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-27.1	zhulh		2015-1-27		Create
 * </pre>
 * @date 2015-1-27
 */
public abstract class WellptEventListener<E extends WellptEvent> extends BaseServiceImpl implements
        ApplicationListener<E> {
}
