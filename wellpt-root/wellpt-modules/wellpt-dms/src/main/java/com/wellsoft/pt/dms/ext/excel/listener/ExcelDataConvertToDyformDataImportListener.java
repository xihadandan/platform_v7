package com.wellsoft.pt.dms.ext.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.excel.AbstractEasyExcelImportListener;
import com.wellsoft.context.util.excel.ExcelRowDataAnalysedResult;
import com.wellsoft.context.util.excel.SheetImportRule;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryEntity;
import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryItemEntity;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryItemService;
import com.wellsoft.pt.basicdata.datadict.service.CdDataDictionaryService;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreData;
import com.wellsoft.pt.basicdata.datastore.bean.DataStoreParams;
import com.wellsoft.pt.basicdata.datastore.facade.service.CdDataStoreService;
import com.wellsoft.pt.dm.service.DataModelService;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.service.FormDefinitionService;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.org.service.OrgElementService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年06月14日   chenq	 Create
 * </pre>
 */
public class ExcelDataConvertToDyformDataImportListener extends AbstractEasyExcelImportListener<HashMap<Integer, String>> {

    FormDefinitionService formDefinitionService;

    OrgElementService orgElementService;

    CdDataDictionaryItemService cdDataDictionaryItemService;

    CdDataStoreService cdDataStoreService;

    DataModelService dataModelService;
    List<Map<String, String>> records = Lists.newArrayList();
    Map<String, Object> cache = Maps.newHashMap();


    public ExcelDataConvertToDyformDataImportListener() {
        formDefinitionService = ApplicationContextHolder.getBean(FormDefinitionService.class);
        orgElementService = ApplicationContextHolder.getBean(OrgElementService.class);
        cdDataStoreService = ApplicationContextHolder.getBean(CdDataStoreService.class);
        cdDataDictionaryItemService = ApplicationContextHolder.getBean(CdDataDictionaryItemService.class);
        dataModelService = ApplicationContextHolder.getBean(DataModelService.class);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext analysisContext) {
        super.invokeHeadMap(headMap, analysisContext);
        ReadSheet sheet = analysisContext.readSheetHolder().getReadSheet();
        if (analysisContext.readRowHolder().getRowIndex() != sheet.getHeadRowNumber()) {
            return;
        }
        // 解析
        Map<String, Object> params = this.currentSheetParams(analysisContext);
        String formUuid = (String) params.get("formUuid");
        if (StringUtils.isBlank(formUuid)) {
            this.stopImport();
            return;
        }
        // 解析表单字段列
        FormDefinition definition = formDefinitionService.getOne(formUuid);
        if (definition == null) {
            this.stopImport();
            return;
        }
        JSONArray fieldArray = JSONObject.fromObject(definition.getDefinitionVjson()).getJSONArray("fields");
        Map<String, String> fieldWtypeMap = Maps.newHashMap();
        Iterator<JSONObject> fieldIterator = fieldArray.iterator();
        while (fieldIterator.hasNext()) {
            JSONObject f = fieldIterator.next();
            fieldWtypeMap.put(f.getJSONObject("configuration").getString("code"), f.getString("wtype"));
        }
        SheetImportRule importRule = this.currentSheetImportRule(analysisContext);
        JSONObject fieldJsonObject = JSONObject.fromObject(definition.getDefinitionJson()).getJSONObject("fields");
        List<String> optionWidgets = Lists.newArrayList("WidgetFormSelect", "WidgetFormRadio", "WidgetFormCheckbox", "WidgetFormTag");
        List<SheetImportRule.HeaderRule> headerRules = importRule.getHeader();
        // 选项数据准备
        for (SheetImportRule.HeaderRule rule : headerRules) {
            JSONObject field = fieldJsonObject.getJSONObject(rule.getCode());
            JSONObject configuration = field.getJSONObject("configuration");
            String dbDataType = field.getString("dbDataType");
            // 选项型的字段
            if (configuration.containsKey("code") && optionWidgets.contains(fieldWtypeMap.get(configuration.getString("code")))) {
                String wtype = fieldWtypeMap.get(configuration.getString("code"));
                if (wtype.equalsIgnoreCase("WidgetFormTag")
                        && configuration.containsKey("tagEditMode") && configuration.getString("tagEditMode").equalsIgnoreCase("input")) {
                    continue;
                }
                JSONObject options = configuration.getJSONObject("options");
                String type = options.getString("type");
                if ("selfDefine".equalsIgnoreCase(type)) {
                    // 自定义的选项
                    rule.setTransformValueType(SheetImportRule.HeaderRule.TransformValueType.define);

                    if (configuration.containsKey("type") && "select-tree".equalsIgnoreCase(configuration.getString("type")) && configuration.containsKey("treeData")) {
                        // 树形组件的自定义数据在treeData
                        JSONArray treeData = configuration.getJSONArray("treeData");

                        List<Map<String, String>> treeListOptions = this.cascadeTreeDataToOptions(treeData, "display", "real", "children");
                        rule.setTransformValueOption(treeListOptions);


                    } else if (options.containsKey("defineOptions")) {
                        JSONArray defineOptions = options.getJSONArray("defineOptions");
                        rule.setTransformValueOption(jsonArrayToListOfMaps(defineOptions));
                    }

//                    treeData
                } else if ("dataDictionary".equalsIgnoreCase(type)) {
                    // 数据字典
                    String dataDictionaryUuid = options.getString("dataDictionaryUuid");
                    CdDataDictionaryService cdDataDictionaryService = ApplicationContextHolder.getBean(CdDataDictionaryService.class);
                    CdDataDictionaryEntity dataDictionaryEntity = cdDataDictionaryService.getByCode(dataDictionaryUuid);
                    if (dataDictionaryEntity == null && NumberUtils.isNumber(dataDictionaryUuid)) {
                        dataDictionaryEntity = cdDataDictionaryService.getOne(Long.parseLong(dataDictionaryUuid));
                    }
                    if (dataDictionaryEntity != null) {
                        List<CdDataDictionaryItemEntity> itemEntities = cdDataDictionaryItemService.listByDataDictUuid(dataDictionaryEntity.getUuid());
                        if (CollectionUtils.isNotEmpty(itemEntities)) {
                            List<Map<String, String>> list = Lists.newArrayList();
                            for (CdDataDictionaryItemEntity item : itemEntities) {
                                Map<String, String> map = Maps.newHashMap();
                                map.put("label", item.getLabel());
                                map.put("value", item.getValue());
                                list.add(map);
                            }
                            rule.setTransformValueType(SheetImportRule.HeaderRule.TransformValueType.define);
                            rule.setTransformValueOption(list);
                        }
                    }


                } else if ("dataModel".equalsIgnoreCase(type) || "dataSource".equalsIgnoreCase(type)) {
                    DataStoreData dataStoreData = new DataStoreData();
                    // 数据仓库
                    if ("dataModel".equalsIgnoreCase(type)) {
                        String dataSourceId = options.getString("dataSourceId");
                        DataStoreParams dataStoreParams = new DataStoreParams();
                        dataStoreParams.setDataStoreId(dataSourceId);
                        dataStoreData = cdDataStoreService.loadData(dataStoreParams);
                    } else {
                        // 数据模型
                        String dataModelUuid = options.getString("dataModelUuid");
                        dataStoreData = dataModelService.loadDataStoreData(Long.parseLong(dataModelUuid), new DataStoreParams());
                    }
                    List<Map<String, Object>> dataList = dataStoreData.getData();
                    List<Map<String, String>> list = Lists.newArrayList();
                    String dataSourceLabelColumn = options.getString("dataSourceLabelColumn");
                    String dataSourceValueColumn = options.getString("dataSourceValueColumn");
                    for (Map<String, Object> item : dataList) {
                        Map<String, String> map = Maps.newHashMap();
                        map.put("label", item.get(dataSourceLabelColumn).toString());
                        map.put("value", item.get(dataSourceValueColumn).toString());
                        list.add(map);
                    }
                    rule.setTransformValueType(SheetImportRule.HeaderRule.TransformValueType.define);
                    rule.setTransformValueOption(list);
                }

            } else if (dbDataType.equals("2")) {
                // 日期格式
                rule.setFormat(field.getString("contentFormat"));
                rule.setDataType(SheetImportRule.HeaderRule.DataType.date);
            } else if ("WidgetFormOrgSelect".equalsIgnoreCase(fieldWtypeMap.get(configuration.getString("code")))) {
                // 组织节点转换
                rule.setTransformValueType(SheetImportRule.HeaderRule.TransformValueType.orgElement);
                rule.setTransformValueOrgUuid(StringUtils.defaultIfBlank(configuration.optString("orgUuid"), null));
            }
        }
    }

    private static List<Map<String, String>> cascadeTreeDataToOptions(JSONArray treeData, String display, String real, String childrenField) {
        List<Map<String, String>> list = Lists.newArrayList();
        if (treeData != null && treeData.size() > 0) {
            Iterator<JSONObject> treeIterator = treeData.iterator();
            while (treeIterator.hasNext()) {
                JSONObject child = treeIterator.next();
                Map<String, String> item = Maps.newHashMap();
                item.put("label", child.getString(display));
                item.put("value", child.getString(real));
                list.add(item);
                if (child.containsKey(childrenField)) {
                    JSONArray children = child.getJSONArray(childrenField);
                    if (children.size() > 0) {
                        List<Map<String, String>> subList = cascadeTreeDataToOptions(children, display, real, childrenField);
                        if (CollectionUtils.isNotEmpty(subList)) {
                            list.addAll(subList);
                        }
                    }
                }
            }
        }
        return list;

    }

    @Override
    public ExcelRowDataAnalysedResult dataAnalysed(HashMap<Integer, String> dataMap, int rowIndex, AnalysisContext analysisContext) {
        Set<Map.Entry<Integer, String>> entrySet = dataMap.entrySet();
        Map<String, Object> record = Maps.newHashMap();
        ExcelRowDataAnalysedResult result = new ExcelRowDataAnalysedResult();
        for (Map.Entry<Integer, String> entry : entrySet) {
            SheetImportRule.HeaderRule headerRule = this.getHeaderRule(entry.getKey(), analysisContext);
            String val = entry.getValue();
            if (StringUtils.isNotBlank(headerRule.getCode())) {
                record.put(headerRule.getCode(), val);
            } else {
                record.put(headerRule.getTitle(), val);
            }
            if (SheetImportRule.HeaderRule.TransformValueType.define.equals(headerRule.getTransformValueType())) {
                if (CollectionUtils.isNotEmpty(headerRule.getTransformValueOption()) && StringUtils.isNotBlank(val)) {
                    String[] valueParts = val.split(Separator.SEMICOLON.getValue());
                    List<String> realValues = Lists.newArrayList();
                    List<String> labels = Lists.newArrayList();
                    for (String v : valueParts) {
                        for (Map<String, String> opt : headerRule.getTransformValueOption()) {
                            if (opt.get("label").equals(v)) {
                                realValues.add(opt.get("value"));
                                labels.add(opt.get("label"));

                            }
                        }
                    }

                    record.put(headerRule.getCode(), StringUtils.join(realValues, Separator.SEMICOLON.getValue()));
                    record.put(headerRule.getCode() + "$LABEL", StringUtils.join(labels, Separator.SEMICOLON.getValue()));


                }
            } else if (SheetImportRule.HeaderRule.DataType.date.equals(headerRule.getDataType())) {
                List<String> dateTryFormats = Lists.newArrayList("yyyy/MM/dd HH:mm", "yyyy/MM/dd", "yyyy-MM-dd", "yyyy年MM月dd日"
                        , "yyyy-MM-dd HH:mm:ss", "yyyy年MM月dd日 HH时mm分ss秒");
                if (StringUtils.isNotBlank(headerRule.getFormat())) {
                    dateTryFormats.add(0, headerRule.getFormat());
                }
                Date date = null;
                try {
                    date = DateUtils.parseDate(val, dateTryFormats.toArray(new String[]{}));
                } catch (Exception e) {
                    if (NumberUtils.isNumber(val)) {
                        date = DateUtil.getJavaDate(Double.valueOf(val), analysisContext.currentReadHolder().globalConfiguration().getUse1904windowing(), null);
                    }
                }
                if (date != null) {
                    record.put(headerRule.getCode(), DateFormatUtils.format(date, headerRule.getFormat()));
                }

            } else if (SheetImportRule.HeaderRule.TransformValueType.orgElement.equals(headerRule.getTransformValueType())) {
                OrgSelectProvider.Params params = new OrgSelectProvider.Params();
                params.put("orgUuid", headerRule.getTransformValueOrgUuid());
                params.put("titles", Arrays.asList(val.split(Separator.SEMICOLON.getValue())));
                List<OrgSelectProvider.Node> nodes = null;
                if (cache.containsKey(headerRule.getTransformValueOrgUuid() + val)) {
                    nodes = (List<OrgSelectProvider.Node>) cache.get(headerRule.getTransformValueOrgUuid() + val);
                } else {
                    nodes = orgElementService.getTreeNodesByTitles("MyOrg", params);
                    cache.put(headerRule.getTransformValueOrgUuid() + val, nodes);
                }
                if (CollectionUtils.isNotEmpty(nodes)) {
                    List<String> values = Lists.newArrayList();
                    List<String> labels = Lists.newArrayList();
                    for (OrgSelectProvider.Node n : nodes) {
                        values.add(n.getKey());
                        labels.add(n.getTitle());
                    }
                    record.put(headerRule.getCode(), StringUtils.join(values, Separator.SEMICOLON.getValue()));
                    record.put(headerRule.getCode() + "$LABEL", StringUtils.join(labels, Separator.SEMICOLON.getValue()));
                }
            }

        }
        result.setDataAnalysed(record);
        return result;
    }

    @Override
    public String name() {
        return "Excel数据转为表单数据";
    }

    @Override
    protected ExcelRowDataAnalysedResult ruleAnalyse(HashMap<Integer, String> dataMap, int rowIndex, AnalysisContext analysisContext) {
        return new ExcelRowDataAnalysedResult();
    }

    private List<Map<String, String>> jsonArrayToListOfMaps(JSONArray jsonArray) {
        List<Map<String, String>> list = Lists.newArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map<String, String> map = new HashMap<>();
            ((Set<String>) jsonObject.keySet()).forEach(key -> map.put(key, jsonObject.getString(key)));
            list.add(map);
        }
        return list;
    }

}
