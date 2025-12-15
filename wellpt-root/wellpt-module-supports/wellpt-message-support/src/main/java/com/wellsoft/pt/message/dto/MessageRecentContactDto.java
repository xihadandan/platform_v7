/*
 * @(#)2018年4月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月26日.1	chenqiong		2018年4月26日		Create
 * </pre>
 * @date 2018年4月26日
 */
public class MessageRecentContactDto implements Serializable {

    private static final long serialVersionUID = 4934066230787260843L;

    private String userId;// 用户id

    private String systemUnitId;// 系统组织id

    private String contactWay; // 联系方式

    private String contacterName;

    private Date lastContactTime;

    public MessageRecentContactDto(String userId, String systemUnitId, String contactWay, String contacterName,
                                   Date lastContactTime) {
        super();
        this.userId = userId;
        this.systemUnitId = systemUnitId;
        this.contactWay = contactWay;
        this.contacterName = contacterName;
        this.lastContactTime = lastContactTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSystemUnitId() {
        return systemUnitId;
    }

    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }

    public String getContacterName() {
        return contacterName;
    }

    public void setContacterName(String contacterName) {
        this.contacterName = contacterName;
    }

    public Date getLastContactTime() {
        return lastContactTime;
    }

    public void setLastContactTime(Date lastContactTime) {
        this.lastContactTime = lastContactTime;
    }

}
