/*
 * @(#)2015年7月24日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.aop;

import com.wellsoft.pt.workflow.gz.facade.GzWorkDataSyncAspectService;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年7月24日.1	zhulh		2015年7月24日		Create
 * </pre>
 * @date 2015年7月24日
 */
@Aspect
public class GzWorkDataSyncAspect implements Ordered {

    @Autowired
    private GzWorkDataSyncAspectService gzWorkDataSyncAspectService;

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 10;
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl.delete(..)) && args(taskInstUuids)")
    public void beforeDelete(JoinPoint jp, Collection<String> taskInstUuids) {
        for (String taskInstUuid : taskInstUuids) {
            gzWorkDataSyncAspectService.checked(taskInstUuid);
        }
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl.deleteByAdmin(..)) && args(taskInstUuids)")
    public void beforeDeleteByAdmin(JoinPoint jp, Collection<String> taskInstUuids) {
        for (String taskInstUuid : taskInstUuids) {
            gzWorkDataSyncAspectService.checked(taskInstUuid);
        }
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl.submit(..)) && args(workBean)")
    public void beforeSubmit(JoinPoint jp, WorkBean workBean) {
        gzWorkDataSyncAspectService.checked(workBean.getTaskInstUuid());
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl.rollback(..)) && args(workBean)")
    public void beforeRollback(JoinPoint jp, WorkBean workBean) {
        gzWorkDataSyncAspectService.checked(workBean.getTaskInstUuid());
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl.rollbackWithWorkData(..)) && args(workBean)")
    public void beforeRollbackWithWorkData(JoinPoint jp, WorkBean workBean) {
        gzWorkDataSyncAspectService.checked(workBean.getTaskInstUuid());
    }

    @SuppressWarnings("unchecked")
    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl.counterSign(..))")
    public void beforeCounterSign(JoinPoint jp) {
        // Collection<String> taskInstUuids, List<String> userIds, String opinionName, String opinionValue, String opinionText
        Collection<String> taskInstUuids = (Collection<String>) jp.getArgs()[0];
        for (String taskInstUuid : taskInstUuids) {
            gzWorkDataSyncAspectService.checked(taskInstUuid);
        }
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl.counterSignWithWorkData(..))")
    public void beforeCounterSignWithWorkData(JoinPoint jp) {
        // WorkBean workBean, List<String> userIds
        WorkBean workBean = (WorkBean) jp.getArgs()[0];
        gzWorkDataSyncAspectService.checked(workBean.getTaskInstUuid());
    }

    @SuppressWarnings("unchecked")
    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl.transfer(..))")
    public void beforeTransfer(JoinPoint jp) {
        // Collection<String> taskInstUuids, List<String> userIds, String opinionName, String opinionValue, String opinionText
        Collection<String> taskInstUuids = (Collection<String>) jp.getArgs()[0];
        for (String taskInstUuid : taskInstUuids) {
            gzWorkDataSyncAspectService.checked(taskInstUuid);
        }
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl.transferWithWorkData(..))")
    public void beforeTransferWithWorkData(JoinPoint jp) {
        // WorkBean workBean, List<String> userIds
        WorkBean workBean = (WorkBean) jp.getArgs()[0];
        gzWorkDataSyncAspectService.checked(workBean.getTaskInstUuid());
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl.remind(..)) && args(taskInstUuids)")
    public void beforeRemind(JoinPoint jp, Collection<String> taskInstUuids) {
        for (String taskInstUuid : taskInstUuids) {
            gzWorkDataSyncAspectService.checked(taskInstUuid);
        }
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.WorkServiceImpl.remind(..)) && args(workBean)")
    public void beforeGotoTask(JoinPoint jp, WorkBean workBean) {
        gzWorkDataSyncAspectService.checked(workBean.getTaskInstUuid());
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.ListWorkServiceImpl.submit(..))")
    public void beforeListSubmit(JoinPoint jp) {
        // String taskInstUuid, String opinionName, String opinionValue, String opinionText
        String taskInstUuid = (String) jp.getArgs()[0];
        gzWorkDataSyncAspectService.checked(taskInstUuid);
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.ListWorkServiceImpl.gotoApprove(..)) && args(taskInstUuid)")
    public void beforeListSubmit(JoinPoint jp, String taskInstUuid) {
        gzWorkDataSyncAspectService.checked(taskInstUuid);
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.ListWorkServiceImpl.gotoTask(..))")
    public void beforeListGotoTask(JoinPoint jp) {
        // String taskInstUuid, String gotoTaskId, List<String> taskUsers
        String taskInstUuid = (String) jp.getArgs()[0];
        gzWorkDataSyncAspectService.checked(taskInstUuid);
    }

    @Before("execution(* com.wellsoft.pt.workflow.work.service.impl.ListWorkServiceImpl.remind(..)) && args(taskInstUuids)")
    public void beforeListRemind(JoinPoint jp, Collection<String> taskInstUuids) {
        for (String taskInstUuid : taskInstUuids) {
            gzWorkDataSyncAspectService.checked(taskInstUuid);
        }
    }

}
