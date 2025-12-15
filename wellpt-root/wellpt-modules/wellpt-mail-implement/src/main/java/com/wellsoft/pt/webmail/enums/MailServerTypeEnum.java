package com.wellsoft.pt.webmail.enums;

/**
 * Description: 邮件服务器类型枚举
 *
 * @author chenq
 * @date 2020/1/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/1/14    chenq		2020/1/14		Create
 * </pre>
 */
public enum MailServerTypeEnum {
    JAMES_MAIL("JamesMail"), CORE_MAIL("CoreMail");

    private String code;


    MailServerTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
