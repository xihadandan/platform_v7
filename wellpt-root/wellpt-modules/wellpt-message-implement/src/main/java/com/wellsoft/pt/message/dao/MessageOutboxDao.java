/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.message.entity.MessageOutbox;

/**
 * Description: 发件箱实体类
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2014-10-17.1	tony		2014-10-16		Create
 * </pre>
 * @date 2014-10-17
 */
public interface MessageOutboxDao extends JpaDao<MessageOutbox, String> {
}
