package com.wellsoft.pt.workflow.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.basicdata.viewcomponent.facade.service.ViewComponentService;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.element.UnitElement;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.log.entity.BusinessDetailsLog;
import com.wellsoft.pt.log.entity.BusinessOperationLog;
import com.wellsoft.pt.log.service.BusinessDetailsLogService;
import com.wellsoft.pt.log.service.BusinessOperationLogService;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.bean.FlowOpinionBean;
import com.wellsoft.pt.workflow.dto.*;
import com.wellsoft.pt.workflow.entity.FlowOpinion;
import com.wellsoft.pt.workflow.entity.FlowOpinionCategory;
import com.wellsoft.pt.workflow.entity.WfFlowInspectionFileRecordEntity;
import com.wellsoft.pt.workflow.entity.WfFlowSignOpinionSaveTempEntity;
import com.wellsoft.pt.workflow.service.*;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/8/25.1	    zenghw		2021/8/25		    Create
 * </pre>
 * @date 2021/8/25
 */
@Service
public class FlowVisaServiceImpl implements FlowVisaService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FlowInstanceService flowInstanceService;
    @Autowired
    private FlowOpinionService flowOpinionService;
    @Autowired
    private FlowOpinionCategoryService flowOpinionCategoryService;
    @Autowired
    private FlowService flowService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private WfFlowInspectionFileRecordService wfFlowInspectionFileRecordService;
    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;
    @Autowired
    private ViewComponentService viewComponentService;
    @Autowired
    private ReadMarkerService readMarkerService;
    @Autowired
    private FormDefinitionService formDefinitionService;
    @Autowired
    private WorkService workService;
    @Autowired
    private BusinessOperationLogService businessOperationLogService;
    @Autowired
    private BusinessDetailsLogService businessDetailsLogService;
    @Autowired
    private BusinessOperationLogService logBusinessOperationService;
    @Autowired
    private WfFlowSignOpinionSaveTempService wfFlowSignOpinionSaveTempService;

    @Override
    public List<FlowOpinionDto> getUserRecentOpinions(String userId, String flowInstUuid, Integer num) {
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        FlowDefinition flowDefinition = flowService.getFlowDefinition(flowInstance.getFlowDefinition().getUuid());
        List<FlowOpinion> list = flowOpinionService.getUserRecentOpinions(userId, flowDefinition.getId(), "", num);
        List<FlowOpinionDto> flowOpinionDtos = BeanUtils.copyCollection(list, FlowOpinionDto.class);
        return flowOpinionDtos;
    }

    @Override
    public List<FlowOpinionDto> getUserCommonOpinions(String userId, Integer num) {
        FlowOpinionCategory flowOpinionCategory = flowOpinionCategoryService
                .getUserOpinionCategoriesWithoutRecentCategory(userId, "个人意见");
        List<FlowOpinionBean> flowOpinionBeans = flowOpinionService
                .getOpinionBeanByCategory(flowOpinionCategory.getUuid());
        List<FlowOpinionDto> flowOpinionDtos = BeanUtils.copyCollection(flowOpinionBeans, FlowOpinionDto.class);
        for (FlowOpinionDto flowOpinionDto : flowOpinionDtos) {
            flowOpinionDto.setContent(StringUtils.trim(flowOpinionDto.getContent()));
        }
        if (num < flowOpinionDtos.size()) {
            return flowOpinionDtos.subList(0, num);
        }
        return flowOpinionDtos;
    }

    @Override
    @Transactional
    public List<UnitElementDto> getTaskOpinion(String flowInstUuid, String taskId) {
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        if (flowInstance == null) {
            throw new BusinessException("flowInstUuid:" + flowInstUuid + " 对应的流程不存在，请检查数据");
        }
        FlowDefinition flowDefinition = flowService.getFlowDefinitionById(flowInstance.getId());
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);
        TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
        List<UnitElement> optNames = taskElement.getOptNames();
        List<UnitElementDto> list = BeanUtils.copyCollection(optNames, UnitElementDto.class);
        return list;
    }

    @Override
    public List<GetInspectionFileListDto> getInspectionFileList(String flowInstUuid, String[] fieldCodes) {
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        String dataUuid = flowInstance.getDataUuid();
        List<GetInspectionFileListDto> getInspectionFileListDtos = new ArrayList<GetInspectionFileListDto>();
        for (String fieldCode : fieldCodes) {
            List<LogicFileInfo> logicFileInfos = mongoFileService.getNonioFilesFromFolder(dataUuid, fieldCode);
            GetInspectionFileListDto dto = new GetInspectionFileListDto();
            dto.setFieldCode(fieldCode);
            List<GetInspectionFileDto> getInspectionFileDtos = toGetInspectionFileDto(logicFileInfos);
            dto.setGetInspectionFileDtos(getInspectionFileDtos);
            getInspectionFileListDtos.add(dto);
        }
        return getInspectionFileListDtos;
    }

    @Override
    public String getInspectionLogs(String flowInstUuid) {
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        String dataUuid = flowInstance.getDataUuid();
        List<GetInspectionLogsDto> inspectionLogsDtos = new ArrayList<>();
        List<WfFlowInspectionFileRecordEntity> entities = wfFlowInspectionFileRecordService
                .getRecordListByFlowInstUuid(flowInstUuid);
        if (entities.size() == 0) {
            return "";
        }
        return entities.get(0).getInspectionLog();
    }

    @Override
    public Integer getFileNumsByFormFields(String flowInstUuid, String[] fieldCodes) {
        List<GetInspectionFileListDto> getInspectionFileListDtos = getInspectionFileList(flowInstUuid, fieldCodes);
        if (getInspectionFileListDtos.size() == 0) {
            return 0;
        }
        return getInspectionFileListDtos.get(0).getGetInspectionFileDtos().size();
    }

    public void updateInspectionFile(String flowInstUuid, List<String> fileUuids, List<String> inspectionFiles,
                                     String inspectionLog) {
        for (int i = 0; i < fileUuids.size(); i++) {
            String fileUuid = fileUuids.get(i);
            WfFlowInspectionFileRecordEntity recordEntity = wfFlowInspectionFileRecordService
                    .getFlowInspectionFileRecordByFileUuid(fileUuid);
            if (recordEntity == null) {
                recordEntity = new WfFlowInspectionFileRecordEntity();
                recordEntity.setFileUuid(fileUuid);
                recordEntity.setFlowInstUuid(flowInstUuid);
                recordEntity.setInspectionLog(inspectionLog);
                recordEntity.setInspectionFileUuid(inspectionFiles.get(i));
            } else {
                recordEntity.setInspectionFileUuid(inspectionFiles.get(i));
                recordEntity.setInspectionLog(inspectionLog);
            }
            wfFlowInspectionFileRecordService.save(recordEntity);
        }
    }

    @Override
    public String updateInspectionFile(String fileUuid, byte[] inspectionFile) {
        InputStream sbs = new ByteArrayInputStream(inspectionFile);
        MongoFileEntity mongoFileEntity = mongoFileService.saveFile(fileUuid, sbs);
        return mongoFileEntity.getId();
    }

    @Override
    public Boolean updateInspectionFile(String flowInstUuid, String fileUuid, BufferedInputStream inputStream) {
        Boolean flg = true;
        try {
            MongoFileEntity mongoFileEntity = mongoFileService.saveFile(fileUuid, inputStream);
            WfFlowInspectionFileRecordEntity recordEntity = new WfFlowInspectionFileRecordEntity();
            recordEntity.setFileUuid(fileUuid);
            recordEntity.setFlowInstUuid(flowInstUuid);
            recordEntity.setInspectionLog("");
            recordEntity.setInspectionFileUuid(mongoFileEntity.getId());

            wfFlowInspectionFileRecordService.save(recordEntity);
        } catch (Exception e) {
            flg = false;
            logger.error("updateInspectionFile :", e);
        }
        return flg;
    }

    @Override
    public GetBootstrapTableDto getBootstrapTableList(String bootstrapTableUuid, String bootstrapTableKey,
                                                      Integer pageNum, Integer pageSize) {
        GetBootstrapTableDto bootstrapTableDto = new GetBootstrapTableDto();
        AppWidgetDefinition appWidgetDefinition = appWidgetDefinitionService.getById(bootstrapTableUuid);
        String definitionJson = appWidgetDefinition.getDefinitionJson();
        JSONObject definitionJsonObj = JSONObject.parseObject(definitionJson).getJSONObject("configuration");
        String dataStoreId = definitionJsonObj.getString("dataStoreId");

        // 解析每列 columns 对象
        JSONArray columnsJsonArray = definitionJsonObj.getJSONArray("columns");
        // 数据仓库对应的列
        List<TableColumnDto> tableColumnDtos = new ArrayList<>();
        // 关键字 嵌套查询条件
        List<Condition> keyConditions = Lists.newArrayList();
        for (int i = 0; i < columnsJsonArray.size(); i++) {
            TableColumnDto tableColumnDto = new TableColumnDto();
            JSONObject columnsJsonObj = columnsJsonArray.getJSONObject(i);
            tableColumnDto.setTitle(columnsJsonObj.getString("header"));
            tableColumnDto.setColumnName(columnsJsonObj.getString("name"));
            tableColumnDto.setDataType(columnsJsonObj.getString("dataType"));
            // 字段是否隐藏，不显示
            if ("0".equals(columnsJsonObj.getString("hidden"))) {
                tableColumnDto.setIsShow("false");
            } else {
                tableColumnDto.setIsShow("true");
            }

            // 是否关键字查询
            if (StringUtils.isNotBlank(bootstrapTableKey) && "1".equals(columnsJsonObj.getString("keywordQuery"))) {
                Condition condition = new Condition();
                condition.setColumnIndex(columnsJsonObj.getString("name"));
                condition.setType("like");
                condition.setValue(bootstrapTableKey);
                keyConditions.add(condition);
            }
        }

        // 添加固定列
        addFixedTableColumn(tableColumnDtos);
        bootstrapTableDto.setTableColumns(tableColumnDtos);

        // 列表配置默认条件
        String defaultCondition = definitionJsonObj.getString("defaultCondition");

        // 排序规则
        JSONArray defaultSortsJsonArray = definitionJsonObj.getJSONArray("defaultSorts");
        List<BootstrapTableOrderDto> dataOrders = new ArrayList<>();
        if (defaultSortsJsonArray.size() > 0) {
            for (int i = 0; i < defaultSortsJsonArray.size(); i++) {
                BootstrapTableOrderDto dto = new BootstrapTableOrderDto();
                dto.setColumnName(defaultSortsJsonArray.getJSONObject(i).getString("sortName"));
                dto.setOrder(defaultSortsJsonArray.getJSONObject(i).getString("sortOrder"));
                dataOrders.add(dto);
            }
        }
        bootstrapTableDto.setDataOrders(dataOrders);

        // 获取列数据
        DataStoreData dataStoreData = viewComponentService.loadAllData(dataStoreId, bootstrapTableKey, keyConditions,
                defaultCondition, pageNum, pageSize);
        PagingInfo pagingInfo = dataStoreData.getPagination();
        // 分页
        PagingInfoDto pagingInfoDto = new PagingInfoDto();
        BeanUtils.copyProperties(pagingInfo, pagingInfoDto);
        pagingInfoDto.setTotalPages(pagingInfo.getTotalPages());
        bootstrapTableDto.setPagingInfo(pagingInfoDto);
        bootstrapTableDto.setDatas(dataStoreData.getData());

        // 添加固定列的数据
        addFixedColumnDatas(bootstrapTableDto);

        return bootstrapTableDto;
    }

    @Override
    public Integer getBootstrapTableListAllNum(String bootstrapTableUuid) {
        GetBootstrapTableDto bootstrapTableDto = new GetBootstrapTableDto();
        AppWidgetDefinition appWidgetDefinition = appWidgetDefinitionService.getById(bootstrapTableUuid);
        String definitionJson = appWidgetDefinition.getDefinitionJson();
        JSONObject definitionJsonObj = JSONObject.parseObject(definitionJson).getJSONObject("configuration");
        String dataStoreId = definitionJsonObj.getString("dataStoreId");

        // 获取列数据
        DataStoreData dataStoreData = viewComponentService.loadAllData(dataStoreId);
        return dataStoreData.getData().size();
    }

    @Override
    @Transactional
    public Boolean submitFlow(SubmitFlowDto submitFlowDto) {
        Boolean isSuccess = true;
        try {
            WorkBean workBean = workService.getTodo(submitFlowDto.getTaskInstUuid(), submitFlowDto.getFlowInstUuid());
            workBean.setOpinionValue(submitFlowDto.getOpinionPositionValue());
            workBean.setOpinionLabel(submitFlowDto.getOpinionPositionLable());
            workBean.setOpinionText(submitFlowDto.getSignOpinion());
            // 提交手写签批流程
            workService.submit(workBean);
            // 更新手写签批附件
            updateInspectionFile(submitFlowDto.getFlowInstUuid(), submitFlowDto.getFileUuids(),
                    submitFlowDto.getInspectionFileUuids(), submitFlowDto.getInspectionLog());
            // 清空此实例的流程签署意见和立场临时保存表
            wfFlowSignOpinionSaveTempService.deleteByFlowInstUuid(submitFlowDto.getFlowInstUuid());
        } catch (Exception e) {
            isSuccess = false;
            logger.error("提交失败", e);
            // 记录相关的提交失败日志 放到业务操作日志里，通过类型区分开
            saveTaskOperation("submit", submitFlowDto.getFlowInstUuid(), e.getMessage());
        }
        return isSuccess;
    }

    @Override
    @Transactional
    public Boolean saveFlow(SubmitFlowDto submitFlowDto) {
        Boolean isSuccess = true;
        try {
            // 保存手写签批流程
            saveFlowSignOpinionAndOpinionPosition(submitFlowDto.getFlowInstUuid(), submitFlowDto.getSignOpinion(),
                    submitFlowDto.getOpinionPositionLable(), submitFlowDto.getOpinionPositionValue());
            // 更新手写签批附件
            updateInspectionFile(submitFlowDto.getFlowInstUuid(), submitFlowDto.getFileUuids(),
                    submitFlowDto.getInspectionFileUuids(), submitFlowDto.getInspectionLog());
        } catch (Exception e) {
            isSuccess = false;
            logger.error("保存失败", e);
            // 记录相关的提交失败日志 放到业务操作日志里，通过类型区分开
            saveTaskOperation("save", submitFlowDto.getFlowInstUuid(), e.getMessage());
        }
        return isSuccess;
    }

    @Override
    @Transactional
    public Boolean rollbackFlow(RollbackFlowDto rollbackFlowDto) {
        Boolean isSuccess = true;
        try {
            WorkBean workBean = workService.getTodo(rollbackFlowDto.getTaskInstUuid(),
                    rollbackFlowDto.getFlowInstUuid());
            workBean.setOpinionValue(rollbackFlowDto.getOpinionPositionValue());
            workBean.setOpinionLabel(rollbackFlowDto.getOpinionPositionLable());
            workBean.setOpinionText(rollbackFlowDto.getSignOpinion());
            // 直接退回
            if (rollbackFlowDto.getRollbackType().equals(2)) {
                workBean.setActionType(WorkFlowOperation.DIRECT_ROLLBACK);
                workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.DIRECT_ROLLBACK));
                // 此参数一定要设置
                workBean.setRollbackToPreTask(Boolean.TRUE);
            } else {
                // 退回
                workBean.setActionType(WorkFlowOperation.ROLLBACK);
                workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.ROLLBACK));
                // 此参数一定要设置
                workBean.setRollbackToPreTask(Boolean.FALSE);
            }
            workService.rollback(workBean);
            // 更新手写签批附件
            updateInspectionFile(rollbackFlowDto.getFlowInstUuid(), rollbackFlowDto.getFileUuids(),
                    rollbackFlowDto.getInspectionFileUuids(), rollbackFlowDto.getInspectionLog());
        } catch (Exception e) {
            isSuccess = false;
            logger.error("退回失败", e);
        }
        return isSuccess;
    }

    @Override
    public GetSignOpinionAndOpinionPositionDto getSignOpinionAndOpinionPosition(String flowInstUuid, String userId) {
        WfFlowSignOpinionSaveTempEntity entity = wfFlowSignOpinionSaveTempService
                .getSignOpinionAndOpinionPosition(flowInstUuid, userId);
        if (entity == null) {
            return null;
        }
        GetSignOpinionAndOpinionPositionDto detailDto = new GetSignOpinionAndOpinionPositionDto();
        detailDto.setOpinionPositionLable(entity.getOpinionLabel());
        detailDto.setOpinionPositionValue(entity.getOpinionValue());
        detailDto.setSignOpinion(entity.getOpinionText());
        return detailDto;
    }

    // ---------------------------------- private ----------------------------------

    private void saveFlowSignOpinionAndOpinionPosition(String flowInstUuid, String signOpinion,
                                                       String opinionPositionLabel, String opinionPositionValue) {
        WfFlowSignOpinionSaveTempEntity entity = wfFlowSignOpinionSaveTempService
                .getSignOpinionAndOpinionPosition(flowInstUuid, SpringSecurityUtils.getCurrentUserId());
        if (entity == null) {
            entity = new WfFlowSignOpinionSaveTempEntity();
        }
        entity.setFlowInstUuid(flowInstUuid);
        entity.setOpinionText(signOpinion);
        entity.setOpinionLabel(opinionPositionLabel);
        entity.setOpinionValue(opinionPositionValue);
        entity.setUserId(SpringSecurityUtils.getCurrentUserId());
        wfFlowSignOpinionSaveTempService.save(entity);
    }

    /**
     * @param entities
     * @param inspectionLogsDtos
     * @param fieldCode          附件字段编码
     * @return void
     **/
    private void toGetInspectionLogsDto(List<WfFlowInspectionFileRecordEntity> entities,
                                        List<GetInspectionLogsDto> inspectionLogsDtos, String fieldCode) {
        for (WfFlowInspectionFileRecordEntity entity : entities) {
            GetInspectionLogsDto getInspectionLogsDto = new GetInspectionLogsDto();
            getInspectionLogsDto.setFieldCode(fieldCode);
            getInspectionLogsDto.setInspectionLog(entity.getInspectionLog());
            getInspectionLogsDto.setOldFileUuid(entity.getFileUuid());
            inspectionLogsDtos.add(getInspectionLogsDto);
        }
    }

    /**
     * 记录相关的提交失败日志 放到业务操作日志里
     *
     * @param flowInstUuid
     * @return void
     **/
    private void saveTaskOperation(String operationType, String flowInstUuid, String error) {
        BusinessOperationLog source = new BusinessOperationLog();
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        FlowDefinition flowDefinition = flowService.getFlowDefinition(flowInstance.getFlowDefinition().getUuid());
        source.setModuleId(flowDefinition.getModuleId());
        source.setDataDefType(ModuleID.WORKFLOW.getValue());
        source.setDataDefId(flowDefinition.getId());
        source.setDataDefName(flowDefinition.getName());
        if (operationType.equals("submit")) {
            source.setOperation("提交");
            source.setOperation2("流程签批提交失败");
        } else {
            source.setOperation("保存");
            source.setOperation2("流程签批保存失败");
        }
        source.setUserId(SpringSecurityUtils.getCurrentUserId());
        source.setUserName(SpringSecurityUtils.getCurrentUserName());
        source.setDataId(flowInstance.getUuid());
        String title = flowInstance.getTitle();
        source.setDataName(title);
        businessOperationLogService.saveLog(source);
        // LOG_BUSINESS_DETAILS 表要记录详情
        BusinessDetailsLog detail = new BusinessDetailsLog();
        detail.setLogId(source.getUuid());
        detail.setDataDefId(flowDefinition.getId());
        detail.setDataDefName(flowDefinition.getName());
        String dataName = getDyformDataName(detail.getDataDefId(), detail.getDataId());
        detail.setDataName(dataName);
        detail.setAfterValue(error);
        businessDetailsLogService.saveDetail(detail);
    }

    /**
     * 查询历史表单标题
     *
     * @param dataDefId
     * @param dataId
     * @return
     */
    private String getDyformDataName(String dataDefId, String dataId) {
        PagingInfo pagingInfo = new PagingInfo(1, 1, false);
        BusinessOperationLog entity = new BusinessOperationLog();
        entity.setDataDefType(ModuleID.DYFORM.getValue());
        entity.setDataDefId(dataDefId);
        entity.setDataId(dataId);
        List<BusinessOperationLog> entities = logBusinessOperationService.listAllByPage(entity, pagingInfo, null);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0).getDataName();
        }
        BusinessDetailsLog entity2 = new BusinessDetailsLog();
        entity2.setDataDefType(ModuleID.DYFORM.getValue());
        entity2.setDataDefId(dataDefId);
        entity2.setDataId(dataId);
        List<BusinessDetailsLog> entities2 = businessDetailsLogService.listAllByPage(entity2, pagingInfo, null);
        if (CollectionUtils.isNotEmpty(entities2)) {
            return entities2.get(0).getDataName();
        }
        return null;
    }

    /**
     * //添加固定列的数据
     *
     * @param bootstrapTableDto
     * @return void
     **/
    private void addFixedColumnDatas(GetBootstrapTableDto bootstrapTableDto) {
        // 数据状态
        List<String> flowInstUuids = new ArrayList<>();
        for (Map<String, Object> data : bootstrapTableDto.getDatas()) {
            if (StringUtils.isBlank(String.valueOf(data.get("flowInstUuid")))) {
                throw new BusinessException("非流程组件列表数据");
            }
            flowInstUuids.add(String.valueOf(data.get("flowInstUuid")));
            data.put("dataStatus", "false");
        }
        List<String> readFlowInstUuidList = readMarkerService.getReadList(flowInstUuids,
                SpringSecurityUtils.getCurrentUserId());
        for (Map<String, Object> data : bootstrapTableDto.getDatas()) {
            for (String readFlowInstUuid : readFlowInstUuidList) {
                if (String.valueOf(data.get("flowInstUuid")).equals(readFlowInstUuid)) {
                    data.put("dataStatus", "true");
                    break;
                }
            }
        }
        // 附件数量 走独立接口获取
        // 找学敏沟通结论：请按OA模块里配置的签批单据上的附件字段来计算附件个数
        // 数据对应表单ID
        for (Map<String, Object> data : bootstrapTableDto.getDatas()) {
            FormDefinition formDefinition = formDefinitionService
                    .findDyFormFormDefinitionByFormUuid(String.valueOf(data.get("formUuid")));
            data.put("formId", formDefinition.getId());
        }

    }

    /**
     * 转化为手写签批附件列表
     *
     * @param logicFileInfos
     * @return java.util.List<com.wellsoft.pt.workflow.dto.GetInspectionFileDto>
     **/
    private List<GetInspectionFileDto> toGetInspectionFileDto(List<LogicFileInfo> logicFileInfos) {
        if (logicFileInfos == null || logicFileInfos.size() == 0) {
            new ArrayList<GetInspectionFileDto>();
        }
        List<GetInspectionFileDto> getInspectionFileDtos = new ArrayList<>();
        for (LogicFileInfo logicFileInfo : logicFileInfos) {
            GetInspectionFileDto dto = new GetInspectionFileDto();
            dto.setFileName(logicFileInfo.getFileName());
            dto.setFilesize(String.valueOf(logicFileInfo.getFileSize()));
            String fileType = logicFileInfo.getFileName().split("\\.")[1];
            dto.setFileType(fileType);
            dto.setFileUuid(logicFileInfo.getFileID());
            dto.setCreator(logicFileInfo.getCreator());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createTimeStr = simpleDateFormat.format(logicFileInfo.getCreateTime());
            dto.setCreateTime(createTimeStr);
            getInspectionFileDtos.add(dto);
        }
        return getInspectionFileDtos;
    }

    /**
     * 添加固定列
     *
     * @param tableColumnDtos
     * @return void
     **/
    private void addFixedTableColumn(List<TableColumnDto> tableColumnDtos) {
        // 数据状态
        TableColumnDto tableColumnDto = new TableColumnDto();
        tableColumnDto.setColumnName("dataStatus");
        tableColumnDto.setTitle("阅读状态");
        tableColumnDto.setDataType("Boolean");
        tableColumnDto.setIsShow("true");
        tableColumnDtos.add(tableColumnDto);
        //// 附件数量
        // tableColumnDto = new TableColumnDto();
        // tableColumnDto.setColumnName("fileNum");
        // tableColumnDto.setTitle("文件数量");
        // tableColumnDto.setDataType("Integer");
        // tableColumnDto.setIsShow("true");
        // tableColumnDtos.add(tableColumnDto);
        //// 流程实例UUID
        // tableColumnDto = new TableColumnDto();
        // tableColumnDto.setColumnName("flowInstUuid");
        // tableColumnDto.setTitle("流程实例UUID");
        // tableColumnDto.setDataType("String");
        // tableColumnDto.setIsShow("true");
        // tableColumnDtos.add(tableColumnDto);
        // 数据对应表单ID
        tableColumnDto = new TableColumnDto();
        tableColumnDto.setColumnName("formId");
        tableColumnDto.setTitle("数据对应表单ID");
        tableColumnDto.setDataType("String");
        tableColumnDto.setIsShow("true");
        tableColumnDtos.add(tableColumnDto);
    }
}
