package com.wellsoft.pt.dyform.implement.data.utils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.io.ClobUtils;
import com.wellsoft.context.util.reflection.ServiceInvokeUtils;
import com.wellsoft.context.util.tree.TreeUtils;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryEntity;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryService;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.material.enums.EnumValidationFlag;
import com.wellsoft.pt.basicdata.material.facade.service.CdMaterialFacadeService;
import com.wellsoft.pt.basicdata.material.support.CdMaterialParams;
import com.wellsoft.pt.basicdata.material.support.CdMaterialParamsBuilder;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberBuildParams;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.enums.EnumValidateCode;
import com.wellsoft.pt.dyform.implement.data.enums.SaveDataErrorCode;
import com.wellsoft.pt.dyform.implement.data.exceptions.SaveDataException;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumDySysVariable;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFieldPropertyName;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFormPropertyName;
import com.wellsoft.pt.dyform.implement.definition.dto.FieldDefinition;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.DyformDataOptions;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.dyform.implement.definition.validator.*;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormDataHandler {
    private static final ThreadLocal<Boolean> FORM_DATA_UPDATED = new ThreadLocal<Boolean>();
    private static final ThreadLocal<Map<String, Set<String>>> NOT_VALIDATE_FIELDS = new ThreadLocal<>();
    private static Logger logger = Logger.getLogger(FormDataHandler.class);
    private final String substitution = "S_U_B_S_T_I_T_U_T_I_O_N";
    DyFormFacade dyFormFacade = (DyFormFacade) ApplicationContextHolder.getBean(DyFormFacade.class);
    private Boolean mongodbFolderExist;
    private FormDefinitionHandler dUtils;
    private Map<String /* 字段名称 */, Set<Validator> /* 校验器 */> customValidators = null;
    private Map<String, Object> formDataColMap = null;
    private DyformDataOptions dyformDataOptions;
    private MongoFileService mongoFileService = (MongoFileService) ApplicationContextHolder.getBean("mongoFileService");

    public FormDataHandler(String formUuid, Map<String, Object> formDataColMap) {
        this(formUuid, formDataColMap, null);
    }

    public FormDataHandler(String formUuid, Map<String, Object> formDataColMap, FormDefinitionHandler dUtils) {
        this(formUuid, formDataColMap, dUtils, null);
    }

    public FormDataHandler(String formUuid, Map<String, Object> formDataColMap, FormDefinitionHandler dUtils,
                           DyformDataOptions dyformDataOptions) {
        this.formDataColMap = formDataColMap;
        this.dyformDataOptions = dyformDataOptions == null ? new DyformDataOptions() : dyformDataOptions;
        this.dUtils = dUtils == null
                ? ((FormDefinition) this.dyFormFacade.getFormDefinition(formUuid)).doGetFormDefinitionHandler()
                : dUtils;
    }

    /**
     * 处理从formdata中由于大小写问题取不到值
     *
     * @param fieldName
     * @param formDatas
     * @return
     */
    public static Object getValueFromMap(String fieldName, Map<String, Object> formDatas) {
        Object value = formDatas.get(fieldName);
        if (value == null) {// 可能大小写问题造成找不到对应的字段值
            value = formDatas.get(fieldName.toLowerCase());
            if (value == null) {
                value = formDatas.get(fieldName.toUpperCase());
            }
        }
        return value;
    }

    private static List<Object> getFiles(Object value) {
        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof String) {
            return Collections.emptyList();
        }
        return (List<Object>) value;
    }

    /**
     * 获取从前台统一上传控件上传过来的文件ID列表
     *
     * @return
     */
    public static List<String> getFileIds(Object value) {
        List<Object> files = getFiles(value);// 获取从前台传送过来的文件列表信息
        if (files == null) {
            return Lists.newArrayListWithCapacity(0);
        }

        List<String> fileIDs = new ArrayList<String>();
        for (Object obj : files) {
            if (obj instanceof LogicFileInfo) {
                LogicFileInfo file = (LogicFileInfo) obj;
                // bug#60615
                if (StringUtils.isNotBlank(file.getFileID())) {
                    fileIDs.add(file.getFileID());
                }
            } else {
                Map<String, String> fileInfo = (Map<String, String>) obj;
                // bug#60615
                if (StringUtils.isNotBlank(fileInfo.get("fileID"))) {
                    fileIDs.add(fileInfo.get("fileID"));
                }
            }
        }
        return fileIDs;
    }

    /**
     * 获取从前台统一上传控件上传过来的引用我的材料文件ID列表
     */
    public static List<String> getRefMyMaterialFileIds(Object value) {
        List<Object> files = getFiles(value);// 获取从前台传送过来的文件列表信息
        if (files == null) {
            return Lists.newArrayListWithCapacity(0);
        }

        List<String> fileIDs = new ArrayList<String>();
        for (Object obj : files) {
            if (obj instanceof LogicFileInfo) {
                LogicFileInfo file = (LogicFileInfo) obj;
                if (StringUtils.equals("引用我的材料", file.getSource())) {
                    fileIDs.add(file.getFileID());
                }
            } else {
                Map<String, String> fileInfo = (Map<String, String>) obj;
                if (StringUtils.equals("引用我的材料", fileInfo.get("source"))) {
                    fileIDs.add(fileInfo.get("fileID"));
                }
            }
        }
        return fileIDs;
    }

    public static Object convertData2DbType(String dbDataType, Object data, Object... paramters) throws ParseException {

        if (data == null) {// 如果值为空则返回默认值
            if (paramters != null && paramters.length > 0) {
                data = paramters[0];// 如果值为空则使用默认值
            } else {
                return null;
            }
        }

        if (data instanceof String && !DyFormConfig.DbDataTypeUtils.isDbDataTypeEqString(dbDataType)
                && ((String) data).trim().length() == 0) {// 对于空字符串的处理
            /*
             * if (DyFormConfig.DbDataTypeUtils.isDbDataTypeAsNumber(dbDataType)) { if
             * (paramters != null && paramters.length > 0) { data = paramters[0]; if() }
             * else { return null; } } else if
             * (DyFormConfig.DbDataTypeUtils.isDbDataTypeAsNumber(dbDataType)) { return
             * null; } else { return null; }
             */
            return null;

        }

        if (DyFormConfig.DbDataTypeUtils.isDbDataTypeEqDate(dbDataType)) {
            return convertData2Date(data, paramters);
        } else if (DyFormConfig.DbDataTypeUtils.isDbDataTypeEqFloat(dbDataType)) {
            return convertData2Float(data);
        } else if (DyFormConfig.DbDataTypeUtils.isDbDataTypeEqDouble(dbDataType)) {
            return convertData2Double(data);
        } else if (DyFormConfig.DbDataTypeUtils.isDbDataTypeEqInt(dbDataType)) {
            return convertData2Int(data);
        } else if (DyFormConfig.DbDataTypeUtils.isDbDataTypeEqLong(dbDataType)) {
            return convertData2Long(data);
        } else if (DyFormConfig.DbDataTypeUtils.isDbDataTypeAsString(dbDataType)) {
            return convertData2String(data);
        } else {

        }
        return data;
    }

    private static Object convertData2String(Object data) {
        String datePattern = "yyyy-MM-dd HH:mm:ss";
        if (data instanceof BigDecimal) {
            return ((BigDecimal) data).toString();
        } else if (data instanceof String) {
            return data;
        } else if (data instanceof Integer) {
            return Integer.toString((Integer) data);
        } else if (data instanceof Long) {
            return Long.toString((Long) data);
        } else if (data instanceof Float) {
            return Float.toString((Float) data);
        } else if (data instanceof Date || data instanceof java.sql.Date) {
            SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
            sdf.format(data);
        } else if (data instanceof Clob) {
            return ClobUtils.ClobToString((Clob) data);
        }
        return data.toString();

    }

    private static Object convertData2Long(Object data) {
        if (data instanceof BigDecimal) {
            return ((BigDecimal) data).intValue();
        } else if (data instanceof String) {
            return (new BigDecimal((String) data)).longValue();// Long.parseLong((String) data);
        } else if (data instanceof Integer) {
            return Long.parseLong(Integer.toString((Integer) data));
        } else if (data instanceof Long) {
            return data;
        } else if (data instanceof Double) {
            return (long) ((Double) data).doubleValue();
        } else if (data instanceof Float) {
            return (long) ((Float) data).floatValue();
        }
        return data;

    }

    private static Object convertData2Int(Object data) {
        if (data instanceof BigDecimal) {
            return ((BigDecimal) data).intValue();
        } else if (data instanceof String) {
            return (new BigDecimal((String) data)).intValue();// Integer.parseInt((String) data);
        } else if (data instanceof Integer) {
            return data;
        } else if (data instanceof Long) {
            return Integer.parseInt(Long.toString((Long) data));
        } else if (data instanceof Double) {
            return (int) ((Double) data).doubleValue();
        } else if (data instanceof Float) {
            return (int) ((Float) data).floatValue();
        }
        return data;

    }

    public static void main(String[] args) {
        Double d = 2.3;
        System.out.println((int) d.doubleValue());

        System.out.println(Float.valueOf("1234567890123.01"));
        System.out.println(Float.valueOf("1234567890123"));
        System.out.println(Double.valueOf("1234567890123"));
    }

    private static Object convertData2Float(Object data) {
        if (data instanceof BigDecimal) {
            return ((BigDecimal) data).floatValue();
        } else if (data instanceof String) {
            return (new BigDecimal((String) data)).floatValue();// Float.parseFloat((String) data);
        } else if (data instanceof Float) {
            return data;
        } else if (data instanceof Integer) {
            return Float.parseFloat(Integer.toString((Integer) data));
        } else if (data instanceof Long) {
            return Float.parseFloat(Long.toString((Long) data));
        } else if (data instanceof Double) {
            return (float) ((Double) data).doubleValue();
        }
        return data;

    }

    private static Object convertData2Double(Object data) {
        if (data instanceof BigDecimal) {
            return ((BigDecimal) data).doubleValue();
        } else if (data instanceof String) {
            return (new BigDecimal((String) data)).doubleValue();// Double.parseDouble((String) data);
        } else if (data instanceof Double) {
            return data;
        } else if (data instanceof Integer) {
            return Double.parseDouble(Integer.toString((Integer) data));
        } else if (data instanceof Long) {
            return Double.parseDouble(Long.toString((Long) data));
        } else if (data instanceof Float) {
            return Double.parseDouble(Float.toString((Float) data));
        }
        return data;
    }

    private static Object convertData2Date(Object data, Object... parameters) throws ParseException {
        String pattern1 = "yyyy-MM-dd HH:mm:ss";
        String pattern2 = "yyyy-MM-dd HH:mm";
        String pattern3 = "yyyy-MM-dd HH";
        String pattern4 = "yyyy-MM-dd";
        String pattern8 = "yyyy-MM";
        String pattern9 = "yyyy";
        String pattern5 = "HH:mm:ss";
        String pattern6 = "HH:mm";
        String pattern7 = "HH";
        String pattern10 = "yyyy-M-d";
        String pattern11 = "yyyy年M月d日";

        if (data instanceof Date || data instanceof java.sql.Date) {
            return data;
        } else if (data instanceof String) {
            SimpleDateFormat sdf = new SimpleDateFormat();
            String pattern = "";
            if (parameters == null || parameters.length < 2) {
                pattern = pattern1;
            } else {
                pattern = (String) parameters[1];
            }
            //            DateUtils.parseDate((String) data, Locale.CHINESE)
            try {
                if (pattern.indexOf("ww") != -1) {
                    // 周计算日期，要按照 ISO 8601 标准定义周（一周从星期一开始算)
                    // Locale 只能用欧洲的，用中国的由于 java 历史版本问题也不是按上述标准计算
                    sdf = new SimpleDateFormat("", Locale.FRANCE);
                }
                sdf.applyPattern(pattern);
                Date date = sdf.parse((String) data);
                return date;
            } catch (ParseException e) {
                try {
                    sdf.applyPattern(pattern1);
                    Date date = sdf.parse((String) data);
                    return date;
                } catch (ParseException e1) {
                    try {
                        sdf.applyPattern(pattern2);
                        Date date = sdf.parse((String) data);
                        return date;
                    } catch (ParseException e2) {
                        try {
                            sdf.applyPattern(pattern3);
                            Date date = sdf.parse((String) data);
                            return date;
                        } catch (ParseException e3) {
                            try {
                                sdf.applyPattern(pattern4);
                                Date date = sdf.parse((String) data);
                                return date;
                            } catch (ParseException e4) {
                                try {
                                    sdf.applyPattern(pattern8);
                                    Date date = sdf.parse((String) data);
                                    return date;
                                } catch (ParseException e8) {
                                    try {
                                        sdf.applyPattern(pattern9);
                                        Date date = sdf.parse((String) data);
                                        return date;
                                    } catch (ParseException e9) {
                                        try {
                                            sdf.applyPattern(pattern10);
                                            Date date = sdf.parse((String) data);
                                            return date;
                                        } catch (ParseException e10) {
                                            try {
                                                sdf.applyPattern(pattern11);
                                                Date date = sdf.parse((String) data);
                                                return date;
                                            } catch (ParseException e11) {
                                                throw e11;
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
            }
        } else {
            return data;
        }
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Object> sortMap = new TreeMap<String, Object>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    public static void setUpdated() {
        FORM_DATA_UPDATED.set(true);
    }

    public static boolean isUpdated() {
        boolean ans = BooleanUtils.isTrue(FORM_DATA_UPDATED.get());
        FORM_DATA_UPDATED.remove();
        return ans;
    }

    /**
     * @param formId
     * @param field
     */
    public static void addNotValidateField(String formId, String field) {
        Map<String, Set<String>> formFieldMap = NOT_VALIDATE_FIELDS.get();
        if (formFieldMap == null) {
            formFieldMap = Maps.newHashMap();
            NOT_VALIDATE_FIELDS.set(formFieldMap);
        }
        Set<String> fields = formFieldMap.get(formId);
        if (fields == null) {
            fields = Sets.newHashSet();
            formFieldMap.put(formId, fields);
        }
        fields.add(field);
    }

    /**
     * @param formId
     */
    public static Set<String> getNotValidateFields(String formId) {
        Map<String, Set<String>> formFieldMap = NOT_VALIDATE_FIELDS.get();
        if (formFieldMap == null) {
            return Collections.emptySet();
        }
        Set<String> fields = formFieldMap.get(formId);
        return fields != null ? fields : Collections.emptySet();
    }

    /**
     * @param formId
     */
    public static void removeNotValidateField(String formId) {
        Map<String, Set<String>> formFieldMap = NOT_VALIDATE_FIELDS.get();
        if (formFieldMap != null) {
            formFieldMap.remove(formId);
            if (MapUtils.isEmpty(formFieldMap)) {
                NOT_VALIDATE_FIELDS.remove();
            }
        }
    }

    /**
     * 数据验证
     */
    /*
     * public EnumValidateCode validate(String fieldName) { Object valueObj =
     * getValueFromMap(fieldName, this.formDataColMap); if
     * (dUtils.isInputModeEqNumber(fieldName)) { String value = (String) valueObj;
     * try { Double.parseDouble(value); } catch (NumberFormatException e) { throw
     * new NumberFormatException(fieldName + "=" + value); } } else if
     * (dUtils.isDbDataTypeEqDate(fieldName)) { if (!this.isDate((String) valueObj))
     * { return EnumValidateCode.INVALID_FORMAT_DATE; } } //if
     * (!StringUtils.isNumeric(value)) { // throw new
     * NumberFormatException(fieldName + "=" + value); //} return
     * EnumValidateCode.SUCESS; }
     */

    /**
     * @param mapValidators 要设置的mapValidators
     */
    public void setCustomValidators(Map<String, Set<Validator>> mapValidators) {
        this.customValidators = mapValidators;
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void info(String msg) {
        logger.info(msg);
    }

    private void processFileField(String fieldName) throws UnsupportedEncodingException {
        // debug("开始处理字段fieldName:" + fieldName + "中的附件");
        Object fileInfoObjs = getValueFromMap(fieldName, this.formDataColMap);

        // debug("字段fieldName:" + fieldName + "中的附件----> " + fileInfoObjs == null ?
        // "附件字段的值为空" : "附件字段有值");
        String dataUuid = (String) getValueFromMap(EnumFormPropertyName.uuid.name(), this.formDataColMap);

        // debug("开始处理 字段fieldName:" + fieldName + "中的附件--->dataUuid=" + dataUuid);

        List<String> fileIds = null;
        if (fileInfoObjs == null) {
            fileIds = new ArrayList<String>();
        } else {
            fileIds = getFileIds(fileInfoObjs);
        }

        // debug("开始处理 字段fieldName:" + fieldName + "中的附件--->dataUuid=" + dataUuid + "，
        // 附件个数为:" + fileIds.size());

        fileOperate(dataUuid, fieldName, fileIds);
        // /putValue(fieldName, null);
        // debug("处理 字段fieldName:" + fieldName + "中的附件--->dataUuid=" + dataUuid +
        // "处理完成");

    }

    private void fileOperate(String folderID, String fieldName, List<String> fileIDListFromPage)
            throws UnsupportedEncodingException {
        // String loggerPrefixMsg = "表单dataUuid=" + folderID + " 字段名称为:" + fieldName +
        // ":";
        List<String> newFileIDList = new ArrayList<String>(); // 该列表中的fileID都是要push到数据库里面的
        if (null == mongodbFolderExist) {
            mongodbFolderExist = mongoFileService.isFolderExist(folderID);
        }

        if (!mongodbFolderExist) {// 新增
            // debug(loggerPrefixMsg + " 新增--->附件个数为" + fileIDListFromPage.size());
            newFileIDList.addAll(fileIDListFromPage);
            // 生成业务材料
            generateMaterialIfRequired(folderID, fieldName, newFileIDList, false);
        } else {// 更新
            // debug(loggerPrefixMsg + " 更新--->附件个数为" + fileIDListFromPage.size());
            List<LogicFileInfo> dbFiles = mongoFileService.getNonioFilesFromFolder(folderID, fieldName);// 取出存放在数据库里面的文件
            // 这里不用mongoFileService.getFilesFromFolder(folderID,
            // fieldName)这个方法，该方法在实体附件不存在时会返回null值

            if (dbFiles == null) {
                dbFiles = new ArrayList<LogicFileInfo>();
            }
            // debug(loggerPrefixMsg + " 更新--->原有附件个数为" + dbFiles.size());

            Iterator<LogicFileInfo> it1 = dbFiles.iterator();
            while (it1.hasNext()) {
                LogicFileInfo dbFile = it1.next();
                String dbFileID = dbFile.getFileID();
                boolean isExist = false;
                Iterator<String> it2 = fileIDListFromPage.iterator();
                while (it2.hasNext()) {
                    String fileId = it2.next();
                    if (dbFileID.equals(fileId)) {// 如果该文件在数据库中已存在,则不用进行再处理
                        it2.remove();
                        isExist = true;
                        break;
                    }
                }

                if (!isExist) {// 如果从页面传进来的文件没有该文件，则表示该文件是要被删除的
                    // debug(loggerPrefixMsg + " 更新--->删除旧文件: " + dbFileID);
                    mongoFileService.popFileFromFolder(folderID, dbFileID);
                }
            }
            newFileIDList.addAll(fileIDListFromPage);

            // 生成业务材料
            generateMaterialIfRequired(folderID, fieldName, newFileIDList, true);
        }

        // 将要保存到数据库的文件push到数据 库中
        if (newFileIDList.size() > 0) {
            mongoFileService.pushFilesToFolder(folderID, newFileIDList, fieldName);
        }

        /* add by zhangyh 20170411 begin */
        // 是否保存文件名称
        Boolean saveFileName2Field = dUtils.getFieldPropertyOfBooleanType(fieldName,
                EnumFieldPropertyName.saveFileName2Field);
        if (saveFileName2Field != null && saveFileName2Field) {
            List<String> newFileNameList = new ArrayList<String>();
            // 取出存放在数据库里面的文件
            List<MongoFileEntity> dbFiles = mongoFileService.getFilesFromFolder(folderID, fieldName);
            if (dbFiles == null) {
                dbFiles = new ArrayList<MongoFileEntity>();
            }
            Iterator<MongoFileEntity> it1 = dbFiles.iterator();
            while (it1.hasNext()) {
                MongoFileEntity dbFile = it1.next();
                newFileNameList.add(dbFile.getFileName());
            }
            // 附件的名称以分号隔开保存到该附件字段,大于4000的字符全部舍掉
            String fileNames = StringUtils.join(newFileNameList, ";");
            fileNames = StringUtils.abbreviate(fileNames, 1999);
            this.putValue(fieldName, fileNames);
        }
        /* add by zhangyh 20170411 end */
    }

    /**
     * 生成业务材料
     *
     * @param folderID
     * @param fieldName
     * @param fileIDListFromPage
     * @param isUpdate
     */
    private void generateMaterialIfRequired(String folderID, String fieldName, List<String> fileIDListFromPage, boolean isUpdate) {
        if (CollectionUtils.isEmpty(fileIDListFromPage)) {
            logger.warn("新增或更新的附件为空，不生成材料信息！");
            return;
        }

        List<String> fileIds = Lists.newArrayList(fileIDListFromPage);
        CdMaterialParams materialParams = createMaterialParams(folderID, fieldName, fileIds, isUpdate);
        if (materialParams == null) {
            return;
        }

        materialParams.setUpdate(isUpdate);
        CdMaterialFacadeService materialFacadeService = ApplicationContextHolder.getBean(CdMaterialFacadeService.class);
        materialFacadeService.generateMaterial(materialParams);
    }

    /**
     * 创建业务材料参数
     *
     * @param folderID
     * @param fieldName
     * @param fileIds
     * @return
     */
    private CdMaterialParams createMaterialParams(String folderID, String fieldName, List<String> fileIds,
                                                  boolean isUpdate) {
        JSONObject relatedMaterialJson = getRelatedMaterialJson(fieldName);
        if (relatedMaterialJson == null) {
            return null;
        }
        if (BooleanUtils.isNotTrue(relatedMaterialJson.getBoolean("enabled"))) {
            return null;
        }

        // 排除引用我的材料的附件
        Object fileInfoObjs = getValueFromMap(fieldName, this.formDataColMap);
        List<String> refMyMaterialFileIds = getRefMyMaterialFileIds(fileInfoObjs);
        fileIds.removeAll(refMyMaterialFileIds);
        if (CollectionUtils.isEmpty(fileIds)) {
            return null;
        }

        // 材料生成方式
        String generateWay = relatedMaterialJson.getString("way");
        String ownerType = relatedMaterialJson.getString("ownerType");
        List<String> materialCodes = Lists.newArrayList();
        List<String> ownerIds = Lists.newArrayList();
        if ("1".equals(generateWay)) {
            JSONArray jsonArray = relatedMaterialJson.getJSONArray("materialCodes");
            for (int index = 0; index < jsonArray.length(); index++) {
                materialCodes.add(jsonArray.getString(index));
            }
        } else {
            JSONArray jsonArray = relatedMaterialJson.getJSONArray("materialCodeFields");
            for (int index = 0; index < jsonArray.length(); index++) {
                String materialCodeField = jsonArray.getString(index);
                if (StringUtils.isBlank(materialCodeField)) {
                    continue;
                }
                String materialCodeValue = Objects.toString(this.formDataColMap.get(materialCodeField), StringUtils.EMPTY);
                if (StringUtils.isNotBlank(materialCodeValue)) {
                    materialCodes.add(materialCodeValue);
                }
            }
        }
        // 材料所有者
        if ("1".equals(ownerType)) {
            ownerIds.add(SpringSecurityUtils.getCurrentUserId());
        } else {
            JSONArray jsonArray = relatedMaterialJson.getJSONArray("ownerField");
            for (int index = 0; index < jsonArray.length(); index++) {
                String ownerField = jsonArray.getString(index);
                if (StringUtils.isBlank(ownerField)) {
                    continue;
                }
                String ownerValue = Objects.toString(this.formDataColMap.get(ownerField), StringUtils.EMPTY);
                if (StringUtils.isNotBlank(ownerValue)) {
                    ownerIds.add(ownerValue);
                }
            }
        }

        return CdMaterialParamsBuilder.create(materialCodes).bizType("dyform").bizId(this.dUtils.getFormId())
                .dataUuid(folderID).purpose(fieldName).ownerIds(ownerIds).repoFileUuids(fileIds)
                .validationFlag(EnumValidationFlag.Valid.getValue()).update(isUpdate).build();
    }

    /**
     * 获取字段关联材料配置
     *
     * @param fieldName
     * @return
     */
    private JSONObject getRelatedMaterialJson(String fieldName) {
        JSONObject fieldDefinitionJson = dUtils.getFieldDefinitionJson(fieldName);
        if (!fieldDefinitionJson.has("configuration")) {
            return null;
        }
        JSONObject configurationJson = fieldDefinitionJson.getJSONObject("configuration");
        if (!configurationJson.has("relatedMaterial")) {
            return null;
        }
        return configurationJson.getJSONObject("relatedMaterial");
    }

    private void parseDateField(String fieldName) throws ParseException {
        String datePattern = dUtils.getDateTimePatternByFieldName(fieldName);
        Object value = getValueFromMap(fieldName, this.formDataColMap);
        putValue(fieldName,
                convertData2DbType(dUtils.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.dbDataType),
                        value, null, datePattern));
    }

    private void parseNumberField(String fieldName) throws ParseException {
        Object value = getValueFromMap(fieldName, this.formDataColMap);
        String defaultValue = dUtils.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.defaultValue);
        /*
         * if (defaultValue == null || defaultValue.trim().length() == 0) { defaultValue
         * = "0"; }
         */
        this.putValue(fieldName, convertData2DbType(
                dUtils.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.dbDataType), value, defaultValue));
    }

    private void parseSerialNumberField(String fieldName) throws ParseException {
        Object value = getFieldDataOptions(fieldName, "serialNumberConfirm");
        if (value == null || StringUtils.isBlank(value.toString())) {
            value = getFieldDataOptions(fieldName.toLowerCase(), "serialNumberConfirm");
        }
        if (value == null || StringUtils.isBlank(value.toString())) {
            value = getFieldDataOptions(fieldName.toUpperCase(), "serialNumberConfirm");
        }
        if (value == null || StringUtils.isBlank(value.toString())) {
            value = dyformDataOptions.getOptions(getFormUuid() + ".undefined." + fieldName + ".serialNumberConfirm");
        }
        if (value == null || StringUtils.isBlank(value.toString())) {
            value = dyformDataOptions.getOptions(getFormUuid() + ".undefined." + fieldName.toLowerCase() + ".serialNumberConfirm");
        }
        if (value == null || StringUtils.isBlank(value.toString())) {
            value = dyformDataOptions.getOptions(getFormUuid() + ".undefined." + fieldName.toUpperCase() + ".serialNumberConfirm");
        }
        if (value == null || StringUtils.isBlank(value.toString())) {
            return;
        }
        // 保存时占用流水号
        try {
            JSONObject valueObj = new JSONObject(value.toString());
            value = valueObj.getString("value");
            boolean occupied = valueObj.getBoolean("occupied");
            boolean automaticNumberSupplement = valueObj.getBoolean("automaticNumberSupplement");
            String serialNoDefId = valueObj.getString("snid");// sn流水号名称，snid流水号ID，snm流水号维护记录
            BasicDataApiFacade serialNumberService = ApplicationContextHolder.getBean(BasicDataApiFacade.class);

            SerialNumberBuildParams params = new SerialNumberBuildParams();
            params.setSerialNumberId(serialNoDefId);
            //流水号重复返回新值提示框点击确定标记需要更新指针 或 前端已占用指针，后端不占用指针
            params.setOccupied(dyformDataOptions.isSerialNumberConfirmed(fieldName) || !occupied);
            params.setAutomaticNumberSupplement(automaticNumberSupplement);
            params.setIsBackEnd(true);
            params.setFormField(fieldName);
            params.setTableName(dUtils.getTblNameOfMainform());
            params.setDataUuid(this.getDataUuid());
            params.setDefaultTransaction(true);
            params.setFormUuid(dUtils.getFormUuid());
            params.setUuid(valueObj.has("uuid") ? valueObj.getString("uuid") : StringUtils.EMPTY);
            params.setSnValue(value.toString());
            params.setRecordUuid(valueObj.has("recordUuid") ? valueObj.getString("recordUuid") : StringUtils.EMPTY);
            String occupiedPointer = valueObj.has("occupiedPointer") ? Objects.toString(valueObj.get("occupiedPointer"), StringUtils.EMPTY) : StringUtils.EMPTY;
            if (StringUtils.isNotBlank(occupiedPointer)) {
                params.setOccupiedPointer(Long.valueOf(occupiedPointer));
            }
            String serialNo = serialNumberService.generateSerialNumber(params);
            if (dyformDataOptions.isSerialNumberConfirmed(fieldName)) {
                // 保存时生成流水号
            } else if (value != null && false == StringUtils.equals(serialNo, value.toString())) {
                String displayName = dUtils.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.displayName);
                Map<String, Object> variables = Maps.newHashMap();
                // variables.put("title", "字段[" + fieldName + "]流水号占用");
                variables.put("title", "流水号已经被占用，系统已为您获取最新流水号");
                variables.put("oldValue", value);
                variables.put("newValue", serialNo);
                variables.put("fieldName", fieldName);
                variables.put("formUuid", getFormUuid());
                variables.put("dataUuid", getDataUuid());
                variables.put("displayName", displayName);
                throw new SaveDataException(SaveDataErrorCode.SerialNumberOccupy, variables);
            }
            String dbDataType = dUtils.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.dbDataType);
            value = convertData2DbType(dbDataType, serialNo);
            this.putValue(fieldName, value);
        } catch (SaveDataException e) {
            logger.error(e.getMessage(), e);
            throw e;// 对外传播
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void parseStringField(String fieldName) throws ParseException {
        Object value = getValueFromMap(fieldName, this.formDataColMap);
        this.putValue(fieldName, convertData2DbType(
                dUtils.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.dbDataType), value));
    }

    public void doProcessValueCreateBySystem(String fieldName) {
        if (this.dUtils.isInputModeEqDate(fieldName)) {
            String defaultValue = dUtils.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.defaultValue);
            Date date = new Date();
            if (StringUtils.isNotBlank(defaultValue)) {
                try {
                    if (StringUtils.equals(EnumDySysVariable.currentDateTimeSec.getKey(), defaultValue)) {
                        date = Calendar.getInstance().getTime();
                    } else {
                        date = (Date) convertData2Date(defaultValue);
                    }

                    // 仅创建保存时候增加日期偏移量计算（显示计算偏移量会在前端计算）
                    if (dUtils.isValueCreateBySystemWhenSave(fieldName)) {
                        JSONObject configuration = dUtils.getFieldPropertyOfJSONType(fieldName, "configuration");
                        if (configuration != null && configuration.has("defaultValueSetting")) {
                            JSONObject defaultValueSetting = configuration.getJSONObject("defaultValueSetting");
                            if (defaultValueSetting != null) {
                                int offset = defaultValueSetting.optInt("offset");
                                String offsetUnit = defaultValueSetting.optString("offsetUnit");
                                String offsetType = defaultValueSetting.optString("offsetType");
                                if ("naturalDay".equals(offsetType)) {
                                    if ("year".equalsIgnoreCase(offsetUnit)) {
                                        date = DateUtils.addYears(date, offset);
                                    } else if ("month".equalsIgnoreCase(offsetUnit)) {
                                        date = DateUtils.addMonths(date, offset);
                                    } else if ("day".equalsIgnoreCase(offsetUnit)) {
                                        date = DateUtils.addDays(date, offset);
                                    } else if ("hour".equalsIgnoreCase(offsetUnit)) {
                                        date = DateUtils.addHours(date, offset);
                                    } else if ("minute".equalsIgnoreCase(offsetUnit)) {
                                        date = DateUtils.addMinutes(date, offset);
                                    }
                                } else {
                                    // 按工作日计算
                                    TsWorkTimePlanFacadeService tsWorkTimePlanFacadeService = ApplicationContextHolder.getBean(TsWorkTimePlanFacadeService.class);
                                    WorkUnit workUnit = null;
                                    if ("day".equalsIgnoreCase(offsetUnit)) {
                                        workUnit = WorkUnit.WorkingDay;
                                    } else if ("hour".equalsIgnoreCase(offsetUnit)) {
                                        workUnit = WorkUnit.WorkingHour;
                                    } else if ("minute".equalsIgnoreCase(offsetUnit)) {
                                        workUnit = WorkUnit.WorkingMinute;
                                    }
                                    date = tsWorkTimePlanFacadeService.getWorkDate(null, date, offset, workUnit, true);
                                }

                            }
                        }
                    }

                } catch (Exception e) {
                    logger.error("日期默认值转换异常：", e);
                }
            }
            putValue(fieldName, date);
        } else if (this.dUtils.isInputModeEqText(fieldName) || this.dUtils.isInputModeEqOrg(fieldName)) {
            // String valueObj = (String) formDataColMap.get(fieldName);
            String value = this.replaceSystemVariable(fieldName);
            putValue(fieldName, value);
        }
    }

    private String replaceSystemVariable(String fieldName) {
        String defaultValue = dUtils.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.defaultValue);
        if (StringUtils.isBlank(defaultValue)) {
            return defaultValue;
        }
        Pattern p = Pattern.compile("\\{(\\w+)\\}");
        Matcher matcher = p.matcher(defaultValue);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            EnumDySysVariable systeVar = EnumDySysVariable.key2EnumObj("{" + matcher.group(1) + "}");
            if (systeVar != null) {
                matcher.appendReplacement(result, StringUtils.isBlank(systeVar.getValue()) ? "" : systeVar.getValue());
            }
        }
        matcher.appendTail(result); // 添加剩余部分
        return result.toString();
    }

    public void doProcessValueCreateByUser(String fieldName) throws ParseException, UnsupportedEncodingException {
        debug("开始处理字段值----> fieldName:" + fieldName);
        if (dUtils.isInputModeEqAttach(fieldName)) {// 附件字段
            debug("开始处理字段值----> fieldName:" + fieldName + "--->附件字段");
            this.processFileField(fieldName);// 处理文件字段

        } else if (dUtils.isInputModeEqDate(fieldName)) {// 日期字段
            this.parseDateField(fieldName);
        } else if (dUtils.isInputModeEqText(fieldName)) {// 文本字段
            this.parseStringField(fieldName);
        } else if (dUtils.isInputModeEqNumber(fieldName)) {
            this.parseNumberField(fieldName);
        } else if (dUtils.isInputModeEqSerialNumber(fieldName)) {
            this.parseSerialNumberField(fieldName);
        }
        debug("处理字段值----> fieldName:" + fieldName + "--->完成");
    }

    public void doProcessValueFileField(String fieldName) {
        if (dUtils.isInputModeEqAttach(fieldName)) {// 附件字段
            Object fileInfoObjs = getValueFromMap(fieldName, this.formDataColMap);
            String dataUuid = (String) getValueFromMap(EnumFormPropertyName.uuid.name(), this.formDataColMap);
            mongoFileService.saveFilesToFolderSort(dataUuid, getFileIds(fileInfoObjs), fieldName);
            mongoFileService.saveFileNames(dataUuid, getFiles(fileInfoObjs), fieldName);
        }
    }

    /**
     * 设置指定字段的值
     *
     * @param fieldName
     * @param data
     */
    public void setValue(String fieldName, Object data) {
        this.putValue(fieldName, data);
    }

    public String putValue(String fieldName, Object value) {
        if (formDataColMap.containsKey(fieldName)) {
            // 未转换
        } else if (formDataColMap.containsKey(fieldName.toLowerCase())) {
            fieldName = fieldName.toLowerCase();
        } else if (formDataColMap.containsKey(fieldName.toUpperCase())) {
            fieldName = fieldName.toUpperCase();
        }
        formDataColMap.put(fieldName, value);
        return fieldName;
    }

    public boolean containsKey(String fieldName) {
        return formDataColMap.containsKey(fieldName);
    }

    public void setValueByApplyTo(String applyTo, Object data) {
        List<String> fieldNames = this.dUtils.getFieldNameByApplyTo(applyTo);
        for (String fieldName : fieldNames) {
            this.setValue(fieldName, data);
        }
    }

    /**
     * 获取指定的字段的值
     *
     * @param fieldName
     * @return
     */
    public Object getValue(String fieldName) {
        Object v = getValueFromMap(fieldName, this.formDataColMap);
        return v;
    }

    /**
     * 获取指定的字段的值
     *
     * @param fieldName
     * @return
     */
    public List<String> getValueOfFileIds(String fieldName) {
        Object fileInfoObjs = getValueFromMap(fieldName, this.formDataColMap);
        putValue(fieldName, null);

        /*
         * if (fileInfoObjs instanceof List) { if (((List) (fileInfoObjs)).size() == 0)
         * { putValue(fieldName, null);
         *
         * } }
         */

        String dataUuid = (String) getValueFromMap(EnumFormPropertyName.uuid.name(), this.formDataColMap);
        List<String> fileIds = null;
        if (fileInfoObjs == null) {
            fileIds = new ArrayList<String>();
        } else {
            fileIds = getFileIds(fileInfoObjs);
        }

        return fileIds;
    }

    /**
     * 获取指定的字段的值
     *
     * @param fieldName
     * @return
     */
    public Object getValueByApplyTo(String applyTo) {
        List<String> fieldNames = this.dUtils.getFieldNameByApplyTo(applyTo);
        if (CollectionUtils.isEmpty(fieldNames)) {
            return null;
        }
        List<Object> values = Lists.newArrayList();
        for (String fieldName : fieldNames) {
            values.add(this.getValue(fieldName));
        }
        if (CollectionUtils.size(values) == 1) {
            return values.get(0);
        }
        return StringUtils.join(values, Separator.SEMICOLON.getValue());
    }

    /**
     * @param fieldName 字段名
     * @return
     */
    public String getDisplayValue(String fieldName) {
        Object valueMap = getValue(fieldName);
        if (valueMap == null) {
            return null;
        }

        // 附件字段
        if (this.dUtils.isInputModeEqAttach(fieldName)) {
            return getFileNamesOfField(fieldName, valueMap);
        } else {
            JSONObject jsonObject = dUtils.getFieldDefinitionJson(fieldName);
            if (jsonObject.has("configuration")) {
                JSONObject configuration = jsonObject.getJSONObject("configuration");
                // 组织选择框
                if (configuration.has("inputMode") && "43".equals(Objects.toString(configuration.get("inputMode")))) {
                    return getOrgSelectDisplayValue(fieldName, Objects.toString(valueMap, StringUtils.EMPTY), configuration);
                } else if (configuration.has("inputMode") && "41".equals(Objects.toString(configuration.get("inputMode")))) {
                    // 职位
                    return getUserJobDisplayValue(fieldName, Objects.toString(valueMap, StringUtils.EMPTY), configuration);
                } else if (configuration.has("options")) {
                    // 下拉等选项字段
                    return getOptionDisplayValue(fieldName, Objects.toString(valueMap, StringUtils.EMPTY), configuration);
                } else {
                    return valueMap.toString();
                }
            } else {
                String valueMapStr = valueMap.toString();
                String separater = dUtils.getFieldSeparater(fieldName);
                List<String> displayValues = new ArrayList<String>();
                for (String realValue : valueMapStr.split(separater)) {
                    displayValues.add(dUtils.getDisplayValue(fieldName, realValue));
                }
                return StringUtils.join(displayValues, separater);
            }
        }
    }

    /**
     * @param fieldName
     * @param fieldValue
     * @param configuration
     * @return
     */
    private String getUserJobDisplayValue(String fieldName, String fieldValue, JSONObject configuration) {
        return getOrgSelectDisplayValue(fieldName, fieldValue, configuration);
    }

    /**
     * @param fieldName
     * @param fieldValue
     * @param configuration
     * @return
     */
    private String getOrgSelectDisplayValue(String fieldName, String fieldValue, JSONObject configuration) {
        if (StringUtils.isBlank(fieldValue)) {
            return StringUtils.EMPTY;
        }

        String fieldSeparater = dUtils.getFieldSeparater(fieldName);
        List<String> values = Arrays.asList(StringUtils.split(fieldValue, fieldSeparater));
        OrgFacadeService orgFacadeService = ApplicationContextHolder.getBean(OrgFacadeService.class);
        Map<String, String> orgIdMap = null;
        if (configuration.has("jobDisplayPattern") && "full".equalsIgnoreCase(Objects.toString(configuration.get("jobDisplayPattern")))) {
            List<String> idPaths = Lists.newArrayList();
            for (String jobId : values) {
                String idPath = orgFacadeService.getOrgElementPathById(jobId);
                if (StringUtils.isNotBlank(idPath)) {
                    idPaths.add(idPath);
                }
            }
            orgIdMap = orgFacadeService.getNameByOrgEleIds(idPaths);
        } else {
            orgIdMap = orgFacadeService.getNameByOrgEleIds(values);
        }
        List<String> displayValues = Lists.newArrayList(orgIdMap.values());
        return CollectionUtils.isNotEmpty(displayValues) ? StringUtils.join(displayValues, fieldSeparater) : fieldValue;
    }

    /**
     * @param fieldName
     * @param files
     * @return
     */
    public String getFileNamesOfField(String fieldName, Object files) {
        List<String> filenames = Lists.newArrayList();
        if (files instanceof List && CollectionUtils.isNotEmpty((List) files)) {
            if (((List) files).get(0) instanceof LogicFileInfo) {
                for (LogicFileInfo file : (List<LogicFileInfo>) files) {
                    filenames.add(file.getFileName().toString());
                }
            } else if (((List) files).get(0) instanceof Map) {
                for (Map file : (List<Map>) files) {
                    if (file.containsKey("filename")) {
                        filenames.add(file.get("filename").toString());
                    } else {
                        filenames.add(file.get("fileName").toString());
                    }
                }
            }
        } else if (files instanceof Map) {
            filenames.add(((Map) files).get("fileName").toString());
        }

        return StringUtils.join(filenames, Separator.COMMA.getValue());
    }

    /**
     * @param fieldName
     * @param fieldValue
     * @param configuration
     * @return
     */
    private String getOptionDisplayValue(String fieldName, String fieldValue, JSONObject configuration) {
        if (StringUtils.isBlank(fieldValue)) {
            return StringUtils.EMPTY;
        }

        String fieldSeparater = dUtils.getFieldSeparater(fieldName);
        List<String> values = Arrays.asList(StringUtils.split(fieldValue, fieldSeparater));
        List<String> displayValues = Lists.newArrayList();
        JSONObject optionsJsonObject = configuration.getJSONObject("options");
        String optionType = optionsJsonObject.getString("type");
        Map<String, String> displayValueMap = Maps.newHashMap();
        if ("selfDefine".equalsIgnoreCase(optionType)) {
            if (optionsJsonObject.has("defineOptions")) {
                JSONArray defineOptionsArray = optionsJsonObject.getJSONArray("defineOptions");
                for (int index = 0; index < defineOptionsArray.length(); index++) {
                    JSONObject option = defineOptionsArray.getJSONObject(index);
                    if (values.contains(option.getString("value"))) {
                        displayValues.add(option.getString("label"));
                        displayValueMap.put(option.getString("label"), option.getString("value"));
                    }
                }
            } else if (configuration.has("treeData")) {
                JSONArray treeDataArray = configuration.getJSONArray("treeData");
                traverseTreeData(treeDataArray, jsonObject -> {
                    if (jsonObject.has("display") && jsonObject.has("real")) {
                        if (values.contains(jsonObject.getString("real"))) {
                            displayValues.add(jsonObject.getString("display"));
                            displayValueMap.put(jsonObject.getString("display"), jsonObject.getString("real"));
                        }
                    }
                });
            }
        } else if ("dataDictionary".equalsIgnoreCase(optionType)) {
            // 数据字典
            String dataDictionaryUuid = optionsJsonObject.getString("dataDictionaryUuid");
            CdDataDictionaryService cdDataDictionaryService = ApplicationContextHolder.getBean(CdDataDictionaryService.class);
            CdDataDictionaryEntity dataDictionaryEntity = cdDataDictionaryService.getByCode(dataDictionaryUuid);
            if (dataDictionaryEntity == null && NumberUtils.isNumber(dataDictionaryUuid)) {
                dataDictionaryEntity = cdDataDictionaryService.getOne(Long.parseLong(dataDictionaryUuid));
            }
            if (dataDictionaryEntity != null) {
                TreeNode treeNode = cdDataDictionaryService.listItemAsTreeByUuid(dataDictionaryEntity.getUuid());
                TreeUtils.traverseTree(treeNode, new Function<TreeNode, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable TreeNode input) {
                        String valueString = Objects.toString(input.getData());
                        if (values.contains(valueString)) {
                            displayValues.add(input.getName());
                            displayValueMap.put(input.getName(), valueString);
                        }
                        return null;
                    }
                });
            }
        } else if ("dataSource".equalsIgnoreCase(optionType) || "dataModel".equalsIgnoreCase(optionType)) {
            CdDataStoreService cdDataStoreService = ApplicationContextHolder.getBean(CdDataStoreService.class);
            String dataSourceId = "dataSource".equalsIgnoreCase(optionType) ? optionsJsonObject.getString("dataSourceId") : StringUtils.EMPTY;
            String dataModelUuid = "dataModel".equalsIgnoreCase(optionType) ? optionsJsonObject.getString("dataModelUuid") : StringUtils.EMPTY;
            String dataSourceLabelColumn = optionsJsonObject.getString("dataSourceLabelColumn");
            String dataSourceValueColumn = optionsJsonObject.getString("dataSourceValueColumn");
            DataStoreParams dataStoreParams = new DataStoreParams();
            dataStoreParams.setDataStoreId(dataSourceId);
            Condition valueInCondition = new Condition(dataSourceValueColumn, values, CriterionOperator.in);
            dataStoreParams.getCriterions().add(valueInCondition);

            DataStoreData dataStoreData = null;
            // 数据模型
            if ("dataModel".equalsIgnoreCase(optionType)) {
                dataStoreData = (DataStoreData) ServiceInvokeUtils.invoke("dataModelService.loadDataStoreData", new Class[]{Long.class, DataStoreParams.class}, Long.parseLong(dataModelUuid), dataStoreParams);
            } else {
                dataStoreData = cdDataStoreService.loadData(dataStoreParams);
            }
            List<Map<String, Object>> dataList = dataStoreData.getData();
            if (CollectionUtils.isNotEmpty(dataList)) {
                dataList.forEach(item -> {
                    String valueString = Objects.toString(item.get(dataSourceValueColumn));
                    if (values.contains(valueString)) {
                        String displayValue = Objects.toString(item.get(dataSourceLabelColumn), StringUtils.EMPTY);
                        displayValues.add(displayValue);
                        displayValueMap.put(displayValue, valueString);
                    }
                });
            }
        }

        displayValues.sort((v1, v2) -> {
            int index1 = values.indexOf(displayValueMap.get(v1));
            int index2 = values.indexOf(displayValueMap.get(v2));
            return index1 - index2;
        });
        return CollectionUtils.isNotEmpty(displayValues) ? StringUtils.join(displayValues, fieldSeparater) : fieldValue;
    }

    /**
     * @param treeDataArray
     * @param consumer
     */
    private void traverseTreeData(JSONArray treeDataArray, Consumer<JSONObject> consumer) {
        for (int i = 0; i < treeDataArray.length(); i++) {
            JSONObject jsonObject = treeDataArray.getJSONObject(i);
            consumer.accept(jsonObject);
            if (jsonObject.has("children")) {
                traverseTreeData(jsonObject.getJSONArray("children"), consumer);
            }
        }
    }

    /**
     * @param applyTo
     * @return
     */
    public String getDisplayValueByApplyTo(String applyTo) {
        List<String> fieldNames = this.dUtils.getFieldNameByApplyTo(applyTo);
        if (CollectionUtils.isEmpty(fieldNames)) {
            return null;
        }
        List<String> displayNames = Lists.newArrayList();
        for (String fieldName : fieldNames) {
            displayNames.add(this.getDisplayValue(fieldName));
        }
        return StringUtils.join(displayNames, Separator.SEMICOLON.getValue());
    }

    public FormDefinitionHandler getdUtils() {
        return dUtils;
    }

    /**
     * ======================================================================
     * 功能：判断字符串是否为日期格式
     *
     * @param str
     * @return
     */
    public boolean isDate(String strDate) {
        Pattern pattern = Pattern.compile(
                "^((/d{2}(([02468][048])|([13579][26]))[/-///s]?((((0?[13578])|(1[02]))[/-///s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[/-///s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[/-///s]?((0?[1-9])|([1-2][0-9])))))|(/d{2}(([02468][1235679])|([13579][01345789]))[/-///s]?((((0?[13578])|(1[02]))[/-///s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[/-///s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[/-///s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(/s(((0?[0-9])|([1-2][0-3]))/:([0-5]?[0-9])((/s)|(/:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public Map<String, Object> getDisplayValues() {
        Map<String, Object> displayValues = new HashMap<String, Object>();
        List<String> fieldNames = this.getdUtils().getFieldNamesOfMainform();
        for (String fieldName : fieldNames) {
//            if (this.dUtils.isValueAsMap(fieldName)) {
            displayValues.put(fieldName, this.getDisplayValue(fieldName));
//            } else {
//                displayValues.put(fieldName, this.getValue(fieldName));
//            }
        }
        displayValues.put(EnumFormPropertyName.uuid.name(), this.getValue(EnumFormPropertyName.uuid.name()));
        return displayValues;
    }

    public ValidateMsg validate() {
        ValidateMsg msg = new ValidateMsg();
        // 1、对于json格式的是否符合
        // 2、对于number是否符合数字
        // 3、对于number及字符串的长度是否符合
        // 4、对于日期类型是否符合
        return msg;
    }

    public ValidateMsg validate(Integer idx) {
        ValidateMsg msg = new ValidateMsg();
        String location = null;
        if (idx != null) {
            location = dUtils.getFormPropertyOfStringType(EnumFormPropertyName.uuid) + "#" + idx;
        } else {
            location = "";
        }
        Map<String, ValidatorFieldContent> fieldValidators = dUtils.getValidatorContent(customValidators);
        doValidate(msg, location, fieldValidators);
        return msg;
    }

    /**
     * @param msg
     * @param location
     * @param fieldValidators
     */
    private void doValidate(ValidateMsg msg, String location, Map<String, ValidatorFieldContent> fieldValidators) {
        for (String fieldName : fieldValidators.keySet()) {
            ValidatorFieldContent validatorFieldContent = fieldValidators.get(fieldName);
            if (validatorFieldContent == null || validatorFieldContent.getValidators().isEmpty()) {
                continue;
            }
            Object value = getValue(fieldName);
            FieldDefinition fieldDefinition = validatorFieldContent.getFieldDefinition();
            List<ValidatorParams> validators = validatorFieldContent.getValidators();
            for (ValidatorParams validator : validators) {
                if (validator.getParams() == null && validator.getValidator() instanceof ValidatorInit) {
                    validator.setParams(((ValidatorInit) validator.getValidator()).init(this));
                }
                ValidatorResult result = validator.getValidator().isValid(value, validatorFieldContent,
                        validator.getParams());
                if (result != null && result.isValid() == false) {
                    ValidateData validateData = new ValidateData();
                    validateData.setLocation(location);
                    validateData.setFieldName(fieldName);
                    validateData.setMsg(result.getValidMeg());
                    validateData.setDisplayName(fieldDefinition.getDisplayName());
                    msg.getErrors().add(validateData);
                    break;
                }
            }
        }
        if (false == msg.getErrors().isEmpty()) {
            msg.setCode(EnumValidateCode.FAIL);
        }
    }

    /**
     * 校验表单数据的数据库约束——长度、类型、必填
     *
     * @param idx
     * @return
     */
    public ValidateMsg validateWithDatabaseConstraints(Integer idx) {
        ValidateMsg msg = new ValidateMsg();
        String location = null;
        if (idx != null) {
            location = dUtils.getFormPropertyOfStringType(EnumFormPropertyName.uuid) + "#" + idx;
        } else {
            location = "";
        }
        Map<String, ValidatorFieldContent> fieldValidators = dUtils.getValidatorContentWithDatabaseConstraints();
        doValidate(msg, location, fieldValidators);
        return msg;
    }

    public String getFormUuid() {
        return dUtils.getFormUuid();
    }

    public String getDataUuid() {
        return (String) getValueFromMap(EnumFormPropertyName.uuid.name(), this.formDataColMap);
    }

    public Object getFieldDataOptions(String fieldName, String key) {
        key = getFormUuid() + "." + getDataUuid() + "." + fieldName + "." + key;
        return dyformDataOptions.getOptions(key);
    }

    public void putFieldDataOptions(String fieldName, String key, Object value) {
        key = getFormUuid() + "." + getDataUuid() + "." + fieldName + "." + key;
        dyformDataOptions.putOptions(key, value);
    }

    public List<String> getValueOfFileIds(String fieldName, String dataUuid) {
        if (mongoFileService.isFolderExist(dataUuid)) {
            List<LogicFileInfo> logicFileInfos = mongoFileService.getNonioFilesFromFolder(dataUuid,
                    fieldName.toLowerCase());
            List<String> fileIds = Lists.newArrayListWithCapacity(logicFileInfos.size());
            for (LogicFileInfo info : logicFileInfos) {
                fileIds.add(info.getFileID());
            }
            return fileIds;
        }
        return Collections.EMPTY_LIST;
    }

}

// 比较器类
class MapKeyComparator implements Comparator<String> {
    public int compare(String str1, String str2) {
        return str1.compareTo(str2);
    }
}
