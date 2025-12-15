/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.processor.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.message.processor.AbstractMessageProcessor;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.webmail.facade.api.service.impl.MailClientApiFacadeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Description: 邮件消息处理器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-29.1	zhulh		2012-10-29		Create
 * </pre>
 * @date 2012-10-29
 */
@Component
public class MailMessageProcessor extends AbstractMessageProcessor {

    @Autowired
    private MailClientApiFacadeImpl mailClientApiFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.processor.MessageProcessor#doProcessor(com.wellsoft.pt.message.support.Message)
     */
    @Override
    public void doProcessor(Message msg) {
		/*List<String> recipients = msg.getRecipients();
		for (String recipient : recipients) {
			//发送邮件
			if ("system".equals(msg.USER_SYSTEM)) {
				//系统邮件
				List<String> list = msg.getRecipients();
				String[] str = new String[list.size()];
				list.toArray(str);
				mailApiFacade.sendBySystem(str, msg.getSubject(), msg.getBody());
			} else {
				//普通
				mailApiFacade.send(msg.getSender(), msg.get, arg2, arg3)
			}
		}*/
        List<String> list = msg.getRecipients();
        String[] str = new String[list.size()];
        list.toArray(str);
        for (String s : str) {
            System.out.println("每个数组元素值：" + s);
        }
        String sender = msg.getSender();
        System.out.println("发送人名称：" + sender);
        String messageAttach = msg.getAttach();
        List<String> field_list = new ArrayList<String>();
        if (messageAttach != null && !"".equals(messageAttach)) {
            String[] fileids = messageAttach.split(Separator.COMMA.getValue());
            field_list = Arrays.asList(fileids);
        }
        // 发送邮件
        // if ("system".equals(msg.getSender())) {
        // 系统邮件
        // mailApiFacade.sendBySystem(str, msg.getSubject(), msg.getBody());
        mailClientApiFacade.sendBySystem(str, msg.getSubject(), msg.getBody(), field_list);
        // } else {
        // 普通
        // mailApiFacade.send(msg.getSender(), str, msg.getSubject(),
        // msg.getBody());
        // }
    }
}
