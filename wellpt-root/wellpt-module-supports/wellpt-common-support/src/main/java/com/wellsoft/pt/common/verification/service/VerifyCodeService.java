/*
 * @(#)Jun 22, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.verification.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.common.verification.entity.VerifyCode;

import java.util.Map;

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
public interface VerifyCodeService extends BaseService {
    /**
     * 如何描述该方法
     *
     * @param templateId
     * @param data
     * @param mobilePhone
     * @return
     */
    VerifyCode sendSmsVerifyCodeWithMessageTemplateId(String templateId, Map<String, Object> data, String mobilePhone);

    /**
     * 如何描述该方法
     *
     * @param templateId
     * @param data
     * @param mobilePhone
     * @return
     */
    VerifyCode sendSmsVerifyCodeWithMessageTemplateId(String templateId, Map<String, Object> data, String mobilePhone,
                                                      Integer timeout);

    /**
     * 发送短信验证吗，默认过期5分钟
     *
     * @param timeout，验证码过期时间， 以秒为单位
     * @return
     */
    VerifyCode sendSmsVerifyCode(String mobilePhone);

    /**
     * 发送短信验证吗
     *
     * @param timeout，验证码过期时间， 以秒为单位
     * @return
     */
    VerifyCode sendSmsVerifyCode(String mobilePhone, Integer timeout);

    /**
     * 发送短信验证吗
     *
     * @param mobilePhone
     * @param smsBody
     * @param timeout
     * @return
     */
    VerifyCode sendSmsVerifyCode(String mobilePhone, String smsBody, Integer timeout);

    /**
     * 发送短信验证吗
     *
     * @param mobilePhone
     * @param smsBody
     * @param data
     * @param timeout
     * @return
     */
    VerifyCode sendSmsVerifyCode(String mobilePhone, String smsBody, Map<String, Object> data, Integer timeout);

    /**
     * 发送短信验证吗
     *
     * @param userId
     * @param mobilePhone
     * @param smsBody
     * @param data
     * @param timeout
     * @return
     */
    VerifyCode sendSmsVerifyCode(String userId, String mobilePhone, String smsBody, Map<String, Object> data,
                                 Integer timeout);

    /**
     * 创建图形验证码
     *
     * @return
     */
    VerifyCode createGraphicVerifyCode();

    /**
     * 创建图形验证码
     *
     * @param timeout
     * @return
     */
    VerifyCode createGraphicVerifyCode(Integer timeout);

    /**
     * 验证验证码
     *
     * @param id   验证码ID
     * @param code 验证码
     * @return
     */
    Integer verify(String id, String code);

    /**
     * 验证验证码
     *
     * @param id          验证码ID
     * @param code        验证码
     * @param mobilePhone 手机号码
     * @return
     */
    Integer verify(String id, String code, String mobilePhone);

    /**
     * 发送邮件生成验证验证码
     *
     * @return
     */
    VerifyCode createEmailVerifyCode();

    /**
     * 发送邮件生成验证验证码
     *
     * @param timeout 验证超时（秒为单位）
     * @return
     */
    VerifyCode createEmailVerifyCode(Integer timeout);

}
