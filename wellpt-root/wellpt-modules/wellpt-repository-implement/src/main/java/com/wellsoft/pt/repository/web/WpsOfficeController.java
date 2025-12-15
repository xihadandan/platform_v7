/*
 * @(#)2019年12月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.web;

import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.repository.convert.FileConvertService;
import com.wellsoft.pt.repository.entity.FileUpload;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.repository.support.RepoUtils;
import com.wellsoft.pt.repository.websocket.WebSocketSessionManager;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年12月12日.1	zhongzh		2019年12月12日		Create
 * </pre>
 * @date 2019年12月12日
 */
@Controller
@RequestMapping("/office/wps")
public class WpsOfficeController extends BaseController {

    private final static String FILE_UPLOAD_PATH = MongoFileController.FILE_UPLOAD_PATH;
    private final static String PREVIEW_PATH = Config.APP_DATA_DIR + File.separator + "preview";

    // private volatile String jsPluginsXML = null;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    @Qualifier("suwellConvertService")
    private FileConvertService fileConvertService;

    @PostConstruct
    public void init() throws ServletException {
        File uploadFolder = new File(FILE_UPLOAD_PATH);
        if (uploadFolder.mkdirs()) {
            //
        }
        File dir = new File(PREVIEW_PATH);
        if (dir.mkdirs()) {
            //
        }
    }

    @ResponseBody
    @RequestMapping(value = "/jsplugins.xml", produces = {MediaType.APPLICATION_XML_VALUE})
    public String getJsPluginsXML(HttpServletRequest request, HttpServletResponse response) throws IOException,
            URISyntaxException {
        String lf = "\n";
        String requestURL = ServletUtils.getServerRequestURLConfig(request);
        StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
        xmlBuilder.append("<jsplugins>").append(lf);
        xmlBuilder.append("<jsplugin name=\"etoa\" url=\"").append(requestURL);
        xmlBuilder.append("/resources/wps/plugins/v3.1/etoa.7z\" type=\"et\" version=\"2.1\"/>").append(lf);
        xmlBuilder.append("<jsplugin name=\"oaassist\" url=\"").append(requestURL);
        xmlBuilder.append("/resources/wps/plugins/v3.1/oaassist.7z\" type=\"wps\" version=\"3.1\"/>").append(lf);
        xmlBuilder.append("<jsplugin name=\"wppoa\" url=\"").append(requestURL);
        xmlBuilder.append("/resources/wps/plugins/v3.1/wppoa.7z\" type=\"wpp\" version=\"2.1\"/>").append(lf);
        xmlBuilder.append("</jsplugins>");
        return xmlBuilder.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/v2/jsplugins.xml", produces = {MediaType.APPLICATION_XML_VALUE})
    public String getJsPluginsXMLS(HttpServletRequest request, HttpServletResponse response) throws IOException,
            URISyntaxException {
        String lf = "\n";
        String requestURL = ServletUtils.getServerRequestURLConfig(request);
        StringBuilder xmlBuilder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
        xmlBuilder.append("<jsplugins>").append(lf);
        xmlBuilder.append("<jspluginonline name=\"EtOAAssist\" type=\"et\" url=\"").append(requestURL);
        xmlBuilder.append("/resources/wps/oaassist/EtOAAssist/\"/>").append(lf);
        xmlBuilder.append("<jspluginonline name=\"WppOAAssist\" type=\"wpp\" url=\"").append(requestURL);
        xmlBuilder.append("/resources/wps/oaassist/WppOAAssist/\"/>").append(lf);
        xmlBuilder.append("<jspluginonline name=\"WpsOAAssist\" type=\"wps\" url=\"").append(requestURL);
        xmlBuilder.append("/resources/wps/oaassist/WpsOAAssist/\"/>").append(lf);
        xmlBuilder.append("</jsplugins>");
        return xmlBuilder.toString();
    }

    @RequestMapping(value = "/savefiles", method = RequestMethod.POST)
    public void saveFiles(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 会话ID
        String wSessionId = request.getParameter("sId");
        if (StringUtils.isBlank(wSessionId)) {
            throw new RuntimeException("参数[sid]非空");
        } else if (WebSocketSessionManager.validateSession(wSessionId)) {
            // 有效会话
        }
        ResultMessage resultMessage = null;
        String once = request.getParameter("once");
        String fileID = request.getParameter("fileID");
        if (request instanceof MultipartHttpServletRequest) {
            resultMessage = saveMultipart((MultipartHttpServletRequest) request, response, wSessionId, once, fileID);
        } else {
            resultMessage = saveSinglepart(request, response, wSessionId, once, fileID);
        }
        resultMessage.setMsg(new StringBuilder(fileID));
        response.setContentType("text/html; charset=utf-8");
        String message = JsonUtils.object2Json(resultMessage);
        if (StringUtils.equals(once, "true")) {
            response.getWriter().write(message);
        } else {
            WebSocketSessionManager.syncSend(wSessionId, message);
        }
    }

    /**
     * @param multipartRequest
     * @param response
     * @param wSessionId
     * @param tId
     * @param once
     * @param fileID
     * @return
     */
    private ResultMessage saveMultipart(MultipartHttpServletRequest multipartRequest, HttpServletResponse response,
                                        String wSessionId, String once, String fileID) {
        InputStream multifileIS = null;
        ResultMessage resultMessage = new ResultMessage();
        List<FileUpload> uploadFiles = new ArrayList<FileUpload>();
        String bsMode = multipartRequest.getParameter("bsMode");
        String source = multipartRequest.getParameter("source");
        String headFileName = multipartRequest.getParameter("filename");
        try {
            headFileName = headFileName == null ? null : URLDecoder.decode(headFileName, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        UserDetails userDetails = WebSocketSessionManager.getUserDetail(wSessionId);
        try {
            IgnoreLoginUtils.login(userDetails);
            Iterator<String> iterator = multipartRequest.getFileNames();
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                MultipartFile multifile = multipartRequest.getFile(fieldName);
                FileUpload fileUpload = new FileUpload();
                String filename = StringUtils.isNotBlank(headFileName) ? headFileName : multifile.getOriginalFilename();
                if (StringUtils.isBlank(fileID)) {
                    // 只支持单个文件修改
                    fileID = UuidUtils.createUuid();
                } else if (StringUtils.equals(source, "新建正文") || StringUtils.equals(source, "导入正文")) {
                    MongoFileEntity file = mongoFileService.getFile(fileID);
                    if (null != file) {
                        source = "编辑";
                    }
                }
                {
                    multifileIS = multifile.getInputStream();
                    MongoFileEntity file = mongoFileService.saveFile(fileID, StringUtils.equals(bsMode, "true"), filename, multifileIS, source);
                    IOUtils.closeQuietly(multifileIS);
                    fileUpload.setFileID(file.getId());
                }
                // 保存文件上传记录
                fileUpload.setFilename(filename);
                fileUpload.setFileSize(multifile.getSize());
                fileUpload.setContentType(multifile.getContentType());
                fileUpload.setUserId(userDetails.getUserId());
                fileUpload.setUserName(userDetails.getUserName());
                fileUpload.setDepartmentId(userDetails.getMainDepartmentId());
                fileUpload.setDepartmentName(userDetails.getMainDepartmentName());
                fileUpload.setCreator(userDetails.getUserId());
                fileUpload.setCreateTime(new Date());
                fileUpload.setUploadTime(new Date());
                uploadFiles.add(fileUpload);
                if (StringUtils.isNotBlank(headFileName)) {
                    // 只传一个文件
                    break;
                }
            }
            resultMessage.setSuccess(true);
            resultMessage.setData(uploadFiles);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            resultMessage.setSuccess(false);
            resultMessage.setData(ex.getMessage());
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
        } finally {
            IgnoreLoginUtils.logout();
        }
        return resultMessage;
    }

    private ResultMessage saveSinglepart(HttpServletRequest request, HttpServletResponse response, String wSessionId,
                                         String once, String fileID) {
        InputStream inStream = null;
        OutputStream outStrem = null;
        int contentLength = request.getContentLength();
        String contentType = request.getContentType();
        ResultMessage resultMessage = new ResultMessage();
        List<FileUpload> uploadFiles = new ArrayList<FileUpload>();
        UserDetails userDetails = WebSocketSessionManager.getUserDetail(wSessionId);
        try {
            IgnoreLoginUtils.login(userDetails);
            FileUpload fileUpload = new FileUpload();
            String bsMode = request.getParameter("bsMode");
            String source = request.getParameter("source");
            String filename = request.getHeader("filename");
            filename = new String(filename.getBytes("iso-8859-1"), "UTF-8");
            if (StringUtils.isBlank(fileID)) {
                // 只支持单个文件修改
                fileID = UuidUtils.createUuid();
            }
            MongoFileEntity file = mongoFileService.saveFile(fileID, StringUtils.equals(bsMode, "true"), filename, inStream = request.getInputStream(), source);
            fileUpload.setFileID(file.getId());
            // 保存文件上传记录
            fileUpload.setFilename(filename);
            fileUpload.setFileSize(contentLength);
            fileUpload.setContentType(contentType);
            fileUpload.setUserId(userDetails.getUserId());
            fileUpload.setUserName(userDetails.getUserName());
            fileUpload.setDepartmentId(userDetails.getMainDepartmentId());
            fileUpload.setDepartmentName(userDetails.getMainDepartmentName());
            fileUpload.setCreator(userDetails.getUserId());
            fileUpload.setCreateTime(new Date());
            fileUpload.setUploadTime(new Date());
            uploadFiles.add(fileUpload);
            resultMessage.setSuccess(true);
            resultMessage.setData(uploadFiles);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            resultMessage.setSuccess(false);
            resultMessage.setData(ex.getMessage());
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
        } finally {
            IgnoreLoginUtils.logout();
            IOUtils.closeQuietly(inStream);
            IOUtils.closeQuietly(outStrem);
        }
        return resultMessage;
    }

    @RequestMapping("download")
    public void download(@RequestParam("fileID") String fileID, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String wSessionId = request.getParameter("sId");
        if (StringUtils.isBlank(wSessionId)) {
            throw new RuntimeException("参数[sid]非空");
        } else if (WebSocketSessionManager.validateSession(wSessionId)) {
            // 有效会话
        }
        File tempFile = null;
        String originFilename = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String isOfd = request.getParameter("isOfd");
        String filename = request.getParameter("filename");
        UserDetails userDetails = WebSocketSessionManager.getUserDetail(wSessionId);
        try {
            IgnoreLoginUtils.login(userDetails);
            MongoFileEntity file = mongoFileService.getFile(fileID);
            response.setContentType("application/x-download");
            // 设置文件名
            originFilename = StringUtils.isNotBlank(filename) ? URLDecoder.decode(filename, "UTF-8") : file.getLogicFileInfo().getFileName();
            // 写文件流
            inputStream = file.getInputstream();
            if (StringUtils.equals(isOfd, "true") && false == RepoUtils.isOfdOrPdf(originFilename)) {
                response.setContentType("application/ofd");
                int lastIndex = originFilename.lastIndexOf(".");
                String ofdFilename = (lastIndex > 0 ? originFilename.substring(0, lastIndex) : originFilename) + ".ofd";
                String encodeName = URLEncoder.encode(ofdFilename, "UTF-8");
                response.addHeader("Content-Disposition", "attachment;filename=" + encodeName.replaceAll("\\+", "%20"));
                // response.addHeader("Content-Disposition", FileDownloadUtils.getFilenamePart(request, ofdFilename));
                String tempFileName = "[" + file.getId() + "]" + originFilename;
                // 写本地
                tempFile = new File(PREVIEW_PATH + "/" + tempFileName);
                IOUtils.copyLarge(inputStream, outputStream = new FileOutputStream(tempFile));
                IOUtils.closeQuietly(outputStream);
                // 转换输出
                Map<String, String> metas = Maps.newHashMap();
                fileConvertService.officeToOFD(tempFile, response.getOutputStream(), metas);
            } else {
                MediaType mediaType = FileDownloadUtils.getMediaType(request.getServletContext(), originFilename);
                response.setContentType(mediaType == null ? "application/octet-stream" : mediaType.toString());
                String encodeName = URLEncoder.encode(originFilename, "UTF-8");
                response.addHeader("Content-Disposition", "attachment;filename=" + encodeName.replaceAll("\\+", "%20"));
                // response.addHeader("Content-Disposition", FileDownloadUtils.getFilenamePart(request, originFilename));
                IOUtils.copyLarge(inputStream, response.getOutputStream());
            }
        } finally {
            IgnoreLoginUtils.logout();
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            FileUtils.deleteQuietly(tempFile);
        }
    }

    @RequestMapping(value = "/sendMessage")
    public void sendMessage(@RequestParam(value = "jsonData") String jsonData, HttpServletRequest request,
                            HttpServletResponse response) throws IOException {
        String wSessionId = request.getParameter("sid");
        if (StringUtils.isBlank(wSessionId)) {
            throw new RuntimeException("参数[sid]非空");
        } else if (WebSocketSessionManager.validateSession(wSessionId)) {
            // 有效会话
        }
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setData(jsonData);
        String message = JsonUtils.object2Json(resultMessage);
        WebSocketSessionManager.syncSend(wSessionId, message);
        response.getWriter().write(message);
    }

}
