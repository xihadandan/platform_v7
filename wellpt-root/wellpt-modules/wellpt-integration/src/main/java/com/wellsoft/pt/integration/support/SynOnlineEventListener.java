/*
 * @(#)2016年11月14日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.pt.jpa.event.WellptEventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class SynOnlineEventListener extends WellptEventListener<SynOnlineEvent> {

    private long lastSendtime = System.currentTimeMillis();

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
    public void onApplicationEvent(SynOnlineEvent event) {
        Object direct = event.getSource();
        if (direct != null && direct instanceof Integer) {
            long currTime = System.currentTimeMillis();
            if (((Integer) direct) == 2 && currTime - lastSendtime > 1000 * 60 * 60 * 4) { // 外网发送短信(4小时安全期,防止链路抖动)
                lastSendtime = currTime;
                logger.info("同步链路重新上线了");
            }
        }
    }
}
