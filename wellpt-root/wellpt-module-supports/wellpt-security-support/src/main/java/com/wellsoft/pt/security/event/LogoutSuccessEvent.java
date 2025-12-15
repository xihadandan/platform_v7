/*
 * @(#)2021年7月20日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.event;

import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.springframework.context.ApplicationEvent;

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
public class LogoutSuccessEvent extends ApplicationEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6967042836684592659L;

    private UserDetails userDetails;

    /**
     * @param source
     */
    public LogoutSuccessEvent(UserDetails userDetails) {
        super(userDetails);
        this.userDetails = userDetails;
    }

    /**
     * @return the userDetails
     */
    public UserDetails getUserDetails() {
        return userDetails;
    }

}
