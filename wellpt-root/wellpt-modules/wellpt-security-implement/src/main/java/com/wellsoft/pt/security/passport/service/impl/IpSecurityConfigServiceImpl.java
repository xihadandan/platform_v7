/*
 * @(#)2013-5-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.passport.bean.IpSecurityConfigBean;
import com.wellsoft.pt.security.passport.dao.IpSecurityConfigDao;
import com.wellsoft.pt.security.passport.entity.IpSecurityConfig;
import com.wellsoft.pt.security.passport.service.IpSecurityConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: IP安全配置服务实现类
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
@Service
public class IpSecurityConfigServiceImpl extends AbstractJpaServiceImpl<IpSecurityConfig, IpSecurityConfigDao, String>
        implements IpSecurityConfigService {

    private static final String IP4_REGULAR = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";

    private static Pattern pattern = Pattern.compile(IP4_REGULAR);

    @Autowired
    private OrgApiFacade orgApiFacade;

    /**
     * 判断指定的IP地址是否为IP4
     *
     * @param ipAddr
     * @return
     */
    private static boolean isIp4Address(String ipAddr) {
        Matcher matcher = pattern.matcher(ipAddr);
        return matcher.matches();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.passport.service.IpSecurityConfigService#saveAllBean(java.util.Collection)
     */
    @Override
    @Transactional
    public void saveAllBean(IpSecurityConfigBean... beans) {
        for (IpSecurityConfigBean bean : beans) {
            String uuid = bean.getUuid();
            // 行编辑状态
            if (IpSecurityConfigBean.ROW_STATUS_EDITED.equals(bean.getRowStatus())) {
                IpSecurityConfig ipSecurityConfig = new IpSecurityConfig();
                if (StringUtils.isBlank(bean.getUuid())) {
                    // 新增
                    BeanUtils.copyProperties(bean, ipSecurityConfig);
                    ipSecurityConfig.setSid(bean.getUserIds());
                    ipSecurityConfig.setSidName(bean.getUsernames());
                    this.dao.save(ipSecurityConfig);
                } else {
                    // 编辑
                    ipSecurityConfig = this.dao.getOne(uuid);
                    BeanUtils.copyProperties(bean, ipSecurityConfig);
                    ipSecurityConfig.setSid(bean.getUserIds());
                    ipSecurityConfig.setSidName(bean.getUsernames());
                    this.dao.save(ipSecurityConfig);
                }
            } else {
                // 行删除状态
                if (StringUtils.isBlank(bean.getUuid())) {
                    // UUID为空忽略
                } else {
                    // 删除
                    this.dao.delete(uuid);
                }
            }
        }
    }

    /**
     * 获取所有的IP安全配置
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.passport.service.IpSecurityConfigService#getAll()
     */
    @Override
    public List<IpSecurityConfigBean> getAllBean() {
        List<IpSecurityConfigBean> ipSecurityConfigBeans = new ArrayList<IpSecurityConfigBean>();
        List<IpSecurityConfig> ipSecurityConfigs = listAll();
        for (IpSecurityConfig ipSecurityConfig : ipSecurityConfigs) {
            IpSecurityConfigBean ipSecurityConfigBean = new IpSecurityConfigBean();
            BeanUtils.copyProperties(ipSecurityConfig, ipSecurityConfigBean);
            ipSecurityConfigBean.setUsernames(ipSecurityConfig.getSidName());
            ipSecurityConfigBean.setUserIds(ipSecurityConfig.getSid());
            ipSecurityConfigBeans.add(ipSecurityConfigBean);
        }
        return ipSecurityConfigBeans;
    }

    /**
     * 判断指定的IP地址是否需要登录验证码，若需要返回true，否则返回false
     * 用"-"隔开表示地址段，多个IP段以";"隔开，单个*表示全部IP地址
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.passport.service.IpSecurityConfigService#isRequiredLoginVerifyCode(java.lang.String)
     */
    @Override
    public boolean isRequiredLoginVerifyCode(String ipAddr) {
        IpSecurityConfig example = new IpSecurityConfig();
        example.setApplyTo(IpSecurityConfig.APPLY_TO_LOGIN_VERIFY_CODE);
        List<IpSecurityConfig> ipSecurityConfigs = this.dao.listByEntity(example);
        // 如果没有配置则不需要验证
        if (ipSecurityConfigs.isEmpty()) {
            return false;
        }

        for (IpSecurityConfig ipSecurityConfig : ipSecurityConfigs) {
            // 需要验证的IP地址
            String ipAddr1 = ipSecurityConfig.getIpAddress1();
            if (StringUtils.isNotBlank(ipAddr1)) {
                String[] ipAddrs1 = ipAddr1.split(Separator.SEMICOLON.getValue());
                for (String string : ipAddrs1) {
                    if (isMatch(ipAddr, string)) {
                        return true;
                    }
                }
            }

            // 不需要验证的IP地址
            String ipAddr2 = ipSecurityConfig.getIpAddress2();
            if (StringUtils.isNotBlank(ipAddr2)) {
                String[] ipAddrs2 = ipAddr2.split(Separator.SEMICOLON.getValue());
                for (String string : ipAddrs2) {
                    if (isMatch(ipAddr, string)) {
                        return false;
                    }
                }
            }
        }

        // 无法区配到指定的IP返回false，不需要验证
        return false;
    }

    /**
     * 判断指定的用户ID是否允许从指定的IP地址登录系统，若允许返回true，否则返回false
     * 用"-"隔开表示地址段，多个IP段以";"隔开，单个*表示全部IP地址
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.passport.service.IpSecurityConfigService#isAllowLogin(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAllowLogin(String userId, String ipAddr) {
        IpSecurityConfig example = new IpSecurityConfig();
        example.setApplyTo(IpSecurityConfig.APPLY_TO_LOGIN_LIMIT);
        List<IpSecurityConfig> ipSecurityConfigs = this.dao.listByEntity(example);
        // 如果没有配置则允许登录
        if (ipSecurityConfigs.isEmpty()) {
            return true;
        }

        for (IpSecurityConfig ipSecurityConfig : ipSecurityConfigs) {
            String sid = ipSecurityConfig.getSid();
            if (StringUtils.isBlank(sid)) {
                continue;
            }

            if (!ApplicationContextHolder.getBean(OrgFacadeService.class).isMemberOf(userId, sid.split(";"))) {
                continue;
            }
            // 允许登录的IP地址
            String ipAddr1 = ipSecurityConfig.getIpAddress1();
            if (StringUtils.isNotBlank(ipAddr1)) {
                String[] ipAddrs1 = ipAddr1.split(Separator.SEMICOLON.getValue());
                for (String string : ipAddrs1) {
                    if (isMatch(ipAddr, string)) {
                        return true;
                    }
                }
            }

            // 禁止登录的IP地址
            String ipAddr2 = ipSecurityConfig.getIpAddress2();
            if (StringUtils.isNotBlank(ipAddr2)) {
                String[] ipAddrs2 = ipAddr2.split(Separator.SEMICOLON.getValue());
                for (String string : ipAddrs2) {
                    if (isMatch(ipAddr, string)) {
                        return false;
                    }
                }
            }
        }

        // 无法区配到指定的IP返回true，允许登录
        return true;
    }

    /**
     * 判断指定的用户ID是否需要短信验证码，若允许返回true，否则返回false
     * 用"-"隔开表示地址段，多个IP段以";"隔开，单个*表示全部IP地址
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.passport.service.IpSecurityConfigService#isRequiredSmsVerifyCode(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isRequiredSmsVerifyCode(String userId, String ipAddr) {
        IpSecurityConfig example = new IpSecurityConfig();
        example.setApplyTo(IpSecurityConfig.APPLY_TO_SMS_VERIFY_CODE);
        List<IpSecurityConfig> ipSecurityConfigs = this.dao.listByEntity(example);
        // 如果没有配置则不需要短信验证码
        if (ipSecurityConfigs.isEmpty()) {
            return false;
        }

        for (IpSecurityConfig ipSecurityConfig : ipSecurityConfigs) {
            String sid = ipSecurityConfig.getSid();
            if (StringUtils.isBlank(sid)) {
                continue;
            }
            if (!orgApiFacade.isMemberOf(userId, sid)) {
                continue;
            }

            // 需要二次验证的的IP地址
            String ipAddr1 = ipSecurityConfig.getIpAddress1();
            if (StringUtils.isNotBlank(ipAddr1)) {
                String[] ipAddrs1 = ipAddr1.split(Separator.SEMICOLON.getValue());
                for (String string : ipAddrs1) {
                    if (isMatch(ipAddr, string)) {
                        return true;
                    }
                }
            }

            // 不需要二次验证的的IP地址
            String ipAddr2 = ipSecurityConfig.getIpAddress2();
            if (StringUtils.isNotBlank(ipAddr2)) {
                String[] ipAddrs2 = ipAddr2.split(Separator.SEMICOLON.getValue());
                for (String string : ipAddrs2) {
                    if (isMatch(ipAddr, string)) {
                        return false;
                    }
                }
            }
        }

        // 无法区配到指定的IP返回false，不需要短信验证码
        return false;
    }

    /**
     * 判断指定的IP地址是否符合指定的IP规则
     *
     * @param ipAddr        要判断的IP地址
     * @param ipAddrRegular IP规则
     * @return
     */
    private boolean isMatch(String ipAddr, String ipAddrRegular) {
        // 如果匹配规则为"*"，则直接返回true
        if (Separator.ASTERISK.getValue().equals(ipAddrRegular)) {
            return true;
        }

        String[] ipAddrs = null;
        String[] ipAddrRegulars = null;

        // 判断指定的IP地址是否为IP4
        if (isIp4Address(ipAddr)) {
            ipAddrs = ipAddr.split("\\.");
            ipAddrRegulars = ipAddrRegular.split("\\.");
        } else {
            // IP6以":"号分隔
            ipAddrs = ipAddr.split(Separator.COLON.getValue());
            ipAddrRegulars = ipAddrRegular.split(Separator.COLON.getValue());
        }

        if (ipAddrs.length != ipAddrRegulars.length) {
            return false;
        }

        for (int index = 0; index < ipAddrs.length; index++) {
            String regular = ipAddrRegulars[index];
            String ipBlock = ipAddrs[index];

            // * 匹配全部
            if (regular.equals(Separator.ASTERISK.getValue())) {
                continue;
            }

            // - 代表IP段
            if (regular.indexOf("-") != -1) {
                String[] digits = regular.split("-");
                if (digits.length != 2) {
                    return false;
                }
                if (!(Integer.valueOf(ipBlock) >= Integer.valueOf(digits[0]) && Integer.valueOf(ipBlock) <= Integer
                        .valueOf(digits[1]))) {
                    return false;
                }
                continue;
            }

            // 相等比较
            if (!regular.equals(ipBlock)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取短信验证码的超时时间
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.passport.service.IpSecurityConfigService#getSmsVerifyCodeTimeOut()
     */
    @Override
    public int getSmsVerifyCodeTimeOut() {
        IpSecurityConfig example = new IpSecurityConfig();
        example.setApplyTo(IpSecurityConfig.APPLY_TO_SMS_VERIFY_CODE);
        List<IpSecurityConfig> ipSecurityConfigs = this.dao.listByEntity(example);
        // 如果没有配置返回0，表示永不超时
        if (ipSecurityConfigs.isEmpty()) {
            return 0;
        }

        return ipSecurityConfigs.get(0).getValidPeriod() == null ? 0 : ipSecurityConfigs.get(0).getValidPeriod();
    }

    @Override
    public List<IpSecurityConfig> getBySystemAndTenant(String system, String tenant) {
        Map<String, Object> param = Maps.newHashMap();
        StringBuilder hql = new StringBuilder("from IpSecurityConfig where 1=1 ");
        if (StringUtils.isNotBlank(tenant)) {
            param.put("tenant", tenant);
            hql.append(" and tenant =:tenant ");
        }
        if (StringUtils.isNotBlank(system)) {
            param.put("system", system);
            hql.append(" and system =:system ");
        } else {
            hql.append(" and system is null ");
        }
        List<IpSecurityConfig> list = dao.listByHQL(hql.toString(), param);
        return list;
    }

    @Override
    @Transactional
    public void saveAllIpSecurityConfig(String system, String tenant, List<IpSecurityConfig> config) {
        Map<String, Object> param = Maps.newHashMap();
        StringBuilder hql = new StringBuilder("delete from IpSecurityConfig where 1=1 ");
        if (StringUtils.isNotBlank(tenant)) {
            param.put("tenant", tenant);
            hql.append(" and tenant =:tenant ");
        }
        if (StringUtils.isNotBlank(system)) {
            param.put("system", system);
            hql.append(" and system =:system ");
        } else {
            hql.append(" and system is null ");
        }
        dao.deleteByHQL(hql.toString(), param);
        dao.saveAll(config);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.passport.service.IpSecurityConfigService#isAllowDomainLogin(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isAllowDomainLogin(String userId, String domainAddr) {
        IpSecurityConfig example = new IpSecurityConfig();
        example.setApplyTo(IpSecurityConfig.APPLY_TO_DOMAIN_LOGIN_LIMIT);
        List<IpSecurityConfig> ipSecurityConfigs = this.dao.listByEntity(example);
        // 如果没有配置则允许登录
        if (ipSecurityConfigs.isEmpty()) {
            return true;
        }


        for (IpSecurityConfig ipSecurityConfig : ipSecurityConfigs) {
            String sid = ipSecurityConfig.getSid();
            if (StringUtils.isBlank(sid)) {
                continue;
            }
            if (!orgApiFacade.isMemberOf(userId, sid.split(";"), null)) {
                continue;
            }

            // 允许登录的域地址
            String domainAddr1 = ipSecurityConfig.getDomainAddress1();
            if (StringUtils.isNotBlank(domainAddr1)) {
                String[] domainAddrs1 = domainAddr1.split(Separator.SEMICOLON.getValue());
                for (String string : domainAddrs1) {
                    if (isDomainMatch(domainAddr, string)) {
                        return true;
                    }
                }
            }

            // 禁止登录的域地址
            String domainAddr2 = ipSecurityConfig.getDomainAddress2();
            if (StringUtils.isNotBlank(domainAddr2)) {
                String[] domainAddrs2 = domainAddr2.split(Separator.SEMICOLON.getValue());
                for (String string : domainAddrs2) {
                    if (isDomainMatch(domainAddr, string)) {
                        return false;
                    }
                }
            }
        }

        // 无法区配到指定的域返回true，允许登录
        return true;
    }

    /**
     * 域名匹配(全匹配)
     * 如何描述该方法
     *
     * @param srcdomainAddr
     * @param domainAddrRegular
     * @return
     */
    private boolean isDomainMatch(String srcdomainAddr, String domainAddrRegular) {
        if (domainAddrRegular.equals(srcdomainAddr)) {
            return true;
        }
        return false;
    }

}
