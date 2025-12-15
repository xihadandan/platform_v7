package com.wellsoft.pt.workflow.work.util;

import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Description: 获取环节办理信息
 * 对应的线程ID:threadId = taskUuid + flowDefId + userId;
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
public class TaskProcessData implements Callable<Map<String, Map<String, Object>>> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private WorkBean workBean;

    private WorkService workService;

    private UserDetails user;

    public TaskProcessData(WorkBean workBean, WorkService workService, UserDetails user) {
        this.workBean = workBean;
        this.workService = workService;
        this.user = user;
    }

    @Override
    public Map<String, Map<String, Object>> call() throws Exception {
        try {
            IgnoreLoginUtils.login(user.getTenantId(), user.getUserId());
            String flowDefId = workBean.getFlowDefId();
            String taskInstUuid = workBean.getTaskInstUuid();
            Map<String, Map<String, Object>> taskProcesses = workService
                    .getTaskProcessByTaskInstUuidAndFlowDefId(taskInstUuid, flowDefId);
            return taskProcesses;
        } catch (Exception e) {
            logger.error("TaskProcessData执行异步任务call中断异常", e);
            throw new ExecutionException(e);
        } finally {
            IgnoreLoginUtils.logout();
        }

    }
}
