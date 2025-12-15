package com.wellsoft.pt.message.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.dao.MessageClassifyDao;
import com.wellsoft.pt.message.dto.MessageClassifyDto;
import com.wellsoft.pt.message.entity.MessageClassify;
import com.wellsoft.pt.message.service.MessageClassifyService;
import com.wellsoft.pt.message.service.MessageInboxService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yt
 * @title: MessageClassifyServiceImpl
 * @date 2020/5/18 9:07 下午
 */
@Service
public class MessageClassifyServiceImpl extends AbstractJpaServiceImpl<MessageClassify, MessageClassifyDao, String> implements MessageClassifyService {

    @Autowired
    private MessageInboxService messageInboxService;

    @Override
    public MessageClassify getByNameNoEqUuid(String name, String uuid) {
        StringBuilder hql = new StringBuilder("from MessageClassify where name = :name ");
        Map<String, Object> values = Maps.newHashMap();
        values.put("name", name);
        if (StringUtils.isNotBlank(uuid)) {
            hql.append(" and uuid <> :uuid");
            values.put("uuid", uuid);
        }
        MessageClassify classify = this.dao.getOneByHQL(hql.toString(), values);
        return classify;
    }

    /**
     * 启用，禁用
     *
     * @param uuid
     * @param isEnable
     */
    @Override
    public void enableFlg(String uuid, int isEnable) {
        MessageClassify classify = this.getOne(uuid);
        if (classify != null) {
            classify.setIsEnable(isEnable);
            this.update(classify);
        }
    }

    /**
     * 保存或更新
     *
     * @param classify
     */
    @Override
    @Transactional
    public void saveOrupdateClassify(MessageClassify classify) {
        if (getByNameNoEqUuid(classify.getName(), classify.getUuid()) != null) {
            throw new RuntimeException("已经存在名称为[" + classify.getName() + "]的消息分类!");
        }
        if (StringUtils.isNotBlank(classify.getUuid())) {
            MessageClassify oldClassify = this.getOne(classify.getUuid());
            if (!oldClassify.getName().equals(classify.getName())) {
                Map<String, Object> values = Maps.newHashMap();
                values.put("classifyUuid", classify.getUuid());
                values.put("classifyName", classify.getName());
                this.updateByHQL("update MessageTemplate t set t.classifyName = :classifyName where t.classifyUuid = :classifyUuid", values);
                this.updateByHQL("update MessageInbox t set t.classifyName = :classifyName where t.classifyUuid = :classifyUuid", values);
                this.updateByHQL("update MessageInboxBak t set t.classifyName = :classifyName where t.classifyUuid = :classifyUuid", values);
            }
            BeanUtils.copyProperties(classify, oldClassify);
            this.save(oldClassify);
        } else {
            this.save(classify);
        }
    }

    /**
     * 删除
     *
     * @param uuids
     */
    @Override
    @Transactional
    public void delClassifys(List<String> uuids) {
        for (String uuid : uuids) {
            this.delete(uuid);
            Map<String, Object> values = Maps.newHashMap();
            values.put("classifyUuid", uuid);
            this.updateByHQL("update MessageTemplate t set t.classifyName = null,t.classifyUuid = null where t.classifyUuid = :classifyUuid", values);
            this.updateByHQL("update MessageInbox t set t.classifyName = null,t.classifyUuid = null where t.classifyUuid = :classifyUuid", values);
            this.updateByHQL("update MessageInboxBak t set t.classifyName = null,t.classifyUuid = null where t.classifyUuid = :classifyUuid", values);
        }
    }

    /**
     * 查询分类
     *
     * @return
     */
    @Override
    public List<MessageClassify> queryList(String name, String systemUnitId) {
        StringBuilder hql = new StringBuilder("from MessageClassify where 1=1 ");

        Map<String, Object> values = Maps.newHashMap();
        if (StringUtils.isNotBlank(systemUnitId)) {
            hql.append(" and systemUnitId = :systemUnitId");
            values.put("systemUnitId", systemUnitId);
        }
        if (StringUtils.isNotBlank(name)) {
            hql.append(" and name like :name");
            values.put("name", "%" + Strings.nullToEmpty(name) + "%");
        }
        hql.append(" order by code asc,name asc");
        List<MessageClassify> classifies = this.listByHQL(hql.toString(), values);
        return classifies;
    }

    /**
     * 前端查询分类
     *
     * @param name
     * @return
     */
    @Override
    public List<MessageClassifyDto> facadeQueryList(String name) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        StringBuilder hql = new StringBuilder("from MessageClassify where isEnable=:isEnable ");
        Map<String, Object> values = Maps.newHashMap();
        values.put("isEnable", 1);
        if (StringUtils.isNotBlank(name)) {
            hql.append(" and name like :name");
            values.put("name", "%" + Strings.nullToEmpty(name) + "%");
        }
        hql.append(" order by code asc,name asc");
        List<MessageClassify> classifies = this.listByHQL(hql.toString(), values);
        List<MessageClassifyDto> classifyDtos = new ArrayList<>();
        this.addDef(classifyDtos, name, MessageClassifyDto.ALL_CLASSIFY, MessageClassifyDto.ALL_CLASSIFY_NAME, userId);
        for (MessageClassify classify : classifies) {
            MessageClassifyDto classifyDto = new MessageClassifyDto();
            BeanUtils.copyProperties(classify, classifyDto);
            classifyDto.setUnReadCount(messageInboxService.getOnlineMessageCount(userId, false, classify.getUuid()));
            classifyDtos.add(classifyDto);
        }
        this.addDef(classifyDtos, name, MessageClassifyDto.USER_CLASSIFY, MessageClassifyDto.USER_CLASSIFY_NAME, userId);
        return classifyDtos;
    }

    /**
     * 设置弹窗查询分类
     *
     * @return
     */
    @Override
    public List<MessageClassify> facadeWinQueryList() {
        StringBuilder hql = new StringBuilder("from MessageClassify where isEnable=:isEnable ");
        Map<String, Object> values = Maps.newHashMap();
        values.put("isEnable", 1);
        hql.append(" order by code asc,name asc");
        List<MessageClassify> classifies = this.listByHQL(hql.toString(), values);
        return classifies;
    }

    private void addDef(List<MessageClassifyDto> classifyDtos, String name, String uuid, String defName, String userId) {
        if (StringUtils.isBlank(name) || defName.indexOf(name) > -1) {
            MessageClassifyDto classifyDto = new MessageClassifyDto(uuid, defName);
            classifyDto.setUnReadCount(messageInboxService.getOnlineMessageCount(userId, false, uuid));
            classifyDtos.add(classifyDto);
        }
    }
}
