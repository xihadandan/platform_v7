package com.wellsoft.pt.basicdata.serialnumber.service.impl;

import com.google.common.primitives.Ints;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.serialnumber.dao.ISerialNumberOldDefDao;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumber;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberMaintain;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberOldData;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberOldDef;
import com.wellsoft.pt.basicdata.serialnumber.enums.ObjectTypeEnum;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SerialNumberFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.service.ISerialNumberOldDataService;
import com.wellsoft.pt.basicdata.serialnumber.service.ISerialNumberOldDefService;
import com.wellsoft.pt.basicdata.serialnumber.service.ISerialNumberRelationService;
import com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberService;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberRecordParams;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Auther: yt
 * @Date: 2022/5/13 15:39
 * @Description:
 */
@Service
public class SerialNumberOldDefServiceImpl extends AbstractJpaServiceImpl<SerialNumberOldDef, ISerialNumberOldDefDao, String> implements ISerialNumberOldDefService {


    private static Map<String, List<SerialNumberMaintain>> maintainMap = new HashMap<>();
    @Autowired
    private SerialNumberService serialNumberService;
    @Autowired
    private SerialNumberFacadeService serialNumberFacadeService;
    @Autowired
    private SerialNumberMaintainServiceImpl serialNumberMaintainService;
    @Autowired
    private HibernateTransactionManager transactionManager;
    @Autowired
    private FlowDefinitionService flowDefinitionService;
    @Autowired
    private ISerialNumberOldDataService serialNumberOldDataService;
    @Autowired
    private ISerialNumberRelationService serialNumberRelationService;
    @Autowired
    private FormDefinitionService formDefinitionService;

    @Override
    public synchronized void oldDataProcess(int pageSize) {
        long count = this.getDao().countByHQL("select count(uuid) from SerialNumberOldDef where dataState = 0 ", null);
        if (count == 0) {
            this.processed(pageSize);
            return;
        }
        PagingInfo pagingInfo = new PagingInfo(1, pageSize);
        pagingInfo.setTotalCount(count);
        for (int i = 0; i < pagingInfo.getTotalPages(); i++) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);// 事物隔离级别，开启新事务
            TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态
            List<SerialNumberOldDef> oldDefList = this.listByHQLAndPage("from SerialNumberOldDef where dataState = 0 order by uuid", null, pagingInfo);
            transactionManager.commit(status);

            this.oldDefList(oldDefList);
            def = new DefaultTransactionDefinition();
            def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);// 事物隔离级别，开启新事务
            status = transactionManager.getTransaction(def); // 获得事务状态
            try {
                this.saveAll(oldDefList);
                transactionManager.commit(status);
                this.clearSession();
            } catch (Exception e) {
                logger.error(String.format("保存数据【%s】错误：", JsonUtils.object2Json(oldDefList)), e);
                transactionManager.rollback(status);
            }
        }
        this.processed(pageSize);
    }

    private void oldDefList(List<SerialNumberOldDef> oldDefList) {
        for (SerialNumberOldDef oldDef : oldDefList) {
            if (StringUtils.isBlank(oldDef.getTableName())) {
                oldDef.setDataState(2);
                oldDef.setSnData("表名不存在");
            }
            JSONArray snJsonArray = new JSONArray();
            StringBuilder sbMsg = new StringBuilder();
            if (oldDef.getDefinitionType() == 1) {
                //表单
                FormDefinition formDefinition = formDefinitionService.getOne(oldDef.getDefinitionUuid());
                this.getDao().getSession().setReadOnly(formDefinition, true);
                FormDefinitionHandler formDefinitionHandler = this.getFormDefinitionHandler(formDefinition);
                if (formDefinitionHandler == null) {
                    oldDef.setDataState(2);
                    oldDef.setSnData("表单解析失败");
                    continue;
                }
                for (String fieldName : formDefinitionHandler.getFieldNamesOfMainform()) {
                    if (formDefinitionHandler.isInputModeEqSerialNumber(fieldName)) {
                        boolean flg = serialNumberRelationService.existsTableFieldName(oldDef.getTableName(), fieldName);
                        if (!flg) {
                            sbMsg.append("表字段不存在[").append(oldDef.getTableName()).append("][").append(fieldName).append("]");
                            continue;
                        }
                        //"designatedId": "SNO_20160224102418",
                        //"designatedType": "lin",
                        JSONObject jsonObject = formDefinitionHandler.getFieldDefinitionJson(fieldName);
                        if (StringUtils.isBlank(jsonObject.getString("designatedId")) && StringUtils.isBlank(jsonObject.getString("designatedType"))) {
                            continue;
                        }
                        JSONObject snJson = new JSONObject();
                        snJson.put("snId", jsonObject.getString("designatedId"));
                        snJson.put("snType", jsonObject.getString("designatedType"));
                        snJson.put("fieldName", fieldName.toLowerCase());
                        snJsonArray.put(snJson);
                    }
                }
            } else if (oldDef.getDefinitionType() == 2) {
                FlowDefinition flowDefinition = flowDefinitionService.getOne(oldDef.getDefinitionUuid());
                this.getDao().getSession().setReadOnly(flowDefinition, true);
                FlowDelegate flowDelegate = this.getFlowDelegate(flowDefinition);
                if (flowDelegate == null) {
                    oldDef.setDataState(2);
                    oldDef.setSnData("流程解析失败");
                    continue;
                }
                if (flowDelegate.getFlow() != null && flowDelegate.getFlow().getTasks() != null) {
                    for (TaskElement task : flowDelegate.getFlow().getTasks()) {
                        if (StringUtils.isNotBlank(task.getSerialNo())) {
                            JSONObject snJson = new JSONObject();
                            snJson.put("snId", task.getSerialNo());
                            snJson.put("snType", "");
                            snJson.put("fieldName", "serial_no");
                            snJsonArray.put(snJson);
                        }
                    }
                }
            }
            if (snJsonArray.length() > 0) {
                oldDef.setDataState(1);
                oldDef.setSnData(snJsonArray.toString());
            } else {
                oldDef.setDataState(2);
                if (sbMsg.length() > 0) {
                    oldDef.setSnData(sbMsg.toString());
                } else {
                    oldDef.setSnData("没有流水号定义数据");
                }
            }
        }
    }

    private FormDefinitionHandler getFormDefinitionHandler(FormDefinition formDefinition) {
        try {
            FormDefinitionHandler formDefinitionHandler = formDefinition.doGetFormDefinitionHandler();
            return formDefinitionHandler;
        } catch (Exception e) {
            logger.error("表单[" + formDefinition.getId() + "]解析失败:", e);
            return null;
        }
    }

    private FlowDelegate getFlowDelegate(FlowDefinition flowDefinition) {
        try {
            FlowDelegate flowDelegate = new FlowDelegate(flowDefinition);
            return flowDelegate;
        } catch (Exception e) {
            logger.error("流程[" + flowDefinition.getId() + "]解析失败:", e);
            return null;
        }
    }

    private void processed(int pageSize) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);// 事物隔离级别，开启新事务
        TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态
        long count = this.getDao().countByHQL("select count(uuid) from SerialNumberOldDef where dataState = 1 ", null);
        transactionManager.commit(status);
        if (count == 0) {
            return;
        }
        PagingInfo pagingInfo = new PagingInfo(1, pageSize);
        pagingInfo.setTotalCount(count);
        for (int i = 0; i < pagingInfo.getTotalPages(); i++) {
            def = new DefaultTransactionDefinition();
            def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);// 事物隔离级别，开启新事务
            status = transactionManager.getTransaction(def); // 获得事务状态
            List<SerialNumberOldDef> oldDefList = this.listByHQLAndPage("from SerialNumberOldDef where dataState = 1 order by uuid", null, pagingInfo);
            try {
                List<SerialNumberOldData> oldDataList = this.processed(oldDefList);
                this.serialNumberOldDataService.saveAll(oldDataList);
                this.saveAll(oldDefList);
                transactionManager.commit(status);
                this.clearSession();
            } catch (Exception e) {
                logger.error(String.format("保存数据【%s】错误：", JsonUtils.object2Json(oldDefList)), e);
                transactionManager.rollback(status);
            }
        }
    }

    private List<SerialNumberOldData> processed(List<SerialNumberOldDef> oldDefList) {
        List<SerialNumberOldData> oldDataList = new ArrayList<>();
        for (SerialNumberOldDef oldDef : oldDefList) {
            String tableName = oldDef.getTableName().toLowerCase();
            JSONArray snJson = new JSONArray(oldDef.getSnData());
            for (int i = 0; i < snJson.length(); i++) {
                JSONObject fieldNameJson = snJson.getJSONObject(i);
                String fieldName = fieldNameJson.getString("fieldName");
                String sql = String.format("select uuid,%s from %s where %s is not null ", fieldName, tableName, fieldName);
                if (oldDef.getDefinitionType() == 2) {
                    sql = sql + "and flow_def_uuid = '" + oldDef.getDefinitionUuid() + "'";
                }
                sql = sql + " order by create_time desc ";
                List<QueryItem> queryItemList = new ArrayList<>();
                try {
                    queryItemList = serialNumberService.getDao().listQueryItemBySQL(sql, null, null);
                } catch (Exception e) {
                    logger.error(String.format("查询语句sql[%s]，执行错误：", sql), e);
                }
                if (CollectionUtils.isEmpty(queryItemList)) {
                    logger.error(String.format("查询语句sql[%s]，数据不存在", sql));
                    continue;
                }

                String snId = fieldNameJson.getString("snId");
                String snType = fieldNameJson.getString("snType");
                List<SerialNumberMaintain> maintainList = null;
                if (StringUtils.isNotBlank(snId)) {
                    maintainList = maintainMap.get("snId_" + snId);
                    if (maintainList == null) {
                        maintainList = serialNumberMaintainService.getDao().listByFieldEqValue("id", snId);
                        maintainMap.put("snId_" + snId, maintainList);
                    }
                } else {
                    maintainList = maintainMap.get("snType_" + snType);
                    if (maintainList == null) {
                        maintainList = new ArrayList<>();
                        String[] snTypes = snType.split(";");
                        for (String type : snTypes) {
                            List<SerialNumber> serialNumberList = serialNumberService.getDao().listByFieldEqValue("type", type);
                            for (SerialNumber serialNumber : serialNumberList) {
                                List<SerialNumberMaintain> serialNumberMaintainList = serialNumberMaintainService.getDao().listByFieldEqValue("id", serialNumber.getId());
                                maintainList.addAll(serialNumberMaintainList);
                            }
                        }
                        maintainMap.put("snType_" + snType, maintainList);
                    }
                }
                Map<String, List<String>> serialNoUuidMap = new LinkedHashMap<>();
                for (QueryItem queryItem : queryItemList) {
                    String uuid = queryItem.getString(QueryItem.getKey("uuid"));
                    String serialNo = queryItem.getString(QueryItem.getKey(fieldName));
                    if (StringUtils.isNotBlank(serialNo)) {
                        List<String> uuidList = serialNoUuidMap.get(serialNo);
                        if (uuidList == null) {
                            uuidList = new ArrayList<>();
                            serialNoUuidMap.put(serialNo, uuidList);
                        }
                        uuidList.add(uuid);
                    }
                }
                for (String serialNo : serialNoUuidMap.keySet()) {
                    SerialNumberMaintain serialNumberMaintain = this.getMaintain(serialNo, maintainList);
                    List<String> uuidList = serialNoUuidMap.get(serialNo);
                    if (serialNumberMaintain != null) {
                        String uuid = uuidList.get(0);
                        SerialNumberRecordParams recordParams = new SerialNumberRecordParams();
                        recordParams.setSnId(serialNumberMaintain.getId());
                        recordParams.setObjectType(ObjectTypeEnum.TABLE.getType());
                        recordParams.setObjectName(tableName);
                        recordParams.setFieldName(fieldName);
                        recordParams.setDataUuid(uuid);
                        recordParams.setKeyPart(serialNumberMaintain.getKeyPart());
                        recordParams.setHeadPart(serialNumberMaintain.getHeadPart());
                        recordParams.setLastPart(serialNumberMaintain.getLastPart());
                        recordParams.setPointer(Ints.tryParse(serialNumberMaintain.getPointer()));
                        recordParams.setSerialNo(serialNo);
                        boolean flg = serialNumberFacadeService.serialNumberRecord(recordParams);
                        if (flg) {
                            for (int j = 1; j < uuidList.size(); j++) {
                                String dataUuid = uuidList.get(j);
                                SerialNumberOldData oldData = new SerialNumberOldData();
                                oldData.setSnType(snType);
                                oldData.setSnId(snId);
                                oldData.setSerialNo(serialNo);
                                oldData.setObjectName(tableName);
                                oldData.setFieldName(fieldName);
                                oldData.setDataUuid(dataUuid);
                                oldData.setDataState(2);
                                oldData.setRecordDataUuid(uuid);
                                oldDataList.add(oldData);
                            }
                        } else {
                            for (String dataUuid : uuidList) {
                                SerialNumberOldData oldData = new SerialNumberOldData();
                                oldData.setSnType(snType);
                                oldData.setSnId(snId);
                                oldData.setSerialNo(serialNo);
                                oldData.setObjectName(tableName);
                                oldData.setFieldName(fieldName);
                                oldData.setDataState(1);
                                oldData.setDataUuid(dataUuid);
                                oldData.setMaintainUuid(serialNumberMaintain.getUuid());
                                oldDataList.add(oldData);
                            }
                        }
                    }
                    if (serialNumberMaintain == null) {
                        for (String uuid : uuidList) {
                            SerialNumberOldData oldData = new SerialNumberOldData();
                            oldData.setSnType(snType);
                            oldData.setSnId(snId);
                            oldData.setSerialNo(serialNo);
                            oldData.setObjectName(tableName);
                            oldData.setFieldName(fieldName);
                            oldData.setDataUuid(uuid);
                            oldData.setDataState(3);
                            oldDataList.add(oldData);
                        }
                    }
                }
            }
            oldDef.setDataState(3);
        }
        return oldDataList;
    }

    private SerialNumberMaintain getMaintain(String serialNo, List<SerialNumberMaintain> maintainList) {
        if (CollectionUtils.isEmpty(maintainList)) {
            return null;
        }
        SerialNumberMaintain serialNumberMaintain = new SerialNumberMaintain();
        for (SerialNumberMaintain maintain : maintainList) {
            String headPart = maintain.getHeadPart();
            String lastPart = maintain.getLastPart();
            if (headPart == null) {
                headPart = "";
            }
            if (lastPart == null) {
                lastPart = "";
            }
            if (serialNo.startsWith(headPart) && serialNo.endsWith(lastPart)) {
                serialNo = serialNo.substring(headPart.length());
                serialNo = serialNo.substring(0, serialNo.lastIndexOf(lastPart));
                if (StringUtils.isBlank(serialNo)) {
                    continue;
                }
                Integer pointer = Ints.tryParse(serialNo);
                if (pointer == null) {
                    continue;
                } else {
                    BeanUtils.copyProperties(maintain, serialNumberMaintain, IdEntity.BASE_FIELDS);
                    serialNumberMaintain.setUuid(maintain.getUuid());
                    serialNumberMaintain.setPointer(pointer + "");
                    return serialNumberMaintain;
                }
            }
        }
        return null;
    }


}
