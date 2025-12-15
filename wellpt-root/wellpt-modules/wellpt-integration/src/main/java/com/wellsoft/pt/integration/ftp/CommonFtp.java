package com.wellsoft.pt.integration.ftp;

import com.wellsoft.context.config.Config;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class CommonFtp {

    private static final int Timeout = 300000;// 180000;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private String encoding;
    private FTPClient ftp;
    private String host;
    private int port;
    private String username;
    private String password;
    // 连接创建时间
    private long createTime;

    public CommonFtp() {
        encoding = System.getProperty("file.encoding");
    }

    public CommonFtp(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        encoding = System.getProperty("file.encoding");
    }

    /**
     * 选择多个元素
     *
     * @param document
     * @param xPath
     * @return
     */
    @SuppressWarnings({"unchecked", "cast"})
    public static List<Element> selectElements(Document document, String xPath) {
        XPath selector = DocumentHelper.createXPath(xPath);
        return (List<Element>) selector.selectNodes(document);
    }

    public static void main(String[] args) {

    }

    /**
     * 打开ftp连接
     *
     * @throws IOException
     */
    public void connect() throws IOException {
        try {
            this.ftp = new FTPClient();
            // for the socket connection
            this.ftp.setDefaultTimeout(Timeout / 10); // 30s
            this.ftp.connect(this.host, this.port);
            // opening a data connection socket.
            this.ftp.setDataTimeout(Timeout);
            // for the currently open socket connection.
            this.ftp.setSoTimeout(Timeout);
            logger.info("Connected to " + this.host + ":" + this.port);
            int reply = this.ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftp.disconnect();
                System.err.println("FTP server refused connection.");
                throw new IOException("FTP server refused connection.");
            }

            if (!this.ftp.login(this.username, this.password)) {
                this.ftp.logout();
                throw new IOException("FTP server refused connection.");
            }
        } catch (IOException ex) {
            logger.error("Connect to " + this.host + ":" + this.port + " Error:  " + ex);
            logger.error(ex.getMessage(), ex);
        } finally {
            createTime = System.currentTimeMillis();
        }
    }

    private void logActiveLength(String happenTimePoint) {
        logger.debug(happenTimePoint + ": this ftp connection has been active length "
                + (System.currentTimeMillis() - createTime) / 1000.0 + "s");
    }

    /**
     * 关闭ftp连接
     */
    public void disconnect() {
        logActiveLength("disconnect");
        if (this.ftp == null) {
            return;
        }
        try {
            this.ftp.logout();
        } catch (Exception ioe) {
            logger.error(ioe.getMessage(), ioe);
        } finally {
            if (this.ftp.isConnected()) {
                try {
                    this.ftp.disconnect();
                } catch (Exception ioe) {
                    logger.error(ioe.toString());
                }
            }
            logger.info("FTP：连接关闭");
        }
    }

    /**
     * 创建文件夹
     *
     * @param path
     * @return
     * @throws Exception
     */
    public synchronized boolean mkdir(String path) throws Exception {
        logActiveLength(" before mkdir");

        boolean flag = true;
        try {
            if (!this.ftp.changeWorkingDirectory(path))
                if (!this.ftp.makeDirectory(path)) {
                    String parentPath = path.substring(0, path.lastIndexOf("/"));
                    mkdir(parentPath);
                    this.ftp.makeDirectory(path);
                    logger.info("FTP创建文件夹：" + path);
                } else {
                    logger.info("FTP创建文件夹：" + path);
                }
        } catch (Exception localException) {
            logger.error(localException.getMessage(), localException);
        }

        logActiveLength(" after mkdir");

        return flag;
    }

    /**
     * 上传文件
     *
     * @param srcFile
     * @param tarFile
     * @return
     * @throws Exception
     */
    public synchronized boolean upload(String srcFile, String tarFile) throws Exception {
        logActiveLength(" before upload");

        boolean retval = true;
        tarFile = FileUtil.normalizePath(tarFile);
        if (tarFile.lastIndexOf("/") > 1) {
            String dir = tarFile.substring(0, tarFile.lastIndexOf("/") + 1);
            mkdir(dir);
            this.ftp.changeWorkingDirectory(dir);
        } else {
            this.ftp.changeWorkingDirectory("/");
        }

        InputStream input = null;
        try {
            File file_in = new File(srcFile);
            if (file_in.isDirectory())
                throw new IOException("FTP cannot upload a directory.");
            this.ftp.setFileType(2);
            this.ftp.enterLocalPassiveMode();

            input = new FileInputStream(srcFile);
            this.ftp.storeFile(tarFile, input);
            logger.info("上传文件:" + srcFile + " to " + tarFile);
        } catch (Exception ex) {
            retval = false;
            logger.error("上传文件失败：" + srcFile + " to " + tarFile + " Failure! " + ex);
        } finally {
            IOUtils.closeQuietly(input);
            logActiveLength(" after upload");

        }
        return retval;
    }

    /**
     * 上传文件（封装后的）
     *
     * @param srcFile
     * @param tarFile
     * @return
     * @throws Exception
     */
    public synchronized boolean uploadFile(File file, String tarFile) throws Exception {
        // String encoding = System.getProperty("file.encoding");

        logActiveLength(" before uploadFile1");

        this.ftp.setControlEncoding(encoding);

        boolean retval = true;
        tarFile += file.getName() + ".oa";// 上传时加.oa后缀
        tarFile = FileUtil.normalizePath(tarFile);
        if (tarFile.lastIndexOf("/") > 1) {
            String dir = tarFile.substring(0, tarFile.lastIndexOf("/") + 1);
            mkdir(dir);
            this.ftp.changeWorkingDirectory(dir);
        } else {
            this.ftp.changeWorkingDirectory("/");
        }
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            this.ftp.setFileType(2);
            this.ftp.enterLocalPassiveMode();
            this.ftp.storeFile(tarFile, in);
            logger.info("上传文件成功:" + file.getName() + " to " + tarFile);
        } catch (Exception ex) {
            retval = false;
            logger.error("上传文件失败：" + file.getName() + " to " + tarFile + " Failure! " + ex);
            logger.error(ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(in);
            logActiveLength(" after uploadFile1");
        }

        return retval;
    }

    /**
     * 判断文件是否在ftp服务器
     *
     * @param tarFile
     * @param fileName
     * @return
     */
    public synchronized boolean findFileByName(String tarFile, String fileName) {
        // String encoding = System.getProperty("file.encoding");
        logActiveLength(" before findFileByName");
        this.ftp.setControlEncoding(encoding);
        this.ftp.enterLocalPassiveMode();
        FTPFile[] fs = this.listFiles(tarFile);
        for (FTPFile ff : fs) {
            String ffName;
            try {
                ffName = new String(ff.getName().getBytes("GBK"), "UTF-8");
                if (ffName.indexOf(fileName) > -1) {
                    return true;
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                logger.error(e.getMessage(), e);
            }
        }
        logActiveLength(" after findFileByName");
        return false;
    }

    /**
     * 重命名
     *
     * @param tarFile
     * @param fileName
     * @return
     */
    public synchronized boolean rename(String tarFile, String fileName) {
        logActiveLength(" before rename");
        // String encoding = System.getProperty("file.encoding");
        this.ftp.setControlEncoding(encoding);
        this.ftp.enterLocalPassiveMode();
        try {
            String ffName = new String(fileName.getBytes("GBK"), "UTF-8");
            try {
                this.ftp.rename(tarFile + ffName, tarFile + ffName.replace(".oa", ""));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            return true;
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        logActiveLength(" before rename");
        return false;
    }

    /**
     * 下载文件
     *
     * @param tarFile
     * @param fileName
     * @return
     */
    public synchronized File downFile(String tarFile, String fileName) {
        logActiveLength(" before downFile");
        tarFile = FileUtil.normalizePath(tarFile);
        OutputStream is = null;
        File file = null;
        try {
            // String encoding = System.getProperty("file.encoding");
            this.ftp.setControlEncoding(encoding);
            this.ftp.setFileType(2);
            this.ftp.enterLocalPassiveMode();

            // 获取文件列表
            String ffName = new String(fileName.getBytes("GBK"), "UTF-8");
            File folder = new File(Config.APP_DATA_DIR + "/mongofilesyn/import/");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            file = new File(folder, ffName);
            if (!file.exists() || file.length() <= 0) {
                FTPFile[] files = ftp.listFiles(ffName);
                if (files != null && files.length > 0) {
                    is = new FileOutputStream(file);
                    this.ftp.retrieveFile(ffName, is);
                    logger.info("下载文件成功:" + tarFile);
                } else {
                    file = null;
                }
            }
            return file;
        } catch (Exception ex) {
            if (file != null && file.exists()) {
                // 下载失败,删除错误数据
                IOUtils.closeQuietly(is);
                file.delete();
                file = null;
            }
            logger.error("下载文件失败：" + tarFile + " Failure! ", ex);
        } finally {
            IOUtils.closeQuietly(is);
            logActiveLength(" after downFile");
        }
        return null;
    }

    /**
     * 下载文件
     *
     * @param tarFile
     * @param fileName
     * @return
     */
    public synchronized FTPFile[] listFolder(String pwd) {
        logActiveLength(" before listFolder");
        pwd = FileUtil.normalizePath(pwd);
        FTPFile[] fs = new FTPFile[0];
        try {
            // String encoding = System.getProperty("file.encoding");
            this.ftp.setControlEncoding(encoding);
            this.ftp.setFileType(2);
            this.ftp.enterLocalPassiveMode();
            fs = this.ftp.listFiles(pwd);
        } catch (Exception ex) {
            logger.error("拉去目录失败：" + pwd + " Failure! " + ex);
        }
        logActiveLength(" after listFolder");
        return fs;
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public synchronized boolean delete(String filePath) throws Exception {
        logActiveLength("before delete");
        this.ftp.setFileType(2);
        this.ftp.enterLocalPassiveMode();
        boolean flag = this.ftp.deleteFile(filePath);
        if (flag) {
            logger.info("FTP删除文件" + filePath);
        }
        logActiveLength("after delete");
        return flag;
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public synchronized boolean deleteFile(String tarFile, String name) throws Exception {
        logActiveLength("deleteFile: tarFile= " + tarFile + ", name=" + name);
        boolean flag = false;
        this.ftp.setFileType(2);
        this.ftp.enterLocalPassiveMode();
        // 获取文件列表
        String ffName = new String(name.getBytes("GBK"), "UTF-8");
        flag = this.ftp.deleteFile(tarFile + ffName);
        if (flag) {
            logger.info("FTP删除文件：" + ffName);
        }
        return flag;
    }

    /**
     * 清理文件
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public synchronized boolean clearFile(String tarFile) {
        logActiveLength("before clearFile");
        boolean flag = false;
        try {
            this.ftp.setFileType(2);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
        }
        this.ftp.enterLocalPassiveMode();
        // 获取文件列表
        FTPFile[] fs = this.listFiles(tarFile);
        for (FTPFile ff : fs) {
            try {
                String ffName = new String(ff.getName().getBytes("GBK"), "UTF-8");
                flag = this.ftp.deleteFile(tarFile + ffName);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.error("FTP删除文件失败");
            }
        }
        logActiveLength("after clearFile");
        return flag;
    }

    /**
     * 获取文件列表
     *
     * @param path
     * @return
     */
    public FTPFile[] listFiles(String path) {
        logActiveLength("before listFiles");
        FTPFile[] files = (FTPFile[]) null;
        try {
            // String encoding = System.getProperty("file.encoding");
            this.ftp.setControlEncoding(encoding);
            this.ftp.setFileType(2);
            // 穿透防火墙
            this.ftp.enterLocalPassiveMode();
            files = this.ftp.listFiles(path);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
        logActiveLength("after listFiles");
        return files;
    }

    /**
     * ftp是否连接
     *
     * @return
     */
    public boolean isConnected() {
        if (this.ftp == null) {
            return false;
        }
        return this.ftp.isConnected();
    }

    /**
     * 如何描述该方法
     *
     * @param host
     * @param port
     * @param user
     * @param pass
     * @return
     */
    public boolean isConnect(String host, int port, String user, String pass) {
        try {
            this.ftp = new FTPClient();
            this.ftp.setDefaultPort(port);
            this.ftp.connect(host);
            System.out.println("Connected to " + host + ".");
            int reply = this.ftp.getReplyCode();
            if (!(FTPReply.isPositiveCompletion(reply))) {
                this.ftp.disconnect();
                logger.info("FTP server refused connection.");
                return false;
            }

            if (!(this.ftp.login(user, pass))) {
                this.ftp.logout();
                return false;
            }
        } catch (Exception ex) {
            return false;
        } finally {
            try {
                if (this.ftp != null)
                    this.ftp.disconnect();
            } catch (Exception e) {

            }
        }

        return true;
    }
}