package com.wellsoft.pt.message.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: MAX短信机配置
 *
 * @author wangbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-5.1	wangbx		2014-1-5		Create
 * </pre>
 * @date 2014-1-5
 */
@Entity
@Table(name = "msg_mas_config")
@DynamicUpdate
@DynamicInsert
public class MasConfig extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    /**
     * 移动代理服务器的 IP 地址
     */
    private String imIp;
    /**
     * 接口创建时的接口登录名
     */
    private String loginName;
    /**
     * 接口创建时的接口登录密码
     */
    private String loginPassword;
    /**
     * 接口编码
     */
    private String apiCode;
    /**
     * 数据库名称
     */
    private String dbName;
    /**
     * 是否启用
     */
    private Boolean isOpen;
    /**
     * 重发时限
     */
    private Integer sendLimit;

    // ADD
    /**
     * webService 地址
     */
    private String sWebService;
    /**
     * 登录名sLoginName
     */
    private String sLoginName;
    /**
     * 登录密码sLoginPassWord
     */
    private String sLoginPassword;
    /**
     * 是否启用sIsOpen
     */
    private Boolean sIsOpen;
    /**
     * 重发时限sSendLimit
     */
    private Integer sSendLimit;
    /**
     * 异步发送
     */
    private Boolean apiAsync;

    private String clientBean;

    private String clientBeanName;

    // 云MAS
    /**
     * 云MAS平台HTTP地址
     */
    private String cloudMasHttp;
    /**
     * 集团客户名称
     */
    private String ecName;
    /**
     * 用户名
     */
    private String apId;
    /**
     * 密码
     */
    private String secretKey;
    /**
     * 网关签名编码
     */
    private String cloudSign;
    /**
     * 扩展码
     */
    private String addSerial;
    /**
     * 重发时限sSendLimit
     */
    private Integer cloudSendLimit;
    /**
     * 是否启用云MAS
     */
    private Boolean cloudIsOpen;

    public String getImIp() {
        return imIp;
    }

    public void setImIp(String imIp) {
        this.imIp = imIp;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Integer getSendLimit() {
        return sendLimit;
    }

    public void setSendLimit(Integer sendLimit) {
        this.sendLimit = sendLimit;
    }

    public String getsWebService() {
        return sWebService;
    }

    public void setsWebService(String sWebService) {
        this.sWebService = sWebService;
    }

    public String getsLoginName() {
        return sLoginName;
    }

    public void setsLoginName(String sLoginName) {
        this.sLoginName = sLoginName;
    }

    public String getsLoginPassword() {
        return sLoginPassword;
    }

    public void setsLoginPassword(String sLoginPassword) {
        this.sLoginPassword = sLoginPassword;
    }

    public Boolean getsIsOpen() {
        return sIsOpen;
    }

    public void setsIsOpen(Boolean sIsOpen) {
        this.sIsOpen = sIsOpen;
    }

    public Integer getsSendLimit() {
        return sSendLimit;
    }

    public void setsSendLimit(Integer sSendLimit) {
        this.sSendLimit = sSendLimit;
    }

    public Boolean getApiAsync() {
        return apiAsync;
    }

    public void setApiAsync(Boolean apiAsync) {
        this.apiAsync = apiAsync;
    }

    public String getClientBean() {
        return clientBean;
    }

    public void setClientBean(String clientBean) {
        this.clientBean = clientBean;
    }

    public String getClientBeanName() {
        return clientBeanName;
    }

    public void setClientBeanName(String clientBeanName) {
        this.clientBeanName = clientBeanName;
    }

    public String getCloudMasHttp() {
        return cloudMasHttp;
    }

    public void setCloudMasHttp(String cloudMasHttp) {
        this.cloudMasHttp = cloudMasHttp;
    }

    public String getEcName() {
        return ecName;
    }

    public void setEcName(String ecName) {
        this.ecName = ecName;
    }

    public String getApId() {
        return apId;
    }

    public void setApId(String apId) {
        this.apId = apId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getCloudSign() {
        return cloudSign;
    }

    public void setCloudSign(String cloudSign) {
        this.cloudSign = cloudSign;
    }

    public String getAddSerial() {
        return addSerial;
    }

    public void setAddSerial(String addSerial) {
        this.addSerial = addSerial;
    }

    public Integer getCloudSendLimit() {
        return cloudSendLimit;
    }

    public void setCloudSendLimit(Integer cloudSendLimit) {
        this.cloudSendLimit = cloudSendLimit;
    }

    public Boolean getCloudIsOpen() {
        return cloudIsOpen;
    }

    public void setCloudIsOpen(Boolean cloudIsOpen) {
        this.cloudIsOpen = cloudIsOpen;
    }

    @Override
    public String toString() {
        return "MasConfig [imIp=" + imIp + ", loginName=" + loginName + ", loginPassword=" + loginPassword
                + ", apiCode=" + apiCode + ", dbName=" + dbName + ", isOpen=" + isOpen + ", sendLimit=" + sendLimit
                + ", sWebService=" + sWebService + ", sLoginName=" + sLoginName + ", sLoginPassword=" + sLoginPassword
                + ", sIsOpen=" + sIsOpen + ", sSendLimit=" + sSendLimit + ", cloudMasHttp=" + cloudMasHttp
                + ", ecName=" + ecName + ", apId=" + apId + ", secretKey=" + secretKey + ", cloudSign=" + cloudSign
                + ", addSerial=" + addSerial + ", cloudSendLimit=" + cloudSendLimit + ", cloudIsOpen=" + cloudIsOpen
                + "]";
    }
}
