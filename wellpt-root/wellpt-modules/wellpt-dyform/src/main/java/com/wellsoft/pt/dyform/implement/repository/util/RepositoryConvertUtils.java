/*
 * @(#)2019年8月21日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.dyform.facade.dto.DyformDataSignature;
import com.wellsoft.pt.dyform.implement.repository.FormData;
import com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext;
import com.wellsoft.pt.dyform.implement.repository.usertable.metadata.ColumnMetadata;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.type.StandardBasicTypes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月21日.1	zhulh		2019年8月21日		Create
 * </pre>
 * @date 2019年8月21日
 */
public class RepositoryConvertUtils {

    /**
     * @param formData
     * @return
     */
    public static Map<String, Object> convert2MapData(FormData formData) {
        return formData.getDataMap();
    }

    /**
     * @param formId
     * @param mainformUuid
     * @param formDatas
     * @param deletedFormDatas
     * @param updatedFormDatas
     * @param addedFormDatas
     * @param signature
     * @return
     */
    public static FormData convert2FormData(FormRepositoryContext repositoryContext, String mainformUuid,
                                            Map<String, List<Map<String, Object>>> formDatas, Map<String, List<String>> deletedFormDatas,
                                            Map<String, Map<String, Set<String>>> updatedFormDatas, Map<String, List<String>> addedFormDatas,
                                            DyformDataSignature signature) {
        String formId = repositoryContext.getFormId();
        // 主表数据
        FormData formData = null;
        // 从表数据
        List<FormData> subformDatas = Lists.newArrayList();
        for (Entry<String, List<Map<String, Object>>> entry : formDatas.entrySet()) {
            String formUuid = entry.getKey();
            List<Map<String, Object>> dataList = entry.getValue();
            // 主表
            if (isMainForm(formUuid, mainformUuid)) {
                Map<String, Object> dataMap = Maps.newHashMap();
                if (CollectionUtils.isNotEmpty(dataList)) {
                    dataMap = dataList.get(0);
                }
                formData = extractFormData(formUuid, formId, dataMap, deletedFormDatas, updatedFormDatas,
                        addedFormDatas);
            } else {
                // 从表
                String subformId = repositoryContext.getFormId(formUuid);
                for (Map<String, Object> map : dataList) {
                    subformDatas.add(extractFormData(formUuid, subformId, map, deletedFormDatas, updatedFormDatas,
                            addedFormDatas));
                }

                // 删除数据
                if (MapUtils.isNotEmpty(deletedFormDatas)) {
                    List<String> deleteDataUuids = deletedFormDatas.get(formUuid);
                    if (CollectionUtils.isNotEmpty(deleteDataUuids)) {
                        for (String deleteDataUuid : deleteDataUuids) {
                            FormData deleteFormData = new FormData(subformId, formUuid, deleteDataUuid);
                            deleteFormData.setDelete(true);
                            subformDatas.add(deleteFormData);
                        }
                    }
                }
            }
        }
        // 从表数据
        formData.setSubformDatas(subformDatas);
        return formData;
    }

    /**
     * @param formUuid
     * @param dataMap
     * @param deletedFormDatas
     * @param updatedFormDatas
     * @param addedFormDatas
     * @return
     */
    private static FormData extractFormData(String formUuid, String formId, Map<String, Object> dataMap,
                                            Map<String, List<String>> deletedFormDatas, Map<String, Map<String, Set<String>>> updatedFormDatas,
                                            Map<String, List<String>> addedFormDatas) {
        // 主表数据
        Map<String, Object> mainData = dataMap;
        String dataUuid = ObjectUtils.toString(mainData.get(FormData.KEY_UUID));
        // 是否新增
        boolean isNew = false;
        if (MapUtils.isNotEmpty(addedFormDatas) && addedFormDatas.containsKey(formUuid)
                && addedFormDatas.get(formUuid).contains(dataUuid)) {
            isNew = true;
        }
        // 是否更新
        boolean isUpdate = false;
        Set<String> updatedFields = Sets.newLinkedHashSetWithExpectedSize(0);
        if (MapUtils.isNotEmpty(updatedFormDatas) && updatedFormDatas.containsKey(formUuid)) {
            Map<String, Set<String>> updateFieldMap = updatedFormDatas.get(formUuid);
            Set<String> formDataUpdatedFields = updateFieldMap.get(dataUuid);
            if (CollectionUtils.isNotEmpty(formDataUpdatedFields)) {
                updatedFields = formDataUpdatedFields;
                isUpdate = true;
            }
        }
        // 表单数据
        FormData formData = new FormData(formId, formUuid, dataUuid);
        formData.setDataMap(mainData);
        formData.setNew(isNew);
        formData.setUpdate(isUpdate);
        formData.setUpdatedFields(updatedFields);
        return formData;
    }

    /**
     * @param formUuid
     * @param mainformUuid
     * @return
     */
    private static boolean isMainForm(String formUuid, String mainformUuid) {
        return StringUtils.equals(formUuid, mainformUuid);
    }

    /**
     * @param repositoryContext
     * @param formUuid
     * @param formDatas
     * @param signature
     * @return
     */
    public static FormData convert2FormDataOfUpdateAll(FormRepositoryContext repositoryContext, String formUuid,
                                                       Map<String, List<Map<String, Object>>> formDatas, DyformDataSignature signature) {
        FormData formData = convert2FormData(repositoryContext, formUuid, formDatas, null, null, null, signature);
        formData.setUpdatedFields(Sets.newHashSet(repositoryContext.getFieldNames()));
        return formData;
    }

    /**
     * @param object
     * @param columnMetadata
     * @return
     */
    public static TypedValue convert2ExpectedTypeValue(Object object, ColumnMetadata columnMetadata) {
        TypedValue retVal = null;
        Object value = null;
        String dataType = columnMetadata.getDataType();
        switch (dataType) {
            case "int":
            case "integer":
                if (object instanceof Integer) {
                    value = object;
                } else if (StringUtils.isNotBlank(object.toString())) {
                    value = Integer.valueOf(object.toString());
                }
                retVal = new TypedValue(StandardBasicTypes.INTEGER, value);
                break;
            case "bigint":
                if (object instanceof BigDecimal) {
                    value = object;
                } else if (StringUtils.isNotBlank(object.toString())) {
                    value = BigInteger.valueOf(Long.valueOf(object.toString()));
                }
                retVal = new TypedValue(StandardBasicTypes.BIG_INTEGER, value);
                break;
            case "float":
                if (object instanceof Float) {
                    value = object;
                } else if (StringUtils.isNotBlank(object.toString())) {
                    value = Float.valueOf(object.toString());
                }
                retVal = new TypedValue(StandardBasicTypes.FLOAT, value);
                break;
            case "long":
                if (object instanceof Long) {
                    value = object;
                } else if (StringUtils.isNotBlank(object.toString())) {
                    value = Long.valueOf(object.toString());
                }
                retVal = new TypedValue(StandardBasicTypes.LONG, value);
                break;
            case "number":
                if (object instanceof BigDecimal) {
                    value = object;
                } else if (StringUtils.isNotBlank(object.toString())) {
                    value = BigDecimal.valueOf(Double.valueOf(object.toString()));
                }
                retVal = new TypedValue(StandardBasicTypes.BIG_DECIMAL, value);
                break;
            case "date":
            case "datetime":
            case "timestamp":
                if (object instanceof Date) {
                    value = object;
                } else if (StringUtils.isNotBlank(object.toString())) {
                    try {
                        value = DateUtils.parse(object.toString());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (StringUtils.equals(dataType, "timestamp")) {
                    retVal = new TypedValue(StandardBasicTypes.TIMESTAMP, value);
                } else {
                    retVal = new TypedValue(StandardBasicTypes.DATE, value);
                }
                break;
            case "varchar":
            case "varchar2":
            case "nvarchar2":
            case "clob":
                if (object != null) {
                    value = object.toString();
                } else {
                    value = object;
                }
                if (value instanceof Clob) {
                    retVal = new TypedValue(StandardBasicTypes.CLOB, value);
                } else {
                    retVal = new TypedValue(StandardBasicTypes.STRING, value);
                }
                break;
            case "blob":
                value = object;
                retVal = new TypedValue(StandardBasicTypes.BLOB, value);
                break;
            default:
                value = object;
                retVal = new TypedValue(StandardBasicTypes.SERIALIZABLE, value);
                break;
        }
        return retVal;
    }
}
