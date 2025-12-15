package com.wellsoft.pt.webmail.listeners;

import com.google.common.base.Stopwatch;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.event.AccountUserEvent;
import com.wellsoft.pt.webmail.entity.WmMailConfigEntity;
import com.wellsoft.pt.webmail.service.WmMailConfigService;
import com.wellsoft.pt.webmail.service.WmMailUserService;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description: 用户组织帐户事件侦听器
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
@Component
public class JamesMailUserOrgAccountEventListener implements
        ApplicationListener<AccountUserEvent> {

    @Resource
    WmMailUserService wmMailUserService;
    @Resource
    WmMailConfigService wmMailConfigService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(AccountUserEvent accountUserEvent) {
        logger.info("开始处理组织用户派发的账号事件");
        Stopwatch timer = Stopwatch.createStarted();
        try {

            AccountUserEvent.AccountUserEventSource source = (AccountUserEvent.AccountUserEventSource) accountUserEvent.getSource();
            List<MultiOrgUserAccount> accountList = source.getAccounts();
            if (CollectionUtils.isEmpty(accountList)) {
                return;
            }
            if (source.isAdd()) {

                if (ApplicationContextHolder.getBean("james", SessionFactory.class) == null
                        || CollectionUtils.isEmpty(accountList)) {
                    return;
                }
                WmMailConfigEntity config = wmMailConfigService.getBySystemUnitId(
                        accountList.get(0).getSystemUnitId());
                //存在添加账号的时候邮局还没配置的情况，所以需要判断下config !=null
                if (config != null) {
                    for (MultiOrgUserAccount user : accountList) {
                        // 更改密码为0
                        wmMailUserService.saveOrUpdateMailUser(config, user, WmMailConfigEntity.MAIL_USER_DEFAULT_PASSWORD);
                    }
                }
            } else if (source.isDelete()) {
                for (MultiOrgUserAccount account : accountList) {
                    wmMailUserService.deleteMailUser(account.getId());
                }
            }


        } catch (Exception e) {
            logger.error("处理组织用户派发的账号事件异常：", e);
        }
        logger.info("结束处理组织用户派发的账号事件，耗时：{}", timer.stop());
    }
}
