package com.wellsoft.pt.webmail.bean;

import java.io.Serializable;

/**
 * @Auther: yt
 * @Date: 2022/2/17 13:41
 * @Description:
 */
public class WmMailUserAddress implements Serializable {
    private static final long serialVersionUID = 7567505664089244982L;

    private String userId;
    private String userName;
    private String mailAddress;
    private String systemUnitId;
    private Integer limitCapacity;
    private Long usedCapacity;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getSystemUnitId() {
        return systemUnitId;
    }

    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public Integer getLimitCapacity() {
        return limitCapacity;
    }

    public void setLimitCapacity(Integer limitCapacity) {
        this.limitCapacity = limitCapacity;
    }

    public Long getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(Long usedCapacity) {
        this.usedCapacity = usedCapacity;
    }
}

