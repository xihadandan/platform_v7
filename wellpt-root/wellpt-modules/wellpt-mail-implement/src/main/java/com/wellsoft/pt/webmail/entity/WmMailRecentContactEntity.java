/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 邮件最近联系人
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月1日.1	chenqiong		2018年3月1日		Create
 * </pre>
 * @date 2018年3月1日
 */
@Entity
@Table(name = "WM_MAIL_RECENT_CONTACT")
@DynamicUpdate
@DynamicInsert
public class WmMailRecentContactEntity extends IdEntity {

    private static final long serialVersionUID = 7582214710773515995L;

    private String userId;// 用户id

    private String systemUnitId;// 系统组织id

    private String contacterMailAddress;

    private String contacterName;

    private Date lastContactTime;

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    /**
     * @return the contacterMailAddress
     */
    public String getContacterMailAddress() {
        return contacterMailAddress;
    }

    /**
     * @param contacterMailAddress 要设置的contacterMailAddress
     */
    public void setContacterMailAddress(String contacterMailAddress) {
        this.contacterMailAddress = contacterMailAddress;
    }

    /**
     * @return the contacterName
     */
    public String getContacterName() {
        return contacterName;
    }

    /**
     * @param contacterName 要设置的contacterName
     */
    public void setContacterName(String contacterName) {
        this.contacterName = contacterName;
    }

    /**
     * @return the lastContactTime
     */
    public Date getLastContactTime() {
        return lastContactTime;
    }

    /**
     * @param lastContactTime 要设置的lastContactTime
     */
    public void setLastContactTime(Date lastContactTime) {
        this.lastContactTime = lastContactTime;
    }

}
