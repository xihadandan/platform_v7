/*
 * @(#)2012-12-26.1 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails.impl;

import com.wellsoft.context.enums.LoginType;
import com.wellsoft.pt.security.core.userdetails.RestfulUserDetailsServiceProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-26.1.1	zhulh		2012-12-26.1		Create
 * </pre>
 * @date 2012-12-26.1
 */
@Service
@Transactional(readOnly = true)
public class RestfulUserDetailsServiceProviderImpl extends UserDetailsServiceProviderImpl implements RestfulUserDetailsServiceProvider {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.core.userdetails.impl.UserDetailsServiceProviderImpl#getLoginType()
     */
    @Override
    public String getLoginType() {
        return LoginType.RESTful;
    }

}
