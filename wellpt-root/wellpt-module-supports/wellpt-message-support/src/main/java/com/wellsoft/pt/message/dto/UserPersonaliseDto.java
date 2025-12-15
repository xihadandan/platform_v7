package com.wellsoft.pt.message.dto;

import java.io.Serializable;

/**
 * @author yt
 * @title: UserPersonaliseDto 消息格式 个性设置
 * @date 2020/5/19 5:23 下午
 */
@SuppressWarnings("serial")
public class UserPersonaliseDto implements Serializable {

    private String templateId;//消息格式Id
    private int isPopup;//是否弹窗(0：否，1：是)
    private String templateName;//消息格式名称

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public int getIsPopup() {
        return isPopup;
    }

    public void setIsPopup(int isPopup) {
        this.isPopup = isPopup;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
