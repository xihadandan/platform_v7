/*
 * @(#)Jun 22, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.verification.service.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.common.verification.entity.VerifyCode;
import com.wellsoft.pt.common.verification.service.VerifyCodeService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.message.facade.service.MessageTemplateApiFacade;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jun 22, 2017.1	zhulh		Jun 22, 2017		Create
 * </pre>
 * @date Jun 22, 2017
 */
@Service
@Transactional
public class VerifyCodeServiceImpl extends BaseServiceImpl implements VerifyCodeService {

    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    @Autowired
    private MessageClientApiFacade messageClientApiFacade;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private MessageTemplateApiFacade messageTemplateService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#sendSmsVerifyCodeWithMessageTemplateId(java.lang.String, java.util.Map, java.lang.String)
     */
    @Override
    public VerifyCode sendSmsVerifyCodeWithMessageTemplateId(String templateId, Map<String, Object> data,
                                                             String mobilePhone) {
        return this.sendSmsVerifyCodeWithMessageTemplateId(templateId, data, mobilePhone, 5 * 60);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#sendSmsVerifyCodeWithMessageTemplateId(java.lang.String, java.util.Map, java.lang.String, java.lang.Integer)
     */
    @Override
    public VerifyCode sendSmsVerifyCodeWithMessageTemplateId(String templateId, Map<String, Object> data,
                                                             String mobilePhone, Integer timeout) {
        MessageTemplate messageTemplate = messageTemplateService.getBeanById(templateId);
        String sendUserId = SpringSecurityUtils.getCurrentUserId();
        if (MessageTemplate.TYPE_SYSTEM.equals(messageTemplate.getType())) {
            List<String> adminIds = orgApiFacade.queryAllAdminIdsByUnitId(SpringSecurityUtils.getCurrentUserUnitId());
            if (!adminIds.isEmpty()) {
                sendUserId = adminIds.get(0);
            }
        }
        return this.sendSmsVerifyCode(sendUserId, mobilePhone, messageTemplate.getSmsBody(), data, timeout);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#sendSmsVerifyCode(java.lang.String)
     */
    @Override
    public VerifyCode sendSmsVerifyCode(String mobilePhone) {
        return this.sendSmsVerifyCode(mobilePhone, 5 * 60);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#sendSmsVerifyCode(java.lang.String, java.lang.Integer)
     */
    @Override
    public VerifyCode sendSmsVerifyCode(String mobilePhone, Integer timeout) {
        return sendSmsVerifyCode(mobilePhone, "短信验证码: ${verifyCode}", timeout);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#sendSmsVerifyCode(java.lang.String, java.lang.String, java.lang.Integer)
     */
    @Override
    public VerifyCode sendSmsVerifyCode(String mobilePhone, String smsBody, Integer timeout) {
        return sendSmsVerifyCode(mobilePhone, smsBody, null, timeout);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#sendSmsVerifyCode(java.lang.String, java.lang.String, java.util.Map, java.lang.Integer)
     */
    @Override
    public VerifyCode sendSmsVerifyCode(String mobilePhone, String smsBody, Map<String, Object> data, Integer timeout) {
        return sendSmsVerifyCode(SpringSecurityUtils.getCurrentUserId(), mobilePhone, smsBody, data, timeout);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#sendSmsVerifyCode(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
     */
    @Override
    public VerifyCode sendSmsVerifyCode(String userId, String mobilePhone, String smsBody, Map<String, Object> data,
                                        Integer timeout) {
        // 忽略登录
        ignorgLogin();

        VerifyCode verifyCode = new VerifyCode();
        try {
// 生成6位随机短信验证码
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int index = 0; index < 6; index++) {
                sb.append(random.nextInt(10));
            }
            String smsVerifyCode = sb.toString();

            verifyCode.setType(VerifyCode.TYPE_SMS);
            verifyCode.setId(UUID.randomUUID().toString());
            verifyCode.setCode(smsVerifyCode);
            verifyCode.setMobilePhone(mobilePhone);
            verifyCode.setTimeout(timeout);
            this.dao.save(verifyCode);

            Map<String, Object> root = new HashMap<String, Object>();
            root.put("verifyCode", verifyCode.getCode());
            if (data != null) {
                root.putAll(data);
            }
            String body = smsBody;
            try {
                body = TemplateEngineFactory.getDefaultTemplateEngine().process(smsBody, root);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 发送短信
            messageClientApiFacade.sendSmsMessages(userId, mobilePhone, body, verifyCode.getId(), null, null);

        } catch (Exception e) {
            logger.error("异常：", e);
            throw new RuntimeException(e);
        } finally {
            ignorgLogout();
        }

        return verifyCode;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#verify(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Integer verify(String id, String code, String mobilePhone) {
        // 忽略登录
        ignorgLogin();
        try {
            VerifyCode example = new VerifyCode();
            example.setId(id);
            example.setCode(code);
            example.setMobilePhone(mobilePhone);
            List<VerifyCode> verifyCodes = this.dao.findByExample(example);
            if (verifyCodes.isEmpty() || verifyCodes.size() > 1) {
                return VerifyCode.VERIFY_RESULT_FAILURE;
            }

            VerifyCode verifyCode = verifyCodes.get(0);
            // 判断是否有效
            if (!StringUtils.equals(verifyCode.getId(), id) || !StringUtils.equals(verifyCode.getCode(), code)) {
                return VerifyCode.VERIFY_RESULT_FAILURE;
            }
            // 已验证，过期
            if (VerifyCode.VERIFY_RESULT_SUCCESS.equals(verifyCode.getResult())) {
                return VerifyCode.VERIFY_RESULT_EXPIRE;
            }
            // 判断是否过期
            Calendar expireCalendar = Calendar.getInstance();
            expireCalendar.setTime(verifyCode.getCreateTime());
            expireCalendar.add(Calendar.SECOND, verifyCode.getTimeout());
            Date expireDate = expireCalendar.getTime();
            if (Calendar.getInstance().getTime().after(expireDate)) {
                return VerifyCode.VERIFY_RESULT_EXPIRE;
            }

            // 保存验证成功结果
            verifyCode.setResult(VerifyCode.VERIFY_RESULT_SUCCESS);
            this.dao.save(verifyCode);
        } catch (Exception e) {
            logger.error("异常：", e);
            throw new RuntimeException(e);
        } finally {
            ignorgLogout();
        }

        return VerifyCode.VERIFY_RESULT_SUCCESS;
    }

    /**
     *
     */
    private void ignorgLogin() {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (user == null) {
            try {
                List<String> adminIds = orgApiFacade.queryAllAdminIdsByUnitId(SpringSecurityUtils
                        .getCurrentUserUnitId());
                if (!adminIds.isEmpty()) {
                    IgnoreLoginUtils.login(Config.DEFAULT_TENANT, adminIds.get(0));
                } else {
                    IgnoreLoginUtils.login(Config.DEFAULT_TENANT, Config.DEFAULT_TENANT);
                }
            } catch (Exception e) {
                logger.error("模拟登录异常：", e);
                IgnoreLoginUtils.logout();
            }
        }
    }

    /**
     *
     */
    private void ignorgLogout() {
        IgnoreLoginUtils.logout();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#createGraphicVerifyCode()
     */
    @Override
    public VerifyCode createGraphicVerifyCode() {
        // 默认5分钟
        return createGraphicVerifyCode(5 * 60);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#createGraphicVerifyCode(java.lang.Integer)
     */
    @Override
    public VerifyCode createGraphicVerifyCode(Integer timeout) {
        // 忽略登录
        ignorgLogin();
        VerifyCode verifyCode = new VerifyCode();
        try {
            // 随机生成4位验证码
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
            }
            String graphicVerifyCode = sb.toString();
            verifyCode.setType(VerifyCode.TYPE_GRAPHIC_CODE);
            verifyCode.setId(UUID.randomUUID().toString());
            verifyCode.setCode(graphicVerifyCode);
            verifyCode.setTimeout(timeout);
            this.dao.save(verifyCode);
        } catch (Exception e) {
            logger.error("异常：", e);
            throw new RuntimeException(e);
        } finally {
            ignorgLogout();
        }


        return verifyCode;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#verify(java.lang.String, java.lang.String)
     */
    @Override
    public Integer verify(String id, String code) {
        // 忽略登录
        ignorgLogin();
        try {
            VerifyCode example = new VerifyCode();
            example.setId(id);
            example.setCode(code);
            List<VerifyCode> verifyCodes = this.dao.findByExample(example);
            if (verifyCodes.isEmpty() || verifyCodes.size() > 1) {
                return VerifyCode.VERIFY_RESULT_FAILURE;
            }

            VerifyCode verifyCode = verifyCodes.get(0);
            // 判断是否有效
            if (!StringUtils.equals(verifyCode.getId(), id) || !StringUtils.equals(verifyCode.getCode(), code)) {
                return VerifyCode.VERIFY_RESULT_FAILURE;
            }
            //
            if (!VerifyCode.TYPE_GRAPHIC_CODE.equals(verifyCode.getType())) {
                // 已验证，过期
                if (VerifyCode.VERIFY_RESULT_SUCCESS.equals(verifyCode.getResult())) {
                    return VerifyCode.VERIFY_RESULT_EXPIRE;
                }

                // 判断是否过期
                Calendar expireCalendar = Calendar.getInstance();
                expireCalendar.setTime(verifyCode.getCreateTime());
                expireCalendar.add(Calendar.SECOND, verifyCode.getTimeout());
                Date expireDate = expireCalendar.getTime();
                if (Calendar.getInstance().getTime().after(expireDate)) {
                    return VerifyCode.VERIFY_RESULT_EXPIRE;
                }
            }
            // 保存验证成功结果
            verifyCode.setResult(VerifyCode.VERIFY_RESULT_SUCCESS);

            this.dao.save(verifyCode);

        } catch (Exception e) {
            logger.error("异常：", e);
            throw new RuntimeException(e);
        } finally {
            ignorgLogout();
        }
        return VerifyCode.VERIFY_RESULT_SUCCESS;

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#sendEmailVerifyCode()
     */
    @Override
    public VerifyCode createEmailVerifyCode() {
        return this.createEmailVerifyCode(5 * 60);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.common.verification.service.VerifyCodeService#sendEmailVerifyCode(java.lang.Integer)
     */
    @Override
    public VerifyCode createEmailVerifyCode(Integer timeout) {
        // 忽略登录
        ignorgLogin();
        VerifyCode verifyCode = new VerifyCode();
        try {
            // 生成6位随机短信验证码
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int index = 0; index < 6; index++) {
                sb.append(random.nextInt(10));
            }
            String smsVerifyCode = sb.toString();

            verifyCode.setType(VerifyCode.TYPE_EMAIL);
            verifyCode.setId(UUID.randomUUID().toString());
            verifyCode.setCode(smsVerifyCode);
            verifyCode.setTimeout(timeout);
            this.dao.save(verifyCode);
        } catch (Exception e) {
            logger.error("异常：", e);
            throw new RuntimeException(e);
        } finally {
            ignorgLogout();
        }

        return verifyCode;
    }

}
