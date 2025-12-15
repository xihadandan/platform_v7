package com.wellsoft.pt.mail.support;

/**
 * 邮件类型
 *
 * @author lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-2	lmw		2015-6-2		Create
 * </pre>
 * @date 2015-6-2
 */
public interface MailType {
    static final String MAILTYPE_GENERAL = "1";// 普通邮件
    static final String MAILTYPE_GROUP = "2";//群邮件
    static final String SENDTYPE_SENDER = "1";//发送
    static final String SENDTYPE_DIFF = "0";//分别发送
    static final String SENDTYPE_JOB = "5";//定时发送
    static final String GROUP_COPY = "1";//发群邮件包括复制转发
    static final String GROUP_REPLY = "2";//群邮件回复
    static final String GROUP_offer = "3";//群邮件转发
}
