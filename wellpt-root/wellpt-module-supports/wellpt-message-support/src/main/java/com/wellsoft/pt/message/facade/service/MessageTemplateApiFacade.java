package com.wellsoft.pt.message.facade.service;

import com.wellsoft.pt.message.entity.MessageTemplate;

import java.util.List;

public interface MessageTemplateApiFacade {

    /**
     * 根据uuid获取对应的消息模板
     *
     * @param uuid 模板UUID
     * @return 消息模板
     */
    public MessageTemplate getMessageTemplateByUuid(String uuid);

    /**
     * 根据ID获取对应的消息模板
     *
     * @param id 消息模板ID
     * @return 消息模板
     */
    public MessageTemplate getById(String id);

    /**
     * 根据消息模板分类获取对应的模板实体对象集合
     *
     * @param category
     * @return
     */
    public List<MessageTemplate> getMessageTemplatesByCategory(String category);

    /**
     * 获取所有的消息模板
     *
     * @return 所有的消息模板
     */
    public List<MessageTemplate> getAll();

    /**
     * 获取所有的消息模板
     *
     * @param orderByProperty 排序树形
     * @param isAsc           是否正序
     * @return 所有的消息模板
     */
    public List<MessageTemplate> getAll(String orderByProperty, boolean isAsc);

    /**
     * @param templateId
     * @return
     */
    public MessageTemplate getBeanById(String templateId);
}
