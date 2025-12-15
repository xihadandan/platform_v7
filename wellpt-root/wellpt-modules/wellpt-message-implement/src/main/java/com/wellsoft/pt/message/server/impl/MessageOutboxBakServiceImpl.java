/*
 * @(#)2018年4月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.server.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.dao.MessageOutboxBakDao;
import com.wellsoft.pt.message.entity.MessageOutboxBak;
import com.wellsoft.pt.message.service.MessageOutboxBakService;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月19日.1	chenqiong		2018年4月19日		Create
 * </pre>
 * @date 2018年4月19日
 */
@Service
public class MessageOutboxBakServiceImpl extends AbstractJpaServiceImpl<MessageOutboxBak, MessageOutboxBakDao, String>
        implements MessageOutboxBakService {

}
