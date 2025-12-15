package com.wellsoft.pt.ei.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.WellException;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.constants.ExportCheckPathEnum;
import com.wellsoft.pt.ei.dao.DataExportTaskDao;
import com.wellsoft.pt.ei.entity.DataExportRecord;
import com.wellsoft.pt.ei.entity.DataExportTask;
import com.wellsoft.pt.ei.processor.AbstractExportProcessor;
import com.wellsoft.pt.ei.processor.thread.ExportThread;
import com.wellsoft.pt.ei.service.DataExportRecordService;
import com.wellsoft.pt.ei.service.DataExportTaskService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Description: 数据导出任务service实现类
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
public class DataExportTaskServiceImpl extends AbstractJpaServiceImpl<DataExportTask, DataExportTaskDao, String> implements DataExportTaskService {


    @Autowired
    DataExportRecordService dataExportRecordService;


    @Override
    @Transactional
    public List<DataExportTask> saveAll(DataExportRecord record) {
        List<DataExportTask> taskList = Lists.newArrayList();
        // 根据导出数据记录record进行组织数据导出
        String dataType = record.getDataType();
        if (StringUtils.isBlank(dataType)) {
            return taskList;
        }
        List<String> dataTypeList = Arrays.asList(dataType.split(Separator.COMMA.getValue()));
        for (String dataTypeStr : dataTypeList) {
            DataExportTask task = new DataExportTask();
            task.setDataType(dataTypeStr);
            task.setSystemUnitIds(record.getSystemUnitIds());
            task.setSystemUnitNames(record.getSystemUnitNames());
            task.setExportPath(record.getExportPath());
            task.setTaskStatus(DataExportConstants.DATA_STATUS_DOING);
            task.setOperator(SpringSecurityUtils.getCurrentUserName());
            task.setExportTime(record.getExportTime());
            task.setReexportTime(record.getExportTime());
            task.setRecordUuid(record.getUuid());
            task.setProgress(0);
            this.save(task);
            taskList.add(task);
            record.setProcessLog((StringUtils.isBlank(record.getProcessLog()) ? "" : record.getProcessLog()) + DataExportConstants.getExportLogHtml(task, DataExportConstants.DATA_STATUS_DOING, null));
            dataExportRecordService.save(record);
        }
        return taskList;
    }

    /**
     * 导出路径校验
     *
     * @param path
     * @return 校验结果
     * @author baozh
     * @date 2021/9/18 17:19
     */
    @Override
    public ExportCheckPathEnum checkPath(String path) {
        if (StringUtils.isBlank(path)) {
            return ExportCheckPathEnum.ISNULL;
        }
        String FILE_SEPARATOR = System.getProperties().getProperty("file.separator");
        String[] invalid = new String[]{">", ":", "/", "\\", "|", "?", "*", "~", ".", "\""};
        if (Arrays.toString(invalid).contains(path)) {
            return ExportCheckPathEnum.INVALID;
        }
        String[] invalid_char = null;
        File[] roots = File.listRoots();
        if ("\\".equals(FILE_SEPARATOR)) {//window系统
            invalid_char = new String[]{">", "|", "?", "*", "~", "\"", " "};
            //验证盘符
            boolean flag = false;
            for (File file : roots) {
                if (path.replaceAll("/", "\\\\").startsWith(file.getAbsolutePath()) || file.getAbsolutePath().replace("\\", "").equals(path)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return ExportCheckPathEnum.NOT_EXIST;
            }
        } else {//Linux系统
            invalid_char = new String[]{">", ":", "|", "?", "*", "~", "\"", " "};
            if (!path.startsWith(roots[0].getAbsolutePath())) {
                return ExportCheckPathEnum.NOT_EXIST;
            }
        }
        for (String s : invalid_char) {
            if (path.indexOf(s) != -1) {
                return ExportCheckPathEnum.INVALID;
            }
        }

        File file = new File(path + FILE_SEPARATOR);
        if (!file.exists()) {
            boolean flag = file.mkdirs();
            if (!flag) {
                return ExportCheckPathEnum.PERMISSION_DENIED;
            }
        }
        ExportCheckPathEnum ok = ExportCheckPathEnum.OK;
        ok.setMessage(file.getAbsolutePath());
        return ok;
    }

    @Override
    public List<DataExportTask> listByRecordUuid(String recordUuid) {
        DataExportTask task = new DataExportTask();
        task.setRecordUuid(recordUuid);
        return listByEntity(task);
    }

    @Override
    @Transactional
    public void cancelTaskByUuid(String uuid) {
        ExportThread exportThread = AbstractExportProcessor.exportThreadMap.get(uuid);
        if (null != exportThread) {
            exportThread.stop();
            try {
                exportThread.getThread().join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DataExportTask dataExportTask = getOne(uuid);
        if (dataExportTask == null) {
            throw new WellException("任务不存在");
        }
        if (DataExportConstants.DATA_STATUS_DOING == dataExportTask.getTaskStatus() || DataExportConstants.DATA_STATUS_ERROR == dataExportTask.getTaskStatus()) {
            //设置任务状态为取消
            dataExportTask.setTaskStatus(DataExportConstants.DATA_STATUS_CANCEL);
            dataExportTask.setTaskStatusCn(DataExportConstants.DATA_STATUS_CANCEL_CN);
            this.update(dataExportTask);
            DataExportRecord record = dataExportRecordService.getOne(dataExportTask.getRecordUuid());
            record.setProcessLog(record.getProcessLog() + DataExportConstants.getExportLogHtml(dataExportTask, DataExportConstants.DATA_STATUS_CANCEL, null));
            dataExportRecordService.save(record);
        }

    }

    @Override
    @Transactional
    public void restartTaskByUuid(String uuid) {
        //检查任务状态
        DataExportTask dataExportTask = getOne(uuid);
        if (dataExportTask == null) {
            throw new WellException("任务不存在");
        }
        if (DataExportConstants.DATA_STATUS_ERROR == dataExportTask.getTaskStatus()) {
            try {
                dataExportTask.setProgress(0);
                dataExportTask.setTaskStatus(DataExportConstants.DATA_STATUS_DOING);
                dataExportTask.setReexportTime(new Date());
                DataExportRecord dataExportRecord = dataExportRecordService.getOne(dataExportTask.getRecordUuid());
                dataExportRecord.setProcessLog(dataExportRecord.getProcessLog() + DataExportConstants.getExportLogHtml(dataExportTask, DataExportConstants.DATA_STATUS_DOING, null));
                dataExportRecordService.save(dataExportRecord);
                this.restartThred(dataExportRecord, dataExportTask);
            } catch (Exception e) {
                //TODO 添加线程池异常，将当前线程数据保存到数据库，等线程池空闲调用
            }
        }
    }

    private void restartThred(DataExportRecord record, DataExportTask task) throws InterruptedException {
        if (AbstractExportProcessor.exportThreadMap.containsKey(task.getUuid())) {
            ExportThread exportThread = AbstractExportProcessor.exportThreadMap.get(task.getUuid());
            exportThread.stop();
            exportThread.getThread().join();
        }
        ExportThread exportThread = new ExportThread(record, task);
        AbstractExportProcessor.exportThreadMap.put(task.getUuid(), exportThread);
        exportThread.start();
    }

    @Override
    public long getExecutingCount() {
        DataExportTask exportTask = new DataExportTask();
        exportTask.setTaskStatus(DataExportConstants.DATA_STATUS_DOING);
        exportTask.setCreator(SpringSecurityUtils.getCurrentUserId());
        return getDao().countByEntity(exportTask);
    }


}
