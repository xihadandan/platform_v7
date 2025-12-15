/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.mail.entity.JamesUser;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.bean.WmMailUserDto;
import com.wellsoft.pt.webmail.dao.WmMailUserDao;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;
import com.wellsoft.pt.webmail.entity.WmMailUserEntity;
import com.wellsoft.pt.webmail.enums.MailServerTypeEnum;
import com.wellsoft.pt.webmail.service.WmMailConfigService;
import com.wellsoft.pt.webmail.service.WmMailUseCapacityService;
import com.wellsoft.pt.webmail.service.WmMailUserService;
import com.wellsoft.pt.webmail.support.WmMailUtils;
import com.wellsoft.pt.webmail.support.WmWebmailConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tebie.applib.api.APIContext;
import tebie.applib.api.IClient;

import java.io.Serializable;
import java.net.Socket;
import java.util.*;

/**
 * Description: 邮件用户服务
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
public class WmMailUserServiceImpl extends
        AbstractJpaServiceImpl<WmMailUserEntity, WmMailUserDao, String> implements
        WmMailUserService {

    private static String GET_MAIL_ADDRESS = "select t1.user_id as user_id,t1.user_name as user_name, t1.mail_address as mail_address ," +
            "t1.is_inner_user as is_inner_user,t1.system_unit_id as system_unit_id,t1.limit_capacity as limit_capacity,t1.used_capacity as used_capacity from wm_mail_user t1  where ";

    private static String COUNT_SQL = "select count(user_name) from james_user t where t.user_name = :userName";

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private WmMailConfigService wmMailConfigService;

    @Autowired
    private WmMailUseCapacityService wmMailUseCapacityService;

    /**
     * (non-Javadoc)
     */
    @Override
    public List<WmMailUserEntity> findByExample(WmMailUserEntity example) {
        return this.dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<WmMailUserEntity> getMailAddressByOrgIds(List<String> orgIds) {
        Set<String> allUserIds = new HashSet<String>();
        for (String orgId : orgIds) {
            try {
                HashMap<String, String> users = this.orgApiFacade.getUsersByOrgIds(orgId);
                if (users != null) {
                    allUserIds.addAll(users.keySet());
                }
            } catch (Exception e) {
                allUserIds.add(orgId);
            }
        }
        return this.querByUserIds(allUserIds);
    }

    @Override
    public List<WmMailUserEntity> querByUserIds(Set<String> userIdset) {
        if (userIdset.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, Object> values = new HashMap<String, Object>();
        StringBuilder sb = new StringBuilder(GET_MAIL_ADDRESS);
        HqlUtils.appendSql("t1.user_id", values, sb, Sets.<Serializable>newHashSet(userIdset));
        return this.dao.listBySQL(sb.toString(), values);
    }

    @Override
    public WmMailUserEntity getByMailAddress(String mailAddress) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("mailAddress", mailAddress);
        return this.dao.getOneByHQL(
                "from WmMailUserEntity where mailAddress=:mailAddress", param);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.service.WmMailUserService#addMailUser(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public String addMailUser(String userId, String password) {
        MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(userId);
        WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(
                user.getSystemUnitId());
        if (configEntity == null) {
            throw new RuntimeException("邮箱服务器信息未配置，请先配置!");
        }
        return saveOrUpdateMailUser(configEntity, user, password).getMailAddress();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.service.WmMailUserService#alterMailUserPassword(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void alterMailUserPassword(String userId, String password) {
        MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(userId);
        WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(
                user.getSystemUnitId());
        if (configEntity == null) {
            throw new RuntimeException("邮箱服务器信息未配置，请先配置!");
        }
        saveOrUpdateMailUser(configEntity, user, password).getMailAddress();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.webmail.service.WmMailUserService#deleteMailUser(java.lang.String)
     */
    @Override
    @Transactional
    public void deleteMailUser(String userId) {
        WmMailUserEntity example = new WmMailUserEntity();
        example.setUserId(userId);
        List<WmMailUserEntity> wmMailUsers = this.dao.listByEntity(example);
        deleteByEntities(wmMailUsers);
    }

    @Override
    @Transactional
    public void deleteMailUserByUuids(List<String> uuid) {
        this.dao.deleteByUuids(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public WmMailUserEntity saveOrUpdateMailUser(WmMailConfigEntity wmMailConfig,
                                                 MultiOrgUserAccount user,
                                                 String password) {
        // 用户ID
        String userId = user.getId();
        // 用户登录名
        String loginName = user.getLoginName();
        // 邮件地址
        String email = loginName.toLowerCase() + "@" + wmMailConfig.getDomain();

        // 1、获取内部邮箱用户
        WmMailUserEntity mailUser = new WmMailUserEntity();
        WmMailUserEntity example = new WmMailUserEntity();
        example.setUserId(userId);
        example.setIsInnerUser(true);
        example.setSystemUnitId(wmMailConfig.getSystemUnitId());
        /**
         * 修改人：陈琼
         * 修改时间：2018/2/6 08:46
         * 修改内容：去掉查询邮件账号的条件参数设置setMailUserName，setMailAddress，
         * 		    如果存在初始域名端口的时候，重置成其他域名端口，会出现同一个两个不同邮件账号的异常数据。bug：#14358
         */
        List<WmMailUserEntity> wmMailUsers = findByExample(example);
        if (wmMailUsers.size() > 1) {
            logger.warn("数据错误，登录名为[" + user.getLoginName() + "]的用户存在多个内部邮箱账号！进行系统删除！");
            Iterator<WmMailUserEntity> wmMailUserIterator = wmMailUsers.iterator();
            int loopCnt = 1;
            while (wmMailUserIterator.hasNext()) {
                if (loopCnt++ > 1) {
                    WmMailUserEntity deleteMail = wmMailUserIterator.next();
                    this.dao.delete(deleteMail);
                    wmMailUserIterator.remove();
                }
            }
        } else if (wmMailUsers.size() == 1) {
            mailUser = wmMailUsers.get(0);
        }

        // 2、更新邮箱用户
        // 用户ID
        mailUser.setUserId(userId);
        // 用户名称
        mailUser.setUserName(user.getUserName());
        // 邮箱用户名
        mailUser.setMailUserName(user.getUserName());
        // 邮箱地址
        mailUser.setMailAddress(email);
        // 邮箱密码
        mailUser.setMailPassword(WmMailUtils.encode(password));
        // 邮箱密码加密算法
        mailUser.setMailPasswordHashAlgorithm("mail");
        // 回复邮箱地址，默认为空
        mailUser.setReplyMailAddress(null);
        // POP3服务器
        mailUser.setPop3Server(wmMailConfig.getPop3Server());
        // POP3服务器端口110
        mailUser.setPop3Port(wmMailConfig.getPop3Port());
        // 发送服务器
        mailUser.setSmtpServer(wmMailConfig.getSmtpServer());
        // 发送服务器端口25
        mailUser.setSmtpPort(wmMailConfig.getSmtpPort());
        // IMAP服务器
        mailUser.setImapServer(wmMailConfig.getImapServer());
        // IMAP服务器端口143
        mailUser.setImapPort(wmMailConfig.getImapPort());
        mailUser.setLimitCapacity(wmMailConfig.getDefaultCapacity());//容量限制
        // 默认的邮件发送地址，用于有多个邮箱账号的默认邮箱
        mailUser.setIsDefault(true);
        mailUser.setIsInnerUser(true);
        mailUser.setUsedCapacity(mailUser.getUsedCapacity() == null ? 0 : mailUser.getUsedCapacity());
        this.save(mailUser);
        this.flushSession();
        // james邮件数据库可控制的情况下
        if (MailServerTypeEnum.JAMES_MAIL.getCode().equalsIgnoreCase(
                wmMailConfig.getMailServerType())) {

            // 3、同步JAMES服务器用户
            UniversalDao jamesDao = ApplicationContextHolder.getBean(UniversalDao.class);
            jamesDao.setSessionFactory(
                    ApplicationContextHolder.getBean("james", SessionFactory.class),
                    null);
            NativeDao nativeDao = ApplicationContextHolder.getBean(NativeDao.class);
            nativeDao.setSessionFactory(
                    ApplicationContextHolder.getBean("james", SessionFactory.class),
                    null);

            String jamesUserName = mailUser.getMailAddress();
            JamesUser existJamesUser = jamesDao.get(JamesUser.class, jamesUserName);

            // JAMES中不存在账号，添加
            if (existJamesUser == null) {
                jamesDao.save(new JamesUser(jamesUserName, password, "MD5"));
            } else {
                logger.error("邮件账号[" + jamesUserName + "]已经存在，不能添加，更新JAMES邮箱密码");

                // 更新JAMES邮箱密码
                HashMap values = new HashMap<String, Object>();
                values.put("username", jamesUserName);
                values.put("newPassword", JamesUser.hashPassword(jamesUserName, password, "MD5"));
                String hql = "update JamesUser t set t.password = :newPassword where t.userName = :username";
                jamesDao.batchExecute(hql, values);
            }
        }
        //初始化使用空间
        wmMailUseCapacityService.saveUserUseCapacityInitial(userId);
        return mailUser;
    }


    @Override
    @Transactional
    public WmMailUserEntity saveMailUser(WmMailUserDto userDto) {
        WmMailUserEntity userEntity = new WmMailUserEntity();
        WmMailConfigEntity configEntity = wmMailConfigService.getBySystemUnitId(SpringSecurityUtils
                .getCurrentUserUnitId());
        if (configEntity == null || configEntity.getSmtpPort() == null
                || StringUtils.isBlank(
                configEntity.getSmtpServer()) || configEntity.getPop3Port() == null
                || StringUtils.isBlank(configEntity.getPop3Server())) {
            throw new BusinessException("[webmail邮件设置]不正确的配置信息或者未配置");
        }
        if (StringUtils.isNotBlank(userDto.getUuid())) {
            userEntity = this.getOne(userDto.getUuid());
            BeanUtils.copyProperties(userDto, userEntity,
                    ArrayUtils.addAll(IdEntity.BASE_FIELDS, "systemUnitId",
                            "userId", "mailPasswordHashAlgorithm", "isInnerUser"));
            userEntity.setMailPassword(WmMailUtils.encode(userDto.getMailPassword()));
            userEntity.setUserName(userDto.getMailUserName());
        } else {
            BeanUtils.copyProperties(userDto, userEntity);
            userEntity.setUserName(userDto.getMailUserName());
            userEntity.setMailPassword(WmMailUtils.encode(userDto.getMailPassword()));
            userEntity.setIsInnerUser(false);
            userEntity.setMailPasswordHashAlgorithm("mail");
            userEntity.setUserId(SpringSecurityUtils.getCurrentUserId());

            //账号使用空间情况
            wmMailUseCapacityService.addUseCapacity(userEntity.getUserId(),
                    userEntity.getMailAddress(), 0L);
        }
        // smtp配置使用内部邮件发送配置
        userEntity.setSmtpPort(configEntity.getSmtpPort());
        userEntity.setSmtpServer(configEntity.getSmtpServer());
        userEntity.setImapPort(configEntity.getImapPort());
        userEntity.setImapServer(configEntity.getImapServer());
        userEntity.setIsDefault(false);
        userEntity.setIsSmtpSsl(false);
        save(userEntity);
        try {
            WmMailUtils.getPop3Store(userEntity);
        } catch (Exception e) {
            logger.error("新增其他邮箱账号，尝试连接POP异常：", e);
            throw new BusinessException("对方邮件服务器拒绝、或用户名密码不正确，请重新验证", e);
        }
        return userEntity;
    }

    @Override
    public WmMailUserEntity getOuterMailUser(String mailAddress, String currentUserId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("address", mailAddress);
        param.put("userId", currentUserId);
        return this.dao.getOneByHQL(
                "from WmMailUserEntity where mailAddress=:address and isInnerUser=false and userId=:userId",
                param);
    }

    @Override
    public WmMailUserEntity getInnerMailUser(String userId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userId", userId);
        return this.dao.getOneByHQL(
                "from WmMailUserEntity where isInnerUser=true and userId=:userId", param);
    }

    @Override
    public List<WmMailUserEntity> listOuterMailUser(String currentUserId) {
        if (StringUtils.isBlank(currentUserId)) {
            return this.dao.listByHQL("from WmMailUserEntity where  isInnerUser=false ", null);
        }
        Map<String, Object> param = Maps.newHashMap();
        param.put("userId", currentUserId);
        return this.dao.listByHQL(
                "from WmMailUserEntity where  isInnerUser=false and userId=:userId", param);
    }

    @Override
    public boolean isInnerMailUserAddress(String mailAddress) {
        String[] mailParts = mailAddress.split(WmWebmailConstants.MAIL_SEPARATOR);
        if (mailParts.length == 2) {
            return CollectionUtils.isNotEmpty(wmMailConfigService.listByDomain(mailParts[1]));
        }
        return false;
    }

    @Override
    public int updateMailUserAccountUseCapacity(String userId, Long increasement,
                                                boolean limit) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("increasement", increasement);
        param.put("userId", userId);
        if (limit) {
            param.put("limit", true);
        }
        return this.dao.updateByNamedSQL("updateMailUserAccountUseCapacity", param);
    }

    @Override
    public List<WmMailUserEntity> getMailUser(String userId, String fromMailAddress) {
        WmMailUserEntity wmMailUser = new WmMailUserEntity();
        wmMailUser.setUserId(userId);
        wmMailUser.setMailAddress(fromMailAddress);
        List<WmMailUserEntity> wmMailUsers = this.findByExample(wmMailUser);
        if (!wmMailUsers.isEmpty()) {
            return wmMailUsers;
        }

        wmMailUser = new WmMailUserEntity();
        wmMailUser.setUserId(userId);
        wmMailUsers = this.findByExample(wmMailUser);
        return wmMailUsers;
    }

    @Override
    public List<WmMailUserEntity> listInnerMailUsers() {
        return this.dao.listByFieldEqValue("isInnerUser", true);
    }

    @Override
    @Transactional
    public void updateMailUserMid(String userId, String mid) {
        WmMailUserEntity userEntity = getInnerMailUser(userId);
        if (userEntity != null) {
            userEntity.setSyncMessageNumber(Integer.parseInt(mid));
            save(userEntity);
        }
    }

    @Override
    @Transactional
    public int updateMailUserPassword(String userId, String password) {
        WmMailUserEntity userEntity = getInnerMailUser(userId);
        if (userEntity != null) {
            userEntity.setMailPassword(WmMailUtils.encode(password));
            save(userEntity);

            //更新coremail的
            WmMailConfigEntity wmMailConfig = wmMailConfigService.getBySystemUnitId(
                    userEntity.getSystemUnitId());
            if (MailServerTypeEnum.CORE_MAIL.getCode().equalsIgnoreCase(
                    wmMailConfig.getMailServerType())) {
                IClient client = null;
                APIContext ret = null;
                try {
                    Socket socket = new Socket(wmMailConfig.getPop3Server(),
                            wmMailConfig.getApiPort());
                    client = APIContext.getClient(socket);
                    ret = client.changeAttrs(userEntity.getMailAddress(), "password=" + password);
                    if (APIContext.RC_NORMAL != ret.getRetCode()) {
                        logger.error("调用coremail客户端修改用户密码失败：code={} , msg={} ", ret.getRetCode(),
                                ret.getResult());
                        throw new RuntimeException("调用coremail客户端修改用户密码失败");
                    }
                } catch (Exception e) {
                    logger.error("对接coremail客户端，创建邮件用户异常：", e);
                } finally {
                    if (client != null)
                        client.close();
                }
            } else if (MailServerTypeEnum.JAMES_MAIL.getCode().equalsIgnoreCase(
                    wmMailConfig.getMailServerType())) {
                UniversalDao jamesDao = ApplicationContextHolder.getBean(UniversalDao.class);
                jamesDao.setSessionFactory(
                        ApplicationContextHolder.getBean("james", SessionFactory.class),
                        null);
                // 更新JAMES邮箱密码
                Map<String, Object> values = Maps.newHashMap();
                values = new HashMap<String, Object>();
                values.put("username", userEntity.getMailAddress());
                values.put("newPassword",
                        JamesUser.hashPassword(userEntity.getMailAddress(), password, "MD5"));
                String hql = "update JamesUser t set t.password = :newPassword where t.userName = :username";
                jamesDao.batchExecute(hql, values);
            }

        }
        return 1;
    }

}
