package com.wellsoft.pt.ei.processor;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.entity.DataImportRecord;
import com.wellsoft.pt.ei.entity.DataImportTask;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.ei.processor.thread.ImportThread;
import com.wellsoft.pt.ei.processor.utils.ExpImpServiceBeanUtils;
import com.wellsoft.pt.ei.processor.utils.FieldTypeUtils;
import com.wellsoft.pt.ei.processor.utils.RequiredFile;
import com.wellsoft.pt.ei.service.DataImportRecordService;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.ei.service.DataImportTaskService;
import com.wellsoft.pt.ei.service.ExpImpService;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.rowset.serial.SerialClob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Auther: yt
 * @Date: 2021/9/29 11:51
 * @Description:
 */
public abstract class AbstractImportProcessor {

    public static ConcurrentMap<String, ImportThread> importThredMap = new ConcurrentHashMap<String, ImportThread>();
    @Autowired
    private DataImportTaskLogService dataImportTaskLogService;
    @Autowired
    private DataImportTaskService dataImportTaskService;
    @Autowired
    private DataImportRecordService dataImportRecordService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    protected boolean isStop(String taskUuid) {
        return importThredMap.get(taskUuid).isStop();
    }

    protected void stop(DataImportRecord record, DataImportTask task) {
        task.setTaskStatus(DataExportConstants.DATA_STATUS_ERROR);
        task.setTaskStatusCn(DataExportConstants.DATA_STATUS_ERROR_CN);
        dataImportTaskService.save(task);
        record.setImportStatus(DataExportConstants.DATA_STATUS_ERROR);
        record.setImportStatusCn(DataExportConstants.DATA_STATUS_ERROR_CN);
        dataImportRecordService.save(record);
        importThredMap.get(task.getUuid()).stop();
    }

    /**
     * ExpImpService
     *
     * @return
     */
    public abstract List<ExpImpService> getExportServiceList(DataImportRecord record);


    public void handle(DataImportRecord record, DataImportTask task) {
        try {
            List<ExpImpService> expImpServiceList = this.getExportServiceList(record);

            //读取文件 计算总数
            Map<ExpImpService, JSONArray> impJSONArrayMap = this.getImpJSONArrayMap(record, task, expImpServiceList);

            /**
             * key:导入前 uuid 或 id
             * val:导入后 uuid 或 id
             */
            Map<String, String> dependentDataMap = Maps.newHashMap();

            Map<ExpImpService, List<ImportEntity>> updateMap = Maps.newHashMap();

            //导入数据
            for (ExpImpService expImpService : expImpServiceList) {
                if (this.isStop(task.getUuid())) {
                    break;
                }
                JSONArray jsonArray = impJSONArrayMap.get(expImpService);
                List<ImportEntity> importEntityList = this.jsonArrayToData(expImpService, record, task, jsonArray, dependentDataMap);
                if (importEntityList.size() > 0) {
                    List<ImportEntity> objects = updateMap.get(expImpService);
                    if (objects == null) {
                        objects = Lists.newArrayList();
                        updateMap.put(expImpService, objects);
                    }
                    objects.addAll(importEntityList);
                }
            }

            //导入完成 处理依赖数据
            for (ExpImpService expImpService : updateMap.keySet()) {
                if (this.isStop(task.getUuid())) {
                    break;
                }
                List<ImportEntity> importEntityList = updateMap.get(expImpService);
                for (ImportEntity importEntity : importEntityList) {
                    if (this.isStop(task.getUuid())) {
                        break;
                    }
                    expImpService.update(importEntity.getObj(), importEntity.getSorce(), dependentDataMap);
                    DataImportTaskLog log = this.createDataImportTaskLog(expImpService, task.getUuid(), importEntity.getSorce(), importEntity.getSorceJson());
                    log.setAfterImportId(expImpService.getId(importEntity.getObj()));
                    log.setAfterImportUuid(expImpService.getUuid(importEntity.getObj()));
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
                    log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_NORMAL_CN);
                    this.saveLog(log, task);
                }
            }
            if (!this.isStop(task.getUuid())) {
                task.setTaskStatus(DataExportConstants.DATA_STATUS_FINISH);
                task.setTaskStatusCn(DataExportConstants.DATA_STATUS_FINISH_CN);
                task.setFinishTime(new Date());
                dataImportTaskService.save(task);
                record.setImportStatus(DataExportConstants.DATA_STATUS_FINISH);
                record.setImportStatusCn(DataExportConstants.DATA_STATUS_FINISH_CN);
                record.setProcessLog(record.getProcessLog() + DataExportConstants.getImportLogHtml(task, DataExportConstants.DATA_STATUS_FINISH, null));
                dataImportRecordService.save(record);
            }
        } catch (Exception e) {
            task.setTaskStatus(DataExportConstants.DATA_STATUS_ERROR);
            task.setTaskStatusCn(DataExportConstants.DATA_STATUS_ERROR_CN);
            dataImportTaskService.save(task);
            record.setImportStatus(DataExportConstants.DATA_STATUS_ERROR);
            record.setImportStatusCn(DataExportConstants.DATA_STATUS_ERROR_CN);
            record.setProcessLog(record.getProcessLog() + DataExportConstants.getImportLogHtml(task, DataExportConstants.DATA_STATUS_ERROR, null));
            dataImportRecordService.save(record);
            e.printStackTrace();
        } finally {
            importThredMap.remove(task.getUuid());
            // 发送消息，在页面右下角弹出提示信息
            sendMessage(task);
        }
    }

    protected void sendMessage(DataImportTask dataImportTask) {
        try {
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, dataImportTask.getCreator());

            Message msg = new Message();

            String dataType = dataImportTask.getDataType();
            if (DataExportConstants.DATA_STATUS_ERROR.equals(dataImportTask.getTaskStatus())) {
                DataImportTaskLog log = new DataImportTaskLog();
                log.setTaskUuid(dataImportTask.getUuid());
                List<DataImportTaskLog> list = dataImportTaskLogService.listAllByPage(log, new PagingInfo(1, 10), "createTime desc");
                if (CollectionUtils.isNotEmpty(list)) {
                    msg.setBody(dataType + "的" + DataExportConstants.IMPORT + "因" + list.get(0).getErrorMsg() + DataExportConstants.TERMINATED);
                } else {
                    msg.setBody(dataType + "的" + DataExportConstants.IMPORT + "因程序问题" + DataExportConstants.TERMINATED);
                }
                msg.setSubject(DataExportConstants.DATA_IMPORT_ERROR);
            } else if (DataExportConstants.DATA_STATUS_FINISH.equals(dataImportTask.getTaskStatus())) {
                msg.setSubject(DataExportConstants.DATA_IMPORT_SUCCESS);
                msg.setBody(dataType + "已成功" + DataExportConstants.IMPORT);
            }

            Map<Object, Object> dytableMap = Maps.newHashMap();
            DataImportRecord record = dataImportRecordService.getOne(dataImportTask.getRecordUuid());
            msg.setForwardDataUuid(record.getUuid());
//            msg.setRelatedUrl("page/preview/56111a4071fa57436e77120e3d55eb67?pageUuid=34425c4e-0773-4be9-8081-98c53320447e&uuid=" + record.getUuid());
            messageClientApiFacade.send(MessageTemplate.DATA_IMPORT_MESSAGE, msg, dytableMap, Lists.newArrayList(SpringSecurityUtils.getCurrentUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    private Map<ExpImpService, JSONArray> getImpJSONArrayMap(DataImportRecord record, DataImportTask task, List<ExpImpService> expImpServiceList) throws IOException, SQLException {
        Map<ExpImpService, JSONArray> impJSONArrayMap = Maps.newHashMap();
        List<String> fileNams = Lists.newArrayList();
        int total = 0;
        for (ExpImpService expImpService : expImpServiceList) {
            String importPath = record.getImportPath() + File.separator + expImpService.filePath();
            File importPathFile = new File(importPath);
            File[] jsonFiles = importPathFile.listFiles();
            List<File> files = Lists.newArrayList();
            for (File jsonFile : jsonFiles) {
                if (jsonFile.isDirectory()) {
                    continue;
                }
                if (jsonFile.getName().indexOf(ExpImpServiceBeanUtils.configName) > -1) {
                    continue;
                }
                if (jsonFile.getName().indexOf(expImpService.fileName()) > -1) {
                    String number = jsonFile.getName().replace(expImpService.fileName(), "").replace(".json", "");
                    if (StringUtils.isBlank(number) || Ints.tryParse(number) != null) {
                        files.add(jsonFile);
                    }
                }
            }
            //文件排序
            if (files.size() > 1) {
                files.sort(new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        String a1 = o1.getName().replace(expImpService.fileName(), "").replace(".json", "");
                        String a2 = o2.getName().replace(expImpService.fileName(), "").replace(".json", "");
                        return Integer.valueOf(a1) - Integer.valueOf(a2);
                    }
                });
            }
            JSONArray jsonArray = new JSONArray();
            for (File file : files) {
                String jsonStr = null;
                try {
                    jsonStr = FileUtils.readFileToString(file, "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                jsonArray.addAll(JSONArray.parseArray(jsonStr));
                fileNams.add(file.getName());
            }
            impJSONArrayMap.put(expImpService, jsonArray);
            total = total + jsonArray.size();
        }

        record.setImportFiles(new SerialClob(
                IOUtils.toCharArray(IOUtils.toInputStream(StringUtils.join(fileNams, Separator.COMMA.getValue())))));
        dataImportRecordService.save(record);
        task.setImportFiles(record.getImportFiles());
        task.setDataTotal(total);
        dataImportTaskService.save(task);

        return impJSONArrayMap;
    }

    protected List<ImportEntity> jsonArrayToData(ExpImpService expImpService, DataImportRecord record, DataImportTask task, JSONArray jsonArray, Map<String, String> dependentDataMap) {
        List<ImportEntity> importEntityList = Lists.newArrayList();
        String systemUnitId = record.getImportUnitId();
        String attachFilePath = record.getImportPath() + File.separator + expImpService.filePath() + File.separator + expImpService.filePath() + "_附件";
        for (int i = 0; i < jsonArray.size(); i++) {
            if (this.isStop(task.getUuid())) {
                break;
            }
            Object data = null;
            // 数据类型校验
            try {
                data = jsonArray.getObject(i, expImpService.dataClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
            String dataJson = jsonArray.getJSONObject(i).toJSONString();
            if (data == null) {
                DataImportTaskLog log = new DataImportTaskLog();
                log.setDataType(expImpService.filePath());
                log.setDataChildType(expImpService.dataChildType());
                log.setImportTime(new Date());
                log.setTaskUuid(task.getUuid());
                log.setSourceData(dataJson);

                log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);
                log.setErrorMsg(DataExportConstants.DATA_TYPE_NO_MATCH);
                this.saveLog(log, task);
                // 异常策略：跳过
                if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                    continue;
                } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                    this.stop(record, task);
                    record.setProcessLog(record.getProcessLog() + DataExportConstants.getImportLogHtml(task, DataExportConstants.DATA_STATUS_ERROR, null));
                    dataImportRecordService.save(record);
                    break;
                }
            }


            DataImportTaskLog log = this.createDataImportTaskLog(expImpService, task.getUuid(), data, dataJson);
            // 重复校验
            DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(expImpService.getDataUuid(data));
            boolean replace = false;
            if (null != dataImportTaskLog) {
                log.setIsRepeat(1);
                //跳过
                if (DataExportConstants.DATA_REPEAT_STRATEGY_SKIP.intValue() == record.getRepeatStrategy().intValue()) {
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
                    log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_NORMAL_CN);
                    this.saveLog(log, task);
                    continue;
                } else {
                    //替换
                    replace = true;
                }
            }

            // 必填校验
            RequiredFile requiredFile = null;
            try {
                requiredFile = FieldTypeUtils.getRequiredFile(expImpService.getDataUuid(data), data);
                if (requiredFile.getRequiredNo().size() > 0) {
                    // 异常的数据，无论终止还是跳过，导入状态都为失败
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                    log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);
                    log.setErrorMsg("数据必填项缺失");
                    this.saveLog(log, task);
                    // 异常策略：跳过
                    if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                        continue;
                    } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                        this.stop(record, task);
                        record.setProcessLog(record.getProcessLog() + DataExportConstants.getImportLogHtml(task, DataExportConstants.DATA_STATUS_ERROR, null));
                        dataImportRecordService.save(record);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 异常的数据，无论终止还是跳过，导入状态都为失败
                log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);
                log.setErrorMsg("必填校验出错");
                this.saveLog(log, task);
                // 异常策略：跳过
                if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                    continue;
                } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                    this.stop(record, task);
                    record.setProcessLog(record.getProcessLog() + DataExportConstants.getImportLogHtml(task, DataExportConstants.DATA_STATUS_ERROR, null));
                    dataImportRecordService.save(record);
                    break;
                }
            }

            //文件处理
            try {
                this.saveFile(requiredFile.getAttachFileMap(), attachFilePath);
            } catch (Exception e) {
                e.printStackTrace();
                // 异常的数据，无论终止还是跳过，导入状态都为失败
                log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);
                log.setErrorMsg("文件处理错误");
                this.saveLog(log, task);
                // 异常策略：跳过
                if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                    continue;
                } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                    this.stop(record, task);
                    record.setProcessLog(record.getProcessLog() + DataExportConstants.getImportLogHtml(task, DataExportConstants.DATA_STATUS_ERROR, "文件处理错误"));
                    dataImportRecordService.save(record);
                    break;
                }
            }


            try {
                //转换保存数据
                ImportEntity importEntity = expImpService.save(data, systemUnitId, replace, dependentDataMap);
                if (importEntity.isPostProcess()) {
                    importEntity.setSorceJson(dataJson);
                    importEntityList.add(importEntity);
                } else {
                    log.setAfterImportId(expImpService.getId(importEntity.getObj()));
                    log.setAfterImportUuid(expImpService.getUuid(importEntity.getObj()));
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
                    log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_NORMAL_CN);
                    this.saveLog(log, task);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 异常的数据，无论终止还是跳过，导入状态都为失败
                log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);
                log.setErrorMsg(e.getMessage());
                this.saveLog(log, task);
                // 异常策略：跳过
                if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                    continue;
                } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                    this.stop(record, task);
                    record.setProcessLog(record.getProcessLog() + DataExportConstants.getImportLogHtml(task, DataExportConstants.DATA_STATUS_ERROR, null));
                    dataImportRecordService.save(record);
                    break;
                }
            }
        }
        return importEntityList;
    }


    protected void saveFile(Map<String, List<String>> attachFileMap, String attachFilePath) throws FileNotFoundException {
        if (attachFileMap.size() > 0) {
            for (String key : attachFileMap.keySet()) {
                List<String> fileList = attachFileMap.get(key);
                for (int i = 0; i < fileList.size(); i++) {
                    String fileName = fileList.get(i);
                    File file = new File(attachFilePath + File.separator + fileName);
                    FileInputStream inputStream = new FileInputStream(file);
                    MongoFileEntity mongoFileEntity = mongoFileService.saveFile(file.getName(), inputStream);
                    fileList.set(i, mongoFileEntity.getFileID());
                }
            }
        }
    }

    protected void saveLog(DataImportTaskLog log, DataImportTask task) {
        dataImportTaskLogService.save(log);

        DataImportRecord record = dataImportRecordService.getOne(task.getRecordUuid());

        // 异常终止并且状态为异常的任务不计数
        if (!(DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue() && DataExportConstants.DATA_LOG_STATUS_ERROR.intValue() == log.getImportStatus().intValue())) {
            task.setProgress(task.getProgress() + 1);
        }
        dataImportTaskService.update(task);
    }


    protected DataImportTaskLog createDataImportTaskLog(ExpImpService expImpService,
                                                        String taskUuid, Object data, String json) {
        String sourceUuid = null;
        String sourceId = null;
        if (data != null) {
            sourceUuid = expImpService.getDataUuid(data);
            sourceId = expImpService.getDataId(data);
        }
        DataImportTaskLog dataImportTaskLog = new DataImportTaskLog();
        dataImportTaskLog.setDataType(expImpService.filePath());
        dataImportTaskLog.setDataChildType(expImpService.dataChildType());
        dataImportTaskLog.setImportTime(new Date());
        dataImportTaskLog.setTaskUuid(taskUuid);
        dataImportTaskLog.setSourceUuid(sourceUuid);
        dataImportTaskLog.setSourceId(sourceId);
        dataImportTaskLog.setSourceData(json);
        return dataImportTaskLog;
    }


}
