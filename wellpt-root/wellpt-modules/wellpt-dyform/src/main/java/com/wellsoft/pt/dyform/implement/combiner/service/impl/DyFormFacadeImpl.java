package com.wellsoft.pt.dyform.implement.combiner.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.context.exception.UniqueException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.context.util.encode.JsonBinder;
import com.wellsoft.context.util.i18n.MsgUtils;
import com.wellsoft.context.util.io.ClobUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.datasource.service.DataSourceDefinitionService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.dyform.facade.dto.*;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.facade.support.DyformDataServiceFilter;
import com.wellsoft.pt.dyform.implement.combiner.dto.impl.DyFormDataImpl;
import com.wellsoft.pt.dyform.implement.data.exceptions.SaveDataException;
import com.wellsoft.pt.dyform.implement.data.utils.FormDataHandler;
import com.wellsoft.pt.dyform.implement.data.utils.ValidateMsg;
import com.wellsoft.pt.dyform.implement.definition.cache.DyformCacheUtils;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFieldPropertyName;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumRelationTblSystemField;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.dyform.implement.definition.event.FormDataDeleteEvent;
import com.wellsoft.pt.dyform.implement.definition.event.FormDataSavedEvent;
import com.wellsoft.pt.dyform.implement.definition.exceptions.HibernateDataExistException;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.BeanCopyUtils;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.DyformDataOptions;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext;
import com.wellsoft.pt.dyform.implement.repository.adapter.FormDataServiceAdapter;
import com.wellsoft.pt.dyform.implement.repository.enums.FormRepositoryModeEnum;
import com.wellsoft.pt.log.LogEvent;
import com.wellsoft.pt.log.entity.BusinessDetailsLog;
import com.wellsoft.pt.log.entity.BusinessOperationLog;
import com.wellsoft.pt.log.support.ContextLogs;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgApiFacade;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.audit.dto.AuditDataLogDto;
import com.wellsoft.pt.security.audit.facade.service.AuditDataFacadeService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonMappingException;
import org.hibernate.criterion.Criterion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.IntrospectionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.ParseException;
import java.util.*;

@Service
public class DyFormFacadeImpl implements DyFormFacade {

    private static Logger logger = LoggerFactory.getLogger(DyFormFacadeImpl.class);

    @Autowired
    public FormDataServiceAdapter formDataService;

    @Autowired
    FormDefinitionService formDefinitionService;

    @Autowired
    MongoFileService mongoFileService;

    @Autowired
    OrgApiFacade orgApiFacade;

    @Autowired
    RoleFacadeService roleFacadeService;
    @Autowired
    DataDictionaryService dataDictionaryService;
    @Autowired
    private MultiOrgApiFacade multiOrgApiFacade;
    @Autowired
    private DataSourceDefinitionService dataSourceDefinitionService;

    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    /**
     * 替换掉表单定义的图片HTML占位符
     *
     * @param doGetFormDefinitionHandler
     */
    private static void replaceImagePlaceHolder(FormDefinitionHandler doGetFormDefinitionHandler) {
        try {
            JSONObject jsonObject = doGetFormDefinitionHandler.getFormDefinition();
            if (jsonObject == null || !jsonObject.has("html")) {
                return;
            }
            String html = jsonObject.getString("html");
            html = StringUtils.replace(html, "src=\"/resources/pt/js/dyform", "imgholder=\"/resources/pt/js/dyform");
            html = StringUtils.replace(html, "src=\"/static/dyform", "imgholder=\"/static/dyform");
            jsonObject.put("html", html);
        } catch (Exception e) {
        }
    }

    @Override
    public List<DyformField> getFormFieldDefintion(String formUuid) {
        DyFormFormDefinition dyFormDefinition = this.getFormDefinition(formUuid);
        List<DyformFieldDefinition> fieldDefinitionList = dyFormDefinition.doGetFieldDefintions();
        List<DyformField> dyformFields = Lists.newArrayList();
        for (DyformFieldDefinition fieldDefinition : fieldDefinitionList) {
            dyformFields.add(new DyformField(fieldDefinition.getName(), fieldDefinition.getInputMode(), fieldDefinition
                    .getFieldName()));
        }
        return dyformFields;
    }

    @Transactional(readOnly = true)
    public List<String> queryFormDataList(String tblName, String fieldName) {
        return formDataService.queryFormDataList(tblName, fieldName);
    }

    /**
     * 检查数据是否已存在<br/>
     * <p>
     * 检查指定的字段的值是否存在于指定的表中
     *
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public boolean queryFormDataExists(String tblName, String fieldName, String fieldValue) throws Exception {
        return formDataService.queryFormDataExists(tblName, fieldName, fieldValue);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean queryFormDataExists(String tblName, String[] fieldName, String[] fieldValue) throws Exception {
        return formDataService.queryFormDataExists(tblName, fieldName, fieldValue);
    }

    /**
     * 检查数据是否已存在<br/>
     * 检查指定的字段的值除了指定的uuid的记录外，还有没有存在于其他记录中<br/>
     *
     * @param tblName
     * @param fieldName
     * @param fieldValue
     * @param uuid
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public boolean queryFormDataExists(String tblName, String fieldName, String fieldValue, String uuid)
            throws Exception {
        return formDataService.queryFormDataExists(uuid, tblName, fieldName, fieldValue);
    }

    @Transactional(readOnly = true)
    public boolean queryFormDataExists(String tblName, String[] fieldName, String[] fieldValue, String uuid)
            throws Exception {
        return formDataService.queryFormDataExists(uuid, tblName, fieldName, fieldValue);
    }

    /**
     * 保存表单数据
     *
     * @param mainformUuid     主表的表定义uuid
     * @param formDatas        表单数据列表
     * @param deletedFormDatas 被删除 的表单数据
     * @param signature
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws ParseException
     */
    @Transactional
    public String saveFormData(String mainformUuid/* 主表表单定义uuid */,
                               Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> formDatas,
                               Map<String/* 表单定义id */, List<String>/* 表单数据id */> deletedFormDatas, DyformDataSignature signature) {
        try {
            return formDataService.saveFormData(mainformUuid, formDatas, deletedFormDatas, signature);
        } catch (JsonDataException e) {
            throw (JsonDataException) e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SaveDataException(e.getMessage(), e);
        }
    }

    @Transactional
    public Boolean updateFieldValue(String formUuid, Map<String, Object> param) {
        // 处理下字段
        Boolean result = formDataService.updateFieldValue(formUuid, param);
        return result;
    }

    /**
     * 保存表单数据
     *
     * @param formData DyFormData对象
     * @return dataUuid
     */
    @Transactional
    public String saveFormData(DyFormData formData) {
        if (StringUtils.isNotBlank(formData.getBeforeSaveFormDataServiceFilter())) {
            DyformDataServiceFilter dyformDataServiceFilter = ApplicationContextHolder.getBean(formData.getBeforeSaveFormDataServiceFilter()
                    , DyformDataServiceFilter.class);
            dyformDataServiceFilter.filter(formData);
        }

        // formData.initFormDefintion();
        /*
         * String dataUuid = this.saveFormData(formData.getFormUuid(),
         * formData.getFormDatas(), formData.getDeletedFormDatas(),
         * formData.getSignature());
         */
        long time1 = System.currentTimeMillis();
        // 记录表单日志到线程上线文
        DyformDataOptions dyformDataOptions = formData.doGetDyformDataOptions();
        Object logs = dyformDataOptions.remove("logs");
        if (null != logs && logs instanceof JSONArray) {
            JSONArray arrLogs = (JSONArray) logs;
            int logsLength = arrLogs.length();
            for (int i = 0; i < logsLength; i++) {
                BusinessDetailsLog detailsLogDto = ContextLogs.addDetailsLog(arrLogs.optString(i));
                if (null != detailsLogDto && null == detailsLogDto.getDataId()) {
                    detailsLogDto.setDataId(formData.getDataUuid());
                }
            }
        }
        String dataUuid = this.formDataService.saveFormData(formData.getFormUuid(), formData.getFormDatas(),
                formData.getDeletedFormDatas(), formData.getUpdatedFormDatas(), formData.getAddedFormDatas(),
                formData.getSignature(), dyformDataOptions);
        long time2 = System.currentTimeMillis();

        logger.info("save dyformData spent:" + (time2 - time1) + " ms");

//        saveOrgFormRoleValue(formData);
        // 不存在主体日志，表单作为主体
        if (null == ContextLogs.getLogEvent()) {
            FormDefinition formDefinition = getFormDefinition(formData.getFormUuid());
            // 用户操作日志
            BusinessOperationLog log = new BusinessOperationLog();
            log.setModuleId(formDefinition.getModuleId());// ModuleID.WORKFLOW.getValue()
            // log.setModuleName(formDefinition.getModuleId());// "工作流程"
            log.setDataDefType(ModuleID.DYFORM.getValue());
            log.setDataDefId(formDefinition.getId());
            log.setDataDefName(formDefinition.getName());
            log.setOperation("保存");
            log.setUserId(SpringSecurityUtils.getCurrentUserId());
            log.setUserName(SpringSecurityUtils.getCurrentUserName());
            log.setDataId(formData.getDataUuid());
            // log.setDataName(DyformTitleUtils.generateDyformTitle(formDefinition, formData));
            // userOperationLogService.save(log);
            Map<String, Object> details = Maps.newHashMap();
            //details.put("dyform", dyFormData.cloneDyFormDatasToJson());
            //details.put("flowInstUuid", flowInstUuid);
            details.put("dataUuid", formData.getDataUuid());
            ContextLogs.sendLogEvent(new LogEvent(log, details));
            ApplicationContextHolder.getApplicationContext().publishEvent(new FormDataSavedEvent(formData.getFormUuid(), dataUuid, formData, RequestSystemContextPathResolver.system()));
        }

        return dataUuid;
    }

    @Override
    public String saveDataModelFormDataAsNewVersion(DyFormData formData, String dataUuid) {
        return this.formDataService.saveFormDataAsNewVersion(formData, dataUuid);
    }

    private void delOnlyFormData(String formUuid, String dataUuid) {
        this.formDataService.delOnlyFormData(formUuid, dataUuid);
        ApplicationContextHolder.getApplicationContext().publishEvent(new FormDataDeleteEvent(formUuid, dataUuid));
    }

    private void copySubformDataAsNewVersion(String parentFormUuid, String parentDataUuid, String newParentDataUuid, Map<String, String> newOldUuidMap) {
        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> formDataMap = getFormData(parentFormUuid, parentDataUuid);
        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> newFormDataMap = getFormData(parentFormUuid, newParentDataUuid);
        Set<String> formUuids = formDataMap.keySet();
        for (String fuid : formUuids) {

            if (!fuid.equals(parentFormUuid)) {
                String tableName = this.getTblNameByFormUuid(fuid);
                List<Map<String, Object>> dataList = formDataMap.get(fuid);

                if (CollectionUtils.isNotEmpty(dataList)) {
                    for (Map<String, Object> dataMap : dataList) {
                        String uid = dataMap.get("uuid").toString();
                        String newUid = newOldUuidMap.containsKey(uid) ? newOldUuidMap.get(uid) : SnowFlake.getId() + "";

                        formDataService.copyAsVersionData(tableName, uid, newUid);
                        delOnlyFormData(fuid, uid);
                        if (!newOldUuidMap.containsKey(uid)) {
                            // 旧数据保存为新的
                            formDataService.saveFormData(uid, null, null, null, null, null);
                        }
                        copySubformDataAsNewVersion(fuid, uid, newUid, newOldUuidMap);
                        newOldUuidMap.put(uid, newUid);

                    }
                }
            }
        }
    }


    /**
     * 获取表单签名(函数未完整实现)
     *
     * @param formUuid
     * @param dataUUid
     * @return
     */
    @Deprecated
    public DyformDataSignature getDyformDataSignature(String formUuid, String dataUuid) {
        return null;
    }

    /**
     * 全量覆盖表单数据
     *
     * @param dyformData 表单数据实体
     * @return 主表单数据UUID
     */
    @Transactional
    public String rewriteFormData(DyFormData dyformData) {
        return this.formDataService.rewriteFormData(dyformData.getFormUuid(), dyformData.getFormDatas(),
                dyformData.getSignature());

    }

    /**
     * 删除整个单据包括从表数据
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     */
    @Transactional
    public void delFullFormData(String formUuid, String dataUuid) {
        this.formDataService.delFullFormData(formUuid, dataUuid);
        ApplicationContextHolder.getApplicationContext().publishEvent(new FormDataDeleteEvent(formUuid, dataUuid));
    }

    /**
     * 删除单条单据
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     */
    @Transactional
    public void delFormData(String formUuid, String dataUuid) {
        this.formDataService.delFormData(formUuid, dataUuid);
        ApplicationContextHolder.getApplicationContext().publishEvent(new FormDataDeleteEvent(formUuid, dataUuid));
    }

    /**
     * 全量删除从表数据
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     */
    @Transactional
    public void delFullSubFormData(String formUuid, String dataUuid) {
        this.formDataService.delFullSubFormData(formUuid, dataUuid);
    }

    /**
     * 全量删除指定从表的数据
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     */
    @Transactional
    public void delFullSubFormData(String formUuid, String dataUuid, String formUuidOfSubform) {
        this.formDataService.delFullSubFormData(formUuid, dataUuid, formUuidOfSubform);
    }

    /**
     * 获取主表数据
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     * @return 表单数据(主表)
     */
    @Transactional(readOnly = true)
    public Map<String /* 表单字段名 */, Object/* 表单字段值 */> getFormDataOfMainform(String formUuid, String dataUuid) {
        return formDataService.getFormDataOfMainform(formUuid, dataUuid);
    }

    /**
     * 获取表单数据(包括主表、从表)
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     * @return 表单数据(包括主表 、 从表)
     * @throws JSONException
     */
    @Transactional(readOnly = true)
    public Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> getFormData(String formUuid,
                                                                                                   String dataUuid) {

        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> formDatas = new HashMap<String, List<Map<String, Object>>>();

        Map<String, Object> formDataOfMainform = this.getFormDataOfMainform(formUuid, dataUuid);
        if (formDataOfMainform == null) {
            return null;
        }
        List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>> list = new ArrayList<Map<String, Object>>();
        list.add(formDataOfMainform);
        formDatas.put(formUuid, list);

        querySubformDatas(formDatas, formUuid, dataUuid, false);
        return formDatas;

    }

    private void querySubformDatas(Map<String, List<Map<String, Object>>> formDatas, String formUuid, String dataUuid, boolean fetchAllSubformFields) {
        FormDefinitionHandler dUtils;
        dUtils = this.getFormDefinition(formUuid).doGetFormDefinitionHandler();
        List<String> subformUuids = dUtils.getFormUuidsOfSubform();
        // 从表个数大于5的表单数据存储进行优化处理
        if (CollectionUtils.size(subformUuids) > 5 && isDyformRepository(dUtils)) {
            Map<String, List<Map<String, Object>>> listMap = this.getSubformDatas(subformUuids, formUuid, dataUuid, fetchAllSubformFields);
            formDatas.putAll(listMap);
        } else {
            for (String formUuidOfSubform : subformUuids) {
                QueryData qd = this.getFormDataOfParentNode(formUuidOfSubform, formUuid, dataUuid, fetchAllSubformFields);
                if (qd == null || qd.getDataList() == null) {
                    continue;
                }
                List formDataOfSubform = qd.getDataList();
                if (formDataOfSubform == null) {
                    continue;
                }
                formDatas.put(formUuidOfSubform, formDataOfSubform);
            }
        }
    }

    @Override
    public Map<String, List<Map<String, Object>>> getFullFormData(String formUuid, String dataUuid) {
        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> formDatas = new HashMap<String, List<Map<String, Object>>>();

        Map<String, Object> formDataOfMainform = this.getFormDataOfMainform(formUuid, dataUuid);
        if (formDataOfMainform == null) {
            return null;
        }

        List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>> list = new ArrayList<Map<String, Object>>();
        list.add(formDataOfMainform);
        formDatas.put(formUuid, list);

        querySubformDatas(formDatas, formUuid, dataUuid, true);
        return formDatas;
    }


    /**
     * @param dUtils
     * @return
     */
    private boolean isDyformRepository(FormDefinitionHandler dUtils) {
        FormRepositoryContext formRepositoryContext = new FormRepositoryContext(dUtils);
        return FormRepositoryModeEnum.Dyform.getValue().equals(formRepositoryContext.getRepositoryMode());
    }

    /**
     * 获取表单数据(包括主表、从表)
     *
     * @param formUuid   定义UUID
     * @param dataUuid   数据UUID
     * @param pagingInfo 分页信息
     * @return 表单数据(包括主表 、 从表)
     * @throws JSONException
     */
    @Transactional(readOnly = true)
    public Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> getFormDataByPage(
            String formUuid, String dataUuid, PagingInfo pagingInfo) {

        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> formDatas = new HashMap<String, List<Map<String, Object>>>();

        Map<String, Object> formDataOfMainform = this.getFormDataOfMainform(formUuid, dataUuid);
        if (formDataOfMainform == null) {
            return null;
        }

        List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>> list = new ArrayList<Map<String, Object>>();
        list.add(formDataOfMainform);
        formDatas.put(formUuid, list);
        FormDefinitionHandler dUtils;

        dUtils = this.getFormDefinition(formUuid).doGetFormDefinitionHandler();

        for (String formUuidOfSubform : dUtils.getFormUuidsOfSubform()) {
            QueryData qd = this.getFormDataOfParentNodeByPage(formUuidOfSubform, formUuid, dataUuid, pagingInfo);
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

    /**
     * 获取动态表单数据
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     * @return 表单数据
     */
    @Transactional(readOnly = true)
    public DyFormData getDyFormData(String formUuid, String dataUuid) {
        return this.getDyFormData(formUuid, dataUuid, true);
    }

    @Override
    public Map<String/* 表单定义uuid */, List<Map<String /* 表单字段值 */, Object/* 表单字段值 */>>> getAllVersionFormData(String formUuid, String dataUuid) {
        return formDataService.getAllVersionFormData(formUuid, dataUuid);
    }

    @Override
    public Map<String, List<Map<String, Object>>> getVersionFormData(String formUuid, String dataUuid) {
        return formDataService.getVersionFormData(formUuid, dataUuid);
    }

    /**
     * 获取动态表单数据附带签名
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     * @return 表单数据
     */
    @Transactional(readOnly = true)
    public DyFormData getDyFormDataWithSignature(String formUuid, String dataUuid) {
        DyFormData dyformData = this.getDyFormData(formUuid, dataUuid, true);
        DyformDataSignature signature = this.getDyformDataSignature(formUuid, dataUuid);
        dyformData.setSignature(signature);
        return dyformData;
    }

    /**
     * 加载表单定义和数据,dataUuid为空或者null只加载定义
     *
     * @param formUuid       定义UUID
     * @param dataUuid       数据UUID
     * @param justDataAndDef true表示加载定义时是去加载各字段中对应的选项,false则反之
     * @return DyFormData
     */
    @Override
    @Transactional(readOnly = true)
    public DyFormData getDyFormData(String formUuid, String dataUuid, boolean justDataAndDef) {
//        logger.info("开始加载表单数据与表单定义，表单UUID={}，数据UUID={}", new Object[]{formUuid, dataUuid});
//        StopWatch stopWatch = new StopWatch("getDyFormData");
//        stopWatch.start("加载表单数据");
        Map<String, List<Map<String, Object>>> formData = new HashMap<String, List<Map<String, Object>>>();
        if (StringUtils.isNotBlank(dataUuid)) {
            formData = this.getFormData(formUuid, dataUuid);
        }
//        stopWatch.stop();
//        logger.info("表单UUID={}，数据UUID={}，加载数据耗时={}秒", new Object[]{formUuid, dataUuid,
//                stopWatch.getLastTaskInfo().getTimeSeconds()});
//
//        stopWatch.start("加载表单定义");
        FormDefinition dyformDefinition = (FormDefinition) getFormDefinition(formUuid);
        DyFormData dyFormData = new DyFormDataImpl();
        dyFormData.setLoadDefaultFormData(true);// 加载默认值
        if (justDataAndDef) {
            dyFormData.setLoadDictionary(true);// 加载数字字段
            dyFormData.setLoadSubformDefinition(true);// 加载从表定义
            // 替换掉表单定义的图片HTML占位符
            replaceImagePlaceHolder(dyformDefinition.doGetFormDefinitionHandler());
        }
        // dyFormData.setLoadDefaultFormData(true);//加载默认值
        // dyFormData.setLoadDictionary(true);//加载数字字段
        // dyFormData.setLoadSubformDefinition(true);//加载从表定义
        dyFormData.setFormDefinition(dyformDefinition);// dyFormData.setFormUuid(formUuid);
        dyFormData.setFormDatas(formData, false);
        //        stopWatch.stop();
//        logger.info("表单UUID={}，数据UUID={}，加载表单定义耗时={}秒", new Object[]{formUuid, dataUuid,
//                stopWatch.getLastTaskInfo().getTimeSeconds()});
//        logger.info("加载表单数据与表单定义结束，执行情况：{}", stopWatch.prettyPrint());

        setOrgFormRoleValue(dyFormData, dyformDefinition);
        dyFormData.setI18ns(appDefElementI18nService.getI18ns(
                dyformDefinition.getId(), null, new BigDecimal(dyformDefinition.getVersion()), IexportType.DyFormDefinition, LocaleContextHolder.getLocale().toString()));
        return dyFormData;
    }

    /**
     * 创建uuid
     *
     * @return uuid
     */
    public String createUuid() {
        return UuidUtils.createUuid();
    }

    /**
     * 获取表单定义
     *
     * @param formUuid 表单定义uuid
     * @return 表单定义
     */

    @Override
    @Transactional(readOnly = true)
    public FormDefinition getFormDefinition(String formUuid) {
        FormDefinition dydf = // this.formDefinitionService.findDyFormDefinitionByFormUuid(formUuid);
                (FormDefinition) DyformCacheUtils.getDyformDefinitionByUuid(formUuid);
        if (dydf == null) {
            logger.error("cann't find dyformdefinition uuid is " + formUuid);
            // throw new
            // RuntimeException("cann't find dyformdefinition uuid is " +
            // formUuid);
            return null;
        }
        FormDefinition df = null;
        try {
            BeanCopyUtils utils = new BeanCopyUtils();
            df = new FormDefinition();
            utils.copyProperties(df, dydf);
            FormDefinitionHandler handler = df.doGetFormDefinitionHandler();
            handler.createOldName4AllFields();
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            return null;
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return df;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DyFormFormDefinition> getFormDefinitionByFormUuids(Set<String> formUuids) {
        if (CollectionUtils.isEmpty(formUuids)) {
            return null;
        }
        List<DyFormFormDefinition> dyFormFormDefinitions = Lists.newArrayList();
        for (String formUuid : formUuids) {
            DyFormFormDefinition dyFormFormDefinition = getFormDefinition(formUuid);
            if (dyFormFormDefinition != null) {
                dyFormFormDefinitions.add(dyFormFormDefinition);
            }
        }
        return dyFormFormDefinitions;
    }

    /**
     * 根据表单定义ID获取表单定义
     *
     * @param formDefId 表单定义ID
     * @return 表单定义
     */
    @Override
    @Transactional(readOnly = true)
    public DyFormFormDefinition getFormDefinitionById(String outerId) {
        return DyformCacheUtils.getDyformDefinitionOfMaxVersionById(outerId);
    }

    /**
     * 获取表单的定义
     *
     * @param isMaxVersion <br>
     *                     true 获取所有最新版本的表单定义基本信息<br>
     *                     false 获取所有的表单定义基本信息
     * @return 表单的定义集合
     */
    @Override
    @Transactional(readOnly = true)
    public List<DyFormFormDefinition> getAllFormDefinitions(boolean isMaxVersion) {
        List<DyFormFormDefinition> lists = new ArrayList<DyFormFormDefinition>();
        if (isMaxVersion) {
            lists.addAll(this.formDefinitionService.getMaxVersionList());
        } else {
            lists.addAll(this.formDefinitionService.getAllFormDefintions());
        }
        return lists;

    }

    /**
     * 获取表单定义基本信息
     *
     * @return
     */
    @Override
    public List<DyFormFormDefinition> listDyFormDefinitionBasicInfo() {
        return formDefinitionService.listDyFormDefinitionBasicInfo();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.service.DyFormFacade#getVformDefinitionsByPformId(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<FormDefinition> getVformDefinitionsByPformId(String pFormUuid) {
        return formDefinitionService.getVformDefinitionsByPformId(pFormUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.service.DyFormFacade#getMformDefinitionsByPformId(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<FormDefinition> getMformDefinitionsByPformId(String pFormUuid) {
        return formDefinitionService.getMformDefinitionsByPformId(pFormUuid);
    }

    /**
     * 根据指定的表单表名和版本号查找对应的字段定义信息列表
     *
     * @param tblName 表单表名
     * @param version 版本号
     * @return 对应的字段定义信息列表
     */
    @Transactional(readOnly = true)
    public List<DyformFieldDefinition> getFieldDefinitions(String tblName, String version) {
        return this.getFormDefinition(tblName, version).doGetFieldDefintions();
    }

    /**
     * 根据指定的表单表名和版本号查找表单定义
     *
     * @param tblName 表单表名
     * @param version 版本号
     * @return 表单定义
     */
    @Transactional(readOnly = true)
    public DyFormFormDefinition getFormDefinition(String tblName, String version) {
        return this.formDefinitionService.getFormDefinition(tblName, version);
    }

    /**
     * 拷贝,并将拷贝完的副本保存到目的表单中
     *
     * @param srcFormUuid  定义UUID
     * @param srcDataUuid  数据UUID
     * @param destFormUuid 目标定义UUID
     * @return 拷贝失败时返回 null,成功时返回数据uuid
     */
    @Transactional
    public String copyAndSaveFormDataOfMainform(String srcFormUuid, String srcDataUuid, String destFormUuid) {
        Map<String, Object> destData = new HashMap<String, Object>();
        Map<String, Object> srcData = this.getFormDataOfMainform(srcFormUuid, srcDataUuid);
        FormDefinitionHandler destDJson = this.getFormDefinition(destFormUuid).doGetFormDefinitionHandler();
        List<String> destFieldNames = destDJson.getFieldNamesOfMainform();
        for (String destFieldName : destFieldNames) {
            destData.put(destFieldName, srcData.get(destFieldName));
        }
        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> formDatas = new HashMap<String, List<Map<String, Object>>>();
        List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>> formDataList = new ArrayList<Map<String, Object>>();
        String destDataUuid = UuidUtils.createUuid();
        destData.put(EnumSystemField.uuid.name(), destDataUuid);
        formDataList.add(destData);
        formDatas.put(destFormUuid, formDataList);

        DyFormData destdyformData = createDyformData(destFormUuid);
        destdyformData.setFormDatas(formDatas, true);
        try {
            return this.saveFormData(destdyformData);
        } catch (SaveDataException e) {
            throw e;
        }

    }

    /**
     * 动态表单主表数据复制
     *
     * @param sourceFormUuid 源表单定义UUID
     * @param sourceDataUuid 源表单数据UUID
     * @param targetFormUuid 目标表单定义UUID
     * @return 返回复制后的表单数据
     */
    @Transactional
    public String copyFormData(String sourceFormUuid, String sourceDataUuid, String targetFormUuid) {
        return this.copyAndSaveFormDataOfMainform(sourceFormUuid, sourceDataUuid, targetFormUuid);
    }

    /**
     * 动态表单主表数据复制
     *
     * @param dyFormData 源表单dyFormData
     * @return 返回复制后的表单数据
     */
    @Transactional
    public DyFormData copyFormData(DyFormData dyFormData) {
        return dyFormData.doCopy(true);
    }

    /**
     * 动态表单从表数据复制
     *
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @param destFormUuid
     * @param destDataUuid
     * @param formUuidOfSubform
     * @param whereSql
     * @param values
     * @return
     */
    @Transactional
    public List<String> copySubFormData(String formUuidOfMainform, String dataUuidOfMainform, String destFormUuid,
                                        String destDataUuid, String formUuidOfSubform, String whereSql, Map<String, Object> values) {
        long time1 = System.currentTimeMillis();
        List<String> newSubformDataUuids = new ArrayList<String>();

        List<Map<String, Object>> list = this.formDataService.getFormDataOfSubform(formUuidOfSubform,
                dataUuidOfMainform, whereSql, values);
        if (list == null || list.size() == 0) {
            return newSubformDataUuids;
        }
        Map<String/* 表单定义id */, List<String>/* 表单数据id */> addedFormDatas = new HashMap<String, List<String>>();

        FormDefinitionHandler jsonHandlerOfSubform = this.getFormDefinition(formUuidOfSubform)
                .doGetFormDefinitionHandler();

        for (int i = 0; i < list.size(); i++) {
            String uuid = createUuid();
            Map<String, Object> formData = list.get(i);
            String srcSubFormDataUuid = (String) formData.get("uuid");
            formData.put(EnumRelationTblSystemField.mainform_data_uuid.name(), destDataUuid);
            formData.put(EnumRelationTblSystemField.mainform_form_uuid.name(), destFormUuid);
            // list.get(i).put(EnumRelationTblSystemField.sort_order.name(),
            // (String) list.get(i).get("sort_order"));

            formData.put(EnumSystemField.uuid.name(), uuid);

            // 复制文件
            Set<String> fileFieldNames = new HashSet<String>();
            for (String fieldName : formData.keySet()) {
                if (jsonHandlerOfSubform.isInputModeEqAttach(fieldName)) {
                    fileFieldNames.add(fieldName);
                }
            }

            if (fileFieldNames.size() > 0) {
                List<LogicFileInfo> files = this.mongoFileService.getNonioFilesFromFolder(srcSubFormDataUuid, null);
                if (files.size() > 0) {
                    for (String fileFieldName : fileFieldNames) {
                        List<LogicFileInfo> fieldFiles = new ArrayList<LogicFileInfo>();
                        for (LogicFileInfo lf : files) {
                            if (fileFieldName.equalsIgnoreCase(lf.getPurpose())) {
                                fieldFiles.add(lf);
                            }
                        }

                        formData.put(fileFieldName, fieldFiles);

                    }
                }
            }

            newSubformDataUuids.add(uuid);

        }
        Map<String, List<Map<String, Object>>> formDatas = new HashMap<String, List<Map<String, Object>>>();

        List<Map<String, Object>> mainFormDataList = new ArrayList<Map<String, Object>>();
        Map<String, Object> mainFormData = new HashMap<String, Object>();
        mainFormData.put(EnumSystemField.uuid.name(), destDataUuid);
        mainFormDataList.add(mainFormData);
        formDatas.put(destFormUuid, mainFormDataList);
        formDatas.put(formUuidOfSubform, list);

        addedFormDatas.put(formUuidOfSubform, newSubformDataUuids);

        this.formDataService.saveFormData(destFormUuid, formDatas, null, null, addedFormDatas, null);
        // this.saveFormData(destDyformData);

        // destDyformData.getAddedFormDatas().clear();
        // destDyformData.getUpdatedFormDatas().clear();
        // destDyformData.getDeletedFormDatas().clear();
        long time2 = System.currentTimeMillis();
        logger.info("copySubFormData and Data spent " + (time2 - time1) / 1000.0 + "s");
        return newSubformDataUuids;
    }

    /**
     * 动态表单从表数据复制
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @param formUuidOfSubform
     * @param whereSql
     * @param values
     * @param destDyformData
     * @return
     */
    /*
     * public List<String> copySubFormData(String formUuidOfMainform, String
     * dataUuidOfMainform, String formUuidOfSubform, String whereSql,
     * Map<String, Object> values, DyFormData destDyformData) {
     *
     * List<String> destUuids = new ArrayList<String>(); try { List<Map<String,
     * Object>> list =
     * this.formDataService.getFormDataOfSubform(formUuidOfSubform,
     * dataUuidOfMainform, whereSql, values);
     *
     * FormDefinitionHandler jsonHandlerOfSubform =
     * this.getFormDefinition(formUuidOfSubform).doGetFormDefinitionHandler();
     * FormDefinitionHandler jsonHandlerOfSrcMainform =
     * this.getFormDefinition(formUuidOfMainform) .doGetFormDefinitionHandler();
     *
     * for (int i = 0; i < list.size(); i++) { String uuid = createUuid();
     * DyFormData subdyformdata =
     * destDyformData.getDyFormData(formUuidOfSubform, uuid); Map<String,
     * Object> formdata = list.get(i); String srcSubFormDataUuid = (String)
     * formdata.get("uuid"); formdata.remove(EnumSystemField.uuid.name()); for
     * (String field : formdata.keySet()) { if
     * (field.equalsIgnoreCase("sort_order")) { System.out.println(); } if
     * (jsonHandlerOfSubform.isInputModeEqAttach(field)) { continue; } if
     * (jsonHandlerOfSubform.isFieldInDefinition(field) ||
     * FormDefinitionHandler.isSysTypeAsSystem(field) ||
     * FormDefinitionHandler.isRelationTblField(field)) {
     * subdyformdata.setFieldValue(field, formdata.get(field)); } }
     *
     * //复制文件 Set<String> fileFieldNames = new HashSet<String>(); for (String
     * fieldName :
     * jsonHandlerOfSrcMainform.getFieldNamesOfSubform(formUuidOfSubform)) { if
     * (jsonHandlerOfSubform.isInputModeEqAttach(fieldName)) {
     * fileFieldNames.add(fieldName); } }
     *
     * if (fileFieldNames.size() > 0) { List<LogicFileInfo> files =
     * this.mongoFileService.getNonioFilesFromFolder(srcSubFormDataUuid, null);
     * for (String fileFieldName : fileFieldNames) { if (files.size() > 0) {
     * List<LogicFileInfo> fieldFiles = new ArrayList<LogicFileInfo>(); for
     * (LogicFileInfo lf : files) { if
     * (fileFieldName.equalsIgnoreCase(lf.getPurpose())) { fieldFiles.add(lf); }
     * } subdyformdata.setFieldValue(fileFieldName, fieldFiles);
     *
     * } } }
     *
     * destUuids.add(uuid);
     *
     * }
     *
     * this.saveFormData(destDyformData);
     *
     * destDyformData.getAddedFormDatas().clear();
     * destDyformData.getUpdatedFormDatas().clear();
     * destDyformData.getDeletedFormDatas().clear(); } catch (JSONException e) {
     * logger.error(e.getMessage(), e); }
     *
     * return destUuids; }
     */

    /**
     * 动态表单数据复制
     *
     * @param dyFormData     动态表单数据
     * @param targetFormUuid 目标表单定义UUID
     * @return 返回复制后的表单数据
     */
    @Transactional
    public String copyFormData(DyFormData dyFormData, String targetFormUuid) {
        long time1 = System.currentTimeMillis();
        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> srcFormDatas = dyFormData
                .getFormDatas();
        Map<String/* 表单定义uuid */, List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>> destFormDatas = new HashMap<String, List<Map<String, Object>>>();
        for (String srcFormUuid : srcFormDatas.keySet()) {
            List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>> srcDataList = srcFormDatas.get(srcFormUuid);
            List<Map<String /* 表单字段名 */, Object/* 表单字段值 */>> destDataList = new ArrayList<Map<String /* 表单字段名 */, Object/* 表单字段值 */>>();
            for (Map<String /* 表单字段名 */, Object/* 表单字段值 */> srcFormData : srcDataList) {
                Map<String /* 表单字段名 */, Object/* 表单字段值 */> destFormData = new HashMap<String /* 表单字段名 */, Object/* 表单字段值 */>();
                for (String srcFieldName : srcFormData.keySet()) {
                    if (StringUtils.equals(EnumSystemField.uuid.name(), srcFieldName)) {
                        destFormData.put(EnumSystemField.uuid.name(), createUuid());
                    } else {
                        destFormData.put(srcFieldName, srcFormData.get(srcFieldName));
                    }
                }
                destDataList.add(destFormData);
            }
            if (StringUtils.equals(dyFormData.getFormUuid(), srcFormUuid)) {
                destFormDatas.put(targetFormUuid, destDataList);
            } else {
                destFormDatas.put(srcFormUuid, destDataList);
            }
        }
        DyFormData destdyformData = createDyformData(targetFormUuid);
        destdyformData.setFormDatas(destFormDatas, true);
        long time2 = System.currentTimeMillis();
        logger.info("copyFormData and Data spent " + (time2 - time1) / 1000.0 + "s");
        try {
            return this.saveFormData(destdyformData);
        } catch (SaveDataException e) {
            throw e;
        }
    }

    /**
     * 将源表单的数据深拷贝成一个新表单,没进行持久化
     *
     * @param srcDyformData 源表单数据
     * @param physicalCopy  false：浅拷贝, true：深拷贝
     * @return 新表单数据
     */
    @Transactional
    public DyFormData createDyformDataFrom(DyFormData srcDyformData, boolean physicalCopy) {
        DyFormData destDyformData = this.createDyformData(srcDyformData.getFormUuid());
        this.copyFormData(srcDyformData, destDyformData, physicalCopy);
        return destDyformData;
    }

    /**
     * 深拷贝表单,没进行持久化
     *
     * @param srcDyformData  源表单
     * @param destDyformData 目标表单
     * @param physicalCopy   false：浅拷贝, true：深拷贝
     */
    @Transactional
    public void copyFormData(DyFormData srcDyformData, DyFormData destDyformData, boolean physicalCopy) {
        Map<String, List<Map<String, Object>>> oldFormDatas = srcDyformData.getFormDatas();
        if (oldFormDatas.size() == 0) {// 源表单没数据
            return;
        }

        Iterator<String> it1 = oldFormDatas.keySet().iterator();
        while (it1.hasNext()) {
            String formUuid = it1.next();

            boolean isMainform = srcDyformData.isMainform(formUuid);
            if (isMainform) {// 主表中的字段
                String dataUuid = srcDyformData.getDataUuid();
                for (String fieldName : srcDyformData.doGetFieldNames()) {

                    if (fieldName.equalsIgnoreCase(EnumSystemField.uuid.name())) {
                        continue;
                    }
                    Object value = srcDyformData.getFieldValue(fieldName);
                    if (srcDyformData.isFileField(fieldName) && physicalCopy) {// 附件字段,且需要深度拷贝
                        List<String> fileIds = FormDataHandler.getFileIds(value);
                        List<LogicFileInfo> files = new ArrayList<LogicFileInfo>();
                        for (String fileId : fileIds) {
                            if (!StringUtils.isNotBlank(fileId)) {
                                continue;
                            }

                            MongoFileEntity fileEntityCopy = null;
                            try {
                                fileEntityCopy = mongoFileService.copyFile(fileId);
                            } catch (FileNotFoundException e) {
                                logger.error("formUuid:[" + formUuid + "]dataUuid:[" + dataUuid
                                        + "] file copy error: fileid[" + fileId + "]", e);
                                continue;
                            } catch (IntrospectionException e) {
                                logger.error("formUuid:[" + formUuid + "]dataUuid:[" + dataUuid
                                        + "] file copy error: fileid[" + fileId + "]", e);
                                continue;
                            } catch (IllegalAccessException e) {
                                logger.error("formUuid:[" + formUuid + "]dataUuid:[" + dataUuid
                                        + "] file copy error: fileid[" + fileId + "]", e);
                                continue;
                            } catch (InvocationTargetException e) {
                                logger.error("formUuid:[" + formUuid + "]dataUuid:[" + dataUuid
                                        + "] file copy error: fileid[" + fileId + "]", e);
                                continue;
                            }

                            files.add(fileEntityCopy.getLogicFileInfo());
                        }

                        value = files;
                    }
                    destDyformData.setFieldValue(fieldName, value);
                }
            } else {// 从表中的字段
                List<Map<String, Object>> formDatasOfOneForm = oldFormDatas.get(formUuid);
                if (formDatasOfOneForm == null || formDatasOfOneForm.size() == 0) {
                    continue;
                }
                for (Map<String, Object> oneFormData : formDatasOfOneForm) {
                    String dataUuid = (String) oneFormData.get(EnumSystemField.uuid.name());
                    DyFormData subsrcDyformData = srcDyformData.getDyFormData(formUuid, dataUuid);
                    destDyformData.addSubformData(this.createDyformDataFrom(subsrcDyformData, physicalCopy));

                }
            }

        }
    }

    /**
     * 获取默认值
     *
     * @param formUuid 定义UUID
     * @return 表单默认值MAP集合
     * @throws JSONException
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDefaultFormData(String formUuid) throws JSONException {
        return formDataService.getDefaultFormData(formUuid);
    }

    /**
     * 分页查询从表父节点记录
     *
     * @param formUuid           从表的表单uuid
     * @param dataUuidOfMainform 主表数据UUID
     * @param pagingInfo         分页信息
     * @return 查询数据
     */
    @Transactional(readOnly = true)
    public QueryData getFormDataOfParentNodeByPage(String formUuidOfSubform, String formUuidOfMainform,
                                                   String dataUuidOfMainform, PagingInfo pagingInfo) {
        return this.formDataService.getFormDataOfParentNodeByPage(formUuidOfSubform, formUuidOfMainform,
                dataUuidOfMainform, pagingInfo);
    }

    /**
     * 获取从表一级节点的数据（从表数据可以是树形结构）
     *
     * @param formUuidOfSubform
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @return
     */
    @Transactional(readOnly = true)
    public QueryData getFormDataOfParentNode(String formUuidOfSubform, String formUuidOfMainform,
                                             String dataUuidOfMainform, boolean fetchAllSubformField) {
        return this.formDataService.getFormDataOfParentNode(formUuidOfSubform, formUuidOfMainform, dataUuidOfMainform, fetchAllSubformField);
    }

    /**
     * 批量获取从表数据
     *
     * @param formUuid
     * @param dataUuid
     * @param subformUuids
     * @param fetchAllSubformFields
     * @return
     */
    private Map<String, List<Map<String, Object>>> getSubformDatas(List<String> subformUuids, String formUuidOfMainform, String dataUuidOfMainform, boolean fetchAllSubformFields) {
        if (StringUtils.isEmpty(dataUuidOfMainform)) {
            return Collections.emptyMap();
        }
        return this.formDataService.getSubformDatas(subformUuids, formUuidOfMainform, dataUuidOfMainform, fetchAllSubformFields);
    }

    /**
     * 获取从表一级节点的子节点
     *
     * @param formUuidOfSubform
     * @param formUuidOfMainform
     * @param dataUuidOfMainform
     * @param dataUuidOfParentNode
     * @return
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getFormDataOfChildNode4ParentNode(String formUuidOfSubform,
                                                                       String formUuidOfMainform, String dataUuidOfMainform, String dataUuidOfParentNode) {
        return this.formDataService.getFormDataOfChildNode4ParentNode(formUuidOfSubform, formUuidOfMainform,
                dataUuidOfMainform, dataUuidOfParentNode);
    }

    /**
     * 查询主表数据
     *
     * @param formUuid
     * @param conditions
     * @param pagingInfo 如果该值为null ，返回所有数据，当结果大于1000时将自动分页，pageSize=1000
     * @return
     */
    @Transactional(readOnly = true)
    public QueryData queryFormDataOfMainform(String formUuid, Criterion conditions, PagingInfo pagingInfo) {

        return this.formDataService.queryFormDataOfMainform(formUuid, conditions, pagingInfo);

    }

    /**
     * 根据formUuid获取表名
     *
     * @param formUuid
     * @return
     */
    @Transactional(readOnly = true)
    public String getTblNameByFormUuid(String formUuid) {
        return this.getFormDefinition(formUuid).doGetFormDefinitionHandler().getTblNameOfMainform();
    }

    /**
     * 获取formUUid获取关系表表名
     *
     * @param formUuid
     * @return
     */
    @Transactional(readOnly = true)
    public String getRlTblNameByFormUuid(String formUuid) {
        return this.getFormDefinition(formUuid).doGetRelationTblNameOfpForm();
    }

    /**
     * 动态表单数据查询
     *
     * @param formUuid      表单uuid
     * @param projection    查询的列名，为空查询所有列
     * @param selection     查询where条件语句
     * @param selectionArgs 查询where条件语句参数
     * @param groupBy       分组语句
     * @param having        分组条件语句
     * @param orderBy       排序
     * @param firstResult   首条记录索引号
     * @param maxResults    最大记录集
     * @return List<QueryItem> 查询结果列表
     */
    @Transactional(readOnly = true)
    public List<QueryItem> query(String formUuid, String[] projection, String selection, String[] selectionArgs,
                                 String groupBy, String having, String orderBy, int firstResult, int maxResults) {

        return this.formDataService.query(formUuid, projection, selection, selectionArgs, groupBy, having, orderBy,
                firstResult, maxResults);
    }

    /**
     * 动态表单数据查询
     *
     * @param formUuid      表单uuid
     * @param projection    查询的列名，为空查询所有列
     * @param selection     查询where条件语句
     * @param selectionArgs 查询where条件语句参数
     * @param groupBy       分组语句
     * @param having        分组条件语句
     * @param orderBy       排序
     * @param firstResult   首条记录索引号
     * @param maxResults    最大记录集
     * @return List<QueryItem> 查询结果列表
     */
    @Transactional(readOnly = true)
    public List<QueryItem> query(boolean distinct, String formUuid, String[] projection, String selection,
                                 Map<String, Object> selectionArgs, String groupBy, String having, String orderBy, int firstResult,
                                 int maxResults) {

        return this.formDataService.query(this.getTblNameByFormUuid(formUuid), distinct, projection, selection,
                selectionArgs, groupBy, having, orderBy, firstResult, maxResults);
    }

    /**
     * 动态表单数据查询
     *
     * @param tableName
     * @param distinct
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param firstResult
     * @param maxResults
     * @return
     */
    @Transactional(readOnly = true)
    public List<QueryItem> query(String tableName, boolean distinct, String[] projection, String selection,
                                 Map<String, Object> selectionArgs, String groupBy, String having, String orderBy, int firstResult,
                                 int maxResults) {
        return this.formDataService.query(tableName, distinct, projection, selection, selectionArgs, groupBy, having,
                orderBy, firstResult, maxResults);

    }

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param firstResult
     * @param maxResults
     * @return
     */
    @Transactional(readOnly = true)
    public List<QueryItem> query2(String formUuid, String[] projection, String selection, String[] selectionArgs,
                                  String groupBy, String having, String orderBy, int firstResult, int maxResults) {
        return this.formDataService.query2(formUuid, projection, selection, selectionArgs, groupBy, having, orderBy,
                firstResult, maxResults);
    }

    /**
     * 根据表名，获取对应的所有版本的定义
     *
     * @param tblName 表名
     * @return 对应的所有版本的定义
     */
    @Transactional(readOnly = true)
    public List<DyFormFormDefinition> getFormDefinitionsByTblName(String tblName) {
        List<DyFormFormDefinition> list = new ArrayList<DyFormFormDefinition>();
        list.addAll(this.formDefinitionService.getFormDefinitionsByTblName(tblName));
        return list;
    }

    /**
     * 将从表及从表对应的数据关系表组成一个视图subformdata的sql语句
     *
     * @param formUuidOfSubform 从表UUID
     * @return sql语句
     */
    @Transactional(readOnly = true)
    public String getSqlOfSubformView(String formUuidOfSubform) {
        FormDefinition dy = this.getFormDefinition(formUuidOfSubform);
        FormDefinitionHandler util = dy.doGetFormDefinitionHandler();
        StringBuilder sqlBuffer = new StringBuilder();
        sqlBuffer
                //.append("WITH " + DyFormConfig.VIEWNAME_OF_SUBFORM + " AS (")
                .append(" (select t2.*, t1.uuid uuid_sb, t1.creator creator_sb, t1.create_time create_time_sb, t1.modifier modifier_sb, t1.modify_time modify_time_sb, t1.rec_ver rec_ver_sb, ")
                .append("  t1.data_uuid, t1.mainform_data_uuid, t1.mainform_form_uuid, t1.sort_order, t1.parent_uuid from ")
                .append(util.doGetRelationTblNameOfPform()).append(" t1 left join ").append(util.doGetTblNameOfpForm())
                .append(" t2 on t1.data_uuid = t2.uuid where t2.uuid is not null ) subformdataview ");
        logger.info("-----getSqlOfSubformView");

        return sqlBuffer.toString();
    }

    public DyformDataSignature getDigestValue(String signedContent) {
        return this.formDataService.getDigestValue(signedContent);
    }

    /**
     * 根据定义uuid创建一个空表单
     *
     * @param formUuid 定义UUID
     * @return 空表单
     */
    @Transactional(readOnly = true)
    public DyFormData createDyformData(final String formUuid) {
        long time1 = System.currentTimeMillis();
        DyFormData dyFormdata = new DyFormDataImpl();
        dyFormdata.setLoadDefaultFormData(false);// 加载默认值
        dyFormdata.setLoadDictionary(false);// 加载数据字典
        dyFormdata.setLoadSubformDefinition(true);// 加载从表定义

        dyFormdata.setFormUuid(formUuid);
        long time2 = System.currentTimeMillis();
        logger.info("create dyformdata spent " + (time2 - time1) / 1000.0 + "s");
        return dyFormdata;
    }

    /**
     * 根据定义uuid创建一个表单带有一笔默认值的数据
     *
     * @param formUuid 定义UUID
     * @return 有一笔默认值的表单数据
     */
    @Transactional(readOnly = true)
    public DyFormData createDyformDataWithDefaultData(final String formUuid) {
        DyFormData dyFormdata = new DyFormDataImpl();
        try {
            Map<String, Object> formData = this.getDefaultFormData(formUuid);
            formData.put("uuid", this.createUuid());
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            list.add(formData);
            Map<String, List<Map<String, Object>>> formdatas = new HashMap<String, List<Map<String, Object>>>();
            formdatas.put(formUuid, list);
            dyFormdata.setFormUuid(formUuid);
            dyFormdata.setFormDatas(formdatas, true);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        return dyFormdata;
    }

    /**
     * 根据搜索条件,查询符合条件的表单数据条数
     *
     * @param tblName    表名
     * @param conditions 条件
     * @return 表单数据条数
     */
    @Transactional(readOnly = true)
    public long queryTotalCountOfFormDataOfMainform(String tblName, Criterion conditions) {
        return this.formDataService.queryTotalCountOfFormDataOfMainform(tblName, conditions);
    }

    /**
     * 根据搜索条件,查询符合条件的表单数据条数
     *
     * @param tblName    表名
     * @param conditions 条件
     * @return 表单数据条数
     */
    @Transactional(readOnly = true)
    public long queryTotalCountOfFormDataOfMainform(String tblName, String conditions) {
        return this.formDataService.queryTotalCountOfFormDataOfMainform(tblName, conditions);
    }

    /**
     * 获取所有的字段定义
     *
     * @param formUuid 定义UUID
     * @return 所有的字段定义
     */
    @Transactional(readOnly = true)
    public List<DyformFieldDefinition> getFieldDefinitions(String formUuid) {
        return this.getFormDefinition(formUuid).doGetFieldDefintions();
    }

    /**
     * 获取指定的表单下面的从表的定义
     *
     * @param formUuid 定义UUID
     * @return 从表的定义
     */
    @Transactional(readOnly = true)
    public List<DyformSubformFormDefinition> getSubformDefinitions(String formUuid) {
        DyFormFormDefinition df = this.getFormDefinition(formUuid);
        return df.doGetSubformDefinitions();
    }

    /**
     * 根据指定的表名获取其对应的最高版本的定义
     *
     * @param tableName 表名
     * @return 最高版本的定义
     */
    @Transactional(readOnly = true)
    public DyFormFormDefinition getFormDefinitionOfMaxVersionByTblName(String tableName) {
        return this.formDefinitionService.getFormDefinitionOfMaxVersionByTblName(tableName);
    }

    @Override
    @Transactional(readOnly = true)
    public DyFormFormDefinition getFormDefinitionOfMaxVersionById(String id) {
        return this.formDefinitionService.getFormDefinitionOfMaxVersionById(id);
    }

    /**
     * 根据指定的表名获取其对应的最低版本的定义
     *
     * @param tableName 表名
     * @return 最低版本的定义
     */
    @Transactional(readOnly = true)
    public DyFormFormDefinition getFormDefinitionOfMinVersionByTblName(String tableName) {
        return this.formDefinitionService.getFormDefinitionOfMinVersionByTblName(tableName);
    }

    /**
     * 通过对外暴露的id获得对应的表单定义uuid
     *
     * @param id 定义ID
     * @return 表单定义uuid
     */
    @Transactional(readOnly = true)
    public String getFormUuidById(String id) {
        DyFormFormDefinition df = this.getFormDefinitionById(id);
        return df == null ? null : df.getUuid();
    }

    /**
     * 获取实体
     *
     * @param clazz 实体类型
     * @param value ID/UUID
     * @return 实体
     */
    @Transactional(readOnly = true)
    public <T> T getEntity(Class<T> clazz, String value) {
        return formDataService.getEntity(clazz, value);
    }

    /**
     * 获取真实值
     *
     * @param value
     * @return
     */
    public String getRealValue(String jsonValue) {
        if (jsonValue == null || jsonValue.trim().length() == 0) {
            return null;
        }
        JSONObject json;
        try {
            json = new JSONObject(jsonValue);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        if (json.keySet().size() == 0) {
            return null;
        }
        Iterator<String> it = json.keys();
        String realvalues = null;
        while (it.hasNext()) {
            String realValue = it.next();
            if (realvalues == null) {
                realvalues = realValue;
            } else {
                realvalues = realvalues + ";" + realValue;
            }
        }

        return realvalues;
    }

    /**
     * 获取展示值
     *
     * @param jsonValue
     * @return 展示值
     */
    public String getDisplayValue(String jsonValue) {
        if (jsonValue == null || jsonValue.trim().length() == 0) {
            return null;
        }
        JSONObject json;
        try {
            json = new JSONObject(jsonValue);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        if (json.keySet().size() == 0) {
            return null;
        }
        Iterator<String> it = json.keys();
        String displayvalues = null;
        while (it.hasNext()) {
            String realvalue = it.next();
            String displayvalue;
            try {
                displayvalue = json.getString(realvalue);
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
            if (displayvalues == null) {
                displayvalues = displayvalue;
            } else {
                displayvalues = displayvalues + ";" + displayvalue;
            }
        }

        return displayvalues;
    }

    /**
     * 通过表单定义UUID返回ID
     *
     * @param formUuid 表单定义UUID
     * @return 定义ID
     */
    @Transactional(readOnly = true)
    public String getFormIdByFormUuid(String formUuid) {
        return this.getFormDefinition(formUuid).getId();
    }

    /*
     * public static void main(String[] args) throws JSONException { JSONObject
     * json = new JSONObject(); json.put("a", "is"); json.put("b", "no");
     *
     * System.out.println(DyFormFacade.getDisplayValue(json.toString()));
     * System.out.println(DyFormFacade.getRealValue(json.toString())); }
     */

    /**
     * 验证表单数据是否满足约束条件
     *
     * @param formUuid 表单定义UUID
     * @param dataList 数据集合
     * @return 验证结果
     */
    // Map<Integer/*第几行*/, List<Map<String/*列名*/, String/*不满足的约束*/>>>
    @Transactional(readOnly = true)
    public Map<Integer, List<Map<String, String>>> validateFormdates(String formUuid, List<Map<String, Object>> dataList) {

        return formDataService.validateFormdates(formUuid, dataList);
    }

    /**
     * 表单数据验证
     *
     * @param dyFormData 表单数据
     * @return 验证结果
     */
    @Transactional(readOnly = true)
    public ValidateMsg validate(DyFormData dyFormData) {
        // return DyformDataValidationUtils.validate(dyFormData);
        return dyFormData.validateFormData();
    }

    /**
     * 将表单定义转换为xml标准串
     *
     * @param formUuid 表单定义UUID
     * @return xml标准串
     */
    @Transactional(readOnly = true)
    public String formDefinationToXml(String formUuid) {
        return formDataService.formDefinationToXml(formUuid);
    }

    /**
     * 删除表单定义及表单结构
     *
     * @param formUuid
     */
    @Transactional
    public void dropForm(String formUuid) {
        FormDefinition dy = formDefinitionService.findDyFormFormDefinitionByFormUuid(formUuid);
        if (dy == null) {
            return;
        }
        List<FormDefinition> dys = new ArrayList<FormDefinition>();
        if (StringUtils.isNotBlank(dy.doGetTblNameOfpForm())) {
            dys.addAll(formDefinitionService.findDyFormFormDefinitionByTblName(dy.doGetTblNameOfpForm()));
        }
        if (dys.size() > 1) {// 该表的定义有多个版本
            if (this.formDefinitionService.isTableExist(dy.doGetTblNameOfpForm())
                    && this.formDefinitionService.existDataInFormByFormUuid(formUuid)) {

                throw new HibernateDataExistException(MsgUtils.getMessage("dyform.droptable.dataExist", new Object[]{
                        dy.getName(), formUuid}));
            }

        } else {// 定义只有一个版本
            long count = 0;// this.dyFormFacade.countDataInForm(dy.getName());
            if (this.formDefinitionService.isTableExist(dy.getTableName())
                    && (count = this.countDataInForm(dy.getTableName())) > 0) {// 表单中中有数据，不得删除
                // "cann't drop this form , there are " + count +
                // " datas in table[" + dy.getName() + "] "
                throw new HibernateDataExistException(MsgUtils.getMessage("dyform.droptable.haveData", new Object[]{
                        count, dy.getName()}));
            }

        }

        this.formDefinitionService.dropForm(formUuid);
        ApplicationContextHolder.getBean(AuditDataFacadeService.class).saveAuditDataLog(
                new AuditDataLogDto().name(dy.getName()).remark("删除表单")
                        .diffEntity(null, dy));
        DyformCacheUtils.delete(dy);
    }

    @Override
    @Transactional
    public void dropFormById(String id) {
        List<FormDefinition> formDefinitions = formDefinitionService.getFormDefinitionsById(id);
        if (CollectionUtils.isNotEmpty(formDefinitions)) {
            if (this.formDefinitionService.isTableExist(formDefinitions.get(0).getTableName())
                    && this.countDataInForm(formDefinitions.get(0).getTableName()) > 0) {
                throw new HibernateDataExistException("表单存在数据, 无法删除");
            }
            for (int i = 0, len = formDefinitions.size(); i < len; i++) {
                this.formDefinitionService.dropForm(formDefinitions.get(i).getUuid());
                DyformCacheUtils.delete(formDefinitions.get(0));
            }

        }
    }

    /**
     * 判断数据表中指定的formUuid有多少数据
     *
     * @param formUuid 表单定义UUID
     * @return 数据数
     */
    @Transactional(readOnly = true)
    public long countByFormUuid(String formUuid) {

        return this.formDataService.countByFormUuid(formUuid);
    }

    /**
     * 查询表下有多少条数据
     *
     * @param tblName 表名
     * @return 数据数
     */
    @Transactional(readOnly = true)
    public long countDataInForm(String tblName) {

        return this.formDataService.countDataInForm(tblName);
    }

    /**
     * 取得多个form的定义
     *
     * @param formUuids 定义UUID集合
     * @return 定义集合
     */
    @Transactional(readOnly = true)
    public List<DyFormFormDefinition> getFormDefinition(List<String> formUuids) {
        List<DyFormFormDefinition> list = new ArrayList<DyFormFormDefinition>();
        if (formUuids == null || formUuids.size() == 0) {
            return list;
        }
        for (String formUuid : formUuids) {
            DyFormFormDefinition dyf = this.getFormDefinition(formUuid);
            if (dyf != null) {
                list.add(dyf);
            }

        }

        return list;
    }

    /**
     * 取得多个form的定义
     *
     * @param formUuids 定义UUID集合
     * @return 定义JSON集合
     */
    @Transactional(readOnly = true)
    public List<String> getFormDefinitionJSON(List<String> formUuids) {
        List<DyFormFormDefinition> formDefs = this.getFormDefinition(formUuids);
        List<String> jsons = new ArrayList<String>();
        if (formDefs != null)
            for (DyFormFormDefinition dfd : formDefs) {
                jsons.add(dfd.getDefinitionJson());
            }

        return jsons;
    }

    public String getCurrentUserMainJobName() {
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        return userDetail.getMainJobName();
    }

    public List<String> getCurrentUserSecondaryJobs() {
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        List<OrgTreeNodeDto> otherJobs = userDetail.getOtherJobs();
        List<String> jobs = new ArrayList<String>();
        if (otherJobs == null) {
            return jobs;
        }

        for (OrgTreeNodeDto job : otherJobs) {
            jobs.add(job.getName());
        }
        return jobs;
    }

    public List<String> getCurrentUserSJobs() {
        List<String> jobs = new ArrayList<String>();
        jobs.add(getCurrentUserMainJobName());// 主职位放在第一个位置
        jobs.addAll(getCurrentUserSecondaryJobs());
        return jobs;
    }

    /**
     * 获取区块
     *
     * @param formUuid 定义UUID
     * @return 取卡列表
     */
    @Transactional(readOnly = true)
    public List<DyformBlock> getBlocksByformUuid(String formUuid) {
        DyFormData dyformdata = this.createDyformData(formUuid);
        return dyformdata.getBlocks();
    }

    /**
     * 获取区块
     *
     * @param formId 定义ID
     * @return 区块列表
     */
    @Transactional(readOnly = true)
    public List<DyformBlock> getBlocksByFormId(String formId) {
        String formUuid = this.getFormUuidById(formId);
        return this.getBlocksByformUuid(formUuid);
    }

    /**
     * 获取手机端HTML
     *
     * @param formUuid 定义UUID
     * @param dataUuid 数据UUID
     * @return 手机端HTML
     */
    @Transactional(readOnly = true)
    public String getMobileHtml(String formUuid, String dataUuid) {
        DyFormData dyFormData = getDyFormData(formUuid, dataUuid);
        Map<String, List<Map<String, Object>>> display = dyFormData.getDisplayValues();
        String uuid = "";
        String html = "";
        try {
            JSONObject mobileConfig = new JSONObject(DyformCacheUtils.getDyformDefinitionByUuid(formUuid)
                    .getDefinitionJson()).getJSONObject("mobileConfig");
            html = mobileConfig.getString("html");
            uuid = mobileConfig.getString("uuid");
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        Document doc = Jsoup.parse(html, "utf-8");
        for (String key : display.keySet()) {
            if (key.equals(uuid)) {// 主表
                List<Map<String, Object>> mainField = display.get(key);
                for (int i = 0; i < mainField.size(); i++) {
                    Map<String, Object> fieldMap = mainField.get(i);
                    for (String keyName : fieldMap.keySet()) {
                        if (doc.getElementById(uuid + "_" + keyName) != null) {
                            doc.getElementById(uuid + "_" + keyName).after(
                                    fieldMap.get(keyName) == null ? "" : fieldMap.get(keyName).toString());
                            doc.getElementById(uuid + "_" + keyName).remove();
                        }
                    }
                }
            } else {// 从表
                String subFields = doc.getElementsByAttributeValue("formuuid", key).get(0).attr("fields");
                String[] fName = subFields.split(",");
                List<Map<String, Object>> dataList = display.get(key);
                String dataUuids = "";
                for (int i = 0; i < dataList.size(); i++) {
                    Map<String, Object> fieldMap = dataList.get(i);
                    dataUuids += "," + fieldMap.get("uuid");
                }
                dataUuids = dataUuids.replaceFirst(",", "");
                for (int i = 0; i < dataList.size(); i++) {
                    Map<String, Object> fieldMap = dataList.get(i);
                    String tr = "<tr class='subItem' dataUuid='" + fieldMap.get("uuid") + "';>";
                    // onclick='location.href=\""
                    // + request.getContextPath() +
                    // "/pt/dyform/dyform_mobile_demo.jsp?formUuid=" + key
                    // + "&type=sub&dataUuids=" + dataUuids +
                    // "&source=source&dataUuid=" + fieldMap.get("uuid")
                    // + "\"'
                    for (int j = 0; j < fName.length; j++) {
                        String fieldVal = fieldMap.get(fName[j]) == null ? "" : fieldMap.get(fName[j]).toString();
                        tr += "<td class='subField' id='" + fName[j] + "'>" + fieldVal + "</td>";
                    }
                    tr += "</tr>";
                    doc.getElementsByAttributeValue("formuuid", key).get(0).getElementsByTag("tbody").append(tr);
                }
            }
        }
        return doc.toString();

    }

    /**
     * Clob转String
     *
     * @param clob Clob值
     * @return String 值
     */
    public String ClobToString(Clob clob) {
        return ClobUtils.ClobToString(clob);
    }

    /**
     * 获取某主表的一条数据对应的某从表数据的最小排序号
     *
     * @param mainformFormUuid 主表定义UUID
     * @param mainformDataUuid 主表数据UUID
     * @param subformFormUuid  从表定义UUID
     * @return 最小排序号
     */
    @Transactional(readOnly = true)
    public Integer getMinOrderNo(String mainformFormUuid, String mainformDataUuid, String subformFormUuid) {
        return this.formDataService.getMinOrderNo(mainformFormUuid, mainformDataUuid, subformFormUuid);
    }

    /**
     * 获取某主表的一条数据对应的某从表数据的最大排序号
     *
     * @param mainformFormUuid 主表定义UUID
     * @param mainformDataUuid 主表数据UUID
     * @param subformFormUuid  从表定义UUID
     * @return 最大排序号
     */
    @Transactional(readOnly = true)
    public Integer getMaxOrderNo(String mainformFormUuid, String mainformDataUuid, String subformFormUuid) {
        return this.formDataService.getMaxOrderNo(mainformFormUuid, mainformDataUuid, subformFormUuid);
    }

    /**
     * 返回表单所有的依赖关系
     * 如何描述该方法
     *
     * @param dyFormDefinition
     * @return Map<" String依赖类型 ", List < String类型UUID集合>>
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Set<String>> getResource(DyFormFormDefinition dyFormDefinition) {
        Map<String, Set<String>> resultMap = new HashMap<String, Set<String>>();
        FormDefinitionHandler dyFormDefinitionJSON = ((FormDefinition) dyFormDefinition).doGetFormDefinitionHandler();
        Set<String> dictCodes = new HashSet<String>();// 数据字典
        Set<String> dictCodeParent = new HashSet<String>();// 数据字典
        Set<String> subforms = new HashSet<String>();// 子表
        Set<String> showModelIds = new HashSet<String>();// 显示单据手机
        Set<String> printTemplateIds = new HashSet<String>();// 打印模板
        Set<String> datasourceIds = new HashSet<String>();// 数据源
        Set<String> viewSourceIds = new HashSet<String>();// 视图
        // 取出表单从表信息
        List<String> subformList = dyFormDefinitionJSON.getFormUuidsOfSubform();
        for (String subform : subformList) {
            subforms.add(subform);
        }
        if (subforms != null && subforms.size() > 0) {
            resultMap.put(IexportType.FormDefinition, subforms);
        }
        /*
         * // 手机端的显示单据code String showMobileModelId = dyFormDefinitionJSON
         * .getFormPropertyOfStringType(EnumFormPropertyName.showMobileModelId);
         *
         * if (showModelIds.size() > 0) {
         * resultMap.put(IexportType.DyFormDisplayModel, showModelIds); }
         */

        // 遍历字段取出数据字典应用信息,数据源
        for (String fieldName : dyFormDefinitionJSON.getFieldNamesOfMainform()) {
            String dictCode = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                    EnumFieldPropertyName.dictCode);
            String dataSourceId = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                    EnumFieldPropertyName.dataSourceId);
            String viewSourceId = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                    EnumFieldPropertyName.relationDataValueTwo);
            // 数据字典
            if (StringUtils.isNotBlank(dictCode)) {
                String dictCodeTemp = dictCode.split(":")[0];
                DataDictionary dataDictionary = dataDictionaryService.getByType(dictCodeTemp);
                if (dataDictionary != null) {
                    dictCodes.add(dataDictionary.getUuid());
                    if (dataDictionary.getParent() != null) {
                        dictCodeParent.add(dataDictionary.getParent().getUuid());
                    }
                }
            }

        }

        // 加入数据字典
        resultMap.put(IexportType.DataDictionaryParent, dictCodes);
        resultMap.put(IexportType.DataDictionary, dictCodes);
        return resultMap;
    }

    /**
     * 获取数据库字符集
     * add by wujx 20160621
     */
    @Transactional(readOnly = true)
    public String getDbCharacterSet() {
        return formDefinitionService.queryDbCharacterSet();
    }

    /**
     * 通过从表信息获取表单数据 add by wujx 20161019
     *
     * @param dataUuidOfSubform
     * @return
     */
    @Transactional(readOnly = true)
    public List<DyFormData> getDyFormDataBySubformInfo(String formUuidOfSubform, String dataUuidOfSubform)
            throws Exception {
        return formDataService.getDyFormDataBySubformInfo(formUuidOfSubform, dataUuidOfSubform);
    }

    @Override
    @Transactional(readOnly = true)
    public Select2QueryData queryAllPforms(Select2QueryInfo queryInfo) {
        String excludeUuids = queryInfo.getOtherParams("excludeUuids");
        List<String> excludeUuidList = null;
        if (StringUtils.isNotBlank(excludeUuids)) {
            excludeUuidList = Arrays.asList(StringUtils.split(excludeUuids, Separator.SEMICOLON.getValue()));
        }
        List<String> systemUnitIds = new ArrayList<>();
        String includeSuperAdmin = queryInfo.getOtherParams("includeSuperAdmin");
        String systemUnitId = queryInfo.getOtherParams("systemUnitId");
        if (StringUtils.isNotBlank(systemUnitId)) {
            systemUnitIds.add(systemUnitId);
        }
        if (StringUtils.equalsIgnoreCase(Boolean.TRUE.toString(), includeSuperAdmin) && systemUnitIds.size() > 0) {
            systemUnitIds.add(MultiOrgSystemUnit.PT_ID);
        }
        if (queryInfo.getOtherParams("includeSubform") != null) {
            return formDefinitionService.queryByNameOrIdOrTableName(queryInfo.getSearchValue(),
                    queryInfo.getPagingInfo(), excludeUuidList, Lists.newArrayList("P", "MST"), systemUnitIds);
        }
        Select2QueryData queryData = formDefinitionService.queryByNameOrIdOrTableName(queryInfo.getSearchValue(),
                queryInfo.getPagingInfo(), excludeUuidList, systemUnitIds);
        return queryData;
    }

    @Override
    @Transactional(readOnly = true)
    public Select2QueryData getSelectedFormDefinition(Select2QueryInfo queryInfo) {
        List<DyFormFormDefinition> list = new ArrayList<DyFormFormDefinition>();
        String[] appFuncUuids = queryInfo.getIds();
        if (appFuncUuids == null || appFuncUuids.length == 0) {

        } else {
            for (String formUuid : appFuncUuids) {
                FormDefinition dfd = formDefinitionService.findDyFormFormDefinitionByFormUuid(formUuid);
                if (dfd != null) {
                    FormDefinition dfdcopy = new FormDefinition();
                    try {
                        BeanCopyUtils.getInstance().copyProperties(dfdcopy, dfd);
                        dfdcopy.setName(dfdcopy.getName() + "(" + dfdcopy.getVersion() + ")");
                    } catch (IllegalAccessException e) {
                        logger.error(e.getMessage(), e);
                    } catch (InvocationTargetException e) {
                        logger.error(e.getMessage(), e);
                    }

                    list.add(dfdcopy);
                }
            }

        }
        return new Select2QueryData(list, "uuid", "name", queryInfo.getPagingInfo());
    }

    @Override
    @Transactional(readOnly = true)
    public Select2QueryData queryAllVforms(Select2QueryInfo queryInfo) {
        String pFormUuid = queryInfo.getRequest().getParameter("pFormUuid");
        Select2QueryData queryData = formDefinitionService.queryTypeByNameOrIdOrTableName(pFormUuid,
                queryInfo.getPagingInfo(), queryInfo.getSearchValue(), DyformTypeEnum.V.getValue());
        return queryData;
    }

    @Override
    @Transactional(readOnly = true)
    public Select2QueryData queryAllMforms(Select2QueryInfo queryInfo) {
        String pFormUuid = queryInfo.getRequest().getParameter("pFormUuid");
        Select2QueryData queryData = formDefinitionService.queryTypeByNameOrIdOrTableName(pFormUuid,
                queryInfo.getPagingInfo(), queryInfo.getSearchValue(), DyformTypeEnum.M.getValue());
        return queryData;
    }

    @Override
    @Transactional(readOnly = true)
    public Select2QueryData queryAllMstforms(Select2QueryInfo queryInfo) {
        Select2QueryData queryData = formDefinitionService.queryTypeByNameOrIdOrTableName(queryInfo.getPagingInfo(),
                queryInfo.getSearchValue(), DyformTypeEnum.MST.getValue());
        return queryData;
    }

    @Override
    public DyFormData parseDyformData(String jsonData) {
        DyFormData dyFormData = JsonBinder.buildNormalBinder().fromJson(jsonData, DyFormDataImpl.class);
        return dyFormData;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DyFormFormDefinition> getAllFormDefinitionBySystemUnitId(String systemUnitId) {
        List<DyFormFormDefinition> dfs = new ArrayList<DyFormFormDefinition>();
        dfs.addAll(this.formDefinitionService.getAllFormDefinitionBySystemUnitId(systemUnitId));
        return dfs;
    }

    @Override
    public DyFormData deserializeDyformData(JsonParser jp, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        return jp.getCodec().readValue(jp, DyFormDataImpl.class);
    }

    @Override
    @Transactional(readOnly = true)
    public DyFormData findUniqueData(String formUuid, String systemUnitId) throws UniqueException {
        logger.info("[formUuid=" + formUuid + "]begin to getDyformDataAndDefinition");
        if (!this.isFormExistByFormUuid(formUuid)) {
            throw new RuntimeException("单据定义uuid[" + formUuid + "]对应的单据不存在");
        }
        long time1 = System.currentTimeMillis();
        Map<String, List<Map<String, Object>>> formData = new HashMap<String, List<Map<String, Object>>>();

        formData = this.formDataService.getUniqueFormData(formUuid, systemUnitId);

        long time3 = System.currentTimeMillis();
        logger.info("[formUuid=" + formUuid + "]get getDyformData spent:" + (time3 - time1) / 1000.0 + "s");

        DyFormData dyFormData = new DyFormDataImpl();

        dyFormData.setLoadDefaultFormData(true);// 加载默认值
        dyFormData.setLoadDictionary(true);// 加载数字字段
        dyFormData.setLoadSubformDefinition(true);// 加载从表定义

        // dyFormData.setLoadDefaultFormData(true);//加载默认值
        // dyFormData.setLoadDictionary(true);//加载数字字段
        // dyFormData.setLoadSubformDefinition(true);//加载从表定义
        dyFormData.setFormUuid(formUuid);
        dyFormData.setFormDatas(formData, false);

        long time2 = System.currentTimeMillis();
        logger.info("[formUuid=" + formUuid + "]load FormDefinition and Data spent " + (time2 - time1) / 1000.0 + "s");
        return dyFormData;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFormExistByFormUuid(String formUuid) {
        FormDefinition df = this.getFormDefinition(formUuid);
        if (df == null) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<TreeNode> getSubformApplyToRootDicts(String uuid) {

        return this.formDefinitionService.getSubformApplyToRootDicts(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreeNode> getFormIdApplyToRootDicts(String uuid) {

        return this.formDefinitionService.getFormIdApplyToRootDicts(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreeNode> getFormFieldApplyToRootDicts(String uuid) {

        return this.formDefinitionService.getFormFieldApplyToRootDicts(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.service.DyFormFacade#isUniqueForFields(java.lang.String, java.util.Map)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isUniqueForFields(String uuid, String formUuid, Map<String, Object> fieldKeyValues,
                                     String isFiterCondition) {
        return formDataService.isUniqueForFields(uuid, formUuid, fieldKeyValues, isFiterCondition);

    }

    @Override
    @Transactional(readOnly = true)
    public List<DyformTab> getSubTabsByformUuid(String formUuid) {
        DyFormData dyformdata = this.createDyformData(formUuid);
        return dyformdata.getTabs();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DyformTab> getSubTabsByFormId(String formId) {
        String formUuid = this.getFormUuidById(formId);
        return this.getSubTabsByformUuid(formUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QueryItem> queryAllDyformDataByFormId(String formId) {
        String formUuid = this.getFormUuidById(formId);
        if (StringUtils.isBlank(formUuid)) {
            return Lists.newArrayList();
        }
        return this.queryAllDyformDataByFormUuid(formUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QueryItem> queryAllDyformDataByFormUuid(String formUuid) {
        return this.query(false, formUuid, null, null, null, null, null, null, 0, -1);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.service.DyFormDefinitionService#convertDefinitionJson(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public String loadDefinitionJsonDefaultInfo(String definitonJson, String formType, String name, String pformUuid)
            throws JSONException {
        FormDefinitionHandler json = new FormDefinitionHandler(definitonJson, formType, name, pformUuid);
        json.loadFieldDictionary(false);
        json.loadFieldJobDictionary();
        json.loadDefaultFormData();
        JSONObject subforms = json.getSubformDefinitions();
        List<String> subformDefinitions = new ArrayList<String>();
        for (Object subformUuid : subforms.keySet()) {
            FormDefinition subform = getFormDefinition(subformUuid.toString());
            FormDefinitionHandler subformJsonHandler = subform.doGetFormDefinitionHandler();
            subformJsonHandler.loadFieldDictionary(false);
            subformJsonHandler.loadFieldJobDictionary();
            subformJsonHandler.loadDefaultFormData();
            subformDefinitions.add(subform.getDefinitionJson());
        }
        json.addFormProperty("subformDefinitions", subformDefinitions);
        return json.toString();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.service.DyFormFacade#queryUniqueForFields(java.lang.String, java.util.Map, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> queryUniqueForFields(String formUuid, Map<String, Object> params, String fiterCondition) {
        return formDataService.queryUniqueForFields(formUuid, params, fiterCondition);
    }

    /**
     * 读取所有的存储和展现单据, 提供给jquery.wSelect2.js查询
     *
     * @param queryInfo
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Select2QueryData queryAllForms(Select2QueryInfo queryInfo) {
        String[] formTypes = queryInfo.getRequest().getParameterValues("formTypes");
        Select2QueryData queryData = formDefinitionService.queryTypeByNameOrIdOrTableName(
                queryInfo.getPagingInfo(), queryInfo.getSearchValue(), formTypes);
        return queryData;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDefinition> getDyFormDefinitionIncludeRefDyFormByModuleId(String moduleId) {
        return formDefinitionService.getDyFormDefinitionIncludeRefDyFormByModuleId(moduleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDefinition> getDyFormDefinitionIncludeRefDyFormByPiUuid(String piUuid) {
        return formDefinitionService.getDyFormDefinitionIncludeRefDyFormByPiUuid(piUuid);
    }

    /**
     * 更新表单定义和表结构
     *
     * @param formDefinition
     * @param deletedFieldNames
     * @return
     */
    @Override
    @Transactional
    public FormDefinition updateFormDefinitionAndFormTable(String formDefinition, String deletedFieldNames) {
        FormDefinition dy = JsonUtils.json2Object(formDefinition, FormDefinition.class);
        JsonBinder jsonBinder = JsonBinder.buildNormalBinder();
        FormDefinition old = new FormDefinition();
        BeanUtils.copyProperties(formDefinitionService.getOne(dy.getUuid()), old);
        formDefinitionService.updateFormDefinitionAndFormTable(dy, jsonBinder.fromJson(deletedFieldNames, List.class));
        formDefinitionService.flushSession();
        ApplicationContextHolder.getBean(AuditDataFacadeService.class)
                .saveAuditDataLog(new AuditDataLogDto().diffEntity(formDefinitionService.getOne(dy.getUuid()), old).name(dy.getName()).remark("编辑表单"));
        return dy;
    }

    /**
     * 根据pFormUuid获取指定formType类型扩展单据最大的UUID
     */
    @Override
    public String getMaxExtFormUuidByFormUuid(String pFormUuid, String formType) {
        return formDefinitionService.getMaxExtFormUuidByFormUuid(pFormUuid, formType);
    }


    @Override
    public List getFormDataByWhere(String formId, String where, Map<String, Object> namedParams) {
        return formDataService.getFormDataByWhere(formId, where, namedParams);
    }

    @Override
    public List<DyFormFormDefinition> listDyFormDefinitionByUuids(List<String> formUuids) {
        if (CollectionUtils.isEmpty(formUuids)) {
            return Collections.emptyList();
        }
        return formDefinitionService.listByUuidsWithoutJson(formUuids);
    }

    @Override
    public List getFormDataByWhereByFormUuid(String formUuid, String where, Map<String, Object> namedParams) {
        return formDataService.getFormDataByWhereByFormUuid(formUuid, where, namedParams);
    }

    @Override
    public List getFormDataUuidByWhereByFormUuid(String formUuid, String where, Map<String, Object> namedParams) {
        return formDataService.getFormDataUuidByWhereByFormUuid(formUuid, where, namedParams);
    }

    /**
     * 保存有关联角色组织控件时，关联保存角色成员
     *
     * @param dyFormData dyFormData
     */
    private void saveOrgFormRoleValue(DyFormData dyFormData) {

        String formDefinition = dyFormData.getFormDefinition();
        JSONObject formDefinitionJsonObject = null;
        // 角色uuid 角色成员值
        Map<String, String> relativeRoleMap = new HashMap<>();

        // 主表
        try {
            formDefinitionJsonObject = new JSONObject(formDefinition);
            // 主表字段
            JSONObject fieldsJsonObject = formDefinitionJsonObject.getJSONObject("fields");

            //循环主表字段
            for (Object fieldName : fieldsJsonObject.keySet()) {
                try {
                    JSONObject fieldNameJsonObject = fieldsJsonObject.getJSONObject(fieldName.toString());

                    String relativeRoleUuids = fieldNameJsonObject.getString("relativeRoleUuids");
                    if (StringUtils.isNotBlank(relativeRoleUuids)) {
                        String relativeRoleUuid = relativeRoleUuids.split(Separator.SEMICOLON.getValue())[0];
                        String fieldValue = dyFormData.getFieldValue(fieldName.toString()).toString();
                        relativeRoleMap.put(relativeRoleUuid, fieldValue);
                    }
                } catch (Exception e) {
                }
            }
        } catch (JSONException e) {
            //			e.printStackTrace();
        }

        // 从表
        JSONObject subformsJsonObject = null;
        try {
            subformsJsonObject = formDefinitionJsonObject.getJSONObject("subforms");
        } catch (JSONException e) {
            //			e.printStackTrace();
        }

        if (subformsJsonObject != null) {

            for (Object subFormUuid : subformsJsonObject.keySet()) {
                // 从表字段
                FormDefinition subFieldDefinition = this.getFormDefinition(subFormUuid.toString());
                JSONObject fieldDefinitionsJson = subFieldDefinition.doGetFormDefinitionHandler().getFieldDefinitions();

                for (Object fieldName : fieldDefinitionsJson.keySet()) {
                    JSONObject fieldNameJsonObject = null;
                    try {
                        fieldNameJsonObject = fieldDefinitionsJson.getJSONObject(fieldName.toString());
                        String relativeRoleUuids = fieldNameJsonObject.getString("relativeRoleUuids");

                        if (StringUtils.isNotBlank(relativeRoleUuids)) {
                            String relativeRoleUuid = relativeRoleUuids.split(Separator.SEMICOLON.getValue())[0];
                            List<Map<String, Object>> formDatas = dyFormData.getFormDatas(subFormUuid.toString());
                            if (formDatas != null) {
                                for (Map<String, Object> formData : formDatas) {
                                    Object fieldValue = formData.get(fieldName.toString());
                                    if (fieldValue != null) {
                                        relativeRoleMap.put(relativeRoleUuid, fieldValue.toString());
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        //						e.printStackTrace();
                    }
                }
            }
        }

        //联动更新角色成员信息
        for (String relativeRoleUuid : relativeRoleMap.keySet()) {
            multiOrgApiFacade.dealRoleRemoveEvent(relativeRoleUuid);
            multiOrgApiFacade.addRoleMembers(relativeRoleUuid, relativeRoleMap.get(relativeRoleUuid));
        }

    }

    /**
     * 设置有关联角色的组织控件值
     *
     * @param dyFormData       dyFormData
     * @param dyformDefinition dyformDefinition
     */
    private void setOrgFormRoleValue(DyFormData dyFormData, FormDefinition dyformDefinition) {
        FormDefinitionHandler formDefinitionHandler = dyformDefinition.doGetFormDefinitionHandler();
        //主表
        JSONObject fieldDefinitionsJson = formDefinitionHandler.getFieldDefinitions();
        // 主表字段
        for (Object fieldName : fieldDefinitionsJson.keySet()) {
            JSONObject fieldNameJsonObject = null;
            try {
                fieldNameJsonObject = fieldDefinitionsJson.getJSONObject(fieldName.toString());

                // relativeRoleUuids realDisplayFieldName
                String relativeRoleUuids = fieldNameJsonObject.has("relativeRoleUuids") ? fieldNameJsonObject
                        .getString("relativeRoleUuids") : StringUtils.EMPTY;
                String realDisplayFieldName = "";
                JSONObject realDisplayJsonObject = fieldNameJsonObject.getJSONObject("realDisplay");
                if (realDisplayJsonObject != null) {
                    realDisplayFieldName = realDisplayJsonObject.has("display") ? realDisplayJsonObject
                            .getString("display") : StringUtils.EMPTY;
                }

                if (fieldNameJsonObject.optBoolean("useRelativeRole") && StringUtils.isNotBlank(relativeRoleUuids)) {
                    String relativeRoleUuid = relativeRoleUuids.split(Separator.SEMICOLON.getValue())[0];
                    Map<String, String> roleMemberMap = roleFacadeService.queryRoleMembers(relativeRoleUuid);
                    String memberIdStr = StringUtils.join(roleMemberMap.keySet(), Separator.SEMICOLON.getValue());
                    String memberNameStr = StringUtils.join(roleMemberMap.values(), Separator.SEMICOLON.getValue());
                    dyFormData.setFieldValue(fieldName.toString(), memberIdStr);

                    if (StringUtils.isNotBlank(realDisplayFieldName)) {
                        dyFormData.setFieldValue(realDisplayFieldName, memberNameStr);
                    }
                }

            } catch (JSONException e) {
                //				e.printStackTrace();
            }
        }

        // 从表
        List<String> subformDefinitions = (List<String>) dyformDefinition.doGetFormDefinitionHandler().getFormProperty(
                "subformDefinitions");
        // 循环所有从表
        if (CollectionUtils.isNotEmpty(subformDefinitions)) {

            for (String subformDefinition : subformDefinitions) {
                try {
                    JSONObject subformDefinitionJsonObject = new JSONObject(subformDefinition);
                    String subFormUuid = subformDefinitionJsonObject.getString(EnumSystemField.uuid.name());

                    // 从表字段
                    JSONObject subFieldsJsonObject = subformDefinitionJsonObject.getJSONObject("fields");
                    for (Object subFieldName : subFieldsJsonObject.keySet()) {
                        try {
                            JSONObject fieldNameJsonObject = subFieldsJsonObject.getJSONObject(subFieldName.toString());

                            // relativeRoleUuids realDisplayFieldName
                            String relativeRoleUuids = fieldNameJsonObject.has("relativeRoleUuids") ? fieldNameJsonObject
                                    .getString("relativeRoleUuids") : StringUtils.EMPTY;
                            String realDisplayFieldName = "";
                            JSONObject realDisplayJsonObject = fieldNameJsonObject.getJSONObject("realDisplay");
                            if (realDisplayJsonObject != null) {
                                realDisplayFieldName = realDisplayJsonObject.getString("display");
                            }

                            if (fieldNameJsonObject.optBoolean("useRelativeRole") && StringUtils.isNotBlank(relativeRoleUuids)) {
                                //获取角色成员
                                String relativeRoleUuid = relativeRoleUuids.split(Separator.SEMICOLON.getValue())[0];
                                Map<String, String> roleMemberMap = roleFacadeService
                                        .queryRoleMembers(relativeRoleUuid);
                                String memberIdStr = StringUtils.join(roleMemberMap.keySet(),
                                        Separator.SEMICOLON.getValue());
                                String memberNameStr = StringUtils.join(roleMemberMap.values(),
                                        Separator.SEMICOLON.getValue());

                                List<Map<String, Object>> formDatas = dyFormData.getFormDatas(subFormUuid);
                                if (formDatas != null) {
                                    for (Map<String, Object> formData : formDatas) {
                                        formData.put(subFieldName.toString(), memberIdStr);
                                    }
                                }
                                if (StringUtils.isNotBlank(realDisplayFieldName) && formDatas != null) {
                                    for (Map<String, Object> formData : formDatas) {
                                        formData.put(realDisplayFieldName, memberNameStr);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            //							e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    //					e.printStackTrace();
                }
            }

        }
    }

}
