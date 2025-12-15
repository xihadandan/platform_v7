package com.wellsoft.pt.message.sms;

public interface MoType {
    /**
     * @return 短信ID
     */
    public String getSmId();

    /**
     * @return 手机号
     */
    public String getMobile();

    /**
     * @return 短信内容
     */
    public String getMoContent();

    /**
     * @return 短信创建时间
     */
    public String getMoTime();

    /**
     * @return 短信对象
     */
    public Object getMo();
}