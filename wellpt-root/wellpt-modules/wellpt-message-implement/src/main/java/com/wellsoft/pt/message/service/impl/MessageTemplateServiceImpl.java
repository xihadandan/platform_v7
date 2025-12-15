/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.bean.MessageTemplateBean;
import com.wellsoft.pt.message.dao.MessageTemplateDao;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.entity.WebServiceParm;
import com.wellsoft.pt.message.facade.service.impl.MessageClientApiFacadeImpl;
import com.wellsoft.pt.message.service.MessageTemplateService;
import com.wellsoft.pt.message.service.WebServiceParmService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 消息模板服务类
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
@Service
public class MessageTemplateServiceImpl extends
        AbstractJpaServiceImpl<MessageTemplate, MessageTemplateDao, String>
        implements MessageTemplateService {

    @Autowired
    private WebServiceParmService webserviceParmService;

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired(required = false)
    private MessageClientApiFacadeImpl messageClientApiFacade;

    // @Autowired
    // private AppFunctionFacade appFunctionFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageTemplateService#getById(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public MessageTemplate getById(String id) {
        return dao.getOneByHQL("from MessageTemplate where id='" + id + "'", null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageTemplateService#getMessageTemplatesByCategory(java.lang.String)
     */
    @Override
    public List<MessageTemplate> getMessageTemplatesByCategory(String category) {
        return this.dao.listByFieldEqValue("category", category);
    }

    /**
     * 根据消息分类id获取对应的模板实体对象集合
     *
     * @param classifyUuid
     * @return
     */
    @Override
    public List<MessageTemplate> getMessageTemplatesByClassifyUuid(String classifyUuid) {
        return this.dao.listByFieldEqValue("classifyUuid", classifyUuid);
    }

    @Override
    public List<MessageTemplate> getMessageTemplatesByClassifyUuidIsNull() {
        List<MessageTemplate> list = this.listByHQL("from MessageTemplate t where t.classifyUuid is null", null);
        return list;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageTemplateService#getBeanById(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public MessageTemplateBean getBeanById(String id) {
        MessageTemplate template = getById(id);
        if (template == null) {
            return null;
        }
        MessageTemplateBean bean = new MessageTemplateBean();
        BeanUtils.copyProperties(template, bean);

        if (StringUtils.isNotBlank(template.getSendWay())) {
            List<String> sendWays = Arrays.asList(
                    StringUtils.split(template.getSendWay(), Separator.COMMA.getValue()));
            bean.setSendWays(sendWays);
        }
        // 设定webservice属性参数
        bean.setChildren(BeanUtils.convertCollection(template.getChildren(), WebServiceParm.class));
        // List<PiItem> piItems =
        // appFunctionFacade.getPiItems(template.getUuid());
        // if (!piItems.isEmpty()) {
        // bean.setPiUuid(piItems.get(0).getParentUuid());
        // }
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageTemplateService#saveBean(com.wellsoft.pt.message.bean.MessageTemplateBean)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    @Transactional
    public void saveBean(MessageTemplateBean bean) {
        MessageTemplate template = new MessageTemplate();
        // 保存新template 设置id值
        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setUuid(null);
            // ID唯一性判断
            template.setId(bean.getId());
            if (getById(bean.getId()) != null) {
                throw new RuntimeException("已经存在ID为[" + template.getId() + "]的消息格式!");
            }
        } else {
            template = this.dao.getOne(bean.getUuid());
        }
        if ("".equals(bean.getViewpointY())) {
            bean.setViewpointY("通过");
        }
        if ("".equals(bean.getViewpointN())) {
            bean.setViewpointN("不通过");
        }
        if ("".equals(bean.getViewpointNone())) {
            bean.setViewpointNone("不处理");
        }
        BeanUtils.copyProperties(bean, template);

        String sendWay = StringUtils.join(bean.getSendWays(), Separator.COMMA.getValue());
        template.setSendWay(sendWay);
        // template.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        this.dao.save(template);

        // 2、保存子结点
        if (StringUtils.isNotBlank(template.getUuid())) {
            this.webserviceParmService.deleteByMsgTemplateUuid(template.getUuid());
        }
        for (WebServiceParm child : bean.getChangedChildren()) {// 添加数据
            WebServiceParm parm = new WebServiceParm();
            BeanUtils.copyProperties(child, parm);
            parm.setMessageTemplate(template);
            this.webserviceParmService.save(parm);
        }
    }

    @Override
    public void sendTemplateMsg(String uuid, String dataJson, List<String> toUserIds) {
        messageClientApiFacade.send(getOne(uuid).getId(), dataJson, toUserIds);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageTemplateService#remove(java.lang.String)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    @Transactional
    public void remove(String uuid) {
        this.dao.delete(uuid);
    }

    @Override
    @Transactional
    public void deleteAllById(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            MessageTemplate messageTemplate = getById(ids[i]);
            dao.delete(messageTemplate);
        }

    }

    @Override
    @Transactional
    public void deleteAllByUuids(List<String> uuids) {
        dao.deleteByUuids(uuids);
    }

    /**
     * 获取所有的消息格式树
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageTemplateService#getAllMessageTemplate(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getAllMessageTemplate(String id, String category) {
        List<CdDataDictionaryItemDto> ddList = basicDataApiFacade.getDataDictionariesByType("MODULE_CATEGORY");
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<MessageTemplate> messageTemplateList = new ArrayList<MessageTemplate>();
        if (!(StringUtils.isBlank(category))) {
            messageTemplateList = dao.listByFieldEqValue("category", category);
            for (CdDataDictionaryItemDto d : ddList) {
                if (StringUtils.equals(d.getValue(), category)) {
                    TreeNode node = new TreeNode();
                    node.setId(Objects.toString(d.getUuid(), StringUtils.EMPTY));
                    node.setData(d.getUuid());
                    node.setNocheck(true);
                    node.setName(d.getLabel());
                    List<TreeNode> childTreeNodes = new ArrayList<TreeNode>();
                    for (MessageTemplate messageTemplate : messageTemplateList) {
                        if (messageTemplate.getCategory().equals(d.getLabel())) {
                            TreeNode node2 = new TreeNode();
                            node2.setId(messageTemplate.getUuid());
                            node2.setData(messageTemplate.getUuid());
                            node2.setName(messageTemplate.getName());
                            childTreeNodes.add(node2);
                            node.setChildren(childTreeNodes);
                            node.setIsParent(true);
                        }
                    }
                    treeNodes.add(node);
                }
            }
        }
        return treeNodes;
    }

    /**
     * 通过UUID获取消息模板
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageTemplateService#getByUuid(java.lang.String)
     */
    @Override
    public MessageTemplate getByUuid(String uuid) {
        // TODO Auto-generated method stub
        return dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageTemplateService#getAll()
     */
    @Override
    public List<MessageTemplate> getAll() {
        return listAll();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageTemplateService#getAll(java.lang.String, boolean)
     */
    @Override
    public List<MessageTemplate> getAll(String orderByProperty, boolean isAsc) {
        return listAllByOrderPage(null, orderByProperty + (isAsc ? " asc " : " desc"));
    }

    @Override
    public Select2QueryData loadSelectionByModule(Select2QueryInfo select2QueryInfo) {
        Map<String, Object> params = Maps.newHashMap();
        String moduleId = select2QueryInfo.getOtherParams("moduleId");
        String idProperty = select2QueryInfo.getOtherParams("idProperty", "id");
        String excludeModuleIds = select2QueryInfo.getOtherParams("excludeModuleIds");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(moduleId)) {
            params.put("moduleId", moduleId);
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(excludeModuleIds)) {
            params.put("excludeModuleIds",
                    Arrays.asList(excludeModuleIds.split(Separator.SEMICOLON.getValue())));
        }
        String systemUnitId = select2QueryInfo.getOtherParams("systemUnitId");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(systemUnitId)) {
            params.put("systemUnitId", systemUnitId);
        }
        List<MessageTemplate> formDefinitions = this.dao.listByNameHQLQuery(
                "queryAllModuleMsgTemplate",
                params);
        return new Select2QueryData(formDefinitions, idProperty, "name");
    }

}
