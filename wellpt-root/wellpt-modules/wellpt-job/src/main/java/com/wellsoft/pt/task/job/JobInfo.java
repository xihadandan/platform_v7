/*
 * @(#)2012-11-8 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.job;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-8.1	zhulh		2012-11-8		Create
 * </pre>
 * @date 2012-11-8
 */
public class JobInfo implements Serializable {
    private static final long serialVersionUID = -2671929404343752757L;

    private JobDetail jobDetail;

    private Trigger trigger;

    /**
     * @return the jobDetail
     */
    public JobDetail getJobDetail() {
        return jobDetail;
    }

    /**
     * @param jobDetail 要设置的jobDetail
     */
    public void setJobDetail(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

    /**
     * @return the trigger
     */
    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * @param trigger 要设置的trigger
     */
    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

}
