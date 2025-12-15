package com.wellsoft.pt.message.strategy;

import com.wellsoft.pt.message.dto.SmsUser;

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
public interface SmsLackOfMobilePhoneStrategy {

    void execute(List<SmsUser> users);
}
