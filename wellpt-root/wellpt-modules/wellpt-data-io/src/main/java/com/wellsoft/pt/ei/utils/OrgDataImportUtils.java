package com.wellsoft.pt.ei.utils;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.entity.DataImportRecord;
import com.wellsoft.pt.ei.entity.DataImportTask;
import com.wellsoft.pt.ei.facade.DataImportApi;
import com.wellsoft.pt.ei.processor.OrgImportProcessor;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.sql.rowset.serial.SerialClob;
import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/24.1	liuyz		2021/9/24		Create
 * </pre>
 * @date 2021/9/24
 */
public class OrgDataImportUtils {

    public static void orgDataImport(DataImportRecord record, DataImportTask task) {
        // 根据导入数据记录record进行组织数据导入
        String dataTypeJson = record.getDataTypeJson();
        if (StringUtils.isBlank(dataTypeJson)) {
            return;
        }

        String importPath = record.getImportPath() + File.separator + DataExportConstants.DATA_TYPE_ORG;
        File importFileFolder = new File(importPath);
        File[] files = importFileFolder.listFiles();

        if (null == files) {
            throw new BusinessException(DataExportConstants.READ_DATA_PACKAGE_ERROR);
        }

        int fileCount = 0;
        for (File tmp : files) {
            if (tmp.getName().endsWith(".json")) {
                fileCount++;
            }
        }
        if (0 == fileCount) {
            throw new BusinessException(DataExportConstants.READ_DATA_PACKAGE_ERROR);
        }

        DataImportTask dataImportTask;
        if (null == task) {
            try {
                dataImportTask = createDataImportTask(record);
            } catch (Exception e) {
                throw new BusinessException(e.getMessage(), e);
            }
        } else {
            dataImportTask = task;
        }

        DataImportApi dataImportApi = ApplicationContextHolder.getBean(DataImportApi.class);
        OrgImportProcessor orgImportProcessor = ApplicationContextHolder.getBean(OrgImportProcessor.class);
        try {
//            dataImportApi.importOrgData(record, dataImportTask);
            orgImportProcessor.handle(record, dataImportTask);
        } catch (Exception e) {
            // 代码异常
            record.setImportStatus(DataExportConstants.DATA_STATUS_ERROR);
            record.setImportStatusCn(DataExportConstants.DATA_STATUS_ERROR_CN);
            dataImportApi.saveDataImportRecord(record);
            dataImportTask.setTaskStatus(DataExportConstants.DATA_STATUS_ERROR);
            dataImportTask.setTaskStatusCn(DataExportConstants.DATA_STATUS_ERROR_CN);
            dataImportApi.saveDataImportTask(dataImportTask);
        } finally {
            // 判断整体任务是否完成，并设置状态
            long count = dataImportApi.getErrorTaskLogs(dataImportTask.getUuid());
            if (0 == count) {
                record.setImportStatus(DataExportConstants.DATA_STATUS_FINISH);
                record.setImportStatusCn(DataExportConstants.DATA_STATUS_FINISH_CN);
                dataImportTask.setTaskStatus(DataExportConstants.DATA_STATUS_FINISH);
                dataImportTask.setTaskStatusCn(DataExportConstants.DATA_STATUS_FINISH_CN);
            } else {
                record.setImportStatus(DataExportConstants.DATA_STATUS_ERROR);
                record.setImportStatusCn(DataExportConstants.DATA_STATUS_ERROR_CN);
                dataImportTask.setTaskStatus(DataExportConstants.DATA_STATUS_ERROR);
                dataImportTask.setTaskStatusCn(DataExportConstants.DATA_STATUS_ERROR_CN);
            }

            dataImportApi.saveDataImportRecord(record);
            dataImportApi.saveDataImportTask(dataImportTask);
        }
    }

    private static DataImportTask createDataImportTask(DataImportRecord record) throws Exception {
        List<String> dataTypes = typeStr2List(record.getDataTypeJson());
        DataImportApi dataImportApi = ApplicationContextHolder.getBean(DataImportApi.class);

        // 创建导入任务
        DataImportTask dataImportTask = new DataImportTask();
        dataImportTask.setDataType(DataExportConstants.DATA_TYPE_ORG);
        dataImportTask.setImportUnitId(record.getImportUnitId());
        dataImportTask.setImportUnitName(record.getImportUnitName());
        dataImportTask.setOperator(SpringSecurityUtils.getCurrentUserName());
        dataImportTask.setImportTime(new Date());
        dataImportTask.setRecordUuid(record.getUuid());

        // 计算导入的数据量及导入的文件
        String importPath = record.getImportPath();
        File importFolder = new File(importPath + File.separator + DataExportConstants.DATA_TYPE_ORG);
        int dataCount = 0;// 数据量
        StringBuffer fileNames = new StringBuffer();// 导入的文件
        if (importFolder.exists()) {
            File[] files = importFolder.listFiles();
            for (File tmp : files) {
                String name = tmp.getName();

                String[] fileName = name.split("\\" + Separator.DOT.getValue())[0].split(Separator.UNDERLINE.getValue());

                if (fileName.length > 1) {
                    if (fileName[0].startsWith(DataExportConstants.DATA_TYPE_ORG)) {
                        if (dataTypes.contains(fileName[1])) {
                            try {
                                fileNames.append(name + Separator.COMMA.getValue());
                                String fileContent = FileUtils.readFileToString(tmp, "UTF-8");
                                if (fileContent.startsWith("{")) {
                                    dataCount++;
                                } else if (fileContent.startsWith("[")) {
                                    JSONArray jsonArray = JSONArray.fromObject(fileContent);
                                    dataCount = dataCount + jsonArray.size();
                                }
                            } catch (Exception e) {
                                record.setImportStatus(DataExportConstants.DATA_STATUS_ERROR);
                                dataImportApi.saveDataImportRecord(record);
                                e.printStackTrace();
                                throw new BusinessException("文件读取异常，请确认文件" + tmp.getName() + "具有读权限及文件内容为JSON格式。", e);
                            }
                        }
                    }
                }
            }
        }
        String importFiles = fileNames.toString();
        if (importFiles.endsWith(Separator.COMMA.getValue())) {
            dataImportTask.setImportFiles(new SerialClob(
                    IOUtils.toCharArray(IOUtils.toInputStream(StringUtils.join(Collections.singleton(importFiles.substring(0, importFiles.length() - 1)), Separator.COMMA.getValue())))));
        } else {
            dataImportTask.setImportFiles(new SerialClob(
                    IOUtils.toCharArray(IOUtils.toInputStream(importFiles))));
        }
        dataImportTask.setDataTotal(dataCount);
        dataImportTask.setTaskStatus(DataExportConstants.DATA_STATUS_DOING);
        dataImportApi.saveDataImportTask(dataImportTask);

        record.setImportFiles(dataImportTask.getImportFiles());
        dataImportApi.saveDataImportRecord(record);
        return dataImportTask;
    }

    public static List<String> typeStr2List(String dataTypeJson) {
        List<String> types = Lists.newArrayList();
        JSONObject json = JSONObject.fromObject(dataTypeJson).getJSONObject("org_data");
        if (DataExportConstants.DATA_SWITCH_ON.equals(json.optString("version"))) {
            types.add(DataExportConstants.DATA_TYPE_ORG_VERSION);
        }
        if (DataExportConstants.DATA_SWITCH_ON.equals(json.optString("user"))) {
            types.add(DataExportConstants.DATA_TYPE_ORG_USER);
        }
        if (DataExportConstants.DATA_SWITCH_ON.equals(json.optString("group"))) {
            types.add(DataExportConstants.DATA_TYPE_ORG_GROUP);
        }
        if (DataExportConstants.DATA_SWITCH_ON.equals(json.optString("duty"))) {
            types.add(DataExportConstants.DATA_TYPE_ORG_DUTY);
        }
        if (DataExportConstants.DATA_SWITCH_ON.equals(json.optString("rank"))) {
            types.add(DataExportConstants.DATA_TYPE_ORG_RANK);
        }
        return types;
    }
}
