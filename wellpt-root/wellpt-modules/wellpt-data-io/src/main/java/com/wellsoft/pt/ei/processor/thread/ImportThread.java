package com.wellsoft.pt.ei.processor.thread;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.entity.DataImportRecord;
import com.wellsoft.pt.ei.entity.DataImportTask;
import com.wellsoft.pt.ei.processor.AbstractImportProcessor;
import com.wellsoft.pt.ei.processor.FlowImportProcessor;
import com.wellsoft.pt.ei.processor.MailImportProcessor;
import com.wellsoft.pt.ei.processor.OrgImportProcessor;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;

/**
 * @Auther: yt
 * @Date: 2021/10/11 10:49
 * @Description:
 */
public class ImportThread {


    private DataImportRecord record;
    private DataImportTask task;
    private Thread thread;
    private volatile boolean toStop = false;
    public ImportThread(DataImportRecord record, DataImportTask task) {
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
                        OrgImportProcessor processor = ApplicationContextHolder.getBean(OrgImportProcessor.class);
                        processor.handle(record, task);
                    } else if (task.getDataType().equals(DataExportConstants.DATA_TYPE_MAIL)) {
                        MailImportProcessor processor = ApplicationContextHolder.getBean(MailImportProcessor.class);
                        processor.handle(record, task);
                    } else if (task.getDataType().equals(DataExportConstants.DATA_TYPE_FLOW)) {
                        FlowImportProcessor processor = ApplicationContextHolder.getBean(FlowImportProcessor.class);
                        processor.handle(record, task);
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
        AbstractImportProcessor.importThredMap.put(task.getUuid(), this);
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
