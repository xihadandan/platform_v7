/*
 * @(#)2013-1-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.datadict.facade.service.CdDataDictionaryFacadeService;
import com.wellsoft.pt.basicdata.dyview.service.ViewDefinitionService;
import com.wellsoft.pt.basicdata.excelexporttemplate.bean.ExcelExportDefinitionBean;
import com.wellsoft.pt.basicdata.excelexporttemplate.entity.ExcelExportColumnDefinition;
import com.wellsoft.pt.basicdata.excelexporttemplate.entity.ExcelExportDefinition;
import com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportRuleService;
import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelImportRule;
import com.wellsoft.pt.basicdata.exceltemplate.service.ExcelImportRuleService;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.service.PrintTemplateService;
import com.wellsoft.pt.basicdata.printtemplate.support.utils.PoiTlUtils;
import com.wellsoft.pt.basicdata.script.facade.service.CdScriptDefinitionFacadeService;
import com.wellsoft.pt.basicdata.script.support.ScriptDefinition;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberDefinitionDto;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumber;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SnSerialNumberDefinitionFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SnSerialNumberFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.service.SerialNumberService;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberBuildParams;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberInfo;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableRelationship;
import com.wellsoft.pt.basicdata.systemtable.service.SystemTableAttributeService;
import com.wellsoft.pt.basicdata.systemtable.service.SystemTableService;
import com.wellsoft.pt.basicdata.view.bean.ViewDefinitionNewBean;
import com.wellsoft.pt.basicdata.view.entity.ColumnDefinitionNew;
import com.wellsoft.pt.basicdata.view.entity.PageDefinitionNew;
import com.wellsoft.pt.basicdata.view.service.ViewDefinitionNewService;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.basicdata.workhour.service.WorkHourService;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.basicdata.workhour.support.WorkingHour;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.service.CommonUnitService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-27.1	zhulh		2013-1-27		Create
 * </pre>
 * @date 2013-1-27
 */
@Service(value = "basicDataApiFacade")
public class BasicDataApiFacadeImpl extends AbstractApiFacade implements BasicDataApiFacade {

    @Autowired
    private CommonUnitService commonUnitService;
    @Autowired
    private CdDataDictionaryFacadeService dataDictionaryService;

    @Autowired
    private SerialNumberService serialNumberService;

    @Autowired
    private SnSerialNumberFacadeService snSerialNumberFacadeService;

    @Autowired
    private SnSerialNumberDefinitionFacadeService snSerialNumberDefinitionFacadeService;

    @Autowired
    private SystemTableService systemTableService;

    @Autowired
    private SystemTableAttributeService systemTableAttributeService;

    @Autowired
    private PrintTemplateService printTemplateService;
    @Autowired
    private ExcelImportRuleService excelImportRuleService;

    @Autowired
    private WorkHourService workHourService;
    @Autowired
    private ViewDefinitionService viewDefinitionService;
    @Autowired
    private ViewDefinitionNewService viewDefinitionNewService;

    @Autowired
    private ExcelExportRuleService excelExportRuleService;

    @Autowired
    private CdScriptDefinitionFacadeService cdScriptDefinitionFacadeService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * 根据字典类型返回指定下子结点的字典编码
     *
     * @param type
     * @return
     */
    public List<CdDataDictionaryItemDto> getDataDictionariesByType(String type) {
        return dataDictionaryService.listItemByDictionaryCode(type);
        // return dataDictionaryService.getDataDictionariesByType(type);
    }

    /**
     * 根据字典编码获取字典项
     *
     * @param dictionaryCode
     * @return
     */
    @Override
    public List<CdDataDictionaryItemDto> listDictionaryItemByDictionaryCode(String dictionaryCode) {
        return dataDictionaryService.listItemByDictionaryCode(dictionaryCode);
    }

    /**
     * 根据字典类型返回指定下子结点的指定字典编码的数据字典列表
     *
     * @param type
     * @param code
     * @return
     */
    public List<CdDataDictionaryItemDto> getDataDictionaries(String type, String code) {
        CdDataDictionaryItemDto dto = dataDictionaryService.getItemByDictionaryCodeAndItemValue(type, code);
        List<CdDataDictionaryItemDto> list = Lists.newArrayList();
        if (dto != null && dto.getUuid() != null) {
            list.add(dto);
        }
        return list;
        // return dataDictionaryService.getDataDictionaries(type, code);
    }

    /**
     * 根据字典编码及字典项值获取字典项
     *
     * @param dictionaryCode
     * @param itemValue
     * @return
     */
    @Override
    public CdDataDictionaryItemDto getDictionaryItemByDictionaryCodeAndItemValue(String dictionaryCode, String itemValue) {
        return dataDictionaryService.getItemByDictionaryCodeAndItemValue(dictionaryCode, itemValue);
    }

    /**
     * 根据表的uuid返回主表及从表属性的集合
     *
     * @param tableUuid
     * @return
     */
    public List<SystemTableAttribute> getAttributesByrelationship(String tableUuid) {
        List<SystemTableAttribute> systemTableColumnsList = systemTableAttributeService
                .getAttributesByrelationship(tableUuid);
        List<SystemTableAttribute> list = new ArrayList<SystemTableAttribute>();
        for (SystemTableAttribute systemTableAttribute : systemTableColumnsList) {
            SystemTableAttribute attribute = new SystemTableAttribute();
            BeanUtils.copyProperties(systemTableAttribute, attribute);

            SystemTable systemTable = new SystemTable();
            BeanUtils.copyProperties(systemTableAttribute.getSystemTable(), systemTable);

            attribute.setSystemTable(systemTable);
            list.add(attribute);
        }
        return list;
    }

    /**
     * 根据表的uuid返回主表及从表属性的集合(返回TreeNode)
     *
     * @param tableUuid
     * @return
     */
    public List<TreeNode> getAttributesTreeNodeByrelationship(String s, String tableUuid) {
        return systemTableAttributeService.getAttributesTreeNodeByrelationship(s, tableUuid);
    }

    /**
     * 根据表的uuid返回主表及从表属性的集合(吴宾)
     *
     * @param tableUuid
     * @return
     */
    public List<SystemTableRelationship> getAttributesByrelationship2(String tableUuid) {
        List<SystemTableRelationship> systemTableColumnsList = systemTableAttributeService
                .getAttributesByrelationship2(tableUuid);
        List<SystemTableRelationship> list = BeanUtils.convertCollection(systemTableColumnsList,
                SystemTableRelationship.class);
        return list;
    }

    /**
     * 获得系统表名
     *
     * @param tableUuid
     * @return
     */
    public SystemTable getTable(String tableUuid) {
        return systemTableAttributeService.getTable(tableUuid);
    }

    /**
     * 获得模块id
     *
     * @param tableUuid
     * @return
     */
    public String getModuleIdFromTable(String tableUuid) {
        String moduleName = systemTableAttributeService.getTable(tableUuid).getModuleName();

        return "";
    }

    /**
     * 返回指定模块ID下的所有系统表属性集合
     *
     * @param ModuleId
     * @return
     */
    public List<SystemTableAttribute> getSystemTableAttributesByModuleId(String moduleId) {
        return systemTableService.getSystemTableAttributesByModuleId(moduleId);
    }

    /**
     * 返回指定模块ID下的所有系统表关系集合
     */
    public List<SystemTableRelationship> getSystemTableRelationshipsByModuleId(String moduleId) {
        return systemTableService.getSystemTableRelationshipsByModuleId(moduleId);
    }

    /**
     * 返回所有系统表集合
     *
     * @return
     */
    public List<SystemTable> getAllSystemTables() {
        return systemTableService.getAllSystemTables();
    }

    /**
     * 根据表的uuid返回表的所有字段的集合
     *
     * @param tableUuid
     * @return
     */
    public List<SystemTableAttribute> getSystemTableColumns(String tableUuid) {
        SystemTable systemTable = systemTableService.getByFullEntityName(tableUuid);
        if (null != systemTable && StringUtils.isNotBlank(systemTable.getUuid())) {
            tableUuid = systemTable.getUuid();
        }
        List<SystemTableAttribute> systemTableColumnsList = systemTableAttributeService
                .getSystemTableColumns(tableUuid);
        List<SystemTableAttribute> list = BeanUtils.convertCollection(systemTableColumnsList,
                SystemTableAttribute.class);
        return list;
    }

    /**
     * 根据表的uuid返回表的所有字段的数据类型
     *
     * @param uuid
     * @return
     * @throws Exception
     */
    public Map<String, String> getColumnTypeAsMap(String tableUuid) {
        Map<String, String> ColumnType = null;
        try {
            SystemTable systemTable = systemTableService.getByUuid(tableUuid);
            ColumnType = systemTableService.getFieldsTypeByForm(systemTable);
        } catch (Exception e) {
            logger.error("返回表字段数据类型出错原因：" + e.getMessage(), e);
        }
        return ColumnType;
    }

    /**
     * 根据实体类和属性，获取实体类属性对应的表字段名称
     *
     * @param clazz
     * @param propertyName
     * @return
     */
    public String getColumnName(Class clazz, String propertyName) {
        return systemTableService.getColumnMap(clazz, propertyName).get("name");

    }

    /**
     * 系统表数据查询
     *
     * @param formUuid      表uuid
     * @param projection    查询的列名，为空查询所有列
     * @param selection     查询where条件语句
     * @param selectionArgs 查询where条件语句参数
     * @param groupBy       分组语句
     * @param having        分组条件语句
     * @param orderBy       排序
     * @param firstResult   首条记录索引号
     * @param maxResults    最大记录集
     */

    public List<Map<String, Object>> query(String tableUuid, Boolean distinct, String[] projection, String selection,
                                           Map<String, Object> selectionArgs, String groupBy, String having, String orderBy, int firstResult,
                                           int maxResults) {
        List<Map<String, Object>> queryResult = null;
        try {
            SystemTable systemTable = systemTableService.getByUuid(tableUuid);
            queryResult = systemTableService.query(systemTable, distinct, projection, selection, selectionArgs, groupBy,
                    having, orderBy, firstResult, maxResults);
        } catch (Exception e) {
            logger.error("系统表数据出错原因：" + e.getMessage(), e);
        }
        return queryResult;
    }

    /**
     * 根据ID返回流水号
     *
     * @return
     */
    public SerialNumber getSerialNumberById(String serialNumberId) {
        return serialNumberService.getById(serialNumberId);
    }

    /**
     * 返回所有流水号定义
     *
     * @return
     */
    public List<SerialNumber> getAllSerialNumbers() {
        List<SerialNumber> serialNumberList = serialNumberService.findAll();
        return serialNumberList;
    }

    /**
     * 返回所有新版流水号定义
     *
     * @return
     */
    @Override
    public List<SnSerialNumberDefinitionDto> getAllSerialNumberDefinitions() {
        return snSerialNumberDefinitionFacadeService.listAll();
    }

    /**
     * 获取所有流水号id集合
     *
     * @return
     */
    public List<String> getSerialNumberIdList() {
        List<String> serialNumberIdList = serialNumberService.getSerialNumberIdList();
        return serialNumberIdList;
    }

    /**
     * 获取所有流水号类型集合
     *
     * @return
     */
    public List<String> getSerialNumberTypeList() {
        List<String> serialNumberTypeList = serialNumberService.getSerialNumberTypeList();
        return serialNumberTypeList;
    }

    /**
     * 获取流水号,（流水号ID，实体集合，是否占用，存放流水号字段）
     */
    public <ENTITY extends IdEntity> String getSerialNumber(String serialNumberId, Collection<ENTITY> entities,
                                                            Boolean isOccupied, String fieldName) throws Exception {
        return serialNumberService.getSerialNumber(serialNumberId, entities, isOccupied, null, fieldName);
    }

    /**
     * 获取流水号,（流水号ID，实体集合，是否占用，动态表单Map集合，存放流水号字段）
     */
    public <ENTITY extends IdEntity> String getSerialNumber(String serialNumberId, Collection<ENTITY> entities,
                                                            Boolean isOccupied, Map<String, Object> dytableMap, String fieldName) throws Exception {
        return serialNumberService.getSerialNumber(serialNumberId, entities, isOccupied, dytableMap, fieldName);
    }

    /**
     * 获取流水号,（流水号ID，单个实体，是否占用，动态表单Map集合，存放流水号字段）
     */
    public <ENTITY extends IdEntity> String getSerialNumber(String serialNumberId, ENTITY entity, Boolean isOccupied,
                                                            Map<String, Object> dytableMap, String fieldName) throws Exception {
        return serialNumberService.getSerialNumber(serialNumberId, entity, isOccupied, dytableMap, fieldName);
    }

    /**
     * 根据打印模板ID获取对应的模板实体对象
     *
     * @param printTemplateId
     * @return
     */
    public PrintTemplate getPrintTemplateById(String printTemplateId) {
        return printTemplateService.getPrintTemplateById(printTemplateId);
    }

    /**
     * 根据打印模板分类获取对应的模板实体对象集合
     *
     * @param category
     * @return
     */
    public List<PrintTemplate> getPrintTemplatesByCategory(String category) {
        return printTemplateService.getPrintTemplatesByCategory(category);
    }

    /**
     * 打印结果是否保存到源文档
     *
     * @param templateId 打印模板编号
     */
    public Boolean isSaveToSource(String templateId) {
        return printTemplateService.isSaveToSource(templateId);
    }

    /**
     * 获取所有可用的打印模板定义
     *
     * @return
     */
    public List<PrintTemplate> getAllPrintTemplates() {
        List<PrintTemplate> printTemplateList = printTemplateService.findAll();
        return printTemplateList;
    }

    /**
     * 打印调用接口,返回文件
     *
     * @param templateId  模板ID
     * @param allEntities 多份工作（实体集合）
     * @param dytableMaps 动态表单集合
     * @param textFile    输入文件(正文)
     */
    public <ENTITY extends IdEntity> File getPrintResultAsFile(String templateId,
                                                               Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps, File textFile) {
        File finalFile = null;
        try {
            finalFile = printTemplateService.getPrintTemplateFile(templateId, allEntities, dytableMaps, textFile);
        } catch (Exception e) {
            logger.error("打印模板调用接口,返回文件出错原因：" + e.getMessage(), e);
            throw new RuntimeException("打印模板调用接口,返回文件出错原因：" + e.getMessage(), e);
        }
        return finalFile;
    }

    /**
     * 打印调用接口,返回文件流
     *
     * @param templateId  模板ID
     * @param allEntities 多份工作（实体集合）
     * @param dytableMaps 动态表单集合
     * @param textFile    输入文件(正文)
     */
    public <ENTITY extends IdEntity> InputStream getPrintResultAsInputStream(String templateId,
                                                                             Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps, File textFile) {
        InputStream inputStream = null;
        try {
            inputStream = printTemplateService.getPrintTemplateInputStream(templateId, allEntities, dytableMaps,
                    textFile);
        } catch (Exception e) {
            logger.error("打印模板调用接口,返回文件出错原因：" + e.getMessage(), e);
            throw new RuntimeException("打印模板调用接口,返回文件出错原因：" + e.getMessage(), e);
        }
        return inputStream;
    }

    /**
     * 打印调用接口,返回文件
     *
     * @param templateId 模板ID
     * @param entities   单份工作
     * @param dytableMap 动态表单集合
     * @param textFile   输入文件(正文)
     */
    public <ENTITY extends IdEntity> File getPrintResultAsFile(String templateId, Collection<ENTITY> entities,
                                                               Map<String, Object> dytableMap, File textFile) {
        File finalFile = null;
        try {
            finalFile = printTemplateService.getPrintTemplateFile(templateId, entities, dytableMap, textFile);
        } catch (Exception e) {
            logger.error("打印模板调用接口,返回文件出错原因：" + e.getMessage(), e);
            throw new RuntimeException("打印模板调用接口,返回文件出错原因：" + e.getMessage(), e);
        }
        return finalFile;
    }

    /**
     * 打印调用接口,返回文件流
     *
     * @param templateId 模板ID
     * @param entities   单份工作
     * @param dytableMap 动态表单集合
     * @param textFile   输入文件(正文)
     */
    public <ENTITY extends IdEntity> InputStream getPrintResultAsInputStream(String templateId,
                                                                             Collection<ENTITY> entities, Map<String, Object> dytableMap, File textFile) {
        InputStream inputStream = null;
        try {
            inputStream = printTemplateService.getPrintTemplateInputStream(templateId, entities, dytableMap, textFile);
        } catch (Exception e) {
            logger.error("打印模板调用接口,返回文件出错原因：" + e.getMessage(), e);
            throw new RuntimeException("打印模板调用接口,返回文件出错原因：" + e.getMessage(), e);
        }
        return inputStream;
    }

    /**
     * 打印调用接口,返回文件流
     *
     * @param templateId 模板ID
     * @param entities   单份工作
     * @param dytableMap 动态表单集合
     * @param textFile   输入所有的文件(正文)
     */
    public <ENTITY extends IdEntity> InputStream getPrintResultAsInputStream(String templateId,
                                                                             Collection<ENTITY> entities, Map<String, Object> dytableMap, Map<String, List<MongoFileEntity>> bodyFiles) {
        return getPrintResultAsInputStream(templateId, (String) null, (String) null, entities, dytableMap, bodyFiles);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade#getPrintResultAsInputStream(java.lang.String, java.lang.String, java.lang.String, java.util.List, java.util.Map, java.util.Map)
     */
    @Override
    public <ENTITY extends IdEntity> InputStream getPrintResultAsInputStream(String templateId,
                                                                             String printTemplateUuid, String lang, Collection<ENTITY> entities, Map<String, Object> dytableMap,
                                                                             Map<String, List<MongoFileEntity>> bodyFiles) {
        InputStream inputStream = null;
        try {
            inputStream = printTemplateService.getPrintTemplateInputStream(templateId, printTemplateUuid, lang,
                    entities, dytableMap, bodyFiles);
        } catch (Exception e) {
            logger.error("打印模板调用接口,返回文件出错原因：" + e.getMessage(), e);
            if (e.getMessage().contains("存在附件格式不符合套打模板要求")) {
                throw new BusinessException(e.getMessage(), e);
            } else if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                throw new RuntimeException("打印模板调用接口,返回文件出错原因：" + e.getMessage(), e);
            }
        }
        return inputStream;
    }

    /**
     * 合并文件（分页）
     */
    @Override
    public <ENTITY extends IdEntity> File uniteDocByPage(Integer docNumber, PrintTemplate printTemplate,
                                                         String finalFileName) {
        return printTemplateService.uniteDocByPage(docNumber, printTemplate, finalFileName);
    }

    /**
     * 合并文件，通过poi合并文件，只支持docx
     */
    @Override
    public <ENTITY extends IdEntity> File uniteDocx(Integer docNumber, PrintTemplate printTemplate,
                                                    String finalFileName, boolean pageBreak) {
        List<String> fileList = new ArrayList<String>();
        for (int i = 1; i <= docNumber; i++) {
            String file = Config.HOME_DIR + File.separator + finalFileName + "-" + i + ".doc";
            fileList.add(file);
            logger.info("当前文件名为：" + file);
        }
        if (fileList.size() == 0) {
            return null;
        }

        List<File> fileList2 = new ArrayList<File>();
        for (String filePath : fileList) {
            fileList2.add(new File(filePath));
        }

        File file = PoiTlUtils.mergeDocx(fileList2, pageBreak);
        PoiTlUtils.deleteFiles(fileList2);

        return file;
    }

    @Override
    public Map getSelectEntityColumn(String uuid) {
        if (uuid != null && !uuid.equals("")) {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            List<SystemTableAttribute> systemTableAttributeList = this.getSystemTableColumns(uuid);
            for (SystemTableAttribute s : systemTableAttributeList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("columnName", s.getAttributeName());
                list.add(map);
            }
            Map<String, List<Map<String, Object>>> m = new HashMap<String, List<Map<String, Object>>>();
            m.put("data", list);
            return m;
        } else {
            return null;
        }
    }

    @Override
    public Map getSelectFormColumn(String uuid) {
        if (uuid != null && !uuid.equals("")) {
            List list = new ArrayList();
            List<DyformFieldDefinition> systemTableAttributeList = dyFormFacade.getFormDefinition(uuid)
                    .doGetFieldDefintions();
            for (DyformFieldDefinition f : systemTableAttributeList) {
                Map map = new HashMap();
                map.put("columnName", f.getName());
                list.add(map);
            }
            Map m = new HashMap();
            m.put("data", list);
            return m;
        } else {
            return null;
        }
    }

    /**************************************** 导入规则相关 ****************************************/
    /**
     * 解析excel为list<Map<String,Object>> (实体或Map)
     *
     * @param uploadFile
     * @param code
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getListByExcel(InputStream inputStream, String id) {
        List<Map<String, Object>> result = null;
        try {
            result = excelImportRuleService.getListFromExcel(id, inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 解析excel为list<Map<String,Object>> (实体或Map)
     *
     * @param inputStream
     * @param id
     * @param sheetName   页签名 可指定导入的页签.
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getListByExcel(InputStream inputStream, String id, String sheetName) {
        List<Map<String, Object>> result = null;
        try {
            result = excelImportRuleService.getListFromExcel(id, inputStream, sheetName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 根据ID获取导入规则
     *
     * @param id
     * @return
     */
    public ExcelImportRule getExcelImportRuleById(String id) {
        return excelImportRuleService.getExcelImportRuleObj(id);
    }

    public List<ExcelImportRule> getAllExcelImportRules() {
        return BeanUtils.convertCollection(excelImportRuleService.getExcelImportRule(), ExcelImportRule.class);
    }

    /**
     * 获取所有的导出规则
     *
     * @return
     */
    public List<ExcelExportDefinition> getAllExcelExportRules() {
        return BeanUtils.convertCollection(excelExportRuleService.getExcelExportRule(), ExcelExportDefinition.class);
    }

    /**************************************** 导入规则相关 ****************************************/

    /**************************************** 工作日相关 ****************************************/
    /**
     * 获取指定工作日的信息
     *
     * @param date
     * @return
     */
    public WorkingHour getWorkingHour(Date date) {
        return workHourService.getWorkingHour(date);
    }

    /**
     * 获取当前日期或将来日期有效的工作日的信息
     *
     * @param date
     * @return
     */
    public WorkingHour getEffectiveWorkingHour(Date date) {
        return workHourService.getEffectiveWorkingHour(date);
    }

    /**
     * 判断是否为工作时间
     *
     * @param date
     * @return
     */
    public boolean isWorkHour(Date date) {
        return workHourService.isWorkHour(date);
    }

    /**
     * 判断是否为工作日
     *
     * @param date
     * @return
     */
    public boolean isWorkDay(Date date) {
        return workHourService.isWorkDay(date);
    }

    /**
     * 返回有效的工作时间点
     *
     * @param date   开始时间点
     * @param amount
     * @param type   工作日、小时时间单位
     * @return
     */
    public Date getWorkDate(Date fromDate, Double amount, WorkUnit workingUnit) {
        return workHourService.getWorkDate(fromDate, amount, workingUnit);
    }

    /**
     * 返回前一工作日的工作时间点
     *
     * @param date
     * @return
     */
    public Date getPrevWorkDate(Date date) {
        return workHourService.getPrevWorkDate(date);
    }

    /**
     * 返回下一工作日的工作时间点
     *
     * @param date
     * @return
     */
    public Date getNextWorkDate(Date date) {
        return workHourService.getNextWorkDate(date);
    }

    /**
     * 返回有效工作时间区段(单位秒)以及经历几个工作日
     *
     * @param fromTime
     * @param toTime
     * @return
     */
    public WorkPeriod getWorkPeriod(Date fromTime, Date toTime) {
        return workHourService.getWorkPeriod(fromTime, toTime);
    }

    /****************************************获取视图的数据api************************************/
    /**
     * 获得视图数据API
     *
     * @param viewUuid
     * @param otherParams
     * @return
     */
    public List<QueryItem> getViewColumnData(DyViewQueryInfoNew dyViewQueryInfoNew, String viewUuid,
                                             String otherParam) {
        List<QueryItem> queryItems = new ArrayList<QueryItem>();

        ViewDefinitionNewBean bean = viewDefinitionNewService.getBeanByUuid(viewUuid);
        // Integer dataScope = bean.getDataScope();
        String defaultCondition = bean.getDefaultCondition();
        if (defaultCondition == null) {
            defaultCondition = "";
        }
        /*******************************解析视图的默认搜索条件中特定的变量********************************/
        while (defaultCondition.contains("  ")) {
            defaultCondition = defaultCondition.replaceAll("  ", " ");
        }
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserName}",
                SpringSecurityUtils.getCurrentUserName());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentLoginName}",
                SpringSecurityUtils.getCurrentLoginName());
        defaultCondition = StringUtils.replace(defaultCondition, "${currentUserId}",
                SpringSecurityUtils.getCurrentUserId());
        // 替换当前登录用户的单位id
        CommonUnit commonUnit = commonUnitService.getByCurrentUserId(SpringSecurityUtils.getCurrentUserId());
        if (commonUnit != null) {
            defaultCondition = StringUtils.replace(defaultCondition, "${currentUnitId}",
                    "'" + commonUnit.getUnitId() + "'");
        }
        defaultCondition = defaultCondition.replace("=${nowDate}", " between :preDate and :nextDate")
                .replace("= ${nowDate}", " between :preDate and :nextDate");
        if (StringUtils.isNotBlank(otherParam)) {
            String[] otherParams = otherParam.split(",");
            defaultCondition += " (1=1) and uuid in (";
            StringBuilder temp = new StringBuilder();
            for (String op : otherParams) {
                temp.append(",'" + op + "'");
            }
            defaultCondition += temp.toString().replaceFirst(",", "");
            defaultCondition += ")";
        }
        // String tableUuid = bean.getFormuuid();
        // String tableName = bean.getTableDefinitionName();
        String tableDefinitionId = bean.getTableDefinitionId();

        // 复选框的key
        String rowIdKey = StringUtils.isBlank(bean.getCheckKey()) ? "uuid" : bean.getCheckKey();
        // 获取视图的角色权限信息
        // String roleType = bean.getRoleType();
        // 获取视图的角色类型
        // String roleValue = bean.getRoleValue();
        // 导出模版的uuid
        String excelUuid = bean.getDataModuleId();

        // Set<ColumnDefinition> columnDefinitions = new
        // HashSet<ColumnDefinition>();
        Set<ColumnDefinitionNew> columnDefinitions = new HashSet<ColumnDefinitionNew>();

        PageDefinitionNew pageDefinition = bean.getPageDefinitionNews();
        if (dyViewQueryInfoNew.getPageInfo() == null) {
            PagingInfo page = new PagingInfo();
            page.setPageSize(Integer.MAX_VALUE);
            page.setCurrentPage(1);
            dyViewQueryInfoNew.setPageInfo(page);
        }

        if (StringUtils.isNotBlank(excelUuid)) {
            // 根据excel导出模版来对视图的列进行额外的添加
            ExcelExportDefinitionBean exportBean = excelExportRuleService.getBeanByUuid(excelUuid);
            Set<ExcelExportColumnDefinition> excelExportColumnDefinitions = exportBean.getExcelExportColumnDefinition();
            for (ExcelExportColumnDefinition excelExportColumnDefinition : excelExportColumnDefinitions) {
                String attributeName = excelExportColumnDefinition.getAttributeName();
                String titleName = excelExportColumnDefinition.getTitleName();
                String entityName = excelExportColumnDefinition.getEntityName();
                String columnAliase = excelExportColumnDefinition.getColumnAliase();
                String columnDataType = excelExportColumnDefinition.getColumnDataType();
                String uuid = UUID.randomUUID().toString();
                ColumnDefinitionNew columnDefinition = new ColumnDefinitionNew();
                columnDefinition.setUuid(uuid);
                columnDefinition.setFieldName(attributeName);
                columnDefinition.setTitleName(titleName);
                columnDefinition.setEntityName(entityName);
                columnDefinition.setColumnAliase(columnAliase);
                columnDefinition.setColumnDataType(columnDataType);
                columnDefinition.setColumnType("直接展示");
                columnDefinitions.add(columnDefinition);
            }
        } else {
            columnDefinitions = bean.getColumnDefinitionNews();
        }
        List<QueryItem> queryItemsNew = new ArrayList<QueryItem>();
        queryItemsNew = viewDefinitionNewService.getViewData(defaultCondition, "", tableDefinitionId, columnDefinitions,
                pageDefinition, dyViewQueryInfoNew, rowIdKey);

        return queryItemsNew;

        // //根据数据源范围的不同取回数据
        // if (dataScope == DyviewConfig.DYVIEW_DATASCOPE_DYTABLE) {
        // queryItems = viewDefinitionService.getColumnData(defaultCondition,
        // tableName, columnDefinitions,
        // pageDefinition, dyViewQueryInfo);
        // } else if (dataScope == DyviewConfig.DYVIEW_DATASCOPE_ENTITY) {
        // queryItems = viewDefinitionService.getColumnData2(tableUuid,
        // defaultCondition, columnDefinitions, rowIdKey,
        // roleType, roleValue, pageDefinition, dyViewQueryInfo, null);
        // } else if (dataScope == DyviewConfig.DYVIEW_DATASCOPE_MOUDLE) {
        // queryItems = viewDefinitionService.getColumnData3(defaultCondition,
        // tableName, columnDefinitions,
        // pageDefinition, dyViewQueryInfo, null);
        // }

    }

    /**************************************** 工作日相关 ****************************************/

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade#getScriptDefinitionById(java.lang.String)
     */
    @Override
    public ScriptDefinition getScriptDefinitionById(String scriptDefinitionId) {
        return cdScriptDefinitionFacadeService.getScriptDefinitionById(scriptDefinitionId);
    }

    /**
     * 根据流水号构建参数，生成响应的流水
     *
     * @param params
     * @return
     */
    @Override
    public String generateSerialNumber(SerialNumberBuildParams params) {
        String sn = null;
        SnSerialNumberDefinitionDto dto = snSerialNumberFacadeService.getSerialNumberDefinitionById(params.getSerialNumberId());
        if (StringUtils.isNotBlank(dto.getUuid())) {
            SerialNumberInfo serialNumberInfo = snSerialNumberFacadeService.generateSerialNumber(params);
            if (serialNumberInfo != null) {
                sn = serialNumberInfo.getSerialNo();
            }
        } else {
            sn = serialNumberService.generateSerialNumber(params);
        }
        serialNumberService.flushSession();
        serialNumberService.clearSession();
        return sn;
    }

}
