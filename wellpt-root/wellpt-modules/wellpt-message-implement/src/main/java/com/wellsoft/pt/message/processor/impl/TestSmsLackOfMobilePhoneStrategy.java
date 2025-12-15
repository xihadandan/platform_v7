package com.wellsoft.pt.message.processor.impl;

import com.wellsoft.pt.message.dto.SmsUser;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年08月14日   chenq	 Create
 * </pre>
 */
@Component
public class TestSmsLackOfMobilePhoneStrategy implements com.wellsoft.pt.message.strategy.SmsLackOfMobilePhoneStrategy {
    @Override
    public void execute(List<SmsUser> users) {
        for (SmsUser su : users) {
            // TODO: 处理缺失移动号码的用户列表
            su.setMobilePhone("15959090121");
        }
    }
}
