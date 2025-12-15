/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailConfigDto;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;
import com.wellsoft.pt.webmail.enums.MailServerTypeEnum;
import com.wellsoft.pt.webmail.facade.service.WmMailConfigMgr;
import com.wellsoft.pt.webmail.service.WmMailConfigService;
import com.wellsoft.pt.webmail.service.WmMailUserService;
import com.wellsoft.pt.webmail.support.SyncCoremailUserCallable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tebie.applib.api.APIContext;
import tebie.applib.api.IClient;

import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Description: 邮件配置管理器实现
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-06-03.1	t		2016-06-03		Create
 * </pre>
 * @date 2016-06-03
 */
@Service
public class WmMailConfigMgrImpl extends BaseServiceImpl implements WmMailConfigMgr {

    @Autowired
    private WmMailConfigService wmMailConfigService;

    @Autowired
    private WmMailUserService wmMailUserService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmMailConfigMgr#getConfig()
     */
    @Override
    public WmMailConfigEntity getConfig() {
        return wmMailConfigService.getBySystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.WmMailConfigMgr#getBean(java.lang.String)
     */
    @Override
    public WmMailConfigDto getBean(String uuid) {
        WmMailConfigEntity entity = wmMailConfigService.getOne(uuid);
        WmMailConfigDto bean = new WmMailConfigDto();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.facade.service.WmMailConfigMgr#resetMailUser(java.lang.String)
     */
    @Override
    public void resetMailUser(String uuid) {
        WmMailConfigEntity wmMailConfig = wmMailConfigService.getOne(uuid);
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<MultiOrgUserAccount> list = orgApiFacade.queryAllAccountOfUnit(unitId);

        for (MultiOrgUserAccount user : list) {
            // 更改密码为0
            try {
                wmMailUserService.saveOrUpdateMailUser(wmMailConfig, user,
                        wmMailConfig.MAIL_USER_DEFAULT_PASSWORD);
            } catch (Exception e) {
                logger.error("重置用户{}的邮箱账号异常：", user.getLoginName(), e);
            }
        }

        //coremail 邮件用户的创建
        if (MailServerTypeEnum.CORE_MAIL.getCode().equalsIgnoreCase(
                wmMailConfig.getMailServerType()) &&
                wmMailConfig.getApiPort() != null) {
            Executors.newFixedThreadPool(1).submit(
                    new SyncCoremailUserCallable(wmMailConfig.getUuid(),
                            wmMailConfig.MAIL_USER_DEFAULT_PASSWORD));
        }

    }

    /**
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void saveBean(WmMailConfigDto bean) {
        String uuid = bean.getUuid();
        WmMailConfigEntity entity = new WmMailConfigEntity();
        if (StringUtils.isNotBlank(uuid)) {
            entity = wmMailConfigService.getOne(uuid);
        }
        BeanUtils.copyProperties(bean, entity);
        wmMailConfigService.save(entity);

        if (MailServerTypeEnum.JAMES_MAIL.getCode().equalsIgnoreCase(entity.getMailServerType())) {
            //james 邮箱服务器增加域名
            UniversalDao jamesDao = ApplicationContextHolder.getBean(UniversalDao.class);
            jamesDao.setSessionFactory(
                    ApplicationContextHolder.getBean("james", SessionFactory.class),
                    null);
            NativeDao jamesNativeDao = ApplicationContextHolder.getBean(NativeDao.class);
            jamesNativeDao.setSessionFactory(
                    ApplicationContextHolder.getBean("james", SessionFactory.class),
                    null);
            Map<String, Object> param = Maps.newHashMap();
            param.put("domainName", entity.getDomain());
            List domains = jamesNativeDao.query(
                    "select domain_name from james_domain where domain_name=:domainName", param);
            if (CollectionUtils.isEmpty(domains)) {
                //不存在则新增
                jamesNativeDao.executeSql(
                        "insert into JAMES_DOMAIN (domain_name) values ('" + entity.getDomain() + "')");

            }
        } else if (MailServerTypeEnum.CORE_MAIL.getCode().equalsIgnoreCase(
                entity.getMailServerType()) && entity.getApiPort() != null) {
            IClient client = null;
            APIContext ret = null;
            try {
                Socket socket = new Socket(entity.getPop3Server(),
                        entity.getApiPort());
                client = APIContext.getClient(socket);
                //是否存在组织
                ret = client.getOrgList();
                boolean orgNotExit = false;
                if (ret.getRetCode() == APIContext.RC_NORMAL) {
                    if (StringUtils.isBlank(ret.getResult()) || StringUtils.indexOf(ret.getResult(),
                            entity.getSystemUnitId()) == -1) {
                        orgNotExit = true;
                    }
                }

                if (orgNotExit) {
                    //添加组织
                    ret = client.addOrg(entity.getSystemUnitId(),
                            "org_name=" + SpringSecurityUtils.getCurrentUserUnitName() + "&domain_name=" + entity.getDomain() + "&cos_id=1&num_of_classes=10000&org_status=0");
                    if (ret.getRetCode() != APIContext.RC_NORMAL) {
                        logger.error("调用CoreMail客户端API添加组织失败，retCode={},error={}", ret.getRetCode(),
                                ret.getErrorInfo());
                    }
                } else {
                    //如果存在组织，则判断组织是否有包含域
                    ret = client.getOrgInfo(entity.getSystemUnitId(), "domain_name");
                    if (APIContext.RC_NORMAL == ret.getRetCode()) {
                        if (StringUtils.isNotBlank(ret.getResult())) {
                            String[] domains = ret.getResult().replace("domain_name=", "").split(
                                    ",");
                            if (ArrayUtils.indexOf(domains, entity.getDomain()) == -1) {
                                ret = client.addOrgDomain(entity.getSystemUnitId(),
                                        entity.getDomain());
                                if (ret.getRetCode() != APIContext.RC_NORMAL) {
                                    logger.error("调用CoreMail客户端API添加组织域失败，retCode={},error={}",
                                            ret.getRetCode(),
                                            ret.getErrorInfo());
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                logger.error("对接coremail客户端异常：", e);
            } finally {
                if (client != null)
                    client.close();
            }


        }

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.WmMailConfigMgr#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        wmMailConfigService.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.WmMailConfigMgr#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        wmMailConfigService.deleteByUuids(Lists.newArrayList(uuids));
    }

}
