/*
 * @(#)2021年7月20日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.event.listener;

import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.event.LogoutSuccessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月20日.1	zhulh		2021年7月20日		Create
 * </pre>
 * @date 2021年7月20日
 */
@Component
public class UserLogoutUnlockWorkListener implements ApplicationListener<LogoutSuccessEvent> {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(LogoutSuccessEvent event) {
        UserDetails userDetails = event.getUserDetails();
        if (userDetails != null) {
            taskService.removeAllUserLock(userDetails);
        }
    }

}
