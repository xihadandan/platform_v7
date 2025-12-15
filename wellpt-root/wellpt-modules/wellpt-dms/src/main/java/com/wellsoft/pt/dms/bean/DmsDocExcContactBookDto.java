package com.wellsoft.pt.dms.bean;

import java.io.Serializable;

/**
 * Description: 文档交换-通讯录
 *
 * @author chenq
 * @date 2018/5/31
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/31    chenq		2018/5/31		Create
 * </pre>
 */
public class DmsDocExcContactBookDto implements Serializable {

    private static final long serialVersionUID = -5718387971207285418L;

    private String uuid;

    private String relaUserId;

    private String contactName;

    private String personalEmail;

    private String cellphoneNumber;

    private String remark;

    private String contactId;

    private String contactUnitUuid;

    private String contactUnitName;

    private String moduleId;

    public String getRelaUserId() {
        return relaUserId;
    }

    public void setRelaUserId(String relaUserId) {
        this.relaUserId = relaUserId;
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

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactUnitUuid() {
        return contactUnitUuid;
    }

    public void setContactUnitUuid(String contactUnitUuid) {
        this.contactUnitUuid = contactUnitUuid;
    }

    public String getContactUnitName() {
        return contactUnitName;
    }

    public void setContactUnitName(String contactUnitName) {
        this.contactUnitName = contactUnitName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
