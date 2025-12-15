package com.wellsoft.pt.dms.enums;

/**
 * Description:文档交换类型
 *
 * @author chenq
 * @date 2018/5/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/5/16    chenq		2018/5/16		Create
 * </pre>
 */
public enum DocExchangeFeedbackTypeEnum {

    RECEIVER_FEEDBACK("收文反馈"), SENDER_ANSWER("发文回执"), SENDER_REQUEST_FEEDBACK_AGAIN("发文要求再次反馈");

    private String name;

    DocExchangeFeedbackTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
