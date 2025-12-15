package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.jpa.service.UUIDGeneratorIndicate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Auther: yt
 * @Date: 2022/2/12 11:43
 * @Description: 邮件信息关联用户表
 */
@Entity
@Table(name = "WM_MAILBOX_INFO_STATUS")
@DynamicUpdate
@DynamicInsert
public class WmMailboxInfoStatus extends IdEntity implements UUIDGeneratorIndicate {
    private static final long serialVersionUID = -5775238520331435964L;


    /**
     * 邮件信息uuid
     */
    private String mailInfoUuid;

    /**
     * 发件人关联信息UUID
     */
    private String mailInfoUserUuid;

    /**
     * 收件人类型（1，收件，2：抄送，3：密送）
     */
    private Integer recipientType;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 邮件用户名
     */
    private String mailName;

    /**
     * 邮件地址
     */
    private String mailAddress;

    /**
     * 状态（0:待发送，1：已发送，2：已投递到邮箱服务，3：地址不存在，4：未开启公网邮箱，5：无效邮件地址，6：邮件服务异常 ）
     */
    private Integer status;

    /**
     * 系统单位Id
     */
    private String systemUnitId;


    public String getMailInfoUuid() {
        return mailInfoUuid;
    }

    public void setMailInfoUuid(String mailInfoUuid) {
        this.mailInfoUuid = mailInfoUuid;
    }

    public String getMailInfoUserUuid() {
        return mailInfoUserUuid;
    }

    public void setMailInfoUserUuid(String mailInfoUserUuid) {
        this.mailInfoUserUuid = mailInfoUserUuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMailName() {
        return mailName;
    }

    public void setMailName(String mailName) {
        this.mailName = mailName;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSystemUnitId() {
        return systemUnitId;
    }

    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public Integer getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(Integer recipientType) {
        this.recipientType = recipientType;
    }
}
