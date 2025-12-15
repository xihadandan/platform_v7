package com.wellsoft.pt.unit.bean;

import com.wellsoft.pt.unit.entity.BusinessType;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Description: BusinessTypeBean VO
 *
 * @author liuzq
 * @date 2013-11-5
 */
public class BusinessTypeBean extends BusinessType {
    private static final long serialVersionUID = -8709017810257887726L;

    @NotBlank
    private String unitName;
    @NotBlank
    private String unitId;

    // 业务类别的单位内业务负责人
    private String businessManagerUserName;
    private String businessManagerUserId;

    // 业务类别的单位内收发业务具体发送人员
    private String businessSenderName;
    private String businessSenderId;
    // 业务类别的单位内收发业务具体接受人员
    private String businessReceiverName;
    private String businessReceiverId;

    private String businessTypeUuid;

    private String businessUnitTreeUuid;

    private String parentUuid;

    public String getBusinessManagerUserId() {
        return businessManagerUserId;
    }

    public void setBusinessManagerUserId(String businessManagerUserId) {
        this.businessManagerUserId = businessManagerUserId;
    }

    public String getBusinessSenderName() {
        return businessSenderName;
    }

    public void setBusinessSenderName(String businessSenderName) {
        this.businessSenderName = businessSenderName;
    }

    public String getBusinessSenderId() {
        return businessSenderId;
    }

    public void setBusinessSenderId(String businessSenderId) {
        this.businessSenderId = businessSenderId;
    }

    public String getBusinessReceiverName() {
        return businessReceiverName;
    }

    public void setBusinessReceiverName(String businessReceiverName) {
        this.businessReceiverName = businessReceiverName;
    }

    public String getBusinessReceiverId() {
        return businessReceiverId;
    }

    public void setBusinessReceiverId(String businessReceiverId) {
        this.businessReceiverId = businessReceiverId;
    }

    public String getBusinessUnitTreeUuid() {
        return businessUnitTreeUuid;
    }

    public void setBusinessUnitTreeUuid(String businessUnitTreeUuid) {
        this.businessUnitTreeUuid = businessUnitTreeUuid;
    }

    public String getBusinessManagerUserName() {
        return businessManagerUserName;
    }

    public void setBusinessManagerUserName(String businessManagerUserName) {
        this.businessManagerUserName = businessManagerUserName;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    public String getBusinessTypeUuid() {
        return businessTypeUuid;
    }

    public void setBusinessTypeUuid(String businessTypeUuid) {
        this.businessTypeUuid = businessTypeUuid;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

}
