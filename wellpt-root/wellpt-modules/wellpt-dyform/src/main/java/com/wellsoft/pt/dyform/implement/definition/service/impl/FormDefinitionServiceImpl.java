package com.wellsoft.pt.dyform.implement.definition.service.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.jqgrid.JqTreeGridNode;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.*;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.xml.XmlConverUtils;
import com.wellsoft.pt.basicdata.datadict.bean.DataDictionaryBean;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.basicdata.datastore.entity.CdDataStoreDefinition;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.basicdata.datastore.service.CdDataStoreDefinitionService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.document.MongoLogService;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.cache.DyformCacheUtils;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.DbDataType;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFieldPropertyName;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFormPropertyName;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.InputMode.EnumOrgInputMode;
import com.wellsoft.pt.dyform.implement.definition.dao.FormDefinitionDao;
import com.wellsoft.pt.dyform.implement.definition.dao.FormDefinitionLogDao;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinitionLog;
import com.wellsoft.pt.dyform.implement.definition.enums.*;
import com.wellsoft.pt.dyform.implement.definition.event.FormFieldsRemovedEvent;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.TableConfig;
import com.wellsoft.pt.dyform.implement.repository.CustomFormRepository;
import com.wellsoft.pt.dyform.implement.repository.FormField;
import com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext;
import com.wellsoft.pt.dyform.implement.repository.adapter.FormDataServiceAdapter;
import com.wellsoft.pt.dyform.implement.repository.enums.FormRepositoryModeEnum;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.support.CustomOracle10gDialect;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AbstractTable;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.CustomConfiguration;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.internal.SessionFactoryImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

@Service
public class FormDefinitionServiceImpl extends AbstractJpaServiceImpl<FormDefinition, FormDefinitionDao, String>
        implements FormDefinitionService, Select2QueryApi {
    private static final String ROOT_DICT_TYPE = "PT";
    private static Logger logger = LoggerFactory.getLogger(FormDefinitionServiceImpl.class);
    private final String QUERY_MAX_VERSION_LIST = "select * from DYFORM_FORM_DEFINITION a,"
            + "(select id, max(version) version from DYFORM_FORM_DEFINITION group by id) b"
            + " where a.id = b.id and a.version = b.version ";
    private final String QUERY_MIN_VERSION_LIST = "select * from DYFORM_FORM_DEFINITION a,"
            + "(select table_name, min(version) version from DYFORM_FORM_DEFINITION group by table_name) b"
            + " where a.table_name = b.table_name and a.version = b.version";
    @Autowired
    DyFormFacade dyFormFacade;
    @Autowired
    private FormDefinitionDao formDefinitionDao;
    @Autowired
    private FormDefinitionLogDao formDefinitionLogDao;
    @Autowired
    private MongoLogService mongoLogService;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private CdDataStoreService cdDataStoreService;
    @Autowired
    private DataDictionaryService dataDictionaryService;
    @Autowired
    private CdDataStoreDefinitionService cdDataStoreDefinitionService;
    /**
     * 通过单例类configuration获取的sessionfactory
     */
    private SessionFactory sessionFactory;

    public FormDefinitionServiceImpl() {
        // 初始化表单缓存
        // initDyformCache();
    }

    private void saveFormDefinition(FormDefinition formDefinition, List<String> deletedFieldNames) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        // 设置版本
        if (StringUtils.isEmpty(formDefinition.getUuid()) || "undefined".equals(formDefinition.getUuid())) {// 新增
            formDefinition.setSystem(RequestSystemContextPathResolver.system());
            formDefinition.setTenant(userDetails.getTenantId());
            formDefinition.doBindVersionAsMinVersion();
            formDefinitionDao.save(formDefinition);
            formDefinition.doBindUuid2Json();
            formDefinitionDao.save(formDefinition);
        } else {
            if ("1".equals(formDefinition.getIsUp())) {// 当调用方要升级版本时,isUp将会设置成1
                String formUuid = formDefinition.getUuid();
                formDefinition.setUuid(null);
                up2LatestVersion(formDefinition);
                formDefinitionDao.save(formDefinition);
                formDefinition.doBindUuid2Json();
                formDefinition.doBindVersion2DefinitionJson(formDefinition.getVersion());
                // formDefinition.updateVersion2Json();
                formDefinitionDao.save(formDefinition);
                if (DyformTypeEnum.isPform(formDefinition.getFormType())) {

                }
            } else {
                FormDefinition obj = this.dao.getOne(formDefinition.getUuid());
                if (obj == null) {
                    formDefinition.setUuid(null);
                    saveFormDefinition(formDefinition, deletedFieldNames);
                    return;// 置空uuid后重新保存,兼容定义不存在的保存方式
                    // throw new RuntimeException("tableName[" + formDefinition.doGetTblNameOfpForm() + "]对应的表单不存在 ");
                }
                try {
                    publishFormFieldsRemovedEvent(obj, deletedFieldNames);
                    saveFormHistory(obj);// 保存历史
                    /**
                     * 仅更新表单允许修改的属性
                     */
                    obj.setName(formDefinition.getName());
                    obj.setRemark(formDefinition.getRemark());
                    obj.setCode(formDefinition.getCode());
                    obj.setDefinitionJson(formDefinition.getDefinitionJson());
                    obj.setDefinitionVjson(formDefinition.getDefinitionVjson());
                    obj.setCustomJsModule(formDefinition.getCustomJsModule());
                    obj.setCategoryUuid(formDefinition.getCategoryUuid());
                    if (DyformTypeEnum.isVform(formDefinition.getFormType())) {
                        obj.setTableName(formDefinition.getTableName());
                        obj.setId(formDefinition.getId());
                        obj.setFormType(formDefinition.getFormType());
                        obj.setRelationTbl(formDefinition.getTableName() + DyFormConfig.DYFORM_RELATIONTBL_POSTFIX);
                    }
                    if (StringUtils.isBlank(obj.getSystem())) {
                        obj.setSystem(RequestSystemContextPathResolver.system());
                    }
                    if (StringUtils.isBlank(obj.getTenant())) {
                        obj.setTenant(userDetails.getTenantId());
                    }

                    // 该拷贝方法不会拷贝null值属性，会导致属性置空无法保存
                    // BeanCopyUtils copyUtils = new BeanCopyUtils();
                    // copyUtils.copyProperties(obj, formDefinition);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                obj.doBindUuid2Json();
                formDefinitionDao.save(obj);
            }
        }

        // saveFormLog(formDefinition, "saveFormDefinition");

    }

    public void saveFormHistory(FormDefinition formDefinition) {
        FormDefinitionLog histEntity = new FormDefinitionLog();
        histEntity.setFormUuid(formDefinition.getUuid());
        histEntity.setDefinitionJson(formDefinition.getDefinitionJson());
        histEntity.setOperateIp(SpringSecurityUtils.getCurrentUserIp());
        formDefinitionLogDao.save(histEntity);
    }

    public void publishFormFieldsRemovedEvent(FormDefinition formDefinition, List<String> deletedFieldNames) {
        if (CollectionUtils.isEmpty(deletedFieldNames)) {
            return;
        }
        FormDefinitionHandler handler = formDefinition.doGetFormDefinitionHandler();
        formDefinition.setFormDefinitionHandler(null);
        ApplicationContextHolder.publishEvent(new FormFieldsRemovedEvent(formDefinition, handler, deletedFieldNames));
    }

    /**
     * 更新到最新版本
     *
     * @param formDefinition
     */
    public void up2LatestVersion(FormDefinition formDefinition) {
        DyFormFormDefinition maxVersionDyformDefinition = this.getDyFormFormDefinitionOfMaxVersionById(formDefinition
                .getId());
        String maxVersion = formDefinition.getMinVersion();
        if (maxVersionDyformDefinition != null) {
            maxVersion = maxVersionDyformDefinition.getVersion();
        }
        String version = maxVersionDyformDefinition.getVersionFormat().format((Float.parseFloat(maxVersion) + 0.1));
        formDefinition.setVersion(version);
    }

    public String createRelationTblHbmXML(FormDefinition formDefinition) {
        JSONObject formDefinitionJson = new JSONObject();
        JSONObject fieldDefinitions = new JSONObject();
        JSONObject suboformDefinitions = new JSONObject();
        String relationTblName = null;
        relationTblName = formDefinition.doGetTblNameOfpForm() + DyFormConfig.DYFORM_RELATIONTBL_POSTFIX;
        try {
            formDefinitionJson.put(EnumFormPropertyName.relationTbl.name(), relationTblName);
            formDefinition.setRelationTbl(relationTblName);
            formDefinition.doGetFormDefinitionHandler().addFormProperty(EnumFormPropertyName.relationTbl.name(),
                    relationTblName);
            formDefinitionJson.put(EnumFormPropertyName.tableName.name(), relationTblName);
            formDefinitionJson.put(EnumFormPropertyName.fields.name(), fieldDefinitions);
            formDefinitionJson.put(EnumFormPropertyName.subforms.name(), suboformDefinitions);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }

        String xml = convertDefinition2HbmCfg(formDefinitionJson.toString(), formDefinition.getFormType(), null, null,
                EnumRelationTblSystemField.class, new ArrayList<String>());

        return xml;
    }

    /**
     * 判断是已存在表名为参数formTblName的数据表单表
     *
     * @param formTblName
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isFormExistByFormTblName(String formTblName) {
        // 查看表是否已存在,若已存在
        List<FormDefinition> dfds = this.findDyFormFormDefinitionByTblName(formTblName);
        if (dfds == null || dfds.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FormDefinition findDyFormFormDefinitionByFormUuid(String formUuid) {
        FormDefinition dydf = this.formDefinitionDao.getOne(formUuid);
        if (dydf != null) {
            // 更新JSON定义
            FormDefinitionHandler formDefinitionHandler = dydf.doGetFormDefinitionHandler();
            String dbCharacterSet = queryDbCharacterSet();
            formDefinitionHandler.addFormProperty(EnumFormPropertyName.dbCharacterSet, dbCharacterSet);
            dydf.setDefinitionJson(formDefinitionHandler.toString());
        }
        return dydf;
    }

    @Override
    public List<FormDefinition> getFormDefinitionVjsonNotNull() {
        return this.dao.listByHQL("from FormDefinition where definitionVjson is not null", null);
    }

    @Override
    @Transactional(readOnly = true)
    public String getFormDefinitionJSONByUuid(String uuid) {
        DyFormFormDefinition dyFormFormDefinition = DyformCacheUtils.getDyformDefinitionByUuid(uuid);
        if (dyFormFormDefinition != null) {
            return dyFormFormDefinition.getDefinitionJson();
        }
        FormDefinition definition = this.dao.getOne(uuid);
        if (definition != null) {
            return definition.getDefinitionJson();
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDefinition> findDyFormFormDefinitionsById(String id) {
        List<FormDefinition> list = new ArrayList<FormDefinition>();
        list.addAll(this.formDefinitionDao.listByFieldEqValue("id", id));
        return list;
    }

    public FormDefinition getDyFormFormDefinitionOfMaxVersionById(String outerId) {
        String hql = QUERY_MAX_VERSION_LIST + " and a.id = :id";
        SQLQuery sqlQuery = this.formDefinitionDao.getSession().createSQLQuery(hql);
        sqlQuery.setString("id", outerId);
        List<FormDefinition> list = sqlQuery.addEntity(FormDefinition.class).list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public List<FormDefinition> findDyFormFormDefinitionByTblName(String tblName) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tableName", tblName);

        List<FormDefinition> list = this.formDefinitionDao.listByHQL(
                "select o from FormDefinition o where o.tableName = :tableName", params);

        List<FormDefinition> listtmp = new ArrayList<FormDefinition>();
        listtmp.addAll(list);
        return listtmp;

    }

    /**
     * 判断是已存在id为参数id的数据表单表
     *
     * @param id
     * @return
     */
    @Override
    public boolean isFormExistById(String id) {
        // 查看表是否已存在,若已存在
        List<FormDefinition> forms = this.findDyFormFormDefinitionsById(id);
        if (forms == null || forms.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private String convertDefinition2HbmCfg(String defintionJson, String formType, String name, String pformUuid,
                                            Class clazz, List<String> delFieldNames) {
        FormDefinitionHandler dJson;
        try {
            dJson = new FormDefinitionHandler(defintionJson, formType, name, pformUuid);
        } catch (JSONException e1) {
            logger.error(e1.getMessage(), e1);
            return null;
        }
        Document doc = DocumentHelper.createDocument();

        doc.addDocType("hibernate-mapping", "-//Hibernate/Hibernate Mapping DTD 3.0//EN",
                "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd");

        Element root = doc.addElement("hibernate-mapping");

        Element classEl = root.addElement("class").addAttribute("entity-name", dJson.doGetTblNameOfpForm());
        TenantFacadeService tenantService = ApplicationContextHolder.getBean(TenantFacadeService.class);
        Tenant tenant = tenantService.getById(SpringSecurityUtils.getCurrentTenantId());
        String schema = tenant.getJdbcUsername();
        //数据库类型判断
        if (((SessionFactoryImpl) formDefinitionDao.sessionFactory()).getDialect() instanceof CustomOracle10gDialect) {
            schema = tenant.getJdbcUsername();
        } else if (((SessionFactoryImpl) formDefinitionDao.sessionFactory()).getDialect() instanceof MySQLDialect) {
            schema = tenant.getJdbcDatabaseName();
        }
        classEl.addAttribute("schema", schema);
        Element commentEl = classEl.addElement("comment");
        JSONObject commentJSON = new JSONObject();
        try {
            commentJSON.put("delFieldNames", delFieldNames);
            commentJSON.put("comment", StringUtils.defaultIfEmpty(name, ""));
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        commentEl.setText(commentJSON.toString());

        //        if(name!=null) {
        //            Element tableComment = classEl.addElement("comment");
        //            tableComment.setText(dJson.getName());
        //        }

        // 创建系统字段
        if (clazz == EnumSystemField.class) {
            for (EnumSystemField field : EnumSystemField.values()) {
                if (EnumSystemField.signature_.equals(field)
                        || EnumSystemField.version.equals(field)) {
                    // 废弃字段
                    continue;
                }
                Element sysElement = null;
                sysElement = classEl.addElement(field.getElementType());
                sysElement.addAttribute("name", field.getName());
                sysElement.addAttribute("column", field.getColumn());
                String type = field.getDataType();
                sysElement.addAttribute("type", type);

                if ("id".equals(field.getElementType())) {
                    sysElement.addElement("generator").addAttribute("class", "org.hibernate.id.UUIDGenerator");
                }
                if (type.equals("string")) {// 字段串类型加长度
                    sysElement.addAttribute("length", field.getLength().toString());
                }

            }
        } else if (clazz == EnumRelationTblSystemField.class) {
            for (EnumRelationTblSystemField field : EnumRelationTblSystemField.values()) {
                Element sysElement = null;
                sysElement = classEl.addElement(field.getElementType());
                sysElement.addAttribute("name", field.getName());
                sysElement.addAttribute("column", field.getColumn());
                String type = field.getDataType();
                sysElement.addAttribute("type", type);

                if ("id".equals(field.getElementType())) {
                    sysElement.addElement("generator").addAttribute("class", "org.hibernate.id.UUIDGenerator");
                }
                if (type.equals("string")) {// 字段串类型加长度
                    sysElement.addAttribute("length", field.getLength().toString());
                }

            }
        } else if (clazz == EnumMstTblSystemField.class) {
            for (EnumMstTblSystemField field : EnumMstTblSystemField.values()) {
                Element sysElement = null;
                sysElement = classEl.addElement(field.getElementType());
                sysElement.addAttribute("name", field.getName());
                sysElement.addAttribute("column", field.getColumn());
                String type = field.getDataType();
                sysElement.addAttribute("type", type);

                if ("id".equals(field.getElementType())) {
                    sysElement.addElement("generator").addAttribute("class", "org.hibernate.id.UUIDGenerator");
                }
                if (type.equals("string")) {// 字段串类型加长度
                    sysElement.addAttribute("length", field.getLength().toString());
                }

            }
        }

        // 设置用户自定义字段

        Iterator<String> it = dJson.getFieldNamesOfMaintable().iterator();
        while (it.hasNext()) {

            String fieldName = it.next();

            Element proEl = classEl.addElement("property");

            proEl.addAttribute("name", fieldName);// 设置原来的字段名

            Element colEl = proEl.addElement("column");

            colEl.addAttribute("name", fieldName);

            // 设置字段的类型
            String dataType = dJson.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.dbDataType);
            String type = "";
            if (DbDataType._date.equals(dataType)) {// 日期类型的字段
                type = "java.sql.Timestamp";
            } else if (DbDataType._int.equals(dataType) || DbDataType._int_positive.equals(dataType)
                    || DbDataType._int_negtive.equals(dataType)) {
                type = "int";
            } else if (DbDataType._long.equals(dataType)) {
                // 修正长整型为number(16)（原来会生成19位，前端如果转数字类型会丢失精度）
                type = AbstractTable.MAPPING_TYPE_BIG_DECIMAL;
                colEl.addAttribute("precision", "16");
                colEl.addAttribute("scale", "0");
            } else if (DbDataType._float.equals(dataType)) {
                type = "float";
            } else if (DbDataType._double.equals(dataType)) {
                type = "double";
            } else if (DbDataType._clob.equals(dataType)) {
                type = "clob";
            } else if (DbDataType._number.equals(dataType) || DbDataType._decimal.equals(dataType)) {
                type = AbstractTable.MAPPING_TYPE_BIG_DECIMAL;// "java.math.BigDecimal";
                String precision = dJson.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.precision);
                String scale = dJson.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.scale);
                // 默认位数20
                colEl.addAttribute("precision", StringUtils.isBlank(precision) ? "20" : precision);
                // 默认小数位2
                colEl.addAttribute("scale", StringUtils.isBlank(scale) ? "2" : scale);
            } else {
                type = "string";
                // 设置字段的长度，默认长度为255
                String dataLength = dJson.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.length);
                if (null != dataLength && dataLength.trim().length() > 0) {
                    colEl.addAttribute("length", dataLength);
                } else {
                    colEl.addAttribute("length", "255");
                }
            }

            // 设置原字段名
            proEl.addAttribute("type", type);
            Element columenComment = colEl.addElement("comment");
            JSONObject columnCommentJSON = new JSONObject();
            String oldFieldName = "";// 对于已有表单，该值为原来的字段名,用于修改字段名
            String displayName = "";
            try {
                oldFieldName = dJson.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.oldName);
                displayName = dJson.getFieldPropertyOfStringType(fieldName, EnumFieldPropertyName.displayName);
            } catch (Exception e) {// 如果调用者没传入该值，则直接用现有的name做为旧字段名,当现有的字段和旧字段名一样时，则不执行字段名更新操作
                oldFieldName = fieldName;
                displayName = fieldName;
                logger.error("设置字段变更信息异常：", e);
            }

            try {
                columnCommentJSON.put("oldName", oldFieldName);
                columnCommentJSON.put("comment", displayName);// 显示名称做为字段的备注
            } catch (JSONException e) {
                logger.error(e.getMessage(), e);
            }
            columenComment.setText(columnCommentJSON.toString());

        }
        String xml = XmlConverUtils.formateDocumentOutStrig(doc, true);
        logger.info("表单定义hibernate-mapping xml：{}", xml);
        return xml;

    }

    /**
     * 从configinstance获取的单例
     */

    public void addNewConfig(Configuration cfg) {
        cfg.buildMappings();
    }

    /**
     * @param @return 设定文件
     * @return SessionFactory    返回类型
     * @throws
     * @Title: getSessionFactory
     * @Description: 获取sessionfactory
     */
    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = ApplicationContextHolder.getBean(Config.TENANT_SESSION_FACTORY_BEAN_NAME,
                    SessionFactory.class);
        }
        return sessionFactory;
    }

    @Override
    @Transactional
    public void createFormDefinitionAndFormTable(FormDefinition formDefinition) {

        /*
         * //保存表单数据表定义信息 this.saveFormDefinition(formDefinition);
         *
         * //创建表单数据表结构
         *
         * this.createOrUpdateDyForm(formDefinition.getDefinitionJson(), null);
         */

        this.updateFormDefinitionAndFormTable(formDefinition, new ArrayList<String>());

    }

    @Override
    @Transactional
    public void updateFormDefinitionAndFormTable(FormDefinition formDefinition, List<String> deletedFieldNames) {
        FormDefinitionHandler handler = formDefinition.doGetFormDefinitionHandler();
        handler.validate();
        // 同步id、name、code和tableName
        String id = handler.getFormPropertyOfStringType(EnumFormPropertyName.id);
        if (StringUtils.isBlank(id) && StringUtils.isNotBlank(formDefinition.getId())) {
            handler.addFormProperty(EnumFormPropertyName.id, formDefinition.getId());
            handler.addFormProperty(EnumFormPropertyName.name, formDefinition.getName());
            handler.addFormProperty(EnumFormPropertyName.code, formDefinition.getCode());
            handler.addFormProperty(EnumFormPropertyName.tableName, formDefinition.getTableName());
        }

        // this.removeFieldsAndTheirAssistedFields(formDefinition,
        // deletedFieldNames);//删除辅助性字段
        handler.removeFields(deletedFieldNames);

        if (DyformTypeEnum.isPform(formDefinition.getFormType())) {// 存储单据，要生成表结构
            // dUtils.removeFieldByFieldName("deletedFieldNames");
            // 用户表存储，生成关系表
            FormRepositoryContext repositoryContext = new FormRepositoryContext(handler);
            if (FormRepositoryModeEnum.UserTable.getValue().equals(repositoryContext.getRepositoryMode())) {
                // 保存或者更新表单数据表定义信息
                String relationTblHbmXML = this.createRelationTblHbmXML(formDefinition);// 生成数据关系表
                this.createOrUpdateTbl(formDefinition, relationTblHbmXML);
            } else if (FormRepositoryModeEnum.Dyform.getValue().equals(repositoryContext.getRepositoryMode())) {
                // 保存或者更新表单数据表定义信息
                String relationTblHbmXML = this.createRelationTblHbmXML(formDefinition);// 生成数据关系表

                String tblHbmXML = this.createTblHbmXML(formDefinition, deletedFieldNames);// 生成数据表

                // 创建或者更新表单数据表结构
                // this.createOrUpdateDyFormTbl(formDefinition, deletedFieldNames);
                this.createOrUpdateTbl(formDefinition, tblHbmXML, relationTblHbmXML);
            }
        } else if (DyformTypeEnum.isCform(formDefinition.getFormType())) {
            List<String> cFields = handler.getFieldNamesOfMaintable();
            if (cFields != null && false == cFields.isEmpty()) {
                FormDefinition pFormDefinition = handler.doGetPformFormDefinition();
                // 赋值新表
                if (StringUtils.length(formDefinition.getId()) > 25) {
                    throw new RuntimeException("formDefinition.id不超过25,实际" + StringUtils.length(formDefinition.getId()));
                } else if (false == handler.hasCustomTable()) {
                    formDefinition.setTableName("uf_" + StringUtils.lowerCase(formDefinition.getId()));
                    handler.addFormProperty(EnumFormPropertyName.tableName, formDefinition.getTableName());
                }
                // 忽略父单据
                if (deletedFieldNames != null && false == deletedFieldNames.isEmpty()) {
                    deletedFieldNames.removeAll(pFormDefinition.doGetFormDefinitionHandler().getFieldNamesOfMainform());
                }
                // 保存或者更新表单数据表定义信息
                String relationTblHbmXML = this.createRelationTblHbmXML(formDefinition);// 生成数据关系表
                String tblHbmXML = this.createMstTblHbmXML(formDefinition, deletedFieldNames);// 生成数据表
                // 创建或者更新表单数据表结构
                this.createOrUpdateTbl(formDefinition, tblHbmXML, relationTblHbmXML);
            }
        } else if (DyformTypeEnum.isMSTform(formDefinition.getFormType())) {// 生成依赖关系
            String tblHbmXML = this.createMstTblHbmXML(formDefinition, deletedFieldNames);// 生成数据表
            this.createOrUpdateTbl(formDefinition, tblHbmXML);
        }
        // 对于在新版本中被删除的列，在其他版本的定义也需要同步删除
        try {
            doSynDefinitionWithOtherVersions(formDefinition, deletedFieldNames);
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
            throw new HibernateException(e.getMessage());
        }

        this.saveFormDefinition(formDefinition, deletedFieldNames);

        // 保存表单控件对应的角色信息
        //		this.saveFormControlRole(formDefinition);

        DyformCacheUtils.updateOrAdd(formDefinition);

    }

    /**
     * 删除辅助性字段
     *
     * @param formDefinition
     * @param deletedFieldNames
     */
    private void removeFieldsAndTheirAssistedFields(FormDefinition formDefinition, List<String> deletedFieldNames) {
        FormDefinitionHandler dJson = formDefinition.doGetFormDefinitionHandler();
        List<String> deletedRealDisplayFieldNames = new ArrayList<String>();
        for (String deletedFieldName : deletedFieldNames) {
            if (dJson.isValueAsMap(deletedFieldName)) {
                String realFieldName = dJson.getAssistedFieldNameRealValue(deletedFieldName);
                if (realFieldName == null) {
                    continue;
                }
                String displayFieldName = dJson.getAssistedFieldNameDisplayValue(deletedFieldName);

                deletedRealDisplayFieldNames.add(realFieldName);
                deletedRealDisplayFieldNames.add(displayFieldName);
            }
        }

        deletedFieldNames.addAll(deletedRealDisplayFieldNames);
        formDefinition.doGetFormDefinitionHandler().removeFields(deletedFieldNames);

    }

    @Override
    public void createOrUpdateTbl(FormDefinition formDefinition, String... tblHbmXMLs) {
        // 将定义json转换成hibernate配置文件(也就是hibernate.cfg.xml格式的文件)
        for (String tblHbmXML : tblHbmXMLs) {
            logger.debug("用户配置后生成相应的hbm文件内容(新建):" + tblHbmXML);
            // 根据hibernate配置文件生成或者更新表结构
            CustomConfiguration config = new CustomConfiguration();
            config.addXML(tblHbmXML);
            this.addNewConfig(config);
            TableConfig tableconfig = new TableConfig(config, formDefinition.getUuid());

            tableconfig.updateTable();
        }
    }

    @Override
    public String createTblHbmXML(DyFormFormDefinition formDefinition, List<String> deletedFieldNames) {
        // createAssistedField(formDefinition);//生成辅助性字段
        return convertDefinition2HbmCfg(formDefinition.getDefinitionJson(), formDefinition.getFormType(),
                formDefinition.getName(), formDefinition.getpFormUuid(), EnumSystemField.class, deletedFieldNames);
    }

    @Override
    public String createMstTblHbmXML(DyFormFormDefinition formDefinition, List<String> deletedFieldNames) {
        return convertDefinition2HbmCfg(formDefinition.getDefinitionJson(), formDefinition.getFormType(),
                formDefinition.getName(), formDefinition.getpFormUuid(), EnumMstTblSystemField.class, deletedFieldNames);
    }

    /**
     * 将参数中的表单的JSON信息同步到其他版本的表单定义中
     *
     * @param formDefinition
     * @throws JSONException
     */
    private void doSynDefinitionWithOtherVersions(FormDefinition formDefinition, List<String> delFieldNames)
            throws JSONException {
        if (delFieldNames == null || delFieldNames.size() == 0) {
            logger.debug("没有字段需要删除");
            return;
        }

        List<FormDefinition> formDefinitions = getFormDefinitionsById(formDefinition.getId());

        for (FormDefinition df : formDefinitions) {
            if (df.getUuid().equalsIgnoreCase(formDefinition.getUuid())) {
                continue;
            }

            for (String delFn : delFieldNames) {
                if (df.doGetFormDefinitionHandler().isFieldInDefinition(delFn)) {
                    df.doGetFormDefinitionHandler().removeFieldByFieldName(delFn);
                }
            }
            this.formDefinitionDao.save(df);
            DyformCacheUtils.delete(df);
        }
    }

    @Override
    public List<String> findVFormAndMForm(Map<String, Object> map) {
        String hql = "from FormDefinition where pFormUuid = :pFormUuid";
        if (map.containsKey("formType")) {
            hql = hql + " and formType = :formType";
        }
        List<FormDefinition> pos = this.dao.listByHQL(hql, map);
        List<String> list = new ArrayList<String>();
        for (FormDefinition po : pos) {
            list.add(po.getUuid());
        }
        return list;
    }

    @Override
    public boolean isTableExist(String s) {
        return this.formDefinitionDao.isTableExist(s);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dytable.service.FormDefinitionService#getForPageAsTree(com.wellsoft.pt.common.component.jqgrid.JqGridQueryInfo, com.wellsoft.pt.core.support.QueryInfo)
     */
    @Override
    @Transactional(readOnly = true)
    public QueryData getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo) {
        // 设置查询字段条件
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());

        // 查询父节点为null的部门
        List<QueryItem> results = null;

        // Session session = formDefinitionDao.getSession();
        // System.out.println(sessionFactory + "" + session);
        if (StringUtils.isBlank(jqGridQueryInfo.getNodeid())) {

            results = this.dao.listQueryItemByNameHQLQuery("topDyFormDefinitionTreeQuery4new", values,
                    queryInfo.getPagingInfo());

        } else {
            String uuid = jqGridQueryInfo.getNodeid();
            DyFormFormDefinition parent = this.formDefinitionDao.getOne(uuid);
            values.clear();// 子节点查询清空查询条件
            values.put("parentUuid", uuid);
            values.put("id", parent.getId());
            results = this.formDefinitionDao.listQueryItemByNameHQLQuery("dyFormDefinitionTreeQuery4new", values,
                    queryInfo.getPagingInfo());
        }
        // results = pageData.getResult();
        List<JqTreeGridNode> retResults = new ArrayList<JqTreeGridNode>();

        int level = jqGridQueryInfo.getN_level() == null ? 0 : jqGridQueryInfo.getN_level() + 1;
        String parentId = jqGridQueryInfo.getNodeid() == null ? "null" : jqGridQueryInfo.getNodeid();
        for (int index = 0; index < results.size(); index++) {
            QueryItem item = results.get(index);
            JqTreeGridNode node = new JqTreeGridNode();
            node.setId(item.get("id").toString());// id
            List<Object> cell = node.getCell();
            cell.add(item.get("uuid"));// UUID

            cell.add(item.get("name") + " (" + item.get("name") + ")");// name
            String formType = (String) item.get("formType");
            cell.add(formType);// UUID
            DyformTypeEnum formTypeName = DyformTypeEnum.value2EnumObj(formType);
            cell.add(formTypeName == null ? "" : formTypeName.getRemark());// formTypeName
            cell.add(item.get("code"));// version
            cell.add(item.get("id"));// version
            cell.add(item.get("tableName"));// name
            cell.add(item.get("version"));// version

            // level field
            cell.add(level);
            // parent id field
            cell.add(parentId);
            // leaf field
            if (StringUtils.isBlank(jqGridQueryInfo.getNodeid())) {
                if (Double.valueOf(1).toString().equals(item.get("version"))) {
                    cell.add(true);
                } else {
                    long count = this.formDefinitionDao.countById(item.get("id").toString());
                    cell.add(count <= 1);
                }
            } else {
                cell.add(true);
            }
            // expanded field 第一个节点展开
            if ("null".equals(parentId)) {
                cell.add(true);
            } else {
                cell.add(false);
            }
            retResults.add(node);
        }
        QueryData queryData = new QueryData();
        queryData.setDataList(retResults);
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDefinition> getFormDefinitionsByTblName(String tblName) {
        return formDefinitionDao.listByFieldEqValue("tableName", tblName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDefinition> getFormDefinitionsById(String id) {
        return formDefinitionDao.listByFieldEqValue("id", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDefinition> getVformDefinitionsByPformId(String pFormUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("pFormUuid", pFormUuid);
        String hql = "from FormDefinition t where t.pFormUuid = :pFormUuid and t.formType = 'V' order by t.code desc";
        return formDefinitionDao.listByHQL(hql, params);
        // return formDefinitionDao.listByFieldEqValue("pFormUuid", pFormUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDefinition> getMformDefinitionsByPformId(String pFormUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("pFormUuid", pFormUuid);
        String hql = "from FormDefinition t where t.pFormUuid = :pFormUuid and t.formType = 'M' order by t.code desc";
        return formDefinitionDao.listByHQL(hql, params);
        // return formDefinitionDao.listByFieldEqValue("pFormUuid", pFormUuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDefinition> getAllFormDefintions() {
        List<FormDefinition> dydefs = new ArrayList<FormDefinition>();
        for (FormDefinition dydef : this.formDefinitionDao.getAll()) {
            dydefs.add(dydef);
        }
        return dydefs;
    }

    /**
     * 获取所有的表单定义基本信息
     *
     * @return
     */
    @Override
    public List<DyFormFormDefinition> listDyFormDefinitionBasicInfo() {
        Map<String, Object> values = Maps.newHashMap();
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            values.put("systemIds", Lists.newArrayList(system));
        }
        values.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        List<FormDefinition> dyFormFormDefinitions = formDefinitionDao.listByNameHQLQuery("getDyFormDefinitionBasicInfo", values);
        List<DyFormFormDefinition> list = Lists.newArrayList();
        list.addAll(dyFormFormDefinitions);
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getLatestUpdatedUuids(int count) {
        String hql = "select uuid from FormDefinition where modifyTime is not null order by modifyTime desc";
        return formDefinitionDao.listCharSequenceByHqlAndPage(hql, null, new PagingInfo(1, count));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDefinition> getMaxVersionList() {
        Map<String, Object> params = new HashMap<>();
        params.put("systemUnitIds",
                new String[]{MultiOrgSystemUnit.PT_ID, SpringSecurityUtils.getCurrentUserUnitId()});
        String hql = QUERY_MAX_VERSION_LIST + " and a.system_unit_id in :systemUnitIds ";
        List<FormDefinition> list = this.formDefinitionDao.getSession().createSQLQuery(hql)
                .addEntity(FormDefinition.class).setProperties(params).list();

        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public DyFormFormDefinition getFormDefinitionOfMaxVersionByTblName(String tableName) {
        String hql = QUERY_MAX_VERSION_LIST + " and a.table_name = :tableName";
        SQLQuery sqlQuery = this.formDefinitionDao.getSession().createSQLQuery(hql);
        sqlQuery.setString("tableName", tableName);
        List<FormDefinition> list = sqlQuery.addEntity(FormDefinition.class).list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public String getFormDefinitionUuidOfMaxVersionByTblName(String tableName) {
        String sql = "select a.uuid as uuid from DYFORM_FORM_DEFINITION a,"
                + "(select table_name, min(version) version from DYFORM_FORM_DEFINITION group by table_name) b"
                + " where a.table_name = b.table_name and a.version = b.version" + " and upper(a.table_name) = upper(:tableName)";
        Map<String, Object> param = Maps.newHashMap();
        param.put("tableName", tableName);
        List<QueryItem> list = dao.listQueryItemBySQL(sql, param, null);
        return CollectionUtils.isEmpty(list) ? null : list.get(0).getString("uuid");
    }

    @Override
    @Transactional(readOnly = true)
    public DyFormFormDefinition getFormDefinitionOfMinVersionByTblName(String tableName) {
        String hql = QUERY_MIN_VERSION_LIST + " and upper(a.table_name) = upper(:tableName)";
        SQLQuery sqlQuery = this.formDefinitionDao.getSession().createSQLQuery(hql);
        sqlQuery.setString("tableName", tableName);
        List<FormDefinition> list = sqlQuery.addEntity(FormDefinition.class).list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DyFormFormDefinition getFormDefinition(String tblName, String version) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tableName", tblName);
        params.put("version", version);
        List<FormDefinition> dfs = this.formDefinitionDao.listByHQL(
                "from FormDefinition fd where fd.tableName = :tableName and fd.version = :version", params);
        if (dfs == null || dfs.size() == 0) {
            return null;
        } else {
            return dfs.get(0);
        }

    }

    @Override
    @Transactional
    public void dropForm(String formUuid) {
        FormDefinition dy = this.findDyFormFormDefinitionByFormUuid(formUuid);
        if (DyformTypeEnum.isPform(dy.getFormType()) || DyformTypeEnum.isMSTform(dy.getFormType())) {
            String tblHbmXML = this.convertDefinition2HbmCfg(dy.getDefinitionJson(), dy.getFormType(), dy.getName(),
                    dy.getpFormUuid(), EnumSystemField.class, null);
            CustomConfiguration config = new CustomConfiguration();
            config.addXML(tblHbmXML);
            this.addNewConfig(config);
            TableConfig tableconfig = new TableConfig(config, formUuid);

            String tblHbmXML2 = this.createRelationTblHbmXML(dy);
            CustomConfiguration config2 = new CustomConfiguration();
            config2.addXML(tblHbmXML2);
            this.addNewConfig(config2);
            TableConfig tableconfig2 = new TableConfig(config2, formUuid);

            tableconfig.dropTable();
            tableconfig2.dropTable();

        }
        //		this.formControlRoleService.deleteRoleByFormId(dy.getId());
        this.formDefinitionDao.delete(formUuid);

    }

    /**
     * 判断指定的formUuid在数据表中是否有数据
     *
     * @param formUuid
     * @return
     */
    @Transactional(readOnly = true)
    public boolean existDataInFormByFormUuid(String formUuid) {
        long countOfSpecifiedForm = this.dyFormFacade.countByFormUuid(formUuid);// 查看该版本是否有数据
        if (countOfSpecifiedForm > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Select2QueryData queryByNameAndTableName(String tableName, String searchValue, PagingInfo pagingInfo) {
        List<FormDefinition> forms = this.formDefinitionDao.queryByNameAndTableName(tableName, searchValue, pagingInfo);
        Select2QueryData select2 = new Select2QueryData(pagingInfo);
        for (DyFormFormDefinition fDefinition : forms) {
            select2.addResultData(new Select2DataBean(fDefinition.getUuid(), fDefinition.getName() + "("
                    + fDefinition.getVersion() + ")"));
        }
        return select2;
    }

    @Override
    public List<FormDefinition> getDyFormDefinitionIncludeRefDyFormByModuleId(String moduleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("moduleId", moduleId);
        return this.dao.listByNameHQLQuery("queryModuleDyFormDefinitionIncludeRefDyForm", params);
    }

    @Override
    public List<FormDefinition> getDyFormDefinitionIncludeRefDyFormByPiUuid(String piUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("piUuid", piUuid);
        return this.dao.listByNameHQLQuery("queryModuleDyFormDefinitionIncludeRefDyForm", params);
    }

    public void saveFormLog(FormDefinition formDefinition, String methodName) {
        String interfaceTitle = "";
        JSONObject interfaceTitleJson = new JSONObject();
        try {
            interfaceTitleJson.put("formUuid", formDefinition.getUuid());
            interfaceTitleJson.put("formName", formDefinition.getName());
            // interfaceTitleJson.put("tableName",
            // formDefinition.doGetTblNameOfpForm());
            interfaceTitle = interfaceTitleJson.toString();
        } catch (JSONException e) {
            logger.error(e.getMessage(), e);
        }
        String interfaceId = this.getClass().getName();
        if (StringUtils.isNotBlank(methodName)) {
            interfaceId += "." + methodName;
        }
        mongoLogService.save(ModuleID.DYTABLE, interfaceId, interfaceTitle, formDefinition.getDefinitionJson());
    }

    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo queryInfo) {
        List<FormDefinition> forms = formDefinitionDao.queryByName(queryInfo.getSearchValue(),
                queryInfo.getPagingInfo());
        Select2QueryData select2 = new Select2QueryData(queryInfo.getPagingInfo());
        for (FormDefinition fDefinition : forms) {
            select2.addResultData(new Select2DataBean(fDefinition.getUuid(), fDefinition.getName() + "("
                    + fDefinition.getVersion() + ")"));
        }
        return select2;
    }

    @Override
    @Transactional(readOnly = true)
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo queryInfo) {
        Select2QueryData select2 = new Select2QueryData();
        if (queryInfo.getIds()[0].length() > 0) {
            FormDefinition fDefinition = formDefinitionDao.getOne(queryInfo.getIds()[0]);
            select2.addResultData(new Select2DataBean(fDefinition.getUuid(), fDefinition.getName() + "("
                    + fDefinition.getVersion() + ")"));
        }
        return select2;
    }

    @Override
    @Transactional(readOnly = true)
    public String queryDbCharacterSet() {
        return this.formDefinitionDao.queryDbCharacterSet();
    }


    @Override
    public Select2QueryData queryByNameOrIdOrTableName(String searchValue, PagingInfo pagingInfo,
                                                       List<String> excludeUuids, List<String> systemUnitIds) {

        List<FormDefinition> forms = this.formDefinitionDao.queryByNameOrIdOrTableName(searchValue, pagingInfo,
                excludeUuids, systemUnitIds);
        Select2QueryData select2 = new Select2QueryData(pagingInfo);
        for (DyFormFormDefinition fDefinition : forms) {
            select2.addResultData(new Select2DataBean(fDefinition.getUuid(), fDefinition.getName() + "("
                    + fDefinition.getVersion() + ")"));
        }
        return select2;

    }


    @Override
    public Select2QueryData queryByNameOrIdOrTableName(String searchValue, PagingInfo pagingInfo,
                                                       List<String> excludeUuids, List<String> formTypes, List<String> systemUnitIds) {

        List<FormDefinition> forms = this.formDefinitionDao.queryByNameOrIdOrTableName(searchValue, pagingInfo,
                excludeUuids, systemUnitIds);
        Select2QueryData select2 = new Select2QueryData(pagingInfo);
        for (DyFormFormDefinition fDefinition : forms) {
            select2.addResultData(new Select2DataBean(fDefinition.getUuid(), fDefinition.getName() + "("
                    + fDefinition.getVersion() + ")"));
        }
        return select2;

    }

    @Override
    public Select2QueryData queryFormDefinitionSelect(Select2QueryInfo queryInfo) {
        Select2QueryData selection = new Select2QueryData();
        Map<String, Object> param = Maps.newHashMap();
        if (StringUtils.isNotBlank(queryInfo.getSearchValue())) {
            param.put("value", "%" + queryInfo.getSearchValue() + "%");
        }
        if (StringUtils.isNotBlank(queryInfo.getOtherParams("includeTypes"))) {
            param.put("includeTypes", queryInfo.getOtherParams("includeTypes").split(",|;"));
        }
        if (StringUtils.isNotBlank(queryInfo.getOtherParams("excludeTypes"))) {
            param.put("excludeTypes", queryInfo.getOtherParams("excludeTypes").split(",|;"));
        }
        if (StringUtils.isNotBlank(queryInfo.getOtherParams("tableName"))) {
            param.put("tableName", queryInfo.getOtherParams("tableName"));
        }
        List<QueryItem> forms = this.formDefinitionDao.queryFormDefinitionSelect(param);
        for (QueryItem fDefinition : forms) {
            selection.addResultData(new Select2DataBean(fDefinition.getString("uuid"), fDefinition.getString("name") + "("
                    + fDefinition.getString("version") + ")"));
        }
        return selection;
    }

    @Override
    public Select2QueryData queryTypeByNameOrIdOrTableName(String pFormUuid, PagingInfo pagingInfo, String searchValue,
                                                           String... fromType) {
        List<FormDefinition> forms = this.formDefinitionDao.queryTypeByNameOrIdOrTableName(pFormUuid, pagingInfo,
                searchValue, fromType);
        Select2QueryData select2 = new Select2QueryData(pagingInfo);
        for (DyFormFormDefinition fDefinition : forms) {
            select2.addResultData(new Select2DataBean(fDefinition.getUuid(), fDefinition.getName() + "("
                    + fDefinition.getVersion() + ")"));
        }
        return select2;
    }

    @Override
    public Select2QueryData queryTypeByNameOrIdOrTableName(PagingInfo pagingInfo, String searchValue,
                                                           String... fromType) {
        return queryTypeByNameOrIdOrTableName(null, pagingInfo, searchValue, fromType);
    }

    /**
     * 返回表单所有的依赖关系
     * 如何描述该方法
     *
     * @param dyFormDefinition
     * @return Map<" ", List < String类型UUID集合>>
     */
    @Override
    public Map<String, Set<String>> getResource(FormDefinition dyFormDefinition) {
        Map<String, Set<String>> resultMap = new HashMap<String, Set<String>>();
        FormDefinitionHandler dyFormDefinitionJSON = dyFormDefinition.doGetFormDefinitionHandler();
        Set<String> dictCodes = new HashSet<String>();// 数据字典
        Set<String> dictCodeParent = new HashSet<String>();// 数据字典
        Set<String> formDefinitions = new HashSet<String>();// 表单
        Set<String> dataStoreIds = new HashSet<String>();// 数据仓库
        Set<String> viewStoreIds = new HashSet<String>();// 视图
        // 取出表单从表信息
        List<String> subformList = dyFormDefinitionJSON.getFormUuidsOfSubform();
        if (subformList != null && subformList.isEmpty() == false) {
            for (String subform : subformList) {
                formDefinitions.add(subform);
            }
        }
        // 遍历字段取出数据字典应用信息,数据源
        for (String fieldName : dyFormDefinitionJSON.getFieldNamesOfMainform()) {
            String inputMode = dyFormDefinitionJSON.getInputMode(fieldName);
            String dictCode = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                    EnumFieldPropertyName.dictCode);
            String dataStoreId = null, viewStoreId = null;
            if (DyFormConfig.INPUTMODE_CHECKBOX.equals(inputMode) || DyFormConfig.INPUTMODE_RADIO.equals(inputMode)
                    || DyFormConfig.INPUTMODE_SELECTMUTILFASE.equals(inputMode)
                    || DyFormConfig.INPUTMODE_COMBOSELECT.equals(inputMode)) {
                dataStoreId = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                        EnumFieldPropertyName.dataSourceId);
            } else if (DyFormConfig.INPUTMODE_DIALOG.equals(inputMode)) {
                //				JSONObject fieldDefinitionJson = dyFormDefinitionJSON.getFieldDefinitionJson(fieldName);
                //				try {
                String relativeMethod = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                        DyFormConfig.DYRELATIVEMETHOD_FIELD);
                //					if (fieldDefinitionJson.has(DyFormConfig.DYRELATIVEMETHOD_FIELD)) {
                //					relativeMethod = (String) fieldDefinitionJson.get(DyFormConfig.DYRELATIVEMETHOD_FIELD);
                //					}
                if (StringUtils.equals(DyFormConfig.DYRELATIVEMETHOD_DIALOG, relativeMethod)) {
                    viewStoreId = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                            EnumFieldPropertyName.relationDataValueTwo);
                } else if (StringUtils.equals(DyFormConfig.DYRELATIVEMETHOD_SEARCH, relativeMethod)) {
                    dataStoreId = dyFormDefinitionJSON.getFieldPropertyOfStringType(fieldName,
                            EnumFieldPropertyName.relationDataValueTwo);
                }
                //				} catch (JSONException ex) {
                //					logger.error(ex.getMessage(), ex);
                //				}
            }
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
            // 视图
            if (StringUtils.isNotBlank(viewStoreId)) {
                viewStoreIds.add(viewStoreId);
            }
            // 数据源
            if (StringUtils.isNotBlank(dataStoreId)) {
                CdDataStoreDefinition cdDataStoreDefinition = cdDataStoreDefinitionService.getBeanById(dataStoreId);
                if (cdDataStoreDefinition != null && StringUtils.isNotBlank(cdDataStoreDefinition.getUuid())) {
                    dataStoreIds.add(cdDataStoreDefinition.getUuid());
                }
            }
        }
        if (StringUtils.equals(DyformTypeEnum.P.getValue(), dyFormDefinition.getFormType())) {
            List<FormDefinition> vFormDefinitions = null;
            for (DyformTypeEnum dyformType : new DyformTypeEnum[]{DyformTypeEnum.V, DyformTypeEnum.M}) {
                vFormDefinitions = dao.getFormDefinitionsByPformUuidAndFormType(dyFormDefinition.getUuid(), dyformType);
                if (vFormDefinitions != null && vFormDefinitions.isEmpty() == false) {
                    for (FormDefinition subform : vFormDefinitions) {
                        formDefinitions.add(subform.getUuid());
                    }
                }
            }
            formDefinitions.addAll(dyFormDefinition.doGetFormDefinitionHandler().getFormUuidsOfTemplate());
        } else if (StringUtils.equals(DyformTypeEnum.V.getValue(), dyFormDefinition.getFormType())) {
            String pFormUuid = dyFormDefinition.getpFormUuid();
            if (StringUtils.isNotBlank(pFormUuid)) {
                formDefinitions.add(pFormUuid);
            }
        } else if (StringUtils.equals(DyformTypeEnum.M.getValue(), dyFormDefinition.getFormType())) {
            String pFormUuid = dyFormDefinition.getpFormUuid();
            if (StringUtils.isNotBlank(pFormUuid)) {
                formDefinitions.add(pFormUuid);
            }
        }
        // 加入表单定义
        resultMap.put(IexportType.FormDefinition, formDefinitions);
        // 加入数据源
        resultMap.put(IexportType.DataStoreDefinition, dataStoreIds);
        // 加入视图
        resultMap.put(IexportType.AppWidgetDefinition, viewStoreIds);
        // 加入数据字典
        resultMap.put(IexportType.DataDictionaryParent, dictCodes);
        resultMap.put(IexportType.DataDictionary, dictCodes);
        return resultMap;
    }

    @Override
    public List<FormDefinition> getAllFormDefinitionBySystemUnitId(String systemUnitId) {

        return this.dao.getAllFormDefinitionBySystemUnitId(systemUnitId);
    }

    @Override
    public List<TreeNode> getSubformApplyToRootDicts(String uuid) {
        return getApplyToDicts(uuid, EnumDyformApplyToDictionary.DY_FORM_SUBFORM_MAPPING);
    }

    @Override
    public List<TreeNode> getFormIdApplyToRootDicts(String uuid) {
        return getApplyToDicts(uuid, EnumDyformApplyToDictionary.DY_FORM_ID_MAPPING);
    }

    @Override
    public List<TreeNode> getFormFieldApplyToRootDicts(String uuid) {
        return getApplyToDicts(uuid, EnumDyformApplyToDictionary.DY_FORM_FIELD_MAPPING);
    }

    private List<TreeNode> getApplyToDicts(String uuid, EnumDyformApplyToDictionary applyTo) {
        String rootUuid = uuid;
        if (TreeNode.ROOT_ID.equals(uuid)) {
            DataDictionary dataDictionary = dataDictionaryService.getByType(applyTo.getType());
            if (dataDictionary != null) {
                rootUuid = dataDictionary.getUuid();
            } else {
                creatDictByType(applyTo);

                TreeNode node = new TreeNode();
                return node.getChildren();
            }
        }
        return dataDictionaryService.getAsTreeAsync(rootUuid);
    }

    private void creatDictByType(EnumDyformApplyToDictionary applyTo) {
        DataDictionaryBean newDataDicBean = new DataDictionaryBean();

        newDataDicBean.setName(applyTo.getName());
        newDataDicBean.setType(applyTo.getType());
        newDataDicBean.setCode(applyTo.getCode());
        newDataDicBean.setParentUuid(getDyFormDictUuid());

        dataDictionaryService.saveBean(newDataDicBean);
    }

    private String getDyFormDictUuid() {
        List<DataDictionary> dataDictionarys = dataDictionaryService.getDataDictionaries(ROOT_DICT_TYPE,
                EnumDyformApplyToDictionary.DY_FORM.getCode());
        if (CollectionUtils.isEmpty(dataDictionarys)) {
            DataDictionaryBean newDataDicBean = new DataDictionaryBean();

            newDataDicBean.setName(EnumDyformApplyToDictionary.DY_FORM.getName());
            newDataDicBean.setType(EnumDyformApplyToDictionary.DY_FORM.getType());
            newDataDicBean.setCode(EnumDyformApplyToDictionary.DY_FORM.getCode());
            newDataDicBean.setParentUuid(dataDictionaryService.getByType(ROOT_DICT_TYPE).getUuid());

            dataDictionaryService.saveBean(newDataDicBean);
            return dataDictionaryService.getByType(EnumDyformApplyToDictionary.DY_FORM.getType()).getUuid();
        } else {
            return dataDictionarys.get(0).getUuid();
        }
    }

    @Override
    public Select2QueryData querySelectDataFromFormDefinition(Select2QueryInfo select2QueryInfo) {

        String queryValue = select2QueryInfo.getSearchValue();

        List<FormDefinition> forms = null;
        if (StringUtils.isBlank(queryValue)) {
            forms = this.listAll();
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", "%" + queryValue + "%");
            forms = this.listByHQL("from FormDefinition where name like :name", map);
        }

        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        for (FormDefinition form : forms) {
            beans.add(new Select2DataBean(form.getUuid(), form.getName()));
        }
        return new Select2QueryData(beans);

    }

    @Override
    public Select2QueryData loadSelectDataFromFormDefinition(Select2QueryInfo select2QueryInfo) {
        Map<String, Object> params = Maps.newHashMap();
        String moduleId = select2QueryInfo.getOtherParams("moduleId");
        String idProperty = select2QueryInfo.getOtherParams("idProperty", "id");
        String excludeModuleIds = select2QueryInfo.getOtherParams("excludeModuleIds");
        if (StringUtils.isNotBlank(moduleId)) {
            params.put("moduleId", moduleId);
        }
        if (StringUtils.isNotBlank(excludeModuleIds)) {
            params.put("excludeModuleIds", Arrays.asList(excludeModuleIds.split(Separator.SEMICOLON.getValue())));
        }
        String systemUnitId = select2QueryInfo.getOtherParams("systemUnitId");
        if (StringUtils.isNotBlank(systemUnitId)) {
            params.put("systemUnitId", systemUnitId);
        }
        List<FormDefinition> formDefinitions = formDefinitionDao.listByNameHQLQuery("queryAllFormDefinition", params);
        return new Select2QueryData(formDefinitions, idProperty, "name");
    }

    @Override
    @Transactional(readOnly = true)
    public DyFormFormDefinition getFormDefinitionOfMaxVersionById(String id) {
        String hql = QUERY_MAX_VERSION_LIST + " and a.id = :id";
        SQLQuery sqlQuery = this.formDefinitionDao.getSession().createSQLQuery(hql);
        sqlQuery.setString("id", id);
        List<FormDefinition> list = sqlQuery.addEntity(FormDefinition.class).list();
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.facade.service.DyFormFacade#queryAllCustomFormRepositories(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData queryAllCustomFormRepositories(Select2QueryInfo queryInfo) {
        Select2QueryData select2 = new Select2QueryData(queryInfo.getPagingInfo());
        Map<String, CustomFormRepository> customFormRepositoryMap = ApplicationContextHolder.getApplicationContext()
                .getBeansOfType(CustomFormRepository.class);
        for (Entry<String, CustomFormRepository> entry : customFormRepositoryMap.entrySet()) {
            select2.addResultData(new Select2DataBean(entry.getKey(), entry.getValue().getName()));
        }
        return select2;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService#getCandidateFormFields(java.lang.String)
     */
    @Override
    public List<FormField> getCandidateFormFields(String formUuid) {
        FormDataServiceAdapter formDataServiceAdapter = ApplicationContextHolder.getBean(FormDataServiceAdapter.class);
        List<FormField> candidateFormFields = Lists.newArrayList();
        List<FormField> formFields = formDataServiceAdapter.getFormFields(formUuid);
        if (CollectionUtils.isEmpty(formFields)) {
            return Collections.emptyList();
        }
        for (FormField formField : formFields) {
            if (isSystemField(formField)) {
                continue;
            }
            candidateFormFields.add(formField);
        }
        return candidateFormFields;
    }

    /**
     * @param formField
     * @return
     */
    private boolean isSystemField(FormField formField) {
        EnumSystemField[] values = EnumSystemField.values();
        String fieldName = formField.getName();
        for (EnumSystemField enumSystemField : values) {
            if (StringUtils.equalsIgnoreCase(enumSystemField.name(), fieldName)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public List<String> getDiffFieldNamesByExtFormUuid(String formUuid, boolean update) {
        List<String> result = Lists.newArrayList();
        FormDefinition extFormDefinition, pFormDefinition;
        if (StringUtils.isNotBlank(formUuid) && (extFormDefinition = getOne(formUuid)) != null
                && StringUtils.isNotBlank(extFormDefinition.getpFormUuid())) {
            //
            String pFormUuid = extFormDefinition.getpFormUuid();
            Map<String, Object> params = Maps.newHashMap();
            params.put("pFormUuid", pFormUuid);
            String pHql = "from FormDefinition t where exists (select 1 from FormDefinition tt,FormDefinition ttt where tt.id = ttt.id and ttt.uuid = t.uuid and tt.uuid = :pFormUuid) order by t.version desc";
            List<FormDefinition> pLists = listByHQLAndPage(pHql, params, new PagingInfo(0, 1, false));
            if (pLists != null && false == pLists.isEmpty()) {
                // 修正pFormUuid
                pFormUuid = pLists.get(0).getUuid();
            }
            params.put("pModifyTime", extFormDefinition.getModifyTime());
            String hql = "from FormDefinitionLog t where exists (select 1 from FormDefinition tt,FormDefinition ttt where tt.id = ttt.id and ttt.uuid = t.formUuid and tt.uuid = :pFormUuid) and t.createTime > :pModifyTime order by t.createTime asc";
            List<FormDefinitionLog> lists = formDefinitionLogDao.listByHQLAndPage(hql, params, new PagingInfo(0, 1,
                    false));
            // 最旧与最新比较
            if (lists != null && false == lists.isEmpty()) {
                pFormDefinition = getOne(pFormUuid);
                FormDefinitionHandler pUtil = pFormDefinition.doGetFormDefinitionHandler();
                FormDefinitionHandler hisUtil = lists.get(0).doGetFormDefinitionHandler();
                JSONObject pFieldDefinitions = pUtil.getFieldDefinitions();
                JSONObject extFieldDefinitions = hisUtil.getFieldDefinitions();
                if (false == JSONObject.NULL.equals(pFieldDefinitions)
                        && false == JSONObject.NULL.equals(extFieldDefinitions)) {
                    for (String fieldName : (Set<String>) pFieldDefinitions.keySet()) {
                        if (diffFieldObject(pFieldDefinitions, extFieldDefinitions, fieldName)) {
                            result.add(fieldName);
                        }
                    }
                }
                pFieldDefinitions = pUtil.getSubformDefinitions();
                extFieldDefinitions = hisUtil.getSubformDefinitions();
                if (false == JSONObject.NULL.equals(pFieldDefinitions)
                        && false == JSONObject.NULL.equals(extFieldDefinitions)) {
                    for (String fieldName : (Set<String>) pFieldDefinitions.keySet()) {
                        if (diffFieldObject(pFieldDefinitions, extFieldDefinitions, fieldName)) {
                            result.add(fieldName);
                        }
                    }
                }
                //				result = pUtil.getFieldNamesOfMainform();
                //				result.removeAll(extUtil.getFieldNamesOfMainform());
                if (update && false == result.isEmpty()) {
                    save(extFormDefinition);// 更新时间,下一次比较忽略
                }
            }
        }
        return result;
    }

    /**
     * 字段属性比较
     *
     * @param result
     * @param pFieldDefinitions
     * @param extFieldDefinitions
     * @param fieldName
     */
    private boolean diffFieldObject(JSONObject pFieldDefinitions, JSONObject extFieldDefinitions, String fieldName) {
        try {
            JSONObject pField = pFieldDefinitions.getJSONObject(fieldName);
            JSONObject extField = extFieldDefinitions.optJSONObject(fieldName);
            return extField == null || false == StringUtils.equals(pField.toString(), extField.toString());
        } catch (JSONException ex) {
            logger.info(ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public String getMaxExtFormUuidByFormUuid(String pFormUuid, String formType) {
        String result = null;
        Map<String, Object> params = Maps.newHashMap();
        params.put("formType", formType);
        params.put("pFormUuid", pFormUuid);
        String hql = "from FormDefinition t where t.formType=:formType and exists (select 1 from FormDefinition tt,FormDefinition ttt where tt.id = ttt.id and ttt.uuid = t.pFormUuid and tt.uuid = :pFormUuid) order by t.version desc";
        List<FormDefinition> lists = listByHQLAndPage(hql, params, new PagingInfo(0, 1, false));
        if (lists != null && false == lists.isEmpty()) {
            result = lists.get(0).getUuid();
        }
        return result;
    }

    @Override
    public long countBySystemUnitId(String systemUnitId, String formType) {
        if (systemUnitId.equals(MultiOrgSystemUnit.PT_ID)) {
            Map<String, Object> params = new HashMap<>();
            StringBuilder hql = new StringBuilder("select count(uuid) from FormDefinition where ");
            if (StringUtils.isNotBlank(formType)) {
                hql.append(" formType=:formType");
                params.put("formType", formType);
            } else {
                hql.append(" 1 = 1");
            }
            long count = this.dao.countByHQL(hql.toString(), params);
            return count;
        } else {
            Map<String, Object> params = new HashMap<>();
            List<String> systemUnitIdList = Lists.<String>newArrayList(MultiOrgSystemUnit.PT_ID, systemUnitId);
            params.put("systemUnitId", systemUnitIdList);
            StringBuilder hql = new StringBuilder(
                    "select count(uuid) from FormDefinition where systemUnitId in (:systemUnitId)");
            if (StringUtils.isNotBlank(formType)) {
                hql.append(" and formType=:formType");
                params.put("formType", formType);
            }
            long count = this.dao.countByHQL(hql.toString(), params);
            return count;
        }
    }

    @Override
    public Map<String, Collection<String>> updateBusinessCategoryOrgEntityId(Set<String> idSet) {
        List<FormDefinition> definitionList = this.dao.listByHQL(
                "from FormDefinition where definitionJson like '%BusinessBook%'", null);
        //key:tableName val:包含BusinessBook 的 fieldName
        Multimap<String, String> tabMap = HashMultimap.create();
        Set<String> inputModeSet = new HashSet<>();
        for (EnumOrgInputMode value : EnumOrgInputMode.values()) {
            inputModeSet.add(value.getValue());
        }
        for (FormDefinition formDefinition : definitionList) {
            if (StringUtils.isBlank(formDefinition.getTableName())) {
                continue;
            }
            FormDefinitionHandler handler = formDefinition.doGetFormDefinitionHandler();
            handler.validate();
            List<DyformFieldDefinition> fieldDefinitionList = formDefinition.doGetFieldDefintions();
            for (DyformFieldDefinition dyformFieldDefinition : fieldDefinitionList) {
                if (inputModeSet.contains(dyformFieldDefinition.getInputMode())) {
                    String fieldName = dyformFieldDefinition.getFieldName();
                    tabMap.put(formDefinition.getTableName(), fieldName);
                    String real = handler.getAssistedFieldNameRealValue(fieldName);
                    if (StringUtils.isNotBlank(real)) {
                        tabMap.put(formDefinition.getTableName(), real);
                    }
                }
            }
        }
        Map<String, Collection<String>> errMap = new HashMap<>();
        Multimap<String, Object[]> tabValMap = HashMultimap.create();
        for (String tableName : tabMap.keySet()) {
            StringBuilder sql = new StringBuilder("select uuid,");
            sql.append(StringUtils.join(tabMap.get(tableName), ","));
            sql.append(" from ").append(tableName).append(" where ");
            for (String fieldName : tabMap.get(tableName)) {
                for (String id : idSet) {
                    sql.append(fieldName).append(" like '%").append(id).append("%' or ");
                }
            }
            sql.delete(sql.length() - 3, sql.length());
            SQLQuery sqlQuery = this.dao.getSession().createSQLQuery(sql.toString());
            try {
                List<Object[]> list = sqlQuery.list();
                if (list.size() > 0) {
                    tabValMap.putAll(tableName, list);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                errMap.put(tableName, tabMap.get(tableName));
            }
        }

        if (tabValMap.size() > 0) {
            Map<String, String> newIdMap = new HashMap<>();
            for (String id : idSet) {
                newIdMap.put(id, id.replaceFirst(IdPrefix.EXTERNAL.getValue(), IdPrefix.CATEGORY.getValue()));
            }
            for (String tableName : tabValMap.keySet()) {
                for (Object[] value : tabValMap.get(tableName)) {
                    boolean flg = false;
                    for (int i = 1; i < value.length; i++) {
                        Object object = value[i];
                        if (object != null) {
                            for (String id : idSet) {
                                if (object.toString().indexOf(id) > -1) {
                                    object = object.toString().replace(id, newIdMap.get(id));
                                    value[i] = object;
                                    flg = true;
                                }
                            }
                        }
                    }
                    if (flg) {
                        //更新
                        StringBuilder sql = new StringBuilder("update ");
                        sql.append(tableName).append(" set ");
                        int i = 1;
                        for (String fieldName : tabMap.get(tableName)) {
                            if (value[i] != null) {
                                sql.append(fieldName).append(" = '").append(value[i]).append("',");
                            }
                            i++;
                        }
                        sql.deleteCharAt(sql.length() - 1);
                        sql.append(" where ").append("uuid = '").append(value[0]).append("'");
                        SQLQuery sqlQuery = this.dao.getSession().createSQLQuery(sql.toString());
                        sqlQuery.executeUpdate();
                    }
                }
            }
        }
        return errMap;
    }

    @Override
    public List<FormDefinition> listByModuleId(String moduleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("moduleId", moduleId);
        return this.dao.listBySQL("select uuid,name,id,version,form_type,module_id,table_name from DYFORM_FORM_DEFINITION " + (StringUtils.isNotBlank(moduleId) ? "where module_id=:moduleId" : ""), params);
    }

    @Override
    public List<FormDefinition> listByIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }

        String sql = "select a.* from DYFORM_FORM_DEFINITION a,"
                + "(select id, max(version) version from DYFORM_FORM_DEFINITION group by id) b"
                + " where a.id = b.id and a.version = b.version and a.id in(:ids) order by a.code asc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("ids", ids);
        return this.dao.listBySQL(sql, params);
    }

    @Override
    public FormDefinition getLatestUuidAndVersion(String id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        List<FormDefinition> list = this.dao.listBySQL("select uuid ,version  from DYFORM_FORM_DEFINITION where id =:id and version = (select max(t.version) from DYFORM_FORM_DEFINITION t where t.id=:id)", params);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<FormDefinition> getFormDefinitionVersions(String id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);
        List<FormDefinition> list = this.dao.listBySQL("select uuid ,version,name ,create_time ,creator from DYFORM_FORM_DEFINITION where id =:id ", params);
        return list;
    }

    @Override
    public List<FormDefinition> listRecentVersionFormByModuleId(String moduleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("moduleId", moduleId);
        return this.dao.listBySQL("select a.uuid,name,a.id,a.version,a.form_type,a.module_id,a.table_name from DYFORM_FORM_DEFINITION a where a.module_id=:moduleId and a.version = (" +
                "select max(m.version) from DYFORM_FORM_DEFINITION m where m.module_id=:moduleId and m.id=a.id" +
                ")", params);

    }

    @Override
    public List<FormDefinition> getModuleFormDefinitionWithoutJson(String moduleId, Boolean vJson) {
        Map<String, Object> params = Maps.newHashMap();
        List<String> where = Lists.newArrayList();
        if (StringUtils.isNotBlank(moduleId)) {
            params.put("moduleId", moduleId);
            where.add(" module_id =:moduleId");
        }
        if (BooleanUtils.isTrue(vJson)) {
            where.add(" definition_vjson is not null");
        }
        return this.dao.listBySQL("select uuid,name,id,version,form_type,module_id,table_name from DYFORM_FORM_DEFINITION " + (where.isEmpty() ? "" : " where " + StringUtils.join(where, " and ")), params);

    }

    @Override
    public List<FormDefinition> queryFormDefinitionIgnoreJsonByModuleIds(List<String> moduleIds) {
        Map<String, Object> params = Maps.newHashMap();
        List<String> where = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(moduleIds)) {
            params.put("moduleIds", moduleIds);
            where.add(" module_id  in (:moduleIds)");
        }
        return this.dao.listBySQL("select uuid,name,id,version,form_type,module_id,table_name from DYFORM_FORM_DEFINITION " + (where.isEmpty() ? "" : " where " + StringUtils.join(where, " and ")), params);
    }

    @Override
    public List<FormDefinition> queryMaxVersionFormDefinitionIgnoreJsonByModuleIds(List<String> moduleIds) {
        Map<String, Object> params = Maps.newHashMap();
        List<String> where = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(moduleIds)) {
            params.put("moduleIds", moduleIds);
            where.add(" d.module_id  in (:moduleIds) ");
        }

        return this.dao.listBySQL("select d.uuid,d.name,d.id,d.version,d.form_type,d.module_id,d.table_name from DYFORM_FORM_DEFINITION d " +
                " where d.version =  ( select max(a.version) from  DYFORM_FORM_DEFINITION a where a.id = d.id ) )" +
                " " + (where.isEmpty() ? "" : " " + StringUtils.join(where, " and ")), params);

    }

    @Override
    public List<FormDefinition> queryFormDefinitionByModuleIds(List<String> moduleIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("moduleIds", moduleIds);
        return this.dao.listByHQL("from FormDefinition where moduleId in :moduleIds", params);
    }

    @Override
    public List<FormDefinition> queryFormDefinitionNoJsonByModuleIds(List<String> moduleIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("moduleId", moduleIds);
        return this.dao.listBySQL("select uuid,name,id,version,form_type,module_id,table_name from DYFORM_FORM_DEFINITION  WHERE module_id in (:moduleId)", params);
    }

    @Override
    public List<QueryItem> queryFormDefinitionIgnoreJsonByKeyword(String keyword, Integer pageSize, Integer pageIndex) {
        Map<String, Object> params = Maps.newHashMap();
        StringBuilder where = new StringBuilder(" where d.version =  ( select max(a.version) from  DYFORM_FORM_DEFINITION a where a.id = d.id ) ");
        if (StringUtils.isNotBlank(keyword)) {
            params.put("keyword", "%" + keyword + "%");
            where.append(" and (");
            where.append(" name like :keyword or");
            where.append(" id like :keyword  )");
        }
        return this.dao.listQueryItemBySQL("select uuid,name,id,version,form_type,module_id,table_name from DYFORM_FORM_DEFINITION d " +
                "" + where + " order by modify_time desc ", params, pageSize != null && pageIndex != null ?
                new PagingInfo(pageIndex, pageSize, false) : null);
    }

    @Override
    public List<DyFormFormDefinition> listByUuidsWithoutJson(List<String> formUuids) {
        List<DyFormFormDefinition> list = Lists.newArrayList();
        Map<String, Object> params = Maps.newHashMap();
        ListUtils.handleSubList(formUuids, 1000, uuids -> {
            params.put("uuids", uuids);
            List<FormDefinition> dyFormFormDefinitions = formDefinitionDao.listByNameHQLQuery("getDyFormDefinitionBasicInfo", params);
            list.addAll(dyFormFormDefinitions);
        });
        return list;
    }

    @Override
    public List<FormDefinition> queryFormDefinitionIgnoreJsonByTableName(String tableName) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("tableName", tableName);
        return this.dao.listBySQL("select uuid,name,id,version,form_type,module_id,table_name from DYFORM_FORM_DEFINITION  WHERE table_name = :tableName", params);
    }

    /**
     * 级联数据源
     *
     * @param dataSourcesJson
     * @param params
     * @throws ParseException
     * @throws JSONException
     */
    public List<TreeNode> chainedDataSource(String dataSourcesJson, Map<String, Object> params) throws ParseException,
            JSONException {
        JSONArray dataSources = new JSONArray(dataSourcesJson);
        return FormDefinitionHandler.chainedDataSource(dataSources, params, cdDataStoreService);
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
    public List<TreeNode> chainedDataDict(String type, String dicTitles) {
        return FormDefinitionHandler.chainedDataDict(type, dicTitles, dataDictionaryService);
    }
}
