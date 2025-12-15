package com.wellsoft.pt.dyform.implement.combiner.dto.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.common.i18n.AppCodeI18nMessageSource;
import com.wellsoft.pt.dyform.facade.dto.*;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.enums.EnumValidateCode;
import com.wellsoft.pt.dyform.implement.data.utils.FormDataHandler;
import com.wellsoft.pt.dyform.implement.data.utils.ValidateMsg;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.*;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumDbCharacterSet;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumRelationTblSystemField;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.DyformDataOptions;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.dyform.implement.definition.validator.Validator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.*;

public class DyFormDataImpl implements DyFormData {

    private static Logger logger = LoggerFactory.getLogger(DyFormData.class);
    transient DyFormFacade dyFormFacade = (DyFormFacade) ApplicationContextHolder.getBean(DyFormFacade.class);
    private String formUuid;
    private Map<String, Object> dyformDataOptions;
    // private boolean isForceCover = false;//是否强制覆盖
    private Map<String/*表单定义id*/, Map<String /*字段名称*/, Set<Validator> /*校验器*/>> customValidators = null;
    // 最终的数据
    private Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> formDatas = new HashMap<String, List<Map<String, Object>>>();
    // 被删除的字段
    private Map<String/* 表单定义id */, List<String>/* 表单数据id */> deletedFormDatas;
    // 被更新的字段　
    private Map<String/* 表单定义id */, Map<String /* 数据记录uuid */, Set<String> /* 字段值 */>> updatedFormDatas = new HashMap<String, Map<String, Set<String>>>();
    // 被新增的数据
    private Map<String/* 表单定义id */, List<String>/* 表单数据id */> addedFormDatas = new HashMap<String, List<String>>();
    private DyformDataSignature signature;
    private String formDefinition = null;
    private String definitionVjson = null;
    private transient FormDefinitionHandler dUtils = null;
    private Map<String, FormDefinition> formDefinitionMap = new HashMap<String, FormDefinition>();
    private boolean loadSubformDefinition = false;
    private boolean loadDefaultFormData = false;
    private boolean loadDictionary = false;
    private Map<String, String> newOldUuidMap = Maps.newHashMap();

    private String beforeSaveFormDataServiceFilter; // 提供业务在表单保存前进行业务数据加工处理

    private List<DyformBlock> blocks;
    private String formId;
    private String tableName;

    private List<AppDefElementI18nEntity> i18ns = Lists.newArrayList();


    /**
     * @deprecated 该构造方法勿用, 请通过dyformApi.getDyformData, 或者dyformApi.createDyformData
     */
    public DyFormDataImpl() {

    }

    /**
     * 创建表单对象
     * @param isPermanent  true表示持久化对象,false则反之
     */
    /*
     * public static DyFormData createDyFormData(boolean isPermanent) {
     * DyFormData dyformData = new DyFormData();
     * dyformData.setPermanent(isPermanent); return dyformData; }
     */
    /**
     * 创建表单对象
     * @param isPermanent  true表示持久化对象,false则反之
     * @param formUuid
     * @return
     */
    /*
     * public static DyFormData createDyFormData(boolean isPermanent, String
     * formUuid) { DyFormData dyformData = new DyFormData(formUuid);
     * dyformData.setPermanent(isPermanent); return dyformData; }
     */

    /**
     * 创建表单对象
     * @param isPermanent  true表示持久化对象,false则反之
     * @param formUuid
     * @param formData
     * @return
     */
    /*
     * public static DyFormData createDyFormData(boolean isPermanent, String
     * formUuid, Map<String, List<Map<String, Object>>> formData) { DyFormData
     * dyformData = new DyFormData(formUuid, formData);
     * dyformData.setPermanent(isPermanent); return dyformData; }
     */

    /**
     * 创建一个表单对象，该对象在数据库中
     * @param formUuid 表单uuid
     * @param formData 新增数据
     */
    /*
     * private DyFormData(String formUuid, Map<String, List<Map<String,
     * Object>>> formData) { this.setFormDatas(formData);
     * this.initFormDefintion(formUuid); }
     */

    /**
     * 创建了一个表单对象，但没有数据,所以创建了一条只有dataUUid的主表数据
     *
     * @param formUuid
     */
    private DyFormDataImpl(String formUuid) {
        this.initFormDefintion(formUuid);
        this.setDataUuid(this.dyFormFacade.createUuid());
    }

    private DyFormDataImpl(String formUuid, boolean loadSubformDefinition, boolean loadDefaultFormData,
                           boolean loadDictionary) {
        this.loadSubformDefinition = loadSubformDefinition;
        this.loadDefaultFormData = loadDefaultFormData;
        this.loadDictionary = loadDictionary;
        this.initFormDefintion(formUuid);
    }

    public static String getMainformDataUuid(String mainformUuid, Map<String, List<Map<String, Object>>> formDatas) {
        List<Map<String, Object>> formDatasOfOneForm = formDatas.get(mainformUuid);
        if (formDatasOfOneForm != null) {
            return (String) formDatasOfOneForm.get(0).get(EnumSystemField.uuid.name());
        }
        return null;
    }

    public void initFormDefintion() {
        this.initFormDefintion(formUuid);
    }

    /**
     * @param formUuid
     */
    private void initFormDefintion(String formUuid) {
        this.formUuid = formUuid;
        FormDefinition dyformDefinition = (FormDefinition) this.dyFormFacade.getFormDefinition(this.formUuid);
        initFormDefintion(dyformDefinition);
    }

    /**
     * 如何描述该方法
     *
     * @param dyformDefinition
     */
    private void initFormDefintion(FormDefinition dyformDefinition) {
        this.formDefinition = dyformDefinition.getDefinitionJson();
        this.definitionVjson = dyformDefinition.getDefinitionVjson();
        this.formDefinitionMap.put(this.formUuid, dyformDefinition);
        if (StringUtils.isNotBlank(dyformDefinition.getpFormUuid())) {
            formDefinitionMap.put(dyformDefinition.getpFormUuid(), dyformDefinition);
        }

        this.dUtils = dyformDefinition.doGetFormDefinitionHandler();
        // add by wujx 20160621
        // 设置文本类型字段的字符最大长度 begin
        String dbCharacterSet = dUtils.getFormPropertyOfStringType(EnumFormPropertyName.dbCharacterSet);// this.dyFormFacade.getDbCharacterSet();
        setFieldMaxField(dyformDefinition.doGetFieldDefintions(), dbCharacterSet);
//        StopWatch stopWatch = new StopWatch("setDyFormDefinitionData");
//        stopWatch.start("设置主表字段的默认值");
        // 设置文本类型字段的字符最大长度 end
        if (this.loadDefaultFormData) {
            this.dUtils.loadDefaultFormData();// 获取主表的默认值
        }
//        stopWatch.stop();
//        stopWatch.start("设置主表字段的选项数据");
        if (this.loadDictionary) {
            this.dUtils.loadFieldDictionary(true);// 加载字段对应的数据字典
        }
//        stopWatch.stop();
//
//        stopWatch.start("设置主表的职位选项数据");
        this.dUtils.loadFieldJobDictionary();// 为职位控件添加option
//        stopWatch.stop();
        this.tableName = dyformDefinition.getTableName();
        this.formId = this.getFormId();
        if (!this.loadSubformDefinition) {
//            logger.info("设置表单默认数据与字段选项结束，执行情况：{}", stopWatch.prettyPrint());
            return;
        }
        List<DyformSubformFormDefinition> subformConfigs = dyformDefinition.doGetSubformDefinitions();
        if (subformConfigs != null) {// 获取从表的定义

            for (DyformSubformFormDefinition config : subformConfigs) {// 遍历从表的配置
                String subformFormUuid = config.getFormUuid();
                FormDefinition df = (FormDefinition) this.dyFormFacade.getFormDefinition(subformFormUuid);
                if (df == null) {
                    continue;
                }
                List<DyformFieldDefinition> fieldDefList = df.doGetFieldDefintions();

                // add by wujx 20160621
                // 设置文本类型字段的字符最大长度 begin
                setFieldMaxField(fieldDefList, dbCharacterSet);
                // 设置文本类型字段的字符最大长度 end

                // 获取从表的默认值
//                stopWatch.start("设置从表[" + df.getId() + "]字段的默认值");
                if (this.loadDefaultFormData) {
                    df.doGetFormDefinitionHandler().loadDefaultFormData();
                }
//                stopWatch.stop();
//
//                stopWatch.start("设置从表[" + df.getId() + "]字段的选项数据");
                if (this.loadDictionary) {
                    df.doGetFormDefinitionHandler().loadFieldDictionary(true);
                }
//                stopWatch.stop();
//                stopWatch.start("设置从表[" + df.getId() + "]的职位选项数据");
                df.doGetFormDefinitionHandler().loadFieldJobDictionary();// 为职位控件添加option
//                stopWatch.stop();
                this.formDefinitionMap.put(subformFormUuid, df);
                if (StringUtils.isNotBlank(df.getpFormUuid())) {
                    formDefinitionMap.put(df.getpFormUuid(), df);
                }
                this.addSubformDefinition(df);

            }
        }
        this.blocks = this.getBlocks();

//        logger.info("设置表单默认数据与字段选项结束，执行情况：{}", stopWatch.prettyPrint());
    }

    /**
     * 如果Oracle 的字符集是Utf-8, varchar2(4000)最多可以存1333个汉字，
     * 而varchar2(4000 char)其实和varchar2(1333 char)一样，也是只能存1333个汉字；
     * 如果Oracle的字符集是GBK,一个汉字是2个字节的话，varchar2(4000)最多可以存2000个汉字，
     * 而varchar2(4000 char)其实和varchar2(2000 char)一样，也是只能存2000个汉字；
     * add by wujx 20160621
     *
     * @param fieldDefList
     * @param dbCharacterSet
     */
    private void setFieldMaxField(List<DyformFieldDefinition> fieldDefList, String dbCharacterSet) {
        for (DyformFieldDefinition fieldDefinition : fieldDefList) {
            Integer length = 0;
            try {
                if (StringUtils.isBlank(fieldDefinition.getLength())) {
                    return;
                }
                length = Integer.valueOf(fieldDefinition.getLength());
            } catch (Exception e) {
                logger.info(e.getMessage(), e);
            }
            Integer maxLength = EnumDbCharacterSet.maxLengthByTypeAndCodeAndName(Config.getValue("database.type"),
                    dbCharacterSet);
            if (maxLength != null && length > maxLength) {
                length = maxLength;
            }
            fieldDefinition.setLength(length.toString());
        }
    }

    public void addSubformDefinition(DyFormFormDefinition df) {
        String formUuid = df.getUuid();
        Object jobj = this.dUtils.getFormProperty("subformDefinitions");
        Object subformUuidObject = this.dUtils.getFormProperty("subformUuids");
        List<String> subformDefinitions = null;
        List<String> subformUuids = null;
        if (jobj == null) {
            subformDefinitions = new ArrayList<String>();
            subformUuids = new ArrayList<String>();
        } else {
            subformDefinitions = (List<String>) jobj;
            subformUuids = (List<String>) subformUuidObject;
        }
        Iterator<String> it = subformDefinitions.iterator();
        Iterator<String> subformUuidIt = subformUuids.iterator();
        while (subformUuidIt.hasNext()) {
            String subformUuid = subformUuidIt.next();
            it.next();
            if (StringUtils.equals(formUuid, subformUuid)) {
                it.remove();
                subformUuidIt.remove();
                break;
            }
        }

        subformDefinitions.add(df.getDefinitionJson());
        subformUuids.add(formUuid);
        this.dUtils.addFormProperty("subformDefinitions", subformDefinitions);
        this.dUtils.addFormProperty("subformUuids", subformUuids);
    }

    public void addSubformData(DyFormData dyformData) {
        List<Map<String, Object>> formdatas = this.getFormDatas(dyformData.getFormUuid());
        if (formdatas == null) {
            formdatas = new ArrayList<Map<String, Object>>();
        }
        if (dyformData.getDataUuid() == null) {
            dyformData.setDataUuid(dyFormFacade.createUuid());
        }
        formdatas.add(dyformData.getFormDataByFormUuidAndDataUuid(dyformData.getFormUuid(), dyformData.getDataUuid()));
        this.formDatas.put(dyformData.getFormUuid(), formdatas);
        markAsAddedFormData(dyformData.getFormUuid(), dyformData.getDataUuid());
    }

    /**
     * 删除所有从表所有数据
     */
    @Override
    public void deleteAllSubFormData() {
        for (String localFormUuid : formDatas.keySet()) {
            if (isMainform(localFormUuid)) {
                continue;
            }
            deleteFormData(localFormUuid);
        }
    }

    /**
     * 删除某张表所有数据
     *
     * @param formUuid
     */
    @Override
    public void deleteFormData(String formUuid) {
        List<String> dataUuids = Lists.newArrayList();
        List<Map<String, Object>> datas = getFormDatas(formUuid);
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }
        for (Map<String, Object> data : datas) {
            String dataUuid = (String) data.get(EnumSystemField.uuid.getName());
            if (StringUtils.isNotBlank(dataUuid)) {
                dataUuids.add(dataUuid);
            }
        }
        deleteFormData(formUuid, dataUuids);
    }

    /**
     * 删除某张表某些数据
     *
     * @param formUuid
     * @param dataUuids
     */
    @Override
    public void deleteFormData(String formUuid, List<String> dataUuids) {
        List<Map<String, Object>> datas = getFormDatas(formUuid);
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }
        List<Map<String, Object>> datas2 = Lists.newArrayList();
        for (String dataUuid : dataUuids) {
            for (Map<String, Object> data : datas) {
                if (StringUtils.equals((String) data.get(EnumSystemField.uuid.getName()), dataUuid)) {
                    datas2.add(data);
                    markAsDeletedData(formUuid, dataUuid);
                }
            }
        }
        datas.removeAll(datas2);
    }

    public String getFormId() {
        if (this.dUtils == null) {
            return this.formId;
        }
        return dUtils.getFormId();
    }

    public String getFormUuid() {
        return formUuid;
    }

    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
        if (StringUtils.isNotBlank(formUuid) && !formUuid.equalsIgnoreCase("-1")) {
            this.initFormDefintion();
        }
    }

    public Map<String, List<Map<String, Object>>> getFormDatas() {
        return formDatas;
    }

    /**
     * 该方法勿使用 ,请用另一方法{@link#setFormDatas(Map,boolean) }
     *
     * @param formDatas
     */
    @Deprecated
    public void setFormDatas(Map<String, List<Map<String, Object>>> formDatas) {
        this.formDatas = formDatas;
    }

    public List<Map<String, Object>> getFormDatas(String formUuid) {
        return this.formDatas.get(formUuid);
    }

    public String getFormUuidByFormId(String formId) {
        DyFormFormDefinition dd = this.getDyformDefinitionById(formId);
        String formUuid = dd.getUuid();
        return formUuid;
    }

    public List<DyFormData> getDyformDatasByFormId(String formId) {
        return this.getDyformDatas(this.getFormUuidByFormId(formId));
    }

    /**
     * 指定formUUid拥有的dyformdata
     *
     * @param formUuid
     * @return
     */
    public List<DyFormData> getDyformDatas(String formUuid) {
        List<DyFormData> dyformDatas = new ArrayList<DyFormData>();
        if (this.isMainform(formUuid)) {// 直接取主表
            dyformDatas.add(this);
            return dyformDatas;
        }

        // 取从表
        List<Map<String, Object>> formDatas = this.getFormDatas(formUuid);
        if (formDatas == null) {
            return null;
        }

        for (Map<String, Object> formData : formDatas) {
            String dataUuid = (String) formData.get(EnumSystemField.uuid.getName());
            if (dataUuid == null) {
                continue;
            }
            dyformDatas.add(this.getDyFormData(formUuid, dataUuid));
        }
        return dyformDatas;
    }

    /**
     * 整个表单的dyformdata,包括从表及主表
     *
     * @return
     */
    public Map<String, List<DyFormData>> doGetDyformDatas() {
        Map<String, List<DyFormData>> dyformDatas = new HashMap<String, List<DyFormData>>();
        List<String> formUuids = this.dUtils.getFormUuids();// 获取表单中所有的formUuid(包括主表,从表)
        for (String formUuid : formUuids) {
            dyformDatas.put(formUuid, this.getDyformDatas(formUuid));
        }
        return dyformDatas;
    }

    /**
     * 通过formUuid及dataUuid 编辑/添加从表数据
     *
     * @param formId
     * @param dataUuid
     * @return
     */
    public DyFormData getDyFormData(final String formUuid, final String dataUuid) {
        DyFormDataImpl dyformdata = new DyFormDataImpl();
        if (isMainform(formUuid)) {// 主表数据不应该通过该接口操作数据
            throw new RuntimeException("formUuid[" + formUuid
                    + "] is mainform, should not operate the data by this function ");
        } else {
            dyformdata.setLoadDictionary(false);
            dyformdata.setLoadDefaultFormData(false);
            dyformdata.setLoadSubformDefinition(false);
        }
        dyformdata.setFormUuid(formUuid);

        // try to get data from current form
        Map<String, Object> data = this.getFormDataByFormUuidAndDataUuid(formUuid, dataUuid);// 获取formUuid、dataUuid对应的数据
        boolean isNewFormData = false;
        if (data == null) {// cann't find data in current form ,so here is 新增
            dyformdata.setDataUuid(dataUuid);
            data = dyformdata.getFormDataByFormUuidAndDataUuid(formUuid, dataUuid);
            this.addSubformData(dyformdata);
            // this.recordAddedFormDatas(formUuid, dataUuid);//record here is
            // add a new datarecord
            isNewFormData = true;
        } else {// find a data in current form, so here is 编辑
            Map<String/* 表单定义id */, Map<String /* 数据记录uuid */, Set<String>/* 字段值 */>> updatedFormDatas = new HashMap<String, Map<String, Set<String>>>();

            // 通过下面的这种方式,获取到的dyformdata即可将更新了哪些字段同步到当前的这个主表dyformdata中
            // pass the this.updatedFormDatas to the dyformdata, and
            // this.updatedFormDatas will record the dyformdata's udpated fields
            Map<String /* 数据记录uuid */, Set<String>/* 字段值 */> records = this.updatedFormDatas.get(formUuid);
            if (records == null) {
                records = new HashMap<String, Set<String>>();
                records.put(dataUuid, new HashSet<String>());
                this.updatedFormDatas.put(formUuid, records);
            }
            updatedFormDatas.put(formUuid, records);
            dyformdata.updatedFormDatas = updatedFormDatas;
        }

        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        datas.add(data);
        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> formDatas = new HashMap<String, List<Map<String, Object>>>();
        formDatas.put(formUuid, datas);
        dyformdata.setFormDatas(formDatas, isNewFormData);

        return dyformdata;
    }

    /**
     * 标记新增数据记录的dataUuid, formUuid<br>
     * 在日志中标识该数据为新增数据
     *
     * @param formUUid 新增记录对应的formUuid
     * @param dataUuid 新增记录对应的dataUuid
     */
    public void markAllAsAddedFormData() {
        for (String formUuid : formDatas.keySet()) {
            List<Map<String, Object>> formDatasOfOneForm = formDatas.get(formUuid);
            if (formDatasOfOneForm == null || formDatasOfOneForm.size() == 0) {
                continue;
            }
            for (Map<String, Object> formData : formDatasOfOneForm) {
                if (formData == null || formData.isEmpty()) {
                    continue;
                }
                String dataUuid = null;
                dataUuid = (String) formData.get(EnumSystemField.uuid.getName());
                if (this.isMarkedAsNewFormData(formUuid, dataUuid)) {// 已被标识为新增记录
                    continue;
                } else {
                    this.markAsAddedFormData(formUuid, dataUuid);
                }
            }
        }
    }

    /**
     * 标记新增数据记录的dataUuid, formUuid<br>
     * 在日志中标识该数据为新增数据
     *
     * @param formUUid 新增记录对应的formUuid
     * @param dataUuid 新增记录对应的dataUuid
     */
    public void markAsAddedFormData(final String formUUid, final String dataUuid) {
        List<String> dataUUids = this.addedFormDatas.get(formUUid);
        if (dataUUids == null) {
            dataUUids = new ArrayList<String>();
            this.addedFormDatas.put(formUUid, dataUUids);
        }
        dataUUids.add(dataUuid);
    }

    /**
     * 标记数据记录中某条记录的哪些字段被更新
     *
     * @param formUuid          被更新字段对应的formUuid
     * @param dataUuid          被更新字段对应的记录的dataUuid
     * @param updatedfieldNames 被更新的字段
     */
    public void markAsUpdatedFields(final String formUuid, final String dataUuid, final List<String> updatedfieldNames) {
        if (StringUtils.isBlank(formUuid) || StringUtils.isBlank(dataUuid)) {
            return;
        }
        if (this.isMarkedAsNewFormData(formUuid, dataUuid)) {
            return;
        }
        Map<String /* 数据记录uuid */, Set<String>/* 字段值 */> records = this.updatedFormDatas.get(formUuid);
        if (records == null) {
            records = new HashMap<String, Set<String>>();
            records.put(dataUuid, new HashSet<String>());
            this.updatedFormDatas.put(formUuid, records);
        }

        Set<String> fieldNames = records.get(dataUuid);
        if (fieldNames == null) {
            fieldNames = new HashSet<String>();
            records.put(dataUuid, fieldNames);
        }
        fieldNames.addAll(updatedfieldNames);

        // this.updatedFormDatas
    }

    /**
     * 标记数据记录中某条记录的哪些字段被更新
     *
     * @param formUuid          被更新字段对应的formUuid
     * @param dataUuid          被更新字段对应的记录的dataUuid
     * @param updatedfieldNames 被更新的字段
     */
    public void markAsUpdatedField(final String formUuid, final String dataUuid, final String updatedfieldName) {
        if (StringUtils.isBlank(formUuid) || StringUtils.isBlank(dataUuid)) {
            return;
        }
        if (this.isMarkedAsNewFormData(formUuid, dataUuid)) {
            return;
        }
        Map<String /* 数据记录uuid */, Set<String>/* 字段值 */> records = this.updatedFormDatas.get(formUuid);
        if (records == null) {
            records = new HashMap<String, Set<String>>();
            this.updatedFormDatas.put(formUuid, records);
        }

        Set<String> fieldNames = records.get(dataUuid);
        if (fieldNames == null) {
            fieldNames = new HashSet<String>();
            records.put(dataUuid, fieldNames);
        }

        fieldNames.add(updatedfieldName);

        // this.updatedFormDatas
    }

    /**
     * 通过formId及dataUuid编辑添加从表数据
     *
     * @param formId
     * @param dataUuid
     * @return
     */
    public DyFormData getDyFormDataByFormId(String formId, String dataUuid) {
        String formUuid = this.getDyformDefinitionById(formId).getUuid();
        return this.getDyFormData(formUuid, dataUuid);
    }

    public List<Map<String, Object>> getFormDatasById(String id) {
        DyFormFormDefinition df = this.getDyformDefinitionById(id);
        if (df == null) {
            df = this.dyFormFacade.getFormDefinitionById(id);
        }
        List<Map<String, Object>> list = this.formDatas.get(df.getUuid());
        return list == null ? new ArrayList<Map<String, Object>>(0) : list;
    }

    private DyFormFormDefinition getDyformDefinitionById(String formId) {
        for (DyFormFormDefinition df : this.formDefinitionMap.values()) {
            if (df.getId().equalsIgnoreCase(formId)) {
                return df;
            }
        }

        return this.dyFormFacade.getFormDefinitionById(formId);

    }

    /**
     * 通过formId和dataUuid获取数据
     *
     * @param formId
     * @param dataUuid
     * @return
     */
    public Map<String, Object> getFormDataByIdAndDataUuid(String formId, String dataUuid) {
        List<Map<String, Object>> formdatas = this.getFormDatasById(formId);
        for (Map<String, Object> formdata : formdatas) {
            if (dataUuid.equals(formdata.get("uuid"))) {
                return formdata;
            }
        }
        return null;
    }

    public Map<String, Object> getFormDataByFormUuidAndDataUuid(String formUuid, String dataUuid) {
        List<Map<String, Object>> formdatas = this.getFormDatas(formUuid);
        if (formdatas == null) {
            return null;
        }
        for (Map<String, Object> formdata : formdatas) {
            if (dataUuid.equals(formdata.get("uuid"))) {
                return formdata;
            }
        }
        return null;
    }

    /**
     * <b>注意该方法不提供给外部使用</b><br/>
     * 描述: 设置数据到dyformData中
     *
     * @param formDatas
     * @param isNonPersistentData 值解释如下:<br/>
     *                            true: 表示非持久化数据(该dataUuid在数据库中不存在),会将对应的dataUuid添加到addedFormDatas中;<br/>
     *                            false: 表示持久化数据(该dataUuid在数据中已存在),这时dataUuid对应的字段数据是没被添加到updatedFormDatas中的<br/>
     *                            如果要添加updatedFormDatas变量中，那么需要调用 markAsUpdatedFields方法，或者markAllFieldsAsUpadtedFields方法
     */
    public void setFormDatas(Map<String, List<Map<String, Object>>> formDatas, boolean isNonPersistentData) {
        if (formDatas == null || formDatas.size() == 0) {
            return;
        }

        this.formDatas = formDatas;

        if (!isNonPersistentData) {// 持久化数据
            markRecordAsPersistentData(formDatas);// 将所有dataUuid标识为持久化
            return;
        }

        this.markFormDatasAsNew(formDatas);
    }

    @Override
    public void loadSubformDatas() {
        String mainFormUuid = getFormUuid();
        String mainDataUuid = getDataUuid();
        List<Map<String, Object>> lists = formDatas.get(mainFormUuid);
        if (lists == null || lists.isEmpty() == true) {
            return;
        }
        String nestformDatasObj = (String) lists.get(0).get(SUBFIELD_NESTFORM_DATAS);
        if (StringUtils.isNotBlank(nestformDatasObj)) {
            DyFormData dyFormData = dyFormFacade.parseDyformData(nestformDatasObj);
            dyFormData.getFormDatas().remove(mainFormUuid);
            if (false == dyFormData.getFormDatas().isEmpty()) {
                formDatas.putAll(dyFormData.getFormDatas());
            }
        } else if (StringUtils.isNotBlank(mainDataUuid) && false == dUtils.getFormUuidsOfSubform().isEmpty()) {
            for (String formUuidOfSubform : dUtils.getFormUuidsOfSubform()) {
                QueryData qd = dyFormFacade.getFormDataOfParentNode(formUuidOfSubform, mainFormUuid, mainDataUuid, false);
                if (qd == null || qd.getDataList() == null) {
                    formDatas.put(formUuidOfSubform, (List) Lists.newArrayList());
                } else {
                    formDatas.put(formUuidOfSubform, (List) qd.getDataList());
                }
            }
        }
    }

    @Override
    public void setEditableSubformOperateBtns(String formUuid, Set<String> operationBtnCodes) {
        this.dUtils.setEditableSubformOperateBtns(formUuid, operationBtnCodes);
    }

    @Override
    public String getFileNamesOfField(Object files) {
        FormDataHandler utils = this.getFormDataUtils(this.formUuid, null);
        return utils.getFileNamesOfField(null, files);
    }

    @Override
    public String geFormDataDisplayValueString() {
        Map<String, Object> dyformMainData = this.getFormDataOfMainform();
        List<String> displayValues = Lists.newArrayList();
        for (String f : dyformMainData.keySet()) {
            String displayName = this.getDisplayNameOfField(f);
            Object value = dyformMainData.get(f);
            if (value != null) {
                if (this.isFileField(f)) {
                    value = getFileNamesOfField(value);
                } else if (this.isValueAsMapField(f)) {
                    value = getFieldDisplayValue(f);
                } else {
                    value = getFieldDisplayValue(f);
                }
                if (value != null) {
                    if (value.toString().contains("<") && value.toString().contains("</")) {
                        displayValues.add(displayName + " : " + Jsoup.parse(value.toString()).text());
                        continue;
                    }
                    displayValues.add(displayName + " : " + value.toString());
                }
            }
        }
        return StringUtils.join(displayValues, " ");
    }

    @Override
    public void setDisplayFormDatas(Map<String, List<Map<String, Object>>> displayFormDatas, boolean isNewData) {
        Map<String, List<Map<String, Object>>> formDatas = new HashMap<String, List<Map<String, Object>>>();
        for (String formUuid : displayFormDatas.keySet()) {
            List<Map<String, Object>> lists = displayFormDatas.get(formUuid);
            if (lists == null || lists.isEmpty() == true) {
                continue;
            }
            List<Map<String, Object>> dataLists = new ArrayList<Map<String, Object>>();
            FormDefinitionHandler dUtil = formDefinitionMap.get(formUuid).doGetFormDefinitionHandler();
            for (Map<String, Object> formData : lists) {
                if (formData == null || formData.isEmpty() == true) {
                    continue;
                } else if (isNewData && formData.containsKey(EnumSystemField.uuid.getName()) == false) {
                    // 创建dataUuid
                    formData.put(EnumSystemField.uuid.getName(), dyFormFacade.createUuid());
                }
                // HashMap复制key
                List<String> fieldNames = new ArrayList<String>();
                fieldNames.addAll(formData.keySet());
                for (String fieldName : fieldNames) {
                    if (dUtil.isValueAsMap(fieldName)) {
                        // String inputMode = dUtil.getInputMode(fieldName);
                        Object fieldValue = formData.get(fieldName);
                        if (fieldValue != null && fieldValue instanceof String) {
                            String fieldSeparater = dUtil.getFieldSeparater(fieldName);
                            String[] fieldValues = StringUtils.split((String) fieldValue, fieldSeparater);
                            List<String> fieldRealValues = new ArrayList<String>();
                            for (String localFieldValue : fieldValues) {
                                String fieldRealValue = dUtil.getFieldDictionaryRealValueByDisplayValue(fieldName,
                                        localFieldValue);
                                fieldRealValues.add(fieldRealValue == null ? localFieldValue : fieldRealValue);
                            }
                            formData.put(fieldName, StringUtils.join(fieldRealValues, fieldSeparater));
                            // 设置显示值和真实值的映射
                            String realFieldName = dUtil.getRealOfRealDisplay(fieldName);
                            String displayFieldName = dUtil.getDisplayOfRealDisplay(fieldName);
                            if (StringUtils.isNotBlank(realFieldName) && formData.containsKey(realFieldName) == false) {
                                formData.put(realFieldName, StringUtils.join(fieldRealValues, fieldSeparater));
                            }
                            if (StringUtils.isNotBlank(displayFieldName)
                                    && formData.containsKey(displayFieldName) == false) {
                                formData.put(displayFieldName, fieldValue);
                            }
                        }
                    }
                }
                dataLists.add(formData);
            }
            formDatas.put(formUuid, dataLists);
        }
        setFormDatas(formDatas, isNewData);
    }

    /**
     * 将formDatas中的dataUuid标识为已存在于数据库中
     *
     * @param formDatas
     */
    private void markRecordAsPersistentData(Map<String, List<Map<String, Object>>> formDatas) {
        if (formDatas == null) {
            return;
        }
        for (String formUuid : formDatas.keySet()) {
            if (!this.isFormUuidInThisForm(formUuid)) {
                continue;
            }
            List<Map<String, Object>> formDatasOfOneForm = formDatas.get(formUuid);
            if (formDatasOfOneForm == null || formDatasOfOneForm.size() == 0) {
                continue;
            }
            for (Map<String, Object> formData : formDatasOfOneForm) {
                String uuid = (String) formData.get(EnumSystemField.uuid.name());
                if (StringUtils.isBlank(uuid)) {
                    continue;
                } else {
                    markRecordAsPersistentData(formUuid, uuid);
                }

            }
        }
    }

    /**
     * 将dataUuid标识为已存在于数据库中
     *
     * @param formUuid2
     * @param dataUuid
     */
    private void markRecordAsPersistentData(String formUuid2, String dataUuid) {
        if (this.isMarkedAsNewFormData(formUuid2, dataUuid)) {
            // throw new RuntimeException("[formUuid:" + formUuid2 +
            // "[dataUuid=" + dataUuid + "]已被标识为新纪录");
            logger.warn("[formUuid:" + formUuid2 + "[dataUuid=" + dataUuid + "]已被标识为新纪录");
        }
        if (this.isMarkedAsUpdatedRecord(formUuid2, dataUuid)) {
            return;
        } else {
            this.markAsUpdatedFields(formUuid2, dataUuid, new ArrayList<String>());
        }
    }

    /**
     * 重置表单数据
     *
     * @param formDatas
     * @param isPersistentData 值解释如下:<br/>
     *                         false: 表示非持久化数据(该dataUuid在数据库中不存在)<br/>
     *                         true: 表示持久化数据(该dataUuid在数据中已存在)<br/>
     */
    public void resetFormDatas(Map<String, List<Map<String, Object>>> formDatas) {
        if (formDatas == null || formDatas.size() == 0) {
            return;
        }
        this.formDatas = formDatas;

        /*
         * if (isPersistentData) {//持久化数据 //this.markAllFieldsAsUpadtedFields();
         * //compareNewAndOldData(formDatas, this.formDatas);//与旧数据进行对比
         *
         * } else { //this.clearPermanenceInfo(); this.formDatas = formDatas;
         * //this.markFormDatasAsNew(formDatas); }
         */

        for (String formUuid : formDatas.keySet()) {
            if (this.isFormUuidInThisForm(formUuid)) {
                List<Map<String, Object>> formDatasOfOneForm = formDatas.get(formUuid);
                if (formDatasOfOneForm == null || formDatasOfOneForm.size() == 0) {
                    continue;
                }

                for (Map<String, Object> formDataOfOneRow : formDatasOfOneForm) {
                    String dataUuid = (String) formDataOfOneRow.get(EnumSystemField.uuid.name());
                    if (StringUtils.isBlank(dataUuid)) {
                        continue;
                    }

                    if (this.isMarkedAsUpdatedRecord(formUuid, dataUuid)) {// 如果已经是持久化数据
                        Map<String /* 数据记录uuid */, Set<String>/* 字段值 */> records = this.updatedFormDatas
                                .get(formUuid);
                        Set<String> fieldNames = records.get(dataUuid);
                        fieldNames.clear();
                        fieldNames.addAll(this.doGetdUtil(formUuid).getFieldNames());
                        // records.put(arg0, arg1)
                    } else if (this.isMarkedAsNewFormData(formUuid, dataUuid)) {// 新记录
                        continue;
                    } else {
                        this.markAsAddedFormData(formUuid, dataUuid);
                    }

                }

            } else {
                logger.debug(formUuid + " not in this form");
                continue;
            }
        }

        cleanUselessUpdatedOrAddedRowRecord();

    }

    private void cleanUselessUpdatedOrAddedRowRecord() {
        if (this.addedFormDatas != null && !this.addedFormDatas.isEmpty()) {
            for (String formUuid : this.addedFormDatas.keySet()) {
                List<String> dataUuids = this.addedFormDatas.get(formUuid);
                Iterator<String> dataUuidItor = dataUuids.iterator();
                while (dataUuidItor.hasNext()) {
                    String dataUuid = dataUuidItor.next();
                    if (!this.isDataUuidExistInFormData(formUuid, dataUuid)) {
                        dataUuidItor.remove();
                    }
                }
            }
        }

        if (this.updatedFormDatas != null && !this.updatedFormDatas.isEmpty()) {
            for (String formUuid : this.updatedFormDatas.keySet()) {
                Map<String, Set<String>> updatedRows = this.updatedFormDatas.get(formUuid);
                Iterator<String> it = updatedRows.keySet().iterator();
                while (it.hasNext()) {
                    String dataUuid = it.next();
                    if (!this.isDataUuidExistInFormData(formUuid, dataUuid)) {
                        this.markAsDeletedData(formUuid, dataUuid);
                        it.remove();
                    }
                }
            }
        }

    }

    private void markAsDeletedData(String formUuid2, String dataUuid) {
        if (!this.isFormUuidInThisForm(formUuid2)) {
            return;
        }
        if (this.isMarkAsDeletedData(formUuid2, dataUuid)) {
            return;
        } else {
            if (this.deletedFormDatas == null) {
                this.deletedFormDatas = new HashMap<String, List<String>>();
            }
            List<String> dataUuids = this.deletedFormDatas.get(formUuid2);
            if (dataUuids == null) {
                dataUuids = new ArrayList<String>();
                this.deletedFormDatas.put(formUuid2, dataUuids);
            }
            if (dataUuids.contains(dataUuid)) {
                return;
            } else {
                dataUuids.add(dataUuid);
                // this.deletedFormDatas.
            }

        }

    }

    public boolean isMarkAsDeletedData(String formUuid2, String dataUuid) {
        if (this.deletedFormDatas == null) {
            return false;
        } else {
            if (this.deletedFormDatas.containsKey(formUuid2)) {
                List<String> dataUuids = this.deletedFormDatas.get(dataUuid);
                if (dataUuids == null || dataUuids.isEmpty()) {
                    return false;
                } else {
                    return dataUuids.contains(dataUuid);
                }
            }
        }
        return false;
    }

    public boolean isDataUuidExistInFormData(String formUuid2, String dataUuid) {
        if (this.getFormDataByFormUuidAndDataUuid(formUuid2, dataUuid) == null) {
            return false;
        }
        return true;
    }

    /**
     * 判断formUuid是否在该表单内，或为主表formUuid,或为从表formUuid,在返回true,不在返回false
     *
     * @param formUuid2
     * @return
     */
    public boolean isFormUuidInThisForm(String formUuid2) {
        if (this.formDefinitionMap.containsKey(formUuid2)) {
            return true;
        }
        return false;
    }

    /**
     * 所有的表单定义字段(非系统字段)设置为都需要更新的字段
     */
    public void markAllFieldsAsUpadtedFields() {

        for (String formUuid : formDatas.keySet()) {
            List<Map<String, Object>> formDatasOfOneForm = formDatas.get(formUuid);
            if (formDatasOfOneForm == null || formDatasOfOneForm.size() == 0) {
                continue;
            }

            for (Map<String, Object> formData : formDatasOfOneForm) {
                if (formData.isEmpty()) {
                    continue;
                }

                String dataUuid = null;
                dataUuid = (String) formData.get(EnumSystemField.uuid.getName());
                if (this.isMarkedAsNewFormData(formUuid, dataUuid)) {// 已被标识为新增记录
                    continue;
                } else {
                    this.markAllFieldsAsUpadtedFields(formUuid, dataUuid);
                }

            }

        }

    }

    private void markAllFieldsAsUpadtedFields(String formUuid, String dataUuid) {
        FormDefinitionHandler dUtils = this.doGetdUtil(formUuid);
        List<String> fieldNames = new ArrayList<String>();
        for (String fieldName : dUtils.getFieldNames()) {
            if (dUtils.isSysField(fieldName)) {// 系统字段
                continue;
            } else {
                fieldNames.add(fieldName);
            }
        }

        this.markAsUpdatedFields(formUuid, dataUuid, fieldNames);
    }

    public boolean isMarkedAsUpdatedRecord(String formUuid, String dataUuid) {
        Map<String /* 数据记录uuid */, Set<String>/* 字段值 */> records = this.updatedFormDatas.get(formUuid);
        if (records == null) {
            return false;
        } else {
            if (records.containsKey(dataUuid)) {
                return true;
            }
        }

        return false;

    }

    private void markFormDatasAsNew(Map<String, List<Map<String, Object>>> formDatas) {
        if (formDatas == null || formDatas.size() == 0) {
            return;
        }
        for (String formUuid : formDatas.keySet()) {
            List<Map<String, Object>> formDatasOfOneForm = formDatas.get(formUuid);
            if (formDatasOfOneForm == null || formDatasOfOneForm.size() == 0) {
                continue;
            }
            // Map<String/*表单定义id*/, List<String>/*表单数据id*/>
            for (Map<String, Object> formData : formDatasOfOneForm) {
                String dataUuid = (String) (FormDataHandler.getValueFromMap(EnumRelationTblSystemField.uuid.name(),
                        formData));
                if (this.isMarkedAsNewFormData(formUuid, dataUuid)) {// 已在日志中有标识了该数据为新增数据
                    continue;
                } else {
                    this.markAsAddedFormData(formUuid, dataUuid);// 在日志中标识该数据为新增数据
                }

            }
        }
    }

    /**
     * 判断数据是否是新数据
     *
     * @param formUuid
     * @param dataUuid
     * @return true 表示新数据,false表示旧数据
     */
    public boolean isMarkedAsNewFormData(String formUuid, String dataUuid) {
        List<String> dataUuids = this.addedFormDatas.get(formUuid);
        if (dataUuids == null || dataUuids.size() == 0) {
            return false;
        }
        if (dataUuids.contains(dataUuid)) {
            return true;
        }

        return false;

    }

    public DyformDataSignature getSignature() {
        return signature;
    }

    public void setSignature(DyformDataSignature signature) {
        this.signature = signature;
    }

    public Map<String, List<String>> getDeletedFormDatas() {
        return deletedFormDatas;
    }

    public void setDeletedFormDatas(Map<String, List<String>> deletedFormDatas) {
        this.deletedFormDatas = deletedFormDatas;
    }

    /**
     * @param formUuid
     * @param dataUuid 对于主表该参数可放置为null
     * @return
     */
    public FormDataHandler getFormDataUtils(String formUuid, String dataUuid) {
        if (this.formDefinition == null) {
            this.initFormDefintion();
        }

        FormDefinitionHandler dUtils = this.formDefinitionMap.get(formUuid).doGetFormDefinitionHandler();
        List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>> formDatasOfFormUuid = this.getFormDatas().get(formUuid);
        if (formDatasOfFormUuid == null) {// 找不到formUuid,dataUuid对应的数据
            if (this.isMainform(formUuid)) {// 如果是主表，则生成一条数据
                Map<String, Object> formDataColMap = new HashMap<String, Object>();
                if (dataUuid == null) {
                    dataUuid = dyFormFacade.createUuid();
                }
                formDataColMap.put(EnumSystemField.uuid.name(), dataUuid);
                formDatasOfFormUuid = new ArrayList<Map<String, Object>>();
                formDatasOfFormUuid.add(formDataColMap);
                Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> formDatas = new HashMap<String, List<Map<String, Object>>>();
                formDatas.put(formUuid, formDatasOfFormUuid);
                this.setFormDatas(formDatas, true);
                return new FormDataHandler(formUuid, formDatasOfFormUuid.get(0), dUtils);
            }
            return null;
        }

        if (this.formUuid.equals(formUuid)) {// 主表
            return new FormDataHandler(formUuid, formDatasOfFormUuid.get(0), dUtils);
        }

        if (dataUuid == null) {
            return null;
        }
        for (Map<String /* 表单字段名 */, Object/* 表单字段值 */> formData : formDatasOfFormUuid) {
            if (dataUuid.equals(((String) formData.get("uuid")))) {
                return new FormDataHandler(formUuid, formData, dUtils);
            }
        }
        return null;
    }

    public boolean isMainform(String formUuid) {
        if (StringUtils.equals(formUuid, this.formUuid) || StringUtils.equals(formUuid, dUtils.getPformUuid())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置表单字段为必填项
     *
     * @param formUuid 表单定义UUID
     * @param fields   字段名列表
     */
    public void setRequiredFields(String formUuid, List<String> requiredFields) {
        setRequiredFields(formUuid, requiredFields, Lists.newArrayList());
    }

    public void setRequiredFields(String formUuid, List<String> requiredFields, List<String> noRequiredFields) {
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        FormDefinitionHandler dUtils = dydef.doGetFormDefinitionHandler();
        List<String> fields = dUtils.getFieldNames();
        for (String fieldName : fields) {
            if (!dUtils.isFieldInDefinition(fieldName)) {
                // 防止流程将从表定义uuid传进来做为字段设置必填
                continue;
            }
            if (requiredFields.contains(fieldName)) {
                dUtils.setRequired(fieldName, true);
            } else if (noRequiredFields.contains(fieldName)) {
                dUtils.setRequired(fieldName, false);
            } else {
                // 其他
            }
        }
        if (!isMainform(formUuid)) {
            // 如果设置的是从表中的定义则更新从表定义
            this.addSubformDefinition(dydef);
        }
    }

    /**
     * 获取表单必填字段
     *
     * @param formUuid
     * @param fields
     * @return
     */
    public List<String> getRequiredFields(String formUuid) {
        List<String> requiredFields = new ArrayList<String>();
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return Collections.emptyList();
        }
        JSONObject fieldDefinitionJSONObjects = dydef.doGetFormDefinitionHandler().getFieldDefinitions();
        Set<String> fieldSet = fieldDefinitionJSONObjects.keySet();
        for (String field : fieldSet) {
            JSONArray rules = dydef.doGetFormDefinitionHandler().getFieldPropertyOfJSONArrayType(field,
                    EnumFieldPropertyName.fieldCheckRules);
            if (rules == null) {
                continue;
            }
            for (int index = 0; index < rules.length(); index++) {
                JSONObject rule;
                try {
                    rule = rules.getJSONObject(index);
                    if (EnumDyCheckRule.notNull.getRuleKey().equals(
                            rule.getString(EnumFieldPropertyName_fieldCheckRules.value.name()))) {
                        requiredFields.add(field);
                        break;
                    }
                } catch (JSONException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return requiredFields;
    }

    /**
     * 设置表单字段为隐藏项
     *
     * @param formUuid 表单定义UUID
     * @param fields   字段名列表
     */
    public void setHiddenFields(String formUuid, List<String> hiddenFields) {
        setHiddenFields(formUuid, hiddenFields, Lists.newArrayList());
    }

    public void setHiddenFields(String formUuid, List<String> hiddenFields, List<String> showFields) {
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        FormDefinitionHandler dUtils = dydef.doGetFormDefinitionHandler();
        List<String> fields = dUtils.getFieldNames();
        for (String fieldName : fields) {
            if (isMainform(formUuid)) {
                if (hiddenFields.contains(fieldName)) {
                    dUtils.setShowTypeAsHide(fieldName, true);
                } else if (showFields.contains(fieldName)) {
                    dUtils.setShowTypeAsHide(fieldName, false);
                } else {
                    // 旧数据
                }
            } else {
                if (hiddenFields.contains(fieldName)) {
                    this.dUtils.addSubformFieldPropertyOfStringType(formUuid, fieldName,
                            EnumSubformFieldPropertyName.hidden, EnumSubFormFieldShow.hide.getValue());
                } else if (showFields.contains(fieldName)) {
                    this.dUtils.addSubformFieldPropertyOfStringType(formUuid, fieldName,
                            EnumSubformFieldPropertyName.hidden, EnumSubFormFieldShow.show.getValue());
                } else {
                    // 旧数据
                }
            }
        }
        if (!isMainform(formUuid)) {
            this.addSubformDefinition(dydef);
        }
    }

    /**
     * 设置表单字段为显示
     *
     * @param formUuid 表单定义UUID
     * @param fields   字段名列表
     */
    public void setShowFields(String formUuid, List<String> fields) {
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        for (String fieldName : fields) {
            if (isMainform(formUuid)) {
                dydef.doGetFormDefinitionHandler().setShowTypeAsHide(fieldName, false);
            } else {
                /*
                 * dUtils.addSubformFieldPropertyOfStringType(formUuid,
                 * fieldName, EnumSubformFieldPropertyName.hidden,
                 * EnumSubFormFieldShow.hide.getValue());
                 */
            }

        }
        if (!isMainform(formUuid))
            this.addSubformDefinition(dydef);
    }

    /**
     * 设置表单字段显示为文本
     *
     * @param formUuid 表单定义UUID
     * @param fields   字段名列表
     */
    public void setLabelFields(String formUuid, List<String> fields) {
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {

            RuntimeException ex = new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
        for (String fieldName : fields) {

            if (isMainform(formUuid)) {
                dydef.doGetFormDefinitionHandler().setShowTypeAsLabel(fieldName);
            } else {
                dUtils.addSubformFieldPropertyOfStringType(formUuid, fieldName, EnumSubformFieldPropertyName.editable,
                        "0");
            }
        }
        if (!isMainform(formUuid))
            this.addSubformDefinition(dydef);
    }

    /**
     * 设置表单字段为只读
     *
     * @param formUuid 表单定义UUID
     * @param fields   字段名列表
     */
    public void setReadonlyFields(String formUuid, List<String> fields) {
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        for (String fieldName : fields) {

            if (isMainform(formUuid)) {
                dydef.doGetFormDefinitionHandler().setShowTypeAsReadOnly(fieldName);
            } else {
                dUtils.addSubformFieldPropertyOfStringType(formUuid, fieldName, EnumSubformFieldPropertyName.editable,
                        "0");
            }
        }
        if (!isMainform(formUuid))
            this.addSubformDefinition(dydef);
    }

    /**
     * 设置表单字段为可编辑
     *
     * @param formUuid 表单定义UUID
     * @param fields   字段名列表
     */
    public void setEditableFields(String formUuid, List<String> fields) {
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        for (String fieldName : fields) {
            if (fieldName.length() > 32) {// 防止流程将从表定义uuid传进来做为字段设置必填
                continue;
            }
            if (isMainform(formUuid)) {
                dydef.doGetFormDefinitionHandler().setShowTypeAsEditable(fieldName);
            } else {
                dUtils.addSubformFieldPropertyOfStringType(formUuid, fieldName, EnumSubformFieldPropertyName.editable,
                        "1");
            }
        }
        if (!isMainform(formUuid))
            this.addSubformDefinition(dydef);
    }

    /**
     * 获取表单可编辑字段
     *
     * @param formUuid
     * @param fields
     * @return
     */
    public List<String> getEditableFields(String formUuid) {
        List<String> editableFields = new ArrayList<String>();
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return Collections.emptyList();
        }
        JSONObject fieldDefinitionJSONObjects = dydef.doGetFormDefinitionHandler().getFieldDefinitions();
        Set<String> fieldSet = fieldDefinitionJSONObjects.keySet();
        for (String field : fieldSet) {
            String showType = dydef.doGetFormDefinitionHandler().getFieldPropertyOfStringType(field,
                    EnumFieldPropertyName.showType);
            if (StringUtils.equals(DyShowType.edit, showType)) {
                editableFields.add(field);
            }
        }
        return editableFields;
    }

    /**
     * 设置表单字段 fileFieldSecDevBtnId
     *
     * @param formUuid             表单定义UUID
     * @param fieldName            字段名列表
     * @param fileFieldSecDevBtnId fileFieldSecDevBtnId
     */
    @Override
    public void setFileFieldSecDevBtnId(String formUuid, String fieldName, String fileFieldSecDevBtnId) {
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }

        if (fieldName.length() > 32) {// 防止流程将从表定义uuid传进来做为字段设置必填
            return;
        }
        if (isMainform(formUuid)) {
            dydef.doGetFormDefinitionHandler().setSecDevBtnIdStr(fieldName, fileFieldSecDevBtnId);
        } else {
            try {
                JSONObject subformProperty = dUtils.getSubformPropertyOfJSONType(formUuid, DyFormConfig.EnumSubformPropertyName.fields);
                if (subformProperty != null && subformProperty.has(fieldName)) {
                    subformProperty.getJSONObject(fieldName).put("flowSecDevBtnIdStr", fileFieldSecDevBtnId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!isMainform(formUuid))
            this.addSubformDefinition(dydef);
    }

    /**
     * 设置表单字段为不可用
     *
     * @param formUuid 表单定义UUID
     * @param fields   字段名列表
     */
    public void setDisabledFields(String formUuid, List<String> fields) {
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        for (String fieldName : fields) {
            if (isMainform(formUuid)) {
                dydef.doGetFormDefinitionHandler().setShowTypeAsDisable(fieldName);
            } else {
                /*
                 * dUtils.addSubformFieldPropertyOfStringType(formUuid,
                 * fieldName, EnumSubformFieldPropertyName.hidden,
                 * EnumSubFormFieldShow.hide.getValue());
                 */
            }
        }
        if (!isMainform(formUuid))
            this.addSubformDefinition(dydef);
    }

    /**
     * 设置表单字段为是否可以下载（附件控件）
     *
     * @param formUuid 表单定义UUID
     * @param fields   字段名列表
     */
    public void setAllowDownloadFields(String formUuid, List<String> fields, boolean allowDownload) {
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        for (String fieldName : fields) {

            if (isMainform(formUuid)) {
                try {
                    dydef.doGetFormDefinitionHandler().addFieldProperty(fieldName, EnumFieldPropertyName.allowDownload,
                            allowDownload);
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getStackTrace(e), e);
                }
            }
        }
    }

    /**
     * 设置表单字段为是否可以删除（附件控件）
     *
     * @param formUuid 表单定义UUID
     * @param fields   字段名列表
     */
    public void setAllowDeleteFields(String formUuid, List<String> fields, boolean allowDelete) {
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        for (String fieldName : fields) {
            if (isMainform(formUuid)) {
                try {
                    dydef.doGetFormDefinitionHandler().addFieldProperty(fieldName, EnumFieldPropertyName.allowDelete,
                            allowDelete);
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getStackTrace(e), e);
                }
            }
        }
    }

    /**
     * 设置表单字段为是否可以上传（附件控件）
     *
     * @param formUuid 表单定义UUID
     * @param fields   字段名列表
     */
    public void setAllowUploadFields(String formUuid, List<String> fields, boolean allowUpload) {
        FormDefinition dydef = this.formDefinitionMap.get(formUuid);
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        for (String fieldName : fields) {

            if (isMainform(formUuid)) {
                try {
                    dydef.doGetFormDefinitionHandler().addFieldProperty(fieldName, EnumFieldPropertyName.allowUpload,
                            allowUpload);
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getStackTrace(e), e);
                }
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#setTemplateEditableFields(java.lang.String, java.util.List)
     */
    @Override
    public void setTemplateEditableFields(String key, List<String> editFields) {
        FormDefinition dydef = this.formDefinitionMap.get(doGetdUtils().getFormUuid());
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        dydef.doGetFormDefinitionHandler().setTemplateEditableFields(key, editFields);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#setTemplateLabelFields(java.lang.String, java.util.List)
     */
    @Override
    public void setTemplateLabelFields(String key, List<String> onlyReadFields) {
        FormDefinition dydef = this.formDefinitionMap.get(doGetdUtils().getFormUuid());
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        dydef.doGetFormDefinitionHandler().setTemplateLabelFields(key, onlyReadFields);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#setTemplateHiddenFields(java.lang.String, java.util.List)
     */
    @Override
    public void setTemplateHiddenFields(String key, List<String> hideFields) {
        FormDefinition dydef = this.formDefinitionMap.get(doGetdUtils().getFormUuid());
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        dydef.doGetFormDefinitionHandler().setTemplateHiddenFields(key, hideFields);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#setTemplateRequiredFields(java.lang.String, java.util.List)
     */
    @Override
    public void setTemplateRequiredFields(String key, List<String> requiredFields) {
        FormDefinition dydef = this.formDefinitionMap.get(doGetdUtils().getFormUuid());
        if (dydef == null) {
            // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
            return;
        }
        dydef.doGetFormDefinitionHandler().setTemplateRequiredFields(key, requiredFields);
    }

    /**
     * 判断是否有指定的字段映射名
     *
     * @param fieldMappingName
     * @return
     */
    public boolean hasFieldMappingName(String fieldMappingName) {
        return this.dUtils.hasFieldNameByAppyTo(fieldMappingName);
    }

    /**
     * 通过传入的映射名来获取字段显示值
     *
     * @param fieldMappingName
     * @return
     */
    public Object getFieldDisplayValueByMappingName(String fieldMappingName) {
        FormDataHandler utils = this.getFormDataUtils(this.formUuid, null);
        return utils.getDisplayValueByApplyTo(fieldMappingName);
    }

    /**
     * 通过传入的映射名来获取字段值
     *
     * @param fieldMappingName
     * @return
     */
    public Object getFieldValueByMappingName(String fieldMappingName) {
        FormDataHandler utils = this.getFormDataUtils(this.formUuid, null);
        return utils.getValueByApplyTo(fieldMappingName);
    }

    /**
     * 通过映射字段获取表单字段名
     *
     * @param mappingName
     * @return
     */
    @Override
    public List<String> getFieldNamesByMappingName(String mappingName) {
        FormDataHandler utils = this.getFormDataUtils(this.formUuid, null);
        return utils.getdUtils().getFieldNameByApplyTo(mappingName);
    }

    /**
     * 通过字段名获取字段显示值
     *
     * @param fieldName
     * @return
     */
    public Object getFieldDisplayValue(String fieldName) {
        FormDataHandler utils = this.getFormDataUtils(this.formUuid, null);
        return utils.getDisplayValue(fieldName);
    }

    /**
     * 通过字段名获取字段真实值
     *
     * @param fieldName
     * @return
     */
    @Override
    @Deprecated
    public Object getFieldRealValue(String fieldName) {
        return getFieldValue(fieldName);
    }

    public boolean isValueBlank(String fieldName) {
        Object v = this.getFieldValue(fieldName);
        if (v == null || StringUtils.isBlank(v.toString())) {
            return true;
        }

        return false;

    }

    /**
     * 通过字段名获取字段值
     *
     * @param fieldName
     * @return
     */
    @Override
    public Object getFieldValue(String fieldName) {
        FormDataHandler utils = this.getFormDataUtils(this.formUuid, null);
        return utils.getValue(fieldName);
    }

    public List<String> getValueOfFileIds(String fieldName) {
        FormDataHandler utils = this.getFormDataUtils(this.formUuid, null);
        return utils.getValueOfFileIds(fieldName);
    }

    @Override
    public List<String> getValueOfFileIds(String fieldName, String dataUuid) {
        FormDataHandler utils = this.getFormDataUtils(this.formUuid, dataUuid);
        return utils.getValueOfFileIds(fieldName, dataUuid);
    }

    public List<String> getValueOfFileIds4Subform(String fieldName, String formUuidx, String dataUuid) {
        FormDataHandler utils = this.getFormDataUtils(formUuidx, dataUuid);
        return utils.getValueOfFileIds(fieldName);
    }

    /**
     * 设置从表的数据排序顺序
     *
     * @param order
     */
    @Override
    public void setSortOrder(final Integer order) {
        if (order == null) {
            this.setFieldValue(EnumRelationTblSystemField.sort_order.name(), 0);
            return;
        }
        this.setFieldValue(EnumRelationTblSystemField.sort_order.name(), order);
    }

    /**
     * 设置字段的值
     *
     * @param fieldName
     * @param value
     */
    public void setFieldValue(String fieldName, Object value) {
        FormDataHandler utils = this.getFormDataUtils(this.formUuid, null);
        if (EnumSystemField.uuid.name().equalsIgnoreCase(fieldName)) {
            final String dataUuid = this.getDataUuid();
            final String formUuid = this.getFormUuid();
            if (isMarkedAsNewFormData(formUuid, dataUuid)) {// 对于新表单则可更改dataUuid
                this.removeFromAddedFormdatas(formUuid, dataUuid);
                utils.setValue(EnumSystemField.uuid.name(), value);
                this.markAsAddedFormData(formUuid, (String) value);
            } else {
                throw new RuntimeException("this is not a new dyform data, uuid cann't be changed");
            }

            return;
        }

        //		if (this.dUtils.isValueAsMap(fieldName)) {
        //			Map<String, Object> d = new HashMap<String, Object>();
        //			try {
        //				String valueObj = (String) FormDataHandler.convertData2DbType(DyFormConfig.DbDataType._string, value,
        //						null);
        //				if (valueObj == null || valueObj.trim().length() == 0) {
        //					utils.setValue(fieldName, d);
        //					return;
        //				} else {
        //					if (valueObj.trim().startsWith("{") && valueObj.trim().endsWith("}")) {
        //						try {
        //							JSONObject jsonValue = new JSONObject(valueObj);
        //							Iterator<String> it = jsonValue.keys();
        //							while (it.hasNext()) {
        //								String key = it.next();
        //								d.put(key, jsonValue.has(key) ? jsonValue.get(key) : null);
        //							}
        //						} catch (JSONException e) {
        //							logger.error(e.getMessage(), e);
        //						}
        //					} else {
        //						d.put(valueObj, valueObj);
        //						utils.setValue(fieldName, d);
        //					}
        //				}
        //				// d.put(fieldName, d);
        //			} catch (ParseException e) {
        //				logger.error(e.getMessage(), e);
        //			}
        //			utils.setValue(fieldName, d);
        //		} else {
        utils.setValue(fieldName, value);
        //		}

        this.markAsUpdatedField(this.getFormUuid(), this.getDataUuid(), fieldName);
    }

    /**
     * 设置字段的值,json控件传显示值
     *
     * @param fieldName
     * @param fieldValue
     */
    @Override
    public void setFieldValueByDisplayValue(String fieldName, Object fieldValue) {
        FormDataHandler utils = this.getFormDataUtils(this.formUuid, null);
        if (dUtils.isValueAsMap(fieldName)) {
            if (fieldValue != null && fieldValue instanceof String) {
                String fieldSeparater = dUtils.getFieldSeparater(fieldName);
                String[] fieldValues = StringUtils.split((String) fieldValue, fieldSeparater);
                List<String> fieldRealValues = new ArrayList<String>();
                for (String localFieldValue : fieldValues) {
                    String fieldRealValue = dUtils
                            .getFieldDictionaryRealValueByDisplayValue(fieldName, localFieldValue);
                    fieldRealValues.add(fieldRealValue == null ? localFieldValue : fieldRealValue);
                }
                utils.setValue(fieldName, StringUtils.join(fieldRealValues, fieldSeparater));
                // 设置显示值和真实值的映射
                String realFieldName = dUtils.getRealOfRealDisplay(fieldName);
                String displayFieldName = dUtils.getDisplayOfRealDisplay(fieldName);
                if (StringUtils.isNotBlank(realFieldName) && utils.containsKey(realFieldName) == false) {
                    utils.setValue(realFieldName, StringUtils.join(fieldRealValues, fieldSeparater));
                }
                if (StringUtils.isNotBlank(displayFieldName) && utils.containsKey(displayFieldName) == false) {
                    utils.setValue(displayFieldName, fieldValue);
                }
            } else {
                utils.setValue(fieldName, fieldValue);
            }
            markAsUpdatedField(getFormUuid(), getDataUuid(), fieldName);
        } else {
            setFieldValue(fieldName, fieldValue);
        }
    }

    private void removeFromAddedFormdatas(String formUuid, String dataUuid) {
        List<String>/* 表单数据id */list = this.getAddedFormDatas().get(formUuid);
        if (list == null || list.size() == 0) {
            return;
        } else {
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                String dataUuidtmp = it.next();
                if (dataUuidtmp != null && dataUuidtmp.equalsIgnoreCase(dataUuid)) {
                    it.remove();
                }
            }
        }
    }

    /**
     * 根据映射名设置对应的字段值
     *
     * @param mappingName
     * @param value
     */
    public void setFieldValueByMappingName(String mappingName, Object value) {
        FormDataHandler utils = this.getFormDataUtils(this.formUuid, null);
        utils.setValueByApplyTo(mappingName, value);
        List<String> fieldNames = dUtils.getFieldNameByApplyTo(mappingName);

        for (String fieldName : fieldNames) {
            this.markAsUpdatedField(this.getFormUuid(), this.getDataUuid(), fieldName);
        }
    }

    public String getDataUuid() {
        Map<String /* 表单字段名 */, Object/* 表单字段值 */> formDatasOfMainform = this.getFormDataOfMainform();
        if (formDatasOfMainform == null) {
            return null;
        }
        return (String) formDatasOfMainform.get("uuid");
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    public void setDataUuid(String dataUUid) {
        this.setFieldValue(EnumSystemField.uuid.name(), dataUUid);
        // /FormDataHandler utils = this.getFormDataUtils(this.formUuid,
        // dataUUid);
        // utils.setValue("uuid", dataUUid);
    }

    @JsonIgnore
    public Map<String /* 表单字段名 */, Object/* 表单字段值 */> getFormDataOfMainform() {
        if (this.getFormDatas() == null) {
            return null;
        }
        if (this.getFormDatas().get(this.formUuid) == null || this.getFormDatas().get(this.formUuid).size() == 0) {
            return null;
        }
        return this.getFormDatas().get(this.formUuid).get(0);
    }

    /**
     * 获取所有的字段的显示值,formUuid为key
     *
     * @return
     */
    @JsonIgnore
    public Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> getDisplayValues() {

        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> displayFormDatas = new HashMap<String, List<Map<String, Object>>>();
        if (formDatas == null) {
            return null;
        }
        for (String formUuid : formDatas.keySet()) {
            List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>> formDataOneForm = this.formDatas.get(formUuid);
            List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>> displayFormDataOneForm = new ArrayList<Map<String, Object>>();
            for (Map<String /* 表单字段名 */, Object/* 表单字段值 */> formData : formDataOneForm) {
                FormDefinition formDefinition = this.formDefinitionMap.get(formUuid);
                if (formDefinition == null) {
                    formDefinition = (FormDefinition) this.dyFormFacade.getFormDefinition(formUuid);
                }
                if (formDefinition == null) {
                    continue;
                }
                FormDataHandler utils = new FormDataHandler(formUuid, formData, formDefinition.doGetFormDefinitionHandler());
                displayFormDataOneForm.add(utils.getDisplayValues());
            }
            displayFormDatas.put(formUuid, displayFormDataOneForm);
        }
        return displayFormDatas;
    }

    /**
     * 获取所有的字段的显示值,formId为key
     *
     * @return
     */
    @JsonIgnore
    public Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> getDisplayValuesKeyAsFormId() {

        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> displayFormDatas = new HashMap<String, List<Map<String, Object>>>();
        if (formDatas == null) {
            return null;
        }
        for (String formUuid : formDatas.keySet()) {
            List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>> formDataOneForm = this.formDatas.get(formUuid);
            List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>> displayFormDataOneForm = new ArrayList<Map<String, Object>>();
            for (Map<String /* 表单字段名 */, Object/* 表单字段值 */> formData : formDataOneForm) {
                if (this.formDefinitionMap.containsKey(formUuid)) {
                    FormDataHandler utils = new FormDataHandler(formUuid, formData, this.formDefinitionMap
                            .get(formUuid).doGetFormDefinitionHandler());
                    displayFormDataOneForm.add(utils.getDisplayValues());
                }
            }
            if (this.formDefinitionMap.containsKey(formUuid)) {
                displayFormDatas.put(this.formDefinitionMap.get(formUuid).getId(), displayFormDataOneForm);
            }
        }
        return displayFormDatas;
    }

    public String getFormDefinition() {
        if (this.dUtils == null) {
            return this.formDefinition;
        }
        return this.dUtils.toString();
    }

    @Override
    public void setFormDefinition(DyFormFormDefinition dyformDefinition) {
        this.formUuid = dyformDefinition.getUuid();
        this.initFormDefintion((FormDefinition) dyformDefinition);
    }

    public String getDefinitionVjson() {
        return definitionVjson;
    }

    @Override
    public void reassignFormDefinition() {
        if (this.dUtils != null) {
            this.formDefinition = this.getFormDefinition();
        }
    }

    public String toJson() {
        JSONObject json = new JSONObject();
        try {

            json.put("formDefinition", this.formDefinition);
            json.put("formUuid", this.getFormUuid());
            json.put("dataUuid", this.getDataUuid());
            json.put("formDatas", this.formDatas);
            json.put("definitionVjson", this.definitionVjson);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }

        return json.toString();
    }

    @Override
    public ValidateMsg validateFormData() {
        ValidateMsg vMsg = new ValidateMsg();
        if (this.formDatas == null) {//没有数据则直接验证通过
            vMsg.setCode(EnumValidateCode.NULL);
            return vMsg;
        } else if (this.formDatas.size() == 0) {
            vMsg.setCode(EnumValidateCode.EMPTY);
            return vMsg;
        }

        Iterator<String> it = this.formDatas.keySet().iterator();
        while (it.hasNext()) {
            int idx = 1;// 下标从第一行开始
            String formUuid = it.next();
            List<Map<String /*表单字段名*/, Object/*表单字段值*/>> formDataList = this.formDatas.get(formUuid);
            for (Map<String, Object> formData : formDataList) {
                ValidateMsg localMsg = this.validateFormData(formUuid, formData, isMainform(formUuid) ? null : idx++);
                if (false == localMsg.getErrors().isEmpty()) {
                    vMsg.getErrors().addAll(localMsg.getErrors());
                }
            }
        }
        if (false == vMsg.getErrors().isEmpty()) {
            vMsg.setCode(EnumValidateCode.FAIL);
            vMsg.setMsg(AppCodeI18nMessageSource.getMessage("formDataValidator.message.validateFail", "pt-dyform", LocaleContextHolder.getLocale().toString(), "校验失败"));
        }
        return vMsg;
    }

    /**
     * 校验表单数据的数据库约束——长度、类型、必填
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#validateFormDataWithDatabaseConstraints()
     */
    @Override
    public ValidateMsg validateFormDataWithDatabaseConstraints() {
        ValidateMsg vMsg = new ValidateMsg();
        if (this.formDatas == null) {//没有数据则直接验证通过
            vMsg.setCode(EnumValidateCode.NULL);
            return vMsg;
        } else if (this.formDatas.size() == 0) {
            vMsg.setCode(EnumValidateCode.EMPTY);
            return vMsg;
        }

        Iterator<String> it = this.formDatas.keySet().iterator();
        while (it.hasNext()) {
            int idx = 1;// 下标从第一行开始
            String formUuid = it.next();
            List<Map<String /*表单字段名*/, Object/*表单字段值*/>> formDataList = this.formDatas.get(formUuid);
            for (Map<String, Object> formData : formDataList) {
                ValidateMsg localMsg = this.validateFormDataWithDatabaseConstraints(formUuid, formData,
                        isMainform(formUuid) ? null : idx++);
                if (false == localMsg.getErrors().isEmpty()) {
                    vMsg.getErrors().addAll(localMsg.getErrors());
                }
            }
        }
        if (false == vMsg.getErrors().isEmpty()) {
            vMsg.setCode(EnumValidateCode.FAIL);
            vMsg.setMsg(AppCodeI18nMessageSource.getMessage("formDataValidator.message.validateFail", "pt-dyform", LocaleContextHolder.getLocale().toString(), "校验失败"));
        }
        return vMsg;
    }

    @Override
    public synchronized boolean addValidator(String fieldName, Validator validator) {
        return addValidator(getFormUuid(), fieldName, validator);
    }

    @Override
    public synchronized boolean addValidator(String formUuid, String fieldName, Validator validator) {
        if (this.customValidators == null) {
            this.customValidators = new HashMap<String, Map<String, Set<Validator>>>();
        }
        Map<String, Set<Validator>> fieldValidators = this.customValidators.get(formUuid);
        if (fieldValidators == null) {
            this.customValidators.put(formUuid, fieldValidators = new HashMap<String, Set<Validator>>());
        }
        Set<Validator> validators = fieldValidators.get(fieldName);
        if (validators == null) {
            fieldValidators.put(fieldName, validators = new HashSet<Validator>());
        }
        return validators.add(validator);
    }

    public ValidateMsg validateFormData(String formUuid, Map<String /*表单字段名*/, Object/*表单字段值*/> formData, Integer idx) {
        FormDataHandler dataUtils = new FormDataHandler(formUuid, formData);
        if (customValidators != null && customValidators.containsKey(formUuid)) {
            dataUtils.setCustomValidators(customValidators.get(formUuid));
        }
        return dataUtils.validate(idx);
    }

    /**
     * 校验表单数据的数据库约束——长度、类型、必填
     *
     * @param formUuid
     * @param formData
     * @param idx
     * @return
     */
    private ValidateMsg validateFormDataWithDatabaseConstraints(String formUuid,
                                                                Map<String /*表单字段名*/, Object/*表单字段值*/> formData, Integer idx) {
        FormDataHandler dataUtils = new FormDataHandler(formUuid, formData);
        return dataUtils.validateWithDatabaseConstraints(idx);
    }

    public void setLoadSubformDefinition(boolean loadSubformDefinition) {
        this.loadSubformDefinition = loadSubformDefinition;
    }

    public void setLoadDefaultFormData(boolean loadDefaultFormData) {
        this.loadDefaultFormData = loadDefaultFormData;
    }

    public void setLoadDictionary(boolean loadDictionary) {
        this.loadDictionary = loadDictionary;
    }

    public void showBlock(String blockCode) {
        this.dUtils.setBlockHide(blockCode, false);
    }

    public void hideBlock(String blockCode) {
        this.dUtils.setBlockHide(blockCode, true);
    }

    public List<DyformBlock> getBlocks() {
        if (this.dUtils == null) {
            return this.blocks;
        }
        JSONObject json = this.dUtils.getBlockDefinitionJSONObjects();
        if (json == null) {
            return null;
        }
        List<DyformBlock> blocks = new ArrayList<DyformBlock>();
        Iterator<String> it = json.keys();
        while (it.hasNext()) {
            String blockCode = it.next();
            try {
                JSONObject jsonObject = json.getJSONObject(blockCode);
                String blockTitle = json.getJSONObject(blockCode).getString("blockTitle");
                Boolean hide = false;
                if (!jsonObject.isNull("hide")) {
                    hide = jsonObject.getBoolean("hide");
                }
                String id = jsonObject.has("id") ? jsonObject.getString("id") : StringUtils.EMPTY;

                DyformBlock block = new DyformBlock();
                block.setBlockCode(blockCode);
                block.setBlockTitle(blockTitle);
                block.setHide(hide);
                block.setId(id);
                blocks.add(block);
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return blocks;
    }

    /**
     * 判断字段是否存在 于主表中
     *
     * @param fieldName
     * @return
     */
    public boolean isFieldExist(String fieldName) {
        return this.dUtils.isFieldInDefinition(fieldName);
    }

    /**
     * 判断字段是否存在于从表中
     *
     * @param fieldName
     * @param formUuid
     * @return
     */
    public boolean isFieldExist(String fieldName, String formUuid) {
        if (!this.isMainform(formUuid)) {
            if (!isSubformExist(formUuid)) {// 从表不存在
                return false;
            } else {
                return this.doGetdUtil(formUuid).isFieldInDefinition(fieldName);
            }
        } else {
            return this.dUtils.isFieldInDefinition(fieldName);
        }

    }

    /**
     * 判断字段是否隐藏
     *
     * @param fieldName
     * @return
     */
    public boolean isFieldHide(String fieldName) {
        return this.dUtils.isFieldHide(fieldName);
    }

    /**
     * 获取字段的中文名
     *
     * @param fieldName
     * @return
     */
    public String getDisplayNameOfField(String fieldName) {
        return this.dUtils.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.displayName);
    }

    /**
     * 判断从表是否存在
     *
     * @param formUuid
     * @return
     */
    public boolean isSubformExist(String formUuid) {
        return this.dUtils.isSubformInDefinition(formUuid);
    }

    /**
     * 隐藏从表的按钮
     */
    public void hideSubformOperateBtn(final String formUuid) {
        this.dUtils.hideSubformOperateBtn(formUuid);
    }

    /**
     * 获取附件字段名称
     *
     * @return
     */
    public List<String> doGetFieldNamesOfFile() {
        return this.dUtils.getFieldNamesOfFile();
    }

    /**
     * 获取所有字段名称
     *
     * @return
     */
    public List<String> doGetFieldNames() {
        return this.dUtils.getFieldNames();
    }

    public boolean isFileField(String fieldName) {
        return this.dUtils.isInputModeEqAttach(fieldName);
    }

    public boolean isValueAsMapField(String fieldName) {
        return this.dUtils.isValueAsMap(fieldName);
    }

    /**
     * 显示从表的按钮
     *
     * @param formUuid
     */
    public void showSubformOperateBtn(final String formUuid) {
        this.dUtils.showSubformOperateBtn(formUuid);
    }

    public Map<String, Map<String, Set<String>>> getUpdatedFormDatas() {
        return updatedFormDatas;
    }

    public void setUpdatedFormDatas(Map<String, Map<String, Set<String>>> updatedFormDatas) {
        this.updatedFormDatas = updatedFormDatas;
    }

    public Map<String, List<String>> getAddedFormDatas() {
        return addedFormDatas;
    }

    public void setAddedFormDatas(Map<String, List<String>> addedFormDatas) {
        this.addedFormDatas = addedFormDatas;
    }

    /**
     * 获取指定的从表的新增记录
     *
     * @param formUuid
     * @return
     */
    public List<String> getAddedFormDatas(String formUuid) {
        if (this.getAddedFormDatas() == null) {
            return null;
        } else {
            return addedFormDatas.get(formUuid);
        }

    }

    public FormDefinitionHandler doGetdUtils() {
        return dUtils;
    }

    public FormDefinitionHandler doGetdUtil(String formUuid) {
        return this.doGetDyformDefinition(formUuid).doGetFormDefinitionHandler();
    }

    public FormDefinition doGetDyformDefinition(String formUuid) {
        return this.formDefinitionMap.get(formUuid);
    }

    public void clearPermanenceInfo() {
        if (this.addedFormDatas != null) {
            this.addedFormDatas.clear();
        }
        if (this.deletedFormDatas != null) {
            this.deletedFormDatas.clear();
        }
        if (this.updatedFormDatas != null) {
            this.updatedFormDatas.clear();
        }

    }

    /**
     * 强制覆盖掉数据库中的记录，不做修改时间对比
     *
     * @param formUuid
     * @param dataUuid
     */
    public void doForceCover(final String formUuid, final String dataUuid) {
        doBindModifyTimeAsCurrent(formUuid, dataUuid);
    }

    /**
     * 强制覆盖掉数据库中的记录，不做修改时间对比
     */
    public void doForceCover() {
        doForceCover(this.getFormUuid(), this.getDataUuid());
    }

    private void doBindModifyTimeAsCurrent(final String formUuid, final String dataUuid) {
        boolean isMainform = isMainform(formUuid);
        for (String localFormUuid : formDatas.keySet()) {
            List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>> formDataList = formDatas.get(localFormUuid);
            for (Map<String /* 表单字段名 */, Object/* 表单字段值 */> formData : formDataList) {
                if (isMainform) {
                    // 主表全部覆盖
                    formData.put(EnumSystemField.modify_time.name(), null);
                } else if (StringUtils.equals(formUuid, localFormUuid)
                        && StringUtils.equals(dataUuid, (String) formData.get(EnumSystemField.uuid.name()))) {
                    // 从表覆盖具体记录
                    formData.put(EnumSystemField.modify_time.name(), null);
                }
            }

        }
    }

    /**
     * 获取主表及从表的formUuid
     *
     * @return
     */
    public List<String> doGetFormUuids() {
        return this.doGetdUtils().getFormUuids();
    }

    public List<String> getSubformUuidsByMappingName(String mappingName) {
        return dUtils.getSubformUuidsByMappingName(mappingName);
    }

    public DyFormData doCopy(boolean depth) {
        String sysFieldUuid = EnumSystemField.uuid.getName();
        DyFormDataImpl dyFormdata = new DyFormDataImpl(formUuid, true, false, false);
        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> localFormDatas = new HashMap<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>>();
        for (String formUuid : formDatas.keySet()) {
            List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>> dataList = formDatas.get(formUuid);
            List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>> localDataList = new ArrayList<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>();
            for (Map<String /* 表单字段名 */, Object/* 表单字段值 */> data : dataList) {
                Map<String /* 表单字段名 */, Object/* 表单字段值 */> localData = new HashMap<String /* 表单字段名 */, Object/* 表单字段值 */>();
                for (Map.Entry<String /* 表单字段名 */, Object/* 表单字段值 */> entry : data.entrySet()) {
                    if (sysFieldUuid.equals(entry.getKey())) {
                        localData.put(entry.getKey(), UuidUtils.createUuid());
                    } else if ("nestformDatas".equals(entry.getKey())) {
                        String nestformDatasObj = (String) entry.getValue();
                        if (StringUtils.isNotBlank(nestformDatasObj)) {
                            DyFormData dyFormData = dyFormFacade.parseDyformData(nestformDatasObj);
                            // 拷贝标识新增
                            DyFormData nestDyFormData = (DyFormData) dyFormData.doCopy(depth);
                            nestformDatasObj = nestDyFormData.cloneDyFormDatasToJson();
                        }
                        localData.put(entry.getKey(), nestformDatasObj);
                    } else {
                        localData.put(entry.getKey(), entry.getValue());
                    }
                }
                localDataList.add(localData);
            }
            localFormDatas.put(formUuid, localDataList);
        }
        dyFormdata.setFormDatas(localFormDatas, true);
        dyFormdata.markAllAsAddedFormData();// 标记为新增
        dyFormdata.dyformDataOptions = dyformDataOptions;
        return dyFormdata;
    }

    /**
     * 获取页签
     *
     * @return
     */
    @Override
    @JsonIgnore
    public List<DyformTab> getTabs() {
        JSONObject json = this.dUtils.getLayOutDefinitionJSONObjects();
        if (json == null) {
            return null;
        }
        List<DyformTab> subTabs = new ArrayList<DyformTab>();
        Iterator<String> it = json.keys();
        //layouts.BASIC_INFO.subtabs.subtbl_C5K5LIVTVAB3F7VC
        while (it.hasNext()) {
            String blockCode = it.next();
            try {
                JSONObject subtabs = json.getJSONObject(blockCode).getJSONObject("subtabs");
                Iterator<String> subtabIterator = subtabs.keys();
                while (subtabIterator.hasNext()) {
                    String key = subtabIterator.next();
                    try {
                        String name = subtabs.getJSONObject(key).getString("name");
                        String displayName = subtabs.getJSONObject(key).getString("displayName");
                        Boolean hide = false;
                        if (!subtabs.getJSONObject(key).isNull("hide")) {
                            hide = subtabs.getJSONObject(key).getBoolean("hide");
                        }
                        DyformTab subtab = new DyformTab();
                        subtab.setDisplayName(displayName);
                        subtab.setName(name);
                        subtab.setHide(hide);
                        subTabs.add(subtab);
                    } catch (JSONException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return subTabs;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#hideSubTab(java.lang.String)
     */
    @Override
    public void hideTab(String tab) {
        this.dUtils.setTabHide(tab, true);
    }

    @Override
    public void showImp(String formUuid) {
        this.dUtils.setImpShow(formUuid, false);
    }

    @Override
    public void hideImp(String formUuid) {
        this.dUtils.setImpShow(formUuid, true);
    }

    @Override
    public void showExp(String formUuid) {
        this.dUtils.setExpShow(formUuid, false);
    }

    @Override
    public void hideExp(String formUuid) {
        this.dUtils.setExpShow(formUuid, true);
    }

    @Override
    public String getBeforeSaveFormDataServiceFilter() {
        return this.beforeSaveFormDataServiceFilter;
    }

    public void setBeforeSaveFormDataServiceFilter(String beforeSaveFormDataServiceFilter) {
        this.beforeSaveFormDataServiceFilter = beforeSaveFormDataServiceFilter;
    }

    @Override
    public void showSubformOperateBtn(String formUuid, EnumHideSubFormOperateBtn operateBtn) {
        this.dUtils.showSubformOperateBtn(formUuid, operateBtn);
    }

    @Override
    public DyformDataOptions doGetDyformDataOptions() {
        if (null == dyformDataOptions) {
            this.dyformDataOptions = new HashMap<String, Object>();
        }
        return new DyformDataOptions(dyformDataOptions);
    }

    public void setDyformDataOptions(Map<String, Object> dyformDataOptions) {
        this.dyformDataOptions = dyformDataOptions;
    }

    /**
     * 克隆JSON数据
     *
     * @return 表单数据的json
     */
    @Override
    public String cloneDyFormDatasToJson() {
        String formDatasJson = JsonUtils.object2Json(this);
        try {
            JSONObject nestformDatasJson = new JSONObject(formDatasJson);
            JSONObject nestformDatasJson2 = new JSONObject();
            for (Object keep : nestformDatasJson.keySet()) {
                if ("formUuid".equals(keep) || "formDatas".equals(keep) || "addedFormDatas".equals(keep)
                        || "updatedFormDatas".equals(keep) || "deletedFormDatas".equals(keep)
                        || "dyformDataOptions".equals(keep)) {
                    nestformDatasJson2.put((String) keep, nestformDatasJson.opt((String) keep));
                }
            }
            formDatasJson = nestformDatasJson2.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return formDatasJson;
    }

    public String doGetTitle() {

        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#hidePlaceholderCtr(java.lang.String)
     */
    @Override
    public void hidePlaceholderCtr(String placeholderCtrField) {
        this.dUtils.setPlaceholderCtrHide(placeholderCtrField);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#showPlaceholderCtr(java.lang.String)
     */
    @Override
    public void showPlaceholderCtr(String placeholderCtrField) {
        this.dUtils.setPlaceholderCtrEditable(placeholderCtrField);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#hideFileLibrary(java.lang.String)
     */
    @Override
    public void hideFileLibrary(String fileLibraryFieldId) {
        this.dUtils.setFileLibraryHide(fileLibraryFieldId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#showFileLibrary(java.lang.String)
     */
    @Override
    public void showFileLibrary(String fileLibraryFieldId) {
        this.dUtils.setFileLibraryEditable(fileLibraryFieldId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#hideTableView(java.lang.String)
     */
    @Override
    public void hideTableView(String fileLibraryFieldId) {
        this.dUtils.setTableViewHide(fileLibraryFieldId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#showTableView(java.lang.String)
     */
    @Override
    public void showTableView(String fileLibraryFieldId) {
        this.dUtils.setTableViewEditable(fileLibraryFieldId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#setHiddenSubforms(java.lang.String, java.util.List)
     */
    @Override
    public void setHiddenSubforms(String wrapperKey, List<String> subformIds) {
        List<String> subformUuids = dUtils.setHiddenSubformByIds(subformIds);
        for (String subformUuid : subformUuids) {
            DyFormFormDefinition dydef = this.formDefinitionMap.get(subformUuid);
            if (dydef == null) {
                // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
                continue;
            }
            this.addSubformDefinition(dydef);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.dto.DyFormData#setEditableSubforms(java.lang.String, java.util.List)
     */
    @Override
    public void setEditableSubforms(String wrapperKey, List<String> subformIds) {
        List<String> subformUuids = dUtils.setEditableSubformByIds(subformIds);
        for (String subformUuid : subformUuids) {
            DyFormFormDefinition dydef = this.formDefinitionMap.get(subformUuid);
            if (dydef == null) {
                // throw new RuntimeException("subform formUuid:" + formUuid + " not in form:" + this.formUuid);
                continue;
            }
            this.addSubformDefinition(dydef);
        }
    }

    public List<AppDefElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<AppDefElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }

    public Map<String, String> getNewOldUuidMap() {
        return newOldUuidMap;
    }

    public void setNewOldUuidMap(Map<String, String> newOldUuidMap) {
        this.newOldUuidMap = newOldUuidMap;
    }
}
