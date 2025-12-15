package com.wellsoft.pt.dms.facade.service.impl;

import cn.hutool.core.util.ZipUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.datastore.support.export.ExportDataSource;
import com.wellsoft.pt.basicdata.datastore.support.export.ExportRows;
import com.wellsoft.pt.dms.facade.service.DmsDataExportService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
@Service
public class DmsDataExportServiceImpl implements DmsDataExportService {

    private Logger logger = LoggerFactory.getLogger(DmsDataExportService.class);

    @Autowired
    MongoFileService mongoFileService;

    @Override
    public DataSource exportRows(ExportRows rows) {
        List<List<String>> headList = Lists.newArrayList();
        for (ExportRows.HeaderTitle title : rows.getTitles()) {
            headList.add(Lists.newArrayList(title.getTitle()));
        }

        // 创建临时导出文件目录
        File directory = new File(new File(System.getProperty("java.io.tmpdir")), "export-" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS"));
        directory.mkdir();
        File sheetFileDirectory = new File(directory.getAbsolutePath() + "/Sheet1");
        sheetFileDirectory.mkdir();
        logger.info("创建导出定义临时文件 => {}", directory.getAbsoluteFile());
        String xlsxFilePath = directory.getAbsolutePath() + "/" + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xlsx";
        EasyExcel.write(xlsxFilePath, List.class)
                .sheet("Sheet1").head(headList).registerWriteHandler(new CustomCellWriteHandler(rows.getTitles()))
                .doWrite(() -> {
                    List<List<Object>> results = new ArrayList<>();
                    List<Map<String, String>> dataList = rows.getDataList();
                    int i = 2;
                    for (Map<String, String> data : dataList) {
                        List<Object> list = Lists.newArrayList();
                        int j = 1;
                        for (ExportRows.HeaderTitle title : rows.getTitles()) {
                            if (StringUtils.isNotBlank(data.get(title.getCode()))) {
                                if (ExportRows.HeaderTitle.DataType.file.equals(title.getDataType())) {
                                    // 下载文件
                                    try {
                                        // 附件文件夹命名：行标_列标
                                        File subFile = null;
                                        if (BooleanUtils.isTrue(title.getExportAttachment())) {
                                            String fileName = i + "_" + digitToLetters(j);
                                            subFile = new File(sheetFileDirectory.getAbsolutePath() + "/" + fileName);
                                            FileUtils.forceMkdir(subFile);// 附件文件夹
                                        }
                                        String[] fileIds = data.get(title.getCode()).split(Separator.SEMICOLON.getValue());
                                        List<String> fileNames = Lists.newArrayList();
                                        for (String fileId : fileIds) {
                                            MongoFileEntity fileEntity = mongoFileService.getFile(fileId);
                                            if (fileEntity != null) {
                                                fileNames.add(fileEntity.getFileName());
                                                if (subFile != null) {
                                                    File f = new File(subFile.getAbsoluteFile() + "/" + fileEntity.getFileName());
                                                    OutputStream outputStream = new FileOutputStream(f);
                                                    IOUtils.copy(fileEntity.getInputstream(), outputStream);
                                                    IOUtils.closeQuietly(outputStream);
                                                }
                                            }
                                        }
                                        // 压缩输出所有文件到压缩包
                                        list.add(StringUtils.join(fileNames, "\r\n"));

                                    } catch (Exception e) {
                                        logger.error("下载附件异常:", e);
                                    }


                                } else if (ExportRows.HeaderTitle.DataType.date.equals(title.getDataType())) {
                                    try {
                                        list.add(DateUtils.parseDate(data.get(title.getCode()), title.getFormat(), "yyyy/MM/dd HH:mm", "yyyy/MM/dd", "yyyy-MM-dd", "yyyy年MM月dd日"
                                                , "yyyy-MM-dd HH:mm:ss", "yyyy年MM月dd日 HH时mm分ss秒"));
                                    } catch (Exception e) {
                                        list.add(data.get(title.getCode()));
                                    }
                                } else if (ExportRows.HeaderTitle.DataType.number.equals(title.getDataType())) {
                                    list.add(new BigDecimal(data.get(title.getCode())));
                                } else {
                                    list.add(data.get(title.getCode()));
                                }
                            } else {
                                list.add(null);
                            }


                            j++;
                        }
                        results.add(list);
                        i++;
                    }
                    return results;
                });
        try {
            if (sheetFileDirectory.list().length == 0) {
                return new ExportDataSource(new FileInputStream(new File(xlsxFilePath)), "application/x-xls; charset=UTF-8",
                        StringUtils.defaultIfBlank(rows.getFileName(), DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")) + ".xlsx");
            } else {
                // 当有附件文件夹时候，打包压缩返回
                return new ExportDataSource(new FileInputStream(ZipUtil.zip(directory)), "application/zip",
                        StringUtils.defaultIfBlank(rows.getFileName(), DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")) + ".zip");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String digitToLetters(int digit) {
        if (digit < 1) {
            throw new IllegalArgumentException("大于0");
        }

        StringBuilder result = new StringBuilder();
        while (digit > 0) {
            // 取出当前位的数字（0-25）
            int currentDigit = digit - 1; // 注意这里先减去1，因为我们想要1对应'A'
            currentDigit %= 26; // 取模得到0-25
            // 转换为对应的字母（'A' 对应 1, 'B' 对应 2, ..., 'Z' 对应 26）
            char letter = (char) (currentDigit + 'A');
            // 将字母添加到结果字符串的前面
            result.insert(0, letter);
            // 去掉已经处理过的最低位的数字（即当前位的值+1）
            digit = (digit - 1) / 26;
        }

        return result.toString();

    }


    class CustomCellWriteHandler implements CellWriteHandler {
        List<ExportRows.HeaderTitle> titles;

        public CustomCellWriteHandler(List<ExportRows.HeaderTitle> titles) {
            this.titles = titles;


        }

        @Override
        public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {

        }

        @Override
        public void afterCellDispose(CellWriteHandlerContext context) {
            WriteSheetHolder writeSheetHolder = context.getWriteSheetHolder();
            Head head = context.getHeadData();
            Cell cell = context.getCell();
            Sheet sheet = writeSheetHolder.getSheet();
            ExportRows.HeaderTitle headerTitle = this.titles.get(head.getColumnIndex());
            if (!context.getHead()) {
                WriteCellStyle writeCellStyle = context.getFirstCellData().getOrCreateStyle();
                writeCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                if (ExportRows.HeaderTitle.DataType.file.equals(headerTitle.getDataType())
                        && StringUtils.isNotBlank(cell.getStringCellValue())) {

                    WriteFont writeFont = new WriteFont();
                    writeCellStyle.setWriteFont(writeFont);
                    writeCellStyle.setWrapped(true);
                    if (BooleanUtils.isTrue(headerTitle.getExportAttachment())) {
                        writeFont.setUnderline(Font.U_SINGLE);
                        writeFont.setColor(IndexedColors.BLUE.getIndex());
                        // 设置文件链接
                        CreationHelper helper = sheet.getWorkbook().getCreationHelper();
                        Hyperlink hyperlink = helper.createHyperlink(HyperlinkType.URL);
                        hyperlink.setAddress("./" + sheet.getSheetName() + "/" + (context.getRelativeRowIndex() + 2) + "_" + digitToLetters(head.getColumnIndex() + 1));
                        cell.setHyperlink(hyperlink);
                    }
                }
                if (ExportRows.HeaderTitle.DataType.date.equals(headerTitle.getDataType())) {
                    // TODO: 日期是否处理

                }

            } else {
                // 设置宽度
                sheet.setColumnWidth(head.getColumnIndex(), ExportRows.HeaderTitle.DataType.file.equals(headerTitle.getDataType()) ?
                        // 附件列设置长一些
                        40 * 256 :
                        head.getHeadNameList().get(0).getBytes().length * 256);

                WriteCellStyle writeCellStyle = context.getFirstCellData().getOrCreateStyle();
                WriteFont writeFont = new WriteFont();
                writeFont.setFontHeightInPoints((short) 12);
                writeFont.setBold(true);
                writeCellStyle.setWriteFont(writeFont);
            }
        }


    }
}
