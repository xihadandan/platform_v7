package com.wellsoft.pt.message.sms;

public class CloudMasReq {

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
     * 手机号码数组，逗号分隔。
     */
    private String mobiles;
    /**
     * 发送短信内容
     */
    private String content;
    /**
     * 网关签名编码
     */
    private String sign;
    /**
     * 扩展码
     */
    private String addSerial;
    /**
     * API输入参数签名结果，
     * 签名算法：将ecName，apId，secretKey，mobiles，content ，sign，addSerial按照顺序拼接，
     * 然后通过md5(32位小写)计算后得出的值
     */
    private String mac;

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

    public String getMobiles() {
        return mobiles;
    }

    public void setMobiles(String mobiles) {
        this.mobiles = mobiles;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAddSerial() {
        return addSerial;
    }

    public void setAddSerial(String addSerial) {
        this.addSerial = addSerial;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

}
