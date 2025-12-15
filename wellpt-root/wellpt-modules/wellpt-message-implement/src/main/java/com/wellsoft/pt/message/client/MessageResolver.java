/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.client;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.message.support.JmsMessage;

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
 * 2012-11-9.1	zhulh		2012-11-9		Create
 * </pre>
 * @date 2012-11-9
 */
public interface MessageResolver {
    /**
     * 根据消息模板及用户解析为JMS消息
     *
     * @param templateId
     * @param entity
     * @param recipients
     * @return
     */
    <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                 Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients);

    /**
     * 根据消息模板及用户解析为JMS消息
     *
     * @param templateId
     * @param entities
     * @param dataMap
     * @param extraData
     * @param recipients
     * @param datauuid
     * @return
     */
    <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                 Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, String datauuid);

    /**
     * 根据消息模板及用户解析为JMS消息
     *
     * @param templateId
     * @param entities
     * @param dataMap
     * @param extraData
     * @param recipients
     * @param datauuid      业务数据逐渐id
     * @param attachmentIds 附件id
     * @return
     */
    public <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                        Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, String datauuid,
                                                        String[] attachmentIds);

    /**
     * 如何描述该方法
     *
     * @param templateId
     * @param entities
     * @param dataMap
     * @param extraData
     * @param recipients
     * @param datauuid
     * @param attachmentIds
     * @param sendWays      发送方式
     * @return
     */
    public <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                        Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, String datauuid,
                                                        String[] attachmentIds, String sendWays);

    /**
     * 根據消息模板及用解析為jms消息
     *
     * @param templateId
     * @param entities
     * @param dataMap
     * @param extraData
     * @param recipients
     * @param entity
     * @return
     */
    <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                 Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, ENTITY entity,
                                                 String datauuid);

    /**
     * 根據消息模板及用解析為jms消息
     *
     * @param templateId
     * @param entities
     * @param dataMap
     * @param extraData
     * @param recipients
     * @param entity
     * @param datauuid
     * @param attachmentIds
     * @return
     */
    <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                 Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, ENTITY entity,
                                                 String datauuid, String[] attachmentIds);

    /**
     * @param templateId
     * @param entities
     * @param dataMap
     * @param extraData
     * @param recipients
     * @param entity
     * @param datauuid
     * @param attachmentIds
     * @param sendWays
     * @param <ENTITY>
     * @return
     */
    <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                 Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, ENTITY entity,
                                                 String datauuid, String[] attachmentIds, String sendWays);

    /**
     * 根據消息模板及用解析為jms消息
     *
     * @param templateId
     * @param entities
     * @param dataMap
     * @param extraData
     * @param recipients
     * @param entity
     * @param datauuid
     * @return
     */
    <ENTITY extends IdEntity> JmsMessage resolve(String templateId, Collection<ENTITY> entities,
                                                 Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, ENTITY entity);

    /**
     * 根據uuid和tpye，生成取消信息的jms信息
     *
     * @param datauuid
     * @param type
     * @return
     */
    <ENTITY extends IdEntity> JmsMessage resolve(String datauuid);

}
