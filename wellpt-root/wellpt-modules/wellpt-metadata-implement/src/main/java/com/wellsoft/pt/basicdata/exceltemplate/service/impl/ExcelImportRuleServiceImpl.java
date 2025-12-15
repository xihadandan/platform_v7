package com.wellsoft.pt.basicdata.exceltemplate.service.impl;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.pt.basicdata.exceltemplate.bean.ExcelColumnDefinitionBean;
import com.wellsoft.pt.basicdata.exceltemplate.bean.ExcelImportRuleBean;
import com.wellsoft.pt.basicdata.exceltemplate.dao.ExcelColumnDefinitionDao;
import com.wellsoft.pt.basicdata.exceltemplate.dao.ExcelImportRuleDao;
import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelColumnDefinition;
import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelImportRule;
import com.wellsoft.pt.basicdata.exceltemplate.service.ExcelImportRuleService;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Description: Excel导入规则服务层实现类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-24.1	zhouyq		2013-4-22		Create
 * </pre>
 * @date 2013-4-22
 */
@Service
@Transactional
public class ExcelImportRuleServiceImpl extends BaseServiceImpl implements ExcelImportRuleService {
    static {
        DateConverter dc = new DateConverter();
        dc.setUseLocaleFormat(true);
        String[] patterns = new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy年MM月dd日", "yyyy年MM月dd日 HH时mm分ss秒",
                "yyyy-MM", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss.SSS"};
        dc.setPatterns(patterns);
        ConvertUtils.register(dc, Date.class);
        ConvertUtils.register(dc, Calendar.class);
    }

    @Autowired
    private ExcelImportRuleDao excelImportRuleDao;
    @Autowired
    private ExcelColumnDefinitionDao excelColumnDefinitionDao;
    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private MongoFileService mongoFileService;

    /**
     * 关闭Object,如果是可关闭的   POI旧版本Workbook是not closeable
     *
     * @param closeable
     */
    public static void closeQuietlyCloseable(Object closeable) {
        if (closeable != null && closeable instanceof Closeable) {
            IOUtils.closeQuietly((Closeable) closeable);
        }
    }

    private static Object getValue(Cell fCell, boolean isDateType) {
        if (fCell.getCellType() == CellType.BLANK) {
            return null;
        } else if (fCell.getCellType() == CellType.NUMERIC) {
            if (isDateType) {
                return fCell.getDateCellValue();
            }
            return fCell.getNumericCellValue();
        } else if (fCell.getCellType() == CellType.BOOLEAN) {
            return fCell.getBooleanCellValue();
        } else {
            return fCell.getStringCellValue();
        }
    }

    /**
     * 获得所有Excel导入规则
     *
     * @return
     */
    @Override
    public List<ExcelImportRule> findAll() {
        return excelImportRuleDao.getAll();
    }

    /**
     * 通过uuid获取Excel导入规则
     *
     * @param id
     * @return
     */
    @Override
    public ExcelImportRule get(String uuid) {
        return excelImportRuleDao.get(uuid);
    }

    /**
     * 保存Excel导入规则bean
     */
    @Override
    public void saveBean(ExcelImportRuleBean bean) {
        // 1.保存父表的数据
        ExcelImportRule excelImportRule = new ExcelImportRule();
        if (excelImportRuleDao.idIsExists(bean.getId(), bean.getUuid())) {
            throw new RuntimeException("ID已经存在！");
        }
        // 保存新ExcelImportRule 设置id值
        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setUuid(null);

            // 先保存父表,然后再保存子表
            BeanUtils.copyProperties(bean, excelImportRule);
            this.excelImportRuleDao.save(excelImportRule);

            Set<ExcelColumnDefinitionBean> changeColumnDefinitions = bean.getChangeColumnDefinitions();
            for (ExcelColumnDefinitionBean changeColumnDefinition : changeColumnDefinitions) {
                ExcelColumnDefinition columnDefinition = new ExcelColumnDefinition();
                columnDefinition.setColumnNum(changeColumnDefinition.getColumnNum());
                columnDefinition.setAttributeName(changeColumnDefinition.getAttributeName());
                // 多方保存时得将实体类也保存而不是bean
                columnDefinition.setExcelImportRule(excelImportRule);
                excelColumnDefinitionDao.save(columnDefinition);
            }
        } else {
            excelImportRule = this.excelImportRuleDao.get(bean.getUuid());
            BeanUtils.copyProperties(bean, excelImportRule);
            this.excelImportRuleDao.save(excelImportRule);
            Set<ExcelColumnDefinitionBean> changeColumnDefinitions = bean.getChangeColumnDefinitions();
            for (ExcelColumnDefinitionBean changeColumnDefinition : changeColumnDefinitions) {
                ExcelColumnDefinition columnDefinition = new ExcelColumnDefinition();
                if (changeColumnDefinition.getUuid() != null && !changeColumnDefinition.getUuid().equals("")) {
                    columnDefinition = excelColumnDefinitionDao.get(changeColumnDefinition.getUuid());
                } else {
                    columnDefinition.setExcelImportRule(excelImportRule);
                }
                columnDefinition.setColumnNum(changeColumnDefinition.getColumnNum());
                columnDefinition.setAttributeName(changeColumnDefinition.getAttributeName());
                excelColumnDefinitionDao.save(columnDefinition);
            }
            Set<ExcelColumnDefinitionBean> deletedExcelRows = bean.getDeletedExcelRows();
            for (ExcelColumnDefinitionBean changeColumnDefinition : deletedExcelRows) {
                ExcelColumnDefinition columnDefinition = new ExcelColumnDefinition();
                columnDefinition = excelColumnDefinitionDao.get(changeColumnDefinition.getUuid());
                if (null != columnDefinition && StringUtils.isNotBlank(columnDefinition.getUuid())) {
                    excelColumnDefinitionDao.delete(columnDefinition);
                }
            }
        }
        /*
         * //保存子表修改的数据 Set<ExcelColumnDefinitionBean> changeColumnDefinitions =
         * bean.getChangeColumnDefinitions(); for (ExcelColumnDefinitionBean
         * changeColumnDefinition : changeColumnDefinitions) {
         * ExcelColumnDefinition excelColumnDefinition =
         * excelColumnDefinitionDao .get(changeColumnDefinition.getUuid());
         * BeanUtils.copyProperties(changeColumnDefinition,
         * excelColumnDefinition);
         * this.excelColumnDefinitionDao.save(excelColumnDefinition); }
         */
    }

    /**
     * 删除Excel导入规则
     */
    @Override
    public void remove(String uuid) {
        this.excelImportRuleDao.delete(uuid);
    }

    @Override
    public void removeAll(String[] uuids) {
        for (int i = 0; i < uuids.length; i++) {
            this.excelImportRuleDao.delete(uuids[i]);
        }
    }

    /**
     * JQgridExcel导入规则列表查询
     */
    @Override
    public JqGridQueryData query(JqGridQueryInfo queryInfo) {
        Page<ExcelImportRule> pageData = new Page<ExcelImportRule>();
        pageData.setPageNo(queryInfo.getPage());
        pageData.setPageSize(queryInfo.getRows());
        this.excelImportRuleDao.findPage(pageData);
        List<ExcelImportRule> excelImportRules = pageData.getResult();
        List<ExcelImportRule> jqUsers = new ArrayList<ExcelImportRule>();
        for (ExcelImportRule excelImportRule : excelImportRules) {
            ExcelImportRule jqExcelImportRule = new ExcelImportRule();
            BeanUtils.copyProperties(excelImportRule, jqExcelImportRule);
            jqUsers.add(jqExcelImportRule);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(jqUsers);
        queryData.setRepeatitems(false);
        queryData.setTotalPages(pageData.getTotalPages());
        queryData.setTotalRows(pageData.getTotalCount());
        return queryData;
    }

    /**
     * 通过uuid获取Excel导入规则VO对象
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.exceltemplate.service.ExcelImportRuleService#getBeanByUuid(java.lang.String)
     */
    @Override
    public ExcelImportRuleBean getBeanByUuid(String uuid) {
        ExcelImportRule excelImportRule = this.excelImportRuleDao.get(uuid);
        ExcelImportRuleBean bean = new ExcelImportRuleBean();
        BeanUtils.copyProperties(excelImportRule, bean);
        if (StringUtils.isNotBlank(excelImportRule.getFileUuid())) {
            List<MongoFileEntity> files = ExcelImportRuleDao.getMongoFileEntityByFileUuid(mongoFileService, excelImportRule.getFileUuid());//mongoFileService.getFilesFromFolder(excelImportRule.getFileUuid(), "attach");
            if (files != null && files.size() > 0) {
                bean.setFileName(files.get(0).getFileName());
            }
        }

        bean.setExcelColumnDefinitions(BeanUtils.convertCollection(excelImportRule.getExcelColumnDefinitions(),
                ExcelColumnDefinition.class));
        return bean;
    }

    /**
     * 列对应列表查询
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.exceltemplate.service.ExcelImportRuleService#queryExcelList(com.wellsoft.pt.common.component.jqgrid.JqGridQueryInfo)
     */
    @Override
    public JqGridQueryData queryExcelList(JqGridQueryInfo queryInfo) {
        Page<ExcelColumnDefinition> pageData = new Page<ExcelColumnDefinition>();
        pageData.setPageNo(queryInfo.getPage());
        pageData.setPageSize(queryInfo.getRows());
        this.excelColumnDefinitionDao.findPage(pageData);
        List<ExcelColumnDefinition> excelColumnDefinitions = pageData.getResult();
        List<ExcelColumnDefinition> jqUsers = new ArrayList<ExcelColumnDefinition>();
        for (ExcelColumnDefinition excelColumnDefinition : excelColumnDefinitions) {
            ExcelColumnDefinition jqExcelColumnDefinition = new ExcelColumnDefinition();
            BeanUtils.copyProperties(excelColumnDefinition, jqExcelColumnDefinition);
            jqUsers.add(jqExcelColumnDefinition);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(jqUsers);
        queryData.setRepeatitems(false);
        queryData.setTotalPages(pageData.getTotalPages());
        queryData.setTotalRows(pageData.getTotalCount());
        return queryData;

    }

    @Override
    public ExcelImportRule getExcelImportRuleObj(String id) {
        return excelImportRuleDao.findUniqueBy("id", id);
    }

    @Override
    public List getListFromExcel(String id, InputStream is) throws Exception {
        return getListFromExcel(id, is, "");
    }

    /**
     * 解析excel
     *
     * @param code
     * @param is
     * @return
     */
    @Override
    public List getListFromExcel(String id, InputStream is, String sheetName) throws Exception {
        return getListFromExcel(id, is, sheetName, false, true);
    }

    /**
     * 解析excel
     *
     * @param id                   规则ID
     * @param is                   要导入的Excel流
     * @param sheetName            excel工作空间名称
     * @param notDefinedField2Null 规则为定义字段设置为空，否则忽略（表单类型有效）
     * @param ignoreNullRow        忽略空行
     * @return
     * @throws Exception
     */
    // @Override
    public List<Object> getListFromExcel(String id, InputStream is, String sheetName,
                                         boolean notDefinedField2Null/* 表单类型有效 */, boolean ignoreNullRow) throws Exception {
        // 解析excel放入List
        ExcelImportRule excelImportRule = excelImportRuleDao.findUniqueBy("id", id);

        ControlCloseAbleInputStream bis = null;
        Workbook fWorkbook = null;
        Sheet fSheet = null;
        List<Object> list = null;
        try {
            try {
                // FIX:实例化XSSFWorkbook(bis)失败时会自动关闭流
                bis = new ControlCloseAbleInputStream(is);
                bis.mark(Integer.MAX_VALUE);
                // Constructs a XSSFWorkbook object, by buffering the whole
                // stream into memory and then opening an OPCPackage object for
                // it.
                fWorkbook = new XSSFWorkbook(bis);
            } catch (Exception ex) {
                logger.info("XSSF解析导入规则ID[" + id + "]的Excel输入流失败,尝试HSSF...", ex);
                ExcelImportRuleServiceImpl.closeQuietlyCloseable(fWorkbook);
                bis.reset(); // 将HSSF读的流重置
                bis.setCanCloseAble();
                fWorkbook = new HSSFWorkbook(bis);
            } finally {
                bis.setCanCloseAble();
            }

            // 如果excel页签名不为空，则取指定的页签.
            if (StringUtils.isNotEmpty(sheetName)) {
                // Sheet with the name provided or null if it does not exist
                fSheet = fWorkbook.getSheet(sheetName);
            }
            if (fSheet == null) {
                // 第一个工作表
                fSheet = fWorkbook.getSheetAt(0);
            }
            String rType = excelImportRule == null ? null : excelImportRule.getType();
            list = new ArrayList<Object>();
            if (StringUtils.isNotBlank(rType) && rType.equals("formdefinition")) {
                String formId = excelImportRule.getEntity();
                if (StringUtils.isBlank(formId)) {
                    throw new RuntimeException("导入规则ID[" + id + "]的定义为表单类型,但表单Id为空.");
                }
                DyFormFormDefinition dyFormDefinition = dyFormApiFacade.getFormDefinition(formId);
                // 列的集合
                List<DyformFieldDefinition> fieldDefinitionList = null;
                if (dyFormDefinition == null
                        || (fieldDefinitionList = dyFormDefinition.doGetFieldDefintions()).isEmpty()) {
                    throw new RuntimeException("导入规则ID[" + id + "]的表单Id[" + formId + "]未能找到列集合.");
                }
                // 日期类型,以表单定义的为准.entityMap构造表单空记录
                Map<String, String> entityMap = new HashMap<String, String>();
                for (DyformFieldDefinition f : fieldDefinitionList) {/*
                 * // 日期类型
                 * if (f.
                 * getDbDataType
                 * (
                 * ).equals(
                 * DyFormConfig
                 * .
                 * DbDataType
                 * ._date))
                 * {
                 * entityMap
                 * .
                 * put(f.getName
                 * (), f.
                 * getDbDataType
                 * ()); }
                 */
                }

                Set<ExcelColumnDefinition> eColDefs = excelImportRule.getExcelColumnDefinitions();
                int dStartRow = excelImportRule.getStartRow();
                Map<Integer, String> caMap = new HashMap<Integer, String>();
                for (Iterator<ExcelColumnDefinition> iter = eColDefs.iterator(); iter.hasNext(); ) {
                    ExcelColumnDefinition e = iter.next();
                    caMap.put(e.getColumnNum(), e.getAttributeName());
                }

                // 循环行Row
                for (int rowNum = dStartRow; rowNum <= fSheet.getLastRowNum(); rowNum++) {
                    Row fRow = fSheet.getRow(rowNum);
                    if (fRow == null) {
                        continue;
                    }
                    boolean nullRow = true;
                    Map<String, Object> map = new HashMap<String, Object>();
                    // 循环列Cell
                    for (int cellNum = 0; cellNum <= fRow.getLastCellNum(); cellNum++) {
                        String fieldName = caMap.get(cellNum + 1);
                        if (StringUtils.isBlank(fieldName)) {
                            continue;
                        }
                        Cell fCell = fRow.getCell(cellNum);
                        if (fCell == null || fCell.getCellType() == CellType.BLANK) {
                            // 定义字段为空
                            map.put(fieldName, null);
                            continue;
                        }
                        nullRow = false;
                        map.put(fieldName, getValue(fCell, entityMap.containsKey(fieldName)));
                    }
                    if (ignoreNullRow && nullRow) {
                        continue;// 忽略空行
                    }
                    if (notDefinedField2Null/* 未定义字段置为空 */) {
                        for (DyformFieldDefinition f : fieldDefinitionList) {
                            if (map.containsKey(f.getName())) {
                                continue;
                            }
                            map.put(f.getName(), null);// 未定义字段写空
                        }
                    }
                    list.add(map);
                }
            } else if (StringUtils.isNotBlank(rType) && rType.equals("entity")) {
                String entity = excelImportRule.getEntity();
                if (StringUtils.isBlank(entity)) {
                    throw new RuntimeException("导入规则ID[" + id + "]的定义为实体类,entity为空.");
                }
                // 开始行
                int dStartRow = excelImportRule.getStartRow();
                Set<ExcelColumnDefinition> aList = excelImportRule.getExcelColumnDefinitions();

                Map<Integer, String> caMap = new HashMap<Integer, String>();
                for (Iterator<ExcelColumnDefinition> iter = aList.iterator(); iter.hasNext(); ) {
                    ExcelColumnDefinition e = iter.next();
                    caMap.put(e.getColumnNum(), e.getAttributeName());
                }

                // 循环行Row
                for (int rowNum = dStartRow + 1; rowNum <= fSheet.getLastRowNum(); rowNum++) {
                    BeanWrapper wrapper = new BeanWrapperImpl(Class.forName(entity).newInstance());
                    Row fRow = fSheet.getRow(rowNum);
                    if (fRow == null) {
                        continue;
                    }
                    boolean nullRow = true;
                    // 循环列Cell
                    for (int cellNum = 0; cellNum <= fRow.getLastCellNum(); cellNum++) {
                        String fieldName = caMap.get(cellNum + 1);
                        if (StringUtils.isBlank(fieldName)) {
                            continue;
                        }
                        Cell fCell = fRow.getCell(cellNum);
                        if (fCell == null || fCell.getCellType() == CellType.BLANK) {
                            // 定义字段为空
                            wrapper.setPropertyValue(fieldName, null);
                            continue;
                        }
                        nullRow = false;
                        // 做相应的数据类型转换
                        Class<?> clazz = wrapper.getPropertyDescriptor(fieldName).getPropertyType();
                        boolean bDate = clazz.isAssignableFrom(java.util.Date.class)
                                || clazz.isAssignableFrom(java.util.Calendar.class);
                        Object tValue = ConvertUtils.convert(getValue(fCell, bDate), clazz);
                        wrapper.setPropertyValue(fieldName, tValue);
                    }
                    if (ignoreNullRow && nullRow) {
                        continue;// 忽略空行
                    }
                    list.add(wrapper.getWrappedInstance());
                }
            } else {
                logger.info("导入规则ID[" + id + "]未知导入类型[" + rType + "]");
            }
        } finally {
            ExcelImportRuleServiceImpl.closeQuietlyCloseable(bis);
            ExcelImportRuleServiceImpl.closeQuietlyCloseable(fWorkbook);
        }
        return list;
    }

    private String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    @Override
    public ExcelImportRule getNameByFileUuid(String fileUuid) {
        return excelImportRuleDao.findUniqueBy("fileUuid", fileUuid);
    }

    @Override
    public List<ExcelImportRule> getExcelImportRule() {
        String hql = "from ExcelImportRule";
        return excelImportRuleDao.find(hql, new HashMap());
    }

    @Override
    public ExcelImportRule getExcelImportRule(String uuid) {
        return excelImportRuleDao.get(uuid);
    }

    /**
     * Description: 关闭可控的流,FIX：实例化XSSFWorkbook(bis)失败时会自动关闭流
     *
     * @author zhongzh
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2016年7月20日.1	zhongzh		2016年7月20日		Create
     * </pre>
     * @date 2016年7月20日
     */
    static private class ControlCloseAbleInputStream extends BufferedInputStream {

        private boolean closeAble;

        /**
         * 默认不可关闭的流,在finally中调setCanCloseAble()
         *
         * @param in
         */
        public ControlCloseAbleInputStream(InputStream in) {
            super(in);
            closeAble = false;
        }

        /**
         * @param closeAble 要设置的closeAble
         */
        public void setCanCloseAble() {
            this.closeAble = true;
        }

        /**
         * 关闭流时判断是否closeAble
         * <p>
         * (non-Javadoc)
         *
         * @see java.io.BufferedInputStream#close()
         */
        @Override
        public void close() throws IOException {
            if (closeAble) {
                super.close();
            }
        }

    }
}
