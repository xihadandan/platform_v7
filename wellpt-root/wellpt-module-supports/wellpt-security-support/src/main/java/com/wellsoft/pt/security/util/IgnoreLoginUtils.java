/*
 * @(#)2013-1-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.util;

import com.wellsoft.context.authentication.encoding.PasswordAlgorithm;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.core.userdetails.IgnoreLoginUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.user.dto.UserDetailsVo;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

/**
 * Description: 忽略登录工具类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-6.1	zhulh		2013-1-6		Create
 * </pre>
 * @date 2013-1-6
 */
public class IgnoreLoginUtils {
    /**
     * 如何描述TENANT_ID_PREFIX
     */
    private static final String TENANT_ID_PREFIX = "T";
    private static final String USER_ID_PREFIX = "U";
    private static final String SUPERADMIN_ID = "admin";
    private static final ThreadLocal<Deque<UserDetails>> ignoreLogin = new ThreadLocal<Deque<UserDetails>>();
    private static final ThreadLocal<String> loginingTenantId = new ThreadLocal<String>();

    /**
     * 模拟登录
     * IgnoreLoginUtils的模拟登录使用，如果有登录，切记要在finally代码块处理logout
     *
     * @param tenantId
     * @param userId
     * @return void
     **/
    public static void login(String tenantId, String userId) throws Exception {
        login(tenantId, userId, userId, false);
    }

    /**
     * 登录超管账号
     * IgnoreLoginUtils的模拟登录使用，如果有登录，切记要在finally代码块处理logout
     *
     * @throws Exception
     */
    public static void loginSuperadmin() throws Exception {
        login(Config.DEFAULT_TENANT, SUPERADMIN_ID);
    }

    /**
     * 模拟登录
     * IgnoreLoginUtils的模拟登录使用，如果有登录，切记要在finally代码块处理logout
     *
     * @param tenantId
     * @param userId
     * @param userName
     * @return void
     **/
    public static void login(String tenantId, String userId, String userName) throws Exception {
        login(tenantId, userId, userName, false);
    }

    /**
     * 模拟登录
     * IgnoreLoginUtils的模拟登录使用，如果有登录，切记要在finally代码块处理logout
     *
     * @param tenantId
     * @param userId
     * @param userName
     * @param replaceUserName
     * @return void
     **/
    public static void login(String tenantId, String userId, String userName, boolean replaceUserName)
            throws Exception {
        TenantFacadeService tenantService = ApplicationContextHolder.getBean(TenantFacadeService.class);
        OrgFacadeService orgFacadeService = ApplicationContextHolder.getBean(OrgFacadeService.class);
        MultiOrgUserService userService = ApplicationContextHolder.getBean(MultiOrgUserService.class);
        // 没有租户这概念了， 大家都是采用统一的默认租户了
        Tenant tenant = tenantService.getById(Config.DEFAULT_TENANT);
        if (tenant != null) {
            loginingTenantId.set(tenant.getId());
        } else {
            throw new RuntimeException("The tenant with id [" + tenantId + "] is not exists or disabled.");
        }
        if (StringUtils.startsWith(userId, IdPrefix.SYSTEM_UNIT.getValue())) {
            List<MultiOrgUserAccount> users = userService.queryAllAdminIdsBySystemUnitId(userId);
            userId = users.isEmpty() ? userId : users.get(0).getId();
        }
        UserDetailsVo userDetailsVo = null;
        OrgUserVo user = null;
        if (!userId.startsWith(USER_ID_PREFIX)) {
            // user = userService.getUserByLoginNameIgnoreCase(userId, PasswordAlgorithm.Plaintext.getValue());
            userDetailsVo = orgFacadeService.getUserDetailsVoByLoginName(userId);
        } else {
            //  user = userService.getUserById(userId);
            userDetailsVo = orgFacadeService.getUserDetailsVoByUserId(userId);
        }
        if (userDetailsVo != null) {
            if (replaceUserName) {
                userDetailsVo.setUserName(userName);
            }
            user = new OrgUserVo();
            BeanUtils.copyProperties(userDetailsVo, user);
            user.setId(userDetailsVo.getUserId());
            user.setPassword(UUID.randomUUID().toString());
            if (userDetailsVo.getUserType() == UserAccountEntity.Type.TENANT_ADMIN.ordinal()
                    || userDetailsVo.getUserType() == UserAccountEntity.Type.PT_ADMIN.ordinal()
                    || userDetailsVo.getUserType() == UserAccountEntity.Type.SUPER_ADMIN.ordinal()) {
                user.setType(1);
            }
        } else {
            if (!userId.startsWith(USER_ID_PREFIX)) {
                user = userService.getUserByLoginNameIgnoreCase(userId, PasswordAlgorithm.Plaintext.getValue());
            } else {
                user = userService.getUserById(userId);
            }
            // 为兼容老数据，找不到用户的，全部用平台账号处理,就不在抛异常了
            if (user == null) {
                user = userService.getUserById(MultiOrgUserAccount.PT_ACCOUNT_ID);
            }
            // throw RuntimeException("模拟用户登录失败， userId [" + userId +
            // "] is not exists or disabled.");
        }

        IgnoreLoginUserDetails userDetails = new IgnoreLoginUserDetails(tenant, user,
                AuthorityUtils.createAuthorityList());
        login(userDetails);
        loginingTenantId.remove();

    }

    /**
     * 模拟登录
     * IgnoreLoginUtils的模拟登录使用，如果有登录，切记要在finally代码块处理logout
     *
     * @param userDetail
     * @return void
     **/
    public static void login(UserDetails userDetail) throws Exception {
        Deque<UserDetails> stack = ignoreLogin.get();
        if (stack == null) {
            stack = new ArrayDeque<UserDetails>();
        }
        stack.push(userDetail);
        ignoreLogin.set(stack);
    }

    public static UserDetails getUserDetails() {
        return ignoreLogin.get().peek();
    }

    public static String getLoginingTenantId() {
        return loginingTenantId.get();
    }

    public static void logout() {
        if (ignoreLogin.get() != null && !ignoreLogin.get().isEmpty()) {
            ignoreLogin.get().pop();
        }
    }

    public static boolean isIgnoreLogin() {
        return ignoreLogin.get() != null && !ignoreLogin.get().isEmpty();
    }

    public static void resetIgnoreLogin() {
        if (ignoreLogin.get() != null) {
            ignoreLogin.get().clear();
        }
    }

}
