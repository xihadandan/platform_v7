/*
 * @(#)Sep 7, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Sep 7, 2017.1	zhulh		Sep 7, 2017		Create
 * </pre>
 * @date Sep 7, 2017
 */
@Entity
@Table(name = "AUDIT_USER_ATTEMPTS")
@DynamicUpdate
@DynamicInsert
public class UserAttempts extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1134131827624522034L;

    // 登录名
    private String loginName;
    // 尝试次数
    private Integer attempts;
    // 最新尝试时间
    private Date latestAttemptTime;

    /**
     * @return the loginName
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * @param loginName 要设置的loginName
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @return the attempts
     */
    public Integer getAttempts() {
        return attempts;
    }

    /**
     * @param attempts 要设置的attempts
     */
    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    /**
     * @return the latestAttemptTime
     */
    public Date getLatestAttemptTime() {
        return latestAttemptTime;
    }

    /**
     * @param latestAttemptTime 要设置的latestAttemptTime
     */
    public void setLatestAttemptTime(Date latestAttemptTime) {
        this.latestAttemptTime = latestAttemptTime;
    }

}
