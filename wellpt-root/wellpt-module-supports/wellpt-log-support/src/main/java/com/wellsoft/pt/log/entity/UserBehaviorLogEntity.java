package com.wellsoft.pt.log.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年09月16日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "USER_BEHAVIOR_LOG")
@DynamicUpdate
@DynamicInsert
public class UserBehaviorLogEntity extends SysEntity {

    private String userId;
    private String userName;
    private String behaviorType;
    private String behaviorDesc;
    private String elementTag;
    private String elementText;
    private String elementXpath;
    private String pageUrl;
    private String pageTitle;
    private String pageId;
    private String referrer;
    private String ipAddress;
    private String userOs;
    private String browser;
    private String extraInfo; // 业务扩展信息字段
    private String businessCode; // 业务代码
    private String moduleId;
    private String moduleName;

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

    public String getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(String behaviorType) {
        this.behaviorType = behaviorType;
    }

    public String getBehaviorDesc() {
        return behaviorDesc;
    }

    public void setBehaviorDesc(String behaviorDesc) {
        this.behaviorDesc = behaviorDesc;
    }

    public String getElementTag() {
        return elementTag;
    }

    public void setElementTag(String elementTag) {
        this.elementTag = elementTag;
    }

    public String getElementText() {
        return elementText;
    }

    public void setElementText(String elementText) {
        this.elementText = elementText;
    }

    public String getElementXpath() {
        return elementXpath;
    }

    public void setElementXpath(String elementXpath) {
        this.elementXpath = elementXpath;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserOs() {
        return userOs;
    }

    public void setUserOs(String userOs) {
        this.userOs = userOs;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }
}
