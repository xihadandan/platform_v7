package com.wellsoft.pt.ei.facade;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.context.util.io.ClobUtils;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.OrgPendingInfo;
import com.wellsoft.pt.ei.dto.org.*;
import com.wellsoft.pt.ei.entity.DataImportRecord;
import com.wellsoft.pt.ei.entity.DataImportTask;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.ei.processor.utils.JsonUtils;
import com.wellsoft.pt.ei.service.DataImportRecordService;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.ei.service.DataImportTaskService;
import com.wellsoft.pt.ei.utils.OrgDataImportUtils;
import com.wellsoft.pt.multi.org.bean.OrgElementAttrVo;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Description: 数据导入的接口查询处理
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
@Component
@Scope("prototype")
public class DataImportApi {
    private static final String SYSTEM_UNIT_ID_PATTERN = IdPrefix.SYSTEM_UNIT.getValue() + "0000000000";
    private static final String DEPT_ID_PATTERN = IdPrefix.DEPARTMENT.getValue() + "0000000000";
    private static final String JOB_ID_PATTERN = IdPrefix.JOB.getValue() + "0000000000";
    private static final String ORG_ID_PATTERN = IdPrefix.ORG.getValue() + "0000000000";
    private static final String ORG_VERSION_ID_PATTERN = IdPrefix.ORG_VERSION.getValue() + "0000000000";
    private static final String BUSINESS_UNIT_ID_PATTERN = IdPrefix.BUSINESS_UNIT.getValue() + "0000000000";
    private static final String DUTY_ID_PATTERN = IdPrefix.DUTY.getValue() + "0000000000";
    private static final String JOB_RANK_ID_PATTERN = IdPrefix.RANK.getValue() + "0000000000";
    private static final String USER_ID_PATTERN = IdPrefix.USER.getValue() + "0000000000";
    private static final String GROUP_ID_PATTERN = IdPrefix.GROUP.getValue() + "0000000000";

    @Autowired
    private DataImportRecordService dataImportRecordService;

    @Autowired
    private DataImportTaskService dataImportTaskService;

    @Autowired
    private DataImportTaskLogService dataImportTaskLogService;

    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;

    @Autowired
    private MultiOrgService multiOrgService;

    @Autowired
    private MultiOrgElementService multiOrgElementService;

    @Autowired
    private MultiOrgElementAttrService multiOrgElementAttrService;

    @Autowired
    private MultiOrgDutyService multiOrgDutyService;

    @Autowired
    private MultiOrgJobRankService multiOrgJobRankService;

    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;

    @Autowired
    private MultiOrgUserInfoService multiOrgUserInfoService;

    @Autowired
    private MultiOrgUserWorkInfoService multiOrgUserWorkInfoService;

    @Autowired
    private IdGeneratorService idGeneratorService;

    private List<OrgPendingInfo> orgPendingInfos = Lists.newArrayList();

    public void saveDataImportTask(DataImportTask dataImportTask) {
        dataImportTaskService.save(dataImportTask);
    }

    public void saveDataImportRecord(DataImportRecord record) {
        dataImportRecordService.save(record);
    }

    public void importOrgData(DataImportRecord record, DataImportTask task) throws IOException {
        // 导入顺序：组织架构节点>职务>职级>用户>群组
        // 根据导入路径、导入的文件进行处理

        // 将文件按类型归类，处理时，直接按照类型处理对应的文件
        String importPath = record.getImportPath() + File.separator + DataExportConstants.DATA_TYPE_ORG + File.separator;
        String[] fileNames = ClobUtils.ClobToString(record.getImportFiles()).split(Separator.COMMA.getValue());
        List<JSONObject> orgVersions = Lists.newArrayList();
        List<JSONArray> orgDutys = Lists.newArrayList();
        List<JSONArray> orgRanks = Lists.newArrayList();
        List<JSONArray> orgUsers = Lists.newArrayList();
        List<JSONArray> orgGroups = Lists.newArrayList();
        File jsonFile;
        String[] fileTypes;
        for (String fileName : fileNames) {
            jsonFile = new File(importPath + fileName);
            fileTypes = fileName.split(Separator.UNDERLINE.getValue());

            String dataType = fileTypes[0];
            String dataChildType = fileTypes[1].replaceAll("\\d+", "");
            if (DataExportConstants.DATA_TYPE_ORG.equals(dataType)) {
                String jsonStr = FileUtils.readFileToString(jsonFile, "UTF-8");
                switch (dataChildType) {
                    case DataExportConstants.DATA_TYPE_ORG_VERSION:
                        orgVersions.add(JSONObject.fromObject(jsonStr));
                        break;
                    case DataExportConstants.DATA_TYPE_ORG_DUTY:
                        orgDutys.add(JSONArray.fromObject(jsonStr));
                        break;
                    case DataExportConstants.DATA_TYPE_ORG_RANK:
                        orgRanks.add(JSONArray.fromObject(jsonStr));
                        break;
                    case DataExportConstants.DATA_TYPE_ORG_USER:
                        orgUsers.add(JSONArray.fromObject(jsonStr));
                        break;
                    case DataExportConstants.DATA_TYPE_ORG_GROUP:
                        orgGroups.add(JSONArray.fromObject(jsonStr));
                        break;
                    default:
                        break;
                }
            }
        }

        List<String> dataTypes = OrgDataImportUtils.typeStr2List(record.getDataTypeJson());
        if (dataTypes.contains(DataExportConstants.DATA_TYPE_ORG_VERSION)) {
            // 组织架构导入 - 节点
            for (JSONObject tmp : orgVersions) {
                if (!importOrgVersionData(record, task, tmp)) {
                    return;
                }
            }
        }

        if (dataTypes.contains(DataExportConstants.DATA_TYPE_ORG_DUTY)) {
            // 职务导入
            for (JSONArray orgDuty : orgDutys) {
                if (!importOrgDutyData(record, task, orgDuty)) {
                    return;
                }
            }
        }

        if (dataTypes.contains(DataExportConstants.DATA_TYPE_ORG_RANK)) {
            // 职级导入
            for (JSONArray orgRank : orgRanks) {
                if (!importOrgRankData(record, task, orgRank)) {
                    return;
                }
            }
        }

        if (dataTypes.contains(DataExportConstants.DATA_TYPE_ORG_USER)) {
            // 用户导入
            for (JSONArray orgUser : orgUsers) {
                if (!importOrgUserData(record, task, orgUser)) {
                    return;
                }
            }
        }

        if (dataTypes.contains(DataExportConstants.DATA_TYPE_ORG_GROUP)) {
            // 群组导入
        }

        // TODO 统一处理父级节点、管理员信息


        // json文件中的类型，与本次导入的数据类型是否匹配

        // 判断要导入的数据是否重复，根据重复策略进行

        // 组织节点：判断必填项、父级节点是否存在、判断引用的系统单位是否存在，否则按照必填项缺失处理

        // 数据依赖校验

        // 按文本路径导入节点，根据id判断，如果节点存在，则建立层级关系，如果节点不存在，则一层层创建节点，建立层级关系

        // 保存负责人、管理员、分管领导的信息（在用户导入后，查询用户是否存在，存在则设置，不存在就不管了）

        // 保存组织版本信息
    }

    public boolean importOrgUserData(DataImportRecord record, DataImportTask task, JSONArray orgUser) {
        if (null != orgUser && !orgUser.isEmpty()) {
            JSONObject user;
            OrgUserData orgUserData;
            String type = DataExportConstants.DATA_TYPE_ORG_USER;
            String uuid, id, userName, loginName, code;
            for (int i = 0, len = orgUser.size(); i < len; i++) {
                user = orgUser.getJSONObject(i);
                orgUserData = (OrgUserData) JSONObject.toBean(user, OrgUserData.class);
                uuid = orgUserData.getUuid();
                id = orgUserData.getId();
                userName = orgUserData.getUserName();
                loginName = orgUserData.getUserName();
                code = orgUserData.getCode();
                DataImportTaskLog log = createDataImportTaskLog(task, DataExportConstants.DATA_TYPE_ORG_VERSION + "-" + type, uuid, id, JsonUtils.toJsonStr(user));

                // 重复校验
                DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(uuid);
                if (null != dataImportTaskLog) {
                    if (DataExportConstants.DATA_REPEAT_STRATEGY_SKIP.intValue() == record.getRepeatStrategy().intValue()) {
                        log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
                        log.setIsRepeat(1);
                        dataImportTaskLogService.save(log);
                        continue;
                    } else if (DataExportConstants.DATA_REPEAT_STRATEGY_REPLACE.intValue() == record.getRepeatStrategy().intValue()) {
                        log.setIsRepeat(1);
                    }
                }

                // 必填校验
                if (StringUtils.isBlank(userName) || StringUtils.isBlank(loginName) || StringUtils.isBlank(code)) {
                    // 异常的数据，无论终止还是跳过，导入状态都为失败
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                    log.setErrorMsg("数据必填项缺失");
                    dataImportTaskLogService.save(log);

                    // 异常策略：跳过
                    if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                        continue;
                    } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                        return false;
                    }
                }

                // 依赖校验

                // 直属上级领导的关联。可能是职位，也可能是用户，在用户全导入后处理

                MultiOrgUserAccount multiOrgUserAccount = null;
                if (null != dataImportTaskLog) {
                    multiOrgUserAccount = multiOrgUserAccountService.getOne(dataImportTaskLog.getAfterImportUuid());
                }
                if (null == multiOrgUserAccount) {
                    multiOrgUserAccount = new MultiOrgUserAccount();
                }

                // 账号信息处理
                BeanUtils.copyProperties(orgUserData, multiOrgUserAccount, "uuid");
                // ID需重新生成
                multiOrgUserAccount.setId(generateElementId(type));
                // 密码统一设置
                multiOrgUserAccount.setPassword(record.getSettingPwd());
                multiOrgUserAccountService.save(multiOrgUserAccount);

                // 个人信息处理
                MultiOrgUserInfo multiOrgUserInfo = multiOrgUserInfoService.getByUserId(multiOrgUserAccount.getId());
                if (null == multiOrgUserInfo) {
                    multiOrgUserInfo = new MultiOrgUserInfo();
                }

                BeanUtils.copyProperties(orgUserData, multiOrgUserInfo, "uuid");
                multiOrgUserInfo.setUserId(multiOrgUserAccount.getId());
                multiOrgUserInfoService.save(multiOrgUserInfo);

                // 工作信息处理
                MultiOrgUserWorkInfo multiOrgUserWorkInfo = multiOrgUserWorkInfoService.getUserWorkInfo(multiOrgUserAccount.getId());
                if (null == multiOrgUserWorkInfo) {
                    multiOrgUserWorkInfo = new MultiOrgUserWorkInfo();
                }
                if (StringUtils.isNotBlank(orgUserData.getUserName())) {

                }

                log.setAfterImportId(multiOrgUserAccount.getId());
                log.setAfterImportUuid(multiOrgUserAccount.getUuid());
                dataImportTaskLogService.save(log);
            }
        }

        return true;
    }

    public boolean importOrgRankData(DataImportRecord record, DataImportTask task, JSONArray orgRank) {
        if (null != orgRank && !orgRank.isEmpty()) {
            JSONObject rank;
            OrgJobRankData orgJobRankData;
            String type = DataExportConstants.DATA_TYPE_ORG_RANK;
            String uuid = null, id = null, name = null;
            for (int i = 0, len = orgRank.size(); i < len; i++) {
                rank = orgRank.getJSONObject(i);
                orgJobRankData = (OrgJobRankData) JSONObject.toBean(rank, OrgJobRankData.class);
                uuid = orgJobRankData.getUuid();
                id = orgJobRankData.getId();
                name = orgJobRankData.getName();

                DataImportTaskLog log = createDataImportTaskLog(task, DataExportConstants.DATA_TYPE_ORG_VERSION + "-" + type, uuid, id, rank.toString());

                // 数据类型校验
                if (!DataExportConstants.DATA_TYPE_ORG_RANK.equals(type)) {
                    // 异常的数据，无论终止还是跳过，导入状态都为失败
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                    log.setErrorMsg(DataExportConstants.DATA_TYPE_NO_MATCH);
                    dataImportTaskLogService.save(log);

                    // 异常策略：跳过
                    if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                        continue;
                    } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                        return false;
                    }
                }

                // 重复校验
                DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(uuid);
                if (null != dataImportTaskLog) {
                    if (DataExportConstants.DATA_REPEAT_STRATEGY_SKIP.intValue() == record.getRepeatStrategy().intValue()) {
                        log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
                        log.setIsRepeat(1);
                        dataImportTaskLogService.save(log);
                        continue;
                    } else if (DataExportConstants.DATA_REPEAT_STRATEGY_REPLACE.intValue() == record.getRepeatStrategy().intValue()) {
                        log.setIsRepeat(1);
                    }
                }

                // 必填校验
                if (StringUtils.isBlank(type) || StringUtils.isBlank(name)) {
                    // 异常的数据，无论终止还是跳过，导入状态都为失败
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                    log.setErrorMsg("数据必填项缺失");
                    dataImportTaskLogService.save(log);

                    // 异常策略：跳过
                    if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                        continue;
                    } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                        return false;
                    }
                }

                // 依赖校验

                MultiOrgJobRank multiOrgJobRank = null;
                if (null != dataImportTaskLog) {
                    multiOrgJobRank = multiOrgJobRankService.getOne(dataImportTaskLog.getAfterImportUuid());
                }
                if (null == multiOrgJobRank) {
                    multiOrgJobRank = new MultiOrgJobRank();
                    if (StringUtils.isNotBlank(id)) {
                        multiOrgJobRank.setId(id);
                    } else {
                        multiOrgJobRank.setId(generateElementId(type));
                    }
                }
                multiOrgJobRank.setCode(orgJobRankData.getCode());
                multiOrgJobRank.setName(name);
                multiOrgJobRank.setSystemUnitId(record.getImportUnitId());
                multiOrgJobRankService.save(multiOrgJobRank);

                log.setAfterImportId(multiOrgJobRank.getId());
                log.setAfterImportUuid(multiOrgJobRank.getUuid());
                dataImportTaskLogService.save(log);
            }
        }
        return true;
    }

    public boolean importOrgDutyData(DataImportRecord record, DataImportTask task, JSONArray orgDuty) {
        if (null != orgDuty && !orgDuty.isEmpty()) {
            JSONObject duty;
            OrgDutyData orgDutyData;
            String uuid, id, name, code, type = DataExportConstants.DATA_TYPE_ORG_DUTY;
            for (int i = 0, len = orgDuty.size(); i < len; i++) {
                duty = orgDuty.getJSONObject(i);
                orgDutyData = (OrgDutyData) JSONObject.toBean(duty, OrgDutyData.class);
                uuid = orgDutyData.getUuid();
                id = orgDutyData.getId();
                name = orgDutyData.getName();
                code = orgDutyData.getCode();
                DataImportTaskLog log = createDataImportTaskLog(task, DataExportConstants.DATA_TYPE_ORG_DUTY, uuid, id, duty.toString());

                // 数据类型校验
                /*if (!DataExportConstants.DATA_TYPE_ORG_DUTY.equals(type)) {
                    // 异常的数据，无论终止还是跳过，导入状态都为失败
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                    log.setErrorMsg("数据类型不匹配");
                    dataImportTaskLogService.save(log);

                    // 异常策略：跳过
                    if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                        continue;
                    } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                        return false;
                    }
                }*/

                // 重复校验
                DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(uuid);
                if (null != dataImportTaskLog) {
                    if (DataExportConstants.DATA_REPEAT_STRATEGY_SKIP.intValue() == record.getRepeatStrategy().intValue()) {
                        log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
                        log.setIsRepeat(1);
                        dataImportTaskLogService.save(log);
                        continue;
                    } else if (DataExportConstants.DATA_REPEAT_STRATEGY_REPLACE.intValue() == record.getRepeatStrategy().intValue()) {
                        log.setIsRepeat(1);
                    }
                }

                // 必填项校验
                if (StringUtils.isBlank(type) || StringUtils.isBlank(name) || StringUtils.isBlank(code)) {
                    // 异常的数据，无论终止还是跳过，导入状态都为失败
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                    log.setErrorMsg("数据必填项缺失");
                    dataImportTaskLogService.save(log);

                    // 异常策略：跳过
                    if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                        continue;
                    } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                        return false;
                    }
                }

                // 依赖校验

                MultiOrgDuty multiOrgDuty = null;
                if (null != dataImportTaskLog) {
                    multiOrgDuty = multiOrgDutyService.getOne(dataImportTaskLog.getAfterImportUuid());
                }
                if (null == multiOrgDuty) {
                    multiOrgDuty = new MultiOrgDuty();
                    if (StringUtils.isNotBlank(id)) {
                        multiOrgDuty.setId(id);
                    } else {
                        multiOrgDuty.setId(IdPrefix.DUTY.getValue() + Separator.UNDERLINE.getValue() + SnowFlake.getId());
                    }
                }
                multiOrgDuty.setCode(code);
                multiOrgDuty.setName(name);
                multiOrgDuty.setRemark(orgDutyData.getRemark());
                multiOrgDuty.setSapCode(orgDutyData.getSapCode());
                multiOrgDuty.setSystemUnitId(record.getImportUnitId());
                multiOrgDutyService.save(multiOrgDuty);

                log.setAfterImportId(multiOrgDuty.getId());
                log.setAfterImportUuid(multiOrgDuty.getUuid());
                dataImportTaskLogService.save(log);
            }
        }

        return true;
    }

    public boolean importOrgVersionData(DataImportRecord record, DataImportTask task, JSONObject orgVersion) {
        // 基本信息不导入，直接从配置信息开始

        // 配置信息：组织节点（orgNodes）、业务单位（businessUnits）、部门（departments）、职位（jobs）、引用系统单位（orgVersions）
        String[] orgConfigs = new String[]{"orgNodes", "businessUnits", "departments", "jobs", "orgVersions"};
        for (String str : orgConfigs) {
            JSONArray orgNodes = orgVersion.getJSONArray(str);

            List<OrgVersionConf> orgVersionConfs = JSONArray.toList(orgNodes, OrgVersionConf.class, new JsonConfig());

            boolean result = importOrgVersionConfig(orgVersionConfs, record, task, str);
            if (!result) {
                return result;
            }
        }

        return true;
    }

    public boolean importOrgVersionConfig(List<OrgVersionConf> orgNodes, DataImportRecord record, DataImportTask task, String str) {
        String type = "";

        switch (str) {
            case "orgNodes":
                type = "组织节点";
                break;
            case "businessUnits":
                type = "业务单位";
                break;
            case "departments":
                type = "部门";
                break;
            case "jobs":
                type = "职位";
                break;
            case "orgVersions":
                type = "引用系统单位";
                break;
        }

        String uuid, id, name, code, parentEleIdPath, parentEleNamePath;
        for (OrgVersionConf orgNode : orgNodes) {
            uuid = orgNode.getEleUuid();
            id = orgNode.getEleId();
            name = orgNode.getName();
            code = orgNode.getCode();
            parentEleIdPath = orgNode.getParentEleIdPath();
            parentEleNamePath = orgNode.getParentEleNamePath();
            DataImportTaskLog log = createDataImportTaskLog(task, DataExportConstants.DATA_TYPE_ORG_VERSION + "-" + type, uuid, id, orgNode.toString());

            // 重复校验
            DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(uuid);

            if (null != dataImportTaskLog) {
                // 重复的数据，无论覆盖还是跳过，导入状态都为正常
                // 重复策略：跳过
                if (DataExportConstants.DATA_REPEAT_STRATEGY_SKIP.intValue() == record.getRepeatStrategy().intValue()) {
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
                    log.setIsRepeat(1);
                    dataImportTaskLogService.save(log);
                    continue;
                } else if (DataExportConstants.DATA_REPEAT_STRATEGY_REPLACE.intValue() == record.getRepeatStrategy().intValue()) {
                    log.setIsRepeat(1);
                }
            }

            // 必填项校验（名称、类型、编号、父级节点）
            if (StringUtils.isBlank(name) || StringUtils.isBlank(type) || StringUtils.isBlank(code) || StringUtils.isBlank(parentEleIdPath) || StringUtils.isBlank(parentEleNamePath)) {
                // 异常的数据，无论终止还是跳过，导入状态都为失败
                log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                log.setErrorMsg("数据必填项缺失");
                dataImportTaskLogService.save(log);

                // 异常策略：跳过
                if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                    continue;
                } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                    return false;
                }
            }

            String eleId = "";
            String eleUuid = "";
            // 依赖校验  引用系统单位才有依赖校验，并且引用系统单位要求该组织版本节点存在，那么如果存在就不需要导入了
            if (type.equals(DataExportConstants.DATA_TYPE_ORG_VERSION_V)) {
                MultiOrgElement relyOn = new MultiOrgElement();
                relyOn.setId(id);
                relyOn.setName(name);
                List<MultiOrgElement> list = multiOrgElementService.listByEntity(relyOn);
                if (null == list || list.size() == 0) {
                    log.setImportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
                    log.setErrorMsg("数据依赖缺失");
                    dataImportTaskLogService.save(log);

                    if (DataExportConstants.DATA_ERROR_STRATEGY_SKIP.intValue() == record.getErrorStrategy().intValue()) {
                        continue;
                    } else if (DataExportConstants.DATA_ERROR_STRATEGY_END.intValue() == record.getErrorStrategy().intValue()) {
                        return false;
                    }
                }
                eleId = id;
                eleUuid = uuid;
            } else {
                MultiOrgElement ele = null;
                MultiOrgTreeNode node = null;
                if (null != dataImportTaskLog) {
                    // 对原先的节点数据进行更新
                    ele = multiOrgElementService.getById(dataImportTaskLog.getAfterImportId());
                }

                // 新建节点
                if (null == ele) {
                    ele = new MultiOrgElement();
                    if (StringUtils.isNotBlank(id)) {
                        ele.setId(id);
                    } else {
                        ele.setId(generateElementId(type));
                    }
                }
                ele.setName(name);
                ele.setShortName(orgNode.getShortName());
                ele.setType(IdPrefix.ORG.getValue());
                ele.setCode(code);
                ele.setRemark(orgNode.getRemark());
                ele.setSapCode(orgNode.getSapCode());

                multiOrgElementService.save(ele);

                eleId = ele.getId();
                eleUuid = ele.getUuid();
                node = multiOrgTreeNodeService.queryByVerIdEleId(record.getVersionId(), eleId);
                if (null == node) {
                    node = new MultiOrgTreeNode();
                }

                // 同步数据到组织树中去，添加子节点数据
                node.setEleId(ele.getId());
                node.setOrgVersionId(record.getVersionId());
                multiOrgTreeNodeService.save(node);
                List<OrgElementAttrVo> orgElementAttrVos = getOrgElementAttrVos(orgNode.getOrgElementAttrs(), ele.getUuid());
                multiOrgElementAttrService.saveDtos(orgElementAttrVos);
            }

            log.setAfterImportId(eleId);
            log.setAfterImportUuid(eleUuid);
            dataImportTaskLogService.save(log);

            // TODO 父级节点，在所有节点导入后处理，包括节点的eleIdPath；管理员信息在用户导入后处理；
            OrgPendingInfo orgPendingInfo = new OrgPendingInfo();
            orgPendingInfo.setId(eleId);
            orgPendingInfo.setSourceId(id);
            orgPendingInfo.setRecord(record);
            orgPendingInfo.setTask(task);
            orgPendingInfo.setLog(log);
            orgPendingInfo.setEleIdPath(orgNode.getEleIdPath());
            orgPendingInfo.setParentEleIdPath(parentEleIdPath);
            orgPendingInfo.setParentEleNamePath(parentEleNamePath);
            orgPendingInfo.setBossIdPaths(orgNode.getBossIdPaths());
            orgPendingInfo.setBossNames(orgNode.getBossNames());
            orgPendingInfo.setManagerIdPaths(orgNode.getManagerIdPaths());
            orgPendingInfo.setManagerNames(orgNode.getManagerNames());
            orgPendingInfo.setBranchLeaderIdPaths(orgNode.getBranchLeaderIdPaths());
            orgPendingInfo.setBranchLeaderNames(orgNode.getBranchLeaderNames());
            orgPendingInfo.setType(type);
            orgPendingInfos.add(orgPendingInfo);
        }
        return true;
    }

    public DataImportTaskLog createDataImportTaskLog(DataImportTask task, String dataChildType, String sourceUuid, String sourceId, String sourceData) {
        DataImportTaskLog dataImportTaskLog = new DataImportTaskLog();
        dataImportTaskLog.setDataType(task.getDataType());
        dataImportTaskLog.setDataChildType(dataChildType);
        dataImportTaskLog.setImportTime(new Date());
        dataImportTaskLog.setTaskUuid(task.getUuid());
        dataImportTaskLog.setSourceUuid(sourceUuid);
        dataImportTaskLog.setSourceId(sourceId);
        dataImportTaskLog.setSourceData(sourceData);
        dataImportTaskLogService.save(dataImportTaskLog);
        return dataImportTaskLog;
    }

    public String generateElementId(String type) {
        if (DataExportConstants.DATA_TYPE_ORG_VERSION_B.equals(type) || IdPrefix.BUSINESS_UNIT.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgUnit.class, BUSINESS_UNIT_ID_PATTERN);
        } else if (DataExportConstants.DATA_TYPE_ORG_VERSION_D.equals(type) || IdPrefix.DEPARTMENT.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgDept.class, DEPT_ID_PATTERN);
        } else if (DataExportConstants.DATA_TYPE_ORG_VERSION_J.equals(type) || IdPrefix.JOB.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgJob.class, JOB_ID_PATTERN);
        } else if (DataExportConstants.DATA_TYPE_ORG_VERSION_O.equals(type) || IdPrefix.ORG.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgVersion.class, ORG_ID_PATTERN);
        } else if (DataExportConstants.DATA_TYPE_ORG_VERSION_V.equals(type) || IdPrefix.ORG_VERSION.getValue().equals(type)) {
            return idGeneratorService.generate(MultiOrgVersion.class, ORG_VERSION_ID_PATTERN);
        } else if (DataExportConstants.DATA_TYPE_ORG_DUTY.equals(type)) {
            return idGeneratorService.generate(MultiOrgDuty.class, DUTY_ID_PATTERN);
        } else if (DataExportConstants.DATA_TYPE_ORG_RANK.equals(type)) {
            return idGeneratorService.generate(MultiOrgJobRank.class, JOB_RANK_ID_PATTERN);
        } else if (DataExportConstants.DATA_TYPE_ORG_USER.equals(type)) {
            return idGeneratorService.generate(MultiOrgUserAccount.class, USER_ID_PATTERN);
        } else if (DataExportConstants.DATA_TYPE_ORG_GROUP.equals(type)) {
            return idGeneratorService.generate(MultiOrgGroup.class, GROUP_ID_PATTERN);
        }
        return null;
    }

    public List<OrgElementAttrVo> getOrgElementAttrVos(List<OrgElementAttr> orgElementAttrs, String eleUuid) {
        List<OrgElementAttrVo> orgElementAttrVos = Lists.newArrayList();

        OrgElementAttrVo orgElementAttrVo;
        for (OrgElementAttr orgElementAttr : orgElementAttrs) {
            orgElementAttrVo = new OrgElementAttrVo();
            BeanUtils.copyProperties(orgElementAttr, orgElementAttrVo);
            orgElementAttrVo.setElementUuid(eleUuid);
            orgElementAttrVos.add(orgElementAttrVo);
        }

        return orgElementAttrVos;
    }

    /**
     * 获取任务下的异常日志数量
     *
     * @param taskUuid
     * @return
     */
    public long getErrorTaskLogs(String taskUuid) {
        return dataImportTaskLogService.countByStatus(taskUuid, DataExportConstants.DATA_LOG_STATUS_ERROR);
    }

    /**
     * 获取组织版本下的所有节点信息，按照层级由浅到深排序
     *
     * @param orgVersionId
     * @return
     */
    public List<OrgNodeInfoData> getAllNodeInfo(String orgVersionId) {
        List<OrgNodeInfoData> list = Lists.newArrayList();

        // 获取该版本所有的树节点，
        List<OrgTreeNodeDto> objs = this.multiOrgTreeNodeService.queryAllNodeOfOrgVersionByEleIdPath(orgVersionId, orgVersionId);
        if (objs == null) {
            return null;
        }

        // 设置节点的中文路径
        Map<String, MultiOrgElement> allEleMap = this.multiOrgElementService.queryElementMapByOrgVersion(orgVersionId);
        for (OrgTreeNodeDto row : objs) {
            row.computeEleNamePath(allEleMap);
        }

        OrgNodeInfoData data;
        for (OrgTreeNodeDto row : objs) {
            data = new OrgNodeInfoData();
            com.wellsoft.pt.jpa.util.BeanUtils.copyProperties(row, data);
            list.add(data);
        }

        // 按照层级排序
        Collections.sort(list, new Comparator<OrgNodeInfoData>() {
            @Override
            public int compare(OrgNodeInfoData o1, OrgNodeInfoData o2) {
                int len1 = o1.getEleIdPath().split(MultiOrgService.PATH_SPLIT_SYSMBOL).length;
                int len2 = o2.getEleIdPath().split(MultiOrgService.PATH_SPLIT_SYSMBOL).length;
                return len1 - len2;
            }
        });

        return list;
    }
}
