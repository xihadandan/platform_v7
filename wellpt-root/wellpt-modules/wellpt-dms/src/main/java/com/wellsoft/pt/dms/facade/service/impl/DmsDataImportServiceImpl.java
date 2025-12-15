/*
 * @(#)2017-12-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service.impl;

import com.google.common.base.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.context.util.excel.*;
import com.wellsoft.context.util.groovy.GroovyUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.dms.bean.DmsDataImportBean;
import com.wellsoft.pt.dms.bean.DmsDataImportFiledBean;
import com.wellsoft.pt.dms.bean.DmsDataImportFiledDefaultsBean;
import com.wellsoft.pt.dms.bean.DmsDataImportFiledRuleBean;
import com.wellsoft.pt.dms.enums.DataImportDefaultTypeEnum;
import com.wellsoft.pt.dms.enums.DataImportRuleTypeEnum;
import com.wellsoft.pt.dms.ext.excel.entity.ImpExcelDataBatchDetailEntity;
import com.wellsoft.pt.dms.ext.excel.entity.ImpExcelDataBatchEntity;
import com.wellsoft.pt.dms.ext.excel.service.ImpExcelDataBatchService;
import com.wellsoft.pt.dms.facade.service.DmsDataImportService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Description: 如何描述该类
 *
 * @author zhongwd
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月5日.1	zhongwd		2018年9月5日		Create
 * </pre>
 * @date 2018年9月5日
 */
@Service
public class DmsDataImportServiceImpl extends BaseServiceImpl implements DmsDataImportService {
    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private DmsDataImportSaveServiceImpl dataImportSaveService;

    @Autowired
    private ImpExcelDataBatchService implExcelDataBatchService;

    private ThreadLocal<Map<String/* 数据 uuid */, Map<String/* 表单字段 */, Object/* 表单字段值 */>>> uuidFieldKeyValues = new ThreadLocal<>();


    /**
     * 导入
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dms.facade.service.DmsDataImportService#importFormRepoFile(java.lang.String, com.wellsoft.pt.dms.bean.DmsDataImportBean)
     */
    public ResultMessage importFormRepoFile(String fileId, DmsDataImportBean dmsDataImportBean) {
        StopWatch stopWatch = new StopWatch("getDyFormData");
        stopWatch.start("初始化配置数据");
        uuidFieldKeyValues.set(Maps.<String, Map<String, Object>>newHashMap());
        ResultMessage resultMessage = new ResultMessage("导入成功！");
        this.checkNotNull(fileId, dmsDataImportBean);
        Map<Integer, DmsDataImportFiledBean> dmsDataImportFiledRowMaps = getDmsDataImportFiledMapsByRow(
                dmsDataImportBean);
        Map<String, DmsDataImportFiledBean> dmsDataImportFiledNameMaps = getDmsDataImportFiledMapsByName(
                dmsDataImportBean);
        stopWatch.stop();
        logger.info("上传Excel ID={}，初始化配置数据结束耗时={}秒", new Object[]{fileId,
                stopWatch.getLastTaskInfo().getTimeSeconds()});
        ExcelImportDataReport dataReport = new ExcelImportDataReport();
        resultMessage.setData(dataReport);
        try {
            stopWatch.start("解析Excel数据开始");
            // 转入文件转成DTO对象
            Map<Integer, DyFormData> dyFormDataImports = getImportDtos(fileId, dmsDataImportBean,
                    dmsDataImportFiledRowMaps);
            stopWatch.stop();
            logger.info("上传附件ID={}，解析Excel数据结束耗时={}秒", new Object[]{fileId,
                    stopWatch.getLastTaskInfo().getTimeSeconds()});
            stopWatch.start("验证数据的有效性开始");
            // 验证数据的有效性
            validate(dyFormDataImports, dmsDataImportFiledNameMaps, dataReport);
            stopWatch.stop();
            logger.info("上传Excel ID={}，验证数据的有效性结束耗时={}秒", new Object[]{fileId,
                    stopWatch.getLastTaskInfo().getTimeSeconds()});

            stopWatch.start("保存数据开始");
            // 保存数据
            dataImportSaveService.save(dyFormDataImports, dmsDataImportBean,
                    uuidFieldKeyValues.get(), dataReport);
            stopWatch.stop();
            logger.info("上传ExcelID={}，保存数据结束耗时={}秒", new Object[]{fileId,
                    stopWatch.getLastTaskInfo().getTimeSeconds()});
            logger.info("上传Excel结束，执行情况：{}", stopWatch.prettyPrint());

        } catch (Exception e) {
            resultMessage.clear();
            resultMessage.addMessage("导入失败！" + e.getMessage());
            resultMessage.setSuccess(false);
            logger.error(e.getMessage(), e);
        }
        return resultMessage;
    }

    @Override
    public ResultMessage importByListener(String fileId, String className) {
        return this.importByListener(fileId, className, null);
    }

    @Override
    public ResultMessage importByListener(String fileId,
                                          String className, ExcelImportRule excelImportRule) {
        ResultMessage message = new ResultMessage();
        try {
            MongoFileEntity fileEntity = mongoFileService.getFile(fileId);
            if (fileEntity != null) {
                message.setData(this.importByListener(fileEntity.getInputstream(), className, excelImportRule));
            }
        } catch (Exception e) {
            logger.error("导入监听处理失败：", e);
            message.setCode(-1);
            message.setSuccess(false);
        }
        return message;
    }

    @Override
    public ExcelImportDataReport importByListener(InputStream inputStream, String className, ExcelImportRule excelImportRule) throws Exception {
        List<SheetImportRule> sheetImportRules = excelImportRule.getSheetImportRules();
        ExcelImportDataReport report = ExcelImportListenerHolder.importInstream(inputStream,
                Class.forName(className), excelImportRule);

        // 记录导入过程
        if (excelImportRule.getImportLog() && CollectionUtils.isNotEmpty(sheetImportRules)) {
            ImpExcelDataBatchEntity batchEntity = new ImpExcelDataBatchEntity();
            batchEntity.setCode(excelImportRule.getSheetImportRules().get(0).getTable());
            batchEntity.setBatchNo(DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS") + String.format("%04d", RandomUtils.nextInt(1, 1000)));
            batchEntity.setFailCount(report.getFail().size());
            batchEntity.setSuccessCount(report.getSuccess().size());
            batchEntity.setSystem(RequestSystemContextPathResolver.system());
            batchEntity.setTenant(SpringSecurityUtils.getCurrentTenantId());
            List<ImpExcelDataBatchDetailEntity> details = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(report.getSheetResults())) {
                for (ExcelImportDataReport.SheetResult sheetResult : report.getSheetResults()) {
                    for (ExcelRowDataAnalysedResult r : sheetResult.getResults()) {
                        ImpExcelDataBatchDetailEntity detail = new ImpExcelDataBatchDetailEntity();
                        detail.setDataJson(r.getDataJson());
                        detail.setRemark(r.getMsg());
                        detail.setRowIndex(r.getRowIndex());
                        detail.setSheet(sheetResult.getSheetName());
                        detail.setSheetIndex(sheetResult.getSheetIndex());
                        detail.setSuccess(r.isOk());
                        details.add(detail);
                    }
                }
            }

            batchEntity.setDetails(details);
            implExcelDataBatchService.saveBatch(batchEntity);
            report.setBatchUuid(batchEntity.getUuid());
        }
        return report;
    }

    @Override
    public ResultMessage generateImportDataReportExcel(String fileId, Integer type,
                                                       ExcelImportDataReport report) {
        ResultMessage message = new ResultMessage();
        MongoFileEntity fileEntity = mongoFileService.getFile(fileId);
        if (fileEntity != null) {
            Workbook workbook = null;
            try {
                workbook = WorkbookFactory.create(fileEntity.getInputstream());
            } catch (Exception ex) {
                logger.error("导入文件流处理异常：", Throwables.getStackTraceAsString(ex));
                throw new RuntimeException(ex);
            }
            // 第一个工作表
            Sheet sheet = workbook.getSheetAt(0);
            int shiftRowCnt = 0, lastRownum = sheet.getLastRowNum();

            if (type != null) {
                List<ExcelRowDataAnalysedResult> resultList = type == 1 ? report.getFail() : report.getSuccess();
                for (ExcelRowDataAnalysedResult result : resultList) {
                    Row row = sheet.getRow(result.getRowIndex() - 1 - shiftRowCnt);
                    if (row != null) {
                        sheet.shiftRows(result.getRowIndex() - shiftRowCnt,
                                lastRownum, -1);
                        shiftRowCnt++;
                    }
                }
            }
            try {
                String fileName = Config.TMP_DIR + File.separator + UuidUtils.createUuid() + ".xlsx";
                File tempExportFile = new File(fileName);
                FileOutputStream outputStream = new FileOutputStream(tempExportFile);
                workbook.write(outputStream);
                IOUtils.closeQuietly(outputStream);
                FileInputStream inputStream = new FileInputStream(tempExportFile);
                fileEntity = mongoFileService.saveFile(
                        "导入" + (type == 1 ? "成功_" : "失败_") + fileEntity.getFileName(), inputStream);
                message.setData(fileEntity.getFileID());
                IOUtils.closeQuietly(inputStream);
                new File(fileName).deleteOnExit();
            } catch (Exception e) {
            }

        }
        return message;
    }

    /**
     * 非空检验
     *
     * @param fileId
     * @param dmsDataImportBean
     */

    void checkNotNull(String fileId, DmsDataImportBean dmsDataImportBean) {
        Preconditions.checkNotNull(fileId);
        Preconditions.checkNotNull(dmsDataImportBean.getDynamicTable());
        Preconditions.checkNotNull(dmsDataImportBean.getBeginRow());
        Preconditions.checkNotNull(dmsDataImportBean.getFieldConfigs());
    }


    /**
     * @param dyFormDataImports
     * @param dmsDataImportFiledMaps
     * @param dataReport
     * @return
     */
    private void validate(Map<Integer, DyFormData> dyFormDataImports,
                          Map<String, DmsDataImportFiledBean> dmsDataImportFiledMaps,
                          ExcelImportDataReport dataReport) {
        Set<Integer> rowIndexs = dyFormDataImports.keySet();
        Set<Integer> removes = Sets.newHashSet();
        for (Integer key : rowIndexs) {
            try {
                DyFormData dyFormData = dyFormDataImports.get(key);
                String error = validateRule(dyFormData, dmsDataImportFiledMaps);
                if (StringUtils.isBlank(error)) {
                    dataReport.getSuccess().add(
                            new ExcelRowDataAnalysedResult(true, null, key + 1));
                } else {
                    dataReport.getFail().add(
                            new ExcelRowDataAnalysedResult(false, error, key + 1));
                    removes.add(key);
                }
            } catch (Exception e) {
                dataReport.getFail().add(
                        new ExcelRowDataAnalysedResult(false, "校验数据处理异常", key + 1));
                removes.add(key);
            }
        }
        for (Integer rk : removes) {
            dyFormDataImports.remove(rk);
        }

    }

    private String validateRule(DyFormData dyFormData, Map<String, DmsDataImportFiledBean> maps) {
        String filedName = "";
        DmsDataImportFiledBean bean = null;
        StringBuilder error = new StringBuilder("");
        for (Map.Entry<String, DmsDataImportFiledBean> entry : maps.entrySet()) {
            filedName = entry.getKey();
            bean = entry.getValue();
            DmsDataImportFiledRuleBean ruleBean = bean.getRule();
            DmsDataImportFiledDefaultsBean defaultsBean = bean.getDefaults();
            if (ruleBean == null) {
                continue;
            }
            //1.校验是否必填
            if (StringUtils.isNotBlank(ruleBean.getRequireds())) {
                validateRuleRequireds(dyFormData, ruleBean, error, filedName);
            }
            //2.校验方式
            if (StringUtils.isNotBlank(ruleBean.getVerification())) {
                validateRuleVerification(dyFormData, ruleBean, error, filedName);
            }
            //3.设置默认值
            if (defaultsBean != null) {
                setDefaultsValue(dyFormData, defaultsBean, error, filedName);
            }
        }
        return error.toString();

    }

    /**
     * 校验是否必填
     *
     * @param dyFormData
     * @param ruleBean
     * @param error
     * @param filedName
     */
    private void validateRuleRequireds(DyFormData dyFormData, DmsDataImportFiledRuleBean ruleBean,
                                       StringBuilder error,
                                       String filedName) {
        if (StringUtils.equals("1", ruleBean.getRequireds())) {
            if (org.springframework.util.StringUtils.isEmpty(dyFormData.getFieldValue(filedName))) {
                error.append(filedName + "字段为空");
            }
        }

    }

    /**
     * 校验校验方式
     *
     * @param dyFormData
     * @param ruleBean
     * @param error
     * @param filedName
     */
    private void validateRuleVerification(DyFormData dyFormData,
                                          DmsDataImportFiledRuleBean ruleBean,
                                          StringBuilder error, String filedName) {
        //自定义正则表达式
        if (StringUtils.equals(DataImportRuleTypeEnum.REGULAR.getType(),
                ruleBean.getVerification())) {
            Object filedValue = dyFormData.getFieldValue(filedName);
            String regularValue = ruleBean.getRegularValue();
            if (filedValue != null) {
                if (StringUtils.isNotBlank(regularValue)) {
                    if (!Pattern.matches(regularValue, String.valueOf(filedValue))) {
                        error.append(filedName + "字段:" + ruleBean.getVerificationText() + "校验失败；");
                    }
                }

            }
        }
        //自定义groovy达式
        if (StringUtils.equals(DataImportRuleTypeEnum.GROOVY.getType(),
                ruleBean.getVerification())) {
            Object filedValue = dyFormData.getFieldValue(filedName);
            String groovyValue = ruleBean.getGroovyValue();
            if (filedValue != null) {
                if (StringUtils.isNotBlank(groovyValue)) {
                    Map<String, Object> maps = Maps.newHashMap();
                    maps.put("_formData", dyFormData.getFormDataOfMainform());
                    maps.put("_fieldValue", filedValue);
                    boolean out = (boolean) GroovyUtils.run(groovyValue, maps);
                    if (!out) {
                        error.append(filedName + "groovy 校验失败；");
                    }
                }

            }
        }
    }

    /**
     * 设置默认值
     *
     * @param dyFormData
     * @param defaultsBean
     * @param row
     * @param fieldName
     */
    private void setDefaultsValue(DyFormData dyFormData,
                                  DmsDataImportFiledDefaultsBean defaultsBean,
                                  StringBuilder error, String fieldName) {
        if (StringUtils.equals(DataImportDefaultTypeEnum.FREEMARK.getType(),
                defaultsBean.getDefaultTypeId())) {
            Object filedValue = dyFormData.getFieldValue(fieldName);
            String defaultValue = defaultsBean.getDefaultValue();
            if (StringUtils.isNotBlank(defaultValue)) {
                try {
                    Map<String, Object> freemarkRoot = Maps.newHashMap();
                    freemarkRoot.put("_fieldValue", filedValue);
                    freemarkRoot.put("_formData", dyFormData.getFormDataOfMainform());
                    //其他可解析的系统变量
                    freemarkRoot.putAll(TemplateEngineFactory.getExplainRootModel());
                    String out = TemplateEngineFactory.getDefaultTemplateEngine().process(
                            defaultValue, freemarkRoot);
                    dyFormData.setFieldValue(fieldName, out);
                } catch (Exception e) {
                    logger.error("导入数据字段freemarker设值异常：", e);
                    error.append("字段[" + fieldName + "]默认值设置异常");
                    return;
                }
            }

        }
        if (StringUtils.equals(DataImportDefaultTypeEnum.OTHER.getType(),
                defaultsBean.getDefaultTypeId())) {
            Object filedValue = dyFormData.getFieldValue(fieldName);
            String defaultValue = defaultsBean.getDefaultValue();
            if (org.springframework.util.StringUtils.isEmpty(filedValue)) {
                if (StringUtils.isNotBlank(defaultValue)) {
                    dyFormData.setFieldValue(fieldName, defaultValue);
                }
            }
        }
        if (StringUtils.equals(DataImportDefaultTypeEnum.GROOVY.getType(),
                defaultsBean.getDefaultTypeId())) {
            Object fieldValue = dyFormData.getFieldValue(fieldName);
            String defaultValue = defaultsBean.getDefaultValue();
            if (StringUtils.isNotBlank(defaultValue)) {
                try {
                    Map<String, Object> maps = Maps.newHashMap();
                    maps.put("_formData", dyFormData.getFormDataOfMainform());
                    maps.put("_fieldValue", fieldValue);
                    Stopwatch timer = Stopwatch.createStarted();
                    Object out = GroovyUtils.run(defaultValue, maps);
                    logger.info("########导入配置，执行groovy计算默认值耗时：{}", timer.stop());
                    dyFormData.setFieldValue(fieldName, out);
                } catch (Exception e) {
                    logger.error("导入数据字段groovy设值异常：", e);
                    error.append("字段[" + fieldName + "]默认值设置异常");
                    return;
                }

            }
        }
    }


    /**
     * @param name
     * @param dataDictionaries
     * @return
     */
    private boolean isExistsNameDataDictionary(String name, List<DataDictionary> dataDictionaries) {
        for (DataDictionary dataDictionary : dataDictionaries) {
            if (StringUtils.equals(dataDictionary.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 如何描述该方法
     *
     * @param fileId
     * @param DmsDataImportBean
     */
    private Map<Integer, DyFormData> getImportDtos(String fileId,
                                                   DmsDataImportBean dmsDataImportBean,
                                                   Map<Integer, DmsDataImportFiledBean> dmsDataImportFiledMaps) {
        Map<Integer, DyFormData> dyFormDataImportDtos = Maps.newLinkedHashMap();
        MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileId);
        InputStream is = null;
        Workbook workbook = null;
        Sheet fSheet = null;
        DyFormData dyFormData = null;
        DmsDataImportFiledBean bean = null;
        String formUuid = "";
        String formId = "";
        String tableName = dmsDataImportBean.getDynamicTable();
        List<DyFormFormDefinition> definitions = dyFormFacade.getFormDefinitionsByTblName(tableName.toLowerCase());
        if (CollectionUtils.isEmpty(definitions)) {
            definitions = dyFormFacade.getFormDefinitionsByTblName(tableName);
        }
        formId = definitions.get(0).getId();
        Preconditions.checkNotNull(formId);
        formUuid = dyFormFacade.getFormUuidById(formId);
        Preconditions.checkNotNull(formUuid);
        int beginRow = Integer.parseInt(
                StringUtils.defaultIfBlank(dmsDataImportBean.getBeginRow(), "2")) - 1;
        try {
            is = mongoFileEntity.getInputstream();

            try {
                workbook = WorkbookFactory.create(is);
            } catch (Exception ex) {
                logger.error("导入文件流处理异常：", Throwables.getStackTraceAsString(ex));
                throw new RuntimeException(ex);

            } finally {
                is.close();
            }
            // 第一个工作表
            fSheet = workbook.getSheetAt(0);
            // 循环行Row
            for (int rowNum = beginRow; rowNum <= fSheet.getLastRowNum(); rowNum++) {
                Row row = fSheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                short start = row.getFirstCellNum();
                short end = row.getLastCellNum();
                dyFormData = dyFormFacade.createDyformData(formUuid);
                boolean existCellData = false;
                for (int i = start; i < end; i++) {
                    Cell cell = row.getCell(i);
                    if (!existCellData && cell != null && !cell.getCellTypeEnum().equals(
                            CellType.BLANK)) {
                        existCellData = true;
                    }
                    bean = dmsDataImportFiledMaps.get(i + 1);
                    if (bean != null) {
                        dyFormData.setFieldValue(bean.getName().toUpperCase(),
                                getStringCellValue(row.getCell(i)));
                    }
                }
                if (existCellData) {
                    setUuidFieldKeyValues(dmsDataImportBean, dyFormData);//缓存数据
                    dyFormDataImportDtos.put(rowNum, dyFormData);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("读取文件数据出错，请按导入模板及说明填入数据！");
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(workbook);
        }
        return dyFormDataImportDtos;
    }

    Map<Integer, DmsDataImportFiledBean> getDmsDataImportFiledMapsByRow(
            DmsDataImportBean dmsDataImportBean) {
        List<DmsDataImportFiledBean> list = dmsDataImportBean.getFieldConfigs();
        Map<Integer, DmsDataImportFiledBean> maps = Maps.uniqueIndex(list,
                new Function<DmsDataImportFiledBean, Integer>() {
                    public Integer apply(DmsDataImportFiledBean from) {
                        if (StringUtils.isNotBlank(from.getCell())) {
                            return Integer.valueOf(from.getCell());
                        } else {
                            return from.getName().hashCode();
                        }
                    }
                });
        return maps;
    }

    Map<String, DmsDataImportFiledBean> getDmsDataImportFiledMapsByName(
            DmsDataImportBean dmsDataImportBean) {
        List<DmsDataImportFiledBean> list = dmsDataImportBean.getFieldConfigs();
        Map<String, DmsDataImportFiledBean> maps = Maps.uniqueIndex(list,
                new Function<DmsDataImportFiledBean, String>() {
                    public String apply(DmsDataImportFiledBean from) {
                        return from.getName();
                    }
                });
        return maps;
    }

    /**
     * @param cell
     * @return
     */
    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return StringUtils.EMPTY;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return StringUtils.EMPTY + getDoubleCellValue(cell);
        }
        return StringUtils.trim(cell.getStringCellValue());
    }

    /**
     * @param cell
     * @return
     */
    private Double getDoubleCellValue(Cell cell) {
        if (cell == null) {
            return Double.valueOf(0);
        }
        return Double.valueOf(cell.getNumericCellValue());
    }

    /**
     * 唯一性校验
     *
     * @param key
     * @return
     */
    boolean isExistsByKey(String key, List<String> list) {
        if (list.contains(key)) {
            return true;
        }
        return false;
    }

    ;

    /**
     * 设置唯一性字段和字段值
     *
     * @param dmsDataImportBean
     * @param dyFormData
     */
    void setUuidFieldKeyValues(DmsDataImportBean dmsDataImportBean, DyFormData dyFormData) {
        String fields = dmsDataImportBean.getUniquenessField();
        if (StringUtils.isNotBlank(fields)) {
            Iterable<String> list = Splitter.on(Separator.SEMICOLON.getValue()).trimResults().split(
                    fields);
            Map<String, Object> fieldKeyValues = Maps.newHashMap();
            for (String field : list) {
                fieldKeyValues.put(field, dyFormData.getFieldValue(field));
            }
            uuidFieldKeyValues.get().put(dyFormData.getDataUuid(), fieldKeyValues);
        }
    }

}
