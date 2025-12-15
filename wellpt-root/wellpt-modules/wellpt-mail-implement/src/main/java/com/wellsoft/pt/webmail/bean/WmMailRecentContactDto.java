/*
 * @(#)2018年3月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 最近联系人数据DTO
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月19日.1	chenqiong		2018年3月19日		Create
 * </pre>
 * @date 2018年3月19日
 */
public class WmMailRecentContactDto implements Serializable {

    private static final long serialVersionUID = 192943536676998128L;

    private String uuid;

    private String userId;// 用户id

    private String systemUnitId;// 系统组织id

    private String contacterMailAddress;

    private String contacterName;

    private Date lastContactTime;

    public WmMailRecentContactDto() {
        super();
    }

    public WmMailRecentContactDto(String userId, String systemUnitId, String contacterMailAddress,
                                  String contacterName, Date lastContactTime) {
        super();
        this.userId = userId;
        this.systemUnitId = systemUnitId;
        this.contacterMailAddress = contacterMailAddress;
        this.contacterName = contacterName;
        this.lastContactTime = lastContactTime;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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
