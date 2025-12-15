/*
 * @(#)2015年8月6日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mail.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.mail.support.James3MailId;

import java.util.List;

/**
 * Description: james3邮件客户端服务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年8月6日.1	zhulh		2015年8月6日		Create
 * </pre>
 * @date 2015年8月6日
 */
public interface James3MailClientService extends BaseService {

    /**
     * 如何描述该方法
     *
     * @param username
     */
    void fetchMail(String username, List<James3MailId> james3MailIds) throws Exception;

}
