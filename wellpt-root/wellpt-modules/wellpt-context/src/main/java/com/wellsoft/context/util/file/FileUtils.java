package com.wellsoft.context.util.file;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream.UnicodeExtraFieldPolicy;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Description: 文件操作工具类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月25日.1	Asus		2015年12月25日		Create
 * </pre>
 * @date 2015年12月25日
 */
public class FileUtils {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 拷贝文件到目标 目录
     *
     * @param fileName   文件名
     * @param folderName 目标目录
     * @throws Exception
     */
    public static void copyFileToFolder(String fileName, String folderName) throws Exception {
        try {
            File file = new File(fileName);
            if (file.isFile()) {
                if (!folderName.endsWith("\\"))
                    folderName += "\\";
                FileInputStream fis = new FileInputStream(file);
                byte[] fileContent = new byte[fis.available()];
                if (fis.read(fileContent) < 0) {
                    // read error
                    LOG.error(new StringBuilder("file[").append(fileName).append("] read no data").toString());
                }
                String path = folderName + file.getName();
                FileOutputStream fos = new FileOutputStream(path);
                fos.write(fileContent);
                fis.close();
                fos.close();
            } else {
                throw new Exception(fileName + " is not a file or it is not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 剪切文件到目标目录
     *
     * @param fileName   文件名
     * @param folderName 目标目录
     * @throws Exception
     */
    public static void cutFileToFolder(String fileName, String folderName) throws Exception {
        try {
            File file = new File(fileName);
            if (file.isFile()) {
                copyFileToFolder(fileName, folderName);
                file.delete();
            } else {
                throw new Exception(fileName + " is not a file or it is not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 拷贝文件夹下的文件到目标目录
     *
     * @param oldFolderName 被拷贝文件夹
     * @param folderName    目标目录
     * @throws Exception
     */
    public static void copyFolderFilesToOtherFolder(String oldFolderName, String folderName) throws Exception {
        try {
            String strMsg = "";
            File file = new File(oldFolderName);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        try {
                            copyFileToFolder(files[i].getPath(), folderName);
                        } catch (Exception ee) {
                            LOG.error(ee.getMessage(), ee);
                            strMsg += "copy file<<" + files[i].getPath() + ">> error,message:" + ee.getMessage()
                                    + "\r\n";
                        }
                    }
                }
                if (!strMsg.equals("")) {
                    throw new Exception(strMsg);
                }
            } else {
                throw new Exception(oldFolderName + " is not a directory or it is not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 剪切文件夹下的文件到目标目录
     *
     * @param oldFolderName 被剪切的文件夹
     * @param folderName    目标目录
     * @throws Exception
     */
    public static void cutFolderFilesToOtherFolder(String oldFolderName, String folderName) throws Exception {
        try {
            String strMsg = "";
            File file = new File(oldFolderName);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        try {
                            cutFileToFolder(files[i].getPath(), folderName);
                        } catch (Exception ee) {
                            LOG.error(ee.getMessage(), ee);
                            strMsg += "cut file<<" + files[i].getPath() + ">> error,message:" + ee.getMessage()
                                    + "\r\n";
                        }
                    }
                }
                if (!strMsg.equals("")) {
                    throw new Exception(strMsg);
                }
            } else {
                throw new Exception(oldFolderName + " is not a directory or it is not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除文件夹下所有文件
     *
     * @param folderName 删除目录
     * @throws Exception
     */
    public static void deleteAllFilesInFolder(String folderName) throws Exception {
        try {
            String strMsg = "";
            File file = new File(folderName);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            } else {
                throw new Exception(folderName + " is not a directory or it is not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 向文件中写内容
     *
     * @param fileFullName 写入文件全路径
     * @param content      内容
     * @param overwrite    是否覆盖
     * @throws IOException 写入失败时抛出
     */
    public static void writeFile(String fileFullName, String content, boolean overwrite) throws IOException {

        if (fileFullName != null) {
            String path = fileFullName.substring(0, fileFullName.lastIndexOf("/"));
            if (!(new File(path).isDirectory())) {
                new File(path).mkdirs();
            }
        }
        File file = new File(fileFullName);
        FileWriter writer = new FileWriter(file);
        if (overwrite) {
            writer.write(content);
        } else {
            writer.write(content);
        }
        writer.flush();
        writer.close();
    }

    /**
     * 向文件中写内容(UTF-8编码)
     *
     * @param fileFullName 写入文件全路径
     * @param content      内容
     * @param overwrite    是否覆盖
     * @throws IOException 写入失败时抛出
     */
    public static void writeFileUTF(String fileFullName, String content, boolean overwrite) throws IOException {

        if (fileFullName != null) {
            String path = fileFullName.substring(0, fileFullName.lastIndexOf(File.separator));
            if (!(new File(path).isDirectory())) {
                new File(path).mkdirs();
            }
        }
        OutputStream os = new FileOutputStream(fileFullName);
        OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
        if (overwrite) {
            writer.write(content);
        } else {
            writer.write(content);
        }
        writer.flush();
        writer.close();
    }

    /**
     * 将文件的内容写入另一个文件
     *
     * @param fileFullName 被写入文件的 全路径
     * @param file         写入的文件
     * @throws Exception
     */
    public static void writeFile(String fileFullName, File file) throws Exception {
        if (file != null) {
            if (fileFullName != null) {
                String path = fileFullName.substring(0, fileFullName.lastIndexOf("/"));
                if (!(new File(path).isDirectory())) {
                    new File(path).mkdirs();
                }
            }

            FileOutputStream outputStream = new FileOutputStream(fileFullName);

            FileInputStream fileIn = new FileInputStream(file);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fileIn.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            fileIn.close();
            outputStream.close();
        }
    }

    /**
     * 将文件的内容写入另一个文件
     *
     * @param fileName 被写入文件的文件名
     * @param path     被写入文件的 路径
     * @param file     写入的文件
     * @throws Exception
     */
    public static void writeFile(String fileName, String path, File file) throws Exception {
        if (file != null) {
            File fileDir = null;
            if (path != null && !(fileDir = new File(path)).isDirectory()) {
                fileDir.mkdirs();
            }
            File copyFile = new File(path, fileName);
            FileOutputStream outputStream = new FileOutputStream(copyFile);

            FileInputStream inputStream = new FileInputStream(file);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            inputStream.close();
            outputStream.close();
        }
    }

    /**
     * 将数据流写入目标文件
     *
     * @param fileName 被写入文件的文件名
     * @param path     被写入文件的 路径
     * @param fileIn   写入的数据流
     * @throws Exception
     */
    public static void writeFile(String fileName, String path, InputStream fileIn) throws Exception {
        if (fileIn != null) {
            File fileDir = null;
            if (path != null && !(fileDir = new File(path)).isDirectory()) { // 合并单分支的判断条件
                fileDir.mkdirs();
            }
            File copyFile = new File(path, fileName);
            FileOutputStream outputStream = new FileOutputStream(copyFile);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fileIn.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            fileIn.close();
            outputStream.close();
        }
    }

    /**
     * 读取文件，按行加入集合
     *
     * @param fileFullName 文件地址
     * @return 文件转换后的集合
     * @throws Exception
     */
    public static ArrayList splitTxtToArray(String fileFullName) throws Exception {
        ArrayList txt = new ArrayList();
        File file = new File(fileFullName);
        BufferedReader in = new BufferedReader(new FileReader(file));
        String strLine = "";
        while ((strLine = in.readLine()) != null) {
            txt.add(strLine);
        }

        in.close();
        return txt;
    }

    /**
     * 获取目录下所有文件名
     *
     * @param folderName 目录名
     * @return 目录下所有文件名
     * @throws Exception
     */
    public static ArrayList<String> getFolderAllFileName(String folderName) throws Exception {
        ArrayList<String> fileNamelist = new ArrayList<String>();
        try {
            // String strMsg = "";
            File file = new File(folderName);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    fileNamelist.add(files[i].getName());
                }
            } else {
                throw new Exception(folderName + " is not a directory or it is not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fileNamelist;
    }

    /**
     * 使用UTF-8格式获取文件内容
     *
     * @param fileFullName 文件全路径
     * @return 文件内容
     * @throws Exception
     */
    public static final String getFileContentAsStringUTF(String fileFullName) throws Exception {
        File file = new File(fileFullName);
        StringBuilder sb = new StringBuilder();
        InputStreamReader input = new InputStreamReader(new FileInputStream(file), "utf-8");
        BufferedReader in = new BufferedReader(input);
        String strLine = "";
        while ((strLine = in.readLine()) != null) {
            sb.append(strLine);
            sb.append("\n");
        }

        input.close();
        in.close();
        return sb.toString();
    }

    /**
     * 获取文件内容
     *
     * @param fileFullName 文件全路径
     * @return 文件内容
     * @throws Exception
     */
    public static final String getFileContentAsString(String fileFullName) throws Exception {
        File file = new File(fileFullName);
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new FileReader(file));
        String strLine = "";
        while ((strLine = in.readLine()) != null) {
            sb.append(strLine);
            sb.append("\n");
        }
        in.close();
        return sb.toString();
    }

    /**
     * 获取文件夹下的全部文件
     *
     * @param folderName 文件夹名
     * @return 文件集
     */
    public static File[] getAllFilesInFolder(String folderName) {
        return getAllFilesInFolderByExtension(folderName, "");
    }

    /**
     * 获取文件夹下某一个扩展名的全部文件
     *
     * @param folderName 文件夹名
     * @param extension  文件扩展名
     * @return 某一个扩展名文件集
     */
    public static File[] getAllFilesInFolderByExtension(String folderName, String extension) {
        File dir = new File(folderName);
        if (dir.isDirectory()) {
            Collection rtn = new ArrayList();
            File[] files = dir.listFiles();

            if (extension != null && extension.trim().length() > 0) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().indexOf("." + extension) != -1) {
                        rtn.add(files[i]);
                    }
                }
                return (File[]) rtn.toArray(new File[rtn.size()]);
            } else {
                return files;
            }
        }

        return new File[0];
    }

    /**
     * 转换文件编码（只针对java文件）
     *
     * @param path         文件路径
     * @param fromEncoding 原始编码
     * @param toEncoding   换行后编码
     * @throws Exception
     */
    public static void convertFileEncoding(String path, String fromEncoding, String toEncoding) throws Exception {
        convertFileEncoding(new File(path), fromEncoding, toEncoding);
    }

    /**
     * 转换文件编码（只针对java文件）
     *
     * @param file         java文件
     * @param fromEncoding 原始编码
     * @param toEncoding   换行后编码
     * @throws Exception
     */
    public static void convertFileEncoding(File file, String fromEncoding, String toEncoding) throws Exception {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                convertFileEncoding(files[i], fromEncoding, toEncoding);
            }
        } else if (file.isFile() && file.getName().trim().toLowerCase().endsWith(".java")) {
            try {
                String command = "native2ascii -encoding " + fromEncoding + " " + file.getAbsolutePath()
                        + " c:/temp.java";
                Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                command = "native2ascii -reverse -encoding " + toEncoding + " c:/temp.java " + file.getAbsolutePath();
                process = Runtime.getRuntime().exec(command);
                process.waitFor();
                System.out.println(file.getAbsolutePath() + " Execute Successed");
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                new File("c:/temp.java").delete();
            }
        }
    }

    /**
     * 把文件复制到指定目录并且重命名
     *
     * @param fileName    文件名
     * @param newFileName 新文件 名
     * @param newFolder   指定目录
     * @throws Exception
     */
    public static void copyFileToFolderAndRenameFile(String fileName, String newFileName, String newFolder)
            throws Exception {
        try {
            File file = new File(fileName);
            if (file.isFile()) {
                if (!newFolder.endsWith("\\"))
                    newFolder += "\\";
                FileInputStream fis = new FileInputStream(file);
                byte[] fileContent = new byte[fis.available()];
                if (fis.read(fileContent) < 0) {
                    // read error
                    LOG.error(new StringBuilder("file[").append(fileName).append("] read no data").toString());
                }
                String path = newFolder + newFileName;
                FileOutputStream fos = new FileOutputStream(path);
                fos.write(fileContent);
                fis.close();
                fos.close();
            } else {
                throw new Exception(fileName + " is not a file or it is not exist!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件名（不包含扩展名）
     *
     * @param file 文件名
     * @return 文件名（不包含扩展名）
     */
    public static String getFileName(String file) {

        int idx = file.lastIndexOf(".");
        String ret = idx >= 0 ? file.substring(0, idx) : file;

        return ret;
    }

    /**
     * 获取文件扩展名
     *
     * @param file 文件名
     * @return 文件扩展名
     */
    public static String getFileExtension(String file) {

        int idx = file.lastIndexOf(".");
        String ret = idx >= 0 ? file.substring(idx + 1) : "";

        return ret;
    }

    /**
     * 根据文件路径获取上级目录
     *
     * @param path 文件路径
     * @return 上级目录
     */
    public static String getParent(String path) {

        int lastSlash = path.lastIndexOf('/');
        String ret = (lastSlash > 0) ? path.substring(0, lastSlash) : "";

        return ret;
    }

    /**
     * 根据文件路径获取文件名
     *
     * @param path 文件路径
     * @return 文件名
     */
    public static String getName(String path) {

        String ret = path.substring(path.lastIndexOf('/') + 1);

        return ret;
    }

    /**
     * 消除名称中的某些特殊符号
     *
     * @param name 名称
     * @return 消除某些特殊后的名称
     */
    public static String escape(String name) {

        String ret = name.replace('/', ' ');
        ret = ret.replace(':', ' ');
        ret = ret.replace('[', ' ');
        ret = ret.replace(']', ' ');
        ret = ret.replace('*', ' ');
        ret = ret.replace('\'', ' ');
        ret = ret.replace('"', ' ');
        ret = ret.replace('|', ' ');
        ret = ret.trim();

        return ret;
    }

    /**
     * 创建一个目录
     *
     * @return 目录文件
     * @throws IOException
     */
    public static File createTempDir() throws IOException {
        final File tmpFile;
        tmpFile = File.createTempFile("okm", Long.toString(System.nanoTime()));//
        if (!tmpFile.delete()) {
            throw new IOException();
        }
        if (!tmpFile.mkdir()) {
            throw new IOException();
        }
        return tmpFile;
    }

    /**
     * Create temp file with extension from mime
     */
    @Deprecated
    public static File createTempFileFromMime(String mimeType) throws IOException {
        //			MimeType mt = MimeTypeDAO.findByName(mimeType);
        //			String ext = mt.getExtensions().iterator().next();
        //			return File.createTempFile("okm", "." + ext);
        return null;
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return ture-删除成功;false-删除失败
     * @see org.apache.commons.io.FileUtils#deleteQuietly(File)
     */
    public static boolean deleteQuietly(File file) {
        return org.apache.commons.io.FileUtils.deleteQuietly(file);
    }

    /**
     * 创建ZIP压缩文件
     *
     * @param path 被压缩文件
     * @param os   压缩文件输出流
     * @throws IOException
     */
    public static void createZip(File path, OutputStream os) throws IOException {

        if (path.exists() && path.canRead()) {
            ZipArchiveOutputStream zos = new ZipArchiveOutputStream(os);
            zos.setComment("Generated by OpenKM");
            zos.setCreateUnicodeExtraFields(UnicodeExtraFieldPolicy.ALWAYS);
            zos.setUseLanguageEncodingFlag(true);
            zos.setFallbackToUTF8(true);
            zos.setEncoding("UTF-8");

            createZipHelper(path, zos, path.getName());

            zos.flush();
            zos.finish();
            zos.close();
        } else {
            throw new IOException("Can't access " + path);
        }

    }

    /**
     * 创建ZIP压缩文件
     *
     * @param fs     被压缩文件
     * @param zos    压缩文件输出流
     * @param zePath 压缩文件入口路径
     * @throws IOException
     */
    private static void createZipHelper(File fs, ZipArchiveOutputStream zos, String zePath) throws IOException {

        File[] files = fs.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                createZipHelper(files[i], zos, zePath + "/" + files[i].getName());
            } else {
                ZipArchiveEntry zae = new ZipArchiveEntry(files[i], zePath + "/" + files[i].getName());
                zos.putArchiveEntry(zae);
                FileInputStream fis = new FileInputStream(files[i]);
                IOUtils.copy(fis, zos);
                fis.close();
                zos.closeArchiveEntry();
            }
        }

    }

    /**
     * 获取目录下的文件数
     *
     * @param dir 文件目录
     * @return 文件数
     */
    public static int countFiles(File dir) {
        File[] found = dir.listFiles();
        int ret = 0;

        if (found != null) {
            for (int i = 0; i < found.length; i++) {
                if (found[i].isDirectory()) {
                    ret += countFiles(found[i]);
                }

                ret++;
            }
        }

        return ret;
    }

    public static void main(String[] args) {
        try {
            // FileOperate.cutFolderFilesToOtherFolder("D:\\test", "d:\\yhp");
            convertFileEncoding("C:/Java/workspace/OBPMBeta/src", "GBK", "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
