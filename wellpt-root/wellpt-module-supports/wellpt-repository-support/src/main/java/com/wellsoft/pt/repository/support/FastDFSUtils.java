package com.wellsoft.pt.repository.support;

import org.apache.commons.io.IOUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.InputStream;
import java.util.Properties;

/**
 * Description: 分布式文件系统FastDFS客户端工具类
 *
 * @author chenq
 * @date 2019/12/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/12/19    chenq		2019/12/19		Create
 * </pre>
 */
public class FastDFSUtils {
    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static Logger logger = LoggerFactory.getLogger(FastDFSUtils.class);
    private static TrackerClient TRACKER_CLIENT = null;
    private static String group;


    static {
        try {
            Resource[] resources = resourcePatternResolver.getResources(
                    ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                            + "system-fastDFS.properties");
            if (resources.length > 0) {
                Properties properties = new Properties();
                properties.load(resources[0].getInputStream());
                ClientGlobal.initByProperties(properties);
                logger.info("fastDFS client properties：{}", ClientGlobal.configInfo());
                TRACKER_CLIENT = new TrackerIpPortTransformClient(
                        properties);
                group = properties.getProperty("fastdfs.storage_server.group");
            }
        } catch (Exception e) {
            logger.error("无法连接分布式文件系统FastDFS", e);
        }

    }

    private static StorageClient1 storageClient() throws Exception {
        TrackerServer trackerServer = TRACKER_CLIENT.getConnection();
        return new StorageClient1(trackerServer,
                TRACKER_CLIENT.getStoreStorage(trackerServer, group));
    }

    /**
     * 上传
     *
     * @param inputStream   文件流
     * @param fileExtName   （可选）文件后缀名
     * @param nameValuePair 其他文件信息
     * @return 文件ID
     * @throws Exception
     */
    public static String uploadFile(InputStream inputStream, String fileExtName,
                                    NameValuePair[] nameValuePair) throws Exception {
        return storageClient().upload_file1(IOUtils.toByteArray(inputStream), fileExtName,
                nameValuePair);
    }

    /**
     * 上传
     *
     * @param bytes         文件二进制
     * @param fileExtName   （可选）文件后缀名
     * @param nameValuePair 其他文件信息
     * @return 文件ID
     * @throws Exception
     */
    public static String uploadFile(byte[] bytes, String fileExtName,
                                    NameValuePair[] nameValuePair) throws Exception {
        return storageClient().upload_file1(group, bytes, fileExtName, nameValuePair);
    }

    /**
     * 上传
     *
     * @param filePath       文件路径
     * @param fileExtName    （可选）文件后缀名
     * @param nameValuePairs 其他文件信息
     * @return 文件ID
     * @throws Exception
     */
    public static String uploadFile(String filePath, String fileExtName,
                                    NameValuePair[] nameValuePairs) throws Exception {
        return storageClient().upload_file1(fileExtName, fileExtName, nameValuePairs);
    }

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @return 文件二进制
     * @throws Exception
     */
    public static byte[] downloadFile(String fileId) throws Exception {
        return storageClient().download_file1(fileId);
    }

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @param output 文件输出路径
     * @throws Exception
     */
    public static void downloadFile(String fileId, String output) throws Exception {
        storageClient().download_file1(fileId, output);
    }

    /**
     * 下载文件
     *
     * @param fileId           文件ID
     * @param downloadCallback 下载回调类
     * @throws Exception
     */
    public static void downloadFile(String fileId,
                                    DownloadCallback downloadCallback) throws Exception {
        storageClient().download_file1(fileId, downloadCallback);
    }

    /**
     * 获取文件信息
     *
     * @param fileId 文件ID
     * @return 文件信息
     * @throws Exception
     */
    public static FileInfo getFileInfo(String fileId) throws Exception {
        return storageClient().get_file_info1(fileId);
    }

    /**
     * 获取文件上传时候额外上传的“其他文件信息”
     *
     * @param fileId 文件ID
     * @return 其他文件信息
     * @throws Exception
     */
    public static NameValuePair[] getFileMetadata(String fileId) throws Exception {
        return storageClient().get_metadata1(fileId);
    }

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否删除成功
     * @throws Exception
     */
    public boolean deleteFile(String fileId) throws Exception {
        int code = storageClient().delete_file1(fileId);
        return code == 0;
    }


}
