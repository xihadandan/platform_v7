package com.wellsoft.pt.webmail.enums;

/**
 * @Auther: yt
 * @Date: 2022/4/6 17:05
 * @Description:
 */
public enum WmMailboxInfoStatusEnum {
    ToBeSent("待发送"),
    HasBeenSent("已发送"),
    PostedToMailboxService("已投递到邮箱服务"),
    AddressDoesNotExist("地址不存在"),
    ThePublicMailboxIsNotOpened("未开启公网邮箱"),
    InvalidEmailAddress("无效邮件地址"),
    MailServiceException("邮件服务异常");
    //0:待发送，1：已发送，2：已投递到邮箱服务，3：地址不存在，4：未开启公网邮箱，5：无效邮件地址，6：邮件服务异常
    private String name;

    WmMailboxInfoStatusEnum(String name) {
        this.name = name;
    }

    public static String getName(int ordinal) {
        for (WmMailboxInfoStatusEnum value : WmMailboxInfoStatusEnum.values()) {
            if (value.ordinal() == ordinal) {
                return value.name;
            }
        }
        return null;
    }
}
