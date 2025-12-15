package com.wellsoft.pt.repository.web;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.pt.log.entity.UserOperationLog;
import com.wellsoft.pt.log.service.UserOperationLogService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

public class OcxFileUpload extends HttpServlet {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private static final int KB_10 = 10 * 1024;
    /**
     * @Fields FILE_UPLOAD_PATH : 文件上传目录
     */
    private static String FILE_UPLOAD_PATH = MongoFileController.FILE_UPLOAD_PATH;
    private static List<String> fileIgnoreExts;
    MongoFileService mongoFileService = null;
    UserOperationLogService userOperationLogService = null;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public void init(ServletConfig config) throws ServletException {
        mongoFileService = (MongoFileService) ApplicationContextHolder.getBean("mongoFileService");
        userOperationLogService = ApplicationContextHolder.getBean(UserOperationLogService.class);
        File uploadFolder = new File(FILE_UPLOAD_PATH);
        if (uploadFolder.mkdirs()) {
        }
        String ignoreExts = Config.getValue("fileupload.ignore.exts");
        if (StringUtils.isNotBlank(ignoreExts)) {
            // .jsp,.exe,.bat,.com,.class
            fileIgnoreExts = Arrays.asList(ignoreExts.split(Separator.COMMA.getValue()));
        }
    }

    private boolean checkFile(String fileName, String clientMD5String) throws Exception {
        boolean res = false;
        res = true;
        return res;
    }

    private boolean checkFileName(String fileName) {
        if (fileIgnoreExts != null && false == fileIgnoreExts.isEmpty()) {
            fileName = fileName.toLowerCase();
            for (String ignoreExt : fileIgnoreExts) {
                if (fileName.endsWith(ignoreExt)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Writer writer = response.getWriter();
        String newVer = request.getParameter("bsMode");
        String source = request.getParameter("source");
        String fileName = request.getHeader("fileName"), fileParams = null, fileId = null;
        if (StringUtils.startsWith(fileName, "fileParams://") && StringUtils.isNotBlank(fileParams = fileName.substring("fileParams://".length()))) {
            try {
                JSONObject fileParamsObj = JSONObject.fromObject(URLDecoder.decode(fileParams, "UTF-8"));
                fileId = fileParamsObj.optString("fileId");
                if (StringUtils.isNotBlank(fileParamsObj.optString("bsMode"))) {
                    newVer = fileParamsObj.optString("bsMode");
                }
                if (StringUtils.isNotBlank(fileParamsObj.optString("source"))) {
                    source = fileParamsObj.optString("source");
                }
                if (StringUtils.isNotBlank(fileParamsObj.optString("fileName"))) {
                    fileName = fileParamsObj.optString("fileName");
                }
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }
        if (StringUtils.isBlank(source)) {
            source = "编辑";
        }
        String userId = SpringSecurityUtils.getCurrentUserId();
        String userName = SpringSecurityUtils.getCurrentUserName();
        if (StringUtils.isBlank(fileName)) {
            logger.warn("[userName=" + userName + ",userId=" + userId + "]OcxFileUpload文件上传:参数fileName为空");
            writer.write("success:0");
            return;
        } else if (false == checkFileName(fileName)) {
            throw new RuntimeException("受限制的文件[" + fileName + "],详情请查看系统参数[fileupload.ignore.exts]");
        }
        InputStream fileInputstream = null;
        InputStream inputStream = request.getInputStream();
        String sessionId = request.getSession() != null ? request.getSession().getId() : "";
        try {
            String fileMd5 = request.getHeader("MD5");
            // 文件块数量
            int blockSize = Integer.parseInt(request.getHeader("blockSize"));
            // 文件块计数
            int blockIndex = Integer.parseInt(request.getHeader("blockIndex"));
            // 文件块大小
            int postSize = Integer.parseInt(request.getHeader("content-length"));
            // 文件名
            String locFileName = URLDecoder.decode(fileName, "UTF-8"), tempFileName = null;
            if (blockSize > 1 && postSize > 0) {
                tempFileName = getTempFileName(sessionId, fileMd5, locFileName, blockSize, blockIndex);
            } else {
                tempFileName = getFileName(sessionId, fileMd5, locFileName);
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("用户接收[" + userName + "]文件块[" + tempFileName + "],大小:" + postSize);
            writeOutBlock(inputStream, tempFileName, postSize);
            // 上传结束
            if (blockSize <= 1 || (blockSize > 1 && mergeAllBlock(sessionId, fileMd5, locFileName, blockSize))) {
                stopWatch.stop();
                // 会话文件名称
                String uploadFileName = getFileName(sessionId, fileMd5, locFileName);
                stopWatch.start("用户接收[" + userName + "]文件[" + uploadFileName + "]上传mongodb");
                File uploadFile = new File(FILE_UPLOAD_PATH + uploadFileName);
                fileInputstream = new FileInputStream(uploadFile);
                // MD5验证文件是否完整。
                if (checkFile(uploadFileName, fileMd5)) {
                    // 开始读输入流，写上传文件
                    MongoFileEntity fileEntity = null;
                    if (StringUtils.isBlank(fileId)) {
                        fileId = UuidUtils.createUuid();
                    }
                    fileEntity = mongoFileService.saveFile(fileId, StringUtils.equals(newVer, "true"), locFileName, fileInputstream, source);
                    fileInputstream.close();
                    uploadFile.delete();
                    // 记录用户操作日志
                    // logUserOperation(fileEntity, request);//
                    String msg = "{success:1, fileID:\"" + fileEntity.getFileID() + "\"}";
                    logger.info("用户[" + userName + "]上传附件[" + locFileName + "]的结果为[" + msg + "]");
                    writer.write(msg);
                } else {
                    writer.write("success:0");
                }
                stopWatch.stop();
            } else {
                writer.write("tempFile:1");
                stopWatch.stop();
            }
            logger.info(stopWatch.prettyPrint());
        } catch (Exception ex) {
            logger.error("用户[" + userName + "]上传附件[" + fileName + "]出现异常!!!", ex);
            if (ex instanceof BusinessException) {
                writer.write("{success:0, msg:\"notAllowedFile\"}");
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(fileInputstream);
        }
    }

    /**
     * @param sessionId
     * @param fileMd5
     * @param fileName
     * @return
     * @Title: getFileName
     * @Description: 生成文件名称
     */
    private String getFileName(String sessionId, String fileMd5, String fileName) {
        StringBuffer name = new StringBuffer("");
        name.append("sessionId-").append(sessionId);
        name.append("-fileMd5-").append(fileMd5);
        name.append("-fileName-").append(fileName);
        return name.toString();
    }

    /**
     * @param sessionId
     * @param fileMd5
     * @param fileName
     * @param blockSize
     * @param blockIndex
     * @return
     * @Title: getTempFileName
     * @Description: 生成临时文件名称
     */
    private String getTempFileName(String sessionId, String fileMd5, String fileName, int blockSize, int blockIndex) {
        StringBuffer name = new StringBuffer("");
        name.append("sessionId-").append(sessionId);
        name.append("-fileMd5-").append(fileMd5);
        name.append("-fileName-").append(fileName);
        name.append("-block-").append(blockIndex).append("-of-").append(blockSize).append(".tmp");
        return name.toString();
    }

    /**
     * @param inputStream
     * @param fileName
     * @param postSize
     * @return
     * @throws IOException
     * @throws Exception
     * @Title: writeOutBlock
     * @Description: 写临时文件
     */
    private boolean writeOutBlock(InputStream inputStream, String fileName, int postSize) throws IOException, Exception {
        FileOutputStream outputStream = null;
        boolean res = false;
        try {
            File dataFile = new File(FILE_UPLOAD_PATH + fileName);
            File tempFile = new File(FILE_UPLOAD_PATH + fileName + ".dat");
            if (dataFile.exists()) {
                // 文件块已经存在，直接返回成功
                return true;
            }
            tempFile.delete();
            tempFile.createNewFile();
            outputStream = new FileOutputStream(tempFile);
            int count = 0;
            int total = 0;
            byte buf[] = new byte[KB_10];
            while (total < postSize) {
                count = inputStream.read(buf);
                if (count == -1) {
                    break;
                } else if (count > 0) {
                    outputStream.write(buf, 0, count);
                    total += count;
                }
            }
            outputStream.flush();
            outputStream.close();
            res = tempFile.renameTo(dataFile);
        } catch (IOException ex) {
            logger.error("写文件块[" + fileName + "]失败：" + ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        return res;

    }

    /**
     * @param fileName
     * @param blockSize
     * @return
     * @throws IOException
     * @throws Exception
     * @Title: mergeBlock
     * @Description: 合并临时文件
     */
    private synchronized boolean mergeAllBlock(String sessionId, String fileMd5, String fileName, int blockSize)
            throws IOException, Exception {
        File tempFile = null;
        String tempFileName = null;
        boolean isAllBlockOK = true;
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            // 判断文件块是否完整
            for (int i = 1; i <= blockSize; i++) {
                tempFileName = getTempFileName(sessionId, fileMd5, fileName, blockSize, i);
                tempFile = new File(FILE_UPLOAD_PATH + tempFileName);
                if (false == tempFile.exists()) {
                    isAllBlockOK = false;
                    break;
                }
            }
            if (isAllBlockOK) {
                File uploadFile = new File(FILE_UPLOAD_PATH + getFileName(sessionId, fileMd5, fileName));
                uploadFile.delete();
                uploadFile.createNewFile();
                byte buf[] = new byte[KB_10];
                outputStream = new FileOutputStream(uploadFile, true);
                for (int i = 1; i <= blockSize; i++) {
                    int size = 0;
                    int count = 0;
                    tempFileName = getTempFileName(sessionId, fileMd5, fileName, blockSize, i);
                    tempFile = new File(FILE_UPLOAD_PATH + tempFileName);
                    inputStream = new FileInputStream(tempFile);
                    while (-1 != (count = inputStream.read(buf))) {
                        outputStream.write(buf, 0, count);
                        size += count;
                    }
                    logger.info(tempFileName + " is merged size = " + size);
                    inputStream.close();
                    tempFile.delete();
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            isAllBlockOK = false;
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
        return isAllBlockOK;
    }

    /**
     * @param fileEntity
     * @param request
     */
    private void logUserOperation(MongoFileEntity fileEntity, HttpServletRequest request) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (userDetails != null) {
            // 用户操作日志
            UserOperationLog log = new UserOperationLog();
            String details = "fileEntity: " + fileEntity;
            if (null != fileEntity) {
                details = "fileId: " + fileEntity.getFileID() + ", fileName: " + fileEntity.getFileName();
            }
            log.setModuleId("MONGO_FILE");
            log.setModuleName("正文附件");
            log.setContent("正文附件上传");
            log.setOperation("上传");
            log.setUserName(userDetails.getUserName());
            log.setDetails(details);
            log.setClientIp(ServletUtils.getRemoteAddr(request));
            userOperationLogService.save(log);
        }
    }
}
