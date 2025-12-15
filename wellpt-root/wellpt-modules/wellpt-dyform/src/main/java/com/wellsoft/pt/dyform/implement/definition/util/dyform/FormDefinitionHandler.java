package com.wellsoft.pt.dyform.implement.definition.util.dyform;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.i18n.MsgUtils;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.datastore.support.Condition;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformSubformFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.utils.FormDataHandler;
import com.wellsoft.pt.dyform.implement.definition.cache.DyformCacheUtils;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.*;
import com.wellsoft.pt.dyform.implement.definition.dto.FieldDefinition;
import com.wellsoft.pt.dyform.implement.definition.dto.SubformFormDefinition;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumMstTblSystemField;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumRelationTblSystemField;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.dyform.implement.definition.exceptions.FormDefinitionUpdateException;
import com.wellsoft.pt.dyform.implement.definition.validator.Validator;
import com.wellsoft.pt.dyform.implement.definition.validator.ValidatorFieldContent;
import com.wellsoft.pt.dyform.implement.definition.validator.ValidatorMaps;
import com.wellsoft.pt.dyform.implement.definition.validator.ValidatorParams;
import com.wellsoft.pt.dyform.implement.repository.usertable.metadata.ColumnMetadata;
import com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableMetadataService;
import com.wellsoft.pt.jpa.criterion.CriterionOperator;
import com.wellsoft.pt.multi.org.bean.OrgElementVo;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.core.userdetails.InternetUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StopWatch;

import java.text.ParseException;
import java.util.*;

public class FormDefinitionHandler {
    private static Logger logger = LoggerFactory.getLogger(FormDefinitionHandler.class);
    private final String assistedShowType = "assistedShowType";
    private Map<String, ValidatorFieldContent> validatorContents;
    private JSONObject formDefinitionJSONObject;
    private JSONObject fieldDefinitionJSONObjects;
    private JSONObject subformDefinitionJSONObjects;
    private JSONObject templateDefinitionJSONObjects;
    private JSONObject placeholderCtrDefinitionJSONObjects;
    private JSONObject fileLibraryDefinitionJSONObjects;
    private JSONObject tableViewDefinitionJSONObjects;
    private JSONObject blockDefinitionJSONObjects = null;// 区域
    private Map<String/* applyTo */, Set<String>/* fieldName */> applyTo2Field = new HashMap<String, Set<String>>();
    private Map<String/* inputMode */, String/* fieldName */> inputMode2Field = new HashMap<String, String>();
    private DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
    private OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
    private String formType;
    private String name;
    private String pformUuid;
    private JSONObject layOutDefinitionJSONObjects = null;// 页签
    // 从表定义MAP
    private Map<String, DyformSubformFormDefinition> subformDefinitionMap = Maps.newHashMap();
    private FormDefinition dyFormFormDefinition = null;

    public FormDefinitionHandler(String defintionJson, String formType, String name, String pformUuid)
            throws JSONException {
        this.formType = formType;
        this.name = name;
        this.pformUuid = pformUuid;
        this.formDefinitionJSONObject = new JSONObject(defintionJson);
        this.fieldDefinitionJSONObjects = this.getFormPropertyOfJSONType(EnumFormPropertyName.fields);
        this.subformDefinitionJSONObjects = this.getFormPropertyOfJSONType(EnumFormPropertyName.subforms);
        this.templateDefinitionJSONObjects = this.getFormPropertyOfJSONType(EnumFormPropertyName.templates);
        this.placeholderCtrDefinitionJSONObjects = this.getFormPropertyOfJSONType(EnumFormPropertyName.placeholderCtr);
        this.fileLibraryDefinitionJSONObjects = this.getFormPropertyOfJSONType(EnumFormPropertyName.fileLibrary);
        this.tableViewDefinitionJSONObjects = this.getFormPropertyOfJSONType(EnumFormPropertyName.tableView);
        if (JSONObject.NULL.equals(templateDefinitionJSONObjects)) {
            templateDefinitionJSONObjects = new JSONObject();
            formDefinitionJSONObject.put(EnumFormPropertyName.templates.name(), templateDefinitionJSONObjects);
        } else if (false == DyformTypeEnum.isMSTform(formType)) {
            String html = getFormPropertyOfStringType(EnumFormPropertyName.html);
            Document document = StringUtils.isNotBlank(html) ? Jsoup.parse(html) : null;
            for (Object mstFormUuid : templateDefinitionJSONObjects.keySet()) {
                JSONObject templateDefinitionJSONObject = templateDefinitionJSONObjects
                        .getJSONObject((String) mstFormUuid);
                DyFormFormDefinition dyMstformDefinition = dyFormFacade.getFormDefinition((String) mstFormUuid);
                if (null == dyMstformDefinition || StringUtils.isBlank(dyMstformDefinition.getUuid())) {
                    logger.warn("mstFormUuid[" + mstFormUuid + "] FormDefinition is not found");
                    continue;
                }
                FormDefinitionHandler formDefinitionHandler = ((FormDefinition) dyMstformDefinition)
                        .doGetFormDefinitionHandler();
                String template = formDefinitionHandler.getFormPropertyOfStringType(EnumFormPropertyName.html);
                if (StringUtils.isBlank(template)) {
                    continue;
                }
                JSONObject templateFieldDefinitions = formDefinitionHandler
                        .getFormPropertyOfJSONType(EnumFormPropertyName.fields);
                List<String> fieldsToRemove = new ArrayList<String>();
                Boolean templateFlag = templateDefinitionJSONObject.getBoolean("templateFlag");
                for (Object fieldName : templateFieldDefinitions.keySet()) {
                    JSONObject fieldDefinition = null;
                    if (fieldDefinitionJSONObjects.has((String) fieldName)) {
                        fieldDefinition = fieldDefinitionJSONObjects.getJSONObject((String) fieldName);
                    }
                    if (fieldDefinition == null || fieldDefinition.has(EnumFormPropertyName.master.name())) {
                        JSONObject templateFieldDefinition = templateFieldDefinitions.getJSONObject((String) fieldName);
                        templateFieldDefinition.put(EnumFormPropertyName.master.name(), templateFlag ? false : true);
                        fieldDefinitionJSONObjects.put((String) fieldName,
                                templateFieldDefinitions.getJSONObject((String) fieldName));
                    } else {
                        fieldsToRemove.add((String) fieldName);
                    }
                }
                if (document != null) {
                    Elements elements = document.select(".template-wrapper[templateUuid=" + mstFormUuid + "]");
                    for (ListIterator<Element> iter = elements.listIterator(); iter.hasNext(); ) {
                        String className = templateFlag ? "inner-box" : "inner-box filter";
                        Element element = iter.next().html("<div class=\"" + className + "\">" + template + "</div>");
                        for (String fieldName : fieldsToRemove) {
                            element.select(".value[name=" + fieldName + "").remove();
                        }
                    }
                }
            }
            formDefinitionJSONObject.put(EnumFormPropertyName.html.name(), document != null ? document.body().html() : null);
            formDefinitionJSONObject.put(EnumFormPropertyName.fields.name(), fieldDefinitionJSONObjects);
            formDefinitionJSONObject.put(EnumFormPropertyName.templates.name(), templateDefinitionJSONObjects);
        }
        JSONObject[] updateObjectRefs = new JSONObject[]{fieldDefinitionJSONObjects, subformDefinitionJSONObjects};
        for (JSONObject updateObjectRef : updateObjectRefs) {
            // System.out.println(updateObjectRef.toString());
            for (Object fieldName : updateObjectRef.keySet()) {
                JSONObject fieldObj = updateObjectRef.getJSONObject((String) fieldName);
                if (fieldObj.has(EnumFieldPropertyName.refId.name())) {
                    String refId = fieldObj.getString(EnumFieldPropertyName.refId.name());
                    JSONObject refObj = DyformCacheUtils.getFieldJSONObject(refId);
                    if (null == refObj) {
//                        logger.error("refObj is not null[" + getFormId() + "(" + getFormVersion() + ")" + "],fieldName["
//                                + fieldName + "],refId[" + refId + "]");
                        continue;
                    }
                    fieldObj.put(EnumFieldPropertyName.displayName.name(),
                            refObj.getString(EnumFieldPropertyName.displayName.name()));
                    // 覆盖冗余引用
                    fieldObj.put(EnumFieldPropertyName.refObj.name(), refObj);
                }
            }
        }

        this.blockDefinitionJSONObjects = this.formDefinitionJSONObject
                .isNull(EnumFormPropertyName.blocks.name()) == true ? new JSONObject()
                : this.getFormPropertyOfJSONType(EnumFormPropertyName.blocks);
        this.layOutDefinitionJSONObjects = this.formDefinitionJSONObject
                .isNull(EnumFormPropertyName.layouts.name()) == true ? new JSONObject()
                : this.getFormPropertyOfJSONType(EnumFormPropertyName.layouts);
    }

    /**
     * 根据数据库字段类型获取时间格式
     *
     * @param dbDataType
     * @return
     */
    public static String getDateTimePatternByContentFormat(String contentFormat) {
        String dateTimePattern = "yyyy-MM-dd HH:mm:ss sss";
        if (NumberUtils.isDigits(contentFormat)) {
            if (DyDateFomat.yearMonthDate.equals(contentFormat)) {
                dateTimePattern = "yyyy-MM-dd";
            } else if (DyDateFomat.yearMonthDate2.equals(contentFormat)) {
                dateTimePattern = "yyyy-M-d";
            } else if (DyDateFomat.yearMonthDateCn.equals(contentFormat)) {
                dateTimePattern = "yyyy年M月d日";
            } else if (DyDateFomat.yearMonthDateCn2.equals(contentFormat)) {
                dateTimePattern = "yyyy年MM月dd日";
            } else if (DyDateFomat.yearCn.equals(contentFormat)) {
                dateTimePattern = "yyyy年";
            } else if (DyDateFomat.yearMonthCn.equals(contentFormat)) {
                dateTimePattern = "yyyy年MM月";
            } else if (DyDateFomat.yearMonth.equals(contentFormat)) {
                dateTimePattern = "yyyy-MM";
            } else if (DyDateFomat.monthDateCn.equals(contentFormat)) {
                dateTimePattern = "MM月dd日";
            } else if (DyDateFomat.year.equals(contentFormat)) {
                dateTimePattern = "yyyy";
            } else if (DyDateFomat.timeHour.equals(contentFormat)) {
                dateTimePattern = "HH";
            } else if (DyDateFomat.timeMin.equals(contentFormat)) {
                dateTimePattern = "HH:mm";
            } else if (DyDateFomat.timeSec.equals(contentFormat)) {
                dateTimePattern = "HH:mm:ss";
            } else if (DyDateFomat.dateTimeHour.equals(contentFormat)) {
                dateTimePattern = "yyyy-MM-dd HH";
            } else if (DyDateFomat.dateTimeMin.equals(contentFormat)) {
                dateTimePattern = "yyyy-MM-dd HH:mm";
            } else if (DyDateFomat.dateTimeSec.equals(contentFormat)) {
                dateTimePattern = "yyyy-MM-dd HH:mm:ss";
            } else if (DyDateFomat.dateWeak.equals(contentFormat)) {
                dateTimePattern = "yyyy-MM-dd";
            }
        } else if (StringUtils.isNotBlank(contentFormat)) {
            return contentFormat;
        }
        return dateTimePattern;
    }

    /**
     * 在数据关系表中是否存在指定的字段
     *
     * @param fieldName
     * @return
     */
    public static boolean isRelationTblField(String fieldName) {
        return EnumRelationTblSystemField.value2EnumObj(fieldName) != null;
    }

    /**
     * 是不是系统字段
     *
     * @param fieldName
     * @return
     */
    public static boolean isSysTypeAsSystem(String fieldName) {
        return EnumSystemField.value2EnumObj(fieldName) != null
                || EnumMstTblSystemField.value2EnumObj(fieldName) != null;
    }

    /**
     * 获取数据表的系统字段
     *
     * @return
     */
    public static List<String> getSysFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        for (EnumSystemField sysField : EnumSystemField.values()) {
            if (sysField.equals(EnumSystemField.signature_) || sysField.equals(EnumSystemField.version)) {
                // FIXME: 这两个字段废弃
                continue;
            }
            fieldNames.add(sysField.name());
        }
        return fieldNames;
    }

    /**
     * 级联数据源
     *
     * @param dataSources
     * @param treeNodes
     * @param cdDataStoreService
     * @throws ParseException
     * @throws JSONException
     */
    public static List<TreeNode> chainedDataSource(JSONArray dataSources, Map<String, Object> params,
                                                   CdDataStoreService cdDataStoreService) throws ParseException, JSONException {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        DataSourceIterate dataSourceIterate = new FormDefinitionHandler.DataSourceIterate(dataSources,
                cdDataStoreService);
        dataSourceIterate.traverseTreeNode(0, treeNodes, null, params);
        return treeNodes;
    }

    /**
     * 级联数据字典
     *
     * @param type
     * @param dicTitles
     * @param dicts
     * @param dataDictionaryService
     * @return
     */
    public static List<TreeNode> chainedDataDict(String type, String dicTitles,
                                                 DataDictionaryService dataDictionaryService) {
        List<TreeNode> dicts = new ArrayList<TreeNode>();
        int dictLevel = Integer.MAX_VALUE;
        if (dicTitles != null && dicTitles.contains("/")) {
            dictLevel = dicTitles.split("/").length;
        }
        if (type != null && type.length() > 0) {
            dicts = dataDictionaryService.getTreeNodeByType(type, dictLevel);
        }
        return dicts;
    }

    /**
     * 获取字符串类型的表单属性
     *
     * @param formPropertyName
     * @return
     */
    public String getFormPropertyOfStringType(EnumFormPropertyName formPropertyName) {
        try {
            if (this.formDefinitionJSONObject.has(formPropertyName.name())) {
                return this.formDefinitionJSONObject.getString(formPropertyName.name());
            } else {
                return null;
            }
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取JSON类型的表单属性
     *
     * @param formPropertyName
     * @return
     */
    public JSONObject getFormPropertyOfJSONType(EnumFormPropertyName formPropertyName) {
        try {
            if (this.formDefinitionJSONObject.isNull(formPropertyName.name())) {
                return null;
            }
            return this.formDefinitionJSONObject.getJSONObject(formPropertyName.name());
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取JSON类型的表单属性
     *
     * @param formPropertyName
     * @return
     */
    public Object getFormProperty(String formPropertyName) {
        try {
            if (this.formDefinitionJSONObject.isNull(formPropertyName)) {
                return null;
            }
            return this.formDefinitionJSONObject.get(formPropertyName);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取指定字段的字段定义
     *
     * @param fieldName
     * @return
     */
    public JSONObject getFieldDefinitionJson(String fieldName) {
        try {
            if (!this.fieldDefinitionJSONObjects.isNull(fieldName)) {
                return this.fieldDefinitionJSONObjects.getJSONObject(fieldName);
            } else if (!this.fieldDefinitionJSONObjects.isNull(fieldName.toLowerCase())) {
                return this.fieldDefinitionJSONObjects.getJSONObject(fieldName.toLowerCase());
            } else if (!this.fieldDefinitionJSONObjects.isNull(fieldName.toUpperCase())) {
                return this.fieldDefinitionJSONObjects.getJSONObject(fieldName.toUpperCase());
            }

            String systemFieldName = fieldName;
            boolean isSystemField = Arrays.stream(EnumSystemField.values()).filter(f -> StringUtils.equalsIgnoreCase(f.getName(), systemFieldName)).findFirst().isPresent();
            if (isSystemField) {
                return null;
            }

            Iterator<String> it = fieldDefinitionJSONObjects.keys();
            while (it.hasNext()) {
                String filedNametmp = it.next();
                if (filedNametmp.equalsIgnoreCase(fieldName)) {
                    fieldName = filedNametmp;
                    break;
                }
            }
            if (!this.fieldDefinitionJSONObjects.isNull(fieldName)) {
                return this.fieldDefinitionJSONObjects.getJSONObject(fieldName);
            } else if (!this.fieldDefinitionJSONObjects.isNull(fieldName.toLowerCase())) {
                return this.fieldDefinitionJSONObjects.getJSONObject(fieldName.toLowerCase());
            } else if (!this.fieldDefinitionJSONObjects.isNull(fieldName.toUpperCase())) {
                return this.fieldDefinitionJSONObjects.getJSONObject(fieldName.toUpperCase());
            } else {
                return null;
            }
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public JSONObject getSubformDefinitionJson(String formUuidOfSubform) {
        return this.getSubformJSONObject(formUuidOfSubform);
    }

    public String getFieldSeparater(String fieldName) {
        String valSeparator = getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.valSeparator);
        return StringUtils.isBlank(valSeparator) ? Separator.SEMICOLON.getValue() : valSeparator;
    }

    /**
     * 根据指定的字段及属性获取字符串类型的属性值
     *
     * @param fieldName
     * @param fieldPropertyName
     * @return
     */
    public String getFieldPropertyOfStringType(String fieldName, EnumFieldPropertyName fieldPropertyName) {
        return this.getFieldPropertyOfStringType(fieldName, fieldPropertyName.name());
    }

    public String getFieldLocaleDisplayName(String fieldName, String locale) {
        String displayName = null;
        try {
            displayName = this.getFieldDefinitionJson(fieldName)
                    .getJSONObject("configuration").getJSONObject("i18n")
                    .getJSONObject(LocaleContextHolder.getLocale().toString()).getString(this.getFieldPropertyOfStringType(fieldName, "id")
                            + "." + this.getFieldPropertyOfStringType(fieldName, "id"));
        } catch (Exception e) {
        }
        return StringUtils.defaultIfBlank(displayName, this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.displayName));
    }

    public String getFieldPropertyOfStringType(String fieldName, String fieldPropertyName) {
        JSONObject fieldDefinitionJSONObject;
        try {
            fieldDefinitionJSONObject = this.getFieldDefinitionJson(fieldName);
            if (fieldDefinitionJSONObject == null) {
                return null;
            }
            Object obj = null;
            if (fieldDefinitionJSONObject.has(fieldPropertyName)) {
                obj = fieldDefinitionJSONObject.get(fieldPropertyName);
            } else if (fieldDefinitionJSONObject.has(EnumFieldPropertyName.refObj.name())) {
                JSONObject refObj = fieldDefinitionJSONObject.getJSONObject(EnumFieldPropertyName.refObj.name());
                if (refObj.has(fieldPropertyName)) {
                    obj = refObj.get(fieldPropertyName);
                }
            }
            if (obj == null) {
                return null;
            }
            if (obj instanceof String) {
                return (String) obj;
            } else if (obj instanceof Long) {
                return Long.toString(((Long) obj).longValue());
            } else if (obj instanceof Integer) {
                return Integer.toString(((Integer) obj).intValue());
            } else if (obj instanceof Double) {
                return Double.toString(((Double) obj).doubleValue());
            } else if (obj instanceof Float) {
                return Float.toString(((Float) obj).floatValue());
            } else if (obj instanceof JSONObject) {
                return obj.toString();
            } else if (JSONObject.NULL.equals(obj)) {
                return null;
            } else {
                return (String) obj;
            }
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    public Object getFieldPropertyOfObjectType(String fieldName, String fieldPropertyName) {
        JSONObject fieldDefinitionJSONObject;
        try {
            fieldDefinitionJSONObject = this.getFieldDefinitionJson(fieldName);
            if (fieldDefinitionJSONObject == null) {
                return null;
            }
            Object obj = null;
            if (fieldDefinitionJSONObject.has(fieldPropertyName)) {
                obj = fieldDefinitionJSONObject.get(fieldPropertyName);
            } else if (fieldDefinitionJSONObject.has(EnumFieldPropertyName.refObj.name())) {
                JSONObject refObj = fieldDefinitionJSONObject.getJSONObject(EnumFieldPropertyName.refObj.name());
                if (refObj.has(fieldPropertyName)) {
                    obj = refObj.get(fieldPropertyName);
                }
            }
            return obj;
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据指定的字段及属性获取字符串类型的属性值
     *
     * @param fieldName
     * @param fieldPropertyName
     * @return add by zhangyh 20170411
     */
    public Boolean getFieldPropertyOfBooleanType(String fieldName, EnumFieldPropertyName fieldPropertyName) {
        JSONObject fieldDefinitionJSONObject;
        try {
            fieldDefinitionJSONObject = this.getFieldDefinitionJson(fieldName);
            if (fieldDefinitionJSONObject == null) {
                return null;
            }
            Boolean obj = null;
            if (fieldDefinitionJSONObject.has(fieldPropertyName.name())) {
                obj = fieldDefinitionJSONObject.getBoolean(fieldPropertyName.name());
            } else if (fieldDefinitionJSONObject.has(EnumFieldPropertyName.refObj.name())) {
                JSONObject refObj = fieldDefinitionJSONObject.getJSONObject(EnumFieldPropertyName.refObj.name());
                if (refObj.has(fieldPropertyName.name())) {
                    obj = refObj.getBoolean(fieldPropertyName.name());
                }
            }
            if (obj == null) {
                return null;
            }
            return obj;
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据指定的字段及属性获取字符串类型的属性值
     *
     * @param fieldName
     * @param fieldPropertyName
     * @return
     */
    public JSONObject getFieldPropertyOfJSONType(String fieldName, EnumFieldPropertyName fieldPropertyName) {
        JSONObject fieldDefinitionJSONObject;
        try {
            fieldDefinitionJSONObject = this.getFieldDefinitionJson(fieldName);
            if (fieldDefinitionJSONObject == null) {
                return null;
            }
            JSONObject obj = null;
            if (fieldDefinitionJSONObject.has(fieldPropertyName.name())) {
                obj = fieldDefinitionJSONObject.getJSONObject(fieldPropertyName.name());
            } else if (fieldDefinitionJSONObject.has(EnumFieldPropertyName.refObj.name())) {
                JSONObject refObj = fieldDefinitionJSONObject.getJSONObject(EnumFieldPropertyName.refObj.name());
                if (refObj.has(fieldPropertyName.name())) {
                    obj = refObj.getJSONObject(fieldPropertyName.name());
                }
            }
            return obj;
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public JSONObject getFieldPropertyOfJSONType(String fieldName, String fieldPropertyName) {
        JSONObject fieldDefinitionJSONObject;
        try {
            fieldDefinitionJSONObject = this.getFieldDefinitionJson(fieldName);
            if (fieldDefinitionJSONObject == null) {
                return null;
            }
            JSONObject obj = null;
            if (fieldDefinitionJSONObject.has(fieldPropertyName)) {
                obj = fieldDefinitionJSONObject.getJSONObject(fieldPropertyName);
            } else if (fieldDefinitionJSONObject.has(EnumFieldPropertyName.refObj.name())) {
                JSONObject refObj = fieldDefinitionJSONObject.getJSONObject(EnumFieldPropertyName.refObj.name());
                if (refObj.has(fieldPropertyName)) {
                    obj = refObj.getJSONObject(fieldPropertyName);
                }
            }
            return obj;
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据指定的字段及属性获取JSON数组类型的属性值
     *
     * @param fieldName
     * @param fieldPropertyName
     * @return
     */
    public JSONArray getFieldPropertyOfJSONArrayType(String fieldName, EnumFieldPropertyName fieldPropertyName) {
        JSONObject fieldDefinitionJSONObject;
        try {
            fieldDefinitionJSONObject = this.getFieldDefinitionJson(fieldName);
            if (fieldDefinitionJSONObject == null) {
                String message = MsgUtils.getMessage("dyform.exception.fieldNotExist", fieldName);
                logger.error(message);
                throw new RuntimeException(message);
            }
            JSONArray obj = null;
            if (fieldDefinitionJSONObject.has(fieldPropertyName.name())) {
                obj = fieldDefinitionJSONObject.getJSONArray(fieldPropertyName.name());
            } else if (fieldDefinitionJSONObject.has(EnumFieldPropertyName.refObj.name())) {
                JSONObject refObj = fieldDefinitionJSONObject.getJSONObject(EnumFieldPropertyName.refObj.name());
                if (refObj.has(fieldPropertyName.name())) {
                    return refObj.getJSONArray(fieldPropertyName.name());
                }
            }
            return obj;
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public JSONObject getSubformJSONObject(String formUuid) {
        try {
            return this.subformDefinitionJSONObjects.getJSONObject(formUuid);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据指定的从表及从表属性名获取字段串类型的属性值
     *
     * @param formUuid
     * @param subformPropertyName
     * @return
     */
    public String getSubformPropertyOfStringType(String formUuid, EnumSubformPropertyName subformPropertyName) {
        try {
            String obj = null;
            JSONObject subformDefinitionJSONObject = this.getSubformJSONObject(formUuid);
            if (subformDefinitionJSONObject.has(subformPropertyName.name())) {
                obj = subformDefinitionJSONObject.getString(subformPropertyName.name());
            } else if (subformDefinitionJSONObject.has(EnumFieldPropertyName.refObj.name())) {
                JSONObject refObj = subformDefinitionJSONObject.getJSONObject(EnumFieldPropertyName.refObj.name());
                if (refObj.has(subformPropertyName.name())) {
                    obj = refObj.getString(subformPropertyName.name());
                }
            }
            return obj;
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据指定的从表及从表属性名获取JSON类型的属性值
     *
     * @param formUuid
     * @param subformPropertyName
     * @return
     */
    public JSONObject getSubformPropertyOfJSONType(String formUuid, EnumSubformPropertyName subformPropertyName) {
        try {
            JSONObject obj = null;
            JSONObject subformDefinitionJSONObject = this.getSubformJSONObject(formUuid);
            if (subformDefinitionJSONObject.has(subformPropertyName.name())) {
                obj = subformDefinitionJSONObject.getJSONObject(subformPropertyName.name());
            } else if (subformDefinitionJSONObject.has(EnumFieldPropertyName.refObj.name())) {
                JSONObject refObj = subformDefinitionJSONObject.getJSONObject(EnumFieldPropertyName.refObj.name());
                if (refObj.has(subformPropertyName.name())) {
                    obj = refObj.getJSONObject(subformPropertyName.name());
                }
            }
            return obj;
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public void addSubformFieldPropertyOfStringType(String formUuidOfSubform, String fieldName,
                                                    EnumSubformFieldPropertyName fieldPropertyName, String subformPropertyValue) {
        try {
            this.getSubformPropertyOfJSONType(formUuidOfSubform, EnumSubformPropertyName.fields)
                    .getJSONObject(fieldName).put(fieldPropertyName.name(), subformPropertyValue);
        } catch (JSONException e) {
            logger.error(ExceptionUtils.getStackTrace(e), e);
        }
    }

    public void addSubformFieldProperty(String formUuidOfSubform, String fieldName,
                                        EnumSubformFieldPropertyName fieldPropertyName, Object subformPropertyValue) {
        try {
            this.getSubformPropertyOfJSONType(formUuidOfSubform, EnumSubformPropertyName.fields)
                    .getJSONObject(fieldName).put(fieldPropertyName.name(), subformPropertyValue);
        } catch (JSONException e) {
            logger.error(ExceptionUtils.getStackTrace(e), e);
        }
    }

    public String getSubformFieldPropertyOfStringType(String formUuid, String fieldName,
                                                      EnumSubformFieldPropertyName propertyName) {

        try {
            JSONObject fieldProperties = this.getSubformPropertyOfJSONType(formUuid, EnumSubformPropertyName.fields)
                    .getJSONObject(fieldName);
            if (!fieldProperties.isNull(propertyName.name())) {
                return fieldProperties.getString(propertyName.name());
            } else {
                return null;
            }

        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 为定义添加字段
     *
     * @param fieldName
     * @param obj
     */
    public void addField(String fieldName, JSONObject obj) {
        try {
            this.fieldDefinitionJSONObjects.put(fieldName, obj);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 为表单添加属性
     *
     * @param formPropertyName
     * @param obj
     */
    public void addFormProperty(EnumFormPropertyName formPropertyName, Object obj) {
        try {
            this.formDefinitionJSONObject.put(formPropertyName.name(), obj);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 为表单添加属性,该接口最好不要使用,最好使用addFormProperty(EnumFormPropertyName formPropertyName, Object obj)
     *
     * @param formPropertyName
     * @param obj
     */
    public void addFormProperty(String formPropertyName, Object obj) {
        try {
            this.formDefinitionJSONObject.put(formPropertyName, obj);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 添加或者修改字段的属性
     *
     * @param fieldName
     * @param propertyName
     * @param propertyValue
     * @throws JSONException
     */
    public void addFieldProperty(String fieldName, EnumFieldPropertyName propertyName, Object propertyValue)
            throws JSONException {
        /*
         * JSONObject field = this.getFieldDefinitionJson(fieldName);
         * field.put(propertyName.name(), propertyValue);
         */
        this.addFieldProperty(fieldName, propertyName.name(), propertyValue);
    }

    /**
     * 添加或者修改字段的属性
     *
     * @param fieldName
     * @param propertyName
     * @param propertyValue
     * @throws JSONException
     */
    public void addFieldProperty(String fieldName, String propertyName, Object propertyValue) throws JSONException {
        JSONObject field = this.getFieldDefinitionJson(fieldName);
        /* lmw 2015-5-25 16:07 begin */
        if (field == null) {
            String message = MsgUtils.getMessage("dyform.exception.fieldNotExist", fieldName);
            logger.error(message);
            throw new RuntimeException(message);
        }
        /* lmw 2015-5-25 16:07 begin */
        field.put(propertyName, propertyValue);
    }

    public void addSubformProperty(String formUuidOfSubform, EnumSubformPropertyName subformPropertyName,
                                   String subformPropertyValue) throws JSONException {
        JSONObject subform = this.getSubformDefinitionJson(formUuidOfSubform);
        if (subform != null) {
            subform.put(subformPropertyName.name(), subformPropertyValue);
        }
    }

    /**
     * 2015-12-23 yuyq
     * 修改表单中从表的属性
     *
     * @param formUuidOfSubform
     * @param subformPropertyFields
     * @param oldFieldsNames
     * @param FieldsNames
     * @throws JSONException
     */
    public void addSubformPropertyFields(String formUuidOfSubform, EnumSubformPropertyName subformPropertyName,
                                         String oldFieldsNames, String FieldsNames) throws JSONException {
        // 获取从表
        JSONObject subform = this.getSubformDefinitionJson(formUuidOfSubform);
        // 从表属性
        JSONObject jsonObject = subform.getJSONObject("fields");
        // 从表对应字段属性
        JSONObject fieldJson = subform.getJSONObject("fields").getJSONObject(oldFieldsNames);
        fieldJson.put(subformPropertyName.name(), FieldsNames);
        jsonObject.remove(oldFieldsNames);
        jsonObject.put(FieldsNames, fieldJson);
    }

    public boolean isFormTypeAsPform() {
        String formType = this.getFormType();
        return DyformTypeEnum.isPform(formType);
    }

    public boolean isFormTypeAsMSTform() {
        String formType = this.getFormType();
        return DyformTypeEnum.isMSTform(formType);
    }

    public boolean isFormTypeAsVform() {
        String formType = this.getFormType();
        return DyformTypeEnum.isVform(formType);
    }

    public boolean isFormTypeAsMform() {
        String formType = this.getFormType();
        return DyformTypeEnum.isMform(formType);
    }

    public boolean isFormTypeAsCform() {
        String formType = this.getFormType();
        return DyformTypeEnum.isCform(formType);
    }

    public boolean hasCustomTable() {
        if (false == isFormTypeAsCform()) {
            return false;
        }
        String tableName = getTblNameOfMainform();
        return false == StringUtils.equals(tableName, doGetPformFormDefinition().getTableName());
    }

    /**
     * 获取主表表名
     *
     * @return
     */
    public String doGetTblNameOfpForm() {

        if (isFormTypeAsPform() || isFormTypeAsCform() || isFormTypeAsMSTform()) {// 存储单据，表名来自本单据的属性
            return getTblNameOfMainform();
        } else if (isFormTypeAsVform() || isFormTypeAsMform()) {// 展现单据，表名来自其存储单据的属性
            if (this.useDataModel()) {
                return tableName();
            }
            FormDefinition pFormDefinition = doGetPformFormDefinition();
            return pFormDefinition == null ? "" : pFormDefinition.getTableName();
        } else {
            logger.error("未知类型的单据(存储单据/展现单据/手机单据):" + this.getFormType());
            return null;
        }

    }

    public boolean useDataModel() {
        return this.formDefinitionJSONObject.has("useDataModel") && this.formDefinitionJSONObject.getBoolean("useDataModel");
    }

    public String tableName() {
        return this.formDefinitionJSONObject.getString("tableName");
    }

    public String doGetRelationTblNameOfPform() {
        if (this.useDataModel()) {
            return tableName() + "_RL";
        }
        if (this.isFormTypeAsPform() || isFormTypeAsMSTform()) {// 存储单据，表名来自本单据的属性
            return this.getTblNameOfRelationTbl();
        } else if (this.isFormTypeAsVform() || this.isFormTypeAsMform()) {// 展现单据，表名来自其存储单据的属性
            return this.doGetPformFormDefinition().doGetRelationTblNameOfpForm();
        } else {
            // return
            // this.getFormPropertyOfStringType(EnumFormPropertyName.tableName);
            logger.error("未知类型的单据(存储单据/展现单据/手机单据):" + this.getFormType());
            return null;
        }

    }

    public FormDefinition doGetPformFormDefinition() {
        if (StringUtils.isBlank(this.getPformUuid())) {
            logger.error("[" + this.getFormUuid() + ":" + this.getName() + "]该单据没有指定存储单据");
            return null;
        } else {
            if (this.dyFormFormDefinition == null) {
                DyFormFacade dyFormApiFacade = dyFormFacade;// ApplicationContextHolder.getBean(DyFormFacade.class);
                this.dyFormFormDefinition = (FormDefinition) dyFormApiFacade.getFormDefinition(this.getPformUuid());
                return this.dyFormFormDefinition;
            } else {
                return this.dyFormFormDefinition;
            }
        }

    }

    /**
     * 获取数据表的表名
     *
     * @return
     */
    public String getTblNameOfMainform() {
        return this.getFormPropertyOfStringType(EnumFormPropertyName.tableName);
    }

    /**
     * 获取数据表对应的关系表
     *
     * @return
     */
    public String getTblNameOfRelationTbl() {
        return this.getFormPropertyOfStringType(EnumFormPropertyName.relationTbl);
    }

    /**
     * 获取主表的所有字段名
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> getFieldNamesOfMainform() {
        return Lists.newArrayList(this.fieldDefinitionJSONObjects.keySet());
    }

    /**
     * 获取主表的所有字段名
     *
     * @return
     */
    public List<String> getFieldNamesOfMaintable() {
        List<String> result = new ArrayList<String>();
        for (Object fieldName : fieldDefinitionJSONObjects.keySet()) {
            String fieldKey = (String) fieldName;
            try {
                JSONObject fieldObject = fieldDefinitionJSONObjects.getJSONObject(fieldKey);
                Boolean master = null;
                if (fieldObject.has(EnumFormPropertyName.master.name())) {
                    master = fieldObject.getBoolean(EnumFormPropertyName.master.name());
                }
                boolean persistAsColumn = true;
                if (fieldObject.has("configuration")) {
                    JSONObject configuration = fieldObject.getJSONObject("configuration");
                    if (configuration.has("persistAsColumn")) {
                        persistAsColumn = configuration.getBoolean("persistAsColumn");
                    }
                }
                if ((master == null || Boolean.FALSE.equals(master)) && persistAsColumn) {
                    // 脱离模板
                    result.add(fieldKey);
                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        FormDefinition dyFormFormDefinition = null;
        if (isFormTypeAsCform() && (dyFormFormDefinition = doGetPformFormDefinition()) != null) {
            result.removeAll(dyFormFormDefinition.doGetFormDefinitionHandler().getFieldNamesOfMainform());
        }
        return result;
    }

    /**
     * 获取主表的所有字段名
     *
     * @return
     */
    public List<String> getFieldNamesOfTemplates() {
        List<String> result = new ArrayList<String>();
        for (Object templateId : templateDefinitionJSONObjects.keySet()) {
            String mstFormUuid = (String) templateId;
            DyFormFormDefinition formDefinition = dyFormFacade.getFormDefinition(mstFormUuid);
            if (null == formDefinition || StringUtils.isBlank(formDefinition.getUuid())) {
                logger.warn("mstFormUuid[" + mstFormUuid + "] FormDefinition is not found");
                continue;
            }
            result.add(formDefinition.getId());
        }
        return result;
    }

    /**
     * 获取母版UUID的
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> getFormUuidsOfTemplate() {
        return Lists.newArrayList(this.templateDefinitionJSONObjects.keySet());
    }

    /**
     * @param key
     * @param editFields
     */
    public void setTemplateEditableFields(String key, List<String> editFields) {
        try {
            JSONObject jsonObject = templateDefinitionJSONObjects.getJSONObject(key);
            if (jsonObject != null) {
                if (!jsonObject.has("fields")) {
                    jsonObject.put("fields", new HashMap<String, Object>());
                }
                JSONObject fieldsObject = jsonObject.getJSONObject("fields");
                for (String editField : editFields) {
                    if (!fieldsObject.has(editField)) {
                        fieldsObject.put(editField, new HashMap<String, Object>());
                    }
                    fieldsObject.getJSONObject(editField).put(EnumFieldPropertyName.showType.name(), DyShowType.edit);
                }
            }
        } catch (JSONException e) {
        }
    }

    /**
     * @param key
     * @param onlyReadFields
     */
    public void setTemplateLabelFields(String key, List<String> onlyReadFields) {
        try {
            JSONObject jsonObject = templateDefinitionJSONObjects.getJSONObject(key);
            if (jsonObject != null) {
                if (!jsonObject.has("fields")) {
                    jsonObject.put("fields", new HashMap<String, Object>());
                }
                JSONObject fieldsObject = jsonObject.getJSONObject("fields");
                for (String onlyReadField : onlyReadFields) {
                    if (!fieldsObject.has(onlyReadField)) {
                        fieldsObject.put(onlyReadField, new HashMap<String, Object>());
                    }
                    fieldsObject.getJSONObject(onlyReadField).put(EnumFieldPropertyName.showType.name(),
                            DyShowType.showAsLabel);
                }
            }
        } catch (JSONException e) {
        }
    }

    /**
     * @param key
     * @param hideFields
     */
    public void setTemplateHiddenFields(String key, List<String> hideFields) {
        try {
            JSONObject jsonObject = templateDefinitionJSONObjects.getJSONObject(key);
            if (jsonObject != null) {
                if (!jsonObject.has("fields")) {
                    jsonObject.put("fields", new HashMap<String, Object>());
                }
                JSONObject fieldsObject = jsonObject.getJSONObject("fields");
                for (String hideField : hideFields) {
                    if (!fieldsObject.has(hideField)) {
                        fieldsObject.put(hideField, new HashMap<String, Object>());
                    }
                    fieldsObject.getJSONObject(hideField).put(EnumFieldPropertyName.showType.name(), DyShowType.hide);
                }
            }
        } catch (JSONException e) {
        }
    }

    /**
     * @param key
     * @param requiredFields
     */
    public void setTemplateRequiredFields(String key, List<String> requiredFields) {
        try {
            JSONObject jsonObject = templateDefinitionJSONObjects.getJSONObject(key);
            if (jsonObject != null) {
                if (!jsonObject.has("fields")) {
                    jsonObject.put("fields", new HashMap<String, Object>());
                }
                JSONObject fieldsObject = jsonObject.getJSONObject("fields");
                for (String requiredField : requiredFields) {
                    if (!fieldsObject.has(requiredField)) {
                        fieldsObject.put(requiredField, new HashMap<String, Object>());
                    }
                    JSONArray rules = new JSONArray();
                    Map<String, String> rule = new HashMap<String, String>();
                    rule.put(EnumFieldPropertyName_fieldCheckRules.value.name(), EnumDyCheckRule.notNull.getRuleKey());
                    rule.put(EnumFieldPropertyName_fieldCheckRules.label.name(),
                            EnumDyCheckRule.notNull.getRuleLabel());
                    rules.put(rule);
                    fieldsObject.getJSONObject(requiredField).put(EnumFieldPropertyName.fieldCheckRules.name(), rules);
                }
            }
        } catch (JSONException e) {
        }
    }

    /**
     * 判断定义中是否存在fieldName指定的字段
     *
     * @param fieldName
     * @return
     */
    public boolean isFieldInDefinition(String fieldName) {
        List<String> fieldNames = this.getFieldNamesOfMaintable();
        for (String fn : fieldNames) {
            if (fn.equalsIgnoreCase(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFieldInPform(String fieldName) {
        return this.doGetPformFormDefinition().doGetFormDefinitionHandler().isFieldInDefinition(fieldName);
    }

    /**
     * 指定的从表是否存在于当前的定义中
     *
     * @param subformUuid
     * @return
     */
    public boolean isSubformInDefinition(String subformUuid) {
        List<String> formUuids = this.getFormUuidsOfSubform();
        for (String fn : formUuids) {
            if (fn.equalsIgnoreCase(subformUuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 2015-12-24 yuyq
     * 指定的从表字段是否存在当前的定义中
     *
     * @param subformUuid 从表uuid
     * @param fieldName
     * @return
     */
    public boolean isSubformFieldName(String subformUuid, String fieldName) {
        // 获取从表
        JSONObject subform = this.getSubformDefinitionJson(subformUuid);
        // add by wujx 20161012 begin
        if (subform == null) {
            return false;
        }
        // add by wujx 20161012 begin
        try {
            // 从表对应字段属性
            if (subform.getJSONObject("fields").isNull(fieldName)) {
                return false;
            } else if (subform.has(EnumFieldPropertyName.refObj.name()) && subform
                    .getJSONObject(EnumFieldPropertyName.refObj.name()).getJSONObject("fields").isNull(fieldName)) {
                return false;
            } else {
                return true;
            }
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

    }

    /**
     * 2015-12-28 yuyq
     * 通过从表uuid和从表字段编码返回字段的中文名
     *
     * @param subformUuid
     * @param fieldName
     * @return
     */
    public String getSubformDisplayName(String subformUuid, String fieldName) {
        // 获取从表
        JSONObject subform = this.getSubformDefinitionJson(subformUuid);
        try {
            // 从表对应字段名称
            String displayName = null;
            if (subform.has("fields")) {
                displayName = subform.getJSONObject("fields").getJSONObject(fieldName).getString("displayName");
            } else if (subform.has(EnumFieldPropertyName.refObj.name())) {
                JSONObject refObj = subform.getJSONObject(EnumFieldPropertyName.refObj.name());
                displayName = refObj.getJSONObject("fields").getJSONObject(fieldName).getString("displayName");
            }
            return displayName;
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * @param fieldName
     * @return
     */
    public String getFormDisplayName(String fieldName) {
        return getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.displayName);
    }

    /**
     * 判断某字段是不是附件
     *
     * @param fieldName
     * @return
     */
    public boolean isInputModeEqAttach(String fieldName) {
        String inputMode = this.getPropertyValueOfInputMode(fieldName);
        if (StringUtils.isBlank(inputMode)) {
            return false;
        }
        if (DyFormConfig.InputModeUtils.isInputModeEqAttach(inputMode)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断某字段是不是日期
     *
     * @param fieldName
     * @return
     */
    public boolean isInputModeEqDate(String fieldName) {
        String inputMode = this.getPropertyValueOfInputMode(fieldName);
        if (DyFormConfig.INPUTMODE_DATE.equals(inputMode)) {
            return true;
        }
        return false;
    }

    /**
     * 判定字段是不是数字类型
     *
     * @param fieldName
     * @return
     */
    public boolean isInputModeEqNumber(String fieldName) {
        String inputMode = this.getPropertyValueOfInputMode(fieldName);
        if (DyFormConfig.INPUTMODE_NUMBER.equals(inputMode)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字段是不是文本类型
     *
     * @param fieldName
     * @return
     */
    public boolean isInputModeEqText(String fieldName) {
        String inputMode = this.getPropertyValueOfInputMode(fieldName);
        if (DyFormConfig.INPUTMODE_Text.equals(inputMode)) {
            return true;
        }
        return false;
    }

    public boolean isValueAsStringInputMode(String fieldName) {
        if (isInputModeEqText(fieldName) || isInputModeEqSelect(fieldName)) {
            return true;
        }
        return false;
    }

    public boolean isInputModeEqSelect(String fieldName) {
        String inputMode = this.getPropertyValueOfInputMode(fieldName);
        if (DyFormConfig.INPUTMODE_SELECT.equals(inputMode) || DyFormConfig.INPUTMODE_COMBOSELECT.equals(inputMode)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字段是不是组织弹出框
     *
     * @param fieldName
     * @return
     */
    public boolean isInputModeEqOrg(String fieldName) {
        String inputMode = this.getPropertyValueOfInputMode(fieldName);
        if (DyFormConfig.INPUTMODE_ORGSELECT2.equals(inputMode)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字段是不是流水号
     *
     * @param fieldName
     * @return
     */
    public boolean isInputModeEqSerialNumber(String fieldName) {
        String inputMode = this.getPropertyValueOfInputMode(fieldName);
        if (DyFormConfig.INPUTMODE_SerialNumber.equals(inputMode)
                || DyFormConfig.INPUTMODE_UNEDITSERIALUMBER.equals(inputMode)) {
            return true;
        }
        return false;
    }

    /**
     * 获取时间字段对应的时间格式
     *
     * @param fieldName
     * @return
     */
    public String getDateTimePatternByFieldName(String fieldName) {
        if (!this.isInputModeEqDate(fieldName)) {
            throw new RuntimeException("fieldName[" + fieldName + "] is not date type");
        }
        String contentFormat = this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.contentFormat);

        return getDateTimePatternByContentFormat(contentFormat);
    }

    public String getRealOfRealDisplay(String fieldName) {
        String real = null;
        if (isValueAsMap(fieldName)) {
            JSONObject realDisplay = getFieldPropertyOfJSONType(fieldName, EnumFieldPropertyName.realDisplay);
            if (realDisplay != null) {
                real = realDisplay.optString("real");
            }
        }
        return real;
    }

    public String getDisplayOfRealDisplay(String fieldName) {
        String real = null;
        if (isValueAsMap(fieldName)) {
            JSONObject realDisplay = getFieldPropertyOfJSONType(fieldName, EnumFieldPropertyName.realDisplay);
            if (realDisplay != null) {
                real = realDisplay.optString("display");
            }
        }
        return real;
    }

    /**
     * 判断值是否为创建时计算
     * 值为系统分配 ,这些值是在保存时向系统请求分配
     *
     * @param fieldName
     * @return
     */
    public boolean isValueCreateBySystemWhenSave(String fieldName) {
        String valueCreateMethod = this.getFieldPropertyOfStringType(fieldName,
                EnumFieldPropertyName.valueCreateMethod);
        return ValueCreateMethod.creatOperation.equals(valueCreateMethod);
    }

    /**
     * 判断值是否为显示时计算
     * 值为系统分配,这些值是在展示一张新表单时才向系统请求分配
     *
     * @param fieldName
     * @return
     */
    public boolean isValueCreateBySystemWhenShowNewForm(String fieldName) {
        String valueCreateMethod = this.getFieldPropertyOfStringType(fieldName,
                EnumFieldPropertyName.valueCreateMethod);
        return ValueCreateMethod.showOperation.equals(valueCreateMethod);
    }

    /**
     * 值为用户输入
     *
     * @param fieldName
     * @return
     */
    public boolean isValueCreateByUser(String fieldName) {
        String valueCreateMethod = this.getFieldPropertyOfStringType(fieldName,
                EnumFieldPropertyName.valueCreateMethod);
        return ValueCreateMethod.userImport.equals(valueCreateMethod);
    }

    /**
     * 获取所有从表的定义uuid
     *
     * @return
     */
    public List<String> getFormUuidsOfSubform() {
        return Lists.newArrayList(this.subformDefinitionJSONObjects.keySet());
    }

    /**
     * 获取指定的从表的表名
     *
     * @param formUuidOfSubform
     * @return
     * @throws JSONException
     */
    public String getTblNameOfSubform(String formUuidOfSubform) throws JSONException {
        return getSubformPropertyOfStringType(formUuidOfSubform, EnumSubformPropertyName.name);
        // return
        // this.subformDefinitionJSONObjects.getJSONObject(formUuidOfSubform).getString(EnumSubformPropertyName.name.name());
    }

    /**
     * 获取指定的从表被引用的字段名
     *
     * @param formUuidOfSubform
     * @return
     * @throws JSONException
     */
    public List<String> getFieldNamesOfSubform(String formUuidOfSubform) throws JSONException {
        return Lists
                .newArrayList(getSubformPropertyOfJSONType(formUuidOfSubform, EnumSubformPropertyName.fields).keySet());
    }

    public JSONObject getFieldDefinitions() {
        return this.fieldDefinitionJSONObjects;
    }

    public JSONObject getFormDefinition() {
        return this.formDefinitionJSONObject;
    }

    public JSONObject getSubformDefinitions() {
        return this.subformDefinitionJSONObjects;
    }

    @Override
    public String toString() {
        return this.formDefinitionJSONObject.toString();
    }

    /**
     * 字段的对应的数据库类型是不是长整型
     *
     * @param fieldName
     * @return
     */
    public boolean isDbDataTypeEqLong(String fieldName) {
        String dbDataType = this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.dbDataType);
        return DyFormConfig.DbDataTypeUtils.isDbDataTypeEqLong(dbDataType);

    }

    /**
     * 字段的对应的数据库类型是不是number
     *
     * @param fieldName
     * @return
     */
    public boolean isDbDataTypeEqNumber(String fieldName) {
        String dbDataType = this.getPropertyValueOfDbDataType(fieldName);
        return DyFormConfig.DbDataTypeUtils.isDbDataTypeEqNumber(dbDataType);
    }

    /**
     * 判断字段数据库类型是否为int类型
     *
     * @param fieldName
     * @return
     */
    public boolean isDbDataTypeEqInt(String fieldName) {
        String dbDataType = this.getPropertyValueOfDbDataType(fieldName);
        return DyFormConfig.DbDataTypeUtils.isDbDataTypeEqInt(dbDataType);
    }

    /**
     * 判断字段数据库类型是否为float类型
     *
     * @param fieldName
     * @return
     */
    public boolean isDbDataTypeEqFloat(String fieldName) {
        String dbDataType = this.getPropertyValueOfDbDataType(fieldName);
        return DyFormConfig.DbDataTypeUtils.isDbDataTypeEqFloat(dbDataType);
    }

    /**
     * 判断字段数据库类型是否为float类型
     *
     * @param fieldName
     * @return
     */
    public boolean isDbDataTypeEqDouble(String fieldName) {
        String dbDataType = this.getPropertyValueOfDbDataType(fieldName);
        return DyFormConfig.DbDataTypeUtils.isDbDataTypeEqDouble(dbDataType);
    }

    /**
     * 判断字段数据库类型是否为date类型
     *
     * @param fieldName
     * @return
     */
    public boolean isDbDataTypeEqDate(String fieldName) {
        String dbDataType = this.getPropertyValueOfDbDataType(fieldName);
        return DyFormConfig.DbDataTypeUtils.isDbDataTypeEqDate(dbDataType);
    }

    /**
     * 判断字段数据库类型是否为date类型
     *
     * @param fieldName
     * @return
     */
    public boolean isDbDataTypeEqClob(String fieldName) {
        String dbDataType = this.getPropertyValueOfDbDataType(fieldName);
        return DyFormConfig.DbDataTypeUtils.isDbDataTypeEqClob(dbDataType);
    }

    /**
     * 字段是否存在于数据表中
     *
     * @param fieldName
     * @return
     */
    public boolean isTblField(String fieldName) {
        return isFieldInDefinition(fieldName) || isSysTypeAsSystem(fieldName) || isRelationTblField(fieldName);
    }

    /**
     * 在定义中是否存在指定的字段
     *
     * @param fieldName
     * @return
     */
    public boolean isFieldInDefinitionJson(String fieldName) {
        return this.isFieldInDefinition(fieldName);
    }

    /**
     * 字段是否只读
     *
     * @param fieldName
     * @return
     */
    public boolean isShowTypeAsReadOnly(String fieldName) {
        String propertyValue = this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.showType);
        return DyShowType.readonly.equals(propertyValue);
    }

    /**
     * 判断字段是否为可编辑
     *
     * @param fieldName
     * @return
     */
    public boolean isShowTypeAsEditable(String fieldName) {
        String propertyValue = this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.showType);
        return DyShowType.edit.equals(propertyValue);
    }

    /**
     * 判断字段是否为隐藏
     *
     * @param fieldName
     * @return
     */
    public boolean isShowTypeAsHide(String fieldName) {
        String propertyValue = this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.showType);
        return DyShowType.hide.equals(propertyValue);
    }

    /**
     * 判断字段是否为隐藏
     *
     * @param fieldName
     * @return
     */
    public boolean isFieldHide(String fieldName) {
        return this.isShowTypeAsHide(fieldName);
    }

    /**
     * 判断字段是不是显示为标签
     *
     * @param fieldName
     * @return
     */
    public boolean isShowTypeAsLabel(String fieldName) {
        String propertyValue = this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.showType);
        return DyShowType.showAsLabel.equals(propertyValue);
    }

    private void setShowType(String fieldName, String showType) {
        if (this.isShowTypeAsHide(fieldName) && !DyShowType.hide.equals(showType)) {// 字段隐藏时设置其他属性
            JSONObject field = this.getFieldDefinitionJson(fieldName);
            if (field == null) {
                return;
            }
            try {
                field.put(assistedShowType, showType);
                // 前端没解析assistedShowType，直接设置到showType中
                field.put(EnumFieldPropertyName.showType.name(), showType);
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            try {
                this.addFieldProperty(fieldName, EnumFieldPropertyName.showType, showType);
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    /**
     * 设置字段为只读
     *
     * @param fieldName
     */
    public void setShowTypeAsReadOnly(String fieldName) {
        this.setShowType(fieldName, DyShowType.readonly);

    }

    /**
     * 设置字段为可编辑
     *
     * @param fieldName
     */
    public void setShowTypeAsEditable(String fieldName) {
        this.setShowType(fieldName, DyShowType.edit);
    }

    /**
     * 设置附件字段二开按钮
     *
     * @param fieldName      fieldName
     * @param secDevBtnIdStr secDevBtnIdStr
     */
    public void setSecDevBtnIdStr(String fieldName, String secDevBtnIdStr) {
        JSONObject field = this.getFieldDefinitionJson(fieldName);
        if (field == null) {
            return;
        }
        try {
            String inputMode = this.getInputMode(fieldName);
            // 图标显示
            if (DyFormConfig.INPUTMODE_ACCESSORY1.equals(inputMode)) {
                List<String> operateBtns = Lists.newArrayList();
                if (StringUtils.isNotBlank(secDevBtnIdStr)) {
                    operateBtns
                            .addAll(Arrays.asList(StringUtils.split(secDevBtnIdStr, Separator.SEMICOLON.getValue())));
                }
                field.put("operateBtns", operateBtns);
                // 是否允许添加附件
                if (operateBtns.contains("1")) {
                    field.put("allowUpload", true);
                } else {
                    field.put("allowUpload", false);
                }
                // 是否允许删除
                if (operateBtns.contains("14")) {
                    field.put("allowDelete", true);
                } else {
                    field.put("allowDelete", false);
                }
            } else if (DyFormConfig.INPUTMODE_ACCESSORY3.equals(inputMode)) {
                // 列表附件
                field.put("flowSecDevBtnIdStr", secDevBtnIdStr);
            } else if (DyFormConfig.INPUTMODE_ACCESSORYIMG.equals(inputMode)) {
                // 图片
                List<String> allOperateBtns = Lists.newArrayList();
                allOperateBtns.add("allowUpload");
                allOperateBtns.add("allowDelete");
                allOperateBtns.add("allowDownload");
                allOperateBtns.add("allowPreview");
                for (String operate : allOperateBtns) {
                    field.put(operate, false);
                }
                List<String> operateBtns = Lists.newArrayList();
                if (StringUtils.isNotBlank(secDevBtnIdStr)) {
                    operateBtns
                            .addAll(Arrays.asList(StringUtils.split(secDevBtnIdStr, Separator.SEMICOLON.getValue())));
                    for (String operate : operateBtns) {
                        field.put(operate, true);
                    }
                }
            }
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param fieldName
     * @param isHide    true为隐藏; false展示
     */
    public void setShowTypeAsHide(String fieldName, boolean isHide) {
        if (isHide) {
            JSONObject field = this.getFieldDefinitionJson(fieldName);
            if (field == null) {
                return;
            }
            try {
                field.put(assistedShowType,
                        this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.showType));
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
            this.setShowType(fieldName, DyShowType.hide);

        } else {// 展示
            JSONObject field = this.getFieldDefinitionJson(fieldName);
            if (field == null) {
                return;
            }
            try {
                if (!field.isNull(assistedShowType)) {
                    String showType = (String) field.get(assistedShowType);
                    // this.setShowType(fieldName, showType);
                    this.addFieldProperty(fieldName, EnumFieldPropertyName.showType, showType);
                } else {
                    if (this.isShowTypeAsHide(fieldName)) {
                        this.setShowType(fieldName, DyShowType.edit);
                    }

                }

            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }

        }
        /*
         * // 手机设置 try {
         * this.getFormPropertyOfJSONType(EnumFormPropertyName.mobileConfig)
         * .getJSONObject
         * (EnumFormPropertyName.subforms.toString()).getJSONObject(fieldName)
         * .put(EnumFieldPropertyName.isShow.toString(), !isHide); } catch (Exception e)
         * { logger.error(e.getMessage(), e); }
         */
    }

    /**
     * 设置字段为隐藏
     *
     * @param fieldName
     */
    public void setShowTypeAsDisable(String fieldName) {
        this.setShowType(fieldName, DyShowType.disabled);
    }

    /**
     * 设置字段显示为标签
     *
     * @param fieldName
     */
    public void setShowTypeAsLabel(String fieldName) {
        this.setShowType(fieldName, DyShowType.showAsLabel);
    }

    /**
     * 判断字段是不是必输
     *
     * @param fieldName
     * @return
     */
    public boolean isRequired(String fieldName) {
        JSONArray rules = this.getFieldPropertyOfJSONArrayType(fieldName, EnumFieldPropertyName.fieldCheckRules);
        if (rules == null || rules.length() == 0) {
            return false;
        }
        for (int i = 0; i < rules.length(); i++) {
            try {
                JSONObject rule = rules.getJSONObject(i);
                String value = rule.has(EnumFieldPropertyName_fieldCheckRules.value.name())
                        ? (String) rule.get(EnumFieldPropertyName_fieldCheckRules.value.name())
                        : null;
                if (EnumDyCheckRule.notNull.getRuleKey().equals(value)) {
                    return true;
                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return false;
    }

    /**
     * 设置必输的检验字段
     *
     * @param fieldName
     */
    public void setRequired(String fieldName, boolean required) {
        JSONArray rules = this.getFieldPropertyOfJSONArrayType(fieldName, EnumFieldPropertyName.fieldCheckRules);
        if (rules == null) {
            rules = new JSONArray();
            try {
                this.addFieldProperty(fieldName, EnumFieldPropertyName.fieldCheckRules, rules);
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        boolean isReqired = false;
        int index = 0;
        for (index = 0; index < rules.length(); index++) {
            JSONObject rule;
            try {
                rule = rules.getJSONObject(index);
                if (rule.has(EnumFieldPropertyName_fieldCheckRules.value.name()) && EnumDyCheckRule.notNull.getRuleKey()
                        .equals(rule.getString(EnumFieldPropertyName_fieldCheckRules.value.name()))) {
                    isReqired = true;
                    break;
                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }

        if (required) {
            if (isReqired) {
                return;
            } else {
                Map<String, String> rule = new HashMap<String, String>();
                rule.put(EnumFieldPropertyName_fieldCheckRules.value.name(), EnumDyCheckRule.notNull.getRuleKey());
                rule.put(EnumFieldPropertyName_fieldCheckRules.label.name(), EnumDyCheckRule.notNull.getRuleLabel());
                rules.put(rule);
            }
            return;
        } else if (isReqired && index < rules.length()) {
            rules.remove(index);
        }

    }

    /**
     * 启动签名与否
     *
     * @param enable
     */
    public void setSignature(boolean enable) {
        this.addFormProperty(EnumFormPropertyName.enableSignature,
                enable ? EnumSignature.enable.getValue() : EnumSignature.disable.getValue());
    }

    /**
     * 获取控件类型
     *
     * @param fieldName
     * @return
     */
    public String getPropertyValueOfInputMode(String fieldName) {
        return this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.inputMode);
    }

    /**
     * 获取数据类型
     *
     * @param fieldName
     * @return
     */
    public String getPropertyValueOfDbDataType(String fieldName) {
        return this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.dbDataType);
    }

    /**
     * 获取值的计算方式
     *
     * @param fieldName
     * @return
     */
    public String getPropertyValueOfValueCreateMethod(String fieldName) {
        return this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.valueCreateMethod);
    }

    /**
     * 判断字段值是不是key_value的形式
     *
     * @param fieldName
     * @return
     */
    public boolean isValueAsMap(String fieldName) {
        String inputType = this.getPropertyValueOfInputMode(fieldName);
        if (DyFormConfig.INPUTMODE_ORGSELECT.equals(inputType)
                || DyFormConfig.INPUTMODE_ORGSELECTSTAFF.equals(inputType)
                || DyFormConfig.INPUTMODE_ORGSELECTDEPARTMENT.equals(inputType)
                || DyFormConfig.INPUTMODE_ORGSELECTSTADEP.equals(inputType)
                || DyFormConfig.INPUTMODE_ORGSELECTADDRESS.equals(inputType)
                || DyFormConfig.INPUTMODE_ORGSELECTGROUP.equals(inputType)
                || DyFormConfig.INPUTMODE_ORGSELECTJOB.equals(inputType)
                || DyFormConfig.INPUTMODE_ORGSELECTPUBLICGROUP.equals(inputType)
                || DyFormConfig.INPUTMODE_ORGSELECTMYDEPT.equals(inputType)
                || DyFormConfig.INPUTMODE_ORGSELECTMYPARENTDEPT.equals(inputType)
                || DyFormConfig.INPUTMODE_ORGSELECTMYUNIT.equals(inputType)
                || DyFormConfig.INPUTMODE_ORGSELECTPUBLICGROUPSTA.equals(inputType)
                || DyFormConfig.INPUTMODE_TREESELECT.equals(inputType) || DyFormConfig.INPUTMODE_RADIO.equals(inputType)
                || DyFormConfig.INPUTMODE_CHECKBOX.equals(inputType)
                || DyFormConfig.INPUTMODE_SELECTMUTILFASE.equals(inputType)
                || DyFormConfig.INPUTMODE_JOB.equals(inputType)
                || DyFormConfig.INPUTMODE_ORGSELECT2.equals(inputType)) {
            return true;
        }
        return false;
    }

    /**
     * 为定义中的所有字段添加oldName属性且值为name对应的值
     * 将字段的oldName设置为name
     *
     * @throws JSONException
     */
    public void createOldName4AllFields() throws JSONException {
        Iterator<String> it = this.getFieldNamesOfMainform().iterator();
        while (it.hasNext()) {
            String fieldName = it.next();
            this.addFieldProperty(fieldName, EnumFieldPropertyName.oldName,
                    this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.name));
        }
    }

    /**
     * 获取指定字段用于保存真实值的真实字段
     *
     * @param fieldName
     * @return
     */
    public String getAssistedFieldNameRealValue(String fieldName) {
        JSONObject jo = this.getFieldPropertyOfJSONType(fieldName, EnumFieldPropertyName.realDisplay);
        if (jo == null) {
            return null;
        }

        try {
            return jo.getString("real");
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * 获取指定字段用于保存显示值的显示字段
     *
     * @param fieldName
     * @return
     */
    public String getAssistedFieldNameDisplayValue(String fieldName) {
        JSONObject jo = this.getFieldPropertyOfJSONType(fieldName, EnumFieldPropertyName.realDisplay);
        if (jo == null) {
            return null;
        }

        try {
            return jo.getString("display");
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 删除字段
     *
     * @param fieldName
     */
    public void removeFieldByFieldName(String fieldName) {
        this.fieldDefinitionJSONObjects.remove(fieldName);
    }

    /**
     * 批量删除字段
     *
     * @param fieldNames
     */
    public void removeFields(List<String> fieldNames) {
        for (String fieldName : fieldNames) {
            this.removeFieldByFieldName(fieldName);
        }
    }

    /**
     * 2015-12-22 yuyq
     * 删除指定从表字段
     *
     * @param formUuid
     * @param fieldName
     */
    public void removeSubformFieldName(String subFormUuid, String fieldName) {
        JSONObject formJsonObject = this.getFormDefinition();
        JSONObject subform = this.getSubformDefinitionJson(subFormUuid);
        JSONObject jsonObject;
        String name = "";
        try {
            // 获取表单的html
            String html = formJsonObject.getString("html");
            // 截取到从表uuid之前的html
            String html1 = html.substring(0, html.indexOf(subFormUuid));
            // 截取从表uuid开始结束
            String html2 = html.substring(html.indexOf(subFormUuid), html.length());
            // 截取从表uuid开始到<table>之间的html
            String html3 = html2.substring(0, html2.indexOf("</table>"));
            // 截取从<table>到结束的html
            String html4 = html2.substring(html2.indexOf("</table>"), html2.length());
            jsonObject = subform.getJSONObject("fields");
            JSONObject fieldJson = jsonObject.getJSONObject(fieldName);
            // 字段中文名
            String displayName = fieldJson.getString("displayName");
            // 将html中的对应字段名删除
            if (html3.indexOf("<th fieldname=\"" + fieldName + "\">" + displayName + "</th>") > -1) {
                html3 = html3.replaceAll("<th fieldname=\"" + fieldName + "\">" + displayName + "</th>", "");
            } else {
                html3 = html3.replaceAll("<th>" + displayName + "</th>", "");
            }
            formJsonObject.put("html", html1 + html3 + html4);
            name = subform.getString("name");
            jsonObject.remove(fieldName);
        } catch (JSONException e) {
            logger.error("该从表" + name + "对应的主表中找不到该字段：" + fieldName, e);
        }

    }

    /**
     * 2015-12-22 yuyq
     * 批量删除从表字段
     *
     * @param formUuid
     * @param fieldNames
     */
    public void removeSubformFieldName(String subFormUuid, List<String> fieldNames) {
        for (String fieldName : fieldNames) {
            removeSubformFieldName(subFormUuid, fieldName);
        }
    }

    /**
     * 删除指定的从表
     *
     * @param formUuid
     */
    public void removeSubform(String formUuid) {
        this.subformDefinitionJSONObjects.remove(formUuid);
    }

    /**
     * 批量删除从表
     *
     * @param formUuids
     */
    public void removeSubforms(List<String> formUuids) {
        for (String formUuid : formUuids) {
            this.removeSubform(formUuid);
        }
    }

    /**
     * 用户自定义字段
     *
     * @param fieldName
     * @return
     */
    public boolean isSysTypeAsCustom(String fieldName) {
        String sysType = this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.sysType);
        return sysType == null ? false
                : DyFieldSysType.custom == Integer
                .parseInt(this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.sysType));
    }

    /**
     * 根据指定的applyTo获取对应的字段名
     *
     * @param applyTo
     * @return
     */
    public List<String> getFieldNameByApplyTo(String applyTo) {
        if (applyTo2Field == null || applyTo2Field.size() == 0) {
            applyTo2Field = new HashMap<String, Set<String>>();
            for (String fieldName : this.getFieldNamesOfMainform()) {
                String fileNameKey = fieldName.toLowerCase();
                String applyTotmp = this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.applyTo);
                if (!applyTo2Field.containsKey(fileNameKey)) {
                    applyTo2Field.put(fileNameKey, new HashSet<String>());
                }
                applyTo2Field.get(fileNameKey).add(fieldName);
                if (StringUtils.isNotBlank(applyTotmp)) {
                    String[] tos = StringUtils.split(applyTotmp, Separator.SEMICOLON.getValue());
                    for (String to : tos) {
                        String toKey = to.toLowerCase();
                        if (!applyTo2Field.containsKey(toKey)) {
                            applyTo2Field.put(toKey, new HashSet<String>());
                        }
                        applyTo2Field.get(toKey).add(fieldName);
                    }
                }
            }
        }
        Set<String> fileNameSet = applyTo2Field.get(applyTo.toLowerCase());
        if (CollectionUtils.isEmpty(fileNameSet)) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(fileNameSet);
    }

    public boolean hasFieldNameByAppyTo(String applyTo) {
        return CollectionUtils.isEmpty(this.getFieldNameByApplyTo(applyTo)) ? false : true;
    }

    /**
     * 根据指定的applyTo获取对应的字段名的控件类型
     *
     * @param applyTo
     * @return
     */
    public String getFieldType(String applyTo) {
        if (inputMode2Field == null || inputMode2Field.size() == 0) {
            inputMode2Field = new HashMap<String, String>();

            for (String fieldName : this.getFieldNamesOfMainform()) {
                String inputMode = this.getPropertyValueOfInputMode(fieldName);
                String applyTotmp = this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.applyTo);
                inputMode2Field.put(fieldName.toLowerCase(), inputMode);
                if (StringUtils.isNotBlank(inputMode)) {
                    String[] tos = StringUtils.split(applyTotmp, Separator.COMMA.getValue());
                    for (String to : tos) {
                        inputMode2Field.put(fieldName.toLowerCase(), inputMode);
                    }
                }
            }
        }

        return inputMode2Field.get(applyTo.toLowerCase());
    }

    public void loadDefaultFormData() {
        DyFormFacade dyFormApiFacade = dyFormFacade;// ApplicationContextHolder.getBean(DyFormFacade.class);
        final String DEFAULTFORMDATA = "defaultFormData";
        Map<String, Object> defaultFormData;
        try {
            if (StringUtils.isNotBlank(this.getPformUuid())) {
                defaultFormData = dyFormApiFacade.getDefaultFormData(this.getPformUuid());
            } else {
                defaultFormData = dyFormApiFacade
                        .getDefaultFormData(this.getFormPropertyOfStringType(EnumFormPropertyName.uuid));
            }
            if (defaultFormData != null)
                this.addFormProperty(DEFAULTFORMDATA, defaultFormData);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }

    }

    public String getInputMode(String fieldName) {
        return this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.inputMode);
    }

    public void loadFieldDictionary(boolean lazyLoading) {
        JSONObject fieldConfigs = this.getFieldDefinitions();
        if (fieldConfigs == null) {
            return;
        }
        StopWatch timer = new StopWatch("EveryFieldLoadOptions");
        DataDictionaryService dataDictionaryService = (DataDictionaryService) ApplicationContextHolder
                .getBean("dataDictionaryService");
        CdDataStoreService cdDataStoreService = ApplicationContextHolder.getBean(CdDataStoreService.class);
        for (Object key : fieldConfigs.keySet()) {
            timer.start("字段[" + key + "]加载选项数据");
            try {
                loadFieldDictionaryOptionSet(dataDictionaryService, cdDataStoreService, (String) key, Maps.newHashMap(),
                        lazyLoading);
            } catch (Exception e) {
                logger.error("字段[{}]选项加载数据异常：", key, e);
                continue;
            } finally {
                timer.stop();
            }
        }
        // logger.info("字段加载选项数据结束，执行情况：{}", timer.prettyPrint());
    }

    /**
     * 加载字段数据字典
     *
     * @param dataDictionaryService
     * @param viewComponentService
     * @param cdDataStoreService
     * @param fieldName
     */
    public void loadFieldDictionaryOptionSet(DataDictionaryService dataDictionaryService,
                                             CdDataStoreService cdDataStoreService, String fieldName, Map<String, Object> params, boolean lazyLoading) {
        // String fieldName = (String) key;
        String inputMode = this.getInputMode(fieldName);
        if (DyFormConfig.INPUTMODE_CHECKBOX.equals(inputMode) || DyFormConfig.INPUTMODE_RADIO.equals(inputMode)
                || DyFormConfig.INPUTTYPE_TAGGROUP.equals(inputMode)) {
            String optionDataSource = this.getFieldPropertyOfStringType(fieldName,
                    EnumFieldPropertyName.optionDataSource);
            if (DyFormConfig.DyDataSourceType.dataDictionary.equals(optionDataSource)) {// 从数据字段取值,这里取得数据字段树
                String dictCode = this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.dictCode);
                if (dictCode != null && dictCode.indexOf(":") != -1) {
                    dictCode = dictCode.split(":")[0];
                }
                List<DataDictionary> dicts = new ArrayList<DataDictionary>();
                JSONObject optionMap = new JSONObject();
                if (dictCode != null && dictCode.length() > 0) {
                    List<DataDictionary> dictsx = dataDictionaryService.getDataDictionariesByType(dictCode);
                    // Collections.sort(dictsx, DataDictionaryService.DictAscComparator);
                    if (dictsx != null && dictsx.size() > 0) {
                        for (DataDictionary dict : dictsx) {
                            dicts.add(dict);
                            try {
                                optionMap.put(dict.getType() == null ? dict.getCode() : dict.getType(), dict.getName());
                            } catch (JSONException ex) {
                                logger.error(ex.getMessage(), ex);
                            }
                        }
                    }
                }

                this.setFieldDictionaryOptionSet(fieldName, dicts);
                this.setFieldDictionaryOptionMap(fieldName, optionMap);
            } else if (DyFormConfig.DyDataSourceType.dataSource.equals(optionDataSource)) {
                // 数据源
                // 延时加载
                if (lazyLoading) {
                    JSONObject jsonObject = getFieldDefinitionJson(fieldName);
                    try {
                        jsonObject.put("lazyLoading", lazyLoading);
                    } catch (JSONException e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    String dataSourceId = this.getFieldPropertyOfStringType(fieldName,
                            EnumFieldPropertyName.dataSourceId);
                    String dataSourceFieldName = this.getFieldPropertyOfStringType(fieldName,
                            EnumFieldPropertyName.dataSourceFieldName);
                    String dataSourceDisplayName = this.getFieldPropertyOfStringType(fieldName,
                            EnumFieldPropertyName.dataSourceDisplayName);
                    DataStoreParams storeParams = new DataStoreParams();
                    storeParams.setParams(params);
                    storeParams.setDataStoreId(dataSourceId);
                    List<Map<String, Object>> queryItems = cdDataStoreService.loadDataWithNewTransaction(storeParams)
                            .getData();
                    JSONObject optionSet = new JSONObject();

                    try {
                        for (Map<String, Object> item : queryItems) {
                            Object dataSourceFieldNameObject = item.get(dataSourceFieldName);

                            Object dataSourceDisplayNameObject = item.get(dataSourceDisplayName);
                            String optionKey = (String) FormDataHandler.convertData2DbType(
                                    DyFormConfig.DbDataType._string, dataSourceFieldNameObject, null);
                            String optionValue = (String) FormDataHandler.convertData2DbType(
                                    DyFormConfig.DbDataType._string, dataSourceDisplayNameObject, null);
                            if (optionKey == null || optionValue == null) {
                                continue;
                            }
                            optionSet.put(optionKey, optionValue);
                        }
                    } catch (JSONException e) {
                        logger.error(e.getMessage(), e);
                    } catch (ParseException e) {
                        logger.error(e.getMessage(), e);
                    }

                    this.setFieldDictionaryOptionSet(fieldName, optionSet);
                    this.setFieldDictionaryOptionMap(fieldName, optionSet);
                }
            }
        } else if (DyFormConfig.INPUTMODE_SELECTMUTILFASE.equals(inputMode)
                || DyFormConfig.INPUTMODE_COMBOSELECT.equals(inputMode)
                || DyFormConfig.INPUTMODE_SELECT.equals(inputMode)) {
            String optionDataSource = this.getFieldPropertyOfStringType(fieldName,
                    EnumFieldPropertyName.optionDataSource);
            if (DyFormConfig.DyDataSourceType.dataDictionary.equals(optionDataSource)) {// 从数据字段取值,这里取得数据字段树
                String dictCode = this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.dictCode);
                if (dictCode != null && dictCode.indexOf(":") != -1) {
                    dictCode = dictCode.split(":")[0];
                }
                JSONArray dicts = new JSONArray();
                JSONObject optionMap = new JSONObject();
                if (dictCode != null && dictCode.length() > 0) {
                    List<DataDictionary> dictsx = dataDictionaryService.getDataDictionariesByType(dictCode);
                    // Collections.sort(dictsx, DataDictionaryService.DictAscComparator); //按seq排序
                    for (DataDictionary dd : dictsx) {
                        try {
                            if (DyFormConfig.INPUTMODE_COMBOSELECT.equals(inputMode)) {
                                List<DataDictionary> children = dataDictionaryService
                                        .getDataDictionariesByParentUuid(dd.getUuid());
                                // Collections.sort(children, DataDictionaryService.DictAscComparator);
                                for (DataDictionary dd2 : children) {
                                    JSONObject dict = new JSONObject();
                                    dict.put(EnumDataSourceValueField.VALUE_FIELD.getField(), dd2.getCode());
                                    dict.put(EnumDataSourceValueField.NAME_FIELD.getField(), dd2.getName());
                                    dict.put(EnumDataSourceValueField.DATA_FIELD.getField(), dd2.getUuid());// 返回UUID
                                    dict.put(EnumDataSourceValueField.GROUP_FIELD.getField(), dd.getName());
                                    optionMap.put(null == dd2.getCode() ? dd2.getType() : dd2.getCode(), dd2.getName());
                                    dicts.put(dict);
                                }
                            } else {
                                JSONObject dict = new JSONObject();
                                dict.put(EnumDataSourceValueField.VALUE_FIELD.getField(), dd.getCode());
                                dict.put(EnumDataSourceValueField.NAME_FIELD.getField(), dd.getName());
                                dict.put(EnumDataSourceValueField.DATA_FIELD.getField(), dd.getUuid());// 返回UUID
                                optionMap.put(null == dd.getCode() ? dd.getType() : dd.getCode(), dd.getName());
                                dicts.put(dict);
                            }
                        } catch (JSONException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
                this.setFieldDictionaryOptionSet(fieldName, dicts);
                this.setFieldDictionaryOptionMap(fieldName, optionMap);
            } else if (DyFormConfig.DyDataSourceType.dataSource.equals(optionDataSource)) {
                // 数据仓库
                // 延时加载，废弃下拉框数据仓库不进行延时加载
                if (lazyLoading && !DyFormConfig.INPUTMODE_SELECTMUTILFASE.equals(inputMode)) {
                    JSONObject jsonObject = getFieldDefinitionJson(fieldName);
                    try {
                        jsonObject.put("lazyLoading", lazyLoading);
                    } catch (JSONException e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    String dataSourceId = this.getFieldPropertyOfStringType(fieldName,
                            EnumFieldPropertyName.dataSourceId);
                    String dataSourceFieldName = this.getFieldPropertyOfStringType(fieldName,
                            EnumFieldPropertyName.dataSourceFieldName);
                    String dataSourceDisplayName = this.getFieldPropertyOfStringType(fieldName,
                            EnumFieldPropertyName.dataSourceDisplayName);
                    String dataSourceGroup = this.getFieldPropertyOfStringType(fieldName,
                            EnumFieldPropertyName.dataSourceGroup);
                    String defaultCondition = this.getFieldPropertyOfStringType(fieldName,
                            EnumFieldPropertyName.defaultCondition);

                    DataStoreParams storeParams = new DataStoreParams();
                    storeParams.setParams(params);
                    storeParams.setDataStoreId(dataSourceId);
                    if (StringUtils.isNotBlank(defaultCondition)) {
                        Condition defaultSqlCondition = new Condition();
                        defaultSqlCondition.setSql(defaultCondition);
                        storeParams.getCriterions().add(defaultSqlCondition);
                    }

                    List<Map<String, Object>> queryItems = new ArrayList<Map<String, Object>>();
                    queryItems.addAll(cdDataStoreService.loadDataWithNewTransaction(storeParams).getData());
                    // List<Map<String, Object>> queryItems =
                    // viewComponentService.loadAllDataWithNewTransaction(dataSourceId).getData();

                    JSONArray optionsSet = new JSONArray();
                    JSONObject optionMap = new JSONObject();
                    try {
                        for (Map<String, Object> item : queryItems) {
                            Object dataSourceFieldNameObject = item.get(dataSourceFieldName);
                            Object dataSourceDisplayNameObject = item.get(dataSourceDisplayName);
                            JSONObject optionSet = new JSONObject();
                            String value = (String) FormDataHandler.convertData2DbType(DyFormConfig.DbDataType._string,
                                    dataSourceFieldNameObject, null);
                            String name = (String) FormDataHandler.convertData2DbType(DyFormConfig.DbDataType._string,
                                    dataSourceDisplayNameObject, null);
                            optionSet.put(EnumDataSourceValueField.VALUE_FIELD.getField(), value);
                            optionSet.put(EnumDataSourceValueField.DATA_FIELD.getField(), item);
                            optionSet.put(EnumDataSourceValueField.NAME_FIELD.getField(), name);
                            optionMap.put(value, name);
                            if (DyFormConfig.INPUTMODE_COMBOSELECT.equals(inputMode)) {
                                Object dataSourceGroupObject = item.get(dataSourceGroup);
                                optionSet.put(EnumDataSourceValueField.GROUP_FIELD.getField(),
                                        (String) FormDataHandler.convertData2DbType(DyFormConfig.DbDataType._string,
                                                dataSourceGroupObject, null));
                            }
                            optionsSet.put(optionSet);
                        }
                    } catch (JSONException e) {
                        logger.error(e.getMessage(), e);
                    } catch (ParseException e) {
                        logger.error(e.getMessage(), e);
                    }

                    this.setFieldDictionaryOptionMap(fieldName, optionMap);
                    this.setFieldDictionaryOptionSet(fieldName, optionsSet);
                }
            }
        } else if (DyFormConfig.INPUTMODE_ORGSELECT2.equals(inputMode)) {
        } else if (DyFormConfig.INPUTTYPE_CHAINED.equals(inputMode)) {
            String optionDataSource = this.getFieldPropertyOfStringType(fieldName,
                    EnumFieldPropertyName.optionDataSource);
            if (DyFormConfig.DyDataSourceType.dataDictionary.equals(optionDataSource)) {// 从数据字段取值,这里取得数据字段树
                String dictCode = this.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.dictCode);
                if (dictCode != null && dictCode.indexOf(":") != -1) {
                    dictCode = dictCode.split(":")[0];
                }
                JSONObject optionMap = new JSONObject();
                String dicTitles = this.getFieldPropertyOfStringType(fieldName, "dicTitles");
                List<TreeNode> dicts = chainedDataDict(dictCode, dicTitles, dataDictionaryService);
                this.setFieldDictionaryOptionSet(fieldName, dicts);
                this.setFieldDictionaryOptionMap(fieldName, optionMap);
            } else if (DyFormConfig.DyDataSourceType.dataSource.equals(optionDataSource)) {
                // 数据仓库
                // 延时加载
                if (lazyLoading) {
                    JSONObject jsonObject = getFieldDefinitionJson(fieldName);
                    try {
                        jsonObject.put("lazyLoading", lazyLoading);
                    } catch (JSONException e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    List<TreeNode> treeNodes = new ArrayList<TreeNode>();
                    JSONArray dataSources = getFieldPropertyOfJSONArrayType(fieldName,
                            EnumFieldPropertyName.dataSources);
                    try {
                        treeNodes = chainedDataSource(dataSources, params, cdDataStoreService);
                    } catch (ParseException | JSONException e) {
                        logger.warn("fieldName:" + fieldName, e);
                    }
                    this.setFieldDictionaryOptionSet(fieldName, treeNodes);
                }
            }
        }
    }

    public void setEditableSubformOperateBtns(String formUuid, Set<String> operationBtnCodes) {
        try {
            JSONObject subform = this.getSubformDefinitionJson(formUuid);
            if (subform != null) {
                subform.put(EnumSubformPropertyName.editableOperateBtns.name(), operationBtnCodes);
            }
        } catch (Exception e) {
            logger.error("设置从表{}的操作按钮可编辑异常：", formUuid, e);
        }

    }

    /**
     * 添加数据字典反向集合value:label
     */
    public void setFieldDictionaryOptionMap(String fieldName, JSONObject dicts) {
        try {
            this.addFieldProperty(fieldName, EnumFieldPropertyName.optionMap.name(), dicts);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public JSONObject getFieldDictionaryOptionMap(String fieldName) {
        return getFieldPropertyOfJSONType(fieldName, EnumFieldPropertyName.optionMap);
    }

    public String getFieldDictionaryRealValueByDisplayValue(String fieldName, String fieldValue) {
        JSONObject jsonObject = getFieldDictionaryOptionMap(fieldName);
        if (false == (jsonObject == null || JSONObject.NULL.equals(jsonObject))) {
            Iterator realValues = jsonObject.keys();
            while (realValues.hasNext()) {
                String realValue = (String) realValues.next();
                String displayValue = (String) jsonObject.optString(realValue);
                if (StringUtils.equals(displayValue, fieldValue)) {
                    return realValue;
                }
            }
        }
        return null;
    }

    /**
     * 添加数据字典集合
     */
    public void setFieldDictionaryOptionSet(String fieldName, Object dicts) {
        try {
            this.addFieldProperty(fieldName, "optionSet", dicts);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 获取数据字典集合
     */
    public Object getFieldDictionaryOptionSet(String fieldName) {
        return this.getFieldPropertyOfObjectType(fieldName, "optionSet");
    }

    /**
     * 添加职位的数据字典
     */
    public void loadFieldJobDictionary() {
        JSONObject fieldConfigs = this.getFieldDefinitions();
        if (fieldConfigs == null) {
            return;
        }

        for (Object key : fieldConfigs.keySet()) {
            String fieldName = (String) key;
            String inputMode = this.getInputMode((String) key);
            if (!(DyFormConfig.INPUTMODE_JOB.equals(inputMode))) {
                continue;
            }

            // OrgUserVo userVo = orgApiFacade.getUserVoById(currUserId);
            List<DataDictionary> dicts = new ArrayList<DataDictionary>();
            // if (userVo != null && CollectionUtils.isNotEmpty(userVo.getJobList())) {
            // for (OrgUserJobDto job : userVo.getJobList()) {
            // OrgTreeNodeDto jobDto = job.getOrgTreeNodeDto();
            // DataDictionary dict = new DataDictionary();
            // dict.setCode((String) jobDto.getEleId());
            // dict.setName((String) jobDto.getName());
            // dicts.add(dict);
            // }
            // }
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            // 从当前用户信息中取职位信息
            if (userDetails != null && !(userDetails instanceof InternetUserDetails)) {
                List<OrgTreeNodeDto> jobs = Lists.newArrayList();
                OrgTreeNodeDto mainJob = userDetails.getMainJob();
                if (mainJob != null) {
                    jobs.add(mainJob);
                }
                List<OrgTreeNodeDto> otherJobs = userDetails.getOtherJobs();
                if (CollectionUtils.isNotEmpty(otherJobs)) {
                    jobs.addAll(otherJobs);
                }
                for (OrgTreeNodeDto orgElementVo : jobs) {
                    DataDictionary dict = new DataDictionary();
                    dict.setCode(orgElementVo.getEleId());
                    dict.setName(orgElementVo.getName());
                    dicts.add(dict);
                }
            } else {
                // 调用组织接口取职位信息
                String currUserId = SpringSecurityUtils.getCurrentUserId();
                List<OrgElementVo> jobs = orgApiFacade.getUserJobElementByUserId(currUserId);
                if (CollectionUtils.isNotEmpty(jobs)) {
                    for (OrgElementVo orgElementVo : jobs) {
                        DataDictionary dict = new DataDictionary();
                        dict.setCode(orgElementVo.getId());
                        dict.setName(orgElementVo.getName());
                        dicts.add(dict);
                    }
                }
            }

            this.setFieldDictionaryOptionSet(fieldName, dicts);

        }

    }

    public void setBlockHide(String blockCode, boolean isHide) {
        if (blockCode == null || blockCode.trim().length() == 0) {
            return;
        }
        if (this.blockDefinitionJSONObjects.isNull(blockCode)) {
            return;
        }
        try {
            this.blockDefinitionJSONObjects.getJSONObject(blockCode).put("hide", isHide);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public JSONObject getBlockDefinitionJSONObjects() {
        return blockDefinitionJSONObjects;
    }

    public JSONObject getLayOutDefinitionJSONObjects() {
        return layOutDefinitionJSONObjects;
    }

    public void hideSubformOperateBtn(final String formUuid) {
        try {
            this.addSubformProperty(formUuid, EnumSubformPropertyName.hideButtons,
                    EnumHideSubFormOperateBtn.hide.getValue());
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void showSubformOperateBtn(String formUuid) {
        try {
            this.addSubformProperty(formUuid, EnumSubformPropertyName.hideButtons,
                    EnumHideSubFormOperateBtn.show.getValue());
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 返回表单所有的formUuid
     *
     * @return
     */
    public List<String> getFormUuids() {
        List<String> formUuids = new ArrayList<String>();
        List<String> subformUuids = this.getFormUuidsOfSubform();
        String mainformUuid = this.getFormUuid();
        formUuids.add(mainformUuid);
        if (subformUuids != null) {
            formUuids.addAll(subformUuids);
        }
        return formUuids;
    }

    public String getFormId() {
        return this.getFormPropertyOfStringType(EnumFormPropertyName.id);
    }

    public String getFormVersion() {
        return this.getFormPropertyOfStringType(EnumFormPropertyName.version);
    }

    public String getFormUuid() {
        return this.getFormPropertyOfStringType(EnumFormPropertyName.uuid);
    }

    /**
     * 返回所有的字段名
     *
     * @return
     */
    public List<String> getFieldNames() {
        JSONObject fieldConfigs = this.getFieldDefinitions();
        if (fieldConfigs == null) {
            return Collections.emptyList();
        }

        List<String> fieldNames = new ArrayList<String>();

        fieldNames.addAll(fieldConfigs.keySet());
        return fieldNames;
    }

    /**
     * 获取附件字段名称
     *
     * @return
     */
    public List<String> getFieldNamesOfFile() {
        List<String> fieldNames = new ArrayList<String>();
        for (Object key : this.getFieldNames()) {
            String fieldName = (String) key;
            if (this.isInputModeEqAttach(fieldName)) {
                fieldNames.add(fieldName);
            }
        }
        return fieldNames;
    }

    public boolean isSysField(String fieldName) {
        for (String sysFieldName : this.getSysFieldNames()) {
            if (sysFieldName.equalsIgnoreCase(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getSubformUuidsByMappingName(String mappingName) {
        if (StringUtils.isBlank(mappingName)) {
            return new ArrayList<String>();
        }
        List<String> applyToSubformUuids = new ArrayList<String>();

        List<String> subformUuids = getFormUuidsOfSubform();

        if (CollectionUtils.isNotEmpty(subformUuids)) {
            for (String subformUuid : subformUuids) {
                // JSONObject jsonObj = getSubformJSONObject(subformUuid);

                // jsonObj.getString(EnumSubformPropertyName.name.toString())
                if (mappingName.equals(getSubformPropertyOfStringType(subformUuid, EnumSubformPropertyName.name))) {
                    applyToSubformUuids.add(subformUuid);
                    continue;
                }
                // jsonObj.getString(EnumSubformPropertyName.applyTo.toString());
                String applyTotmp = getSubformPropertyOfStringType(subformUuid, EnumSubformPropertyName.applyTo);
                if (StringUtils.isNotBlank(applyTotmp)) {
                    String[] tos = StringUtils.split(applyTotmp, Separator.SEMICOLON.getValue());
                    for (String to : tos) {
                        if (mappingName.equals(to)) {
                            applyToSubformUuids.add(subformUuid);
                            break;
                        }
                    }
                }
            }
        }
        return applyToSubformUuids;
    }

    public void validate() {
        List<String> fieldNames = this.getFieldNames();
        List<String> duplicateFieldName = new ArrayList<String>();
        for (String fieldName1 : fieldNames) {
            duplicateFieldName.clear();
            for (String fieldName2 : fieldNames) {
                if (fieldName1.trim().equalsIgnoreCase(fieldName2.trim())) {
                    duplicateFieldName.add(fieldName2);
                    if (duplicateFieldName.size() > 1) {
                        throw new FormDefinitionUpdateException(
                                "Duplicate fieldName " + StringUtils.join(duplicateFieldName.toArray(), '-'));
                    }

                }
            }
        }
    }

    public DyformFieldDefinition getFieldDefinition(String fieldName) {
        return new FieldDefinition(fieldName, this);
    }

    public DyformSubformFormDefinition getSubformDefinition(String formUuidOfSubform) {
        if (subformDefinitionMap.containsKey(formUuidOfSubform)) {
            return subformDefinitionMap.get(formUuidOfSubform);
        }
        DyformSubformFormDefinition dsfd = new SubformFormDefinition(formUuidOfSubform, this);
        subformDefinitionMap.put(formUuidOfSubform, dsfd);
        return dsfd;
    }

    public String getFormType() {
        return formType;
    }

    public String getName() {
        return name;
    }

    public String getPformUuid() {
        return pformUuid;
    }

    /**
     * @return 表单默认展示单据
     */
    public String getDefaultVFormUuid() {
        if (DyformTypeEnum.isVform(getFormType())) {
            return getFormUuid();
        } else if (DyformTypeEnum.isPform(getFormType())) {
            return getFormPropertyOfStringType(EnumFormPropertyName.defaultVFormUuid);
        }
        FormDefinition pFormDefinition = doGetPformFormDefinition();
        if (pFormDefinition != null && pFormDefinition.doGetFormDefinitionHandler() != null) {
            return pFormDefinition.doGetFormDefinitionHandler()
                    .getFormPropertyOfStringType(EnumFormPropertyName.defaultVFormUuid);
        }
        return null;
    }

    /**
     * @return 表单默认手机单据
     */
    public String getDefaultMFormUuid() {
        if (DyformTypeEnum.isMform(getFormType())) {
            return getFormUuid();
        } else if (DyformTypeEnum.isPform(getFormType())) {
            return getFormPropertyOfStringType(EnumFormPropertyName.defaultMFormUuid);
        }
        FormDefinition pFormDefinition = doGetPformFormDefinition();
        if (pFormDefinition != null && pFormDefinition.doGetFormDefinitionHandler() != null) {
            return pFormDefinition.doGetFormDefinitionHandler()
                    .getFormPropertyOfStringType(EnumFormPropertyName.defaultMFormUuid);
        }
        return null;
    }

    public Collection<? extends String> getFieldNamesOfMainformInPform() {
        List<String> fieldNames = this.getFieldNamesOfMaintable();
        List<String> fieldNamesInPform = new ArrayList<String>();
        for (String fieldName : fieldNames) {
            if (this.isFieldInPform(fieldName)) {
                fieldNamesInPform.add(fieldName);
            }
        }
        return fieldNamesInPform;
    }

    public void setTabHide(String tabName, boolean isHide) {
        if (tabName == null || tabName.trim().length() == 0) {
            return;
        }
        Iterator<String> it = layOutDefinitionJSONObjects.keys();
        while (it.hasNext()) {
            String tabCode = it.next();
            try {
                JSONObject subtabs = layOutDefinitionJSONObjects.getJSONObject(tabCode).getJSONObject("subtabs");
                Iterator<String> subtabIterator = subtabs.keys();
                while (subtabIterator.hasNext()) {
                    String key = subtabIterator.next();

                    String name = subtabs.getJSONObject(key).getString("name");
                    if (StringUtils.equals(tabName, name)) {
                        subtabs.getJSONObject(key).put("hide", isHide);
                    }
                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    public void setImpShow(String formUuid, boolean isHide) {
        try {
            this.addSubformProperty(formUuid, EnumSubformPropertyName.showImpButton2, String.valueOf(isHide));
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void setExpShow(String formUuid, boolean isHide) {
        try {
            this.addSubformProperty(formUuid, EnumSubformPropertyName.showExpButton2, String.valueOf(isHide));
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void showSubformOperateBtn(String formUuid, EnumHideSubFormOperateBtn btn) {
        try {
            this.addSubformProperty(formUuid, EnumSubformPropertyName.hideButtons2, btn.getValue());
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param realValue
     * @return
     */
    public String getDisplayValue(String fieldName, String realValue) {
        JSONObject jsonObject = getFieldDictionaryOptionMap(fieldName);
        if (jsonObject == null || JSONObject.NULL.equals(jsonObject)) {
            return null;
        }
        return jsonObject.optString(realValue);
    }

    /**
     * 复选框、标签控件
     * <p>
     * key  复选框、标签控件
     * value 对应的显示值
     *
     * @return
     */
    public Map<String, String> getMultiFields() {
        Map<String, String> result = new HashMap<String, String>();
        List<String> fieldNames = getFieldNamesOfMaintable();
        for (String fieldName : fieldNames) {
            String inputMode = this.getInputMode(fieldName);
            if (DyFormConfig.INPUTMODE_CHECKBOX.equals(inputMode) || DyFormConfig.INPUTTYPE_TAGGROUP.equals(inputMode)
                    || DyFormConfig.INPUTMODE_SELECT.equals(inputMode)) {
                // 获取显示值字段
                String showField = null;
                try {
                    showField = fieldDefinitionJSONObjects.getJSONObject(fieldName).getJSONObject("realDisplay")
                            .getString("display");
                    if (showField != null) {
                        showField.toLowerCase();
                    }
                } catch (JSONException e) {
                    // no show field ignore
                }
                result.put(fieldName.toLowerCase(), showField);
            }
        }
        return result;
    }

    public synchronized Map<String, ValidatorFieldContent> getValidatorContent(
            Map<String /* 字段名称 */, Set<Validator> /* 校验器 */> customValidators) {
        if (null == validatorContents) {
            this.loadFieldDictionary(false); // 加载数据字典
            validatorContents = new HashMap<String, ValidatorFieldContent>();
            try {
                for (Object fieldName : fieldDefinitionJSONObjects.keySet()) {
                    String localFieldName = (String) fieldName;
                    FieldDefinition fieldDefinition = new FieldDefinition(localFieldName, this);
                    ValidatorFieldContent validatorFieldContent = new ValidatorFieldContent();
                    validatorFieldContent.setFieldDefinition(fieldDefinition);
                    validatorContents.put(localFieldName, validatorFieldContent);
                    JSONArray rules = getFieldPropertyOfJSONArrayType(localFieldName,
                            EnumFieldPropertyName.fieldCheckRules);
                    if (rules != null && false == JSONObject.NULL.equals(rules)) {
                        for (int i = 0; i < rules.length(); i++) {
                            JSONObject rule = rules.getJSONObject(i);
                            String value = rule.optString("value");
                            if (StringUtils.isNotBlank(value)) {
                                String label = rule.optString("label");
                                if ("1".equals(value)) {// 非空(约束)
                                    validatorFieldContent.getValidators()
                                            .add(new ValidatorParams(new ValidatorMaps.RequiredValiator()));
                                } else if ("5".equals(value)) {// 唯一(约束)
                                    validatorFieldContent.getValidators()
                                            .add(new ValidatorParams(new ValidatorMaps.UniqueValiator()));
                                } else if ("10".equals(value)) {// 普通(文本校验)
                                    int length = NumberUtils.toInt(fieldDefinition.getLength());
                                    validatorFieldContent.getValidators()
                                            .add(new ValidatorParams(new ValidatorMaps.MaxLengthValidator(length)));
                                } else if ("11".equals(value)) {// url(文本校验)
                                    validatorFieldContent.getValidators()
                                            .add(new ValidatorParams(new ValidatorMaps.URLValidator()));
                                } else if ("12".equals(value)) {// email(文本校验)
                                    validatorFieldContent.getValidators()
                                            .add(new ValidatorParams(new ValidatorMaps.EmailValidator()));
                                } else if ("13".equals(value)) {// idCard(文本校验)
                                    validatorFieldContent.getValidators()
                                            .add(new ValidatorParams(new ValidatorMaps.IdCardValidator()));
                                } else if ("14".equals(value)) {// 固定电话(文本校验)
                                    validatorFieldContent.getValidators()
                                            .add(new ValidatorParams(new ValidatorMaps.TelPhoneValidator()));
                                } else if ("15".equals(value)) {// 手机(文本校验)
                                    validatorFieldContent.getValidators()
                                            .add(new ValidatorParams(new ValidatorMaps.MobilePhoneValidator()));
                                } else if ("n12".equals(value)) {// 双精度浮点数(数字校验)
                                    validatorFieldContent.getValidators()
                                            .add(new ValidatorParams(new ValidatorMaps.DoubleValidator()));
                                } else if ("n13".equals(value)) {// 整数(数字校验)
                                    validatorFieldContent.getValidators()
                                            .add(new ValidatorParams(new ValidatorMaps.IntegerValidator()));
                                } else if ("n14".equals(value)) {// 长整数(数字校验)
                                    validatorFieldContent.getValidators()
                                            .add(new ValidatorParams(new ValidatorMaps.IntegerValidator()));
                                } else if ("n15".equals(value)) {// 浮点数(数字校验)
                                    validatorFieldContent.getValidators()
                                            .add(new ValidatorParams(new ValidatorMaps.FloatValidator()));
                                } else if ("max".equals(value)) {// 最大值(数字校验)
                                    validatorFieldContent.getValidators().add(new ValidatorParams(
                                            new ValidatorMaps.MaxValidator(Integer.parseInt(label))));
                                } else if ("min".equals(value)) {// 最小值(数字校验)
                                    validatorFieldContent.getValidators().add(new ValidatorParams(
                                            new ValidatorMaps.MinValidator(Integer.parseInt(label))));
                                }
                            }
                        }
                    }
                    // JSONObject fieldJsonObject = getFieldDefinitionJson(localFieldName);
                    String inputMode = fieldDefinition.getInputMode();
                    if (DyFormConfig.INPUTMODE_NUMBER.equals(inputMode)) {
                        if (DbDataTypeUtils.isDbDataTypeEqInt(fieldDefinition.getDbDataType())) {
                            validatorFieldContent.getValidators()
                                    .add(new ValidatorParams(new ValidatorMaps.IntegerValidator()));
                            validatorFieldContent.getValidators()
                                    .add(new ValidatorParams(new ValidatorMaps.MaxLengthValidator(9)));
                        } else if (DbDataTypeUtils.isDbDataTypeEqLong(fieldDefinition.getDbDataType())) {
                            validatorFieldContent.getValidators()
                                    .add(new ValidatorParams(new ValidatorMaps.IntegerValidator()));
                            validatorFieldContent.getValidators()
                                    .add(new ValidatorParams(new ValidatorMaps.MaxLengthValidator(16)));
                        } else if (DbDataTypeUtils.isDbDataTypeEqFloat(fieldDefinition.getDbDataType())) {
                            validatorFieldContent.getValidators()
                                    .add(new ValidatorParams(new ValidatorMaps.FloatValidator()));
                            validatorFieldContent.getValidators()
                                    .add(new ValidatorParams(new ValidatorMaps.MaxLengthValidator(12)));
                        } else if (DbDataTypeUtils.isDbDataTypeEqDouble(fieldDefinition.getDbDataType())) {
                            validatorFieldContent.getValidators()
                                    .add(new ValidatorParams(new ValidatorMaps.DoubleValidator()));
                            validatorFieldContent.getValidators()
                                    .add(new ValidatorParams(new ValidatorMaps.MaxLengthValidator(18)));
                        }
                    } else if (DyFormConfig.INPUTMODE_CHECKBOX.equals(inputMode)
                            && "1".equals(getFieldPropertyOfStringType(localFieldName, "selectMode"))) {
                        Set<String> sets = new HashSet<String>();
                        JSONObject singleCheckContent = new JSONObject(
                                getFieldPropertyOfStringType(localFieldName, "singleCheckContent"));
                        JSONObject singleUnCheckContent = new JSONObject(
                                getFieldPropertyOfStringType(localFieldName, "singleUnCheckContent"));
                        if (singleCheckContent != null && !JSONObject.NULL.equals(singleCheckContent)) {
                            sets.addAll(singleCheckContent.keySet());
                        }
                        if (singleUnCheckContent != null && !JSONObject.NULL.equals(singleUnCheckContent)) {
                            sets.addAll(singleUnCheckContent.keySet());
                        }
                        validatorFieldContent.getValidators()
                                .add(new ValidatorParams(new ValidatorMaps.DictValidator(sets)));
                    } else if (DyFormConfig.INPUTMODE_SELECTMUTILFASE.equals(inputMode)
                            || DyFormConfig.INPUTMODE_CHECKBOX.equals(inputMode)
                            || DyFormConfig.INPUTMODE_RADIO.equals(inputMode)) { // 数据字典合规性校验
                        Object opt = getFieldPropertyOfObjectType(localFieldName,
                                EnumFieldPropertyName.optionSet.name());
                        if (opt instanceof JSONObject) {
                            validatorFieldContent.getValidators().add(
                                    new ValidatorParams(new ValidatorMaps.DictValidator(((JSONObject) opt).keySet())));
                        } else if (opt instanceof JSONArray) {
                            JSONArray jsonArray = (JSONArray) opt;
                            if (jsonArray != null && jsonArray.length() > 0) {
                                Set<String> sets = new HashSet<String>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject.length() == 1) {
                                        sets.add((String) jsonObject.keys().next());
                                    } else {
                                        sets.add((String) jsonObject.optString("code"));
                                    }
                                }
                                validatorFieldContent.getValidators()
                                        .add(new ValidatorParams(new ValidatorMaps.DictValidator(sets)));
                            }
                        } else if (opt instanceof List) {
                            List<DataDictionary> jsonArray = (List<DataDictionary>) opt;
                            if (jsonArray != null && false == jsonArray.isEmpty()) {
                                Set<String> sets = new HashSet<String>();
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    DataDictionary jsonObject = jsonArray.get(i);
                                    sets.add(jsonObject.getCode());
                                }
                                validatorFieldContent.getValidators()
                                        .add(new ValidatorParams(new ValidatorMaps.DictValidator(sets)));
                            }
                        }
                    }
                    if (customValidators != null && customValidators.containsKey(localFieldName)) {
                        for (Validator validator : customValidators.get(localFieldName)) {
                            validatorFieldContent.getValidators().add(new ValidatorParams(validator));
                        }
                    }
                }
                for (Object fieldName : subformDefinitionJSONObjects.keySet()) {
                    // 从表空行
                }
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }
        return validatorContents;
    }

    /**
     * @return
     */
    public Map<String, ValidatorFieldContent> getValidatorContentWithDatabaseConstraints() {
        UserTableMetadataService userTableMetadataService = ApplicationContextHolder
                .getBean(UserTableMetadataService.class);
        Map<String, ValidatorFieldContent> validatorContents = Maps.newHashMap();
        try {
            for (Object fieldName : fieldDefinitionJSONObjects.keySet()) {
                String localFieldName = (String) fieldName;
                if (isInputModeEqAttach(localFieldName)) {
                    continue;
                }
                FieldDefinition fieldDefinition = new FieldDefinition(localFieldName, this);
                ValidatorFieldContent validatorFieldContent = new ValidatorFieldContent();
                validatorFieldContent.setFieldDefinition(fieldDefinition);
                validatorContents.put(localFieldName, validatorFieldContent);
                ColumnMetadata columnMetadata = userTableMetadataService
                        .getColumnMetadata(StringUtils.lowerCase(localFieldName), this.getTblNameOfMainform());
                if (columnMetadata == null) {
                    continue;
                }
                List<ValidatorParams> validatorParams = validatorFieldContent.getValidators();
                // 非空
                if (!columnMetadata.isNullable()) {
                    validatorParams.add(new ValidatorParams(new ValidatorMaps.RequiredValiator()));
                }
                String dataType = columnMetadata.getDataType();
                int precision = columnMetadata.getPrecision();
                int scale = columnMetadata.getScale();
                switch (dataType) {
                    case "int":
                    case "integer":
                    case "bigint":
                    case "long":
                        validatorParams.add(new ValidatorParams(new ValidatorMaps.IntegerValidator()));
                        break;
                    case "float":
                        validatorParams.add(new ValidatorParams(new ValidatorMaps.FloatValidator()));
                        if (precision > 0) {
                            if (scale > 0) {
                                validatorParams
                                        .add(new ValidatorParams(new ValidatorMaps.MaxLengthValidator(precision + 1)));
                            } else {
                                validatorParams.add(new ValidatorParams(new ValidatorMaps.MaxLengthValidator(precision)));
                            }
                        }
                        break;
                    case "number":
                        validatorParams.add(new ValidatorParams(new ValidatorMaps.DoubleValidator()));
                        if (precision > 0) {
                            if (scale > 0) {
                                validatorParams
                                        .add(new ValidatorParams(new ValidatorMaps.MaxLengthValidator(precision + 1)));
                            } else {
                                validatorParams.add(new ValidatorParams(new ValidatorMaps.MaxLengthValidator(precision)));
                            }
                        }
                        break;
                    case "date":
                    case "datetime":
                    case "timestamp":
                        validatorParams.add(new ValidatorParams(
                                new ValidatorMaps.DateTimeValidator(this.getDateTimePatternByFieldName(localFieldName))));
                        break;
                    case "varchar":
                    case "varchar2":
                    case "nvarchar2":
                        if (precision > 0) {
                            validatorParams.add(new ValidatorParams(new ValidatorMaps.MaxLengthValidator(precision)));
                        }
                        break;
                    case "clob":
                        break;
                    case "blob":
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return validatorContents;
    }

    public String getTitleExpression() {
        String titleType = formDefinitionJSONObject.optString("titleType");
        String titleContent = formDefinitionJSONObject.optString("titleContent");
        if (StringUtils.equals("1", titleType) || StringUtils.isBlank(titleContent)) {
            titleContent = "${表单名称}_${年}-${月}-${日}";
        }
        return titleContent;
    }

    /**
     * @param placeholderCtrField
     */
    public void setPlaceholderCtrHide(String placeholderCtrField) {
        if (StringUtils.isBlank(placeholderCtrField)) {
            return;
        }
        if (this.placeholderCtrDefinitionJSONObjects == null
                || this.placeholderCtrDefinitionJSONObjects.isNull(placeholderCtrField)) {
            return;
        }
        try {
            this.placeholderCtrDefinitionJSONObjects.getJSONObject(placeholderCtrField)
                    .put(EnumFieldPropertyName.showType.name(), DyShowType.hide);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param placeholderCtrField
     */
    public void setPlaceholderCtrEditable(String placeholderCtrField) {
        if (StringUtils.isBlank(placeholderCtrField)) {
            return;
        }
        if (this.placeholderCtrDefinitionJSONObjects == null
                || this.placeholderCtrDefinitionJSONObjects.isNull(placeholderCtrField)) {
            return;
        }
        try {
            this.placeholderCtrDefinitionJSONObjects.getJSONObject(placeholderCtrField)
                    .put(EnumFieldPropertyName.showType.name(), DyShowType.edit);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param fileLibraryFieldId
     * @param b
     */
    public void setFileLibraryHide(String fileLibraryFieldId) {
        if (StringUtils.isBlank(fileLibraryFieldId)) {
            return;
        }
        if (this.fileLibraryDefinitionJSONObjects == null
                || this.fileLibraryDefinitionJSONObjects.isNull(fileLibraryFieldId)) {
            return;
        }
        try {
            this.fileLibraryDefinitionJSONObjects.getJSONObject(fileLibraryFieldId)
                    .put(EnumFieldPropertyName.showType.name(), DyShowType.hide);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param fileLibraryFieldId
     */
    public void setFileLibraryEditable(String fileLibraryFieldId) {
        if (StringUtils.isBlank(fileLibraryFieldId)) {
            return;
        }
        if (this.fileLibraryDefinitionJSONObjects == null
                || this.fileLibraryDefinitionJSONObjects.isNull(fileLibraryFieldId)) {
            return;
        }
        try {
            this.fileLibraryDefinitionJSONObjects.getJSONObject(fileLibraryFieldId)
                    .put(EnumFieldPropertyName.showType.name(), DyShowType.edit);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param fileLibraryFieldId
     */
    public void setTableViewHide(String fileLibraryFieldId) {
        if (StringUtils.isBlank(fileLibraryFieldId)) {
            return;
        }
        if (this.tableViewDefinitionJSONObjects == null
                || this.tableViewDefinitionJSONObjects.isNull(fileLibraryFieldId)) {
            return;
        }
        try {
            this.tableViewDefinitionJSONObjects.getJSONObject(fileLibraryFieldId)
                    .put(EnumFieldPropertyName.showType.name(), DyShowType.hide);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param fileLibraryFieldId
     */
    public void setTableViewEditable(String fileLibraryFieldId) {
        if (StringUtils.isBlank(fileLibraryFieldId)) {
            return;
        }
        if (this.tableViewDefinitionJSONObjects == null
                || this.tableViewDefinitionJSONObjects.isNull(fileLibraryFieldId)) {
            return;
        }
        try {
            this.tableViewDefinitionJSONObjects.getJSONObject(fileLibraryFieldId)
                    .put(EnumFieldPropertyName.showType.name(), DyShowType.edit);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param subformIds
     * @return
     */
    public List<String> setHiddenSubformByIds(List<String> subformIds) {
        if (this.subformDefinitionJSONObjects == null) {
            return Collections.emptyList();
        }
        List<String> subformUuids = Lists.newArrayList();
        try {
            for (Object key : subformDefinitionJSONObjects.keySet()) {
                JSONObject jsonObject = (JSONObject) subformDefinitionJSONObjects.get(key.toString());
                String formId = jsonObject.getString("name");
                if (subformIds.contains(formId)) {
                    jsonObject.put(EnumFieldPropertyName.showType.name(), DyShowType.hide);
                    subformUuids.add(key.toString());
                }
            }
        } catch (JSONException e) {
        }
        return subformUuids;
    }

    /**
     * @param subformIds
     * @return
     */
    public List<String> setEditableSubformByIds(List<String> subformIds) {
        if (this.subformDefinitionJSONObjects == null) {
            return Collections.emptyList();
        }
        List<String> subformUuids = Lists.newArrayList();
        try {
            for (Object key : subformDefinitionJSONObjects.keySet()) {
                JSONObject jsonObject = (JSONObject) subformDefinitionJSONObjects.get(key.toString());
                String formId = jsonObject.getString("name");
                if (subformIds.contains(formId)) {
                    jsonObject.put(EnumFieldPropertyName.showType.name(), DyShowType.edit);
                    subformUuids.add(key.toString());
                }
            }
        } catch (JSONException e) {
        }
        return subformUuids;
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> getPlaceholderCtrIds() {
        List<String> placeholderCtrIds = Lists.newArrayListWithExpectedSize(0);
        if (this.placeholderCtrDefinitionJSONObjects == null) {
            return placeholderCtrIds;
        }
        Iterator<String> it = this.placeholderCtrDefinitionJSONObjects.keys();
        while (it.hasNext()) {
            placeholderCtrIds.add(it.next());
        }
        return placeholderCtrIds;
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> getFileLibraryIds() {
        List<String> fileLibraryIds = Lists.newArrayListWithExpectedSize(0);
        if (this.fileLibraryDefinitionJSONObjects == null) {
            return fileLibraryIds;
        }
        Iterator<String> it = this.fileLibraryDefinitionJSONObjects.keys();
        while (it.hasNext()) {
            fileLibraryIds.add(it.next());
        }
        return fileLibraryIds;
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> getTableViewIds() {
        List<String> tableViewIds = Lists.newArrayListWithExpectedSize(0);
        if (this.tableViewDefinitionJSONObjects == null) {
            return tableViewIds;
        }
        Iterator<String> it = this.tableViewDefinitionJSONObjects.keys();
        while (it.hasNext()) {
            tableViewIds.add(it.next());
        }
        return tableViewIds;
    }

    private static class DataSourceIterate {
        private JSONArray dataSources;
        // private Map<String, Object> params;
        private CdDataStoreService cdDataStoreService;

        /**
         * @param dataSources
         * @param cdDataStoreService
         */
        public DataSourceIterate(JSONArray dataSources, CdDataStoreService cdDataStoreService) {
            // this.params = params;
            this.dataSources = dataSources;
            this.cdDataStoreService = cdDataStoreService;
        }

        public void traverseTreeNode(Integer idx, List<TreeNode> treeNodes, TreeNode pTreeNode,
                                     Map<String, Object> params) throws ParseException, JSONException {
            if (idx < dataSources.length()) {
                JSONObject dataSource = dataSources.optJSONObject(idx);
                String dataSourceId = dataSource.optString(EnumFieldPropertyName.dataSourceId.name());
                String dataSourceFieldName = dataSource.optString(EnumFieldPropertyName.dataSourceFieldName.name());
                String dataSourceDisplayName = dataSource.optString(EnumFieldPropertyName.dataSourceDisplayName.name());
                DataStoreParams storeParams = new DataStoreParams();
                storeParams.setParams(params);
                storeParams.setDataStoreId(dataSourceId);
                if (pTreeNode != null) {
                    String dataSourceRel = dataSource.optString("dataSourceRel");
                    Condition defaultSqlCondition = new Condition(dataSourceRel, pTreeNode.getId(),
                            CriterionOperator.eq);
                    storeParams.getCriterions().add(defaultSqlCondition);
                } else if (StringUtils.isNotBlank(dataSource.optString("dataSourceSql"))) {
                    String dataSourceSql = dataSource.optString("dataSourceSql");
                    Condition defaultSqlCondition = new Condition();
                    defaultSqlCondition.setSql(dataSourceSql);
                    storeParams.getCriterions().add(defaultSqlCondition);
                }
                List<Map<String, Object>> queryItems = cdDataStoreService.loadDataWithNewTransaction(storeParams)
                        .getData();
                for (Map<String, Object> item : queryItems) {
                    Object dataSourceFieldNameObject = item.get(dataSourceFieldName);
                    Object dataSourceDisplayNameObject = item.get(dataSourceDisplayName);
                    String optionKey = (String) FormDataHandler.convertData2DbType(DyFormConfig.DbDataType._string,
                            dataSourceFieldNameObject, null);
                    String optionValue = (String) FormDataHandler.convertData2DbType(DyFormConfig.DbDataType._string,
                            dataSourceDisplayNameObject, null);
                    if (optionKey == null || optionValue == null) {
                        continue;
                    }
                    TreeNode treeNode = new TreeNode();
                    treeNodes.add(treeNode);
                    treeNode.setId(optionKey);
                    treeNode.setName(optionValue);
                    treeNode.setPath(pTreeNode == null ? optionValue : pTreeNode.getPath() + "/" + optionValue);
                    traverseTreeNode(idx + 1, treeNode.getChildren(), treeNode, Maps.newHashMap());
                }
            }
        }

    }

}
