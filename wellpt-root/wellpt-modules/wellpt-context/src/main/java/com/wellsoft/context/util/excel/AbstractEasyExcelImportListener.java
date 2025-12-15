package com.wellsoft.context.util.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * Description: excel导入基类
 * (注意：实现导入基类的实例类不允许托管Spring，如果想要使用spring服务实例类，则通过构造函数传入初始化变量)
 *
 * @author chenq
 * @date 2019/12/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/12/9    chenq		2019/12/9		Create
 * </pre>
 */
public abstract class AbstractEasyExcelImportListener<T extends Serializable> extends
        AnalysisEventListener<T> {

    private static Map<Class, Set<Field>> fieldMap = new HashMap<>();
    protected Logger logger = LoggerFactory.getLogger(AnalysisEventListener.class);
    private Stopwatch couter = Stopwatch.createStarted();
    private AtomicInteger total = new AtomicInteger(0);
    private AtomicInteger success = new AtomicInteger(0);
    private ExcelImportDataReport dataReport = new ExcelImportDataReport();
    private AtomicInteger currentSheetTotalRows = new AtomicInteger(0);
    private AtomicInteger currentSheetSuccess = new AtomicInteger(0);
    private List<SheetImportRule> sheetImportRules = null;
    protected ExcelImportRule excelImportRule = null;
    private Map<String, Pattern> patterns = Maps.newHashMap();
    protected NamedParameterJdbcTemplate jdbcTemplate;
    private Map<String, List<Map<String, Object>>> valueOptions = Maps.newHashMap();
    protected ThreadLocal<Boolean> stop = new ThreadLocal<>();

    public AbstractEasyExcelImportListener() {
        jdbcTemplate = ApplicationContextHolder.getBean(NamedParameterJdbcTemplate.class);
        stop.set(false);
    }

    protected void stopImport() {
        stop.set(true);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        ReadSheet sheet = context.readSheetHolder().getReadSheet();
        if (dataReport.getSheetResults().size() == sheet.getSheetNo()) {
            dataReport.getSheetResults().add(new ExcelImportDataReport.SheetResult(sheet.getSheetNo(), sheet.getSheetName()));
        }
        if (context.readRowHolder().getRowIndex() != sheet.getHeadRowNumber()) {
            return;
        }
        int sheetNo = context.readSheetHolder().getSheetNo();
        ExcelRowDataAnalysedResult result = new ExcelRowDataAnalysedResult(false, "工作表列与模板不匹配，无法导入");
        if (sheetNo >= sheetImportRules.size()) {
            // 多余的sheet页，不进行导入
            result.fail("超过可导入的工作表数量: " + sheetImportRules.size());
            dataReport.addSheetResult(sheet, result);
            dataReport.getFail().add(result);
            stop.set(true);
            return;
        }
        // 严格校验导入标题是否与定义的规则一致
        if (BooleanUtils.isTrue(excelImportRule.getStrict()) && CollectionUtils.isNotEmpty(sheetImportRules)) {
            // 校验当前sheet的标题头是否与规则的头部一致
            List<SheetImportRule.HeaderRule> headerRules = sheetImportRules.get(sheet.getSheetNo()).getHeader();
            if (headerRules.size() != headMap.size()) {
                stop.set(true);
                this.dataReport.getFail().add(result);
                this.dataReport.addSheetResult(context.readSheetHolder().getReadSheet(), result);
                return;
            }
            for (int i = 0, len = headerRules.size(); i < len; i++) {
                if (!headerRules.get(i).getTitle().trim().equalsIgnoreCase(StringUtils.trim(headMap.get(i)))) {
                    stop.set(true);
                    this.dataReport.getFail().add(result);
                    this.dataReport.addSheetResult(context.readSheetHolder().getReadSheet(), result);
                    break;
                }
            }

        }

    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return !stop.get();
    }

    /**
     * 执行每一条导入数据处理
     *
     * @param t
     * @param analysisContext
     */
    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        ExcelRowDataAnalysedResult result = new ExcelRowDataAnalysedResult();
        if (!couter.isRunning()) {
            couter.start();
        }
        ReadSheet sheet = analysisContext.readSheetHolder().getReadSheet();
        logger.info("当前读取sheet页: {}", sheet.getSheetName());


        int rowIndex = analysisContext.readRowHolder().getRowIndex();
        total.incrementAndGet();
        currentSheetTotalRows.incrementAndGet();
        Stopwatch timer = Stopwatch.createStarted();
        String originalData = JsonUtils.object2Gson(t);

        try {
            logger.debug("开始读取第{}行数据", rowIndex + 1);
            if (!this.allFieldIsNull(t)) {
                result = ruleAnalyse(t, rowIndex, analysisContext);
                if (result.isOk() && StringUtils.isBlank(result.getMsg())) {
                    result = dataAnalysed(t, rowIndex, analysisContext);
                }

                if (result.isOk()) {
                    success.incrementAndGet();
                    dataReport.addSheetResult(sheet, result);
                    currentSheetSuccess.incrementAndGet();
                    dataReport.getSuccess().add(result);
                } else {
                    dataReport.addSheetResult(sheet, result);
                    dataReport.getFail().add(result);
                }
            } else {
                result.fail("解析数据为空");
                dataReport.getFail().add(result);
                dataReport.addSheetResult(sheet, result);
            }
        } catch (Exception e) {
            dataReport.getFail().add(result.fail("导入处理异常: " + e.getMessage()));
            dataReport.addSheetResult(sheet, result);
            logger.error("读取第{}行数据异常：{}", rowIndex + 1, Throwables.getStackTraceAsString(e));
        } finally {
            result.setDataJson(originalData);
            result.setRowIndex(rowIndex + 1);//设置真实行标
            logger.debug("结束读取第{}行数据，耗时：{}", rowIndex + 1, timer.stop());
        }
    }

    protected ExcelRowDataAnalysedResult ruleAnalyse(T t, int rowIndex, AnalysisContext analysisContext) {
        ExcelRowDataAnalysedResult result = new ExcelRowDataAnalysedResult();
        SheetImportRule sheetImportRule = this.currentSheetImportRule(analysisContext);
        if (sheetImportRule != null) {
            List<SheetImportRule.HeaderRule> headerRules = sheetImportRule.getHeader();
            if (CollectionUtils.isNotEmpty(headerRules)) {
                if (Map.class.isAssignableFrom(t.getClass())) {
                    Map<Integer, String> dataMap = (Map<Integer, String>) t;
                    Set<Map.Entry<Integer, String>> entries = dataMap.entrySet();
                    for (Map.Entry<Integer, String> ent : entries) {
                        if (ent.getKey() >= headerRules.size()) {
                            continue;
                        }
                        SheetImportRule.HeaderRule headerRule = headerRules.get(ent.getKey());
                        if (BooleanUtils.isTrue(headerRule.getRequired()) && StringUtils.isBlank(ent.getValue())) {
                            result.setOk(false);
                            result.setMsg("列[" + headerRule.getTitle() + "]: 必填");
//                            result.putErrorCellMsg(ent.getKey(), result.getMsg());
                            return result;
                        }

                        if (StringUtils.isNotBlank(headerRule.getRegExp()) && StringUtils.isNotBlank(ent.getValue())) {
                            try {
                                Pattern pattern = this.patterns.get(headerRule.getRegExp());
                                if (pattern == null) {
                                    pattern = Pattern.compile(headerRule.getRegExp());
                                    this.patterns.put(headerRule.getRegExp(), pattern);
                                }
                                if (!pattern.matcher(ent.getValue()).matches()) {
                                    return result.fail("列[" + headerRule.getTitle() + "]: 数据不符合格式");
                                }
                            } catch (Exception e) {
                                result.setOk(false);
                                result.setMsg("列[" + headerRule.getTitle() + "]的正则表达式错误, 无法分析数据格式正确性");
                                return result;
                            }
                        }

                        if (SheetImportRule.DuplicateStrategy.ignore.equals(sheetImportRule.getDuplicateStrategy()) && StringUtils.isNotBlank(sheetImportRule.getTable())
                                && CollectionUtils.isNotEmpty(sheetImportRule.getDuplicateDataHeader())) {
                            if (sheetImportRule.getJoin() != null && StringUtils.isNotBlank(sheetImportRule.getJoin().getTable())
                                    && StringUtils.isNotBlank(sheetImportRule.getJoin().getJoinHeader())
                                    && StringUtils.isNotBlank(sheetImportRule.getJoin().getJoinColumn())) {
                                // 有关联父级表，则重复数据要与父级表数据的UUID有关，关系的方式由实际使用决定
                                continue;
                            }
                            // 判断数据是否重复，进行跳过
                            StringBuilder sql = new StringBuilder("select 1 from ").append(sheetImportRule.getTable());
                            sql.append(" where 1=1 ");
                            Map<String, Object> sqlParameter = Maps.newHashMap();
                            for (String title : sheetImportRule.getDuplicateDataHeader()) {
                                SheetImportRule.HeaderRule r = sheetImportRule.getHeaderRuleByTitle(title);
                                if (r == null || StringUtils.isBlank(r.getCode())) {
                                    continue;
                                }
                                String code = r.getCode();
                                sql.append(" and ").append(code).append(" =:").append(code);
                                sqlParameter.put(code, transformCellValue(dataMap.get(r.getIndex()), r.getIndex(), analysisContext));
                            }
                            try {
                                if (CollectionUtils.isNotEmpty(this.jdbcTemplate.queryForList(sql.toString(), sqlParameter))) {
                                    result.setMsg("数据重复跳过");
                                    return result;
                                }
                            } catch (Exception e) {
                                return result.fail("数据重复性校验异常: " + e.getMessage());
                            }

                        }

                    }
                }
            }

        }
        return result;

    }


    public void setSheetImportRules(List<SheetImportRule> rules) {
        sheetImportRules = rules;
    }

    protected SheetImportRule currentSheetImportRule(AnalysisContext analysisContext) {
        if (sheetImportRules != null && analysisContext.readSheetHolder().getSheetNo() < sheetImportRules.size()) {
            return sheetImportRules.get(analysisContext.readSheetHolder().getSheetNo());
        }
        return null;
    }

    protected Map<String, Object> currentSheetParams(AnalysisContext analysisContext) {
        if (sheetImportRules != null && analysisContext.readSheetHolder().getSheetNo() < sheetImportRules.size()) {
            return sheetImportRules.get(analysisContext.readSheetHolder().getSheetNo()).getParams();
        }
        return null;
    }

    protected SheetImportRule.HeaderRule getHeaderRule(int index, AnalysisContext analysisContext) {
        SheetImportRule sheetImportRule = this.currentSheetImportRule(analysisContext);
        if (sheetImportRule != null) {
            return sheetImportRule.getHeader().get(index);
        }
        return null;
    }

    protected Object transformCellValue(String val, int colIndex, AnalysisContext analysisContext) {
        SheetImportRule.HeaderRule headerRule = this.getHeaderRule(colIndex, analysisContext);
        if (headerRule != null) {
            if (SheetImportRule.HeaderRule.TransformValueType.dataDict.equals(headerRule.getTransformValueType())) {
                if (StringUtils.isNotBlank(headerRule.getTransformValueDataDictCode())) {
                    List<Map<String, Object>> dataDictItems = null;
                    if (valueOptions.containsKey(headerRule.getTransformValueDataDictCode())) {
                        dataDictItems = valueOptions.get(headerRule.getTransformValueDataDictCode());
                    } else {
                        Map<String, Object> sqlParams = Maps.newHashMap();
                        sqlParams.put("dataDictCode", headerRule.getTransformValueDataDictCode());
                        dataDictItems = this.jdbcTemplate.queryForList("select i.label ,i.value from cd_data_dictionary_item i " +
                                " where exists ( select 1 from cd_data_dictionary d where i.data_dict_uuid= d.uuid and d.code =:dataDictCode )", sqlParams);
                    }
                    if (CollectionUtils.isNotEmpty(dataDictItems)) {
                        for (Map<String, Object> m : dataDictItems) {
                            if (m.get("label").toString().equalsIgnoreCase(val)) {
                                val = m.get("value").toString();
                                break;
                            }
                        }
                    }
                }

            } else if (SheetImportRule.HeaderRule.TransformValueType.define.equals(headerRule.getTransformValueType())) {
                if (CollectionUtils.isNotEmpty(headerRule.getTransformValueOption())) {
                    for (Map<String, String> opt : headerRule.getTransformValueOption()) {
                        if (opt.get("label").equalsIgnoreCase(val)) {
                            return opt.get("value").toString();

                        }
                    }

                }
            }

            // 直接通过类型转换
            if (SheetImportRule.HeaderRule.DataType.number.equals(headerRule.getDataType())) {
                return new BigDecimal(val);
            } else if (SheetImportRule.HeaderRule.DataType.date.equals(headerRule.getDataType())) {
                try {
                    List<String> dateTryFormats = Lists.newArrayList("yyyy/MM/dd HH:mm", "yyyy/MM/dd", "yyyy-MM-dd", "yyyy年MM月dd日"
                            , "yyyy-MM-dd HH:mm:ss", "yyyy年MM月dd日 HH时mm分ss秒");
                    if (StringUtils.isNotBlank(headerRule.getFormat())) {
                        dateTryFormats.add(0, headerRule.getFormat());
                    }
                    return DateUtils.parseDate(val, dateTryFormats.toArray(new String[]{}));
                } catch (Exception e) {
                    if (NumberUtils.isNumber(val)) {
                        return DateUtil.getJavaDate(Double.valueOf(val), analysisContext.currentReadHolder().globalConfiguration().getUse1904windowing(), null);
                    }
                    throw new RuntimeException("日期格式值转换失败");
                }
            }
            return val;

        }

        return val;
    }

    protected SheetImportRule.HeaderRule getHeaderRuleByTitle(String title, AnalysisContext analysisContext) {
        SheetImportRule sheetImportRule = this.currentSheetImportRule(analysisContext);
        if (sheetImportRule != null) {
            for (SheetImportRule.HeaderRule r : sheetImportRule.getHeader()) {
                if (r.getTitle().equalsIgnoreCase(title)) {
                    return r;
                }
            }
            return null;
        }
        return null;
    }

    protected SheetImportRule.HeaderRule getHeaderRuleByCode(String code, AnalysisContext analysisContext) {
        SheetImportRule sheetImportRule = this.currentSheetImportRule(analysisContext);
        if (sheetImportRule != null) {
            for (SheetImportRule.HeaderRule r : sheetImportRule.getHeader()) {
                if (r.getCode().equalsIgnoreCase(code)) {
                    return r;
                }
            }
            return null;
        }
        return null;
    }

    /**
     * 判断所有字段是否null ""
     *
     * @param t
     * @return
     */
    protected boolean allFieldIsNull(T t) throws Exception {
        if (Map.class.isAssignableFrom(t.getClass())) {
            Map<Integer, String> map = (Map<Integer, String>) t;
            Set<Map.Entry<Integer, String>> entries = map.entrySet();
            for (Map.Entry<Integer, String> ent : entries) {
                if (StringUtils.isNotBlank(ent.getValue())) {
                    return false;
                }
            }
            return true;
        } else {
            Set<Field> fieldSet = fieldMap.get(t.getClass());
            if (fieldSet == null) {
                fieldSet = ReflectionUtils.getFieldAll(t.getClass());
                fieldMap.put(t.getClass(), fieldSet);
            }
            if (fieldSet.size() == 0) {
                return true;
            }
            for (Field field : fieldSet) {
                Object object = field.get(t);
                if (object != null) {
                    if (object instanceof String) {
                        if (StringUtils.isNotBlank(object.toString())) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }

    }

    /**
     * 数据分析
     *
     * @param t        数据
     * @param rowIndex excel第几行
     * @return
     */
    public abstract ExcelRowDataAnalysedResult dataAnalysed(T t, int rowIndex, AnalysisContext analysisContext);

    /**
     * 所有数据解析导入完成
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        this.allAnalysed(analysisContext);
        String sheetName = analysisContext.readSheetHolder().getSheetName();
        logger.debug("当前读取sheet页: {} , 所有数据解析完成，总数：{}，成功：{}，耗时：{}",
                new Object[]{sheetName, currentSheetTotalRows.intValue(), currentSheetSuccess.intValue(),
                        couter.isRunning() ? couter.stop() : 0
                });
        couter.reset();
        currentSheetSuccess.set(0);
        currentSheetTotalRows.set(0);
    }

    protected void allAnalysed(AnalysisContext analysisContext) {
    }


    /**
     * excel导入监听器名称
     *
     * @return
     */
    public abstract String name();

    public ExcelImportDataReport getDataReport() {
        return this.dataReport;
    }


    public void finish() {

    }

    public ExcelImportRule getExcelImportRule() {
        return excelImportRule;
    }

    public void setExcelImportRule(ExcelImportRule excelImportRule) {
        this.excelImportRule = excelImportRule;
        this.sheetImportRules = excelImportRule.getSheetImportRules();
    }
}
