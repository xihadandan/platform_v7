/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails;

import com.wellsoft.context.enums.LoginType;
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
 * 2012-12-26.1	zhulh		2012-12-26		Create
 * </pre>
 * @date 2012-12-26
 */
public class SuperAdminDetails extends DefaultUserDetails {

    private static final long serialVersionUID = -8304093688475738379L;

    /**
     * @param tenant
     * @param user
     * @param authorities
     */
    public SuperAdminDetails(Tenant tenant, OrgUserVo user, Collection<? extends GrantedAuthority> authorities) {
        super(tenant, user, authorities, LoginType.SUPER_ADMIN);
    }

}
