/*
 * @(#)Feb 20, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.web.api;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.encode.JsonBinder;
import com.wellsoft.context.util.file.FileDownloadUtils;
import com.wellsoft.context.util.file.ZipUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.context.web.controller.AbstractJsonDataServicesController;
import com.wellsoft.context.web.controller.ApiResult;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.dms.bean.DmsFileVo;
import com.wellsoft.pt.dms.entity.DmsFileEntity;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.file.action.FileAction;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.dms.file.executor.FileActionExecutor;
import com.wellsoft.pt.dms.file.executor.FileActionExecutorMethod;
import com.wellsoft.pt.dms.file.service.DmsFileActionService;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dms.service.DmsFileService;
import com.wellsoft.pt.dms.service.DmsFolderService;
import com.wellsoft.pt.dms.support.EncodingDetect;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.service.MultiOrgUserAccountService;
import com.wellsoft.pt.repository.entity.FileUpload;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 数据管理服务统一控制层_restful
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 20, 2017.1	zhulh		Feb 20, 2017		Create
 * </pre>
 * @date Feb 20, 2017
 */

@Api(tags = "数据管理服务统一控制层_restful接口")
@RestController
@RequestMapping("/api/dms/file/")
public class ApiDmsFileController extends AbstractJsonDataServicesController {

    private static final String IMAGE_TIFF = "image/tiff";
    private static List<String> TXT_EXTS = new ArrayList<String>();

    static {
        TXT_EXTS.add("txt");
        TXT_EXTS.add("text");
        TXT_EXTS.add("log");
    }

    @Autowired
    private DmsFileService dmsFileService;

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    @Autowired
    private DmsFolderService dmsFolderService;

    @Autowired
    private List<FileAction> fileActions;

    private Map<String, FileAction> fileActionMap = new HashMap<String, FileAction>();

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private DmsFileActionService dmsFileActionService;
    @Autowired
    private MultiOrgUserAccountService multiOrgUserAccountService;

    @Autowired
    private List<FileActionExecutor<?, ?>> fileActionExecutors;
    private Map<Class<?>, FileActionExecutorMethod> fileActionExecutorMap = new ConcurrentHashMap<Class<?>, FileActionExecutorMethod>();

    /**
     * @param multipartRequest
     * @param response
     */
    @ApiOperation(value = "文件上传", notes = "文件上传")
    @PostMapping("/upload")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "multipartRequest", value = "MultipartHttpServletRequest", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "response", value = "HttpServletResponse", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "folderUuid", value = "文件夹uuid", paramType = "query", dataType = "String", required = true)})
    public void upload(MultipartHttpServletRequest multipartRequest, HttpServletResponse response,
                       @RequestParam(value = "folderUuid", defaultValue = "", required = true) String folderUuid)
            throws IOException {
        ResultMessage resultMessage = new ResultMessage();
        List<FileUpload> uploadFiles = new ArrayList<FileUpload>();

        try {
            Iterator<String> iterator = multipartRequest.getFileNames();
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                MultipartFile multifile = multipartRequest.getFile(fieldName);
                String filename = multifile.getOriginalFilename();
                InputStream multifileIS = multifile.getInputStream();
                String contentType = multifile.getContentType();
                long size = multifile.getSize();
                MongoFileEntity file = this.mongoFileService.saveFile(UUID.randomUUID().toString(), filename,
                        multifileIS);
                IOUtils.closeQuietly(multifileIS);

                this.dmsFileActionService.uploadFile(folderUuid, file);

                UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
                FileUpload fileUpload = new FileUpload();
                fileUpload.setUserId(userDetails.getUserId());
                fileUpload.setUserName(userDetails.getUserName());
                fileUpload.setFilename(filename);
                fileUpload.setContentType(contentType);
                fileUpload.setFileSize(size);

                uploadFiles.add(fileUpload);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultMessage.setSuccess(false);
            resultMessage.setMsg(new StringBuilder(e.getMessage()));
            response.setContentType("text/html; charset=utf-8");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.getWriter().write(JsonBinder.buildNormalBinder().toJson(resultMessage).toString());
            return;
        }
        resultMessage.setSuccess(true);
        resultMessage.setData(uploadFiles);
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(JsonUtils.object2Json(resultMessage));
    }

    @ApiOperation(value = "获取文件")
    @GetMapping("/get")
    public ApiResult<DmsFileEntity> getFile(@RequestParam(value = "uuid") String uuid) {
        DmsFileEntity dmsFileEntity = null;
        if (dmsFileActionService.hasFilePermission(uuid, FileActions.READ_FILE, FileActions.EDIT_FILE)) {
            dmsFileEntity = dmsFileService.get(uuid);
        }
        return ApiResult.success(dmsFileEntity);
    }

    /**
     * 文件下载
     *
     * @param request
     * @param response
     * @param downloadFileName 指定的下载文件名
     * @param fileUuid         多个以分号隔开
     * @param folderUuid       多个以分号隔开
     * @throws IOException
     */
    @ApiOperation(value = "文件下载", notes = "文件下载")
    @GetMapping("/download")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "HttpServletRequest", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "response", value = "HttpServletResponse", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "downloadFileName", value = "指定的下载文件名", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "fileUuid", value = "文件uuid 多个以分号隔开", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "folderUuid", value = "文件夹uuid 多个以分号隔开", paramType = "query", dataType = "String", required = true)})
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @RequestParam(value = "downloadFileName", defaultValue = "", required = false) String downloadFileName,
                         @RequestParam(value = "fileUuid", defaultValue = "", required = false) String fileUuid,
                         @RequestParam(value = "folderUuid", defaultValue = "", required = false) String folderUuid)
            throws Exception {
        // 只下载一个文件
        if (isDownloadOneFile(fileUuid, folderUuid)) {
            downloadFile(request, response, downloadFileName, fileUuid);
        } else {
            // 打包下载
            tarDownload(request, response, downloadFileName, fileUuid, folderUuid);
        }
    }

    /**
     * 在线查看
     *
     * @param fileId
     * @param response
     * @param request
     */
    @ApiOperation(value = "在线查看", notes = "在线查看")
    @GetMapping("/view/{fileId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId", value = "文件ID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "response", value = "HttpServletResponse", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "request", value = "HttpServletRequest", paramType = "query", dataType = "String", required = true)})
    public void view(@PathVariable(value = "fileId") String fileId, HttpServletResponse response,
                     HttpServletRequest request) {
        MongoFileEntity file = mongoFileService.getFile(fileId);
        response.reset();
        response.setHeader("Content-Length", file.getLength() + "");
        MediaType mediaType = null;
        String contentType = file.getContentType();
        if (StringUtils.isBlank(contentType) && (mediaType = FileDownloadUtils.getMediaType(request.getServletContext(),
                file.getFileName())) != null) {
            contentType = mediaType.toString();
        }
        // TIFF图片转换为JPEG
        if (IMAGE_TIFF.equalsIgnoreCase(contentType)) {
            try {
                convertTiff2Jepg(file, response);
                response.setContentType(contentType);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                FileDownloadUtils.output(file.getInputstream(), response);
            }
        } else if (isText(file)) {
            File tmpFile = createTempFile(file);
            Writer writer = null;
            try {
                response.setCharacterEncoding(Encoding.UTF8.getValue());
                response.setContentType(MediaType.TEXT_HTML_VALUE);
                String encoding = EncodingDetect.getJavaEncode(tmpFile.getAbsolutePath());
                String fileContent = FileUtils.readFileToString(tmpFile.getAbsoluteFile(), encoding);
                writer = response.getWriter();
                writer.write(fileContent);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } finally {
                tmpFile.delete();
                IOUtils.closeQuietly(writer);
            }
        } else {
            FileDownloadUtils.download(request, response, file.getInputstream(), file.getFileName());
        }
    }

    /**
     * 在线查看器
     *
     * @param fileUuid
     * @param response
     * @param request
     */
    @ApiOperation(value = "在线查看器", notes = "在线查看器")
    @GetMapping("/viewer/{fileUuid}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileUuid", value = "文件UUID", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "response", value = "HttpServletResponse", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "request", value = "HttpServletRequest", paramType = "query", dataType = "String", required = true),
            @ApiImplicitParam(name = "model", value = "Model", paramType = "query", dataType = "String", required = true)})
    public String viewer(@PathVariable(value = "fileUuid") String fileUuid,
                         @RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "viewFileUrl", required = false) String viewFileUrl, HttpServletResponse response,
                         HttpServletRequest request, Model model) {
        if (StringUtils.isNotBlank(title)) {
            model.addAttribute("title", title);
        } else {
            DmsFileEntity entity = dmsFileService.get(fileUuid);
            if (null != entity) {
                title = entity.getFileName();
            } else {
                LogicFileInfo logicFileInfo = mongoFileService.getLogicFileInfo(fileUuid);
                title = null == logicFileInfo ? "" : logicFileInfo.getFileName();
            }
            model.addAttribute("title", title);
        }
        model.addAttribute("fileUuid", fileUuid);
        model.addAttribute("viewFileUrl", viewFileUrl);
        return "/dms/viewer/dms_file_viewer";
    }

    @ApiOperation(value = "查询归档文件uuids", notes = "查询归档文件uuids")
    @PostMapping("/queryDmsFileUuids")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "DmsFileEntity", value = "归档数据(需排除uuid,attach参数)", paramType = "body", dataType = "DmsFileEntity")
    })
    public ApiResult<List<String>> queryDmsFileUuids(@ApiParam(hidden = true) @RequestBody(required = false) JSONObject jsonObject) {
        if (jsonObject == null) {
            return ApiResult.fail("请求参数格式错误");
        }
        if (jsonObject.size() == 0) {
            return ApiResult.fail("请求参数不能为空");
        }
        DmsFileEntity fileEntity = new DmsFileEntity();
        Map<String, Object> map = BeanUtil.beanToMap(fileEntity);
        map.remove("uuid");
        map.remove("attach");
        ApiResult apiResult = this.getApiResult(jsonObject, map);
        if (apiResult != null) {
            return apiResult;
        }
        fileEntity = JsonUtils.json2Object(jsonObject.toJSONString(), DmsFileEntity.class);
        if (StringUtils.isNotBlank(fileEntity.getCreator())) {
            MultiOrgUserAccount userAccount = multiOrgUserAccountService.getAccountById(fileEntity.getCreator());
            if (userAccount == null) {
                return ApiResult.fail("creator参数值不存在");
            }
        }
        if (StringUtils.isNotBlank(fileEntity.getDataUuid())) {
            Long count = dmsFileService.countByDataUuid(fileEntity.getDataUuid());
            if (count == null || count.intValue() == 0) {
                return ApiResult.fail("dataUuid参数值不存在");
            }
        }
        List<String> uuids = dmsFileServiceFacade.queryDmsFileUuids(fileEntity);
        if (CollectionUtils.isEmpty(uuids)) {
            return ApiResult.fail("归档数据不存在");
        }
        return ApiResult.success(uuids);
    }


    @ApiOperation(value = "更新归档文件创建人", notes = "更新归档文件创建人")
    @PostMapping("/updateDmsFileCreator")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "DmsFileVo", value = "归档数据更改创建人", paramType = "body", dataType = "DmsFileVo", required = true)
    })
    public ApiResult<Integer> updateDmsFileCreator(@ApiParam(hidden = true) @RequestBody(required = false) JSONObject jsonObject) {
        if (jsonObject == null) {
            return ApiResult.fail("请求参数格式错误");
        }
        if (jsonObject.size() == 0) {
            return ApiResult.fail("请求参数不能为空");
        }
        DmsFileVo fileVo = new DmsFileVo();
        Map<String, Object> map = BeanUtil.beanToMap(fileVo);
        ApiResult apiResult = this.getApiResult(jsonObject, map);
        if (apiResult != null) {
            return apiResult;
        }

        DmsFileVo dmsFileVo = JsonUtils.json2Object(jsonObject.toJSONString(), DmsFileVo.class);
        if (dmsFileVo == null) {
            return ApiResult.fail("请求参数格式错误");
        }
        if (StringUtils.isBlank(dmsFileVo.getUserId())) {
            return ApiResult.fail("用户ID不能为空");
        }
        if (CollectionUtils.isEmpty(dmsFileVo.getUuids())) {
            return ApiResult.fail("uuids不能为空");
        }
        MultiOrgUserAccount userAccount = multiOrgUserAccountService.getAccountById(dmsFileVo.getUserId());
        if (userAccount == null) {
            return ApiResult.fail("用户ID不存在");
        }
        int result = dmsFileServiceFacade.updateDmsFileCreator(dmsFileVo);
        if (result == 0) {
            return ApiResult.fail("归档数据不存在");
        }
        return ApiResult.success(result);
    }

    private ApiResult getApiResult(JSONObject jsonObject, Map<String, Object> map) {
        List<String> params = new ArrayList<>();
        List<String> errorParams = new ArrayList<>();
        Set<String> keySet = map.keySet();
        for (String key : jsonObject.keySet()) {
            if (keySet.contains(key)) {
                params.add(key);
            } else {
                errorParams.add(key);
            }
        }
        if (CollectionUtils.isNotEmpty(errorParams)) {
            String errors = StringUtils.join(errorParams, ",");
            return ApiResult.fail("请求参数[" + errors + "]不合法");
        }

        List<String> blankList = new ArrayList<>();
        List<String> nullList = new ArrayList<>();
        for (String param : params) {
            Object val = jsonObject.get(param);
            if (val == null) {
                nullList.add(param);
            } else {
                if (StringUtils.isBlank(val.toString())) {
                    nullList.add(param);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(nullList)) {
            String nullStr = StringUtils.join(nullList, ",");
            return ApiResult.fail("请输入[" + nullStr + "]参数值");
        }
        if (CollectionUtils.isNotEmpty(blankList)) {
            String blankStr = StringUtils.join(blankList, ",");
            return ApiResult.fail("[" + blankStr + "]参数值不能为空");
        }
        return null;
    }

    // ------------------------------private------------------------------

    /**
     * @param file
     * @return
     */
    private boolean isText(MongoFileEntity file) {
        String extension = FilenameUtils.getExtension(file.getFileName());
        if (StringUtils.isBlank(extension)) {
            return false;
        }
        if (TXT_EXTS.contains(extension.toLowerCase())) {
            return true;
        }
        return false;
    }

    /**
     * @param file
     * @param response
     * @throws Exception
     */
    private void convertTiff2Jepg(MongoFileEntity file, HttpServletResponse response) throws Exception {
        OutputStream out = response.getOutputStream();
        ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", file.getInputstream(), null);
        ImageEncoder encoder = ImageCodec.createImageEncoder("png", response.getOutputStream(), null);
        encoder.encode(decoder.decodeAsRenderedImage());
        out.close();
    }

    /**
     * @param fileAction
     * @return
     */
    @SuppressWarnings("rawtypes")
    private FileActionExecutorMethod getFileActionExecutorMethod(FileAction fileAction) {
        if (fileActionExecutorMap.isEmpty()) {
            for (FileActionExecutor fileActionExecutor : fileActionExecutors) {
                Class<?> fileActionParamType = null;
                Class<?> fileActionResultType = null;
                Class<?> actionClass = null;
                Type[] genericInterfaces = fileActionExecutor.getClass().getGenericInterfaces();
                if (genericInterfaces.length > 0) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];
                    Type[] argumentTypes = parameterizedType.getActualTypeArguments();
                    fileActionParamType = (Class<?>) argumentTypes[0];
                    fileActionResultType = (Class<?>) argumentTypes[1];
                    actionClass = ReflectionUtils.getSuperClassGenricType(fileActionParamType);
                } else {
                    fileActionParamType = ReflectionUtils.getSuperClassGenricType(fileActionExecutor.getClass(), 0);
                    fileActionResultType = ReflectionUtils.getSuperClassGenricType(fileActionExecutor.getClass(), 1);
                    actionClass = ReflectionUtils.getSuperClassGenricType(fileActionParamType);
                }

                FileActionExecutorMethod fileActionExecutorMethod = new FileActionExecutorMethod();
                fileActionExecutorMethod.setFileActionExecutor(fileActionExecutor);
                fileActionExecutorMethod.setFileActionParamType(fileActionParamType);
                fileActionExecutorMethod.setFileActionResultType(fileActionResultType);
                fileActionExecutorMap.put(actionClass, fileActionExecutorMethod);
            }
        }
        return fileActionExecutorMap.get(AopUtils.getTargetClass(fileAction));
    }

    /**
     * @param action
     * @return
     */
    private FileAction getFileAction(String action) {
        if (fileActionMap.isEmpty()) {
            for (FileAction fileAction : fileActions) {
                fileActionMap.put(fileAction.getId(), fileAction);
            }
        }
        return fileActionMap.get(action);
    }

    /**
     * 如何描述该方法
     *
     * @param file
     * @return
     */
    private File createTempFile(MongoFileEntity file) {
        File downloadDir = new File(Config.APP_DATA_DIR, "dmsfiles");
        if (downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        String fileName = UUID.randomUUID().toString();
        File tmpFile = new File(downloadDir, fileName);
        InputStream input = null;
        OutputStream output = null;
        try {
            input = file.getInputstream();
            output = new FileOutputStream(tmpFile);
            IOUtils.copy(input, output);
        } catch (Exception e) {
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
        return tmpFile;
    }

    /**
     * 如何描述该方法
     *
     * @param fileUuids
     * @param folderUuids
     * @return
     */
    private String createDownloadFolder(String tarFileName, List<String> fileUuids, List<String> folderUuids) {
        File downloadDir = new File(Config.APP_DATA_DIR, "dmsfiles");
        if (downloadDir.exists()) {
            downloadDir.mkdirs();
        }
        File tarFolder = new File(downloadDir + "/" + UUID.randomUUID().toString(), tarFileName);
        if (!tarFolder.exists()) {
            tarFolder.mkdirs();
        }
        // 文件
        for (String fileUuid : fileUuids) {
            createFile(tarFolder, fileUuid);
        }
        // 夹
        for (String folderUuid : folderUuids) {
            File subFolder = createFolder(tarFolder, folderUuid);
            createDownloadFolderAndFiles(subFolder, folderUuid);
        }

        return tarFolder.getAbsolutePath();
    }

    /**
     * @param folder
     * @param folderUuid
     * @return
     */
    private File createFolder(File folder, String folderUuid) {
        DmsFolderEntity dmsFolderEntity = dmsFolderService.get(folderUuid);
        File subFolder = new File(folder, dmsFolderEntity.getName());
        if (!subFolder.exists()) {
            subFolder.mkdirs();
        }
        return subFolder;
    }

    /**
     * @param folder
     * @param fileUuid
     */
    private void createFile(File folder, String fileUuid) {
        DmsFileEntity fileEntity = dmsFileService.get(fileUuid);
        MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileEntity.getDataUuid());
        if (mongoFileEntity != null) {
            InputStream input = mongoFileEntity.getInputstream();
            OutputStream output = null;
            try {
                output = new FileOutputStream(new File(folder, mongoFileEntity.getFileName()));
                IOUtils.copy(input, output);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(input);
                IOUtils.closeQuietly(output);
            }
        }
    }

    /**
     * @param folder
     * @param folderUuid
     */
    private void createDownloadFolderAndFiles(File folder, String folderUuid) {
        List<DmsFile> dmsFiles = dmsFileActionService.listFolderAndFiles(folderUuid);
        for (DmsFile dmsFile : dmsFiles) {
            if (isFolder(dmsFile)) {
                String subFolderUuid = dmsFile.getUuid();
                File subFolder = createFolder(folder, subFolderUuid);
                createDownloadFolderAndFiles(subFolder, subFolderUuid);
            } else {
                createFile(folder, dmsFile.getUuid());
            }
        }
    }

    /**
     * @param dmsFile
     * @return
     */
    private boolean isFolder(DmsFile dmsFile) {
        return StringUtils.equals("application/folder", dmsFile.getContentType());
    }

    /**
     * @param downloadFileName
     * @param fileUuids
     * @param folderUuids
     * @return
     */
    private String getTarFileName(String downloadFileName, List<String> fileUuids, List<String> folderUuids) {
        // 返回指定名称
        if (StringUtils.isNotBlank(downloadFileName)) {
            return downloadFileName;
        }
        // 返回文件所在夹的名称
        if (!fileUuids.isEmpty()) {
            DmsFileEntity fileEntity = dmsFileService.get(fileUuids.get(0));
            DmsFolderEntity folderEntity = dmsFolderService.get(fileEntity.getFolderUuid());
            return folderEntity.getName();
        }
        // 返回夹所在夹的名称
        if (!folderUuids.isEmpty()) {
            DmsFolderEntity folderEntity = dmsFolderService.get(folderUuids.get(0));
            return folderEntity.getName();
        }
        return "unknow";
    }

    /**
     * 打包下载
     *
     * @param request
     * @param response
     * @param downloadFileName
     * @param fileUuid
     * @param folderUuid
     * @throws Exception
     */
    private void tarDownload(HttpServletRequest request, HttpServletResponse response, String downloadFileName,
                             String fileUuid, String folderUuid) throws Exception {
        List<String> fileUuids = Arrays.asList(StringUtils.split(fileUuid, Separator.SEMICOLON.getValue()));
        List<String> folderUuids = Arrays.asList(StringUtils.split(folderUuid, Separator.SEMICOLON.getValue()));
        String tarFileName = getTarFileName(downloadFileName, fileUuids, folderUuids);

        // 生成临时文件、夹
        String srcFolder = createDownloadFolder(tarFileName, fileUuids, folderUuids);
        String destZipFile = srcFolder + ".zip";

        ZipUtils.zipFolder(srcFolder, destZipFile);
        String downloadName = tarFileName + ".zip";
        FileDownloadUtils.download(request, response, FileUtils.openInputStream(new File(destZipFile)), downloadName);

        // 删除临时文件、夹
        FileUtils.deleteQuietly(new File(srcFolder).getParentFile());
    }

    /**
     * @param fileUuid
     * @param folderUuid
     * @return
     */
    private boolean isDownloadOneFile(String fileUuid, String folderUuid) {
        if (StringUtils.isNotBlank(folderUuid)) {
            return false;
        }
        if (StringUtils.contains(fileUuid, Separator.SEMICOLON.getValue())) {
            return false;
        }
        return StringUtils.isNotBlank(fileUuid);
    }

    /**
     * @param request
     * @param response
     * @param downloadFileName
     * @param fileUuid
     */
    private void downloadFile(HttpServletRequest request, HttpServletResponse response, String downloadFileName,
                              String fileUuid) {
        String downloadName = downloadFileName;
        DmsFileEntity dmsFileEntity = dmsFileService.get(fileUuid);
        String fileId = dmsFileEntity.getDataUuid();
        MongoFileEntity mongoFileEntity = mongoFileService.getFile(fileId);
        if (StringUtils.isBlank(downloadName)) {
            downloadName = dmsFileEntity.getFileName();
        }
        FileDownloadUtils.download(request, response, mongoFileEntity.getInputstream(), downloadName);
    }
}
