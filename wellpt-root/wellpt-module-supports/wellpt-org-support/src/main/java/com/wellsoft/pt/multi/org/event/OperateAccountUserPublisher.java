/*
 * @(#)2018年4月11日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * Description: 账号操作事件发布
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月11日.1	chenqiong		2018年4月11日		Create
 * </pre>
 * @date 2018年4月11日
 */
@Component
public class OperateAccountUserPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    public void publish(AccountUserEvent.AccountUserEventSource source) {
        this.publisher.publishEvent(new AccountUserEvent(source));
    }


}
