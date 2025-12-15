/*
 * @(#)2013-1-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.groovy.GroovyUseable;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.excelexporttemplate.entity.ExcelExportDefinition;
import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelImportRule;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.script.support.ScriptDefinition;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberDefinitionDto;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumber;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberBuildParams;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTable;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;
import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableRelationship;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.basicdata.workhour.support.WorkingHour;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
@GroovyUseable
public interface BasicDataApiFacade {

    /**
     * 根据字典类型返回指定下子结点的字典编码
     *
     * @param type
     * @return
     */
    @Deprecated
    List<CdDataDictionaryItemDto> getDataDictionariesByType(String type);

    /**
     * 根据字典编码获取字典项
     *
     * @param dictionaryCode
     * @return
     */
    List<CdDataDictionaryItemDto> listDictionaryItemByDictionaryCode(String dictionaryCode);

    /**
     * 根据字典类型返回指定下子结点的指定字典编码的数据字典列表
     *
     * @param type
     * @param code
     * @return
     */
    List<CdDataDictionaryItemDto> getDataDictionaries(String type, String code);

    /**
     * 根据字典编码及字典项值获取字典项
     *
     * @return
     */
    CdDataDictionaryItemDto getDictionaryItemByDictionaryCodeAndItemValue(String dictionaryCode, String itemValue);

    /**
     * 根据表的uuid返回主表及从表属性的集合
     *
     * @param tableUuid
     * @return
     */
    public List<SystemTableAttribute> getAttributesByrelationship(String tableUuid);

    /**
     * 根据表的uuid返回主表及从表属性的集合(返回TreeNode)
     *
     * @param tableUuid
     * @return
     */
    public List<TreeNode> getAttributesTreeNodeByrelationship(String s, String tableUuid);

    /**
     * 根据表的uuid返回主表及从表属性的集合(吴宾)
     *
     * @param tableUuid
     * @return
     */
    public List<SystemTableRelationship> getAttributesByrelationship2(String tableUuid);

    /**
     * 获得系统表名
     *
     * @param tableUuid
     * @return
     */
    public SystemTable getTable(String tableUuid);

    /**
     * 获得模块id
     *
     * @param tableUuid
     * @return
     */
    public String getModuleIdFromTable(String tableUuid);

    /**
     * 返回指定模块ID下的所有系统表属性集合
     *
     * @param ModuleId
     * @return
     */
    public List<SystemTableAttribute> getSystemTableAttributesByModuleId(String moduleId);

    /**
     * 返回指定模块ID下的所有系统表关系集合
     */
    public List<SystemTableRelationship> getSystemTableRelationshipsByModuleId(String moduleId);

    /**
     * 返回所有系统表集合
     *
     * @return
     */
    public List<SystemTable> getAllSystemTables();

    /**
     * 根据表的uuid返回表的所有字段的集合
     *
     * @param tableUuid
     * @return
     */
    public List<SystemTableAttribute> getSystemTableColumns(String tableUuid);

    /**
     * 根据表的uuid返回表的所有字段的数据类型
     *
     * @param uuid
     * @return
     * @throws Exception
     */
    public Map<String, String> getColumnTypeAsMap(String tableUuid);

    /**
     * 根据实体类和属性，获取实体类属性对应的表字段名称
     *
     * @param clazz
     * @param propertyName
     * @return
     */
    public String getColumnName(Class clazz, String propertyName);

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
                                           int maxResults);

    /**
     * 根据ID返回流水号
     *
     * @return
     */
    public SerialNumber getSerialNumberById(String serialNumberId);

    /**
     * 返回所有流水号定义
     *
     * @return
     */
    public List<SerialNumber> getAllSerialNumbers();

    /**
     * 返回所有新版流水号定义
     *
     * @return
     */
    public List<SnSerialNumberDefinitionDto> getAllSerialNumberDefinitions();

    /**
     * 获取所有流水号id集合
     *
     * @return
     */
    public List<String> getSerialNumberIdList();

    /**
     * 获取所有流水号类型集合
     *
     * @return
     */
    public List<String> getSerialNumberTypeList();

    /**
     * 获取流水号,（流水号ID，实体集合，是否占用，存放流水号字段）
     */
    public <ENTITY extends IdEntity> String getSerialNumber(String serialNumberId, Collection<ENTITY> entities,
                                                            Boolean isOccupied, String fieldName) throws Exception;

    /**
     * 获取流水号,（流水号ID，实体集合，是否占用，动态表单Map集合，存放流水号字段）
     */
    public <ENTITY extends IdEntity> String getSerialNumber(String serialNumberId, Collection<ENTITY> entities,
                                                            Boolean isOccupied, Map<String, Object> dytableMap, String fieldName) throws Exception;

    /**
     * 获取流水号,（流水号ID，单个实体，是否占用，动态表单Map集合，存放流水号字段）
     */
    public <ENTITY extends IdEntity> String getSerialNumber(String serialNumberId, ENTITY entity, Boolean isOccupied,
                                                            Map<String, Object> dytableMap, String fieldName) throws Exception;

    /**
     * 根据打印模板ID获取对应的模板实体对象
     *
     * @param printTemplateId
     * @return
     */
    public PrintTemplate getPrintTemplateById(String printTemplateId);

    /**
     * 根据打印模板分类获取对应的模板实体对象集合
     *
     * @param category
     * @return
     */
    public List<PrintTemplate> getPrintTemplatesByCategory(String category);

    /**
     * 打印结果是否保存到源文档
     *
     * @param templateId 打印模板编号
     */
    public Boolean isSaveToSource(String templateId);

    /**
     * 获取所有可用的打印模板定义
     *
     * @return
     */
    public List<PrintTemplate> getAllPrintTemplates();

    /**
     * 打印调用接口,返回文件
     *
     * @param templateId  模板ID
     * @param allEntities 多份工作（实体集合）
     * @param dytableMaps 动态表单集合
     * @param textFile    输入文件(正文)
     */
    public <ENTITY extends IdEntity> File getPrintResultAsFile(String templateId,
                                                               Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps, File textFile);

    /**
     * 打印调用接口,返回文件流
     *
     * @param templateId  模板ID
     * @param allEntities 多份工作（实体集合）
     * @param dytableMaps 动态表单集合
     * @param textFile    输入文件(正文)
     */
    public <ENTITY extends IdEntity> InputStream getPrintResultAsInputStream(String templateId,
                                                                             Collection<Collection<ENTITY>> allEntities, List<Map<String, Object>> dytableMaps, File textFile);

    /**
     * 打印调用接口,返回文件
     *
     * @param templateId 模板ID
     * @param entities   单份工作
     * @param dytableMap 动态表单集合
     * @param textFile   输入文件(正文)
     */
    public <ENTITY extends IdEntity> File getPrintResultAsFile(String templateId, Collection<ENTITY> entities,
                                                               Map<String, Object> dytableMap, File textFile);

    /**
     * 打印调用接口,返回文件流
     *
     * @param templateId 模板ID
     * @param entities   单份工作
     * @param dytableMap 动态表单集合
     * @param textFile   输入文件(正文)
     */
    public <ENTITY extends IdEntity> InputStream getPrintResultAsInputStream(String templateId,
                                                                             Collection<ENTITY> entities, Map<String, Object> dytableMap, File textFile);

    /**
     * 打印调用接口,返回文件流
     *
     * @param templateId 模板ID
     * @param entities   单份工作
     * @param dytableMap 动态表单集合
     * @param textFile   输入所有的文件(正文)
     */
    public <ENTITY extends IdEntity> InputStream getPrintResultAsInputStream(String templateId,
                                                                             Collection<ENTITY> entities, Map<String, Object> dytableMap, Map<String, List<MongoFileEntity>> bodyFiles);

    /**************************************** 导入规则相关 ****************************************/
    /**
     * 解析excel为list<Map<String,Object>> (实体或Map)
     *
     * @param uploadFile
     * @param code
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getListByExcel(InputStream inputStream, String id);

    /**
     * 解析excel为list<Map<String,Object>> (实体或Map)
     *
     * @param inputStream
     * @param id
     * @param sheetName   页签名 可指定导入的页签.
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getListByExcel(InputStream inputStream, String id, String sheetName);

    /**
     * 根据ID获取导入规则
     *
     * @param id
     * @return
     */
    public ExcelImportRule getExcelImportRuleById(String id);

    public List<ExcelImportRule> getAllExcelImportRules();

    /**
     * 获取所有的导出规则
     *
     * @return
     */
    public List<ExcelExportDefinition> getAllExcelExportRules();

    /**************************************** 导入规则相关 ****************************************/

    /**************************************** 工作日相关 ****************************************/
    /**
     * 获取指定工作日的信息
     *
     * @param date
     * @return
     */
    public WorkingHour getWorkingHour(Date date);

    /**
     * 获取当前日期或将来日期有效的工作日的信息
     *
     * @param date
     * @return
     */
    public WorkingHour getEffectiveWorkingHour(Date date);

    /**
     * 判断是否为工作时间
     *
     * @param date
     * @return
     */
    public boolean isWorkHour(Date date);

    /**
     * 判断是否为工作日
     *
     * @param date
     * @return
     */
    public boolean isWorkDay(Date date);

    /**
     * 返回有效的工作时间点
     *
     * @param date   开始时间点
     * @param amount
     * @param type   工作日、小时时间单位
     * @return
     */
    public Date getWorkDate(Date fromDate, Double amount, WorkUnit workingUnit);

    /**
     * 返回前一工作日的工作时间点
     *
     * @param date
     * @return
     */
    public Date getPrevWorkDate(Date date);

    /**
     * 返回下一工作日的工作时间点
     *
     * @param date
     * @return
     */
    public Date getNextWorkDate(Date date);

    /**
     * 返回有效工作时间区段(单位秒)以及经历几个工作日
     *
     * @param fromTime
     * @param toTime
     * @return
     */
    public WorkPeriod getWorkPeriod(Date fromTime, Date toTime);

    /****************************************获取视图的数据api************************************/
    /**
     * 获得视图数据API
     *
     * @param viewUuid
     * @param otherParams
     * @return
     */
    public List<QueryItem> getViewColumnData(DyViewQueryInfoNew dyViewQueryInfoNew, String viewUuid, String otherParam);

    /**************************************** 工作日相关 ****************************************/

    /****************************************获取脚本定义的api************************************/
    /**
     * 根据脚本定义ID，获取脚本定义
     *
     * @param scriptDefinitionId
     * @return
     */
    public ScriptDefinition getScriptDefinitionById(String scriptDefinitionId);

    /**************************************** 获取脚本定义的api ***********************************/

    /**
     * 如何描述该方法
     *
     * @param tmpPrintTemplateId
     * @param printTemplateUuid
     * @param lang
     * @param entities
     * @param bjsqdFormMap
     * @param bodyFiles
     * @return
     */
    public <ENTITY extends IdEntity> InputStream getPrintResultAsInputStream(String templateId,
                                                                             String printTemplateUuid, String lang, Collection<ENTITY> entities, Map<String, Object> dytableMap,
                                                                             Map<String, List<MongoFileEntity>> bodyFiles);

    /**
     * 合并文件（分页）
     */
    <ENTITY extends IdEntity> File uniteDocByPage(Integer docNumber, PrintTemplate printTemplate, String finalFileName);

    <ENTITY extends IdEntity> File uniteDocx(Integer docNumber, PrintTemplate printTemplate,
                                             String finalFileName, boolean pageBreak);

    Map getSelectEntityColumn(String uuid);

    Map getSelectFormColumn(String uuid);

    String generateSerialNumber(SerialNumberBuildParams params);

}
