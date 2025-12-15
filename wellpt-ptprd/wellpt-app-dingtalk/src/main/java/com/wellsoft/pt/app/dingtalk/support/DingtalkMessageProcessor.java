/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.app.dingtalk.entity.MultiOrgDingUser;
import com.wellsoft.pt.app.dingtalk.enums.EnumDingtalkMsgType;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgDingUserService;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiUtils;
import com.wellsoft.pt.message.processor.AbstractMessageProcessor;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年6月16日.1	zhongzh		2020年6月16日		Create
 * </pre>
 * @date 2020年6月16日
 */
@Component
@Deprecated
public class DingtalkMessageProcessor extends AbstractMessageProcessor {

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private MultiOrgDingUserService multiOrgDingUserService;

    @Override
    public void doProcessor(Message msg) {
        List<String> useridList = Lists.newArrayList();
        for (String userId : msg.getRecipients()) {
            if (StringUtils.startsWith(userId, IdPrefix.ORG_VERSION.getValue())) {
                userId = userId.substring(userId.lastIndexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) + 1);
            }
            MultiOrgDingUser multiOrgDingUser = multiOrgDingUserService.getByPtUserId(userId);
            if (null == multiOrgDingUser || StringUtils.isBlank(multiOrgDingUser.getDing_userId())) {
                continue;
            }
            useridList.add(multiOrgDingUser.getDing_userId());
        }
        if (StringUtils.isBlank(msg.getDtMessageType()) || StringUtils.equals(msg.getDtMessageType(), "ActionCard")) {
            DingtalkApiUtils.sendDingtalkMsg(StringUtils.join(useridList, ","),
                    EnumDingtalkMsgType.action_card.getValue(), msg.getSubject(), msg.getBody(), null, null, null,
                    msg.getDtBtnOrientation(), msg.getDtBtnJsonList(), DingtalkApiUtils.getAccessToken());
        }
    }
}
