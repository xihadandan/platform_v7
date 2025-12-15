/*
 * @(#)2012-10-30 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.data.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.context.exception.UniqueException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.context.util.io.ClobUtils;
import com.wellsoft.pt.dyform.facade.dto.*;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.dao.FormDataDao;
import com.wellsoft.pt.dyform.implement.data.enums.EnumDyformExceptionType;
import com.wellsoft.pt.dyform.implement.data.enums.EnumFormDataStatus;
import com.wellsoft.pt.dyform.implement.data.enums.EnumValidateCode;
import com.wellsoft.pt.dyform.implement.data.exceptions.DyformDataSaveException;
import com.wellsoft.pt.dyform.implement.data.exceptions.FormDataValidateException;
import com.wellsoft.pt.dyform.implement.data.exceptions.SaveDataException;
import com.wellsoft.pt.dyform.implement.data.service.FormDataService;
import com.wellsoft.pt.dyform.implement.data.utils.FormDataHandler;
import com.wellsoft.pt.dyform.implement.data.utils.FormDataResultTransformer;
import com.wellsoft.pt.dyform.implement.data.utils.ValidateMsg;
import com.wellsoft.pt.dyform.implement.definition.cache.DyformCacheUtils;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.DyDateFomat;
import com.wellsoft.pt.dyform.implement.definition.dao.DyformFieldValueRelDao;
import com.wellsoft.pt.dyform.implement.definition.entity.DyformFieldValueRel;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumRelationTblSystemField;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.DyformDataOptions;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQuery;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryData;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo;
import com.wellsoft.pt.dyform.implement.repository.usertable.support.UserTableFormDataQueryImpl;
import com.wellsoft.pt.dyform.implement.repository.usertable.support.UserTableFormDataQueryInfo;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
import com.wellsoft.pt.jpa.support.QueryItemResultTransformer;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.repository.dao.DbTableDao;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.entity.CommonUnit;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.exception.SQLGrammarException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.Provider;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author lilin
 * @ClassName: FormDefinitionService
 * @Description: 动态表单数据业务处理
 */
@Service
@Transactional
public class FormDataServiceImpl implements FormDataService {
    @Autowired
    FormDataDao formDataDao;
    @Autowired
    DyformFieldValueRelDao dyformFieldValueRelDao;
    @Autowired
    DyFormFacade dyFormFacade;
    @Autowired
    OrgApiFacade orgApiFacade;
    @Autowired
    MongoFileService mongoFileService;
    // private static Logger LOG = LoggerFactory.getLogger(FormDataServiceImpl.class);
    private Logger logger = LoggerFactory.getLogger(FormDataServiceImpl.class);
    @Autowired
    private DbTableDao dbTableDao;
    @Value("${database.type:Oracle11g}")
    private String databaseType;

    /**
     * Add the names that are non-null in columns to s, separating them with
     * commas.
     *
     * @param sb
     * @param columns
     */
    public static void appendColumns(StringBuilder sb, String[] columns) {
        int n = columns.length;

        for (int i = 0; i < n; i++) {
            String column = columns[i];

            if (column != null) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(column);
            }
        }
        sb.append(' ');
    }

    /**
     * @param sb
     * @param name
     * @param clause
     */
    private static void appendClause(StringBuilder sb, String name, String clause) {
        if (!StringUtils.isBlank(clause)) {
            sb.append(name);
            sb.append(clause);
        }
    }

    /**
     * 判断是否是新增记录
     *
     * @param mainformUuid
     * @param dataUuidOfMainform
     * @param addedFormDatas
     * @return
     */
    private static boolean isInAddedOperateLog(String formUuid, String dataUuid,
                                               Map<String, List<String>> addedFormDatas) {
        if (addedFormDatas == null || addedFormDatas.size() == 0) {
            return false;
        }
        List<String> addedRecords = addedFormDatas.get(formUuid);
        if (addedRecords == null || addedRecords.size() == 0) {
            return false;
        }
        for (String du : addedRecords) {
            if (StringUtils.equals(du, dataUuid)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否是新增记录
     *
     * @param mainformUuid
     * @param dataUuidOfMainform
     * @param addedFormDatas
     * @return
     */
    private static boolean isInUpdatedOperateLog(String formUuid, String dataUuid,
                                                 Map<String, Map<String, Set<String>>> updatedFormDatas) {
        if (updatedFormDatas == null || updatedFormDatas.size() == 0) {
            return false;
        }

        Set<String> fields = getUpdatedFields(formUuid, dataUuid, updatedFormDatas);
        if (fields == null || fields.size() == 0) {
            return false;
        }
        return true;
    }

    private static Set<String> getUpdatedFields(String formUuid, String dataUuid,
                                                Map<String, Map<String, Set<String>>> updatedFormDatas) {
        Map<String, Set<String>> updatedRecords = updatedFormDatas.get(formUuid);
        if (updatedRecords == null || updatedRecords.size() == 0) {
            return null;
        }
        for (String du : updatedRecords.keySet()) {
            if (du.equals(dataUuid)) {
                Set<String> fields = updatedRecords.get(du);
                return fields;
            }
        }
        return null;
    }

    private static Map<String, Object> getData(Map<String, List<Map<String, Object>>> formDatas, String formUuid,
                                               String dataUuid) {
        List<Map<String, Object>> records = formDatas.get(formUuid);
        if (records == null) {
            return null;
        }
        for (Map<String, Object> map : records) {
            FormDataDao.setKeyAsLowerCase(map);// 将key设置转化成小写
            if (dataUuid.equals(map.get(EnumSystemField.uuid.name()))) {
                return map;
            }
        }
        return null;
    }

    /*
     * public void saveSignature(String dataUuid, DyformDataSignature signature)
     * { if (signature == null) { return; }
     *
     * if (DyformDataSignature.STATUS_FAILURE == signature.getStatus()) { throw
     * new RuntimeException(signature.getRemark()); }
     *
     * if (DyformDataSignature.STATUS_SUCCESS == signature.getStatus()) { //
     * 当前用户证书登录验证 if (signature.getCertificate() != null) {
     * fjcaAppsService.checkCurrentCertificate(signature.getCertificate());
     *
     * // 签名验证 int retCode = FJCAUtils.verify(signature.getDigestValue(),
     * signature.getSignatureValue(), signature.getCertificate()); if (retCode
     * != 0) { throw new RuntimeException("表单数据数字签名验证失败!"); } } String
     * signedFormData = signature.getSignedData(); if (signedFormData == null) {
     * signedFormData = ""; } // 将由前台页面js生成的表单json保存成一个文件,且同时保存该文件对应的签名
     * MongoFileEntity file = mongoFileService.saveFile("formData.json", new
     * StringInputStream(signedFormData, DyFormConfig.CHARSET),
     * signature.getDigestValue(), signature.getDigestAlgorithm(), signature
     * .getSignatureValue(), signature.getCertificate()); if (file == null) {//
     * 文件保存失败 throw new RuntimeException("签名信息保存失败"); } else {// 文件保存成功
     * mongoFileService.pushFileToFolder(dataUuid, file.getFileID(),
     * EnumSystemField.signature_.name()); }
     *
     * } }
     */

    public List<String> queryFormDataList(String tblName, String fieldName) {
        return formDataDao.queryFormDataList(tblName, fieldName);
    }

    @Override
    public List<String> queryFormDataList(String tblName, String fieldName, String where, Map<String, Object> params) {
        return formDataDao.queryFormDataList(tblName, fieldName, where, params);
    }

    // @Autowired
    // FJCAAppsService fjcaAppsService;
    @Override
    public boolean queryFormDataExists(String tblName, String fieldName, String fieldValue) throws Exception {
        List<Map<String, Object>> list = formDataDao.queryFormDataList(tblName, fieldName, fieldValue, 1, 1);
        if (list == null || list.size() == 0) {// 数据在表单中不存在
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean queryFormDataExists(String tblName, String[] fieldName, String[] fieldValue) throws Exception {
        List<Map<String, Object>> list = formDataDao.queryFormDataList(tblName, fieldName, fieldValue, 1, 1);
        if (list == null || list.size() == 0) {// 数据在表单中不存在
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean queryFormDataExists(String uuid, String tblName, String fieldName, String fieldValue)
            throws Exception {
        List<Map<String, Object>> list = formDataDao.queryFormDataList(uuid, tblName, fieldName, fieldValue, 1, 1);
        if (list == null || list.size() == 0) {// 数据在表单中不存在
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean queryFormDataExists(String uuid, String tblName, String[] fieldName, String[] fieldValue)
            throws Exception {
        List<Map<String, Object>> list = formDataDao.queryFormDataList(uuid, tblName, fieldName, fieldValue, 1, 1);
        if (list == null || list.size() == 0) {// 数据在表单中不存在
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String saveFormData(String mainformUuid,
                               Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> formDatas,
                               Map<String, List<String>> deletedFormDatas, DyformDataSignature signature) {
        // long time1 = System.currentTimeMillis();
        Assert.notNull(formDatas, "表单数据为null");
        Assert.notEmpty(formDatas, "表单数据为空");
        // Transaction transaction = this.formDataDao.getSession().getTransaction();
        try {
            Iterator<String> formUuidIt = formDatas.keySet().iterator();

            FormDefinition mainformDefinition = // this.getDefinition(mainformUuid);
                    (FormDefinition) dyFormFacade.getFormDefinition(mainformUuid);

            // 获取主表的数据id
            Map<String, Object> formDataOfMainform = formDatas.get(mainformUuid).get(0);
            Object dataUuidOfMainform = formDataOfMainform.get("uuid");
            if (dataUuidOfMainform == null || ((String) dataUuidOfMainform).trim().length() == 0) {
                dataUuidOfMainform = dyFormFacade.createUuid();
                formDataOfMainform.put("uuid", dataUuidOfMainform);
            }

            // 保存数据
            while (formUuidIt.hasNext()) {
                String formUuid = formUuidIt.next();
                if (mainformUuid.equals(formUuid)) {// 主表数据
                    this.updateFormData(mainformDefinition, formDataOfMainform);
                } else {// 从表数据
                    FormDefinition formDefinition = (FormDefinition) dyFormFacade.getFormDefinition(formUuid);
                    List<Map<String, Object>> formDatasInOneForm = formDatas.get(formUuid);
                    for (Map<String, Object> map : formDatasInOneForm) {
                        if (map == null) {
                            continue;
                        }
                        map.put(EnumRelationTblSystemField.mainform_data_uuid.name(), dataUuidOfMainform);
                        map.put(EnumRelationTblSystemField.mainform_form_uuid.name(), mainformDefinition.getUuid());
                        // map.put(EnumRelationTblSystemField.sort_order.name(),
                        // map.get("seqNO"));//设置前台的排序
                        this.updateFormData(formDefinition, map);
                    }
                }
            }

            // 保存签名
            // this.saveSignature((String) dataUuidOfMainform, signature);

            // 被解除关系的从表
            if (deletedFormDatas == null || deletedFormDatas.size() == 0) {
                return (String) dataUuidOfMainform;
            }
            formUuidIt = deletedFormDatas.keySet().iterator();
            while (formUuidIt.hasNext()) {
                String formUuid = formUuidIt.next();
                List<String> deletedFormData = deletedFormDatas.get(formUuid);
                if (deletedFormData == null || deletedFormData.isEmpty()) {
                    continue;
                }
                // 解除关系
                this.doProcessDeletedFormData(formUuid, deletedFormData, (String) dataUuidOfMainform);
            }

            return (String) dataUuidOfMainform;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (e instanceof FormDataValidateException) {
                throw (FormDataValidateException) e;
            } else {
                throw new SaveDataException(e.getMessage());
            }
        }
    }

    private void doProcessDeletedFormData(String formUuid, List<String> dataUuids, String dataUuidOfMainform) {
        DyFormFormDefinition FormDefinition = (DyFormFormDefinition) dyFormFacade.getFormDefinition(formUuid);
        this.formDataDao.deleteSubformFormMainform(FormDefinition.doGetRelationTblNameOfpForm(), dataUuids,
                dataUuidOfMainform);
    }

    /**
     * 根据定义保存数据
     *
     * @param formUuid           数据保存的目的表的定义uuid
     * @param mainformUuid       主表的定义uuid,如果该值为null则表示要处理的数据是主表的数据，如果非null表示要处理的数据是从表的数据
     * @param formDataOfMainform
     * @throws JSONException
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws ParseException
     */
    @SuppressWarnings("unchecked")
    public String updateFormData(String formUuid, Map<String, Object> formDataColMap) throws JSONException,
            ParseException, UnsupportedEncodingException {
        // 根据定义来设置特殊字段
        FormDefinition formDefinition = (FormDefinition) dyFormFacade.getFormDefinition(formUuid);
        return updateFormData(formDefinition, formDataColMap);
    }

    /**
     * 根据定义保存数据
     *
     * @param formDefinition
     * @param formDataColMap
     * @return
     * @throws ParseException
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    private String updateFormData(FormDefinition formDefinition, Map<String, Object> formDataColMap)
            throws ParseException, UnsupportedEncodingException, JSONException {
        String formUuid = formDefinition.getUuid();
        boolean isNew = true;// 如果表单数据已存在于表单中则为false, 如果不存在于表单中则为true
        String dataUuid = (String) formDataColMap.get(EnumSystemField.uuid.name());
        if (dataUuid == null || dataUuid.trim().length() == 0) {
            dataUuid = dyFormFacade.createUuid();
            formDataColMap.put(EnumSystemField.uuid.name(), dataUuid);
        }

        FormDefinitionHandler dUtils = formDefinition.doGetFormDefinitionHandler();
        FormDataHandler dataUtils = new FormDataHandler(formUuid, formDataColMap, dUtils);

        List<String> fieldNames = dUtils.getFieldNamesOfMaintable();// 表单的各字段名称

        Map<String, Object> dbFormDataColMap = this.getFormDataOfMainform(dUtils.doGetTblNameOfpForm(), dataUuid,
                fieldNames);

        if (dbFormDataColMap == null || dbFormDataColMap.size() == 0) {
            isNew = true;
        } else {
            isNew = false;
        }

        // 验证数据格式的正确性
        ValidateMsg vMsg = dataUtils.validate();
        if (vMsg.getCode() != EnumValidateCode.SUCESS) {
            throw new FormDataValidateException(vMsg.getMsg());
        }

        if (isNew) {// 新数据
            Iterator<String> it = fieldNames.iterator();
            while (it.hasNext()) {
                String fieldName = it.next();
                logger.debug("processed fieldName:" + fieldName);
                if (!dUtils.isTblField(fieldName)) {// 非表单的字段
                    it.remove();
                }
                if (dUtils.isValueCreateBySystemWhenSave(fieldName)) {// 在保存时由系统产生
                    dataUtils.doProcessValueCreateBySystem(fieldName);
                } else/* if (dUtils.isValueCreateByUser(fieldName)) */ {// 从前台插入
                    dataUtils.doProcessValueCreateByUser(fieldName);
                }
                // dataUtils.validate(fieldName);//验证数据
            }

            formDataColMap.put(EnumSystemField.status.name(), EnumFormDataStatus.DYFORM_DATA_STATUS_DEFAULT.getValue());
            formDataColMap.put(EnumSystemField.form_uuid.name(), formUuid);

            this.formDataDao.save(formDefinition, formDataColMap);
            //
            dealFieldValueRef(formDefinition, dUtils, formDataColMap, false);

        } else {
            Iterator<String> it = formDataColMap.keySet().iterator();
            while (it.hasNext()) {
                String fieldName = it.next();
                logger.debug("processed fieldName:" + fieldName);
                if (!dUtils.isTblField(fieldName)) {// 非表单的字段
                    it.remove();
                }
                if (!dUtils.isFieldInDefinition(fieldName)) {
                    continue;
                }
                if (dUtils.isValueCreateBySystemWhenSave(fieldName)) {// 在保存时由系统产生
                    dataUtils.doProcessValueCreateBySystem(fieldName);
                } else /* if (dUtils.isValueCreateByUser(fieldName)) */ {// 从前台插入

                    dataUtils.doProcessValueCreateByUser(fieldName);
                }
            }

            this.formDataDao.update(formDefinition, formDataColMap);
            dealFieldValueRef(formDefinition, dUtils, formDataColMap, true);
        }
        return dataUuid;
    }

    public Map<String, Object> getFormDataOfMainform(String formType, String tblName, String dataUuid,
                                                     List<String> fieldNames) {

        StringBuilder sqlBuffer = new StringBuilder();
        sqlBuffer.append("select uuid,creator,create_time,modifier,modify_time, form_uuid, rec_ver");
        for (Object fieldNameObj : fieldNames) {
            String fieldName = (String) fieldNameObj;
            sqlBuffer.append(", ").append(fieldName);
        }
        sqlBuffer.append(" from ");
        sqlBuffer.append(tblName);
        if (dataUuid != null) {
            sqlBuffer.append(" where ");
            if (DyformTypeEnum.isMSTform(formType)) {
                sqlBuffer.append(" mainform_data_uuid = '");
                sqlBuffer.append(dataUuid);
                sqlBuffer.append("'");
            } else {
                sqlBuffer.append(" uuid = '");
                sqlBuffer.append(dataUuid);
                sqlBuffer.append("'");
            }
        }

        try {
            List<Map<String, Object>> list = dbTableDao.query(sqlBuffer.toString(), 1);
            if (list == null || list.size() == 0) {
                return null;
            }

            List<String> upperCaseFields = Lists.newArrayList();
            for (String fieldName : fieldNames) {
                if (!StringUtils.equals(fieldName, fieldName.toLowerCase())) {
                    upperCaseFields.add(fieldName);
                }
            }
            for (String fieldName : upperCaseFields) {
                for (Map<String, Object> record : list) {
                    Object value = record.get(fieldName.toLowerCase());
                    record.remove(fieldName.toLowerCase());
                    record.put(fieldName, value);
                }
            }

            return list.get(0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public Map<String, Object> getFormDataOfMainform(String tblName, String dataUuid, List<String> fieldNames) {
        return getFormDataOfMainform(DyformTypeEnum.P.getValue(), tblName, dataUuid, fieldNames);
    }

    public Map<String, Object> getFormDataOfMainform(String formUuid, String dataUuid, boolean formatValue) {
        FormDefinition formDefinition = ((FormDefinition) dyFormFacade.getFormDefinition(formUuid));
        return getFormDataOfMainform(formDefinition, dataUuid, formatValue);

    }

    /**
     * @param formDefinition
     * @param dataUuid
     * @return
     */
    private Map<String, Object> getFormDataOfMainform(FormDefinition formDefinition, String dataUuid,
                                                      boolean formatValue) {
        FormDefinitionHandler dUtils = formDefinition.doGetFormDefinitionHandler();
        Map<String, Object> formDataOfMainform = getFormDataOfMainform2(formDefinition, dataUuid, dUtils);
        if (formDataOfMainform == null) {
            return null;
        }
        List<LogicFileInfo> allfiles = this.mongoFileService.getNonioFilesFromFolder(dataUuid, null);
        if (allfiles != null && allfiles.size() > 0) {
            for (String fieldName : dUtils.getFieldNamesOfMainform()) {
                if (dUtils.isInputModeEqAttach(fieldName)) {// 附件
                    List<LogicFileInfo> files = this.getFiles(allfiles, fieldName);
                    formDataOfMainform.put(fieldName, files);
                }
            }
        }
        // 转换时间格式
        formatValueOfFormData(formDataOfMainform, dUtils);
        return formDataOfMainform;
    }

    /**
     * 如何描述该方法
     *
     * @param formDefinition
     * @param dataUuid
     * @param dUtils
     * @return
     */
    private Map<String, Object> getFormDataOfMainform2(FormDefinition formDefinition, String dataUuid,
                                                       FormDefinitionHandler dUtils) {
        List<String> fieldNames = new ArrayList<String>();
        if ((dUtils.isFormTypeAsVform() || dUtils.isFormTypeAsMform())
                && (!dUtils.getFormDefinition().has("useDataModel") || !dUtils.getFormDefinition().getBoolean("useDataModel"))) {// 展现单据
            fieldNames.addAll(dUtils.getFieldNamesOfMainformInPform());
        } else {
            fieldNames.addAll(dUtils.getFieldNamesOfMaintable());
        }
        Map<String, Object> formDataOfMainform = null, formDataOfMainform2 = null;
        if (dUtils.isFormTypeAsCform() && dUtils.hasCustomTable()) {
            FormDefinition pFormDefinition = dUtils.doGetPformFormDefinition();
            formDataOfMainform2 = getFormDataOfMainform2(pFormDefinition, dataUuid,
                    pFormDefinition.doGetFormDefinitionHandler());
        }
        formDataOfMainform = getFormDataOfMainform(formDefinition.getFormType(), dUtils.doGetTblNameOfpForm(),
                dataUuid, fieldNames);
        if (formDataOfMainform2 != null && formDataOfMainform != null) {
            // 自定义单据覆盖存储单据
            formDataOfMainform2.putAll(formDataOfMainform);
            return formDataOfMainform2;
        }
        return formDataOfMainform;
    }

    @Override
    public Map<String, Object> getFormDataOfMainform(String formUuid, String dataUuid) {
        FormDefinition formDefinition = ((FormDefinition) dyFormFacade.getFormDefinition(formUuid));
        FormDefinitionHandler dUtils = formDefinition.doGetFormDefinitionHandler();
        Map<String, Object> formDataOfTemplate = null;
        Map<String, Object> formDataOfMainform = new HashMap<String, Object>();
        List<String> templateFields = dUtils.getFormUuidsOfTemplate();
        for (String templateFormUuid : templateFields) {
            if (StringUtils.isNotBlank(dataUuid)
                    && (formDataOfTemplate = getFormDataOfMainform(templateFormUuid, dataUuid, true)) != null) {
                //				for (EnumSystemField systemField : EnumSystemField.values()) {
                //					formDataOfTemplate.remove(systemField.getName());
                //				}
                formDataOfMainform.putAll(formDataOfTemplate);
            }
        }
        if ((formDataOfTemplate = getFormDataOfMainform(formDefinition, dataUuid, true)) != null) {
            formDataOfMainform.putAll(formDataOfTemplate);
        }
        return formDataOfMainform;
    }

    private List<LogicFileInfo> getFiles(List<LogicFileInfo> allfiles, String fieldName) {
        List<LogicFileInfo> lfiles = new ArrayList<LogicFileInfo>();
        if (allfiles == null) {
            return new ArrayList<LogicFileInfo>();
        }
        for (LogicFileInfo file : allfiles) {
            String purpose = file.getPurpose();
            if (purpose == null) {
                continue;
            }
            if (purpose.toLowerCase().equals(fieldName.toLowerCase())) {
                lfiles.add(file);
            }
        }
        return lfiles;
    }

    /**
     * 格式化数据格式
     *
     * @param formDataOfMainform
     * @param dUtils
     */
    @SuppressWarnings("static-method")
    private void formatValueOfFormData(Map<String, Object> formDataOfMainform, FormDefinitionHandler dUtils) {
        // DyFormDataUtils dataUtils = new DyFormDataUtils(dUtils);
        Iterator<String> it = dUtils.getFieldNamesOfMainform().iterator();
        while (it.hasNext()) {
            String fieldName = it.next();

            Object value = formDataOfMainform.get(fieldName);
            if (value == null) {
                continue;
            }
            try {
                if (dUtils.isInputModeEqDate(fieldName)) {
//                    SimpleDateFormat sdf = new SimpleDateFormat();
                    String datePattern = dUtils.getDateTimePatternByFieldName(fieldName);
//                    sdf.applyPattern(datePattern);
//                    value = sdf.format((Date) value);
                    if (StringUtils.isNotBlank(datePattern) && datePattern.indexOf("ww") != -1) {
                        // 周计算: 年份要转为基于周的年份
                        datePattern = datePattern.replace("yyyy", "xxxx");
                    }
                    value = DateTimeFormat.forPattern(datePattern).print(new DateTime((Date) value));
                } else if (/*dUtils.isDbDataTypeEqClob(fieldName) &&*/value instanceof Clob) {// 修复表单配置变更字段类型时大字段数据无法转JSON
                    value = ClobUtils.ClobToString((Clob) value);
                } //else if (dUtils.isInputModeEqOrg(fieldName)) {
                //                	String separater = dUtils.getFieldSeparater(fieldName);
                //                	HashMap<String, String> optionSet = orgApiFacade.getNameByOrgEleIds(Arrays.asList(value.toString().split(separater)));
                //                	String orgOptionSetName = "___" + fieldName;
                //                	if(false == formDataOfMainform.containsKey(orgOptionSetName)){
                //                		formDataOfMainform.put(orgOptionSetName, optionSet);
                //                	}
                //}
            } catch (Exception e) {
                String errorMsg = "fieldName Is [" + fieldName + "]\n" + e.getMessage();
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg);
            }
            formDataOfMainform.put(fieldName, value);
        }

    }

    @Override
    public Map<String, Object> getDefaultFormData(String formUuid) throws JSONException {

        FormDefinition formDefinition = ((FormDefinition) dyFormFacade.getFormDefinition(formUuid));
        if (formDefinition == null) {
            return null;
        }
        FormDefinitionHandler dUtils = formDefinition.doGetFormDefinitionHandler();

        Map<String, Object> formData = new HashMap<String, Object>();
        FormDataHandler dataUtils = new FormDataHandler(formUuid, formData, formDefinition.doGetFormDefinitionHandler());
        List<String> fieldNames = dUtils.getFieldNamesOfMainform();// 表单的各字段名称
        for (String fieldName : fieldNames) {
            if (dUtils.isValueCreateBySystemWhenShowNewForm(fieldName)) {
                dataUtils.doProcessValueCreateBySystem(fieldName);
            } /*
             * else if (dUtils.isInputModeEqNumber(fieldName)) {
             * formData.put(key, value); }
             */
        }

        formatValueOfFormData(formData, dUtils);

        return formData;
    }

    @Override
    public QueryData getFormDataOfParentNodeByPage(String formUuidOfSubform, String formUuidOfMainform,
                                                   String dataUuidOfMainform, PagingInfo pagingInfo) {

        FormDefinition formDefinitionOfMainform = (FormDefinition) dyFormFacade.getFormDefinition(formUuidOfMainform);

        FormDefinitionHandler dUtils = null;
        List<String> fieldNames = new ArrayList<String>();

        String relationTblNameOfSubform = null;

        dUtils = formDefinitionOfMainform.doGetFormDefinitionHandler();
        List<String> fieldNamesOfSubform = null;
        try {
            fieldNamesOfSubform = dUtils.getFieldNamesOfSubform(formUuidOfSubform);
            fieldNames.addAll(fieldNamesOfSubform);
            List<String> sysFieldNames = FormDefinitionHandler.getSysFieldNames();
            fieldNames.addAll(sysFieldNames);
            fieldNames.add(EnumRelationTblSystemField.sort_order.name());
        } catch (JSONException e2) {
            logger.error(e2.getMessage(), e2);
            return null;
        }

        FormDefinition subformdyd = (FormDefinition) dyFormFacade.getFormDefinition(formUuidOfSubform);
        relationTblNameOfSubform = subformdyd.doGetRelationTblNameOfpForm();

        // 记录总条数
        StringBuilder totalSqlBuffer = new StringBuilder();
        totalSqlBuffer.append("select count(uuid) c from ");
        totalSqlBuffer.append(relationTblNameOfSubform);
        totalSqlBuffer.append(" where parent_uuid is null and ");
        totalSqlBuffer.append(" MAINFORM_DATA_UUID = '");
        totalSqlBuffer.append(dataUuidOfMainform);
        totalSqlBuffer.append("'");
        long totalCount = 0;
        try {
            String totalCountstr = dbTableDao.query(totalSqlBuffer.toString()).get(0).get("c").toString();
            totalCount = Long.parseLong(totalCountstr);
        } catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
        }
        if (pagingInfo == null) {// 没有传入翻页信息
            int DEFAULTPAGESIZE = 1000;
            pagingInfo = new PagingInfo();
            if (totalCount > DEFAULTPAGESIZE) {
                pagingInfo.setPageSize(DEFAULTPAGESIZE);
            } else {
                pagingInfo.setCurrentPage(1);
                pagingInfo.setPageSize((int) totalCount);
            }
        }
        pagingInfo.setTotalCount(totalCount);
        pagingInfo.setAutoCount(true);

        // 分页查询
        StringBuilder sqlBuffer = new StringBuilder();
        // sqlBuffer.append(this.dyFormFacade.getSqlOfSubformView(formUuidOfSubform));
        // String VIEWNAME_OF_SUBFORM = DyFormConfig.VIEWNAME_OF_SUBFORM;
        sqlBuffer.append(" select 1 ");
        for (Object fieldNameObj : fieldNames) {
            String fieldName = (String) fieldNameObj;
            sqlBuffer.append(", ").append(fieldName);
        }
        sqlBuffer.append(" from ");
        sqlBuffer.append(this.dyFormFacade.getSqlOfSubformView(formUuidOfSubform));
        // sqlBuffer.append(VIEWNAME_OF_SUBFORM);
        sqlBuffer.append(" where rowid in(select rid from (select rownum rn,rid from ");
        sqlBuffer.append(" (select rowid rid    ");
        sqlBuffer.append("  from    ");

        sqlBuffer.append(this.dyFormFacade.getSqlOfSubformView(formUuidOfSubform));
        // sqlBuffer.append(VIEWNAME_OF_SUBFORM);

        sqlBuffer.append(" where parent_uuid is null and ");

        sqlBuffer.append("MAINFORM_DATA_UUID = '");
        sqlBuffer.append(dataUuidOfMainform);
        sqlBuffer.append("'");

        sqlBuffer.append(" order by sort_order asc ");
        sqlBuffer.append(") where rownum<=  ");
        sqlBuffer.append(pagingInfo.getFirst() + pagingInfo.getPageSize() + 1);
        sqlBuffer.append(") where rn >  ");

        sqlBuffer.append(pagingInfo.getFirst());
        sqlBuffer.append(") order by sort_order asc");

        try {
            List<Map<String, Object>> list = dbTableDao.query(sqlBuffer.toString());
//            for (Map<String, Object> record : list) {
//                for (String fieldName : fieldNames) {
//                    Object value = record.get(fieldName.toLowerCase());
//                    record.remove(fieldName.toLowerCase());
//                    record.put(fieldName, value);
//                }
//            }

            // 格式化
            if (list == null || list.size() == 0) {
                return null;
            }

            for (Map<String, Object> record : list) {
                formatValueOfFormData(record, subformdyd.doGetFormDefinitionHandler());

                List<LogicFileInfo> allfiles = this.mongoFileService.getNonioFilesFromFolder(
                        (String) record.get(EnumSystemField.uuid.name()), null);
                if (allfiles != null && allfiles.size() > 0) {
                    for (String fieldName : subformdyd.doGetFormDefinitionHandler().getFieldNamesOfMainform()) {
                        if (subformdyd.doGetFormDefinitionHandler().isInputModeEqAttach(fieldName)) {// 附件

                            List<LogicFileInfo> files = this.getFiles(allfiles, fieldName);
                            record.put(fieldName, files);
                        }
                    }
                }

                /*
                 * for (String fieldName : fieldNamesOfSubform) { if
                 * (subformdyd.
                 * doGetFormDefinitionHandler().isInputModeEqAttach(fieldName))
                 * {//附件 List<LogicFileInfo> files =
                 * this.mongoFileService.getNonioFilesFromFolder( (String)
                 * record.get(EnumSystemField.uuid.name()), fieldName);
                 * record.put(fieldName, files); } }
                 */

            }
            QueryData queryData = new QueryData();
            queryData.setDataList(list);
            queryData.setPagingInfo(pagingInfo);

            return queryData;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    @Override
    public QueryData getFormDataOfParentNode(String formUuidOfSubform, String formUuidOfMainform,
                                             String dataUuidOfMainform, boolean queryAllSubformFieldValue) {
        FormDefinition formDefinitionOfMainform = (FormDefinition) dyFormFacade.getFormDefinition(formUuidOfMainform);
        try {
            // 获取从表数据查询SQL
            String sql = getQuerySubformDataSql(formDefinitionOfMainform, formUuidOfSubform, queryAllSubformFieldValue);
            Map<String, Object> values = Maps.newHashMap();
            values.put("dataUuidOfMainform", dataUuidOfMainform);
            List<Map<String, Object>> list = dbTableDao.queryByParams(sql, values);

            if (CollectionUtils.isNotEmpty(list)) {
                FormDefinition subformdyd = (FormDefinition) dyFormFacade.getFormDefinition(formUuidOfSubform);
                FormDefinitionHandler subformdUtils = subformdyd.doGetFormDefinitionHandler();
                List<String> upperCaseFields = Lists.newArrayList();
                for (String fieldName : subformdUtils.getFieldNames()) {
                    if (!StringUtils.equals(fieldName, fieldName.toLowerCase())) {
                        upperCaseFields.add(fieldName);
                    }
                }
                for (String fieldName : upperCaseFields) {
                    for (Map<String, Object> record : list) {
                        Object value = record.get(fieldName.toLowerCase());
                        record.remove(fieldName.toLowerCase());
                        record.put(fieldName, value);
                    }
                }
                // 格式化
                List<String> fieldNamesOfFiles = subformdUtils.getFieldNamesOfFile();
                Map<String, Map<String, Object>> subformDataUuidMap = null;
                if (CollectionUtils.isNotEmpty(fieldNamesOfFiles)) {
                    subformDataUuidMap = Maps.newHashMap();
                }
                for (Map<String, Object> record : list) {
                    formatValueOfFormData(record, subformdUtils);
                    if (subformDataUuidMap != null) {
                        subformDataUuidMap.put((String) record.get(EnumSystemField.uuid.name()), record);
                    }
                }
                // 附件处理
                if (CollectionUtils.isNotEmpty(fieldNamesOfFiles)) {
                    Map<String, List<LogicFileInfo>> allfileMap = mongoFileService.getNonioFilesFromFolders(subformDataUuidMap.keySet());
                    for (Entry<String, Map<String, Object>> entry : subformDataUuidMap.entrySet()) {
                        List<LogicFileInfo> allfiles = allfileMap.get(entry.getKey());
                        if (CollectionUtils.isNotEmpty(allfiles)) {
                            Map<String, Object> record = entry.getValue();
                            for (String fieldName : fieldNamesOfFiles) {
                                // 附件
                                List<LogicFileInfo> files = this.getFiles(allfiles, fieldName);
                                record.put(fieldName, files);
                            }
                        }
                    }
                }
            }
            QueryData queryData = new QueryData();
            queryData.setDataList(list);
            return queryData;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    public void printLineInfo(String remark) {
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {
                logger.info("stackInfo[" + remark + "]:" + stackElements[i].getClassName() + ":"
                        + stackElements[i].getFileName() + ":" + stackElements[i].getLineNumber() + ":"
                        + stackElements[i].getMethodName());

            }
        }

    }

    @Override
    public List<Map<String, Object>> getFormDataOfChildNode4ParentNode(String formUuidOfSubform,
                                                                       String formUuidOfMainform, String dataUuidOfMainform, String dataUuidOfParentNode) {
        try {
            FormDefinitionHandler dUtils = ((FormDefinition) dyFormFacade.getFormDefinition(formUuidOfMainform))
                    .doGetFormDefinitionHandler();
            List<String> fieldNames = new ArrayList<String>();
            List<String> fieldNamesOfSubform = dUtils.getFieldNamesOfSubform(formUuidOfSubform);
            fieldNames.addAll(fieldNamesOfSubform);
            List<String> sysFieldNames = FormDefinitionHandler.getSysFieldNames();
            fieldNames.addAll(sysFieldNames);
            /*
             * for (String fieldName : fieldNamesOfSubform) {
             * sysFieldNames.add(fieldName); }
             */

            fieldNames.add(EnumRelationTblSystemField.mainform_data_uuid.name());
            fieldNames.add(EnumRelationTblSystemField.parent_uuid.name());

            /*
             * Iterator<String> it = fieldNames.iterator(); while (it.hasNext())
             * {//删除掉uuid字段，在从表里面有这个字段，在关系表中也有这个字段 String fieldName = it.next();
             * if (fieldName.equalsIgnoreCase("uuid")) { it.remove(); } }
             */

            List<Map<String, Object>> list = getFormDataOfChildNode4ParentNode(formUuidOfSubform, fieldNames,
                    dataUuidOfMainform, dataUuidOfParentNode);
            if (list == null || list.size() == 0) {
                return null;
            }
            FormDefinitionHandler dUtils2 = ((FormDefinition) dyFormFacade.getFormDefinition(formUuidOfSubform))
                    .doGetFormDefinitionHandler();
            for (Map<String, Object> record : list) {
                formatValueOfFormData(record, dUtils2);
                for (String fieldName : fieldNamesOfSubform) {
                    if (dUtils2.isInputModeEqAttach(fieldName)) {// 附件
                        List<LogicFileInfo> files = this.mongoFileService.getNonioFilesFromFolder(
                                (String) record.get(EnumSystemField.uuid.name()), fieldName);
                        record.put(fieldName, files);
                    }
                }
            }
            return list;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private List<Map<String, Object>> getFormDataOfChildNode4ParentNode(String formUuidOfSubform,
                                                                        List<String> fieldNamesOfSubform, String dataUuidOfMainform, String dataUuidOfParentNode) {
        StringBuilder sqlBuffer = new StringBuilder();
        // sqlBuffer.append(this.dyFormFacade.getSqlOfSubformView(formUuidOfSubform));
        sqlBuffer.append(" select 1 ");
        for (String fieldNameObj : fieldNamesOfSubform) {
            String fieldName = fieldNameObj;
            sqlBuffer.append(", ").append(fieldName);
        }
        sqlBuffer.append(" from   ");
        sqlBuffer.append(this.dyFormFacade.getSqlOfSubformView(formUuidOfSubform));
        // sqlBuffer.append(DyFormConfig.VIEWNAME_OF_SUBFORM);
        sqlBuffer.append(" where parent_uuid = '");
        sqlBuffer.append(dataUuidOfParentNode);
        sqlBuffer.append("'");
        sqlBuffer.append(" and MAINFORM_DATA_UUID = '");
        sqlBuffer.append(dataUuidOfMainform);
        sqlBuffer.append("' order by sort_order asc ");

        try {
            List<Map<String, Object>> list = dbTableDao.query(sqlBuffer.toString());
//            for (Map<String, Object> record : list) {
//                for (String fieldName : fieldNamesOfSubform) {
//                    Object value = record.get(fieldName.toLowerCase());
//                    record.remove(fieldName.toLowerCase());
//                    record.put(fieldName, value);
//                }
//            }

            return list;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * (non-Javadoc)
     *
     * @see FormDataService#getSubformDatas(List, String, String, boolean)
     */
    @Override
    public Map<String, List<Map<String, Object>>> getSubformDatas(List<String> subformUuids, String formUuidOfMainform,
                                                                  String dataUuidOfMainform, boolean fetchAllSubformFields) {
        Map<String, List<Map<String, Object>>> subformDatas = Maps.newHashMap();
        for (String formUuidOfSubform : subformUuids) {
            QueryData queryData = getFormDataOfParentNode(formUuidOfSubform, formUuidOfMainform, dataUuidOfMainform, fetchAllSubformFields);
            List<Map<String, Object>> list = (List<Map<String, Object>>) queryData.getDataList();
            if (CollectionUtils.isNotEmpty(list)) {
                subformDatas.put(formUuidOfSubform, list);
            }
        }
        return subformDatas;
    }

    /**
     * @return
     */
    private String getQuerySubformDataSql(FormDefinition formDefinitionOfMainform, String formUuidOfSubform, boolean allSubformFields) {
        String sql = DyformCacheUtils.getQuerySubformDataSqlByUuid(formDefinitionOfMainform.getUuid(), formUuidOfSubform + (allSubformFields ? ":full" : ""));
        if (StringUtils.isNotBlank(sql)) {
            return sql;
        }
        FormDefinitionHandler dUtils = formDefinitionOfMainform.doGetFormDefinitionHandler();
        List<String> fieldNames = new ArrayList<String>();
        List<String> fieldNamesOfSubform = null;
        try {
            fieldNamesOfSubform = dUtils.getFieldNamesOfSubform(formUuidOfSubform);
            if (allSubformFields) {
                List<DyformFieldDefinition> fieldDefinitions = dyFormFacade.getFieldDefinitions(formUuidOfSubform);
                if (CollectionUtils.isNotEmpty(fieldDefinitions)) {
                    for (DyformFieldDefinition d : fieldDefinitions) {
                        fieldNames.add(d.getFieldName());
                    }
                }
            }
            fieldNames.addAll(fieldNamesOfSubform);
            List<String> sysFieldNames = FormDefinitionHandler.getSysFieldNames();
            fieldNames.addAll(sysFieldNames);
            fieldNames.add(EnumRelationTblSystemField.sort_order.name());
            fieldNames = Lists.newArrayList(Sets.newHashSet(fieldNames));
        } catch (JSONException e2) {
            logger.error(e2.getMessage(), e2);
            return null;
        }

        // 分页查询
        StringBuilder sqlBuffer = new StringBuilder();
        // sqlBuffer.append(this.dyFormFacade.getSqlOfSubformView(formUuidOfSubform));
        // String VIEWNAME_OF_SUBFORM = DyFormConfig.VIEWNAME_OF_SUBFORM;
        sqlBuffer.append(" select ").append(fieldNames.get(0));
        List<String> orders = new ArrayList<String>();
        int fieldSize = fieldNames.size();
        for (int i = 1; i < fieldSize; i++) {
            String fieldName = (String) fieldNames.get(i);
            sqlBuffer.append(", ").append(fieldName);
            if (dUtils.isSubformFieldName(formUuidOfSubform, fieldName)) {
                String sort = dUtils.getSubformFieldPropertyOfStringType(formUuidOfSubform, fieldName,
                        DyFormConfig.EnumSubformFieldPropertyName.sort);
                if (StringUtils.isNotBlank(sort)) {
                    // isOrderByField = true;
                    orders.add(fieldName + " " + sort);
                }
            }
        }
        sqlBuffer.append(" from ");
        sqlBuffer.append(this.dyFormFacade.getSqlOfSubformView(formUuidOfSubform));
        // sqlBuffer.append(VIEWNAME_OF_SUBFORM);

        sqlBuffer.append(" where parent_uuid is null and ");

        sqlBuffer.append("MAINFORM_DATA_UUID = :dataUuidOfMainform");

        // sqlBuffer.append("  ");
        // if (orders.size() > 0) {
        orders.add(" sort_order  asc");// 默认排序

        sqlBuffer.append(" order by   ");
        sqlBuffer.append(StringUtils.join(orders, ","));
        // } else {
        // sqlBuffer.append(" order by to_number(sort_order)  asc ");
        // }
        sql = sqlBuffer.toString();
        DyformCacheUtils.setQuerySubformDataSqlByUuid(formDefinitionOfMainform.getUuid(), formUuidOfSubform + (allSubformFields ? ":full" : ""), sql);
        return sql;
    }

    @Override
    public QueryData queryFormDataOfMainform(String formUuid, Criterion conditions, PagingInfo pagingInfo) {
        QueryData queryData = new QueryData();
        try {
            FormDefinitionHandler dUtils = ((FormDefinition) dyFormFacade.getFormDefinition(formUuid))
                    .doGetFormDefinitionHandler();
            String tblName = dUtils.doGetTblNameOfpForm();
            long totalCount = this.queryTotalCountOfFormDataOfMainform(tblName, conditions);
            if (pagingInfo == null) {// 没有传入翻页信息
                int DEFAULTPAGESIZE = 1000;
                pagingInfo = new PagingInfo();
                if (totalCount > DEFAULTPAGESIZE) {
                    pagingInfo.setPageSize(DEFAULTPAGESIZE);
                } else {
                    pagingInfo.setCurrentPage(1);
                    pagingInfo.setPageSize((int) totalCount);
                }
            }
            pagingInfo.setTotalCount(totalCount);
            pagingInfo.setAutoCount(true);
            queryData.setPagingInfo(pagingInfo);
            List<Map<String, Object>> datas = this.queryFormDataOfMainformByLimit(tblName, conditions,
                    pagingInfo.getFirst(), pagingInfo.getPageSize());
            for (Map<String, Object> record : datas) {
                for (String fieldName : dUtils.getFieldNamesOfMainform()) {
                    Object value = record.get(fieldName.toLowerCase());
                    record.remove(fieldName.toLowerCase());
                    record.put(fieldName, value);
                }
            }
            queryData.setDataList(datas);
            return queryData;
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private List<Map<String, Object>> queryFormDataOfMainformByLimit(String tblName, Criterion conditions, int first,
                                                                     int pageSize) throws Exception {
        StringBuilder sqlBuffer = new StringBuilder();
        sqlBuffer.append("select * ");
        sqlBuffer.append(" from ");
        sqlBuffer.append(tblName);
        sqlBuffer.append(" where rowid in(select rid from (select rownum rn,rid from ");
        sqlBuffer.append(" (select rowid rid ");
        sqlBuffer.append("  from    ");
        sqlBuffer.append(tblName);

        sqlBuffer.append(" where 1=1  and (");

        sqlBuffer.append(conditions.toString());
        sqlBuffer.append("  )    ");
        sqlBuffer.append(" order by  modify_time asc ");
        sqlBuffer.append(") where rownum <=  ");
        sqlBuffer.append(first + pageSize);
        sqlBuffer.append(") where rn >  ");

        sqlBuffer.append(first);
        sqlBuffer.append(") order by modify_time asc ");

        return this.dbTableDao.query(sqlBuffer.toString());
    }

    /**
     * 根据搜索条件,查询符合条件的表单数据条数
     *
     * @param tblName
     * @param conditions
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public long queryTotalCountOfFormDataOfMainform(String tblName, Criterion conditions) {
        StringBuilder totalSqlBuffer = new StringBuilder();
        totalSqlBuffer.append("select count(uuid) c from ");
        totalSqlBuffer.append(tblName);
        totalSqlBuffer.append(" where 1=1 ");
        if (conditions != null) {
            totalSqlBuffer.append(" and (");
            totalSqlBuffer.append(conditions.toString());
            totalSqlBuffer.append(" )");
        }
        long totalCount = 0;
        try {
            String totalCountstr = dbTableDao.query(totalSqlBuffer.toString()).get(0).get("c").toString();
            totalCount = Long.parseLong(totalCountstr);
        } catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
        }
        return totalCount;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dytable.service.FormDataService#getDigestValue(java.lang.String)
     */
    @Override
    public DyformDataSignature getDigestValue(String signedContent) {
        DyformDataSignature signature = new DyformDataSignature();
        String digestValue = null;
        String digestAlgorithm = "MD5";
        try {
            MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
            Provider provider = md.getProvider();
            System.out.println(provider.getInfo());
            md.update(signedContent.getBytes("UTF8"));
            byte[] digestData = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digestData) {
                String s = Integer.toHexString((0x000000ff & b) | 0xffffff00).substring(6);
                sb.append(s);
            }
            digestValue = sb.toString();
            signature.setDigestAlgorithm(digestAlgorithm);
            signature.setDigestValue(digestValue);
            signature.setSignedData(signedContent);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("表单数据无法生成消息摘要!");
        }
        return signature;
    }

    @Override
    public List<Map<String, Object>> getFormDataOfSubform(String formUuidOfSubform, String dataUuidOfMainform,
                                                          String whereSql, Map<String, Object> values) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Session session = this.dbTableDao.getSession();
        StringBuilder sqlBuffer = new StringBuilder();
        sqlBuffer
                .append(" ")
                // .append(this.dyFormFacade.getSqlOfSubformView(formUuidOfSubform))
                .append("select *\n from " + this.dyFormFacade.getSqlOfSubformView(formUuidOfSubform) + "  where "
                        + EnumRelationTblSystemField.mainform_data_uuid.name() + " = '").append(dataUuidOfMainform)
                .append("'");
        if (whereSql != null && whereSql.trim().length() > 0) {
            sqlBuffer.append(" and ").append(whereSql);
        }
        SQLQuery sqlquery = session.createSQLQuery(sqlBuffer.toString());
        if (whereSql != null && whereSql.trim().length() > 0) {
            sqlquery.setProperties(values);
        }

        list = sqlquery.setResultTransformer(FormDataResultTransformer.INSTANCE).list();

        return list;
    }

    @Override
    public List<QueryItem> query(String formUuid, String[] projection, String selection, String[] selectionArgs,
                                 String groupBy, String having, String orderBy, int firstResult, int maxResults) {
        DyFormFormDefinition FormDefinition = (DyFormFormDefinition) dyFormFacade.getFormDefinition(formUuid);
        String table = FormDefinition.doGetTblNameOfpForm();
        String queryString = buildQueryString(false, table, projection, selection, groupBy, having, orderBy);
        SQLQuery sqlQuery = this.dbTableDao.getSession().createSQLQuery(queryString);
        List<QueryItem> queryItems = sqlQuery.setFirstResult(firstResult).setMaxResults(maxResults)
                .setResultTransformer(QueryItemResultTransformer.INSTANCE).list();
        return queryItems;
    }

    public List<QueryItem> query2(String formUuid, String[] projection, String selection, String[] selectionArgs,
                                  String groupBy, String having, String orderBy, int firstResult, int maxResults) {
        DyFormFormDefinition FormDefinition = (DyFormFormDefinition) dyFormFacade.getFormDefinition(formUuid);
        String table = FormDefinition.doGetTblNameOfpForm();
        String queryString = buildQueryString(false, table, projection, selection, groupBy, having, orderBy);
        SQLQuery sqlQuery = this.dbTableDao.getSession().createSQLQuery(queryString);
        List<QueryItem> queryItems = sqlQuery.setFirstResult(firstResult).setMaxResults(maxResults)
                .setResultTransformer(QueryItemResultTransformer.INSTANCE).list();
        return queryItems;
    }

    @Override
    public List<QueryItem> query(String tableName, boolean distinct, String[] projection, String selection,
                                 Map<String, Object> selectionArgs, String groupBy, String having, String orderBy, int firstResult,
                                 int maxResults) {
        String queryString = buildQueryString(distinct, tableName, projection, selection, groupBy, having, orderBy);
        SQLQuery sqlQuery = this.dbTableDao.getSession().createSQLQuery(queryString);
        sqlQuery.setProperties(selectionArgs);
        List<QueryItem> queryItems = sqlQuery.setFirstResult(firstResult).setMaxResults(maxResults)
                .setResultTransformer(QueryItemResultTransformer.INSTANCE).list();
        return queryItems;
    }

    /**
     * Description how to use this method
     *
     * @param b
     * @param table
     * @param columns
     * @param where
     * @param groupBy
     * @param having
     * @param orderBy
     */
    private String buildQueryString(boolean distinct, String tables, String[] columns, String where, String groupBy,
                                    String having, String orderBy) {
        if (StringUtils.isBlank(groupBy) && !StringUtils.isBlank(having)) {
            throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
        }

        StringBuilder query = new StringBuilder(120);
        query.append("SELECT ");
        if (distinct) {
            query.append("DISTINCT ");
        }
        if (columns != null && columns.length != 0) {
            appendColumns(query, columns);
        } else {
            query.append("* ");
        }
        query.append("FROM ");
        query.append(tables);
        appendClause(query, " WHERE ", where);
        appendClause(query, " GROUP BY ", groupBy);
        appendClause(query, " HAVING ", having);
        appendClause(query, " ORDER BY ", orderBy);

        return query.toString();
    }

    @Override
    public List<Map<String, Object>> getFormDataOfMainform(String formUuid) {
        FormDefinition dy = (FormDefinition) dyFormFacade.getFormDefinition(formUuid);
        List<String> fieldNames = dy.doGetFormDefinitionHandler().getFieldNamesOfMaintable();
        StringBuilder sqlBuffer = new StringBuilder();
        sqlBuffer.append("select uuid ");
        for (String fieldName : fieldNames) {
            // if (dy.doGetFormDefinitionHandler().isValueAsMap(fieldName)) {
            sqlBuffer.append(", ").append(fieldName);
            // }

        }
        sqlBuffer.append(" from ");
        sqlBuffer.append(dy.doGetFormDefinitionHandler().doGetTblNameOfpForm());

        try {
            List<Map<String, Object>> list = dbTableDao.query(sqlBuffer.toString());
            if (list == null || list.size() == 0) {
                return null;
            }
//            for (Map<String, Object> record : list) {
//                for (String fieldName : fieldNames) {
//                    Object value = record.get(fieldName.toLowerCase());
//                    record.remove(fieldName.toLowerCase());
//                    record.put(fieldName, value);
//                }
//            }
            return list;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    @Override
    public <T> T getEntity(Class<T> clazz, String value) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", value);
        String hql = "select o from " + clazz.getName() + " o where o.id = :uuid";
        /*
         * if (clazz == FmFile.class) { hql = "select o from " + clazz.getName()
         * + " o where o.uuid = :uuid"; }
         */

        if (clazz == CommonUnit.class) {
            return null;
        } else {
            return (T) formDataDao.findUnique(hql, params);
        }
    }

    @Override
    public void update(Object obj) {
        this.formDataDao.save(obj);

    }

    @Override
    public void executeSql(String sql) {
        try {
            Session session = this.formDataDao.getSessionFactory().getCurrentSession();

            SQLQuery sqlquery = session.createSQLQuery(sql.toString());
            sqlquery.executeUpdate();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public Map<Integer, List<Map<String, String>>> validateFormdates(String formUuid, List<Map<String, Object>> dataList) {
        Map<Integer, List<Map<String, String>>> map = new HashMap<Integer, List<Map<String, String>>>();
        return map;
    }

    @Override
    public String formDefinationToXml(String formUuid) {
        DyFormFormDefinition FormDefinition = (DyFormFormDefinition) dyFormFacade.getFormDefinition(formUuid);
        List<DyformFieldDefinition> fieldDefintions = FormDefinition.doGetFieldDefintions();
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("item");
        // 生成主表结构
        for (DyformFieldDefinition dyformFieldDefinition : fieldDefintions) {
            root.addComment(dyformFieldDefinition.getDisplayName());
            Element fieldElement = root.addElement(dyformFieldDefinition.getName());
            if (DyFormConfig.InputModeUtils.isInputModeEqAttach(dyformFieldDefinition.getInputMode())) {// 附件
                fieldElement.addAttribute("isAttachment", "1");
            }
        }
        // 生成从表结构
        List<DyformSubformFormDefinition> subformDefinitions = FormDefinition.doGetSubformDefinitions();
        for (DyformSubformFormDefinition dyformSubformFormDefinition : subformDefinitions) {
            root.addComment(dyformSubformFormDefinition.getDisplayName());
            Element listElement = root.addElement(dyformSubformFormDefinition.getName());
            listElement.addAttribute("isList", "1");
            Element itemElement = listElement.addElement("item");
            List<DyformSubformFieldDefinition> subformFieldDefinitions = dyformSubformFormDefinition
                    .getSubformFieldDefinitions();
            for (DyformSubformFieldDefinition subformFieldDefinition : subformFieldDefinitions) {
                itemElement.addComment(subformFieldDefinition.getDisplayName());
                itemElement.addElement(subformFieldDefinition.getName());
                if (DyFormConfig.InputModeUtils.isInputModeEqAttach(subformFieldDefinition.getInputMode())) {// 附件
                    itemElement.addAttribute("isAttachment", "1");
                }
            }
        }
        OutputFormat formate = OutputFormat.createPrettyPrint();
        StringWriter out = new StringWriter();
        XMLWriter writer = new XMLWriter(out, formate);
        try {
            writer.write(document);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return out.toString();
    }

    @Override
    public long countByFormUuid(String formUuid) {
        DyFormFormDefinition dy = (DyFormFormDefinition) dyFormFacade.getFormDefinition(formUuid);
        SQLQuery sqlquery = this.formDataDao.getSession().createSQLQuery(
                "select count(uuid) c from " + dy.doGetTblNameOfpForm() + " where form_uuid ='" + formUuid + "' ");

        return ((Number) sqlquery.list().get(0)).longValue();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public long countDataInForm(String tblName) {
        Session session = this.formDataDao.getSession();
        SQLQuery sqlquery = session.createSQLQuery("select count(uuid) c from " + tblName + " ");
        long cnt = ((Number) sqlquery.list().get(0)).longValue();
        session.flush();
        return cnt;

    }

    /**
     * 根据搜索条件,查询符合条件的表单数据条数
     *
     * @param tblName
     * @param conditions
     * @return
     */
    @Override
    public long queryTotalCountOfFormDataOfMainform(String tblName, String conditions) {
        StringBuilder totalSqlBuffer = new StringBuilder();
        totalSqlBuffer.append("select count(uuid) c from ");
        totalSqlBuffer.append(tblName);
        totalSqlBuffer.append(" o where 1=1 ");
        if (conditions != null) {
            totalSqlBuffer.append(" and (");
            totalSqlBuffer.append(conditions);
            totalSqlBuffer.append(" )");
        }
        long totalCount = 0;
        try {
            String totalCountstr = dbTableDao.query(totalSqlBuffer.toString()).get(0).get("c").toString();
            totalCount = Long.parseLong(totalCountstr);
        } catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
        }
        return totalCount;
    }

    @Override
    public String saveFormData(String mainformUuid, Map<String, List<Map<String, Object>>> formDatas,
                               Map<String, List<String>> deletedFormDatas, Map<String, Map<String, Set<String>>> updatedFormDatas,
                               Map<String, List<String>> addedFormDatas, DyformDataSignature signature, DyformDataOptions dyformDataOptions) {
        // long time1 = System.currentTimeMillis();
        Assert.notNull(formDatas, "表单数据为null");
        Assert.notEmpty(formDatas, "表单数据为空");
        // Transaction transaction =
        // this.formDataDao.getSession().getTransaction();
        try {
            FormDefinition mainformDefinition = (FormDefinition) dyFormFacade.getFormDefinition(mainformUuid);// 主表定义

            Iterator<String> formUuidIt = formDatas.keySet().iterator();

            // 获取主表的数据id
            Map<String, Object> formDataOfMainform = formDatas.get(mainformUuid).get(0);
            String dataUuidOfMainform = (String) formDataOfMainform.get(EnumSystemField.uuid.name());
            boolean isNew = false;
            if (dataUuidOfMainform == null || ((String) dataUuidOfMainform).trim().length() == 0) {
                // 该单据为新增单据
                dataUuidOfMainform = dyFormFacade.createUuid();
                isNew = true;
                formDataOfMainform.put(EnumSystemField.uuid.name(), dataUuidOfMainform);
            }

            // 保存模板数据开始
            List<String> mstFormUuids = null;
            FormDefinitionHandler formDefinitionHandler = mainformDefinition.doGetFormDefinitionHandler();
            if (DyformTypeEnum.isCform(mainformDefinition.getFormType())) {
                String pFormUuid = mainformDefinition.getpFormUuid();
                // 定义主体
                FormDefinition pPormDefinition = formDefinitionHandler.doGetPformFormDefinition();
                FormDefinitionHandler pFormDefinitionHandler = pPormDefinition.doGetFormDefinitionHandler();
                List<String> fieldNamesOfMainform = pFormDefinitionHandler.getFieldNamesOfMainform();
                List<String> formUuidsOfSubform = pFormDefinitionHandler.getFormUuidsOfSubform();
                // 单据数据结构
                Map<String, List<Map<String, Object>>> formDataOfCform = new HashMap<String, List<Map<String, Object>>>();
                Map<String, List<String>> deletedFormDatasOfC = new HashMap<String, List<String>>();
                Map<String, List<String>> addedFormDatasOfC = new HashMap<String, List<String>>();
                Map<String, Map<String, Set<String>>> updatedFormDatasOfC = new HashMap<String, Map<String, Set<String>>>();
                for (String formUuid : formUuidsOfSubform) {
                    if (formDatas.containsKey(formUuid)) {
                        formDataOfCform.put(formUuid, formDatas.remove(formUuid));
                    }
                    if (null != addedFormDatas && addedFormDatas.containsKey(formUuid)) {
                        addedFormDatasOfC.put(formUuid, addedFormDatas.remove(formUuid));
                    }
                    if (null != deletedFormDatas && deletedFormDatas.containsKey(formUuid)) {
                        deletedFormDatasOfC.put(formUuid, deletedFormDatas.remove(formUuid));
                    }
                    if (null != updatedFormDatas && updatedFormDatas.containsKey(formUuid)) {
                        updatedFormDatasOfC.put(formUuid, updatedFormDatas.remove(formUuid));
                    }
                }
                // 主表数据结构-数据
                Map<String, Object> formDataOfC = new HashMap<String, Object>();
                List<Map<String, Object>> formDataOfTemplates = new ArrayList<Map<String, Object>>();
                formDataOfTemplates.add(formDataOfC);
                formDataOfCform.put(pFormUuid, formDataOfTemplates);
                // 主表数据结构-更新字段
                Set<String> mainUpdatedFieldNames = Sets.newHashSet(), mainUpdatedFieldNames2 = null;
                if (updatedFormDatas != null && updatedFormDatas.get(mainformUuid) != null) {
                    mainUpdatedFieldNames2 = updatedFormDatas.get(mainformUuid).get(dataUuidOfMainform);
                }
                // 一对一关联，uuid==mainform_data_uuid
                formDataOfC.put(EnumSystemField.form_uuid.name(), pFormUuid);
                formDataOfC.put(EnumSystemField.uuid.name(), dataUuidOfMainform);
                if (isNew || isInAddedOperateLog(mainformUuid, dataUuidOfMainform, addedFormDatas)) {
                    addedFormDatasOfC.put(pFormUuid, Lists.newArrayList(dataUuidOfMainform));
                } else {
                    Map<String, Set<String>> updatedFormDataOfC = Maps.newHashMap();
                    updatedFormDataOfC.put(dataUuidOfMainform, mainUpdatedFieldNames);
                    updatedFormDatasOfC.put(pFormUuid, updatedFormDataOfC);
                }
                for (String fieldName : fieldNamesOfMainform) {
                    if (formDataOfMainform.containsKey(fieldName)) {
                        formDataOfC.put(fieldName, formDataOfMainform.remove(fieldName));
                    }
                    if (mainUpdatedFieldNames2 != null && mainUpdatedFieldNames2.remove(fieldName)) {
                        mainUpdatedFieldNames.add(fieldName);
                    }
                }
                String dataUuid = saveFormData(pFormUuid, formDataOfCform, deletedFormDatasOfC, updatedFormDatasOfC,
                        addedFormDatasOfC, signature, dyformDataOptions);
                formDataOfMainform.put("mainform_form_uuid", pFormUuid);
                formDataOfMainform.put("mainform_data_uuid", dataUuid);// 主表数据
            } else if (false == DyformTypeEnum.isMSTform(mainformDefinition.getFormType())
                    && CollectionUtils.isNotEmpty(mstFormUuids = formDefinitionHandler.getFormUuidsOfTemplate())) {
                for (String mstFormUuid : mstFormUuids) {
                    DyFormFormDefinition formDefinition = dyFormFacade.getFormDefinition(mstFormUuid);
                    if (null == formDefinition || StringUtils.isBlank(formDefinition.getUuid())) {
                        logger.warn("mstFormUuid[" + mstFormUuid + "] FormDefinition is not found");
                        continue;
                    }
                    String dataUuid = dataUuidOfMainform;
                    Map<String, Object> formDataOfTemplate = new HashMap<String, Object>();
                    formDataOfTemplate.putAll(formDataOfMainform);
                    formDataOfTemplate.put("uuid", dataUuid);
                    formDataOfTemplate.put("mainform_form_uuid", mainformUuid);
                    formDataOfTemplate.put("mainform_data_uuid", dataUuidOfMainform);
                    List<Map<String, Object>> formDataOfTemplates = new ArrayList<Map<String, Object>>();
                    formDataOfTemplates.add(formDataOfTemplate);
                    Map<String, List<Map<String, Object>>> formDataOfMstform = new HashMap<String, List<Map<String, Object>>>();
                    formDataOfMstform.put(formDefinition.getUuid(), formDataOfTemplates);
                    Map<String, List<String>> addedFormDatasOfMst = new HashMap<String, List<String>>();
                    if (null != addedFormDatas && false == addedFormDatas.isEmpty()) {
                        addedFormDatasOfMst.putAll(addedFormDatas);
                        if ((addedFormDatas.get(mainformUuid)) != null) {
                            List<String> addedFormData = new ArrayList<String>();
                            addedFormData.addAll(addedFormDatas.get(mainformUuid));
                            addedFormDatasOfMst.put(formDefinition.getUuid(), addedFormData);
                        }
                    }
                    Map<String, List<String>> deletedFormDatasOfMst = new HashMap<String, List<String>>();
                    if (null != deletedFormDatas && false == deletedFormDatas.isEmpty()) {
                        deletedFormDatasOfMst.putAll(deletedFormDatas);
                        if ((deletedFormDatas.get(mainformUuid)) != null) {
                            List<String> deletedFormData = new ArrayList<String>();
                            deletedFormData.addAll(deletedFormDatas.get(mainformUuid));
                            deletedFormDatasOfMst.put(formDefinition.getUuid(), deletedFormData);
                        }
                    }
                    Map<String, Map<String, Set<String>>> updatedFormDatasOfMst = new HashMap<String, Map<String, Set<String>>>();
                    if (null != updatedFormDatas && false == updatedFormDatas.isEmpty()) {
                        updatedFormDatasOfMst.putAll(updatedFormDatas);
                        if ((updatedFormDatas.get(mainformUuid)) != null) {
                            Map<String, Set<String>> updatedFormData = new HashMap<String, Set<String>>();
                            updatedFormData.putAll(updatedFormDatas.get(mainformUuid));
                            updatedFormDatasOfMst.put(formDefinition.getUuid(), updatedFormData);
                        }
                    }
                    dataUuid = saveFormData(formDefinition.getUuid(), formDataOfMstform, deletedFormDatasOfMst,
                            updatedFormDatasOfMst, addedFormDatasOfMst, signature, dyformDataOptions);
                }
            }

            // 保存模板数据结束

            // 保存数据
            while (formUuidIt.hasNext()) {
                String formUuid = formUuidIt.next();
                FormDefinition dy = (FormDefinition) dyFormFacade.getFormDefinition(formUuid);
                List<Map<String, Object>> formDatasInOneForm = formDatas.get(formUuid);
                for (Map<String, Object> formData : formDatasInOneForm) {
                    if (formData == null) {
                        continue;
                    }
                    String nestformDatasObj = (String) formData.remove("nestformDatas");
                    formData = FormDataDao.setKeyAsLowerCase(formData);
                    String dataUuid = (String) (FormDataHandler.getValueFromMap(EnumSystemField.uuid.name(), formData));
                    if (StringUtils.isNotBlank(nestformDatasObj)) {
                        // 处理嵌套从表
                        DyFormData dyFormData = dyFormFacade.parseDyformData(nestformDatasObj);
                        dyFormData.getFormDatas().remove(formUuid);// 删除主表数据
                        if (false == dyFormData.getFormDatas().isEmpty()) {
                            Map<String, Object> nestMainFormData = new HashMap<String, Object>();
                            nestMainFormData.put(EnumSystemField.uuid.name(), dataUuid);
                            dyFormData.getFormDatas().put(formUuid, Lists.newArrayList(nestMainFormData));
                            if (dyFormData.getDeletedFormDatas() != null) {
                                dyFormData.getDeletedFormDatas().remove(formUuid);
                            }
                            if (dyFormData.getUpdatedFormDatas() != null) {
                                dyFormData.getUpdatedFormDatas().remove(formUuid);
                            }
                            if (dyFormData.getAddedFormDatas() != null) {
                                dyFormData.getAddedFormDatas().remove(formUuid);
                            }
                            saveFormData(dyFormData.getFormUuid(), dyFormData.getFormDatas(),
                                    dyFormData.getDeletedFormDatas(), dyFormData.getUpdatedFormDatas(),
                                    dyFormData.getAddedFormDatas(), dyFormData.getSignature(), dyformDataOptions);
                        }
                    }
                    if (!formUuid.equals(mainformUuid)) {// 非主表
                        formData.put(EnumRelationTblSystemField.mainform_data_uuid.name(), dataUuidOfMainform);
                        if (false == DyformTypeEnum.isPform(mainformDefinition.getFormType())
                                && StringUtils.isNotBlank(mainformDefinition.getpFormUuid())) {
                            formData.put(EnumRelationTblSystemField.mainform_form_uuid.name(),
                                    mainformDefinition.getpFormUuid());
                        } else {
                            formData.put(EnumRelationTblSystemField.mainform_form_uuid.name(),
                                    mainformDefinition.getUuid());
                        }
                    }

                    if (isInAddedOperateLog(formUuid, dataUuid, addedFormDatas)
                            || (isNew && formUuid.equals(mainformUuid))) {

                        if (!formUuid.equals(mainformUuid)) {// 非主表,保存数据关系
                            this.addSubformRelation(dy.doGetRelationTblNameOfpForm(), formData);
                        }
                        if (DyformTypeEnum.isMSTform(mainformDefinition.getFormType())
                                || DyformTypeEnum.isCform(mainformDefinition.getFormType())) {

                        } else {
                            formData.remove(EnumRelationTblSystemField.mainform_data_uuid.name());
                            formData.remove(EnumRelationTblSystemField.mainform_form_uuid.name());
                        }
                        formData.remove(EnumRelationTblSystemField.sort_order.name());
                        this.insertFormData(dy, formData, dyformDataOptions);
                        // 删除操作日志　
                        this.removeFromOperateLog(formUuid, dataUuid, addedFormDatas, updatedFormDatas);
                    } else if (isInUpdatedOperateLog(formUuid, dataUuid, updatedFormDatas)) {
                        if (!formUuid.equals(mainformUuid)) {// 非主表
                            String linked = (String) (FormDataHandler.getValueFromMap("linked", formData));
                            if (linked != null && linked.equals("true")) {// 该数据已存在于从表中,只需要建主表从表数据关系即可
                                this.addSubformRelation(dy.doGetRelationTblNameOfpForm(), formData);
                            }
                        }
                        String sortOrder = (FormDataHandler.getValueFromMap(
                                EnumRelationTblSystemField.sort_order.name(), formData)) + StringUtils.EMPTY;
                        if (DyformTypeEnum.isMSTform(mainformDefinition.getFormType())
                                || DyformTypeEnum.isCform(mainformDefinition.getFormType())) {

                        } else {
                            formData.remove(EnumRelationTblSystemField.mainform_data_uuid.name());
                            formData.remove(EnumRelationTblSystemField.mainform_form_uuid.name());
                        }
                        formData.remove(EnumRelationTblSystemField.sort_order.name());
                        Set<String> updatedFields = getUpdatedFields(formUuid, dataUuid, updatedFormDatas);
                        this.updateFormData(dy, formData, updatedFields, dyformDataOptions);

                        if (sortOrder != null
                                && sortOrder.trim().length() > 0
                                && (updatedFields != null && updatedFields
                                .contains(EnumRelationTblSystemField.sort_order.name()))) {
                            this.formDataDao.updateSortOrder(dy.doGetRelationTblNameOfpForm(), dataUuidOfMainform,
                                    dataUuid, SpringSecurityUtils.getCurrentUserId(), new Date(), sortOrder);
                        }
                        // 删除操作日志　
                        this.removeFromOperateLog(formUuid, dataUuid, addedFormDatas, updatedFormDatas);
                    }
                }
            }

            // 保存签名
            // this.saveSignature((String) dataUuidOfMainform, signature);

            // 被解除关系的从表
            if (deletedFormDatas == null || deletedFormDatas.size() == 0) {
                //保存文件控件文件排序
                saveFilesToFolderSortAndFileName(mainformUuid, formDatas);

                return (String) dataUuidOfMainform;
            }
            formUuidIt = deletedFormDatas.keySet().iterator();
            while (formUuidIt.hasNext()) {
                String formUuid = formUuidIt.next();

                List<String> deletedFormData = deletedFormDatas.get(formUuid);
                if (deletedFormData == null || deletedFormData.isEmpty()) {
                    continue;
                }

                // 解除关系
                this.doProcessDeletedFormData(formUuid, deletedFormDatas.get(formUuid), (String) dataUuidOfMainform);
                // add by wujx 20160804 begin
                // 删除从表数据
                for (String dataUuid : deletedFormDatas.get(formUuid)) {
                    // 判断当前从表数据是否存在和其他主表有关联（处理多主表关联同一从表数据），存在则不删除从表数据
                    DyFormFormDefinition DyformSubformFormDefinition = (DyFormFormDefinition) dyFormFacade
                            .getFormDefinition(formUuid);
                    if (!this.formDataDao.isExistOtherRelaction(
                            DyformSubformFormDefinition.doGetRelationTblNameOfpForm(), dataUuid, mainformUuid)) {
                        this.delFormData(formUuid, dataUuid);
                    }

                }
                // add by wujx 20160804 end
            }

            //保存文件控件文件排序
            saveFilesToFolderSortAndFileName(mainformUuid, formDatas);

            return (String) dataUuidOfMainform;
        } catch (Exception e) {

            if (e instanceof SQLGrammarException) {// sql语法错误
                SQLGrammarException ex = (SQLGrammarException) e;
                logger.error(ex.getMessage(), ex);
                throw new DyformDataSaveException(ex.getSQLException().toString(),
                        EnumDyformExceptionType.SQLGRAM.getValue());
            } else if (e.getCause() instanceof SQLException) {// 可能是字段长度超级之类的
                SQLException ex = (SQLException) e.getCause();
                logger.error(ex.getMessage(), ex);
                throw new DyformDataSaveException(ex.toString(), EnumDyformExceptionType.SQLGRAM.getValue());
            } else if (e instanceof DyformDataSaveException) {
                DyformDataSaveException ex = (DyformDataSaveException) e;
                logger.error(ex.getData().toString(), ex);
                throw ex;
            } else if (e instanceof JsonDataException) {
                throw (JsonDataException) e;
            } else {
                logger.error(e.getMessage(), e);
                throw new SaveDataException(e.getMessage(), e);
            }

        }
    }

    private void addSubformRelation(String relTableName, Map<String, Object> formData) {
        String uluuid = (String) (FormDataHandler.getValueFromMap(EnumRelationTblSystemField.uuid.name(), formData));
        Object sortOrder = FormDataHandler.getValueFromMap(EnumRelationTblSystemField.sort_order.name(), formData);
        this.formDataDao.addSubRelation(relTableName, SpringSecurityUtils.getCurrentUserId(), new Date(),
                SpringSecurityUtils.getCurrentUserId(), new Date(), uluuid, ((String) FormDataHandler.getValueFromMap(
                        EnumRelationTblSystemField.mainform_form_uuid.name(), formData)), ((String) FormDataHandler
                        .getValueFromMap(EnumRelationTblSystemField.mainform_data_uuid.name(), formData)),
                (String) FormDataHandler.getValueFromMap(EnumRelationTblSystemField.parent_uuid.name(), formData), 0,
                (sortOrder != null ? sortOrder.toString() : null),
                dyFormFacade.createUuid());
    }

    /**
     * 删除对应的操作日志
     *
     * @param formUuid
     * @param dataUuid
     * @param addedFormDatas
     * @param updatedFormDatas
     */
    private void removeFromOperateLog(String formUuid, Object dataUuid, Map<String, List<String>> addedFormDatas,
                                      Map<String, Map<String, Set<String>>> updatedFormDatas) {
        if (addedFormDatas != null && addedFormDatas.size() > 0) {
            List<String> addedRecords = addedFormDatas.get(formUuid);
            if (addedRecords != null && addedRecords.size() > 0) {
                while (addedRecords.contains(dataUuid)) {
                    addedRecords.remove(dataUuid);
                }
            }
        }

        if (updatedFormDatas != null && updatedFormDatas.size() > 0) {
            Map<String, Set<String>> updatedRecords = updatedFormDatas.get(formUuid);
            if (updatedRecords != null && updatedRecords.size() > 0 && updatedRecords.containsKey(dataUuid)) {
                updatedRecords.remove(dataUuid);
            }
        }
    }

    @Override
    public String updateFormData(String formUuid, Map<String, Object> formDataColMap, Set<String> updateFields)
            throws JSONException, JsonParseException, JsonMappingException, IOException, ParseException {
        if (formDataColMap == null || updateFields == null || updateFields.size() == 0) {
            return null;
        }
        FormDefinition dy = (FormDefinition) this.dyFormFacade.getFormDefinition(formUuid);
        return updateFormData(dy, formDataColMap, updateFields, new DyformDataOptions());
    }

    /**
     * 如何描述该方法
     *
     * @param formDataColMap
     * @param updateFields
     * @param dy
     * @return
     * @throws ParseException
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    private String updateFormData(FormDefinition dy, Map<String, Object> formDataColMap, Set<String> updateFields, DyformDataOptions dyformDataOptions)
            throws ParseException, UnsupportedEncodingException, JSONException {
        if (formDataColMap == null || updateFields == null || updateFields.size() == 0) {
            return null;
        }
        String formUuid = dy.getUuid();
        Object modifyTime = formDataColMap.get(EnumSystemField.modify_time.name());
        SimpleDateFormat sdf = new SimpleDateFormat(FormDefinitionHandler.getDateTimePatternByContentFormat(null));
        Set<String> notValidateFields = FormDataHandler.getNotValidateFields(dy.getId());
        FormDataHandler.removeNotValidateField(dy.getId());
        boolean fieldNotValidate = containsTheSameFields(notValidateFields, updateFields);
        if (!fieldNotValidate && isDataModifiedBeforeUpToDate(
                formUuid,
                (String) formDataColMap.get(EnumSystemField.uuid.name()),
                modifyTime == null ? null : (modifyTime instanceof String ? (String) modifyTime : sdf
                        .format(modifyTime)))) {// 在打开页面后到用户点提交时，数据被其他用户修改了
            throw new DyformDataSaveException("在您提交数据之前，数据已被他人所修改，请重新加载数据后再修改提交",
                    EnumDyformExceptionType.DATA_OUT_OF_DATE.getValue());
        }

        FormDefinitionHandler dUtils = dy.doGetFormDefinitionHandler();
        FormDataDao.setMemberAsLowerCase(updateFields);// 将被更新的字段名设置为小写
        Iterator<String> it = formDataColMap.keySet().iterator();
        Map<String, Object> updateFormDataColMap = new HashMap<String, Object>();
        while (it.hasNext()) {
            String field = it.next();
            if (!updateFields.contains(field) && !FormDefinitionHandler.isSysTypeAsSystem(field)) {
                // formDataColMap.remove(field);
            } else {
                if (FormDefinitionHandler.isSysTypeAsSystem(field)
                        || dy.doGetFormDefinitionHandler().isFieldInDefinition(field))
                    updateFormDataColMap.put(field, formDataColMap.get(field));
            }
        }
        // uuid和form_uuid，7.0不用form_uuid
        if (updateFormDataColMap.size() > 2 || (!updateFormDataColMap.containsKey("form_uuid") && updateFormDataColMap.size() > 1)) {
            FormDataHandler dataUtils = new FormDataHandler(formUuid, formDataColMap, dUtils, dyformDataOptions);
            Iterator<String> it2 = updateFormDataColMap.keySet().iterator();
            while (it2.hasNext()) {
                String fieldName = it2.next();
                logger.debug("processed fieldName:" + fieldName);
                if (!dUtils.isTblField(fieldName)) {// 非表单的字段
                    it2.remove();
                }
                if (!dUtils.isFieldInDefinition(fieldName)) {
                    continue;
                }
                if (dUtils.isValueCreateBySystemWhenSave(fieldName)) {// 在保存时由系统产生
                    // bug#61224
                    // dataUtils.doProcessValueCreateBySystem(fieldName);
                } else /* if (dUtils.isValueCreateByUser(fieldName)) */ {// 从前台插入

                    dataUtils.doProcessValueCreateByUser(fieldName);
                    updateFormDataColMap.put(fieldName, formDataColMap.get(fieldName));
                }
            }
            this.formDataDao.update(dy, updateFormDataColMap);
            if (!fieldNotValidate) {
                FormDataHandler.setUpdated();
            }

            dealFieldValueRef(dy, dUtils, formDataColMap, true);
        }

        return null;
    }

    /**
     * @param notValidateFields
     * @param updateFields
     * @return
     */
    private boolean containsTheSameFields(Set<String> notValidateFields, Set<String> updateFields) {
        if (CollectionUtils.size(notValidateFields) != CollectionUtils.size(updateFields)) {
            return false;
        }
        Set<String> tmpSet = Sets.newHashSet(notValidateFields);
        tmpSet.removeAll(updateFields);
        return CollectionUtils.isEmpty(tmpSet);
    }

    /**
     * 判断数据在修改之前是否up to date, 是返回false, 否返回true
     *
     * @param modifyTime
     * @return
     */
    private boolean isDataModifiedBeforeUpToDate(String formUUid, String dataUuid, String modifyTime) {
        if (StringUtils.isBlank(modifyTime)) {// 没有传入modifyTime，则默认为已更新到最新
            return false;
        }
        Map<String, Object> formData = this.getFormDataOfMainform(formUUid, dataUuid);
        if (formData == null) {
            return false;
        }
        Date modifyTimeInDbD = null;
        modifyTimeInDbD = (Date) formData.get(EnumSystemField.modify_time.name());
        if (modifyTimeInDbD == null) {
            return false;
        }
        // String pattern = modifyTime
        // SimpleDateFormat sdf = new
        // SimpleDateFormat(FormDefinitionHandler.getDateTimePatternByContentFormat(null));
        SimpleDateFormat sdf = new SimpleDateFormat(
                FormDefinitionHandler.getDateTimePatternByContentFormat(DyDateFomat.dateTimeSec));
        Date modifyTimeD = null;
        try {
            modifyTimeD = sdf.parse(modifyTime);
        } catch (ParseException e) {
            // logger.error(e.getMessage(), e);
            sdf = new SimpleDateFormat(FormDefinitionHandler.getDateTimePatternByContentFormat(null));
            try {
                modifyTimeD = sdf.parse(modifyTime);
                modifyTimeInDbD = sdf.parse(sdf.format(modifyTimeInDbD));
            } catch (ParseException e1) {
                logger.error(e1.getMessage(), e1);
                return false;
            }

        }

        // modifyTimeInDbD = sdf.parse(sdf.format(modifyTimeInDbD));
        if (modifyTimeInDbD.after(modifyTimeD)) {// 在用户打开页面后，提交到数据库之前，已经有其他用户对在数据库里面的这条记录做了修改
            return true;
        }

        return false;
    }

    private void saveFilesToFolderSortAndFileName(String mainformUuid, Map<String, List<Map<String, Object>>> formDatas) {
        for (String formUuid : formDatas.keySet()) {
            if (mainformUuid.equals(formUuid)) {// 主表数据
                FormDefinition mainformDefinition = // this.getDefinition(mainformUuid);
                        (FormDefinition) dyFormFacade.getFormDefinition(mainformUuid);

                FormDefinitionHandler dUtils = mainformDefinition.doGetFormDefinitionHandler();
                Map<String, Object> formDataOfMainform = formDatas.get(formUuid).get(0);
                FormDataHandler dataUtils = new FormDataHandler(formUuid, formDataOfMainform, dUtils);
                for (String fieldName : formDataOfMainform.keySet()) {
                    dataUtils.doProcessValueFileField(fieldName);
                }
            } else {// 从表数据
                FormDefinition formDefinition = (FormDefinition) dyFormFacade.getFormDefinition(formUuid);

                FormDefinitionHandler dUtils = formDefinition.doGetFormDefinitionHandler();

                List<Map<String, Object>> maps = formDatas.get(formUuid);
                for (Map<String, Object> map : maps) {
                    FormDataHandler dataUtils = new FormDataHandler(formUuid, map, dUtils);
                    for (String fieldName : map.keySet()) {
                        dataUtils.doProcessValueFileField(fieldName);
                    }
                }
            }
        }
    }

    @Override
    public String insertFormData(String formUuid, Map<String, Object> formDataColMap) throws JSONException,
            JsonParseException, JsonMappingException, IOException, ParseException {
        // 根据定义来设置特殊字段
        FormDefinition formDefinition = (FormDefinition) dyFormFacade.getFormDefinition(formUuid);
        return insertFormData(formDefinition, formDataColMap, new DyformDataOptions());
    }

    /**
     * 如何描述该方法
     *
     * @param formDefinition
     * @param formDataColMap
     * @return
     * @throws ParseException
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    private String insertFormData(FormDefinition formDefinition, Map<String, Object> formDataColMap, DyformDataOptions dyformDataOptions)
            throws ParseException, UnsupportedEncodingException, JSONException {
        String formUuid = formDefinition.getUuid();
        String dataUuid = (String) (FormDataHandler.getValueFromMap(EnumRelationTblSystemField.uuid.name(),
                formDataColMap));
        if (dataUuid == null || dataUuid.trim().length() == 0) {
            dataUuid = dyFormFacade.createUuid();
            formDataColMap.put(EnumSystemField.uuid.name(), dataUuid);
        }
        FormDefinitionHandler dUtils = formDefinition.doGetFormDefinitionHandler();
        FormDataHandler dataUtils = new FormDataHandler(formUuid, formDataColMap, dUtils, dyformDataOptions);

        List<String> fieldNames = dUtils.getFieldNamesOfMaintable();// 表单的各字段名称

        // 验证数据格式的正确性
        ValidateMsg vMsg = dataUtils.validate();
        if (vMsg.getCode() != EnumValidateCode.SUCESS) {
            throw new FormDataValidateException(vMsg.getMsg());
        }

        Iterator<String> it = fieldNames.iterator();
        while (it.hasNext()) {
            String fieldName = it.next();
            logger.debug("processed fieldName:" + fieldName);
            if (!dUtils.isTblField(fieldName)) {// 非表单的字段
                it.remove();
            }
            if (dUtils.isValueCreateBySystemWhenSave(fieldName)) {// 在保存时由系统产生
                dataUtils.doProcessValueCreateBySystem(fieldName);
            } else/* if (dUtils.isValueCreateByUser(fieldName)) */ {// 从前台插入
                dataUtils.doProcessValueCreateByUser(fieldName);
            }
            // dataUtils.validate(fieldName);//验证数据
        }
        if (!formDataColMap.containsKey(EnumSystemField.status.name())) {
            formDataColMap.put(EnumSystemField.status.name(), EnumFormDataStatus.DYFORM_DATA_STATUS_DEFAULT.getValue());
        }
        if (false == DyformTypeEnum.isPform(formDefinition.getFormType())
                && StringUtils.isNotBlank(formDefinition.getpFormUuid())) {
            formDataColMap.put(EnumSystemField.form_uuid.name(), formDefinition.getpFormUuid());
        } else {
            formDataColMap.put(EnumSystemField.form_uuid.name(), formUuid);
        }


        this.formDataDao.save(formDefinition, formDataColMap);
        dealFieldValueRef(formDefinition, dUtils, formDataColMap, false);

        return dataUuid;
    }

    public DbTableDao getDbTableDao() {
        return dbTableDao;
    }

    @Override
    public String rewriteFormData(String formUuid, Map<String, List<Map<String, Object>>> formDatas,
                                  DyformDataSignature signature) {

        // 找不到数据
        if (formDatas == null || formDatas.size() == 0) {
            return null;
        }

        // 获取主表的数据id
        Map<String, Object> formDataOfMainform = formDatas.get(formUuid).get(0);
        String dataUuidOfMainform = formDataOfMainform.get(EnumSystemField.uuid.name()) + StringUtils.EMPTY;

        // 将原表单的所有数据事先删除掉
        this.delFullFormData(formUuid, dataUuidOfMainform);

        return this.saveFormData(formUuid, formDatas, null, signature);
    }

    @Override
    public void delFullFormData(String formUuid, String dataUuid) {
        // 删除主表数据
        this.delFormData(formUuid, dataUuid);

        // 全量删除从表数据
        this.delFullSubFormData(formUuid, dataUuid);
    }

    /**
     * 删除指定的表单单条记录
     *
     * @param formUuid
     * @param dataUuid
     */
    @Override
    public void delFormData(String formUuid, String dataUuid) {
        // FormDefinition FormDefinition =
        // dyFormFacade.getFormDefinition(formUuid);
        DyFormFormDefinition FormDefinition = (DyFormFormDefinition) dyFormFacade.getFormDefinition(formUuid);
        mongoFileService.popAllFilesFromFolder(dataUuid);// 清空附件
        this.formDataDao.delFormData(FormDefinition.doGetTblNameOfpForm(),
                FormDefinition.doGetRelationTblNameOfpForm(), dataUuid, formUuid);
        deleteFieldValueRef(dataUuid);
    }

    @Override
    public void delFullSubFormData(String formUuid, String dataUuid) {
        DyFormFormDefinition FormDefinition = (DyFormFormDefinition) dyFormFacade.getFormDefinition(formUuid);
        List<DyformSubformFormDefinition> subformConfigs = FormDefinition.doGetSubformDefinitions();
        for (DyformSubformFormDefinition subformConfig : subformConfigs) {
            String subformUuid = subformConfig.getFormUuid();
            List<Map<String, Object>> subformFormdatas = this.getFormDataOfSubform(subformUuid, dataUuid, null, null);
            for (Map<String, Object> formdata : subformFormdatas) {
                String subdataUuid = formdata.get(EnumSystemField.uuid.name()) + StringUtils.EMPTY;
                this.delFormData(subformUuid, subdataUuid);
            }
        }
    }

    @Override
    public void delFullSubFormData(String formUuid, String dataUuid, String formUuidOfSubform) {
        DyFormFormDefinition FormDefinition = (DyFormFormDefinition) dyFormFacade.getFormDefinition(formUuid);
        List<DyformSubformFormDefinition> subformConfigs = FormDefinition.doGetSubformDefinitions();
        for (DyformSubformFormDefinition subformConfig : subformConfigs) {
            String subformUuid = subformConfig.getFormUuid();
            if (!subformUuid.equalsIgnoreCase(formUuidOfSubform)) {
                continue;
            }
            List<Map<String, Object>> subformFormdatas = this.getFormDataOfSubform(subformUuid, dataUuid, null, null);
            for (Map<String, Object> formdata : subformFormdatas) {
                String subdataUuid = formdata.get(EnumSystemField.uuid.name()) + StringUtils.EMPTY;
                this.delFormData(subformUuid, subdataUuid);
            }
        }

    }

    @Override
    public Integer getMinOrderNo(String mainformFormUuid, String mainformDataUuid, String subformFormUuid) {
        String rlTblName = dyFormFacade.getRlTblNameByFormUuid(subformFormUuid);// 获取关系表表名
        return this.formDataDao.getMinOrderNo(rlTblName, mainformFormUuid, mainformDataUuid, subformFormUuid);
    }

    @Override
    public Integer getMaxOrderNo(String mainformFormUuid, String mainformDataUuid, String subformFormUuid) {
        String rlTblName = dyFormFacade.getRlTblNameByFormUuid(subformFormUuid);// 获取关系表表名
        return this.formDataDao.getMaxOrderNo(rlTblName, mainformFormUuid, mainformDataUuid, subformFormUuid);

    }

    /**
     * 通过从表信息获取表单数据
     *
     * @param dataUuidOfSubform
     * @return
     * @throws Exception
     */
    public List<DyFormData> getDyFormDataBySubformInfo(String subFormUuid, String subDataUuid) throws Exception {
        List<DyFormData> dyFormDataList = new ArrayList<DyFormData>();
        DyFormFormDefinition DyformSubformFormDefinition = (DyFormFormDefinition) dyFormFacade
                .getFormDefinition(subFormUuid);
        String relationTblName = DyformSubformFormDefinition.doGetRelationTblNameOfpForm();
        List<Map<String, Object>> mainformInfoList = formDataDao.getMainFormInfoBySubDataUuid(relationTblName,
                subDataUuid);

        for (Map<String, Object> mainformInfo : mainformInfoList) {
            String mainFormUuid = mainformInfo.get(EnumRelationTblSystemField.mainform_form_uuid.getName()).toString();
            String mainDataUuid = mainformInfo.get(EnumRelationTblSystemField.mainform_data_uuid.getName()).toString();
            DyFormData dyFormData = dyFormFacade.getDyFormData(mainFormUuid, mainDataUuid);
            dyFormDataList.add(dyFormData);
        }
        return dyFormDataList;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getUniqueFormData(String formUuid, String systemUnitId) {
        String tableName = dyFormFacade.getTblNameByFormUuid(formUuid);
        if (StringUtils.isBlank(systemUnitId)) {
            throw new RuntimeException("查询表单数据未定义系统单位ID");
        }
        String sql = "select * from " + tableName + " where " + EnumSystemField.form_uuid.name() + "=:formUuid";
        if (systemUnitId.startsWith(IdPrefix.SYSTEM_UNIT.getValue())) {
            sql += " and system_unit_id=:systemUnitId";
        } else {
            sql += " and " + systemUnitId;
        }

        Map<String, Object> map = Maps.newHashMap(TemplateEngineFactory.getExplainRootModel());
        map.put("formUuid", formUuid);
        map.put("systemUnitId", systemUnitId);
        List<Map<String, Object>> allDatas;
        try {
            allDatas = dbTableDao.query(sql, map, 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (allDatas != null && allDatas.size() > 1) {
            throw new UniqueException("表单查询唯一数据异常:  条件[" + systemUnitId + "], 单据定义uuid[" + formUuid + "],对应有多条数据存在 ");
        } else if (allDatas == null || allDatas.size() == 0) {
            return null;
        } else {

            Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> formDatas = new HashMap<String, List<Map<String, Object>>>();

            Map<String, Object> formDataOfMainform = allDatas.get(0);
            if (formDataOfMainform == null) {
                return null;
            }

            List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>> list = new ArrayList<Map<String, Object>>();
            list.add(formDataOfMainform);
            formDatas.put(formUuid, list);

            FormDefinition df = (FormDefinition) dyFormFacade.getFormDefinition(formUuid);
            FormDefinitionHandler dUtils = df.doGetFormDefinitionHandler();
            for (String formUuidOfSubform : dUtils.getFormUuidsOfSubform()) {
                QueryData qd = this.getFormDataOfParentNode(formUuidOfSubform, formUuid,
                        (String) formDataOfMainform.get(EnumSystemField.uuid.name()), false);
                if (qd == null || qd.getDataList() == null) {
                    continue;
                }
                List formDataOfSubform = qd.getDataList();
                if (formDataOfSubform == null) {
                    continue;
                }
                formDatas.put(formUuidOfSubform, formDataOfSubform);
            }
            return formDatas;
        }

    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#isUniqueForFields(java.lang.String, java.util.Map, java.lang.String)
     */
    @Override
    public boolean isUniqueForFields(String uuid, String formUuid, Map<String, Object> params, String fiterCondition) {
        String tblNm = dyFormFacade.getTblNameByFormUuid(formUuid);
        List list = formDataDao.getDataByTblNmAndParams(tblNm, params, fiterCondition, uuid);
        if (list.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#queryUniqueForFields(java.lang.String, java.util.Map, java.lang.String)
     */
    @Override
    public List<String> queryUniqueForFields(String formUuid, Map<String, Object> params, String fiterCondition) {
        List<String> lists = Lists.newArrayList();
        String tblNm = dyFormFacade.getTblNameByFormUuid(formUuid);
        List<Object> list = formDataDao.getDataByTblNmAndParams(tblNm, params, fiterCondition, "");
        for (Object object : list) {
            lists.add(String.valueOf(object));
        }
        return lists;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#query(com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo)
     */
    @SuppressWarnings("rawtypes")
    @Transactional(readOnly = true)
    public FormDataQueryData query(FormDataQueryInfo queryInfo) {
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinitionById(queryInfo.getFormId());
        UserTableFormDataQueryInfo userTableFormDataQueryInfo = new UserTableFormDataQueryInfo(
                dyFormFormDefinition.getTableName(), queryInfo);
        NativeDao nativeDao = ApplicationContextHolder.getBean(NativeDao.class);
        FormDataQuery formDataQuery = new UserTableFormDataQueryImpl(userTableFormDataQueryInfo, nativeDao);
        List<HashMap> queryItems = formDataQuery.list(HashMap.class);
        // CLOB字段值转为字符串
        clob2String(queryItems);
        FormDataQueryData queryData = new FormDataQueryData();
        queryData.setDataList(queryItems);
        PagingInfo pagingInfo = queryInfo.getPagingInfo();
        // 查询总数
        if (pagingInfo != null && pagingInfo.isAutoCount()) {
            pagingInfo.setTotalCount(formDataQuery.count());
        }
        queryData.setPagingInfo(pagingInfo);
        return queryData;
    }

    /**
     * @param queryItems
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void clob2String(List<HashMap> queryItems) {
        for (Map queryItem : queryItems) {
            Set<String> keys = queryItem.keySet();
            for (String key : keys) {
                Object value = queryItem.get(key);
                if (value instanceof Clob) {
                    queryItem.put(key, ClobUtils.ClobToString((Clob) value));
                }
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.data.service.FormDataService#count(com.wellsoft.pt.dyform.implement.repository.usertable.support.UserTableFormDataQueryInfo)
     */
    @Override
    @Transactional(readOnly = true)
    public long count(FormDataQueryInfo queryInfo) {
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinitionById(queryInfo.getFormId());
        UserTableFormDataQueryInfo userTableFormDataQueryInfo = new UserTableFormDataQueryInfo(
                dyFormFormDefinition.getTableName(), queryInfo);
        NativeDao nativeDao = ApplicationContextHolder.getBean(NativeDao.class);
        FormDataQuery formDataQuery = new UserTableFormDataQueryImpl(userTableFormDataQueryInfo, nativeDao);
        return formDataQuery.count();
    }

    @Override
    @Transactional
    public void copyAsVersionData(String table, String dataUuid, String newDataUuid) {
        SQLQuery versionQuery = formDataDao.getSession().createSQLQuery(" SELECT MAX(VERSION) FROM " + table + "_VN WHERE NUUID=:uuid");
        versionQuery.setParameter("uuid", dataUuid);
        List list = versionQuery.list();
        float version = 1.0f;
        if (CollectionUtils.isNotEmpty(list) && list.get(0) != null) {
            version = ((BigDecimal) list.get(0)).floatValue();
            version += 0.1f;
        }

        SQLQuery sqlQuery = formDataDao.getSessionFactory().getCurrentSession().createSQLQuery(
                NamedQueryScriptLoader.generateDynamicNamedQueryString(formDataDao.getSessionFactory(), "queryTableColumnMetadata", null));
        sqlQuery.setParameter("tableName", table);
        sqlQuery.setResultTransformer(QueryItemResultTransformer.INSTANCE);
        List<QueryItem> columns = sqlQuery.list();
        List<String> colNames = Lists.newArrayList();
        for (QueryItem column : columns) {
            colNames.add(column.getString("columnName").toUpperCase());
        }
        SQLQuery query = formDataDao.getSession().createSQLQuery(" INSERT INTO " + table + "_VN ( " + StringUtils.join(colNames, " , ") + " , NUUID , VERSION) " +
                "SELECT  C." + (StringUtils.join(colNames, ", C.")) + " , "
                + newDataUuid + " AS NUUID, " + version + " AS VERSION FROM " + table + " C WHERE C.UUID=:uuid");
        query.setParameter("uuid", dataUuid);
        query.executeUpdate();

        query = formDataDao.getSession().createSQLQuery(" UPDATE " + table + "_VN SET NUUID =:nuuid WHERE NUUID=:onuuid ");
        query.setParameter("onuuid", dataUuid);
        query.setParameter("nuuid", newDataUuid);
        query.executeUpdate();
    }

    @Override
    public List<Map<String, Object>> queryFormDataListByFormIdWhere(String formId, String where, Map<String, Object> namedParams) {
        DyFormFormDefinition formFormDefinition = dyFormFacade.getFormDefinitionOfMaxVersionById(formId);
        List<String> uuids = this.queryFormDataList(formFormDefinition.getTableName(), "uuid", where, namedParams);
        if (CollectionUtils.isNotEmpty(uuids)) {
            List list = Lists.newArrayList();
            for (String u : uuids) {
                Map<String, Object> d = this.getFormDataOfMainform(formFormDefinition.getUuid(), u);
                if (MapUtils.isNotEmpty(d)) {
                    list.add(d);
                }
            }
            return list;
        }
        return null;
    }

    @Override
    public void delOnlyFormData(String formUuid, String dataUuid) {
        Session session = this.formDataDao.getSessionFactory().getCurrentSession();
        DyFormFormDefinition FormDefinition = (DyFormFormDefinition) dyFormFacade.getFormDefinition(formUuid);
        StringBuilder sql = new StringBuilder();
        sql.append("delete from  " + FormDefinition.getTableName() + "  ");
        sql.append(" where  ");
        sql.append(EnumSystemField.uuid.name());
        sql.append("= '");
        sql.append(dataUuid);
        sql.append("'   ");

        SQLQuery sqlquery = session.createSQLQuery(sql.toString());
        sqlquery.executeUpdate();
    }

    @Override
    @Transactional
    public String saveFormDataAsNewVersion(DyFormData formData, String dataUuid) {
        this.copyAsVersionData(dyFormFacade.getTblNameByFormUuid(formData.getFormUuid()), dataUuid, formData.getNewOldUuidMap().get(dataUuid));
        saveSubformDataAsNewVersion(formData, dataUuid, formData.getNewOldUuidMap(), formData.getFormUuid());
        delOnlyFormData(formData.getFormUuid(), dataUuid);
        String uuid = dyFormFacade.saveFormData(formData);
        return uuid;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getAllVersionFormData(String formUuid, String dataUuid) {
        String tableName = dyFormFacade.getTblNameByFormUuid(formUuid);
        Map<String, List<Map<String, Object>>> result = Maps.newHashMap();
        try {
            List<Map<String, Object>> dataList = dbTableDao.queryByParams("select v.* from " + tableName + "_vn v where v.nuuid=:nuuid order by version desc", ImmutableMap.<String, Object>builder().put("formUuid", formUuid).put("nuuid", dataUuid).build());
            result.put(formUuid, dataList);
            if (CollectionUtils.isNotEmpty(dataList)) {
                FormDefinition formDefinition = ((FormDefinition) dyFormFacade.getFormDefinition(formUuid));
                FormDefinitionHandler dUtils = formDefinition.doGetFormDefinitionHandler();
                for (Map<String, Object> d : dataList) {
                    List<LogicFileInfo> allfiles = this.mongoFileService.getNonioFilesFromFolder(d.get("uuid").toString(), null);
                    if (allfiles != null && allfiles.size() > 0) {
                        for (String fieldName : dUtils.getFieldNamesOfMainform()) {
                            if (dUtils.isInputModeEqAttach(fieldName)) {// 附件
                                List<LogicFileInfo> files = this.getFiles(allfiles, fieldName);
                                d.put(fieldName, files);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取版本表单数据异常", e);
        }
        return result;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getVersionFormData(String formUuid, String dataUuid) {
        String tableName = dyFormFacade.getTblNameByFormUuid(formUuid);
        Map<String, List<Map<String, Object>>> result = Maps.newHashMap();
        try {
            List<Map<String, Object>> dataList = dbTableDao.queryByParams("select * from " + tableName + "_vn where  uuid=:dataUuid"
                    , ImmutableMap.<String, Object>builder().put("formUuid", formUuid).put("dataUuid", dataUuid).build());
            result.put(formUuid, dataList);
            if (CollectionUtils.isNotEmpty(dataList)) {
                FormDefinition formDefinition = ((FormDefinition) dyFormFacade.getFormDefinition(formUuid));
                FormDefinitionHandler dUtils = formDefinition.doGetFormDefinitionHandler();
                for (Map<String, Object> d : dataList) {
                    List<LogicFileInfo> allfiles = this.mongoFileService.getNonioFilesFromFolder(d.get("uuid").toString(), null);
                    if (allfiles != null && allfiles.size() > 0) {
                        for (String fieldName : dUtils.getFieldNamesOfMainform()) {
                            if (dUtils.isInputModeEqAttach(fieldName)) {// 附件
                                List<LogicFileInfo> files = this.getFiles(allfiles, fieldName);
                                d.put(fieldName, files);
                            }
                        }
                    }
                }
                result.putAll(getVersionSubformDatas(formDefinition, dataUuid));
            }
        } catch (Exception e) {
            logger.error("获取版本表单数据异常", e);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> queryFormDataListByFormUuidWhere(String formUuid, String where, Map<String, Object> namedParams) {
        DyFormFormDefinition formFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        List<String> uuids = this.queryFormDataList(formFormDefinition.getTableName(), "uuid", where, namedParams);
        if (CollectionUtils.isNotEmpty(uuids)) {
            List list = Lists.newArrayList();
            for (String u : uuids) {
                Map<String, Object> d = this.getFormDataOfMainform(formFormDefinition.getUuid(), u);
                if (MapUtils.isNotEmpty(d)) {
                    list.add(d);
                }
            }
            return list;
        }
        return null;
    }

    @Override
    public List<String> getFormDataUuidByWhereByFormUuid(String formUuid, String where, Map<String, Object> namedParams) {
        DyFormFormDefinition formFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        List<String> uuids = this.queryFormDataList(formFormDefinition.getTableName(), "uuid", where, namedParams);
        return uuids;
    }

    private Map<String, List<Map<String, Object>>> getVersionSubformDatas(FormDefinition formDefinition, String dataUuid) {
        Map<String, List<Map<String, Object>>> result = Maps.newHashMap();
        List<DyformSubformFormDefinition> subformFormDefinitions = formDefinition.doGetSubformDefinitions();
        if (CollectionUtils.isNotEmpty(subformFormDefinitions)) {
            for (DyformSubformFormDefinition subformFormDefinition : subformFormDefinitions) {
                String tableName = dyFormFacade.getTblNameByFormUuid(subformFormDefinition.getFormUuid());
                try {
                    List<Map<String, Object>> dataList = dbTableDao.queryByParams("select v.* from " + tableName + "_rl rl , " + tableName + "_vn v" +
                                    " where  rl.mainform_data_uuid=:dataUuid and rl.data_uuid=v.uuid order by rl.sort_order asc"
                            , ImmutableMap.<String, Object>builder().put("formUuid", formDefinition.getUuid()).put("dataUuid", dataUuid).build());
                    FormDefinition sbf = ((FormDefinition) dyFormFacade.getFormDefinition(subformFormDefinition.getFormUuid()));
                    FormDefinitionHandler dUtils = sbf.doGetFormDefinitionHandler();
                    for (Map<String, Object> d : dataList) {
                        result.putAll(getVersionSubformDatas(sbf, d.get("uuid").toString()));
                        List<LogicFileInfo> allfiles = this.mongoFileService.getNonioFilesFromFolder(d.get("uuid").toString(), null);
                        if (allfiles != null && allfiles.size() > 0) {
                            for (String fieldName : dUtils.getFieldNamesOfMainform()) {
                                if (dUtils.isInputModeEqAttach(fieldName)) {// 附件
                                    List<LogicFileInfo> files = this.getFiles(allfiles, fieldName);
                                    d.put(fieldName, files);
                                }
                            }
                        }
                    }
                    result.put(subformFormDefinition.getFormUuid(), dataList);
                } catch (Exception e) {
                    logger.error("获取版本表单数据异常", e);
                }

            }
        }
        return result;
    }

    private void saveSubformDataAsNewVersion(DyFormData dyFormData, String dataUuid, Map<String, String> newOldUuidMap, String formUuid) {
        List<DyformSubformFormDefinition> subformFormDefinitions = dyFormFacade.getSubformDefinitions(dyFormData.getFormUuid());
        if (CollectionUtils.isNotEmpty(subformFormDefinitions)) {
            for (DyformSubformFormDefinition sf : subformFormDefinitions) {
                List<Map<String, Object>> newsubFormDataList = dyFormData.getFormDatas(sf.getFormUuid());
                Map<String, Map<String, Object>> uuidDataMap = Maps.newHashMap();
                boolean isEmpty = (dyFormData.getDeletedFormDatas() == null || !dyFormData.getDeletedFormDatas().containsKey(sf.getFormUuid())
                        || CollectionUtils.isEmpty(dyFormData.getDeletedFormDatas().get(sf.getFormUuid())))
                        && (dyFormData.getUpdatedFormDatas() == null || !dyFormData.getUpdatedFormDatas().containsKey(sf.getFormUuid())
                        || MapUtils.isEmpty(dyFormData.getUpdatedFormDatas().get(sf.getFormUuid())))
                        && (dyFormData.getAddedFormDatas() == null || !dyFormData.getAddedFormDatas().containsKey(sf.getFormUuid())
                        || CollectionUtils.isEmpty(dyFormData.getAddedFormDatas().get(sf.getFormUuid())));
                if (CollectionUtils.isNotEmpty(newsubFormDataList)) {
                    for (Map<String, Object> m : newsubFormDataList) {
                        uuidDataMap.put(m.get("uuid").toString(), m);
                    }
                }
                String subFormTableName = dyFormFacade.getTblNameByFormUuid(sf.getFormUuid());
                Map<String, List<Map<String, Object>>> subFormDataMap = getSubformDatas(Lists.newArrayList(sf.getFormUuid()), dyFormData.getFormUuid(), dataUuid, false);
                List<Map<String, Object>> subformDatas = subFormDataMap.get(sf.getFormUuid());
                if (CollectionUtils.isNotEmpty(subformDatas)) {
                    for (Map<String, Object> map : subformDatas) {
                        String uuid = map.get("uuid").toString();
                        String nuuid = null;
                        String nestformDatas = null;
                        Map<String, Object> m = null;
                        if (isEmpty) {
                            // 前端未打开编辑的情况
                            nuuid = SnowFlake.getId() + "";
                            map.put("uuid", nuuid);
                            newOldUuidMap.put(uuid, nuuid);
                            if (!dyFormData.getAddedFormDatas().containsKey(sf.getFormUuid())) {
                                dyFormData.getAddedFormDatas().put(sf.getFormUuid(), Lists.newArrayList());
                                dyFormData.getFormDatas().put(sf.getFormUuid(), Lists.newArrayList());
                            }
                            dyFormData.getAddedFormDatas().get(sf.getFormUuid()).add(nuuid);
                            dyFormData.getFormDatas().get(sf.getFormUuid()).add(map);
                        }
                        if (uuidDataMap.containsKey(newOldUuidMap.get(uuid))) {
                            nuuid = newOldUuidMap.get(uuid);
                            m = uuidDataMap.get(nuuid);
                            Set<String> keys = map.keySet();
                            keys.removeAll(m.keySet());
                            if (CollectionUtils.isNotEmpty(keys)) {
                                for (String k : keys) {
                                    m.put(k, map.get(k));
                                }
                            }
                            nestformDatas = m.containsKey("nestformDatas") ? m.get("nestformDatas").toString() : null;
                        }
                        DyFormData subDyformData = null;
                        if (nestformDatas != null) {
                            subDyformData = dyFormFacade.parseDyformData(nestformDatas);
                        } else {
                            subDyformData = dyFormFacade.createDyformData(sf.getFormUuid());
                        }
                        saveSubformDataAsNewVersion(subDyformData, uuid, newOldUuidMap, sf.getFormUuid());
                        if (m != null) {
                            m.put("nestformDatas", subDyformData.cloneDyFormDatasToJson());
                        } else {
                            map.put("nestformDatas", subDyformData.cloneDyFormDatasToJson());
                        }
                        copyAsVersionData(subFormTableName, uuid, nuuid);
                        delOnlyFormData(sf.getFormUuid(), uuid);
                    }
                }
            }
        }
    }

    private void dealFieldValueRef(FormDefinition formDefinition, FormDefinitionHandler dUtils,
                                   Map<String, Object> formDataColMap, boolean isDelete) {
        Map<String, String> fields = dUtils.getMultiFields();
        Map<String, Object> item = new HashMap<String, Object>();
        // 处理大小写问题
        for (String key : formDataColMap.keySet()) {
            item.put(key.toLowerCase(), formDataColMap.get(key));
        }
        for (String field : fields.keySet()) {
            Object o = null;
            Object show = null;
            if (item.containsKey(field) && (o = item.get(field)) != null) {
                // 获取显示字段的值
                String showField = fields.get(field);
                if (showField != null) {
                    show = item.get(showField);
                }
                if (isDelete) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("dataUuid", item.get("uuid"));
                    params.put("fieldCode", field);
                    // 先删除数据
                    String hql = "delete from DyformFieldValueRel where dataUuid=:dataUuid and fieldCode=:fieldCode";
                    dyformFieldValueRelDao.deleteByHQL(hql, params);
                }
                String[] array = o.toString().split(",|;");
                String[] showArray = null;
                if (show != null) {
                    showArray = show.toString().split(",|;");
                    if (showArray.length != array.length) {
                        showArray = new String[array.length];
                    }
                } else {
                    showArray = new String[array.length];
                }
                for (int i = 0; i < array.length; i++) {
                    if (StringUtils.isNotEmpty(array[i])) {
                        DyformFieldValueRel ref = new DyformFieldValueRel();
                        // system Fields
                        ref.setCreator(SpringSecurityUtils.getCurrentUserId());
                        ref.setCreateTime(Calendar.getInstance().getTime());
                        ref.setModifier(SpringSecurityUtils.getCurrentUserId());
                        ref.setModifyTime(Calendar.getInstance().getTime());
                        // field_code=字段名，filed_value=真实值（拆分掉），field_text=显示值（拆分掉）
                        ref.setFieldCode(field);
                        ref.setFieldValue(array[i]);
                        ref.setFieldText(showArray[i]);
                        ref.setDataUuid(item.get("uuid").toString());
                        ref.setFormId(formDefinition.getId());
                        dyformFieldValueRelDao.save(ref);
                    }
                }
            }
        }
    }

    /**
     * 如何描述该方法
     */
    private void deleteFieldValueRef(String dataUuid) {
        String hql = "delete from DyformFieldValueRel where dataUuid=:dataUuid";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dataUuid", dataUuid);
        dyformFieldValueRelDao.deleteByHQL(hql, params);
    }

}
