package com.wellsoft.pt.message.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yt
 * @title: UserPersonalise 用户个性化设置
 * @date 2020/5/18 7:02 下午
 */
@Entity
@Table(name = "msg_user_personalise")
@DynamicUpdate
@DynamicInsert
public class UserPersonalise extends TenantEntity {

    private String userId;//用户Id
    private String templateId;//消息格式Id
    private Integer isPopup;//是否弹窗（打开）(0：否，1：是)
    private Integer type; //开关类型 1：主开关，2：消息格式

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Integer getIsPopup() {
        return isPopup;
    }

    public void setIsPopup(Integer isPopup) {
        this.isPopup = isPopup;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
