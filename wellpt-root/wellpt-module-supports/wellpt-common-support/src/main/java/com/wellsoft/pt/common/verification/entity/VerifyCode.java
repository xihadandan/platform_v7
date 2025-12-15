/*
 * @(#)Jun 22, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.verification.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

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
@Entity
@Table(name = "cd_verify_code")
@DynamicUpdate
@DynamicInsert
public class VerifyCode extends IdEntity {
    // 验证码类型
    public static final String TYPE_SMS = "SMS";
    // 图片验证码
    public static final String TYPE_GRAPHIC_CODE = "GRAPHIC_CODE";
    // 验证码类型
    public static final String TYPE_EMAIL = "EMAIL";
    // 验证结果
    public static final Integer VERIFY_RESULT_SUCCESS = 1;
    public static final Integer VERIFY_RESULT_FAILURE = -1;
    public static final Integer VERIFY_RESULT_EXPIRE = -2;
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1396724443464749559L;
    // 验证码类型，短信SMS、随机图
    private String type;
    // 验证码
    private String id;
    // 验证码
    private String code;
    // 过期时间
    private Integer timeout;
    // 接收短信验证码的手机号
    private String mobilePhone;
    // 验证结果
    private Integer result;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the timeout
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * @param timeout 要设置的timeout
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the mobilePhone
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * @param mobilePhone 要设置的mobilePhone
     */
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    /**
     * @return the result
     */
    public Integer getResult() {
        return result;
    }

    /**
     * @param result 要设置的result
     */
    public void setResult(Integer result) {
        this.result = result;
    }

}
