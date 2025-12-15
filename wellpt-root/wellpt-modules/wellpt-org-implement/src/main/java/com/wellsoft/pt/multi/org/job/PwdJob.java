package com.wellsoft.pt.multi.org.job;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgPwdSettingFacadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 密码有效期提醒定时任务
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本 修改人 修改日期 修改内容
 * 2021/3/31.1 zenghw 2021/3/31 Create
 * </pre>
 * @date 2021/3/31
 */
public class PwdJob {

    private static Logger logger = LoggerFactory.getLogger(PwdJob.class);

    public static void startWarn() {
        MultiOrgPwdSettingFacadeService multiOrgPwdSettingFacadeService = ApplicationContextHolder
                .getBean(MultiOrgPwdSettingFacadeService.class);
        multiOrgPwdSettingFacadeService.pwdValidityWarn();
    }

    public static void startPwdUnlock() {
        MultiOrgPwdSettingFacadeService multiOrgPwdSettingFacadeService = ApplicationContextHolder
                .getBean(MultiOrgPwdSettingFacadeService.class);
        multiOrgPwdSettingFacadeService.pwdUnlock();
    }
}
