package com.wellsoft.pt.ei.service.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.entity.CdDataStoreDefinition;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreConfiguration;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreProxy;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreType;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskActivity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceService;
import com.wellsoft.pt.bpm.engine.service.TaskActivityService;
import com.wellsoft.pt.bpm.engine.service.TaskInstanceService;
import com.wellsoft.pt.bpm.engine.service.TaskOperationService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.combiner.dto.impl.DyFormDataImpl;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.ei.constants.DataExportConstants;
import com.wellsoft.pt.ei.dto.ExportFieldItemData;
import com.wellsoft.pt.ei.dto.ImportEntity;
import com.wellsoft.pt.ei.dto.flow.exportNew.*;
import com.wellsoft.pt.ei.dto.flow.ipt.ImportFlowAddData;
import com.wellsoft.pt.ei.entity.DataExportRecord;
import com.wellsoft.pt.ei.entity.DataImportRecord;
import com.wellsoft.pt.ei.entity.DataImportTaskLog;
import com.wellsoft.pt.ei.processor.utils.AttachFieldDesc;
import com.wellsoft.pt.ei.processor.utils.ExpImpServiceBeanUtils;
import com.wellsoft.pt.ei.processor.utils.FieldDesc;
import com.wellsoft.pt.ei.processor.utils.ImportExportUtils;
import com.wellsoft.pt.ei.service.DataImportTaskLogService;
import com.wellsoft.pt.ei.service.ExpImpService;
import com.wellsoft.pt.ei.utils.FlowDataImportUtils;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.wellsoft.pt.ei.constants.ExportFieldTypeEnum.*;

@Service
@Transactional(readOnly = true)
public class FlowExpImpService implements ExpImpService<FlowExportJsonItemData, FlowInstance> {

    public static final ThreadLocal<AttachFieldDesc> dyFormAttachFieldDescThreadLocal = new ThreadLocal<>();
    public static final ThreadLocal<Map<String, FlowDataImportUtils.FlwDescJsonData>> flwDescJsonDataFieldDescMapThreadLocal = new ThreadLocal<>();
    public static final ThreadLocal<DataExportRecord> dataExportRecordThreadLocal = new ThreadLocal<>();
    public static final ThreadLocal<DataImportRecord> dataImportRecordThreadLocal = new ThreadLocal<>();
    public static final ThreadLocal<ImportFlowAddData> importFlowAddDataThreadLocal = new ThreadLocal<>();
    private static final Log log = LogFactory.getLog(FlowExpImpService.class);
    @Resource
    private FlowInstanceService flowInstanceService;
    @Resource
    private TaskInstanceService taskInstanceService;

//    @Resource
//    private MultiOrgSystemUnitService multiOrgSystemUnitService;
    @Resource
    private TaskOperationService taskOperationService;

//    @Resource
//    private FlowDefinitionService flowDefinitionService;
//
//    @Resource
//    private AclService aclService;
    @Resource
    private TaskActivityService taskActivityService;
    @Resource
    private FormDefinitionService formDefinitionService;
    @Resource
    private DyFormFacade dyFormFacade;
    @Resource
    private MongoFileService mongoFileService;
    @Resource
    private OrgApiFacade orgApiFacade;
    @Resource
    private CdDataStoreDefinitionService cdDataStoreDefinitionService;
    @Resource
    private DataImportTaskLogService dataImportTaskLogService;

    @Override
    public Class dataClass() {
        return FlowExportJsonItemData.class;
    }

    @Override
    public String filePath() {
        return DataExportConstants.DATA_TYPE_FLOW;
    }

    @Override
    public String fileName() {
        return DataExportConstants.DATA_TYPE_FLOW_ALL_FILE_NAME;
    }

    @Override
    public String dataChildType() {
        return DataExportConstants.DATA_TYPE_FLOW_ALL;
    }

    @Override
    public long total(String systemUnitId) {
        Map<String, Object> params = new HashMap<>();
        params.put("systemUnitIdList", Collections.singleton(systemUnitId));
        List<QueryItem> queryItemList = flowInstanceService.getDao().listQueryItemByNameSQLQuery("cuntExportEndFlowInstance", params, null);
        return queryItemList.get(0).getLong("count");
    }

    @Override
    public List<FlowInstance> queryAll(String systemUnitId) {
        List<FlowInstance> result = new ArrayList<>();

        if (StringUtils.isNotBlank(systemUnitId)) {
            Map<String, Object> params = new HashMap<>();
            params.put("systemUnitIdList", Collections.singleton(systemUnitId));
            result = flowInstanceService.listByNameSQLQuery("queryExportAllEndFlowInstance", params);
        }

        return result;
    }


    @Override
    public List<FlowInstance> queryByPage(String systemUnitId, Integer currentPage, Integer pageSize) {
        PagingInfo pagingInfo = new PagingInfo(currentPage, pageSize);
        Map<String, Object> params = new HashMap<>();
        params.put("systemUnitIdList", Collections.singleton(systemUnitId));
        return flowInstanceService.listByNameSQLQueryAndPage("queryExportAllEndFlowInstance", params, pagingInfo, "");
    }

    private long countDataByTableNameAndUuid(String tableName, String uuid) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableName", tableName);
        params.put("uuid", uuid);
        return flowInstanceService.getDao().listQueryItemByNameSQLQuery("countDataByTableNameAndUuid", params, null).size();
    }

    private long updateTableSystemUnitIdField(String tableName, String uuid, String systemUnitId) {
        Map<String, Object> params = new HashMap<>();
        params.put("tableName", tableName);
        params.put("systemUnitId", systemUnitId);
        params.put("uuid", uuid);
        return flowInstanceService.getDao().updateByNamedSQL("updateTableSystemUnitIdField", params);
    }


    @Override
    public FlowExportJsonItemData toData(FlowInstance flowInstance) {
        FlowExportJsonItemData flowExportJsonItemData = new FlowExportJsonItemData();

//        DataExportTaskLog dataExportTaskLog = new DataExportTaskLog();
//        dataExportTaskLog.setDataType("flow");
//        dataExportTaskLog.setDataChildType("");
//        dataExportTaskLog.setDataUuid(flowInstance.getUuid());
//        dataExportTaskLog.setExportTime(new Date());
//        dataExportTaskLog.setExportStatus(DataExportConstants.DATA_LOG_STATUS_NORMAL);
//        dataExportTaskLog.setErrorMsg("");
//        dataExportTaskLog.setTaskUuid(dataExportTask.getUuid());
//        DataExportTaskLogService dataExportTaskLogService = ApplicationContextHolder.getBean(DataExportTaskLogService.class);

        try {

            //flowExportJsonData
//        FlowExportJsonData flowExportJsonData = new FlowExportJsonData();

            flowExportJsonItemData.setUuid(flowInstance.getDataUuid());
            flowExportJsonItemData.setFlowInstance(this.getFlowInstanceExportData(flowInstance));
            Pair<Map<String, Object>, List<Map<String, Object>>> flowDyformDataExport = this.getFlowDyformDataExport(flowInstance);
            if (flowDyformDataExport == null) {
                return null;
            }
            flowExportJsonItemData.setDyformData(flowDyformDataExport.getFirst());
            flowExportJsonItemData.setSubFormDataList(flowDyformDataExport.getSecond());
//            if (MapUtils.isEmpty(flowExportJsonItemData.getDyformData())) {
//                return flowExportJsonItemData;
//            }

//            handleDyFormFileAndClobField(dyformData);

//        flowExportJsonItemData.setFlowExportJsonData(flowExportJsonData);


            // fileIdMap
//        Map<String, List<String>> fileIdMap = new HashMap<>();
//        Map<String, String> clobFieldMap = new HashMap<>();

//            // flowInstance
//            handleFileField(flowExportJsonItemData.getFlowInstance().getFields(), fileIdMap);
//            handleClobField(flowExportJsonItemData.getFlowInstance().getFields(), clobFieldMap);
            // dyform
//            handleFileField(flowExportJsonItemData.getDyformData().getFields(), fileIdMap);
//            handleClobField(flowExportJsonItemData.getDyformData().getFields(), clobFieldMap);

            // dyform.subform
//            List<RelateFormData> relateFormDataList = flowExportJsonItemData.getDyformData().getRelateForms();
//            for (RelateFormData relateFormData : relateFormDataList) {
//                handleFileField(relateFormData.getFields(), fileIdMap);
//                handleClobField(relateFormData.getFields(), clobFieldMap);
//            }

//            dataExportTaskLogService.save(dataExportTaskLog);

        } catch (Exception e) {
//            dataExportTaskLog.setErrorMsg(e.getMessage());
//            dataExportTaskLog.setExportStatus(DataExportConstants.DATA_LOG_STATUS_ERROR);
//            dataExportTaskLogService.save(dataExportTaskLog);
            e.printStackTrace();
            throw new BusinessException("解析流程数据报错：" + e.getMessage());
        }


//        flowExportData.setFileIdMap(fileIdMap);
//        flowExportData.setClobFieldMap(clobFieldMap);

        return flowExportJsonItemData;

//        Map<String, List<String>> fileIdMap = new HashMap<>();
//        Map<String, String> clobFieldMap = new HashMap<>();
//
//        return this.getFlowExportJsonItemData(flowInstance, fileIdMap, clobFieldMap);
    }


//    private void handleDyFormFileAndClobField(Map<String, Object> dyformDataMap) {
//        String dataUuid = (String) dyformDataMap.get("uuid");
//        String formUuid = (String) dyformDataMap.get("form_uuid");
//        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
//
//        Map<String, List<Map<String, Object>>> formDatas = dyFormData.getFormDatas();
//
//        // 主表单
//        List<Map<String, Object>> mapList = formDatas.get(formUuid);
//        Map<String, Object> mainDyformMap = mapList.get(0);
//        JSONObject dyFormDefJsonObject = JSONObject.fromObject(dyFormData.getFormDefinition());
//        JSONObject databaseFieldsJSONObject = dyFormDefJsonObject.getJSONObject("databaseFields");
//
//
//        if (dyformDataMap.containsKey(FlowDataImportUtils.RELATE_FORM_DATAS_KEY)) {
//            Object relateFormDatas = dyformDataMap.get(FlowDataImportUtils.RELATE_FORM_DATAS_KEY);
//            if (relateFormDatas instanceof List) {
//                List<Map<String, Object>> relateFormDataList = (List<Map<String, Object>>) relateFormDatas;
//                for (Map<String, Object> map : relateFormDataList) {
////                    handleDyFormFileAndClobField(map);
//                }
//            }
//        }
//
//    }


    @Override
    public String getUuid(FlowInstance flowInstance) {
        return flowInstance.getDataUuid();
    }

    @Override
    public String getDataUuid(FlowExportJsonItemData flowExportJsonItemData) {
        return flowExportJsonItemData.getUuid();
    }

    @Override
    public String getId(FlowInstance flowInstance) {
        return null;
    }

    @Override
    public String getDataId(FlowExportJsonItemData flowExportJsonItemDataNew) {
        return null;
    }

    @Override
    public AttachFieldDesc getAttachFieldDesc(FlowExportJsonItemData flowExportJsonItemData, FlowInstance flowInstance) {
        AttachFieldDesc attachFieldDesc = dyFormAttachFieldDescThreadLocal.get();

//        AttachFieldDesc attachFieldDesc = new AttachFieldDesc();
//        attachFieldDesc.getFieldDesc().put("dyformData", Lists.newArrayList());
//        attachFieldDesc.getClobMap().put("datauuid_abc", "content");
//        attachFieldDesc.getAttachFileMap().put("datauuid_abc", Lists.newArrayList());

        return attachFieldDesc;
    }

    @Override
    @Transactional
    public ImportEntity<FlowInstance, FlowExportJsonItemData> save(FlowExportJsonItemData flowExportJsonItemData, String systemUnitId, boolean replace, Map<String, String> dependentDataMap) {


//        flowExportJsonItemData = com.alibaba.fastjson.JSONObject.toJavaObject(
//                (JSON) JSON.parse(JSONObject.fromObject(flowExportJsonItemData).toString())
//                , FlowExportJsonItemData.class
//        );
//        FlowDataImportUtils.handleMapForImportFlowInstanceExportData(flowExportJsonItemData);

//        FlowInstanceExportData flowInstanceExportData = flowExportJsonItemData.getFlowInstance();
        ImportFlowAddData importFlowAddData = new ImportFlowAddData();

        // flowInstance
//        String formUuid = "";// flowInstanceExportData.getFormUuid();// (String) flowInstanceMap.get("formUuid");
//        String flowInstanceUuid = flowInstanceExportData.getUuid();// (String) flowInstanceMap.get("uuid");
//        String flowDefUuid = "";//flowInstanceExportData.getFlowDefUuid();// (String) flowInstanceMap.get("flowDefUuid");
//        String flowDefUuid = (String) flowInstanceMap.get("flowDefUuid");
        String startUserId = "";//flowInstanceExportData.getStartUserId();

        Object tableNameObj = flowExportJsonItemData.getDyformData().get(FlowDataImportUtils.TABLE_NAME_KEY);
        if (tableNameObj == null || StringUtils.isBlank(tableNameObj.toString())) {
            throw new BusinessException("表单数据中表名不能为空");
        }
        String tableName = tableNameObj.toString();
        DyFormFormDefinition mainDyFormDefinition = dyFormFacade.getFormDefinitionOfMaxVersionByTblName(tableName);
        if (mainDyFormDefinition == null) {
            throw new BusinessException("表名" + tableName + "对应的表单定义不存在");
        }
        if (!formDefinitionService.isTableExist(tableName)) {
            // 表不存在
            throw new BusinessException("表单定义" + mainDyFormDefinition.getId() + "对应表" + mainDyFormDefinition.getTableName() + "未创建");
        }

//        FormDefinition formDefinition = ((FormDefinition) dyFormFacade.getFormDefinition(formUuid));
//        if (formDefinition == null) {
//            throw new BusinessException("表单定义不存在");
//        }
//        FlowDefinition flowDefinition = flowDefinitionService.getOne(flowDefUuid);
//        if (flowDefinition == null) {
//            throw new BusinessException("流程定义不存在");
//        }

        //生成说明文件
        String descJsonFilePath = ExpImpServiceBeanUtils.configFileName(dataImportRecordThreadLocal.get().getImportPath(), this);

        Map<String, FlowDataImportUtils.FlwDescJsonData> stringFlwDescJsonDataMap = flwDescJsonDataFieldDescMapThreadLocal.get();
        if (stringFlwDescJsonDataMap == null) {
            stringFlwDescJsonDataMap = new HashMap<>();
            flwDescJsonDataFieldDescMapThreadLocal.set(stringFlwDescJsonDataMap);
        }
        stringFlwDescJsonDataMap = flwDescJsonDataFieldDescMapThreadLocal.get();


        FlowDataImportUtils.FlwDescJsonData flwDescJsonData;
        if (stringFlwDescJsonDataMap.get(descJsonFilePath) == null) {
            flwDescJsonData = new FlowDataImportUtils.FlwDescJsonData(descJsonFilePath);
            stringFlwDescJsonDataMap.put(descJsonFilePath, flwDescJsonData);
        } else {
            flwDescJsonData = stringFlwDescJsonDataMap.get(descJsonFilePath);
        }

        FlowDataImportUtils.setSystemUnitIdForImportFlowInstanceExportData(flowExportJsonItemData, systemUnitId);
        Map<String, List<Pair<String, String>>> fileFieldMap = new HashMap<>();
        FlowDataImportUtils.handleClobAndFileFieldForImportFlowExportJsonItemData(flowExportJsonItemData, fileFieldMap, flwDescJsonData);

        replace = countDataByTableNameAndUuid(tableName, getDataUuid(flowExportJsonItemData)) > 0;
        if (replace) {
            updateByImportFlowData(flowExportJsonItemData, fileFieldMap, importFlowAddData);
        } else {
            insertByImportFlowData(flowExportJsonItemData, fileFieldMap, importFlowAddData);
        }

        ImportEntity<FlowInstance, FlowExportJsonItemData> importEntity = new ImportEntity<>();
        importEntity.setPostProcess(false);
        FlowInstance flowInstanceObj = new FlowInstance();
        flowInstanceObj.setDataUuid(importFlowAddData.getDataUuid());
        importEntity.setObj(flowInstanceObj);
        importEntity.setSorce(flowExportJsonItemData);
        return importEntity;
    }

    private void insertByImportFlowData(FlowExportJsonItemData flowExportJsonItemData, Map<String, List<Pair<String, String>>> fileFieldMap, ImportFlowAddData importFlowAddData) {

        try {

            Object tableNameObj = flowExportJsonItemData.getDyformData().get(FlowDataImportUtils.TABLE_NAME_KEY);
            String tableName = tableNameObj.toString();
            FormDefinition mainDyFormDefinition = (FormDefinition) dyFormFacade.getFormDefinitionOfMaxVersionByTblName(tableName);
            mainDyFormDefinition.doGetFormDefinitionHandler();

            Map<String, String> tableNameFormUuidMap = new HashMap<>();
            tableNameFormUuidMap.put(tableName, mainDyFormDefinition.getUuid());
            List<DyformSubformFormDefinition> dyformSubformFormDefinitions = mainDyFormDefinition.doGetSubformDefinitions();
            for (DyformSubformFormDefinition dyformSubformFormDefinition : dyformSubformFormDefinitions) {
                String formUuid = dyformSubformFormDefinition.getFormUuid();
                FormDefinition subDyFormFormDefinition = formDefinitionService.findDyFormFormDefinitionByFormUuid(formUuid);
                tableNameFormUuidMap.put(subDyFormFormDefinition.getTableName(), formUuid);
            }

            // dyform
            Map<String, List<Map<String, Object>>> formDatas = FlowDataImportUtils.getFormDatas(flowExportJsonItemData, tableNameFormUuidMap);
            String formUuid = mainDyFormDefinition.getUuid();
            DyFormData dyFormData = dyFormFacade.createDyformData(formUuid);
            dyFormData.setFormDatas(formDatas, true);
            dyFormFacade.saveFormData(dyFormData);
            importFlowAddData.setFormUuid(formUuid);
            importFlowAddData.setDataUuid(dyFormData.getDataUuid());

            try {
                for (String uuid : fileFieldMap.keySet()) {

                    List<Pair<String, String>> pairs = fileFieldMap.get(uuid);
                    for (Pair<String, String> stringStringPair : pairs) {
                        String attachPath = ExpImpServiceBeanUtils.attachPathName(dataImportRecordThreadLocal.get().getImportPath(), this);
                        File file = new File(attachPath + File.separator + stringStringPair.getValue());
                        if (file != null && file.exists()) {
                            MongoFileEntity fileEntity = mongoFileService.saveFile(file.getName(), new FileInputStream(file));
                            mongoFileService.pushFileToFolder(uuid, fileEntity.getFileID(), stringStringPair.getKey());
                        }
                    }

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateByImportFlowData(FlowExportJsonItemData flowExportJsonItemData, Map<String, List<Pair<String, String>>> fileFieldMap, ImportFlowAddData importFlowAddData) {
        try {

            DataImportTaskLog dataImportTaskLog = dataImportTaskLogService.getBySourceUuid(this.getDataUuid(flowExportJsonItemData));


            Object tableNameObj = flowExportJsonItemData.getDyformData().get(FlowDataImportUtils.TABLE_NAME_KEY);
            String tableName = tableNameObj.toString();
            FormDefinition mainDyFormDefinition = (FormDefinition) dyFormFacade.getFormDefinitionOfMaxVersionByTblName(tableName);
            mainDyFormDefinition.doGetFormDefinitionHandler();
            Map<String, String> tableNameFormUuidMap = new HashMap<>();
            tableNameFormUuidMap.put(tableName, mainDyFormDefinition.getUuid());
            List<DyformSubformFormDefinition> dyformSubformFormDefinitions = mainDyFormDefinition.doGetSubformDefinitions();
            for (DyformSubformFormDefinition dyformSubformFormDefinition : dyformSubformFormDefinitions) {
                String subFormUuid = dyformSubformFormDefinition.getFormUuid();
                FormDefinition subDyFormFormDefinition = formDefinitionService.findDyFormFormDefinitionByFormUuid(subFormUuid);
                tableNameFormUuidMap.put(subDyFormFormDefinition.getTableName(), subFormUuid);
            }

            // dyform
            Map<String, List<Map<String, Object>>> formDatas = FlowDataImportUtils.getFormDatas(flowExportJsonItemData, tableNameFormUuidMap);
//
            String formUuid = mainDyFormDefinition.getUuid();
            List<Map<String, Object>> mapList = formDatas.get(formUuid);
            Map<String, Object> map = mapList.get(0);
            String dataUuid;
            if (dataImportTaskLog != null && StringUtils.isNotBlank(dataImportTaskLog.getAfterImportUuid())) {
                dataUuid = dataImportTaskLog.getAfterImportUuid();
                map.put("uuid", dataUuid);
            } else {
                dataUuid = (String) map.get("uuid");
            }

            Map<String, List<Map<String, Object>>> formData = dyFormFacade.getFormData(formUuid, dataUuid);
            DyFormData dyFormData;
            if (formData == null ||
                    (formData != null && CollectionUtils.isNotEmpty(formData.get(formUuid)) && MapUtils.isEmpty(formData.get(formUuid).get(0)))
            ) {
                dyFormData = dyFormFacade.createDyformData(formUuid);
                dyFormData.setFormDatas(formDatas, true);
                //        validateFormData(dyFormData);
            } else {
                dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);

                dyFormData.setFormDatas(formDatas, false);
                //        validateFormData(dyFormData);
            }
//
            ((DyFormDataImpl) dyFormData).markAllFieldsAsUpadtedFields();
            dyFormFacade.saveFormData(dyFormData);

            // 更新 system_unit_uuid  58600 超管，选择不同的系统单位重复导入数据，数据的系统单位id没有更新
            for (String formUuidIter : dyFormData.getFormDatas().keySet()) {
                List<Map<String, Object>> formDataMapList = dyFormData.getFormDatas().get(formUuidIter);
                for (Map<String, Object> stringObjectMap : formDataMapList) {
                    updateTableSystemUnitIdField((String) stringObjectMap.get(FlowDataImportUtils.TABLE_NAME_KEY), (String) stringObjectMap.get(EnumSystemField.uuid.getColumn()), (String) stringObjectMap.get(EnumSystemField.system_unit_id.getColumn()));
                }
            }

            importFlowAddData.setFormUuid(formUuid);
            importFlowAddData.setDataUuid(dyFormData.getDataUuid());

            try {

                for (String uuid : fileFieldMap.keySet()) {

                    List<Pair<String, String>> pairs = fileFieldMap.get(uuid);

                    for (Pair<String, String> pair : pairs) {
                        String fieldName = pair.getKey();
                        List<MongoFileEntity> instAttaches = mongoFileService.getFilesFromFolder(
                                uuid, fieldName);
                        for (MongoFileEntity instAttach : instAttaches) {
                            mongoFileService.popFileFromFolder(uuid, instAttach.getFileID());
                        }
                    }

                    for (Pair<String, String> stringStringPair : pairs) {
                        String fieldName = stringStringPair.getKey();

                        String attachPath = ExpImpServiceBeanUtils.attachPathName(dataImportRecordThreadLocal.get().getImportPath(), this);
                        File file = new File(attachPath + File.separator + stringStringPair.getValue());
                        if (file != null && file.exists()) {
                            MongoFileEntity fileEntity = mongoFileService.saveFile(file.getName(), new FileInputStream(file));
                            mongoFileService.pushFileToFolder(uuid, fileEntity.getFileID(), fieldName);
                        }
                    }

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public void update(FlowInstance flowInstance, FlowExportJsonItemData flowExportJsonItemData, Map<String, String> dependentDataMap) {

    }

    //-----------------------

    // FlowInstanceExportData
    private FlowInstanceExportData getFlowInstanceExportData(FlowInstance flowInstance) {
        FlowInstanceExportData flowInstanceExportData = new FlowInstanceExportData();
        flowInstance = flowInstanceService.get(flowInstance.getUuid());
//        flowInstanceExportData.setUuid(flowInstance.getUuid());
        flowInstanceExportData.setFlow_title(flowInstance.getTitle());
        flowInstanceExportData.setFlow_create_time(flowInstance.getCreateTime());
        String creatorName = orgApiFacade.getUserNameById(flowInstance.getCreator());
        flowInstanceExportData.setFlow_creator_name(creatorName);
        flowInstanceExportData.setFlow_done_user_name(this.getFlowDoneUserName(flowInstance));

        return flowInstanceExportData;
    }

    private List<TaskInstanceExportData> getTaskInstanceExportDataList(FlowInstance flowInstance) {
        List<TaskInstanceExportData> taskInstanceExportDataList = new ArrayList<>();

        List<TaskInstance> taskInstanceList = taskInstanceService.getByFlowInstUuid(flowInstance.getUuid());
        for (TaskInstance taskInstance : taskInstanceList) {
            taskInstance = taskInstanceService.get(taskInstance.getUuid());
            taskInstanceExportDataList.add(this.getTaskInstanceExportData(taskInstance));
        }

        return taskInstanceExportDataList;
    }


    /**
     * 获取流程办理人
     *
     * @param flowInstance flowInstance
     * @return
     */
    private String getFlowDoneUserName(FlowInstance flowInstance) {
        Set<String> userNameSet = new HashSet<>();
        List<TaskOperation> taskOperationList = taskOperationService.getByFlowInstUuid(flowInstance.getUuid());
        for (TaskOperation taskOperation : taskOperationList) {
            userNameSet.add(taskOperation.getAssigneeName());
        }

        return StringUtils.join(userNameSet, Separator.SEMICOLON.getValue());
    }

    private List<TaskOperationExportData> getTaskOperationExportDataList(FlowInstance flowInstance) {
        List<TaskOperationExportData> taskOperationExportDataList = new ArrayList<>();

        List<TaskOperation> taskOperationList = taskOperationService.getByFlowInstUuid(flowInstance.getUuid());
        for (TaskOperation taskOperation : taskOperationList) {
            taskOperationExportDataList.add(this.getTaskOperationExportData(taskOperation));
        }

        return taskOperationExportDataList;
    }

    private List<TaskActivityExportData> getTaskActivityExportDataList(FlowInstance flowInstance) {
        List<TaskActivityExportData> taskActivityExportDataList = new ArrayList<>();

        List<TaskActivity> taskActivityList = taskActivityService.getByFlowInstUuid(flowInstance.getUuid());
        for (TaskActivity taskActivity : taskActivityList) {
            taskActivityExportDataList.add(this.getTaskActivityExportData(taskActivity));
        }

        return taskActivityExportDataList;

    }

    private TaskInstanceExportData getTaskInstanceExportData(TaskInstance taskInstance) {
        TaskInstanceExportData taskInstanceExportData = new TaskInstanceExportData();

        org.springframework.beans.BeanUtils.copyProperties(taskInstance, taskInstanceExportData);

        taskInstanceExportData.setFlowInstUuid(taskInstance.getFlowInstance().getUuid());
        taskInstanceExportData.setFlowDefUuid(taskInstance.getFlowDefinition().getUuid());
        taskInstanceExportData.setParentTaskInstUuid(taskInstance.getParent() == null ? null : taskInstance.getParent().getUuid());

        return taskInstanceExportData;
    }


    private TaskOperationExportData getTaskOperationExportData(TaskOperation taskOperation) {
        TaskOperationExportData taskOperationExportData = new TaskOperationExportData();

        org.springframework.beans.BeanUtils.copyProperties(taskOperation, taskOperationExportData);
        return taskOperationExportData;
    }

    private TaskActivityExportData getTaskActivityExportData(TaskActivity taskActivity) {
        TaskActivityExportData taskActivityExportData = new TaskActivityExportData();

        org.springframework.beans.BeanUtils.copyProperties(taskActivity, taskActivityExportData);

        return taskActivityExportData;
    }

    // dyformData

    /**
     * @param flowInstance
     * @return <主表数据，从表数据>
     */
    private Pair<Map<String, Object>, List<Map<String, Object>>> getFlowDyformDataExport(FlowInstance flowInstance) {

        /* 相对文件夹路径: [fileId] */
        Map<String, List<String>> fileIdMap = new HashMap<>();
        /* 相对文件夹路径: clobText */
        Map<String, String> clobFieldMap = new HashMap<>();

        DyFormData dyFormData;
        try {
            FormDefinition definition = formDefinitionService.findDyFormFormDefinitionByFormUuid(flowInstance.getFormUuid());
            if (definition != null) {
//                System.out.println("20211006getFormUuid" + flowInstance.getFormUuid() + "    " + flowInstance.getDataUuid());
                dyFormData = dyFormFacade.getDyFormData(flowInstance.getFormUuid(), flowInstance.getDataUuid());
            } else {
                throw new BusinessException("表单定义不存在");
            }
        } catch (Exception e) {
            return null;
//            throw new BusinessException("表单数据为空");
        }


        String formUuid = dyFormData.getFormUuid();
        String dataUuid = dyFormData.getDataUuid();

        Map<String, List<Map<String, Object>>> formDatas = dyFormData.getFormDatas();
        List<Map<String, Object>> mapList = formDatas.get(formUuid);
        Map<String, Object> mainDyformMap = mapList.get(0);

        if (MapUtils.isEmpty(mainDyformMap)) {
            return null;
        }

        JSONObject dyFormDefJsonObject = JSONObject.fromObject(dyFormData.getFormDefinition());
        JSONObject databaseFieldsJSONObject = dyFormDefJsonObject.getJSONObject("databaseFields");
        if (databaseFieldsJSONObject.isEmpty()) {
            databaseFieldsJSONObject = dyFormDefJsonObject.getJSONObject("fields");
        }


        mainDyformMap = getExportFieldItemDataListByDyform(mainDyformMap, databaseFieldsJSONObject, fileIdMap, clobFieldMap, true);

//        JSONObject subformsJsonObject = dyFormDefJsonObject.getJSONObject("subforms");
        JSONArray subformDefinitions;
        if (dyFormDefJsonObject.containsKey("subformDefinitions")) {
            subformDefinitions = dyFormDefJsonObject.getJSONArray("subformDefinitions");
        } else {
            subformDefinitions = new JSONArray();
        }

        List<Map<String, Object>> allSubFormDataMapList = new ArrayList<>();
        for (Object subformDefinition : subformDefinitions) {
            JSONObject subformDefinitionJsonObject = (JSONObject) subformDefinition;
            String subFormUuid = subformDefinitionJsonObject.getString("uuid");
            JSONObject subFieldsDef = subformDefinitionJsonObject.getJSONObject("fields");
//            JSONObject subFormDefJsonObject = subformsJsonObject.getJSONObject(subFormUuid);

//            allSubFormDataMapList.addAll(formDatas.get(subFormUuid));

            List<Map<String, Object>> subFormDataMapList = formDatas.get(subFormUuid);
            if (CollectionUtils.isNotEmpty(subFormDataMapList)) {
                for (Map<String, Object> subDyformMap : subFormDataMapList) {
                    subDyformMap = getExportFieldItemDataListByDyform(subDyformMap, subFieldsDef, fileIdMap, clobFieldMap, false);
                    allSubFormDataMapList.add(subDyformMap);
                }
            }
        }

//        String systemUnitId = flowInstance.getSystemUnitId();
//        handleDyFormFileAndClobField(systemUnitId, fileIdMap, clobFieldMap);

//        if (CollectionUtils.isNotEmpty(allSubFormDataMapList)) {
//            mainDyformMap.put(FlowDataImportUtils.RELATE_FORM_DATAS_KEY, allSubFormDataMapList);
//        }

        return new Pair<>(mainDyformMap, allSubFormDataMapList);
    }


    // def -> List<ExportFieldItemData>
    private Map<String, Object> getExportFieldItemDataListByDyform(Map<String, Object> dyformMap, JSONObject databaseFieldsJSONObject, Map<String, List<String>> fileIdMap, Map<String, String> clobFieldMap, boolean isMainForm) {

        Map<String, String> dyFormInputModeDataTypeMap = ImportExportUtils.getDyFormInputModeDataTypeMap();
        String formUuid = (String) dyformMap.get("form_uuid");
        String dataUuid = (String) dyformMap.get("uuid");

        List<FieldDesc> fieldDescList = new ArrayList<>();

//        List<ExportFieldItemData> fields = new ArrayList<>();

        Set<String> keySet = dyformMap.keySet();
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>(keySet);
        for (String fieldName : linkedHashSet) {
            if (!databaseFieldsJSONObject.containsKey(fieldName)) {
                continue;
            }
            JSONObject jsonObject = databaseFieldsJSONObject.getJSONObject(fieldName);
            Object fieldValueObj = dyformMap.get(fieldName);

            ExportFieldItemData exportFieldItemData;
            if (jsonObject == null || jsonObject.size() == 0) {
                // 表单定义中无该字段 - > 根据字段值判断类型
                String dataType = "";
                if (fieldValueObj instanceof String) {
                    dataType = STRING.getValue();
                } else if (fieldValueObj instanceof Timestamp) {
                    dataType = DATE.getValue();
                } else if (fieldValueObj instanceof Long) {
                    dataType = LONG.getValue();
                } else if (fieldValueObj instanceof Boolean) {
                    dataType = BOOLEAN.getValue();
                } else if (fieldValueObj instanceof Double) {
                    dataType = DOUBLE.getValue();
                } else if (fieldValueObj instanceof Float) {
                    dataType = FLOAT.getValue();
                } else if (fieldValueObj instanceof Integer) {
                    dataType = INTEGER.getValue();
                } else {
                    dataType = STRING.getValue();
                }

                fieldDescList.add(new FieldDesc(fieldName, dataType, "", "", ""));

//                exportFieldItemData = getExportFieldItemData(dataUuid, fieldName, value2EnumObj(dataType), fieldValueObj);
            } else {
                // 根据表单字段定义 判断
                String inputMode = jsonObject.getString("inputMode");
                String displayValueField = "";

                JSONObject realDisplayJSONObject = jsonObject.getJSONObject("realDisplay");
                if (MapUtils.isNotEmpty(realDisplayJSONObject)) {
                    String display = realDisplayJSONObject.getString("display");

                    if (StringUtils.isNotBlank(display)) {
                        displayValueField = display;
                    }

                    if (org.apache.commons.lang.StringUtils.isBlank(display) && fieldValueObj != null && org.apache.commons.lang.StringUtils.isNotEmpty(fieldValueObj.toString())) {

                        // 无 显示值字段 && 值为空，不获取显示值
                        Object optionDataSourceObj = jsonObject.get("optionDataSource");
                        if (optionDataSourceObj == null) {
                            // 无 备选项来源

                            // 是否为组织弹出框
                            if (DyFormConfig.INPUTMODE_ORGSELECT2.equals(inputMode)) {
                                // 组织弹出框
                                OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
                                Map<String, String> nameByOrgEleIdMap;
                                try {
                                    nameByOrgEleIdMap = orgApiFacade.getNameByOrgEleIds(Arrays.asList(org.apache.commons.lang.StringUtils.split(fieldValueObj.toString(), Separator.SEMICOLON.getValue())));
                                } catch (Exception e) {
                                    nameByOrgEleIdMap = new HashMap<>();
                                }
                                String displayValue = org.apache.commons.lang.StringUtils.join(nameByOrgEleIdMap.values(), Separator.SEMICOLON.getValue());
                                displayValueField = fieldName + "_display";
                                int displayValueFieldIndex = 2;
                                while (dyformMap.containsKey(displayValueField)) {
                                    displayValueField += displayValueFieldIndex++;
                                }
                                dyformMap.put(displayValueField, displayValue);
                                fieldDescList.add(new FieldDesc(displayValueField, STRING.getValue(), jsonObject.getString("displayName") + "显示值", "", ""));

//                                exportFieldItemData = getExportFieldItemData(dataUuid, fieldName + "_display", value2EnumObj(STRING.getValue()), displayValue);
//                                fields.add(exportFieldItemData);
                            }
//                        else if(DyFormConfig.INPUTMODE_JOB.equals(inputMode)){
//                            // 职位
//                        }

                        } else {


                            try {

                                String optionDataSource = jsonObject.getString("optionDataSource");

                                String displayValue = org.apache.commons.lang.StringUtils.EMPTY;
                                if (DyFormConfig.DyDataSourceType.dataConstant.equals(optionDataSource)) {
                                    // 常量
                                    System.out.println(optionDataSource);
                                    if (jsonObject.has("optionSet")) {

                                        JSONObject optionSetJSONObject;
                                        Object optionSet = jsonObject.get("optionSet");
                                        if (optionSet instanceof JSONArray) {
                                            optionSetJSONObject = new JSONObject();

                                            try {
                                                for (Object obj : (JSONArray) optionSet) {
                                                    JSONObject optionJsonObject = (JSONObject) obj;
                                                    optionSetJSONObject.put(optionJsonObject.getString("value"), optionJsonObject.get("name"));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        } else if (optionSet instanceof JSONObject) {
                                            optionSetJSONObject = jsonObject.getJSONObject("optionSet");
                                        } else {
                                            optionSetJSONObject = new JSONObject();
                                        }

                                        displayValue = ImportExportUtils.getDictValueStr(fieldValueObj.toString(), optionSetJSONObject);
                                    }

                                    displayValueField = fieldName + "_display";
                                    int displayValueFieldIndex = 2;
                                    while (dyformMap.containsKey(displayValueField)) {
                                        displayValueField += displayValueFieldIndex++;
                                    }
                                    dyformMap.put(displayValueField, displayValue);
                                    fieldDescList.add(new FieldDesc(displayValueField, STRING.getValue(), jsonObject.getString("displayName") + "显示值", "", ""));
//                                exportFieldItemData = getExportFieldItemData(dataUuid, fieldName + "_display", value2EnumObj(STRING.getValue()), displayValue);
//                                fields.add(exportFieldItemData);

                                } else if (DyFormConfig.DyDataSourceType.dataDictionary.equals(optionDataSource)) {
                                    // 字典
                                    DataDictionaryService dataDictionaryService = ApplicationContextHolder.getBean(DataDictionaryService.class);
                                    String dictCode = jsonObject.getString("dictCode");
                                    String[] split = org.apache.commons.lang.StringUtils.split(dictCode, Separator.COLON.getValue());
                                    if (ArrayUtils.isNotEmpty(split)) {
                                        List<DataDictionary> dataDictionaries = dataDictionaryService.getDataDictionariesByType(split[0]);
                                        Map<String, Object> map = new HashMap<>();
                                        for (DataDictionary dataDictionary : dataDictionaries) {
                                            map.put(dataDictionary.getCode(), dataDictionary.getName());
                                        }
                                        displayValue = ImportExportUtils.getDictValueStr(fieldValueObj.toString(), map);
                                    }

                                    displayValueField = fieldName + "_display";
                                    int displayValueFieldIndex = 2;
                                    while (dyformMap.containsKey(displayValueField)) {
                                        displayValueField += displayValueFieldIndex++;
                                    }
                                    dyformMap.put(displayValueField, displayValue);
                                    fieldDescList.add(new FieldDesc(displayValueField, STRING.getValue(), jsonObject.getString("displayName") + "显示值", "", ""));
//                                exportFieldItemData = getExportFieldItemData(dataUuid, fieldName + "_display", value2EnumObj(STRING.getValue()), displayValue);
//                                fields.add(exportFieldItemData);

                                } else if (DyFormConfig.DyDataSourceType.dataView.equals(optionDataSource)) {
                                    // 字典
                                    DataDictionaryService dataDictionaryService = ApplicationContextHolder.getBean(DataDictionaryService.class);
                                    String dictCode = jsonObject.getString("dictCode");
                                    String[] split = org.apache.commons.lang.StringUtils.split(dictCode, Separator.COLON.getValue());
                                    if (ArrayUtils.isNotEmpty(split)) {
                                        List<DataDictionary> dataDictionaries = dataDictionaryService.getDataDictionariesByType(split[0]);
                                        Map<String, Object> map = new HashMap<>();
                                        for (DataDictionary dataDictionary : dataDictionaries) {
                                            map.put(dataDictionary.getUuid(), dataDictionary.getName());
                                        }
                                        displayValue = ImportExportUtils.getDictValueStr(fieldValueObj.toString(), map);
                                    }


                                    displayValueField = fieldName + "_display";
                                    int displayValueFieldIndex = 2;
                                    while (dyformMap.containsKey(displayValueField)) {
                                        displayValueField += displayValueFieldIndex++;
                                    }
                                    dyformMap.put(displayValueField, displayValue);
                                    fieldDescList.add(new FieldDesc(displayValueField, STRING.getValue(), jsonObject.getString("displayName") + "显示值", "", ""));

//                                exportFieldItemData = getExportFieldItemData(dataUuid, fieldName + "_display", value2EnumObj(STRING.getValue()), displayValue);
//                                fields.add(exportFieldItemData);

                                } else if (DyFormConfig.DyDataSourceType.dataSource.equals(optionDataSource)) {
                                    // 数据仓库
                                    String dataSourceId = jsonObject.getString("dataSourceId");
                                    String dataSourceFieldName = jsonObject.getString("dataSourceFieldName");
                                    String dataSourceDisplayName = jsonObject.getString("dataSourceDisplayName");

//                                DataSourceApiFacade dataSourceApiFacade = ApplicationContextHolder.getBean(DataSourceApiFacade.class);
//                                List<DataSourceColumn> dataSourceColumnList = dataSourceApiFacade.getDataSourceFieldsById(dataSourceId);


                                    CdDataStoreService cdDataStoreService = ApplicationContextHolder.getBean(CdDataStoreService.class);
                                    DataStoreParams dataStoreParams = new DataStoreParams();
                                    dataStoreParams.setDataStoreId(dataSourceId);
                                    DataStoreProxy proxy = new DataStoreProxy();
                                    proxy.setStoreId(dataSourceId);
                                    dataStoreParams.setProxy(proxy);
                                    dataStoreParams.setPagingInfo(new PagingInfo(1, 25, false));
                                    List<Condition> criterions = new ArrayList<>();

                                    criterions.add(new Condition(dataSourceFieldName, Arrays.asList(StringUtils.split(fieldValueObj.toString(), Separator.SEMICOLON.getValue())), CriterionOperator.in));
                                    dataStoreParams.setCriterions(criterions);

                                    if (checkDataStoreParams(dataStoreParams)) {
                                        DataStoreData dataStoreData = cdDataStoreService.loadData(dataStoreParams);
                                        List<Map<String, Object>> data = dataStoreData.getData();

                                        List<String> displayValueList = new ArrayList<>();
                                        for (Map<String, Object> datum : data) {
                                            Object o = datum.get(dataSourceDisplayName);
                                            displayValueList.add(o.toString());
                                        }
                                        displayValue = StringUtils.join(displayValueList, Separator.SEMICOLON.getValue());
                                    }

                                    displayValueField = fieldName + "_display";
                                    dyformMap.put(displayValueField, displayValue);
                                    fieldDescList.add(new FieldDesc(fieldName, STRING.getValue(), jsonObject.getString("displayName"), displayValueField, ""));
                                }
                                System.out.println(optionDataSource);

                            } catch (Exception e) {
                                // 获取字段
                                log.error("流程数据导入导出，获取字段" + fieldName + "显示值数据失败, 流程表单uuid：" + dataUuid);
                            }
                        }
                    }
                }

                String dataType = dyFormInputModeDataTypeMap.get(inputMode);

                if (STRING.getValue().equals(dataType)) {
                    if (jsonObject.has(DyFormConfig.EnumFieldPropertyName.dbDataType.toString())
                            && DyFormConfig.DbDataType._clob.equals(jsonObject.getString(DyFormConfig.EnumFieldPropertyName.dbDataType.toString()))) {
                        // clob
                        dataType = CLOB.getValue();
                    } else {
                        dataType = STRING.getValue();
                    }
                }

                if (dataType == null) {
                    dataType = STRING.getValue();
                }

                // ------------
                String fileDirectoryPath = this.getFileDirectoryPath(dataUuid, fieldName);
                // clob
                if (CLOB.getValue().equals(dataType) && fieldValueObj != null) {
                    clobFieldMap.put(fileDirectoryPath, fieldValueObj.toString());
                    dyformMap.put(fieldName, fileDirectoryPath + File.separator + dataUuid + Separator.UNDERLINE.getValue() + fieldName + ".txt");
                }
                // file
                if (FILE.getValue().equals(dataType)) {
                    List<MongoFileEntity> mongoFileEntityList = mongoFileService.getFilesFromFolder(dataUuid, fieldName);
                    List<String> fileIdList = new ArrayList<>();
                    List<String> fileAboPathList = new ArrayList<>();
                    for (MongoFileEntity fileEntity : mongoFileEntityList) {
                        fileIdList.add(fileEntity.getFileID());
                        fileAboPathList.add(fileDirectoryPath + File.separator + ExpImpServiceBeanUtils.linuxFileNameTooLongConvert(fileEntity.getFileName()));
                    }
                    if (CollectionUtils.isNotEmpty(fileIdList)) {
                        fileIdMap.put(fileDirectoryPath, fileIdList);
                    }
                    dyformMap.put(fieldName, fileAboPathList);
                }

//                displayValueField = fieldName + "_display";
//                mainDyformMap.put(displayValueField, displayValue);

                fieldDescList.add(new FieldDesc(fieldName, dataType, jsonObject.getString("displayName"), displayValueField, ""));
            }
        }

        fieldDescList.add(new FieldDesc(FlowDataImportUtils.TABLE_NAME_KEY, STRING.getValue(), "表名", "", ""));
        // setTableName
        try {
            FormDefinition dyFormFormDefinition = formDefinitionService.findDyFormFormDefinitionByFormUuid(formUuid);
            String tableName = dyFormFormDefinition.getTableName();
            dyformMap.put(FlowDataImportUtils.TABLE_NAME_KEY, tableName);
        } catch (Exception e) {
            log.error("setTableName error formUuid: " + formUuid);
        }

        AttachFieldDesc attachFieldDesc = dyFormAttachFieldDescThreadLocal.get();
        if (attachFieldDesc == null) {
            attachFieldDesc = new AttachFieldDesc();
            dyFormAttachFieldDescThreadLocal.set(attachFieldDesc);
        }

        // fieldDescMap
        Map<String, List<FieldDesc>> fieldDescMap = attachFieldDesc.getFieldDesc();
//        if (fieldDescMap == null) {
//            fieldDescMap = new HashMap<>();
//        }

        if (isMainForm) {
            String tableName = StringUtils.defaultString((String) dyformMap.get(FlowDataImportUtils.TABLE_NAME_KEY));

            String dyformDataKey = "dyformData";
            List<FieldDesc> subFormDataListFieldDescList = fieldDescMap.get(dyformDataKey);
            if (subFormDataListFieldDescList == null) {
                fieldDescMap.put(dyformDataKey, new ArrayList<>());
                subFormDataListFieldDescList = fieldDescMap.get(dyformDataKey);
            }
            FieldDesc formFieldDesc = new FieldDesc();
            formFieldDesc.setFieldName(tableName);
            formFieldDesc.setType("List<Object>");

            formFieldDesc.setDesc("表单" + tableName + "字段说明");
            formFieldDesc.setFields(fieldDescList);
            subFormDataListFieldDescList.add(formFieldDesc);

//            List<FieldDesc> dyformDataFieldDescList = fieldDescMap.get("dyformData");
//            if (dyformDataFieldDescList == null) {
//                fieldDescMap.put("dyformData", new ArrayList<>());
//                dyformDataFieldDescList = fieldDescMap.get("dyformData");
//            }
//            FieldDesc formFieldDesc = new FieldDesc();
//            formFieldDesc.setFieldName(formUuid);
//            formFieldDesc.setType("List<Object>");
//            formFieldDesc.setDesc("表单" + formUuid + "字段说明");
//            formFieldDesc.setFields(fieldDescList);
//            dyformDataFieldDescList.add(formFieldDesc);
        } else {
            String tableName = StringUtils.defaultString((String) dyformMap.get(FlowDataImportUtils.TABLE_NAME_KEY));

            String subFormDataListKey = "subFormDataList";
            List<FieldDesc> subFormDataListFieldDescList = fieldDescMap.get(subFormDataListKey);
            if (subFormDataListFieldDescList == null) {
                fieldDescMap.put(subFormDataListKey, new ArrayList<>());
                subFormDataListFieldDescList = fieldDescMap.get(subFormDataListKey);
            }
            FieldDesc formFieldDesc = new FieldDesc();
            formFieldDesc.setFieldName(tableName);
            formFieldDesc.setType("List<Object>");

            formFieldDesc.setDesc("表单" + tableName + "字段说明");
            formFieldDesc.setFields(fieldDescList);
            subFormDataListFieldDescList.add(formFieldDesc);
        }

        attachFieldDesc.setFieldDesc(fieldDescMap);

        // attachFileMap
        Map<String, List<String>> attachFileMap = attachFieldDesc.getAttachFileMap();
        attachFileMap.putAll(fileIdMap);

        // clobMap
        Map<String, String> clobMap = attachFieldDesc.getClobMap();
        clobMap.putAll(clobFieldMap);

        return dyformMap;
    }

//    private void handleDyFormFileAndClobField(String systemUnitUuid, Map<String, List<String>> fileIdMap, Map<String, String> clobFieldMap) {
//        MultiOrgSystemUnit multiOrgSystemUnit = multiOrgSystemUnitService.getById(systemUnitUuid);
//        if (multiOrgSystemUnit == null) {
//            return;
//        }
//        String systemUnitPath = getSystemUnitPath(dataExportRecordThreadLocal.get().getExportPath(), multiOrgSystemUnit.getName(), dataExportRecordThreadLocal.get().getExportTime());
//
//        try {
//            // file
//            if (MapUtils.isNotEmpty(fileIdMap)) {
//                for (String path : fileIdMap.keySet()) {
//                    File flowFileDirectory = new File(systemUnitPath + File.separator + path);
//                    if (!flowFileDirectory.exists()) {
//                        flowFileDirectory.mkdirs();
//                    }
//                    List<String> fileIdList = fileIdMap.get(path);
//                    for (String fileId : fileIdList) {
//                        MongoFileEntity file = mongoFileService.getFile(fileId);
//
//                        FileUtils.writeFile(file.getFileName(), flowFileDirectory.getPath(), file.getInputstream());
//                    }
//                }
//            }
//
//            // clob
//            for (String key : clobFieldMap.keySet()) {
//                File flowFileDirectory = new File(systemUnitPath + File.separator + key);
//                if (!flowFileDirectory.exists()) {
//                    flowFileDirectory.mkdirs();
//                }
//                String clobFieldFullName = flowFileDirectory.getPath() + File.separator + flowFileDirectory.getName() + ".txt";
//                FileUtils.writeFileUTF(clobFieldFullName, clobFieldMap.get(key), true);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    protected String getSystemUnitPath(String exportPath, String systemUnitName, Date exportTime) {
        return exportPath + File.separator + systemUnitName + Separator.UNDERLINE.getValue() + new SimpleDateFormat("yyyyMMddHHmmss").format(exportTime);
    }


    private String getFileDirectoryPath(String uuid, String fieldName) {
        return uuid + Separator.UNDERLINE.getValue() + fieldName;
    }

    /**
     * 如何描述该方法
     *
     * @param params
     * @return
     */
    private boolean checkDataStoreParams(DataStoreParams params) {
        CdDataStoreDefinition dataStoreDefinition = cdDataStoreDefinitionService.getBeanById(params.getDataStoreId());
        if (dataStoreDefinition == null) {
            return false;
        }
        DataStoreConfiguration dataStoreConfiguration = new DataStoreConfiguration();
        BeanUtils.copyProperties(dataStoreDefinition, dataStoreConfiguration);
        String configType = dataStoreConfiguration.getType();

        if (DataStoreType.DATA_INTERFACE.getType().equals(configType)) {
            try {
                String dataInterfaceName = dataStoreConfiguration.getDataInterfaceName();
                Class.forName(dataInterfaceName);
            } catch (ClassNotFoundException ex) {
                return false;
            }

        }

        String defaultCondition = dataStoreConfiguration.getDefaultCondition();
        if (StringUtils.isNotBlank(defaultCondition)) {
            // 数据仓库需要传递参数，不处理
            return defaultCondition.indexOf(Separator.COLON.getValue()) <= 0;
        }

        return true;
    }
}
