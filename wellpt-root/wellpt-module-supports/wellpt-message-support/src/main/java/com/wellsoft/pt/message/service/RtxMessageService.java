/*
 * @(#)2013-8-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service;

import com.wellsoft.pt.message.support.Message;

/**
 * Description: rtx消息服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-8-14.1	zhulh		2013-8-14		Create
 * </pre>
 * @date 2013-8-14
 */
public interface RtxMessageService {

    /**
     * 发送rtx消息
     *
     * @param msg
     */
    public void send(Message msg);
}
