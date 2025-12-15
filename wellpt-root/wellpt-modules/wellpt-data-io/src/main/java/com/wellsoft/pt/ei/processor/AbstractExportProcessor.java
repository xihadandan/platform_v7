package com.wellsoft.pt.ei.processor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.file.FileUtils;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.entity.DataExportRecord;
import com.wellsoft.pt.ei.entity.DataExportTask;
import com.wellsoft.pt.ei.entity.DataExportTaskLog;
import com.wellsoft.pt.ei.processor.thread.ExportThread;
import com.wellsoft.pt.ei.processor.utils.*;
import com.wellsoft.pt.ei.service.DataExportRecordService;
import com.wellsoft.pt.ei.service.DataExportTaskLogService;
import com.wellsoft.pt.ei.service.DataExportTaskService;
import com.wellsoft.pt.ei.service.ExpImpService;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Auther: yt
 * @Date: 2021/9/29 11:09
 * @Description:
 */
public abstract class AbstractExportProcessor {

    public static ConcurrentMap<String, ExportThread> exportThreadMap = new ConcurrentHashMap<String, ExportThread>();
    @Autowired
    protected DataExportRecordService dataExportRecordService;
    @Autowired
    protected DataExportTaskService dataExportTaskService;
    @Autowired
    protected DataExportTaskLogService dataExportTaskLogService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    /**
     * ExpImpService
     *
     * @return
     */
    public abstract List<ExpImpService> getExportServiceList(DataExportRecord record);

    protected boolean isStop(String taskUuid) {
        return exportThreadMap.get(taskUuid).isStop();
    }

    public void handle(DataExportRecord record, DataExportTask task) {
        try {
            List<ExpImpService> expImpServices = this.getExportServiceList(record);
            List<String> systemUnitIdList = Arrays.asList(record.getSystemUnitIds().split(Separator.COMMA.getValue()));
            long dataTotal = this.dataTotal(expImpServices, systemUnitIdList);
            task.setDataTotal(Integer.valueOf(dataTotal + ""));
            dataExportTaskService.update(task);

            List<String> systemUnitNameList = Arrays.asList(record.getSystemUnitNames().split(Separator.COMMA.getValue()));
            for (int i = 0; i < systemUnitIdList.size(); i++) {
                if (this.isStop(task.getUuid())) {
                    break;
                }
                // 系统单位目录，命名规则：${系统单位名称}_${导出时间(年月日时分秒)}
                String systemUnitName = systemUnitNameList.get(i);
                String systemUnitId = systemUnitIdList.get(i);
                String systemUnitFilePath = this.getSystemUnitPath(record.getExportPath(), systemUnitName, record.getExportTime());
                //清理所有文件，重新导出
                FileUtils.deleteQuietly(new File(systemUnitFilePath));
                for (ExpImpService expImpService : expImpServices) {
                    if (this.isStop(task.getUuid())) {
                        break;
                    }
                    int total = Integer.valueOf(expImpService.total(systemUnitId) + "");
                    if (total == 0) {
                        continue;
                    }
                    Map<String, List<FieldDesc>> fieldDescMap = Maps.newHashMap();
                    //生成数据文件
                    // 数据总数大于批次数量时，分批
                    if (null != record.getBatchQuantity() && total > record.getBatchQuantity()) {
                        int pageSize = record.getBatchQuantity();
                        int batchNo = total / pageSize;
                        if (total % pageSize != 0) {
                            batchNo = batchNo + 1;
                        }
                        for (int j = 1; j <= batchNo; j++) {
                            if (this.isStop(task.getUuid())) {
                                break;
                            }
                            this.dataToFile(fieldDescMap, expImpService, systemUnitFilePath, systemUnitId, task, j, pageSize);
                        }
                    } else {
                        this.dataToFile(fieldDescMap, expImpService, systemUnitFilePath, systemUnitId, task, null, null);
                    }
                    //生成说明文件
                    this.configToFile(fieldDescMap, expImpService, systemUnitFilePath);
                }
            }
            if (!this.isStop(task.getUuid())) {
                task.setTaskStatus(DataExportConstants.DATA_STATUS_FINISH);
                task.setTaskStatusCn(DataExportConstants.DATA_STATUS_FINISH_CN);
                task.setFinishTime(new Date());
                dataExportTaskService.save(task);
                record = dataExportRecordService.getOne(record.getUuid());// 重新获取最新数据，避免报update by another transaction 错误
                record.setProcessLog(record.getProcessLog() + DataExportConstants.getExportLogHtml(task, DataExportConstants.DATA_STATUS_FINISH, null));
                dataExportRecordService.save(record);
            }
        } catch (Exception e) {
            task.setTaskStatus(DataExportConstants.DATA_STATUS_ERROR);
            task.setTaskStatusCn(DataExportConstants.DATA_STATUS_ERROR_CN);
            dataExportTaskService.save(task);
            record = dataExportRecordService.getOne(record.getUuid());// 重新获取最新数据，避免报update by another transaction 错误
            record.setProcessLog(record.getProcessLog() + DataExportConstants.getExportLogHtml(task, DataExportConstants.DATA_STATUS_ERROR, null));
            dataExportRecordService.save(record);
            e.printStackTrace();
        } finally {
            exportThreadMap.remove(task.getUuid());
            // 发送消息，在页面右下角弹出提示信息
            sendMessage(task);
        }
    }

    protected void sendMessage(DataExportTask dataExportTask) {
        try {
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, dataExportTask.getCreator());

            Message msg = new Message();
            String dataType = dataExportTask.getDataType();
            if (DataExportConstants.DATA_STATUS_ERROR.equals(dataExportTask.getTaskStatus())) {
                DataExportTaskLog log = new DataExportTaskLog();
                log.setTaskUuid(dataExportTask.getUuid());
                List<DataExportTaskLog> list = dataExportTaskLogService.listAllByPage(log, new PagingInfo(1, 10), "createTime desc");
                if (CollectionUtils.isNotEmpty(list)) {
                    msg.setBody(dataType + "的" + DataExportConstants.EXPORT + "因" + list.get(0).getErrorMsg() + DataExportConstants.TERMINATED + "。");
                } else {
                    msg.setBody(dataType + "的" + DataExportConstants.EXPORT + "因程序问题" + DataExportConstants.TERMINATED + "。");
                }
                msg.setSubject(DataExportConstants.DATA_EXPOPT_ERROR);
            } else if (DataExportConstants.DATA_STATUS_FINISH.equals(dataExportTask.getTaskStatus())) {
                msg.setSubject(DataExportConstants.DATA_EXPORT_SUCCESS);
                msg.setBody(dataType + DataExportConstants.ALREADY_EXPORT_TO + "<br/>" + dataExportTask.getExportPath());
            }

            Map<Object, Object> dytableMap = Maps.newHashMap();
            DataExportRecord record = dataExportRecordService.getOne(dataExportTask.getRecordUuid());
            // record 的 uuid
            msg.setForwardDataUuid(record.getUuid());
//            msg.setRelatedUrl("page/preview/56111a4071fa57436e77120e3d55eb67?pageUuid=34425c4e-0773-4be9-8081-98c53320447e&uuid=" + record.getUuid());
            messageClientApiFacade.send(MessageTemplate.DATA_EXPORT_MESSAGE, msg, dytableMap, Lists.newArrayList(SpringSecurityUtils.getCurrentUserId()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IgnoreLoginUtils.logout();
        }
    }

    private void configToFile(Map<String, List<FieldDesc>> fieldDescMap, ExpImpService expImpService, String systemUnitFilePath) throws Exception {
        String fileName = ExpImpServiceBeanUtils.configFileName(systemUnitFilePath, expImpService);
        String path = fileName.substring(0, fileName.lastIndexOf(File.separator));
        this.mkdirs(path);
        File file = new File(fileName);
        if (file.exists()) {
            return;
        }

        // 去重
        for (String key : fieldDescMap.keySet()) {
            List<FieldDesc> fieldDescList = fieldDescMap.get(key);
            fieldDescList = new ArrayList<>(new HashSet<>(fieldDescList));
            fieldDescMap.put(key, fieldDescList);
        }

        String json = FieldTypeUtils.toJson(fieldDescMap, expImpService.dataClass());
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        writer.write(json);
        writer.flush();
        writer.close();
        fileOutputStream.close();
    }


    protected void dataToFile(Map<String, List<FieldDesc>> fieldDescMap, ExpImpService expImpService, String systemUnitFilePath, String systemUnitId, DataExportTask task, Integer currentPage, Integer pageSize) throws Exception {
        List<Object> dataList = null;
        String fileName = systemUnitFilePath + File.separator + expImpService.filePath() + File.separator + expImpService.fileName();
        String attachFilePath = ExpImpServiceBeanUtils.attachPathName(systemUnitFilePath, expImpService);
        if (!expImpService.isPage()) {
            dataList = expImpService.queryAll(systemUnitId);
            for (Object sourceObj : dataList) {
                if (this.isStop(task.getUuid())) {
                    break;
                }
                DataExportTaskLog log = this.addDataExportTaskLog(expImpService.getUuid(sourceObj), expImpService.filePath(), expImpService.fileName(), task.getUuid());
                try {
                    Object data = expImpService.toData(sourceObj);
                    String file = fileName + Separator.UNDERLINE.getValue() + expImpService.expFileName(data);
                    //提取文件uuid
                    this.attachFileConvert(fieldDescMap, expImpService, attachFilePath, sourceObj, data);
                    String json = JsonUtils.toJsonStr(data);
                    FileUtils.writeFileUTF(file, json, true);
                    updateProcess(task);
                } catch (Exception e) {
                    log.setExportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                    log.setExportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);
                    log.setErrorMsg(e.getMessage());
                    if (StringUtils.isBlank(log.getErrorMsg())) {
                        log.setErrorMsg("数据[" + log.getDataType() + "-" + log.getDataChildType() + log.getDataUuid() + "]中存在脏数据");
                    }
                    dataExportTaskLogService.save(log);
                    throw e;
                }
            }
            return;
        }

        if (currentPage != null) {
            dataList = expImpService.queryByPage(systemUnitId, currentPage, pageSize);
            fileName = fileName + currentPage;
        } else {
            dataList = expImpService.queryAll(systemUnitId);
        }

        fileName = fileName + ".json";
        String path = fileName.substring(0, fileName.lastIndexOf(File.separator));
        this.mkdirs(path);
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(fileName, true);
        OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        writer.write("[\r\n");
        for (int i = 0; i < dataList.size(); i++) {
            if (this.isStop(task.getUuid())) {
                break;
            }
            Object sourceObj = dataList.get(i);
            DataExportTaskLog log = this.addDataExportTaskLog(expImpService.getUuid(sourceObj), expImpService.filePath(), expImpService.fileName(), task.getUuid());
            try {
                Object data = expImpService.toData(sourceObj);
                // 数据为null，为脏数据，减少导出总数，跳过
                if (data == null) {
                    task.setDataTotal(task.getDataTotal() - 1);
                    dataExportTaskService.save(task);
                    dataExportTaskLogService.delete(log);
                    continue;
                }

                this.attachFileConvert(fieldDescMap, expImpService, attachFilePath, sourceObj, data);
                String json = JsonUtils.toJsonStr(data);
                if (i < dataList.size() - 1) {
                    json = json + ",\r\n";
                }
                writer.write(json);
                writer.flush();
                updateProcess(task);
            } catch (Exception e) {
                log.setExportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                log.setExportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);
                if (StringUtils.isBlank(e.getMessage())) {
                    log.setErrorMsg("NullPointException");
                } else {
                    log.setErrorMsg(e.getMessage());
                }
                dataExportTaskLogService.save(log);
                throw e;
            }
        }
        writer.write("\r\n]");
        writer.close();
        fileOutputStream.close();
    }

    private void attachFileConvert(Map<String, List<FieldDesc>> fieldDescMap, ExpImpService expImpService, String attachFilePath, Object sourceObj, Object data) throws Exception {
        //提取文件uuid
        Map<String, List<String>> attachFileMap = FieldTypeUtils.getAttachFileMap(expImpService.getDataUuid(data), data);
        Map<String, String> clobMap = Maps.newHashMap();
        AttachFieldDesc attachFieldDesc = expImpService.getAttachFieldDesc(data, sourceObj);
        if (attachFieldDesc != null) {
            attachFileMap.putAll(attachFieldDesc.getAttachFileMap());
            clobMap = attachFieldDesc.getClobMap();
            for (String key : attachFieldDesc.getFieldDesc().keySet()) {
                List<FieldDesc> fieldDescList = fieldDescMap.get(key);
                if (fieldDescList == null) {
                    fieldDescList = Lists.newArrayList();
                    fieldDescMap.put(key, fieldDescList);
                }
                fieldDescList.addAll(attachFieldDesc.getFieldDesc().get(key));
            }
        }
        //生成文件，转换为文件路径
        this.toAttachFile(attachFileMap, clobMap, attachFilePath);
    }

    protected void toAttachFile(Map<String, List<String>> attachFileMap, Map<String, String> clobMap, String attachFilePath) throws IOException {
        // 按照需求，不管有没有附件，都要生成附件文件夹
        this.mkdirs(attachFilePath);
        if (attachFileMap.size() > 0) {
            for (String key : attachFileMap.keySet()) {
                List<String> fileList = attachFileMap.get(key);
                for (int i = 0; i < fileList.size(); i++) {
                    String fileId = fileList.get(i);
                    MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileId);
                    if (mongoFileEntity == null) {
                        continue;
                    }
                    String fileName = mongoFileEntity.getFileName();
                    fileName = ExpImpServiceBeanUtils.linuxFileNameTooLongConvert(fileName);

                    String relativePath = key + File.separator + fileName;
                    String directory = attachFilePath + File.separator + key;
                    this.mkdirs(directory);

                    String filePath = attachFilePath + File.separator + relativePath;
                    InputStream input = mongoFileEntity.getInputstream();
                    OutputStream output = new FileOutputStream(new File(filePath));
                    IOUtils.copy(input, output);
                    IOUtils.closeQuietly(input);
                    IOUtils.closeQuietly(output);
                    fileList.set(i, relativePath);
                }
            }
        }
        if (clobMap.size() > 0) {
            for (String key : clobMap.keySet()) {
                String relativePath = key + File.separator + key + ".txt";
                String fileName = attachFilePath + File.separator + relativePath;
                FileUtils.writeFileUTF(fileName, clobMap.get(key), true);
            }
        }

    }

    protected void mkdirs(String path) {
        if (!(new File(path).isDirectory())) {
            new File(path).mkdirs();
        }
    }


    protected DataExportTaskLog addDataExportTaskLog(String uuid, String dataType, String dataChildType, String taskUuid) {
        DataExportTaskLog log = new DataExportTaskLog();
        log.setDataType(dataType);
        log.setDataChildType(dataChildType);
        log.setDataUuid(uuid);
        log.setExportTime(new Date());
        log.setExportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
        log.setExportStatusCn(DataExportConstants.DATA_LOG_STATUS_NORMAL_CN);
        log.setTaskUuid(taskUuid);
        dataExportTaskLogService.save(log);
        return log;
    }


    protected String getSystemUnitPath(String exportPath, String systemUnitName, Date exportTime) {
        String filePath = exportPath + File.separator + systemUnitName + Separator.UNDERLINE.getValue() + new SimpleDateFormat("yyyyMMddHHmmss").format(exportTime);
        return filePath;
    }

    protected void updateProcess(DataExportTask dataExportTask) {
        Integer progress = dataExportTask.getProgress();
        if (null == progress) {
            dataExportTask.setProgress(1);
        } else {
            dataExportTask.setProgress(progress.intValue() + 1);
        }
        dataExportTaskService.save(dataExportTask);
    }

    /**
     * 总数计算
     *
     * @param expImpServices
     * @param systemUnitIdList
     * @return
     */
    public long dataTotal(List<ExpImpService> expImpServices, List<String> systemUnitIdList) {
        long total = 0;
        for (ExpImpService expImpService : expImpServices) {
            for (String systemUnitId : systemUnitIdList) {
                long count = expImpService.total(systemUnitId);
                total = total + count;
            }
        }
        return total;
    }

}
