package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 邮件个人通讯录
 *
 * @author chenq
 * @date 2018/6/6
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/6    chenq		2018/6/6		Create
 * </pre>
 */
@Entity
@Table(name = "UF_PT_MAIL_CONTACT_BOOK")
@DynamicInsert
@DynamicUpdate
public class WmMailContactBookEntity extends TenantEntity {
    private static final long serialVersionUID = 4621272623939209634L;


    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 电子邮件
     */
    private String personalEmail;

    /**
     * 手机号码
     */
    private String cellphoneNumber;

    /**
     * 备注
     */
    private String remark;

    /**
     * 联系人Id
     */
    private String contactId;

    /**
     * 分组Uuid
     */
    private String groupUuid;


    /**
     * 获取 联系人姓名
     *
     * @return
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * 设置 联系人姓名
     *
     * @param contactName
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * 获取 电子邮件
     *
     * @return
     */
    public String getPersonalEmail() {
        return personalEmail;
    }

    /**
     * 设置 电子邮件
     *
     * @param personalEmail
     */
    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    /**
     * 获取 手机号码
     *
     * @return
     */
    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    /**
     * 设置 手机号码
     *
     * @param cellphoneNumber
     */
    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    /**
     * 获取 备注
     *
     * @return
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 备注
     *
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }


    /**
     * 获取 分组Uuid
     *
     * @return
     */
    public String getGroupUuid() {
        return groupUuid;
    }

    /**
     * 设置 分组Uuid
     *
     * @param groupUuid
     */
    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    /**
     * 获取 联系人Id
     *
     * @return
     */
    public String getContactId() {
        return contactId;
    }

    /**
     * 设置 联系人Id
     *
     * @param contactId
     */
    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

}
