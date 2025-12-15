/*
 * @(#)2016年3月23日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.wopi.web;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.config.SystemParamsUtils;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.http.HttpUtil;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.common.wopi.support.FileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年3月23日.1	zhulh		2016年3月23日		Create
 * </pre>
 * @date 2016年3月23日
 */
@Controller
@RequestMapping(value = "/wopi/files")
public class WopiController extends BaseController {
    /**
     * 如何描述SHA256
     */
    private static final String SHA_256 = "SHA-256";

    private static String SEPERATOR_CHAR = ".";

    @Autowired
    private MongoFileService mongoFileService;

    /**
     * @param input
     * @return
     * @throws Exception
     */
    public static byte[] digestAsSHA256(InputStream input) throws Exception {
        File tmpDir = new File(Config.APP_DATA_DIR, "temp");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        File tmpFile = new File(tmpDir, UUID.randomUUID().toString());
        FileOutputStream output = new FileOutputStream(tmpFile);
        IOUtils.copy(input, output);
        IOUtils.closeQuietly(output);
        // SHA256Hash
        MessageDigest m = MessageDigest.getInstance(SHA_256);
        FileInputStream fis = new FileInputStream(tmpFile);
        FileChannel ch = fis.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, tmpFile.length());
        m.update(byteBuffer);
        byte s[] = m.digest();
        IOUtils.closeQuietly(fis);
        return s;
    }

    /**
     * @param fileId
     * @return
     */
    private static String extractFileUuid(String fileId) {
        if (StringUtils.isBlank(fileId)) {
            return fileId;
        }
        if (fileId.lastIndexOf(SEPERATOR_CHAR) == -1) {
            return fileId;
        }
        return StringUtils.split(fileId, SEPERATOR_CHAR)[0];
    }

    /**
     * 获取文件信息
     *
     * @param fileId
     * @param accessToken
     * @param request
     * @param response
     * @return
     */
    @CrossOrigin(value = "*")
    @RequestMapping(value = "/{fileId:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> getFileInfo(@PathVariable("fileId") String fileId,
                                              @RequestParam(value = "access_token") String accessToken, HttpServletRequest request,
                                              HttpServletResponse response) {
        // 获取文件名，加速返回
        String ignoreSha256 = request.getParameter("ignoreSha256");
        // 比较快速的文件版本比较
        String documentVersion = request.getParameter("documentVersion");
        String fileUuid = extractFileUuid(fileId);
        MongoFileEntity fileEntity = mongoFileService.getFile(fileUuid);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setBaseFileName(fileEntity.getFileName());
        fileInfo.setOwnerId(fileEntity.getLogicFileInfo().getCreator());
        fileInfo.setSize(fileEntity.getLength());
        fileInfo.setUserId(fileEntity.getLogicFileInfo().getCreator());
        if ("true".equals(ignoreSha256)) {
            // 忽略计算Sha256
            fileInfo.setSha256(fileEntity.getFileID());
        } else if (StringUtils.isNotBlank(documentVersion)
                && StringUtils.equals(fileEntity.getLogicFileInfo().getRecVer() + StringUtils.EMPTY, documentVersion)) {
            // 版本号相同
            fileInfo.setSha256("304");
        } else {
            InputStream input = fileEntity.getInputstream();
            try {
                fileInfo.setSha256(Base64.encodeBase64String(DigestUtils.sha256(input)));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(input);
            }
        }
        fileInfo.setVersion(fileEntity.getLogicFileInfo().getRecVer() + StringUtils.EMPTY);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<String>(JsonUtils.object2Json(fileInfo), HttpStatus.OK);
    }

    /**
     * 获取文件流
     *
     * @param fileId
     * @param accessToken
     * @param request
     * @param response
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "/{fileId:.+}/contents")
    public void getFile(@PathVariable("fileId") String fileId,
                        @RequestParam(value = "access_token") String accessToken, HttpServletRequest request,
                        HttpServletResponse response) throws UnsupportedEncodingException {
        // String userAgent = request.getHeader("User-Agent");
        String fileUuid = extractFileUuid(fileId);
        MongoFileEntity fileEntity = mongoFileService.getFile(fileUuid);
        response.reset();
        response.setCharacterEncoding(Encoding.UTF8.getValue());
        MediaType mediaType = FileDownloadUtils.getMediaType(request.getServletContext(), fileEntity.getFileName());
        response.setContentType(null == mediaType ? "application/octet-stream; charset=UTF-8" : mediaType.toString());
        String fileName = URLEncoder.encode(fileEntity.getFileName(), "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        FileDownloadUtils.output(fileEntity.getInputstream(), response);
    }

    @CrossOrigin(value = "*")
    @RequestMapping(value = "/getViewPermission/{fileId:.+}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getFilePermission(@PathVariable("fileId") String fileId,
                                                                 @RequestParam(value = "userId") String userId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> permission = new HashMap<String, Object>();
        permission.put("print", true);
        permission.put("openFile", false);
        permission.put("download", false);
        permission.put("viewBookmark", false);
        return new ResponseEntity<Map<String, Object>>(permission, HttpStatus.OK);
    }

    @CrossOrigin(value = "*")
    @RequestMapping(value = "/getPrintHistory/{fileId:.+}", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> getFilePermission(@PathVariable("fileId") String fileId,
                                                                       HttpServletRequest request, HttpServletResponse response) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("username", "张三");
        obj.put("printcount", "1");
        obj.put("printdate", "2021-06-19");
        list.add(obj);
        obj = new HashMap<String, Object>();
        obj.put("username", "李四");
        obj.put("printcount", "2");
        obj.put("printdate", "2021-08-10");
        list.add(obj);
        return new ResponseEntity<List<Map<String, Object>>>(list, HttpStatus.OK);
    }

    @CrossOrigin(value = "*")
    @RequestMapping(value = "/savePrintReason/{fileId:.+}", method = RequestMethod.POST)
    public ResponseEntity<String> savePrintReason(@PathVariable("fileId") String fileId,
                                                  @RequestParam(value = "userId") String userId, @RequestParam(value = "reason") String reason,
                                                  HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("fileId", fileId);
        obj.put("userId", userId);
        obj.put("reason", reason);
        return new ResponseEntity<String>(JsonUtils.object2Json(obj), HttpStatus.OK);
    }

    @CrossOrigin(value = "*")
    @RequestMapping(value = "/getYozoPreviewUrl", method = RequestMethod.GET)
    public ResponseEntity<String> getYozoPreviewUrl(@RequestParam String fileName, @RequestParam String fileUrl, @RequestParam String convertType) throws IOException {
        String yozoServer = SystemParamsUtils.getValue("app.filepreview.server");
        String isPrint = "1";
        if (StringUtils.isNotBlank(SystemParamsUtils.getValue("app.filepreview.print"))) {
            isPrint = SystemParamsUtils.getValue("app.filepreview.print");
        }
        Map<String, String> params = new HashMap<>();
        params.put("password", "");
        params.put("acceptTracks", "0");
        params.put("callType", "0");
        params.put("convertTimeOut", "60");
        params.put("convertType", convertType);
        params.put("fileAngle", "90");
        params.put("fileUrl", fileUrl);
        params.put("isCopy", "0");
        params.put("isPrint", isPrint);
        params.put("isDccAsync", "0");
        params.put("htmlName", fileName);
        params.put("htmlTitle", fileName);
        params.put("isDelSrc", "0");
        params.put("isDownload", "0");
        params.put("isFirstImage", "0");
        params.put("isHeaderBar", "1");
        params.put("isImageLocation", "0");
        params.put("isShowColWidth", "1");
        params.put("isShowComment", "1");
        params.put("isShowGridline", "0");
        params.put("isShowHideSlide", "1");
        params.put("isShowList", "1");
        params.put("isSignature", "0");
        params.put("noCache", "0");
        params.put("num", "-1");
        params.put("pageEnd", "-1");
        params.put("pageStart", "-1");
        params.put("time", "-1");
        params.put("wmRotate", "45");
        params.put("wmSize", "18");
        params.put("wmTransparency", "0.5");
        params.put("zipConvertType", "0");
        params.put("zoom", "1");
        params.put("zoomPic", "0");
        String result = HttpUtil.httpPost(yozoServer + "/fcscloud/composite/httpfile", params);
        return new ResponseEntity<String>(result.replaceAll("/r/n", ""), HttpStatus.OK);
    }
}
