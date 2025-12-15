package com.wellsoft.pt.ei.processor.thread;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.entity.DataExportRecord;
import com.wellsoft.pt.ei.entity.DataExportTask;
import com.wellsoft.pt.ei.processor.AbstractExportProcessor;
import com.wellsoft.pt.ei.processor.FlowExportProcessor;
import com.wellsoft.pt.ei.processor.MailExportProcessor;
import com.wellsoft.pt.ei.processor.OrgExportProcessor;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: yt
 * @Date: 2021/10/11 10:49
 * @Description:
 */
public class ExportThread {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private DataExportRecord record;
    private DataExportTask task;
    private Thread thread;
    private volatile boolean toStop = false;
    public ExportThread(DataExportRecord record, DataExportTask task) {
        this.record = record;
        this.task = task;
    }

    public void start() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    IgnoreLoginUtils.login(Config.DEFAULT_TENANT, record.getCreator());
                    if (task.getDataType().equals(DataExportConstants.DATA_TYPE_ORG)) {
                        OrgExportProcessor orgExportProcessor = ApplicationContextHolder.getBean(OrgExportProcessor.class);
                        orgExportProcessor.handle(record, task);
                    } else if (task.getDataType().equals(DataExportConstants.DATA_TYPE_MAIL)) {
                        MailExportProcessor mailExportProcessor = ApplicationContextHolder.getBean(MailExportProcessor.class);
                        mailExportProcessor.handle(record, task);
                    } else if (task.getDataType().equals(DataExportConstants.DATA_TYPE_FLOW)) {
                        FlowExportProcessor flowExportProcessor = ApplicationContextHolder.getBean(FlowExportProcessor.class);
                        flowExportProcessor.handle(record, task);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IgnoreLoginUtils.logout();
                }

            }
        });
        thread.setDaemon(true);
        thread.setName(task.getUuid());
        thread.start();
        AbstractExportProcessor.exportThreadMap.put(task.getUuid(), this);
    }

    public Thread getThread() {
        return thread;
    }


    public boolean isStop() {
        return toStop;
    }

    public void stop() {
        toStop = true;
    }


}
