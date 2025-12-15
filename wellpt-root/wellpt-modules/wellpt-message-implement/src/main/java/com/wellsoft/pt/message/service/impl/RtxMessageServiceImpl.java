/*
 * @(#)2013-8-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service.impl;

import com.wellsoft.pt.basicdata.rtx.entity.Rtx;
import com.wellsoft.pt.basicdata.rtx.service.RtxService;
import com.wellsoft.pt.message.service.RtxMessageService;
import com.wellsoft.pt.message.support.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
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
@Service
public class RtxMessageServiceImpl implements RtxMessageService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RtxService rtxService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.RtxMessageService#send(com.wellsoft.pt.message.support.Message)
     */
    @Override
    public void send(Message msg) {
        // 判断rtx系统消息发送方式是否设置在线消息
        if (rtxService.getAll().size() != 0) {
            Rtx rtx = rtxService.getAll().get(0);
            if (rtx.getIsEnable()) {
                String messageSendWay = rtx.getMessageSendWay();
                if ("ON_LINE".equals(messageSendWay)) {
                    logger.debug("在线消息使用的模板id为：" + msg.getTemplateId());
                    rtxService.sendMessage(msg);
                }
            } else {
                logger.debug("不启用RTX");
            }
        } else {
            logger.error("RTX默认数据库为空！");
        }
    }

}
