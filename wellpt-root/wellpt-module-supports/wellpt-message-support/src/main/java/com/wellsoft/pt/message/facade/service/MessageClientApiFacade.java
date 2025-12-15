/*
 * @(#)2013-1-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.facade.service;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.message.entity.MessageOutbox;
import com.wellsoft.pt.message.support.MessageParams;

import java.util.Collection;
import java.util.Map;

/**
 * Description: 消息发送对外接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-28.1	zhulh		2013-1-28		Create
 * </pre>
 * @date 2013-1-28
 */
public interface MessageClientApiFacade extends BaseService {

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entity     实体数据
     * @param recipients 接收用户ID集合
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> userIds);


    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entity     实体数据
     * @param userIds    接收用户ID集合
     * @param datauuid
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> userIds,
                                               String datauuid);

    /**
     * 发送消息
     *
     * @param templateId    模板ID
     * @param entity        实体数据
     * @param userIds       接收用户ID集合
     * @param datauuid
     * @param attachmentIds 附件ID集合
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> userIds,
                                               String datauuid, String[] attachmentIds);

    /**
     * 发送消息
     *
     * @param templateId    模板ID
     * @param sendWays      发送方式
     * @param entity        实体数据
     * @param userIds       接收用户ID集合
     * @param datauuid
     * @param attachmentIds 附件IDS
     */
    public <ENTITY extends IdEntity> void send(String templateId, String sendWays, ENTITY entity,
                                               Collection<String> userIds, String datauuid,
                                               String[] attachmentIds);

    /**
     * 发送消息
     *
     * @param templateId    模板ID
     * @param entity        实体数据
     * @param userIds       接收用户ID集合
     * @param attachmentIds 附件IDS
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Collection<String> userIds,
                                               String[] attachmentIds);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entity     业务实体
     * @param dytableMap 表单数据
     * @param userIds    接收用户ID集合
     */
    public <ENTITY extends IdEntity> void send(String templateId, ENTITY entity,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> userIds);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entities   实体数据集合
     * @param userIds    接收用户ID集合
     */
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Collection<String> userIds);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entities   实体数据集合
     * @param dytableMap 表单数据
     * @param userIds    接收用户ID集合
     */
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> userIds);

    /**
     * @param templateId
     * @param sendWays
     * @param entities
     * @param dytableMap
     * @param userIds
     * @param dataUuid
     * @param attachmentIds
     * @param <ENTITY>
     */
    public <ENTITY extends IdEntity> void send(String templateId, String sendWays, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> userIds, String dataUuid, String[] attachmentIds);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entities   实体数据集合
     * @param dytableMap 表单数据
     * @param userIds    接收用户ID集合
     * @param entity     实体数据
     */
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> userIds, ENTITY entity);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param sendWays   发送方法
     * @param entities   实体数据集合
     * @param dytableMap 表单数据
     * @param userIds    接收用户ID集合
     * @param entity     实体数据
     * @param dataUuid   关联数据UUID
     */
    public <ENTITY extends IdEntity> void send(String templateId, String sendWays, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> userIds, ENTITY entity, String dataUuid, String[] attachmentIds);

    /**
     * @param templateId 模板ID
     * @param entities
     * @param dytableMap 表单数据
     * @param recipients 接收用户ID集合
     * @param entity     消息实体
     * @param dataUuid
     */
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Collection<String> recipients, ENTITY entity,
                                               String dataUuid);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给指定的人
     *
     * @param templateId 模板ID
     * @param entities   实体数据集合
     * @param dytableMap 表单数据
     * @param root       其他数据
     * @param userIds    接收用户ID集合
     */
    public <ENTITY extends IdEntity> void send(String templateId, Collection<ENTITY> entities,
                                               Map<Object, Object> dytableMap,
                                               Map<String, Object> root,
                                               Collection<String> userIds);

    /**
     * 指定消息格式中的ID，发送相应的业务实体消息，给互联网用户
     *
     * @param templateId 模板ID
     * @param entity     实体数据
     * @param extraData  额外数据
     * @param userIds    接收用户ID集合
     */
    <ENTITY extends IdEntity> void sendToInternetUser(String templateId, ENTITY entity, Map<String, Object> extraData,
                                                      Collection<String> userIds);

    /**
     * 发送普通短信，手机号收信人不是系统内数据
     *
     * @param recipients   收信人ID
     * @param mbphones     收信人手机号
     * @param body         短信内容
     * @param businesskey1 业务键1
     * @param businesskey2 业务键2
     * @param businesskey3 业务键3
     * @return 发送结果
     */
    public String sendSmsMessages(String recipients, String mbphones, String body,
                                  String businesskey1,
                                  String businesskey2, String businesskey3);

    /**
     * 发送普通短信，手机号收信人不是系统内数据
     *
     * @param recipients   收信人ID
     * @param mbphones     收信人手机号
     * @param body         短信内容
     * @param businesskey1 业务键1
     * @param businesskey2 业务键2
     * @param businesskey3 业务键3
     * @return 发送结果
     */
    public String sendSmsMessages(String recipients, String mbphones, String body,
                                  String businesskey1,
                                  String businesskey2, String businesskey3, Boolean async);

    /**
     * 取消发件箱消息
     *
     * @param dataUuid
     */
    public <ENTITY extends IdEntity> void cancelMessage(String dataUuid);

    /**
     * 取消定时消息
     *
     * @param businessId
     * @return
     */
    public boolean cancelScheduleMessage(String businessId);

    /**
     * 发送消息
     *
     * @param params
     */
    public void sendByParams(MessageParams params);

    /**
     * 删除消息
     *
     * @param correlationId
     */
    void deleteByCorrelationId(String correlationId);

    /**
     * 获取消息发送记录
     *
     * @param messageId
     * @return
     */
    MessageOutbox getOutBoxByMessageId(String messageId);

    /**
     * 获取消息发送结果代码，0：成功，1：失败，2：发送中，3：取消，4：超时，5：失败重试，6：部分成功
     *
     * @param messageId
     * @return
     */
    Integer getSendResultCodeByMessageId(String messageId, String... sendWays);
}
