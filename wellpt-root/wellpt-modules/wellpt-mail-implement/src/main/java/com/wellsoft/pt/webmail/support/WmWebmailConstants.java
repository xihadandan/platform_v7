/*
 * @(#)2016年6月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.support;

/**
 * Description: 邮件相关常量
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年6月4日.1	zhulh		2016年6月4日		Create
 * </pre>
 * @date 2016年6月4日
 */
public class WmWebmailConstants {
    // 0草稿 1发送成功 2接收成功 3接收失败（空间不足） -1删除 -2彻底删除
    public static final Integer STATUS_DRAFT = 0;
    public static final Integer STATUS_SEND_SUCCESS = 1;
    public static final Integer STATUS_RECEIVE_SUCCESS = 2;
    public static final Integer STATUS_RECEIVE_FAIL = 3;
    public static final Integer STATUS_DELETE = -1;
    public static final Integer STATUS_PHYSICAL_DELETE = -2;

    // 收件箱，发件箱
    public static final String INBOX = "INBOX";//收件箱
    public static final String OUTBOX = "OUTBOX";//发件箱
    public static final String DRAFT = "DRAFT";//草稿箱
    public static final String RECYCLE = "RECYCLE";//回收站

    // 附件头信息
    public static final String HEADER_FILE_ID = "fileID";
    public static final String HEADER_PRIORITY = "X-Priority";
    public static final String HEADER_REQUIRE_READ_RECEIPT = "Disposition-Notification-To";
    public static final String HEADER_FROM_MAIL_ID = "from-mail-id";
    public static final String HEADER_MAIL_SEND_TIMESTAMP = "send-timestamp";
    public static final String HEADER_TO_ADDRESS = "send-to-address";
    public static final String HEADER_CC_ADDRESS = "send-cc-address";
    public static final String HEADER_BCC_ADDRESS = "send-bcc-address";
    public static final String HEADER_TO_NAME = "send-to-name";
    public static final String HEADER_CC_NAME = "send-cc-name";
    public static final String HEADER_BCC_NAME = "send-bcc-name";
    // 邮件分隔符
    public static final String MAIL_SEPARATOR = "@";

    // 设置优先级(1:紧急 3:普通 5:低)
    public static final String PRIORITY_MERGE = "1";
    public static final String PRIORITY_NORMAL = "3";
    public static final String PRIORITY_LOWER = "5";

    // 是否已读
    public static final String FLAG_UNREAD = "0";
    public static final String FLAG_READ = "1";
}
