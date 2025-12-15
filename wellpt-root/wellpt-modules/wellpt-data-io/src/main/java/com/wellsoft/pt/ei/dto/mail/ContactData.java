package com.wellsoft.pt.ei.dto.mail;

import com.wellsoft.pt.ei.annotate.FieldType;

import java.io.Serializable;

/**
 * @Auther: yt
 * @Date: 2021/9/28 19:07
 * @Description:
 */
public class ContactData implements Serializable {
    private static final long serialVersionUID = 6250562415645817310L;

    @FieldType(desc = "主键uuid", required = true)
    private String uuid;
    @FieldType(desc = "用户ID", required = true)
    private String userId;
    @FieldType(desc = "联系人Id")
    private String contactId;
    @FieldType(desc = "联系人姓名", required = true)
    private String contactName;
    @FieldType(desc = "联系人电子邮箱", required = true)
    private String personalEmail;
    @FieldType(desc = "联系人手机号码")
    private String cellphoneNumber;
    @FieldType(desc = "备注")
    private String remark;
    @FieldType(desc = "分组uuid")
    private String groupUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }
}
