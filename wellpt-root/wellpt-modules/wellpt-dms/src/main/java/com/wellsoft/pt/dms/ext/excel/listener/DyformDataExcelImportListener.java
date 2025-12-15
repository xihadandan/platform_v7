package com.wellsoft.pt.dms.ext.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.excel.AbstractEasyExcelImportListener;
import com.wellsoft.context.util.excel.ExcelRowDataAnalysedResult;
import com.wellsoft.context.util.excel.SheetImportRule;
import com.wellsoft.pt.dm.jdbc.Model;
import com.wellsoft.pt.dm.jdbc.service.ModelService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.dyform.implement.repository.adapter.FormDataServiceAdapter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年05月24日   chenq	 Create
 * </pre>
 */
public class DyformDataExcelImportListener extends AbstractEasyExcelImportListener<HashMap<Integer, String>> {

    DyFormFacade dyFormFacade;


    ModelService modelService;

    FormDefinitionService formDefinitionService;

    FormDataServiceAdapter formDataServiceAdapter;


    public DyformDataExcelImportListener() {
        dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        modelService = ApplicationContextHolder.getBean(ModelService.class);
        formDefinitionService = ApplicationContextHolder.getBean(FormDefinitionService.class);
        formDataServiceAdapter = ApplicationContextHolder.getBean(FormDataServiceAdapter.class);
    }


    @Override
    public ExcelRowDataAnalysedResult dataAnalysed(HashMap<Integer, String> dataMap, int rowIndex, AnalysisContext analysisContext) {
        Map<String, Object> params = this.currentSheetParams(analysisContext);
        ExcelRowDataAnalysedResult result = new ExcelRowDataAnalysedResult();
        if (params != null) {
            SheetImportRule sheetRule = this.currentSheetImportRule(analysisContext);
            String formUuid = null;
            if ("dyform".equalsIgnoreCase(params.get("importToType").toString())) {
                formUuid = params.get("formUuid").toString();
            } else {
                // 查询表对应的最新表单定义
                formUuid = formDefinitionService.getFormDefinitionUuidOfMaxVersionByTblName(sheetRule.getTable());
            }
            DyFormData dyFormData = null;
            Model model = null;
            if (formUuid != null) {
                dyFormData = dyFormFacade.createDyformData(formUuid);
            } else {
                model = new Model(sheetRule.getTable());
            }
            Set<Map.Entry<Integer, String>> entries = dataMap.entrySet();
            Map<String, Object> fieldValue = Maps.newHashMap();
            Map<String, Object> duplicateFieldValue = Maps.newHashMap();

            StringBuilder duplicateDataQueryCondition = new StringBuilder("");
            for (Map.Entry<Integer, String> ent : entries) {
                SheetImportRule.HeaderRule rule = this.getHeaderRule(ent.getKey(), analysisContext);
                if (rule != null && StringUtils.isNotBlank(rule.getCode()) && StringUtils.isNotBlank(ent.getValue())) {
                    Object value = this.transformCellValue(ent.getValue(), ent.getKey(), analysisContext);
                    if (dyFormData != null) {
                        dyFormData.setFieldValue(rule.getCode(), value);
                    }
                    if (model != null) {
                        model.setPropValue(rule.getCode(), value);
                    }
                    fieldValue.put(rule.getCode(), value);
                    if (CollectionUtils.isNotEmpty(sheetRule.getDuplicateDataHeader()) && sheetRule.getDuplicateDataHeader().contains(rule.getTitle())
                            && StringUtils.isNotBlank(rule.getCode())) {
                        duplicateDataQueryCondition.append(rule.getCode()).append("=:").append(rule.getCode());
                        duplicateFieldValue.put(rule.getCode(), value);
                    }
                }
            }
            boolean update = false;
            if (formUuid != null) {
                try {
                    if (SheetImportRule.DuplicateStrategy.update.equals(sheetRule.getDuplicateStrategy())
                            && duplicateDataQueryCondition.length() > 0) {
                        List<String> uuids = formDataServiceAdapter.queryUniqueForFields(formUuid, duplicateFieldValue, null);
                        if (CollectionUtils.isNotEmpty(uuids)) {
                            dyFormData.setDataUuid(uuids.get(0));
                            update = true;
                        }
                    }
                } catch (Exception e) {
                    logger.error("导入数据判断数据重复性异常: ", e);
                    return result.fail("判断数据重复性异常");
                }
                // 保存关联关系表
                SheetImportRule.Join join = sheetRule.getJoin();
                if (join != null && StringUtils.isNotBlank(join.getTable()) && StringUtils.isNotBlank(join.getJoinHeader())) {
                    Map<String, Object> joinFieldValue = Maps.newHashMap();
                    SheetImportRule.HeaderRule headerRule = sheetRule.getHeaderRuleByTitle(join.getJoinHeader());
                    if (headerRule != null) {
                        joinFieldValue.put(join.getJoinColumn(), dataMap.get(headerRule.getIndex()));
                        List<String> uuids = formDataServiceAdapter.queryUniqueForFields(join.getFormUuid()
                                , joinFieldValue, null);
                        if (CollectionUtils.isNotEmpty(uuids)) {
                            if (CollectionUtils.isNotEmpty(sheetRule.getDuplicateDataHeader()) && sheetRule.getDuplicateStrategy() != null) {
                                Map<String, Object> queryParam = Maps.newHashMap();
                                queryParam.put("mDataUuid", uuids.get(0));
                                queryParam.put("mFormUuid", join.getFormUuid());
                                StringBuilder joinQuery = new StringBuilder(" select r.data_uuid as uuid from ");
                                joinQuery.append(sheetRule.getTable()).append("_rl r where ").append(" r.mainform_data_uuid=:mDataUuid ")
                                        .append(" and r.mainform_form_uuid =:mFormUuid and exists ( select 1 from ").append(sheetRule.getTable())
                                        .append(" s where s.uuid = r.data_uuid ");
                                for (String title : sheetRule.getDuplicateDataHeader()) {
                                    SheetImportRule.HeaderRule r = sheetRule.getHeaderRuleByTitle(title);
                                    if (r == null || StringUtils.isBlank(r.getCode()) || !sheetRule.getDuplicateDataHeader().contains(r.getTitle())) {
                                        continue;
                                    }
                                    String code = r.getCode();
                                    joinQuery.append(" and s.").append(code).append(" =:").append(code);
                                    queryParam.put(code, this.transformCellValue(dataMap.get(r.getIndex()), r.getIndex(), analysisContext));
                                }
                                joinQuery.append(")");
                                List<Map<String, Object>> exists = jdbcTemplate.queryForList(joinQuery.toString(), queryParam);
                                if (CollectionUtils.isNotEmpty(exists)) {
                                    if (SheetImportRule.DuplicateStrategy.ignore.equals(sheetRule.getDuplicateStrategy())) {
                                        // 数据重复，跳过
                                        result.setMsg("数据重复跳过");
                                        return result;
                                    }
                                    dyFormData.setDataUuid(exists.get(0).get("uuid").toString());
                                    update = true;
                                }
                            }

                            Map<String, List<Map<String, Object>>> subFormDatas = dyFormData.getFormDatas();
                            Map<String, Object> mainDataMap = Maps.newHashMap();
                            mainDataMap.put("uuid", uuids.get(0));
                            subFormDatas.put(join.getFormUuid(), Lists.newArrayList(mainDataMap));
                            DyFormData mainFormData = dyFormFacade.createDyformData(join.getFormUuid());
                            mainFormData.setDataUuid(uuids.get(0));
                            mainFormData.getAddedFormDatas().remove(join.getFormUuid());
                            mainFormData.addSubformData(dyFormData);
                            if (update) {
                                // 更新从表数据
                                mainFormData.getAddedFormDatas().remove(formUuid);
                                Map<String, Set<String>> updateFields = Maps.newHashMap();
                                Set<String> field = Sets.newHashSet();
                                for (SheetImportRule.HeaderRule r : sheetRule.getHeader()) {
                                    if (StringUtils.isNotBlank(r.getCode())) {
                                        field.add(r.getCode());
                                    }
                                }
                                updateFields.put(dyFormData.getDataUuid(), field);
                                mainFormData.getUpdatedFormDatas().put(formUuid, updateFields);

                            }
                            formDataServiceAdapter.saveFormData(join.getFormUuid(), mainFormData.getFormDatas(),
                                    null, mainFormData.getUpdatedFormDatas(), mainFormData.getAddedFormDatas(), null);
                            return result;

                        }
                    }

                }

                formDataServiceAdapter.saveFormData(formUuid, dyFormData.getFormDatas(), null, null);

            } else {
                List<Long> uuids = modelService.queryUuidsByFields(model.getTable(), fieldValue, duplicateDataQueryCondition.toString());
                if (CollectionUtils.isNotEmpty(uuids)) {
                    model.setUuid(uuids.get(0));
                }
                modelService.saveOrUpdate(model);
            }
        }

        return result;

    }

    @Override
    protected void allAnalysed(AnalysisContext analysisContext) {
    }

    @Override
    public String name() {
        return "表单数据导入";
    }
}
