/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.web;

import cn.hutool.core.util.ZipUtil;
import com.google.common.base.Throwables;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.SystemParamsUtils;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.context.util.encode.EncodeUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.iexport.protos.ProtoData;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.service.IexportService;
import com.wellsoft.pt.basicdata.iexport.service.ImportIexportService;
import com.wellsoft.pt.basicdata.iexport.suport.ExportTreeContextHolder;
import com.wellsoft.pt.basicdata.iexport.suport.IExportEntityStream;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import com.wellsoft.pt.basicdata.iexport.table.ExportTable;
import com.wellsoft.pt.basicdata.iexport.visitor.ExportVisitor;
import com.wellsoft.pt.basicdata.iexport.web.request.ExportRequestParams;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.log.enums.LogManageOperationEnum;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
@Controller
@RequestMapping("/common/iexport/service")
public class IexportController extends BaseController {

    @Autowired
    private IexportService iexportService;

    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private ImportIexportService importIexportService;
    @Autowired
    private FlowDefinitionService flowDefinitionService;

    public static boolean isNewExport() {
        String flg = SystemParamsUtils.getValue("iexport.isNewExport", "true");
        return flg.equals("true");
    }

    @RequestMapping(value = "/eexportDataxport")
    @ResponseBody
    public void export(@RequestParam(value = "uuid") String uuid, @RequestParam(value = "type") String type,
                       @RequestParam(required = false) boolean s, // 导出时候指定数据归属
                       HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<TreeNode> tree = null;
        if (isNewExport()) {
            tree = importIexportService.getExportTree(uuid, type);
        } else {
            tree = iexportService.getExportTree(uuid, type);
        }
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        Writer out = response.getWriter();
        out.write(JsonUtils.object2Json(tree));
        out.flush();
        out.close();
    }

    @PostMapping(value = "/getExportDataDefinitionTree")
    @ResponseBody
    public List<TreeNode> getExportDataDefinitionTree(@RequestBody ExportRequestParams params,
                                                      HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExportTreeContextHolder.remove();
        List<TreeNode> nodes = null;
        try {
            ExportTreeContextHolder.setExportDependency(BooleanUtils.isTrue(params.getExportDependency()));
            ExportTreeContextHolder.setEnableThread(BooleanUtils.isTrue(params.getThread()));
            nodes = importIexportService.exportDataDefAsTree(params.getUuids(), params.getTypes());
            if (BooleanUtils.isTrue(params.getThread())) {
                ExportTreeContextHolder.waitForAllCallableDone(5, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            ExportTreeContextHolder.remove();
        }
        return nodes;
    }


    @PostMapping(value = "/exportDataDefinition")
    @ResponseBody
    public void exportDataDefinition(@RequestParam String typeUuids,
                                     HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-msdownload;charset=utf-8");
        String fileName = request.getParameter("fileName");
        if (StringUtils.isNotBlank(fileName)) {
            fileName = EncodeUtils.urlDecode(request.getParameter("fileName"));
            boolean flg = false;
            fileName = fileName.replaceAll(":", "_");
            fileName = fileName.replace(" ", "");
            if (Charset.forName("iso-8859-1").newEncoder().canEncode(fileName)) {
                flg = true;
                fileName = new String(fileName.getBytes("ISO_8859_1"), "utf-8");
                fileName = fileName + ".def";
            } else {
                fileName = URLEncoder.encode(fileName + ".def", "utf-8");
            }
            if (flg) {
                fileName = new String(fileName.getBytes("utf-8"), "ISO_8859_1");
            }
        } else {
            fileName = System.currentTimeMillis() + ".def";
        }
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        Cookie requestIdCk = new Cookie(request.getParameter("requestId"), "true");
        requestIdCk.setPath("/");
        requestIdCk.setMaxAge(10);
        response.addCookie(requestIdCk);
        ExportTreeContextHolder.remove();
        importIexportService.exportDataDefinition(Arrays.asList(typeUuids.split(";")), response);

    }

    @PostMapping(value = "/exportTableDataAsTree")
    @ResponseBody
    public ApiResult<List<TreeNode>> exportTableDataAsTree(@RequestBody ExportTable table) throws IOException {
        List<TreeNode> treeNodes = iexportService.exportTableDataAsTree(table);
        return ApiResult.success(treeNodes);
    }


    @RequestMapping(value = "/exportData", method = RequestMethod.POST)
    @ResponseBody
    public void exportData(@RequestParam(value = "treeNodeIds") String treeNodeIds,
                           @RequestParam(value = "uuid") String uuid, @RequestParam(value = "type") String type,
                           HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (isNewExport()) {
            String[] treeNodeList = treeNodeIds.split(";");
            String fileName = request.getParameter("fileName");
            boolean flg = false;
            fileName = fileName.replaceAll(":", "：");
            fileName = fileName.replace(" ", "");
            if (Charset.forName("iso-8859-1").newEncoder().canEncode(fileName)) {
                flg = true;
                fileName = new String(fileName.getBytes("ISO_8859_1"), "utf-8");
                fileName = fileName + "[新版].defpf";
            } else {
                fileName = URLEncoder.encode(fileName + "[新版].defpf", "utf-8");
            }
            if (flg) {
                fileName = new String(fileName.getBytes("utf-8"), "ISO_8859_1");
            }
            ProtoData.ProtoDataList protoDataList = importIexportService.getExportData(treeNodeList);
            response.setContentType("application/x-msdownload;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            OutputStream out = response.getOutputStream();
            protoDataList.writeTo(out);
            out.flush();
            out.close();
        } else {
            String[] treeNodeList = treeNodeIds.split(";");
            String fileName = request.getParameter("fileName");
            fileName = fileName.replaceAll(":", "：");
            fileName = fileName.replace(" ", "");

            fileName = URLEncoder.encode(fileName + ".defpf", "utf-8");
            ExportVisitor exportVisitor = iexportService.getExportData(treeNodeList, uuid, type);
            response.setContentType("application/x-msdownload;charset=utf-8");
            // response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            OutputStream out = response.getOutputStream();
            exportVisitor.write(out);
            out.flush();
            out.close();
        }
        // task:6441 开发-主导-流程定义修改日志
        if (type.equals("flowDefinition")) {
            String[] uuids = uuid.split(";");
            for (String flowDefUuid : uuids) {
                FlowDefinition flowDefinition = flowDefinitionService.getOne(flowDefUuid);
                flowDefinitionService.saveLogManageOperation(flowDefinition, null, LogManageOperationEnum.defExport);
            }

        }
    }

    @PostMapping(value = "/uploadDefFile")
    @ResponseBody
    public void uploadDefFile(@RequestParam(value = "file") MultipartFile file, HttpServletResponse response)
            throws IOException {
        String fileName = DateFormatUtils.format(new Date(), "yyyy_MM_dd_") + SnowFlake.getId() + "";
        String fileIndexes = null;
        JSONObject msg = new JSONObject();
        msg.put("directory", fileName);
        try {
            File tempFile = new File(new File(System.getProperty("java.io.tmpdir")), fileName + ".def");
            tempFile.createNewFile();
            OutputStream outputStream = new FileOutputStream(tempFile);
            IOUtils.copyLarge(file.getInputStream(), outputStream);
            IOUtils.closeQuietly(outputStream);

            File unzipFile = ZipUtil.unzip(tempFile);

            File indexFile = new File(unzipFile.getAbsoluteFile(), "index");
            List<HashMap> list = null;
            if (indexFile.exists()) {
                InputStream indexInput = new FileInputStream(indexFile);
                fileIndexes = IOUtils.toString(indexInput);
                IOUtils.closeQuietly(indexInput);
                list = (List<HashMap>) JsonUtils.toCollection(fileIndexes, HashMap.class);
            }
            for (HashMap<String, String> map : list) {
                try {
                    File f = new File(unzipFile.getAbsoluteFile(), map.get("filename"));
                    if (f.exists()) {
                        FileInputStream input = new FileInputStream(f);
                        MongoFileEntity fileEntity = mongoFileService.savePhysicalFile(SpringSecurityUtils.getCurrentTenantId(), null, f.getName(),
                                new MimetypesFileTypeMap().getContentType(f.getName()), input);
                        IOUtils.closeQuietly(input);
                        if (file == null) {
                            throw new RuntimeException("上传文件异常");
                        }
                        map.put("filePhysicalID", fileEntity.getPhysicalID());
                        File subDirectory = new File(unzipFile.getAbsoluteFile(), map.get("uuid"));
                        if (subDirectory.exists()) {
                            JSONArray subFileList = new JSONArray();
                            File[] subFiles = subDirectory.listFiles();
                            for (File sf : subFiles) {
                                FileInputStream subInput = new FileInputStream(sf);
                                MongoFileEntity subFileEntity = mongoFileService.savePhysicalFile(SpringSecurityUtils.getCurrentTenantId(), null, sf.getName(),
                                        new MimetypesFileTypeMap().getContentType(sf.getName()), subInput);
                                JSONObject subFile = new JSONObject();
                                subFile.put("filePhysicalID", subFileEntity.getPhysicalID());
                                subFile.put("filename", sf.getName());
                                subFileList.put(subFile);
                                IOUtils.closeQuietly(subInput);
                            }
                            map.put("attachment", subFileList.toString());
                        }
                    }
                } catch (Exception e) {
                    map.put("status", "error");
                    map.put("errorMsg", e.getMessage());
                    logger.error("上传定义文件 - {} , 异常: {}", map.get("filename"), e);
                }

            }

            msg.put("fileIndexes", list);
            tempFile.delete();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.getWriter().write(msg.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }


    @PostMapping(value = "/compareImportDataDiffToCurrent")
    @ResponseBody
    public ApiResult<List> compareImportDataDiffToCurrent(@RequestBody ArrayList<HashMap<String, Object>> data)
            throws IOException {
        try {

            for (Map<String, Object> map : data) {
                String type = map.get("type").toString();
                String uuid = map.get("uuid").toString();
                String filePhysicalId = map.get("filePhysicalID").toString();

                try {
                    IexportDataProvider dataProvider = IexportDataProviderFactory.getDataProvider(type);
                    if (dataProvider != null) {

                        // 读取
                        MongoFileEntity fileEntity = mongoFileService.getPhysicalFile(filePhysicalId);
                        if (fileEntity != null) {
                            String json = IOUtils.toString(fileEntity.getInputstream());
                            if (StringUtils.isNotBlank(json)) {
                                IExportEntityStream stream = IExportEntityStream.fromJSON(json);
                                if (stream != null) {
                                    IexportDataProvider.CompareStatus status = dataProvider.importEntityCompare(dataProvider.entityFromJsonString(stream.getMetadata().getData()));
                                    if (status != null) {
                                        map.put("status", status.name());
                                        continue;
                                    }

                                    JpaEntity entity = dataProvider.getEntity(uuid);
                                    if (entity == null) {
                                        map.put("status", "newData");
                                        continue;
                                    }
//                                    String recVer = stream.getMetadata().getRecVer() != null ? stream.getMetadata().getRecVer().toString() : null;
//                                    boolean sameRecVer = recVer != null && entity.getRecVer() != null && recVer.equals(entity.getRecVer().toString());
                                    String currentDataJsonStr = dataProvider.entityJsonString(entity);
                                    map.put("status", stream.getMetadata().getData().equals(currentDataJsonStr) ? "duplicate" : "conflict");

//                                    if (sameRecVer) {
//                                        // 版本号一致
//                                        map.put("status", "duplicate");
//                                    } else {
//                                        if (StringUtils.isBlank(recVer)) {
//                                            map.put("status", stream.getMetadata().getData().equals(currentDataJsonStr) ? "duplicate" : "conflict");
//                                        } else {
//                                            map.put("status", "conflict");
//                                        }
//                                    }
                                    if (map.get("status").equals("conflict")) {
                                        map.put("importData", stream.getMetadata().getData());
                                        map.put("currentData", currentDataJsonStr);
                                    }
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    map.put("status", "error");
                    logger.error("比较导入实体数据差异性异常", e);
                }
            }

            return ApiResult.success(data);
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
    }


    @RequestMapping(value = "/importDefData", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult<List> importDefData(@RequestBody ArrayList<HashMap<String, String>> data) {
        try {

            for (Map<String, String> map : data) {
                String type = map.get("type");
                String filePhysicalId = map.get("filePhysicalID");
                String attachment = map.get("attachment");
                try {
                    IexportDataProvider dataProvider = IexportDataProviderFactory.getDataProvider(type);
                    if (dataProvider != null) {
                        MongoFileEntity fileEntity = mongoFileService.getPhysicalFile(filePhysicalId);
                        if (fileEntity != null) {
                            // 读取
                            String json = IOUtils.toString(fileEntity.getInputstream());
                            if (StringUtils.isNotBlank(json)) {
                                IExportEntityStream stream = IExportEntityStream.fromJSON(json);
                                if (StringUtils.isNotBlank(attachment)) {
                                    List<HashMap<String, String>> attachmentList = (List<HashMap<String, String>>) JsonUtils.toCollection(attachment, HashMap.class);
                                    stream.setFiles(Lists.newArrayList());
                                    if (CollectionUtils.isNotEmpty(attachmentList)) {
                                        for (HashMap<String, String> attach : attachmentList) {
                                            MongoFileEntity subFile = mongoFileService.getPhysicalFile(attach.get("filePhysicalID"));
                                            if (subFile != null) {
                                                stream.getFiles().add(new IExportEntityStream.File(attach.get("filename"), subFile.getInputstream()));
                                            }
                                        }
                                    }
                                }
                                dataProvider.saveEntityStream(stream);
                                map.put("status", "success");
                            }
                        }

                    }
                } catch (Exception e) {
                    map.put("status", "error");
                    map.put("errorMsg", Throwables.getRootCause(e).getMessage());
                    logger.error("比较导入实体数据差异性异常", e);
                }
            }

            return ApiResult.success(data);
        } catch (Exception e) {
            return ApiResult.fail(e.getMessage());
        }
    }


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public void upload(@RequestParam(value = "iupload") MultipartFile upload, HttpServletResponse response)
            throws IOException {
        String fileId = "";
        try {
            MongoFileEntity fileEntity = mongoFileService.saveFile(upload.getOriginalFilename(),
                    upload.getInputStream());
            fileId = fileEntity.getId();
            // result.put("status", "success");
            // result.put("msg", "上传成功！");
            // result.put("fileId", fileId);
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            // result.put("status", "error");
            // result.put("msg", "上传失败！失败原因：" + e.getMessage());
        }

        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.getWriter().write(fileId);
        response.getWriter().flush();
        response.getWriter().close();
    }

}
