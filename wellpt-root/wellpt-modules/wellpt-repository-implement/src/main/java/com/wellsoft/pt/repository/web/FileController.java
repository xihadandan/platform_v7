package com.wellsoft.pt.repository.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.sftp.SftpClient;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.dytable.service.FormSignatureDataService;
import com.wellsoft.pt.repository.entity.FileUpload;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.jobs.UploadFileFromSftp2MongoJob;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@Api(tags = "附件")
@RestController
@RequestMapping("/api/repository/file/mongo")
public class FileController extends BaseController {

    @Autowired
    FormSignatureDataService formSignatureDataService;
    @Autowired
    private MongoFileService mongoFileService;

    /**
     * 查询附件历史
     *
     * @param fileID
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/queryFileHistory")
    @ApiOperation(value = "查询附件历史", notes = "根据fileID查询附件历史")
    public ResultMessage queryFileHistory(@RequestParam(value = "fileID") String fileID) {
        ResultMessage resultMessage = new ResultMessage();
        List<Map<String, Object>> list = Lists.newArrayList();
        List<LogicFileInfo> history = mongoFileService.queryFileHistory(fileID);
        if (false == CollectionUtils.isEmpty(history)) {
            for (LogicFileInfo fileInfo : history) {
                Map<String, Object> fileObj = Maps.newHashMap();
                fileObj.put("url", null);
                fileObj.put("source", "server");
                fileObj.put("fileID", fileInfo.getUuid());
                fileObj.put("name", fileInfo.getFileName());
                fileObj.put("size", fileInfo.getFileSize());
                fileObj.put("creator", fileInfo.getCreator());
                fileObj.put("timestamp", fileInfo.getCreateTime());
                fileObj.put("source", fileInfo.getSource());
                list.add(fileObj);
            }
        }
        resultMessage.setData(list);
        return resultMessage;
    }

    @ResponseBody
    @PostMapping(value = "/getFileInfos")
    @ApiOperation(value = "查询附件信息", notes = "查询附件信息")
    public ApiResult<List<LogicFileInfo>> getFileInfos(@RequestBody List<String> fileIds) {
        return ApiResult.success(mongoFileService.getLogicFileInfo(fileIds));
    }


    @ResponseBody
    @GetMapping(value = "/getFileInfosUnderFolder")
    @ApiOperation(value = "查询文件夹下的附件信息", notes = "查询文件夹下的附件信息")
    public ApiResult<List<LogicFileInfo>> getFileInfosUnderFolder(@RequestParam String folderUuid, @RequestParam(required = false) String purpose) {
        return ApiResult.success(mongoFileService.getNonioFilesFromFolder(folderUuid, purpose));
    }

    @ResponseBody
    @PutMapping("{fileId}/fileName")
    @ApiOperation("重命名文件名称")
    public LogicFileInfo saveFileName(@PathVariable("fileId") String fileID,
                                      @RequestParam("newFileName") String newFileName) throws FileNotFoundException {
        return mongoFileService.saveFileName(fileID, newFileName);
    }


    @RequestMapping(value = "uploadNoStreamFileMD5", method = RequestMethod.GET)
    @ApiOperation("上传文件的MD5（无文件流）")
    public @ResponseBody
    ApiResult<FileUpload> uploadNoStreamFileMD5(HttpServletRequest request, HttpServletResponse response, @RequestParam("md5") String md5,
                                                @RequestParam("filename") String filename, @RequestParam("contentType") String contentType,
                                                @RequestParam(value = "source", required = false) String source,
                                                @RequestParam(value = "localFileSourceIcon", required = false) String localFileSourceIcon,
                                                @RequestParam(value = "bsMode", required = false) String newVer, @RequestParam(value = "origUuid", required = false) String origUuid) {
        try {
            MongoFileEntity file = null;
            if (StringUtils.isNotBlank(origUuid)) {
                file = mongoFileService.uploadNoStreamFileMD5(origUuid, filename,
                        md5, "MD5", "", "", null, source, StringUtils.equals(newVer, "true"));
            } else {
                file = mongoFileService.uploadNoStreamFileMD5(null, filename,
                        md5, "MD5", "", "", null, source, null);
            }
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileID(file.getFileID());
            fileUpload.setFilename(file.getLogicFileInfo().getFileName());
            fileUpload.setContentType(file.getLogicFileInfo().getContentType());
            fileUpload.setUserId(userDetails.getUserId());
            fileUpload.setUserName(userDetails.getUserName());
            fileUpload.setDepartmentId(userDetails.getMainDepartmentId());
            fileUpload.setDepartmentName(userDetails.getMainDepartmentName());
            fileUpload.setCreator(userDetails.getUserId());
            fileUpload.setFileSize(file.getLogicFileInfo().getFileSize());
            fileUpload.setCreateTime(file.getLogicFileInfo().getCreateTime());
            fileUpload.setUploadTime(file.getLogicFileInfo().getCreateTime());
            fileUpload.setOrigUuid(file.getLogicFileInfo().getOrigUuid());
            fileUpload.setDigestValue(file.getLogicFileInfo().getDigestValue());
            return ApiResult.success(fileUpload);
        } catch (Exception e) {
            logger.error("上传文件MD5异常：", e);
        }
        return ApiResult.fail();
    }

    @GetMapping("mongoFileUpload2Sftp")
    @ApiOperation("mongo文件上传到sftp服务")
    public @ResponseBody
    ApiResult<Map<String, String>> mongoFileUpload2Sftp(@RequestParam("fileIDs") String[] fileIDs,
                                                        @RequestParam(value = "fileNameType", required = false, defaultValue = "MD5") String fileNameType) {

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
            SftpClient client = new SftpClient(host, username, password, port, privateKey, passphrase);
            try {
                boolean fileNameAsMD5 = "MD5".equalsIgnoreCase(fileNameType);
                Map<String, String> filenameMap = Maps.newHashMap();
                for (String fileID : fileIDs) {
                    MongoFileEntity fileEntity = mongoFileService.getFile(fileID);
                    String filename = fileEntity.getFileName();
                    if (fileNameAsMD5) {
                        filename = fileEntity.getMd5();
                        LogicFileInfo logicFileInfo = fileEntity.getLogicFileInfo();
                        if ("MD5".equalsIgnoreCase(logicFileInfo.getDigestAlgorithm()) && StringUtils.isNotBlank(logicFileInfo.getDigestValue())) {
                            filename = logicFileInfo.getDigestValue();
                        }
                    }
                    try {
                        client.uploadFile(fileEntity.getInputstream(), dir, filename, false);
                    } catch (Exception e) {
                        logger.error("mongo文件上传到sftp服务异常：", e);
                        continue;
                    }
                    filenameMap.put(fileID, filename);
                }
                client.disconnect();
                return ApiResult.success(filenameMap);
            } catch (Exception e) {
                logger.error("mongo文件上传到sftp服务异常：", e);
                return ApiResult.fail();
            }
        }
        return ApiResult.success();
    }

    @GetMapping("/testUploadFileFromSftp2MongoJob")
    public @ResponseBody
    ApiResult testUploadFileFromSftp2MongoJob() {
        UploadFileFromSftp2MongoJob.execute();
        return ApiResult.success();
    }
}
