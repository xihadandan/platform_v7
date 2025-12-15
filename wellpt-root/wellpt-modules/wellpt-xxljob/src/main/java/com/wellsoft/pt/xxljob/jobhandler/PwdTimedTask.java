package com.wellsoft.pt.xxljob.jobhandler;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import com.wellsoft.pt.xxljob.model.ExecutionParam;
import com.wellsoft.pt.xxljob.service.JobHandlerName;
import com.wellsoft.pt.xxljob.utils.ParamUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.well.annotation.WellXxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Description: 密码定时任务
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/31.1	    zenghw		2021/3/31		    Create
 * </pre>
 * @date 2021/3/31
 */
@Component
public class PwdTimedTask {
    private Logger logger = LoggerFactory.getLogger(PwdTimedTask.class);

    /**
     * 每天的凌晨1点执行一次
     */
    @XxlJob(JobHandlerName.Timed.PwdValidityWarnJob)
    @WellXxlJob(jobDesc = "密码有效期提前提醒", jobCron = "0 0 1 * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
    public ReturnT<String> pwdValidityWarnJob(String param) throws Exception {
        XxlJobLogger.log("PwdValidityWarnJob execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            ApplicationContextHolder.getBean(UserInfoFacadeService.class).notifyUserAccountPasswordAreAboutToExpired();
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("PwdValidityWarnJob execute end");
        return ReturnT.SUCCESS;
    }

    /**
     * 每分钟执行一次
     */
    @XxlJob(JobHandlerName.Timed.PwdUnlockJob)
    @WellXxlJob(jobDesc = "账号自动解锁定", jobCron = "0 0/1 * * * ?", executorParam = "{\"tenantId\":\"T001\",\"userId\":\"U0000000000\"}", triggerStatus = false)
    public ReturnT<String> pwdUnlockJob(String param) throws Exception {
        XxlJobLogger.log("pwdUnlockJob execute start");
        ExecutionParam executionParam = ParamUtils.toExecutionParam(param);
        try {
            IgnoreLoginUtils.login(executionParam.getTenantId(), executionParam.getUserId());
            ApplicationContextHolder.getBean(UserInfoFacadeService.class).unlockUserAccountHaveReachedUnlockTime();
        } catch (Exception e) {
            XxlJobLogger.log(e);
            throw e;
        } finally {
            IgnoreLoginUtils.logout();
        }
        XxlJobLogger.log("pwdUnlockJob execute end");
        return ReturnT.SUCCESS;
    }
}
