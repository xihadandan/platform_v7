package com.wellsoft.pt.webmail.enums;

/**
 * @Auther: yt
 * @Date: 2021/5/7 17:07
 * @Description: 邮箱实际状态
 */
public enum WmMailBoxActualStatus {

    /**
     * 1：投递中
     */
    Sending(1, "投递中"),
    /**
     * 2: 投递成功
     */
    SentSuccess(2, "投递成功"),
    /**
     * 3: 邮件服务异常
     */
    MailServiceException(3, "邮件服务异常"),
    /**
     * 4: 收件人邮箱空间已满
     */
    RecipientSMailboxIsFull(4, "收件人邮箱空间已满"),
    /**
     * 5: 收件人接收异常
     */
    RecipientReceivingException(5, "收件人接收异常");

    /**
     * 邮件实际状态值
     */
    private int code;
    /**
     * 邮件实际状态值说明
     */
    private String name;

    WmMailBoxActualStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static WmMailBoxActualStatus getStatusByCode(Integer code) {
        if (code == null) {
            return null;
        }
        WmMailBoxActualStatus[] statusArray = WmMailBoxActualStatus.values();
        for (WmMailBoxActualStatus s : statusArray) {
            if (s.getCode() == code) {
                return s;
            }
        }
        return null;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
