/*
 * @(#)2014-6-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.web;

import bitronix.tm.utils.ExceptionUtils;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.excelexporttemplate.entity.ExcelExportDefinition;
import com.wellsoft.pt.basicdata.excelexporttemplate.service.ExcelExportRuleService;
import com.wellsoft.pt.basicdata.exceltemplate.dao.ExcelImportRuleDao;
import com.wellsoft.pt.basicdata.view.support.DyViewQueryInfoNew;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-13.1	wubin		2014-6-13		Create
 * </pre>
 * @date 2014-6-13
 */
@Controller
@RequestMapping("/basicdata/excelexportrule")
public class ExcelExportController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(ExcelExportController.class);
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private ExcelExportRuleService excelExportRuleService;

    /**
     * 跳转到管理界面的excel导出规则界面
     *
     * @return
     */
    @RequestMapping(value = "")
    public String excelExport() {
        return forward("/basicdata/excelexportrule/excelexportrule");
    }

    /**
     * 上传excel导出模版
     *
     * @param upload
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/uploadExcel", method = RequestMethod.POST)
    @ResponseBody
    public void uploadExcel(@RequestParam(value = "upload") MultipartFile upload, HttpServletResponse response)
            throws IOException {
        String uuid = UUID.randomUUID().toString();
        //		上传处理
        mongoFileService.popAllFilesFromFolder(uuid);
        MongoFileEntity file = mongoFileService.saveFile(uuid, upload.getOriginalFilename(), upload.getInputStream());
        mongoFileService.pushFileToFolder(uuid, file.getId(), "attach");

        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.getWriter().write(uuid);
        response.getWriter().flush();
        response.getWriter().close();
    }

    //读取处理
    @RequestMapping(value = "/downloadImage")
    @ResponseBody
    public void downloadImage(@RequestParam("uuid") String uuid, HttpServletRequest request,
                              HttpServletResponse response) {
        ExcelExportDefinition excelExportDefinition = excelExportRuleService.getExcelExportDefinition(uuid);
        if (excelExportDefinition.getFileUuid() != null && !"".equals(excelExportDefinition.getFileUuid())) {
            List<MongoFileEntity> files = ExcelImportRuleDao.getMongoFileEntityByFileUuid(mongoFileService, excelExportDefinition.getFileUuid());//mongoFileService.getFilesFromFolder(excelExportDefinition.getFileUuid(),"attach");
            if (files == null || files.size() == 0) {

            }
            MongoFileEntity mongoFileEntity = files.get(0);
            InputStream inputStream = mongoFileEntity.getInputstream();
            FileDownloadUtils.download(request, response, inputStream, mongoFileEntity.getFileName());
        } else {
            ServletOutputStream os;
            try {
                os = response.getOutputStream();
                os.print("<script type='text/javascript'>alert('该规则未上传模版');</script>");
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @RequestMapping(value = "/export")
    @ResponseBody
    public String exportData(@RequestBody DyViewQueryInfoNew dyViewQueryInfoNew, HttpServletRequest request,
                             HttpServletResponse response) {
        //		ResultMessage resultMessage = new ResultMessage();
        String viewUuid = dyViewQueryInfoNew.getViewUuid();
        Map<String, String> exMap = dyViewQueryInfoNew.getExpandParams();
        String data = "";
        if (exMap != null) {
            data = exMap.get("data");
        }
        if (StringUtils.isNotBlank(data)) {
            data = data.replaceFirst(",", "");
        }
        dyViewQueryInfoNew.setExpandParams(null);
        String path = excelExportRuleService.generateExcelFile(dyViewQueryInfoNew, viewUuid, data, request, response);
        return path;
    }

    @RequestMapping(value = "/getExcelFile")
    public void getExcelFile(@RequestParam("path") String path, HttpServletRequest request, HttpServletResponse response) {
        File file = new File(path);
        try {
            InputStream is = new FileInputStream(file);
            FileDownloadUtils.download(request, response, is, "excel.xls"); // WorkForm Def
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            FileDownloadUtils.download(request, response, new ByteArrayInputStream(ExceptionUtils.getStackTrace(e)
                    .getBytes()), "error.txt");
        }
    }

    @RequestMapping(value = "/getExcelFileByFileName")
    public void getExcelFileByFileName(@RequestParam("path") String path, @RequestParam("fileName") String fileName,
                                       HttpServletRequest request, HttpServletResponse response) {
        File file = new File(path);
        try {
            InputStream is = new FileInputStream(file);

            fileName = convert(fileName);

            FileDownloadUtils.download(request, response, is, fileName + ".xls"); // WorkForm Def
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            FileDownloadUtils.download(request, response, new ByteArrayInputStream(ExceptionUtils.getStackTrace(e)
                    .getBytes()), "error.txt");
        }
    }

    @RequestMapping(value = "/getExcelFileByFileNameNoConvert")
    public void getExcelFileByFileNameNoConvert(@RequestParam("path") String path, @RequestParam("fileName") String fileName,
                                                HttpServletRequest request, HttpServletResponse response) {
        File file = new File(path);
        try {
            InputStream is = new FileInputStream(file);

            FileDownloadUtils.download(request, response, is, fileName + ".xls"); // WorkForm Def
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            FileDownloadUtils.download(request, response, new ByteArrayInputStream(ExceptionUtils.getStackTrace(e)
                    .getBytes()), "error.txt");
        }
    }

    private String convert(String fileName) {
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;
        while ((i = fileName.indexOf("\\u", pos)) != -1) {
            sb.append(fileName.substring(pos, i));
            if (i + 5 < fileName.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(fileName.substring(i + 2, i + 6), 16));
            }
        }
        return sb.toString();
    }

    //读取管道中的流数据
    public byte[] readStream(InputStream inStream) {
        ByteArrayOutputStream bops = new ByteArrayOutputStream();
        int data = -1;
        try {
            while ((data = inStream.read()) != -1) {
                bops.write(data);
            }
            return bops.toByteArray();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

}