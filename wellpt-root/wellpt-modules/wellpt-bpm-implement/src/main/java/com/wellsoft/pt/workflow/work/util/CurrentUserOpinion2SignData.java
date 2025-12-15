package com.wellsoft.pt.workflow.work.util;

import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.bean.WorkOpinionBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Description: 获取常用和最近使用意见
 * 对应的线程ID:threadId = flowDefUuid + taskId + userId;
 *
 * @author liuxj
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * V1.0 	liuxj		2022/5/13		Create
 * </pre>
 * @date 2022/5/13
 */
public class CurrentUserOpinion2SignData implements Callable<WorkOpinionBean> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private WorkService workService;

    private UserDetails user;

    private WorkBean workBean;

    public CurrentUserOpinion2SignData(WorkBean workBean, WorkService workService, UserDetails user) {
        this.workBean = workBean;
        this.workService = workService;
        this.user = user;
    }

    @Override
    public WorkOpinionBean call() throws Exception {
        try {
            IgnoreLoginUtils.login(user.getTenantId(), user.getUserId());
            String flowDefUuid = workBean.getFlowDefUuid();
            String taskId = workBean.getTaskId();
            WorkOpinionBean workOpinionBean = workService.getCurrentUserOpinion2Sign(flowDefUuid, taskId);
            return workOpinionBean;
        } catch (Exception e) {
            logger.error("CurrentUserOpinion2SignData执行异步任务call中断异常", e);
            throw new ExecutionException(e);
        } finally {
            IgnoreLoginUtils.logout();
        }

    }
}
