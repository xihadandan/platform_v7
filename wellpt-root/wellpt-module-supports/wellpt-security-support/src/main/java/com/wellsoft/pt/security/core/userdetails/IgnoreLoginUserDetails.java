/*
 * @(#)2013-12-1 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails;

import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-1.1	zhulh		2013-12-1		Create
 * </pre>
 * @date 2013-12-1
 */
public class IgnoreLoginUserDetails extends DefaultUserDetails {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1796819052016228166L;

    /**
     * @param tenant
     * @param user
     * @param authorities
     */
    public IgnoreLoginUserDetails(Tenant tenant, OrgUserVo user, Collection<? extends GrantedAuthority> authorities) {
        super(tenant, user, authorities);
    }

}
