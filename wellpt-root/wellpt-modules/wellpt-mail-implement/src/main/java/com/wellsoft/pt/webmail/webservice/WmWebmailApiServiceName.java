/*
 * @(#)2016年7月18日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.webservice;

/**
 * Description: Api服务名称
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月18日.1	zhulh		2016年7月18日		Create
 * </pre>
 * @date 2016年7月18日
 */
public interface WmWebmailApiServiceName {
    //1、收件箱
    public static final String WM_WEBMAIL_INBOX_QUERY = "wm.webmail.inbox.query";
    public static final String WM_WEBMAIL_INBOX_GET = "wm.webmail.inbox.get";
    //2、 发送(发送、转发、回复)
    public static final String WM_WEBMAIL_SEND = "wm.webmail.send";
    //3、发件箱
    public static final String WM_WEBMAIL_OUTBOX_QUERY = "wm.webmail.outbox.query";
    public static final String WM_WEBMAIL_OUTBOX_GET = "wm.webmail.outbox.get";
}
