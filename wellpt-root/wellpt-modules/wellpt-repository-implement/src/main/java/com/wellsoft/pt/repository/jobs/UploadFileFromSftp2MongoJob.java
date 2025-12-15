package com.wellsoft.pt.repository.jobs;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.jcraft.jsch.SftpException;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.sftp.SftpClient;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.repository.service.FileWaitUploadService;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * 2021年11月04日   chenq	 Create
 * </pre>
 */
public class UploadFileFromSftp2MongoJob {
    private static Logger logger = LoggerFactory.getLogger(UploadFileFromSftp2MongoJob.class);

    public static void execute() {
        try {
            String host = Config.getValue("fileupload.sftp.host");
            String portStr = Config.getValue("fileupload.sftp.port");
            String username = Config.getValue("fileupload.sftp.username");
            String password = Config.getValue("fileupload.sftp.password");
            String dir = Config.getValue("fileupload.sftp.dir");
            String privateKey = Config.getValue("fileupload.sftp.privateKey");
            String passphrase = Config.getValue("fileupload.sftp.passphrase");
            int port = 22;
            if (StringUtils.isNotBlank(portStr)) {
                port = Integer.parseInt(portStr);
            }
            if (StringUtils.isNotBlank(host) && StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
                FileWaitUploadService fileWaitUploadService = ApplicationContextHolder.getBean(FileWaitUploadService.class);
                Map<String, Object> params = Maps.newHashMap();
                params.put("retryTime", new Date());
                params.put("failCnt", 10);//最大重试次数
                List<QueryItem> itemList = fileWaitUploadService.listQueryItemByNameSQLQuery("queryFileWaitUpload", params, new PagingInfo(1, 5));
                MongoFileService mongoFileService = ApplicationContextHolder.getBean(MongoFileService.class);
                if (CollectionUtils.isNotEmpty(itemList)) {
                    SftpClient client = new SftpClient(host, username, password, port, privateKey, passphrase);
                    client.connectWithException();
                    for (QueryItem item : itemList) {
                        String fileID = item.getString("fileid");
                        String md5 = item.getString("md5");
                        if (fileWaitUploadService.lock(item.getString("uuid"))) {
                            try {
                                // 从sftp上获取文件并上传到mongodb
                                mongoFileService.updateFileInputstream(fileID, item.getString("filename"), null, client.downloadFileStream(dir, md5));
                                fileWaitUploadService.delete(item.getString("uuid"));
                            } catch (SftpException sftpException) {
                                boolean notFoundFile = sftpException.getMessage().indexOf("No such file") != -1;
                                fileWaitUploadService.saveFailLog(item.getString("uuid"), Throwables.getStackTraceAsString(sftpException), !notFoundFile,
                                        (notFoundFile ? DateUtils.addMinutes(new Date(), 3) : null));
                                logger.error("从sftp下载文件fileID={}, 异常: {}", fileID, Throwables.getStackTraceAsString(sftpException));
                            } catch (Exception e) {
                                fileWaitUploadService.saveFailLog(item.getString("uuid"), Throwables.getStackTraceAsString(e), true, null);
                                logger.error("从sftp下载文件fileID={}, 异常: {}", fileID, Throwables.getStackTraceAsString(e));
                            }

                        }
                    }
                }
            } else {
                logger.error("未配置文件上传sftp服务器的相关属性");
            }
        } catch (Exception e) {
            logger.error("从sftp下载文件异常：", e);
        }

    }
}
