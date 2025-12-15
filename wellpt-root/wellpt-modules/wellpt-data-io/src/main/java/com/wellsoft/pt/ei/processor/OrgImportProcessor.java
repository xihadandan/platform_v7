package com.wellsoft.pt.ei.processor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.org.OrgGroupData;
import com.wellsoft.pt.ei.dto.org.OrgNodeInfoData;
import com.wellsoft.pt.ei.dto.org.OrgVersionConf;
import com.wellsoft.pt.ei.entity.DataImportRecord;
import com.wellsoft.pt.ei.entity.DataImportTask;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.ei.facade.DataImportApi;
import com.wellsoft.pt.ei.processor.utils.ExpImpServiceBeanUtils;
import com.wellsoft.pt.ei.processor.utils.FieldTypeUtils;
import com.wellsoft.pt.ei.processor.utils.RequiredFile;
import com.wellsoft.pt.ei.service.DataImportRecordService;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.ei.service.DataImportTaskService;
import com.wellsoft.pt.ei.service.ExpImpService;
import com.wellsoft.pt.ei.service.impl.OrgGroupExpImpServiceImpl;
import com.wellsoft.pt.ei.service.impl.OrgUserExpImpServiceImpl;
import com.wellsoft.pt.ei.service.impl.OrgVersionConfExpImpServiceImpl;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.sql.rowset.serial.SerialClob;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/30.1	liuyz		2021/9/30		Create
 * </pre>
 * @date 2021/9/30
 */
@Component
@Scope("prototype")
public class OrgImportProcessor extends AbstractImportProcessor {

    @Autowired
    private DataImportRecordService dataImportRecordService;
    @Autowired
    private DataImportTaskService dataImportTaskService;
    @Autowired
    private DataImportTaskLogService dataImportTaskLogService;
    @Autowired
    private MultiOrgVersionService multiOrgVersionService;
    @Autowired
    private MultiOrgElementService multiOrgElementService;
    @Autowired
    private MultiOrgElementLeaderService multiOrgElementLeaderService;
    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;
    @Autowired
    private MultiOrgUserTreeNodeService multiOrgUserTreeNodeService;

    private List<OrgNodeInfoData> orgNodeInfoDataList;

    public void handle(DataImportRecord record, DataImportTask task) {
        try {
            List<ExpImpService> expImpServiceList = this.getExportServiceList(record);

            //读取文件 计算总数
            Map<ExpImpService, com.alibaba.fastjson.JSONArray> impJSONArrayMap = this.getImpJSONArrayMap(record, task, expImpServiceList);

            /**
             * key:导入前 uuid 或 id
             * val:导入后 uuid 或 id
             */
            Map<String, String> dependentDataMap = Maps.newHashMap();

            Map<ExpImpService, List<ImportEntity>> updateMap = Maps.newHashMap();

            MultiOrgVersion version = multiOrgVersionService.getById(record.getVersionId());
            //导入数据
            for (ExpImpService expImpService : expImpServiceList) {
                if (this.isStop(task.getUuid())) {
                    break;
                }
                com.alibaba.fastjson.JSONArray jsonArray = impJSONArrayMap.get(expImpService);
                List<ImportEntity> importEntityList = this.jsonArrayToData(expImpService, record, task, jsonArray, dependentDataMap);
                if (importEntityList.size() > 0) {
                    List<ImportEntity> objects = updateMap.get(expImpService);
                    if (objects == null) {
                        objects = Lists.newArrayList();
                        updateMap.put(expImpService, objects);
                    }
                    objects.addAll(importEntityList);
                }
                // 组织节点需要立即挂载到组织树，避免用户导入时重复创建节点
                if (expImpService instanceof OrgVersionConfExpImpServiceImpl) {
                    if (this.isStop(task.getUuid())) {
                        break;
                    }
                    for (ImportEntity importEntity : importEntityList) {
                        if (this.isStop(task.getUuid())) {
                            break;
                        }
                        expImpService.update(importEntity.getObj(), importEntity.getSorce(), record.getVersionId(), version.getName());
                    }
                }
            }

            // 不管有没有导入用户，都对组织版本的管理员进行更新
            updateManager(task.getUuid(), record.getVersionId());

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

            //导入完成 处理依赖数据
            for (ExpImpService expImpService : updateMap.keySet()) {
                if (this.isStop(task.getUuid()) || (expImpService instanceof OrgVersionConfExpImpServiceImpl)) {
                    break;
                }
                List<ImportEntity> importEntityList = updateMap.get(expImpService);
                for (ImportEntity importEntity : importEntityList) {
                    if (this.isStop(task.getUuid())) {
                        break;
                    }
                    // 只用来更新组织节点的父级节点 以及 用户的直属上级领导
                    expImpService.update(importEntity.getObj(), importEntity.getSorce(), record.getVersionId(), version.getName());
                }
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
        }
        this.sendMessage(task);
    }

    protected Map<ExpImpService, com.alibaba.fastjson.JSONArray> getImpJSONArrayMap(DataImportRecord record, DataImportTask task, List<ExpImpService> expImpServiceList) throws IOException, SQLException {
        Map<ExpImpService, com.alibaba.fastjson.JSONArray> impJSONArrayMap = Maps.newHashMap();
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
                    files.add(jsonFile);
                }
            }
            //文件排序
            if (files.size() > 1) {
                files.sort(new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        String a1 = o1.getName().replace(expImpService.fileName(), "").replace(".json", "");
                        String a2 = o2.getName().replace(expImpService.fileName(), "").replace(".json", "");
                        if (expImpService.fileName().contains(DataExportConstants.DATA_TYPE_ORG_VERSION)) {
                            a1 = a1.split(IdPrefix.ORG_VERSION.getValue())[1];
                            a2 = a2.split(IdPrefix.ORG_VERSION.getValue())[1];
                        }
                        return Integer.valueOf(a1) - Integer.valueOf(a2);
                    }
                });
            }

            com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
            for (File file : files) {
                String jsonStr = null;
                try {
                    jsonStr = FileUtils.readFileToString(file, "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                if (expImpService instanceof OrgVersionConfExpImpServiceImpl) {
                    // 组织版本数据一条数据一个文件，这边需要另外处理
                    com.alibaba.fastjson.JSONObject orgVersion = com.alibaba.fastjson.JSONObject.parseObject(jsonStr);
                    // 配置信息：组织节点（orgNodes）、业务单位（businessUnits）、部门（departments）、职位（jobs）、引用系统单位（orgVersions）
                    String[] orgConfigs = new String[]{"orgNodes", "businessUnits", "departments", "jobs", "orgVersions"};
                    for (String str : orgConfigs) {
                        com.alibaba.fastjson.JSONArray tmp = orgVersion.getJSONArray(str);
                        if (null != tmp && !tmp.isEmpty()) {
                            jsonArray.addAll(orgVersion.getJSONArray(str));
                        }
                    }

                    // 按照层级深度进行排序，浅的排前面，层级深的排后面
                    List<com.alibaba.fastjson.JSONObject> jsonObjects = Lists.newArrayList();
                    for (int i = 0, len = jsonArray.size(); i < len; i++) {
                        jsonObjects.add(jsonArray.getJSONObject(i));
                    }
                    Collections.sort(jsonObjects, new Comparator<com.alibaba.fastjson.JSONObject>() {
                        @Override
                        public int compare(com.alibaba.fastjson.JSONObject o1, com.alibaba.fastjson.JSONObject o2) {
                            int len1 = o1.getString("parentEleIdPath").split(MultiOrgService.PATH_SPLIT_SYSMBOL).length;
                            int len2 = o2.getString("parentEleIdPath").split(MultiOrgService.PATH_SPLIT_SYSMBOL).length;
                            return len1 - len2;
                        }
                    });
                    jsonArray = new com.alibaba.fastjson.JSONArray();
                    for (com.alibaba.fastjson.JSONObject obj : jsonObjects) {
                        jsonArray.add(obj);
                    }
                } else {
                    jsonArray.addAll(com.alibaba.fastjson.JSONArray.parseArray(jsonStr));
                }

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

    protected List<ImportEntity> jsonArrayToData(ExpImpService expImpService, DataImportRecord record, DataImportTask task, com.alibaba.fastjson.JSONArray jsonArray, Map<String, String> dependentDataMap) {
        if (expImpService instanceof OrgVersionConfExpImpServiceImpl) {
            DataImportApi dataImportApi = ApplicationContextHolder.getBean(DataImportApi.class);
            orgNodeInfoDataList = dataImportApi.getAllNodeInfo(record.getVersionId());
        }

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
            DataImportTaskLog dataImportTaskLog;
            boolean replace = false;
            if (data instanceof OrgVersionConf) {
                // 组织架构节点的重复判断：是否存在同路径、同名称、同类型的节点；重复则直接略过
                String importDataType = ((OrgVersionConf) data).getType();
                String importDataName = ((OrgVersionConf) data).getName();
                String parentEleNamePath = ((OrgVersionConf) data).getParentEleNamePath();
                MultiOrgVersion version = multiOrgVersionService.getById(record.getVersionId());
                String importDataNamePath = version.getName() + parentEleNamePath.replace(parentEleNamePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[0], "") + MultiOrgService.PATH_SPLIT_SYSMBOL + importDataName;
                if (existSameNode(record.getVersionId(), version.getName(), importDataType, importDataName, importDataNamePath)) {
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
                    log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_NORMAL_CN);
                    log.setIsRepeat(1);
                    this.saveLog(log, task);
                    continue;
                }

            } else {
                // 重复校验
                dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(expImpService.getDataUuid(data));
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
            }

            // 依赖校验：引用系统单位必须存在
            if (data instanceof OrgVersionConf && ((OrgVersionConf) data).getType().equals(IdPrefix.ORG_VERSION.getValue())) {
                OrgVersionConf orgVersionConf = (OrgVersionConf) data;
                if (null != orgVersionConf) {
                    MultiOrgVersion relyOn = new MultiOrgVersion();
                    relyOn.setId(orgVersionConf.getEleId());
                    relyOn.setName(orgVersionConf.getName());
                    List<MultiOrgVersion> list = multiOrgVersionService.listByEntity(relyOn);
                    if (null == list || list.size() == 0) {
                        log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                        log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);
                        log.setErrorMsg("数据依赖缺失，引用系统单位不存在。");
                        this.saveLog(log, task);

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

            if (expImpService instanceof OrgUserExpImpServiceImpl) {
                // 导入用户时，需要设置默认密码
                dependentDataMap.put("defaultPwd", record.getSettingPwd());
                // 可能有附件，需要传递附件的路径
                dependentDataMap.put("fileId", StringUtils.join(requiredFile.getAttachFileMap().values(), Separator.COMMA.getValue()));
                // 对于用户的职位路径，需要组织版本id作为根节点
                dependentDataMap.put("versionId", record.getVersionId());
            }

            // 针对群组导入的判断
            if (expImpService instanceof OrgGroupExpImpServiceImpl) {
                Integer type = ((OrgGroupData) data).getType();
                // 个人群组不导入
                if (1 == type.intValue()) {
                    log.setErrorMsg("个人群组不导入。");
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                    log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);

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

                OrgGroupData orgGroupData = (OrgGroupData) data;
                String[] memberIdPaths = orgGroupData.getMemberIdPaths().split(Separator.SEMICOLON.getValue());

                boolean exist = true;
                // 处理群组成员的ID，同时判断成员是否存在
                for (int idx = 0, len = memberIdPaths.length; idx < len; idx++) {
                    String id = memberIdPaths[idx];
                    dataImportTaskLog = dataImportTaskLogService.getBySourceId(id);
                    if (null != dataImportTaskLog) {
                        memberIdPaths[idx] = dataImportTaskLog.getAfterImportId();
                    } else {
                        // 异常的数据，无论终止还是跳过，导入状态都为失败
                        log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                        log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);
                        log.setErrorMsg("群组成员" + id + "不存在。");
                        this.saveLog(log, task);
                        exist = false;
                        break;
                    }
                }

                if (!exist) {
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

            try {
                //转换保存数据
                ImportEntity importEntity = null;
                if (expImpService instanceof OrgVersionConfExpImpServiceImpl) {
                    importEntity = expImpService.save(data, systemUnitId, replace, record.getVersionId());
                } else {
                    importEntity = expImpService.save(data, systemUnitId, replace, dependentDataMap);
                }
                if (importEntity.isPostProcess()) {
                    importEntity.setSorceJson(dataJson);
                    importEntityList.add(importEntity);
                }
                log.setAfterImportId(expImpService.getId(importEntity.getObj()));
                log.setAfterImportUuid(expImpService.getUuid(importEntity.getObj()));
                log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
                log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_NORMAL_CN);
                this.saveLog(log, task);
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

    protected List<ImportEntity> fileToData(ExpImpService expImpService, DataImportRecord record, DataImportTask task, String fileName) {
        List<ImportEntity> importEntityList = Lists.newArrayList();
        String systemUnitId = record.getImportUnitId();
        String filePath = record.getImportPath() + File.separator + expImpService.filePath();
        String attachFilePath = filePath + File.separator + expImpService.filePath() + "_附件";
        File jsonFile = new File(filePath + File.separator + fileName);
        String jsonStr = null;
        try {
            jsonStr = FileUtils.readFileToString(jsonFile, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        JSONArray jsonArray = JSONArray.fromObject(jsonStr);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Object data = null;

            // 数据类型校验
            try {
                data = JSONObject.toBean(jsonObject, expImpService.dataClass());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (data == null) {
                DataImportTaskLog log = new DataImportTaskLog();
                log.setDataType(expImpService.filePath());
                log.setDataChildType(expImpService.dataChildType());
                log.setImportTime(new Date());
                log.setTaskUuid(task.getUuid());
                log.setSourceData(jsonStr);

                log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                log.setErrorMsg("数据类型不匹配");
                this.saveLog(log, task);
                // 异常策略：跳过
                if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                    continue;
                } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                    break;
                }
            }


            DataImportTaskLog log = this.createDataImportTaskLog(expImpService, task.getUuid(), data, jsonStr);
            // 重复校验
            DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(expImpService.getDataUuid(data));
            boolean replace = false;
            if (null != dataImportTaskLog) {
                log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
                log.setIsRepeat(1);
                this.saveLog(log, task);
                //跳过
                if (DataExportConstants.DATA_REPEAT_STRATEGY_SKIP.intValue() == record.getRepeatStrategy().intValue()) {
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
                    log.setErrorMsg("数据必填项缺失");
                    this.saveLog(log, task);
                    // 异常策略：跳过
                    if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                        continue;
                    } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 异常的数据，无论终止还是跳过，导入状态都为失败
                log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                log.setErrorMsg("必填校验出错");
                this.saveLog(log, task);
                // 异常策略：跳过
                if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                    continue;
                } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
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
                log.setErrorMsg("文件处理错误");
                this.saveLog(log, task);
                // 异常策略：跳过
                if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                    continue;
                } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                    break;
                }
            }

            Map<String, String> map = Maps.newHashMap();
            if (expImpService instanceof OrgUserExpImpServiceImpl) {
                // 导入用户时，需要设置默认密码
                map.put("defaultPwd", record.getSettingPwd());
                // 可能有附件，需要传递附件的路径
                map.put("fileId", StringUtils.join(requiredFile.getAttachFileMap().values(), Separator.COMMA.getValue()));
                // 对于用户的职位路径，需要组织版本id作为根节点
                map.put("versionId", record.getVersionId());
            }

            //转换保存数据
            if (expImpService instanceof OrgGroupExpImpServiceImpl && ((OrgGroupData) data).getType().equals("1")) {
                // 个人群组不导入
                log.setErrorMsg("个人群组不导入。");
                log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);
            } else {
                // 处理群组成员的ID，同时判断成员是否存在
                if (expImpService instanceof OrgGroupExpImpServiceImpl) {
                    OrgGroupData orgGroupData = (OrgGroupData) data;
                    String[] memberIdPaths = orgGroupData.getMemberIdPaths().split(Separator.SEMICOLON.getValue());

                    for (int idx = 0, len = memberIdPaths.length; idx < len; idx++) {
                        String id = memberIdPaths[idx];
                        dataImportTaskLog = dataImportTaskLogService.getBySourceId(id);
                        if (null != dataImportTaskLog) {
                            memberIdPaths[idx] = dataImportTaskLog.getAfterImportId();
                        } else {
                            // 异常的数据，无论终止还是跳过，导入状态都为失败
                            log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                            log.setErrorMsg("群组成员不存在。");
                            this.saveLog(log, task);
                            // 异常策略：跳过
                            if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                                continue;
                            } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                                break;
                            }
                        }
                    }
                }
                // 一切正常，开始导入数据
                ImportEntity importEntity = expImpService.save(data, systemUnitId, replace, map);
                if (importEntity.isPostProcess()) {
                    importEntityList.add(importEntity);
                }
                log.setAfterImportId(expImpService.getId(importEntity.getObj()));
                log.setAfterImportUuid(expImpService.getUuid(importEntity.getObj()));
            }

            this.saveLog(log, task);
        }
        return importEntityList;
    }

    /*protected List<ImportEntity> fileToVersionData(ExpImpService expImpService, DataImportRecord record, DataImportTask task, String fileName) {
        List<ImportEntity> importEntityList = Lists.newArrayList();
        String systemUnitId = record.getImportUnitId();
        String filePath = record.getImportPath() + File.separator + expImpService.filePath();
        String attachFilePath = filePath + File.separator + expImpService.filePath() + "_附件";
        File jsonFile = new File(filePath + File.separator + fileName);
        String jsonStr = null;
        try {
            jsonStr = FileUtils.readFileToString(jsonFile, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        // 组织版本数据一条数据一个文件，这边需要另外处理
        JSONObject orgVersion = JSONObject.fromObject(jsonStr);
        // 配置信息：组织节点（orgNodes）、业务单位（businessUnits）、部门（departments）、职位（jobs）、引用系统单位（orgVersions）
        String[] orgConfigs = new String[]{"orgNodes", "businessUnits", "departments", "jobs", "orgVersions"};
        for (String str : orgConfigs) {
            JSONArray orgNodes = orgVersion.getJSONArray(str);

            for (int i = 0; i < orgNodes.size(); i++) {
                JSONObject jsonObject = orgNodes.getJSONObject(i);
                Object data = null;

                // 数据类型校验
                try {
                    data = JSONObject.toBean(jsonObject, OrgVersionConf.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (data == null) {
                    DataImportTaskLog dataImportTaskLog = new DataImportTaskLog();
                    dataImportTaskLog.setDataType(expImpService.filePath());
                    dataImportTaskLog.setDataChildType(expImpService.dataChildType());
                    dataImportTaskLog.setImportTime(new Date());
                    dataImportTaskLog.setTaskUuid(task.getUuid());
                    dataImportTaskLog.setSourceData(jsonStr);

                    dataImportTaskLog.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                    dataImportTaskLog.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);
                    dataImportTaskLog.setErrorMsg("数据类型不匹配");
                    dataImportTaskLogService.save(dataImportTaskLog);
                    // 异常策略：跳过
                    if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                        continue;
                    } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                        break;
                    }
                }


                DataImportTaskLog log = createVersionDataImportTaskLog(expImpService, task.getUuid(), data, jsonStr, str);

                // 重复校验
                DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(expImpService.getDataUuid(data));
                boolean replace = false;
                if (null != dataImportTaskLog) {
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
                    log.setIsRepeat(1);
                    this.saveLog(log, task);
                    //跳过
                    if (DataExportConstants.DATA_REPEAT_STRATEGY_SKIP.intValue() == record.getRepeatStrategy().intValue()) {
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
                        log.setErrorMsg("数据必填项缺失");
                        dataImportTaskLogService.save(log);
                        // 异常策略：跳过
                        if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                            continue;
                        } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // 异常的数据，无论终止还是跳过，导入状态都为失败
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                    log.setErrorMsg("必填校验出错");
                    dataImportTaskLogService.save(log);
                    // 异常策略：跳过
                    if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                        continue;
                    } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                        break;
                    }
                }

                // 依赖校验
                if (str.equals("orgVersions")) {
                    OrgVersionConf orgVersionConf = (OrgVersionConf) data;
                    if (null != orgVersionConf) {
                        MultiOrgElement relyOn = new MultiOrgElement();
                        relyOn.setId(orgVersionConf.getEleId());
                        relyOn.setName(orgVersionConf.getName());
                        List<MultiOrgElement> list = multiOrgElementService.listByEntity(relyOn);
                        if (null == list || list.size() == 0) {
                            log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                            log.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_ERROR_CN);
                            log.setErrorMsg("数据依赖缺失，引用系统单位不存在。");
                            dataImportTaskLogService.save(log);

                            if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                                continue;
                            } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                                break;
                            }
                        }
                    }
                }

                //文件处理
                try {
                    this.saveFile(requiredFile.getAttachFileMap(), attachFilePath);
                } catch (Exception e) {
                    e.printStackTrace();
                    // 异常的数据，无论终止还是跳过，导入状态都为失败
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                    log.setErrorMsg("文件处理错误");
                    dataImportTaskLogService.save(log);
                    // 异常策略：跳过
                    if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                        continue;
                    } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                        break;
                    }
                }

                //转换保存数据
                ImportEntity importEntity = expImpService.save(data, systemUnitId, replace, record.getVersionId());
                if (importEntity.isPostProcess()) {
                    importEntityList.add(importEntity);
                }

                log.setAfterImportId(expImpService.getId(importEntity.getObj()));
                log.setAfterImportUuid(expImpService.getUuid(importEntity.getObj()));
                dataImportTaskLogService.save(log);
            }

        }

        return importEntityList;
    }*/

    @Override
    public List<ExpImpService> getExportServiceList(DataImportRecord record) {
        return ExpImpServiceBeanUtils.orgExpImpServices(record.getDataTypeJson());
    }

    protected DataImportTaskLog createDataImportTaskLog(ExpImpService expImpService, String taskUuid, Object data, String json) {
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

    private DataImportTaskLog createVersionDataImportTaskLog(ExpImpService expImpService, String taskUuid, Object data, String json, String str) {
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
        dataImportTaskLog.setImportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
        dataImportTaskLog.setImportStatusCn(DataExportConstants.DATA_LOG_STATUS_NORMAL_CN);
        dataImportTaskLogService.save(dataImportTaskLog);
        return dataImportTaskLog;
    }

    private String getType(String str) {
        switch (str) {
            case "orgNodes":
            case "O":
                return DataExportConstants.DATA_TYPE_ORG_VERSION_O;
            case "businessUnits":
            case "B":
                return DataExportConstants.DATA_TYPE_ORG_VERSION_B;
            case "departments":
            case "D":
                return DataExportConstants.DATA_TYPE_ORG_VERSION_D;
            case "jobs":
            case "J":
                return DataExportConstants.DATA_TYPE_ORG_VERSION_J;
            case "orgVersions":
            case "V":
                return DataExportConstants.DATA_TYPE_ORG_VERSION_V;
            default:
                return "";
        }
    }

    /**
     * 更新组织版本的管理员
     */
    private void updateManager(String taskUuid, String versionId) {
        DataImportTaskLog dataImportTaskLog = new DataImportTaskLog();
        dataImportTaskLog.setTaskUuid(taskUuid);
        dataImportTaskLog.setDataType(DataExportConstants.DATA_TYPE_ORG);

        List<DataImportTaskLog> dataImportTaskLogs = dataImportTaskLogService.listByEntity(dataImportTaskLog);

        for (DataImportTaskLog tmp : dataImportTaskLogs) {
            // 组织架构节点才有管理员信息
            if (tmp.getDataChildType().equals(DataExportConstants.DATA_TYPE_ORG_TREE_NODE)) {
                String json = tmp.getSourceData();
                JSONObject jsonObject = JSONObject.fromObject(json);
                OrgVersionConf orgVersionConf = (OrgVersionConf) JSONObject.toBean(jsonObject, OrgVersionConf.class);

                // 仅节点、业务单位、部门  需要设置管理员信息
                String childType = orgVersionConf.getType();
                if (childType.equals(IdPrefix.ORG.getValue()) || childType.equals(IdPrefix.BUSINESS_UNIT.getValue()) || childType.equals(IdPrefix.DEPARTMENT.getValue())) {
                    String branchLeaderIdPaths = orgVersionConf.getBranchLeaderIdPaths();
                    String bossIdPaths = orgVersionConf.getBossIdPaths();
                    String managerIdPaths = orgVersionConf.getManagerIdPaths();
                    String eleId = tmp.getAfterImportId();

                    // 先删除旧的管理员信息
                    multiOrgElementLeaderService.deleteLeaderList(eleId, versionId);

                    // 添加该职位的分管领导（多个，包含职位、人员）
                    if (StringUtils.isNotBlank(branchLeaderIdPaths)) {
                        addLeaderListByType(eleId, versionId, branchLeaderIdPaths, orgVersionConf.getBranchLeaderNames(), MultiOrgElementLeader.TYPE_BRANCHED_LEADER);
                    }
                    // 添加对应的负责人（职位）
                    if (StringUtils.isNotBlank(bossIdPaths)) {
                        addLeaderListByType(eleId, versionId, bossIdPaths, orgVersionConf.getBossNames(), MultiOrgElementLeader.TYPE_BOSS);
                    }
                    // 添加对应的管理员（人员）
                    if (StringUtils.isNotBlank(orgVersionConf.getManagerIdPaths())) {
                        addLeaderListByType(eleId, versionId, managerIdPaths, orgVersionConf.getManagerNames(), MultiOrgElementLeader.TYPE_MANAGER);
                    }
                }
            }
        }
    }

    /**
     * @param eleId           节点ID
     * @param eleOrgVersionId 组织版本ID
     * @param ids             导入的管理员id路径（有缩减）
     * @param names           导入的管理员名称
     * @param leaderType
     */
    private void addLeaderListByType(String eleId, String eleOrgVersionId, String ids, String names, Integer leaderType) {
        String[] pathArray = ids.split(Separator.SEMICOLON.getValue());
        ArrayList<MultiOrgElementLeader> list = new ArrayList<MultiOrgElementLeader>();
        DataImportTaskLog log = null;
        List<DataImportTaskLog> logs = null;
        for (int i = 0, len = pathArray.length; i < len; i++) {
            String idPath = pathArray[i];
            String[] idAndVer = idPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            String objId = idAndVer[1];// 导入的管理员ID

            // 从已导入的数据中获取管理员
            log = new DataImportTaskLog();
            log.setSourceId(objId);
            logs = dataImportTaskLogService.listAllByPage(log, new PagingInfo(), "createTime desc");

            if (CollectionUtils.isNotEmpty(logs)) {
                log = logs.get(0);
                String newId = log.getAfterImportId();// 导入后的管理员ID

                MultiOrgElementLeader m = new MultiOrgElementLeader();
                m.setEleId(eleId);
                m.setEleOrgVersionId(eleOrgVersionId);
                m.setTargetObjId(newId);

                // 如果管理员不存在，则不设置。关系缺失也算导入成功
                if (objId.startsWith(IdPrefix.USER.getValue())) {
                    MultiOrgUserAccount multiOrgUserAccount = multiOrgUserAccountService.getAccountById(newId);
                    if (null == multiOrgUserAccount) {
                        continue;
                    }
                    MultiOrgUserTreeNode node = new MultiOrgUserTreeNode();
                    node.setUserId(newId);
                    List<MultiOrgUserTreeNode> nodes = multiOrgUserTreeNodeService.listByEntity(node);
                    if (CollectionUtils.isNotEmpty(nodes)) {
                        node = nodes.get(0);
                        m.setTargetObjOrgVersionId(node.getOrgVersionId());
                    } else {
                        continue;
                    }
                } else {
                    MultiOrgElement multiOrgElement = multiOrgElementService.getById(newId);
                    if (null == multiOrgElement) {
                        continue;
                    }
                    MultiOrgTreeNode node = new MultiOrgTreeNode();
                    node.setEleId(newId);
//                    node.setOrgVersionId(eleOrgVersionId);
                    List<MultiOrgTreeNode> nodes = multiOrgTreeNodeService.listByEntity(node);
                    if (CollectionUtils.isNotEmpty(nodes)) {
                        node = nodes.get(0);
                        m.setTargetObjOrgVersionId(node.getOrgVersionId());
                    } else {
                        continue;
                    }
                }

                m.setLeaderType(leaderType);
                list.add(m);
            }
        }
        multiOrgElementLeaderService.saveAll(list);
    }

    /**
     * 判断是否存在相同节点
     *
     * @param versionId
     * @param versionName
     * @param type
     * @param name
     * @param eleNamePath
     * @return
     */
    public boolean existSameNode(String versionId, String versionName, String type, String name, String eleNamePath) {
        if (CollectionUtils.isEmpty(orgNodeInfoDataList)) {
            DataImportApi dataImportApi = ApplicationContextHolder.getBean(DataImportApi.class);
            orgNodeInfoDataList = dataImportApi.getAllNodeInfo(versionId);
        }

        if (!eleNamePath.startsWith(versionName)) {
            eleNamePath = versionName + eleNamePath.replace(eleNamePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[0], "");
        }

        for (OrgNodeInfoData data : orgNodeInfoDataList) {
            if (data.getType().equals(type) && data.getName().equals(name) && data.getEleNamePath().equals(eleNamePath)) {
                return true;
            }
        }

        return false;
    }
}
