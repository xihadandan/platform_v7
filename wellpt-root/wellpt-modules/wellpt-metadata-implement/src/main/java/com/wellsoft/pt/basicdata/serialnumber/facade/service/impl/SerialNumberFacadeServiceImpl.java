/*
 * @(#)2018年4月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.facade.service.impl;

import com.google.common.collect.Sets;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.serialnumber.bean.SerialNumberComplementCalculation;
import com.wellsoft.pt.basicdata.serialnumber.bean.SerialNumberMaintainBean;
import com.wellsoft.pt.basicdata.serialnumber.bean.SerialNumberSupplementBean;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumber;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberMaintain;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberRecord;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumberRelation;
import com.wellsoft.pt.basicdata.serialnumber.enums.ObjectTypeEnum;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SerialNumberFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.service.ISerialNumberRecordService;
import com.wellsoft.pt.basicdata.serialnumber.service.ISerialNumberRelationService;
import com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberMaintainService;
import com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberService;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberRecordParams;
import com.wellsoft.pt.jpa.util.HqlUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月19日.1	chenqiong		2018年4月19日		Create
 * </pre>
 * @date 2018年4月19日
 */
@Service
public class SerialNumberFacadeServiceImpl extends AbstractApiFacade implements SerialNumberFacadeService {

    @Autowired
    SerialNumberService serialNumberService;
    @Autowired
    private SerialNumberMaintainService serialNumberMaintainService;

    @Autowired
    private ISerialNumberRecordService serialNumberRecordService;
    @Autowired
    private ISerialNumberRelationService serialNumberRelationService;


    @Override
    public SerialNumber getById(String serialNo) {
        return serialNumberService.getById(serialNo);
    }

    @Transactional
    @Override
    public ResultMessage addSerialNumberRecord(SerialNumberRecordParams params) {
        params.paramValid();
        SerialNumber serialNumber = serialNumberService.getById(params.getSnId());
        if (serialNumber == null) {
            return new ResultMessage("流水号定义不存在", false);
        }
        SerialNumberMaintain serialNumberMaintain = serialNumberMaintainService.getByIdAndKeyPart(params.getSnId(), params.getKeyPart());
        if (serialNumberMaintain == null) {
            return new ResultMessage("流水号维护记录不存在", false);
        }
        if (!serialNumberRelationService.existsTable(params.getObjectName())) {
            return new ResultMessage("表【" + params.getObjectName() + "】不存在", false);
        }
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("uuid", params.getDataUuid());
        String sql = "select uuid from " + params.getObjectName() + " where uuid =:uuid";
        List<String> uuidList = serialNumberService.getDao().listCharSequenceBySQL(sql, paramsMap);
        if (CollectionUtils.isEmpty(uuidList)) {
            return new ResultMessage("表【" + params.getObjectName() + "】数据uuid:【" + params.getDataUuid() + "】不存在", false);
        }
        if (this.serialNumberRecord(params)) {
            return new ResultMessage("保存成功", true);
        } else {
            return new ResultMessage("流水号重复", false);
        }
    }


    @Override
    public boolean serialNumberRecord(SerialNumberRecordParams params) {
        params.paramValid();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("snId", params.getSnId());
        paramsMap.put("objectType", params.getObjectType());
        paramsMap.put("objectName", params.getObjectName());
        paramsMap.put("fieldName", params.getFieldName());
        if (StringUtils.isBlank(params.getKeyPart())) {
            params.setKeyPart("null");
        }
        SerialNumberRelation relation = serialNumberRelationService.getDao().getOneByHQL("from SerialNumberRelation where snId=:snId and objectType=:objectType and objectName=:objectName and fieldName=:fieldName ", paramsMap);
        if (relation == null) {
            relation = params.relation();
            serialNumberRelationService.save(relation);
        }
        paramsMap = new HashMap<>();
        paramsMap.put("relationUuid", relation.getUuid());
        paramsMap.put("pointer", params.getPointer());
        paramsMap.put("keyPart", params.getKeyPart());
        StringBuilder sbHql = new StringBuilder("from SerialNumberRecord where relationUuid=:relationUuid and keyPart=:keyPart and pointer=:pointer ");
        SerialNumberRecord record = serialNumberRecordService.getDao().getOneByHQL(sbHql.toString(), paramsMap);
        if (record == null) {
            paramsMap.put("dataUuid", params.getDataUuid());
            record = serialNumberRecordService.getDao().getOneByHQL("from SerialNumberRecord where relationUuid=:relationUuid and dataUuid=:dataUuid", paramsMap);
        }
        if (record == null) {
            record = params.record(relation.getUuid());
            serialNumberRecordService.save(record);
        } else if (record.getDataUuid().equals(params.getDataUuid())) {
            params.convert(record);
            serialNumberRecordService.update(record);
        } else {
            logger.error(String.format("流水号重复：%s", JsonUtils.object2Json(params)));
            return false;
        }
        return true;
    }

    @Override
    public List<SerialNumberSupplementBean> supplementByTableFieldName(String snId, String tableName, String fieldName, List<SerialNumberMaintainBean> maintainList) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("snId", snId);
        //数据库表
        paramsMap.put("objectType", ObjectTypeEnum.TABLE.getType());
        paramsMap.put("objectName", tableName.toLowerCase());
        paramsMap.put("fieldName", fieldName.toLowerCase());

        SerialNumberRelation relation = serialNumberRelationService.getDao().getOneByHQL("from SerialNumberRelation where snId=:snId and objectType=:objectType and objectName=:objectName and fieldName=:fieldName", paramsMap);
        if (relation == null) {
            SerialNumberComplementCalculation complementCalculation = SerialNumberComplementCalculation.getInstance(maintainList, null, null, null);
            return complementCalculation.getSupplementBeanList();
        }
        List<SerialNumberRelation> relationList = new ArrayList<>();
        relationList.add(relation);
        return getSupplementBeanList(maintainList, relationList);

    }

    @Override
    public List<SerialNumberSupplementBean> supplement(String snId, List<SerialNumberMaintainBean> maintainList) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("snId", snId);
        //数据库表
        paramsMap.put("objectType", ObjectTypeEnum.TABLE.getType());
        List<SerialNumberRelation> relationList = serialNumberRelationService.listByHQL("from SerialNumberRelation where snId=:snId and objectType=:objectType ", paramsMap);
        if (CollectionUtils.isEmpty(relationList)) {
            SerialNumberComplementCalculation complementCalculation = SerialNumberComplementCalculation.getInstance(maintainList, null, null, null);
            return complementCalculation.getSupplementBeanList();
        }
        return getSupplementBeanList(maintainList, relationList);
    }

    private List<SerialNumberSupplementBean> getSupplementBeanList(List<SerialNumberMaintainBean> maintainList, List<SerialNumberRelation> relationList) {
        Map<String, String> uuidObjectNamMap = new HashMap<>();
        for (SerialNumberRelation relation : relationList) {
            uuidObjectNamMap.put(relation.getUuid(), relation.getObjectName());
        }
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("relationUuids", uuidObjectNamMap.keySet());
        List<SerialNumberRecord> recordList = serialNumberRecordService.listByHQL("from SerialNumberRecord where relationUuid in (:relationUuids)", paramsMap);

        //key:objectName val:Set<dataUuid>
        Map<String, Set<String>> objectNameDataUuidMap = new HashMap<>();
        for (SerialNumberRecord record : recordList) {
            String objectName = uuidObjectNamMap.get(record.getRelationUuid());
            Set<String> dataUuidSet = objectNameDataUuidMap.get(objectName);
            if (dataUuidSet == null) {
                dataUuidSet = new HashSet<>();
                objectNameDataUuidMap.put(objectName, dataUuidSet);
            }
            dataUuidSet.add(record.getDataUuid());
        }
        Map<String, Set<String>> tableDataUuidMap = new HashMap<>();
        for (String tableName : objectNameDataUuidMap.keySet()) {
            if (!serialNumberRelationService.existsTable(tableName)) {
                tableDataUuidMap.put(tableName, new HashSet<>());
            } else {
                Set<String> uuidSet = objectNameDataUuidMap.get(tableName);
                Map<String, Object> params = new HashMap<>();
                StringBuilder sql = new StringBuilder("select uuid from " + tableName.toLowerCase() + " where ");
                HqlUtils.appendSql("uuid", params, sql, Sets.<Serializable>newHashSet(uuidSet));
                List<String> uuidList = serialNumberService.getDao().listCharSequenceBySQL(sql.toString(), params);
                tableDataUuidMap.put(tableName, new HashSet<>(uuidList));
            }
        }
        SerialNumberComplementCalculation complementCalculation = SerialNumberComplementCalculation.getInstance(maintainList, relationList, recordList, tableDataUuidMap);
        for (SerialNumberRecord record : complementCalculation.getDelRcordList()) {
            serialNumberRecordService.delete(record);
        }
        return complementCalculation.getSupplementBeanList();
    }


}
