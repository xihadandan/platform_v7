package com.wellsoft.pt.ei.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.BUtils;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;
import com.wellsoft.pt.ei.dto.flow.exportNew.FlowExportJsonItemData;
import com.wellsoft.pt.ei.dto.flow.exportNew.FlowInstanceExportData;
import com.wellsoft.pt.ei.processor.utils.ExpImpServiceBeanUtils;
import com.wellsoft.pt.ei.processor.utils.FieldDesc;
import com.wellsoft.pt.ei.service.ExpImpService;
import com.wellsoft.pt.ei.service.impl.FlowExpImpService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public class FlowDataImportUtils {

//    public static String RELATE_FORM_DATAS_KEY = "relateFormDatas";

    public static String TABLE_NAME_KEY = "tableName";

    /**
     * 返回字段名 和 对应值
     *
     * @param map
     * @return
     */
    public static Pair<List<String>, List<String>> getInsertSqlFieldNameAndValueList(Map<String, Object> map) {

        List<String> fieldNameList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();

        for (String fieldName : map.keySet()) {
            if ("attach".equalsIgnoreCase(fieldName)) {
                continue;
            }

            Object obj = map.get(fieldName);

            fieldNameList.add(BUtils.camel2underline(fieldName));

            if (obj == null) {
                valueList.add("NULL");
            } else if (obj instanceof Date) {
                valueList.add("TO_TIMESTAMP('" + DateUtils.formatDateTime((Date) obj) + "', 'SYYYY-MM-DD HH24:MI:SS')");
            } else if (obj instanceof Boolean) {
                if ((Boolean) obj) {
                    valueList.add("1");
                } else {
                    valueList.add("0");
                }

            } else {
                valueList.add("'" + obj.toString() + "'");
            }
        }

        return new Pair<>(fieldNameList, valueList);
    }

    public static List<String> getUpdateFieldNameSqlList(Pair<List<String>, List<String>> flowInstanceFiledNameValuePair) {
        List<String> fieldNameList = flowInstanceFiledNameValuePair.getKey();
        List<String> valueList = flowInstanceFiledNameValuePair.getValue();
        List<String> updateFieldNameSqlList = new ArrayList<>();

        for (int i = 0; i < fieldNameList.size(); i++) {
            String fieldName = fieldNameList.get(i);
            updateFieldNameSqlList.add(fieldName + " = " + valueList.get(i));
        }
        return updateFieldNameSqlList;
    }

    public static Map<String, List<Map<String, Object>>> getFormDatas(FlowExportJsonItemData flowExportJsonItemData, Map<String, String> tableNameFormUuidMap) {
        Map<String, List<Map<String, Object>>> formDatas = new HashMap<>();


        Map<String, Object> dyformData = flowExportJsonItemData.getDyformData();
        String formUuid = tableNameFormUuidMap.get(dyformData.get(TABLE_NAME_KEY).toString());
        dyformData.put("form_uuid", formUuid);
        FlowInstanceExportData flowInstanceExportData = flowExportJsonItemData.getFlowInstance();
        dyformData.put("flow_create_time", flowInstanceExportData.getFlow_create_time());
        dyformData.put("flow_creator_name", flowInstanceExportData.getFlow_creator_name());
        dyformData.put("flow_done_user_name", flowInstanceExportData.getFlow_done_user_name());
        dyformData.put("flow_title", flowInstanceExportData.getFlow_title());
        dyformData.put(EnumSystemField.modify_time.name(), null);// 避免报 FormDataServiceImpl.saveFormData DATA_OUT_OF_DATE

        List<Map<String, Object>> subFormDataList = flowExportJsonItemData.getSubFormDataList();
        for (Map<String, Object> subFormData : subFormDataList) {
            Object tableNameObj = subFormData.get(TABLE_NAME_KEY);
            if (tableNameObj == null || StringUtils.isBlank(tableNameObj.toString())) {
                continue;
            }
            String subFormUuid = tableNameFormUuidMap.get(tableNameObj.toString());
            if (StringUtils.isBlank(subFormUuid)) {
                continue;
            }
            subFormData.put("form_uuid", subFormUuid);
            subFormData.put(EnumSystemField.modify_time.name(), null);// 避免报 FormDataServiceImpl.saveFormData DATA_OUT_OF_DATE

            List<Map<String, Object>> mapList = formDatas.get(subFormUuid);
            if (mapList == null) {
                mapList = new ArrayList<>();
            }
            mapList.add(subFormData);
            formDatas.put(subFormUuid, mapList);
        }

//        if (dyformData.containsKey(FlowDataImportUtils.RELATE_FORM_DATAS_KEY)) {
//            List<Map<String, Object>> subFormDataList = (List<Map<String, Object>>) dyformData.get(FlowDataImportUtils.RELATE_FORM_DATAS_KEY);
//            dyformData.remove(FlowDataImportUtils.RELATE_FORM_DATAS_KEY);
//            for (Map<String, Object> subFormData : subFormDataList) {
//                formDatas.put((String) subFormData.getOrDefault("form_uuid", ""), new ArrayList<>(Collections.singleton(subFormData)));
//            }
//        }
        formDatas.put((String) dyformData.getOrDefault("form_uuid", ""), new ArrayList<>(Collections.singleton(dyformData)));
        return formDatas;
    }

    public static void setSystemUnitIdForImportFlowInstanceExportData(FlowExportJsonItemData flowExportJsonItemData, String systemUnitId) {
//        flowExportJsonItemData.getFlowInstance().setSystemUnitId(systemUnitId);

        Map<String, Object> dyformData = flowExportJsonItemData.getDyformData();
        dyformData.put("system_unit_id", systemUnitId);

        List<Map<String, Object>> subFormDataList = flowExportJsonItemData.getSubFormDataList();
        for (Map<String, Object> subFormDatamap : subFormDataList) {
            subFormDatamap.put("system_unit_id", systemUnitId);
        }

//        if (dyformData.containsKey(RELATE_FORM_DATAS_KEY)) {
//            List<Map<String, Object>> subFormDataMapList = (List<Map<String, Object>>) dyformData.get(RELATE_FORM_DATAS_KEY);
//            for (Map<String, Object> subFormDataMap : subFormDataMapList) {
//                subFormDataMap.put("system_unit_id", systemUnitId);
//            }
//        }
    }

//    public static void handleMapForImportFlowInstanceExportData(FlowExportJsonItemData flowExportJsonItemData) {
//        Map<String, Object> dyformData = flowExportJsonItemData.getDyformData();
//        if (dyformData.containsKey(RELATE_FORM_DATAS_KEY)) {
//            List<Map<String, Object>> subFormDataMapList = new ArrayList<>();
//            List<Object> subFormObjList = (List<Object>) dyformData.get(RELATE_FORM_DATAS_KEY);
//            for (Object subFormObj : subFormObjList) {
//                Map<String, Object> subFormMap = new HashMap<>();
//                if (subFormObj instanceof MorphDynaBean) {
//                    MorphDynaBean subFormMorphDynaBean = (MorphDynaBean) subFormObj;
//                    DynaProperty[] dynaProperties = subFormMorphDynaBean.getDynaClass().getDynaProperties();
//                    for (DynaProperty dynaProperty : dynaProperties) {
//                        String name = dynaProperty.getName();
//                        subFormMap.put(name, subFormMorphDynaBean.get(name));
//                    }
//                }
//                if (subFormObj instanceof Map) {
//                    subFormMap = (Map<String, Object>) subFormObj;
//                }
//                subFormDataMapList.add(subFormMap);
//            }
//            dyformData.put(RELATE_FORM_DATAS_KEY, subFormDataMapList);
//        }
//    }

    //把JavaBean转化为map
    public static Map<String, Object> bean2map(Object bean) throws Exception {
        Map<String, Object> map = new HashMap<>();
        //获取JavaBean的描述器
        BeanInfo b = Introspector.getBeanInfo(bean.getClass(), Object.class);
        //获取属性描述器
        PropertyDescriptor[] pds = b.getPropertyDescriptors();
        //对属性迭代
        for (PropertyDescriptor pd : pds) {
            //属性名称
            String propertyName = pd.getName();
            //属性值,用getter方法获取
            Method m = pd.getReadMethod();
            Object properValue = m.invoke(bean);//用对象执行getter方法获得属性值

            //把属性名-属性值 存到Map中
            map.put(propertyName, properValue);
        }
        return map;
    }

    public static void handleClobAndFileFieldForImportFlowExportJsonItemData(FlowExportJsonItemData flowExportJsonItemData, Map<String, List<Pair<String, String>>> fileFieldMap, FlowDataImportUtils.FlwDescJsonData flwDescJsonData) {
        Map<String, Object> dyformData = flowExportJsonItemData.getDyformData();
        handleClobAndFileFieldItem(fileFieldMap, flwDescJsonData, dyformData, true);

        List<Map<String, Object>> subFormDataList = flowExportJsonItemData.getSubFormDataList();
        for (Map<String, Object> subFormDataMap : subFormDataList) {
            handleClobAndFileFieldItem(fileFieldMap, flwDescJsonData, subFormDataMap, false);
        }

//        if (dyformData.containsKey(FlowDataImportUtils.RELATE_FORM_DATAS_KEY)) {
//            List<Map<String, Object>> subFormDataList = (List<Map<String, Object>>) dyformData.get(FlowDataImportUtils.RELATE_FORM_DATAS_KEY);
//            for (Map<String, Object> subFormDataMap : subFormDataList) {
//                handleClobAndFileFieldItem(fileFieldMap, flwDescJsonData, subFormDataMap);
//            }
//        }
    }


    private static void handleClobAndFileFieldItem(Map<String, List<Pair<String, String>>> fileFieldMap, FlwDescJsonData flwDescJsonData, Map<String, Object> dyformData, boolean isMainForm) {
        String uuid = (String) dyformData.get("uuid");
        FieldDesc fieldDesc = null;
        if (isMainForm) {
            fieldDesc = flwDescJsonData.getDyformDataFieldDescByTableName((String) dyformData.get(FlowDataImportUtils.TABLE_NAME_KEY), true);
        } else {
            fieldDesc = flwDescJsonData.getDyformDataFieldDescByTableName((String) dyformData.get(FlowDataImportUtils.TABLE_NAME_KEY), false);
        }

        List<FieldDesc> fields = fieldDesc.getFields();

        List<Pair<String, String>> filePairList = new ArrayList<>();// uuid {fieldName: filePath}

        for (FieldDesc field : fields) {
            String fieldName = field.getFieldName();
            Object o = dyformData.get(fieldName);
            if (o instanceof Number) {
                dyformData.put(fieldName, o.toString());
            }

            if (ExportFieldTypeEnum.CLOB.getValue().equals(field.getType())) {

                String attachPath = ExpImpServiceBeanUtils.attachPathName(FlowExpImpService.dataImportRecordThreadLocal.get().getImportPath(), ApplicationContextHolder.getBean("flowExpImpService", ExpImpService.class));

                String clobFilePath = attachPath + File.separator + dyformData.get(fieldName);//todo  + clobFilePath
                String clobString = "";
                if (clobFilePath != null) {
                    File file = new File(clobFilePath);
                    if (file.exists()) {
                        try {
                            clobString = FileUtils.readFileToString(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                dyformData.put(fieldName, clobString);
            }

            if (ExportFieldTypeEnum.FILE.getValue().equals(field.getType())) {
                if (o instanceof Collection) {
                    Collection o1 = (Collection) o;
                    for (Iterator iterator = o1.iterator(); iterator.hasNext(); ) {
                        String filePath = (String) iterator.next();
                        filePairList.add(Pair.create(fieldName, filePath));
                    }
                }
                dyformData.put(fieldName, null);
            }
        }
        fileFieldMap.put(uuid, filePairList);
    }


    public static class FlwDescJsonData {

        private FieldDesc flowIntanceFieldDesc;
        //        private FieldDesc taskInstancesFieldDesc;
//        private FieldDesc taskActivitiesFieldDesc;
//        private FieldDesc taskOperationsFieldDesc;
        private List<FieldDesc> dyformDataFieldDescList;
        private List<FieldDesc> subFormDataFieldDescList;

        public FlwDescJsonData(String descJsonFilePath) {
            dyformDataFieldDescList = new ArrayList<>();
            subFormDataFieldDescList = new ArrayList<>();
            String fileToString = null;
            try {
                fileToString = org.apache.commons.io.FileUtils.readFileToString(new File(descJsonFilePath));


                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readValue(fileToString, JsonNode.class);
                for (JsonNode node : jsonNode) {
                    if (node.toString().contains("$ref")) {
                        JsonNode fields = node.get("fields");
                        for (Iterator<JsonNode> iterator = fields.iterator(); iterator.hasNext(); ) {
                            JsonNode next = iterator.next();
                            if (next.has("$ref")) {
                                iterator.remove();
                            }
                        }
                    }

                    FieldDesc fieldDesc = objectMapper.readValue(node.toString(), FieldDesc.class);
//                    FieldDesc fieldDesc = JsonBinder.buildNormalBinder().fromJson(o.toString(), FieldDesc.class);

//                    FieldDesc fieldDesc = JsonBinder.buildNormalBinder().fromJson(o.toString(), FieldDesc.class);
                    if (fieldDesc.getFieldName().equals("flowInstance")) {
                        this.flowIntanceFieldDesc = fieldDesc;
                    }
//                    if (fieldDesc.getFieldName().equals("taskInstances")) {
//                        this.taskInstancesFieldDesc = fieldDesc;
//                    }
//                    if (fieldDesc.getFieldName().equals("taskOperations")) {
//                        this.taskOperationsFieldDesc = fieldDesc;
//                    }
//                    if (fieldDesc.getFieldName().equals("taskActivities")) {
//                        this.taskActivitiesFieldDesc = fieldDesc;
//                    }
                    if (fieldDesc.getFieldName().equals("dyformData")) {
                        if (CollectionUtils.isNotEmpty(fieldDesc.getFields())) {
                            dyformDataFieldDescList.addAll(fieldDesc.getFields());
                        }
                    }
                    if (fieldDesc.getFieldName().equals("subFormDataList")) {
                        if (CollectionUtils.isNotEmpty(fieldDesc.getFields())) {
                            subFormDataFieldDescList.addAll(fieldDesc.getFields());
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public FieldDesc getFlowIntanceFieldDesc() {
            return flowIntanceFieldDesc;
        }

        public FieldDesc getDyformDataFieldDescByTableName(String tableName, boolean isMainFormData) {
            FieldDesc result = new FieldDesc();
            List<FieldDesc> fields;
            if (isMainFormData) {
                fields = this.dyformDataFieldDescList;
            } else {
                fields = this.subFormDataFieldDescList;
            }

            for (FieldDesc field : fields) {
                if (StringUtils.isNotBlank(tableName) && (tableName).equals(field.getFieldName())) {
                    result = field;
                }
            }

            return result;
        }

//        public FieldDesc getDyformDataFieldDescByFormUuid(String formUuid) {
//            FieldDesc result = null;
//
//            List<FieldDesc> fields = subFormDataListFieldDesc.getFields();
//            for (FieldDesc field : fields) {
//                if (field.getFieldName().equals(formUuid)) {
//                    result = field;
//                }
//            }
//
//            return result;
//        }
    }


}
