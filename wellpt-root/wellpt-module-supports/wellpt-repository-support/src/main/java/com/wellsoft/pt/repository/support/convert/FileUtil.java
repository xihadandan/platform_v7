package com.wellsoft.pt.repository.support.convert;

import com.wellsoft.pt.repository.support.FileUploadHandler;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 文件工具类
 *
 * @author jackCheng
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-16.1	jackCheng		2013-4-16		Create
 * </pre>
 * @date 2013-4-16
 */
public class FileUtil {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);
    private static final String PHYSICAL_FILE_TABLE = "fs";

    private static String currentTenantId;

    /**
     * 获取文件名称[不含后缀名]
     *
     * @param fileName
     * @return
     */
    public static String getFilePrefix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, splitIndex);
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName
     * @return
     */
    public static String getFileSufix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(splitIndex + 1);
    }

    /**
     * 文件复制
     *
     * @param inputFile
     * @param outputFile
     * @throws IOException
     */
    public static void copyFile(String inputFile, String outputFile) throws IOException {
        File sFile = new File(inputFile);
        File tFile = new File(outputFile);
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(sFile);
            output = new FileOutputStream(tFile);
            IOUtils.copyLarge(input, output);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * 将文档转换成SWF文件，参加中列表文件所在的位置，即为swf所生成的位置，原文件名与swf文件名一致
     *
     * @param files
     */
    public static void convert2SWF(List<java.io.File> files) {
        if (null == files || files.size() == 0) {
            return;
        }
        List<java.io.File> dosFiles = new ArrayList<java.io.File>();

        for (int i = 0; i < files.size(); i++) {
            dosFiles.add(files.get(i));
            //每20个文档一个处理线程
            if (i % 20 == 0) {

                new Thread(new FileUploadHandler(dosFiles.subList(i, i == 0 ? i + 1 : (i % 20 + 1) * 20))).start();
                //tempFiles.clear();
            }
        }

    }

    /**
     * 将文件流生成的相应的swf文件，fileName是文件流对应的文件名称【带后缀】，这个接口生成的swf文件放在System.properties中配置的TempSaveSwfFilePath.dir中
     *
     * @param fileStream
     * @param fileName
     */
    public static void convert2SWF(InputStream fileStream, String fileName) {
        throw new UnsupportedOperationException();// new Thread(new FileUploadHandler(fileStream, fileName));
    }

    public static boolean isImage(String filePath) throws FileNotFoundException {
        try {
            File f = new File(filePath);
            BufferedImage bi = ImageIO.read(f);
            return bi != null;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public static void main(String[] args) {
        //		File tempFile = new File("F:\\配置问题.doc");
        //		List<File> listFile = new ArrayList<File>();
        //		listFile.add(tempFile);
        //		FileUtil.convert2SWF(listFile);
        String a = getFilePrefix("12312313123");
        System.out.println(a);
    }

    /**
     * 得到当前mongo文件表名
     */
    public static String getPHYSICAL_TABLE() {
        return PHYSICAL_FILE_TABLE;
    }

    public static String getCurrentTenantId() {
        if (currentTenantId == null) {
            return TenantContextHolder.getTenantId();
        } else {
            return currentTenantId;
        }
    }
}
