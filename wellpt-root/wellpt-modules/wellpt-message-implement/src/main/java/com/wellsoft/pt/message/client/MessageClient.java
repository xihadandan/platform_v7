/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.client;

import com.wellsoft.context.jdbc.entity.IdEntity;

import java.util.Collection;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-29.1	zhulh		2012-10-29		Create
 * </pre>
 * @date 2012-10-29
 */
public interface MessageClient {
    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId
     * @param entity
     * @param recipients
     */
    <ENTITY extends IdEntity> void send(String templateId, ENTITY entity, Collection<String> recipients);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId
     * @param entity
     * @param recipients
     */
    <ENTITY extends IdEntity> void send(String templateId, ENTITY entity, Collection<String> recipients, String dataUuid);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId
     * @param entity
     * @param recipients
     */
    <ENTITY extends IdEntity> void send(String templateId, ENTITY entity, Map<Object, Object> dytableMap,
                                        Collection<String> recipients);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId
     * @param entities
     * @param recipients
     */
    <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities, Collection<String> recipients);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId
     * @param entities
     * @param recipients
     */
    <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities, Map<Object, Object> dytableMap,
                                        Collection<String> recipients);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId
     * @param entities
     * @param recipients
     */
    <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities, Map<Object, Object> dytableMap,
                                        Map<String, Object> root, Collection<String> recipients);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId
     * @param entities
     * @param dytableMap
     * @param recipients
     * @param entity
     */
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap, Collection<String> recipients, ENTITY entity);

    /**
     * @param templateId
     * @param sendWays
     * @param entities
     * @param dytableMap
     * @param recipients
     * @param entity
     * @param dataUuid
     * @param attachmentIds
     * @param <ENTITY>
     */
    public <ENTITY extends IdEntity> void send(String templateId, String sendWays, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap, Collection<String> recipients,
                                               ENTITY entity, String dataUuid, String[] attachmentIds);


    /**
     * 取消消息
     *
     * @param dataUuid
     */
    public <ENTITY extends IdEntity> void cancelMessage(String dataUuid);
}
