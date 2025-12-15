package com.wellsoft.pt.ei.web;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.ei.bo.DataExportRecordInfoBo;
import com.wellsoft.pt.ei.bo.DataImportRecordInfoBo;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.constants.ExportCheckPathEnum;
import com.wellsoft.pt.ei.entity.*;
import com.wellsoft.pt.ei.processor.AbstractImportProcessor;
import com.wellsoft.pt.ei.processor.FlowImportProcessor;
import com.wellsoft.pt.ei.processor.MailImportProcessor;
import com.wellsoft.pt.ei.processor.OrgImportProcessor;
import com.wellsoft.pt.ei.processor.thread.ExportThread;
import com.wellsoft.pt.ei.processor.thread.ImportThread;
import com.wellsoft.pt.ei.processor.utils.ExpImpServiceBeanUtils;
import com.wellsoft.pt.ei.service.*;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.MultiOrgUserAccountService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Description: 数据导入导出controller
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/15.1	liuyz		2021/9/15		Create
 * </pre>
 * @date 2021/9/15
 */
@Api(tags = "数据导入导出")
@RestController
@RequestMapping("/api/dataIO")
public class DataIOController extends BaseController {

    @Autowired
    DataExportRecordService dataExportRecordService;

    @Autowired
    DataExportTaskService dataExportTaskService;

    @Autowired
    DataExportTaskLogService dataExportTaskLogService;

    @Autowired
    DataImportRecordService dataImportRecordService;

    @Autowired
    DataImportTaskService dataImportTaskService;

    @Autowired
    DataImportTaskLogService dataImportTaskLogService;

    @Autowired
    private MailImportProcessor mailImportProcessor;

    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;

    @Autowired
    private MultiOrgService multiOrgService;


    @ApiOperation("导出数据")
    @PostMapping(value = "/exportData")
    public ApiResult exportData(@ApiParam(value = "导出记录", required = true) @RequestBody DataExportRecord record) {
        record.setOperator(SpringSecurityUtils.getCurrentUserName());
        record.setExportTime(new Date());
        dataExportRecordService.save(record);

        String[] systemUnitNameList = record.getSystemUnitNames().split(Separator.COMMA.getValue());
        for (String systemUnitName : systemUnitNameList) {
            String exportFolderPath = record.getExportPath() + File.separator + systemUnitName + Separator.UNDERLINE.getValue() + new SimpleDateFormat("yyyyMMddHHmmss").format(record.getExportTime());
            File exportFolder = new File(exportFolderPath);
            if (!exportFolder.exists()) {
                exportFolder.mkdirs();
            }
        }

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(servletRequestAttributes, true);//设置子线程共享

        List<DataExportTask> taskList = dataExportTaskService.saveAll(record);
        for (DataExportTask task : taskList) {
            ExportThread exportThread = new ExportThread(record, task);
            exportThread.start();
        }
        return ApiResult.success();
    }


    @ApiOperation("导入数据")
    @PostMapping(value = "/importData")
    public ResultMessage importData(@ApiParam(value = "导入记录", required = true) @RequestBody DataImportRecord record) {

        String importPath = record.getImportPath() + File.separator + record.getDataType();
        File importPathFile = new File(importPath);
        File[] jsonFiles = importPathFile.listFiles();
        if (null == jsonFiles) {
            return new ResultMessage(DataExportConstants.READ_DATA_PACKAGE_ERROR, false);
        } else {
            int fileCount = (int) Arrays.stream(jsonFiles).filter(file -> file.getName().endsWith(".json")).count();
            if (0 == fileCount) {
                return new ResultMessage(DataExportConstants.READ_DATA_PACKAGE_ERROR, false);
            }
        }

        record.setOperator(SpringSecurityUtils.getCurrentUserName());
        record.setImportTime(new Date());
        record.setBatchNo(new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));


        DataImportTask task = new DataImportTask();
        task.setDataType(record.getDataType());
        task.setImportUnitId(record.getImportUnitId());
        task.setImportUnitName(record.getImportUnitName());
        task.setTaskStatus(DataExportConstants.DATA_STATUS_DOING);
        task.setTaskStatusCn(DataExportConstants.DATA_STATUS_EXPORT_DOING_CN);
        task.setOperator(SpringSecurityUtils.getCurrentUserName());
        task.setProgress(0);
        task.setImportTime(record.getImportTime());
        task.setReimportTime(record.getImportTime());

        // 检测文件是否完全
        AbstractImportProcessor processor = null;
        if (task.getDataType().equals(DataExportConstants.DATA_TYPE_ORG)) {
            processor = ApplicationContextHolder.getBean(OrgImportProcessor.class);
        } else if (task.getDataType().equals(DataExportConstants.DATA_TYPE_MAIL)) {
            processor = ApplicationContextHolder.getBean(MailImportProcessor.class);
        } else if (task.getDataType().equals(DataExportConstants.DATA_TYPE_FLOW)) {
            processor = ApplicationContextHolder.getBean(FlowImportProcessor.class);
        }
        if (processor != null) {
            List<ExpImpService> expImpServiceList = processor.getExportServiceList(record);
            for (ExpImpService expImpService : expImpServiceList) {
                List<File> files = Lists.newArrayList();
                for (File jsonFile : jsonFiles) {
                    if (jsonFile.isDirectory()) {
                        continue;
                    }
                    if (jsonFile.getName().indexOf(ExpImpServiceBeanUtils.configName) > -1) {
                        continue;
                    }
                    if (jsonFile.getName().indexOf(expImpService.fileName()) > -1) {
                        if (task.getDataType().equals(DataExportConstants.DATA_TYPE_ORG)) {
                            files.add(jsonFile);
                        } else {
                            String number = jsonFile.getName().replace(expImpService.fileName(), "").replace(".json", "");
                            if (StringUtils.isBlank(number) || Ints.tryParse(number) != null) {
                                files.add(jsonFile);
                            }
                        }
                    }
                }

                if (CollectionUtils.isEmpty(files)) {
                    return new ResultMessage(-1, DataExportConstants.READ_DATA_PACKAGE_ERROR, false, null);
                }
            }
        }

        dataImportRecordService.save(record);
        task.setRecordUuid(record.getUuid());
        dataImportTaskService.save(task);

        record.setProcessLog(DataExportConstants.getImportLogHtml(task, DataExportConstants.DATA_STATUS_DOING, null));
        dataImportRecordService.save(record);

        ImportThread importThread = new ImportThread(record, task);
        importThread.start();
        return new ResultMessage(DataExportConstants.READ_DATA_PACKAGE_SUCCESS);
    }

    /**
     * 校验路径
     *
     * @param path
     * @return
     * @author baozh
     * @date 2021/9/22 11:55
     */
    @GetMapping("/checkPath")
    public ApiResult checkPath(@RequestParam String path) {
        ExportCheckPathEnum result = dataExportTaskService.checkPath(path);
        if (ExportCheckPathEnum.OK != result) {
            return ApiResult.build(result.getCode(), "请输入正确的磁盘路径", null);
        }
        return ApiResult.success(result.toString());
    }

    /**
     * 查看任务记录详情
     *
     * @param uuid 记录Id
     * @return
     * @author baozh
     * @date 2021/9/22 11:55
     */
    @GetMapping("/taskRecordInfo/{type}/{uuid}")
    public ApiResult taskRecordInfo(@PathVariable("type") String type, @PathVariable("uuid") String uuid) throws IOException, SQLException {
        if ("null".equals(uuid)) {
            return ApiResult.fail("记录Id不能为空");
        }
        if (DataExportConstants.DATA_TYPE_OPERATE_EXPORT.equals(type)) {
            DataExportRecordInfoBo bo = dataExportRecordService.getTaskRecordInfoByUuid(uuid);
            return ApiResult.success(bo);
        } else if (DataExportConstants.DATA_TYPE_OPERATE_IMPORT.equals(type)) {
            DataImportRecordInfoBo bo = dataImportRecordService.getTaskRecordInfoByUuid(uuid);
            return ApiResult.success(bo);
        }
        return ApiResult.fail();
    }

    /**
     * 导出任务取消
     *
     * @param uuid
     * @return
     * @author baozh
     * @date 2021/9/22 15:27
     */
    @GetMapping("/cancelTask/{type}/{uuid}")
    public ApiResult cancelTask(@PathVariable("type") String type, @PathVariable("uuid") String uuid) {
        if (DataExportConstants.DATA_TYPE_OPERATE_EXPORT.equals(type)) {
            dataExportTaskService.cancelTaskByUuid(uuid);
        } else if (DataExportConstants.DATA_TYPE_OPERATE_IMPORT.equals(type)) {
            dataImportTaskService.cancelTaskByUuid(uuid);
        }
        return ApiResult.success();
    }

    /**
     * 重新发起任务
     *
     * @param uuid
     * @return
     * @author baozh
     * @date 2021/9/22 15:30
     */
    @GetMapping("/restartTask/{type}/{uuid}")
    public ApiResult restartTask(@PathVariable("type") String type, @PathVariable("uuid") String uuid) {
        if (DataExportConstants.DATA_TYPE_OPERATE_EXPORT.equals(type)) {
            dataExportTaskService.restartTaskByUuid(uuid);
        } else if (DataExportConstants.DATA_TYPE_OPERATE_IMPORT.equals(type)) {
            dataImportTaskService.restartTaskByUuid(uuid);
        }
        return ApiResult.success();
    }

    /**
     * 徽标接口
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/9/22 15:33
     */
    @GetMapping("/getExecutingCount/{type}")
    public ApiResult getExecutingCount(@PathVariable("type") String type) {
        long count = 0;
        if (DataExportConstants.DATA_TYPE_OPERATE_EXPORT.equals(type)) {
            count = dataExportTaskService.getExecutingCount();
        } else if (DataExportConstants.DATA_TYPE_OPERATE_IMPORT.equals(type)) {
            count = dataImportTaskService.getExecutingCount();
        }
        return ApiResult.success(count);
    }

    /**
     * 默认临时文件夹路径接口
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/9/22 15:33
     */
    @GetMapping("/defaultFilePath")
    public ApiResult defaultFilePath() {
        String osName = System.getProperties().getProperty("os.name");
        String defaultPath = "/tmp";
        if (osName.contains("Windows")) {
            defaultPath = "C:/tmp";
        }
        return ApiResult.success(defaultPath);
    }

    /**
     * 获取源数据
     *
     * @param sourceUuid
     * @return
     * @author baozh
     * @date 2021/9/23 15:13
     */
    @GetMapping("/getImportSourceData/{sourceUuid}")
    public ApiResult getImportSourceData(@PathVariable("sourceUuid") String sourceUuid) {
        DataImportTaskLog taskLog = new DataImportTaskLog();
        taskLog.setSourceUuid(sourceUuid);
        List<DataImportTaskLog> dataImportTaskLogs = dataImportTaskLogService.listAllByPage(taskLog, null, "createTime desc");
        return ApiResult.success(dataImportTaskLogs.subList(0, 1));
    }

    @ApiOperation(value = "获取系统单位", notes = "获取系统单位：当前用户为超管时，下拉可见本系统的全部系统单位；当前用户为系统管理员或者系统单位下用户时，下拉仅可见本单位的系统单位名称。")
    @GetMapping("/querySystemUnitList")
    @ApiOperationSupport(order = 43)
    public ApiResult<List<MultiOrgSystemUnit>> querySystemUnitList() {

        MultiOrgUserAccount account = multiOrgUserAccountService.getAccountById(SpringSecurityUtils.getCurrentUserId());

//        2.1 当前用户为超管时，下拉可见本系统的全部系统单位
//        2.2 当前用户为系统管理员或者系统单位下用户时，下拉仅可见本单位的系统单位名称
        List<MultiOrgSystemUnit> list = Lists.newArrayList();
        if ("admin".equals(account.getLoginName())) {
            list = multiOrgService.queryAllSystemUnitList();
        } else {
            list.add(multiOrgService.getSystemUnitById(account.getSystemUnitId()));
        }

        return ApiResult.success(list);
    }

}
