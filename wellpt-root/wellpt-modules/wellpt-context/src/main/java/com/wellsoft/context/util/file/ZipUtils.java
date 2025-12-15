package com.wellsoft.context.util.file;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

/**
 * Description: Zip压缩文件处理工具类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月23日.1	Asus		2015年12月23日		Create
 * </pre>
 * @date 2015年12月25日
 */
public class ZipUtils {
    /**
     * 缓冲区大小
     */
    public static final int BUFFER_SIZE = 10240;
    /**
     * 文件名编码
     */
    private static final String SUN_JNU_ENCODING = "sun.jnu.encoding";

    /**
     * 将文件压缩如zip中
     *
     * @param srcFolder   要压缩的文件
     * @param destZipFile 压缩文件
     * @throws Exception
     */
    public static void zipFolder(String srcFolder, String destZipFile) throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;

        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);
        zip.setEncoding(System.getProperty(SUN_JNU_ENCODING));

        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();
    }

    /**
     * 如何描述该方法
     *
     * @param srcFolder
     * @param destZipFile
     * @param includeSrcFolder
     * @throws Exception
     */
    public static void zipFolder(String srcFolder, String destZipFile, boolean includeSrcFolder) throws Exception {
        if (includeSrcFolder) {
            zipFolder(srcFolder, destZipFile);
        } else {
            ZipOutputStream zip = null;
            FileOutputStream fileWriter = null;

            fileWriter = new FileOutputStream(destZipFile);
            zip = new ZipOutputStream(fileWriter);
            zip.setEncoding(System.getProperty(SUN_JNU_ENCODING));

            // addFolderToZip("", srcFolder, zip);
            File folder = new File(srcFolder);
            String[] fileNames = folder.list();
            for (int i = 0; i < fileNames.length; i++) {
                addFileToZip("", srcFolder + "/" + fileNames[i], zip);
            }

            zip.flush();
            zip.close();
        }
    }

    static private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {
        File folder = new File(srcFile);
        if (folder.isDirectory()) {
            if (StringUtils.isBlank(path)) {
                path = folder.getName();
            } else {
                path = path + "/" + folder.getName();
            }
            ZipEntry zipEntry = new ZipEntry(path + "/");
            zipEntry.setUnixMode(755);
            zip.putNextEntry(zipEntry);
            File[] f = folder.listFiles();
            if (f != null) {
                for (int i = 0; i < f.length; i++) {
                    addFileToZip(path, f[i].getPath(), zip);
                }
            }
        } else {
            byte[] buf = new byte[1024];
            int len;
            FileInputStream in = new FileInputStream(srcFile);
            if (StringUtils.isBlank(path)) {
                ZipEntry zipEntry = new ZipEntry(folder.getName());
                zipEntry.setUnixMode(644);
                zip.putNextEntry(zipEntry);
            } else {
                ZipEntry zipEntry = new ZipEntry(path + "/");
                zipEntry.setUnixMode(644);
                zip.putNextEntry(zipEntry);
                ZipEntry zipEntryFloder = new ZipEntry(path + "/" + folder.getName());
                zipEntryFloder.setUnixMode(644);
                zip.putNextEntry(zipEntryFloder);
            }
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }

            in.close();
        }
    }

    static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
        File folder = new File(srcFolder);

        String[] fileNames = folder.list();
        for (int i = 0; i < fileNames.length; i++) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileNames[i], zip);
            } else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileNames[i], zip);
            }
        }
    }

    /**
     * 创建一个Zip压缩文件
     *
     * @param archiveFile     压缩文件
     * @param tobeZippedFiles 要压缩的文件
     * @throws Exception
     */
    public static void createZipArchive(File archiveFile, File[] tobeZippedFiles) throws Exception {
        try {
            byte buffer[] = new byte[BUFFER_SIZE];
            // Open archive file
            FileOutputStream stream = new FileOutputStream(archiveFile);
            ZipOutputStream out = new ZipOutputStream(stream);
            out.setEncoding(System.getProperty(SUN_JNU_ENCODING));

            for (int i = 0; i < tobeZippedFiles.length; i++) {
                if (tobeZippedFiles[i] == null || !tobeZippedFiles[i].exists() || tobeZippedFiles[i].isDirectory())
                    continue;

                // Add archive entry
                // String fileName =
                // StringUtil.toUTFBody(tobeZippedFiles[i].getName());
                ZipEntry zipAdd = new ZipEntry(tobeZippedFiles[i].getName());
                zipAdd.setTime(tobeZippedFiles[i].lastModified());
                out.putNextEntry(zipAdd);

                // Read input & write to output
                FileInputStream in = new FileInputStream(tobeZippedFiles[i]);
                while (true) {
                    int nRead = in.read(buffer, 0, buffer.length);
                    if (nRead <= 0)
                        break;
                    out.write(buffer, 0, nRead);
                }
                in.close();
            }

            out.flush();
            out.close();
            stream.close();
            // System.out.println("Adding completed OK");
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 压缩文件
     *
     * @param newFileName   压缩文件名称
     * @param inputFileName 要压缩文件的路径
     * @param destDir       压缩文件存放目录
     * @throws Exception
     */
    public static void compressFiles(String newFileName, String inputFilePath, String destDir) throws Exception {
        compressFiles(newFileName, new String[]{inputFilePath}, destDir);
    }

    /**
     * 压缩文件
     *
     * @param newFileName    压缩文件名
     * @param inputFilePaths 要压缩的文件路径数组
     * @param destDir        压缩文件存放目录
     * @throws Exception
     */
    public static void compressFiles(String newFileName, String[] inputFilePaths, String destDir) throws Exception {
        String zipPathName = destDir + "/" + newFileName + ".zip";

        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipPathName));
        zipOut.setEncoding(System.getProperty(SUN_JNU_ENCODING));

        for (int i = 0; i < inputFilePaths.length; i++) {
            File inputFile = new File(inputFilePaths[i]);

            if (inputFile.exists() && inputFile.isFile()) {
                FileInputStream in = new FileInputStream(inputFile);

                zipOut.putNextEntry(new ZipEntry(inputFile.getName()));

                int nNumber;
                byte[] buffer = new byte[512];
                while ((nNumber = in.read(buffer)) != -1) {
                    zipOut.write(buffer, 0, nNumber);
                }
                in.close();
            }
        }
        zipOut.close();
    }

    /**
     * 读取Zip内的文件的内容(UTF-8编码读取文件内容)
     *
     * @param fileName 文件全名
     * @return 文件内容数组（一个文件一个String）
     * @throws IOException
     */
    public static String[] readZipFile(String fileName) throws IOException {
        return readZipFile(fileName, "UTF-8");
    }

    public static String[] readZipFile(String fileName, String charsetName) throws IOException {
        return readZipFile(new File(fileName), charsetName);
    }

    /**
     * 读取Zip内的文件的内容(UTF-8编码读取文件内容)
     *
     * @param zipFile zip格式文件
     * @return 文件内容数组（一个文件一个String）
     * @throws IOException
     */
    public static String[] readZipFile(File zipFile) throws IOException {
        return readZipFile(zipFile, "UTF-8");
    }

    /**
     * 读取Zip内的文件的内容
     *
     * @param zipFile
     * @param charsetName 读取文件内容编码 {@linkplain java.nio.charset.Charset </code>charset<code>}
     * @return 文件内容数组（一个文件一个String）
     * @throws IOException
     */
    public static String[] readZipFile(File zipFile, String charsetName) throws IOException {
        Collection<String> contentList = new ArrayList<String>();
        ZipFile zip = new ZipFile(zipFile);
        // 建立与目标文件的输入连接
        Enumeration<ZipEntry> entries = zip.getEntries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            // String fileName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int nNumber;
            byte[] buffer = new byte[512];
            while ((nNumber = in.read(buffer)) != -1) {
                out.write(buffer, 0, nNumber);
            }
            contentList.add(out.toString(charsetName));

            in.close();
            out.close();
        }

        return contentList.toArray(new String[contentList.size()]);
    }

    /**
     * 解压zip文件到当前目录
     *
     * @param inputFileName 要解压的文件
     * @throws Exception
     */
    public static void decompress(String inputFileName) throws Exception {
        decompress(new File(inputFileName), "");
    }

    /**
     * 解压zip文件到指定目录
     *
     * @param infile  要解压的文件
     * @param destDir 目标目录
     * @throws Exception
     */
    public static void decompress(File infile, String destDir) throws Exception {
        // 检查是否是ZIP文件
        ZipFile zip = new ZipFile(infile);
        // 建立与目标文件的输入连接
        Enumeration<ZipEntry> entries = zip.getEntries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String fileName = entry.getName();
            InputStream in = zip.getInputStream(entry);

            File dir = new File(destDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fullPath = destDir + "/" + fileName;
            FileOutputStream out = new FileOutputStream(fullPath);

            int nNumber;
            byte[] buffer = new byte[512];
            while ((nNumber = in.read(buffer)) != -1) {
                out.write(buffer, 0, nNumber);
            }

            in.close();
            out.close();
        }
    }

    /**
     * 根据扩展名获取zip中的文件（多个获取第一个）
     *
     * @param extension 扩展名
     * @return Zip中对于扩展名的文件
     * @throws IOException
     */
    public static File getFileByExtension(ZipFile zipFile, String extension) throws IOException {
        Enumeration<ZipEntry> entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().indexOf("." + extension) != -1) {
                InputStream in = zipFile.getInputStream(entry);
                File rtn = new File(entry.getName());
                FileOutputStream out = new FileOutputStream(rtn);

                int nNumber;
                byte[] buffer = new byte[512];
                while ((nNumber = in.read(buffer)) != -1) {
                    out.write(buffer, 0, nNumber);
                }

                in.close();
                out.close();

                return rtn;
            }
        }

        return null;
    }

}