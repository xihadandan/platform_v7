/*
 * @(#)2014-8-20 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.provider;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.message.support.Message;

import java.util.Collection;
import java.util.Map;


public abstract class AbstractMessageEventSourceProvider implements MessageEventSourceProvider {
    /**
     * 接收消息后触发事件
     *
     * @param mesage
     */
    public void executeServerMessageEvent(Message mesage, String viewpoint, String note) {
    }

    /**
     * 发送消息触发事件
     *
     * @param mesage
     */
    public <ENTITY extends IdEntity> void executeClientMessageEvent(String templateId, Collection<ENTITY> entities,
                                                                    Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, ENTITY entity) {

    }

}
