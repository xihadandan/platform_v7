/*
 * @(#)2019年12月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.owner.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.dms.dp.owner.AbstractCurrentUserDataOwnerProvider;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年12月12日.1	zhulh		2019年12月12日		Create
 * </pre>
 * @date 2019年12月12日
 */
@Component
public class CurrentUserIdDataOwnerProviderImpl extends AbstractCurrentUserDataOwnerProvider {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.owner.CurrentUserDataOwnerProvider#getName()
     */
    @Override
    public String getName() {
        return "当前用户";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.owner.CurrentUserDataOwnerProvider#getType()
     */
    @Override
    public String getType() {
        return "currentUserId";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.dp.owner.CurrentUserDataOwnerProvider#getOwnerIds()
     */
    @Override
    public List<String> getOwnerIds() {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        return Lists.newArrayList(userDetails.getUserId());
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 10;
    }

}
