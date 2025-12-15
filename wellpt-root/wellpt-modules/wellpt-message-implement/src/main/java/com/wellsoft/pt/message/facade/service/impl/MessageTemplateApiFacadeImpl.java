/*
 * @(#)2013-1-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.facade.service.impl;

import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.message.bean.MessageTemplateBean;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.facade.service.MessageTemplateApiFacade;
import com.wellsoft.pt.message.service.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:消息模板对外接口
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-14.1	zhouyq		2014-1-14		Create
 * </pre>
 * @date 2014-1-14
 */
@Component
public class MessageTemplateApiFacadeImpl extends AbstractApiFacade implements
        MessageTemplateApiFacade, Select2QueryApi {
    @Autowired
    private MessageTemplateService messageTemplateService;

    /**
     * 根据uuid获取对应的消息模板
     *
     * @param uuid 模板UUID
     * @return 消息模板
     */
    public MessageTemplate getMessageTemplateByUuid(String uuid) {
        return messageTemplateService.getByUuid(uuid);
    }

    /**
     * 根据ID获取对应的消息模板
     *
     * @param id 消息模板ID
     * @return 消息模板
     */
    public MessageTemplate getById(String id) {
        return messageTemplateService.getById(id);
    }

    /**
     * 根据消息模板分类获取对应的模板实体对象集合
     *
     * @param category
     * @return
     */
    public List<MessageTemplate> getMessageTemplatesByCategory(String category) {
        return messageTemplateService.getMessageTemplatesByCategory(category);
    }

    /**
     * 获取所有的消息模板
     *
     * @return 所有的消息模板
     */
    public List<MessageTemplate> getAll() {
        return messageTemplateService.getAll();
    }

    /**
     * 获取所有的消息模板
     *
     * @param orderByProperty 排序树形
     * @param isAsc           是否正序
     * @return 所有的消息模板
     */
    public List<MessageTemplate> getAll(String orderByProperty, boolean isAsc) {
        return messageTemplateService.getAll(orderByProperty, isAsc);
    }

    /**
     * 通过ID获取消息模板VO对象
     *
     * @param id
     * @return
     */
    @Override
    public MessageTemplateBean getBeanById(String id) {
        return messageTemplateService.getBeanById(id);
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        List<MessageTemplate> messageTemplates = messageTemplateService.listAll();
        return new Select2QueryData(messageTemplates, "id", "name");
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        String[] ids = queryInfo.getIds();
        if (ids == null || ids.length == 0) {
            return new Select2QueryData();
        }
        String hql = "from MessageTemplate where id in (:ids)";
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        List<MessageTemplate> messageTemplates = messageTemplateService.listByHQL(hql, params);
        return new Select2QueryData(messageTemplates, "id", "name");
    }

}
