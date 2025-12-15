/*
 * @(#)2013-8-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.message.client.MessageClient;
import com.wellsoft.pt.message.dto.MessageRecentContactDto;
import com.wellsoft.pt.message.service.OnlineMessageService;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author hwt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-28.1	hwt		2013-10-28		Create
 * </pre>
 * @date 2013-10-28
 */
@Service
public class OnlineMessageServiceImpl implements OnlineMessageService {
    @Autowired
    private MessageClient messageClient;

    @Override
    @Transactional
    public void send(String templateId, Message mesg, Collection<String> recipients, IdEntity entity) {
        Collection<IdEntity> entities = new ArrayList<IdEntity>();
        entities.add(mesg);
        saveRecentContactRecord(mesg, recipients);
        messageClient.send(templateId, entities, null, recipients, entity);

    }

    /**
     * @param mesg
     * @param recipients
     */
    private void saveRecentContactRecord(Message mesg, Collection<String> recipients) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        Date currentTime = new Date();
        Map<String, MessageRecentContactDto> dtoMap = Maps.newHashMap();
        int i = -1;
        for (String receiverId : recipients) {
            i++;
            if (StringUtils.isEmpty(receiverId)
                    || receiverId.indexOf(Separator.SLASH.getValue() + IdPrefix.USER.getValue()) == -1) {// 非用户的不记录
                continue;
            }
            if (receiverId.indexOf(Separator.SLASH.getValue()) != -1) {
                receiverId = receiverId.split(Separator.SLASH.getValue())[1];
            }
            MessageRecentContactDto dto = new MessageRecentContactDto(user.getUserId(), user.getSystemUnitId(), receiverId,
                    mesg.getRecipientNames().get(i), currentTime);
            dtoMap.put(receiverId, dto);
        }
    }

}
