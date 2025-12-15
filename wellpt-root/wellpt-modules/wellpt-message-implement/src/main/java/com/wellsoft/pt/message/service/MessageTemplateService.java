/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.bean.MessageTemplateBean;
import com.wellsoft.pt.message.dao.MessageTemplateDao;
import com.wellsoft.pt.message.entity.MessageTemplate;

import java.util.List;

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
public interface MessageTemplateService extends
        JpaService<MessageTemplate, MessageTemplateDao, String> {

    /**
     * 通过UUID获取消息模板
     *
     * @param uuid
     * @return
     */
    public MessageTemplate getByUuid(String uuid);

    /**
     * 通过ID获取消息模板
     *
     * @param id
     * @return
     */
    public MessageTemplate getById(String id);

    /**
     * 根据消息模板分类获取对应的模板实体对象集合
     *
     * @param category
     * @return
     */
    List<MessageTemplate> getMessageTemplatesByCategory(String category);

    /**
     * 根据消息分类id获取对应的模板实体对象集合
     *
     * @param classifyUuid
     * @return
     */
    List<MessageTemplate> getMessageTemplatesByClassifyUuid(String classifyUuid);

    List<MessageTemplate> getMessageTemplatesByClassifyUuidIsNull();

    /**
     * 通过ID获取消息模板VO对象
     *
     * @param id
     * @return
     */
    public MessageTemplateBean getBeanById(String id);

    /**
     * 保存消息模板
     *
     * @param uuid
     * @return
     */
    public void saveBean(MessageTemplateBean bean);

    /**
     * 消息模板发送消息，用于调试
     *
     * @param uuid
     * @param dataJson
     */
    public void sendTemplateMsg(String uuid, String dataJson, List<String> toUserIds);

    /**
     * 通过UUID删除消息模板
     *
     * @param uuid
     * @return
     */
    public void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuids
     */
    public void deleteAllById(String[] ids);

    void deleteAllByUuids(List<String> uuids);

    /**
     * 获取所有的消息格式树
     *
     * @param id       默认传过去的节点id
     * @param category 消息分类
     * @return
     */
    public List<TreeNode> getAllMessageTemplate(String id, String category);

    /**
     * 获取所有的消息模板
     *
     * @return
     */
    public List<MessageTemplate> getAll();

    /**
     * 获取所有的消息模板
     *
     * @param orderByProperty
     * @param isAsc
     * @return
     */
    public List<MessageTemplate> getAll(String orderByProperty, boolean isAsc);


    Select2QueryData loadSelectionByModule(Select2QueryInfo select2QueryInfo);
}
