package com.wellsoft.pt.workflow.work.util;

import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Description: 获取流程工作数据
 * 对应的线程ID: threadId = taskUuid + flowInstUuid + userId;
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
public class WorkData implements Callable<WorkBean> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private WorkBean workBean;

    private WorkService workService;

    private UserDetails user;

    public WorkData(WorkBean workBean, WorkService workService, UserDetails user) {
        this.workBean = workBean;
        this.workService = workService;
        this.user = user;
    }

    @Override
    public WorkBean call() throws Exception {
        try {
            IgnoreLoginUtils.login(user.getTenantId(), user.getUserId());
            workBean = workService.getWorkData(workBean);
            workBean.getDyFormData().reassignFormDefinition();
            return workBean;
        } catch (Exception e) {
            logger.error("WorkData执行异步任务call中断异常", e);
            throw new ExecutionException(e);
        } finally {
            IgnoreLoginUtils.logout();
        }
    }
}
