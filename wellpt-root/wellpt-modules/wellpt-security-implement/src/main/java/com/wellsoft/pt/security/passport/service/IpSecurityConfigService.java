/*
 * @(#)2013-5-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.passport.bean.IpSecurityConfigBean;
import com.wellsoft.pt.security.passport.dao.IpSecurityConfigDao;
import com.wellsoft.pt.security.passport.entity.IpSecurityConfig;

import java.util.List;

/**
 * Description: IP安全配置服务接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-2.1	zhulh		2013-5-2		Create
 * </pre>
 * @date 2013-5-2
 */
public interface IpSecurityConfigService extends JpaService<IpSecurityConfig, IpSecurityConfigDao, String> {

    /**
     * 保存所有IP配置VO类
     *
     * @param beans
     */
    void saveAllBean(IpSecurityConfigBean... beans);

    /**
     * 获取所有的IP安全配置
     *
     * @return
     */
    List<IpSecurityConfigBean> getAllBean();

    /**
     * 判断指定的IP地址是否需要登录验证码，若需要返回true，否则返回false
     * 用"-"隔开表示地址段，多个IP段以";"隔开，单个*表示全部IP地址
     *
     * @param ipAddr
     * @return
     */
    boolean isRequiredLoginVerifyCode(String ipAddr);

    /**
     * 判断指定的用户ID是否允许从指定的IP地址登录系统，若允许返回true，否则返回false
     * 用"-"隔开表示地址段，多个IP段以";"隔开，单个*表示全部IP地址
     *
     * @param userId
     * @param ipAddr
     * @return
     */
    boolean isAllowLogin(String userId, String ipAddr);

    /**
     * 判断指定的用户ID是否允许从指定的域登录系统，若允许返回true，否则返回false
     * 用;号隔开
     *
     * @param userId
     * @param domainAddr
     * @return
     */
    boolean isAllowDomainLogin(String userId, String domainAddr);

    /**
     * 判断指定的用户ID是否需要短信验证码，若允许返回true，否则返回false
     * 用"-"隔开表示地址段，多个IP段以";"隔开，单个*表示全部IP地址
     *
     * @param userId
     * @param ipAddr
     * @return
     */
    boolean isRequiredSmsVerifyCode(String userId, String ipAddr);

    /**
     * 获取短信验证码的超时时间
     *
     * @return
     */
    int getSmsVerifyCodeTimeOut();

    List<IpSecurityConfig> getBySystemAndTenant(String system, String tenant);

    void saveAllIpSecurityConfig(String system, String tenant, List<IpSecurityConfig> config);
}
