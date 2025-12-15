/*
 * @(#)2018年3月29日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.util;

import com.wellsoft.context.util.security.SpringSecurityUtilsFacade;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Description: 如何描述该类
 *
 * @author {Svn璐﹀彿}
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月29日.1	{Svn璐﹀彿}		2018年3月29日		Create
 * </pre>
 * @date 2018年3月29日
 */
@Service
public class SpringSecurityUtilsFacadeImpl implements SpringSecurityUtilsFacade {

    @Override
    public UserDetails getCurrentUser() {
        return SpringSecurityUtils.getCurrentUser();
    }
}
