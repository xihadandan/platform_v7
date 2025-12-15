/*
 * @(#)2014-7-31 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service.impl;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.message.provider.AbstractMessageEventSourceProvider;
import com.wellsoft.pt.message.provider.MessageEventSourceProvider;
import com.wellsoft.pt.message.service.MessageEventService;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.message.support.MessageEventConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 消息的服务类的实现
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-22.1	tony		2014-10-22		Create
 * </pre>
 * @date 2014-10-22
 */
@Service
public class MessageEventServiceImpl implements MessageEventService, Select2QueryApi {
    @Autowired(required = false)
    private Map<String, AbstractMessageEventSourceProvider> messageEventProviderMap;

    @Override
    public List getEventSourceList(String s, String id) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeNode treeNode;
        for (String key : messageEventProviderMap.keySet()) {
            MessageEventSourceProvider source = messageEventProviderMap.get(key);
            treeNode = new TreeNode();
            treeNode.setName(source.getModuleName());
            treeNode.setId(key);
            treeNodes.add(treeNode);

        }
        return treeNodes;
    }

    @Override
    public List getEventClientSourceList(String s, String id) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeNode treeNode;
        for (String key : messageEventProviderMap.keySet()) {
            MessageEventSourceProvider source = messageEventProviderMap.get(key);
            if (MessageEventConstant.EVENT_CLIENT.equals(source.getMessageType())) {
                treeNode = new TreeNode();
                treeNode.setName(source.getModuleName());
                treeNode.setId(key);
                treeNodes.add(treeNode);
            }

        }
        return treeNodes;
    }

    @Override
    public List getEventServerSourceList(String s, String id) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeNode treeNode;
        for (String key : messageEventProviderMap.keySet()) {
            MessageEventSourceProvider source = messageEventProviderMap.get(key);
            if (MessageEventConstant.EVENT_SERVER.equals(source.getMessageType())) {
                treeNode = new TreeNode();
                treeNode.setName(source.getModuleName());
                treeNode.setId(key);
                treeNodes.add(treeNode);
            }

        }
        return treeNodes;
    }

    @Override
    public void exeServerEventInstanceWithNone(String id) {
        AbstractMessageEventSourceProvider dp = messageEventProviderMap.get(id);
        if (dp != null)
            dp.executeServerMessageEvent(null, "", "");
    }

    /**
     * 立场选择完触发事件
     *
     * @param id
     * @param message
     * @param viewpoint
     * @param note
     */
    @Override
    public void exeServerEventInstance(String message, String viewpoint, String note) {
        System.out.println(message);
        Message message_o = JsonUtils.json2Object(message, Message.class);
        AbstractMessageEventSourceProvider dp = null;
        if (message_o.getBackgroundEvent() != null) {
            dp = messageEventProviderMap.get(message_o.getBackgroundEvent());
        }
        System.out.println("the backgroungEvent id is" + message_o.getBackgroundEvent());
        if (dp != null)
            dp.executeServerMessageEvent(message_o, viewpoint, note);
    }

    /**
     * 发送消息时触发事件
     */
    @Override
    public <ENTITY extends IdEntity> void exeClientEventInstance(String id, String templateId,
                                                                 Collection<ENTITY> entities, Map<Object, Object> dataMap, Map<String, Object> extraData,
                                                                 Collection<String> recipients, ENTITY entity) {
        AbstractMessageEventSourceProvider dp = null;
        if (!"".equals(id) && id != null) {
            dp = messageEventProviderMap.get(id);
        }
        if (dp != null)
            dp.executeClientMessageEvent(templateId, entities, dataMap, extraData, recipients, entity);
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        Select2QueryData data = new Select2QueryData();
        if (messageEventProviderMap != null) {
            for (String key : messageEventProviderMap.keySet()) {
                MessageEventSourceProvider source = messageEventProviderMap.get(key);
                if (MessageEventConstant.EVENT_SERVER.equals(source.getMessageType())) {
                    Select2DataBean bean = new Select2DataBean(key, source.getModuleName());
                    data.addResultData(bean);
                }
            }
        }
        return data;
    }

    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        return null;
    }

}
