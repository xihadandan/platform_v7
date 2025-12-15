/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.dto.PwdErrorDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.enums.PwdErrorLockEnum;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.core.userdetails.SuperAdminDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.superadmin.entity.SuperAdmin;
import com.wellsoft.pt.security.superadmin.service.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 超级管理员登录服务提供者
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
@Service
@Transactional(readOnly = true)
public class SuperAdminDetailsServiceProviderImpl extends BaseServiceImpl implements UserDetailsServiceProvider {
    @Autowired
    private SuperAdminService superAdminService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private MultiOrgUserService multiOrgUserService;
    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private MultiOrgUserAccountFacadeService multiOrgUserAccountService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider#getLoginType()
     */
    @Override
    public String getLoginType() {
        return LoginType.SUPER_ADMIN;
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public UserDetails getUserDetails(String username) {
        String loginName = username;
        if (loginName.startsWith(Config.COMMON_TENANT + ".")) {
            loginName = loginName.substring((Config.COMMON_TENANT + ".").length());
        }
        SuperAdmin user = superAdminService.getByLoginName(loginName);
        if (user == null) {
            throw new RuntimeException("用户[" + username + "]不存在");
        }
        Set<GrantedAuthority> grantedAuths = new HashSet<GrantedAuthority>();
        grantedAuths.add(new SimpleGrantedAuthority(BuildInRole.ROLE_ANONYMOUS.name()));
        grantedAuths.add(new SimpleGrantedAuthority(BuildInRole.ROLE_USER.name()));
        grantedAuths.add(new SimpleGrantedAuthority(BuildInRole.ROLE_ADMIN.name()));
        // 用户允许，账号过期，账号锁定目前都设置为true.
        // boolean enabled = true;
        // boolean accountNonExpired = true;
        // boolean credentialsNonExpired = true;
        // boolean accountNonLocked = true;

        // 取消租户的概念了，所以超级管理员也都判定成是租户t001
        Tenant tenant = new Tenant();
        String tenantAccount = Config.COMMON_TENANT;
        tenant.setAccount(tenantAccount);
        tenant.setId("T001");
//        OrgUserVo tempUser = orgApiFacade.getPTAdmin();
        String adminUserId = MultiOrgUserAccount.PT_ACCOUNT_ID;
        OrgUserVo vo = new OrgUserVo();
        vo.setId(adminUserId);
        vo.setUserName("超级管理员");
        vo.setLoginName("admin");
        vo.setPassword(user.getPassword());
        SuperAdminDetails userdetails = new SuperAdminDetails(tenant, vo, grantedAuths);
        return userdetails;
    }

    /**
     * 验证用户信息
     *
     * @param loginName
     * @param loginNameHashAlgorithmCode
     * @return void
     **/
    private void checkUserDetails(String loginName, String loginNameHashAlgorithmCode) {
        try {
            MultiOrgUserAccount user = multiOrgUserAccountService.getUserByLoginNameIgnoreCase(loginName,
                    loginNameHashAlgorithmCode);

            if (PwdErrorLockEnum.Locked.getValue().equals(user.getPwdErrorLock())) {
                PwdErrorDto dto = new PwdErrorDto();
                dto.setLocked(Boolean.TRUE);
                dto.setMessage("账号" + user.getLoginName() + "因密码输入错误，已被锁定！\n" + "\n" + "锁定期间无法使用，"
                        + DateUtils.formatDateTimeMin(user.getLastUnLockedTime()) + "将自动解锁！");
                throw new RuntimeException(dto.getMessage());
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Set<GrantedAuthority> queryAllGrantedAuthoritiesByUserId(String userId) {
        return null;
    }

}
