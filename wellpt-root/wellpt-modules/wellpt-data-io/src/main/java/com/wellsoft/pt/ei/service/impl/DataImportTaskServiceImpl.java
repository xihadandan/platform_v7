package com.wellsoft.pt.ei.service.impl;

import com.wellsoft.context.exception.WellException;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dao.DataImportTaskDao;
import com.wellsoft.pt.ei.entity.DataImportRecord;
import com.wellsoft.pt.ei.entity.DataImportTask;
import com.wellsoft.pt.ei.processor.AbstractImportProcessor;
import com.wellsoft.pt.ei.processor.thread.ImportThread;
import com.wellsoft.pt.ei.service.DataImportRecordService;
import com.wellsoft.pt.ei.service.DataImportTaskService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Description: 数据导入任务service实现类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/16.1	liuyz		2021/9/16		Create
 * </pre>
 * @date 2021/9/16
 */
@Service
public class DataImportTaskServiceImpl extends AbstractJpaServiceImpl<DataImportTask, DataImportTaskDao, String> implements DataImportTaskService {

    @Autowired
    private DataImportRecordService dataImportRecordService;

    @Override
    public List<DataImportTask> listByRecordUuid(String recordUuid) {
        DataImportTask importTask = new DataImportTask();
        importTask.setRecordUuid(recordUuid);
        return getDao().listByEntity(importTask);
    }

    @Override
    @Transactional
    public void cancelTaskByUuid(String uuid) {
        ImportThread importThread = AbstractImportProcessor.importThredMap.get(uuid);
        if (null != importThread) {
            importThread.stop();
            try {
                importThread.getThread().join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DataImportTask importTask = getOne(uuid);
        if (importTask == null) {
            throw new WellException("任务不存在");
        }
        //检查任务状态
        if (DataExportConstants.DATA_STATUS_DOING == importTask.getTaskStatus() || DataExportConstants.DATA_STATUS_ERROR == importTask.getTaskStatus()) {
            //设置任务状态为取消
            importTask.setTaskStatus(DataExportConstants.DATA_STATUS_CANCEL);
            importTask.setTaskStatusCn(DataExportConstants.DATA_STATUS_CANCEL_CN);
            this.update(importTask);
            DataImportRecord record = dataImportRecordService.getOne(importTask.getRecordUuid());
            record.setImportStatus(DataExportConstants.DATA_STATUS_CANCEL);
            record.setImportStatusCn(DataExportConstants.DATA_STATUS_CANCEL_CN);
            record.setProcessLog(record.getProcessLog() + DataExportConstants.getImportLogHtml(importTask, DataExportConstants.DATA_STATUS_CANCEL, null));
            dataImportRecordService.save(record);
        }

    }

    @Override
    @Transactional
    public void restartTaskByUuid(String uuid) {
        //检查任务状态
        DataImportTask dataImportTask = getOne(uuid);
        if (dataImportTask == null) {
            throw new WellException("任务不存在");
        }
        if (DataExportConstants.DATA_STATUS_ERROR == dataImportTask.getTaskStatus()) {
            try {
                dataImportTask.setProgress(0);
                dataImportTask.setTaskStatus(DataExportConstants.DATA_STATUS_DOING);
                dataImportTask.setReimportTime(new Date());
                DataImportRecord record = dataImportRecordService.getOne(dataImportTask.getRecordUuid());
                record.setProcessLog(record.getProcessLog() + DataExportConstants.getImportLogHtml(dataImportTask, DataExportConstants.DATA_STATUS_DOING, null));
                dataImportRecordService.save(record);
                this.restartThred(record, dataImportTask);
            } catch (Exception e) {
                //TODO 添加线程池异常，将当前线程数据保存到数据库，等线程池空闲调用
            }
        }
    }

    private void restartThred(DataImportRecord record, DataImportTask task) throws InterruptedException {
        if (AbstractImportProcessor.importThredMap.containsKey(task.getUuid())) {
            ImportThread importThread = AbstractImportProcessor.importThredMap.get(task.getUuid());
            importThread.stop();
            importThread.getThread().join();
        }
        ImportThread importThread = new ImportThread(record, task);
        AbstractImportProcessor.importThredMap.put(task.getUuid(), importThread);
        importThread.start();
    }

    @Override
    public long getExecutingCount() {
        DataImportTask importTask = new DataImportTask();
        importTask.setTaskStatus(DataExportConstants.DATA_STATUS_DOING);
        importTask.setCreator(SpringSecurityUtils.getCurrentUserId());
        return getDao().countByEntity(importTask);
    }
}
