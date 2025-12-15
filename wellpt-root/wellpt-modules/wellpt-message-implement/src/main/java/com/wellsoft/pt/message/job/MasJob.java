package com.wellsoft.pt.message.job;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.message.service.ShortMessageService;
import com.wellsoft.pt.task.job.Job;
import com.wellsoft.pt.task.job.JobData;
import org.quartz.JobExecutionContext;

public class MasJob extends Job {

    @Override
    protected void execute(JobExecutionContext context, JobData jobData) {

        // 一般短信接口
        ShortMessageService shortMessageService = ApplicationContextHolder.getBean(ShortMessageService.class);

        // 统一短信接口
        shortMessageService.doMasJob();

    }
}
