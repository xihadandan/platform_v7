/*
 * @(#)2016年11月14日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.pt.integration.entity.SysProperties;
import com.wellsoft.pt.integration.facade.ExchangedataApiFacade;
import com.wellsoft.pt.jpa.event.WellptEventListener;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年11月14日.1	zhongzh		2016年11月14日		Create
 * </pre>
 * @date 2016年11月14日
 */
@Service
@Transactional
public class SynErrorEventListener extends WellptEventListener<SynErrorEvent> {

    private long lastSendtime;

    @Autowired
    private ExchangedataApiFacade exchangedataApiFacade;

    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    public static void main(String[] args) {
        Object a = (int) 1;
        System.out.println(a instanceof Integer);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public synchronized void onApplicationEvent(SynErrorEvent event) {
        Object direct = event.getSource();
        if (direct != null && direct instanceof Map) {
            long currTime = System.currentTimeMillis();
            if (currTime - lastSendtime > 1000 * 60 * 60 * 12) { // 外网发送短信(12小时安全期,防止过于频繁地发送)
                lastSendtime = currTime; // 更新最后发送时间
                String body = null, rec = null, succ = null;
                Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
                // 消息主体
                body = sysPropertiess.get("syn_offline_msgbody").getProValue();
                // 消息接收人
                rec = sysPropertiess.get("syn_offline_recipients").getProValue();
                succ = messageClientApiFacade.sendSmsMessages(rec, rec, body, null, null, null);
                logger.info("同步链路离线短信发送[" + succ + "],recipients:" + rec + ",msgBody:" + body);
            }
        }
    }

}
