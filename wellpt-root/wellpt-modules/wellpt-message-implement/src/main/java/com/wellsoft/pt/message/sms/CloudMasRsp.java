package com.wellsoft.pt.message.sms;

public class CloudMasRsp {

    /**
     * 响应码
     */
    private String rspcod;
    /**
     * 消息批次号
     */
    private String msgGroup;
    /**
     * true,false
     */
    private boolean success;

    public String getRspcod() {
        return rspcod;
    }

    public void setRspcod(String rspcod) {
        this.rspcod = rspcod;
    }

    public String getMsgGroup() {
        return msgGroup;
    }

    public void setMsgGroup(String msgGroup) {
        this.msgGroup = msgGroup;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
