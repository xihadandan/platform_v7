/*
 * @(#)2014-6-17 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.service.impl;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.excelexporttemplate.bean.ExcelExportColumnDefinitionBean;
import com.wellsoft.pt.basicdata.excelexporttemplate.bean.ExcelExportDefinitionBean;
import com.wellsoft.pt.basicdata.excelexporttemplate.entity.ExcelExportColumnDefinition;
import com.wellsoft.pt.basicdata.excelexporttemplate.entity.ExcelExportDefinition;
import com.wellsoft.pt.basicdata.excelexporttemplate.provider.ExcelExportDataProvider;
import com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportColumnDefinitionService;
import com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportDefinitionService;
import com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportRuleService;
import com.wellsoft.pt.basicdata.exceltemplate.dao.ExcelImportRuleDao;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.view.bean.ViewDefinitionNewBean;
import com.wellsoft.pt.basicdata.view.entity.ColumnDefinitionNew;
import com.wellsoft.pt.basicdata.view.service.ViewDefinitionNewService;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-17.1	wubin		2014-6-17		Create
 * </pre>
 * @date 2014-6-17
 */
@Service
public class ExcelExportRuleServiceImpl implements ExcelExportRuleService {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExcelExportDefinitionService excelExportDefinitionService;
    @Autowired
    private ExcelExportColumnDefinitionService excelExportColumnDefinitionService;
    @Autowired
    private ViewDefinitionNewService viewDefinitionNewService;

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired(required = false)
    private Map<String, ExcelExportDataProvider> excelExportDataProviderMap;

    /**
     * 通过uuid获取Excel导出规则bean
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportRuleService#getBeanByUuid(java.lang.String)
     */
    @Override
    @Transactional
    public ExcelExportDefinitionBean getBeanByUuid(String uuid) {
        ExcelExportDefinition excelExportDefinition = this.excelExportDefinitionService.getOne(
                uuid);
        ExcelExportDefinitionBean bean = new ExcelExportDefinitionBean();
        BeanUtils.copyProperties(excelExportDefinition, bean);
        if (StringUtils.isNotBlank(bean.getFileUuid())) {
            List<MongoFileEntity> files = ExcelImportRuleDao.getMongoFileEntityByFileUuid(mongoFileService, excelExportDefinition.getFileUuid());
            //mongoFileService.getFilesFromFolder(bean.getFileUuid(), "attach");
            if (files != null && files.size() > 0) {
                bean.setFileName(files.get(0).getFileName());
            }
        }
        bean.setExcelExportColumnDefinition(BeanUtils.convertCollection(
                excelExportDefinition.getExcelExportColumnDefinition(),
                ExcelExportColumnDefinition.class));
        return bean;
    }

    /**
     * 保存导出规则bean
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportRuleService#SaveBean(com.wellsoft.pt.basicdata.excelexporttemplate.bean.ExcelExportDefinitionBean)
     */
    @Override
    @Transactional
    public void saveBean(ExcelExportDefinitionBean bean) {
        ExcelExportDefinition excelExportDefinition = new ExcelExportDefinition();
        if (excelExportDefinitionService.idIsExists(bean.getId(), bean.getUuid())) {
            throw new RuntimeException("ID已经存在！");
        }

        // 保存新ExcelImportRule 设置id值
        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setUuid(null);
            // 先保存父表,然后再保存子表
            BeanUtils.copyProperties(bean, excelExportDefinition);
            this.excelExportDefinitionService.save(excelExportDefinition);

            Set<ExcelExportColumnDefinitionBean> changeColumnDefinitions = bean.getChangeColumnDefinitions();
            for (ExcelExportColumnDefinitionBean changeColumnDefinition : changeColumnDefinitions) {
                ExcelExportColumnDefinition columnDefinition = new ExcelExportColumnDefinition();
                columnDefinition.setColumnNum(changeColumnDefinition.getColumnNum());
                columnDefinition.setAttributeName(changeColumnDefinition.getAttributeName());
                columnDefinition.setValueType(changeColumnDefinition.getValueType());
                columnDefinition.setTitleName(changeColumnDefinition.getTitleName());
                columnDefinition.setEntityName(changeColumnDefinition.getEntityName());
                columnDefinition.setColumnAliase(changeColumnDefinition.getColumnAliase());
                columnDefinition.setColumnDataType(changeColumnDefinition.getColumnDataType());
                // 多方保存时得将实体类也保存而不是bean
                columnDefinition.setExcelExportDefinition(excelExportDefinition);
                excelExportColumnDefinitionService.save(columnDefinition);
            }
        } else {
            excelExportDefinition = this.excelExportDefinitionService.getOne(bean.getUuid());
            BeanUtils.copyProperties(bean, excelExportDefinition);
            this.excelExportDefinitionService.save(excelExportDefinition);
            Set<ExcelExportColumnDefinitionBean> changeColumnDefinitions = bean.getChangeColumnDefinitions();
            for (ExcelExportColumnDefinitionBean changeColumnDefinition : changeColumnDefinitions) {
                ExcelExportColumnDefinition columnDefinition = new ExcelExportColumnDefinition();
                if (StringUtils.isNotBlank(changeColumnDefinition.getUuid())) {
                    columnDefinition = excelExportColumnDefinitionService.getOne(
                            changeColumnDefinition.getUuid());
                } else {
                    columnDefinition.setExcelExportDefinition(excelExportDefinition);
                }
                columnDefinition.setColumnNum(changeColumnDefinition.getColumnNum());
                columnDefinition.setAttributeName(changeColumnDefinition.getAttributeName());
                columnDefinition.setValueType(changeColumnDefinition.getValueType());
                columnDefinition.setTitleName(changeColumnDefinition.getTitleName());
                columnDefinition.setEntityName(changeColumnDefinition.getEntityName());
                columnDefinition.setColumnAliase(changeColumnDefinition.getColumnAliase());
                columnDefinition.setColumnDataType(changeColumnDefinition.getColumnDataType());
                excelExportColumnDefinitionService.save(columnDefinition);
            }
            Set<ExcelExportColumnDefinitionBean> deletedExcelRows = bean.getDeletedExcelRows();
            for (ExcelExportColumnDefinitionBean deletedExcelRow : deletedExcelRows) {
                ExcelExportColumnDefinition columnDefinition = new ExcelExportColumnDefinition();
                columnDefinition = excelExportColumnDefinitionService.getOne(
                        deletedExcelRow.getUuid());
                excelExportColumnDefinitionService.delete(columnDefinition);
            }
        }
    }

    /**
     * 删除Excel导出规则
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportRuleService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        this.excelExportDefinitionService.delete(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportRuleService#removeAll(java.lang.String[])
     */
    @Override
    @Transactional
    public void removeAll(String[] uuids) {
        for (int i = 0; i < uuids.length; i++) {
            this.excelExportDefinitionService.delete(uuids[i]);
        }
    }

    /**
     * JQgridExcel导入规则列表查询
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportRuleService#query(com.wellsoft.pt.common.component.jqgrid.JqGridQueryInfo)
     */
    @Override
    public JqGridQueryData query(JqGridQueryInfo queryInfo) {
        PagingInfo pageData = new PagingInfo(queryInfo.getPage(), queryInfo.getRows(), true);
        List<ExcelExportDefinition> excelExportDefinitions = this.excelExportDefinitionService.listAllByOrderPage(
                pageData, null);
        List<ExcelExportDefinition> jqUsers = new ArrayList<ExcelExportDefinition>();
        for (ExcelExportDefinition excelExportDefinition : excelExportDefinitions) {
            ExcelExportDefinition jqExcelExportDefinition = new ExcelExportDefinition();
            BeanUtils.copyProperties(excelExportDefinition, jqExcelExportDefinition);
            jqUsers.add(jqExcelExportDefinition);
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
     * 导出excel的模版
     */
    public String generateExcelFile(DyViewQueryInfoNew dyViewQueryInfoNew, String id, String data,
                                    HttpServletRequest request, HttpServletResponse response) {

        // 根据传入的id获得视图的bean
        ViewDefinitionNewBean viewDefinitionBean = viewDefinitionNewService.getBeanByUuid(id);
        // 根据视图的配置信息获取视图配置的导出模版的id
        String excelUuid = viewDefinitionBean.getDataModuleId();
        // 根据视图的配置信息获取视图配置的数据导出接口的ID
        String excelExportId = viewDefinitionBean.getDataInterfaceId();
        List<QueryItem> queryItems = basicDataApiFacade.getViewColumnData(dyViewQueryInfoNew, id,
                data);

        Map<String, String> rowMap = null;
        // 视图所有要导出数据
        List<Map<String, String>> allViewDataList = new ArrayList<Map<String, String>>();

        // 过滤后的视图数据List
        List<Map<String, String>> allViewDataListFilter = new ArrayList<Map<String, String>>();
        List<Map<String, String>> allViewDataListFilter2 = new LinkedList<Map<String, String>>();
        // 过滤后每一行数据
        Map<String, String> rowMapFilter = null;
        for (QueryItem queryItem : queryItems) {
            // 视图一行数据
            rowMap = new HashMap<String, String>();
            for (String key : queryItem.keySet()) {
                Object obj = queryItem.get(key);
                String value = "";
                if (obj == null) {

                } else {
                    value = obj.toString();
                }
                rowMap.put(key, value);
            }
            allViewDataList.add(rowMap);
        }

        String[] titleNameArray = new String[200];
        // 视图数据导出规则配置的字段信息
        Set<ExcelExportColumnDefinition> excelColumnSet = null;
        // 如果有配置数据导出模板
        if (StringUtils.isNotBlank(excelUuid)) {
            ExcelExportDefinition excelExportDefinition = this.excelExportDefinitionService.getOne(
                    excelUuid);
            excelColumnSet = excelExportDefinition.getExcelExportColumnDefinition();

            // 数据导出规则列定义数值
            // List<Map<Integer,String>> columnDefinitionList = new
            // LinkedList<Map<Integer,String>>();
            Map<Integer, String> columnDefinitionMap = new HashMap<Integer, String>();
            if (null != excelColumnSet && excelColumnSet.size() > 0) {
                for (ExcelExportColumnDefinition o : excelColumnSet) {
                    if (rowMap != null) {
                        String titleName = o.getTitleName();
                        Integer columnNum = o.getColumnNum();// 列数
                        String entity = o.getEntityName();
                        String columnDataType = o.getColumnDataType();
                        String attributeName = o.getAttributeName();// 字段名称
                        columnDefinitionMap.put(columnNum, attributeName.replace("_", ""));
                    }
                }
            }

            // =======================过滤数据开始 start
            // huangwy============================

            for (int i = 0; i < allViewDataList.size(); i++) {
                Map<String, String> map = allViewDataList.get(i);
                rowMapFilter = new LinkedHashMap<String, String>();
                for (int j = 0; j < columnDefinitionMap.size(); j++) {
                    for (String s : map.keySet()) {
                        String lowerCase = StringUtils.lowerCase(s);
                        String colummDefLowerCase = StringUtils.lowerCase(
                                (String) columnDefinitionMap.get(j));
                        if (lowerCase.equals(colummDefLowerCase)) {
                            rowMapFilter.put(s, map.get(s));
                        }
                    }
                }
                allViewDataListFilter.add(rowMapFilter);
            }
            // =======================过滤数据开始 end
            // huangwy============================
            // 获得文件uuid
            // String fileUuid = excelExportDefinition.getFileUuid();
            // MongoFileEntity fileEntity = mongoFileService.getFile(fileUuid);
            // 获得mongo数据文件
            List<MongoFileEntity> files = ExcelImportRuleDao.getMongoFileEntityByFileUuid(mongoFileService, excelExportDefinition.getFileUuid());
            //mongoFileService.getFilesFromFolder( excelExportDefinition.getFileUuid(),"attach");
            if (files != null && !files.isEmpty()) {
                MongoFileEntity mongoFileEntity = files.get(0);
                if (mongoFileEntity != null) {
                    titleNameArray = getExcelExportTitle(mongoFileEntity.getInputstream());
                }
            }
        } else {

            StringBuilder tempStr = new StringBuilder();
            Set<ColumnDefinitionNew> columnDefinitions = viewDefinitionBean.getColumnDefinitionNews();
            // 过滤视图定义列中隐藏的那些列
            for (int i = 0; i < allViewDataList.size(); i++) {
                Map<String, String> map = allViewDataList.get(i);
                rowMapFilter = new LinkedHashMap<String, String>();
                for (ColumnDefinitionNew columnDefinition : columnDefinitions) {
                    for (String s : map.keySet()) {
                        String lowerCase = StringUtils.lowerCase(s);
                        String titleName = columnDefinition.getOtherName();
                        if (StringUtils.isBlank(titleName)) {
                            titleName = columnDefinition.getTitleName();
                        }
                        String fieldName = columnDefinition.getFieldName();
                        String columnAlise = columnDefinition.getColumnAliase();
                        Boolean fieldHidden = columnDefinition.getHidden();
                        if (columnAlise.indexOf("_") > -1) {
                            columnAlise = fieldName.replaceAll("_", "");
                            columnAlise = StringUtils.lowerCase(columnAlise);
                        } else {
                            columnAlise = StringUtils.lowerCase(columnAlise);
                        }
                        if (lowerCase.equals(columnAlise) && fieldHidden == false) {
                            rowMapFilter.put(s, map.get(s));
                            if (i == 0) {
                                tempStr.append(";").append(titleName);
                            }
                        }
                    }
                }
                allViewDataListFilter2.add(rowMapFilter);
            }
            // for (ColumnDefinitionNew columnDefinition : columnDefinitions) {
            // String titleName = columnDefinition.getOtherName();
            // tempStr.append(";").append(titleName);
            // }
            titleNameArray = tempStr.toString().replaceFirst(";", "").split(";");
        }
        // 生成excel文件
        String path = null;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("dataUuid", data);
        List<Map<String, String>> dpData = new ArrayList<Map<String, String>>();
        String[] titleArray = new String[200];
        if (StringUtils.isNotBlank(excelExportId)) {
            dpData = excelExportDataProviderMap.get(excelExportId).getExportDataArray(map);
            titleArray = excelExportDataProviderMap.get(excelExportId).getExportTitleArray();
        }
        if (dpData.size() != 0) {
            if (titleArray == null) {
                titleArray = new String[200];
            }
            path = generateXmlFile(dpData, titleArray, request, response);
        } else {
            if (allViewDataListFilter.size() != 0) {
                path = generateXmlFile(allViewDataListFilter, titleNameArray, request, response);
            } else {
                path = generateXmlFile(allViewDataListFilter2, titleNameArray, request, response);
            }
        }
        return path;
    }

    /**
     * 生成excel文件方法
     *
     * @param dataList
     * @param titleArray
     * @return
     */
    @Override
    public String generateXmlFile(List<Map<String, String>> dataList, String[] titleArray,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        //
        WritableFont font = new WritableFont(WritableFont.TIMES, 20, WritableFont.NO_BOLD);
        try {
            font.setColour(jxl.format.Colour.RED);
            WritableCellFormat formatTitle = new WritableCellFormat(font);
            formatTitle.setWrap(true);
        } catch (WriteException e1) {
            logger.error(ExceptionUtils.getStackTrace(e1));
        }

        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("Sheet1");
        sheet.setDefaultColumnWidth((short) 30);
        // sheet.sautoSizeColumn((short) 1);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow((int) 0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        // style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
        HSSFCell cell = row.createCell((short) 0);
        for (int i = 0; i < titleArray.length; i++) {
            cell.setCellValue(titleArray[i]);
            cell.setCellStyle(style);
            cell = row.createCell((short) ((short) i + 1));
        }
        // 第五步，写入实体数据 实际应用中这些数据从数据库得到，
        for (int i = 0; i < dataList.size(); i++) {
            row = sheet.createRow((short) i + 1);
            Map<String, String> map = dataList.get(i);
            int j = 0;
            for (String s : map.keySet()) {
                String str = map.get(s);
                row.createCell((short) j).setCellValue(str);

                j = j + 1;
            }
        }
        // 第六步，将文件存到指定位置
        String path = request.getSession().getServletContext().getRealPath(
                "/") + UUID.randomUUID() + ".xls";
        File file = new File(path);
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(file);
            wb.write(fout);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return path;
    }

    /**
     * 根据文件流获取excel导出模版的标题
     *
     * @param is
     * @return
     * @throws Exception
     */
    public String[] getExcelExportTitle(InputStream is) {

        // 解析excel放入List
        HSSFWorkbook hssfWorkbook = null;
        try {
            hssfWorkbook = new HSSFWorkbook(is);
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        // 第一个工作表
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        // 获取第一行row，即标题行
        HSSFRow hssfRow = hssfSheet.getRow(0);
        StringBuilder tempStr = new StringBuilder();
        // 获得数据导出规则模板的标题
        for (int cellNum = 0; cellNum <= hssfRow.getLastCellNum(); cellNum++) {
            HSSFCell hssfCell = hssfRow.getCell(cellNum);
            if (hssfCell != null) {
                String titleName = String.valueOf(hssfCell.getStringCellValue());
                if (titleName != null) {
                    System.out.print("    " + titleName);
                    tempStr.append(";" + titleName);
                }
            }
        }
        String[] titleNames = tempStr.toString().replaceFirst(";", "").split(";");
        return titleNames;
    }

    /**
     * 获取所有的导出规则
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportRuleService#getExcelExportRule()
     */
    @Override
    public List<ExcelExportDefinition> getExcelExportRule() {
        String hql = "from ExcelExportDefinition";
        return excelExportDefinitionService.listByHQL(hql, new HashMap());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportRuleService#getExcelExportDefinition(java.lang.String)
     */
    @Override
    public ExcelExportDefinition getExcelExportDefinition(String uuid) {
        return excelExportDefinitionService.getOne(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportRuleService#getDataSourceList(java.lang.String, java.lang.String)
     */
    @Override
    public List getDataSourceList(String s) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeNode treeNode;
        for (String key : excelExportDataProviderMap.keySet()) {
            ExcelExportDataProvider source = excelExportDataProviderMap.get(key);
            treeNode = new TreeNode();
            treeNode.setName(source.getModuleName());
            treeNode.setId(key);
            treeNode.setData(source.getModuleId());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }
}
