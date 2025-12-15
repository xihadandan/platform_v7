package com.wellsoft.pt.security.oauth2.listener;

import com.google.common.base.Stopwatch;
import com.wellsoft.context.Context;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.event.AccountUserEvent;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/31
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/31    chenq		2019/7/31		Create
 * </pre>
 */
//@Component
public class OAuth2UserOrgAccountEventListener implements
        ApplicationListener<AccountUserEvent> {

    @Autowired
    UserInfoFacadeService userInfoFacadeService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(AccountUserEvent accountUserEvent) {
        if (!Context.isOauth2Enable()) {
            return;
        }
        logger.info("开始处理组织用户派发的账号事件");
        Stopwatch timer = Stopwatch.createStarted();
        try {

            AccountUserEvent.AccountUserEventSource source = (AccountUserEvent.AccountUserEventSource) accountUserEvent.getSource();
            List<MultiOrgUserAccount> accountList = source.getAccounts();
            if (CollectionUtils.isEmpty(accountList)) {
                return;
            }
            if (source.isAdd()) {
                try {
                    UserDto userDto = new UserDto();
                    userDto.setLoginName(accountList.get(0).getLoginName());
                    userDto.setPassword(accountList.get(0).getPassword());
                    userDto.setUserName(accountList.get(0).getUserName());
                    userInfoFacadeService.addUser(userDto);
                } catch (Exception e) {
                    throw new RuntimeException("新增账号异常");
                }

            } else if (source.isDelete()) {
            }


        } catch (Exception e) {
            logger.error("处理组织用户派发的账号事件异常：", e);
            throw e;
        }
        logger.info("结束处理组织用户派发的账号事件，耗时：{}", timer.stop());
    }
}
