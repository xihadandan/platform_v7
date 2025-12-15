package com.wellsoft.context.util.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: excel工具类工厂类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月24日.1	Asus		2015年12月24日		Create
 * </pre>
 * @date 2015年12月24日
 */
public class ExcelUtilFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelUtilFactory.class);

    private ExcelUtilFactory instance = null;

    private HttpServletRequest excelRequest = null;

    private HttpServletResponse excelResponse = null;

    private ExcelUtilFactory() {
    }

    /**
     * 创建一个excel到指定文件
     *
     * @param sheetName     名称
     * @param list          excel内容
     * @param firstRowValue excel标题
     * @param realPath      输出的文件路径
     */
    public static void outputToRealPath(String sheetName, List<Object[]> list, String[] firstRowValue, String realPath) {
        ExcelWorkBook work = new ExcelWorkBook();
        work.setWorkbookName(sheetName);
        ExcelCellStyleUtils style = new ExcelCellStyleUtils(work);
        ExcelSheetCell.createCurrRowTitle(work, firstRowValue, style.titleStyle);
        ExcelSheetCell.createCurrRowRecord(work, list);

        work.writerFileStream(realPath);
    }

    /**
     * 创建excel输出到指定目录下
     *
     * @param sheetName  名称
     * @param list       内容
     * @param titleValue 标题
     * @param destDir
     * @param excelName
     */
    public static void outputToDirectory(String sheetName, List<Object[]> list, String[] titleValue, String destDir,
                                         String excelName) {
        ExcelWorkBook work = new ExcelWorkBook();
        work.setWorkbookName(sheetName);
        ExcelCellStyleUtils style = new ExcelCellStyleUtils(work);
        ExcelSheetCell.createCurrRowTitle(work, titleValue, style.titleStyle);
        ExcelSheetCell.createCurrRowRecord(work, list);

        work.write(destDir, excelName);
    }

    private static File getExcelDownloadPath(String excelName) {
        // String realPath = excelRequest.getRealPath("/UploadFile");
        // excelName = realPath+ "\\" + excelName;
        // excelName = replaceRNAll(excelName);
        File excelFile = new File(excelName);
        return excelFile;
    }

    // 用传入参数的判断
    private static boolean excelFileNotFund(String strfileName) {
        return strfileName == null || strfileName.equals("");
    }

    public static void main(String[] args) {
        long beginTime = System.currentTimeMillis();
        System.out.println("开始时间:" + beginTime / 1000);
        List<Object[]> beanList = new ArrayList<Object[]>();
        String[] excelTitle = new String[10];
        excelTitle[0] = "编号";
        excelTitle[1] = "基金名称";
        excelTitle[2] = "单位净值";
        excelTitle[3] = "日增长率";
        excelTitle[4] = "累积净值";
        excelTitle[5] = "编号";
        excelTitle[6] = "基金名称";
        excelTitle[7] = "单位净值";
        excelTitle[8] = "日增长率";
        excelTitle[9] = "累积净值";
        for (int i = 0; i < 100; i++) {
            String[] beanArr = new String[10];
            beanArr[0] = String.valueOf(i + 1);
            beanArr[1] = "基金A" + i;
            beanArr[2] = "1.0427";
            beanArr[3] = "-2.7514%";
            beanArr[4] = "1.1558";
            beanArr[5] = String.valueOf(i + 1);
            beanArr[6] = "基金A" + i;
            beanArr[7] = "1.0427";
            beanArr[8] = "-2.7514%";
            beanArr[9] = "1.1558";
            beanList.add(beanArr);
        }
        ExcelUtilFactory.outputToDirectory("今天测试_factory", beanList, excelTitle, "c:/", "test.xls");
        long endTime = System.currentTimeMillis();
        System.out.println("测试,总计" + (endTime - beginTime) / 1000 + "秒,用时");
    }

    /**
     * 返回工厂实体
     *
     * @param request  web请求
     * @param response web响应
     * @return 工厂实体
     */
    public ExcelUtilFactory getInstance(HttpServletRequest request, HttpServletResponse response) {
        if (instance == null) {
            instance = new ExcelUtilFactory();
        }
        excelRequest = request;
        excelResponse = response;
        return instance;
    }

    /**
     * 输出一个EXCE文档
     *
     * @param excelName     excel名称
     * @param list          excle内容
     * @param firstRowValue excel标题
     */
    public void outputExcel(String excelName, List<Object[]> list, String[] firstRowValue) {
        ExcelWorkBook work = new ExcelWorkBook();
        work.setWorkbookName(excelName);
        ExcelCellStyleUtils style = new ExcelCellStyleUtils(work);
        ExcelSheetCell.createCurrRowTitle(work, firstRowValue, style.titleStyle);
        ExcelSheetCell.createCurrRowRecord(work, list);
        String realPath = getExcelRealPath(excelName);
        // String realPath = "e:/temp/testRealPath_2.xls";
        work.writerFileStream(realPath);
        downloadFile(realPath);
    }

    private String getExcelRealPath(String excelName) {
        String realPath = excelRequest.getServletContext().getRealPath("/UploadFile");
        File excelFile = new File(realPath);
        if (!excelFile.exists()) {
            excelFile.mkdirs();
        }
        excelName = realPath + File.separator + excelName + ".xls";
        return excelName;
    }

    private void downloadFile(String strfileName) {
        try {
            // 获得ServletContext对象
            if (excelFileNotFund(strfileName)) {
                throw new IllegalArgumentException("File=[" + strfileName + "] not fund file path");
            }
            // 取得文件的绝对路径
            File excelFile = getExcelDownloadPath(strfileName);
            putResponseStream(strfileName, excelFile);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * @param strfileName :
     *                    文件名称
     * @param excelName   :
     *                    文件的相对路径或绝对路径
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void putResponseStream(String strfileName, File excelName) throws UnsupportedEncodingException,
            FileNotFoundException, IOException {
        strfileName = URLEncoder.encode(strfileName, "UTF-8");
        excelResponse.setHeader("Content-disposition", "attachment; filename=" + strfileName);
        excelResponse.setContentLength((int) excelName.length());
        excelResponse.setContentType("application/x-download");
        byte[] buffer = new byte[1024];
        int i = 0;
        FileInputStream fis = new FileInputStream(excelName);
        while ((i = fis.read(buffer)) > 0) {
            excelResponse.getOutputStream().write(buffer, 0, i);
        }
        fis.close();
    }
}
