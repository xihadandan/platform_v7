package com.wellsoft.pt.basicdata.rtx.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 * Description: Rtx设置实体类
 *
 * @author zhoyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-6-17.1	zhoyq		2013-6-17		Create
 * </pre>
 * @date 2013-6-17
 */
@Entity
@Table(name = "cd_rtx")
@DynamicUpdate
@DynamicInsert
public class Rtx extends IdEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 是否启用
     */
    private Boolean isEnable;
    /**
     * RTX服务器IP
     */
    @NotBlank
    private String rtxServerIp;
    /**
     * RTX服务器端口
     */
    @NotNull
    @Digits(fraction = 0, integer = 10)
    private Integer rtxServerPort;
    /**
     * SDK服务器IP
     */
    private String sdkServerIp;
    /**
     * SDK服务器端口
     */
    //	@NotNull
    //	@Digits(fraction = 0, integer = 10)
    private Integer sdkServerPort;
    /**
     * RTX应用服务器IP
     */
    private String rtxApplicationServerIp;
    /**
     * RTX应用服务器端口
     */
    @Digits(fraction = 0, integer = 10)
    private Integer rtxApplicationServerPort;
    /**
     * 系统消息发送方式
     */
    private String messageSendWay;
    /**
     * 是否启用用户简称
     */
    private Boolean isEnableAbbreviation;
    /**
     * 同步操作
     */
    private String synchronizationOperation;
    /**
     * RTX客户端下载地址
     */
    private String rtxClientDownloadAddress;

    public Boolean getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Boolean isEnable) {
        this.isEnable = isEnable;
    }

    public String getRtxServerIp() {
        return rtxServerIp;
    }

    public void setRtxServerIp(String rtxServerIp) {
        this.rtxServerIp = rtxServerIp;
    }

    public String getSdkServerIp() {
        return sdkServerIp;
    }

    public void setSdkServerIp(String sdkServerIp) {
        this.sdkServerIp = sdkServerIp;
    }

    public String getRtxApplicationServerIp() {
        return rtxApplicationServerIp;
    }

    public void setRtxApplicationServerIp(String rtxApplicationServerIp) {
        this.rtxApplicationServerIp = rtxApplicationServerIp;
    }

    public String getMessageSendWay() {
        return messageSendWay;
    }

    public void setMessageSendWay(String messageSendWay) {
        this.messageSendWay = messageSendWay;
    }

    public Boolean getIsEnableAbbreviation() {
        return isEnableAbbreviation;
    }

    public void setIsEnableAbbreviation(Boolean isEnableAbbreviation) {
        this.isEnableAbbreviation = isEnableAbbreviation;
    }

    public String getRtxClientDownloadAddress() {
        return rtxClientDownloadAddress;
    }

    public void setRtxClientDownloadAddress(String rtxClientDownloadAddress) {
        this.rtxClientDownloadAddress = rtxClientDownloadAddress;
    }

    public String getSynchronizationOperation() {
        return synchronizationOperation;
    }

    public void setSynchronizationOperation(String synchronizationOperation) {
        this.synchronizationOperation = synchronizationOperation;
    }

    public Integer getRtxServerPort() {
        return rtxServerPort;
    }

    public void setRtxServerPort(Integer rtxServerPort) {
        this.rtxServerPort = rtxServerPort;
    }

    public Integer getSdkServerPort() {
        return sdkServerPort;
    }

    public void setSdkServerPort(Integer sdkServerPort) {
        this.sdkServerPort = sdkServerPort;
    }

    public Integer getRtxApplicationServerPort() {
        return rtxApplicationServerPort;
    }

    public void setRtxApplicationServerPort(Integer rtxApplicationServerPort) {
        this.rtxApplicationServerPort = rtxApplicationServerPort;
    }

}
