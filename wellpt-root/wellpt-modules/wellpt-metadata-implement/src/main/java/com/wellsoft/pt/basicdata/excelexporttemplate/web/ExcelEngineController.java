/*
 * @(#)2014-6-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.web;

import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelEngineService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author FashionSUN
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-13.1	FashionSUN		2014-6-13		Create
 * </pre>
 * @date 2014-6-13
 */
@Controller
@RequestMapping("/basicdata/excel")
public class ExcelEngineController extends BaseController {

    protected static Logger logger = LoggerFactory.getLogger(ExcelEngineController.class);
    @Autowired
    private DyFormFacade dyFormApiFacade;
    @Autowired
    private ExcelEngineService excelEngineService;

    public static void download(HttpServletRequest paramHttpServletRequest,
                                HttpServletResponse paramHttpServletResponse, InputStream paramInputStream, String fileName) {
        if (StringUtils.isBlank(fileName)) {
            throw new RuntimeException("Filename should not be empty.");
        }
        String outFileName = getFilenamePart(fileName, paramHttpServletRequest);
        paramHttpServletResponse.reset();
        paramHttpServletResponse.setContentType("application/octet-stream; charset=UTF-8");
        paramHttpServletResponse.setHeader("Content-Disposition", "attachment;" + outFileName);
        ServletOutputStream localServletOutputStream = null;
        try {
            localServletOutputStream = paramHttpServletResponse.getOutputStream();
            int i = -1;
            byte[] arrayOfByte = new byte[102400];
            while ((i = paramInputStream.read(arrayOfByte)) != -1) {
                localServletOutputStream.write(arrayOfByte, 0, i);
            }
            localServletOutputStream.flush();
        } catch (Exception localException) {
            logger.error(localException.getMessage(), localException);
            try {
                IOUtils.write(ExceptionUtils.getStackTrace(localException), localServletOutputStream);
            } catch (IOException localIOException) {
                logger.error(localIOException.getMessage(), localIOException);
            }
        } finally {
            if (paramInputStream != null) {
                IOUtils.closeQuietly(paramInputStream);
            }
            if (localServletOutputStream != null) {
                IOUtils.closeQuietly(localServletOutputStream);
            }
        }
    }

    private static String getFilenamePart(HttpServletRequest paramHttpServletRequest, String fileName) {
        String retFielName = null;
        try {
            String agent = paramHttpServletRequest.getHeader("user-agent");
            agent = (agent == null) ? "" : agent.toLowerCase();
            if (StringUtils.isBlank(agent)) {
                retFielName = "filename*=UTF-8''" + fileName;
            } else if (agent.indexOf("msie") != -1) {
                String name = fileName;
                int i = fileName.lastIndexOf(".");
                if (i != -1) {
                    name = fileName.substring(0, i);
                }
                retFielName = "filename=" + URLEncoder.encode(name, "UTF-8");
                if (i != -1) {
                    retFielName = retFielName + fileName.substring(i);
                }
            } else if (agent.indexOf("chrome") != -1) {
                retFielName = "filename=" + MimeUtility.encodeText(fileName, "UTF-8", "B");
            } else if (agent.indexOf("firefox") != -1) {
                retFielName = "filename=" + MimeUtility.encodeText(fileName, "UTF-8", "B");
            } else if (agent.indexOf("opera") != -1) {
                retFielName = "filename=" + fileName;
            } else if (agent.indexOf("safari") != -1) {
                retFielName = "filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            }
        } catch (Exception localException) {
            logger.error(localException.getMessage(), localException);
            retFielName = "filename*=UTF-8''" + fileName;
        }
        return retFielName;
    }

    private static String getFilenamePart(String fileName, HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        String retFielName = "";
        boolean isMSIE = (agent != null && agent.toUpperCase().indexOf("MSIE") != -1);
        agent = agent == null ? "other" : agent.toLowerCase();
        try {
            if (isMSIE) {
                retFielName = "filename=" + URLEncoder.encode(fileName, "UTF-8");
                if (retFielName.length() > 150) {
                    retFielName = StringUtils.replace(retFielName, "+", "%20");
                    retFielName = new String(retFielName.getBytes("GB2312"), "ISO-8859-1");
                    retFielName = StringUtils.replace(retFielName, " ", "%20");
                }
            } else if (agent.indexOf("chrome") != -1) {
                retFielName = "filename=" + MimeUtility.encodeText(fileName, "UTF-8", "B");
            } else if (agent.indexOf("firefox") != -1) {
                retFielName = "filename=\"" + MimeUtility.encodeText(fileName, "UTF-8", "B") + "\"";// FIXBUG FireFox文件名带空格下载时不全
            } else if (agent.indexOf("opera") != -1) {
                retFielName = "filename=" + fileName;
            } else if (agent.indexOf("safari") != -1) {
                retFielName = "filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            } else {
                retFielName = "filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            retFielName = "filename*=UTF-8" + fileName;
        }
        return retFielName;
    }

    /**
     * 如何描述该方法
     *
     * @param upload
     * @param formUuid
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/parseJSON", method = RequestMethod.POST)
    @ResponseBody
    public void parseJSON(@RequestParam(value = "upload") MultipartFile upload, HttpServletResponse response)
            throws IOException {
        // InputStream inputStream = upload.getInputStream();
        String jsonString = excelEngineService.parseJSON(upload);
        response.setHeader("Content-type", MediaType.TEXT_PLAIN + ";charset=UTF-8");
        //		response.setContentType(MediaType.APPLICATION_JSON);
        //		response.setHeader("Accept", MediaType.APPLICATION_JSON);
        response.getWriter().write(jsonString);
        response.getWriter().flush();
        response.getWriter().close();
    }

    /**
     * 如何描述该方法
     *
     * @param upload
     * @param formUuid
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/parseJSONS", method = RequestMethod.POST)
    @ResponseBody
    public void parseJSONS(@RequestParam(value = "upload") MultipartFile upload, HttpServletResponse response)
            throws IOException {
        // InputStream inputStream = upload.getInputStream();
        String jsonString = excelEngineService.parseJSONS(upload);
        response.setHeader("Content-type", MediaType.TEXT_PLAIN + ";charset=UTF-8");
        //response.setContentType(MediaType.TEXT_PLAIN);//防止IE提示下载JSON文件
        //response.setHeader("Accept", MediaType.TEXT_PLAIN);//防止IE提示下载JSON文件
        response.getWriter().write(jsonString);
        response.getWriter().flush();
        response.getWriter().close();
    }

    @RequestMapping(value = "/parseExcel", method = RequestMethod.POST)
    @ResponseBody
    public void parseExcel(@RequestParam(value = "upload") MultipartFile upload,
                           @RequestParam(value = "excelJsonObejct") String paramObject, HttpServletRequest request,
                           HttpServletResponse response) throws IOException {//
        // InputStream inputStream = upload.getInputStream();
        File file = excelEngineService.parseExcel(upload, paramObject);
        if (file == null) {
            response.setContentType(MediaType.TEXT_PLAIN);//防止IE提示下载JSON文件
            response.getWriter().write("{code:-1,msg:'文件导出失败'}");
            response.getWriter().flush();
            response.getWriter().close();
        } else {
            InputStream reInputStream = new FileInputStream(file);
            FileDownloadUtils.download(request, response, reInputStream, file.getName());
        }
    }

    @RequestMapping(value = "/parseExcelEx", method = RequestMethod.POST)
    @ResponseBody
    public void parseExcelEx(@RequestParam(value = "excelJsonObejct") String paramObject, HttpServletRequest request,
                             HttpServletResponse response) throws IOException {//

        try {
            File file = excelEngineService.parseExcel(paramObject);

            InputStream reInputStream = new FileInputStream(file);
            FileDownloadUtils.download(request, response, reInputStream, file.getName());

        } catch (Exception e) {
            logger.error(e.getMessage());
            String fullContentType = "text/html;charset=UTF-8";
            response.setContentType(fullContentType);
            response.getWriter().write(
                    ("<html xmlns='http://www.w3.org/1999/xhtml'>"
                            + "<head><meta content='text/html; charset=UTF-8' http-equiv='Content-Type'>"
                            + "<title>errormsg</title></head><body><h4>" + e.getMessage() + "</h4></body></html>"));
        }

    }

    @RequestMapping(value = "/parseExcelDb", method = RequestMethod.POST)
    @ResponseBody
    public void parseExcelDb(@RequestParam(value = "formUuid") String formUuid,
                             @RequestParam(value = "dataUuid") String dataUuid, HttpServletRequest request, HttpServletResponse response)
            throws IOException {//
        DyFormData dyFormData = dyFormApiFacade.getDyFormData(formUuid, dataUuid);
        Map<String, Object> dyFormDisplayData = new HashMap<String, Object>();
        dyFormDisplayData.put("formId", dyFormApiFacade.getFormIdByFormUuid(formUuid));
        dyFormDisplayData.put("formUuid", formUuid);
        dyFormDisplayData.put("formDatas", dyFormData.getDisplayValues());
        String jsonObject = JsonUtils.object2Json(dyFormDisplayData);
        jsonObject = jsonObject.replace("=null", "=''");
        File file = excelEngineService.parseExcel(jsonObject);
        if (file == null) {
            response.getWriter().write(
                    "<html xmlns='http://www.w3.org/1999/xhtml'>"
                            + "<head><meta content='text/html; charset=UTF-8' http-equiv='Content-Type'>"
                            + "<title>错误提示</title></head><body><h4>文件导出失败,模版不存在!</h4></body></html>");
            response.getWriter().flush();
            response.getWriter().close();
        } else {
            InputStream reInputStream = new FileInputStream(file);
            FileDownloadUtils.download(request, response, reInputStream, file.getName());
        }
    }

    @RequestMapping(value = "/downTemplate")
    @ResponseBody
    public void downTemplate(@RequestParam(value = "formId") String formId, HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        MongoFileEntity entity = excelEngineService.getExcelTemplateStream(formId);
        InputStream inputStream = null;
        if (entity == null || (inputStream = entity.getInputstream()) == null) {
            response.getWriter().write(
                    "<html xmlns='http://www.w3.org/1999/xhtml'>"
                            + "<head><meta content='text/html; charset=UTF-8' http-equiv='Content-Type'>"
                            + "<title>错误提示</title></head><body><h4>模版下载失败,模版不存在!</h4></body></html>");
            response.getWriter().flush();
            response.getWriter().close();
        } else {
            String fileName = entity.getFileName();
            if (fileName == null) {
                fileName = excelEngineService.getExcelTemplateName(formId);
                // fileName = "模版文件";
            }
            if (fileName.indexOf(".") < 0) {
                fileName = fileName + ".xls";
            }
            download(request, response, inputStream, fileName);
        }
    }
}