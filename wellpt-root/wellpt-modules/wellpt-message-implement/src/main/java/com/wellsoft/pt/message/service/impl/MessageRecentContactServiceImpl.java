/*
 * @(#)2018年4月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.dao.MessageRecentContactDao;
import com.wellsoft.pt.message.dto.MessageRecentContactDto;
import com.wellsoft.pt.message.entity.MessageRecentContactEntity;
import com.wellsoft.pt.message.service.MessageRecentContactService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月26日.1	chenqiong		2018年4月26日		Create
 * </pre>
 * @date 2018年4月26日
 */
@Service
public class MessageRecentContactServiceImpl extends
        AbstractJpaServiceImpl<MessageRecentContactEntity, MessageRecentContactDao, String> implements
        MessageRecentContactService {

    @Override
    @Transactional
    public void saveRecentContact(List<MessageRecentContactDto> dtoList) {
        List<MessageRecentContactEntity> entities = Lists.newArrayList();
        Map<String, Object> params = Maps.newHashMap();
        for (MessageRecentContactDto dto : dtoList) {
            params.put("userId", dto.getUserId());
            params.put("way", dto.getContactWay());
            MessageRecentContactEntity entity = new MessageRecentContactEntity();
            // 查看之前是否有联系过
            List<MessageRecentContactEntity> exists = listByHQL(
                    "from MessageRecentContactEntity where userId=:userId and contactWay=:way", params);
            if (CollectionUtils.isNotEmpty(exists)) {
                entity = exists.get(0);
                entity.setLastContactTime(Calendar.getInstance().getTime());
            } else {
                BeanUtils.copyProperties(dto, entity);
            }
            entities.add(entity);
        }
        saveAll(entities);
    }

}
