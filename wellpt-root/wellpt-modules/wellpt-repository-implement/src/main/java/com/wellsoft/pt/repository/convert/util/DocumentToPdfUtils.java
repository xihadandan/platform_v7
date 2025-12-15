package com.wellsoft.pt.repository.convert.util;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.presentation.Presentation;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/1.1	liuyz		2021/9/1		Create
 * </pre>
 * @date 2021/9/1
 */
public class DocumentToPdfUtils {

    private static Logger logger = LoggerFactory.getLogger(DocumentToPdfUtils.class);

    static {
        SpireLicenseInstall.installLicense();
    }


    /**
     * word文档转pdf
     *
     * @param sourceFile
     * @param targetPath
     * @throws FileNotFoundException
     */
    public static void wordToPdf(File sourceFile, String targetPath) throws FileNotFoundException {
        // 通过流加载文档
        FileInputStream inputStream = new FileInputStream(sourceFile);
        Document document = new Document();
        document.loadFromStream(inputStream, FileFormat.Auto);

        //保存为PDF
        document.saveToFile(targetPath, FileFormat.PDF);
        IOUtils.closeQuietly(inputStream);

    }

    /**
     * word文档转pdf
     *
     * @param sourceFile
     * @param targetFile
     * @throws FileNotFoundException
     */
    public static void wordToPdf(File sourceFile, File targetFile) throws FileNotFoundException {
        // 通过流加载文档
        FileInputStream inputStream = new FileInputStream(sourceFile);
        Document document = new Document();
        document.loadFromStream(inputStream, FileFormat.Auto);

        //保存为PDF
        document.saveToFile(targetFile.getAbsolutePath(), FileFormat.PDF);
        IOUtils.closeQuietly(inputStream);

    }

    /**
     * word文档转pdf(接口适用)
     *
     * @param sourceFile
     * @param fileOutputStream
     * @throws FileNotFoundException
     */
    public static void wordToPdf(File sourceFile, FileOutputStream fileOutputStream) throws FileNotFoundException {
        // 通过流加载文档
        FileInputStream inputStream = new FileInputStream(sourceFile);
        Document document = new Document();
        document.loadFromStream(inputStream, FileFormat.Auto);

        //保存为PDF
        document.saveToFile(fileOutputStream, FileFormat.PDF);
        IOUtils.closeQuietly(inputStream);
    }

    /**
     * excel文档转pdf
     *
     * @param sourceFile
     * @param targetPath
     * @throws FileNotFoundException
     */
    public static void excelToPdf(File sourceFile, String targetPath) throws FileNotFoundException {
        //加载Excel文档
        Workbook wb = new Workbook();
        FileInputStream inputStream = new FileInputStream(sourceFile);
        wb.loadFromStream(inputStream);

        // 在PDF的一页上显示，不会分割
        wb.getConverterSetting().setSheetFitToPage(true);

        //调用方法保存为PDF格式
        wb.saveToFile(targetPath);
        IOUtils.closeQuietly(inputStream);

    }

    /**
     * 指定excel文档的某个sheet转pdf
     *
     * @param sourceFile
     * @param targetPath
     * @param sheetIndex
     * @throws FileNotFoundException
     */
    public static void excelToPdf(File sourceFile, String targetPath, Integer sheetIndex) throws FileNotFoundException {
        //加载Excel文档
        Workbook wb = new Workbook();
        FileInputStream inputStream = new FileInputStream(sourceFile);
        wb.loadFromStream(inputStream);

        // 在PDF的一页上显示，不会分割
        wb.getConverterSetting().setSheetFitToPage(true);

        //调用方法保存为PDF格式
        Worksheet sheet = wb.getWorksheets().get(sheetIndex);
        sheet.saveToPdf(targetPath);
        IOUtils.closeQuietly(inputStream);

    }

    /**
     * ppt文档转pdf
     *
     * @param sourceFile
     * @param targetPath
     * @throws Exception
     */
    public static void pptToPdf(File sourceFile, String targetPath) throws Exception {
        // 创建Presentation实例
        Presentation presentation = new Presentation();
        FileInputStream inputStream = new FileInputStream(sourceFile);
        // 加载PPT示例文档
        presentation.loadFromStream(inputStream, com.spire.presentation.FileFormat.AUTO);
        // 保存为PDF
        presentation.saveToFile(targetPath, com.spire.presentation.FileFormat.PDF);
        presentation.dispose();
        IOUtils.closeQuietly(inputStream);

    }

}
